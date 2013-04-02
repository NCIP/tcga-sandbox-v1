/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Author: David Nassau
 * <p/>
 * Parent web controller class which allows injection of names of success and error views.
 */
public abstract class WebController extends AbstractCommandController {

    protected String successView, errorView;
    // used to get session key out of session params
    protected static final String SESSION_KEY_NAME = "damSessionKey";

    protected abstract ModelAndView handle(HttpServletRequest request, HttpServletResponse response,
                                           Object command, BindException errors);

    public void setSuccessView(final String successView) {
        this.successView = successView;
    }

    public void setErrorView(final String errorView) {
        this.errorView = errorView;
    }

    // sets the session key.  will overwrite any old one, so only
    // use when it's a new "session" (can be same HttpSession, but
    // a new conceptual session, as in they have finished downloading and want to get more.)

    protected void setSessionKey(final HttpServletRequest request) {
        UUID key = UUID.randomUUID();
        request.getSession().setAttribute(SESSION_KEY_NAME, key);
    }

    public UUID getSessionKey(final HttpServletRequest request) {
        return (UUID) request.getSession().getAttribute(SESSION_KEY_NAME);
    }

    protected void clearSessionKey(final HttpServletRequest request) {
        request.getSession().removeAttribute(SESSION_KEY_NAME);
    }

    //set thread-local value which will determine which schema to use by DAO

    protected void setDiseaseForDataSource(final String disease) {
        DiseaseContextHolder.setDisease(disease);
    }

}
