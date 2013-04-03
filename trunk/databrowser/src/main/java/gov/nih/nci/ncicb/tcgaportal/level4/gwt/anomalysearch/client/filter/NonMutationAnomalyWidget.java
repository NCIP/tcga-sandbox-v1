/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.AnomalyType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;

/**
 * @author Silpa Nanan
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */

public abstract class NonMutationAnomalyWidget extends AnomalyWidget {

    public NonMutationAnomalyWidget(final ColumnType cType) {
        super(cType);
    }

    protected double getDefaultFrequency() {
        return Math.round(100 * ((AnomalyType) cType).getFrequency());
    }

    protected boolean hasFrequencyWidget() {
        return true;
    }

    protected boolean hasRangeWidget() {
        return true;
    }

    protected String getRangeText() {
        return "Frequency >=";
    }

    protected String getRangeUnits() {
        return "%";
    }


}

