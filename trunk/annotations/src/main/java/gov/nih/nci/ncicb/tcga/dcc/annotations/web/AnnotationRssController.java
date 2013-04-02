package gov.nih.nci.ncicb.tcga.dcc.annotations.web;

import gov.nih.nci.ncicb.tcga.dcc.annotations.service.AnnotationRssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for RSS feed.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Controller
public class AnnotationRssController {
    @Autowired
    private AnnotationRssService annotationRssService;

    @RequestMapping(value="/annotations.rss", method= RequestMethod.GET)
    public String getFeed(final ModelMap model) {
        model.put("annotations", annotationRssService.getAnnotationsForFeed());
        return "rss";
    }

    public void setAnnotationRssService(final AnnotationRssService annotationRssService) {
        this.annotationRssService = annotationRssService;
    }
}
