/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.common.service.FileTypeLookup;
import gov.nih.nci.ncicb.tcga.dcc.common.service.StatusCallback;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLoggerI;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.LoaderArchive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.LoaderSDRF;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.LoaderQueriesException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.MailGenerator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.RowCounter;
import org.apache.log4j.Level;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The main class that handles a data-loading job.
 *
 * @author nassaud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Loader implements Serializable {

    //use a static instance of the probeLookup, so if we spawn multiple threads they will use the
    //same one and not have to download the probe names again.
    transient static ProbeLookup probeLookup;

    private String loadDirectory;
    private String magetabDirectory;
    transient private LoaderQueries loaderQueries;
    transient private ArchiveQueries commonArchiveQueries;
    transient private ArchiveQueries diseaseArchiveQueries;
    transient private UUIDDAO uuidDAO;
    private FileTypeLookup ftLookup;
    private StatusCallback statusCallback;
    transient private MailSender mailSender;
    private String mailTo;
    private RowCounter rowCounter, datasetFileRowCounter;
    transient private MailGenerator mailGenerator;
    private LoaderArchive archive, magetabArchive;
    private LoaderSDRF sdrf;
    transient private ProcessLoggerI logger;

    public void setLogger(final ProcessLoggerI logger) {
        this.logger = logger;
    }

    /**
     * Exploded archive directory containing data files.  It may also contain SDRF file.
     *
     * @param loadDirectory the archive directory
     */
    public void setLoadDirectory(final String loadDirectory) {
        this.loadDirectory = loadDirectory;
        if (this.loadDirectory != null) {
            this.loadDirectory = this.loadDirectory.replace("\\", File.separator);
        }
    }

    public String getLoadDirectory() {
        return loadDirectory;
    }

    /**
     * For new-submission-scheme archives, a separate exploded directory containing the SDRF file.
     *
     * @param magetabDirectory the directory where the SDRF can be found (null if same as archive)
     */
    public void setMagetabDirectory(final String magetabDirectory) {
        this.magetabDirectory = magetabDirectory;
        if (this.magetabDirectory != null) {
            this.magetabDirectory = this.magetabDirectory.replace("\\", File.separator);
        }
    }

    public String getMagetabDirectory() {
        return magetabDirectory;
    }

    /**
     * DAO object that does the database inserts.
     *
     * @param loaderQueries the DAO
     */
    public void setLoaderQueries(final LoaderQueries loaderQueries) {
        this.loaderQueries = loaderQueries;
    }

    public void setCommonArchiveQueries(final ArchiveQueries commonArchiveQueries) {
        this.commonArchiveQueries = commonArchiveQueries;
    }

    public void setDiseaseArchiveQueries(final ArchiveQueries diseaseArchiveQueries) {
        this.diseaseArchiveQueries = diseaseArchiveQueries;
    }

    public void setUuidDAO(final UUIDDAO uuidDAO) {
        this.uuidDAO = uuidDAO;
    }

    /**
     * Object provided by the caller which the Loader can call to find out file-type
     * information for each file which the loader thinks might be a data file.
     *
     * @param ftLookup the FileTypeLookup object
     */
    public void setFileTypeLookup(final FileTypeLookup ftLookup) {
        this.ftLookup = ftLookup;
    }

    /**
     * Object provided by the caller which the Loader calls to inform of its status.
     *
     * @param statusCallback the callback for alerting of Loader status
     */
    public void setStatusCallback(final StatusCallback statusCallback) {
        this.statusCallback = statusCallback;
        if (statusCallback != null) {
            statusCallback.sendStatus(StatusCallback.Status.Queued);
        }
    }

    /**
     * Object that connects to a mail server to send email when the Loader is done.
     *
     * @param mailSender the mail sender
     */
    public void setMailSender(final MailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Address of mail recipient.
     *
     * @param mailTo the address of mail recipient
     */
    public void setMailTo(final String mailTo) {
        this.mailTo = mailTo;
    }

    public LoaderArchive getArchive() {
        return archive;
    }

    public void setArchive(LoaderArchive archive) {
        this.archive = archive;
    }

    /**
     * Main method that starts the load process.
     *
     * @throws LoaderException if loading fails
     */
    public void go() throws LoaderException {
        try {
            long startTimeMillis = System.currentTimeMillis();
            logger.logToLogger(Level.INFO, "++++ PROCESSING ARCHIVE IN " + loadDirectory);
            if (statusCallback != null) {
                statusCallback.sendStatus(StatusCallback.Status.Started);
            }
            if (mailSender != null && mailTo != null) {
                mailGenerator = new MailGenerator(mailSender, mailTo, loadDirectory, rowCounter);
                mailGenerator.sendStartEmail();
            }
            checkInputs();
            archive = new LoaderArchive(loadDirectory, ftLookup);
            if (magetabDirectory != null && magetabDirectory.length() > 0) {
                magetabArchive = new LoaderArchive(magetabDirectory, ftLookup);
            }
            //if magetab archive exists, get the sdrf from there
            File sdrfFile = (magetabArchive != null ? magetabArchive.getSDRFFile() : archive.getSDRFFile());
            sdrf = new LoaderSDRF(sdrfFile, new String[]{"Extract Name", "Hybridization Name"});
            setupProbeLookup();

            //all the db inserts happen in here
            boolean allDatasetsSucceeded = loadTheData();
            if (statusCallback != null) {
                statusCallback.sendStatus(StatusCallback.Status.Succeeded);
            }
            printAndEmailRowCounts(startTimeMillis, allDatasetsSucceeded);
        } catch (Exception e) {  //catch anything, in case of runtime exception
            logger.logToLogger(Level.ERROR, "Loader failed for " + loadDirectory);
            logger.logError(e);
            if (mailGenerator != null) {
                mailGenerator.sendFailureEmail(e);
            }
            if (statusCallback != null) {
                statusCallback.sendStatus(StatusCallback.Status.Failed);
            }
            if (e instanceof LoaderException) {
                throw (LoaderException) e;
            } else {
                throw new LoaderException(e);
            }
        }
    }

    //use the same message for log and email

    private void printAndEmailRowCounts(final long startTimeMillis, final boolean allDatasetsSucceeded) {
        double elapsedTimeMinutes = ((double) (System.currentTimeMillis() - startTimeMillis)) / 60000.;
        String elapsedTimeStr = (new DecimalFormat("###.##")).format(elapsedTimeMinutes);
        StringBuilder msg = new StringBuilder();
        if (allDatasetsSucceeded) {
            msg.append("Loader Succeeded. ");
        } else {
            msg.append("At least one dataset failed. ");
        }
        msg.append("Archive directory: ").append(loadDirectory).append("\n\n");
        msg.append("Total rows added:\n");
        msg.append("   experiment: ").append(rowCounter.getRowCount("experiment")).append('\n');
        msg.append("   hybridization_ref: ").append(rowCounter.getRowCount("hybridization_ref")).append('\n');
        msg.append("   data_set: ").append(rowCounter.getRowCount("data_set")).append('\n');
        msg.append("   hybridization_data_group: ").append(rowCounter.getRowCount("hybridization_data_group")).append('\n');
        msg.append("   hybrid_ref_data_set: ").append(rowCounter.getRowCount("hybrid_ref_data_set")).append('\n');
        msg.append("   hybridization_value: ").append(rowCounter.getRowCount("hybridization_value")).append("\n\n");
        msg.append("Total time:\n");
        msg.append("   ").append(elapsedTimeStr).append(" minutes");
        String message = msg.toString();
        logger.logToLogger(Level.INFO, message);
        if (mailGenerator != null) {
            mailGenerator.sendSuccessEmail(message);
        }
    }

    private void setupProbeLookup() throws LoaderQueriesException {
        if (probeLookup == null) {          //a static instance, so we hang on to the probe values for other threads/runs
            synchronized (Loader.class) {    //sync so only one thread creates the instance
                if (probeLookup == null) {
                    probeLookup = new ProbeLookup();
                    probeLookup.setLoaderQueries(loaderQueries);
                    probeLookup.setLogger(logger);
                }
            }
        }
        DiseaseContextHolder.setDisease(archive.getDisease());
        String platform = archive.getPlatform();
        int platformId = loaderQueries.lookupPlatformId(platform);
        //the synchronized method will make sure that only one thread will download the probes
        //for any given platform, and any other thread wanting that platform will wait
        probeLookup.load(platformId);
    }

    private void checkInputs() {
        if (loadDirectory == null || loadDirectory.length() == 0) {
            throw new IllegalArgumentException("loadDirectory cannot be null");
        }
        if (loaderQueries == null) {
            throw new IllegalArgumentException("loaderQueries cannot be null");
        }
        if (ftLookup == null) {
            throw new IllegalArgumentException("fileTypeLookup cannot be null");
        }
    }

    //here is where real work starts to being happening

    private boolean loadTheData() throws LoaderException {
        try {
            DiseaseContextHolder.setDisease(archive.getDisease());
            rowCounter = makeRowCounter();
            int platformId = loaderQueries.lookupPlatformId(archive.getPlatform());
            int centerId = loaderQueries.lookupCenterId(archive.getCenter(), platformId);
            long archiveId = diseaseArchiveQueries.getArchiveIdByName(archive.getArchiveName());
            //insert one experiment per archive
            long experimentId = loaderQueries.lookupExperimentId(archive.getBasename(), archive.getBatch(), archive.getRevision());
            if (experimentId < 0) {
                experimentId = loaderQueries.insertExperiment(archive.getBasename(), archive.getBatch(), archive.getRevision(), centerId, platformId);
                rowCounter.addToRowCounts("experiment");
            }
            archive.setExperimentId(experimentId);

            //insert hybrefs based on sdrf, one per row
            Map<String, Long> hybrefIdsByName = insertHybRefs();

            boolean allDatasetsSucceeded = true;
            for (final String filetype : archive.getFileTypes()) {
                boolean succeeded = loadDataset(centerId, platformId, experimentId, hybrefIdsByName, filetype, archiveId);
                allDatasetsSucceeded = allDatasetsSucceeded & succeeded;
                if (!allDatasetsSucceeded) {
                    break;  // don't load anymore, we have enough to clean up already
                }
            }
            // if all the datasets loaded successfully, set the data_upload_date in the archive_info table
            // in both common and disease schema
            if (allDatasetsSucceeded){
                diseaseArchiveQueries.updateArchiveInfo(archiveId);
                commonArchiveQueries.updateArchiveInfo(archiveId);
            }
            return allDatasetsSucceeded;
        } catch (LoaderQueriesException e) {
            logger.logError(e);
            throw new LoaderException(e);
        }
    }

    protected RowCounter makeRowCounter() {
        return new RowCounter();
    }

    private boolean loadDataset(
            final int centerId, final int platformId, final long experimentId, final Map<String, Long> hybrefIdsByName,
            final String filetype, final long archiveId) {

        logger.logToLogger(Level.INFO, String.format("Loading dataset for filetype %s", filetype));
        boolean loaded = false;
        long datasetId = -1;              // outside of the try{} for error report email
        DataFile currentDataFile = null;  // outside of the try{} for error report email
        try {
            DiseaseContextHolder.setDisease(archive.getDisease());
            datasetId = loaderQueries.insertDataset(experimentId, syntheticFileName(filetype), filetype, archive.getAccessLevel(), 2, centerId, platformId, archiveId);
            rowCounter.addToRowCounts("data_set");
            Map<String, Long> fileInfoIdsByName = loaderQueries.lookupFileInfoData(archiveId);

            //insert hybridization_data_group records based on columns in first data file
            //(assumption is that all files in archive OF THIS TYPE have the same columns) [which is a correct assumption]
            List<DataFile> filesForType = archive.getFilesForType(filetype);
            DataFile firstFile = filesForType.get(0);
            if (firstFile == null) {
                throw new LoaderException(String.format("No data files found for data type %s", filetype));
            }
            List<String> columns = firstFile.getColumnNames();
            Map<String, Long> hybGroupIds = loaderQueries.insertHybDataGroups(datasetId, columns);
            rowCounter.addToRowCounts("hybridization_data_group", columns.size());

            for (final DataFile datafile : archive.getFilesForType(filetype)) {
                currentDataFile = datafile;
                logger.logToLogger(Level.INFO, String.format("Loading data file %s", datafile.getName()));
                long fileInfoId;
                fileInfoId = fileInfoIdsByName.get(datafile.getName());
                loaderQueries.insertDataSetFile(datasetId, datafile.getName(), fileInfoId);
                datasetFileRowCounter = new RowCounter();
                insertHybridizationValues(platformId, experimentId, hybrefIdsByName, datafile, datasetId, hybGroupIds);
                rowCounter.addToRowCounts(datasetFileRowCounter);
                datasetFileRowCounter = null;
                loaderQueries.setDataSetFileLoaded(datasetId, datafile.getName());
            }

            loaderQueries.setDataSetLoaded(datasetId);
            loaded = true;
        } catch (LoaderException e) {
            logger.logError(e);
            String body = generateLoaderExceptionEmailBody(e, datasetId, currentDataFile, centerId, platformId,
                    experimentId, filetype, archiveId);
            mailSender.send(mailTo, null, "LoaderException", body, false);
        } catch (LoaderQueriesException e) {
            logger.logError(e);
            String body = generateLoaderExceptionEmailBody(e, datasetId, currentDataFile, centerId, platformId,
                    experimentId, filetype, archiveId);
            mailSender.send(mailTo, null, "LoaderQueriesException", body, false);
        }
        return loaded;
    }

    private String generateLoaderExceptionEmailBody(final Exception e, final long datasetId, final DataFile currentDataFile,
                                                    final int centerId, final int platformId, final long experimentId,
                                                    final String filetype, final long archiveId) {
        final StringBuilder body = new StringBuilder(e.getClass().getSimpleName());
        body.append(": ");
        body.append(e.getMessage());
        body.append("\nWhile loading dataset with id=");
        body.append(datasetId);
        body.append(" in file ");
        body.append((currentDataFile != null) ? currentDataFile.getName() : "null");
        body.append("\n\nWhere...\ncenterId = ");
        body.append(centerId);
        body.append("\nplatformId = ");
        body.append(platformId);
        body.append("\nexperimentId = ");
        body.append(experimentId);
        body.append("\nfiletype = ");
        body.append(filetype);
        body.append("\narchiveId = ");
        body.append(archiveId);
        body.append("\n\nStack trace:\n");
        body.append(StringUtil.stackTraceAsString(e));
        return body.toString();
    }

    private String syntheticFileName(final String filetype) {
        String ret = null;
        int slash = loadDirectory.lastIndexOf("/");
        if (slash >= 0) {
            ret = loadDirectory.substring(slash + 1);
        }
        ret += ("/*" + filetype);
        return ret;
    }

    private void insertHybridizationValues(
            final int platformId, final long experimentId, final Map<String, Long> hybrefIdsByName,
            final DataFile datafile, final long datasetId, final Map<String, Long> hybGroupIds) throws LoaderException {
        DiseaseContextHolder.setDisease(archive.getDisease());
        ValuesLoader vloader = makeValuesLoader();
        vloader.datafile = datafile;
        vloader.hybGroupIds = hybGroupIds;
        vloader.hybrefIdsByName = hybrefIdsByName;
        vloader.datasetId = datasetId;
        vloader.experimentId = experimentId;
        vloader.platformId = platformId;
        vloader.loaderQueries = loaderQueries;
        vloader.rowCounter = datasetFileRowCounter;
        vloader.insertValues();
    }

    protected ValuesLoader makeValuesLoader() {
        return new ValuesLoader();
    }

    private Map<String, Long> insertHybRefs() throws LoaderQueriesException {
        DiseaseContextHolder.setDisease(archive.getDisease());
        Map<String, Long> hybrefIdsByName = new TreeMap<String, Long>();
        List<String> nonTcgaHybrefs = new ArrayList<String>();
        for (int row = 1; row < sdrf.getRowCount(); row++) {
            String hybRefName = sdrf.getColumnData("Hybridization Name", row);
            String bestbarcode = sdrf.getColumnData("Extract Name", row);
            if (!hybrefIdsByName.containsKey(hybRefName) && isTcgaSample(bestbarcode)) {
                String sampleName = bestbarcode.substring(0, 15);
                long hybrefId;
                String uuid;
                try {
                    uuid = uuidDAO.getUUIDForBarcode(bestbarcode);
                    if (uuid == null) {
                        throw new LoaderQueriesException("The barcode: " + bestbarcode +
                                " does not have a uuid associated with");
                    }
                    hybrefId = loaderQueries.insertHybRef(bestbarcode, sampleName, 0, uuid);
                } catch (LoaderQueriesException e) {
                    if (e.getMessage().contains("unique constraint") || e.getCause().getMessage().contains("unique constraint")) {
                        //maybe two threads tried to load same barcode, second one failed, look up the value
                        hybrefId = loaderQueries.lookupHybRefId(bestbarcode);
                    } else {
                        throw e;
                    }
                }
                rowCounter.addToRowCounts("hybridization_ref");
                hybrefIdsByName.put(hybRefName, hybrefId);
            } else {
                nonTcgaHybrefs.add(hybRefName);
            }
        }
        // any non-tcga hybrefs that don't also map to a tcga barcode go in with null values
        for (String nonTcgaHybref : nonTcgaHybrefs) {
            if (!hybrefIdsByName.containsKey(nonTcgaHybref)) {
                hybrefIdsByName.put(nonTcgaHybref, null);
            }
        }
        return hybrefIdsByName;
    }

    private boolean isTcgaSample(final String bestbarcode) {
        return bestbarcode.startsWith("TCGA-");
    }

}
