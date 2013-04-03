package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes;

/**
 * Filter column type representing a correlation between two other types.
 *
 * @author David Nassau
 *         Last updated by: $Author: chenjw $
 * @version $Rev: 8774 $
 */

//todo  I don't think should extend NonMutationAnomalyType - correlations are not anomalies.
//todo  Has side effect of forcing us to deal with ratio thresholds even though they don't apply here.
public class CorrelationType extends ColumnType {
    private AnomalyType anomalyType1;
    private AnomalyType anomalyType2;
    private double pvalueLimit;
    private UpperAndLowerLimits limits = new UpperAndLowerLimits();

    public String getDisplayName() {
        if (super.getDisplayName() == null) {
            return anomalyType1.getDisplayName() + "/" + anomalyType2.getDisplayName();
        } else {
            return super.getDisplayName();
        }
    }

    public AnomalyType getAnomalyType1() {
        return anomalyType1;
    }

    public void setAnomalyType1(AnomalyType anomalyType1) {
        this.anomalyType1 = anomalyType1;
    }

    public AnomalyType getAnomalyType2() {
        return anomalyType2;
    }

    public void setAnomalyType2(AnomalyType anomalyType2) {
        this.anomalyType2 = anomalyType2;
    }

    public void setUpperOperator(UpperAndLowerLimits.Operator upperOperator) {
        limits.setUpperOperator(upperOperator);
    }

    public UpperAndLowerLimits.Operator getUpperOperator() {
        return limits.getUpperOperator();
    }

    public void setLowerOperator(UpperAndLowerLimits.Operator lowerOperator) {
        limits.setLowerOperator(lowerOperator);
    }

    public UpperAndLowerLimits.Operator getLowerOperator() {
        return limits.getLowerOperator();
    }

    public void setUpperLimit(double d) {
        limits.setUpperLimit(d);
    }

    public double getUpperLimit() {
        return limits.getUpperLimit();
    }

    public void setLowerLimit(double d) {
        limits.setLowerLimit(d);
    }

    public double getLowerLimit() {
        return limits.getLowerLimit();
    }

    public boolean passesCriteria(double value) {
        return limits.passesCriteria(value);
    }

    //can't use Object.clone() because GWT doesn't support it
    public Object cloneColumn() {
        CorrelationType column = (CorrelationType) super.cloneColumn();

        column.setAnomalyType1((AnomalyType) getAnomalyType1().cloneColumn());
        column.setAnomalyType2((AnomalyType) getAnomalyType2().cloneColumn());
        column.setPvalueLimit(getPvalueLimit());
        column.setDisplayName(getDisplayName());

        column.setLowerOperator(getLowerOperator());
        column.setLowerLimit(getLowerLimit());
        column.setUpperOperator(getUpperOperator());
        column.setUpperLimit(getUpperLimit());

        return column;
    }

    protected ColumnType instanceForClone() {
        return new CorrelationType();
    }

    public double getPvalueLimit() {
        return pvalueLimit;
    }

    public void setPvalueLimit(double pvalueLimit) {
        this.pvalueLimit = pvalueLimit;
    }

    //todo  side-effect of deriving from AnomalyType
    protected float getDefaultRatioThreshold() {
        return 0;
    }

    public String getDisplayCriteria(String formattedPValue) {
        UpperAndLowerLimits.Operator lowerOperator = limits.getLowerOperator();
        UpperAndLowerLimits.Operator upperOperator = limits.getUpperOperator();
        double lowerLimit = limits.getLowerLimit();
        double upperLimit = limits.getUpperLimit();
        StringBuilder sCrit = new StringBuilder();
        if (lowerOperator != UpperAndLowerLimits.Operator.None || upperOperator != UpperAndLowerLimits.Operator.None) {
            sCrit.append("rvalue ");
            if (lowerOperator != UpperAndLowerLimits.Operator.None) {
                sCrit.append(lowerOperator.toString()).append(' ').append(lowerLimit).append(' ');
                if (upperOperator != UpperAndLowerLimits.Operator.None) {
                    sCrit.append("or ");
                }
            }
            if (upperOperator != UpperAndLowerLimits.Operator.None) {
                sCrit.append(upperOperator.toString()).append(' ').append(upperLimit);
            }
        }
        if (formattedPValue != null && formattedPValue.length() > 0) {
            sCrit.append(sCrit.length() > 0 ? ", " : "").append("pvalue <= ").append(formattedPValue);
        }
        return sCrit.toString();
    }
}
