package gov.nih.nci.ncicb.tcgaportal.pathway.util;

import gov.nih.nci.ncicb.tcgaportal.pathway.dao.IPathwayQueries;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Nov 10, 2008
 * Time: 4:55:47 PM
 * To change this template use File | Settings | File Templates.
 * Last updated by: $Author$
 *
 * @version $Rev$
 */
public class MockPathwaySVGFetcher implements IPathwaySVGFetcher {
    public static final String PORTAL_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    private static final String testSvgFile = "PathwayDiagramHandlerTest.svg";

    public String fetchPathwaySVG(String pathwayName, String bcgene, String anomaly) throws IOException, IPathwayQueries.PathwayQueriesException {
        String ret = null;
        //FileInputStream in = null;
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(PORTAL_FOLDER + testSvgFile));
            StringBuffer buf = new StringBuffer();
            byte[] bb = new byte[128];
            int read = in.read(bb);
            while (read > 0) {
                for (int i = 0; i < read; i++) {
                    buf.append((char) bb[i]);
                }
                read = in.read(bb);
            }
            ret = buf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) in.close();
        }
        return ret;
    }

}
