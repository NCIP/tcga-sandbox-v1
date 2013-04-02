package gov.nih.nci.ncicb.tcga.dcc.annotations.web.view;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItem;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Rss view for annotations.  Uses the ROME library to generate the XML returned.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class AnnotationRssView extends AbstractView {
    private static final String WEBSERVICE_PATH_VIEWANNOTATION = "/resources/viewannotation/xml/";
    private static final String TCGA_DCC = "TCGA DCC";
    private String applicationUrl;

    private static final String DATE_FORMAT_STRING = "MM/dd/yyyy h:mm a";

    public AnnotationRssView() {
        setContentType("application/xml; charset=UTF-8");
    }

    protected List<SyndEntry> makeEntries(final List<DccAnnotation> annotations) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
        final List<SyndEntry> entries = new ArrayList<SyndEntry>();
        for (final DccAnnotation annotation : annotations) {
            final SyndEntry entry = new SyndEntryImpl();
            entry.setAuthor(TCGA_DCC);
            entry.setTitle(makeEntryTitle(annotation));

            entry.setLink(getApplicationUrl() + WEBSERVICE_PATH_VIEWANNOTATION + annotation.getId());
            final SyndContent content = new SyndContentImpl();
            content.setType("text/html");
            content.setValue(makeContentText(annotation, simpleDateFormat));
            entry.setDescription(content);

            entries.add(entry);
        }
        return entries;
    }

    private String makeContentText(final DccAnnotation annotation, final SimpleDateFormat simpleDateFormat) {
        final StringBuilder entryContentHtml = new StringBuilder();

        entryContentHtml.append("<table>");
        for (final DccAnnotationItem item : annotation.getItems()) {
            entryContentHtml.append("<tr><td>").append(item.getItemType().getItemTypeName()).append(":</td><td>").append(item.getItem()).append("</td></tr>");
            entryContentHtml.append("<tr><td>Disease:</td><td>").append(item.getDisease().getTumorDescription()).append("</td></tr>");
        }
        entryContentHtml.append("<tr><td>Annotation Classification:</td><td>").
                append(annotation.getAnnotationCategory().getAnnotationClassification().getAnnotationClassificationName()).append("</td></tr>");
        entryContentHtml.append("<tr><td>Annotation Category:</td><td>").append(annotation.getAnnotationCategory().getCategoryName()).append("</td></tr>");
        entryContentHtml.append("<tr><td>Created:</td><td>").append(simpleDateFormat.format(annotation.getDateCreated())).append(" by ").
                append(annotation.getCreatedBy()).append("</td></tr>");
        if (!annotation.isNotesEmpty()) {
            entryContentHtml.append("<tr><td colspan=\"2\">Notes:</td></tr>");
            for (final DccAnnotationNote note : annotation.getNotes()) {
                entryContentHtml.append("<tr><td>").append(simpleDateFormat.format(note.getDateAdded())).append(" by ").append(note.getAddedBy()).append(":</td>");
                entryContentHtml.append("<td>").append(note.getNoteText()).append("</td></tr>");
            }
        }
        entryContentHtml.append("</table>");
        return entryContentHtml.toString();
    }

    private String makeEntryTitle(final DccAnnotation annotation) {
        return new StringBuilder().append(annotation.getDiseases().get(0).getTumorName()).append(" ").
                append(annotation.getItemTypes().get(0)).append(" ").append(annotation.getItems().get(0).getItem()).
                append(" ").append(annotation.getAnnotationCategory().getAnnotationClassification().getAnnotationClassificationName()).
                append(": ").append(annotation.getAnnotationCategory().getCategoryName()).toString();
    }

    @Override
    protected void renderMergedOutputModel(final Map model,
                                           final HttpServletRequest httpServletRequest,
                                           final HttpServletResponse httpServletResponse) throws Exception {

        final List<DccAnnotation> annotations = (List<DccAnnotation>) model.get("annotations");
        final SyndFeed feed = makeFeed(annotations);
        httpServletResponse.setContentType(getContentType());
        final SyndFeedOutput feedOutput = new SyndFeedOutput();
        feedOutput.output(feed, httpServletResponse.getWriter());
    }

    protected SyndFeed makeFeed(final List<DccAnnotation> annotations) {

        final SyndFeed feed = new SyndFeedImpl();
        feed.setAuthor(TCGA_DCC);
        feed.setTitle("TCGA DCC Annotations");
        feed.setDescription("Shows the latest approved annotations to TCGA samples");
        feed.setLink(getApplicationUrl() + "/rss.htm");
        feed.setFeedType("rss_2.0");

        final List<SyndEntry> entries = makeEntries(annotations);
        feed.setEntries(entries);

        return feed;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(final String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }
}
