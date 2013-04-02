/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Bean representing a generic clinical object, such as patient, sample, or aliquot.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ClinicalObject {
    private String barcode;
    private Map<String, String> attributes = new HashMap<String, String>();
    private List<ClinicalObject> children = new ArrayList<ClinicalObject>();
    private String objectType;
    private Archive archive;
    private ClinicalTable clinicalTable;
    private String uuid;
    private String dynamicIdentifier;
    private Long parentId;
    private ClinicalTable parentTable;

    /**
     * Sets the barcode for the object
     * @param barcode the barcode
     */
    public void setBarcode(final String barcode) {
        this.barcode = barcode;
    }

    /**
     * Gets the object's barcode
     * @return the barcode
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * Gets the list of attributes whose values are set for this object.
     * @return collection of attribute names
     */
    public Collection<String> getAttributeNames() {
        return attributes.keySet();
    }

    /**
     * Gets the value for the given attribute
     * @param attribute the attribute name
     * @return the value, or null if not found
     */
     public String getValue(final String attribute) {
        return attributes.get(attribute);
    }

    /**
     * Adds an attribute name and value to this object.
     * @param name the attribute name
     * @param value the attribute value
     */
     public void addAttribute(final String name, final String value) {
    	String newValue = value; 
    	if (StringUtils.isNotEmpty(value)){
	    	newValue = value.trim();
    	}
    	if (attributes.containsKey(name)){
    		newValue = attributes.get(name) + "," + newValue;
    	}
        attributes.put(name, newValue);
    	
    }

    /**
     * Adds a child to this object.
     * @param child the child object
     */
    public void addChild(final ClinicalObject child) {
        children.add(child);
    }

    /**
     * Gets a list of this object's children
     * @return children clinical objects
     */
    public List<ClinicalObject> getChildren() {
        return children;
    }

    /**
     * Sets the type of this clinical object.
     * @param objectType the type of clinical object this is, such as "patient"
     */
    public void setObjectType(final String objectType) {
        this.objectType = objectType;
    }

    /**
     * Gets the type of this object.  This should correspond to the name of the XML element where the values for this
     * object can be found.
     *
     * @return the object type, such as "patient"
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * Sets the archive where this object was found.
     * @param archive archive object
     */
    public void setArchive(final Archive archive) {
        this.archive = archive;
    }

    /**
     * Gets the archive where this object was found
     * @return archive
     */
    public Archive getArchive() {
        return archive;
    }

    /**
     * Sets the clinical table object representing where this object type would be stored.
     *
     * @param clinicalTable the clinical table
     */
    public void setClinicalTable(final ClinicalTable clinicalTable) {
        this.clinicalTable = clinicalTable;
    }

    /**
     * Gets the clinical table object representing where this object would be stored
     * @return the clinical table
     */
    public ClinicalTable getClinicalTable() {
        return clinicalTable;
    }

    /**
     * Replaces the object's attributes with the given map.
     * @param attributes the attributes for the object
     */
    public void setAttributes(final Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the dynamic identifier corresponding to this object.
     * Only available if the Object represents a table that is dynamic.
     * For example, a dynmic identifier could be "follow_up_v3_0"
     * @return String - representing the dynamic identifier
     */
    public String getDynamicIdentifier() {
        return dynamicIdentifier;
    }

    /**
     * Sets the dynamic identifier for this object
     * @param dynamicIdentifier
     */
    public void setDynamicIdentifier(final String dynamicIdentifier) {
        this.dynamicIdentifier = dynamicIdentifier;
    }
    
    public void setParentId(final long parentId) {
        this.parentId = parentId;
    }

    public void setParentTable(final ClinicalTable parentTable) {
        this.parentTable = parentTable;
    }

    public ClinicalTable getParentTable() {
        return parentTable;
    }

    public Long getParentId() {
        return parentId;
    }
}
