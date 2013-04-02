package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DAMUtilsI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletContext;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test class for DAMHelper
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DAMHelperFastTest {
    private Mockery context = new JUnit4Mockery();
    private DAMHelper damHelper;
    private DAMUtilsI damUtils;
    private ServletContext servletContext;


    @Before
    public void setup() {
        servletContext = context.mock(ServletContext.class);
        damUtils = context.mock(DAMUtilsI.class);
        damHelper = new DAMHelper() {
            protected ServletContext getServletContext() {
                return servletContext;
            }

            protected DAMUtilsI getDAMUtils() {
                return damUtils;
            }
        };


    }

    @Test
    public void cacheTumorCenterPlatformInfo() {
        final Collection<Map<String, Object>> tumors = new ArrayList<Map<String, Object>>();
        final Collection<Map<String, Object>> centers = new ArrayList<Map<String, Object>>();
        final Collection<Map<String, Object>> platforms = new ArrayList<Map<String, Object>>();
        final Collection<Map<String, Object>> dataTypes = new ArrayList<Map<String, Object>>();

        context.checking(new Expectations() {{
            one(damUtils).getAllTumors();
            will(returnValue(tumors));
            one(servletContext).setAttribute(DAMHelper.CACHE_TUMORS, tumors);
            one(damUtils).getAllCenters();
            will(returnValue(centers));
            one(servletContext).setAttribute(DAMHelper.CACHE_CENTERS, centers);
            one(damUtils).getAllPlatforms();
            will(returnValue(platforms));
            one(servletContext).setAttribute(DAMHelper.CACHE_PLATFORMS, platforms);
            one(damUtils).getAllDataTypes();
            will(returnValue(dataTypes));
            one(servletContext).setAttribute(DAMHelper.CACHE_DATA_TYPES, dataTypes);

        }});

        damHelper.cacheTumorCenterPlatformInfo();
    }
}
