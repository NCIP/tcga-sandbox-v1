/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.json;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DEFAULT_LIMIT;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DEFAULT_START;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DIR;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SORT;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Duration;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDReportService;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for all UUID Manager reports json requests 
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

@Controller
public class UUIDReportsController {

    @Autowired
    private UUIDService uuidService;

    @Autowired
    private UUIDReportService uuidReportService;

    /**
     * Gets the results for a given report type [eg New UUIDs report/Missing UUIDs report/Submitted UUIDs]  
     * @param model ModelMap for the request
     * @param start starting index for the search results page
     * @param limit number of results to be returned
     * @param sortColumn column to be sorted
     * @param direction direction of sorting
     * @param reportType type of report
     * @param session HttpSession for the request
     * @return the Model map for the controller action
     */
    @RequestMapping (value="/uuidReport.json", method = RequestMethod.POST)
    public ModelMap getUUIDReportResults(
            final ModelMap model,
            @RequestParam Integer start,
            @RequestParam Integer limit,
            @RequestParam (value=SORT, required = false) final String sortColumn,
            @RequestParam (value=DIR, required = false) final String direction,
            @RequestParam (value="reportType") final String reportType,
            final HttpSession session) {

        List<UUIDDetail> uuidList;
        session.removeAttribute(UUIDConstants.EXPORT_DATA);

        //initializing the start and limit values since they are not sent by the client
        // when the request is for sorting only
        if (start == null) {
            start = DEFAULT_START;
            limit = DEFAULT_LIMIT;
        }        

        uuidList = getReportData(reportType);
        uuidReportService.sortList(uuidList, sortColumn, direction);
        session.setAttribute(UUIDConstants.EXPORT_DATA, uuidList);
        List<UUIDDetail> uuidListPaginated = uuidReportService.getPaginatedList(uuidList, start, limit);

        int totalCount = uuidReportService.getTotalCount(uuidList);
        model.addAttribute(UUIDConstants.TOTAL_COUNT, totalCount);

        model.addAttribute(UUIDConstants.REPORT_RESULTS, uuidListPaginated);
        model.addAttribute(UUIDConstants.SUCCESS, true);
        return model;
    }

    private List<UUIDDetail> getReportData(final String reportType) {
        List<UUIDDetail> uuidList = null;
        
        if(reportType != null){
            if(reportType.equals(UUIDConstants.REPORT_TYPE_NEW_UUID)) {
                // get the filter value for day/month/week or center etc
                // assuming the filter is for a week [till we implement filters on UI]
                uuidList = uuidService.getNewlyGeneratedUUIDs(Duration.Week);
            }else if(reportType.equals(UUIDConstants.REPORT_TYPE_SUBMITTED_UUID)) {
                uuidList = uuidService.getSubmittedUUIDs();
            }else if(reportType.equals(UUIDConstants.REPORT_TYPE_MISSING_UUID)) {
                uuidList = uuidService.getMissingUUIDs();
            }
        }
        return uuidList;
    }

    protected void setUuidService(final UUIDService uuidService) {
        this.uuidService = uuidService;
    }

    protected void setUuidReportService(final UUIDReportService uuidReportService) {
        this.uuidReportService = uuidReportService;
    }
}
