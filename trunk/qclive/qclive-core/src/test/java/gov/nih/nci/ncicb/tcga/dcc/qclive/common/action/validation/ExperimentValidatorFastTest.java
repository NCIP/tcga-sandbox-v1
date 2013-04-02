/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;

import java.util.List;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;

import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for ExperimentValidator
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ExperimentValidatorFastTest {

    private ExperimentValidator experimentValidator = new ExperimentValidator();
    private Experiment experiment = new Experiment();
    private Archive availableArchive = new Archive();
    private Archive uploadedArchive = new Archive();
    private Archive uploadedArchive2 = new Archive();
    final private QcContext qcContext = new QcContext();
    private Mockery context = new JUnit4Mockery();
    @SuppressWarnings("unchecked")
    final Processor<Archive, Boolean> mockManifestValidator =
            (Processor<Archive, Boolean>) context.mock( Processor.class, "manifestValidator" );
    @SuppressWarnings("unchecked")
    final Processor<Archive, Boolean> mockDataMatrixArchiveValidator =
            (Processor<Archive, Boolean>) context.mock( Processor.class, "dataMatrixValidator" );
    @SuppressWarnings("unchecked")
    final Processor<Archive, Boolean> mockTraceFileValidator =
            (Processor<Archive, Boolean>) context.mock( Processor.class, "traceFileValidator" );
    @SuppressWarnings("unchecked")
    final Processor<Archive, Boolean> mockMafFileValidator =
            (Processor<Archive, Boolean>) context.mock( Processor.class, "mafFileValidator" );

    @Before
    public void setup() {
        availableArchive.setDeployStatus( Archive.STATUS_AVAILABLE );
        availableArchive.setRealName( "availableArchive" );
        uploadedArchive.setDeployStatus( Archive.STATUS_UPLOADED );
        uploadedArchive.setRealName( "uploadedArchive" );
        uploadedArchive2.setDeployStatus( Archive.STATUS_UPLOADED );
        uploadedArchive2.setRealName( "uploadedArchive2" );
        experiment.addArchive( availableArchive );
        experiment.addArchive( uploadedArchive );
        experiment.addArchive( uploadedArchive2 );
        experimentValidator.addListProcessor( mockManifestValidator );
        experimentValidator.addListProcessor( mockDataMatrixArchiveValidator );
        experimentValidator.addListProcessor( mockTraceFileValidator );
        experimentValidator.addListProcessor( mockMafFileValidator );
    }

    // test that the list of archives
    // fetched to run on the list-action is correct
    @Test
    public void testGetWorkList() throws Processor.ProcessorException {
        List<Archive> archives = experimentValidator.getWorkList( experiment, new QcContext() );
        // uploaded archive should be in there
        assertTrue( archives.contains( uploadedArchive ) );
        // already available archive should not ...
        assertFalse( archives.contains( availableArchive ) );
    }

    @Test
    public void testCgcc() throws Processor.ProcessorException {
        testExecute( Experiment.TYPE_CGCC );
    }

    @Test
    public void testGsc() throws Processor.ProcessorException {
        testExecute( Experiment.TYPE_GSC );
    }

    @Test
    public void testBcr() throws Processor.ProcessorException {
        testExecute( Experiment.TYPE_BCR );
    }

    public void testExecute( String type ) throws Processor.ProcessorException {
        uploadedArchive.setExperimentType( type );
        // expect all action to be called for both uploaded archives, no matter what the type
        context.checking( new Expectations() {{
            one( mockManifestValidator ).execute( uploadedArchive, qcContext );
            will( returnValue( true ) );
            one( mockDataMatrixArchiveValidator ).execute( uploadedArchive, qcContext );
            will( returnValue( true ) );
            one( mockTraceFileValidator ).execute( uploadedArchive, qcContext );
            will( returnValue( true ) );
            one( mockMafFileValidator ).execute( uploadedArchive, qcContext );
            will( returnValue( true ) );
            one( mockManifestValidator ).execute( uploadedArchive2, qcContext );
            will( returnValue( true ) );
            one( mockDataMatrixArchiveValidator ).execute( uploadedArchive2, qcContext );
            will( returnValue( true ) );
            one( mockTraceFileValidator ).execute( uploadedArchive2, qcContext );
            will( returnValue( true ) );
            one( mockMafFileValidator ).execute( uploadedArchive2, qcContext );
            will( returnValue( true ) );
        }} );
        boolean isValid = experimentValidator.execute( experiment, qcContext );
        assertTrue( "validation of experiment did not pass", isValid );
        assertEquals( Archive.STATUS_VALIDATED, uploadedArchive.getDeployStatus() );
        assertEquals( Archive.STATUS_VALIDATED, uploadedArchive2.getDeployStatus() );
    }

    @Test
    public void testFailed() throws Processor.ProcessorException {
        context.checking( new Expectations() {{
            one( mockManifestValidator ).execute( uploadedArchive, qcContext );
            will( returnValue( false ) );
            one( mockDataMatrixArchiveValidator ).execute( uploadedArchive, qcContext );
            will( returnValue( true ) );
            one( mockTraceFileValidator ).execute( uploadedArchive, qcContext );
            will( returnValue( true ) );
            one( mockMafFileValidator ).execute( uploadedArchive, qcContext );
            will( returnValue( true ) );
            one( mockManifestValidator ).execute( uploadedArchive2, qcContext );
            will( returnValue( true ) );
            one( mockDataMatrixArchiveValidator ).execute( uploadedArchive2, qcContext );
            will( returnValue( true ) );
            one( mockTraceFileValidator ).execute( uploadedArchive2, qcContext );
            will( returnValue( true ) );
            one( mockMafFileValidator ).execute( uploadedArchive2, qcContext );
            will( returnValue( true ) );
        }} );
        assertFalse( experimentValidator.execute( experiment, qcContext ) );
        assertEquals( Experiment.STATUS_FAILED, experiment.getStatus() );
    }

    @Test
    public void errorInValidator() throws Processor.ProcessorException {

        context.checking( new Expectations() {{
            one( mockManifestValidator ).execute( uploadedArchive, qcContext );
            will( returnValue( true ) );
            one( mockDataMatrixArchiveValidator ).execute( uploadedArchive, qcContext );
            will( addError( qcContext ) );
            one( mockTraceFileValidator ).execute( uploadedArchive, qcContext );
            will( returnValue( true ) );
            one( mockMafFileValidator ).execute( uploadedArchive, qcContext );
            will( returnValue( true ) );
            one( mockManifestValidator ).execute( uploadedArchive2, qcContext );
            will( returnValue( true ) );
            one( mockDataMatrixArchiveValidator ).execute( uploadedArchive2, qcContext );
            will( returnValue( true ) );
            one( mockTraceFileValidator ).execute( uploadedArchive2, qcContext );
            will( returnValue( true ) );
            one( mockMafFileValidator ).execute( uploadedArchive2, qcContext );
            will( returnValue( true ) );
        }} );
        assertFalse( experimentValidator.execute( experiment, qcContext ) );
        assertEquals( Experiment.STATUS_FAILED, experiment.getStatus() );
    }


    public  <T> Action addError(QcContext qcContext) {
        return new AddErrorAction(qcContext);
    }

    public class AddErrorAction<T> implements Action {
        private QcContext qcContext;

        public AddErrorAction(QcContext qcContext) {
            this.qcContext = qcContext;
        }

        public void describeTo(Description description) {
            description.appendText("adds error to qccontext ");
        }

        public Object invoke(Invocation invocation) throws Throwable {
            ((QcContext)invocation.getParameter(1)).addError("Some error");
            return new Boolean(true);
        }
    }
}
