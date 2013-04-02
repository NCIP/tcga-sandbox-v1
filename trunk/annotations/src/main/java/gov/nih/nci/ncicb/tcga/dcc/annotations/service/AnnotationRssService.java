package gov.nih.nci.ncicb.tcga.dcc.annotations.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;

import java.util.List;

/**
 * Interface for service for Annotation RSS feed
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface AnnotationRssService {
    /**
     * Get annotations to show in feed.  Currently takes no parameters, but in the future if different RSS feeds
     * (per disease or per classification/category) as desired could pass the params in here.
     * @return list of annotations to show in RSS feed
     */
    public List<DccAnnotation> getAnnotationsForFeed();

}
