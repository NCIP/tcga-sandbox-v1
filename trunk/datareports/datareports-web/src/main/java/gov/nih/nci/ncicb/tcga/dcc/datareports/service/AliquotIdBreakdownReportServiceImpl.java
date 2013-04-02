/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.common.util.FancyExceptionLogger;
import gov.nih.nci.ncicb.tcga.dcc.common.util.GetterMethod;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotIdBreakdown;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.AliquotIdBreakdownReportDAO;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotIdBreakdownReportConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * implementation of the biospecimenbreakdown report service
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
@Service
public class AliquotIdBreakdownReportServiceImpl implements AliquotIdBreakdownReportService {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private AliquotIdBreakdownReportDAO daoImpl;

    @Autowired
    private DatareportsService commonService;

    @PostConstruct
    private void initAllBiospecimenBreakdownCache() {
        getAliquotIdBreakdown();
    }

    public List<AliquotIdBreakdown> getAliquotIdBreakdown() {
        return daoImpl.getAliquotIdBreakdown();
    }

    public Map<String, Comparator> getAliquotIdBreakdownComparator() {
        return commonService.getComparatorMap(AliquotIdBreakdown.class, AliquotIdBreakdownReportConstants.ALIQUOT_ID_BREAKDOWN_COLS);
    }

    public List<AliquotIdBreakdown> getFilteredAliquotIdBreakdownList(
            final List<AliquotIdBreakdown> list, final String aliquotId, final String analyteId,
            final String sampleId,
            final String participantId) {
        StringBuilder strLog = new StringBuilder();
        strLog.append("Filter used: aliquotId:").append(aliquotId).append(" analyteId:")
                .append(analyteId).append(" sampleId:").append(sampleId)
                .append(" participantId:").append(participantId);
        logger.debug(strLog);
        if (aliquotId == null && analyteId == null && sampleId == null && participantId == null) {
            return list; //quick test so we don't have to evaluate the predicates
        }
        //Cool predicates to do my sql behavior WHERE .. AND ... in java collections
        List<Predicate> bbPredicateList = new LinkedList<Predicate>();
        bbPredicateList.add(processAliquotIdBreakdownPredicates("aliquotId",aliquotId,""));
        bbPredicateList.add(processAliquotIdBreakdownPredicates("analyteId",analyteId,""));
        bbPredicateList.add(processAliquotIdBreakdownPredicates("sampleId",sampleId,""));
        bbPredicateList.add(processAliquotIdBreakdownPredicates("participantId",participantId,""));
        Predicate biospecimenBreakdownPredicates = PredicateUtils.allPredicate(bbPredicateList);
        List<AliquotIdBreakdown> fList = (List<AliquotIdBreakdown>) CollectionUtils.select(list,
                biospecimenBreakdownPredicates);
        return fList;
    }

    private Predicate processAliquotIdBreakdownPredicates(final String getter,final String filter,
                                                           final String emptyValueFilter){
         final Class bbClass = AliquotIdBreakdown.class;
         return new Predicate() {
            public boolean evaluate(Object o) {
                if (filter == null || emptyValueFilter.equals(filter)) {
                    return true;
                }
                try {
                    return GetterMethod.getGetter(bbClass,getter).invoke(o).toString().startsWith(filter);
                } catch (Exception e) {
                    logger.debug(FancyExceptionLogger.printException(e));
                    return true;
                }
            }
         };
    }
    
}//End of Class
