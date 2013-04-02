/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.web.json;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Class to test the json request to viewname translator.
 * This class is only use for spring underlying view resolver,
 * still it's ok to make sure the viewname is resolved correctly.
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class JsonRequestToViewNameTranslatorFastTest {

    MockHttpServletRequest request;

    @Before
    public void before(){
        request = new MockHttpServletRequest();
    }

    @Test
    public void JsonRequestToViewNameTranslatorTestouille() throws Exception {

        JsonRequestToViewNameTranslator jsonView = new JsonRequestToViewNameTranslator();
        jsonView.setView("Domi View");
        assertNotNull(jsonView.getView());
        String view = jsonView.getViewName(request);
        assertEquals("Domi View", view);
    }

}
