/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataAccessMatrixJSPUtil;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacade;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMFacadeI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMHelperI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMModel;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.ErrorInfo;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.FilterChoices;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.StaticMatrixModelFactoryI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.support.StringMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Author: David Nassau
 * Processes filter requests into an instance of the DAMFilterModel, which is
 * contained within the DAMFacade.
 */
public class DataAccessMatrixController extends WebController {

    private boolean administrativeMode;
    private StaticMatrixModelFactoryI staticMatrixModelFactory;
    boolean isUsedInTest = false;   //todo kludge? But you have to avoid calling getWebApplicationContext()
    boolean enabled = true;
    private DAMHelperI damHelper;

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void setStaticMatrixModelFactory(StaticMatrixModelFactoryI staticMatrixModelFactory) {
        this.staticMatrixModelFactory = staticMatrixModelFactory;
    }

    /**
     * Set to True by Spring when this controller is runing in administrative mode.
     * This allows it to be called with parameter refreshMatrix=true, which flushes the existing
     * data and refreshes it for all users
     *
     * @param adminMode run in admin mode or not
     */
    public void setAdministrativeMode(final boolean adminMode) {
        this.administrativeMode = adminMode;
    }

    public void setDamHelper(DAMHelperI damHelper) {
        this.damHelper = damHelper;
    }

    protected ModelAndView handle(
            HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) {
        ModelAndView responseModelAndView;
        try {
            if (!enabled) {
                throw new IllegalStateException("Data Matrix user interface is disabled");
            }
            final FilterRequestI filterRequest = (FilterRequestI) command;
            if (filterRequest.getDiseaseType() == null) {
                filterRequest.setDiseaseType(DataAccessMatrixQueries.DEFAULT_DISEASETYPE);
            }
            filterRequest.validate();
            setDiseaseForDataSource(filterRequest.getDiseaseType());

            if (administrativeMode) {
                staticMatrixModelFactory.refreshAll();
                damHelper.cacheTumorCenterPlatformInfo();
                FilterChoices.clearInstances();
            }
            DAMFacadeI facadeI = (DAMFacadeI) request.getSession().getAttribute("damFacade");
            if (facadeI == null || !(filterRequest.getDiseaseType().equals(facadeI.getDiseaseType()))) {
                if (!isUsedInTest) {
                    DataAccessMatrixJSPUtil.storeLookups(getWebApplicationContext().getServletContext());
                }
                final DAMModel staticModel = staticMatrixModelFactory.getOrMakeModel(filterRequest.getDiseaseType(), false);
                facadeI = new DAMFacade(staticModel);
                request.getSession().setAttribute("damFacade", facadeI);
            }
            ensureProperBackButtonBehavior(filterRequest, request.getSession());
            facadeI.unselectAll();
            facadeI.setFilter(filterRequest);
            responseModelAndView = new ModelAndView(successView, "damFacade", facadeI);
        } catch (IllegalArgumentException e) {
            responseModelAndView = new ModelAndView(errorView, "ErrorInfo", new ErrorInfo(e));
        } catch (IllegalStateException e) {
            responseModelAndView = new ModelAndView(errorView, "ErrorInfo", new ErrorInfo(e));
        } catch (DataAccessMatrixQueries.DAMQueriesException e) {
            responseModelAndView = new ModelAndView(errorView, "ErrorInfo", new ErrorInfo(e));
        }
        return responseModelAndView;
    }

    protected void initApplicationContext() {
        super.initApplicationContext();
        damHelper.cacheTumorCenterPlatformInfo();
    }


    //if user reached this page by clicking Back in browser, we want it to function
    //as a no-op regardless of what the argument says.
    private void ensureProperBackButtonBehavior(FilterRequestI filterRequest, HttpSession session) {
        long millis = filterRequest.getMillis();
        if (millis > 0) {
            final Long storedMillis = (Long) session.getAttribute("filterMillis");
            if (storedMillis != null && storedMillis == millis) {
                //got the same millisecond argument, user must have clicked Back.
                //Make sure it just displays the current state
                filterRequest.setMode(FilterRequest.Mode.NoOp);
            } else {
                //normal request, store the millis for next time
                session.setAttribute("filterMillis", millis);
            }
        }
    }

    //register a custom editor that will translate uploaded file into string
    protected void initBinder(HttpServletRequest httpServletRequest,
                              ServletRequestDataBinder servletRequestDataBinder) {
        servletRequestDataBinder.registerCustomEditor(String.class, "sampleListFile", new StringMultipartFileEditor());
    }


}
