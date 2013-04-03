/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Hyperlink which contains information needed to sort a column.
 * Initially displays as plain text. When the client calls activateLink(),
 * switches to hyperlink display.
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class SortingLink extends HorizontalPanel {
    String linkText;
    long columnId;
    String annotationName;
    boolean initialAscending;
    boolean active;
    HTML linkLabel;
    TooltipListener ttListener;

//    public SortingLink(String text, long columnId, String annotationName, boolean initialAscending) {
//        this(text, columnId, annotationName, initialAscending, null);
//    }

    public SortingLink(String text, long columnId, String annotationName, boolean initialAscending, String tooltipText) {
        this.active = false;
        this.columnId = columnId;
        this.annotationName = annotationName;
        this.linkText = text;
        this.initialAscending = initialAscending;

        linkLabel = new HTML(linkText); //Label(linkText);
        linkLabel.addStyleName("action_not_yet"); //pointer cursor
        if (tooltipText != null) {
            ttListener = new TooltipListener(new HTML(tooltipText));
            linkLabel.addMouseListener(ttListener);
        }
        this.add(linkLabel);
    }

    public long getColumnId() {
        return columnId;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    //is this field first sorted ascending? Doesn't indicate its current sort order. That information is in the ModeController
    public boolean isInitialAscending() {
        return initialAscending;
    }

    public void activateLink(ClickListener listener) {
        if (!active) {
            active = true;
            linkLabel.removeStyleName("action_not_yet");
            linkLabel.addStyleName("action"); //add an underline to look like a link
            linkLabel.addClickListener(listener);

            //turn off tooltip when clicked
            linkLabel.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                    ttListener.hideTooltip();
                }
            });
        }
    }

    public void setTooltipText(String text) {
        TooltipListener ttListener = new TooltipListener(new HTML(text));
    }


}
