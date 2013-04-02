package gov.nih.nci.ncicb.tcga.dcc.annotations.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationSearchCriteria;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test for AnnotationRssServiceImpl
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AnnotationRssServiceImplFastTest {
    private final Mockery context = new JUnit4Mockery();
    private AnnotationRssServiceImpl rssService;
    private AnnotationQueries mockAnnotationQueries;

    @Before
    public void setUp() {
        mockAnnotationQueries = context.mock(AnnotationQueries.class);
        rssService = new AnnotationRssServiceImpl();
        rssService.setAnnotationQueries(mockAnnotationQueries);
        rssService.setMonthsToSearch(3);
    }

    @Test
    public void testGetSearchCriteria() {
        final AnnotationSearchCriteria criteria = rssService.getSearchCriteria();
        assertNotNull(criteria.getEnteredAfter());
        final Calendar threeMonthsAgo = Calendar.getInstance();
        threeMonthsAgo.set(Calendar.HOUR_OF_DAY, 0);
        threeMonthsAgo.set(Calendar.MINUTE, 0);
        threeMonthsAgo.set(Calendar.SECOND, 0);
        threeMonthsAgo.set(Calendar.MILLISECOND, 0);
        threeMonthsAgo.add(Calendar.MONTH, -1 * 3);
        assertEquals(threeMonthsAgo.getTime(), criteria.getEnteredAfter());
    }

    @Test
    public void testGetAnnotationsForFeed() throws Exception {
        final AnnotationSearchCriteria expectedCriteria = rssService.getSearchCriteria();
        final List<DccAnnotation> annotations = new ArrayList<DccAnnotation>();
        context.checking(new Expectations() {{
            one(mockAnnotationQueries).searchAnnotations(expectedCriteria);
            will(returnValue(annotations));
        }});
        rssService.getAnnotationsForFeed();
    }
}
