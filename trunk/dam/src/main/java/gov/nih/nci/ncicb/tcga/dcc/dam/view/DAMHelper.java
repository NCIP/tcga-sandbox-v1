package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DAMUtilsI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DAMUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.util.Collection;
import java.util.Map;

/**
 * Helper class for DAM web layer
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMHelper implements DAMHelperI {
    private Logger logger = LoggerFactory.getLogger(DAMHelper.class);
    public static final String CACHE_TUMORS = "tumors";
    public static final String CACHE_CENTERS = "centers";
    public static final String CACHE_PLATFORMS = "platforms";
    public static final String CACHE_DATA_TYPES = "datatypes";

    /**
     * Use this API to cache tumor, center, platform, dataTypes
     * in servletContext
     */

    public void cacheTumorCenterPlatformInfo() {

        final ServletContext servletContext = getServletContext();
        if (servletContext == null) {
            logger.error("Error: Could not cache TumorCenterPlatform info in servlet context.");
            return;
        }
        final DAMUtilsI damUtils = getDAMUtils();

        final Collection<Map<String, Object>> tumors = damUtils.getAllTumors();
        servletContext.setAttribute(CACHE_TUMORS, tumors);

        final Collection<Map<String, Object>> centers = damUtils.getAllCenters();
        servletContext.setAttribute(CACHE_CENTERS, centers);

        final Collection<Map<String, Object>> platforms = damUtils.getAllPlatforms();
        servletContext.setAttribute(CACHE_PLATFORMS, platforms);

        final Collection<Map<String, Object>> dataTypes = damUtils.getAllDataTypes();
        servletContext.setAttribute(CACHE_DATA_TYPES, dataTypes);
    }

    public void refreshTumorCenterPlatformInfoCache() {
        cacheTumorCenterPlatformInfo();
    }

    protected ServletContext getServletContext() {
        ApplicationContext applicationContext = SpringApplicationContext.getApplicationContext();
        if (applicationContext instanceof WebApplicationContext) {
            WebApplicationContext webApplicationContext = (WebApplicationContext) applicationContext;
            return webApplicationContext.getServletContext();
        }
        return null;
    }

    protected DAMUtilsI getDAMUtils() {
        return DAMUtils.getInstance();
    }
}
