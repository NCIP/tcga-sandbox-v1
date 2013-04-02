/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.TraceRelationship;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRDataService;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRIDQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.TraceRelationshipQueries;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.File;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test class for TraceFileProcessor
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class TraceFileProcessorFastTest {

    private static final String SAMPLE_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private String goodFile = SAMPLE_DIR + "qclive/traceFileValidator/good"
            + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
    private String traceFile = SAMPLE_DIR
            + "qclive/traceFileValidator/good/file.tr";
    private TraceFileProcessor processor = new TraceFileProcessor();

    private Mockery context = new JUnit4Mockery();
    private FileInfoQueries mockFileInfoQueries = context
            .mock(FileInfoQueries.class);
    private BCRDataService bcrDataService = context.mock(BCRDataService.class);
    private BCRIDQueries mockBCRIDQueries = context.mock(BCRIDQueries.class);
    private TraceRelationshipQueries mockTraceRelationshipQueries = context
            .mock(TraceRelationshipQueries.class);
    private Archive archive;
    private QcContext qcContext;
    private TraceRelationship tr1, tr2;
    private Date now, fileDate;

    @Before
    public void setup() {
        processor.setBcrDataService(bcrDataService);
        processor.setFileInfoQueries(mockFileInfoQueries);
        processor.setBcrIdQueries(mockBCRIDQueries);
        processor
                .setCommonTraceRelationshipQueries(mockTraceRelationshipQueries);
        archive = new Archive();
        archive.setExperimentType(Experiment.TYPE_GSC);
        archive.setArchiveType(Archive.TYPE_LEVEL_1);
        archive.setId(0L);
        archive.setArchiveFile(new File(goodFile));
        archive.setDeployLocation(goodFile);
        qcContext = new QcContext();
        qcContext.setArchive(archive);

        now = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        fileDate = new Date(new File(traceFile).lastModified());

        tr1 = new TraceRelationship();
        tr1.setBiospecimenID(123);
        tr1.setDccReceived(new java.sql.Date(fileDate.getTime()));
        tr1.setFileID(1);
        tr1.setTraceID(Long.MAX_VALUE);
        tr2 = new TraceRelationship();
        tr2.setBiospecimenID(456);
        tr2.setDccReceived(new java.sql.Date(fileDate.getTime()));
        tr2.setFileID(1);
        tr2.setTraceID(2);

    }

    private void setExpectations() {
        context.checking(new Expectations() {
            {
                one(mockFileInfoQueries).getFileId("file.tr", 0L);
                will(returnValue(1L));
                one(mockBCRIDQueries).exists("TCGA-00-0000-00A-00B-0000-00");
                will(returnValue(123));
                one(mockBCRIDQueries).exists("TCGA-01-0000-00A-00B-0000-00");
                will(returnValue(456));
                one(mockBCRIDQueries).exists("TCGA-02-0000-00A-00B-0000-00");
                will(returnValue(789));
                // returning null means the relationship is not in the db yet
                one(mockTraceRelationshipQueries).getDccDate(123,
                        Long.MAX_VALUE);
                will(returnValue(null));

                // this one is already in the db, should just do updates as
                // needed
                one(mockTraceRelationshipQueries).getDccDate(456, 2);
                will(returnValue(now));

                // date is later than file date, so set date to file date
                one(mockTraceRelationshipQueries).updateDccDate(tr2);
                one(mockTraceRelationshipQueries).getFileId(456, 2);
                will(returnValue(-1L));

                // file id in db is less than given file id, so update it
                one(mockTraceRelationshipQueries).updateFileID(tr2);

                // this one returns the same date as file date, so only will
                // update the file info id part
                one(mockTraceRelationshipQueries).getDccDate(789, 3);
                will(returnValue(fileDate));
                one(mockTraceRelationshipQueries).getFileId(789, 3);
                will(returnValue(1L)); // same id, so don't update it
            }
        });
    }

    @Test
    public void test() throws Processor.ProcessorException {
        setExpectations();
        // in this test, the add succeeds
        context.checking(new Expectations() {
            {
                one(mockTraceRelationshipQueries).addTraceRelationship(
                        with(equal(tr1)));
                will(returnValue(1));
                one(bcrDataService).addBioSpecimenToFileAssociations(with(any(List.class)), with(any(Tumor.class)));
                one(bcrDataService).addShippedBiospecimensFileRelationship(with(any(List.class)), with(any(Long.class)));
            }
        });

        processor.execute(archive, qcContext);
        assertEquals(qcContext.getErrors().toString(), 0,
                qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
    }

    @Test
    public void testInsertFails() throws Processor.ProcessorException {
        setExpectations();
        // in this test, the add does not succeed!
        context.checking(new Expectations() {
            {
                one(mockTraceRelationshipQueries).addTraceRelationship(
                        with(equal(tr1)));
                will(returnValue(-1));
                one(bcrDataService).addBioSpecimenToFileAssociations(with(any(List.class)), with(any(Tumor.class)));
                one(bcrDataService).addShippedBiospecimensFileRelationship(with(any(List.class)), with(any(Long.class)));
            }
        });

        processor.execute(archive, qcContext);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
        assertEquals(Archive.STATUS_IN_REVIEW, archive.getDeployStatus());
    }

    @Test
    public void testDatabaseException() throws Processor.ProcessorException {
        // in this test, the add succeeds
        context.checking(new Expectations() {
            {
                one(mockFileInfoQueries).getFileId("file.tr", 0L);
                // noinspection ThrowableInstanceNeverThrown
                will(throwException(new DataIntegrityViolationException(
                        "something bad has happened")));
            }
        });

        processor.execute(archive, qcContext);
        assertEquals(Archive.STATUS_IN_REVIEW, archive.getDeployStatus());
    }
}
