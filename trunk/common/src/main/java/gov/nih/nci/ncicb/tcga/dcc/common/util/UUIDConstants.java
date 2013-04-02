/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Constants used in UUID Application
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

public class UUIDConstants {
    public static final String EXPORT_TYPE = "exportType";
    public static final String EXPORT_TITLE = "title";
    public static final String DATA = "data";
    public static final String EXPORT_FILENAME = "fileName";
    public static final String COLS = "cols";
    public static final String EXPORT_DATE_FORMAT = "dateFormat";
    public static final String CSV = "csv";
    public static final String TAB = "tab";
    public static final String EXCEL = "xl";
    public static final String EXPORT_UUID = "uuidExport";

    public static final String COLUMN_UUID = "uuid";
    public static final String COLUMN_CENTER_NAME = "center";
    public static final String COLUMN_CREATED_BY = "createdBy";
    public static final String COLUMN_GENERATION_METHOD = "generationMethod";
    public static final String COLUMN_CREATION_DATE = "creationDate";
    public static final String COLUMN_LATEST_BARCODE = "latestBarcode";
    public static final String COLUMN_DISEASE = "diseaseAbbrev";
    public static final String SORT = "sort";
    public static final String DIR = "dir";
    public static final String JSON = "/json";
    public static final String XML = "/xml";
    public static final String SEPARATOR = ",";

    public static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    public enum GenerationMethod {

        Web(1, "Web"),
        Rest(2, "Rest"),
        Upload(3, "Upload"),
        API(4, "API");

        private int methodNumber;
        private String displayValue;


        GenerationMethod(final int methodNumber, final String displayValue) {
            this.methodNumber = methodNumber;
            this.displayValue = displayValue;
        }

        public int getMethodNumber() {
            return methodNumber;
        }

        public String getDisplayValue() {
            return displayValue;
        }

        @Override
        public String toString() {
            return displayValue;
        }

    }

    public static GenerationMethod getGenerationMethod(final int generationMethod) {
        for (final GenerationMethod method : GenerationMethod.values()) {
            if ((method.getMethodNumber() == generationMethod)) {
                return method;
            }
        }
        return null;
    }

    public static final int DEFAULT_START = 0;
    public static final int DEFAULT_LIMIT = 25;

    public static final String REPORT_TYPE_NEW_UUID = "newUUID";
    public static final String REPORT_TYPE_SUBMITTED_UUID = "submittedUUID";
    public static final String REPORT_TYPE_MISSING_UUID = "missingUUID";

    public static final String SEARCH_CRITERIA = "searchCriteria";
    public static final String SEARCH_RESULTS = "searchResults";
    public static final String REPORT_RESULTS = "reportResults";
    public static final String EXPORT_DATA = "exportData";

    public static final String TOTAL_COUNT = "totalCount";
    public static final String SUCCESS = "success";
    public static final String ERROR_MESSAGE = "errorMessage";

    public static final String ASC = "ASC";
    public static final String DESC = "DESC";

    public static final String DATE_FORMAT = "MM/dd/yyyy";

    // This is the user name, representing an admin user, used when the UUIDs are generated from qclive
    public static final String MASTER_USER = "DCC";
    public static final String CENTER_USER = "center_user";

