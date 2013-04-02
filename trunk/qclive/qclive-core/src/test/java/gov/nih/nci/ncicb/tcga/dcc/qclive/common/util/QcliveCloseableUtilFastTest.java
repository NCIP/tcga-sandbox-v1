package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import org.apache.log4j.Level;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.Closeable;
import java.io.IOException;

/**
 * QcliveCloseableUtil unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class QcliveCloseableUtilFastTest {

    private Mockery mockery = new Mockery();
    private Closeable mockCloseable;
    private Logger mockLogger;


    @Before
    public void setUp() {
        mockCloseable = mockery.mock(Closeable.class);
        mockLogger = mockery.mock(Logger.class);
    }

    @Test
    public void testClose() throws IOException {

        mockery.checking(new Expectations() {{
            one(mockCloseable).close();
        }});

        QcliveCloseableUtil.close(mockCloseable, mockLogger);
    }

    @Test
    public void testCloseThrowsIOExceptionWithLogger() throws IOException {

        final String errorMsg = "not good";

        mockery.checking(new Expectations() {{
            one(mockCloseable).close();
            will(throwException(new IOException(errorMsg)));
            one(mockLogger).log(with(Level.WARN), with(errorMsg));
        }});

        QcliveCloseableUtil.close(mockCloseable, mockLogger);
    }

    @Test
    public void testCloseThrowsIOExceptionWithoutLogger() throws IOException {

        final String errorMsg = "not good";

        mockery.checking(new Expectations() {{
            one(mockCloseable).close();
            will(throwException(new IOException(errorMsg)));
        }});

        QcliveCloseableUtil.close(mockCloseable, null);
    }

    @Test
    public void testCloseWhenResourceNull() throws IOException {

        QcliveCloseableUtil.close(null, null);
    }
}
