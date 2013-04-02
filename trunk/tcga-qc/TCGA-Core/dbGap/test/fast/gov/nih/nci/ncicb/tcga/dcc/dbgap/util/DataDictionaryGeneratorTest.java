/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dbgap.util;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ClinicalMetaQueries;
import gov.nih.nci.ncicb.tcga.dcc.dbgap.DbGapSubmissionGenerator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for DataDictionaryGenerator
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class DataDictionaryGeneratorTest {
    private Mockery context = new JUnit4Mockery();
    private ClinicalMetaQueries clinicalMetaQueries = context.mock(ClinicalMetaQueries.class);
    private ClinicalMetaQueries.ClinicalFile subjectsFile;
    private DataDictionaryGenerator dataDictionaryGenerator;

    private static final String DISEASE = "testDisease";

    @Before
    public void setup() {
        // create subjects file object
        subjectsFile = new ClinicalMetaQueries.ClinicalFile();

        // create columns
        ClinicalMetaQueries.ClinicalFileColumn column1 = new ClinicalMetaQueries.ClinicalFileColumn();
        column1.columnName = "Column1";
        column1.description = "Column 1 is nice";
        column1.type = "string";
        column1.values = Arrays.asList("1", "2", "3");

        ClinicalMetaQueries.ClinicalFileColumn column2 = new ClinicalMetaQueries.ClinicalFileColumn();
        column2.columnName = "Column2";
        column2.description = "Column 2 is evil";
        column2.type = "integer";

        // this one should  not appear in the data dictionary at all
        ClinicalMetaQueries.ClinicalFileColumn column3 = new ClinicalMetaQueries.ClinicalFileColumn();
        column3.hasNonNullData = false;

        // add columns to file
        subjectsFile.columns = Arrays.asList( column1, column2, column3 );

        Map<String, ClinicalMetaQueries> clinicalMetaQueriesMap = new HashMap<String, ClinicalMetaQueries>();
        clinicalMetaQueriesMap.put(DISEASE, clinicalMetaQueries);
        dataDictionaryGenerator = new DataDictionaryGenerator(clinicalMetaQueriesMap);
    }

    @Test
    public void testGenerateDictionary() {
        context.checking( new Expectations() {{
            one(clinicalMetaQueries).getClinicalFile(DbGapSubmissionGenerator.DbGapFile.Samples.getFileId(), true, DISEASE);
            will(returnValue(subjectsFile));
        }});

        String dictionary = dataDictionaryGenerator.generateDataDictionary(DbGapSubmissionGenerator.DbGapFile.Samples, DISEASE);
        String[] dictionaryLines = dictionary.split(DbGapSubmissionGenerator.NEWLINE);
        assertEquals(3, dictionaryLines.length);
        checkHeaderAndGeneralColumns(dictionaryLines);
    }

    @Test
    public void testGeneratePatientDictionary() {
        context.checking( new Expectations() {{
            one(clinicalMetaQueries).getClinicalFile(DbGapSubmissionGenerator.DbGapFile.Subjects.getFileId(), true, DISEASE);
            will(returnValue(subjectsFile));
        }});

        String dictionary = dataDictionaryGenerator.generateDataDictionary(DbGapSubmissionGenerator.DbGapFile.Subjects, DISEASE);
        String[] dictionaryLines = dictionary.split(DbGapSubmissionGenerator.NEWLINE);
        assertEquals(5, dictionaryLines.length);
        checkHeaderAndGeneralColumns(dictionaryLines);
        assertEquals("DISEASETYPE" + DbGapSubmissionGenerator.FIELD_SEPARATOR + "Disease type" + DbGapSubmissionGenerator.FIELD_SEPARATOR +
                "string" + DbGapSubmissionGenerator.FIELD_SEPARATOR + DISEASE, dictionaryLines[3]);
        assertEquals("CONSENT" + DbGapSubmissionGenerator.FIELD_SEPARATOR + "Consent group as determined by DAC" +
        DbGapSubmissionGenerator.FIELD_SEPARATOR + "encoded value" + DbGapSubmissionGenerator.FIELD_SEPARATOR +
                "1=General Research Use", dictionaryLines[4]);
    }

    private void checkHeaderAndGeneralColumns(final String[] dictionaryLines) {
        assertEquals("VARNAME" + DbGapSubmissionGenerator.FIELD_SEPARATOR + "VARDESC" +
                DbGapSubmissionGenerator.FIELD_SEPARATOR + "TYPE" + DbGapSubmissionGenerator.FIELD_SEPARATOR + "VALUES",
                dictionaryLines[0]);
        assertEquals("Column1" + DbGapSubmissionGenerator.FIELD_SEPARATOR + "Column 1 is nice" +
                DbGapSubmissionGenerator.FIELD_SEPARATOR + "string" + DbGapSubmissionGenerator.FIELD_SEPARATOR + "1" +
                DbGapSubmissionGenerator.FIELD_SEPARATOR + "2" + DbGapSubmissionGenerator.FIELD_SEPARATOR + "3", dictionaryLines[1]);
        assertEquals("Column2" + DbGapSubmissionGenerator.FIELD_SEPARATOR + "Column 2 is evil" +
                DbGapSubmissionGenerator.FIELD_SEPARATOR + "integer" + DbGapSubmissionGenerator.FIELD_SEPARATOR, dictionaryLines[2]); // no values
    }
}
