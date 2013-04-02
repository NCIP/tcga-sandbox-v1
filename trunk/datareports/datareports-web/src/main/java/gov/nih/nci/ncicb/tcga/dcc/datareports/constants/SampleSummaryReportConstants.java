/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.constants;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.ExtJsFilter;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * constants for the sample summary report
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class SampleSummaryReportConstants {

    /**
     * data file to load for this test *
     */
    public static final String SAMPLE_SUMMARY_DB_FILE = "SampleSummary_TestDB.xml";
    public static final String SS_URL = "/datareports/sampleSummaryReport.htm";
    public static final String SAMPLE_SUMMARY_REPORT_VIEW = "sampleSummaryReport";
    public static final String SAMPLE_DETAILED_REPORT_VIEW = "sampleDetailedReport";
    public static final String SAMPLE_SUMMARY_REPORT_URL = "/" + SAMPLE_SUMMARY_REPORT_VIEW + ".htm";
    public static final String SAMPLE_DETAILED_EXPORT_URL = "/sampleDetailedExport.htm";
    public static final String SAMPLE_SUMMARY_EXPORT_URL = "/sampleSummaryExport.htm";
    public static final String SAMPLE_SUMMARY_REPORT_JSON_URL = "/" + SAMPLE_SUMMARY_REPORT_VIEW + ".json";
    public static final String SAMPLE_DETAILED_JSON_URL = "/" + SAMPLE_DETAILED_REPORT_VIEW + ".json";
    public static final String SAMPLE_SUMMARY_FILTER_DATA_URL = "/sampleSummaryFilterData.json";
    public static final String LAST_REFRESH = "last_refresh";
    public static final String CENTER_EMAIL = "centerEmail";
    public static final String PORTION_ANALYTE = "portionAnalyte";
    public static final String LEVEL2_SS = "totalLevelTwo";
    public static final String LEVEL3_SS = "totalLevelThree";
    public static final String LEVEL4_SS = "levelFourSubmitted";
    public static final String BCR_SENT = "totalBCRSent";
    public static final String CENTER_SENT = "totalCenterSent";
    public static final String BCR_UNKNOWN = "totalBCRUnaccountedFor";
    public static final String CENTER_UNKNOWN = "totalCenterUnaccountedFor";
    public static final String EMAIL_NOTE1 = "<div align='left'><span>A value of <quote>Undetermined</quote> " +
            "for Platform indicates that the DCC has not received data for the indicated sample-analytes<br/>" +
            "<span style='color: red;'>* = Although not in the latest current archive, level 4 data has been " +
            "submitted</span><br/></span><br /></div>";
    public static final String EMAIL_NOTE2 = "<div align='left'><span>The most up-to-date information is always" +
            " available as a dynamic web report.";
    public static final String QUESTIONS_COMMENTS = "If you have any questions or comments about " +
            "this email or the reports listed here, please contact the DCC via the contact listed below.</span></div>";
    public static final String REPORT_DISCLAIMER = "Disclaimer: The above table(s) reflect as accurately as possible the sample IDs and ID annotations submitted to the DCC to " +
            "date. At the present time, the intended platform for a given aliquot can only be inferred from the disease and the identity " +
            "of the GSC/CGCC that is encoded in the aliquot ID. If a given GSC/CGCC is using only one platform for a given disease, " +
            "then this inference will be accurate. If the GSC/CGCC is using more than one platform for a disease, the DCC cannot " +
            "accurately report ID counts per platform per disease. Of course, once a GSC/CGCC submits molecular data for an aliquot " +
            "to the DCC, then the platform is known. In Phase 2 of the TCGA project there will be a standard operating procedure for a " +
            "GSC/CGCC to report the intended platform for a given aliquot before submitting the molecular data.";
    public static final String SAMPLE_SUMMARY_FILTER_MODEL = "sampleSummaryFilterModel";
    public static final String SAMPLE_SUMMARY_DATA = "sampleSummaryData";
    public static final String EMPTY_SAMPLE_SUMMARY_FILTER = "\t{\"disease\":\"\",\"center\":\"\"," +
            "\"portionAnalyte\":\"\",\"platform\":\"\",\"levelFourSubmitted\":\"\"}";
    public static final String LEVEL1_SS = "totalLevelOne";
    public static final Map<String, String> SAMPLE_SUMMARY_COLS = new LinkedHashMap<String, String>() {{
        put(DatareportsCommonConstants.DISEASE, "Disease");
        put(DatareportsCommonConstants.CENTER, "Center");
        put(PORTION_ANALYTE, "Portion Analyte");
        put(DatareportsCommonConstants.PLATFORM, "Platform");
        put(BCR_SENT, "Sample IDs BCR Reported Sending to Center");
        put(CENTER_SENT, "Sample IDs DCC Received from Center");
        put(BCR_UNKNOWN, "Unaccounted for BCR Sample IDs that Center Reported");
        put(CENTER_UNKNOWN, "Unaccounted for Center Sample IDs that BCR Reported");
        put(LEVEL1_SS, "Sample IDs with Level 1 Data");
        put(LEVEL2_SS, "Sample IDs with Level 2 Data");
        put(LEVEL3_SS, "Sample IDs with Level 3 Data");
        put(LEVEL4_SS, "Level 4 Submitted (Y/N)");
    }};

    public static final List<ExtJsFilter> SS_LEVEL_LIST = new LinkedList<ExtJsFilter>() {{
        add(new ExtJsFilter("Y", "Y"));
        add(new ExtJsFilter("Y*", "Y*"));
        add(new ExtJsFilter("N", "N"));
    }};
    /**
     * This "query" is a procedure call to refresh the sample_summary_report table which can occur as frequently
     * as nightly
     */
    public static final String QUERY_REFRESH_SAMPLE_SUMMARY_TABLE =
            "call build_sample_summary_report()";
    /**
     * This query returns a partial result set for the Sample Summary table on the DataSummary.htm page in the DCC
     * reports. </p> To get the columns in the right order select * from data_summary_report_detail order by
     * center_name,center_type,portion_analyte
     */
    public static final String QUERY_SAMPLE_SUMMARY_FOR_DISEASE_ABBR =
            "select * from sample_summary_report_detail " +
                    "where disease_abbreviation = ? " +
                    "order by center_name, center_type_code, portion_analyte_code";
    /**
     * This query returns the entire result set for the Sample Summary table on the DataSummary.htm page in the DCC
     * reports. </p> To get the columns in the right order select * from data_summary_report_detail order by
     * center_name,center_type,portion_analyte
     */
    public static final String QUERY_SAMPLE_SUMMARY =
            "select * from sample_summary_report_detail " +
                    "order by disease_abbreviation, center_name, center_type_code, portion_analyte_code";
    /**
     * This query returns the basic information needed to email the Sample Summary report to each center
     */
    public static final String QUERY_CENTER_EMAIL_INFO =
            "select c.domain_name, c.center_type_code, ce.email_address, c.display_name " +
                    "from center c, center_email ce where c.center_id = ce.center_id " +
                    "and center_type_code <> 'BCR';";

    public static final String TOTAL_BCR_SENT = "total_bcr_sent";
    public static final String TOTAL_CENTERS_SENT = "total_centers_sent";
    public static final String TOTAL_BCR_UNACCOUNTED = "total_bcr_unaccounted";
    public static final String TOTAL_CENTER_UNACCOUNTED = "total_center_unaccounted";
    public static final String TOTAL_WITH_LEVEL1 = "total_with_level1";
    public static final String TOTAL_WITH_LEVEL2 = "total_with_level2";
    public static final String TOTAL_WITH_LEVEL3 = "total_with_level3";

    public static final String QUERY_SAMPLE_IDS_BCR_REPORTED_SENDING_TO_CENTER_QUERY =
            "SELECT sample, " +
                    "RTRIM(XMLAGG(XMLELEMENT(e, to_char(ship_date,'YYYY/MM/DD') || DECODE(ship_date,null,'',', ')) " +
                    "ORDER BY ship_date DESC).EXTRACT('//text()').getCLOBVal(),', ') ship_date  " +
                    "FROM samples_sent_by_bcr " +
                    "WHERE disease_abbreviation = ? " +
                    "AND center_name = ? " +
                    "AND center_type_code = ? " +
                    "AND portion_analyte_code = ? " +
                    "GROUP BY sample";

    public static final String QUERY_SAMPLE_IDS_DCC_RECEIVED_FROM_CENTER =
            "SELECT sample, " +
                    "RTRIM(XMLAGG(XMLELEMENT(e, to_char(date_received,'YYYY/MM/DD') || ', ') " +
                    "ORDER BY date_received DESC).EXTRACT('//text()').getCLOBVal(),', ') date_received   " +
                    "FROM latest_samples_received_by_dcc " +
                    "WHERE disease_abbreviation = ? " +
                    "AND center_name = ? " +
                    "AND center_type_code = ? " +
                    "AND portion_analyte_code = ? " +
                    "AND platform = ? " +
                    "GROUP BY sample";

    public static final String QUERY_UNACCOUNTED_FOR_BCR_SAMPLE_IDS_THAT_CENTER_REPORTED =
            "SELECT sample, " +
                    "RTRIM(XMLAGG(XMLELEMENT(e, to_char(date_received,'YYYY/MM/DD') || ', ') " +
                    "ORDER BY date_received DESC).EXTRACT('//text()').getCLOBVal(),', ') date_received  " +
                    "FROM   latest_samples_received_by_dcc " +
                    "WHERE disease_abbreviation = ? " +
                    "AND      center_name = ? " +
                    "AND      center_type_code = ? " +
                    "AND      portion_analyte_code = ? " +
                    "AND      platform = ? " +
                    "and sample not in ( " +
                    "       SELECT  sample " +
                    "        FROM    samples_sent_by_bcr " +
                    "        WHERE  disease_abbreviation = ? " +
                    "        AND    center_name = ? " +
                    "        AND    center_type_code = ? " +
                    "        AND    portion_analyte_code = ? " +
                    "        ) " +
                    "GROUP BY sample";

    public static final String QUERY_UNACCOUNTED_FOR_CENTER_SAMPLE_IDS_THAT_BCR_REPORTED =
            "SELECT sample, " +
                    "RTRIM(XMLAGG(XMLELEMENT(e, to_char(ship_date,'YYYY/MM/DD') || DECODE(ship_date,null,'',', ')) " +
                    "ORDER BY ship_date DESC).EXTRACT('//text()').getCLOBVal(),', ') ship_date  " +
                    "from samples_sent_by_bcr  " +
                    "WHERE disease_abbreviation = ? " +
                    "AND center_name = ? " +
                    "AND center_type_code = ? " +
                    "AND portion_analyte_code = ? " +
                    "AND  sample not in( " +
                    "            SELECT sample " +
                    "            FROM   latest_samples_received_by_dcc " +
                    "            WHERE disease_abbreviation = ? " +
                    "            AND      center_name = ? " +
                    "            AND      center_type_code = ? " +
                    "            AND      portion_analyte_code = ? " +
                    "            AND      platform = ? " +
                    "            ) " +
                    "GROUP BY sample";

    public static final String QUERY_SAMPLE_IDS_WITH_LEVEL_X_DATA =
            "SELECT bb.sample, " +
                    "RTRIM(XMLAGG(XMLELEMENT(e, to_char(a.date_added,'YYYY/MM/DD') || ', ') " +
                    "ORDER BY a.date_added DESC).EXTRACT('//text()').getCLOBVal(),', ') date_added  " +
                    "FROM center c, archive_info a, platform p, file_info f, center_to_bcr_center cb, " +
                    "shipped_biospecimen_file bf, shipped_biospecimen_aliquot bb, disease d, file_to_archive fa " +
                    "WHERE c.center_id = a.center_id " +
                    "AND   c.center_id = cb.center_id " +
                    "AND   d.disease_abbreviation = ? " +
                    "AND   c.domain_name = ? " +
                    "AND   cb.center_type_code = ? " +
                    "AND   bb.portion_analyte_code = ? " +
                    "AND   bb.is_viewable = 1 " +
                    "AND   p.platform_alias = ? " +
                    "AND   f.level_number = ? " +
                    "AND   a.platform_id = p.platform_id " +
                    "AND   f.file_id = fa.file_id " +
                    "AND   a.archive_id = fa.archive_id " +
                    "AND   f.file_id = bf.file_id " +
                    "AND   a.disease_id = d.disease_id " +
                    "AND   bf.shipped_biospecimen_id = bb.shipped_biospecimen_id " +
                    "AND   a.is_latest = 1 " +
                    "AND   a.deploy_status = 'Available' " +
                    "GROUP BY bb.sample";

    public static final String QUERY_TOTAL_SAMPLES_GSC_SENT = "SELECT count(distinct bb.sample) as samples, " +
            "d.disease_abbreviation, c.center_id, c.domain_name as center_name, " +
            "cb.center_type_code as center_type, p.platform_alias as platform, " +
            "bb.portion_analyte_code as portion_analyte " +
            "FROM archive_info a, center c, platform p, disease d, file_info f, file_to_archive fa, " +
            "center_to_bcr_center cb, shipped_biospecimen_file bf, shipped_biospecimen_aliquot bb " +
            "WHERE a.is_latest = 1  " +
            "AND   a.deploy_status = 'Available' " +
            "AND   a.center_id = c.center_id " +
            "AND   a.platform_id = p.platform_id " +
            "AND   a.disease_id = d.disease_id " +
            "AND   f.file_id = fa.file_id " +
            "AND   a.archive_id = fa.archive_id " +
            "AND   f.file_id = bf.file_id " +
            "AND   bf.shipped_biospecimen_id = bb.shipped_biospecimen_id " +
            "AND   bb.is_viewable = 1 " +
            "AND   bb.bcr_center_id = cb.bcr_center_id " +
            "GROUP BY d.disease_abbreviation, c.center_id, c.domain_name, cb.center_type_code, " +
            "p.platform_alias, bb.portion_analyte_code " +
            "order by domain_name";

    public static final String QUERY_TOTAL_SAMPLES_CGCC_SENT = "SELECT count(distinct bb.sample) as samples, " +
            "d.disease_abbreviation, c.center_id, c.domain_name as center_name, " +
            "cb.center_type_code as center_type, p.platform_alias as platform, " +
            "bb.portion_analyte_code as portion_analyte " +
            "FROM archive_info a, center c, platform p, disease d, file_info f, file_to_archive fa, " +
            "center_to_bcr_center cb, shipped_biospecimen_file bf, shipped_biospecimen_aliquot bb " +
            "WHERE a.is_latest = 1  " +
            "AND   a.deploy_status = 'Available' " +
            "AND   a.center_id = c.center_id " +
            "AND   a.platform_id = p.platform_id " +
            "AND   a.disease_id = d.disease_id " +
            "AND   f.file_id = fa.file_id " +
            "AND   a.archive_id = fa.archive_id " +
            "AND   f.file_id = bf.file_id " +
            "AND   bf.shipped_biospecimen_id = bb.shipped_biospecimen_id " +
            "AND   bb.is_viewable = 1 " +
            "AND   bb.bcr_center_id = cb.bcr_center_id " +
            "GROUP BY d.disease_abbreviation, c.center_id , c.domain_name, cb.center_type_code, " +
            "p.platform_alias , bb.portion_analyte_code " +
            "order by domain_name";

    public static final String QUERY_TOTAL_SAMPLES_BCR_SENT = "SELECT count(distinct bb.sample) as samples, " +
            "d.disease_abbreviation, c.center_id, c.domain_name as center_name, " +
            "cb.center_type_code as center_type, bb.portion_analyte_code as portion_analyte " +
            "FROM shipped_biospecimen_aliquot bb, shipped_biospec_bcr_archive ba, Archive_info a, " +
            "disease d, center c, center_to_bcr_center cb  " +
            "WHERE c.center_id = cb.center_id  " +
            "AND cb.bcr_center_id = bb.bcr_center_id " +
            "AND bb.shipped_biospecimen_id = ba.shipped_biospecimen_id " +
            "AND ba.archive_id = a.archive_id " +
            "AND a.disease_id = d.disease_id " +
            "AND a.is_latest = 1 " +
            "AND bb.is_viewable = 1 " +
            "AND a.deploy_status = 'Available' " +
            "GROUP BY d.disease_abbreviation,c.center_id ,c.domain_name,cb.center_type_code," +
            "bb.portion_analyte_code " +
            "order by domain_name";

    public static final Map<String, String> SAMPLE_BCR_COLS = new LinkedHashMap<String, String>() {{
        put("name", "Samples");
        put("sampleDate", "Ship Date");
    }};

    public static final Map<String, String> SAMPLE_CENTER_COLS = new LinkedHashMap<String, String>() {{
        put("name", "Samples");
        put("sampleDate", "Date Received");
    }};

}//End of Class

