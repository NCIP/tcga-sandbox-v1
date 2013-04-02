/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FancyExceptionLogger;
import gov.nih.nci.ncicb.tcga.dcc.common.util.GetterMethod;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.LatestArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Maf;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Sdrf;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.LatestGenericReportDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
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

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_FORMAT_US_STRING;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.LatestGenericReportConstants.ARCHIVE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.LatestGenericReportConstants.LATEST_ARCHIVE_COLS;

/**
 *  Class that implements the latestXreport service interface
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Service
public class LatestGenericReportServiceImpl implements LatestGenericReportService {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    LatestGenericReportDAO daoImpl;

    @Autowired
    private DatareportsService commonService;

    public List<Sdrf> getLatestSdrfWS() {
        return daoImpl.getLatestSdrfWS();
    }

    public List<Archive> getLatestArchiveWS() {
        return daoImpl.getLatestArchiveWS();
    }

    public List<Archive> getLatestArchiveWSByType(final String archiveType) {
        return daoImpl.getLatestArchiveWSByType(archiveType);
    }

    public List<Maf> getLatestMafWS() {
        return daoImpl.getLatestMafWS();
    }

    public List<LatestArchive> getLatestArchive() {
        return daoImpl.getLatestArchive();
    }

    public List<LatestArchive> getFilteredLatestArchiveList(
            final List<LatestArchive> list, final List<String> archiveType, final String dateFromStr,
            final String dateToStr) {
        final StringBuilder strLog = new StringBuilder();
        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_US_STRING);
        strLog.append("Filter used: archiveType:").append(archiveType).append(" dateFrom:")
                .append(dateFromStr).append(" dateTo:").append(dateToStr);
        logger.debug(strLog);
        if (archiveType == null && dateFromStr == null && dateToStr == null) {
            return list; //quick test so we don't have to evaluate the predicates
        }
        final List<Predicate> archPredicateList = new LinkedList<Predicate>();
        archPredicateList.add(commonService.genDatePredicate(LatestArchive.class, "dateAdded", false, dateFromStr, dateFormat));
        archPredicateList.add(commonService.genDatePredicate(LatestArchive.class, "dateAdded", true, dateToStr, dateFormat));
        commonService.genORPredicateList(LatestArchive.class, archPredicateList, archiveType, ARCHIVE_TYPE);
        final Predicate latestArchivePredicates = PredicateUtils.allPredicate(archPredicateList);
        final List<LatestArchive> fList = (List<LatestArchive>) CollectionUtils.select(list, latestArchivePredicates);
        return fList;
    }

    public Map<String, Comparator> getLatestArchiveComparator() {
        return commonService.getComparatorMap(LatestArchive.class, LATEST_ARCHIVE_COLS);
    }

    public List<ExtJsFilter> getLatestArchiveFilterDistinctValues(final String getterString) {
        final Set<String> tmpSet = new LinkedHashSet<String>();
        final List<ExtJsFilter> bfList = new LinkedList<ExtJsFilter>();
        try {
            final Method getter = GetterMethod.getGetter(LatestArchive.class,getterString);
            for (final LatestArchive arch : getLatestArchive()) {
                tmpSet.add(getter.invoke(arch).toString());
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

} //End of Class
