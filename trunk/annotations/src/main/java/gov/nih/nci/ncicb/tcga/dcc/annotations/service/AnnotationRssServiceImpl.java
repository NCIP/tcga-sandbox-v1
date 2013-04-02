package gov.nih.nci.ncicb.tcga.dcc.annotations.service;

import gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache.Cached;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

/**
 * Implementation of service for Annotation RSS feed.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class AnnotationRssServiceImpl implements AnnotationRssService {
    private Integer monthsToSearch = 3;  // default is to show last 3 months of annotations

    @Autowired
    private AnnotationQueries annotationQueries;

    /**
     * Get annotations to show in feed.  Currently takes no parameters, but in the future if different RSS feeds
     * (per disease or per classification/category) as desired could pass the params in here.
     *
     * @return list of annotations to show in RSS feed
     */
    public List<DccAnnotation> getAnnotationsForFeed() {
        return annotationQueries.searchAnnotations(getSearchCriteria());
    }

    public void setAnnotationQueries(final AnnotationQueries annotationQueries) {
        this.annotationQueries = annotationQueries;
    }

    protected AnnotationSearchCriteria getSearchCriteria() {
        final AnnotationSearchCriteria searchCriteria = new AnnotationSearchCriteria();
        final Calendar threeMonthsAgo = Calendar.getInstance();
        threeMonthsAgo.set(Calendar.HOUR_OF_DAY, 0);
        threeMonthsAgo.set(Calendar.MINUTE, 0);
        threeMonthsAgo.set(Calendar.SECOND, 0);
        threeMonthsAgo.set(Calendar.MILLISECOND, 0);
        threeMonthsAgo.add(Calendar.MONTH, -1 * getMonthsToSearch());

        searchCriteria.setEnteredAfter(threeMonthsAgo.getTime());
        return searchCriteria;
    }

    public void setMonthsToSearch(final Integer monthsToSearch) {
        this.monthsToSearch = monthsToSearch;
    }

    public Integer getMonthsToSearch() {
        return this.monthsToSearch;
    }
}
