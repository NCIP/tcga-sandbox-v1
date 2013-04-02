/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
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
 * Constants for the pending uuid report
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class PendingUUIDReportConstants {

    public static final String PENDING_UUID_REPORT_VIEW = "shipped-items-pending-bcr-data-submission";
    public static final String PENDING_UUID_REPORT_URL = "/" + PENDING_UUID_REPORT_VIEW + ".htm";
    public static final String PENDING_UUID_REPORT_JSON_URL = "/pendingUUIDReport.json";
    public static final String PENDING_UUID_REPORT_FILTER_DATA_URL = "/pendingUUIDFilterData.json";
    public static final String PENDING_UUID_REPORT_EXPORT_URL = "/pendingUUIDExport.htm";
    public static final String PENDING_UUID_REPORT_FILTER_MODEL = "pendingUUIDFilterModel";
    public static final String PENDING_UUID_REPORT_DATA = "pendingUUIDData";
    public static final String EMPTY_PENDING_UUID_REPORT_FILTER = "{\"bcr\":\"\"," +
            "\"batch\":\"\",\"center\":\"\",\"plateId\":\"\"}";

    public static final Map<String, String> PENDING_UUID_REPORT_COLS = new LinkedHashMap<String, String>() {{
        put(DatareportsCommonConstants.BCR, "BCR");
        put(DatareportsCommonConstants.CENTER, "Center");
        put("shippedDate", "Date Shipped");
        put(DatareportsCommonConstants.PLATE_ID, "Plate Id");
        put("batchNumber", "Batch");
        put("plateCoordinate", "Plate Coordinate");
        put("uuid", "UUID");
        put("bcrAliquotBarcode", "Barcode");
        put("sampleType", "Sample Type");
        put("analyteType", "Analyte Type");
        put("portionNumber", "Portion");
        put("vialNumber", "Vial");
        put("itemType", "Item Type");
    }};
}
