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
import junit.framework.Assert;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
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

/**
 * Test class for the TCGAExcelView
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

public class TCGAExcelViewFastTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private TCGAExcelView xlView;
    private ModelMap model;
    private WritableWorkbook wb;

    @Before
    public void before() throws Exception {

        xlView = new TCGAExcelView();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        wb = Workbook.createWorkbook(response.getOutputStream());
        model = new ModelMap(){{
          put("exportType","xl");
          put("title","domi");
          put("fileName","domi.xls");
          put("cols",mockMapCols);
          put("data",makeMockArchive());
      }};
    }

    @Test
    public void buildExcelDocument() throws Exception {
      xlView.buildExcelDocument(model,wb,request,response);
      assertEquals("attachment; filename=domi.xls",response.getHeader("Content-Disposition"));
      WritableSheet sheet = wb.getSheet(0);
      Assert.assertEquals("domi",sheet.getName());
      Assert.assertEquals(3,sheet.getColumns());
      Assert.assertEquals("Archive Name",sheet.getCell(0,0).getContents());
      Assert.assertEquals("Platform",sheet.getCell(1,0).getContents());
      Assert.assertEquals("Url",sheet.getCell(2,0).getContents());
      Assert.assertEquals("mockArchive1",sheet.getCell(0,1).getContents());
      Assert.assertEquals("mockplatform1",sheet.getCell(1,1).getContents());
      Assert.assertEquals("mockUrl",sheet.getCell(2,1).getContents());
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

}//End of Class
