/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.constants;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * project Case dashboard constants
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ProjectCaseDashboardConstants {

    public static final String PROJECT_CASE_DASHBOARD_VIEW = "projectCaseDashboard";
    public static final String PROJECT_CASE_DASHBOARD_URL = "/" + PROJECT_CASE_DASHBOARD_VIEW + ".htm";
    public static final String PROJECT_CASE_DASHBOARD_JSON_URL = "/" + PROJECT_CASE_DASHBOARD_VIEW + ".json";
    public static final String PROJECT_CASE_DASHBOARD_FILTER_DATA_URL = "/projectCaseDashboardFilterData.json";
    public static final String PROJECT_CASE_DASHBOARD_EXPORT_URL = "/projectCaseDashboardExport.htm";
    public static final String PROJECT_CASE_DASHBOARD_FILTER_MODEL = "projectCaseDashboardFilterModel";
    public static final String PROJECT_CASE_DASHBOARD_DATA = "projectCaseDashboardData";
    public static final String EMPTY_PROJECT_CASE_DASHBOARD_FILTER = "{\"disease\":\"\"}";
    public static final String TOTALS = "TOTALS";
    public static final String TARGET_CASE_JSON_FILE = "targetCase.json";

    public static final Map<String, String> PROJECT_CASE_DASHBOARD_COLS = new LinkedHashMap<String, String>() {{
        put(DatareportsCommonConstants.DISEASE, "Disease");
        put("overallProgress", "% Complete");
        put("methylationCGCC", "GCC Methylation Data");
        put("microRNACGCC", "GCC microRNA Data");
        put("expressionArrayCGCC", "GCC Expression Data (Array)");
        put("expressionRNASeqCGCC", "GCC Expression Data (RNASeq)");
        put("copyNumberSNPCGCC", "GCC SNP/Copy Number Data");
        put("genomeGSC", "GSC Genome Sequence Data");
        put("exomeGSC", "GSC Exome and Sanger Sequence Data");
        put("mutationGSC", "GSC Mutation Data");
        put("expressionRNASeqGSC", "GSC Expression Data (RNASeq)");
        put("microRNAGSC", "GSC miRNASeq Data");
        put("projectedCaseBCR", "Case Required");
        put("currentCaseGapBCR", "Case Gap");
        put("receivedBCR", "BCR Received");
        put("shippedBCR", "BCR Shipped");
        put("completeCases", "Complete Cases");
        put("incompleteCases", "Incomplete Cases");
        put("lowPassGCC", "GCC Low Pass");
        put("lowPassGSC", "GSC Low Pass");
    }};

    public static final String QUERY_PROJECT_CASE_COUNTS = "select disease_abbreviation, " +
            "metholated_data_cases, microrna_data_cases, exparray_data_cases, exprnaseq_data_cases, " +
            "copynumber_data_cases, gsc_mutation_data_cases, gsc_microrna_cases, gsc_rnaseq_cases, " +
            "gsc_exome_cases, gsc_genome_cases, gcc_lowpass_cases, gsc_lowpass_cases " +
            "from projectoverview_case_counts order by disease_abbreviation";

    public static final String QUERY_REFRESH_PROJECT_CASE_DASHBOARD_PROC =
            "call pcod_report.build_projectOverview_counts()";

    public static final String QUERY_COMPLETE_CASE_BY_DISEASE = "select count(distinct participant_barcode) " +
            "as complete_cases from pcod_normal_tumor_stats " +
            "where cn_TumorCount = 1 " +
            "and cn_NormalCount = 1 " +
            "and (expArray_TumorCount = 1 or expRnaSeq_TumorCount = 1) " +
            "and mirna_TumorCount = 1 " +
            "and methylation_TumorCount = 1 " +
            "and gsc_exome_TumorCount = 1 " +
            "and gsc_exome_NormalCount = 1 " +
            "and mutation_TumorCount= 1 " +
            "and mutation_normalCount = 1 " +
            "and disease_abbreviation = ?";

}//End of Class
