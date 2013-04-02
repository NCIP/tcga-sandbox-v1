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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FancyExceptionLogger;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.util.GetterMethod;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BCRJson;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ProjectCase;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.ProjectCaseDashboardConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.ProjectCaseDashboardDAO;
import net.sf.ehcache.Cache;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties.pipelineReportJsonFilesPath;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.ProjectCaseDashboardConstants.TOTALS;

/**
 * Service implementation of the project case dashboard
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Service
public class ProjectCaseDashboardServiceImpl implements ProjectCaseDashboardService {

    protected final Log logger = LogFactory.getLog(getClass());

    @Resource(name = "beanCache")
    private Cache cache;

    @Autowired
    private ProjectCaseDashboardDAO daoImpl;

    @Autowired
    private DatareportsService commonService;

    @Autowired
    CodeTablesReportService codeTablesReportService;

    @Override
    @Cached
    public List<ProjectCase> getAllProjectCaseCounts() {
        final List<ProjectCase> projectCaseList = daoImpl.getAllProjectCasesCounts();
        try {
            final List<BCRJson> listIGC = getBCRJson(pipelineReportJsonFilesPath + getMostRecentBCRFile(pipelineReportJsonFilesPath, "IGC"));
            final List<BCRJson> listNWCH = getBCRJson(pipelineReportJsonFilesPath + getMostRecentBCRFile(pipelineReportJsonFilesPath, "NWCH"));
            return completeBCRProjectCase(projectCaseList, listIGC, listNWCH);
        } catch (IOException e) {
            logger.info(e);
            return projectCaseList;
        }
    }

    protected String getMostRecentBCRFile(String path, String bcr) {
        final List<Date> dateList = new LinkedList<Date>();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
        if (path == null) {
            return null;
        }
        final File file = new File(path);
        if (file.isDirectory()) {
            for (int i = 0; i < file.list().length; i++) {
                final String el = file.list()[i];
                if (el.endsWith(".json") && el.contains(bcr + "-BCR_")) {
                    try {
                        dateList.add(dateFormat.parse(el.substring(el.indexOf("_") + 1, el.indexOf(".json"))));
                    } catch (ParseException e) {
                        logger.info(e);
                        return null;
                    }
                }
            }
            if (dateList.size() > 0) {
                Collections.sort(dateList, new Comparator<Date>() {
                    public int compare(final Date o1, final Date o2) {
                        return -(o1.compareTo(o2));
                    }
                });
                return bcr + "-BCR_" + dateFormat.format(dateList.get(0)) + ".json";
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * completeBCRProjectCase
     *
     * @param list
     * @param list IGC
     * @param list NWCH
     * @return lost of ProjectCase
     */
    protected List<ProjectCase> completeBCRProjectCase(final List<ProjectCase> list,
                                                       final List<BCRJson> listIGC, final List<BCRJson> listNWCH) throws IOException {
        final List<BCRJson> jsonList = processBCRJsonFile(listIGC, listNWCH);
        final ProjectCase total = new ProjectCase();
        total.setDisease(TOTALS);
        total.setDiseaseName("Totals");
        for (final ProjectCase pc : list) {
            pc.setDiseaseName(getDiseaseName(pc.getDisease()));
            for (final BCRJson jo : jsonList) {
                if (pc.getDisease().equals(jo.getDisease())) {
                    final int shipped = jo.getShipped();
                    final int pending = jo.getPending_shipment();
                    final int received = jo.getReceived();
                    final float jsonPassRate = jo.getQual_pass_rate();
                    final float QCPassRate = getQCPassRate(jsonPassRate, shipped, pending, received);
                    final int targetCase = Integer.parseInt(pc.getProjectedCaseBCR());
                    final int bcrShipped = shipped + pending;
                    final Integer caseRequired = (bcrShipped < 100) ? (targetCase * 2) : Math.round(targetCase / QCPassRate);
                    pc.setProjectedCaseBCR(String.valueOf(caseRequired));
                    pc.setCurrentCaseGapBCR(String.valueOf(getCurrentCaseGap(targetCase, shipped, pending, QCPassRate)));
                    pc.setReceivedBCR(String.valueOf(received) + "/" + caseRequired);
                    pc.setShippedBCR(String.valueOf(bcrShipped) + "/" + targetCase);
                    break;
                }
            }
            processTotals(total, pc);
        }
        list.add(total);
        return list;
    }

    /**
     * process totals for the list of project cases
     *
     * @param total
     * @param pc
     */
    protected void processTotals(final ProjectCase total, final ProjectCase pc) {
        final Integer totalCurrentCaseGapBCR = (total.getCurrentCaseGapBCR() == null ? 0 :
                getNumber(total.getCurrentCaseGapBCR())) + (pc.getCurrentCaseGapBCR() == null ? 0 :
                getNumber(pc.getCurrentCaseGapBCR()));
        final Integer totalProjectedCaseBCR = (total.getProjectedCaseBCR() == null ? 0 :
                getNumber(total.getProjectedCaseBCR())) + (pc.getProjectedCaseBCR() == null ? 0 :
                getNumber(pc.getProjectedCaseBCR()));
        final Integer totalCompleteCases = (total.getCompleteCases() == null ? 0 :
                getNumber(total.getCompleteCases())) + (pc.getCompleteCases() == null ? 0 :
                getNumber(pc.getCompleteCases()));
        final Integer totalIncompleteCases = (total.getIncompleteCases() == null ? 0 :
                getNumber(total.getIncompleteCases())) + (pc.getIncompleteCases() == null ? 0 :
                getNumber(pc.getIncompleteCases()));

        total.setMethylationCGCC(getTotalFromRatio(total.getMethylationCGCC(), pc.getMethylationCGCC()));
        total.setMicroRNACGCC(getTotalFromRatio(total.getMicroRNACGCC(), pc.getMicroRNACGCC()));
        total.setExpressionArrayCGCC(getTotalFromRatio(total.getExpressionArrayCGCC(), pc.getExpressionArrayCGCC()));
        total.setExpressionRNASeqCGCC(getTotalFromRatio(total.getExpressionRNASeqCGCC(), pc.getExpressionRNASeqCGCC()));
        total.setCopyNumberSNPCGCC(getTotalFromRatio(total.getCopyNumberSNPCGCC(), pc.getCopyNumberSNPCGCC()));
        total.setMutationGSC(getTotalFromRatio(total.getMutationGSC(), pc.getMutationGSC()));
        total.setMicroRNAGSC(getTotalFromRatio(total.getMicroRNAGSC(), pc.getMicroRNAGSC()));
        total.setExpressionRNASeqGSC(getTotalFromRatio(total.getExpressionRNASeqGSC(), pc.getExpressionRNASeqGSC()));
        total.setExomeGSC(getTotalFromRatio(total.getExomeGSC(), pc.getExomeGSC()));
        total.setGenomeGSC(getTotalFromRatio(total.getGenomeGSC(), pc.getGenomeGSC()));
        total.setReceivedBCR(getTotalFromRatio(total.getReceivedBCR(), pc.getReceivedBCR()));
        total.setShippedBCR(getTotalFromRatio(total.getShippedBCR(), pc.getShippedBCR()));
        total.setOverallProgress(getTotalFromRatio(total.getOverallProgress(), pc.getOverallProgress()));
        total.setProjectedCaseBCR(String.valueOf(totalProjectedCaseBCR));
        total.setCurrentCaseGapBCR(String.valueOf(totalCurrentCaseGapBCR));
        total.setCompleteCases(String.valueOf(totalCompleteCases));
        total.setIncompleteCases(String.valueOf(totalIncompleteCases));
        total.setLowPassGCC(getTotalFromRatio(total.getLowPassGCC(), pc.getLowPassGCC()));
        total.setLowPassGSC(getTotalFromRatio(total.getLowPassGSC(), pc.getLowPassGSC()));
    }

    /**
     * internal process of totals for ration values in project cases
     *
     * @param total
     * @param pc
     * @return total ratios
     */
    protected String getTotalFromRatio(final String total, final String pc) {
        Integer pcNumerator = 0;
        Integer pcDenominator = 0;
        Integer totalNumerator = 0;
        Integer totalDenominator = 0;
        if (total != null) {
            final String[] totalTab = total.split("/");
            if (totalTab.length == 2) {
                totalNumerator = getNumber(totalTab[0]);
                totalDenominator = getNumber(totalTab[1]);
            }
        }
        if (pc != null) {
            final String[] pcTab = pc.split("/");
            if (pcTab.length == 2) {
                pcNumerator = getNumber(pcTab[0]);
                pcDenominator = getNumber(pcTab[1]);
            }
        }
        return String.valueOf(totalNumerator + pcNumerator) + "/" + String.valueOf(totalDenominator + pcDenominator);
    }

    /**
     * safe string conversion into number
     *
     * @param value
     * @return number
     */
    private Integer getNumber(final String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * processBCRJsonFile
     *
     * @param listIGC
     * @param listNWCH
     * @return list of BCRJson
     */
    protected List<BCRJson> processBCRJsonFile(List<BCRJson> listIGC, List<BCRJson> listNWCH) {
        if (listIGC != null && listIGC.size() > 0) {
            for (final BCRJson igc : listIGC) {
                for (final BCRJson nwch : listNWCH) {
                    if (nwch.getDisease().equals(igc.getDisease())) {
                        igc.setPending_shipment(igc.getPending_shipment() + nwch.getPending_shipment());
                        igc.setReceived(igc.getReceived() + nwch.getReceived());
                        igc.setShipped(igc.getShipped() + nwch.getShipped());
                        listNWCH.remove(nwch);
                        break;
                    }
                }
            }
        }
        listIGC.addAll(listNWCH);
        return listIGC;
    }

    /**
     * getBCRJson
     *
     * @param fileName
     * @return list of BCRJson
     * @throws IOException
     */
    protected List<BCRJson> getBCRJson(String fileName) {
        final List<BCRJson> list = new LinkedList<BCRJson>();
        try {
            final String json = FileUtil.readFile(new File(fileName), true);
            final JSONObject jsonObject = JSONObject.fromObject(json);
            final JSONArray caseByDisease = (JSONArray) jsonObject.get("case_summary_by_disease");
            for (int i = 0; i < caseByDisease.size(); i++) {
                final JSONObject jo = caseByDisease.getJSONObject(i);
                final String disease = (String) jo.get("tumor_abbrev");
                final int shipped = (Integer) jo.get("shipped");
                final int pending = (Integer) jo.get("pending_shipment");
                final int received = (Integer) jo.get("total_cases_rcvd");
                final float jsonPassRate = jo.get("qual_pass_rate") == null ? 0f : Float.valueOf(jo.get("qual_pass_rate").toString());
                list.add(new BCRJson(disease, shipped, pending, received, jsonPassRate));
            }
        } catch (Exception e) {
            return list;
        }
        return list;
    }

    /**
     * getQCPassRate
     *
     * @param jsonPassRate
     * @param shipped
     * @param pending
     * @param total
     * @return double value
     */
    protected float getQCPassRate(float jsonPassRate, int shipped, int pending, int total) {
        if ((shipped + pending) < 100) {
            return .5f;
        } else {
            if (jsonPassRate > 0f) {
                return jsonPassRate;
            }
            float passRate = .5f;
            if (total != 0) {
                passRate = (new Float(shipped) + new Float(pending)) / new Float(total);
            }
            return passRate == 0f ? .5f : passRate;
        }
    }

    /**
     * getCurrentCaseGap
     *
     * @param targetCase
     * @param shipped
     * @param pending
     * @param qcPassRate
     * @return long value
     */
    protected int getCurrentCaseGap(int targetCase, int shipped, int pending, float qcPassRate) {
        //target number of cases - shipped - awaiting_shipment ] / qual_pass_rate
        int gap = Math.round((targetCase - shipped - pending) / qcPassRate);

        if (gap < 0) {
            gap = 0;
        }
        return gap;
    }

    @Override
    public List<ProjectCase> getFilteredProjectCaseList(final List<ProjectCase> list, final List<String> disease) {
        final StringBuilder strLog = new StringBuilder();
        final ProjectCase total = new ProjectCase();
        total.setDisease(TOTALS);
        total.setDiseaseName("Totals");
        strLog.append("Filter used: Disease:").append(disease);
        logger.debug(strLog);
        if (disease == null) {
            return list;
        }
        final List<Predicate> pcPredicateList = new LinkedList<Predicate>();
        commonService.genORPredicateList(ProjectCase.class, pcPredicateList, disease, DatareportsCommonConstants.DISEASE);
        final Predicate projectCasePredicates = PredicateUtils.allPredicate(pcPredicateList);
        final List<ProjectCase> fList = (List<ProjectCase>) CollectionUtils.select(list, projectCasePredicates);
        for (final ProjectCase pc : fList) {
            processTotals(total, pc);
        }
        fList.add(total);
        return fList;
    }

    @Override
    @Cached
    public List<ExtJsFilter> getProjectCaseFilterDistinctValues(String getterString) {
        final Set<String> tmpSet = new LinkedHashSet<String>();
        final List<ExtJsFilter> bfList = new LinkedList<ExtJsFilter>();
        try {
            final Method getter = GetterMethod.getGetter(ProjectCase.class, getterString);
            for (ProjectCase pc : getAllProjectCaseCounts()) {
                tmpSet.add(getter.invoke(pc).toString());
            }
            for (String str : tmpSet) {
                bfList.add(new ExtJsFilter(str, str));
            }
        } catch (Exception e) {
            logger.debug(FancyExceptionLogger.printException(e));
            return null;
        }
        bfList.remove(new ExtJsFilter(TOTALS, TOTALS));
        Collections.sort(bfList, commonService.comparatorExtJsFilter());
        return bfList;
    }

    @Override
    @Cached
    public Map<String, Comparator> getProjectCaseComparator() {
        final Map<String, Comparator> compMap = new HashMap<String, Comparator>();
        for (final Map.Entry<String, String> e : ProjectCaseDashboardConstants.PROJECT_CASE_DASHBOARD_COLS.entrySet()) {
            compMap.put(e.getKey(), new Comparator() {
                public int compare(final Object o1, final Object o2) {
                    try {
                        final Method getter = GetterMethod.getGetter(ProjectCase.class, e.getKey());
                        final String str1 = getter.invoke(ProjectCase.class.cast(o1)).toString();
                        final String str2 = getter.invoke(ProjectCase.class.cast(o2)).toString();
                        if (str1 == null || str2 == null) {
                            return 0;
                        }
                        if (!isRatio(str1) && !isRatio(str2)) {
                            return str1.compareTo(str2);
                        }
                        return processRatio(str1).compareTo(processRatio(str2));
                    } catch (Exception e) {
                        logger.debug(FancyExceptionLogger.printException(e));
                        return 0;
                    }
                }
            });
        }
        return compMap;
    }

    @Override
    public void refreshProjectCaseDashboardProcedure() {
        daoImpl.refreshProjectCaseDashboardProcedure();
    }

    protected boolean isRatio(final String str) {
        return (StringUtils.isNumeric(str) || str.contains("/"));
    }

    protected Float processRatio(final String ratio) {
        if (ratio == null) {
            return 0f;
        }
        if ("N/A".equalsIgnoreCase(ratio)) {
            return -1f;
        } else if (!ratio.contains("/")) {
            return Float.parseFloat(ratio);
        } else {
            final String[] tab = ratio.split("/", -1);
            return new Float(tab[0]) / new Float(tab[1]);
        }
    }

    protected String getDiseaseName(final String disease) {
        for (final Tumor tumor : codeTablesReportService.getTumor()) {
            if (disease.equalsIgnoreCase(tumor.getTumorName())) {
                return tumor.getTumorDescription();
            }
        }
        return "Unknonw";
    }


    public void emptyPCODCache() {
        final List cacheKey = cache.getKeys();
        for (final Object key : cacheKey) {
            if (key != null && key.toString().contains("ProjectCase")) {
                cache.remove(key);
            }
        }
    }

}//End of Class
