package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Header validator for TCGA-specific VCF files.
 *
 * @author Your Name
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TcgaVcfFileHeaderValidator extends VcfFileHeaderValidator {
    public static final String HEADER_TYPE_PEDIGREE = "PEDIGREE";
    public static final String HEADER_TYPE_VCFPROCESSLOG = "vcfProcessLog";
    public static final String HEADER_TYPE_ASSEMBLY = "assembly";
    public static final String HEADER_TYPE_PHASING = "phasing";
    public static final String HEADER_TYPE_REFERENCE = "reference";
    public static final String HEADER_TYPE_FILEDATE = "fileDate";
    public static final String HEADER_TYPE_GENE_ANNO = "geneAnno";
    public static final String HEADER_TYPE_CENTER = "center";
    public static final String HEADER_TYPE_SAMPLE = "SAMPLE";
    public static final String HEADER_TYPE_INDIVIDUAL = "INDIVIDUAL";

    public static final String SAMPLE_KEY_ID = "ID";
    public static final String SAMPLE_KEY_INDIVIDUAL = "Individual";
    public static final String SAMPLE_KEY_FILE = "File";
    public static final String SAMPLE_KEY_PLATFORM = "Platform";
    public static final String SAMPLE_KEY_SOURCE = "Source";
    public static final String SAMPLE_KEY_ACCESSION = "Accession";
    public static final String SAMPLE_KEY_GENOMES = "Genomes";
    public static final String SAMPLE_KEY_MIXTURE = "Mixture";
    public static final String SAMPLE_KEY_GENOME_DESCRIPTION = "Genome_Description";
    public static final String SAMPLE_KEY_SAMPLE_NAME = "SampleName";
    public static final String SAMPLE_KEY_SAMPLE_UUID = "SampleUUID";
    public static final String SAMPLE_KEY_SAMPLE_TCGA_BARCODE = "SampleTCGABarcode";
    public static final String UUID_REGEXP = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

    public static final List<String> HEADER_TYPE_SAMPLE_REQUIRED_KEYS_LIST = Arrays.asList(SAMPLE_KEY_ID, SAMPLE_KEY_INDIVIDUAL,
            SAMPLE_KEY_FILE, SAMPLE_KEY_PLATFORM, SAMPLE_KEY_SOURCE, SAMPLE_KEY_ACCESSION, SAMPLE_KEY_SAMPLE_NAME);

    public static final List<String> HEADER_TYPE_SAMPLE_REQUIRED_KEYS_LIST_POST_UUID_TRANSITION = Arrays.asList(SAMPLE_KEY_ID, SAMPLE_KEY_FILE,
            SAMPLE_KEY_PLATFORM, SAMPLE_KEY_SOURCE, SAMPLE_KEY_ACCESSION, SAMPLE_KEY_SAMPLE_UUID, SAMPLE_KEY_SAMPLE_TCGA_BARCODE);

    public static final List<String> REQUIRED_TCGA_VCF_HEADERS = new ArrayList<String>() {{
        add(HEADER_TYPE_PHASING);
        add(HEADER_TYPE_REFERENCE);
        add(HEADER_TYPE_FILEDATE);
        add(HEADER_TYPE_VCFPROCESSLOG);
        add(HEADER_TYPE_CENTER);
    }};

    private static final List<String> EXCLUDE_FROM_SUPER_VALIDATION_HEADERS = new ArrayList<String>() {{
        add(HEADER_TYPE_VCFPROCESSLOG);
        add(HEADER_TYPE_ASSEMBLY);
        add(HEADER_TYPE_GENE_ANNO);
        add(HEADER_TYPE_CENTER);
        add(HEADER_TYPE_SAMPLE);
    }};

    private static final Pattern PEDIGREE_VALUE_PATTERN = Pattern.compile("[^<>\\s,]+");
    public static final String FILEDATE_FORMAT = "yyyyMMdd";

    private static final SimpleDateFormat dateParser = new SimpleDateFormat(FILEDATE_FORMAT);

    static {
        dateParser.setLenient(false);
    }

    public static final Pattern PHASING_VALUES_PATTERN = Pattern.compile("^partial|none$");

    public static final Pattern CENTER_VALUE_PATTERN = Pattern.compile("(\"[^\"]*\"|[^\\s]*)");
    public static final Pattern ANGULAR_BRACKETS_PATTERN = Pattern.compile("<.*>");
    public static final Pattern GENOME_DESCRIPTION_PATTERN = Pattern.compile("<(\"([^,\"])*\")(,\"([^,\"])*\")*>");
    public static final Pattern GENOMES_PATTERN = Pattern.compile("<[^\\s<>]+>");
    public static final Pattern QUOTES_PATTERN = Pattern.compile("\".+\"");
    public static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    public static final Pattern URL_PATTERN = Pattern.compile("(http|https|ftp)://([a-zA-Z0-9-\\.])+/?(.)*");

    /**
     * Default separator for vcfProcessLog header tag values
     */
    public static final String VCF_PROCESS_LOG_HEADER_TAG_VALUES_DEFAULT_SEPARATOR = ",";

    /**
     * Separator for vcfProcessorLog header tag values for the following tags: 'InputVCFParam' and 'MergeParam'
     */
    public static final String VCF_PROCESS_LOG_HEADER_TAG_VALUES_SPECIAL_SEPARATOR = ";";

    /**
     * List of the vcfProcessLog header tags that use the special separator for multiple values
     */
    public static final List<String> VCF_PROCESS_LOG_SPECIAL_TAGS = new LinkedList<String>();

    static {
        TcgaVcfFileHeaderValidator.VCF_PROCESS_LOG_SPECIAL_TAGS.add("InputVCFParam");
        TcgaVcfFileHeaderValidator.VCF_PROCESS_LOG_SPECIAL_TAGS.add("MergeParam");
    }

    /**
     * Prefix of vcfProcessLog header tags that are excluded from the requirement of having the same number of multiple values
     */
    public static final List<String> VCF_PROCESS_LOG_TAG_PREFIXES_TO_EXCLUDE_FROM_NUMBER_OF_VALUES_VALIDATION = new LinkedList<String>();

    static {
        VCF_PROCESS_LOG_TAG_PREFIXES_TO_EXCLUDE_FROM_NUMBER_OF_VALUES_VALIDATION.add("Merge");
    }

    /**
     * Prefix of vcfProcessLog header tags that are excluded from the requirement of having the same number of multiple values
     */
    public static final List<String> VCF_PROCESS_LOG_TAG_PREFIXES_TO_EXCLUDE_FROM_VALUES_DUPLICATES_VALIDATION = new LinkedList<String>();

    static {
        VCF_PROCESS_LOG_TAG_PREFIXES_TO_EXCLUDE_FROM_VALUES_DUPLICATES_VALIDATION.add("Merge");
        VCF_PROCESS_LOG_TAG_PREFIXES_TO_EXCLUDE_FROM_VALUES_DUPLICATES_VALIDATION.add("InputVCFParam");
    }

    /**
     * The missing identifier for the vcfProcessLog header tags sub values
     */
    public static final String VCF_PROCESS_LOG_MISSING_IDENTIFIER = ".";

    /**
     * Prefix for Merge* tags for the vcfProcessLog header
     */
    public static final String VCF_PROCESS_LOG_MERGE_TAG_PREFIX = "Merge";

    /**
     * The InputVCF tag for the vcfProcessLog header
     */
    public static final String VCF_PROCESS_LOG_INPUTVCF_TAG = "InputVCF";

    /**
     * Tag names of vcfProcessLog header tags that are required to exist when InputVCF tag has multiple values
     */
    public static final List<String> VCF_PROCESS_LOG_REQUIRED_TAGS_WHEN_MULTIPLE_INPUTVCF_VALUES = new LinkedList<String>();

    static {
        VCF_PROCESS_LOG_REQUIRED_TAGS_WHEN_MULTIPLE_INPUTVCF_VALUES.add("MergeSoftware");
        VCF_PROCESS_LOG_REQUIRED_TAGS_WHEN_MULTIPLE_INPUTVCF_VALUES.add("MergeParam");
        VCF_PROCESS_LOG_REQUIRED_TAGS_WHEN_MULTIPLE_INPUTVCF_VALUES.add("MergeVer");
        VCF_PROCESS_LOG_REQUIRED_TAGS_WHEN_MULTIPLE_INPUTVCF_VALUES.add("MergeContact");
    }

    /**
     * Tag names of vcfProcessLog header tags that must have the same number of multiple values, or the missing identifier
     */
    public static final List<String> VCF_PROCESS_LOG_TAGS_WITH_SAME_NUMBER_OF_MULTIPLE_VALUES = new LinkedList<String>();
    private VcfHeaderDefinitionStore vcfHeaderDefinitionStore;

    static {
        VCF_PROCESS_LOG_TAGS_WITH_SAME_NUMBER_OF_MULTIPLE_VALUES.add("MergeSoftware");
        VCF_PROCESS_LOG_TAGS_WITH_SAME_NUMBER_OF_MULTIPLE_VALUES.add("MergeParam");
        VCF_PROCESS_LOG_TAGS_WITH_SAME_NUMBER_OF_MULTIPLE_VALUES.add("MergeVer");
    }

    @Override
    public List<String> getRequiredHeaderTypes() {
        return REQUIRED_TCGA_VCF_HEADERS;
    }

    @Override
    public boolean validate(final VcfFileHeader vcfFileHeader, final QcContext context) {
        boolean isValid;

        if (EXCLUDE_FROM_SUPER_VALIDATION_HEADERS.contains(vcfFileHeader.getName())) {
            // exclude from default validation headers that do not conform to standard validation
            // rules as defined in the super class. These will be processed specifically in
            // VcfValidator.postDataValidation
            isValid = true;
        } else {
            isValid = super.validate(vcfFileHeader, context);
        }

        if (HEADER_TYPE_PEDIGREE.equals(vcfFileHeader.getName())) {
            isValid = validatePedigreeHeader(vcfFileHeader, context) && isValid;
        } else if (HEADER_TYPE_FILEDATE.equals(vcfFileHeader.getName())) {
            isValid = validateFileDateHeader(vcfFileHeader, context) && isValid;
        } else if (HEADER_TYPE_REFERENCE.equals(vcfFileHeader.getName())) {
            isValid = validateReferenceHeader(vcfFileHeader, context) && isValid;
        } else if (HEADER_TYPE_PHASING.equals(vcfFileHeader.getName())) {
            isValid = validatePhasingHeader(vcfFileHeader, context) && isValid;
        } else if (HEADER_TYPE_INDIVIDUAL.equals(vcfFileHeader.getName())) {
            isValid = validateIndividualHeader(vcfFileHeader, context) && isValid;
        }

        final VcfFileHeader headerDefinition = vcfHeaderDefinitionStore.getHeaderDefinition(vcfFileHeader.getName(), vcfFileHeader.getValueFor("ID"));
        if (headerDefinition != null) {
            for (final String definedKey : headerDefinition.getValueMap().keySet()) {
                final String expectedValue = headerDefinition.getValueMap().get(definedKey);
                final String foundValue = vcfFileHeader.getValueMap().get(definedKey);

                if (foundValue == null || !expectedValue.equals(foundValue)) {
                    addErrorMessage(context, new StringBuilder().append(vcfFileHeader.getName()).append(" header with ID '").
                            append(vcfFileHeader.getValueMap().get("ID")).append("' expected to have ").
                            append(definedKey).append(" value ").append(expectedValue).append(" but found ").
                            append(foundValue).toString());
                    isValid = false;
                }
            }
        }

        return isValid;
    }

    @Override
    public boolean validateSampleColumnHeader(final VcfFile vcf, final QcContext context) {
        boolean isValid = true;
        final List<String> columnHeaderLine = vcf.getColumnHeader();
        List<String> sampleColumns = Collections.EMPTY_LIST;
        if (columnHeaderLine != null && columnHeaderLine.size() >= VcfFileDataLineValidatorImpl.VcfColumns.FORMAT.colPos() + 1) {
            sampleColumns = VcfFile.getSamplesColumns(columnHeaderLine);
        }
        final List<VcfFileHeader> vcfFileHeaders = vcf.getHeadersForType(VcfFile.HEADER_TYPE_SAMPLE);
        final List<String> sampleIdsFromSAMPLEHeaders = new ArrayList<String>();

        // gather all IDs from SAMPLE headers
        for (final VcfFileHeader fileHeader : vcfFileHeaders) {
            final Map<String, String> sampleHeaderValueMap = fileHeader.getValueMap();
            if (sampleHeaderValueMap != null) {
                final String sampleHeaderIdValue = sampleHeaderValueMap.get(SAMPLE_KEY_ID);
                if (sampleHeaderIdValue != null) {
                    sampleIdsFromSAMPLEHeaders.add(sampleHeaderIdValue);
                }
            }
        }

        // if any sample columns aren't in SAMPLE header then fail
        for (final String s : sampleColumns) {
            if (!sampleIdsFromSAMPLEHeaders.contains(s)) {
                addErrorMessage(context,
                        new StringBuilder()
                                .append("Column header contains sample column name '")
                                .append(s)
                                .append("' that does not have a corresponding SAMPLE header").toString());
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * validate the reference header values for the tcga vcf files
     *
     * @param vcfFileHeader
     * @param context
     * @return true if validation passes
     */
    private boolean validateReferenceHeader(final VcfFileHeader vcfFileHeader, final QcContext context) {
        boolean isValid = true;
        if (vcfFileHeader.getValueMap() != null) {
            if (vcfFileHeader.getValueMap().size() > 0) {
                final Set<String> keySet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                keySet.addAll(vcfFileHeader.getValueMap().keySet());
                if (!(keySet.contains("ID") && keySet.contains("Source"))) {
                    addErrorMessage(context, HEADER_TYPE_REFERENCE + " header must contain keys: <ID, Source>");
                    isValid = false;
                }
            } else {
                addErrorMessage(context, HEADER_TYPE_REFERENCE + " header must not contain empty keys: <>");
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * validate phasing header values
     *
     * @param vcfFileHeader
     * @param context
     * @return true if validation passes
     */
    private boolean validatePhasingHeader(final VcfFileHeader vcfFileHeader, final QcContext context) {
        boolean isValid = true;
        if (vcfFileHeader.getValueMap() != null || !PHASING_VALUES_PATTERN.matcher(vcfFileHeader.getValue()).matches()) {
            addErrorMessage(context, HEADER_TYPE_PHASING + " header must contain values 'partial' or 'none'");
            isValid = false;
        }
        return isValid;
    }

    /**
     * validate Individual header values
     *
     * @param vcfFileHeader
     * @param context
     * @return true if validation passes
     */
    private boolean validateIndividualHeader(final VcfFileHeader vcfFileHeader, final QcContext context) {
        boolean isValid = true;
        if (vcfFileHeader.getValueMap() != null || !QcLiveBarcodeAndUUIDValidatorImpl.PATIENT_BARCODE_PATTERN.matcher(vcfFileHeader.getValue()).matches()) {
            addErrorMessage(context, HEADER_TYPE_INDIVIDUAL + " header value must be a patient barcode.");
            isValid = false;
        }
        return isValid;
    }

    private boolean validatePedigreeHeader(final VcfFileHeader vcfFileHeader, final QcContext context) {
        boolean isValid = true;
        if (vcfFileHeader.getValueMap() == null || vcfFileHeader.getValueMap().size() == 0) {
            addErrorMessage(context, HEADER_TYPE_PEDIGREE + " header must be in the format '<key1=value1,key2=value2,...>'");
            isValid = false;
        } else {
            int nameKeyCount = 0;
            final Set<String> pedigreeValues = new HashSet<String>();
            for (final String key : vcfFileHeader.getValueMap().keySet()) {
                nameKeyCount++;
                final String value = vcfFileHeader.getValueMap().get(key);
                if (!PEDIGREE_VALUE_PATTERN.matcher(value).matches()) {
                    addErrorMessage(context, HEADER_TYPE_PEDIGREE + " header values may not contain whitespace, angle brackets, or commas, but found '" + value + "'");
                    isValid = false;
                }

                if (!pedigreeValues.add(value)) { // Duplicate value
                    addErrorMessage(context, HEADER_TYPE_PEDIGREE + " header values may not be repeated across keys, but found '" + value + "' more than once.");
                    isValid = false;
                }
            }
            if (nameKeyCount < 2) {
                addErrorMessage(context, HEADER_TYPE_PEDIGREE + " header must contain at least 2 keys, but found " + nameKeyCount);
                isValid = false;
            }
        }
        return isValid;
    }

    protected boolean validateFileDateHeader(final VcfFileHeader vcfFileHeader, final QcContext context) {
        boolean isValid;
        if (vcfFileHeader.getValue() == null) {
            isValid = false;
        } else {
            try {
                final Date date = dateParser.parse(vcfFileHeader.getValue());
                isValid = date != null;
            } catch (ParseException e) {
                isValid = false;
            }
        }
        if (!isValid) {
            addErrorMessage(context, HEADER_TYPE_FILEDATE + " header must contain value in the format '" + FILEDATE_FORMAT + "'");
        }
        return isValid;
    }

    public void setVcfHeaderDefinitionStore(final VcfHeaderDefinitionStore vcfHeaderDefinitionStore) {
        this.vcfHeaderDefinitionStore = vcfHeaderDefinitionStore;
    }

    public VcfHeaderDefinitionStore getVcfHeaderDefinitionStore() {
        return vcfHeaderDefinitionStore;
    }

}
