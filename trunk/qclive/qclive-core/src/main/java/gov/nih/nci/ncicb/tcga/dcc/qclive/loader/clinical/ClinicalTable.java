/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical;

/**
 * Bean representing a clinical table's meta-data.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ClinicalTable {

    private String barcodeElementName;
    private String tableName;
    private String idColumnName;
    private String barcodeColumName;
    private String elementTableName;
    private String archiveLinkTableName;
    private String uuidElementName;
    private String elementNodeName;
    private boolean isDynamic;
    private String dynamicIdentifierColumnName;
    private Long clinicalTableId;
    private Long parentTableId;

    /**
     * Sets the name of the element in the XML that contains the barcode value for objects held by this table.
     * @param barcodeElementName the XML element name that contains the barcode
     */
    public void setBarcodeElementName(final String barcodeElementName) {
        this.barcodeElementName = barcodeElementName;
    }

    /**
     * Sets the name of the table
     * @param tableName the table name
     */
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    /**
     * Sets the name of the table column that contains the ID (primary key) of the table.
     * @param idColumnName the ID column name
     */
    public void setIdColumnName(final String idColumnName) {
        this.idColumnName = idColumnName;
    }

    /**
     * Sets the name of the table column that contains the barcode.
     * @param barcodeColumnName the barcode column name
     */
    public void setBarcodeColumName(final String barcodeColumnName) {
        this.barcodeColumName = barcodeColumnName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getBarcodeElementName() {
        return barcodeElementName;
    }

    public String getIdColumnName() {
        return idColumnName;
    }

    public String getBarcodeColumName() {
        return barcodeColumName;
    }

    public String getElementTableName() {
        return elementTableName;
    }

    /**
     * Sets the name of the table that contains key/value attribute pairs for this table.
     * @param elementTableName the name of the element table corresponding to this table
     */
    public void setElementTableName(final String elementTableName) {
        this.elementTableName = elementTableName;
    }

    public String getArchiveLinkTableName() {
        return archiveLinkTableName;
    }

    public void setArchiveLinkTableName(final String archiveLinkTableName) {
        this.archiveLinkTableName = archiveLinkTableName;
    }

    public void setUuidElementName(final String uuidElementName) {
        this.uuidElementName = uuidElementName;
    }

    public String getUuidElementName() {
        return uuidElementName;
    }

    public String getElementNodeName() {
        return elementNodeName;
    }

    public void setElementNodeName(String elementNodeName) {
        this.elementNodeName = elementNodeName;
    }

    public boolean isDynamic() {
        return isDynamic;
    }

    public void setDynamic(final boolean dynamic) {
        isDynamic = dynamic;
    }

    public String getDynamicIdentifierColumnName() {
        return dynamicIdentifierColumnName;
    }

    public void setDynamicIdentifierColumnName(final String dynamicIdentifierColumnName) {
        this.dynamicIdentifierColumnName = dynamicIdentifierColumnName;
    }

    public Long getClinicalTableId() {
        return clinicalTableId;
    }

    public void setClinicalTableId(final Long clinicalTableId) {
        this.clinicalTableId = clinicalTableId;
    }

    public Long getParentTableId() {
        return parentTableId;
    }

    public void setParentTableId(final Long parentTableId) {
        this.parentTableId = parentTableId;
    }
}
