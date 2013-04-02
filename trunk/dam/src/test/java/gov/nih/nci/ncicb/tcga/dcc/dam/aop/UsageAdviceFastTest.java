/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.aop;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileClinical;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelOne;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwo;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileMetadata;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage.AbstractUsageLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage.UsageLoggerException;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage.UsageLoggerI;
import gov.nih.nci.ncicb.tcga.dcc.dam.processors.FilePackager;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.FilterRequestValidator;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.Header;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.prefs.Preferences;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

/**
 * Tests UsageAdvice class.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
@RunWith(JMock.class)
public class UsageAdviceFastTest {

    private final Mockery context = new JUnit4Mockery();
    private UsageAdvice advice;
    private UsageLoggerI mockUsageLoggerDb;
    private Map<String, Object> expectedActions;
    private FilePackager fakeFilePackager;
    private List<DataFile> dataFiles;
    private FilePackagerBean filePackagerBean;

    private static final String FP_KEY = "067e6162-3b6f-4ae2-a171-2470b63dff00";
    private static final String SAMPLE_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FOLDER = SAMPLE_DIR + "usageAdvice";
    private FilterRequestValidator mockFilterRequestValidator;

    @Before
    public void setUp() throws UsageLoggerException {
        mockUsageLoggerDb = context.mock(UsageLoggerI.class, "usageLoggerDb");
        final UsageLoggerI mockUsageLoggerFile = context.mock(UsageLoggerI.class, "usageLoggerFile");
        advice = new UsageAdvice();
        advice.setUsageLoggerDb(mockUsageLoggerDb);
        mockFilterRequestValidator = context.mock(FilterRequestValidator.class);
        advice.setFilterRequestValidator(mockFilterRequestValidator);
        advice.setUsageLoggerFile(mockUsageLoggerFile);
        advice.setWriteToDb(true);
        expectedActions = new HashMap<String, Object>();
        fakeFilePackager = new FilePackager();
        dataFiles = new ArrayList<DataFile>();
        filePackagerBean = new FilePackagerBean();
        filePackagerBean.setKey(UUID.fromString(FP_KEY));
        fakeFilePackager.setFilePackagerBean(filePackagerBean);
    }

    @Test
    public void testAfterSetSelectedFiles() throws UsageLoggerException, IOException {

        dataFiles.add(makeDataFile("1", false, 100));
        dataFiles.add(makeDataFile("1", true, 200));
        dataFiles.add(makeDataFile("2", false, 500));
        dataFiles.add(makeDataFile("2", true, 10));
        dataFiles.add(makeDataFile(DataAccessMatrixQueries.LEVEL_CLINICAL, true, 500));
        // 5 files total, 2 level 1, 2 level 2, 1 clinical; 2 public, 3 protected

        addExpectedAction(AbstractUsageLogger.ActionType.FILES_SELECTED, 5);
        addExpectedAction(AbstractUsageLogger.ActionType.LEVEL_1_FILES_SELECTED, 2);
        addExpectedAction(AbstractUsageLogger.ActionType.LEVEL_2_FILES_SELECTED, 2);
        addExpectedAction(AbstractUsageLogger.ActionType.LEVEL_3_FILES_SELECTED, 0);
        addExpectedAction(AbstractUsageLogger.ActionType.CLINICAL_FILES_SELECTED, 1);
        addExpectedAction(AbstractUsageLogger.ActionType.PUBLIC_FILES_SELECTED, 2);
        addExpectedAction(AbstractUsageLogger.ActionType.PROTECTED_FILES_SELECTED, 3);
        addExpectedAction(AbstractUsageLogger.ActionType.CALCULATED_SIZE, 1310L);
        addExpectedAction(AbstractUsageLogger.ActionType.METADATA_FILES_SELECTED, 0);

        fakeFilePackager.getFilePackagerBean().setSelectedFiles(dataFiles);

        context.checking(new Expectations() {{
            one(mockUsageLoggerDb).logActionGroup(with(FP_KEY), with(correctActionMap(expectedActions)));
        }});

        advice.setSelectedFilesAction(dataFiles, fakeFilePackager);
    }

    @Test
    public void testBeforeRunJob() throws ParseException, IOException, UsageLoggerException {
        context.checking(new Expectations() {{
            one(mockUsageLoggerDb).logAction(FP_KEY,
                    AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.ARCHIVE_STARTED),
                    null);
        }});

        advice.archiveCreationStartedAction(filePackagerBean);
    }

    @Test
    public void testAfterRunJob() throws ParseException, IOException, UsageLoggerException {

        fakeFilePackager.setStartTime(0);
        fakeFilePackager.getFilePackagerBean().setCreationTime(0);
        fakeFilePackager.setEndFileProcessingTime(500);
        fakeFilePackager.setEndTime(1000);
        fakeFilePackager.setActualUncompressedSize(12345);
        filePackagerBean.setArchivePhysicalName(TEST_DATA_FOLDER + File.separator + "test");
        addExpectedAction(AbstractUsageLogger.ActionType.ARCHIVE_FINISHED, null);
        addExpectedAction(AbstractUsageLogger.ActionType.COMPRESSED_SIZE, 333L);
        addExpectedAction(AbstractUsageLogger.ActionType.UNCOMPRESSED_SIZE, 12345L);
        addExpectedAction(AbstractUsageLogger.ActionType.WAITING_IN_QUEUE_TIME, 0L);
        addExpectedAction(AbstractUsageLogger.ActionType.ARCHIVE_GENERATION_TIME, 0L);
        addExpectedAction(AbstractUsageLogger.ActionType.FILE_PROCESSING_TIME, 500L);
        addExpectedAction(AbstractUsageLogger.ActionType.TOTAL_ARCHIVE_CREATION_TIME, 1000L);
        addExpectedAction(AbstractUsageLogger.ActionType.WAITING_IN_QUEUE_TIME, 0L);
        addExpectedAction(AbstractUsageLogger.ActionType.ARCHIVE_FINISHED, null);
        fakeFilePackager.getFilePackagerBean().setSelectedFiles(dataFiles);

        context.checking(new Expectations() {{
            one(mockUsageLoggerDb).logActionGroup(with(FP_KEY), with(correctActionMap(expectedActions)));
        }});
        advice.archiveCreationFinishedAction(fakeFilePackager);
    }

    @Test
    public void testBeforeQueueForProcessing() throws IOException, UsageLoggerException, ParseException {
        context.checking(new Expectations() {{
            one(mockUsageLoggerDb).logAction(FP_KEY,
                    AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.ARCHIVE_QUEUED),
                    null);
        }});

        advice.archiveCreationQueuedAction(FP_KEY);
    }

    @Test
    public void testAfterDAMControllerHandle() throws IOException, UsageLoggerException {

        final String testDiseaseName = "TEST DISEASE";
        context.checking(new Expectations() {{
            one(mockUsageLoggerDb).logAction("1",
                    AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.DAM_REQUESTED),
                    testDiseaseName);
        }});
        advice.dataAccessMatrixRequestedAction("1", testDiseaseName);
    }

    @Test
    public void testAfterHeaderSelected() throws UsageLoggerException, IOException {

        final Header header = new Header(Header.HeaderCategory.PlatformType, "Exp-Genes", null, Header.HeaderType.COL_HEADER);

        addExpectedAction(AbstractUsageLogger.ActionType.HEADER_SELECTED, header.getName());
        addExpectedAction(AbstractUsageLogger.ActionType.HEADER_CATEGORY_SELECTED, header.getCategory());
        addExpectedAction(AbstractUsageLogger.ActionType.HEADER_TYPE_SELECTED, header.getHeaderType());
        context.checking(new Expectations() {{
            one(mockUsageLoggerDb).logActionGroup(with(FP_KEY), with(correctActionMap(expectedActions)));
        }});
        advice.headerSelectedAction(FP_KEY, header, false, true); // not intersected; selected
    }

    @Test
    public void testAfterHeaderIntersected() throws UsageLoggerException {

        final Header header = new Header(Header.HeaderCategory.Batch, "Batch 3", null, Header.HeaderType.ROW_HEADER);
        addExpectedAction(AbstractUsageLogger.ActionType.HEADER_INTERSECTED, header.getName());
        addExpectedAction(AbstractUsageLogger.ActionType.HEADER_CATEGORY_INTERSECTED, header.getCategory());
        addExpectedAction(AbstractUsageLogger.ActionType.HEADER_TYPE_INTERSECTED, header.getHeaderType());
        context.checking(new Expectations() {{
            one(mockUsageLoggerDb).logActionGroup(with(FP_KEY), with(correctActionMap(expectedActions)));
        }});
        advice.headerSelectedAction(FP_KEY, header, true, true); // intersected; selected
    }

    @Test
    public void testAfterHeaderDeselected() throws UsageLoggerException {

        final Header header = new Header(Header.HeaderCategory.PlatformType, "Exp-Genes", null, Header.HeaderType.COL_HEADER);
        addExpectedAction(AbstractUsageLogger.ActionType.HEADER_DESELECTED, header.getName());
        addExpectedAction(AbstractUsageLogger.ActionType.HEADER_CATEGORY_DESELECTED, header.getCategory());
        addExpectedAction(AbstractUsageLogger.ActionType.HEADER_TYPE_DESELECTED, header.getHeaderType());
        context.checking(new Expectations() {{
            one(mockUsageLoggerDb).logActionGroup(with(FP_KEY), with(correctActionMap(expectedActions)));
        }});
        advice.headerSelectedAction(FP_KEY, header, false, false); // not intersected; not selected
    }

    @Test
    public void testAfterDamReset() throws UsageLoggerException {
        context.checking(new Expectations() {{
            one(mockUsageLoggerDb).logAction("1234", AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.DAM_RESET), null);
        }});
        advice.unselectAllAction("1234");
    }

    @Test
    public void testAfterFilterControllerHandle() throws ParseException, UsageLoggerException, IOException {
        final String[] availabilitySelected = new String[]{"A"};
        final String[] batchesSelected = new String[]{"Batch 1", "Batch 2"};
        final String[] centersSelected = new String[]{"Broad", "Stanford", "UNC"};
        final String[] platformsSelected = new String[]{"SNP6", "HumanMethylationn27", "IlluminaRNASeq"};
        final String[] levelsSelected = new String[]{"1", "2", "3", "X"};
        final String[] validLevels = new String[]{"1", "2", "3"};
        final String[] protectedStatusSelected = new String[]{"P", "N"};
        final String[] platformTypesSelected = new String[]{"Exp-Gene"};
        final String[] tumorNormalSelected = new String[]{"normal"};
        final String[] samplesSelected = new String[]{"TCGA-1234-567-89"};

        final StringBuilder centerPlatforms = new StringBuilder();
        centerPlatforms.append(centersSelected[0]).append(".").append(platformsSelected[0]).append(",");
        centerPlatforms.append(centersSelected[1]).append(".").append(platformsSelected[1]).append(",");
        centerPlatforms.append(centersSelected[2]).append(".").append(platformsSelected[2]);

        final FilterRequest filter = new FilterRequest();
        filter.setMode(FilterRequestI.Mode.ApplyFilter);
        filter.setAvailability("A");
        filter.setBatch("Batch 1,Batch 2");
        // called setCenter but from the DAM it is center.platform!  Needs to be renamed, confusing...
        filter.setCenter(centerPlatforms.toString());
        filter.setLevel("1,2,3,X");
        filter.setPlatformType("Exp-Gene");
        filter.setProtectedStatus("P,N");
        filter.setSampleList("TCGA-1234-567-89");
        filter.setTumorNormal("normal");
        filter.setEndDate("07/15/2008");
        filter.setStartDate("06/01/2007");

        addExpectedAction(AbstractUsageLogger.ActionType.FILTER_APPLIED, null);
        addExpectedAction(AbstractUsageLogger.ActionType.AVAILABILITY_FILTER_APPLIED, availabilitySelected);
        addExpectedAction(AbstractUsageLogger.ActionType.BATCH_FILTER_APPLIED, batchesSelected);
        addExpectedAction(AbstractUsageLogger.ActionType.CENTER_FILTER_APPLIED, centersSelected);
        addExpectedAction(AbstractUsageLogger.ActionType.PLATFORM_FILTER_APPLIED, platformsSelected);
        addExpectedAction(AbstractUsageLogger.ActionType.LEVEL_FILTER_APPLIED, validLevels);
        addExpectedAction(AbstractUsageLogger.ActionType.PLATFORM_TYPE_FILTER_APPLIED, platformTypesSelected);
        addExpectedAction(AbstractUsageLogger.ActionType.PROTECTED_STATUS_FILTER_APPLIED, protectedStatusSelected);
        addExpectedAction(AbstractUsageLogger.ActionType.SAMPLE_FILTER_APPLIED, samplesSelected);
        addExpectedAction(AbstractUsageLogger.ActionType.TUMOR_NORMAL_FILTER_APPLIED, tumorNormalSelected);
        addExpectedAction(AbstractUsageLogger.ActionType.END_DATE_FILTER_APPLIED, "07/15/2008");
        addExpectedAction(AbstractUsageLogger.ActionType.START_DATE_FILTER_APPLIED, "06/01/2007");
        context.checking(new Expectations() {{
            one(mockUsageLoggerDb).logActionGroup(with(FP_KEY), with(correctActionMap(expectedActions)));
            one(mockFilterRequestValidator).getValidAvailabilitySelections(availabilitySelected);
            will(returnValue(availabilitySelected));
            one(mockFilterRequestValidator).getValidBatchSelections(batchesSelected);
            will(returnValue(batchesSelected));
            one(mockFilterRequestValidator).getValidCenterSelections(centersSelected);
            will(returnValue(centersSelected));
            one(mockFilterRequestValidator).getValidLevelSelections(levelsSelected);
            will(returnValue(validLevels));
            one(mockFilterRequestValidator).getValidPlatformTypeSelections(platformTypesSelected);
            will(returnValue(platformTypesSelected));
            one(mockFilterRequestValidator).getValidProtectedStatusSelections(protectedStatusSelected);
            will(returnValue(protectedStatusSelected));
            one(mockFilterRequestValidator).getValidTumorNormalSelections(tumorNormalSelected);
            will(returnValue(tumorNormalSelected));
            one(mockFilterRequestValidator).getValidSampleSelections("TCGA-1234-567-89");
            will(returnValue(samplesSelected));
            one(mockFilterRequestValidator).getValidPlatformSelections(platformsSelected);
            will(returnValue(platformsSelected));
        }});

        advice.filterAppliedAction(FP_KEY, filter);
    }

    @Test
    public void testClearFilter() throws UsageLoggerException {
        final FilterRequest filterRequest = new FilterRequest();
        filterRequest.setMode(FilterRequestI.Mode.Clear);
        context.checking(new Expectations() {{
            one(mockUsageLoggerDb).logAction("1234", "filter cleared", null);
        }});
        advice.filterAppliedAction("1234", filterRequest);

    }

    @Test
    public void testAfterDADControllerHandle() throws UsageLoggerException, IOException {
        context.checking(new Expectations() {{
            one(mockUsageLoggerDb).logAction("123", AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.DAD_REQUESTED), null);
        }});

        advice.dadPageRequestedAction("123");
    }

    @Test
    public void testAfterDAMColorSchemeControllerHandle() throws UsageLoggerException, IOException {
        context.checking(new Expectations() {{
            one(mockUsageLoggerDb).logAction("123", AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.COLOR_SCHEME_CHANGED), "tissue type");
        }});

        advice.colorSchemeChangedAction("123", "tissue type");
    }

    private DataFile makeDataFile(final String level, final boolean isProtected, final long size) {
        final DataFile df;
        if (level.equals("1")) {
            df = new DataFileLevelOne();
        } else if (level.equals("2")) {
            df = new DataFileLevelTwo();
        } else if (level.equals("3")) {
            df = new DataFileLevelThree();
        } else if (level.equals(DataAccessMatrixQueries.LEVEL_CLINICAL)) {
            df = new DataFileClinical();
        } else if (level.equals(DataAccessMatrixQueries.LEVEL_METADATA)) {
            df = new DataFileMetadata();
        } else {
            throw new IllegalArgumentException("Unknown data file type");
        }
        df.setProtected(isProtected);
        df.setSize(size);
        return df;
    }

    private void addExpectedAction(final AbstractUsageLogger.ActionType actionType, final Object value) {
        expectedActions.put(AbstractUsageLogger.getActionName(actionType), value);
    }

    private static TypeSafeMatcher<Map<String, Object>> correctActionMap(final Map<String, Object> expectedActions) {
        return new TypeSafeMatcher<Map<String, Object>>() {
            @Override
            public boolean matchesSafely(final Map<String, Object> actionMap) {
                // all keys in expectedActions should be in this, and values should all be the same
                final Set<String> keys = actionMap.keySet();
                final Set<String> expectedKeys = expectedActions.keySet();
                if (keys.size() != expectedKeys.size()) {
                    return false;
                } else {
                    for (final String expectedKey : expectedKeys) {
                        final Object value = actionMap.get(expectedKey);
                        final Object expectedValue = expectedActions.get(expectedKey);
                        if (expectedValue instanceof Object[]) {
                            if (!Arrays.equals((Object[]) expectedValue, (Object[]) value)) {
                                System.out.println("Expected " + expectedValue + " for " + expectedKey + " but found " + value);
                                return false;
                            }
                        }
                        else if (expectedValue == null) {
                            if (value != null) {
                                System.out.println("Expected null for " + expectedKey + " but found " + value);
                                return false;
                            }
                        } else if(!value.equals(expectedValue)) {
                            System.out.println("Expected " + expectedValue + " (" + expectedValue.getClass().getName() + ") " +
                                    " for " + expectedKey + " but found " + value + " (" + value.getClass().getName() + ")");
                            return false;
                        }
                    }
                }
                return true;
            }

            @Override
            public void describeTo(final Description description) {
                  description.appendText("matches all keys and values");
            }
        };
    }

}
