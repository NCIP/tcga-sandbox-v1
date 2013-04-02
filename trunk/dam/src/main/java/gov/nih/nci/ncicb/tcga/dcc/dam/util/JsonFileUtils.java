/*
 *
 *  * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 *  * Copyright Notice.  The software subject to this notice and license includes both human
 *  * readable source code form and machine readable, binary, object code form (the "caBIG
 *  * Software").
 *  *
 *  * Please refer to the complete License text for full details at the root of the project.
 *
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * Interface for JsonFileUtils
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface JsonFileUtils {

    /**
     * Gets the latest json file from the given directory, using the given filename regexp and date format.
     *
     * @param directory directory containing json files
     * @param regexpForExtractingDateStringFromFilename regexp to match filenames, first match should be date string
     * @param dateFormatForFilenames simple date format for converting to Date object
     * @return the latest json file matching the regexp by date in the filename, or null if none found
     */
    File getLatestJsonFile(File directory, Pattern regexpForExtractingDateStringFromFilename,
                           SimpleDateFormat dateFormatForFilenames) throws ParseException;

    JSONObject getJsonObjectFromFile(File file) throws IOException;
}
