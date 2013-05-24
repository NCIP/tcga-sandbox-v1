/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.bamloader;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamContext;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlFileRef;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlResult;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlResultSet;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BAMFileQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CodeTableQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

/**
 * Bam file validator class.
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Component
public class BAMValidator {
    protected final Log logger = LogFactory.getLog(getClass());
    private final String PROCESS = "Validation";
    @Autowired
    private CodeTableQueries codeTableQueries;
    @Autowired
    private TumorQueries tumorQueries;
    @Autowired
    private BAMFileQueries bamFileQueries;
    @Autowired
    private QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator;

    public void validate(final BamXmlResultSet bamXmlResultSet, final BamContext bamContext) {
        if (bamXmlResultSet != null) {
            final List<BamXmlResult> bamXmlResultList = bamXmlResultSet.getBamXmlResultList();
            if (bamXmlResultList != null && bamXmlResultList.size() > 0) {
                HashSet<BamXmlResult> blackList = new HashSet<BamXmlResult>();
                for (final BamXmlResult bamXmlResult : bamXmlResultList) {
                    validateDisease(bamXmlResult, blackList, bamContext);
                    validateAliquotUuid(bamXmlResult, blackList, bamContext);
                    validateFiles(bamXmlResult, blackList, bamContext);
                    validateAnalyteCode(bamXmlResult, blackList, bamContext);
                    validateCenter(bamXmlResult, blackList, bamContext);
                }
                bamXmlResultList.removeAll(blackList);
            } else {
                bamContext.addError(PROCESS, "BAM Data is empty.");
            }
        } else {
            bamContext.addError(PROCESS, "No BAM Data found.");
        }
    }

    protected void validateCenter(final BamXmlResult bamXmlResult, final HashSet<BamXmlResult> blackList,
                                  final BamContext bamContext) {
        final String analysisId = bamXmlResult.getAnalysisId();
        final String center = bamXmlResult.getCenter();
        final Long centerId = bamFileQueries.getDCCCenterId(center);
        if (centerId == 0L) {
            bamContext.addError(PROCESS, "Center '" + center + "' is invalid for analysis Id: "
                    + analysisId);
            blackList.add(bamXmlResult);
        } else {
            bamXmlResult.setCenter(centerId.toString());
        }
    }

    protected void validateAnalyteCode(final BamXmlResult bamXmlResult, HashSet<BamXmlResult> blackList,
                                       final BamContext bamContext) {
        final String analysisId = bamXmlResult.getAnalysisId();
        final String analyteCode = bamXmlResult.getAnalyteCode();
        if (StringUtils.isBlank(analyteCode)) {
            bamContext.addWarning(PROCESS, "Analyte Code is blank for analysis Id: " + analysisId);
        } else {
            if (!codeTableQueries.portionAnalyteExists(analyteCode)) {
                bamContext.addError(PROCESS, "Analyte Code '" + analyteCode + "' is invalid for analysis Id: "
                        + analysisId);
                blackList.add(bamXmlResult);
            }
        }
    }

    protected void validateFiles(final BamXmlResult bamXmlResult, HashSet<BamXmlResult> blackList,
                                 final BamContext bamContext) {
        final String analysisId = bamXmlResult.getAnalysisId();
        final List<BamXmlFileRef> bamXmlFileRefList = bamXmlResult.getBamXmlFileRefList();
        if (bamXmlFileRefList != null && bamXmlFileRefList.size() != 1) {
            bamContext.addError(PROCESS, "More than 1 file element found for analysis Id: "
                    + analysisId);
            blackList.add(bamXmlResult);
        }

    }

    protected void validateAliquotUuid(final BamXmlResult bamXmlResult, HashSet<BamXmlResult> blackList,
                                       final BamContext bamContext) {
        final String analysisId = bamXmlResult.getAnalysisId();
        final String uuid = bamXmlResult.getAliquotUUID();
        if (!qcLiveBarcodeAndUUIDValidator.validateUUIDFormat(uuid)) {
            bamContext.addError(PROCESS, "Aliquot UUID '" + uuid + "' is invalid for analysis Id: "
                    + analysisId);
            blackList.add(bamXmlResult);
        }
    }

    protected void validateDisease(final BamXmlResult bamXmlResult, HashSet<BamXmlResult> blackList,
                                   final BamContext bamContext) {
        final String analysisId = bamXmlResult.getAnalysisId();
        final String disease = bamXmlResult.getDisease();
        final Tumor tumor = tumorQueries.getTumorForName(disease);
        if (tumor == null) {
            bamContext.addError(PROCESS, "Disease '" + disease + "' is invalid for analysis Id: "
                    + analysisId);
            blackList.add(bamXmlResult);
        } else {
            bamXmlResult.setDisease(tumor.getTumorId().toString());
        }
    }

    public void setCodeTableQueries(CodeTableQueries codeTableQueries) {
        this.codeTableQueries = codeTableQueries;
    }

    public void setBamFileQueries(BAMFileQueries bamFileQueries) {
        this.bamFileQueries = bamFileQueries;
    }

    public void setTumorQueries(TumorQueries tumorQueries) {
        this.tumorQueries = tumorQueries;
    }

    public void setQcLiveBarcodeAndUUIDValidator(QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator) {
        this.qcLiveBarcodeAndUUIDValidator = qcLiveBarcodeAndUUIDValidator;
    }
}//End of class
