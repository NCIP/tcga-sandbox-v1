/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.PortionAnalyte;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.TissueSourceSite;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDType;

import javax.sql.DataSource;
import java.util.List;

/**
 * DAO layer interface defining database queries methods for the uuid browser
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface UUIDBrowserDAO {

    /**
     * set datasource
     *
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource);

    /**
     * get all rows from the uuid_hierarchy table
     *
     * @return list of biospecimenMetaData
     */
    public List<BiospecimenMetaData> getUUIDRows();

    /**
     * Returns list of barcodes that exists in the database
     *
     * @param barcodes to check whether they exist in the database or not
     * @return list of barcodes that exists in the database
     */
    public List<String> getExistingBarcodes(List<String> barcodes);


    /**
     * get rows from the uuid_hierarchy table filtered by barcode
     *
     * @return list of biospecimenMetaData
     */
    public List<BiospecimenMetaData> getBiospecimenMetaDataRowsFromBarcode(String barcode);

    /**
     * get rows from the uuid_hierarchy table filtered by uuid
     *
     * @return list of biospecimenMetaData
     */
    public List<BiospecimenMetaData> getBiospecimenMetaDataRowsFromUUID(String uuid);

    /**
     * get rows from the uuid_hierarchy table filtered by list of uuids
     *
     * @return list of biospecimenMetaData
     */
    public List<BiospecimenMetaData> getBiospecimenMetaDataRowsFromMultipleUUID(List<String> uuids);

    /**
     * get rows from the uuid_hierarchy table filtered by list of uuids
     *
     * @return list of biospecimenMetaData
     */
    public List<BiospecimenMetaData> getBiospecimenMetaDataRowsFromMultipleBarcode(List<String> barcodes);

    /**
     * get list of all diseases
     *
     * @return list of tumor
     */
    public List<Tumor> getDiseases();

    /**
     * get list of full centers
     *
     * @return list of  center
     */
    public List<Center> getCenters();

    /**
     * get list of full centers with bcr code
     *
     * @return list of  center
     */
    public List<Center> getCentersWithBCRCode();

    /**
     * get list of all platforms
     *
     * @return list of platform
     */
    public List<Platform> getPlatforms();

    /**
     * get list of all sample types
     *
     * @return list of sampleType
     */
    public List<SampleType> getSampleTypes();

    /**
     * get list of all portion analytes
     *
     * @return list of portionAnalyte
     */
    public List<PortionAnalyte> getPortionAnalytes();

    /**
     * get list of all uuid types
     *
     * @return list of uuidType
     */
    public List<UUIDType> getUuidTypes();

    /**
     * get list of all tissue source sites
     *
     * @return list of tissueSourceSite
     */
    public List<TissueSourceSite> getTissueSourceSites();

}
