package gov.nih.nci.ncicb.tcga.dcc.annotations.web;

import gov.nih.nci.ncicb.tcga.dcc.annotations.service.AnnotationRssService;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test for AnnotationRssController
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AnnotationRssControllerFastTest {
    private final Mockery context = new JUnit4Mockery();
    private AnnotationRssController controller;
    private AnnotationRssService mockRssService;

    @Before
    public void setUp() {
        mockRssService = context.mock(AnnotationRssService.class);
        controller = new AnnotationRssController();
        controller.setAnnotationRssService(mockRssService);
    }


    @Test
    public void test() {
        final List<DccAnnotation> annotations = new ArrayList<DccAnnotation>();
        context.checking(new Expectations() {{
            one(mockRssService).getAnnotationsForFeed();
            will(returnValue(annotations));
        }});
        final ModelMap model = new ModelMap();
        final String viewName = controller.getFeed(model);
        assertEquals("rss", viewName);
        assertEquals(annotations, model.get("annotations"));
    }
}
