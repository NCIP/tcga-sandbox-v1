/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class InfoPanel extends VerticalPanel implements ResultsPanel {

    private String title;

    public InfoPanel(String title, Widget info) {
        this.title = title;
        add(info);
    }

    public String getTabText() {
        return title;
    }

    public String getTabId() {
        return title;
    }

}
