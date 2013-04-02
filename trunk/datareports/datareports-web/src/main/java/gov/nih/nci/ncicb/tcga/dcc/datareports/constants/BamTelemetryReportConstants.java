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
 * bam telemetry constants
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BamTelemetryReportConstants {

    public static final String BAM_TELEMETRY_REPORT_VIEW = "bamTelemetryReport";
    public static final String BAM_TELEMETRY_REPORT_URL = "/" + BAM_TELEMETRY_REPORT_VIEW + ".htm";
    public static final String BAM_TELEMETRY_REPORT_JSON_URL = "/" + BAM_TELEMETRY_REPORT_VIEW + ".json";
    public static final String BAM_TELEMETRY_EXPORT_URL = "/bamTelemetryExport.htm";
    public static final String BAM_TELEMETRY_FILTER_DATA_URL = "/bamTelemetryFilterData.json";
    public static final String BAM_TELEMETRY_FILTER_MODEL = "bamTelemetryFilterModel";
    public static final String BAM_TELEMETRY_DATA = "bamTelemetryData";
    public static final String EMPTY_BAM_TELEMETRY_FILTER = "";
    public static final String DATE_RECEIVED = "dateReceived";
    public static final String BAM_FILE = "bamFile";

    public static final Map<String, String> BAM_TELEMETRY_COLS = new LinkedHashMap<String, String>() {{

        put(DatareportsCommonConstants.DISEASE, "Disease");
        put(DatareportsCommonConstants.CENTER, "Center");
        put(DATE_RECEIVED, "Date Received");
        put(BAM_FILE, "BAM File");
        put(DatareportsCommonConstants.ALIQUOT_ID, "Aliquot ID");
        put(DatareportsCommonConstants.PARTICIPANT_ID, "Participant ID");
        put(DatareportsCommonConstants.SAMPLE_ID, "Sample ID");
        put(DatareportsCommonConstants.MOLECULE, "Molecule Type");
        put(DatareportsCommonConstants.DATA_TYPE, "Data Type");
        put(DatareportsCommonConstants.FILE_SIZE, "File Size");
        put(DatareportsCommonConstants.ALIQUOT_UUID, "UUID");
    }};

    public static final String QUERY_BAM_TELEMETRY_LIST = "select bam_file_name," +
            "disease_abbreviation,domain_name," +
            "bam_file_size,date_received,general_datatype," +
            "molecule,built_barcode,center_type_code,uuid " +
            "from bam_file bf, shipped_biospecimen sb, " +
            "bam_file_datatype bfd, shipped_biospecimen_bamfile sbb, " +
            "center c, disease d " +
            "where bf.bam_datatype_id = bfd.bam_datatype_id " +
            "and bf.disease_id = d.disease_id " +
            "and bf.center_id = c.center_id " +
            "and bf.bam_file_id = sbb.bam_file_id " +
            "and sbb.shipped_biospecimen_id = sb.shipped_biospecimen_id " +
            "order by date_received desc";

}//End of Class
