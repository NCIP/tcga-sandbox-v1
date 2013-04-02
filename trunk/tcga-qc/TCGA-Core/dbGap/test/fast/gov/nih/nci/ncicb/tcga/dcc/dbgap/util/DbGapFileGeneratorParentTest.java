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
import gov.nih.nci.ncicb.tcga.dcc.dbgap.DbGapTestParent;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for DbGapFileGeneratorParent
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class DbGapFileGeneratorParentTest extends DbGapTestParent {
    private Mockery context = new JUnit4Mockery();
    private ClinicalMetaQueries queries1, queries2, queries3;
    private DbGapFileGeneratorParent dbGapFileGeneratorParent;
    private Map<String, ClinicalMetaQueries> queriesMap;
    private static final String DISEASE1 = "D1";
    private static final String DISEASE2 = "D2";
    private static final String DISEASE3 = "D3";


    @Before
    public void setup() {
        queries1 = context.mock(ClinicalMetaQueries.class, "clinicalMetaQueries1");
        queries2 = context.mock(ClinicalMetaQueries.class, "clinicalMetaQueries2");
        queries3 = context.mock(ClinicalMetaQueries.class, "clinicalMetaQueries3");
        queriesMap = new HashMap<String, ClinicalMetaQueries>();
        queriesMap.put(DISEASE1, queries1);
        queriesMap.put(DISEASE2, queries2);
        queriesMap.put(DISEASE3, queries3);

        dbGapFileGeneratorParent = new DbGapFileGeneratorParent(queriesMap);

        final ClinicalMetaQueries.ClinicalFile file1 = makeClinicalFile(true, true, false);
        final ClinicalMetaQueries.ClinicalFile file2 = makeClinicalFile(true, true, true);
        final ClinicalMetaQueries.ClinicalFile file3 = makeClinicalFile(true, false, true);
        context.checking(new Expectations() {{
            allowing(queries1).getClinicalFile(with(any(Integer.class)), with(true), with(any(String.class)));
            will(returnValue(file1));
            allowing(queries2).getClinicalFile(with(any(Integer.class)), with(true), with(any(String.class)));
            will(returnValue(file2));
            allowing(queries3).getClinicalFile(with(any(Integer.class)), with(true), with(any(String.class)));
            will(returnValue(file3));
        }});
    }

    @Test
    public void testGetClinicalFileInfoNoDisease() {
        // no disease, so should get this file for ALL diseases, and all columns have non-null data for at least 1 disease
        ClinicalMetaQueries.ClinicalFile file = dbGapFileGeneratorParent.getClinicalFileInfo(
                DbGapSubmissionGenerator.DbGapFile.Subjects, null);
        assertEquals(3, file.columns.size());
        assertTrue(file.columns.get(0).hasNonNullData);
        assertTrue(file.columns.get(1).hasNonNullData);
        assertTrue(file.columns.get(2).hasNonNullData);

        // also maybe should put in code in the actual method to throw an exception if the files in different schemas have different columns defined!
    }

    @Test
    public void testGetClinicalFileInfoWithDisease() {
        // disease 1 has only 2 columns with non-null data
        ClinicalMetaQueries.ClinicalFile file = dbGapFileGeneratorParent.getClinicalFileInfo(
                DbGapSubmissionGenerator.DbGapFile.Subjects, DISEASE1);
        assertEquals(3, file.columns.size());
        assertTrue(file.columns.get(0).hasNonNullData);
        assertTrue(file.columns.get(1).hasNonNullData);
        assertFalse(file.columns.get(2).hasNonNullData);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testDifferentFileColumnSizes() {
        final ClinicalMetaQueries queries4 = context.mock(ClinicalMetaQueries.class, "clinicalMetaQueries4");
        queriesMap.put("D4", queries4);
        // this file has only 2 columns but the rest (defined in setup) have 3
        final ClinicalMetaQueries.ClinicalFile file4 = makeClinicalFile(false, true);
        context.checking(new Expectations() {{
            one(queries4).getClinicalFile(with(any(Integer.class)), with(true), with(any(String.class)));
            will(returnValue(file4));
        }});
        dbGapFileGeneratorParent.getClinicalFileInfo(DbGapSubmissionGenerator.DbGapFile.Subjects, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testDifferentFileColumnNames() {
        final ClinicalMetaQueries queries4 = context.mock(ClinicalMetaQueries.class, "clinicalMetaQueries4");
        queriesMap.put("D4", queries4);
        final ClinicalMetaQueries.ClinicalFile file5 = makeClinicalFile(true, true, true);
        // change one column name
        file5.columns.get(0).columnName = "different";
        context.checking(new Expectations() {{
            one(queries4).getClinicalFile(with(any(Integer.class)), with(true), with(any(String.class)));
            will(returnValue(file5));
        }});

        dbGapFileGeneratorParent.getClinicalFileInfo(DbGapSubmissionGenerator.DbGapFile.Subjects, null);
    }


    private ClinicalMetaQueries.ClinicalFile makeClinicalFile(final Boolean... hasNonNulls) {
        final ClinicalMetaQueries.ClinicalFile file = new ClinicalMetaQueries.ClinicalFile();
        for (int i=0; i<hasNonNulls.length; i++) {
            addFileColumn(file, "col" + i, "Column " + i, hasNonNulls[i]);
        }
        return file;
    }
}
