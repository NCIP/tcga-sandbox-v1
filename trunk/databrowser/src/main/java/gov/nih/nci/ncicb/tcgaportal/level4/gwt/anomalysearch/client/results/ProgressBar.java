/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.user.client.ui.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;

/**
 * ProgressBar for displaying search progress
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class ProgressBar extends Composite {
    protected SimplePanel progressBar = new SimplePanel();
    protected HorizontalPanel progressBarOuter = new HorizontalPanel();
    protected HorizontalPanel progressPanel = new HorizontalPanel();
    protected HTML progressPercent = new HTML("&nbsp;0%");

    public ProgressBar() {
        VerticalPanel mainPanel = new VerticalPanel();
        initWidget(mainPanel);
        setupProgressPanel();
        mainPanel.add(progressPanel);
        progressPanel.setWidth("100%");
    }

    public void updateProgress(Results results) {
        if (!results.isFinalRowCount()) {
            progressPanel.setVisible(true);
            double percentDone = ((double) results.getRowsSearched() / (double) results.getRowsToSearch());
            progressPercent.setHTML("&nbsp;" + (int) (percentDone * 100) + "%");
            int progressWidth = (int) (percentDone * (double) progressBarOuter.getOffsetWidth());
            progressBar.setWidth(progressWidth + "px");
        } else {
            progressPanel.setVisible(false);
        }
    }

    private void setupProgressPanel() {
        Label progressLabel = new Label();
        progressLabel.setText("Search progress: ");
        progressBarOuter.setStyleName("progressBarOuter");
        progressBarOuter.setWidth("200px");
        progressBar.setStyleName("progressBar");
        progressBar.setWidth("1px");
        progressBar.add(new HTML("&nbsp;"));
        progressBarOuter.add(progressBar);
        progressPanel.add(progressLabel);
        progressPanel.add(progressBarOuter);
        progressPanel.add(progressPercent);
        progressBarOuter.setCellHorizontalAlignment(progressBar, HasHorizontalAlignment.ALIGN_LEFT);

        progressPanel.setCellVerticalAlignment(progressLabel, HasVerticalAlignment.ALIGN_MIDDLE);
        progressPanel.setCellVerticalAlignment(progressBarOuter, HasVerticalAlignment.ALIGN_MIDDLE);
        progressPanel.setCellHorizontalAlignment(progressLabel, HasHorizontalAlignment.ALIGN_RIGHT);
        progressPanel.setCellHorizontalAlignment(progressBarOuter, HasHorizontalAlignment.ALIGN_LEFT);
    }
}
