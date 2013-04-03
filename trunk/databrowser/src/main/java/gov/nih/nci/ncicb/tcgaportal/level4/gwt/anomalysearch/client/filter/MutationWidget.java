/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MutationType;

/**
 * Widget for mutation columns
 *
 * @author Silpa Nanan
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public class MutationWidget extends AnomalyWidget {

    MutationWidget(ColumnType cType) {
        super(cType);
    }

    protected double getDefaultFrequency() {
        return Math.round(((MutationType) cType).getFrequency() * 100);
    }

    protected boolean hasFrequencyWidget() {
        return true;
    }

    protected boolean hasRangeWidget() {
        return false;
    }

    protected String getRangeText() {
        return "Frequency >=";
    }

    protected String getRangeUnits() {
        return "%";
    }

    protected AnomalyWidget instanceForClone() {
        return new MutationWidget(cType);
    }

    public MutationType.Category getCategory() {
        return ((MutationType) cType).getCategory();
    }

}
