/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dbgap.util;

import gov.nih.nci.ncicb.tcga.dcc.dbgap.DbGapSubmissionGenerator;
import gov.nih.nci.ncicb.tcga.dcc.dbgap.DbGapTestParent;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.jmock.Mockery;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.integration.junit4.JMock;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ClinicalMetaQueries;
import gov.nih.nci.ncicb.tcga.dcc.dbgap.dao.DbGapQueries;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;

/**
 * Test class for DbGap data file generator.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class DataFileGeneratorTest extends DbGapTestParent {
    private Mockery context = new JUnit4Mockery();
    private ClinicalMetaQueries clinicalMetaQueries, clinicalMetaQueries2;
    private DbGapQueries dbGapQueries, dbGapQueries2;
    private DataFileGenerator generator;

    private static final String DISEASE = "dis";
    private static final String DISEASE2 = "dis2";

    @Before
    public void setup() {
        clinicalMetaQueries = context.mock(ClinicalMetaQueries.class, "metaQueries1");
        clinicalMetaQueries2 = context.mock(ClinicalMetaQueries.class, "metaQueries2");
        dbGapQueries = context.mock(DbGapQueries.class, "dbgapQueries1");
        dbGapQueries2 = context.mock(DbGapQueries.class, "dbgapQueries2");
        Map<String, ClinicalMetaQueries> clinicalMetaQueriesMap = new HashMap<String, ClinicalMetaQueries>();
        Map<String, DbGapQueries> dbGapQueriesMap = new HashMap<String, DbGapQueries>();
        clinicalMetaQueriesMap.put(DISEASE, clinicalMetaQueries);
        clinicalMetaQueriesMap.put(DISEASE2, clinicalMetaQueries2);
        dbGapQueriesMap.put(DISEASE, dbGapQueries);
        dbGapQueriesMap.put(DISEASE2, dbGapQueries2);
        generator = new DataFileGenerator(clinicalMetaQueriesMap, dbGapQueriesMap);
    }

    @Test
    public void testGenerate() {
        final ClinicalMetaQueries.ClinicalFile samplesFile = new ClinicalMetaQueries.ClinicalFile();
        addFileColumn(samplesFile, "col1", "Column 1");
        addFileColumn(samplesFile, "col2", "Column 2");
        addFileColumn(samplesFile, "col3", "Column 3");
        addFileColumn(samplesFile, "col4", "Column 4", false); // has no non-nulls, should be skipped
        final List<List<String>> samplesData = new ArrayList<List<String>>();
        samplesData.add(Arrays.asList("a1", "a2", "a3"));
        samplesData.add(Arrays.asList("b1", "b2", "b3"));
        samplesData.add(Arrays.asList("c1", null, null));  // all nulls except ID, so will not be included

        context.checking( new Expectations() {{
            one(clinicalMetaQueries).getClinicalFile(DbGapSubmissionGenerator.DbGapFile.Samples.getFileId(), true, DISEASE);
            will(returnValue(samplesFile));
            one(dbGapQueries).getClinicalData(samplesFile);
            will(returnValue(samplesData));
        }});

        String dataContent = generator.generateDataFile(DbGapSubmissionGenerator.DbGapFile.Samples, DISEASE);
        assertNotNull(dataContent);
        String[] dataLines = dataContent.split("\n");        
        assertEquals(3, dataLines.length);
        assertEquals("Data file header not as expected", "col1\tcol2\tcol3", dataLines[0]);
        assertEquals("a1\ta2\ta3", dataLines[1]);
        assertEquals("b1\tb2\tb3", dataLines[2]);
    }

    @Test
    public void testGenerateSubjectsFile() {
        // subjects file has extra columns for DISEASETYPE and CONSENT
        final ClinicalMetaQueries.ClinicalFile subjectsFile = new ClinicalMetaQueries.ClinicalFile();
        addFileColumn(subjectsFile, "BARCODE", "barcode");
        addFileColumn(subjectsFile, "SQUIRREL", "squirrel", false);
        final ClinicalMetaQueries.ClinicalFile subjectsFile2 = new ClinicalMetaQueries.ClinicalFile();
        addFileColumn(subjectsFile2, "BARCODE", "barcode");
        addFileColumn(subjectsFile2, "SQUIRREL", "squirrel", true);

        final List<List<String>> subjectsData = new ArrayList<List<String>>();
        final List<String> aList = new ArrayList<String>();
        aList.add("a");
        aList.add(null);
        final List<String> bList = new ArrayList<String>();
        bList.add("b");
        bList.add(null);
        subjectsData.add(aList);
        subjectsData.add(bList);

        final List<List<String>> subjectsData2 = new ArrayList<List<String>>();
        final List<String> cList = new ArrayList<String>();
        cList.add("c");
        cList.add("acorn");
        subjectsData2.add(cList);

        context.checking( new Expectations() {{
            one(clinicalMetaQueries).getClinicalFile(DbGapSubmissionGenerator.DbGapFile.Subjects.getFileId(), true, null);
            will(returnValue(subjectsFile));
            one(dbGapQueries).getClinicalData(with(any(ClinicalMetaQueries.ClinicalFile.class)));
            will(returnValue(subjectsData));

            one(clinicalMetaQueries2).getClinicalFile(DbGapSubmissionGenerator.DbGapFile.Subjects.getFileId(), true, null);
            will(returnValue(subjectsFile2));
            one(dbGapQueries2).getClinicalData(with(any(ClinicalMetaQueries.ClinicalFile.class)));
            will(returnValue(subjectsData2));

        }});
        String data = generator.generateDataFile(DbGapSubmissionGenerator.DbGapFile.Subjects, null);
        String[] dataLines = data.split("\n");
        Arrays.sort(dataLines);
        assertEquals("BARCODE\tSQUIRREL\t" + DataDictionaryGenerator.DISEASETYPE + "\t" +
                DataDictionaryGenerator.SUBJECT_CONSENT_COLUMN_NAME, dataLines[0]);
        assertEquals("a\tnull\t" + DISEASE + "\t1", dataLines[1]);
        assertEquals("b\tnull\t" + DISEASE + "\t1", dataLines[2]);
        assertEquals("c\tacorn\t" + DISEASE2 + "\t1", dataLines[3]);

    }
}
