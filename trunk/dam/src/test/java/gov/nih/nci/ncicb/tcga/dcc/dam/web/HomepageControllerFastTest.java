/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.web;

import gov.nih.nci.ncicb.tcga.dcc.dam.view.DAMStaticModel;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.StaticMatrixModelFactoryI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequest;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;

import javax.servlet.ServletContext;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

/**
 * Fast test for HomepageController.
 *
 * @author Silpa Nanan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class HomepageControllerFastTest extends BaseWebTstParent {

    public void testHandle() throws Exception {
        Mockery mockery = new Mockery();
        final StaticMatrixModelFactoryI staticModel = mockery.mock( StaticMatrixModelFactoryI.class );
        final FilterRequestI filterRequest = mockery.mock( FilterRequestI.class );
        final WebApplicationContext contextGetter = mockery.mock( WebApplicationContext.class );
        final ServletContext context = mockery.mock( ServletContext.class );
        HomepageController homepageController = new HomepageController();
        homepageController.isUsedInTest = true;
        homepageController.setCommandClass( FilterRequest.class );
        homepageController.setCommandName( "DataAccessMatrixControllerRequest" );
        homepageController.setSuccessView( "homepage" );
        homepageController.setErrorView( "homepageError" );
        homepageController.setStaticMatrixModelFactory( staticModel );
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        final DAMStaticModel model = DAMStaticModel.createInstance( DISEASE_TYPE, dpts, null );
        //dn: need this explained
        mockery.checking( new Expectations() {
            {
                one( filterRequest ).validate();
                allowing( filterRequest ).getDiseaseType();
                will( returnValue( DISEASE_TYPE ) );
                one( staticModel ).getOrMakeModel( DISEASE_TYPE, false );
                will( returnValue( model ) );
            }
        } );
        ModelAndView modelAndView = homepageController.handle( request, response, filterRequest, null );
        mockery.assertIsSatisfied();
        assertEquals( "homepage", modelAndView.getViewName() );
    }
}
