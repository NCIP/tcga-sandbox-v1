package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import java.util.HashMap;
import java.util.Map;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccProperty;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.AdminDataModel;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import java.net.URLDecoder;


/**
 * Controller class for admin related requests.
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Controller
public class AdminController implements ServletContextAware{

    @Autowired
    private AdminService adminService;

    @Autowired(required=true)
    private HttpServletRequest httpServletRequest;
    
    private static final Map<String, String> applicationURIMap = new HashMap<String, String>();
    static {
    	applicationURIMap.put("/tcga/admin/data-access-matrix.htm", ConstantValues.APP_NAME_DAM);
    	applicationURIMap.put("/tcga/admin/data-reports.htm", ConstantValues.APP_NAME_DATA_REPORTS_WEB);
    	applicationURIMap.put("/tcga/admin/data-browser.htm", ConstantValues.APP_NAME_DATA_BROWSER);
    	applicationURIMap.put("/tcga/admin/annotations.htm", ConstantValues.APP_NAME_ANNOTATIONS);
    	applicationURIMap.put("/tcga/admin/metadata-browser.htm", ConstantValues.APP_NAME_METADATA_BROWSER);
    	applicationURIMap.put("/tcga/admin/qclive.htm", ConstantValues.APP_NAME_QCLIVE);
    	applicationURIMap.put("/tcga/admin/other.htm",ConstantValues.APP_NAME_COMMON);
    }
    
    private ServletContext servletContext;

    /**
     * Handles dam admin requests.
     * @param propertyBean
     * @return admin requests result data
     * @throws Exception
     */
    @RequestMapping(value={"/admin/data-access-matrix.htm","/admin/data-reports.htm", "/admin/data-browser.htm", "/admin/annotations.htm", "/admin/metadata-browser.htm","/admin/qclive.htm","/admin/other.htm"})
    protected ModelAndView handleDamAdminRequests(final DccProperty propertyBean, final String command)throws Exception{
        AdminDataModel adminDataModel = new AdminDataModel();
        String applicationName = applicationURIMap.get(httpServletRequest.getRequestURI());
        StringBuffer completeCurrentPath = httpServletRequest.getRequestURL();
        String completeReferrerPath = httpServletRequest.getHeader("referer");
        String propertyName = "";
        String serverName = "";
        String propertyValue = "";
        String propertyDescription = "";
            
        servletContext.setAttribute("currentAdminUrl", httpServletRequest.getRequestURI());
        servletContext.setAttribute("currentPage", "admin");      
        
        //For security this section of code only runs if referer is not null and same as current url
        //that way hackers can't inject db with url parameter since escaping fields alone does not make app safe
        if(command != null && !command.isEmpty() && completeCurrentPath.toString().equals(completeReferrerPath)){
        	
            String propertyId = propertyBean.getPropertyId().toString();
            if( propertyBean.getPropertyName() != null ){
            	propertyName = URLDecoder.decode( propertyBean.getPropertyName().replace( propertyId + "escPercent", "%"), "UTF-8");
            }
            if( propertyBean.getServerName() != null ){
            	serverName = URLDecoder.decode( propertyBean.getServerName().replace( propertyId + "escPercent", "%"), "UTF-8");
            }
            if( propertyBean.getPropertyValue() != null ){
            	propertyValue = URLDecoder.decode(propertyBean.getPropertyValue().replace( propertyId + "escPercent", "%"), "UTF-8");
            }
            if( propertyBean.getPropertyDescription() != null ){
            	propertyDescription = URLDecoder.decode(propertyBean.getPropertyDescription().replace( propertyId + "escPercent", "%"), "UTF-8");
            }
            
            propertyBean.setApplicationName(applicationName);
            propertyBean.setPropertyName(propertyName);
            propertyBean.setServerName(serverName);
            propertyBean.setPropertyValue(propertyValue);
            propertyBean.setPropertyDescription(propertyDescription); 
            
            if(ConstantValues.COMMAND_UPDATE.equals(command) ||
                    ConstantValues.COMMAND_ADD.equals(command)){
                if(ConstantValues.COMMAND_ADD.equals(command)){
                    // Do not change this value as hibernate uses this to
                    //identify whether to add or update the entry
                    propertyBean.setPropertyId(DccProperty.UNASSIGNED_PROPERTY_ID);
                }
                adminService.addOrUpdateProperty(propertyBean);
            }else if(ConstantValues.COMMAND_DELETE.equals(command)){
                adminService.deleteProperty(propertyBean);
            }

        }
        adminDataModel.setPropertyData(adminService.getAllPropertiesForAnApplication(applicationName));
        return new ModelAndView("tcgaAdmin", "dataModel", adminDataModel);
    }
    
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    public void setServletContext(ServletContext servletContext){
        this.servletContext = servletContext;
    }

    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }
}

