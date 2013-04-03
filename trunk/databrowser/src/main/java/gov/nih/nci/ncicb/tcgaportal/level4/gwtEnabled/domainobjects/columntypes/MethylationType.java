/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes;

/**
 * Represents a methylation column
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class MethylationType extends NonMutationAnomalyType {
    public static float DEFAULT_RATIO_THRESHOLD = 0.4f;

    public MethylationType() {
        super(AnomalyType.GeneticElementType.MethylationProbe);
    }

    protected ColumnType instanceForClone() {
        return new MethylationType();
    }

    protected float getDefaultRatioThreshold() {
        return DEFAULT_RATIO_THRESHOLD;
    }

    public String getDisplayCriteria(String formattedFrequency) {
        //no lower limit for methylation
        StringBuilder sCrit = new StringBuilder();
        UpperAndLowerLimits.Operator upperOperator = limits.getUpperOperator();
        double upperLimit = limits.getUpperLimit();
        if (upperOperator == UpperAndLowerLimits.Operator.None) {
            return "";
        }
        sCrit.append(upperOperator.toString()).append(' ').append(upperLimit);
        if (formattedFrequency != null && formattedFrequency.length() > 0) {
            sCrit.append(sCrit.length() > 0 ? ", " : "").append("Frequency >= ").append(formattedFrequency);
        }
        return sCrit.toString();
    }

    //always a no-op on the lower
    public UpperAndLowerLimits.Operator getLowerOperator() {
        return UpperAndLowerLimits.Operator.None;
    }

    public double getLowerLimit() {
        return 0.;
    }

    public void setGeneticElementType(GeneticElementType geneticElementType) {
        //stubbed - this column type can only be MethlationRegion
    }
}
