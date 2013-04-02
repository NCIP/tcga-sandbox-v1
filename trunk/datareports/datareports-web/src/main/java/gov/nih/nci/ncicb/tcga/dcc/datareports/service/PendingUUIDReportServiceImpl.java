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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PendingUUID;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.PendingUUIDDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.BCR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.PendingUUIDReportConstants.PENDING_UUID_REPORT_COLS;

/**
 * implementation of the pending uuid report service
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Service
public class PendingUUIDReportServiceImpl implements PendingUUIDReportService {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private DatareportsService commonService;

    @Autowired
    private PendingUUIDDAO daoImpl;

    @Override
    public List<PendingUUID> getAllPendingUUIDs() {
        return daoImpl.getAllPendingUUIDs();
    }

    @Override
    public List<PendingUUID> getFilteredPendingUUIDList(final List<PendingUUID> list, final List<String> bcr,
                                                        final List<String> center, final String batch,
                                                        final String plateId) {
        final StringBuilder strLog = new StringBuilder();
        strLog.append("Filter used: Bcr:").append(bcr).append(" Center:").append(center)
                .append(" Batch:").append(batch).append(" Plate Id:").append(plateId);
        logger.debug(strLog);
        if (bcr == null && center == null && batch == null && plateId == null) {
            return list;
        }
        final List<Predicate> pUUIDPredicateList = new LinkedList<Predicate>();
        pUUIDPredicateList.add(new Predicate() {
            public boolean evaluate(Object o) {
                if (batch == null || "".equals(batch)) {
                    return true;
                }
                return batch.equals(((PendingUUID) o).getBatchNumber());
            }
        });
        pUUIDPredicateList.add(new Predicate() {
            public boolean evaluate(Object o) {
                if (plateId == null || "".equals(plateId)) {
                    return true;
                }
                return plateId.equals(((PendingUUID) o).getPlateId());
            }
        });
        commonService.genORPredicateList(PendingUUID.class, pUUIDPredicateList, bcr, BCR);
        commonService.genORPredicateList(PendingUUID.class, pUUIDPredicateList, center, CENTER);
        final Predicate pendingUUIDPredicates = PredicateUtils.allPredicate(pUUIDPredicateList);
        final List<PendingUUID> fList = (List<PendingUUID>) CollectionUtils.select(list, pendingUUIDPredicates);
        return fList;
    }

    @Override
    @Cached
    public List<ExtJsFilter> getPendingUUIDFilterDistinctValues(final String getterString) {
        final Set<String> tmpSet = new LinkedHashSet<String>();
        final List<ExtJsFilter> bfList = new LinkedList<ExtJsFilter>();
        try {
            final Method getter = GetterMethod.getGetter(PendingUUID.class, getterString);
            for (final PendingUUID pendingUUID : getAllPendingUUIDs()) {
                final Object obj = getter.invoke(pendingUUID);
                final String str = obj == null ? "" : obj.toString();
                tmpSet.add(str);
            }
            for (final String str : tmpSet) {
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
    public Map<String, Comparator> getPendingUUIDComparator() {
        return commonService.getComparatorMap(PendingUUID.class, PENDING_UUID_REPORT_COLS);
    }
}
