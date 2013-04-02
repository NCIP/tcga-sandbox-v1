/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BcrBatchCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.CenterCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.CodeReport;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.DataType;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PlatformCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Tissue;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.TissueSourceSite;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Interface for the code tables service layer
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public interface CodeTablesReportService {

    /**
     * A query to get all TISSUE_SOURCE_SITE code report
     *
     * @return a list of TISSUE_SOURCE_SITE
     */
    public List<TissueSourceSite> getTissueSourceSite();

    /**
     * get the TISSUE_SOURCE_SITE comparator map
     *
     * @return a TISSUE_SOURCE_SITE comparator map
     */
    public Map<String, Comparator> getTissueSourceSiteComparator();

    /**
     * A query to get all of the rows of the center code report
     *
     * @return a list of CenterCode
     */
    public List<CenterCode> getCenterCode();

    /**
     * get the CenterCode comparator map
     *
     * @return a CenterCode comparator map
     */
    public Map<String, Comparator> getCenterCodeComparator();

    /**
     * A query to get all of the rows of the data level code report
     *
     * @return a list of CodeReport
     */
    public List<CodeReport> getDataLevel();

    /**
     * A query to get all of the rows of the data type code report
     *
     * @return a list of String
     */
    public List<DataType> getDataType();

    /**
     * get the DataType comparator map
     *
     * @return a DataType comparator map
     */
    public Map<String, Comparator> getDataTypeComparator();

    /**
     * A query to get all of the rows of the tumor code report
     *
     * @return a list of Tumor
     */
    public List<Tumor> getTumor();

    /**
     * get the Tumor comparator map
     *
     * @return a Tumor comparator map
     */
    public Map<String, Comparator> getTumorComparator();

    /**
     * A query to get all of the rows of the platform code report
     *
     * @return a list of PlatformCode
     */
    public List<PlatformCode> getPlatformCode();

    /**
     * get the PlatformCode comparator map
     *
     * @return a PlatformCode comparator map
     */
    public Map<String, Comparator> getPlatformCodeComparator();

    /**
     * A query to get all of the rows of the Analyte code report
     *
     * @return a list of Analyte
     */
    public List<CodeReport> getPortionAnalyte();

    /**
     * A query to get all of the rows of the SampleType code report
     *
     * @return a list of SampleType
     */
    public List<SampleType> getSampleType();

    /**
     * get the SampleType comparator map
     *
     * @return a SampleType comparator map
     */
    public Map<String, Comparator> getSampleTypeComparator();

    /**
     * get the CodeReport comparator map
     *
     * @return a CodeReport comparator map
     */
    public Map<String, Comparator> getCodeReportComparator();

    /**
     * A query to get all of the rows of the tissue code report
     *
     * @return a list of String
     */
    public List<Tissue> getTissue();

    /**
     * A query to get all of the rows of the bcrBatch code report
     *
     * @return a list of Tissue
     */
    public List<BcrBatchCode> getBcrBatchCode();

    /**
     * get the BcrbatchCode comparator map
     *
     * @return a BcrbatchCode comparator map
     */
    public Map<String, Comparator> getBcrBatchCodeComparator();
}
