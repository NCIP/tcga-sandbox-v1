/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.controllers;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.GenerationMethod.Upload;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.SecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.uuid.bean.FileUploadBean;
import gov.nih.nci.ncicb.tcga.dcc.uuid.service.UUIDReportService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

/**
 * Class to test FileUploadController
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

@RunWith(JMock.class)
public class FileUploadControllerFastTest {

    private final JUnit4Mockery context = new JUnit4Mockery();
    private FileUploadController controller;
    private UUIDService uuidService;
    private UUIDReportService uuidReportService;
    private SecurityUtil mockSecurityUtil;

    private MultipartFile multipartFile;
    private HttpSession session;
    private FileUploadBean fileUploadbean;
    private List<String> uuidList;
    private List<UUIDDetail> uuidDetailList;
    private static final int CENTER_ID = 1;
    public static final String SAMPLE_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    @Before
    public void setup() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        session = request.getSession();

        uuidList = new ArrayList<String>();
        uuidList.add("mammoth");
        uuidList.add("tiger");

        multipartFile = context.mock(MultipartFile.class);
        uuidService = context.mock(UUIDService.class);
        uuidReportService = context.mock(UUIDReportService.class);
        mockSecurityUtil = context.mock(SecurityUtil.class);

        fileUploadbean = new FileUploadBean();
        fileUploadbean.setCenterId(CENTER_ID);
        fileUploadbean.setFile(multipartFile);

        uuidDetailList = new ArrayList<UUIDDetail>();
        uuidDetailList.add(new UUIDDetail("mammoth", new Date(), Upload, new Center(), "master_user"));
        uuidDetailList.add(new UUIDDetail("tiger", new Date(), Upload, new Center(), "master_user"));
    }

    private void prepareController(boolean override) {

        if (override) {
            controller = new FileUploadController() {
                protected List<String> parseUploadFile(final InputStream inputStream) {
                    return uuidList;
                }
            };
        } else {
            controller = new FileUploadController();
        }

        controller.setUuidService(uuidService);
        controller.setUuidReportService(uuidReportService);
        controller.setSecurityUtil(mockSecurityUtil);
    }


    @Test
    public void testFileUploadRequest() throws UUIDException, IOException {

        final String userName = "userName";
        prepareController(true);
        context.checking(new Expectations() {{
            one(multipartFile).getInputStream();
            one(uuidService).uploadUUID(CENTER_ID, uuidList, userName);
            will(returnValue(uuidDetailList));
            one(multipartFile).getOriginalFilename();
            will(returnValue("myNiceUUIDFile.txt"));
            one(mockSecurityUtil).getAuthenticatedPrincipalLoginName();
            will(returnValue(userName));
        }});
        ModelMap model = new ModelMap();
        String view = controller.getUploadFile(model, fileUploadbean, session);
        assertNotNull(model);
        assertEquals(view, "uuidUploadStatus");
        assertEquals(model.get("response"), "{\"success\":true,\"message\":\"myNiceUUIDFile.txt\"}");
    }

    @Test
    public void testParseUploadFile() throws UUIDException, IOException {

        FileInputStream fileInputStream = null;
        try {
            context.checking(new Expectations() {{
                one(uuidService).isValidUUID("4774e379-5e08-4b41-a87d-c014d4e15afb");
                will(returnValue(true));
                one(uuidService).isValidUUID("902f966e-fc4a-4fbc-87c5-4d201b092526");
                will(returnValue(true));
            }});
            prepareController(false);
            String testDir = SAMPLE_DIR + "upload/";
            File uploadFile = new File(testDir + "uuidUpload.txt");
            //noinspection IOResourceOpenedButNotSafelyClosed
            fileInputStream = new FileInputStream(uploadFile);
            List<String> uuidStrList = controller.parseUploadFile(fileInputStream);
            assertNotNull(uuidStrList);
            assertEquals(2, uuidStrList.size());
            assertEquals("4774e379-5e08-4b41-a87d-c014d4e15afb", uuidStrList.get(0));
            assertEquals("902f966e-fc4a-4fbc-87c5-4d201b092526", uuidStrList.get(1));
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    @Test(expected = UUIDException.class)
    public void testParseBadUploadFile() throws UUIDException, IOException {

        FileInputStream fileInputStream = null;
        try {
            context.checking(new Expectations() {{
                one(uuidService).isValidUUID("4774e379-5e08-4b41-a87d-c014d4e15afb");
                will(returnValue(true));
                one(uuidService).isValidUUID("902f966e-fc4a-4fbc-87c5-4d201b092526");
                will(returnValue(true));
                one(uuidService).isValidUUID("uuid1wes-houl-dfai-lher-e00000000007");
                will(returnValue(false));
            }});
            prepareController(false);
            String testDir = SAMPLE_DIR + "upload/";
            File uploadFile = new File(testDir + "uuidUploadBad.txt");
            //noinspection IOResourceOpenedButNotSafelyClosed
            fileInputStream = new FileInputStream(uploadFile);
            controller.parseUploadFile(fileInputStream);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

}