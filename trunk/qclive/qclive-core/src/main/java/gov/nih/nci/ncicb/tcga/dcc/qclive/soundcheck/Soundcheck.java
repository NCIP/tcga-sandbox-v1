/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.SampleTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.DataMatrixParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParserImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.ArchiveExpander;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.MageTabExperimentChecker;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.UploadChecker;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ArchiveNameValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ArraySdrfValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.BiospecimenXmlValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ClinicalXmlValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ControlArchiveValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.DNASeqSdrfValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.DataMatrixValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ExperimentValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.GccIdfValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.GscExperimentValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.GscIdfValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MD5Validator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.Maf2FileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MafFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MafFileValidatorDispatcher;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MageTabExperimentValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ManifestValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MiRNASeqFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MiRNASeqIsoformFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MiRnaSeqSdrfValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ProteinArraySdrfValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqExonFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqGeneFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqJunctionFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqRSEMGeneNormalizedFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqRSEMGeneResultsFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqRSEMIsoformFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RNASeqRSEMIsoformNormalizedFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.RnaSeqSdrfValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.SdrfValidatorDispatcher;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.TraceFileValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.VcfValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.remote.RemoteDomainNameValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.remote.RemotePlatformValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.remote.RemoteTumorTypeValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.LoggerDestination;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.LoggerImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.StdoutLoggerDestination;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRUtilsImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidatorRemoteImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.ShippedPortionIdProcessorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileDataLineValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfFileDataLineValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfHeaderDefinitionStorePropertyFileImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc.RemoteCenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.remote.ExperimentQueriesRemoteImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.remote.RemoteCodeTableQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.ChromInfoUtils;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.ChromInfoUtilsSoundCheckImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.QCliveXMLSchemaValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.BiospecimenIdWsQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.ValidationWebServiceQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.impl.ShippedBiospecimenWSQueriesImpl;
import gov.nih.nci.system.applicationservice.ApplicationException;
import org.apache.log4j.Level;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Stand-alone validator for archives.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: waltonj $
 * @version $Rev: 18467 $
 */
public class Soundcheck {

    /* Instance variables */
    private QcContext qcContext = new QcContext();
    private Processor<File, Archive> uploadChecker;
    private boolean verbose = true;
    private Logger logger;
    private String centerType;
    private RemoteValidationHelper remoteValidationHelper;
    private static boolean useRemoteValidation = true;
    private boolean wasSuccessful = false;
    private final ChromInfoUtils chromInfoUtils = new ChromInfoUtilsSoundCheckImpl();

    private static final String MAGETAB_FLAG = "magetab";
    public static final String FILE_PARAMETER_KEY = "archiveFile";
    public static final String VALIDATOR_URL_AND_MESSAGE = "The latest version of the validator can be found here: https://wiki.nci.nih.gov/display/TCGA/Download+TCGA+Archive+Validator";

    private static final String APP_CONTEXT_FILE_NAME = "application-context.xml";
    private static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext(APP_CONTEXT_FILE_NAME);

    /**
     * The standalone validator version as it should be displayed when run.
     */
    private String version;

