/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Used to represent upper and lower limit parts of a filter expression
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class UpperAndLowerLimits implements IsSerializable {

    public enum Operator {
        None() {
            public String toString() {
                return "";
            }
            public boolean evaluate(double left, double right) {
                return true;
            }
        },
        GT() {
            public String toString() {
                return ">";
            }
            public boolean evaluate(double left, double right) {
                return left > right;
            }
        },
        GTE() {
            public String toString() {
                return ">=";
            }
            public boolean evaluate(double left, double right) {
                return left >= right;
            }
        },
        LT() {
            public String toString() {
                return "<";
            }
            public boolean evaluate(double left, double right) {
                return left < right;
            }
        },
        LTE() {
            public String toString() {
                return "<=";
            }
            public boolean evaluate(double left, double right) {
                return left <= right;
            }
        };

        public abstract boolean evaluate(double left, double right);

        public static UpperAndLowerLimits.Operator fromString(String str) {
            if (GT.toString().equals(str)) {
                return GT;
            } else if (GTE.toString().equals(str)) {
                return GTE;
            } else if (LT.toString().equals(str)) {
                return LT;
            } else if (LTE.toString().equals(str)) {
                return LTE;
            } else {
                return None;
            }
        }
    }

    Operator lowerOperator, upperOperator;
    double lowerLimit, upperLimit;

    public Operator getLowerOperator() {
        return lowerOperator;
    }

    public void setLowerOperator(Operator lowerOperator) {
        this.lowerOperator = lowerOperator;
    }

    public Operator getUpperOperator() {
        return upperOperator;
    }

    public void setUpperOperator(Operator upperOperator) {
        this.upperOperator = upperOperator;
    }

    public double getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public boolean passesCriteria(double number) {
        if ((lowerOperator != null && lowerOperator != Operator.None) && (upperOperator != null && upperOperator != Operator.None)) {
            return passesLowerCriteria(number) || passesUpperCriteria(number);
        } else if (lowerOperator != null && lowerOperator != Operator.None) {
            return passesLowerCriteria(number);
        } else if (upperOperator != null && upperOperator != Operator.None) {
            return passesUpperCriteria(number);
        }
        // if got here, no limits set
        return true;
    }

    private boolean passesLowerCriteria(double number) {
        return lowerOperator.evaluate(number, lowerLimit);
    }

    private boolean passesUpperCriteria(double number) {
        return upperOperator.evaluate(number, upperLimit);
    }
}