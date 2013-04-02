/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.processors;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileClinical;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelOne;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileMetadata;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobStatus;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataAccessMatrixJSPUtil;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.Header;
import org.apache.log4j.Level;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

/**
 * Packages files selected by the user into a .tar.gz archive. Sends an email to the user
 * when complete.
 * Calls DataAccessMatrixQueries.addPathsToSelectedFiles to get a path for each data file.
 * Deletes temp files, and then itself, when done.
 *
 * @author David Nassau
 *         Last updated by: Rohini Raman
 * @version $Rev$
 */

public class FilePackager implements FilePackagerI {
    private static final String DOUBLE_LINE_SEPARATOR = "\n\n";

    private FilePackagerFactoryI filePackagerFactory;
    private FilePackagerBean filePackagerBean;
    private ProcessLogger logger;
    private DataAccessMatrixQueries dataAccessMatrixQueries;
    private MailSender mailSender;
    private String failEmail;
    private String tempfileDirectory;
    private String manifestTempfileName;
    private boolean emailTiming;
    private int hoursTillDeletion;
    private long actualUncompressedSize;
    //For keeping track of performance
    private long startTime;
    private long endTime;
    private long endFileProcessingTime;
    private long startArchiveCreationTime;

    public FilePackager() {
    }

    public void setLogger(final ProcessLogger logger) {
        this.logger = logger;
    }

    public String getCompressedArchivePhysicalName() {
        return filePackagerBean.getArchivePhysicalName() + ".tar.gz";
    }

    public File getCompressedArchive() {
        return new File(prefixPathForExternalServer(getCompressedArchivePhysicalName()));
    }

    public int getHoursTillDeletion() {
        return hoursTillDeletion;
    }

    public void setHoursTillDeletion(int hoursTillDeletion) {
        this.hoursTillDeletion = hoursTillDeletion;
    }

    public void setEmailTiming(final boolean emailTiming) {
        this.emailTiming = emailTiming;
    }

    public boolean isEmailTiming() {
        return emailTiming;
    }

    public FilePackagerBean getFilePackagerBean() {
        return filePackagerBean;
    }

    public void setFilePackagerBean(FilePackagerBean filePackagerBean) {
        this.filePackagerBean = filePackagerBean;
    }

    public DataAccessMatrixQueries getDataAccessMatrixQueries() {
        return dataAccessMatrixQueries;
    }

    public void setDataAccessMatrixQueries(DataAccessMatrixQueries dataAccessMatrixQueries) {
        this.dataAccessMatrixQueries = dataAccessMatrixQueries;
    }

