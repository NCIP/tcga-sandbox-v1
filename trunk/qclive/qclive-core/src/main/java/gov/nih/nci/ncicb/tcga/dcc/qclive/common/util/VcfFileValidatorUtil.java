/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

/**
 * Utility methods that are semantic in nature and apply specifically to
 * Vcf Files formatted as per the VCF specification. These utility
 * methods apply to VCF files regardless of whether they are VCF or
 * TCGA VCF files
 * These utility methods assume that the syntax of the parameters are valid as per
 * the VCF or TCGA VCF specification
 *
 * @author srinivasand
 *         Last updated by: $Author$
 * @version $Rev$
 */
public final class VcfFileValidatorUtil {
    /**
     * This method assumes that the FORMAT string contains keys delimited by the <code>SAMPLE_DATA_SEPARATOR</code>
     * and that the SAMPLE string contains values delimited by the <code>SAMPLE_DATA_SEPARATOR</code>
     * In this case, it extracts the relevant SAMPLE value corresponding to the FORMAT key since
     * the FORMAT keys and SAMPLE values are ordered similarly
     * @param formatKey The Key in FORMAT to extract values from SAMPLE
     * @param formatString The FORMAT column value string
     * @param sampleString The SAMPLE column value string
     * @return String : the extracted value from the SAMPLE column string
     */
    public static String getSampleValue(final String formatKey, final String formatString, final String sampleString) {
        assert(formatKey != null); assert(formatString != null); assert(sampleString != null);
        String sampleValue = "";
        final String[] formatKeys = formatString.split(VcfFileDataLineValidatorImpl.SAMPLE_DATA_SEPARATOR,-1);
        final String[] sampleKeys = sampleString.split(VcfFileDataLineValidatorImpl.SAMPLE_DATA_SEPARATOR,-1);
        for(int i = 0; i < formatKeys.length; i++) {
            if(formatKey.equals(formatKeys[i])) {
                if(i < sampleKeys.length) {
                    sampleValue = sampleKeys[i];
                }
                break;
            }
        }
        return sampleValue;
    }

    /**
     * This method assumes that the INFO string contains keyvalue pairs delimited by <code>DATA_VALUE_SEPARATOR</code>
     * and that each key value pair is delimited by <code>KEY_VALUE_SEPARATOR</code>
     * In this case it extracts the value corresponding to the infoKey
     * @param infoKey The INFO key for which the value is returned
     * @param infoString The INFO string containing key value pairs
     * @return String The value corresponding to the infoKey
     */
    public static String getInfoValue(final String infoKey, final String infoString) {
        assert(infoKey != null); assert(infoString != null);
        String formatValue = "";
        final String[] infoKeyValuePairs = infoString.split(VcfFileDataLineValidatorImpl.DATA_VALUE_SEPARATOR,-1);
        for(String infoKeyValuePair : infoKeyValuePairs) {
        	//APPS-4404 checking for key= 
            if(infoKeyValuePair.contains(infoKey + VcfFileDataLineValidatorImpl.KEY_VALUE_SEPARATOR)) {
                String[] keyAndValue = infoKeyValuePair.split(VcfFileDataLineValidatorImpl.KEY_VALUE_SEPARATOR);
                if(keyAndValue.length >= 2) {
                    formatValue = keyAndValue[1];
                }
                break;
            }
        }
        return formatValue;
    }

    /**
     * Checks if the info string contains the given key.  Will not match if the searched-for key is a substring of an
     * actual key.  (For example searching for "HI" when there is a key "HILL" will not match.  If made case-insensitive,
     * will match regardless of the case.
     *
     * @param infoKey the key you are checking for
     * @param infoString the value from the INFO column
     * @param caseSensitive whether to require matching case
     * @return true if the infoString contains the infoKey, false otherwise
     */
    public static boolean containsInfoKey(final String infoKey, final String infoString, final boolean caseSensitive) {
        final String[] infoKeyValuePairs = infoString.split(VcfFileDataLineValidatorImpl.DATA_VALUE_SEPARATOR,-1);
        for (final String keyValuePair : infoKeyValuePairs) {
            String key = keyValuePair;
            if (keyValuePair.contains(VcfFileDataLineValidatorImpl.KEY_VALUE_SEPARATOR)) {
                final String[] keyAndValue = keyValuePair.split(VcfFileDataLineValidatorImpl.KEY_VALUE_SEPARATOR);
                key = keyAndValue[0];
            }
            if ((caseSensitive && infoKey.equals(key)) || (!caseSensitive && infoKey.equalsIgnoreCase(key))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the specifide searchKey exists in the provided formatString
     * This method specifically assumes that keys in the formatString are delimited by
     * : and uses that assumption to find keys.
     * @param formatString The FORMAT string to search
     * @param searchKey The search string
     * @return <code>true</code> is successful, <code></code> false otherwise
     */
    public static Boolean isExistsFormatKey(final String formatString, final String searchKey) {
        assert(formatString != null); assert(searchKey != null);
        boolean exists = false;
        final int indexOfSearchKey = formatString.indexOf(searchKey);
        if(formatString.contains(searchKey) &&
            (
                (indexOfSearchKey-1>=0
                 && formatString.charAt(indexOfSearchKey-1)==':')
                ||
                (indexOfSearchKey+searchKey.length()<formatString.length())
                    && formatString.charAt(indexOfSearchKey+searchKey.length())==':')
                ||
                formatString.equals(searchKey)
            ) {
            exists = true;
        }
        return exists;
    }
}
