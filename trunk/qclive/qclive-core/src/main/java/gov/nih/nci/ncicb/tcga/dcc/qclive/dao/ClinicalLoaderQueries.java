/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalObject;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalTable;

import java.util.List;

/**
 * Interface for ClinicalLoaderQueries
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ClinicalLoaderQueries {

    /**
     * Gets all clinical tables in the system.
     * @return a list of ClinicalTable objects representing all records in clinical_table
     */
    public List<ClinicalTable> getAllClinicalTables();

    /**
     * Gets a clinical table object for the given "elementName" where the elementName is the name the object's element
     * will have in the clinical XML (In the clinical_table table, it's the element_node_name column)
     * and where the table object's parent Id is matching the given parent Id.
     *
     * Some examples of elementName in the current schema are "patient" and "sample".
     *
     * @param elementName the name of the element that holds the object in XML
     * @param parentId the Id of the parent table, or <code>null</code> if there is no parent
     * @return a clinical table object for this element, or null if it wasn't found
     */
    public ClinicalTable getClinicalTableForElementName(final String elementName,
                                                        final Long parentId);

    /**
     * Gets all clinicalTable objects representing dynamic clinical tables.
     * @return list of ClinicalTable object that are dynamic
     */
    public List<ClinicalTable> getDynamicClinicalTables();

    /**
     * Gets the ID (primary key) of the given clinical object. The clinical object must have its clinicalTable variable
     * set as well as its barcode.
     *
     * @param clinicalObject the object whose id you want to look up
     * @return the ID of the object or -1 if the object is not in the database
     */
    public long getId(ClinicalObject clinicalObject);

    /**
     * Inserts the clinical object into the database.  Will insert a new record into the corresponding table as well
     * as as many needed to hold the object's attributes.  NOTE: DOES NOT RECURSE THROUGH CHILDREN.
     *
     * @param clinicalObject the object to insert
     * @param parentId the ID for the parent object, if any (use -1 if none)
     * @param archiveId the ID of the archive in which this object was found
     * @return the ID of the newly inserted object
     */
    public long insert(ClinicalObject clinicalObject, long parentId, long archiveId, List newElements);

    /**
     * Inserts a record into the clinical_xsd_element table for new elements in the xsd for this disease
     *
     * @param elementName the name of the new element
     * @param isProtected 1 for protected, 0 for not protected
     * @param description the description of the new element
     * @param valueType the value type of the new element (ie, String, Float, etc)
     * @param expected "Y" for expected, "N" for not expected
     */
    public long insertClinicalXsdElement(String elementName, int isProtected, String description, String valueType, String expected);

    /**
     * Updates the clinical object in the database.  Changed attribute values will be updated, new attributes will be
     * inserted, and any attributes not in the object that are in the database will be deleted from the database.
     *
     * @param clinicalObject the object to update
     * @param archiveId the ID of the archive in which this object was found
     */
    public void update(ClinicalObject clinicalObject, long archiveId, List newElements);

    public void addArchiveLink(ClinicalObject clinicalObject, long archiveId);

    /**
     * Gets the parent table of the given clinical table.
     *
     * @param clinicalTable the clinical table
     * @return the clinical table's parent table, or null if no parent
     */
    public ClinicalTable getParentTable(ClinicalTable clinicalTable);

    /**
     * Gets the clinical object for the given barcode, type and parent Id.
     * Will have attributes set but will not populate children.
     *
     * @param objectElementName the element name of the object, such as "patient"
     * @param barcode the barcode of the object
     * @param parentId the Id of the parent table, or <code>null</code> if there is no parent
     * @return the clinical object or null if not found
     */
    public ClinicalObject getClinicalObjectForBarcode(final String objectElementName,
                                                      final String barcode,
                                                      final Long parentId);

    /**
     * Looks up the element name to see if it represents a clinical table.
     *
     * @param elementName the element name
     * @return whether the element represents a clinical table
     */
    public boolean elementRepresentsClinicalTable(final String elementName);


    /**
     * Looks up if a clinical XSD element with the given name exists in the database.
     * @param elementName the element name
     * @return whether there is an xsd element in the database with that name
     */
    public boolean clinicalXsdElementExists(String elementName);


    /**
     * Returns a list of clinicalXSDElements
     */
    public List<String> getClinicalXsdElements();

    /**
     * Returns a list of archives to load for clinical loader
     * @return  a list of archive ids to load
     */
     public List<Long> getArchivesToLoadId ();

}
