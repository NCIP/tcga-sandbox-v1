package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validator for VcfFileHeaders.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class VcfFileHeaderValidator {

    private static final String ID_REGEXP = "[^\\s,]+";
    public static final Pattern ID_PATTERN = Pattern.compile(ID_REGEXP);

    private static final String NUMBER_REGEXP = "[0-9]+|A|G|\\.";
    public static final Pattern NUMBER_PATTERN = Pattern.compile(NUMBER_REGEXP);

    private static final String TYPE_REGEXP = "Integer|String|Float|Flag|Character";
    public static final Pattern TYPE_PATTERN = Pattern.compile(TYPE_REGEXP);
    // this one does not have "Flag" as an option
    public static final Pattern FORMAT_TYPE_PATTERN = Pattern.compile("Integer|String|Float|Character");

    // quoted string either with no whitespace or with whitespace not at either end
    private static final String DESCRIPTION_REGEXP = "\"([^\\s\"]+|([^\\s]+[^\"]*[^\\s]+))\"";
    public static final Pattern DESCRIPTION_PATTERN = Pattern.compile(DESCRIPTION_REGEXP);

    private static final Pattern GENERAL_KEY_PATTERN = Pattern.compile("[^\\s,=;\"']+");
    private static final Pattern GENERAL_VALUE_PATTERN = Pattern.compile("(\"[^\\s\"]+|[^\\s]+[^\"]*[^\\s]+\")|[^\\s,;=\"']+");

    /*
     * Map header types to a map of required keys and the patterns used to validate them
     */
    private static final Map<String, Map<String, Pattern>> REQUIRED_KEYS_FOR_HEADER_TYPES =
            new HashMap<String, Map<String, Pattern>>() {{
                put(
                        VcfFile.HEADER_TYPE_INFO,
                        new LinkedHashMap<String, Pattern>() {{
                            put("ID", ID_PATTERN);
                            put("Number", NUMBER_PATTERN);
                            put("Type", TYPE_PATTERN);
                            put("Description", DESCRIPTION_PATTERN);
                        }}
                );

                put(
                        VcfFile.HEADER_TYPE_FORMAT,
                        new LinkedHashMap<String, Pattern>() {{
                            put("ID", ID_PATTERN);
                            put("Number", NUMBER_PATTERN);
                            put("Type", FORMAT_TYPE_PATTERN);
                            put("Description", DESCRIPTION_PATTERN);
                        }}
                );

                put(VcfFile.HEADER_TYPE_FILTER,
                        new LinkedHashMap<String, Pattern>() {{
                            put("ID", ID_PATTERN);
                            put("Description", DESCRIPTION_PATTERN);
                        }});
            }};

    /**
     * Map of unused keys in headers.
     *
     * Note: a warning will be generated for each of those keys when encountered.
     */
    private static final Map<String, List<String>> UNUSED_KEYS_FOR_HEADER_TYPES = new HashMap<String, List<String>>();
    static {
        UNUSED_KEYS_FOR_HEADER_TYPES.put(VcfFile.HEADER_TYPE_FILTER, new LinkedList<String>(){{
            add("Number");
            add("Type");
        }});
    }

    protected Map<String, Map<String, Pattern>> getRequiredKeysForHeaderTypes() {
        return REQUIRED_KEYS_FOR_HEADER_TYPES;
    }

    public List<String> getRequiredHeaderTypes() {
        //there are no required headers besides the first one which is already validated
        return null;
    }

    protected Pattern getGeneralKeyPattern() {
        return GENERAL_KEY_PATTERN;
    }

    protected Pattern getGeneralValuePattern() {
        return GENERAL_VALUE_PATTERN;
    }

    protected void addErrorMessage(final QcContext context, final String errorMessage) {
        context.addError((context.getFile() != null ? "[" + context.getFile().getName() + "] " : "") + errorMessage);
    }

    /**
     * Add a warning message to the context. Prepend the message with the name of the file where the warning occurred, if available.
     *
     * @param context the context
     * @param warningMessage the warning message
     */
    private void addWarningMessage(final QcContext context, final String warningMessage) {
        context.addWarning((context.getFile() != null ? "[" + context.getFile().getName() + "] " : "") + warningMessage);
    }

    public boolean validate(final VcfFileHeader vcfFileHeader, final QcContext context) {
        boolean isValid = true;
        final String errorPrefix = vcfFileHeader.getName() + " header on line " + vcfFileHeader.getLineNumber();

        isValid = validateKey(vcfFileHeader.getName(), errorPrefix, context) && isValid;
        if (!getRequiredKeysForHeaderTypes().containsKey(vcfFileHeader.getName())) {
            if (vcfFileHeader.getValue() != null) {
                isValid = validateValue(vcfFileHeader.getValue(), errorPrefix, context) && isValid;
            } else {
                if (vcfFileHeader.getValueMap() != null) {
                    for (final String valueKey : vcfFileHeader.getValueMap().keySet()) {
                        isValid = validateKey(valueKey, errorPrefix + " '" + valueKey + "'", context) && isValid;
                        isValid = validateValue(vcfFileHeader.getValueMap().get(valueKey), errorPrefix + " " + valueKey, context) && isValid;
                    }
                } else {
                    addErrorMessage(context, errorPrefix + " has no value");
                }
            }
        } else {
            isValid = validateRequiredKeys(vcfFileHeader, context, isValid, errorPrefix) && isValid;
        }

        generateWarningsForUnusedKeys(vcfFileHeader, errorPrefix, context);

        return isValid;
    }

    /**
     * Generate warnings for unused keys in headers
     *
     * @param vcfFileHeader the <code>VcfFileHeader</code> to parse
     * @param warningPrefix the warning prefix to prepend warnings with
     * @param context the context
     */
    private void generateWarningsForUnusedKeys(final VcfFileHeader vcfFileHeader, final String warningPrefix, final QcContext context) {

        final List<String> unusedKeyList = UNUSED_KEYS_FOR_HEADER_TYPES.get(vcfFileHeader.getName());

        if(unusedKeyList != null) {

            if(vcfFileHeader.getValueMap() != null) {
                
                for (final String valueKey : vcfFileHeader.getValueMap().keySet()) {

                    if(unusedKeyList.contains(valueKey)) {
                        addWarningMessage(context, warningPrefix + " has a '" + valueKey + "' key that is not being used.");
                    }
                }
            }
        }
    }

    /**
     * validate the sample column header names from a vcf file
     * @param vcf the vcf file object
     * @param context qc context
     * @return true if validation passes
     */
    public boolean validateSampleColumnHeader(final VcfFile vcf, final QcContext context) {
        // no special validation for a vcf file sample header column names.
        return true;
    }

    private boolean validateKey(final String key, final String errorPrefix, final QcContext context) {
        if (!isValid(key, getGeneralKeyPattern())) {
            addErrorMessage(context, errorPrefix + " is invalid: key may not contain spaces, commas, semi-colons, or quotes");
            return false;
        } else {
            return true;
        }
    }

    private boolean validateValue(final String value, final String errorPrefix, final QcContext context) {
        if (!isValid(value, getGeneralValuePattern())) {
            addErrorMessage(context,
                    errorPrefix + " value '" + value +
                            "' is invalid: may not contain spaces, equals signs, commas, or semi-colons unless surrounded by double quotes");
            return false;
        } else {
            return true;
        }
    }



    private boolean validateRequiredKeys(final VcfFileHeader vcfFileHeader, final QcContext context, boolean valid, final String errorPrefix) {
        if (getRequiredKeysForHeaderTypes().containsKey(vcfFileHeader.getName())) {

            Map<String, Pattern> requiredKeys = getRequiredKeysForHeaderTypes().get(vcfFileHeader.getName());
            Map<String, String> valueMap = vcfFileHeader.getValueMap();

            if (valueMap == null) {
                addErrorMessage(context,
                        errorPrefix + " must have key/value pairs " +
                        getRequiredKeysForHeaderTypes().get(vcfFileHeader.getName()).values().toString());
                valid = false;

            } else {

                for (final String requiredKey : requiredKeys.keySet()) {

                    if (valueMap.containsKey(requiredKey)) {

                        final String value = valueMap.get(requiredKey);
                        Pattern validationPattern = requiredKeys.get(requiredKey);
                        if (! isValid(value, validationPattern)) {
                            addErrorMessage(context,
                                    errorPrefix + " has invalid " + requiredKey + " value '" + value + "'");
                            valid = false;
                        }

                    } else {
                        addErrorMessage(context,
                                errorPrefix + " is missing required key " + requiredKey);
                        valid = false;
                    }
                }
            }
        }
        return valid;
    }

    protected boolean isValid(final String value, final Pattern validationPattern) {
        return value != null && validationPattern.matcher(value).matches();
    }
}
