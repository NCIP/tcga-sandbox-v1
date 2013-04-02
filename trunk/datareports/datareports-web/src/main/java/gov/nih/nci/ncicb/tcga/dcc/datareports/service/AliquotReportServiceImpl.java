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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Aliquot;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.AliquotReportDAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotReportConstants.ALIQUOT_COLS;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotReportConstants.LEVEL_ONE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotReportConstants.LEVEL_THREE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotReportConstants.LEVEL_TWO;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.PLATFORM;

/**
 * Implementation of the aliquot Report Service interface
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Service
public class AliquotReportServiceImpl implements AliquotReportService {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private AliquotReportDAO daoImpl;

    @Autowired
    private DatareportsService commonService;

    @PostConstruct
    private void initAllAliquotCache() {
        getAllAliquot();
    }

    public List<Aliquot> getAllAliquot() {
        return daoImpl.getAliquotRows();
    }

    public Map<String, Comparator> getAliquotComparator() {
        return commonService.getComparatorMap(Aliquot.class, ALIQUOT_COLS);
    }

    public List<AliquotArchive> getAllAliquotArchive(final String aliquotId, final int level) {
        return daoImpl.getAliquotArchive(aliquotId, level);
    }

    public List<Aliquot> getFilteredAliquotList(
            final List<Aliquot> list, final String aliquot,
            final List<String> disease, final List<String> center,
            final List<String> platform, final String bcrBatch,
            final List<String> levelOne, final List<String> levelTwo,
            final List<String> levelThree) {
        StringBuilder strLog = new StringBuilder();
        strLog.append("Filter used: aliquot:").append(aliquot).append(" disease:")
                .append(disease).append(" center:").append(center).append(" platform:")
                .append(platform).append(" bcrBatch:").append(bcrBatch).append(" levelOne:")
                .append(levelOne).append(" levelTwo:").append(levelTwo).append(" levelThree:")
                .append(levelThree);
        logger.debug(strLog);
        if (aliquot == null && disease == null && center == null && platform == null &&
                bcrBatch == null && levelOne == null && levelTwo == null && levelThree == null) {
            return list; //quick test so we don't have to evaluate the predicates
        }
        //Cool predicates to do my sql behavior WHERE .. AND ... in java collections 
        List<Predicate> aliPredicateList = new LinkedList<Predicate>();
        aliPredicateList.add(new Predicate() {
            public boolean evaluate(Object o) {
                if (aliquot == null || "".equals(aliquot)) {
                    return true;
                }
                return (((Aliquot) o).getAliquotId()).startsWith(aliquot);
            }
        });
        aliPredicateList.add(new Predicate() {
            public boolean evaluate(Object o) {
                if (bcrBatch == null || "".equals(bcrBatch)) {
                    return true;
                }
                return bcrBatch.equals(((Aliquot) o).getBcrBatch());
            }
        });
        commonService.genORPredicateList(Aliquot.class, aliPredicateList, disease, DISEASE);
        commonService.genORPredicateList(Aliquot.class, aliPredicateList, center, CENTER);
        commonService.genORPredicateList(Aliquot.class, aliPredicateList, platform, PLATFORM);
        commonService.genORPredicateList(Aliquot.class, aliPredicateList, levelOne, LEVEL_ONE);
        commonService.genORPredicateList(Aliquot.class, aliPredicateList, levelTwo, LEVEL_TWO);
        commonService.genORPredicateList(Aliquot.class, aliPredicateList, levelThree, LEVEL_THREE);

        Predicate aliquotPredicates = PredicateUtils.allPredicate(aliPredicateList);
        List<Aliquot> fList = (List<Aliquot>) CollectionUtils.select(list, aliquotPredicates);
        return fList;
    }

    @Cached
    public List<ExtJsFilter> getAliquotFilterDistinctValues(final String getterString) {
        final Set<String> tmpSet = new LinkedHashSet<String>();
        final List<ExtJsFilter> bfList = new LinkedList<ExtJsFilter>();
        try {
            final Method getter = GetterMethod.getGetter(Aliquot.class, getterString);
            for (Aliquot bio : getAllAliquot()) {
                tmpSet.add(getter.invoke(bio).toString());
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

}//End of Class
