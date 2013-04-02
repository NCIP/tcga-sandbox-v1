/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.web.ArchiveQueryRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataAccessMatrixJSPUtil;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Robert S. Sfeir
 * @author David Kane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveFormController extends SimpleFormController {

    private String selectView = null;
    private ArchiveQueries archiveQueries = null;
    private TumorQueries tumorQueries = null;
    private PlatformQueries platformQueries = null;
    private CenterQueries centerQueries = null;
    private DataTypeQueries dataTypeQueries = null;
    private ArchiveQueryRequest archiveQueryRequest = null;
    private Collection tumors = null;
    private Collection platforms = null;
    private Collection centers = null;
    private Collection dataTypes = null;
    private Collection archiveTypes = null;

    public ArchiveFormController() {

        setCommandName("archive");
        setCommandClass(ArchiveQueryRequest.class);
    }

    protected Object formBackingObject(final HttpServletRequest request) {
        return new ArchiveQueryRequest();
    }

    protected void initApplicationContext() {

        super.initApplicationContext();

        final ServletContext servletContext = super.getWebApplicationContext().getServletContext();

        // Setting tumors
        if (tumors == null) {
            tumors = tumorQueries.getAllTumors();
        }

        if (servletContext.getAttribute("tumors") == null) {
            super.getWebApplicationContext().getServletContext().setAttribute("tumors", tumors);
        }

        // Setting centers
        if (centers == null) {
            centers = centerQueries.getAllCenters();
        }

        if (servletContext.getAttribute("centers") == null) {
            super.getWebApplicationContext().getServletContext().setAttribute("centers", centers);
        }

        // Setting platforms
        if (platforms == null) {
            platforms = platformQueries.getAllPlatforms();
        }

        if (servletContext.getAttribute("platforms") == null) {
            super.getWebApplicationContext().getServletContext().setAttribute("platforms", platforms);
        }

        // Setting dataTypes
        if (dataTypes == null) {
            dataTypes = dataTypeQueries.getAllDataTypes();
        }

        if (servletContext.getAttribute("datatypes") == null) {
            super.getWebApplicationContext().getServletContext().setAttribute("datatypes", dataTypes);
        }

        // Setting archiveTypes
        if (this.archiveTypes == null) {
            archiveTypes = archiveQueries.getAllArchiveTypes();
        }

        if (servletContext.getAttribute("archiveTypes") == null) {
            super.getWebApplicationContext().getServletContext().setAttribute("archiveTypes", archiveTypes);
        }

        if (this.selectView == null) {
            throw new IllegalArgumentException("selectView isn't set");
        }
    }

    public void initLists() {

        if (tumors == null) {
            tumors = tumorQueries.getAllTumors();
        }

        if (centers == null) {
            centers = centerQueries.getAllCenters();
        }

        if (platforms == null) {
            platforms = platformQueries.getAllPlatforms();
        }

        if (dataTypes == null) {
            dataTypes = dataTypeQueries.getAllDataTypes();
        }

        if (archiveTypes == null) {
            archiveTypes = archiveQueries.getAllArchiveTypes();
        }

        final Map<String, List> dataLookups = new HashMap<String, List>();
        dataLookups.put(DataAccessMatrixJSPUtil.ATTRIBUTE_KEY_CENTERS, new ArrayList(centers));
        dataLookups.put(DataAccessMatrixJSPUtil.ATTRIBUTE_KEY_DATATYPES, new ArrayList(dataTypes));
        dataLookups.put(DataAccessMatrixJSPUtil.ATTRIBUTE_KEY_PLATFORMS, new ArrayList(platforms));
        DataAccessMatrixJSPUtil.storeLookups(dataLookups);
    }

    protected ModelAndView onSubmit(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final Object command,
                                    final BindException errors) {

        request.getSession().setAttribute("platVals", request.getParameterValues("platform"));
        request.getSession().setAttribute("centerVals", request.getParameterValues("center"));
        request.getSession().setAttribute("dataTypeVals", request.getParameterValues("dataType"));
        request.getSession().setAttribute("tumorVals", request.getParameterValues("project"));
        request.getSession().setAttribute("archiveTypeVals", request.getParameterValues("archiveType"));
        request.getSession().setAttribute("dateStart", request.getParameter("dateStart"));
        request.getSession().setAttribute("dateEnd", request.getParameter("dateEnd"));
        request.getSession().setAttribute("fileName", request.getParameter("fileName"));

        final ArchiveQueryRequest queryParameter = (ArchiveQueryRequest) command;
        final Collection results = archiveQueries.getMatchingArchives(queryParameter);

        return new ModelAndView(getSuccessView(), "archiveList", results);
    }

    public Collection getTumors() {
        return tumors;
    }

    public void setTumors(final Collection tumors) {
        this.tumors = tumors;
    }

    public TumorQueries getTumorQueries() {
        return tumorQueries;
    }

    public void setTumorQueries(final TumorQueries tumorQueries) {
        this.tumorQueries = tumorQueries;
    }

    public PlatformQueries getPlatformQueries() {
        return platformQueries;
    }

    public void setPlatformQueries(final PlatformQueries platformQueries) {
        this.platformQueries = platformQueries;
    }

    public CenterQueries getCenterQueries() {
        return centerQueries;
    }

    public void setCenterQueries(final CenterQueries centerQueries) {
        this.centerQueries = centerQueries;
    }

    public Collection getPlatforms() {
        return platforms;
    }

    public void setPlatforms(final Collection platforms) {
        this.platforms = platforms;
    }

    public Collection getCenters() {
        return centers;
    }

    public void setCenters(final Collection centers) {
        this.centers = centers;
    }

    public Collection getArchiveTypes() {
        return archiveTypes;
    }

    public void setArchiveTypes(final Collection archiveTypes) {
        this.archiveTypes = archiveTypes;
    }

    public ArchiveQueryRequest getArchiveQuery() {
        return archiveQueryRequest;
    }

    public void setArchiveQuery(final ArchiveQueryRequest archiveQueryRequest) {
        this.archiveQueryRequest = archiveQueryRequest;
    }

    public ArchiveQueries getArchiveQueries() {
        return archiveQueries;
    }

    public void setArchiveQueries(final ArchiveQueries archiveQueries) {
        this.archiveQueries = archiveQueries;
    }

    public Collection getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(final Collection dataTypes) {
        this.dataTypes = dataTypes;
    }

    public DataTypeQueries getDataTypeQueries() {
        return dataTypeQueries;
    }

    public void setDataTypeQueries(final DataTypeQueries dataTypeQueries) {
        this.dataTypeQueries = dataTypeQueries;
    }

    public String getSelectView() {
        return selectView;
    }

    public void setSelectView(final String selectView) {
        this.selectView = selectView;
    }
}