    public static Map<String, String> parseArgs(final String[] args) {
        Map<String, String> parsedArgs = new HashMap<String, String>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                while (arg.startsWith("-")) {
                    arg = arg.substring(1);
                }
                if (i + 1 == args.length || args[i + 1].startsWith("-")) {
                    // this arg is a flag, no value
                    parsedArgs.put(arg.toLowerCase(), arg);
                } else {
                    parsedArgs.put(arg.toLowerCase(), args[i + 1]);
                    i++;
                }
            } else if (arg.equalsIgnoreCase("bypass")) {
                parsedArgs.put("bypass", "bypass");
            } else if (arg.equalsIgnoreCase("useUUID")) {
                parsedArgs.put("useuuid", "useuuid");
            } else {
                parsedArgs.put(FILE_PARAMETER_KEY, arg);
            }
        }
        return parsedArgs;
    }

    public static void main(final String[] args) {

        final Logger logger = createLogger();
        final Soundcheck soundcheck = (Soundcheck) applicationContext.getBean("soundcheck");
        runSoundcheckWith(soundcheck, args, logger);
    }

    /**
     * Run Soundcheck with the equivalent of command line arguments
     *
     * @param soundcheck a {@link Soundcheck} instance
     * @param args       see @printHelp
     * @param logger a {@link Logger}
     * @return a Soundcheck instance if a it was indeed run, false if there was any issue with the command line
     *         arguments or simply no archives to validate in the directory specified.
     */
    public static Soundcheck runSoundcheckWith(final Soundcheck soundcheck,
                                               final String[] args,
                                               final Logger logger) {
        
        logger.log(Level.INFO, MessageFormat.format("[QC Version {0}]", soundcheck.getVersion()));
        Map<String, String> parsedArgs = parseArgs(args);
        if (!parsedArgs.containsKey(FILE_PARAMETER_KEY) || parsedArgs.containsKey("help") || parsedArgs.containsKey("h")) {
            printHelp(logger);
            return null;
        }
        String filename = parsedArgs.get(FILE_PARAMETER_KEY);
        File archiveFile = new File(filename);
        boolean doBypass = parsedArgs.containsKey("bypass");
        String centerType = parsedArgs.get("centertype");
        useRemoteValidation = !parsedArgs.containsKey("noremote");

        boolean useUUID = parsedArgs.containsKey("useuuid");
        boolean useBarcodes = parsedArgs.containsKey("usebarcode");

        if (useUUID && useBarcodes) {
            // can't use both flags!
            System.out.println("Use either -useuuid or -usebarcode flag but not both");
            return null;
        } else if (!useUUID && !useBarcodes) {
            // have to use at least one flag
            System.out.println("Please specify UUID transition status with either -useuuid or -usebarcode flag");
            return null;
        }

        boolean isUUIDConverted = parsedArgs.containsKey("useuuid");

        if (!archiveFile.exists()) {
            logger.log(Level.ERROR, "File '" + filename + "' not found");
            return null;
        }
        if (!archiveFile.canRead()) {
            logger.log(Level.ERROR, "File '" + filename + "' is not readable");
            return null;
        }
        List<File> filesToValidate = new ArrayList<File>();
        if (archiveFile.isDirectory()) {
            // Look for all tar and tar.gz files in directory and add them to the list
            final String directoryName = archiveFile.getAbsolutePath();
            filesToValidate.addAll(getReadableFiles(directoryName, ConstantValues.COMPRESSED_ARCHIVE_EXTENSION, logger));
            filesToValidate.addAll(getReadableFiles(directoryName, ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION, logger));

            if (filesToValidate.size() == 0) {
                logger.log(Level.ERROR, "No archive files were found in directory " + archiveFile);
                return null;
            }
        } else {
            // argument was a single archive file, not a directory, so just add it
            filesToValidate.add(archiveFile);
        }
        // create context object
        QcContext qcContext = new QcContext();
        qcContext.setNoRemote(true);
        qcContext.setLogger(logger);
        soundcheck.setCenterType(centerType);
        soundcheck.setQcContext(qcContext);
        soundcheck.setLogger(logger);
        if (useRemoteValidation) {
            try {
                soundcheck.setRemoteValidationHelper(new RemoteValidationHelperCaCoreImpl());
                // if successfully set remote validator helper, not considered stand-alone
                qcContext.setNoRemote(false);
            } catch (Throwable e) {
                logger.log(Level.WARN, "Unable to use web service for remote validation, running stand-alone validation. As a result a few things " +
                        "cannot be validated for sure and will be unknown until the archive is submitted.");
                qcContext.addWarning("Unable to use web service for remote validation -- running stand-alone");
            }
        }

        qcContext.setCenterConvertedToUUID(isUUIDConverted);

        final boolean requiresMageTab = parsedArgs.containsKey(MAGETAB_FLAG);
        qcContext.setExperimentRequiresMageTab(requiresMageTab);

        // run soundcheck on the list of files
        soundcheck.execute(filesToValidate, doBypass);
        return soundcheck;
    }

    /**
     * Return a list of all readable files in the given directory with the given extension.
     *
     * @param directoryName the directory name
     * @param fileExtension the file extension
     * @param logger        a logger to log errors
     * @return a list of all readable files in the given directory with the given extension
     */
    private static List<File> getReadableFiles(final String directoryName,
                                               final String fileExtension,
                                               final Logger logger) {

        final List<File> result = new ArrayList<File>();
        final File[] files = DirectoryListerImpl.getFilesByExtension(directoryName, fileExtension);

        for (final File file : files) {
            if (file.canRead()) {
                result.add(file);
            } else {
                logger.log(Level.ERROR, "File '" + file.getName() + "' is not readable");
            }
        }

        return result;
    }

    private static Logger createLogger() {
        Logger logger = new LoggerImpl();
        LoggerDestination stoutDest = new StdoutLoggerDestination();
        stoutDest.setMinLevel(Level.INFO);
        logger.addDestination(stoutDest);
        return logger;
    }

    /**
     * Print the help message on the standard output.
     *
     * @param logger a standard output logger
     */
    private static void printHelp(final Logger logger) {
        logger.log(Level.INFO, getHelpMessage());
    }

    /**
     * Return the help message.
     *
     * @return the help message
     */
    protected static String getHelpMessage() {

        return new StringBuilder("\nUsage: validate[.sh/.bat] 'archive or directory to validate' -[useuuid|usebarcode] [-bypass] [-noremote] [-centertype CGCC|GSC|BCR|GDAC] [-magetab]\n")
                .append("\nIf the first argument is a directory, all files in the directory ending with '")
                .append(ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION).append("' and '").append(ConstantValues.COMPRESSED_ARCHIVE_EXTENSION).append("' will be validated.\n")
                .append("\nFor CGCC submissions, the first argument MUST be a directory containing all archives you intend to submit, including the updated mage-tab archive. \n")
                .append("For non-CGCC archives, the argument may be either a single archive file or a directory containing multiple archives.\n")
                .append("\nDuring the UUID Transition, either -useuuid or -usebarcode must be specified. If -useuuid is used the validator will assume the archive has been converted to use UUIDs.\n")
                .append("\nIf the -bypass flag is set, MD5 checks will be skipped, and existing expanded directories will be used.\n")
                .append("Once your archive passes using the -bypass flag, please run it again without -bypass to verify the final MD5 value.\n")
                .append("\nWithout the -noremote flag, the validator will use the DCC web service to validate certain parts of the archive.\n")
                .append("If you are running without an internet connection or are getting errors related to connecting to the DCC web service, run with the -noremote to disable this feature.\n")
                .append("\nThe -centertype argument is optional; the program will normally get the center type from the DCC web service or based on the center/platform name. Valid values are CGCC, GSC, BCR, or GDAC.\n")
                .append("\nThe -magetab flag is optional; For GSC archives, it indicates that a mage-tab archive must be provided (unless the GSC archive does not contain MAF files)\n")
                .append("\n\n" + VALIDATOR_URL_AND_MESSAGE + "\n")
                .toString();
    }


    /**
     * @param experimentFiles the archive files to validate
     * @param bypass          if true, skip MD5 and archive expander steps
     */
    public void execute(final List<File> experimentFiles, final boolean bypass) {
        uploadChecker = new UploadChecker();
        initUploadChecker(uploadChecker, bypass);
        qcContext.setStandaloneValidator(true);
        try {
            // first expand and run upload checker on all archives
            Map<String, Experiment> experiments = new HashMap<String, Experiment>();
            boolean ok = true;
            for (final File archiveFile : experimentFiles) {
                if (verbose) {
                    logger.log(Level.INFO, "Unpacking " + archiveFile.getName());
                }
                Archive archive = checkArchiveFile(archiveFile);
                if (archive != null && archive.getDeployStatus().equals(Archive.STATUS_UPLOADED)) {
                    Experiment experiment = experiments.get(archive.getExperimentName());
                    if (experiment == null) {
                        experiment = new Experiment();
                        experiment.setName(archive.getExperimentName());
                        experiments.put(archive.getExperimentName(), experiment);
                    }
                    setupArchive(experiment, archiveFile, archive);
                } else {
                    ok = false;
                }
            }

            // now, if upload checkers all passed, do detailed validation on each archive
            if (ok) {
                for (final Experiment experiment : experiments.values()) {
                    if (remoteValidationHelper != null) {
                        addExistingArchivesToExperiment(experiment);
                    }

                    if (verbose) {
                        logger.log(Level.INFO, "Beginning validation of " + experiment.getName() + "...");
                    }
                    ExperimentValidator validator = makeValidator(experiment.getType(), bypass);
                    if (validator.execute(experiment, qcContext) && qcContext.getErrorCount() == 0) {
                        success(bypass);
                    } else {
                        failure();
                    }
                }
            } else {
                failure();
            }
        } catch (Processor.ProcessorException e) {
            qcContext.addError(e.getMessage());
            failure();

        } catch (Throwable e) {
            if ("Too many errors. Stopped processing the archives.".equals(e.getMessage())) {
                logger.log(Level.ERROR, "Too many errors. Stopped validation.");
            } else {
                reportException(e);
            }
            failure();
        }
    }

    // used by CLIDE
    public QcContext getQcContext() {
        return qcContext;
    }

    protected void addExistingArchivesToExperiment(final Experiment experiment) throws Processor.ProcessorException {
        logger.log(Level.INFO, "Getting information about currently available archives from the DCC...");
        try {
            List<gov.nih.nci.ncicb.tcga.dccws.Archive> latestArchives = remoteValidationHelper.getLatestArchives(experiment.getTumorName(), experiment.getCenterName(), experiment.getPlatformName());
            for (final gov.nih.nci.ncicb.tcga.dccws.Archive wsArchive : latestArchives) {
                Archive existingArchive = new Archive(wsArchive.getDeployLocation());
                existingArchive.setRealName(wsArchive.getName());
                existingArchive.setDeployStatus(wsArchive.getDeployStatus());
                existingArchive.setDeployLocation(wsArchive.getDeployLocation());
                existingArchive.setSerialIndex(String.valueOf(wsArchive.getSerialIndex()));
                existingArchive.setRevision(String.valueOf(wsArchive.getRevision()));
                existingArchive.setExperimentType(centerType);
                // for now figure out archive type based on name
                Matcher nameMatcher = ArchiveNameValidator.ARCHIVE_NAME_PATTERN.matcher(existingArchive.getRealName());
                if (nameMatcher.matches()) {
                    existingArchive.setArchiveType(nameMatcher.group(ArchiveNameValidator.INDEX_IN_ARCHIVE_NAME_ARCHIVE_TYPE));
                } else {
                    existingArchive.setArchiveType(Archive.TYPE_CLASSIC);
                }

                if (existingArchive.getDeployStatus().equals(Archive.STATUS_AVAILABLE)) {
                    boolean foundReplacement = false;
                    for (final Archive newArchive : experiment.getArchives()) {
                        if ((newArchive.getArchiveType().equals(existingArchive.getArchiveType()) || existingArchive.getArchiveType().equals(Archive.TYPE_CLASSIC))
                                && newArchive.getSerialIndex().equals(existingArchive.getSerialIndex())) {
                            if (Integer.valueOf(newArchive.getRevision()) < Integer.valueOf(existingArchive.getRevision())) {
                                throw new Processor.ProcessorException(new StringBuilder().append("Archive ").
                                        append(newArchive.getRealName()).append(" can not be submitted, because archive ").append(existingArchive.getRealName()).
                                        append(", which has a higher revision for the same serial index, has already been deposited and processed by the DCC.").toString());
                            } else if (newArchive.getRevision().equals(existingArchive.getRevision())) {
                                throw new Processor.ProcessorException("Archive " + existingArchive.getRealName() + " has already been deposited and processed by the DCC; please increment revision.");
                            }
                            experiment.addPreviousArchive(existingArchive);
                            foundReplacement = true;
                        }
                    }
                    if (!foundReplacement) {
                        experiment.addArchive(existingArchive);
                    }
                }
            }
        } catch (ApplicationException e) {
            throw new Processor.ProcessorException(e.getMessage(), e);
        }
    }

    protected void setupArchive(final Experiment experiment, final File archiveFile, final Archive archive) throws ApplicationException {
        // set real name based on parsed bits of name
        archive.setRealName(new StringBuilder().append(archive.getDomainName()).append("_").append(archive.getTumorType()).append(".").append(archive.getPlatform()).append(".").append(archive.getArchiveType()).append(".").append(archive.getSerialIndex()).append(".").append(archive.getRevision()).append(".0").toString());
        archive.setDeployLocation(archiveFile.getAbsolutePath());
        if (centerType != null) {
            archive.setExperimentType(centerType);
        } else {
            archive.setExperimentType(detectExperimentType(archive.getDomainName(), archive.getPlatform()));
        }
        experiment.setType(archive.getExperimentType());
        experiment.addArchive(archive);
        experiment.setName(new StringBuilder().append(archive.getDomainName()).append("_").append(archive.getTumorType()).append(".").append(archive.getPlatform()).toString());
    }

    protected void initUploadChecker(final Processor<File, Archive> uploadChecker, final boolean bypass) {
        if (!bypass) {
            uploadChecker.addInputValidator(new MD5Validator());
        }
        uploadChecker.addOutputValidator(new ArchiveNameValidator());
        if (remoteValidationHelper != null) {
            uploadChecker.addOutputValidator(new RemoteDomainNameValidator(remoteValidationHelper));
            uploadChecker.addOutputValidator(new RemotePlatformValidator(remoteValidationHelper));
            uploadChecker.addOutputValidator(new RemoteTumorTypeValidator(remoteValidationHelper));
        }
        if (!bypass) {
            uploadChecker.addPostProcessor(new ArchiveExpander());
        }
    }

    private boolean reportException(final Throwable e) {
        logger.log(Level.ERROR, "FAILURE: An unexpected error occurred: " + e.getMessage());
        e.printStackTrace();
        logger.log(Level.ERROR, "If this problem persists, please report it to the DCC.");
        return false;
    }

    protected ExperimentValidator makeValidator(final String type, final boolean bypass) {

        final ExperimentValidator validator = new ExperimentValidator();
        final ManifestValidator manifestValidator = new ManifestValidator(new ManifestParserImpl());
        manifestValidator.setDoMd5Check(!bypass);
        validator.addListProcessor(manifestValidator);

        final QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator = getBarcodeValidator();
        final BarcodeTumorValidator barcodeTumorValidator = getBarcodeTumorValidator();

        final VcfValidator vcfValidator = new VcfValidator() {
            public VcfFileDataLineValidator getVcfFileDataLineValidator() {
                return new TcgaVcfFileDataLineValidatorImpl();
            }
        };
        vcfValidator.setTcgaVcfVersion("1.1");
        vcfValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);

        final TcgaVcfFileHeaderValidator vcfFileHeaderValidator = new TcgaVcfFileHeaderValidator();
        vcfFileHeaderValidator.setVcfHeaderDefinitionStore(new VcfHeaderDefinitionStorePropertyFileImpl(logger));
        vcfValidator.setVcfFileHeaderValidator(vcfFileHeaderValidator);

        if (Experiment.TYPE_CGCC.equals(type)) {

            final CenterQueries standaloneCenterQueries = makeStandaloneCenterQueries(true);

            final MageTabExperimentChecker mageTabExperimentChecker = new MageTabExperimentChecker();
            mageTabExperimentChecker.setCenterQueries(standaloneCenterQueries);
            final ManifestParserImpl manifestParser = new ManifestParserImpl();
            mageTabExperimentChecker.setManifestParser(manifestParser);

            validator.addInputValidator(mageTabExperimentChecker);

            final MageTabExperimentValidator mageTabExperimentValidator = new MageTabExperimentValidator();
            mageTabExperimentValidator.setRemote(true);
            mageTabExperimentValidator.setMatrixParser(new DataMatrixParser());
            mageTabExperimentValidator.setMatrixValidator(new DataMatrixValidator());
            mageTabExperimentValidator.setManifestParser(manifestParser);

            if (remoteValidationHelper != null) {
                mageTabExperimentValidator.setExperimentQueries(new ExperimentQueriesRemoteImpl(remoteValidationHelper));
            }

            validator.addInputValidator(mageTabExperimentValidator);

            final ArraySdrfValidator arraySdrfValidator = new ArraySdrfValidator();
            arraySdrfValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);

            final RnaSeqSdrfValidator rnaSeqSdrfValidator = new RnaSeqSdrfValidator();
            rnaSeqSdrfValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);

            final MiRnaSeqSdrfValidator miRnaSeqSdrfValidator = new MiRnaSeqSdrfValidator();
            miRnaSeqSdrfValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);

            final ProteinArraySdrfValidator proteinArraySdrfValidator = new ProteinArraySdrfValidator();
            proteinArraySdrfValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);

            final SdrfValidatorDispatcher sdrfValidatorDispatcher = new SdrfValidatorDispatcher();
            sdrfValidatorDispatcher.setArraySdrfValidator(arraySdrfValidator);
            sdrfValidatorDispatcher.setRnaSeqSdrfValidator(rnaSeqSdrfValidator);
            sdrfValidatorDispatcher.setMiRnaSeqSdrfValidator(miRnaSeqSdrfValidator);
            sdrfValidatorDispatcher.setProteinArraySdrfValidator(proteinArraySdrfValidator);
            arraySdrfValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);
            validator.addListProcessor(sdrfValidatorDispatcher);
            validator.addListProcessor(new GccIdfValidator());

            final MessagePropertyType rnaSeqDataFileValidationErrorMessagePropertyType = MessagePropertyType.RNA_SEQ_DATA_FILE_VALIDATION_ERROR;

            final RNASeqGeneFileValidator rnaSeqGeneFileValidator = new RNASeqGeneFileValidator();
            rnaSeqGeneFileValidator.setChromInfoUtils(chromInfoUtils);
            rnaSeqGeneFileValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);
            rnaSeqGeneFileValidator.setSeqDataFileValidationErrorMessagePropertyType(rnaSeqDataFileValidationErrorMessagePropertyType);
            rnaSeqGeneFileValidator.setBarcodeTumorValidator(barcodeTumorValidator);
            validator.addListProcessor(rnaSeqGeneFileValidator);

            final RNASeqExonFileValidator rnaSeqExonFileValidator = new RNASeqExonFileValidator();
            rnaSeqExonFileValidator.setChromInfoUtils(chromInfoUtils);
            rnaSeqExonFileValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);
            rnaSeqExonFileValidator.setSeqDataFileValidationErrorMessagePropertyType(rnaSeqDataFileValidationErrorMessagePropertyType);
            rnaSeqExonFileValidator.setBarcodeTumorValidator(barcodeTumorValidator);
            validator.addListProcessor(rnaSeqExonFileValidator);

            final RNASeqJunctionFileValidator rnaSeqJunctionFileValidator = new RNASeqJunctionFileValidator();
            rnaSeqJunctionFileValidator.setChromInfoUtils(chromInfoUtils);
            rnaSeqJunctionFileValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);
            rnaSeqJunctionFileValidator.setSeqDataFileValidationErrorMessagePropertyType(rnaSeqDataFileValidationErrorMessagePropertyType);
            rnaSeqJunctionFileValidator.setBarcodeTumorValidator(barcodeTumorValidator);
            validator.addListProcessor(rnaSeqJunctionFileValidator);

            final RNASeqRSEMGeneNormalizedFileValidator rnaSeqRSEMGeneNormalizedFileValidator = new RNASeqRSEMGeneNormalizedFileValidator();
            rnaSeqRSEMGeneNormalizedFileValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);
            rnaSeqRSEMGeneNormalizedFileValidator.setSeqDataFileValidationErrorMessagePropertyType(rnaSeqDataFileValidationErrorMessagePropertyType);
            rnaSeqRSEMGeneNormalizedFileValidator.setBarcodeTumorValidator(barcodeTumorValidator);
            validator.addListProcessor(rnaSeqRSEMGeneNormalizedFileValidator);

            final RNASeqRSEMGeneResultsFileValidator rnaSeqRSEMGeneResultsFileValidator = new RNASeqRSEMGeneResultsFileValidator();
            rnaSeqRSEMGeneResultsFileValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);
            rnaSeqRSEMGeneResultsFileValidator.setSeqDataFileValidationErrorMessagePropertyType(rnaSeqDataFileValidationErrorMessagePropertyType);
            rnaSeqRSEMGeneResultsFileValidator.setBarcodeTumorValidator(barcodeTumorValidator);
            validator.addListProcessor(rnaSeqRSEMGeneResultsFileValidator);

            final RNASeqRSEMIsoformNormalizedFileValidator rnaSeqRSEMIsoformNormalizedFileValidator = new RNASeqRSEMIsoformNormalizedFileValidator();
            rnaSeqRSEMIsoformNormalizedFileValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);
            rnaSeqRSEMIsoformNormalizedFileValidator.setSeqDataFileValidationErrorMessagePropertyType(rnaSeqDataFileValidationErrorMessagePropertyType);
            rnaSeqRSEMIsoformNormalizedFileValidator.setBarcodeTumorValidator(barcodeTumorValidator);
            validator.addListProcessor(rnaSeqRSEMIsoformNormalizedFileValidator);

            final RNASeqRSEMIsoformFileValidator rnaSeqRSEMIsoformFileValidator = new RNASeqRSEMIsoformFileValidator();
            rnaSeqRSEMIsoformFileValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);
            rnaSeqRSEMIsoformFileValidator.setSeqDataFileValidationErrorMessagePropertyType(rnaSeqDataFileValidationErrorMessagePropertyType);
            rnaSeqRSEMIsoformFileValidator.setBarcodeTumorValidator(barcodeTumorValidator);
            validator.addListProcessor(rnaSeqRSEMIsoformFileValidator);

            final MessagePropertyType miRnaSeqDataFileValidationErrorMessagePropertyType = MessagePropertyType.MIRNA_SEQ_DATA_FILE_VALIDATION_ERROR;

            final MiRNASeqFileValidator miRNASeqFileValidator = new MiRNASeqFileValidator();
            miRNASeqFileValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);
            miRNASeqFileValidator.setSeqDataFileValidationErrorMessagePropertyType(miRnaSeqDataFileValidationErrorMessagePropertyType);
            miRNASeqFileValidator.setBarcodeTumorValidator(barcodeTumorValidator);
            validator.addListProcessor(miRNASeqFileValidator);

            final MiRNASeqIsoformFileValidator miRNASeqIsoformFileValidator = new MiRNASeqIsoformFileValidator();
            miRNASeqIsoformFileValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);
            miRNASeqIsoformFileValidator.setSeqDataFileValidationErrorMessagePropertyType(miRnaSeqDataFileValidationErrorMessagePropertyType);
            miRNASeqIsoformFileValidator.setBarcodeTumorValidator(barcodeTumorValidator);
            validator.addListProcessor(miRNASeqIsoformFileValidator);

            validator.addListProcessor(vcfValidator);

        } else if (Experiment.TYPE_GSC.equals(type)) {

            //Input Validators
            final CenterQueries standaloneCenterQueries = makeStandaloneCenterQueries(qcContext.experimentRequiresMageTab());

            final MageTabExperimentChecker mageTabExperimentChecker = new MageTabExperimentChecker();
            mageTabExperimentChecker.setCenterQueries(standaloneCenterQueries);
            final ManifestParserImpl manifestParser = new ManifestParserImpl();
            mageTabExperimentChecker.setManifestParser(manifestParser);

            validator.addInputValidator(mageTabExperimentChecker);

            final MageTabExperimentValidator mageTabExperimentValidator = new MageTabExperimentValidator();
            mageTabExperimentValidator.setRemote(true);
            mageTabExperimentValidator.setMatrixParser(new DataMatrixParser());
            mageTabExperimentValidator.setMatrixValidator(new DataMatrixValidator());
            mageTabExperimentValidator.setManifestParser(manifestParser);

            if (remoteValidationHelper != null) {
                mageTabExperimentValidator.setExperimentQueries(new ExperimentQueriesRemoteImpl(remoteValidationHelper));
            }

            validator.addInputValidator(mageTabExperimentValidator);
            validator.addInputValidator(new GscExperimentValidator());

            //Processors
            MafFileValidatorDispatcher mafFileValidatorDispatcher = new MafFileValidatorDispatcher();
            mafFileValidatorDispatcher.setDefaultSpecVersion("1.0");
            MafFileValidator mafValidator1 = new MafFileValidator();
            Maf2FileValidator mafValidator2 = new Maf2FileValidator();


            // OK to hardcode SampleType per APPS-3072
            final SampleTypeQueries localSampleTypeQueries = new SampleTypeQueries() {
                @Override
                public List<SampleType> getAllSampleTypes() {
                    final List<SampleType> sampleList = new ArrayList<SampleType>();
                    for (int i = 1; i <= 99; i++) {
                        SampleType type = new SampleType();
                        if (i < 10) {
                            type.setSampleTypeCode("0" + i);
                            type.setIsTumor(true);
                        } else {
                            type.setSampleTypeCode("" + i);
                            type.setIsTumor(false);
                        }
                        type.setDefinition("");
                        sampleList.add(type);
                    }
                    return sampleList;
                }
            };
            
            mafValidator1.setSampleTypeQueries(localSampleTypeQueries);
            mafValidator2.setSampleTypeQueries(localSampleTypeQueries);
            
            final ShippedBiospecimenWSQueriesImpl shippedBiospecimenWSQueries = 
                    (ShippedBiospecimenWSQueriesImpl) applicationContext.getBean("shippedBiospecimenWSQueries");
            shippedBiospecimenWSQueries.setQcContext(qcContext);
            shippedBiospecimenWSQueries.setUseRemoteService(useRemoteValidation);
            shippedBiospecimenWSQueries.setLogger(logger);
            
            mafValidator1.setShippedBiospecimenQueries(shippedBiospecimenWSQueries);
            mafValidator2.setShippedBiospecimenQueries(shippedBiospecimenWSQueries);
            
            mafValidator1.setChromInfoUtils(chromInfoUtils);
            mafValidator2.setChromInfoUtils(chromInfoUtils);
            
            mafValidator1.setBarcodeValidator(qcLiveBarcodeAndUUIDValidator);
            mafValidator2.setBarcodeValidator(qcLiveBarcodeAndUUIDValidator);

            if (remoteValidationHelper != null) {
                RemoteCenterQueries remoteCenterQueries = new RemoteCenterQueries();
                remoteCenterQueries.setRemoteValidationHelper(remoteValidationHelper);
                mafValidator2.setCenterQueries(remoteCenterQueries);
            }

            mafFileValidatorDispatcher.addMafHandler(mafValidator1, "1.0");
            mafFileValidatorDispatcher.addMafHandler(mafValidator2, "2.3");

            validator.addListProcessor(mafFileValidatorDispatcher);
            validator.addListProcessor(new TraceFileValidator(qcLiveBarcodeAndUUIDValidator));
            validator.addListProcessor(vcfValidator);

            final DNASeqSdrfValidator dnaSeqSdrfValidator = new DNASeqSdrfValidator();
            dnaSeqSdrfValidator.setQcLiveBarcodeAndUUIDValidator(qcLiveBarcodeAndUUIDValidator);
            validator.addListProcessor(dnaSeqSdrfValidator);
            validator.addListProcessor(new GscIdfValidator());

        } else if (Experiment.TYPE_BCR.equals(type)) {

            final ClinicalXmlValidator xmlVal = new ClinicalXmlValidator(qcLiveBarcodeAndUUIDValidator);
            xmlVal.setBcrUtils(new BCRUtilsImpl());
            xmlVal.setDatesToValidateString("birth,last_known_alive,death,last_followup,initial_pathologic_diagnosis,tumor_progression,tumor_recurrence,new_tumor_event_after_initial_treatment,additional_surgery_locoregional_procedure,additional_surgery_metastatic_procedure,form_completion,procedure,radiation_treatment_start,radiation_treatment_end,drug_treatment_start,drug_treatment_end,radiation_therapy_start,radiation_therapy_end,drug_therapy_start,drug_therapy_end,collection,sample_procurement,shipment,creation");

            // Setting the dates to compare
            final String datesToCompareAsString = "last_followup>=initial_pathologic_diagnosis";
            try {
                xmlVal.setDateComparatorsString(datesToCompareAsString);
            } catch (final Exception e) {
                System.out.println("Could not set the dates to compare with the following argument: '" + datesToCompareAsString + "'");
                e.printStackTrace();
            }

            BCRIDProcessorImpl bcridProcessor = new BCRIDProcessorImpl();
            bcridProcessor.setAliquotElementXPath("//aliquots/aliquot");
            bcridProcessor.setAliquotBarcodeElement("bcr_aliquot_barcode");
            bcridProcessor.setAliquotUuidElement("bcr_aliquot_uuid");
            bcridProcessor.setShipDayElement("day_of_shipment");
            bcridProcessor.setShipMonthElement("month_of_shipment");
            bcridProcessor.setShipYearElement("year_of_shipment");
            bcridProcessor.setShippedPortionElementXPath("//portions/portion");
            bcridProcessor.setShippedPortionBarcodeElement("shipment_portion_bcr_aliquot_barcode");
            bcridProcessor.setShippedPortionUuidElement("bcr_shipment_portion_uuid");
            bcridProcessor.setShippedPortionShipDayElement("day_of_shipment");
            bcridProcessor.setShippedPortionShipMonthElement("month_of_shipment");
            bcridProcessor.setShippedPortionShipYearElement("year_of_shipment");
            xmlVal.setBcridProcessor(bcridProcessor);

            ShippedPortionIdProcessorImpl shippedPortionIdProcessor = new ShippedPortionIdProcessorImpl();
            shippedPortionIdProcessor.setShipmentPortionPath("//portions/shipment_portion");
            xmlVal.setShippedPortionIdProcessor(shippedPortionIdProcessor);

            xmlVal.setDayOfPrefix("day_of_");
            xmlVal.setMonthOfPrefix("month_of_");
            xmlVal.setYearOfPrefix("year_of_");
            xmlVal.setAllowLocalSchema(false);
            xmlVal.setShipmentPortionPath("//portions/shipment_portion");
            xmlVal.setBcrShipmentPortionUuidElementName("bcr_shipment_portion_uuid");
            xmlVal.setCenterIdElementName("center_id");
            xmlVal.setPlateIdElementName("plate_id");
            xmlVal.setShipmentPortionBcrAliquotBarcodeElementName("shipment_portion_bcr_aliquot_barcode");

            QCliveXMLSchemaValidator xsdValidator = new QCliveXMLSchemaValidator();
            xsdValidator.setValidXsdDomainPattern("tcga-data\\.nci\\.nih\\.gov");
            xsdValidator.setValidXsdPrefixPattern("bcr");
            xsdValidator.setValidXsdVersionPattern("2\\.6(\\.\\d*)?");
            xmlVal.setqCliveXMLSchemaValidator(xsdValidator);

            validator.addListProcessor(xmlVal);

            final BiospecimenXmlValidator biospecimenXmlValidator = new BiospecimenXmlValidator();
            biospecimenXmlValidator.setBcrUtils(new BCRUtilsImpl());
            biospecimenXmlValidator.setCodeTableQueries(new RemoteCodeTableQueries(remoteValidationHelper));

            validator.addListProcessor(biospecimenXmlValidator);

            final ControlArchiveValidator controlArchiveValidator = new ControlArchiveValidator();
            validator.addListProcessor(controlArchiveValidator);

        } else if (Experiment.TYPE_GDAC.equals(type)) {
            // nothing to add for now for GDAC validation -- we only need the manifest validator

        } else {
            throw new IllegalArgumentException("Center type '" + type + "' is not valid; must be CGCC, GSC, BCR, or GDAC.");
        }
        return validator;
    }

    /**
     * Return a dummy {@link CenterQueries} object for the standalone.
     * The only method actually implemented is doesCenterRequireMageTab() which returns the provided value.
     *
     * @param experimentRequiresMageTab value to be returned by a call to doesCenterRequireMageTab()
     * @return a dummy {@link CenterQueries} object for the standalone
     */
    private CenterQueries makeStandaloneCenterQueries(final boolean experimentRequiresMageTab) {

        return new CenterQueries() {
            @Override
            public Integer findCenterId(final String centerName,
                                        final String centerType) {
                return null;
            }

            @Override
            public Collection<Map<String, Object>> getAllCenters() {
                return null;
            }

            @Override
            public List<Center> getCenterList() {
                return null;
            }

            @Override
            public List<Center> getRealCenterList() {
                return null;
            }

            @Override
            public Center getCenterById(final Integer centerId) {
                return null;
            }

            @Override
            public Center getCenterByName(final String centerName,
                                          final String centerTypeCode) {
                return null;
            }

            @Override
            public Integer getCenterIdForBCRCenter(final String bcrCenterCode) {
                return null;
            }

            @Override
            public List<Center> getConvertedToUUIDCenters() {
                return null;
            }

            @Override
            public boolean isCenterCenvertedToUUID(final Center center) {
                return false;
            }

            @Override
            public boolean isCenterConvertedToUUID(final String centerName,
                                                   final String centerTypeCode) {
                return false;
            }

            @Override
            public boolean doesCenterRequireMageTab(final String centerName,
                                                    final String centerTypeCode) {
                return experimentRequiresMageTab;
            }
        };
    }

    protected String detectExperimentType(final String domain, final String platform) throws ApplicationException {
        if (remoteValidationHelper != null) {
            centerType = remoteValidationHelper.getCenterTypeForPlatform(platform);
            return centerType;
        } else {
            if (platform.equals("bio")) {
                return Experiment.TYPE_BCR;
            } else if (domain.equals("jhu-usc.edu") || domain.equals("lbl.gov") || domain.equals("mskcc.org") ||
                    domain.equals("hudsonalpha.org") || domain.equals("unc.edu") || domain.equals("hms.harvard.edu")) {
                return Experiment.TYPE_CGCC;
            } else if (domain.equals("broad.mit.edu")) {
                if (platform.equals("ABI") || platform.equals("454") || platform.equals("IlluminaGA_DNASeq")) {
                    return Experiment.TYPE_GSC;
                } else {
                    return Experiment.TYPE_CGCC;
                }
            } else if (domain.equals("hgsc.bcm.edu") || domain.equals("genome.wustl.edu")) {
                return Experiment.TYPE_GSC;
            } else if (domain.equals("intgen.org") || domain.equals("nationwidechildrens.org")) {
                return Experiment.TYPE_BCR;
            } else if (platform.equals("fh_analyses") || platform.equals("fh_stddata") || platform.equals("fh_reports")) {
                return Experiment.TYPE_GDAC;
            } else {
                throw new IllegalArgumentException("Unable to automatically determine center type. Please rerun validator, using -centerType flag, giving center type (CGCC, GSC, BCR, or GDAC) as flag value.");
            }
        }
    }

    protected Archive checkArchiveFile(final File archiveFile) throws Processor.ProcessorException {
        Archive archive = uploadChecker.execute(archiveFile, qcContext);
        if (!archive.getArchiveType().equals(Archive.TYPE_AUX) &&
                !archive.getArchiveType().equals(Archive.TYPE_LEVEL_1) &&
                !archive.getArchiveType().equals(Archive.TYPE_LEVEL_2) &&
                !archive.getArchiveType().equals(Archive.TYPE_LEVEL_3) &&
                !archive.getArchiveType().equals(Archive.TYPE_LEVEL_4) &&
                !archive.getArchiveType().equals(Archive.TYPE_MAGE_TAB)
                ) {
            qcContext.addError(new StringBuilder().append("Type '").
                    append(archive.getArchiveType()).append("' is not valid.  ").append("Type must be one of: ").
                    append(Archive.TYPE_AUX).append(", ").append(Archive.TYPE_LEVEL_1).append(", ").
                    append(Archive.TYPE_LEVEL_2).append(", ").append(Archive.TYPE_LEVEL_3).append(", ").
                    append(Archive.TYPE_LEVEL_4).append(", or ").append(Archive.TYPE_MAGE_TAB).append("\t[archive ").append(archive.getArchiveName()).append("]").toString());
        }
        return archive;
    }

    protected boolean success(final boolean bypass) {
        wasSuccessful = true;
        if (qcContext.getWarningCount() > 0) {
            logger.log(Level.INFO, "\nValidation passed with warnings.\n\nWarnings:");
            for (final String warning : qcContext.getWarnings()) {
                logger.log(Level.INFO, "- " + warning);
            }
        } else {
            logger.log(Level.INFO, "\nValidation passed with no errors or warnings.");
        }
        if (bypass) {
            logger.log(Level.INFO, "\nNOTE: bypass was enabled for this validation.  Remember to update your MD5s and re-run the validator without bypass before submitting.");
        }
        return true;
    }

    protected boolean failure() {
        logger.log(Level.INFO, "Validation failed\n");
        if (qcContext.getErrorCount() > 0) {
            logger.log(Level.INFO, "\nErrors:\n");
            for (final String errorStr : qcContext.getErrors()) {
                logger.log(Level.INFO, "- " + errorStr + "\n");
            }
        } else {
            logger.log(Level.INFO, "No specific errors were recorded.  Please contact the DCC for assistance.\n");
        }
        if (qcContext.getWarningCount() > 0) {
            logger.log(Level.INFO, "\n\nWarnings:\n");
            for (final String warning : qcContext.getWarnings()) {
                logger.log(Level.INFO, "- " + warning + "\n");
            }
        }
        logger.log(Level.INFO, "\nPlease address the above issues before submitting the archives to the DCC.");
        logger.log(Level.INFO, "\n\n" + VALIDATOR_URL_AND_MESSAGE);

        return false;
    }

    /**
     * Returns a dummy object for -noremote validation, ws stub otherwise
     *
     * @return
     */
    private QcLiveBarcodeAndUUIDValidator getBarcodeValidator() {
        QcLiveBarcodeAndUUIDValidator barcodeValidator = null;

        // in case of a non remote validation , return an object that does simple validation
        if (remoteValidationHelper == null) {
            barcodeValidator = new QcLiveBarcodeAndUUIDValidatorRemoteImpl();

        } else {
            // return remote stub
            final QcLiveBarcodeAndUUIDValidatorImpl remoteBarcodeValidator = new QcLiveBarcodeAndUUIDValidatorImpl();

            final BiospecimenIdWsQueries biospecimenIdWsQueries = (BiospecimenIdWsQueries) applicationContext.getBean("biospecimenIdWsQueries");
            remoteBarcodeValidator.setBiospecimenIdWsQueries(biospecimenIdWsQueries);

            final ValidationWebServiceQueries validationWebServiceQueries = (ValidationWebServiceQueries) applicationContext.getBean("validationWebServiceQueries");
            remoteBarcodeValidator.setValidationWebServiceQueries(validationWebServiceQueries);

            final ShippedBiospecimenWSQueriesImpl shippedBiospecimenWSQueries = 
                    (ShippedBiospecimenWSQueriesImpl) applicationContext.getBean("shippedBiospecimenWSQueries");
            shippedBiospecimenWSQueries.setQcContext(qcContext);
            shippedBiospecimenWSQueries.setUseRemoteService(useRemoteValidation);
            shippedBiospecimenWSQueries.setLogger(logger);
            
            remoteBarcodeValidator.setShippedBiospecimenQueries(shippedBiospecimenWSQueries);

            barcodeValidator = remoteBarcodeValidator;
        }
        return barcodeValidator;
    }

    /**
     * Returns an implementation of {@link BarcodeTumorValidator} that always returns <code>true</code>,
     * even when -noremote is used since we don't have a web service equivalent (yet)
     *
     * @return an implementation of {@link BarcodeTumorValidator} that always returns <code>true</code>
     */
    private BarcodeTumorValidator getBarcodeTumorValidator() {

        return new BarcodeTumorValidator() {

            @Override
            public boolean barcodeIsValidForTumor(final String barcode,
                                                  final String tumorAbbreviation) throws Processor.ProcessorException {
                return true;
            }
        };
    }

    public boolean getWasSuccessful() {
        return wasSuccessful;
    }

    private void setCenterType(final String centerType) {
        this.centerType = centerType;
    }

    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    public void setQcContext(final QcContext qcContext) {
        this.qcContext = qcContext;
    }

    public void setLogger(final Logger logger) {
        this.logger = logger;
    }

    public void setRemoteValidationHelper(final RemoteValidationHelper remoteValidationHelper) {
        this.remoteValidationHelper = remoteValidationHelper;
    }
    
    public static boolean useRemoteValidation() {
        return useRemoteValidation;
    }

    public static void setUseRemoteValidation(final boolean useRemoteValidation) {
        Soundcheck.useRemoteValidation = useRemoteValidation;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }
}
