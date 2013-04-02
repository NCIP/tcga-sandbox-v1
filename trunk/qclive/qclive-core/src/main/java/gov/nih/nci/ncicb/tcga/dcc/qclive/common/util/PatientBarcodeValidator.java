/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Patient Barcode Validator.
 * Note: It just checks the string against the expected regular expression.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class PatientBarcodeValidator {

    public final static String PATIENT_BARCODE_PATTERN_STRING = "((TCGA)-([A-Z0-9]{2})-([A-Z0-9]{4}))";
    public final static Pattern BARCODE_PATTERN = Pattern.compile(PATIENT_BARCODE_PATTERN_STRING);

    /**
     * Validates the given input against the expected regular expression for patient barcode.
     *
     * @param input the input to validate
     * @return <code>true</code> if the given input matches a patient barcode, <code>false</code> otherwise
     */
    public static boolean validate(final String input) {

        final String trimmedInput = input.trim();
        final Matcher barcodeMatcher = BARCODE_PATTERN.matcher(trimmedInput);

        return barcodeMatcher.matches();
    }

    /**
     * Returns the patient barcode if the given input matches one, returns null otherwise
     *
     * @param input the input to parse
     * @return the patient barcode if the given input matches one, returns null otherwise
     */
    public static String getPatientBarcode(final String input) {

        String result = null;

        final String trimmedInput = input.trim();
        final Matcher barcodeMatcher = BARCODE_PATTERN.matcher(trimmedInput);

        if(barcodeMatcher.find()) {
            result = barcodeMatcher.group();
        }

        return result;
    }
}
