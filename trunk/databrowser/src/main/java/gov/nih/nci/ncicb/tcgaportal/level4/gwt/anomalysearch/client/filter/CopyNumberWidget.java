/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.StyleConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.WidgetHelper;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;


/**
 * Widget for copy number columns
 *
 * @author Silpa Nanan
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */

public class CopyNumberWidget extends NonMutationAnomalyWidget {
    protected CheckBox gisticCheckBox;

    public CopyNumberWidget(final ColumnType cType) {
        super(cType);
        gisticCheckBox = new CheckBox("Avg. Across Patients");
        gisticCheckBox.addStyleName(StyleConstants.MARGIN_BOTTOM_5PX);
        gisticCheckBox.addStyleName(StyleConstants.MARGIN_LEFT_10PX);
        frequencyPanel.add(gisticCheckBox);
        setUpGisticClickListeners();
    }

    public AnomalyWidget cloneWidget() {

        CopyNumberWidget clone = (CopyNumberWidget) super.cloneWidget();
        clone.setGisticCheckboxEnabled(gisticCheckBox.isEnabled());
        clone.gisticCheckBox.setChecked(gisticCheckBox.isChecked());
        return clone;
    }

    protected AnomalyWidget instanceForClone() {
        return new CopyNumberWidget(cType);
    }

    public CopyNumberType.CalculationType getCalculationType() {
        if (gisticCheckBox.isChecked()) {
            return CopyNumberType.CalculationType.GISTIC;
        } else {
            return CopyNumberType.CalculationType.Regular;
        }
    }

    public void setGisticCheckboxEnabled(boolean b) {
        gisticCheckBox.setEnabled(b);
        if (!b) {
            gisticCheckBox.setChecked(false);
        }
    }

    public boolean isDoGistic() {
        return gisticCheckBox.isEnabled() && gisticCheckBox.isChecked();
    }

    private void setUpGisticClickListeners() {
        gisticCheckBox.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                //if gistic checkbox is checked, we disable the ratio text, since it won't be used in the search
                boolean gisticChecked = ((CheckBox) sender).isChecked();
                frequencyTextBox.setEnabled(!gisticChecked);
                WidgetHelper.setHtmlTextEnable(frequencyHTML, !gisticChecked);
                WidgetHelper.setHtmlTextEnable(frequencyPercentHtml, !gisticChecked);
            }
        });
    }

}
