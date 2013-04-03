/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util;

import com.google.gwt.user.client.ui.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.FilterPanel;

/**
 * A panel that has a top bar that has a button to allow the main content to be hidden or shown.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class CloseablePanel extends DockPanel {
    final String expandImage = FilterPanel.IMAGES_PATH + "expandDown.gif";
    final String collapseImage = FilterPanel.IMAGES_PATH + "expandUp.gif";

    private SimplePanel contentPanel = new SimplePanel();
    private Widget content;
    private Label headerLabel;
    private Image toggleImage;

    public interface PanelRemovedCallback {
        public void panelRemoved();
    }

    public CloseablePanel(String headerText, String headerStyle) {
        this(headerText, headerStyle, false, null);
    }

    public CloseablePanel(String headerText, String headerStyle, boolean allowRemove, final PanelRemovedCallback callback) {
        HorizontalPanel header = new HorizontalPanel();
        header.setWidth("100%");
        header.addStyleName(headerStyle);

        HorizontalPanel togglePanel = new HorizontalPanel();

        toggleImage = new Image(collapseImage);
        toggleImage.addStyleName("action");
        toggleImage.setTitle("Click to hide details");
        ClickListener toggleClickListener = new ClickListener() {
            public void onClick(Widget sender) {
                if (content.isVisible()) {
                    collapse();
                } else {
                    expand();
                }
            }
        };
        toggleImage.addClickListener(toggleClickListener);
        headerLabel = new Label(headerText);
        headerLabel.addClickListener(toggleClickListener);
        headerLabel.addStyleName("actionNoUnderline");
        headerLabel.addStyleName("closeableHeader");
        togglePanel.add(toggleImage);
        togglePanel.add(headerLabel);
        togglePanel.setWidth("100%");

        togglePanel.setCellHorizontalAlignment(toggleImage, HasHorizontalAlignment.ALIGN_LEFT);
        togglePanel.setCellVerticalAlignment(toggleImage, HasVerticalAlignment.ALIGN_MIDDLE);
        togglePanel.setCellHorizontalAlignment(header, HasHorizontalAlignment.ALIGN_LEFT);
        togglePanel.setCellVerticalAlignment(header, HasVerticalAlignment.ALIGN_MIDDLE);
        togglePanel.setCellWidth(toggleImage, "20px");

        header.add(togglePanel);

        if (allowRemove) {
            Image removeImage = new Image(FilterPanel.IMAGES_PATH + "red-x.gif");
            removeImage.addStyleName("action");
            removeImage.setTitle("Click to remove selection");
            final CloseablePanel self = this;
            removeImage.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                    self.setVisible(false);
                    if (callback != null) {
                        callback.panelRemoved();
                    }
                }
            });
            header.add(removeImage);
            header.setCellHorizontalAlignment(removeImage, HasHorizontalAlignment.ALIGN_RIGHT);
            header.setCellVerticalAlignment(removeImage, HasVerticalAlignment.ALIGN_MIDDLE);
        }

        this.add(header, DockPanel.NORTH);
        contentPanel.setWidth("100%");
        this.add(contentPanel, DockPanel.CENTER);
    }

    public void setContent(Widget content) {
        this.content = content;
        this.contentPanel.add(content);
    }

    public Label getHeaderLabel() {
        return headerLabel;
    }

    public void collapse() {
        setWidth(getOffsetWidth() + "px");
        toggleImage.setUrl(expandImage);
        toggleImage.setTitle("Click to expand");
        // if collapsed representation is null, that means pretend it doesn't have one for now
        if (content instanceof HasCollapsedRepresentation && ((HasCollapsedRepresentation) content).getCollapsedRepresentation() != null) {
            //replace content with collapsed representation
            content.setVisible(false);
            contentPanel.remove(content);
            ((HasCollapsedRepresentation) content).collapse();
            Widget collapseContent = ((HasCollapsedRepresentation) content).getCollapsedRepresentation();
            collapseContent.setWidth("100%");
            String expansionLinkText = ((HasCollapsedRepresentation) content).getExpansionLinkText();
            if (expansionLinkText != null) {
                //use this text for the link that expands back to original content
                Hyperlink link = new Hyperlink(expansionLinkText, "_");
                link.addClickListener(new ClickListener() {
                    public void onClick(Widget sender) {
                        expand();
                    }
                });
                link.addStyleName("center");
                link.addStyleName("padding5px");
                VerticalPanel vpanel = new VerticalPanel();
                vpanel.add(collapseContent);
                vpanel.add(link);
                collapseContent = vpanel;
                vpanel.setWidth("100%");
            }
            contentPanel.clear();
            contentPanel.add(collapseContent);
        } else {
            content.setVisible(false);
        }
    }

    public void expand() {
        if (content instanceof HasCollapsedRepresentation && ((HasCollapsedRepresentation) content).getCollapsedRepresentation() != null) {
            //put regular content back in
            contentPanel.remove(contentPanel.getWidget());
            contentPanel.add(content);
            ((HasCollapsedRepresentation) content).expand();
        }
        content.setVisible(true);
        toggleImage.setUrl(collapseImage);
        toggleImage.setTitle("Click to collapse");
    }

}
