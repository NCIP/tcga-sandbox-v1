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
 * AliquotIdBreakdownReport constants
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class AliquotIdBreakdownReportConstants {

    public static final String ALIQUOT_ID_BREAKDOWN_REPORT_VIEW = "aliquotIdBreakdownReport";
    public static final String ALIQUOT_ID_BREAKDOWN_REPORT_URL = "/" + ALIQUOT_ID_BREAKDOWN_REPORT_VIEW + ".htm";
    public static final String ALIQUOT_ID_BREAKDOWN_REPORT_JSON_URL = "/" + ALIQUOT_ID_BREAKDOWN_REPORT_VIEW + ".json";
    public static final String ALIQUOT_ID_BREAKDOWN_EXPORT_URL = "/aliquotIdBreakdownExport.htm";
    public static final String ALIQUOT_ID_BREAKDOWN_FILTER_MODEL = "aliquotIdBreakdownFilterModel";
    public static final String ALIQUOT_ID_BREAKDOWN_DATA = "aliquotIdBreakdownData";
    public static final String EMPTY_ALIQUOT_ID_BREAKDOWN_FILTER = "{\"aliquotId\":\"\"," +
            "\"analyteId\":\"\",\"sampleId\":\"\",\"participantId\":\"\"}";

    public static final Map<String, String> ALIQUOT_ID_BREAKDOWN_COLS = new LinkedHashMap<String, String>() {{
        put(DatareportsCommonConstants.ALIQUOT_ID, "Aliquot ID");
        put(DatareportsCommonConstants.ANALYTE_ID, "Analyte ID");
        put(DatareportsCommonConstants.SAMPLE_ID, "Sample ID");
        put(DatareportsCommonConstants.PARTICIPANT_ID, "Participant ID");
        put(DatareportsCommonConstants.PROJECT, "Project");
        put(CodeTablesReportConstants.TISSUE_SOURCE_SITE, "Tissue Source Site");
        put(DatareportsCommonConstants.PARTICIPANT, "Participant");
        put(DatareportsCommonConstants.SAMPLE_TYPE, "Sample Type");
        put(DatareportsCommonConstants.VIAL_ID, "Vial ID");
        put(DatareportsCommonConstants.PORTION_ID, "Portion ID");
        put(SampleSummaryReportConstants.PORTION_ANALYTE, "Portion Analyte");
        put(DatareportsCommonConstants.PLATE_ID, "Plate ID");
        put(DatareportsCommonConstants.CENTER_ID, "Center Code");
    }};

    public static final String QUERY_ALIQUOT_ID_BREAKDOWN = "select barcode, biospecimen, sample, " +
            "specific_patient, project_code, tss_code, patient, sample_type_code, sample_sequence, " +
            "portion_sequence, portion_analyte_code, plate_id, bcr_center_id " +
            "from shipped_biospecimen_aliquot " +
            "where is_viewable = 1 " +
            "order by barcode";

}//End of Class
