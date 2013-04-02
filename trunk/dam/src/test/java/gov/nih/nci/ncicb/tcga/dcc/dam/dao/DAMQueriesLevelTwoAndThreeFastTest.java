/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwo;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileMutation;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetMutation;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Fast tests for DAMQueriesLevelTwoAndThree
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class DAMQueriesLevelTwoAndThreeFastTest {

    private Mockery context = new JUnit4Mockery();
    private DAMUtils damUtils;
    private DataTypeQueries mockDataTypeQueries;
    private DAMQueriesLevel2 queriesLevel2;
    private DAMQueriesLevel3 queriesLevel3;

    @Before
    public void setUp() throws Exception {
        mockDataTypeQueries = context.mock(DataTypeQueries.class);
        damUtils = DAMUtils.getInstance();
        damUtils.setDataTypeQueries(mockDataTypeQueries);
        context.checking(new Expectations() {{
            allowing(mockDataTypeQueries).getAllDataTypes();
            will(returnValue(makeMockDataTypes()));
        }});
        damUtils.setDataTypeIds();
        queriesLevel2 = new DAMQueriesLevel2();
        queriesLevel3 = new DAMQueriesLevel3ExpGene();
        queriesLevel3.setDamUtils(damUtils);
    }

    @Test
    public void testEstimateFileSizes() {
        DAMQueriesCGCCLevelTwoAndThree queries = new DAMQueriesCGCCLevelTwoAndThree() {
            protected void generateFile(final DataFileLevelTwoThree dataFile, final Writer writer) {
                // do nothing
            }

            protected long getNumberOfLinesForFile(final DataFileLevelTwoThree df) {
                return 3500000;
            }

            protected long getAverageLineSize(final DataFileLevelTwoThree datafile) {
                return 3000;
            }

            protected List<DataSet> buildInitialList(final String diseaseType, final boolean forControls) {
                return null;
            }

            protected int getDataLevel() {
                return 0;
            }

            @Override
            public List<DataSet> getDataSetsForControls(final List<String> diseaseTypes) throws DAMQueriesException {
                return null;
            }
        };

        List<DataFile> files = new ArrayList<DataFile>();
        DataFile file = new DataFileLevelTwo();
        files.add(file);
        queries.estimateFileSizes(files);
        assertEquals(10500000000L, file.getSize());
    }

    @Test
    public void testShouldHandleDataset() {
        DataSetLevelTwoThree level2 = new DataSetLevelTwoThree();
        level2.setLevel("2");

        DataSetLevelTwoThree level3_expgene = new DataSetLevelTwoThree();
        level3_expgene.setLevel("3");
        level3_expgene.setPlatformTypeId("3");

        DataSetLevelTwoThree level3_cna = new DataSetLevelTwoThree();
        level3_cna.setLevel("3");
        level3_cna.setPlatformTypeId("4");

        DataSetMutation mutationLevel2 = new DataSetMutation();
        mutationLevel2.setLevel("2");

        DataSetMutation mutationLevel3 = new DataSetMutation();
        mutationLevel3.setLevel("3");

        assertTrue(queriesLevel2.shouldHandleDataSet(level2));
        assertFalse(queriesLevel2.shouldHandleDataSet(level3_expgene));
        assertFalse(queriesLevel2.shouldHandleDataSet(level3_cna));
        assertFalse(queriesLevel2.shouldHandleDataSet(mutationLevel2));
        assertFalse(queriesLevel2.shouldHandleDataSet(mutationLevel3));

        assertFalse(queriesLevel3.shouldHandleDataSet(level2));
        assertTrue(queriesLevel3.shouldHandleDataSet(level3_expgene));
        assertFalse(queriesLevel3.shouldHandleDataSet(level3_cna));
        assertFalse(queriesLevel3.shouldHandleDataSet(mutationLevel2));
        assertFalse(queriesLevel3.shouldHandleDataSet(mutationLevel3));
    }

    @Test
    public void testShouldGenerateFile() {
        final DataFileLevelTwoThree dataFile2 = new DataFileLevelTwo();
        final DataFileLevelTwoThree dataFile3_expgene = new DataFileLevelThree();
        dataFile3_expgene.setPlatformTypeId("3");
        final DataFileLevelThree dataFile3_cna = new DataFileLevelThree();
        dataFile3_cna.setPlatformTypeId("4");

        final DataFileMutation dataFileMutation = new DataFileMutation();

        assertTrue(queriesLevel2.shouldGenerateFile(dataFile2));
        assertFalse(queriesLevel2.shouldGenerateFile(dataFile3_expgene));
        assertFalse(queriesLevel2.shouldGenerateFile(dataFile3_cna));
        assertFalse(queriesLevel2.shouldGenerateFile(dataFileMutation));

        assertFalse(queriesLevel3.shouldGenerateFile(dataFile2));
        assertTrue(queriesLevel3.shouldGenerateFile(dataFile3_expgene));
        assertFalse(queriesLevel3.shouldGenerateFile(dataFile3_cna));
        assertFalse(queriesLevel3.shouldGenerateFile(dataFileMutation));
    }

    private Collection<Map<String, Object>> makeMockDataTypes(){
          Collection<Map<String, Object>> rows = new LinkedList<Map<String,Object>>();
          rows.add(new HashMap(){{
              put("data_type_id","3");
              put("name","Expression-Genes");
              put("ftp_display","transcriptome");
          }});
          rows.add(new HashMap(){{
              put("data_type_id","1");
              put("name","SNP");
              put("ftp_display","snp");
          }});
        return rows;
    }
}
