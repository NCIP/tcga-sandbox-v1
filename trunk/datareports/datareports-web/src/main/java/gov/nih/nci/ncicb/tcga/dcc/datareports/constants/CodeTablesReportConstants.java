/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
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
 * Code table report constants
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class CodeTablesReportConstants {

    public static final String CODE_TABLES_REPORT_VIEW = "codeTablesReport";
    public static final String CODE_TABLES_REPORT_URL = "/" + CODE_TABLES_REPORT_VIEW + ".htm";
    public static final String CODE_TABLES_REPORT_JSON_URL = "/" + CODE_TABLES_REPORT_VIEW + ".json";
    public static final String CODE_TABLES_EXPORT_URL = "/codeTablesExport.htm";
    public static final String CODE_TABLES_REPORT = "codeTablesReport";
    public static final String TISSUE_SOURCE_SITE = "tissueSourceSite";
    public static final String CENTER_CODE = "centerCode";
    public static final String PLATFORM_CODE = "platformCode";
    public static final String CODE = "code";
    public static final String CENTER_TYPE = "centerType";
    public static final String DEFINITION = "definition";
    public static final String STUDY_CODE = "studyCode";
    public static final String STUDY_NAME = "studyName";
    public static final String DISEASE_STUDY = "diseaseStudy";
    public static final String BCR_BATCH_CODE = "bcrBatchCode";

    public static final String CELL_LINE_CONTROL_DISPLAY = "Cell Line Control";

    public static final Map<String, String> CENTER_CODE_COLS = new LinkedHashMap<String, String>() {{
        put(CODE, "Code");
        put("centerName", "Center Name");
        put(CENTER_TYPE, "Center Type");
        put("centerDisplayName", "Display Name");
        put("shortName", "Short Name");
    }};

    public static final Map<String, String> DATA_TYPE_COLS = new LinkedHashMap<String, String>() {{
        put(CENTER_TYPE, "Center Type");
        put("displayName", "Display Name");
        put("ftpDisplay", "FTP Display");
        put("available", "Available");
    }};

    public static final Map<String, String> TUMOR_COLS = new LinkedHashMap<String, String>() {{
        put("tumorName", "Study Abbreviation");
        put("tumorDescription", "Study Name");
    }};

    public static final Map<String, String> PLATFORM_COLS = new LinkedHashMap<String, String>() {{
        put("platformName", "Platform Code");
        put("platformAlias", "Platform Alias");
        put("platformDisplayName", "Platform Name");
        put("available", "Available");
    }};

    public static final Map<String, String> CODE_REPORT_COLS = new LinkedHashMap<String, String>() {{
        put(CODE, "Code");
        put(DEFINITION, "Definition");
    }};

    public static final Map<String, String> SAMPLE_TYPE_COLS = new LinkedHashMap<String, String>() {{
        put("sampleTypeCode", "Code");
        put(DEFINITION, "Definition");
        put("shortLetterCode", "Short Letter Code");
    }};

    public static final Map<String, String> DATA_LEVEL_COLS = new LinkedHashMap<String, String>() {{
        put(CODE, "Level Number");
        put(DEFINITION, "Definition");
    }};

    public static final Map<String, String> TISSUE_COLS = new LinkedHashMap<String, String>() {{
        put(DatareportsCommonConstants.TISSUE, "Tissue");
    }};

    public static final Map<String, String> TISSUE_SOURCE_SITE_COLS = new LinkedHashMap<String, String>() {{
        put(CODE, "TSS Code");
        put(DEFINITION, "Source Site");
        put(STUDY_NAME, "Study Name");
        put(DatareportsCommonConstants.BCR, "BCR");

    }};

    public static final Map<String, String> BCR_BATCH_COLS = new LinkedHashMap<String, String>() {{
        put(DatareportsCommonConstants.BCR_BATCH, "BCR Batch");
        put(STUDY_CODE, "Study Abbreviation");
        put(STUDY_NAME, "Study Name");
        put(DatareportsCommonConstants.BCR, "BCR");
    }};

    public static final String QUERY_TISSUE_SOURCE_SITE = "SELECT tss.tss_code as tss_no, " +
            "tss.tss_definition as site, d.disease_name as study_name, c.short_name as BCR " +
            "FROM   tissue_source_site tss, tss_to_disease td, disease d, center c " +
            "WHERE  tss.tss_code = td.tss_code " +
            "AND    td.disease_id=d.disease_id " +
            "AND    tss.receiving_center_id = c.center_id " +
            "ORDER BY tss.tss_code";

    public static final String QUERY_CENTER = "select bcr_center_id,domain_name,ctbc.center_type_code," +
            "display_name,short_name " +
            "from center c, center_to_bcr_center ctbc " +
            "where c.center_id = ctbc.center_id " +
            "order by bcr_center_id";

    public static final String QUERY_DATA_LEVEL = "select level_number,level_definition from data_level";

    public static final String QUERY_DATA_TYPE = "select center_type_code,name,ftp_display,available " +
            "from data_type " +
            "order by sort_order";

    public static final String QUERY_DISEASE = "select disease_abbreviation,disease_name " +
            "from disease " +
            "order by disease_name";

    public static final String QUERY_DISEASE_ACTIVE = "select disease_abbreviation,disease_name " +
            "from disease where active = 1 " +
            "order by disease_name";

    public static final String QUERY_PLATFORM = "select platform_name,platform_alias,platform_display_name,available " +
            "from platform " +
            "order by sort_order";

    public static final String QUERY_PORTION_ANALYTE = "select portion_analyte_code,definition from portion_analyte " +
            "order by portion_analyte_code";

    public static final String QUERY_SAMPLE_TYPE = "select sample_type_code,definition,short_letter_code from sample_type order by sample_type_code";

    public static final String QUERY_TISSUE = "select tissue from tissue order by tissue_id";

    public static final String QUERY_BCR_BATCH = "select b.batch_id,d.disease_abbreviation, " +
            "d.disease_name as study_name,c.short_name as bcr " +
            "from batch_number_assignment b, disease d, center c " +
            "where b.disease_id=d.disease_id " +
            "and b.center_id=c.center_id " +
            "order by batch_id";

}//End of Class
