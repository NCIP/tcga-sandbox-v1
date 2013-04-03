package gov.nih.nci.ncicb.tcgaportal.level4.export.ui;

import gov.nih.nci.ncicb.tcgaportal.level4.util.ExportHelper;
import gov.nih.nci.ncicb.tcgaportal.level4.util.Locations;
import gov.nih.nci.ncicb.tcgaportal.level4.util.SpringBeanGetter;
import gov.nih.nci.ncicb.tcgaportal.util.ProcessLogger;
import org.apache.log4j.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Description : Given the name of the export file to fetch, this servlet will
 * go to the export directory on the server and display it to the user for download
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class ExportServlet extends HttpServlet {

    private static ProcessLogger logger;

    private static final int DEFAULT_BUFFER_SIZE = 10240;
    private static final String FILE_NAME_QUERY_PARAM_NAME = "fileName";

    private String filePath;
    private int timesToWaitForFile = 50;
    private int waitTimeInMilliseconds = 500;
    private int waitTimeToDeleteInMilliseconds = 30000;
    
    public void init() throws ServletException {
        this.filePath = getExportFileLocation();
        setWaitTimes();
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

        String fileName = request.getParameter(FILE_NAME_QUERY_PARAM_NAME);
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "";
        }
        
        final File file = new File(filePath, fileName);
        
        if(!file.getCanonicalPath().startsWith(filePath)) {
        	response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        try {
            int tries = 0;
            Thread.sleep(waitTimeInMilliseconds);
            while (!file.exists() && tries++ < timesToWaitForFile) {
                Thread.sleep(waitTimeInMilliseconds);
            }
        } catch (InterruptedException e) {
            // do nothing
        }

        // Check if file actually exists in filesystem.
        if (!file.exists()) {
            // Throw an exception, or send 404, or show default/warning page, or just ignore it.
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }

        // make sure the file is not still growing...
        long fileLength;
        try {
            do {
                fileLength = file.length();
                Thread.sleep(100);
            } while (file.length() > fileLength);

        } catch (InterruptedException e) {
            // nothing
        }

        // Get content type by filename.
        String contentType = getServletContext().getMimeType(file.getName());
        // If content type is unknown, then set the default value.
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // Init servlet response.
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        //response.setHeader("Content-Disposition", "inline");

        // Prepare streams.
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            // Open streams.
            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

            // Write file contents to response.
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

        } finally {
            if (output != null) {
                output.flush();
            }
            close(output);
            close(input);
        }

        // delet the export file on server disk afte some time 
        planDeletionOfFile(fileName);

    }

    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                logger.logToLogger(Level.ERROR, ProcessLogger.stackTracePrinter(e));
            }
        }
    }


    /**
     * Returns the real path to the Spring application context file, as specified in the appcontext
     * parameter in the web.xml.
     *
     * @return the real path to the Spring application context file
     */
    protected String getAppContextPath() {
        //get the appcontext file location and inject into SpringBeanGetter
        String appcontext = getServletConfig().getInitParameter("appcontext");
        if (appcontext == null) throw new RuntimeException("No appcontext property specified in web.xml");
        appcontext = getServletContext().getRealPath(appcontext);
        return appcontext;
    }

    public String getExportFileLocation() {
        //export file location is configured in the spring xml
        Locations locations = (Locations) SpringBeanGetter.getInstance(getAppContextPath()).getBean("locations");
        return locations.getTempFileLocation();
    }

    private void setWaitTimes() {
        ExportHelper exportHelper = (ExportHelper) SpringBeanGetter.getInstance(getAppContextPath()).getBean("exportHelper");
        timesToWaitForFile = exportHelper.getTimesToWaitForFile();
        waitTimeInMilliseconds = exportHelper.getWaitLengthInMilliseconds();
        waitTimeToDeleteInMilliseconds = exportHelper.getWaitTimeToDeleteInMilliseconds();
        
        logger = exportHelper.getLogger();
    }


    //thread that waits a relatively long time then deletes the export file on the server side
    class DeleterThread extends Thread {
        String filename;

        public DeleterThread(String filename) {
            this.filename = filename;
        }

        public void run() {
            try {
                Thread.sleep(waitTimeToDeleteInMilliseconds);
            } catch (InterruptedException e) {
                // do nothing
            }
            deleteExportFile(filename);
        }
    }

    public void planDeletionOfFile(String filename) {
        (new DeleterThread(filename)).start();
    }

    private void deleteExportFile(String filename) {
        int lastslash = filename.lastIndexOf('/');
        if (lastslash >= 0) {
            filename = filename.substring(lastslash + 1);
        }
        File f = new File(filePath + "/" + filename);
        if (f.exists()) {
            boolean deleted = f.delete();
            if (!deleted) {
                // log this somehow?
            }
        }
    }

}
