package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.remote;

import gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck.RemoteValidationHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Fast test for RemoteCodeTableQueries.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class RemoteCodeTableQueriesFastTest {
    private final Mockery context = new JUnit4Mockery();
    private RemoteValidationHelper mockRemoteValidationHelper;

    private RemoteCodeTableQueries remoteCodeTableQueries;

    @Before
    public void setUp() {
        mockRemoteValidationHelper = context.mock(RemoteValidationHelper.class);
        remoteCodeTableQueries = new RemoteCodeTableQueries(mockRemoteValidationHelper);
    }

    @Test
    public void testProjectNameExists() throws ApplicationException {
        context.checking(new Expectations() {{
            one(mockRemoteValidationHelper).projectExists("TCGA");
            will(returnValue(true));
        }});
        assertTrue(remoteCodeTableQueries.projectNameExists("TCGA"));
    }

    @Test
    public void testTssCodeExists() throws ApplicationException {
        context.checking(new Expectations() {{
            one(mockRemoteValidationHelper).tssCodeExists("99");
            will(returnValue(true));
        }});
        assertTrue(remoteCodeTableQueries.tssCodeExists("99"));
    }

    @Test
    public void testSampleTypeExists() throws ApplicationException {
        context.checking(new Expectations() {{
            one(mockRemoteValidationHelper).sampleTypeExists("01");
            will(returnValue(true));
        }});
        assertTrue(remoteCodeTableQueries.sampleTypeExists("01"));
    }

    @Test
    public void testPortionAnalyteExists() throws ApplicationException {
        context.checking(new Expectations() {{
            one(mockRemoteValidationHelper).portionAnalyteExists("W");
            will(returnValue(true));
        }});
        assertTrue(remoteCodeTableQueries.portionAnalyteExists("W"));
    }

    @Test
    public void testBcrCenterIdExists() throws ApplicationException {
        context.checking(new Expectations() {{
            one(mockRemoteValidationHelper).bcrCenterIdExists("20");
            will(returnValue(true));
        }});
        assertTrue(remoteCodeTableQueries.bcrCenterIdExists("20"));
    }
}
