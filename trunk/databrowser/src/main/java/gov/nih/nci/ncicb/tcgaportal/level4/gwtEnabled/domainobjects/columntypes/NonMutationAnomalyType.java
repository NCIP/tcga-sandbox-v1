/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes;

/**
 * Column representing an anomaly which is not a mutation, e.g. copy number or expression.
 *
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */

//todo  limit values and operators should have defaults
public abstract class NonMutationAnomalyType extends AnomalyType {

    public NonMutationAnomalyType() {
        super();
    }

    public NonMutationAnomalyType(AnomalyType.GeneticElementType geType) {
        super(geType);
    }

    protected UpperAndLowerLimits limits = new UpperAndLowerLimits();

    public String getDisplayName() {
        if (super.getDisplayName() == null) {
            return getDisplayCenter() + " " + getDisplayPlatform();
        } else {
            return super.getDisplayName();
        }
    }

    public UpperAndLowerLimits.Operator getLowerOperator() {
        return limits.getLowerOperator();
    }

    /**
     * Client sets this to specify lower operator to use in the search.
     *
     * @param lowerOperator the lower operator
     */
    public void setLowerOperator(UpperAndLowerLimits.Operator lowerOperator) {
        limits.setLowerOperator(lowerOperator);
    }

    public double getLowerLimit() {
        return limits.getLowerLimit();
    }

    /**
     * Client sets to specify lower limit to use in the search.
     *
     * @param lowerLimit the lower limit
     */
    public void setLowerLimit(double lowerLimit) {
        limits.setLowerLimit(lowerLimit);
    }

    public UpperAndLowerLimits.Operator getUpperOperator() {
        return limits.getUpperOperator();
    }

    /**
     * Client sets to specify upper operator to use in the search.
     *
     * @param upperOperator the upper operator
     */
    public void setUpperOperator(UpperAndLowerLimits.Operator upperOperator) {
        limits.setUpperOperator(upperOperator);
    }

    public double getUpperLimit() {
        return limits.getUpperLimit();
    }

    /**
     * Client sets to specify upper limit to use in the search.
     *
     * @param upperLimit the upper limit
     */
    public void setUpperLimit(double upperLimit) {
        limits.setUpperLimit(upperLimit);
    }

    public boolean passesCriteria(double value) {
        return limits.passesCriteria(value);
    }

    public boolean equals(Object other) {
        if (!(other instanceof NonMutationAnomalyType)) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        NonMutationAnomalyType at = (NonMutationAnomalyType) other;
        return at.getLowerLimit() == getLowerLimit() &&
                at.getLowerOperator() == getLowerOperator() &&
                at.getUpperLimit() == getUpperLimit() &&
                at.getUpperOperator() == getUpperOperator();
    }

    public String getDisplayCriteria() {
        UpperAndLowerLimits.Operator lowerOperator = limits.getLowerOperator();
        UpperAndLowerLimits.Operator upperOperator = limits.getUpperOperator();
        double lowerLimit = limits.getLowerLimit();
        double upperLimit = limits.getUpperLimit();
        if (lowerOperator == UpperAndLowerLimits.Operator.None && upperOperator == UpperAndLowerLimits.Operator.None) {
            return "";
        }
        StringBuilder sCrit = new StringBuilder();
        if (lowerOperator != UpperAndLowerLimits.Operator.None) {
            sCrit.append(lowerOperator.toString()).append(' ').append(lowerLimit).append(' ');
            if (upperOperator != UpperAndLowerLimits.Operator.None) {
                sCrit.append("or ");
            }
        }
        if (upperOperator != UpperAndLowerLimits.Operator.None) {
            sCrit.append(upperOperator.toString()).append(' ').append(upperLimit);
        }
        return sCrit.toString();
    }

    public Object cloneColumn() {
        NonMutationAnomalyType column = (NonMutationAnomalyType) super.cloneColumn();
        column.setLowerOperator(getLowerOperator());
        column.setLowerLimit(getLowerLimit());
        column.setUpperOperator(getUpperOperator());
        column.setUpperLimit(getUpperLimit());
        return column;
    }
}
