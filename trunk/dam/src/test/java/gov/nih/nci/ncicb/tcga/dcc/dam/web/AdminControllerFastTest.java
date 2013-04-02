package gov.nih.nci.ncicb.tcga.dcc.dam.web;


import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccProperty;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.AdminDataModel;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.AdminService;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertTrue;

/**
 * Test class for AdminController
 * @author ramanr
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class AdminControllerFastTest {
    private final Mockery context = new JUnit4Mockery();
    private AdminController adminController;
    private AdminService mockAdminService;
    private ServletContext mockServletContext;
    private HttpServletRequest mockServletRequest;


    @Before
    public void setup() {
        adminController = new AdminController();
        mockAdminService= context.mock(AdminService.class);
        mockServletContext = context.mock(ServletContext.class);
        mockServletRequest = context.mock(HttpServletRequest.class);
        adminController.setAdminService(mockAdminService);
        adminController.setServletContext(mockServletContext);
        adminController.setHttpServletRequest(mockServletRequest);

    }
    
    @Test
    public void handleDamAdminRequests()throws Exception{

        final List<DccProperty> properties = new ArrayList<DccProperty>();
        final DccProperty dccProperty = new DccProperty();
        dccProperty.setPropertyName("property");
        dccProperty.setPropertyValue("value");
        context.checking(new Expectations() {{
            one(mockAdminService).getAllPropertiesForAnApplication("dam");
            will(returnValue(properties));
            one(mockServletContext).setAttribute("currentAdminUrl", "/tcga/admin/data-access-matrix.htm");
            one(mockServletContext).setAttribute("currentPage", "admin");
            allowing(mockServletRequest).getRequestURI();
            will(returnValue("/tcga/admin/data-access-matrix.htm"));
            allowing(mockServletRequest).getRequestURL();
            will(returnValue(new StringBuffer("http://localhost:8080/tcga/admin/data-access-matrix.htm")));
            allowing(mockServletRequest).getHeader("referer");
            will(returnValue("http://localhost:8080/tcga/admin/data-access-matrix.htm"));
        }});

        ModelAndView modelAndView = adminController.handleDamAdminRequests(new DccProperty(),"");
        AdminDataModel adminDataModel = (AdminDataModel)modelAndView.getModel().get("dataModel");

        assertTrue(properties.equals(adminDataModel.getPropertyData()));
    }

    @Test
    public void addDccProperty()throws Exception{

        final List<DccProperty> properties = new ArrayList<DccProperty>();
        final DccProperty dccProperty = new DccProperty();
        dccProperty.setPropertyName("property");
        dccProperty.setPropertyValue("value");
        dccProperty.setPropertyId(1l );

        context.checking(new Expectations() {{
            one(mockAdminService).addOrUpdateProperty(with(validatePropertyBean(DccProperty.UNASSIGNED_PROPERTY_ID)));
            one(mockAdminService).getAllPropertiesForAnApplication("dam");
            will(returnValue(properties));
            one(mockServletContext).setAttribute("currentAdminUrl", "/tcga/admin/data-access-matrix.htm");
            one(mockServletContext).setAttribute("currentPage", "admin");
            allowing(mockServletRequest).getRequestURI();
            will(returnValue("/tcga/admin/data-access-matrix.htm"));
            allowing(mockServletRequest).getRequestURL();
            will(returnValue(new StringBuffer("http://localhost:8080/tcga/admin/data-access-matrix.htm")));
            allowing(mockServletRequest).getHeader("referer");
            will(returnValue("http://localhost:8080/tcga/admin/data-access-matrix.htm"));
        }});

        ModelAndView modelAndView = adminController.handleDamAdminRequests(dccProperty, ConstantValues.COMMAND_ADD);
        AdminDataModel adminDataModel = (AdminDataModel)modelAndView.getModel().get("dataModel");

        assertTrue(properties.equals(adminDataModel.getPropertyData()));
    }

    @Test
    public void updateDccProperty()throws Exception{

        final List<DccProperty> properties = new ArrayList<DccProperty>();
        final DccProperty dccProperty = new DccProperty();
        dccProperty.setPropertyName("property");
        dccProperty.setPropertyValue("value");
        dccProperty.setPropertyId(2l);

        context.checking(new Expectations() {{
            one(mockAdminService).addOrUpdateProperty(with(validatePropertyBean(2l)));
            one(mockAdminService).getAllPropertiesForAnApplication("dam");
            will(returnValue(properties));
            one(mockServletContext).setAttribute("currentAdminUrl", "/tcga/admin/data-access-matrix.htm");
            one(mockServletContext).setAttribute("currentPage", "admin");
            allowing(mockServletRequest).getRequestURI();
            will(returnValue("/tcga/admin/data-access-matrix.htm"));
            allowing(mockServletRequest).getRequestURL();
            will(returnValue(new StringBuffer("http://localhost:8080/tcga/admin/data-access-matrix.htm")));
            allowing(mockServletRequest).getHeader("referer");
            will(returnValue("http://localhost:8080/tcga/admin/data-access-matrix.htm"));
        }});

        ModelAndView modelAndView = adminController.handleDamAdminRequests(dccProperty, ConstantValues.COMMAND_UPDATE);
        AdminDataModel adminDataModel = (AdminDataModel)modelAndView.getModel().get("dataModel");

        assertTrue(properties.equals(adminDataModel.getPropertyData()));
    }

    @Test
    public void deleteDccProperty()throws Exception{

        final List<DccProperty> properties = new ArrayList<DccProperty>();
        final DccProperty dccProperty = new DccProperty();
        dccProperty.setPropertyName("property");
        dccProperty.setPropertyValue("value");
        dccProperty.setPropertyId(2l);

        context.checking(new Expectations() {{
            one(mockAdminService).deleteProperty(with(validatePropertyBean(2l)));
            one(mockAdminService).getAllPropertiesForAnApplication("dam");
            will(returnValue(properties));
            one(mockServletContext).setAttribute("currentAdminUrl", "/tcga/admin/data-access-matrix.htm");
            one(mockServletContext).setAttribute("currentPage", "admin");
            allowing(mockServletRequest).getRequestURI();
            will(returnValue("/tcga/admin/data-access-matrix.htm"));
            allowing(mockServletRequest).getRequestURL();
            will(returnValue(new StringBuffer("http://localhost:8080/tcga/admin/data-access-matrix.htm")));
            allowing(mockServletRequest).getHeader("referer");
            will(returnValue("http://localhost:8080/tcga/admin/data-access-matrix.htm"));
        }});

        ModelAndView modelAndView = adminController.handleDamAdminRequests(dccProperty, ConstantValues.COMMAND_DELETE);
        AdminDataModel adminDataModel = (AdminDataModel)modelAndView.getModel().get("dataModel");

        assertTrue(properties.equals(adminDataModel.getPropertyData()));
    }

    public static TypeSafeMatcher<DccProperty> validatePropertyBean(final Long expectedPropertyId) {
		return new TypeSafeMatcher<DccProperty>() {

			@Override
			public boolean matchesSafely(final DccProperty dccProperty) {
				return (dccProperty.getPropertyId()== expectedPropertyId);
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("validates property bean");
			}
		};
	}



    
}
