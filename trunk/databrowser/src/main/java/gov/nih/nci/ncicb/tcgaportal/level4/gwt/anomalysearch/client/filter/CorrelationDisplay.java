/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.SeleniumTags;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.WidgetHelper;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CorrelationType;

import java.util.ArrayList;

/**
 * @author Silpa Nanan
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */

public class CorrelationDisplay extends AnomalyDisplay {
    private VerticalPanel correlationPanel = new VerticalPanel();
    protected ArrayList<CorrelationWidget> selectedCorrelationWidgets = new ArrayList<CorrelationWidget>();

    CorrelationDisplay(FilterPanel filterPanel) {
        super(filterPanel);
        WidgetHelper.setDomId(addAnomalyButton, SeleniumTags.ADDCORR_BUTTON);
    }

    public VerticalPanel getCorrelationPanel() {
        return correlationPanel;
    }

    public ArrayList<CorrelationWidget> getSelectedCorrelationWidgets() {
        return selectedCorrelationWidgets;
    }

    protected boolean shouldInclude(ColumnType ctype) {
        return ctype instanceof CorrelationType;
    }

    protected AnomalyWidget makeNewWidget(ColumnType selectedColumnType, boolean isListByGene) {
        return new CorrelationWidget(selectedColumnType);
    }

}
