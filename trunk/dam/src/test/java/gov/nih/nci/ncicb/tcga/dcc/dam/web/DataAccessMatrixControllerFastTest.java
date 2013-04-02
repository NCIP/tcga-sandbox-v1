/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMHelperI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMStaticModel;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.StaticMatrixModelFactoryI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by IntelliJ IDEA.
 * User: nanans
 * Date: Sep 8, 2008
 * Time: 3:07:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataAccessMatrixControllerFastTest extends BaseWebTstParent {
    private Mockery mockery;
    private StaticMatrixModelFactoryI staticModelFactory;
    private FilterRequestI filterRequest;
    private DAMHelperI damHelper;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private DAMStaticModel model;
    private DataAccessMatrixController damController;


    public void setUp() throws Exception {
        super.setUp();
        mockery = new Mockery();
        staticModelFactory = mockery.mock(StaticMatrixModelFactoryI.class);
        filterRequest = mockery.mock(FilterRequestI.class);
        damHelper = mockery.mock(DAMHelperI.class);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        model = DAMStaticModel.createInstance(DISEASE_TYPE, dpts, null);
        damController = new DataAccessMatrixController();
        damController.setDamHelper(damHelper);
    }


    public void testGettingNewDAMFacade() throws Exception {
        damController.setCommandClass(FilterRequest.class);
        damController.setCommandName("DataAccessMatrixControllerRequest");
        damController.setSuccessView("dataAccessMatrix");
        damController.setErrorView("dataAccessMatrixError");
        damController.setStaticMatrixModelFactory(staticModelFactory);
        damController.isUsedInTest = true;

        final Long milli = Long.MIN_VALUE;
        mockery.checking(new Expectations() {
            {
                one(filterRequest).validate();
                allowing(filterRequest).getDiseaseType();
                will(returnValue(DISEASE_TYPE));
                one(filterRequest).getMillis();
                will(returnValue(milli));
                one(filterRequest).getMode();
                will(returnValue(FilterRequestI.Mode.Clear));
                one(staticModelFactory).getOrMakeModel(DISEASE_TYPE, false);
                will(returnValue(model));
            }
        });
        ModelAndView modelAndView = damController.handle(request, response, filterRequest, null);
        mockery.assertIsSatisfied();
        assertEquals("dataAccessMatrix", modelAndView.getViewName());
    }

    public void testAdmin() throws Exception {
        damController.setCommandClass(FilterRequest.class);
        damController.setCommandName("DataAccessMatrixControllerRequest");
        damController.setSuccessView("dataAccessMatrix");
        damController.setErrorView("dataAccessMatrixError");
        damController.setStaticMatrixModelFactory(staticModelFactory);
        damController.isUsedInTest = true;
        damController.setAdministrativeMode(true);
        final Long milli = Long.MIN_VALUE;
        mockery.checking(new Expectations() {
            {
                one(filterRequest).validate();
                allowing(filterRequest).getDiseaseType();
                will(returnValue(DISEASE_TYPE));
                one(staticModelFactory).refreshAll();
                one(damHelper).cacheTumorCenterPlatformInfo();
                one(filterRequest).getMillis();
                will(returnValue(milli));
                one(filterRequest).getMode();
                will(returnValue(FilterRequestI.Mode.Clear));
                one(staticModelFactory).getOrMakeModel(DISEASE_TYPE, false);
                will(returnValue(model));
            }
        });
        ModelAndView modelAndView = damController.handle(request, response, filterRequest, null);
        mockery.assertIsSatisfied();
        assertEquals("dataAccessMatrix", modelAndView.getViewName());
    }

/*
    public void testFacadeReuse() throws Exception {
        assessDAMFacadeReuseScenario( 0L, 0L, false );
    }

    public void testBackButtonUsed() throws Exception {
        assessDAMFacadeReuseScenario( 10L, 10L, true );
    }

    public void testBackButtonBypassed() throws Exception {
        assessDAMFacadeReuseScenario( 20L, 10L, false );
    }

    public void assessDAMFacadeReuseScenario( final Long sessionMillis, final Long filterMillis,
                                              final boolean backButtonClicked ) throws Exception {
        Mockery mockery = new Mockery();
        final StaticMatrixModelFactoryI staticModel = mockery.mock( StaticMatrixModelFactoryI.class );
        final FilterRequestI filterRequest = mockery.mock( FilterRequestI.class );
        final WebApplicationContext contextGetter = mockery.mock( WebApplicationContext.class );
        final ServletContext servletContext = mockery.mock( ServletContext.class );
        final DAMFacadeI facade = mockery.mock( DAMFacadeI.class );
        final DAMStaticModel model = DAMStaticModel.createInstance( DISEASE_TYPE, dpts );
        TestableDataAccessMatrixController damController = new TestableDataAccessMatrixController();
        damController.setApplicationContextFactory( contextGetter );
        damController.setCommandClass( FilterRequest.class );
        damController.setCommandName( "DataAccessMatrixControllerRequest" );
        damController.setSuccessView( "dataAccessMatrix" );
        damController.setErrorView( "dataAccessMatrixError" );
        damController.setStaticMatrixModelFactory( staticModel );
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute( "damFacade", facade );
        session.setAttribute( "filterMillis", sessionMillis );
        request.setSession( session );
        mockery.checking( new Expectations() {
            {
                one( filterRequest ).validate();
                one( facade ).getDiseaseType();
                will( returnValue( DISEASE_TYPE ) );
                exactly( 2 ).of( filterRequest ).getDiseaseType();
                will( returnValue( DISEASE_TYPE ) );
                one( filterRequest ).getMillis();
                will( returnValue( filterMillis ) );
                if(backButtonClicked) {
                    one( filterRequest ).setMode( FilterRequest.Mode.NoOp );
                }
                one( facade ).unselectAll();
                one( facade ).setFilter( filterRequest );
            }
        } );
        ModelAndView modelAndView = damController.handle( request, response, filterRequest, null );
        mockery.assertIsSatisfied();
        assertEquals( filterMillis, session.getAttribute( "filterMillis" ) );
        assertEquals( "dataAccessMatrix", modelAndView.getViewName() );
    }*/
}
