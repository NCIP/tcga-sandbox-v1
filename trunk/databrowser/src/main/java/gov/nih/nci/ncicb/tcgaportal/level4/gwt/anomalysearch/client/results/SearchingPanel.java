/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.FilterPanel;

/**
 * A Vertical Panel that implements ResultsPanel and has a "Searching" message and graphic.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class SearchingPanel extends VerticalPanel implements ResultsPanel {
    private String title;
    private String tabId;

    public SearchingPanel(String title) {
        this(title, title);
    }

    public SearchingPanel(String title, String tabId) {
        this.title = title;
        this.tabId = tabId;

        VerticalPanel searching = new VerticalPanel();
        searching.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        searching.setStyleName("searchingDisplay");
        searching.add(new HTML("Searching..."));
        searching.add(new com.google.gwt.user.client.ui.Image(FilterPanel.IMAGES_PATH + "loader-transparent.gif"));
        searching.setWidth("100%");
        this.setHeight("200px");
        this.setWidth("100%");
        add(searching);
    }

    public String getTabText() {
        return title;
    }

    public String getTabId() {
        return tabId;
    }
}
