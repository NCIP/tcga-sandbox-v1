package gov.nih.nci.ncicb.tcga.dcc.annotations.web.view;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationCategory;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationClassification;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItem;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for AnnotationRssView
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AnnotationRssViewFastTest {
    private final Mockery context = new JUnit4Mockery();
    private Map<String, Object> model;
    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private List<DccAnnotation> annotations;
    private AnnotationRssView rssView;
    private Tumor disease;
    private static final String SAMPLE_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy h:mm a");

    @Before
    public void setUp() throws Exception {
        rssView = new AnnotationRssView();
        rssView.setApplicationUrl("https://localhost:8443/annotations");
        mockRequest = context.mock(HttpServletRequest.class);
        mockResponse = context.mock(HttpServletResponse.class);

        model = new HashMap<String, Object>();
        disease = new Tumor();
        disease.setTumorName("GBM");
        disease.setTumorDescription("Glioblastoma multiforme");

        annotations = new ArrayList<DccAnnotation>();
        annotations.add(makeAnnotation(123L, "TCGA-01-1111", "patient", "tester", "04/22/2011 3:37 PM",
                "withdrew consent", "Notification", "information obtained from providing tissue source site"));
        annotations.add(makeAnnotation(456L, "TCGA-02-2222-01A", "sample", "DCC", "01/09/2011 8:01 AM",
                        "part of set", "CenterNotification", "sample is part of test set"));

        model.put("annotations", annotations);
    }

    @Test
    public void testContentType() {
        assertEquals("application/xml; charset=UTF-8", rssView.getContentType());
    }

    @Test
    public void testMakeFeed() {
        final SyndFeed feed = rssView.makeFeed(annotations);
        assertEquals("TCGA DCC", feed.getAuthor());
        assertEquals("TCGA DCC Annotations", feed.getTitle());
        assertEquals("rss_2.0", feed.getFeedType());
        assertEquals("https://localhost:8443/annotations/rss.htm", feed.getLink());
    }

    @Test
    public void testMakeEntries() {
        final List<SyndEntry> entries = rssView.makeEntries(annotations);
        assertEquals(2, entries.size());
        checkEntry(entries.get(0), 123L, "TCGA-01-1111", "patient", "tester", "04/22/2011 3:37 PM",
                "withdrew consent", "Notification", "information obtained from providing tissue source site");

        checkEntry(entries.get(1), 456L, "TCGA-02-2222-01A", "sample", "DCC", "01/09/2011 8:01 AM",
                        "part of set", "CenterNotification", "sample is part of test set");
    }

    @Test
    public void testRenderModel() throws Exception {
        final StringWriter stringWriter = new StringWriter();
        @SuppressWarnings({"IOResourceOpenedButNotSafelyClosed"}) final PrintWriter printWriter = new PrintWriter(stringWriter);

        try {
            context.checking(new Expectations() {{
                one(mockResponse).setContentType(rssView.getContentType());
                one(mockResponse).getWriter();
                will(returnValue(printWriter));
            }});

            rssView.renderMergedOutputModel(model, mockRequest, mockResponse);

            final File expectedFile = new File(SAMPLE_DIR + "rssView/expectedOutput.xml");
            final String expectedXml = FileUtil.readFile(expectedFile, false).trim();
            assertEquals(expectedXml, stringWriter.getBuffer().toString().trim());
        } finally {
            IOUtils.closeQuietly(printWriter);
        }
    }

      private DccAnnotation makeAnnotation(final Long annotationId, final String barcode, final String itemTypeName, final String createdBy,
                                         final String createdDateString, final String categoryName,
                                         final String classificationName, final String note) throws ParseException {
        final Date creationDate = SIMPLE_DATE_FORMAT.parse(createdDateString);
        final DccAnnotation annotation = new DccAnnotation();
        annotation.setId(annotationId);
        final DccAnnotationItem item = new DccAnnotationItem();
        final DccAnnotationCategory category = new DccAnnotationCategory();
        final DccAnnotationClassification classification = new DccAnnotationClassification();
        final DccAnnotationItemType itemType = new DccAnnotationItemType();
        itemType.setItemTypeName(itemTypeName);
        item.setItem(barcode);
        item.setItemType(itemType);
        item.setDisease(disease);
        category.setCategoryName(categoryName);
        classification.setAnnotationClassificationName(classificationName);
        category.setAnnotationClassification(classification);
        annotation.setAnnotationCategory(category);
        annotation.addNote(note, createdBy, creationDate);
        annotation.setItems(Arrays.asList(item));
        annotation.setCreatedBy(createdBy);
        annotation.setDateCreated(creationDate);

        return annotation;
    }

     private void checkEntry(final SyndEntry entry, final Long annotationId, final String barcode, final String itemType, final String createdBy,
                            final String createdDateString, final String category, final String classification, final String note) {

        // title should be like "GBM patient TCGA-01-0002 Notification: removed consent"
        assertEquals(disease.getTumorName() + " " + itemType + " " + barcode + " " + classification + ": " + category, entry.getTitle());
        assertEquals("TCGA DCC", entry.getAuthor());
        final String expectedContent =  "<table>" +
                        "<tr><td>" + itemType + ":</td><td>" + barcode + "</td></tr>" +
                        "<tr><td>Disease:</td><td>" + disease.getTumorDescription() +"</td></tr>" +
                        "<tr><td>Annotation Classification:</td><td>" + classification + "</td></tr>" +
                        "<tr><td>Annotation Category:</td><td>" + category + "</td></tr>" +
                        "<tr><td>Created:</td><td>" + createdDateString + " by " + createdBy + "</td></tr>" +
                        "<tr><td colspan=\"2\">Notes:</td></tr>" +
                        "<tr><td>" + createdDateString + " by " + createdBy + ":</td><td>" + note + "</td></tr>" +
                "</table>";
        assertEquals(expectedContent, entry.getDescription().getValue());
         assertEquals("https://localhost:8443/annotations/resources/viewannotation/xml/" + annotationId, entry.getLink());
    }
}
