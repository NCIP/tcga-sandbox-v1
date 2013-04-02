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
 * latest generic report constants
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class LatestGenericReportConstants {
    
    public static final String LATEST_ARCHIVE_REPORT_VIEW = "latestArchiveReport";
    public static final String LATEST_ARCHIVE_REPORT_URL = "/" + LATEST_ARCHIVE_REPORT_VIEW + ".htm";
    public static final String LATEST_ARCHIVE_REPORT_JSON_URL = "/" + LATEST_ARCHIVE_REPORT_VIEW + ".json";
    public static final String LATEST_ARCHIVE_EXPORT_URL = "/latestArchiveExport.htm";
    public static final String LATEST_ARCHIVE_FILTER_DATA_URL = "/latestArchiveFilterData.json";
    public static final String ARCHIVE_TYPE = "archiveType";
    public static final String REAL_NAME = "realName";
    public static final String ARCHIVE_NAME = "archiveName";
    public static final String ARCHIVE_URL = "deployLocation";
    public static final String SDRF_NAME = "sdrfName";
    public static final String SDRF_URL = "sdrfUrl";
    public static final String MAF_NAME = "mafName";
    public static final String MAF_URL = "mafUrl";
    public static final String DATE_ADDED = "dateAdded";
    public static final String LATEST_ARCHIVE_FILTER_MODEL = "latestArchiveFilterModel";
    public static final String LATEST_ARCHIVE_DATA = "latestArchiveData";
    public static final String EMPTY_LATEST_ARCHIVE_FILTER = "{\"archiveType\":\"\",\"dateFrom\":\"\"," +
            "\"dateTo\":\"\"}";

    public static final Map<String, String> SDRF_COLS = new LinkedHashMap<String, String>() {{
        put(REAL_NAME, "ARCHIVE_NAME");
        put(DATE_ADDED, "DATE_ADDED");
        put(SDRF_URL, "SDRF_FILE_URL");
    }};

    public static final Map<String, String> MAF_COLS = new LinkedHashMap<String, String>() {{
        put(REAL_NAME, "ARCHIVE_NAME");
        put(DATE_ADDED, "DATE_ADDED");
        put(MAF_URL, "MAF_FILE_URL");
    }};

    public static final Map<String, String> ARCHIVE_COLS = new LinkedHashMap<String, String>() {{
        put(REAL_NAME, "ARCHIVE_NAME");
        put(DATE_ADDED, "DATE_ADDED");
        put(ARCHIVE_URL, "ARCHIVE_URL");
    }};

    public static final Map<String, String> LATEST_ARCHIVE_COLS = new LinkedHashMap<String, String>() {{
        put(ARCHIVE_NAME, "Archive");
        put(DATE_ADDED, "Date Added");
        put(ARCHIVE_TYPE, "Archive Type");
        put(SDRF_NAME, "SDRF File");
        put(MAF_NAME, "MAF File");
    }};

    public static final String QUERY_LATEST_SDRF = "select a.archive_name,a.date_added, fa.file_location_url  " +
            "from archive_info a, archive_type t, file_info f, File_to_Archive fa " +
            "where a.is_latest = 1  " +
            "and a.deploy_status = 'Available'  " +
            "and a.archive_type_id = t.archive_type_id  " +
            "and a.archive_id = fa.archive_id  " +
            "and f.file_name like '%sdrf%'  " +
            "and f.file_id = fa.file_id  " +
            "and t.archive_type = 'mage-tab'  " +
            "order by a.archive_name, a.date_added";

    public static final String QUERY_LATEST_ARCHIVE = "select a.archive_name, a.date_added, a.deploy_location  " +
            "from archive_info a  " +
            "where a.is_latest = 1  " +
            "and a.deploy_status = 'Available'  " +
            "order by a.archive_name, a.date_added";

    public static final String QUERY_LATEST_ARCHIVE_BY_TYPE = "select a.archive_name,a.date_added, " +
            "a.deploy_location  " +
            "from archive_info a, archive_type t  " +
            "where a.is_latest = 1  " +
            "and a.deploy_status = 'Available'  " +
            "and a.archive_type_id = t.ARCHIVE_TYPE_ID  " +
            "and t.archive_type = ?  " +
            "order by a.archive_name,a.date_added";

    public static final String QUERY_LATEST_MAF = "select a.archive_name,a.date_added, fa.file_location_url " +
            "from archive_info a, archive_type t, file_info f, File_to_Archive fa  " +
            "where a.is_latest = 1  " +
            "and a.deploy_status = 'Available'  " +
            "and a.archive_type_id = t.ARCHIVE_TYPE_ID  " +
            "and a.archive_id = fa.archive_id  " +
            "and f.file_name like '%maf%'  " +
            "and f.file_id = fa.file_id  " +
            "order by a.archive_name,a.date_added";

    public static final String QUERY_LATEST_COMBINED = "select distinct a.archive_name,a.date_added, a.deploy_location, t.archive_type,  COALESCE(sdrf2.file_name,sdrf1.file_name) as sdrf_file_name, " +
            "COALESCE(sdrf2.file_location_url, sdrf1.file_location_url) as sdrf_file_location_url,  maf.file_name as maf_file_name, maf.file_location_url as maf_file_location_url  " +
            "FROM archive_type t, archive_info a " +
            "LEFT OUTER JOIN( " +
            "select a.archive_name,a.date_added,f.file_name,fa.file_location_url , a.center_id, a.platform_id, a.disease_id  " +
            "from archive_info a, file_info f, File_to_Archive fa  " +
            "where a.is_latest = 1  " +
            "and a.archive_id = fa.archive_id  " +
            "and f.file_name like '%.maf'  " +
            "and f.file_id = fa.file_id  " +
            "order by 1,2" +
            ") maf  " +
            "ON a.center_id = maf.center_id and a.platform_id=maf.platform_id and a.disease_id = maf.disease_id " +
            "LEFT OUTER JOIN(select a.archive_name,a.date_added,f.file_name,fa.file_location_url , a.center_id, a.platform_id, a.disease_id  " +
            "from archive_info a, archive_type t, file_info f, File_to_Archive fa  " +
            "where a.is_latest = 1  " +
            "and a.archive_type_id = t.archive_type_id  " +
            "and a.archive_id = fa.archive_id  " +
            "and f.file_name like '%sdrf%'  " +
            "and f.file_id = fa.file_id  " +
            "and t.archive_type = 'mage-tab'  " +
            "order by 1,2" +
            ") sdrf2  " +
            "ON a.center_id = sdrf2.center_id and a.platform_id=sdrf2.platform_id and a.disease_id=sdrf2.disease_id " +
            "LEFT OUTER JOIN (select a.archive_name,a.date_added,a.deploy_location,f.file_name,fa.file_location_url , a.center_id, a.platform_id, a.disease_id  " +
            "from archive_info a, file_info f, File_to_Archive fa   " +
            "where a.is_latest = 1  " +
            "and a.archive_name not like '%mage-tab%'  " +
            "and a.archive_id = fa.archive_id  " +
            "and f.file_name like '%sdrf%'  " +
            "and f.file_id = fa.file_id  " +
            "order by 1,2 " +
            ") sdrf1  " +
            "ON a.center_id = sdrf1.center_id and a.platform_id=sdrf1.platform_id and a.disease_id=sdrf1.disease_id " +
            "and a.archive_name=sdrf1.archive_name  " +
            "where a.is_latest = 1  " +
            "and a.deploy_status = 'Available'  " +
            "and a.archive_type_id = t.archive_type_id " +
            "order by a.archive_name";

    public static final String QUERY_ARCHIVE_TYPE = "select type from archive_type order by type";

}//End of Class
