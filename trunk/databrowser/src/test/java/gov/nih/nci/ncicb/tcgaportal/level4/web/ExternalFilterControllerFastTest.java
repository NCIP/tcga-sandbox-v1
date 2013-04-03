package gov.nih.nci.ncicb.tcgaportal.level4.web;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4Queries;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesGetter;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.mock.Level4QueriesMock;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.web.request.ExternalRequest;
import gov.nih.nci.ncicb.tcgaportal.util.ProcessLogger;
import static junit.framework.Assert.assertEquals;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;

/**
 * Description :
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ExternalFilterControllerFastTest {

    private ExternalFilterController externalFilterController;

    @Before
    public void setUp() {
        externalFilterController = new ExternalFilterController();
        Level4QueriesMock daoMock = new Level4QueriesMock();
        Level4QueriesGetter level4QueriesGetter = new Level4QueriesGetter() {
            public Level4Queries getLevel4Queries(final String disease) {
                return null;
            }

            public Collection<String> getDiseaseNames() {
                return Arrays.asList("GBM", "OV");
            }
        };
        externalFilterController.setLevel4QueriesGetter(level4QueriesGetter);
        externalFilterController.setSuccessView("externalRequest");
        externalFilterController.setFailView("exception");

        ProcessLogger logger = new ProcessLogger();
        externalFilterController.setLogger(logger);         
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEmptyMode() throws QueriesException {
        HttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse resp = new MockHttpServletResponse();

        ExternalRequest command = new ExternalRequest();
        command.setDisease("GBM");

        externalFilterController.handle(req, resp, command, null);        
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEmptyDisease() throws QueriesException {
        HttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse resp = new MockHttpServletResponse();

        ExternalRequest command = new ExternalRequest();
        command.setMode("gene");
        externalFilterController.handle(req, resp, command, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testMode() throws QueriesException {
        HttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse resp = new MockHttpServletResponse();

        ExternalRequest command = new ExternalRequest();
        command.setMode("nomode"); // the correct value is 'gene' or patient etc
        command.setDisease("GBM");
        externalFilterController.handle(req, resp, command, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testDisease() throws IllegalArgumentException, QueriesException {
        HttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse resp = new MockHttpServletResponse();

        ExternalRequest command = new ExternalRequest();
        command.setMode("gene");
        command.setDisease("noDisease"); // the correct value could be GBM or OV etc
        externalFilterController.handle(req, resp, command, null);
    }

    @Test
    public void testGeneMode() throws QueriesException {
        HttpServletRequest req = new MockHttpServletRequest();
        HttpServletResponse resp = new MockHttpServletResponse();

        ExternalRequest command = new ExternalRequest();
        command.setMode(FilterSpecifier.ListBy.Genes.getStringValue());
        command.setDisease("GBM");

        ModelAndView ret = externalFilterController.handle(req, resp, command, null);
        assertEquals(ret.getViewName(), "externalRequest");
    }
    
        
}
