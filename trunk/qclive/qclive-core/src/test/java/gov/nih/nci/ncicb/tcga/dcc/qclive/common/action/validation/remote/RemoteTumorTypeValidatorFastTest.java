package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.remote;

import static junit.framework.Assert.assertEquals;
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
 * Test for RemoteTumorTypeValidator
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class RemoteTumorTypeValidatorFastTest {
    private Mockery context = new JUnit4Mockery();
    private RemoteValidationHelper mockRemoteValidationHelper = context.mock( RemoteValidationHelper.class );

    private RemoteTumorTypeValidator validator;
    private Archive archive = new Archive();
    private QcContext qcContext = new QcContext();

    @Before
    public void setup() {
        validator  = new RemoteTumorTypeValidator(mockRemoteValidationHelper);
        archive.setTumorType("TUMOR");
    }

    @Test
    public void testExecute() throws ApplicationException, Processor.ProcessorException {
        context.checking( new Expectations() {{
            one(mockRemoteValidationHelper).diseaseExists("TUMOR");
            will(returnValue(true));
        }});

        assertTrue(validator.execute(archive, qcContext));
    }

    @Test
    public void testExecuteNotValid() throws ApplicationException, Processor.ProcessorException {
        context.checking( new Expectations() {{
          oneOf(mockRemoteValidationHelper).diseaseExists("TUMOR");
            will(returnValue(false));
        }});
        
        assertFalse(validator.execute(archive, qcContext));
    }

    @Test
    public void testExecuteException() throws ApplicationException {
        context.checking( new Expectations() {{
            oneOf(mockRemoteValidationHelper).diseaseExists("TUMOR");
            //noinspection ThrowableInstanceNeverThrown
            will(throwException(new ApplicationException("test")));
        }});

        try {
            validator.execute(archive, qcContext);
            fail("Exception should have been thrown");
        } catch (Processor.ProcessorException e) {
            assertTrue(e.getCause() instanceof ApplicationException);
            assertEquals("test", e.getMessage());
        }
    }
}
