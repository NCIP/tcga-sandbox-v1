/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;

/**
 * @author Silpa Nanan
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public abstract class NonMutationAnomalyDisplay extends AnomalyDisplay {
    protected ArrayList<NonMutationAnomalyWidget> nonMutationAnomalyWidgets = new ArrayList<NonMutationAnomalyWidget>();

    public NonMutationAnomalyDisplay(FilterPanel filterPanel) {
        super(filterPanel);
        setDomId(addAnomalyButton);
    }

    //for selenium testing
    protected abstract void setDomId(Widget w);


    public ArrayList<NonMutationAnomalyWidget> getNonMutationAnomalyWidgets() {
        return nonMutationAnomalyWidgets;
    }


}

