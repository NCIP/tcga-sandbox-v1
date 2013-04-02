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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * aliquot report constants
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class AliquotReportConstants {

    public static final String ALIQUOT_REPORT_VIEW = "aliquotReport";
    public static final String ALIQUOT_ARCHIVE_VIEW = "aliquotArchive";
    public static final String ALIQUOT_REPORT_URL = "/" + ALIQUOT_REPORT_VIEW + ".htm";
    public static final String ALIQUOT_REPORT_JSON_URL = "/" + ALIQUOT_REPORT_VIEW + ".json";
    public static final String ALIQUOT_ARCHIVE_JSON_URL = "/" + ALIQUOT_ARCHIVE_VIEW + ".json";
    public static final String ALIQUOT_EXPORT_URL = "/aliquotExport.htm";
    public static final String ALIQUOT_FILTER_DATA_URL = "/aliquotFilterData.json";
    public static final String LEVEL_ONE = "levelOne";
    public static final String LEVEL_TWO = "levelTwo";
    public static final String LEVEL_THREE = "levelThree";
    public static final String ALIQUOT_FILTER_MODEL = "aliquotFilterModel";
    public static final String ALIQUOT_DATA = "aliquotData";
    public static final String EMPTY_ALIQUOT_FILTER = "{\"disease\":\"\",\"levelOne\":\"\"," +
            "\"aliquotId\":\"\",\"center\":\"\",\"levelTwo\":\"\",\"bcrBatch\":\"\"," +
            "\"platform\":\"\",\"levelThree\":\"\"}";

    public static final Map<String, String> ALIQUOT_COLS = new LinkedHashMap<String, String>() {{
        put(DatareportsCommonConstants.ALIQUOT_ID, "Aliquot ID");
        put(DatareportsCommonConstants.DISEASE, "Disease");
        put(DatareportsCommonConstants.BCR_BATCH, "BCR Batch");
        put(DatareportsCommonConstants.CENTER, "Receiving Center");
        put(DatareportsCommonConstants.PLATFORM, "Platform");
        put(LEVEL_ONE, "Level 1 Data");
        put(LEVEL_TWO, "Level 2 Data");
        put(LEVEL_THREE, "Level 3 Data");
    }};

    public static final String QUERY_ALIQUOT_ARCHIVES_FILES = "SELECT DISTINCT a.archive_id, a.archive_name, " +
            "f.file_id, f.file_name, fa.file_location_url " +
            "FROM shipped_biospecimen bb , shipped_biospecimen_file bf,   file_info f, file_to_archive fa, archive_info a " +
            "WHERE bb.built_barcode = ? " +
            "AND   bb.shipped_biospecimen_id = bf.shipped_biospecimen_id " +
            "AND   bf.file_id = f.file_id " +
            "AND   f.level_number = ? " +
            "AND  f.file_id = fa.file_id " +
            "AND  fa.archive_id = a.archive_id " +
            "AND   a.is_latest=1 ORDER BY a.archive_id";

    public static final String QUERY_ALIQUOT_LIST = "SELECT centers.disease_abbreviation,centers.barcode,bcr.batch_number,centers.center,centers.platform as platform_alias,centers.level1," +
            "centers.level2,centers.level3 " +
            "FROM " +
            "(SELECT biospecimen_id,disease_abbreviation, barcode, " +
            "center_name||chr(32)||'('||center_type_code||')' as center, platform,  " +
            "DECODE(submit_level1,'Y','Submitted','Not Submitted') level1,DECODE(submit_level2,'Y','Submitted','Not Submitted') level2," +
            "DECODE (submit_level3,'Y','Submitted','Not Submitted') level3  " +
            "FROM latest_samples_received_by_dcc) centers  " +
            " LEFT OUTER JOIN  " +
            "(SELECT biospecimen_id, disease_abbreviation, barcode, batch_number," +
            "center_name||chr(32)||'('||center_type_code||')' as center " +
            "FROM samples_sent_by_bcr) bcr ON centers.biospecimen_id = bcr.biospecimen_id  " +
            " UNION " +
            "SELECT nvl(centers.disease_abbreviation,bcr.disease_abbreviation) as disease_abbreviation,nvl(centers.barcode,bcr.barcode) as barcode," +
            "bcr.batch_number,nvl(centers.center,bcr.center) as center," +
            "nvl(centers.platform,'bio') as platform_alias,nvl(centers.level1,'Not Submitted') as level1," +
            "nvl(centers.level2,'Not Submitted') as level2,nvl(centers.level3,'Not Submitted') as level3 " +
            " FROM " +
            "(SELECT biospecimen_id,disease_abbreviation, barcode, " +
            "center_name||chr(32)||'('||center_type_code||')' as center, platform,  " +
            "DECODE(submit_level1,'Y','Submitted','Not Submitted') level1,DECODE(submit_level2,'Y','Submitted','Not Submitted') level2," +
            "DECODE(submit_level3,'Y','Submitted','Not Submitted') level3  " +
            " FROM latest_samples_received_by_dcc) centers  " +
            " RIGHT OUTER JOIN  " +
            "(SELECT biospecimen_id, disease_abbreviation, barcode, batch_number," +
            "center_name||chr(32)||'('||center_type_code||')' as center" +
            " FROM samples_sent_by_bcr) bcr ON centers.biospecimen_id = bcr.biospecimen_id";

    public static final List<ExtJsFilter> ALI_LEVEL_LIST = new LinkedList<ExtJsFilter>() {{
        add(new ExtJsFilter("Submitted", "Submitted"));
        add(new ExtJsFilter("Not Submitted", "Not Submitted"));
    }};

}//End of Class
