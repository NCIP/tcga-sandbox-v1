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

import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of JsonFileUtils
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class JsonFileUtilsImpl implements JsonFileUtils {
    @Override
    public File getLatestJsonFile(final File directory, final Pattern regexpForExtractingDateStringFromFilename,
                                  final SimpleDateFormat dateFormatForFilenames) throws ParseException {

        if (directory != null && directory.isDirectory()) {
            final String[] listOfJsonFiles = directory.list(new FilenameFilter() {
                @Override
                public boolean accept(final File directory, final String filename) {
                    return filename.endsWith(".json");
                }
            });

            Date latestDate = null;
            String latestFile = null;

            for (final String jsonFile : listOfJsonFiles) {
                Matcher matcher = regexpForExtractingDateStringFromFilename.matcher(jsonFile);
                if (matcher.find() && matcher.groupCount() > 0) {
                    final String dateString = matcher.group(1);
                    final Date jsonDate = dateFormatForFilenames.parse(dateString);
                    if (latestDate == null || jsonDate.after(latestDate)) {
                        latestDate = jsonDate;
                        latestFile = jsonFile;
                    }
                }
            }

            return latestFile == null ? null : new File(latestFile);
        } else {
            throw new IllegalArgumentException(directory == null ? "Directory must be provided" : directory + " is not a directory");
        }
    }

    @Override
    public JSONObject getJsonObjectFromFile(final File file) throws IOException {
        final String fileContent = FileUtil.readFile(file,true);
        return JSONObject.fromObject(fileContent);
    }
}
