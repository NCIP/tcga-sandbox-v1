/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.webservice;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.Disease;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.JobProcess;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobStatus;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzQueueJobDetails;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DAMDiseaseQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DAMUtils;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.processors.FilePackagerFactoryI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.Cell;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMModel;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.Header;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.StaticMatrixModelFactoryI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;

import java.lang.reflect.Field;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.action.CustomAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test Class for the Job Process Web service
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class JobProcessWSFastTest {

    private static final String INVALID_UUID = "/ticket/thisIsNotAValidUUID";

    private Mockery context = new JUnit4Mockery();
    private JobProcessWS webService;
    private DAMWSUtil damWsUtil;
    private DAMDiseaseQueries damDiseaseQueries;
    private CenterQueries centerQueries;
    private PlatformQueries platformQueries;
    private DataTypeQueries dataTypeQueries;
    private DAMUtils damUtils = DAMUtils.getInstance();
    private UriInfo uriInfo;
    private UriBuilder mockUri = UriBuilder.fromUri("bouhbah");
    private Disease mockDisease;
    private FilePackagerFactoryI mockFPFactory;

    @Before
    public void setup() throws Exception {

        final StaticMatrixModelFactoryI mockFactory = context.mock(StaticMatrixModelFactoryI.class);
        final DataAccessMatrixQueries mockDAO = context.mock(DataAccessMatrixQueries.class);
        final DAMModel mockStaticModel = context.mock(DAMModel.class);
        mockFPFactory = context.mock(FilePackagerFactoryI.class);
        final FilePackagerBean stillProcessingPackagerBean = getProcessingFilePackagerBean();

        damDiseaseQueries = context.mock(DAMDiseaseQueries.class);
        centerQueries = context.mock(CenterQueries.class);
        platformQueries = context.mock(PlatformQueries.class);
        dataTypeQueries = context.mock(DataTypeQueries.class);
        mockDisease = new Disease("GBM", "GBM", true);
        uriInfo = context.mock(UriInfo.class);

        Field damDiseaseQueriesField = DAMUtils.class.getDeclaredField("damDiseaseQueries");
        damDiseaseQueriesField.setAccessible(true);
        damDiseaseQueriesField.set(damUtils, damDiseaseQueries);
        Field centerQueriesField = DAMUtils.class.getDeclaredField("centerQueries");
        centerQueriesField.setAccessible(true);
        centerQueriesField.set(damUtils, centerQueries);
        Field platformQueriesField = DAMUtils.class.getDeclaredField("platformQueries");
        platformQueriesField.setAccessible(true);
        platformQueriesField.set(damUtils, platformQueries);
        Field dataTypeQueriesField = DAMUtils.class.getDeclaredField("dataTypeQueries");
        dataTypeQueriesField.setAccessible(true);
        dataTypeQueriesField.set(damUtils, dataTypeQueries);

        damWsUtil = new DAMWSUtil();
        webService = new JobProcessWS();
        damWsUtil.setSizeLimitGigs(25);
        damWsUtil.setArchivePhysicalPathPrefix("/tcgafile");
        damWsUtil.setDownloadLinkSite("http://myarchiverepo.com");

        Field mockDAOField = webService.getClass().getDeclaredField("dataAccessMatrixQueries");
        mockDAOField.setAccessible(true);
        mockDAOField.set(webService, mockDAO);
        Field mockStaticModelField = webService.getClass().getDeclaredField("staticMatrixModelFactory");
        mockStaticModelField.setAccessible(true);
        mockStaticModelField.set(webService, mockFactory);
        Field mockDAMWSUtilField = webService.getClass().getDeclaredField("damWSUtil");
        mockDAMWSUtilField.setAccessible(true);
        mockDAMWSUtilField.set(webService, damWsUtil);
        Field mockWSFilePackagerField = webService.getClass().getDeclaredField("fpFactory");
        mockWSFilePackagerField.setAccessible(true);
        mockWSFilePackagerField.set(webService, mockFPFactory);
        Field uriInfoField = webService.getClass().getDeclaredField("uriInfo");
        uriInfoField.setAccessible(true);
        uriInfoField.set(webService, uriInfo);

        final List<Header> ptHeaders = new ArrayList<Header>();
        final Header ptHeader1 = new Header(Header.HeaderCategory.PlatformType, "CN", null, Header.HeaderType.COL_HEADER);
        ptHeaders.add(ptHeader1);
        final Header ptHeader2 = new Header(Header.HeaderCategory.PlatformType, "Exp-Gene", null, Header.HeaderType.COL_HEADER);
        ptHeaders.add(ptHeader2);

        final List<Header> cHeaders = new ArrayList<Header>();
        final Header cHeader1 = new Header(Header.HeaderCategory.Center, "BI", ptHeader1, Header.HeaderType.COL_HEADER);
        ptHeader1.getChildHeaders().add(cHeader1);
        cHeaders.add(cHeader1);
        final Header cHeader2 = new Header(Header.HeaderCategory.Center, "JHU", ptHeader2, Header.HeaderType.COL_HEADER);
        ptHeader2.getChildHeaders().add(cHeader2);
        cHeaders.add(cHeader2);

        final List<Header> lHeaders = new ArrayList<Header>();
        final Header lHeader1 = new Header(Header.HeaderCategory.Level, "3", cHeader1, Header.HeaderType.COL_HEADER);
        cHeader1.getChildHeaders().add(lHeader1);
        lHeaders.add(lHeader1);
        final Header lHeader2 = new Header(Header.HeaderCategory.Level, "3", cHeader2, Header.HeaderType.COL_HEADER);
        cHeader2.getChildHeaders().add(lHeader2);
        lHeaders.add(lHeader2);

        final Header bHeader1 = new Header(Header.HeaderCategory.Batch, "Batch 1", null, Header.HeaderType.ROW_HEADER);
        final Header bHeader2 = new Header(Header.HeaderCategory.Batch, "Batch 2", null, Header.HeaderType.ROW_HEADER);

        final List<Header> sHeaders = new ArrayList<Header>();
        final Header sHeader1 = new Header(Header.HeaderCategory.Sample, "TCGA-01-0001-01", bHeader1, Header.HeaderType.ROW_HEADER);
        bHeader1.getChildHeaders().add(sHeader1);
        sHeaders.add(sHeader1);
        final Header sHeader2 = new Header(Header.HeaderCategory.Sample, "TCGA-01-0001-02", bHeader2, Header.HeaderType.ROW_HEADER);
        bHeader2.getChildHeaders().add(sHeader2);
        sHeaders.add(sHeader2);

        final List<DataSet> dataSets1 = new ArrayList<DataSet>();
        DataSet ds = new DataSetLevelTwoThree();
        ds.setBatch("Batch 0");
        ds.setCenterId("3");
        ds.setPlatformId("0");
        ds.setPlatformTypeId("0");
        ds.setLevel("3");
        ds.setSample("TCGA-01-0001-01");
        ds.setAvailability(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
        dataSets1.add(ds);

        final List<DataSet> dataSets2 = new ArrayList<DataSet>();
        DataSet ds2 = new DataSetLevelTwoThree();
        ds2.setBatch("Batch 1");
        ds2.setCenterId("3");
        ds2.setPlatformId("1");
        ds2.setPlatformTypeId("1");
        ds2.setLevel("3");
        ds2.setSample("TCGA-01-0001-02");
        ds2.setAvailability(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
        dataSets2.add(ds2);

        final List<DataSet> allDataSets = new ArrayList<DataSet>();
        allDataSets.addAll(dataSets1);
        allDataSets.addAll(dataSets2);

        final List<Cell> sampleCells1 = new ArrayList<Cell>();
        Cell cellone = new Cell();
        cellone.setId("1");
        cellone.addDataset(ds);
        sampleCells1.add(cellone);
        Cell celloneBlank = new Cell();
        celloneBlank.setId("2");
        DataSet nullDS1 = new DataSet();
        nullDS1.setAvailability(DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE);
        celloneBlank.addDataset(nullDS1);
        sampleCells1.add(celloneBlank);

        final List<Cell> sampleCells2 = new ArrayList<Cell>();
        Cell cell2blank = new Cell();
        cell2blank.setId("3");
        DataSet nullDS2 = new DataSet();
        nullDS2.setAvailability(DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE);
        cell2blank.addDataset(nullDS2);
        sampleCells2.add(cell2blank);
        Cell cell2 = new Cell();
        cell2.setId("4");
        cell2.addDataset(ds2);
        sampleCells2.add(cell2);

        final List<DataFile> dataFiles1 = new ArrayList<DataFile>();
        DataFile df = new DataFileLevelThree();
        df.setCenterId("1");
        df.setPlatformId("1");
        df.setPlatformTypeId("2");
        df.setSamples(Arrays.asList("TCGA-01-0001-01"));
        df.setSize(999L);
        dataFiles1.add(df);

        final List<DataFile> dataFiles2 = new ArrayList<DataFile>();
        df = new DataFileLevelThree();
        df.setCenterId("1");
        df.setPlatformId("1");
        df.setPlatformTypeId("2");
        df.setSamples(Arrays.asList("TCGA-01-0001-02"));
        df.setSize(1001L);
        dataFiles2.add(df);

        final List<DataFile> allDataFiles = new ArrayList<DataFile>();
        allDataFiles.addAll(dataFiles1);
        allDataFiles.addAll(dataFiles2);

        context.checking(new Expectations() {{
            allowing(damDiseaseQueries).getDisease("GBM");
            will(returnValue(mockDisease));
            allowing(centerQueries).getAllCenters();
            will(returnValue(makeMockCenters()));
            allowing(dataTypeQueries).getAllDataTypes();
            will(returnValue(makeMockDataTypes()));
            allowing(platformQueries).getAllPlatforms();
            will(returnValue(makeMockPlatforms()));
            allowing(mockFactory).getOrMakeModel(DataAccessMatrixQueries.DEFAULT_DISEASETYPE, false);
            will(returnValue(mockStaticModel));
            allowing(mockStaticModel).getDiseaseType();
            will(returnValue(DataAccessMatrixQueries.DEFAULT_DISEASETYPE));
            allowing(mockStaticModel).getHeadersForCategory(Header.HeaderCategory.PlatformType);
            will(returnValue(ptHeaders));
            allowing(mockStaticModel).getHeadersForCategory(Header.HeaderCategory.Center);
            will(returnValue(cHeaders));
            allowing(mockStaticModel).getHeadersForCategory(Header.HeaderCategory.Level);
            will(returnValue(lHeaders));
            allowing(mockStaticModel).getTotalBatches();
            will(returnValue(2));
            allowing(mockStaticModel).getBatchHeader(0);
            will(returnValue(bHeader1));
            allowing(mockStaticModel).getBatchHeader(1);
            will(returnValue(bHeader2));
            allowing(mockStaticModel).getHeadersForCategory(Header.HeaderCategory.Sample);
            will(returnValue(sHeaders));
            allowing(mockStaticModel).getCellsForHeader(sHeader1);
            will(returnValue(sampleCells1));
            allowing(mockStaticModel).getCellsForHeader(sHeader2);
            will(returnValue(sampleCells2));
            allowing(mockStaticModel).getCellsForHeader(lHeader1);
            will(returnValue(sampleCells1));
            allowing(mockStaticModel).getCellsForHeader(lHeader2);
            will(returnValue(sampleCells2));
            allowing(mockDAO).getFileInfoForSelectedDataSets(new ArrayList(), false);
            will(returnValue(dataFiles1));
            allowing(mockDAO).getFileInfoForSelectedDataSets(dataSets1, false);
            will(returnValue(dataFiles1));
            allowing(mockDAO).getFileInfoForSelectedDataSets(dataSets2, false);
            will(returnValue(dataSets2));
            allowing(uriInfo).getAbsolutePathBuilder();
            will(returnValue(mockUri));
            allowing(mockFPFactory).createFilePackagerBean(with(any(String.class)), with(any(List.class)), with(any(String.class)),
                    with(any(Boolean.class)), with(any(Boolean.class)), with(any(UUID.class)), with(any(FilterRequestI.class)));
            will(returnValue(stillProcessingPackagerBean));

            allowing(mockFPFactory).enqueueFilePackagerBean(stillProcessingPackagerBean);
            will(getActionForFilePackagerBean(stillProcessingPackagerBean));
        }});

    }

    /**
     * Return the <code>Action</code> to be used by the mock <code>FilePackagerFactory</code> when expecting methods enqueueFilePackagerBean() and getQuartzJobHistory()
     * in order to instantiate the <code>FilePackagerFactory.quartzJobHistory</code>
     *
     * @param filePackagerBean the <code>FilePackagerBean<code> to act upon
     * @return the <code>Action</code> to be used by the mock <code>FilePackagerFactory</code> when expecting methods enqueueFilePackagerBean() and getQuartzJobHistory()
     */
    private CustomAction getActionForFilePackagerBean(final FilePackagerBean filePackagerBean) {

        return new CustomAction("filePackagerBeanCustomAction") {

            /**
             * The implementation of the FilePackagerEnqueuer instantiate FilePackagerBean.quartzJobHistory so do it here.
             * Return the FilePackagerBean's <code>QuartzJobHistory</code> if the method call was getQuartzJobHistory(), <code>null</code otherwise
             *
             * @param invocation the <code>Invocation</code>
             * @return the FilePackagerBean's <code>QuartzJobHistory</code> if the method call was getQuartzJobHistory(), <code>null</code otherwise
             * @throws Throwable
             */
            @Override
            public Object invoke(Invocation invocation) throws Throwable {

                final QuartzJobHistory quartzJobHistory = new QuartzJobHistory();
                quartzJobHistory.setJobName(UUID.randomUUID().toString());
                quartzJobHistory.setJobWSSubmissionDate(new Date());
                filePackagerBean.setQuartzJobHistory(quartzJobHistory);

                final boolean returnQuartJobHistory = "getQuartzJobHistory".equals(invocation.getInvokedMethod().getName());

                return returnQuartJobHistory?filePackagerBean.getQuartzJobHistory():null;
            }
        };
    }

    /**
     * We can't actually test for the formatted json or xml result but just for a filled up jobProcess object.
     */
    @Test
    public void testProcessJobToJsonNoTicket() throws Exception {

        final String platformAlias = "ABI";

        final Platform expectedPlatform = new Platform();
        expectedPlatform.setPlatformAlias(platformAlias);
        expectedPlatform.setCenterType("GSC");
        context.checking(new Expectations() {{
            allowing(platformQueries).getPlatformWithAlias(platformAlias);
            will(returnValue(expectedPlatform));
        }});

        webService.disease = "GBM";
        webService.center = "BI";
        webService.email = "i@i.com";
        webService.level = "3";
        webService.platform = platformAlias;
        webService.platformType = "CN";

        final JobProcess job = webService.processJobToJson(null);

        assertNotNull(job);
        assertNotNull(job.getTicket());
        assertNotNull(job.getSubmissionTime());
        assertNotNull(job.getStatusCheckUrl());
        assertEquals("201", job.getJobStatus().getStatusCode());
        assertTrue(job.getStatusCheckUrl().contains("bouhbah/ticket/"));
    }

    /**
     * We can't actually test for the formatted json or xml result but just for a filled up jobProcess object.
     */
    @Test
    public void testProcessJobToXmlNoTicket() throws Exception {

        final String platformAlias = "ABI";

        final Platform expectedPlatform = new Platform();
        expectedPlatform.setPlatformAlias(platformAlias);
        expectedPlatform.setCenterType("GSC");

        context.checking(new Expectations() {{
            allowing(platformQueries).getPlatformWithAlias(platformAlias);
            will(returnValue(expectedPlatform));
        }});

        webService.disease = "GBM";
        webService.center = "BI";
        webService.email = "i@i.com";
        webService.level = "3";
        webService.platform = "ABI";
        webService.platformType = "CN";

        final JobProcess job = webService.processJobToXml(null);

        assertNotNull(job);
        assertNotNull(job.getTicket());
        assertNotNull(job.getSubmissionTime());
        assertNotNull(job.getStatusCheckUrl());
        assertEquals("201", job.getJobStatus().getStatusCode());
        assertTrue(job.getStatusCheckUrl().contains("bouhbah/ticket/"));
    }

    @Test(expected = WebApplicationException.class)
    public void testProcessJobBadDate() throws Exception {
        webService.disease = "GBM";
        webService.center = "BI";
        webService.email = "i@i.com";
        webService.level = "3";
        webService.platformType = "CN";
        webService.endDate = "bad date";
        JobProcess job = webService.processJobToXml(null);
    }

    @Test(expected = WebApplicationException.class)
    public void testProcessJobMissingArgument() throws Exception {
        webService.disease = "GBM";
        webService.center = "BI";
        webService.email = "i@i.com";
        webService.level = "3";
        webService.platformType = "CN";
        JobProcess job = webService.processJobToXml(null);
    }

    @Test(expected = WebApplicationException.class)
    public void testProcessJobToXmlWithInvalidTicket() throws Exception {

        final JobProcess jobProcess = webService.processJobToXml(INVALID_UUID);
        assertNull(jobProcess);
    }

    @Test(expected = WebApplicationException.class)
    public void testProcessJobToJsonWithInvalidTicket() throws Exception {

        final JobProcess jobProcess = webService.processJobToJson(INVALID_UUID);
        assertNull(jobProcess);
    }

    @Test
    public void testProcessJobToJsonWithTicket() throws Exception {

        final String uuidAsString = "067e6162-3b6f-4ae2-a171-2470b63dff00";
        final QuartzJobHistory acceptedQuartzJobHistory = getAcceptedQuartzJobHistory(uuidAsString);

        context.checking(new Expectations() {{
            allowing(mockFPFactory).getQuartzJobHistory(UUID.fromString(uuidAsString));
            will(returnValue(acceptedQuartzJobHistory));
        }});

        JobProcess job = webService.processJobToJson("/ticket/" + uuidAsString);
        assertNotNull(job);
        assertNotNull(job.getTicket());
        assertNotNull(job.getSubmissionTime());
        assertNotNull(job.getStatusCheckUrl());
        assertEquals("202", job.getJobStatus().getStatusCode());
        assertTrue(job.getStatusCheckUrl().contains("bouhbah"));
    }

    @Test
    public void testProcessJobToJsonWithTicketDone() throws Exception {

        final String uuidAsString = "067e6162-3b6f-4ae2-a171-2470b63dff01";
        final QuartzJobHistory succeededQuartzJobHistory = getSucceededQuartzJobHistory();

        context.checking(new Expectations() {{
            allowing(mockFPFactory).getQuartzQueueJobDetails(UUID.fromString(uuidAsString));
            will(returnValue(null));
            allowing(mockFPFactory).getQuartzJobHistory(UUID.fromString(uuidAsString));
            will(returnValue(succeededQuartzJobHistory));
        }});

        final JobProcess job = webService.processJobToJson("/ticket/067e6162-3b6f-4ae2-a171-2470b63dff01");

        assertNotNull(job);
        assertNotNull(job.getTicket());
        assertNotNull(job.getSubmissionTime());
        assertNotNull(job.getStatusCheckUrl());
        assertEquals("200", job.getJobStatus().getStatusCode());
        assertTrue(job.getStatusCheckUrl(), job.getStatusCheckUrl().contains("bouhbah"));
        assertEquals("https://tcga-data.nci.nih.gov/tcga/blah/blahblah/test-archive.tar.gz", job.getJobStatus().getArchiveUrl());
    }

    @Test(expected = WebApplicationException.class)
    public void testProcessJobToJsonWithTicketError() throws Exception {

        final String uuidAsString = "067e6162-3b6f-4ae2-a171-2470b63dff02";
        final QuartzJobHistory failedQuartzJobHistory = getFailedQuartzJobHistory();

        context.checking(new Expectations() {{
            allowing(mockFPFactory).getQuartzQueueJobDetails(UUID.fromString(uuidAsString));
            will(returnValue(null));
            allowing(mockFPFactory).getQuartzJobHistory(UUID.fromString(uuidAsString));
            will(returnValue(failedQuartzJobHistory));
        }});

        webService.processJobToJson("/ticket/067e6162-3b6f-4ae2-a171-2470b63dff02");
    }


    private Collection<Map<String, Object>> makeMockCenters() {
        Collection<Map<String, Object>> rows = new LinkedList<Map<String, Object>>();
        rows.add(new HashMap() {{
            put("center_id", "1");
            put("short_name", "BI");
            put("domain_name", "broad.mit.edu");
            put("center_type_code", "GSC");
        }});
        return rows;
    }

    private Collection<Map<String, Object>> makeMockPlatforms() {
        Collection<Map<String, Object>> rows = new LinkedList<Map<String, Object>>();
        rows.add(new HashMap() {{
            put("platform_id", "17");
            put("platform_alias", "ABI");
        }});
        return rows;
    }

    private Collection<Map<String, Object>> makeMockDataTypes() {
        Collection<Map<String, Object>> rows = new LinkedList<Map<String, Object>>();
        rows.add(new HashMap() {{
            put("data_type_id", "2");
            put("name", "Copy Number Results");
            put("ftp_display", "CN");
        }});
        return rows;
    }

    private FilePackagerBean getProcessingFilePackagerBean() {
        FilePackagerBean stillProcessingPackagerBean = new FilePackagerBean();
        stillProcessingPackagerBean.setArchivePhysicalPathPrefix("/tcgafile");
        stillProcessingPackagerBean.setArchiveLinkSite("http://myarchiverepo.com");
        stillProcessingPackagerBean.setJobWSSubmissionDate(new Date());
        stillProcessingPackagerBean.setEstimatedUncompressedSize(999L);
        stillProcessingPackagerBean.setKey(UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00"));
        stillProcessingPackagerBean.setStatus(QuartzJobStatus.Queued);
        return stillProcessingPackagerBean;
    }

    /**
     * Return a <code>QuartzJobHistory</code> for a failed job
     *
     * @return a <code>QuartzJobHistory</code> for a failed job
     */
    private QuartzJobHistory getFailedQuartzJobHistory() {

        final QuartzJobHistory result = new QuartzJobHistory();
        result.setJobName("067e6162-3b6f-4ae2-a171-2470b63dff02");
        result.setStatus(QuartzJobStatus.Failed);
        result.setLinkText("blah blah error filePackager");

        return result;
    }

    /**
     * Return a <code>QuartzJobHistory</code> for a succeeded job
     *
     * @return a <code>QuartzJobHistory</code> for a succeeded job
     */
    private QuartzJobHistory getSucceededQuartzJobHistory() {

        final QuartzJobHistory result = new QuartzJobHistory();
        result.setJobName("067e6162-3b6f-4ae2-a171-2470b63dff01");
        result.setStatus(QuartzJobStatus.Succeeded);
        result.setJobWSSubmissionDate(new Date());
        result.setLinkText("https://tcga-data.nci.nih.gov/tcga/blah/blahblah/test-archive.tar.gz");
        result.setEstimatedUncompressedSize(999L);

        return result;
    }

    /**
     * Return a <code>QuartzJobHistory</code> for an accepted job
     *
     * @param uuidAsString
     * @return a <code>QuartzJobHistory</code> for an accepted job
     */
    private QuartzJobHistory getAcceptedQuartzJobHistory(String uuidAsString) {

        final QuartzJobHistory result = new QuartzJobHistory();
        result.setJobName(uuidAsString);
        result.setStatus(QuartzJobStatus.Accepted);
        result.setJobWSSubmissionDate(new Date());

        return result;
    }

    /**
     * Return a <code>QuartzQueueJobDetails</code> with the given job name
     *
     * @param jobName the job name
     * @return a <code>QuartzQueueJobDetails</code> with the given job name
     */
    private QuartzQueueJobDetails getPretendQuartzQueueJobDetails(final String jobName) {

        final String jobGroup = "jobGroup";
        final String description = "decription";
        final String jobClassName = "job.class.name";
        final boolean isDurable = true;
        final boolean isVolatile = true;
        final boolean isStateFul = true;
        final boolean requestsRecovery = true;
        final Blob jobData = null;

        final QuartzQueueJobDetails result = new QuartzQueueJobDetails(
                jobName,
                jobGroup,
                description,
                jobClassName,
                isDurable,
                isVolatile,
                isStateFul,
                requestsRecovery,
                jobData
        );

        result.setJobWSSubmissionDate(new Date());
        result.setEstimatedUncompressedSize(2011L);

        return result;
    }

    //saraswatv: these test cases are designed to test condition for clinical job process
    //for test to fail user should put values for center,platform and level
    //alongwith clinical test platformType=[cC]

    @Test(expected = WebApplicationException.class)
    public void testProcessJobClinicalBadCenter() throws Exception {
        webService.disease = "GBM";
        webService.center = "BI";
        webService.platformType = "C";
        final JobProcess job = webService.processJobToJson(null);
        final JobProcess jobXml = webService.processJobToXml(null);
    }

    @Test(expected = WebApplicationException.class)
    public void testProcessJobClinicalBadPlatform() throws Exception {
        webService.disease = "GBM";
        webService.platform = "ABI";
        webService.platformType = "C";
        final JobProcess job = webService.processJobToJson(null);
        final JobProcess jobXml = webService.processJobToXml(null);
    }

    @Test(expected = WebApplicationException.class)
    public void testProcessJobClinicalBadLevel() throws Exception {
        webService.disease = "GBM";
        webService.level = "3";
        webService.platformType = "C";
        final JobProcess job = webService.processJobToJson(null);
        final JobProcess jobXml = webService.processJobToXml(null);
    }

   @Test
    public void testProcessJobClinicalGood(){

        webService.disease = "GBM";
        webService.platformType = "C";
        webService.protectedStatus="N";
        webService.sampleList="TCGA-02-0001-01";
        final JobProcess job = webService.processJobToJson(null);
        final JobProcess jobXml = webService.processJobToXml(null);

        assertNotNull(job);
        assertNotNull(job.getTicket());
        assertNotNull(job.getSubmissionTime());
        assertNotNull(job.getStatusCheckUrl());
        assertEquals("201", job.getJobStatus().getStatusCode());
        assertTrue(job.getStatusCheckUrl().contains("bouhbah/ticket/"));
   }
}
