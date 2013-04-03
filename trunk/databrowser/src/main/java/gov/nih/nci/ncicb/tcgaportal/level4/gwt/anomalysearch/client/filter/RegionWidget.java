/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import com.google.gwt.user.client.ui.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.StyleConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.WidgetHelper;

/**
 * Custom widget for gene regions.
 *
 * @author David Nassau
 *         Last updated by: $Author: jordanjm $
 * @version $Rev: 10259 $
 */

public class RegionWidget extends Composite {

    private CheckBox includeRegion;
    protected ListBox chromosome;
    protected TextBox start;
    protected TextBox stop;

    public RegionWidget() {
        includeRegion = new CheckBox("Chrom");
        includeRegion.setChecked(true);

        chromosome = new ListBox();
        chromosome.setWidth("55px");
        for (int ichrom = 1; ichrom <= 22; ichrom++) {
            chromosome.addItem(ichrom + "");
        }

        start = WidgetHelper.getTextBoxWithValidator();
        start.removeStyleName(StyleConstants.TEXTBOX_WIDTH);
        start.setVisibleLength(5);
        stop = WidgetHelper.getTextBoxWithValidator();
        stop.removeStyleName(StyleConstants.TEXTBOX_WIDTH);
        stop.setVisibleLength(5);

        HorizontalPanel geneRegionSubPanel = new HorizontalPanel();
        geneRegionSubPanel.add(includeRegion);
        geneRegionSubPanel.add(chromosome);
        geneRegionSubPanel.add(new HTML("Start"));
        geneRegionSubPanel.add(start);
        geneRegionSubPanel.add(new HTML("Stop"));
        geneRegionSubPanel.add(stop);
        geneRegionSubPanel.addStyleName("marginLeft10px");
        geneRegionSubPanel.setSpacing(3);
        initWidget(geneRegionSubPanel);
    }

    public void copyFrom(RegionWidget rw) {
        this.start.setText(rw.start.getText());
        this.stop.setText(rw.stop.getText());
        this.chromosome.setSelectedIndex(rw.chromosome.getSelectedIndex());
        this.includeRegion.setChecked(rw.includeRegion.isChecked());
    }

    public String getChromosome() {
        String ret = null;
        if (chromosome != null) {
            ret = chromosome.getItemText(chromosome.getSelectedIndex());
        }
        return ret;
    }

    public long getStart() {
        long ret = -1;
        if (start != null && start.getText() != null && start.getText().trim().length() > 0) {
            ret = Long.parseLong(start.getText().trim());
        }
        return ret;
    }

    public long getStop() {
        long ret = -1;
        if (stop != null && stop.getText() != null && stop.getText().trim().length() > 0) {
            ret = Long.parseLong(stop.getText().trim());
        }
        return ret;
    }

    public void setRegionEnabled(boolean b) {
        includeRegion.setEnabled(b);
        chromosome.setEnabled(b);
        start.setEnabled(b);
        stop.setEnabled(b);
    }

    public boolean includeInFilter() {
        return includeRegion.isChecked();
    }

}
