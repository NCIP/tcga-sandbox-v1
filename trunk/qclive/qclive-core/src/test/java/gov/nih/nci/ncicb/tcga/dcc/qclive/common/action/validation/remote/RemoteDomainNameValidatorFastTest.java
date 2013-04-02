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
 * Test class for RemoteDomainNameValidator
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class RemoteDomainNameValidatorFastTest {
    private Mockery context = new JUnit4Mockery();
    private RemoteValidationHelper mockRemoteValidationHelper = context.mock( RemoteValidationHelper.class );

    private RemoteDomainNameValidator validator;
    private Archive archive = new Archive();
    private QcContext qcContext = new QcContext();

    @Before
    public void setup() {
        validator =  new RemoteDomainNameValidator(mockRemoteValidationHelper);
        archive.setDomainName("NAME");
    }

    @Test
    public void testExecute() throws ApplicationException, Processor.ProcessorException {
        context.checking( new Expectations() {{
            one(mockRemoteValidationHelper).centerExists("NAME");
            will(returnValue(true));
        }});

        boolean isValid = validator.execute(archive, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testNotValid() throws ApplicationException, Processor.ProcessorException {
        context.checking( new Expectations() {{
            one(mockRemoteValidationHelper).centerExists("NAME");
            will(returnValue(false));
        }});

        boolean isValid = validator.execute(archive, qcContext);
        assertFalse("Validation should have failed", isValid);
    }

    @Test
    public void testRemoteExceptionThrown() throws ApplicationException {

        context.checking( new Expectations() {{
            one(mockRemoteValidationHelper).centerExists("NAME");
            //noinspection ThrowableInstanceNeverThrown
            will(throwException(new ApplicationException("test")));
        }});

        try {
            validator.execute(archive, qcContext);
            fail("Exception should have been thrown");
            
        } catch (Processor.ProcessorException pe) {
            // expect ProcessorException to be thrown because of ApplicationException
            assertEquals("test", pe.getMessage());
            assertTrue(pe.getCause() instanceof ApplicationException);
        }
    }
}
