/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.propertymanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * A Hibernate implementation for DCC property service.
 * @author Stan Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 * */
public class DccPropertyDaoImpl extends HibernateDaoSupport implements DccPropertyDao {	    	
		
	/*
	 * @see gov.nih.nci.ncicb.tcga.dcc.propertymanager.DccPropertyDao#getPropertyValue(java.lang.String)
	 */
	public String getPropertyValue (String propertyName){		
		String returnPropertyValue = "";					
		
		List<DccProperty> propertiesFromDb = getHibernateTemplate().find(
				"from DccProperty p where p.propertyName=?",propertyName);
		
		if (propertiesFromDb != null && propertiesFromDb.size() > 0){		
			returnPropertyValue = propertiesFromDb.get(0).getPropertyValue();
		}
		
        return returnPropertyValue;        
	}

	/*
	 * @see gov.nih.nci.ncicb.tcga.dcc.propertymanager.DccPropertyDao#getAllPropertiesForAnApplication(java.lang.String)
	 */
	public List<DccProperty> getAllPropertiesForAnApplication(
			String applicationName) {		
		List<DccProperty> propertyList = new ArrayList<DccProperty>();		
		
		if(StringUtils.isNotEmpty(applicationName)){
			List<DccProperty> propertiesFromDb = 
					getHibernateTemplate().find("from DccProperty p where p.applicationName=?",applicationName.toLowerCase());
			if (propertiesFromDb != null && propertiesFromDb.size() > 0){
				propertyList = propertiesFromDb;
			}
		}
		return propertyList;
	}

	@Override
	public List<DccProperty>  getAllPropertiesForServer(String serverName) {
		List<DccProperty> propertyList = new ArrayList<DccProperty>();							
		if(StringUtils.isNotEmpty(serverName)){
			List<DccProperty> propertiesFromDb = 
					getHibernateTemplate().find("from DccProperty p where p.serverName=?",serverName.toLowerCase());
			
			if (propertiesFromDb != null && propertiesFromDb.size() > 0){
				propertyList = propertiesFromDb;
			}
		}
		return propertyList;
	}

	@Override
	public List<DccProperty>   getAllPropertiesForApplicationAndServer(
			String applicationName, String serverName) {
		
			List<DccProperty> propertyList = new ArrayList<DccProperty>();							
			if(StringUtils.isNotEmpty(serverName) && 
					StringUtils.isNotEmpty(applicationName)){
				List<DccProperty> propertiesFromDb = 
						getHibernateTemplate().find("from DccProperty p where p.serverName=? and p.applicationName = ?",
								new String[] {serverName.toLowerCase(),applicationName.toLowerCase()});
				
				if (propertiesFromDb != null && propertiesFromDb.size() > 0){
					propertyList = propertiesFromDb;
				}
			}
			return propertyList;
	}		
	
    @Override
    public List<DccProperty> getDccPropertyList(){
        return getHibernateTemplate().loadAll(DccProperty.class);
    }

    @Override
    public void addOrUpdateProperty(final DccProperty dccProperty){
        getHibernateTemplate().saveOrUpdate(dccProperty);
    }

    @Override
    public void deleteProperty(final DccProperty dccProperty){
        getHibernateTemplate().delete(dccProperty);
    }

	
}
