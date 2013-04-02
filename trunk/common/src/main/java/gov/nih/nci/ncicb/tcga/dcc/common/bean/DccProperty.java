/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.common.bean;

/**
 * An entity for storing DCC properties
 * 
 * @author Stan Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 * */
public class DccProperty {
    public static long UNASSIGNED_PROPERTY_ID = 0;
	private Long propertyId;		
	private String  propertyName;			
	private String  propertyValue;				
	private String  serverName;	
	private String  propertyDescription;
	private String  applicationName;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((applicationName == null) ? 0 : applicationName.hashCode());
		result = prime
				* result
				+ ((propertyDescription == null) ? 0 : propertyDescription
						.hashCode());
		result = prime * result
				+ ((propertyId == null) ? 0 : propertyId.hashCode());
		result = prime * result
				+ ((propertyName == null) ? 0 : propertyName.hashCode());
		result = prime * result
				+ ((propertyValue == null) ? 0 : propertyValue.hashCode());
		result = prime * result
				+ ((serverName == null) ? 0 : serverName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DccProperty other = (DccProperty) obj;
		if (applicationName == null) {
			if (other.applicationName != null){
				return false;
			}
		} else if (!applicationName.equals(other.applicationName))
			return false;
		if (propertyDescription == null) {
			if (other.propertyDescription != null){
				return false;
			}
		} else if (!propertyDescription.equals(other.propertyDescription))
			return false;
		if (propertyId == null) {
			if (other.propertyId != null){
				return false;
			}
		} else if (!propertyId.equals(other.propertyId))
			return false;
		if (propertyName == null) {
			if (other.propertyName != null){
				return false;
			}
		} else if (!propertyName.equals(other.propertyName))
			return false;
		if (propertyValue == null) {
			if (other.propertyValue != null){
				return false;
			}
		} else if (!propertyValue.equals(other.propertyValue))
			return false;
		if (serverName == null) {
			if (other.serverName != null){
				return false;
			}
		} else if (!serverName.equals(other.serverName))
			return false;
		return true;
	}
	public Long getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getPropertyValue() {
		return propertyValue;
	}
	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getPropertyDescription() {
		return propertyDescription;
	}
	public void setPropertyDescription(String propertyDescription) {
		this.propertyDescription = propertyDescription;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	
}
