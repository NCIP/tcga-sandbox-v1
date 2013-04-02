/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

/**
 * Bean class representing the type of a uuid
 *
 * @author Dominique Berton
 *         Last updated by: Stan Girshik
 * @version $Rev$
 */
public class UUIDType {

    private Integer uuidTypeId;
    private String uuidType;
    private Integer sortOrder;
    private String xmlElement;
   
	public UUIDType() {
    }

    public UUIDType(Integer uuidTypeId, String uuidType, Integer sortOrder, String xmlElement) {
        this.uuidTypeId = uuidTypeId;
        this.uuidType = uuidType;
        this.sortOrder = sortOrder;
        this.xmlElement = xmlElement;
    }

    public Integer getUuidTypeId() {
        return uuidTypeId;
    }

    public void setUuidTypeId(Integer uuidTypeId) {
        this.uuidTypeId = uuidTypeId;
    }

    public String getUuidType() {
        return uuidType;
    }

    public void setUuidType(String uuidType) {
        this.uuidType = uuidType;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    public String getXmlElement() {
		return xmlElement;
	}

	public void setXmlElement(String xmlElement) {
		this.xmlElement = xmlElement;
	}

}//End of Class
