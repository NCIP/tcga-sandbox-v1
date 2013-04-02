package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck.RemoteValidationHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for RemotePlatformValidator
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class RemotePlatformValidatorFastTest {
    private Mockery context = new JUnit4Mockery();
    private RemoteValidationHelper mockRemoteValidationHelper = context.mock( RemoteValidationHelper.class );

    private RemotePlatformValidator validator;
    private Archive archive = new Archive();
    private QcContext qcContext = new QcContext();

    @Before
    public void setup() {

        validator = new RemotePlatformValidator(mockRemoteValidationHelper);
        archive.setPlatform("PLATFORM");
    }

    @Test
    public void testExecute() throws ApplicationException, Processor.ProcessorException {
        context.checking( new Expectations() {{
            one(mockRemoteValidationHelper).platformExists("PLATFORM");
            will(returnValue(true));
        }});

        assertTrue(validator.execute(archive, qcContext));
    }

    @Test
    public void testNotValid() throws Processor.ProcessorException, ApplicationException {
        context.checking( new Expectations() {{
            one(mockRemoteValidationHelper).platformExists("PLATFORM");
            will(returnValue(false));
        }});

        assertFalse(validator.execute(archive, qcContext));
    }

    @Test
    public void testExceptionThrown() throws ApplicationException {
        context.checking( new Expectations() {{
            one(mockRemoteValidationHelper).platformExists("PLATFORM");
            //noinspection ThrowableInstanceNeverThrown
            will(throwException(new ApplicationException("test")));
        }});

        try {
            validator.execute(archive, qcContext);
            fail("Exception should have been thrown");
        } catch (Processor.ProcessorException pe) {
            assertTrue(pe.getCause() instanceof ApplicationException);
            assertEquals("test", pe.getMessage());
        }
    }
}
