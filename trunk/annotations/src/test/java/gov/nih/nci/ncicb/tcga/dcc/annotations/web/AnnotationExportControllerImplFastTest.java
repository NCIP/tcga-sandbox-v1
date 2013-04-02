/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.annotations.web;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationCategory;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationClassification;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItem;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.util.BeanToTextExporter;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ExportUtils;
import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for class that implements annotation export controller.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AnnotationExportControllerImplFastTest {
    private Mockery context;
    private AnnotationExportControllerImpl annotationExportController;
    private HttpSession mockSession;
    private ModelMap modelMap;
    private List<DccAnnotation> searchResults;

    @Before
    public void setUp() throws Exception {
        context = new JUnit4Mockery();
        annotationExportController = new AnnotationExportControllerImpl();
        mockSession = context.mock(HttpSession.class);
        modelMap = new ModelMap();
        searchResults = new ArrayList<DccAnnotation>();
        DccAnnotation annotation = new DccAnnotation();
        DccAnnotationCategory category = new DccAnnotationCategory();
        DccAnnotationClassification classification = new DccAnnotationClassification();
        final DccAnnotationNote note1 = new DccAnnotationNote();
        final DccAnnotationNote note2 = new DccAnnotationNote();
        note1.setNoteText("this is my note1");
        note2.setNoteText("this\nis\tmy note2");
        List<DccAnnotationNote> notes = new LinkedList<DccAnnotationNote>() {{
            add(note1);
            add(note2);
        }};
        annotation.setNotes(notes);
        classification.setAnnotationClassificationName("this is a classification");
        category.setCategoryName("this is a category");
        category.setAnnotationClassification(classification);
        annotation.setAnnotationCategory(category);
        annotation.setApproved(true);
        annotation.setCreatedBy("me");
        Calendar march142011 = Calendar.getInstance();
        march142011.set(Calendar.MONTH, Calendar.APRIL);
        march142011.set(Calendar.DAY_OF_MONTH, 14);
        march142011.set(Calendar.YEAR, 2011);
        annotation.setDateCreated(march142011.getTime());
        annotation.setId(123L);
        DccAnnotationItem item1 = new DccAnnotationItem();
        DccAnnotationItemType itemType1 = new DccAnnotationItemType();
        itemType1.setItemTypeName("type1");
        item1.setItem("item1");
        item1.setItemType(itemType1);
        Tumor disease1 = new Tumor();
        disease1.setTumorName("dis1");
        item1.setDisease(disease1);
        annotation.addItem(item1);
        annotation.setId(123L);

        DccAnnotationItem item2 = new DccAnnotationItem();
        DccAnnotationItemType itemType2 = new DccAnnotationItemType();
        itemType2.setItemTypeName("type2");
        item2.setItemType(itemType2);
        item2.setItem("item2");
        Tumor disease2 = new Tumor();
        disease2.setTumorName("dis2");
        item2.setDisease(disease2);
        annotation.addItem(item2);

        searchResults.add(annotation);

        context.checking(new Expectations() {{
            one(mockSession).getAttribute(AnnotationControllerImpl.ATTRIBUTE_LAST_SEARCH_RESULTS);
            will(returnValue(searchResults));
        }});
    }

    @Test
    public void testExportSearchResultsTabText() throws Exception {

        PrintWriter writer = null;

        try {
            String viewName = annotationExportController.exportSearchResults(mockSession, modelMap, "tab");

            checkColumnHeadersAndAttributes();
            assertEquals("txt", viewName);
            assertEquals("tab", modelMap.get(ExportUtils.ATTRIBUTE_EXPORT_TYPE).toString());
            assertEquals("tcga_annotations.txt", modelMap.get(ExportUtils.ATTRIBUTE_FILE_NAME));

            StringWriter stringWriter = new StringWriter();
            //noinspection IOResourceOpenedButNotSafelyClosed
            writer = new PrintWriter(stringWriter);

            // check to make sure we can use the BeanToTextExporter on the resulting object
            String output = BeanToTextExporter.beanListToText("tab", writer,
                    (Map<String, String>) modelMap.get(ExportUtils.ATTRIBUTE_COLUMN_HEADERS),
                    (List) modelMap.get(ExportUtils.ATTRIBUTE_DATA),
                    (DateFormat) modelMap.get(ExportUtils.ATTRIBUTE_DATE_FORMAT));
            writer.close();
            assertEquals("ID\tDisease\tItem Type\tItem Barcode\tAnnotation Classification\tAnnotation Category\tAnnotation Notes\tDate Created\tCreated By\tStatus\n" +
                    "123\tdis1; dis2\ttype1; type2\titem1; item2\tthis is a classification\tthis is a category\tthis is my note1; this is my note2\t04/14/2011\tme\tApproved\n",
                    stringWriter.getBuffer().toString());
            assertFalse(output.equals("An error occurred."));
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    @Test
    public void testExportSearchResultsNoResults() throws Exception {
        searchResults = null;

        annotationExportController.exportSearchResults(mockSession, modelMap, "tab");
        assertNotNull(modelMap.get(ExportUtils.ATTRIBUTE_DATA));
    }

    @Test
    public void testExportSearchResultsXls() {
        String viewName = annotationExportController.exportSearchResults(mockSession, modelMap, "xl");
        assertEquals("xl", viewName);
        assertEquals("xl", modelMap.get(ExportUtils.ATTRIBUTE_EXPORT_TYPE).toString());
        assertEquals("tcga_annotations.xlsx", modelMap.get(ExportUtils.ATTRIBUTE_FILE_NAME));
    }

    private void checkColumnHeadersAndAttributes() {
        assertEquals(searchResults, modelMap.get(ExportUtils.ATTRIBUTE_DATA));
        assertTrue(modelMap.containsKey(ExportUtils.ATTRIBUTE_DATA));
        assertTrue(modelMap.containsKey(ExportUtils.ATTRIBUTE_DATE_FORMAT));
        assertTrue(modelMap.get(ExportUtils.ATTRIBUTE_DATE_FORMAT) instanceof DateFormat);
        assertTrue(modelMap.containsKey(ExportUtils.ATTRIBUTE_COLUMN_HEADERS));
        Map<String, String> columns = (Map<String, String>) modelMap.get(ExportUtils.ATTRIBUTE_COLUMN_HEADERS);
        assertEquals("Disease", columns.get("diseases"));
        assertEquals("Item Type", columns.get("itemTypes"));
        assertEquals("Annotation Category", columns.get("annotationCategory"));
        assertEquals("Annotation Classification", columns.get("annotationCategory.annotationClassification"));
        assertTrue(columns.get("items").equals("Item Barcode"));
        assertTrue(columns.get("dateCreated").equals("Date Created"));
        assertTrue(columns.get("createdBy").equals("Created By"));
        assertTrue(columns.get("notes").equals("Annotation Notes"));
        assertTrue(columns.get("status").equals("Status"));
        assertEquals("ID", columns.get("id"));


    }
}
