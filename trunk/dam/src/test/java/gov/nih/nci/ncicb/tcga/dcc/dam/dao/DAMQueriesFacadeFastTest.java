/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelOne;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataSorterAndGapFillerI;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.TumorNormalClassifierI;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Fast tests for DAMQueriesFacade
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DAMQueriesFacadeFastTest {

    private Mockery context = new JUnit4Mockery();
    private DAMQueriesFacade facade;
    private DataAccessMatrixQueries mockQueries1, mockQueries2, mockQueries3;
    private DAMSubmittedSampleI mockDamSubmittedSampleI;
    private DataSorterAndGapFillerI mockDataSorterAndGapFiller;
    private TumorNormalClassifierI mockTumorNormalClassifier;

    private DataSet dataSet1 = new DataSet();
    private DataSet dataSet2 = new DataSet();
    private DataSet dataSet3 = new DataSet();
    private DataSet dataSet4 = new DataSet();
    private DataSet dataSet5 = new DataSet();

    private List<DataSet> queries1ReturnVal = Arrays.asList(dataSet1, dataSet2, dataSet3);
    private List<DataSet> queries2ReturnVal = Arrays.asList(dataSet4, dataSet5);
    private List<DataSet> queries3ReturnVal = null;

    private Set<String> submittedSamples = new HashSet<String>();

    @Before
    public void setup() {
        facade = new DAMQueriesFacade();
        mockQueries1 = context.mock(DataAccessMatrixQueries.class, "queries1");
        mockQueries2 = context.mock(DataAccessMatrixQueries.class, "queries2");
        mockQueries3 = context.mock(DataAccessMatrixQueries.class, "queries3");
        mockDamSubmittedSampleI = context.mock(DAMSubmittedSampleI.class);
        mockDataSorterAndGapFiller = context.mock(DataSorterAndGapFillerI.class);
        mockTumorNormalClassifier = context.mock(TumorNormalClassifierI.class);

        facade.setDAOs(Arrays.asList(mockQueries1, mockQueries2, mockQueries3));
        facade.setSorterAndGapFiller(mockDataSorterAndGapFiller);
        facade.setSubmittedSampleGetter(mockDamSubmittedSampleI);
        facade.setTumorNormalClassifier(mockTumorNormalClassifier);
    }

    @Test
    public void testGetDataSetsForDisease() throws DataAccessMatrixQueries.DAMQueriesException {
        context.checking(new Expectations() {{
            one(mockQueries1).getDataSetsForDiseaseType("TEST");
            will(returnValue(queries1ReturnVal));
            one(mockQueries2).getDataSetsForDiseaseType("TEST");
            will(returnValue(queries2ReturnVal));
            one(mockQueries3).getDataSetsForDiseaseType("TEST");
            will(returnValue(queries3ReturnVal));

            one(mockDamSubmittedSampleI).getSubmittedSampleIds("TEST");
            will(returnValue(submittedSamples));

            one(mockDataSorterAndGapFiller).sortAndFillGaps(with(any(List.class)), with(submittedSamples));
            one(mockTumorNormalClassifier).classifyTumorNormal(with(any(List.class)));
        }});
        final List<DataSet> dataSets = facade.getDataSetsForDiseaseType("TEST");
        assertEquals(5, dataSets.size());
        assertTrue(dataSets.contains(dataSet1));
        assertTrue(dataSets.contains(dataSet2));
        assertTrue(dataSets.contains(dataSet3));
        assertTrue(dataSets.contains(dataSet4));
        assertTrue(dataSets.contains(dataSet5));
    }


    @Test
    public void testGetDataSetsForControls() throws DataAccessMatrixQueries.DAMQueriesException {
        final List<String> diseases = Arrays.asList("DIS1", "DIS2", "DIS3");

        context.checking(new Expectations() {{
            one(mockQueries1).getDataSetsForControls(diseases);
            will(returnValue(queries1ReturnVal));
            one(mockQueries2).getDataSetsForControls(diseases);
            will(returnValue(queries2ReturnVal));
            one(mockQueries3).getDataSetsForControls(diseases);
            will(returnValue(queries3ReturnVal));

            one(mockDamSubmittedSampleI).getSubmittedControls(diseases);
            will(returnValue(submittedSamples));

            one(mockDataSorterAndGapFiller).sortAndFillGaps(with(any(List.class)), with(submittedSamples));
            one(mockTumorNormalClassifier).classifyTumorNormal(with(any(List.class)));

        }});

        final List<DataSet> controlDataSets = facade.getDataSetsForControls(diseases);
        assertEquals(5, controlDataSets.size());
        assertTrue(controlDataSets.contains(dataSet1));
        assertTrue(controlDataSets.contains(dataSet2));
        assertTrue(controlDataSets.contains(dataSet3));
        assertTrue(controlDataSets.contains(dataSet4));
        assertTrue(controlDataSets.contains(dataSet5));

    }

    @Test
    public void testGetFileInfoForSelectedDataSets() throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataFile> dataFiles1 = new ArrayList<DataFile>();
        DataFile dataFileA = makeDataFile("a");
        DataFile dataFileC = makeDataFile("c");
        dataFiles1.add(dataFileA);
        dataFiles1.add(dataFileC);

        final List<DataFile> dataFiles2 = new ArrayList<DataFile>();
        DataFile dataFileB = makeDataFile("b");
        DataFile dataFileSampleAnnotation = makeDataFile(DAMQueriesMetadata.SAMPLE_ANNOTATION_FILENAME);
        dataFiles2.add(dataFileB);
        dataFiles2.add(dataFileSampleAnnotation);

        final List<DataFile> dataFiles3 = new ArrayList<DataFile>();
        DataFile dataFileZ = makeDataFile("z");
        DataFile dataFileQ = makeDataFile("Q");
        dataFiles3.add(dataFileZ);
        dataFiles3.add(dataFileQ);

        context.checking(new Expectations() {{
            one(mockQueries1).getFileInfoForSelectedDataSets(null, false);
            will(returnValue(dataFiles1));

            one(mockQueries2).getFileInfoForSelectedDataSets(null, false);
            will(returnValue(dataFiles2));

            one(mockQueries3).getFileInfoForSelectedDataSets(null, false);
            will(returnValue(dataFiles3));
        }});

        List<DataFile> dataFiles = facade.getFileInfoForSelectedDataSets(null, false);
        assertEquals(6, dataFiles.size());
        assertEquals(dataFileA, dataFiles.get(0));
        assertEquals(dataFileC, dataFiles.get(1));
        assertEquals(dataFileSampleAnnotation, dataFiles.get(2));
        assertEquals(dataFileB, dataFiles.get(3));
        assertEquals(dataFileQ, dataFiles.get(4));
        assertEquals(dataFileZ, dataFiles.get(5));
    }

    private DataFile makeDataFile(final String name) {
        DataFile df = new DataFileLevelOne();
        df.setFileName(name);
        return df;
    }
}
