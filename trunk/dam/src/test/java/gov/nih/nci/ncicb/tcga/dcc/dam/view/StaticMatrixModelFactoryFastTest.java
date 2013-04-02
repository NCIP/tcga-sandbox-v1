/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.Disease;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.HomePageStatsService;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.HomePageStatsServiceImpl;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.TumorDetailsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for StaticMatrixModelFactory
 *
 * @author Jessica Walton
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class StaticMatrixModelFactoryFastTest {
    private Mockery context = new JUnit4Mockery();
    private StaticMatrixModelFactory factory;
    private TumorDetailsService mockTumorDetailsService;
    private HomePageStatsService mockHomePageStatsService;
    private DataAccessMatrixQueries matrixQueries;
    private List<String> diseasesPassedToGetDataSetsForDiseaseType;
    private int numCallsToGetDataSetsForControls;
    private long timeToWait;

    @Before
    public void setup() {
        diseasesPassedToGetDataSetsForDiseaseType = new ArrayList<String>();
        numCallsToGetDataSetsForControls = 0;
        timeToWait = 1000;
        mockTumorDetailsService = context.mock(TumorDetailsService.class);
        mockHomePageStatsService = context.mock(HomePageStatsService.class);

        matrixQueries = new DataAccessMatrixQueries() {
            public List<DataSet> getDataSetsForDiseaseType(final String diseaseType) throws DAMQueriesException {
                diseasesPassedToGetDataSetsForDiseaseType.add(diseaseType);
                try {
                    /*
                        Reason for the wait here:

                        I am trying to simulate the fact that getting the data sets for disease type takes a non-trivial
                        amount of time without having to actually get the real data sets. I picked 1 second because 5
                        seconds was too long and I got impatient. I wanted something long enough that while it was sleeping,
                        other threads would have time to try to request models so I could test the synchronization.
                    */
                    Thread.sleep(timeToWait);
                    return new ArrayList<DataSet>();
                } catch (InterruptedException e) {
                    throw new DAMQueriesException(e);
                }
            }

            @Override
            public List<DataSet> getDataSetsForControls(final List<String> diseaseTypes) throws DAMQueriesException {
                numCallsToGetDataSetsForControls++;
                assertEquals(1, diseaseTypes.size());
                assertTrue(diseaseTypes.contains("disease1"));
                return new ArrayList<DataSet>();
            }

            public List<DataFile> getFileInfoForSelectedDataSets(
                    final List<DataSet> selectedDataSets, final boolean consolidateFiles) throws DAMQueriesException {
                return null;
            }

            public void addPathsToSelectedFiles(final List<DataFile> selectedFiles) throws DAMQueriesException {
                // do nothing
            }
        };

    }

    private void addExpectationsForRefreshAll(final int numTimesCalled) throws Exception {
        final Sequence refreshSequence = context.sequence("refresh");
        context.checking(new Expectations() {{
            for (int i=0; i<numTimesCalled; i++) {
                one(mockTumorDetailsService).calculateAndSaveTumorDataTypeCounts(); inSequence(refreshSequence);
                one(mockHomePageStatsService).populateTable(); inSequence(refreshSequence);
            }
        }});
    }

    private void callConstructor(final boolean initNow, final boolean controlIsActive) {
        // this constructor will "init now" meaning the static model for all diseases should be created here
        // so in testing, calls to get the model should not have to wait
        factory = new StaticMatrixModelFactory(true, matrixQueries, initNow, mockTumorDetailsService,
                mockHomePageStatsService, null, "CNTL") {
            @Override
            protected List<Disease> getActiveDiseases() {
                // construct a fake disease, that is active and the default
                if (controlIsActive) {
                    return Arrays.asList(getDisease("disease1"), getDisease("CNTL"));
                } else {
                    return Arrays.asList(getDisease("disease1"));
                }
            }

            @Override
            protected Disease getDisease(String abbrev) {
                // construct a fake disease, that is active and the default
                return new Disease(abbrev, abbrev, true);
            }
        };
    }

    @Test
    public void testConstructor() throws Exception {
        addExpectationsForRefreshAll(1);
        callConstructor(true, true);

        assertEquals(1, diseasesPassedToGetDataSetsForDiseaseType.size());
        assertEquals("disease1", diseasesPassedToGetDataSetsForDiseaseType.get(0));
        assertEquals(1, numCallsToGetDataSetsForControls);
    }

    @Test
    public void testConstructorControlNotActive() throws Exception {
        context.checking(new Expectations() {{
            one(mockTumorDetailsService).calculateAndSaveTumorDataTypeCounts();
            one(mockHomePageStatsService).populateTable();
        }});

        callConstructor(true, false);

        assertEquals(1, diseasesPassedToGetDataSetsForDiseaseType.size());
        assertEquals("disease1", diseasesPassedToGetDataSetsForDiseaseType.get(0));
        assertEquals(0, numCallsToGetDataSetsForControls);
    }

    @Test
    public void testGetOrMakeModel() throws DataAccessMatrixQueries.DAMQueriesException {
        callConstructor(false, true);
        long startTime = System.currentTimeMillis();
        DAMModel model = factory.getOrMakeModel("disease1", false);
        long endTime = System.currentTimeMillis();
        assertTrue(endTime - startTime < timeToWait);
        assertEquals("disease1", model.getDiseaseType());
    }

    @Test
    public void testRefreshAll() throws Exception {
        addExpectationsForRefreshAll(1);
        callConstructor(false, true);
        DiseaseContextHolder.clearDisease();
        long startTime = System.currentTimeMillis();
        factory.refreshAll();
        long stopTime = System.currentTimeMillis();
        assertTrue(stopTime - startTime >= timeToWait - 10);
        assertNotNull(DiseaseContextHolder.getDisease());
    }

    @Test
    public void testOverlappingRefreshAll() throws Exception {
        addExpectationsForRefreshAll(3);
        callConstructor(true, true);

        MethodTimerRunner refreshRunner = new MethodTimerRunner() {
            protected void callMethod() throws DataAccessMatrixQueries.DAMQueriesException {
                factory.refreshAll();
            }
        };

        MethodTimerRunner secondRefreshRunner = new MethodTimerRunner() {
            protected void callMethod() throws DataAccessMatrixQueries.DAMQueriesException {
                factory.refreshAll();
            }
        };

        Thread refreshThread = new Thread(refreshRunner);
        Thread secondRefreshThread = new Thread(secondRefreshRunner);

        refreshThread.start();
        Thread.sleep(10);
        secondRefreshThread.start();
        while (!secondRefreshRunner.isDone || !refreshRunner.isDone) {
            Thread.sleep(100);
        }
        assertFalse(refreshRunner.hadErrors);
        assertFalse(secondRefreshRunner.hadErrors);
    }

    @Test
    public void testGetOrMakeModelDuringRefresh() throws Exception {
        addExpectationsForRefreshAll(2);
        callConstructor(true, true);
        MethodTimerRunner refreshRunner = new MethodTimerRunner() {
            protected void callMethod() throws DataAccessMatrixQueries.DAMQueriesException {
                factory.refreshAll();
            }
        };

        MethodTimerRunner getModelRunner = new MethodTimerRunner() {
            protected void callMethod() throws DataAccessMatrixQueries.DAMQueriesException {
                factory.getOrMakeModel("disease1", false);
            }
        };

        Thread refreshThread = new Thread(refreshRunner);
        Thread getModelThread = new Thread(getModelRunner);

        refreshThread.start();
        // make sure the first thread is already in the getDataSetsForDiseaseType wait before the second call happens
        Thread.sleep(10);
        getModelThread.start();
        while (!getModelRunner.isDone || !refreshRunner.isDone) {
            Thread.sleep(100);
        }
        assertFalse(refreshRunner.hadErrors);
        assertFalse(getModelRunner.hadErrors);
        assertTrue("Time waited = " + refreshRunner.waitTime, refreshRunner.waitTime >= timeToWait - 10);
        assertTrue("Time waited = " + getModelRunner.waitTime, getModelRunner.waitTime < timeToWait);

    }

    @Test
    public void testSchemaSwitchingWithRefresh() throws DataAccessMatrixQueries.DAMQueriesException {
        callConstructor(false, true);
        // first set the disease to something else
        DiseaseContextHolder.setDisease("NOT_TEST");
        factory.getOrMakeModel("TEST", true);
        // now check that calling getOrMakeModel actually set the disease
        assertEquals("TEST", DiseaseContextHolder.getDisease());
    }


    abstract class MethodTimerRunner implements Runnable {
        boolean isDone = false;
        boolean hadErrors = false;
        long waitTime;

        public void run() {
            try {
                long startTime = System.currentTimeMillis();
                callMethod();
                long stopTime = System.currentTimeMillis();
                waitTime = stopTime - startTime;
            } catch (DataAccessMatrixQueries.DAMQueriesException e) {
                hadErrors = true;
            } finally {
                isDone = true;
            }
        }

        protected abstract void callMethod() throws DataAccessMatrixQueries.DAMQueriesException;
    }


}
