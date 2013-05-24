/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * These are the queries used to generate DCC reports for the all purposes.
 * <p/>
 * Statically import this classes constants so you can use all the them as though they were part of your class.
 * <p/>
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $
 */

public class DatareportsCommonConstants {

    public static final String OV = "OV";
    public static final String GBM = "GBM";
    public static final String GSC = "GSC";
    public static final String CGCC = "CGCC";
    public static final String BR = "<br />";
    public static final String DISEASE = "disease";
    public static final String CENTER = "center";
    public static final String PLATFORM = "platform";
    public static final String DATA_TYPE = "dataType";
    public static final String BCR = "bcr";
    public static final String BATCH = "batch";
    public static final String IGC = "igc";
    public static final String ALIQUOT_ID = "aliquotId";
    public static final String ANALYTE_ID = "analyteId";
    public static final String SAMPLE_ID = "sampleId";
    public static final String PARTICIPANT_ID = "participantId";
    public static final String BCR_BATCH = "bcrBatch";
    public static final String EXPORT_TYPE = "exportType";
    public static final String EXPORT_TITLE = "title";
    public static final String EXPORT_DATA = "data";
    public static final String EXPORT_DATE_FORMAT = "dateFormat";
    public static final String EXPORT_FILENAME = "fileName";
    public static final String MODE = "mode";
    public static final String TYPE = "type";
    public static final String SELECTION = "selection";
    public static final String DIR = "dir";
    public static final String SORT = "sort";
    public static final String START = "start";
    public static final String LIMIT = "limit";
    public static final String COLS = "cols";
    public static final String FORM_FILTER = "formFilter";
    public static final String FILTER_REQ = "filterReq";
    public static final String FILTER = "filter";
    public static final String LEVEL = "level";
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    public static final String SEPARATOR = ",";
    public static final String SERVER_URL = "serverUrl";
    public static final String CSV = "csv";
    public static final String TAB = "tab";
    public static final String XL = "xl";
    public static final String TOTAL_COUNT = "totalCount";
    public static final String PAGE_SIZE = "pageSize";
    public static final String SHOW_FILTER_BOX = "showFilterBox";
    public static final String YES = "yes";
    public static final String ALL = "all";
    public static final String NOT_AVAILABLE = "N/A";
    public static final String DATE_FORMAT_US_STRING = "MM/dd/yyyy";
    public static final String DATE_TIME_FORMAT_US_STRING = "MM/dd/yyyy HH:mm";
    public static final DateFormat DATE_FORMAT_US = new SimpleDateFormat(DATE_FORMAT_US_STRING);
    public static final DateFormat DATE_TIME_FORMAT_US = new SimpleDateFormat(DATE_TIME_FORMAT_US_STRING);
    public static final String DATE = "date";
    public static final String DATE_FROM = "dateFrom";
    public static final String DATE_TO = "dateTo";
    public static final String PROJECT = "project";
    public static final String PARTICIPANT = "participant";
    public static final String VIAL_ID = "vialId";
    public static final String PORTION_ID = "portionId";
    public static final String PLATE_ID = "plateId";
    public static final String CENTER_ID = "centerId";
    public static final String VALID = "valid";
    public static final String DATA_LEVEL = "dataLevel";
    public static final String SAMPLE_TYPE = "sampleType";
    public static final String TISSUE = "tissue";
    public static final String FILE_SIZE = "fileSize";
    public static final String ALIQUOT_UUID = "aliquotUUID";
    public static final String NA = "N/A";

    //Constant for the datareports home page
    public static final String DATAREPORTS_HOME_VIEW = "dataReportsHome";
    public static final String DATAREPORTS_HOME_URL = "/" + DATAREPORTS_HOME_VIEW + ".htm";

    //List of default values for filters form used in datareports
    public static final List<String> EMPTY_FORM_VALUES = new LinkedList() {{
        add(SampleSummaryReportConstants.EMPTY_SAMPLE_SUMMARY_FILTER);
        add(AliquotReportConstants.EMPTY_ALIQUOT_FILTER);
        add(LatestGenericReportConstants.EMPTY_LATEST_ARCHIVE_FILTER);
        add(AliquotIdBreakdownReportConstants.EMPTY_ALIQUOT_ID_BREAKDOWN_FILTER);
        add(ProjectCaseDashboardConstants.EMPTY_PROJECT_CASE_DASHBOARD_FILTER);
    }};

}//End of Class