/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.json;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DATE_FORMAT;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DEFAULT_LIMIT;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DEFAULT_START;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.DIR;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SORT;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDReportService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * JSON Controller for Search functionality
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

@Controller
public class SearchUUIDController {

    @Autowired
    private UUIDService uuidService;

    @Autowired
    private UUIDReportService uuidReportService;

    /**
     * Handles the search UUID request
     * @param model Model containing status for the search operation
     * @param searchCriteria criteria for search
     * @param start starting index for the search results page
     * @param limit number of reults to be returned
     * @param session HttpSession for the request
     * @return the Model map for the controller action
     */
    @RequestMapping (value = "/searchUUID.json", method = RequestMethod.POST)
    public ModelMap performSearch(
            final ModelMap model, final SearchCriteria searchCriteria,
            @RequestParam (required = false) Integer start,
            @RequestParam (required = false) Integer limit,
            final HttpSession session) {

        List<UUIDDetail> searchResults;

        // use default start and limit values when not specified
        if (start == null) {
            start = DEFAULT_START;
            limit = DEFAULT_LIMIT;
        }
        session.removeAttribute(UUIDConstants.SEARCH_RESULTS);
        searchResults = uuidService.searchUUIDs(searchCriteria);
        session.setAttribute(UUIDConstants.SEARCH_RESULTS, searchResults);
        getResultPage(model, searchResults, start, limit);
        return model;
    }


    /**
     * Handles the pagination request for search results
     * @param model Model containing status for the search operation
     * @param searchCriteria criteria for search
     * @param start starting index for the search results page
     * @param limit number of results to be returned
     * @param sortColumn column to be sorted
     * @param direction direction of sorting
     * @param session HttpSession for the request
     * @return the Model map for the controller action
     */    
    @RequestMapping (value = "/paginatedResults.json", method = RequestMethod.POST)
    public ModelMap paginateAndSortResults(
            ModelMap model, final SearchCriteria searchCriteria,
            @RequestParam (required = false) Integer start,
            @RequestParam (required = false) Integer limit,
            @RequestParam (value=SORT, required = false) final String sortColumn,
            @RequestParam (value=DIR, required = false) final String direction,
            final HttpSession session) {

        List<UUIDDetail> searchResults;

        //initializing the start and limit values since they are not sent by the client
        // when the request is for sorting only
        if (start == null) {
            start = DEFAULT_START;
            limit = DEFAULT_LIMIT;
        }

        if (searchCriteria.isNewSearch()) {
            if (session.getAttribute(UUIDConstants.SEARCH_RESULTS) != null) {
                session.removeAttribute(UUIDConstants.SEARCH_RESULTS);
            }
        }

        if (session.getAttribute(UUIDConstants.SEARCH_RESULTS) == null) {
            searchResults = uuidService.searchUUIDs(searchCriteria);
            session.setAttribute(UUIDConstants.SEARCH_RESULTS, searchResults);
        }
        else {
            searchResults = (List<UUIDDetail>) session.getAttribute(UUIDConstants.SEARCH_RESULTS);
        }
        uuidReportService.sortList(searchResults, sortColumn, direction);
        // get the results page to be displayed
        getResultPage(model, searchResults, start, limit);
        return model;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    protected void setUuidService(final UUIDService uuidService) {
        this.uuidService = uuidService;
    }

    public void setUuidReportService(final UUIDReportService uuidReportService) {
        this.uuidReportService = uuidReportService;
    }


    private void getResultPage(final ModelMap model, final List<UUIDDetail> searchResults,
                               final Integer start,
                               final Integer limit) {
        
        List<UUIDDetail> uuidListPaginated = uuidReportService.getPaginatedList(searchResults, start, limit);
        int totalCount = uuidReportService.getTotalCount(searchResults);
        model.addAttribute(UUIDConstants.TOTAL_COUNT, totalCount);
        model.addAttribute(UUIDConstants.SEARCH_RESULTS, uuidListPaginated);
        model.addAttribute(UUIDConstants.SUCCESS, true);
    }

}
