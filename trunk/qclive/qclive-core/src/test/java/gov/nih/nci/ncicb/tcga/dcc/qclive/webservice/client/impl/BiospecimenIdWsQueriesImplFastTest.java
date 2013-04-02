package gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.client.impl;

import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util.RestfulWebserviceClient;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util.WebserviceInput;
import gov.nih.nci.ncicb.tcga.dcc.qclive.webservice.util.WebserviceOutput;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for BiospecimenIdWsQueries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class BiospecimenIdWsQueriesImplFastTest {
	private final Mockery context = new JUnit4Mockery();
    private BiospecimenIdWsQueriesImpl biospecimenIdWsQueries;
    private RestfulWebserviceClient mockRestfulWebserviceClient;
    public static final String BASE_URI = "http://test-server/barcode";

    @Before
	public void setup() {
        biospecimenIdWsQueries = new BiospecimenIdWsQueriesImpl();
        biospecimenIdWsQueries.setBaseBiospecimenJsonWs(BASE_URI);
        mockRestfulWebserviceClient = context.mock(RestfulWebserviceClient.class);
        biospecimenIdWsQueries.setClient(mockRestfulWebserviceClient);
    }


    @Test
	public void exists() {
        final String barcode = "testbarcode";
        final WebserviceOutput webserviceOutput = new WebserviceOutput(200);
		context.checking(new Expectations() {
        {
            one(mockRestfulWebserviceClient).executeGet(with(validateInput(barcode)));
            will(returnValue(webserviceOutput));
        }});
        assertTrue(biospecimenIdWsQueries.exists(barcode));
    }

    @Test
    public void existsWithSpace() {
        final String barcode = "test bad barcode";
        final WebserviceOutput webserviceOutput = new WebserviceOutput(412);
		context.checking(new Expectations() {
        {
            one(mockRestfulWebserviceClient).executeGet(with(validateInput("test+bad+barcode")));
            will(returnValue(webserviceOutput));
        }});
        assertFalse(biospecimenIdWsQueries.exists(barcode));
    }



    @Test
	public void batchExists() throws WebServiceException{

        biospecimenIdWsQueries.setWebServiceBatchSize(1); // Set the batch size to 1

        final List<String> barcode = Arrays.asList("testbarcode", "testbarcode2");
        final WebserviceOutput webserviceOutput = new WebserviceOutput(200);

        context.checking(new Expectations() {
        {
            one(mockRestfulWebserviceClient).executeGet(with(validateInput("testbarcode")));
            will(returnValue(webserviceOutput));
            one(mockRestfulWebserviceClient).executeGet(with(validateInput("testbarcode2")));
            will(returnValue(webserviceOutput));
        }});

        biospecimenIdWsQueries.exists(barcode);
    }

    @Test
    public void batchExistsEncodingNeeded() throws WebServiceException {
        biospecimenIdWsQueries.setWebServiceBatchSize(2);

        final List<String> barcode = Arrays.asList("testbarcode", "testbarcode 2");
        final WebserviceOutput webserviceOutput = new WebserviceOutput(200);

        context.checking(new Expectations() {
        {
            one(mockRestfulWebserviceClient).executeGet(with(validateInput("testbarcode,testbarcode+2")));
            will(returnValue(webserviceOutput));
        }});

        biospecimenIdWsQueries.exists(barcode);
    }

    private static Matcher<WebserviceInput> validateInput(final String barcode) {
        return new TypeSafeMatcher<WebserviceInput>() {
            @Override
            public boolean matchesSafely(WebserviceInput we) {
               return (we.getUri().toString().equals(BASE_URI+"/" + barcode));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("expected input " + BASE_URI + "/" + barcode);
            }
        };
    }


}