    public static final String UUID_BROWSER_VIEW = "uuidBrowser";
    public static final String UUID_BROWSER_UPLOAD_VIEW = "uuidUploadStatus";
    public static final String UUID_BROWSER_URL = "/" + UUID_BROWSER_VIEW + ".htm";
    public static final String UUID_BROWSER_UPLOAD_URL = "/" + UUID_BROWSER_UPLOAD_VIEW + ".htm";
    public static final String UUID_BROWSER_JSON_URL = "/" + UUID_BROWSER_VIEW + ".json";
    public static final String UUID_PARENT_JSON_URL = "/uuidParent.json";
    public static final String UUID_BROWSER_FILTER_DATA_URL = "/uuidBrowserFilterData.json";
    public static final String UUID_BROWSER_EXPORT_URL = "/uuidBrowserExport.htm";
    public static final String BCR = "bcr";
    public static final String TSS = "tss";
    public static final String CENTER = "center";
    public static final String UUID = "uuid";
    public static final String UUID_TYPE = "uuidType";
    public static final String ELEMENT_TYPE = "elementType";
    public static final String BARCODE = "barcode";
    public static final String DISEASE = "disease";
    public static final String PLATFORM = "platform";
    public static final String SAMPLE_TYPE = "sampleType";
    public static final String ANALYTE_TYPE = "analyteType";
    public static final String RECEIVING_CENTER = "receivingCenter";
    public static final String TISSUE_SOURCE_SITE = "tissueSourceSite";
    public static final String PARTICIPANT_ID = "participantId";
    public static final String FILTER = "filter";
    public static final String CENTER_TYPE = "centerType";
    public static final String COLUMN = "column";
    public static final String PARTICIPANT = "participant";
    public static final String DRUG = "drug";
    public static final String EXAMINATION = "examination";
    public static final String SURGERY = "surgery";
    public static final String RADIATION = "radiation";
    public static final String SAMPLE = "sample";
    public static final String ANALYTE = "analyte";
    public static final String ALIQUOT = "aliquot";
    public static final String PORTION = "portion";
    public static final String SHIPPED_PORTION = "shippedPortion";
    public static final String PLATE_ID = "plateId";
    public static final String VIAL_ID = "vialId";
    public static final String PORTION_ID = "portionId";
    public static final String SLIDE = "slide";
    public static final String SLIDE_LAYER = "slideLayer";
    public static final String START = "start";
    public static final String LIMIT = "limit";
    public static final String FILE = "file";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String ABBREVIATION = "abbreviation";
    public static final String STUDY_NAME = "study Name";
    public static final String TYPE = "type";
    public static final String CODE = "code";
    public static final String SHORT_LETTER_CODE = "short Letter Code";
    public static final String NAME = "name";
    public static final String DOMAIN = "domain";
    public static final String UUID_LIST = "uuidList";
    public static final String UUID_FIELD = "uuidField";
    public static final String BARCODE_FIELD = "barcodeField";
    public static final String SEARCH_PARAMS = "searchParams";
    public static final String UUID_TYPE_COMBO = "uuidTypeCombo";
    public static final String PLATFORM_COMBO = "platformCombo";
    public static final String DISEASE_COMBO = "diseaseCombo";
    public static final String SAMPLE_COMBO = "sampleCombo";
    public static final String ANALYTE_COMBO = "analyteCombo";
    public static final String BCR_COMBO = "bcrCombo";
    public static final String CENTER_COMBO = "centerCombo";
    public static final String CENTER_TYPE_COMBO = "centerTypeCombo";
    public static final String BATCH = "batch";
    public static final String PLATE = "plate";
    public static final String VIAL = "vial";
    public static final String UPDATE_DATE = "updateDate";
    public static final String CREATE_DATE = "createDate";
    public static final String UPDATE_AFTER = "updateAfter";
    public static final String UPDATE_BEFORE = "updateBefore";
    public static final String UPDATED_AFTER = "updatedAfter";
    public static final String SUBMITTED_BEFORE = "submittedBefore";
    public static final String SUBMITTED_AFTER = "submittedAfter";
    public static final String UPDATED_BEFORE = "updatedBefore";
    public static final String UUID_BROWSER_DATA = "uuidBrowserData";
    public static final String UUID_PARENT_DATA = "uuidParentData";
    public static final String DATE_FORMAT_US_STRING = "MM/dd/yyyy";
    public static final DateFormat DATE_FORMAT_US = new SimpleDateFormat(DATE_FORMAT_US_STRING);
    public static final String EMPTY_SEARCH_PARAMS_FILTER = "";
    public static final String UUID_SEARCH_RADIO = "uuidSearchRadio";
    public static final String BARCODE_SEARCH_RADIO = "barcodeSearchRadio";
    public static final String FILE_SEARCH_RADIO = "fileSearchRadio";
    public static final String TUMOR = "tumor";
    public static final String NORMAL = "normal";
    public static final String CELL_LINE = "cellLine";
    public static final String CELL_LINE_CONTROL = "Cell Line Control";
    public static final String TOP = "top";
    public static final String MIDDLE = "middle";
    public static final String BOTTOM = "bottom";
    public static final List<String> EMPTY_FORM_VALUES = new LinkedList() {{
        add(EMPTY_SEARCH_PARAMS_FILTER);
    }};

    public static final Map<String, String> UUID_BROWSER_COLS = new LinkedHashMap<String, String>() {{
        put(BARCODE, "Barcode");
        put(DISEASE, "Disease");
        put(UUID, "UUID");
        put(UUID_TYPE, "Element Type");
        put(RECEIVING_CENTER, "Receiving Center");
        put(PLATFORM, "Platform");
        put(TISSUE_SOURCE_SITE, "Tissue Source Site");
        put(PARTICIPANT_ID, "Participant Number");
        put(SAMPLE_TYPE, "Sample Type");
        put(ANALYTE_TYPE, "Analyte Type");
        put(PLATE_ID, "Plate Id");
        put(VIAL_ID, "Vial Id");
        put(PORTION_ID, "Portion Sequence");
        put(SLIDE, "Slide");
        put(BCR, "BCR Source");
        put(BATCH, "Batch");
        put(UPDATE_DATE, "Update Date");
        put(CREATE_DATE, "Creation Date");
    }};
}
