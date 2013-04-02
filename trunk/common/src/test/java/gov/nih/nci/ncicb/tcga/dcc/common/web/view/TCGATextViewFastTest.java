/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.web.view;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test TCGA text view
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class TCGATextViewFastTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private TCGATextView textView;
    private ModelMap model;

    @Before
    public void before() throws Exception {

        textView = new TCGATextView();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        model = new ModelMap(){{
          put("exportType","csv");
          put("fileName","domi.txt");
          put("cols",mockMapCols);
          put("data",makeMockArchive());
      }};
    }

    @Test
    public void renderMergedOutputModel() throws Exception {
      textView.renderMergedOutputModel(model,request,response);
      assertEquals("text/plain",response.getContentType());
      assertEquals("attachment; filename=domi.txt",response.getHeader("Content-Disposition"));
      String content = response.getContentAsString();
      assertFalse(content.contains("version1"));
      assertTrue(content.contains("\"mockArchive1\",\"mockplatform1\",\"mockUrl\""));

    }

    public Map<String, String> mockMapCols = new LinkedHashMap<String, String>() {{
        put("realName", "Archive Name");
        put("platform", "Platform");
        put("deployLocation", "Url");
    }};

    public List<Archive> makeMockArchive() {
        List<Archive> list = new LinkedList<Archive>();
        Archive archi = new Archive();
            archi.setRealName("mockArchive1");
            archi.setDateAdded(new Date(123456789));
            archi.setDisplayVersion("version1");
            archi.setPlatform("mockplatform1");
            archi.setDeployLocation("mockUrl");

        list.add(archi);

       return list;
    }

} //End of Class
