/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;

/**
 * @author Silpa Nanan
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public class CorrelationWidget extends AnomalyWidget {
    static final double DEFAULT_PVALUE_LIMIT = 0.05;

    CorrelationWidget(ColumnType cType) {
        super(cType);
        this.cType = cType;
    }

    protected double getDefaultFrequency() {
        return DEFAULT_PVALUE_LIMIT;
    }

    protected boolean hasFrequencyWidget() {
        return true;
    }

    protected boolean hasRangeWidget() {
        return true;
    }

    protected String getRangeText() {
        return "with p-value <=";
    }

    protected String getRangeUnits() {
        return "";
    }

    public float getFrequency() {
        return (float) getPValueLimit();
    }

    protected AnomalyWidget instanceForClone() {
        return new CorrelationWidget(cType);
    }

    public double getPValueLimit() {
        String limitText = frequencyTextBox.getText();
        if (limitText.trim().length() == 0) {
            return -1;
        } else {
            try {
                return Double.parseDouble(limitText);
            } catch (NumberFormatException ex) {
                return -1;
            }
        }
    }
}