    public MailSender getMailSender() {
        return mailSender;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public String getFailEmail() {
        return failEmail;
    }

    public void setFailEmail(String failEmail) {
        this.failEmail = failEmail;
    }

    public String getTempfileDirectory() {
        return tempfileDirectory;
    }

    public void setTempfileDirectory(String tempfileDirectory) {
        this.tempfileDirectory = tempfileDirectory;
    }

    public String getManifestTempfileName() {
        return manifestTempfileName;
    }

    public void setManifestTempfileName(String manifestTempfileName) {
        this.manifestTempfileName = manifestTempfileName;
    }

    public FilePackagerFactoryI getFilePackagerFactory() {
        return filePackagerFactory;
    }

    public void setFilePackagerFactory(FilePackagerFactoryI filePackagerFactory) {
        this.filePackagerFactory = filePackagerFactory;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getEndFileProcessingTime() {
        return endFileProcessingTime;
    }

    public void setEndFileProcessingTime(long endFileProcessingTime) {
        this.endFileProcessingTime = endFileProcessingTime;
    }

    public long getStartArchiveCreationTime() {
        return startArchiveCreationTime;
    }

    public void setStartArchiveCreationTime(long startArchiveCreationTime) {
        this.startArchiveCreationTime = startArchiveCreationTime;
    }

    /**
     * The main method that starts the job.
     */

    public void runJob(FilePackagerBean filePackagerBean) throws Exception {
        if (filePackagerBean == null) {
            throw new Exception("filePackagerBean is null. Cannot proceed.");
        }
        try {
            this.filePackagerBean = filePackagerBean;
            setStartTime(System.currentTimeMillis());
            addPaths();
            setEndFileProcessingTime(System.currentTimeMillis());
            setStartArchiveCreationTime(System.currentTimeMillis());
            calculateActualUncompressedSize();
            writeManifest();
            makeArchive();
            filePackagerBean.setStatus(QuartzJobStatus.Succeeded);
        } catch (Exception ex) {
            //we need this catchall because any uncaught exception (e.g. a RuntimeException)
            //will cause a dead link to be sent to the user
            filePackagerBean.setStatus(QuartzJobStatus.Failed);
            Exception origEx = ex;
            if (ex.getCause() != null && ex.getCause() instanceof Exception) {
                origEx = (Exception) ex.getCause();
            }

            filePackagerBean.setException(origEx);
            logger.logError(origEx);
            throw ex;
        } finally {
            setEndTime(System.currentTimeMillis());
            sendEmailToUser();
            ensureMyOwnTimelyDemise();
        }
    }

    /**
     * Adds paths to DataFiles, which in many cases involves generating temp files from the database.
     *
     * @throws DataAccessMatrixQueries.DAMQueriesException
     *
     */
    private void addPaths() throws DataAccessMatrixQueries.DAMQueriesException {
        checkForOddCharacters();
        //make a list of files to process not including those that we will get from cache
        List<DataFile> filesToProcess = new ArrayList<DataFile>();
        for (final DataFile df : filePackagerBean.getSelectedFiles()) {
            if (df.getPath() == null) {
                filesToProcess.add(df);
            }
        }
        getDataAccessMatrixQueries().addPathsToSelectedFiles(filesToProcess);
        verifyLevel1FilePaths();
    }

    //check for space, slash or backslash. Change to _ and send email to ourselves

    private void checkForOddCharacters() {
        for (final DataFile df : filePackagerBean.getSelectedFiles()) {
            String filename = df.getFileName();
            if (filename != null) {
                String newFilename = filename;
                if (newFilename.contains(" ")) {
                    newFilename = newFilename.replace(" ", "_");
                }
                if (newFilename.contains("/")) {
                    newFilename = newFilename.replace("/", "_");
                }
                if (newFilename.contains("\\")) {
                    newFilename = newFilename.replace("\\", "_");
                }
                if (!filename.equals(newFilename)) {
                    df.setFileName(newFilename);
                    StringBuilder buf = new StringBuilder();
                    buf.append("Strange characters in filename.\n");
                    buf.append("\"").append(filename).append("\" was changed to \"").append(newFilename).append("\"");
                    if (getMailSender() != null && getFailEmail() != null) {
                        getMailSender().send(getFailEmail(), null, "DAM: filename corrected", buf.toString(), false);
                    }
                }
            }
        }
    }

    /**
     * Calculates the uncompressed size of the archive by adding up the sizes of all the actual files.
     * Called after all files have been generated.
     */
    private void calculateActualUncompressedSize() {
        actualUncompressedSize = 0;
        for (final DataFile df : filePackagerBean.getSelectedFiles()) {
            actualUncompressedSize += new File(df.getPath()).length();
        }
    }

    //adds a prefix to the path to write to a particular server

    private String prefixPathForExternalServer(String path) {
        if (filePackagerBean.getArchivePhysicalPathPrefix() != null && filePackagerBean.getArchivePhysicalPathPrefix().length() > 0) {
            path = ConstantValues.SEPARATOR + filePackagerBean.getArchivePhysicalPathPrefix() + path;
        }
        return path;
    }

    private void writeManifest() throws IOException {
        BufferedWriter writer = null;
        try {
            final String tempDir = getTempfileDirectory();
            if (tempDir == null || tempDir.length() == 0) {
                throw new IOException("FilePackagerFactory.tempfileDirectory is null");
            }
            manifestTempfileName = tempDir + ConstantValues.SEPARATOR + UUID.randomUUID();
            writer = new BufferedWriter(new FileWriter(manifestTempfileName));
            final StringBuilder line = new StringBuilder();
            line.append("Platform Type").append('\t').append("Center").append('\t').append("Platform").append('\t')
                    .append("Level").append('\t').append("Sample").append('\t')    //todo: add batch?
                    .append("Barcode").append('\t')
                    .append("File Name").append('\n');
            writer.write(line.toString());
            for (final DataFile df : filePackagerBean.getSelectedFiles()) {
                line.setLength(0);
                line.append(DataAccessMatrixJSPUtil.lookupPlatformTypeName(df.getPlatformTypeId())).append('\t');
                line.append(DataAccessMatrixJSPUtil.lookupCenterName(df.getCenterId())).append('\t');
                if (df instanceof DataFileClinical) {
                    line.append("n/a\tn/a\t");  //no platform or level
                } else {
                    line.append(DataAccessMatrixJSPUtil.lookupPlatformName(df.getPlatformId())).append('\t');
                    if (DataAccessMatrixQueries.LEVEL_METADATA.equals(df.getLevel())) {
                        line.append("n/a\t");
                    } else {
                        line.append(df.getLevel()).append('\t');
                    }
                }
                line.append(df.getDisplaySample()).append('\t');
                line.append(df.getDisplayBarcodes()).append('\t');
                line.append(df.getFileName()).append('\n');
                writer.write(line.toString());
            }
            writer.flush();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void deleteTempFiles() {
        try {
            //manifest file
            if (manifestTempfileName != null) {
                final File f = new File(manifestTempfileName);
                if (f.exists()) {
                    if (!f.delete()) {
                        logger.logToLogger(Level.ERROR, "Failed to delete temp file " + manifestTempfileName);
                    }
                }
            }
            //clinical & level 2/3 temp files
            for (final DataFile df : filePackagerBean.getSelectedFiles()) {
                if (!df.isPermanentFile() && df.getPath() != null) { //can be null if it failed to write out in the first place
                    final File f = new File(df.getPath());
                    if (f.exists()) {
                        if (!f.delete()) {
                            logger.logToLogger(Level.ERROR, "Failed to delete temp file " + df.getPath());
                        }
                    }
                }
            }
        } catch (Exception e) {
            //just report to log, don't propagate. Worst that can happen is temp files stay around
            logger.logToLogger(Level.ERROR, "Exception while trying to delete temp files: " + e.getMessage());
        }
    }

    /**
     * The FP is done with its work, but needs to hang around long enough so the browser
     * can poll for the link. After that, it can remove itself from the factory's map and be GCd
     * This is done in a new thread so the queue doesn't have to wait before starting the next FP.
     */
    private void ensureMyOwnTimelyDemise() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    //long enough time so the browser can certainly poll for the link
                    Thread.sleep(3 * DataAccessMatrixJSPUtil.DAFP_POLLING_INTERVAL);
                } catch (InterruptedException ie) {
                    // this space intentionally left blank
                }
                deleteTempFiles(); //do it after pausing to make sure file locks have been released
                deleteMyself();
            }
        }).start();
    }

    /**
     * Removes itself from the LivePackagers map, which allows it to be collected
     */
    void deleteMyself() {
        filePackagerFactory.removeFilePackagerBean(filePackagerBean.getKey());
    }

    private void sendEmailToUser() {
        try {
            if (filePackagerBean.getEmail() != null && getMailSender() != null) {
                if (filePackagerBean.isDone()) {
                    final StringBuilder buf = new StringBuilder();
                    buf.append("The archive you created is available at ").append(filePackagerBean.getLinkText()).append(DOUBLE_LINE_SEPARATOR);
                    buf.append("It will be available for download for ").
                            append(getHoursTillDeletion()).
                            append(" hours, after which it will be deleted from our servers.").append(DOUBLE_LINE_SEPARATOR);

                    buf.append("IMPORTANT: Data downloaders are urged to use the data annotation search interface (https://tcga-data.nci.nih.gov/annotations/) to query the case, sample, and aliquot identifiers in their download to obtain the latest information associated with their data.");
                    buf.append(DOUBLE_LINE_SEPARATOR);

                    if (filePackagerBean.getFilterRequest() != null) {
                        buf.append(generateFilterTextForEmail());
                    }
                    if (isEmailTiming()) { //debug performance numbers
                        long fileProcessingTime = endFileProcessingTime - startTime;
                        long archiveGenerationTime = endTime - startArchiveCreationTime;
                        long totalTime = endTime - startTime;
                        long waitingInQueueTime = startTime - filePackagerBean.getCreationTime();
                        Date fpCreationDate = new Date(filePackagerBean.getCreationTime());
                        buf.append(DOUBLE_LINE_SEPARATOR).append("Archive Processing Details: ").append(DOUBLE_LINE_SEPARATOR);
                        buf.append("Total file processing time: ").append(formatTime(fileProcessingTime)).append(DOUBLE_LINE_SEPARATOR);
                        buf.append("Total archive generation time: ").append(formatTime(archiveGenerationTime)).append(DOUBLE_LINE_SEPARATOR);
                        buf.append("Total processing time: ").append(formatTime(totalTime)).append(DOUBLE_LINE_SEPARATOR);
                        buf.append("When added to queue: ").append(DateFormat.getTimeInstance(DateFormat.MEDIUM).format(fpCreationDate)).append(DOUBLE_LINE_SEPARATOR);
                        buf.append("Time waiting in queue: ").append(formatTime(waitingInQueueTime)).append(DOUBLE_LINE_SEPARATOR);
                    }
                    buf.append(DOUBLE_LINE_SEPARATOR).append("The TCGA Data Coordinating Center");
                    getMailSender().send(filePackagerBean.getEmail(), null, "Download Available", buf.toString(), false);
                } else if (filePackagerBean.isFailed()) {
                    final StringBuilder buf = new StringBuilder();
                    String errmsg = filePackagerBean.getException().getMessage();
                    if (errmsg == null) {
                        errmsg = filePackagerBean.getException().getClass().toString();
                    }
                    buf.append("Sorry, we were unable to process your archive. Please try again.").append(DOUBLE_LINE_SEPARATOR)
                            .append("Error message: ").append(errmsg)
                            .append(DOUBLE_LINE_SEPARATOR).append("The TCGA Data Coordinating Center");
                    getMailSender().send(filePackagerBean.getEmail(), getFailEmail(), "Download Unavailable", buf.toString(), true);
                }
            }
        } catch (Exception ex) {
            logger.logToLogger(Level.ERROR, "Could not send user email: " + ex.getMessage());
        }
    }

    private String generateFilterTextForEmail() {
        StringBuilder emailText = new StringBuilder();
        emailText.append("The following filter settings were used for the search criteria: ").append(DOUBLE_LINE_SEPARATOR);
        emailText.append(filePackagerBean.getFilterRequest().toString());
        return emailText.toString();
    }

    //sigh - got to do it ourselves because Java's SimpleDateFormatter sucks.

    private String formatTime(long millis) {
        int hours = (int) (millis / (1000 * 60 * 60));
        millis = millis - (hours * 1000 * 60 * 60);
        int minutes = (int) (millis / (1000 * 60));
        millis = millis - (minutes * 1000 * 60);
        int seconds = (int) (millis / 1000);
        StringBuilder ret = new StringBuilder();
        if (hours > 0) {
            ret.append(hours).append(":");
        }
        ret.append(leadingZeros(minutes, 2)).append(":");
        ret.append(leadingZeros(seconds, 2));
        return ret.toString();
    }

    private String leadingZeros(final int i, final int digits) {
        return "0000000000".substring(0, digits - (i + "").length()) + i;
    }

    /**
     * Make sure the file path matches the file name, as a final check to make sure we never write a protected file to an unprotected archive
     * In case of database corruption. Only applies to level 1 files since other files are generated as temp files.
     */
    private void verifyLevel1FilePaths() throws IllegalStateException {
        for (final DataFile df : filePackagerBean.getSelectedFiles()) {
            if (df instanceof DataFileLevelOne) {
                if (df.getPath() == null || !df.getPath().endsWith(df.getFileName())) {
                    throw new IllegalStateException("Retrieved incorrect file path for " + df.getFileName());
                }
            }
        }
    }

    TarArchiveOutputStream makeTarGzOutputStream(final File archiveFile) throws IOException {
        final TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(archiveFile)));
        tarArchiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        tarArchiveOutputStream.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);

        return tarArchiveOutputStream;
    }

    void makeArchive() throws IOException {
        final byte[] buffer = new byte[1024];
        File archiveFile = null;
        TarArchiveOutputStream out = null;
        //in case we need to write to an external server
        final String archiveName = prefixPathForExternalServer(filePackagerBean.getArchivePhysicalName() + ".tar.gz");
        try {
            archiveFile = new File(archiveName);
            out = makeTarGzOutputStream(archiveFile);
            copyManifestToArchive(out);
            int i = 0;
            for (final DataFile fileInfo : filePackagerBean.getSelectedFiles()) {
                final File file = new File(fileInfo.getPath());
                if (!file.exists()) {
                    throw new IOException("Data file does not exist: " + fileInfo.getPath());
                }
                logger.logToLogger(Level.DEBUG, "tarring file " + (++i) + ":" + fileInfo.getPath() + " into " + archiveName);
                //"synthetic" file path, as we want it to appear in the tar
                final String archiveFilePath = constructInternalFilePath(fileInfo);
                final TarArchiveEntry tarAdd = new TarArchiveEntry(file);
                tarAdd.setModTime(file.lastModified());
                tarAdd.setName(archiveFilePath);
                out.putArchiveEntry(tarAdd);
                FileInputStream in = null;
                try {
                    in = new FileInputStream(file);
                    int nRead = in.read(buffer, 0, buffer.length);
                    while (nRead >= 0) {
                        out.write(buffer, 0, nRead);
                        nRead = in.read(buffer, 0, buffer.length);
                    }
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
                out.closeArchiveEntry();
                if (fileInfo.getCacheFileToGenerate() != null) {
                    //a special case where there should be a cache file but it doesn't exist -
                    // Send email with error message
                    //filePackagerFactory.getErrorMailSender().send(Messages.CACHE_ERROR, MessageFormat.format(Messages.CACHE_FILE_NOT_FOUND, fileInfo.getCacheFileToGenerate()));
                }
            }
        } catch (IOException ex) {
            //delete the out file if it exists
            if (out != null) {
                out.close();
                out = null;
            }
            if (archiveFile != null && archiveFile.exists()) {
                // give OS time to delete file handle
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    // it's ok
                }
                // keep track of uncompressed size
                this.actualUncompressedSize = archiveFile.length();
                //noinspection ResultOfMethodCallIgnored
                archiveFile.delete();
            }
            throw ex;
        } finally {
            if (out != null) {
                out.close();
            }
        }
        logger.logToLogger(Level.DEBUG, "Created tar " + archiveName);
    }

    /**
     * Called in the case where a cache file *should* exist but doesn't. We copy the
     * newly generated file to the cache directory.
     *
     * @param fileInfo
     */
    private void generateCacheFile(final DataFile fileInfo) {
        final byte[] buffer = new byte[1024];
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            makeSureCacheFileDirectoryExists(fileInfo.getCacheFileToGenerate());
            in = new FileInputStream(fileInfo.getPath());
            out = new FileOutputStream(fileInfo.getCacheFileToGenerate());
            int nRead = in.read(buffer, 0, buffer.length);
            while (nRead >= 0) {
                out.write(buffer, 0, nRead);
                nRead = in.read(buffer, 0, buffer.length);
            }
        } catch (IOException e) {
            logger.logToLogger(Level.ERROR, "Could not generate cache file: exception follows" + DOUBLE_LINE_SEPARATOR);
            logger.logError(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                logger.logToLogger(Level.DEBUG, "Could not close open input or output stream.");
            }
        }
    }

    private void makeSureCacheFileDirectoryExists(final String cacheFileName) throws IOException {
        int pos = cacheFileName.lastIndexOf(ConstantValues.SEPARATOR);
        //hack  on Windows, path may still have been specified using / in servlet xml file
        if (pos < 0) {
            pos = cacheFileName.lastIndexOf('/');
        }
        String dir = cacheFileName.substring(0, pos);
        File f = new File(dir);
        if (!f.exists()) {
            if (!f.mkdir()) {
                throw new IOException("Could not create cache directory " + dir);
            }
        }
    }

    private void copyManifestToArchive(final TarArchiveOutputStream out) throws IOException {
        final byte[] buffer = new byte[1024];
        final File file = new File(manifestTempfileName);
        if (!file.exists()) {
            throw new IOException("Manifest file does not exist: " + manifestTempfileName);
        }
        final TarArchiveEntry tarAdd = new TarArchiveEntry(file);
        tarAdd.setModTime(file.lastModified());
        tarAdd.setName("file_manifest.txt");
        out.putArchiveEntry(tarAdd);
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            int nRead = in.read(buffer, 0, buffer.length);
            while (nRead >= 0) {
                out.write(buffer, 0, nRead);
                nRead = in.read(buffer, 0, buffer.length);
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        out.closeArchiveEntry();
    }

    //constructs a path for each file within the tar. Does not correspond to a physical location on the server.

    private String constructInternalFilePath(final DataFile fileInfo) {
        String internalFilePath;
        if (filePackagerBean.isFlatten()) {
            internalFilePath = fileInfo.getFileName();
        } else {
            final StringBuilder sb = new StringBuilder();
            final String platformType = fileInfo.getPlatformTypeId();
            final String center = fileInfo.getCenterId();
            final String platform = fileInfo.getPlatformId();
            final String level = fileInfo.getLevel();
            final String platformTypeText = DataAccessMatrixJSPUtil.lookupHeaderText(Header.HeaderCategory.PlatformType, platformType);
            sb.append(platformTypeText).append(ConstantValues.SEPARATOR);
            final String centerText = DataAccessMatrixJSPUtil.lookupHeaderText(Header.HeaderCategory.Center, center);
            final String platformText = DataAccessMatrixJSPUtil.lookupHeaderText(Header.HeaderCategory.Platform, platform);
            sb.append(centerText);
            if (platformText != null && platformText.length() > 0) {
                sb.append("__").append(platformText);
            }
            sb.append(ConstantValues.SEPARATOR);
            if (!(fileInfo instanceof DataFileClinical) && !(fileInfo instanceof DataFileMetadata)) {
                sb.append("Level_").append(level).append(ConstantValues.SEPARATOR);
            }
            sb.append(fileInfo.getFileName());
            internalFilePath = sb.toString();
            internalFilePath = internalFilePath.replace("(", "").replace(")", "").replace(" ", "_");
        }
        return internalFilePath;
    }

    public long getActualUncompressedSize() {
        return actualUncompressedSize;
    }

    public void setActualUncompressedSize(final long size) {
        actualUncompressedSize = size;
    }

    /**
     * @return the number of milliseconds it took to do file processing
     */
    public long getFileProcessingTime() {
        if (endFileProcessingTime != 0) {
            return endFileProcessingTime - startTime;
        } else {
            return 0;
        }
    }

    public long getArchiveGenerationTime() {
        if (startArchiveCreationTime != 0) {
            return endTime - startArchiveCreationTime;
        } else {
            return 0;
        }
    }

    public long getTotalTime() {
        return endTime - startTime;
    }

    public long getWaitingInQueueTime() {
        return startTime - filePackagerBean.getCreationTime();
    }
}
