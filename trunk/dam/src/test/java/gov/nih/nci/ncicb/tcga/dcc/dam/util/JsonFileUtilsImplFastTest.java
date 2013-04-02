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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test for JsonFileUtils
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class JsonFileUtilsImplFastTest {
    private static final String SAMPLE_FOLDER = Thread.currentThread().getContextClassLoader().
            getResource("samples/jsonFileUtils").getPath();

    private JsonFileUtilsImpl jsonFileUtils;

    @Before
    public void setup() {
        jsonFileUtils = new JsonFileUtilsImpl();
    }

    @Test
    public void testGetLatestJsonFile() throws Exception {
        final File latestFile = jsonFileUtils.getLatestJsonFile(new File(SAMPLE_FOLDER + File.separator + "jsonDir"),
                Pattern.compile("BCR-(\\d\\d-\\d\\d\\d\\d)\\.json"), new SimpleDateFormat("MM-yyyy"));
        assertEquals("test-BCR-03-2013.json", latestFile.getName());

    }

    @Test
    public void testGetLatestJsonFileNull() throws ParseException {
        // this dir has no files that match the pattern
        final File latestFile = jsonFileUtils.getLatestJsonFile(new File(SAMPLE_FOLDER + File.separator + "noJsonDir"),
                Pattern.compile("BCR-(\\d\\d-\\d\\d\\d\\d)\\.json"), new SimpleDateFormat("MM-yyyy"));
        assertNull(latestFile);
    }

    @Test
    public void testGetObjectFromFile() throws Exception {
        final File testFile = new File(SAMPLE_FOLDER + File.separator + "jsonDir" + File.separator +
                "test-BCR-03-2013.json");
        JSONObject jsonObject = jsonFileUtils.getJsonObjectFromFile(testFile);
        assertNotNull(jsonObject);
        assertEquals("bcr-test/DCC", jsonObject.getString("version"));
        JSONArray caseSummaryArray = jsonObject.getJSONArray("case_summary_by_disease");
        assertEquals(2, caseSummaryArray.size());
        assertEquals("BRCA", caseSummaryArray.getJSONObject(0).getString("tumor_abbrev"));
        assertEquals("OV", caseSummaryArray.getJSONObject(1).getString("tumor_abbrev"));
        assertEquals("919", caseSummaryArray.getJSONObject(0).getString("shipped"));
        assertEquals("572", caseSummaryArray.getJSONObject(1).getString("shipped"));
    }
}
