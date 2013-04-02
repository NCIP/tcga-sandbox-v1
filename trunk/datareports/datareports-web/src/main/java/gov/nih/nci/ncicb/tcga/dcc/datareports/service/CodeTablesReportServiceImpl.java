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
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.CodeTablesReportDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the code tables service layer interface
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Service
public class CodeTablesReportServiceImpl implements CodeTablesReportService {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private CodeTablesReportDAO daoImpl;

    @Autowired
    private DatareportsService commonService;

    public List<TissueSourceSite> getTissueSourceSite() {
        return daoImpl.getTissueSourceSite();
    }

    public Map<String, Comparator> getTissueSourceSiteComparator() {
        return commonService.getComparatorMap(TissueSourceSite.class, CodeTablesReportConstants.TISSUE_SOURCE_SITE_COLS);
    }

    public List<CodeReport> getDataLevel() {
        return daoImpl.getDataLevel();
    }

    public List<CenterCode> getCenterCode() {
        return daoImpl.getCenterCode();
    }

    public Map<String, Comparator> getCenterCodeComparator() {
        return commonService.getComparatorMap(CenterCode.class, CodeTablesReportConstants.CENTER_CODE_COLS);
    }

    public List<DataType> getDataType() {
        return daoImpl.getDataType();
    }

    public Map<String, Comparator> getDataTypeComparator() {
        return commonService.getComparatorMap(DataType.class, CodeTablesReportConstants.DATA_TYPE_COLS);
    }

    public List<Tumor> getTumor() {
        return daoImpl.getTumor();
    }

    public Map<String, Comparator> getTumorComparator() {
        return commonService.getComparatorMap(Tumor.class, CodeTablesReportConstants.TUMOR_COLS);
    }

    public List<PlatformCode> getPlatformCode() {
        return daoImpl.getPlatformCode();
    }

    public Map<String, Comparator> getPlatformCodeComparator() {
        return commonService.getComparatorMap(PlatformCode.class, CodeTablesReportConstants.PLATFORM_COLS);
    }

    public List<CodeReport> getPortionAnalyte() {
        return daoImpl.getPortionAnalyte();
    }

    public List<SampleType> getSampleType() {
        return daoImpl.getSampleType();
    }

    public Map<String, Comparator> getSampleTypeComparator() {
        return commonService.getComparatorMap(SampleType.class, CodeTablesReportConstants.SAMPLE_TYPE_COLS);
    }

    public Map<String, Comparator> getCodeReportComparator() {
        return commonService.getComparatorMap(CodeReport.class, CodeTablesReportConstants.CODE_REPORT_COLS);
    }

    public List<Tissue> getTissue() {
        return daoImpl.getTissue();
    }

    public List<BcrBatchCode> getBcrBatchCode() {
        return daoImpl.getBcrBatchCode();
    }

    public Map<String, Comparator> getBcrBatchCodeComparator() {
        return commonService.getComparatorMap(BcrBatchCode.class, CodeTablesReportConstants.BCR_BATCH_COLS);
    }
}//End of Class
