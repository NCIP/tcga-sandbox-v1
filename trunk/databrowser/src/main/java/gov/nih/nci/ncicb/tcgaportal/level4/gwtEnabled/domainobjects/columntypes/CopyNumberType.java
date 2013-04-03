/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes;

/**
 * Represents a CopyNumber column, either gene- or mirna-based.
 *
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public class CopyNumberType extends NonMutationAnomalyType {
    public static final float DEFAULT_RATIO_THRESHOLD = 0.2f;

    public enum CalculationType {
        Regular, GISTIC
    }

    private CopyNumberType.CalculationType calculationType;

    public CopyNumberType() {
        super();
    }

    public CopyNumberType(AnomalyType.GeneticElementType geType) {
        super(geType);
    }

    public CopyNumberType.CalculationType getCalculationType() {
        return calculationType;
    }

    public String getDisplayPlatformType() {
        return super.getDisplayPlatformType();
    }

    /**
     * Client sets this to specify whether the values should be calculated "normally" or using a GISTIC method.
     *
     * @param calculationType the calculation type
     */
    public void setCalculationType(CopyNumberType.CalculationType calculationType) {
        this.calculationType = calculationType;
    }

    public Object cloneColumn() {
        CopyNumberType column = (CopyNumberType) super.cloneColumn();
        column.setCalculationType(getCalculationType());
        return column;
    }

    protected ColumnType instanceForClone() {
        return new CopyNumberType();
    }

    protected float getDefaultRatioThreshold() {
        return DEFAULT_RATIO_THRESHOLD;
    }

    public String getDisplayCriteria(String formattedFrequency) {
        StringBuilder sCrit = new StringBuilder();
        sCrit.append(super.getDisplayCriteria());
        if (calculationType == CalculationType.GISTIC) {
            sCrit.append(sCrit.length() > 0 ? ", " : "").append("Average Across Patients");
        } else {

            if (formattedFrequency != null && formattedFrequency.length() > 0) {
                sCrit.append(sCrit.length() > 0 ? ", " : "").append("Frequency >= ").append(formattedFrequency);
            }
        }
        return sCrit.toString();
    }
}
