/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Constants Interface which simply holds constant values to be used for the whole application.
 *
 * @author Robert S. Sfeir Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public interface ConstantValues {

    static final String DEFAULT_DISEASETYPE = "GBM";

    static final String CONTROL_DISEASE = "CNTL";
    /**
     * constants used to indicate dataset availability
     */
    static final String AVAILABILITY_AVAILABLE = "A";
    static final String AVAILABILITY_PENDING = "P";
    static final String AVAILABILITY_NOTAVAILABLE = "N";
    static final String AVAILABILITY_NOTAPPLICABLE = "NA";
    static final String PROTECTEDSTATUS = "protectedStatus";
    static final String PROTECTEDSTATUS_PROTECTED_VALUE = "P";
    static final String PROTECTEDSTATUS_PUBLIC_VALUE = "N";
    static final String PROTECTEDSTATUS_PROTECTED = "Protected";
    static final String PROTECTEDSTATUS_PUBLIC = "Public";
    static final String CENTER = "center";
    static final String PLATFORM_TYPE = "platformType";
    static final String PLATFORM = "platform";
    /**
     * pseudo platformtype, center Ids used in the UI clinical types The actual values are arbitrary and are not used in
     * db queries
     */
    static final String CLINICAL_PLATFORMTYPE = "-999";
    static final String CLINICAL_PROTECTED_CENTER = "-888";
    static final String CLINICAL_NOTPROTECTED_CENTER = "-777";
    static final String MANIFEST_NAME = "MANIFEST.txt";
    static final String FAILED_MD5_CHECK = "FAILED! The MD5 Check Failed, we are stopping the process.";
    static final String SEPARATOR = File.separator;
    static final String SDRF_HEADER_FILE_PATH = "gov/SDRFHeaders.txt";
    static final String DEPRECATED_SDRF_HEADER_FILE_PATH = "gov/DeprecatedSDRFHeaders.txt";
    static final String FAILURE_THE_SDRF_FILE = "FAILURE: The SDRF File ";
    static final String LISTED_IN_THE_MAGE_FILE_IS_INVALID_OR_MISSING = " listed in the MAGE file is INVALID OR MISSING!";
    static final String CHECKING_SDRF_FILE = "-- Checking SDRF File --";
    static final String FILES_LISTED_IN_MAGE_SDRF_ERROR = "Files listed in MAGE SDRF are missing from your manifest.  The Files are:";
    static final String MISSING_FILE_REFERENCED_IN_SDRF = "Missing file referenced in SDRF: ";
    static final String FAILURE_THE_SOURCE_NAME_COLUMN = "FAILURE:  The Source Name column in the SDRF file does not contain any valid data.";
    static final String FAILURE_THE_PROVIDER_COLUMN = "FAILURE:  The Provider column in the SDRF file does not contain any valid data.";
    static final String FAILURE_THE_MATERIAL_TYPE = "FAILURE:  The Material Type column in the SDRF file does not contain any valid data.";
    static final String FAILURE_CHARACTERISTICS_GENOTYPE_COLUMN = "FAILURE:  The Characteristics [Genotype] column in the SDRF file does not contain any valid data.";
    static final String FAILURE_CHARACTERISTICS_ORGANISM_COLUMN = "FAILURE:  The Characteristics[Organism] column in the SDRF file does not contain any valid data.";
    static final String FAILURE_SAMPLE_NAME_COLUMN = "FAILURE:  The Sample Name column in the SDRF file does not contain any valid data.";
    static final String FAILURE_TERM_SOURCE_REF_VALUES = "FAILURE:  The Term Source REF values don't match those in the IDF file.";
    static final String FAILURE_MISSING = "FAILURE:  Missing: ";
    static final String FROM_IDF_TERM_SOURCE_NAME = " from IDF Term Source Name";
    static final String INVALID_PATTERN_DETECTED = "FAILURE:  An invalid pattern was detected for the Array Design REF value in the SDRF file.";
    static final String PROTOCOL_DESCRIPTION_FROM_IDF = " \"Protocol Description\"(s) from your IDF.";
    static final String TOO_MANY_PROTOCOL_DESCRIPTION = "FAILURE:  You have too many \"Protocol Description\" in your IDF.";
    static final String PROTOCOL_NAME = "Protocol Name";
    static final String PROTOCOL_DESCRIPTION = "Protocol Description";
    static final String PROTOCOL_TYPE = "Protocol Type";
    static final String PROTOCOL_TYPE_FROM_IDF = " \"Protocol Type\"(s) from your IDF.";
    static final String FAILURE_TOO_MANY_PROTOCOL_TYPES = "FAILURE:  You have too many \"Protocol Types\"(s) in your IDF.";
    static final String TOO_MANY_PROTOCOL_TERM_SOURCE_REFS = "FAILURE:  You have too many \"Protocol Term Source REF\"(s) in your IDF.";
    static final String PROTOCOL_NAME_DOESNT_MATCH = "FAILURE:  The Protocol Name in the IDF doesn't match the Protocol REF in the SDRF!";
    static final String SRDF_PROTOCOL_REF = "The SRDF Protocol REF: ";
    static final String PROT_REF_NOT_FOUND = " was not found in the IDF Protocol Name.";
    static final String DEPLOYING_ARCHIVE = "Deploying archive...";
    static final String ARCHIVE_DEPLOYED_TO = "Archive deployed to: ";
    static final String INCORRECT_NUMBER_OF_ELEMENTS_PROTOCL_NAME = "FAILURE: The Protocol Name Validator is reporting an incorrect number of elements in your Protocol Name value.  Errors will follow below.";
    static final String DOMAIN_NAME_DOES_NOT_MATCH = "FAILURE: The Domain Name of the archive does not match the Domain Name in the Protocol Name column in the IDF file.";
    static final String PLATFORM_DOES_NOT_MATCH = "FAILURE: The Platform of the archive does not match the Platform in the Protocol Name column in the IDF file.";
    static final String PROTOCOL_TYPE_IS_INVALID = "FAILURE: The Protocol Type is invalid in the Protocol Name column in the IDF file.";
    static final String VERSION_NUMBER_IS_INVALID = "FAILURE: The Version number is invalid in the Protocol Name column in the IDF file.";
    static final String PROTOCOL_DESCRIPTION_FOR_PROTOCOL_NAME = "FAILURE: The Protocol Description for Protocol Name: ";
    static final String PROTOCOL_NAME_CANNOT_BE_EMPTY = " cannot be empty.  It needs to contain an informative value (e.g. a URL to the protocol or an actual description of the protocol.  The value -> is not valid.";
    static final String IDF_VALUES_NOT_ALLOWED = "FAILURE: The IDF File contains -> values which are not allowed.  Please review those columns with the -> and enter an appropriate value.";
    static final String TERM_SOURCE_NAME = "Term Source Name";
    static final String TERM_SOURCE_FILE = "Term Source File";
    static final String TERM_SOURCE_VERSION = "Term Source Version";
    static final String TERM_SOURCE_REF = "Term Source REF";
    static final String FAILURE_ORPHANED_VALUE = "FAILURE The Value ";
    static final String IDF_ORPHANED_TERM_SOURCE_NAME = " in the IDF file is orphaned, and does not exist in the values listed in Term Source Name.";
    static final String SOURCE_NAME = "Source Name";
    static final String PROVIDER = "Provider";
    static final String MATERIAL_TYPE = "Material Type";
    static final String CHARACTERISTICS_GENOTYPE = "Characteristics [Genotype]";
    static final String CHARACTERISTICS_ORGANISM = "Characteristics [Organism]";
    static final String SAMPLE_NAME = "Sample Name";
    static final String LEVEL23_SAMPLE_PHRASE = "selected_samples";
    static final String PROTOCOL_TERM_SOURCE_REF = "Protocol Term Source REF";
    static final String STARTUP_ERROR = "You did not pass the archive name as part of the process.\n\n" +
            "The correct usage is:\n\n" +
            "    ./validator.sh archiveName\n\n" +
            "Alternately if you already have the exploded archive, you may pass in a second argument to bypass MD5 checking and untar'ing.\n\n" +
            "    ./validator.sh archiveName bypass\n\n" +
            "The above is ONLY recommended to quickly test the contents of your archive, and the archive must exist in the same location as the already exploded directory.\n" +
            "Please always check the full archive processing before submitting to the DCC.\n\n" +
            "[Stopping Processing.]\n\n";
    static final String MD5_HASH_FAIL = "The Archive's MD5 hash check failed for Archive: ";
    static final String MD5_HASH_PASS = "The Archive's MD5 hash check passed for Archive: ";
    static final int ALIQUOT_BARCODE_LENGTH = 29;
    String ARCHIVE_AVAILABLE = "Available";
    String ARCHIVE_IN_REVIEW = "In Review";
    String PLATFORM_TYPE_ID = "platform_type_id";
    String CENTER_ID = "center_id";
    String PLATFORM_ID = "platform_id";
    String SAMPLE = "sample";
    String SERIAL_INDEX = "serial_index";
    String REVISION = "revision";
    String PIPE_SYMBOL = "|";
    String BARCODE = "barcode";
    String BUILT_BARCODE = "built_barcode";
    String BCR_BATCH = "bcr_batch";
    String ACCESS_TYPE = "access_type";
    String ARCHIVE_PRIVATE = "Private";
    String AVAILABILITY = "availability";
    String EMAIL_SEPARATOR = ",";

    @Deprecated
    public static final String ARCHIVE_EXTENSION = ".tar.gz";

    public static final String UNCOMPRESSED_ARCHIVE_EXTENSION = ".tar";
    public static final String COMPRESSED_ARCHIVE_EXTENSION = ".tar.gz";
    public static final String FILE_EXTENSION_XML = "xml";
    public static final String UNCLASSIFIED_BATCH = "Unclassified";
    String QCLIVE_SHUTDOWN_MESSAGE = "The QCLive application is being restarted or shutdown.";
    public static final int NOT_ASSIGNED = -1;
    public static int BATCH_SIZE = 500;
    public static int DATALEVEL_UNKNOWN = 0;
    public static String DIR_UNKNOWN = "unknown";
    // Spring bean name constants
    public static String LIVE_OBJECT_SPRING_BEAN_NAME = "qcLive";
    public static String EXPERIMENT_DAO_OBJECT_SPRING_BEAN_NAME = "experimentDAO";
    public static String LEVEL2_CACHE_GENERATOR_SPRING_BEAN_NAME = "level2DataCacheGenerator";
    public static String LEVEL2_CACHE_ENQUEUER_SPRING_BEAN_NAME = "level2DataCacheEnqueuer";
    public static String CLINICAL_CACHE_GENERATOR_SPRING_BEAN_NAME = "clinicalCacheFileGenerator";
    public static String MAIL_ERROR_HELPER_SPRING_BEAN_NAME = "mailErrorHelper";
    public static String MAIL_SENDER_SPRING_BEAN_NAME = "mailSender";
    public static String DISEASE_ARCHIVE_QUERIES_SPRING_BEAN_NAME = "diseaseArchiveQueries";
    public static String COMMON_ARCHIVE_QUERIES_SPRING_BEAN_NAME = "archiveQueries";
    public static String PLATFORM_QUERIES_SPRING_BEAN_NAME = "platformQueries";
    public static String CENTER_QUERIES_SPRING_BEAN_NAME = "centerQueries";
    public static String DAM_HELPER_SPRING_BEAN_NAME = "damHelper";
    public static String BIOTAB_GENERATOR_SPRING_BEAN_NAME = "bioTabFileGenerator";
    public static String BIOTAB_GENERATOR_JOB = "biotabGeneratorJob";
    public static String LIVE_SCHEDULER = "liveScheduler";
    public static String BIOTAB_SCHEDULER = "qcliveCronJobScheduler";
    public static String BIOTAB_GENERATOR_TRIGGER = "biotabGeneratorTrigger";
    public static String BIOTAB_GENERATOR_DELAY = "biotabGeneratorDelay";
    public static String DISEASE_ROUTING_DS = "diseaseDataSource";
    public static final int DATA_LEVEL_2 = 2;
    public static final String DATA_LEVEL_2_STR = "level2";
    public static final String LEVEL_2_LOADER_GROUP_NAME = "Level_2_Loader";
    public static final String LEVEL_2_CACHE_GROUP_NAME = "Level_2_Cache";
    public static final int LATEST_ARCHIVE = 1;
    public static final String DATA_BEAN = "DataBean";
    public static final String JOB_BEAN_NAME = "JobBeanName";
    public static final Pattern HTTP_URL_PATTERN = Pattern.compile("https?://[\\w\\d:#@%/;$()~_?\\+-=\\\\.&]+");

    // Source File Types
    public static final String MIRNA_QUANTIFICATION_SOURCE_FILE_TYPE = "mirna_quantification";
    public static final String ISOFORM_QUANTIFICATION_SOURCE_FILE_TYPE = "isoform_quantification";
    public static final String EXON_QUANTIFICATION_SOURCE_FILE_TYPE = "expression_exon";
    public static final String GENE_QUANTIFICATION_SOURCE_FILE_TYPE = "expression_gene";
    public static final String JUNCTION_QUANTIFICATION_SOURCE_FILE_TYPE = "expression_junction";
    public static final String PROTEIN_EXPRESSION_SOURCE_FILE_TYPE = "protein_expression";
    public static final String GENE_QUANTIFICATION_RSEM_GENE_SOURCE_FILE_TYPE = "expression_rsem_gene";
    public static final String GENE_QUANTIFICATION_RSEM_GENE_NORMALIZED_SOURCE_FILE_TYPE = "expression_rsem_gene_normalized";
    public static final String GENE_QUANTIFICATION_RSEM_ISOFORMS_SOURCE_FILE_TYPE = "expression_rsem_isoforms";
    public static final String GENE_QUANTIFICATION_RSEM_ISOFORMS_NORMALIZED_SOURCE_FILE_TYPE = "expression_rsem_isoforms_normalized";

    // platform used to validate file name
    public final static String PROTEIN_ARRAY_PLATFORM = "MDA_RPPA_Core";

    // constants used for redaction service and uuid hierarchy queries
    public static final String UUID = "uuid";

    public static final String WS_BARCODE_DELIMITER = ",";
    public static final char WS_QUERY_PARAMETERS_DELIMITER = ',';
    public static final String WS_BATCH_DELIMITER = ",";
    public static final Integer WS_BATCH_SIZE = 75;
    public static final Integer UUID_WS_BATCH_SIZE = 100;

    public static final String UUID_HIERARCHY_QUERIES = "uuidHierarchyQueriesImpl";

    public static final String COMMAND_ADD = "Add";
    public static final String COMMAND_UPDATE = "Update";
    public static final String COMMAND_DELETE = "Delete";

    public static final String APP_NAME_QCLIVE = "qclive-core";
    public static final String APP_NAME_DAM = "dam";
    public static final String APP_NAME_DATA_BROWSER = "databrowser";
    public static final String APP_NAME_METADATA_BROWSER = "uuid";
    public static final String APP_NAME_DATA_REPORTS_WEB = "datareports-web";
    public static final String APP_NAME_ANNOTATIONS = "annotations";
    public static final String APP_NAME_UUID = "uuid";
    public static final String APP_NAME_COMMON = "common";

    public static final int IN_CLAUSE_SIZE = 300;
    public static final String PROTECTED_DIR = "tcga4yeo";
    public static final String PUBLIC_DIR = "anonymous";
    public static final String MD5_EXTENSION = ".md5";
}
