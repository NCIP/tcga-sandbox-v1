/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes;

/**
 * Represents an Expression column, whether gene or miRNA
 *
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */

public class ExpressionType extends NonMutationAnomalyType {
    public static float DEFAULT_RATIO_THRESHOLD = 0.4f;

    public ExpressionType() {
        super();
    }

    public ExpressionType(AnomalyType.GeneticElementType geType) {
        super(geType);
    }

    protected ColumnType instanceForClone() {
        return new ExpressionType();
    }

    protected float getDefaultRatioThreshold() {
        return DEFAULT_RATIO_THRESHOLD;
    }

    public String getDisplayCriteria(String formattedFrequency) {
        StringBuilder sCrit = new StringBuilder();
        sCrit.append(super.getDisplayCriteria());
        if (formattedFrequency != null && formattedFrequency.length() > 0) {
            sCrit.append(sCrit.length() > 0 ? ", " : "").append("Frequency >= ").append(formattedFrequency);
        }
        return sCrit.toString();
    }
}
