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
 * stats dashboard constants
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class StatsDashboardConstants {

    public static final String STATS_DASHBOARD_HOME_VIEW = "statsDashboard";
    public static final String STATS_DASHBOARD_HOME_URL = "/" + STATS_DASHBOARD_HOME_VIEW + ".htm";
    public static final Map<String, String> FILTER_PIE_CHART = new LinkedHashMap<String, String>() {{
        put("batchFilter", "14");
        put("platformTypeFilter", "17");
        put("accessTierFilter", "18");
        put("levelFilter", "16");
    }};

    public static final Map<String, String> FILTER_PIE_CHART_CAPTION = new LinkedHashMap<String, String>() {{
        put("batchFilter", "Most Requested Batches");
        put("platformTypeFilter", "Most Requested Platforms Types");
        put("accessTierFilter", "Most Requested Access Type");
        put("levelFilter", "Most Requested Data Level");
    }};

    public static final String QUERY_NUMBER_ARCHIVE_DOWNLOAD_PER_MONTHYEAR = "SELECT " +
            "TO_CHAR(TO_DATE(NVL(monthyear,0),'MM/YYYY'),'MON-YYYY') " +
            "as monthyear, NVL(downloads,0) as downloads " +
            "FROM (SELECT monthyear, sum(action_total) as downloads " +
            "FROM portal_action_summary " +
            "WHERE action_type_id=34 " +
            "AND disease_abbreviation = ? " +
            "GROUP BY monthyear) " +
            "ORDER BY TO_DATE(NVL(monthyear,0),'MM/YYYY')";

    public static final String QUERY_CUMULATIVE_NUMBER_ARCHIVE_DOWNLOAD_PER_MONTHYEAR = "SELECT " +
            "TO_CHAR(TO_DATE(NVL(monthyear,0),'MM/YYYY'),'MON-YYYY') " +
            "as monthyear,  " +
            "sum(downloads) over (ORDER BY TO_DATE(NVL(monthyear,0),'MM/YYYY') " +
            "rows between unbounded preceding and current row) as cumulative " +
            "FROM (SELECT monthyear, sum(action_total) as downloads " +
            "FROM portal_action_summary " +
            "WHERE action_type_id=34  " +
            "AND disease_abbreviation =? " +
            "GROUP BY monthyear) " +
            "ORDER BY TO_DATE(NVL(monthyear,0),'MM/YYYY')";

    public static final String QUERY_SIZE_ARCHIVE_DOWNLOAD_PER_MONTHYEAR = "SELECT " +
            "TO_CHAR(TO_DATE(NVL(monthyear,0),'MM/YYYY'),'MON-YYYY')  " +
            "as monthyear, NVL(downloads,0) as downloads " +
            "FROM (SELECT monthyear, sum(action_total) as downloads " +
            "FROM portal_action_summary " +
            "WHERE action_type_id=36 " +
            "AND disease_abbreviation = ? " +
            "GROUP BY monthyear) " +
            "ORDER BY TO_DATE(NVL(monthyear,0),'MM/YYYY')";

    public static final String QUERY_CUMULATIVE_SIZE_ARCHIVE_DOWNLOAD_PER_MONTHYEAR = "SELECT " +
            "TO_CHAR(TO_DATE(NVL(monthyear,0),'MM/YYYY'),'MON-YYYY') " +
            "as monthyear,  " +
            "sum(downloads) over (ORDER BY TO_DATE(NVL(monthyear,0),'MM/YYYY') " +
            "rows between unbounded preceding and current row) as cumulative " +
            "FROM (SELECT monthyear, sum(action_total) as downloads " +
            "FROM portal_action_summary " +
            "WHERE action_type_id=36 " +
            "AND disease_abbreviation = ? " +
            "GROUP BY monthyear) " +
            "ORDER BY TO_DATE(NVL(monthyear,0),'MM/YYYY')";

    public static final String QUERY_NUMBER_ARCHIVE_DOWNLOAD_TOTAL_PER_DISEASE = "select " +
            "disease_abbreviation, sum(action_total) " +
            "from portal_action_summary " +
            "where action_type_id=34 " +
            "group by disease_abbreviation " +
            "order by disease_abbreviation";

    public static final String QUERY_SIZE_ARCHIVE_DOWNLOAD_TOTAL_PER_DISEASE = "select " +
            "disease_abbreviation, sum(action_total) " +
            "from portal_action_summary " +
            "where action_type_id=36 " +
            "group by disease_abbreviation " +
            "order by disease_abbreviation";

    public static final String QUERY_ABS_TOTAL_NUMBER_ARCHIVE_DOWNLOAD = "select sum(action_total) " +
            "from portal_action_summary where action_type_id=34";

    public static final String QUERY_ABS_TOTAL_SIZE_ARCHIVE_DOWNLOAD = "select sum(action_total) " +
            "from portal_action_summary where action_type_id=36";

    public static final String QUERY_ABS_TOTAL_NUMBER_ARCHIVE_RECEIVED = "select count(*) from archive_info";

    public static final String QUERY_ABS_TOTAL_SIZE_ARCHIVE_RECEIVED = "select sum(final_size_kb) from archive_info";

    public static final String QUERY_NUMBER_ARCHIVE_RECEIVED_TOTAL_PER_DISEASE = "select " +
            " disease_abbreviation,count(*) " +
            " from archive_info a, disease d " +
            " where a.disease_id=d.disease_id " +
            " group by disease_abbreviation " +
            " order by disease_abbreviation";

    public static final String QUERY_SIZE_ARCHIVE_RECEIVED_TOTAL_PER_DISEASE = "select " +
            " disease_abbreviation,sum(final_size_kb) as total_size_kb " +
            " from archive_info a, disease d " +
            " where a.disease_id=d.disease_id " +
            " group by disease_abbreviation " +
            " order by disease_abbreviation";

    public static final String QUERY_NUMBER_ARCHIVE_RECEIVED_PER_MONTHYEAR = "select " +
            "to_char(trunc(date_added,'MONTH'),'MON/YYYY') as monthyr, count(*) " +
            "from archive_info a, disease d " +
            "where a.disease_id=d.disease_id " +
            "and disease_abbreviation = ? " +
            "group by trunc(date_added,'MONTH') " +
            "order by trunc(date_added,'MONTH')";

    public static final String QUERY_CUMULATIVE_NUMBER_ARCHIVE_RECEIVED_PER_MONTHYEAR = "select " +
            " to_char(trunc(date_added,'MONTH'),'MON/YYYY') as " +
            " monthyr, sum(count(*)) over " +
            " (order by trunc(date_added,'MONTH') rows between unbounded preceding and current row) as cumulative " +
            " from archive_info a, disease d " +
            " where a.disease_id=d.disease_id " +
            " and disease_abbreviation = ? " +
            " group by trunc(date_added,'MONTH') " +
            " order by trunc(date_added,'MONTH')";

    public static final String QUERY_SIZE_ARCHIVE_RECEIVED_PER_MONTHYEAR = "select " +
            "to_char(trunc(date_added,'MONTH'),'MON/YYYY') as monthyr, sum(final_size_kb) " +
            "from archive_info a, disease d " +
            "where a.disease_id=d.disease_id " +
            "and disease_abbreviation = ? " +
            "group by trunc(date_added,'MONTH') " +
            "order by trunc(date_added,'MONTH')";

    public static final String QUERY_CUMULATIVE_SIZE_ARCHIVE_RECEIVED_PER_MONTHYEAR = "select " +
            " to_char(trunc(date_added,'MONTH'),'MON/YYYY') as " +
            " monthyr, sum(sum(final_size_kb)) over " +
            " (order by trunc(date_added,'MONTH') rows between unbounded preceding and current row) as cumulative " +
            " from archive_info a, disease d " +
            " where a.disease_id=d.disease_id " +
            " and disease_abbreviation = ? " +
            " group by trunc(date_added,'MONTH') " +
            " order by trunc(date_added,'MONTH')";

    public static final String QUERY_FILTER_PIE_CHART = "select " +
            "selection, sum(action_total) " +
            "from portal_action_summary " +
            "where action_type_id=? " +
            "group by selection " +
            "order by selection";

    public static final String QUERY_FILTER_BATCH = "select " +
            "selection, sum(action_total) " +
            "from portal_action_summary " +
            "where action_type_id=? " +
            "group by selection " +
            "order by to_number(regexp_substr(selection,'[0-9]+'))";

    public static final String QUERY_FILTER_PIE_CHART_PER_DISEASE = "select " +
            "disease_abbreviation, sum(action_total) " +
            "from portal_action_summary " +
            "where action_type_id=? " +
            "and selection = ? " +
            "group by disease_abbreviation " +
            "order by disease_abbreviation";

    public static final String QUERY_REFRESH_STATS_DASHBOARD_PROC =
            "call build_portal_action_summary.get_summary_details()";

}//End of Class
