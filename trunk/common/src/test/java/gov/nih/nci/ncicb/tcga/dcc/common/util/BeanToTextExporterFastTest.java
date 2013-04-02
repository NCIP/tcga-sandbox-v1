/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationCategory;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationClassification;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItem;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Test class for the BeanToTextExporter
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class BeanToTextExporterFastTest {
    private StringWriter out;
    private DateFormat dateFormat;

    public static final Map<String, String> mockColumnMap = new LinkedHashMap<String, String>() {{
        put("realName", "ARCHIVE_NAME");
        put("dateAdded", "DATE_ADDED");
        put("deployLocation", "ARCHIVE_URL");
    }};

    @Before
    public void setUp() {
        out = new StringWriter();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    }

      @Test
    public void testBeanListToTextTab() throws Exception {
        String res = BeanToTextExporter.beanListToText("tab", out, mockColumnMap, makeMockArchive(),dateFormat);
        assertNotNull(res);
        assertEquals("ARCHIVE_NAME\tDATE_ADDED\tARCHIVE_URL\n" +
                "mock archive name\t01/02/1970 05:17\tmock archive url", res.trim());
    }

    @Test
    public void testBeanListToTextCSV() throws Exception {
        String res = BeanToTextExporter.beanListToText(null, out, mockColumnMap, makeMockArchive(),dateFormat);
        assertNotNull(res);
        assertEquals("\"ARCHIVE_NAME\",\"DATE_ADDED\",\"ARCHIVE_URL\"\n" +
                "\"mock archive name\",\"01/02/1970 05:17\",\"mock archive url\"", res.trim());
    }

    @Test
    public void testBeanListToTextError() throws Exception {
        String res = BeanToTextExporter.beanListToText("tab", out, MOCK_ERROR_MAP, makeMockArchive(),dateFormat);
        assertNotNull(res);
        assertEquals("An error occurred.", res.trim());
    }

    @Test
    public void testBeanListToTextDate() throws Exception {
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String res = BeanToTextExporter.beanListToText("tab", out, MOCK_DATE_MAP, makeMockArchive(),dateFormat);
        assertNotNull(res);
        assertEquals("ARCHIVE_NAME\tDATE_ADDED\tARCHIVE_URL\n" +
                "mock archive name\t01/02/1970\tmock archive url", res.trim());
    }

    @Test
    public void testBeanListToTextDateNullDateFormat() throws Exception {
        String res = BeanToTextExporter.beanListToText("tab", out, MOCK_DATE_MAP, makeMockArchive(),null);
        assertNotNull(res);
        assertEquals("ARCHIVE_NAME\tDATE_ADDED\tARCHIVE_URL\n" +
                "mock archive name\tFri Jan 02 05:17:36 EST 1970\tmock archive url", res.trim());
    }

    @Test
    public void testBeanListToTextWithNewline() {
        List<Archive> archiveList = makeMockArchive();
        Archive archiveWithWeirdName = new Archive();
        archiveWithWeirdName.setRealName("Hi\nI have a newline in my name!");
        archiveWithWeirdName.setDeployLocation("somewhere");
        archiveWithWeirdName.setDateAdded(new Date(123456789));
        archiveList.add(archiveWithWeirdName);
        String res = BeanToTextExporter.beanListToText("tab", out, mockColumnMap, archiveList, dateFormat);
        assertNotNull(res);
        assertEquals("ARCHIVE_NAME\tDATE_ADDED\tARCHIVE_URL\n" +
                "mock archive name\t01/02/1970 05:17\tmock archive url\n" +
                "Hi I have a newline in my name!\t01/02/1970 05:17\tsomewhere", res.trim());
    }

    @Test
    public void testBeanListToTextWithTab() {
        List<Archive> archiveList = makeMockArchive();
        Archive archiveWithWeirdName = new Archive();
        archiveWithWeirdName.setRealName("Hi\tI have a tab in my name!");
        archiveWithWeirdName.setDeployLocation("out there");
        archiveWithWeirdName.setDateAdded(new Date(123456789));
        archiveList.add(archiveWithWeirdName);
        String res = BeanToTextExporter.beanListToText("tab", out, mockColumnMap, archiveList, dateFormat);
        assertNotNull(res);
        assertEquals("ARCHIVE_NAME\tDATE_ADDED\tARCHIVE_URL\n" +
                "mock archive name\t01/02/1970 05:17\tmock archive url\n" +
                "Hi   I have a tab in my name!\t01/02/1970 05:17\tout there", res.trim());
    }

    @Test
    public void testBeanListToTextCollectionProperty() {
        Map<String, String> centerColumns = new LinkedHashMap<String, String>() {{
        put("centerName", "Center Name");
        put("centerType", "Center Type");
        put("emailList", "Email Addresses");
    }};
        Center center = new Center();
        center.setCenterName("ABC");
        center.setEmailList(Arrays.asList("1@1", "2@2", "3@3"));
        center.setCenterType("TEST");        
        String text = BeanToTextExporter.beanListToText("tab", out, centerColumns, Arrays.asList(center), dateFormat);
        assertEquals("Center Name\tCenter Type\tEmail Addresses\n" +
                "ABC\tTEST\t1@1; 2@2; 3@3\n", text);
    }

    @Test
    public void testAnnotationBeanToText() {
        final Map<String, String> columns = new LinkedHashMap<String, String>();
        columns.put("diseases", "Disease");
        columns.put("itemTypes", "Item Type");
        columns.put("items", "Item Barcode");
        columns.put("annotationCategory.annotationClassification", "Annotation Classification");
        columns.put("annotationCategory", "Annotation Category");
        columns.put("notes", "Annotation Notes");
        columns.put("dateCreated", "Date Created");
        columns.put("createdBy", "Created By");
        columns.put("status", "Status");

        final DccAnnotation annotation = new DccAnnotation();

        annotation.setCreatedBy("me");
        final DccAnnotationItem item = new DccAnnotationItem();
        item.setItem("tcga-barcode");
        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        itemType.setItemTypeName("testObject");
        item.setItemType(itemType);
        final Tumor disease = new Tumor();
        disease.setTumorName("TST");
        item.setDisease(disease);
        annotation.setItems(Arrays.asList(item));
        final DccAnnotationCategory category = new DccAnnotationCategory();
        category.setCategoryName("some category");
        final DccAnnotationClassification classification = new DccAnnotationClassification();
        classification.setAnnotationClassificationName("TEST");
        category.setAnnotationClassification(classification);
        annotation.setAnnotationCategory(category);

        final String text = BeanToTextExporter.beanListToText("tab", out, columns, Arrays.asList(annotation), dateFormat);
        assertEquals("Disease\tItem Type\tItem Barcode\tAnnotation Classification\tAnnotation Category\tAnnotation Notes\tDate Created\tCreated By\tStatus\n" +
                "TST\ttestObject\ttcga-barcode\tTEST\tsome category\t\t\tme\tApproved\n", text);
    }

    private static final Map<String, String> MOCK_ERROR_MAP = new LinkedHashMap<String, String>() {{
        put("nonExistentMemberOfBeanCreatingException", "ARCHIVE_NAME");
        put("dateAdded", "DATE_ADDED");
        put("deployLocation", "ARCHIVE_URL");
    }};

    private static final Map<String, String> MOCK_DATE_MAP = new LinkedHashMap<String, String>() {{
        put("realName", "ARCHIVE_NAME");
        put("dateAdded", "DATE_ADDED");
        put("deployLocation", "ARCHIVE_URL");
    }};

    public List<Archive> makeMockArchive() {
        List<Archive> list = new LinkedList<Archive>();
        Archive arch = new Archive();
        arch.setDeployLocation("mock archive url");
        arch.setRealName("mock archive name");
        arch.setDateAdded(new Date(123456789));
        list.add(arch);

        return list;
    }
    
}//End of Class
