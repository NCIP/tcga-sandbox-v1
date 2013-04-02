package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck.RemoteValidationHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Fast test for RemoteCenterQueries
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class RemoteCenterQueriesFastTest {
    private final Mockery context = new JUnit4Mockery();
    private RemoteValidationHelper mockRemoteValidationHelper;
    private RemoteCenterQueries remoteCenterQueries;

    @Before
    public void setUp() throws Exception {
        mockRemoteValidationHelper = context.mock(RemoteValidationHelper.class);
        remoteCenterQueries = new RemoteCenterQueries();
        remoteCenterQueries.setRemoteValidationHelper(mockRemoteValidationHelper);

        context.checking(new Expectations() {{
            one(mockRemoteValidationHelper).getCenterId("a", "gsc");
            will(returnValue(12));
        }});
    }

    @Test
    public void testFindCenterId() throws ApplicationException {
        assertEquals(new Integer(12), remoteCenterQueries.findCenterId("a", "gsc"));
    }

    @Test
    public void testFindCenterIdMultipleTimes() {
        // call multiple times but the expectations set in setUp still hold - only
        // call remote val helper once, the other times it gets the value from the cache
        assertEquals(new Integer(12), remoteCenterQueries.findCenterId("a", "gsc"));
        assertEquals(new Integer(12), remoteCenterQueries.findCenterId("a", "gsc"));
        assertEquals(new Integer(12), remoteCenterQueries.findCenterId("a", "gsc"));
    }
}
