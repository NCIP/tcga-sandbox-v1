/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache.Cached;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FancyExceptionLogger;
import gov.nih.nci.ncicb.tcga.dcc.common.util.GetterMethod;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BamTelemetry;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.BamTelemetryReportDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.BamTelemetryReportConstants.BAM_TELEMETRY_COLS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATA_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_FORMAT_US_STRING;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.MOLECULE;

/**
 * bam telemetry service implementation
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Service
public class BamTelemetryReportServiceImpl implements BamTelemetryReportService {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private BamTelemetryReportDAO daoImpl;

    @Autowired
    private DatareportsService commonService;

    @Override
    public List<BamTelemetry> getAllBamTelemetry() {
        return daoImpl.getBamTelemetryRows();
    }

    @Override
    public List<BamTelemetry> getFilteredBamTelemetryList(final List<BamTelemetry> list,
                                                          final String aliquotUUID, final String aliquotId, final String dateFrom, final String dateTo, final List<String> disease,
                                                          final List<String> center, final List<String> dataType, final List<String> molecule) {
        final StringBuilder strLog = new StringBuilder();
        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_US_STRING);
        strLog.append("Filter used: aliquotUUID:").append(aliquotUUID).append("aliquotID")
                .append(aliquotId).append(" disease:")
                .append(disease).append(" center:").append(center).append(" dataType:")
                .append(dataType).append(" molecule:").append(molecule).append(" dateFrom:")
                .append(dateFrom).append(" dateTo:").append(dateTo);
        logger.debug(strLog);
        if (aliquotId == null && aliquotUUID == null && disease == null && center == null && dataType == null &&
                molecule == null && dateFrom == null && dateTo == null) {
            return list;
        }

        final List<Predicate> bamPredicateList = new LinkedList<Predicate>();
        bamPredicateList.add(new Predicate() {
            public boolean evaluate(Object o) {
                boolean isValid = true;

                if (StringUtils.isNotEmpty(aliquotUUID)) {
                    isValid = (((BamTelemetry) o).getAliquotUUID()).equalsIgnoreCase(aliquotUUID);
                } else if (StringUtils.isNotEmpty(aliquotId)) {
                    isValid = (((BamTelemetry) o).getAliquotId()).startsWith(aliquotId);
                } else {
                    return isValid;
                }
                return isValid;
            }
        });
        bamPredicateList.add(commonService.genDatePredicate(BamTelemetry.class, "dateReceived", false, dateFrom, dateFormat));
        bamPredicateList.add(commonService.genDatePredicate(BamTelemetry.class, "dateReceived", true, dateTo, dateFormat));
        commonService.genORPredicateList(BamTelemetry.class, bamPredicateList, disease, DISEASE);
        commonService.genORPredicateList(BamTelemetry.class, bamPredicateList, center, CENTER);
        commonService.genORPredicateList(BamTelemetry.class, bamPredicateList, dataType, DATA_TYPE);
        commonService.genORPredicateList(BamTelemetry.class, bamPredicateList, molecule, MOLECULE);

        Predicate bamTelemetryPredicates = PredicateUtils.allPredicate(bamPredicateList);
        List<BamTelemetry> fList = (List<BamTelemetry>) CollectionUtils.select(list, bamTelemetryPredicates);
        return fList;
    }

    @Override
    @Cached
    public List<ExtJsFilter> getBamTelemetryFilterDistinctValues(String getterString) {
        final Set<String> tmpSet = new LinkedHashSet<String>();
        final List<ExtJsFilter> bfList = new LinkedList<ExtJsFilter>();
        try {
            final Method getter = GetterMethod.getGetter(BamTelemetry.class, getterString);
            for (BamTelemetry bam : getAllBamTelemetry()) {
                tmpSet.add(getter.invoke(bam).toString());
            }
            for (String str : tmpSet) {
                bfList.add(new ExtJsFilter(str, str));
            }
        } catch (Exception e) {
            logger.debug(FancyExceptionLogger.printException(e));
            return null;
        }
        Collections.sort(bfList, commonService.comparatorExtJsFilter());
        return bfList;
    }

    @Override
    public Map<String, Comparator> getBamTelemetryComparator() {
        return commonService.getComparatorMap(BamTelemetry.class, BAM_TELEMETRY_COLS);
    }

}//End of class
