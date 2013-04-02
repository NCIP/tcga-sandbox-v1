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
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
 * Test class for the TCGAExcelSXSSView
 *
 * @author bertondl Last updated by: $Author$
 * @version $Rev$
 */
public class TCGAExcelSXSSViewFastTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private TCGAExcelSXSSView xlView;
    private ModelMap model;
    private SXSSFWorkbook wb;

    @Before
    public void before() throws Exception {

        xlView = new TCGAExcelSXSSView();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        wb = new SXSSFWorkbook(1000);
        wb.write(response.getOutputStream());
        response.getOutputStream().close();
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
      Sheet sheet = wb.getSheetAt(0);
      Assert.assertEquals("domi", sheet.getSheetName());
      Assert.assertEquals("Archive Name", sheet.getRow(0).getCell(0).getStringCellValue());
      Assert.assertEquals("Platform",sheet.getRow(0).getCell(1).getStringCellValue());
      Assert.assertEquals("Url",sheet.getRow(0).getCell(2).getStringCellValue());
      Assert.assertEquals("mockArchive1",sheet.getRow(1).getCell(0).getStringCellValue());
      Assert.assertEquals("mockplatform1",sheet.getRow(1).getCell(1).getStringCellValue());
      Assert.assertEquals("mockUrl",sheet.getRow(1).getCell(2).getStringCellValue());
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

}
