package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.web.ArchiveQueryRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * ArchiveFormController unit test
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ArchiveFormControllerFastTest {

    private Mockery mockery = new Mockery();
    private HttpServletRequest mockHttpServletRequest = mockery.mock(HttpServletRequest.class);
    private HttpServletResponse mockHttpServletResponse = mockery.mock(HttpServletResponse.class);
    private HttpSession mockHttpSession = mockery.mock(HttpSession.class);
    private WebApplicationContext mockWebApplicationContext = mockery.mock(WebApplicationContext.class);
    private ServletContext mockServletContext = mockery.mock(ServletContext.class);
    private TumorQueries mockTumorQueries = mockery.mock(TumorQueries.class);
    private CenterQueries mockCenterQueries = mockery.mock(CenterQueries.class);
    private PlatformQueries mockPlatformQueries = mockery.mock(PlatformQueries.class);
    private DataTypeQueries mockDataTypeQueries = mockery.mock(DataTypeQueries.class);
    private ArchiveQueries mockArchiveQueries = mockery.mock(ArchiveQueries.class);

    private ArchiveFormController archiveFormController;
    private String viewName = "viewName";
    private String successView = "successView";


    @Before
    public void setUp() {

        archiveFormController = new ArchiveFormController();
        archiveFormController.setTumorQueries(mockTumorQueries);
        archiveFormController.setCenterQueries(mockCenterQueries);
        archiveFormController.setPlatformQueries(mockPlatformQueries);
        archiveFormController.setDataTypeQueries(mockDataTypeQueries);
        archiveFormController.setArchiveQueries(mockArchiveQueries);
        archiveFormController.setSelectView(viewName);
        archiveFormController.setSuccessView(successView);
    }

    @Test
    public void testConstructor() {

        assertEquals("archive", archiveFormController.getCommandName());
        assertEquals(ArchiveQueryRequest.class, archiveFormController.getCommandClass());
    }

    @Test
    public void testFormBackingObject() {

        final Object formBackingObject = archiveFormController.formBackingObject(mockHttpServletRequest);

        assertNotNull(formBackingObject);
        assertTrue(formBackingObject instanceof ArchiveQueryRequest);
    }

    @Test
    public void testInitApplicationContext() {
        checkInitApplicationContext();
    }

    @Test
    public void testInitApplicationContextWhenSelectViewNotSet() {

        archiveFormController.setSelectView(null);

        try {
            checkInitApplicationContext();
            fail("IllegalArgumentException was not thrown.");

        } catch(final IllegalArgumentException e) {
            assertEquals("selectView isn't set", e.getMessage());
        }
    }

    @Test
    public void testInitLists() {

        assertNull(archiveFormController.getTumors());
        assertNull(archiveFormController.getCenters());
        assertNull(archiveFormController.getPlatforms());
        assertNull(archiveFormController.getDataTypes());
        assertNull(archiveFormController.getArchiveTypes());

        final Collection<Map<String, Object>> tumors = makeCollection("tumor name", "tumor value");
        final Collection<Map<String, Object>> centers = makeCollection("center_id", "center value");
        final Collection<Map<String, Object>> platforms = makeCollection("platform_id", "platform value");
        final Collection<Map<String, Object>> dataTypes = makeCollection("data_type_id", "dataType value");
        final Collection<Map<String, Object>> archiveTypes = makeCollection("archiveType name", "archiveType value");

        mockery.checking(new Expectations() {{

            one(mockTumorQueries).getAllTumors();
            will(returnValue(tumors));

            one(mockCenterQueries).getAllCenters();
            will(returnValue(centers));

            one(mockPlatformQueries).getAllPlatforms();
            will(returnValue(platforms));

            one(mockDataTypeQueries).getAllDataTypes();
            will(returnValue(dataTypes));

            one(mockArchiveQueries).getAllArchiveTypes();
            will(returnValue(archiveTypes));
        }});

        archiveFormController.initLists();

        assertNotNull(archiveFormController.getTumors());
        assertNotNull(archiveFormController.getCenters());
        assertNotNull(archiveFormController.getPlatforms());
        assertNotNull(archiveFormController.getDataTypes());
        assertNotNull(archiveFormController.getArchiveTypes());
    }

    @Test
    public void testOnSubmit() {

        final ArchiveQueryRequest command = new ArchiveQueryRequest();

        final String[] platformValues = {"platform 1"};
        final String[] centerValues = {"center 1"};
        final String[] dataTypeValues = {"dataType 1"};
        final String[] tumorValues = {"tumor 1"};
        final String[] archiveTypeValues = {"archive type 1"};
        final String dateStart = "now";
        final String dateEnd = "tomorrow";
        final String fileName = "test.txt";
        final List<Archive> matchingArchives = makeMatchingArchives();

        mockery.checking(new Expectations() {{
            allowing(mockHttpServletRequest).getSession();
            will(returnValue(mockHttpSession));

            one(mockHttpServletRequest).getParameterValues("platform");
            will(returnValue(platformValues));
            one(mockHttpSession).setAttribute("platVals", platformValues);

            one(mockHttpServletRequest).getParameterValues("center");
            will(returnValue(centerValues));
            one(mockHttpSession).setAttribute("centerVals", centerValues);

            one(mockHttpServletRequest).getParameterValues("dataType");
            will(returnValue(dataTypeValues));
            one(mockHttpSession).setAttribute("dataTypeVals", dataTypeValues);

            one(mockHttpServletRequest).getParameterValues("project");
            will(returnValue(tumorValues));
            one(mockHttpSession).setAttribute("tumorVals", tumorValues);

            one(mockHttpServletRequest).getParameterValues("archiveType");
            will(returnValue(archiveTypeValues));
            one(mockHttpSession).setAttribute("archiveTypeVals", archiveTypeValues);

            one(mockHttpServletRequest).getParameter("dateStart");
            will(returnValue(dateStart));
            one(mockHttpSession).setAttribute("dateStart", dateStart);

            one(mockHttpServletRequest).getParameter("dateEnd");
            will(returnValue(dateEnd));
            one(mockHttpSession).setAttribute("dateEnd", dateEnd);

            one(mockHttpServletRequest).getParameter("fileName");
            will(returnValue(fileName));
            one(mockHttpSession).setAttribute("fileName", fileName);

            one(mockArchiveQueries).getMatchingArchives(command);
            will(returnValue(matchingArchives));
        }});

        final ModelAndView modelAndView = archiveFormController.onSubmit(mockHttpServletRequest, mockHttpServletResponse, command, null);

        final Map<String, Object> expectedModel = new HashMap<String, Object>();
        expectedModel.put("archiveList", matchingArchives);

        assertNotNull(modelAndView);
        assertEquals(successView, modelAndView.getViewName());
        assertEquals(expectedModel, modelAndView.getModel());
    }

    /**
     * Return a {@link List} of 1 {@link Archive}
     * 
     * @return a {@link List} of 1 {@link Archive}
     */
    private List<Archive> makeMatchingArchives() {

        final List<Archive> result = new ArrayList<Archive>();
        result.add(new Archive());

        return result;
    }

    /**
     * Set archiveFormController's application context and check expectations
     */
    private void checkInitApplicationContext() {

        final Collection<Map<String, Object>> tumors = makeCollection("tumor name", "tumor value");
        final Collection<Map<String, Object>> centers = makeCollection("center name", "center value");
        final Collection<Map<String, Object>> platforms = makeCollection("platform name", "platform value");
        final Collection<Map<String, Object>> dataTypes = makeCollection("dataType name", "dataType value");
        final Collection<Map<String, Object>> archiveTypes = makeCollection("archiveType name", "archiveType value");

        mockery.checking(new Expectations() {{

            allowing(mockWebApplicationContext).getServletContext();
            will(returnValue(mockServletContext));

            one(mockTumorQueries).getAllTumors();
            will(returnValue(tumors));
            one(mockServletContext).getAttribute("tumors");
            will(returnValue(null));
            one(mockServletContext).setAttribute("tumors", tumors);

            one(mockCenterQueries).getAllCenters();
            will(returnValue(centers));
            one(mockServletContext).getAttribute("centers");
            will(returnValue(null));
            one(mockServletContext).setAttribute("centers", centers);

            one(mockPlatformQueries).getAllPlatforms();
            will(returnValue(platforms));
            one(mockServletContext).getAttribute("platforms");
            will(returnValue(null));
            one(mockServletContext).setAttribute("platforms", platforms);

            one(mockDataTypeQueries).getAllDataTypes();
            will(returnValue(dataTypes));
            one(mockServletContext).getAttribute("datatypes");
            will(returnValue(null));
            one(mockServletContext).setAttribute("datatypes", dataTypes);

            one(mockArchiveQueries).getAllArchiveTypes();
            will(returnValue(archiveTypes));
            one(mockServletContext).getAttribute("archiveTypes");
            will(returnValue(null));
            one(mockServletContext).setAttribute("archiveTypes", archiveTypes);
        }});

        archiveFormController.setApplicationContext(mockWebApplicationContext); // Calls initApplicationContext()
    }

    /**
     * Return a {@link Collection} for 1 record
     *
     * @param columnName the column name for the record
     * @param columnValue the column value for the record
     * @return a {@link Collection} for 1 record
     */
    private Collection<Map<String, Object>> makeCollection(final String columnName,
                                                           final String columnValue) {

        final Collection<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        final Map<String, Object> record1 = new HashMap<String, Object>();
        record1.put(columnName, columnValue);

        result.add(record1);

        return result;
    }
}
