/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BcrBatchCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.CenterCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.CodeReport;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.DataType;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PlatformCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Tissue;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.TissueSourceSite;

import javax.sql.DataSource;
import java.util.List;

/**
 * Interface describing all the code table dao methods
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public interface CodeTablesReportDAO {

    public void setDataSource(DataSource dataSource);

    /**
     * A query to get all of the rows of the TISSUE_SOURCE_SITE code report
     *
     * @return a list of TISSUE_SOURCE_SITE
     */
    public List<TissueSourceSite> getTissueSourceSite();

    /**
     * A query to get all of the rows of the center code report
     *
     * @return a list of CenterCode
     */
    public List<CenterCode> getCenterCode();

    /**
     * A query to get all of the rows of the data level code report
     *
     * @return a list of CodeReport
     */
    public List<CodeReport> getDataLevel();

    /**
     * A query to get all of the rows of the data type code report
     *
     * @return a list of DataType
     */
    public List<DataType> getDataType();

    /**
     * A query to get all of the rows of the tumor code report
     *
     * @return a list of Tumor
     */
    public List<Tumor> getTumor();

    /**
     * A query to get all of the rows of the active tumor code report
     *
     * @return a list of Tumor
     */
    public List<Tumor> getActiveTumor();

    /**
     * A query to get all of the rows of the platform code report
     *
     * @return a list of PlatformCode
     */
    public List<PlatformCode> getPlatformCode();

    /**
     * A query to get all of the rows of the Analyte code report
     *
     * @return a list of CodeReport
     */
    public List<CodeReport> getPortionAnalyte();

    /**
     * A query to get all of the rows of the SampleType code report
     *
     * @return a list of SampleType
     */
    public List<SampleType> getSampleType();

    /**
     * A query to get all of the rows of the tissue code report
     *
     * @return a list of Tissue
     */
    public List<Tissue> getTissue();

    /**
     * A query to get all of the rows of the bcrBatch code report
     *
     * @return a list of Tissue
     */
    public List<BcrBatchCode> getBcrBatchCode();
}
