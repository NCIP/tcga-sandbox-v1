/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for ExperimentDeployer
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ExperimentDeployerFastTest {

    private Mockery context = new JUnit4Mockery();
    @SuppressWarnings("unchecked")
    private Processor<Archive, Archive> mockArchiveDeployer = (Processor<Archive, Archive>) context.mock(Processor.class);
    final Processor<Archive, Archive> mockArchiveReadmeCreator = (Processor<Archive, Archive>) context.mock( Processor.class, "archiveReadmeCreator" );

    @Test
    public void test() throws Processor.ProcessorException {
        ExperimentDeployer deployer = new ExperimentDeployer();
        deployer.addListProcessor(mockArchiveDeployer);
        Experiment e = new Experiment();
        final Archive a = new Archive();
        a.setDeployStatus(Archive.STATUS_VALIDATED);
        a.setArchiveType(Archive.TYPE_LEVEL_1);
        e.addArchive(a);
        final QcContext qcContext = new QcContext();
        qcContext.setArchive(a);
        qcContext.setExperiment(e);
        context.checking(new Expectations() {{
            one(mockArchiveDeployer).execute(a, qcContext);
            will(returnArchive(a, Archive.STATUS_DEPLOYED));
        }});
        deployer.execute(e, qcContext);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(Experiment.STATUS_DEPLOYED, e.getStatus());
        assertEquals(Archive.STATUS_DEPLOYED, a.getDeployStatus());
    }

    @Test
    public void testFail() throws Processor.ProcessorException {
        ExperimentDeployer deployer = new ExperimentDeployer();
        deployer.addListProcessor(mockArchiveDeployer);
        Experiment e = new Experiment();
        final Archive a = new Archive();
        a.setDeployStatus(Archive.STATUS_VALIDATED);
        e.addArchive(a);
        final Archive a2 = new Archive();
        a2.setDeployStatus(Archive.STATUS_VALIDATED);
        e.addArchive(a2);
        final QcContext qcContext = new QcContext();
        qcContext.setArchive(a);
        qcContext.setExperiment(e);
        context.checking(new Expectations() {{
            one(mockArchiveDeployer).execute(a, qcContext);
            will(returnArchive(a, Archive.STATUS_IN_REVIEW));
            one(mockArchiveDeployer).execute(a2, qcContext);
            will(returnArchive(a2, Archive.STATUS_AVAILABLE));
        }});
        deployer.execute(e, qcContext);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals(Experiment.STATUS_FAILED, e.getStatus());
        // both archives should have been set to In Review by the Experiment deployer, since one failed
        assertEquals(Archive.STATUS_IN_REVIEW, a.getDeployStatus());
        assertEquals(Archive.STATUS_IN_REVIEW, a2.getDeployStatus());
    }

    @Test
    public void addError() throws Processor.ProcessorException {

        ExperimentDeployer deployer = new ExperimentDeployer();
        deployer.addListProcessor(mockArchiveDeployer);
        deployer.addListProcessor(mockArchiveReadmeCreator);
        Experiment e = new Experiment();
        final Archive a = new Archive();
        a.setDeployStatus(Archive.STATUS_VALIDATED);
        a.setArchiveType(Archive.TYPE_LEVEL_1);
        e.addArchive(a);
        final QcContext qcContext = new QcContext();
        qcContext.setArchive(a);
        qcContext.setExperiment(e);
        context.checking(new Expectations() {{

            one(mockArchiveDeployer).execute(a, qcContext);
            will(addError(a,qcContext));
            one(mockArchiveReadmeCreator).execute(a, qcContext);
            will(returnValue(a));
        }});

        deployer.execute(e, qcContext);
        assertEquals(2, qcContext.getErrorCount());
        assertEquals(Experiment.STATUS_FAILED, e.getStatus());
        assertEquals(Archive.STATUS_IN_REVIEW, a.getDeployStatus());

    }

    public  <T> Action addError(Archive archive, QcContext qcContext) {
        return new AddErrorAction(archive, qcContext);
    }

    public class AddErrorAction<T> implements Action {
        private QcContext qcContext;
        private Archive archive;

        public AddErrorAction(Archive archive,QcContext qcContext) {
            this.archive = archive;
            this.qcContext = qcContext;
        }

        public void describeTo(Description description) {
            description.appendText("adds error to qccontext ");
        }

        public Object invoke(Invocation invocation) throws Throwable {
            ((QcContext)invocation.getParameter(1)).addError("Some error");
            return archive;
        }
    }

    public static Action returnArchive(final Archive archive, final String status) {
        return new Action() {
            public void describeTo(final Description description) {
                description.appendText("return archive with status given");
            }

            public Object invoke(final Invocation invocation) throws Throwable {
                archive.setDeployStatus(status);
                return archive;
            }
        };
    }
}
