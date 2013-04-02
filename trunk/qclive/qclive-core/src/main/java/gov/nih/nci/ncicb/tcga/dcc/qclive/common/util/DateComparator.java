/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * This class holds the name of the 2 dates to be compared and the comparator to use for the comparison
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DateComparator {

    public static final String PRECISION_YEAR = "year";
    public static final String PRECISION_DAY = "day";
    public static final String PRECISION_MONTH = "month";

    private String leftOperandName;
    private String rightOperandName;
    private Operator operator;

    /**
     * Parses the input looking for a comparator string as a separator of 2 operand names.
     * The comparator can be any of: '==', '<', '>', '<=', '>=' or '<>'
     *
     * @param datesToCompare the dates names to be compared, separated by a valid comparator string
     * @throws IllegalArgumentException if the argument is invalid
     */
    public DateComparator(final String datesToCompare) {

        if(datesToCompare == null) {
            throw new IllegalArgumentException("'datesToCompare' parameter can not be null");
        }

        // Parsing datesToCompare
        int indexOfOperator;
        boolean operatorFound = false;

        for(final Operator operator : Operator.values()) {

            if(!operatorFound) {

                indexOfOperator = datesToCompare.indexOf(operator.getValue());

                if(indexOfOperator != -1) {

                    operatorFound = true;
                    
                    setOperator(operator);
                    setLeftOperandName(datesToCompare.substring(0, indexOfOperator).trim());
                    setRightOperandName(datesToCompare.substring(indexOfOperator + operator.getValue().length()).trim());
                }
            }
        }

        if(!operatorFound) {
            throw new IllegalArgumentException("No valid operator could be found in the following dates to compare: " + datesToCompare);

        } else if(StringUtils.isBlank(getLeftOperandName())) {
            throw new IllegalArgumentException("The left operand in the following dates to compare is blank: " + datesToCompare);

        } else if(StringUtils.isBlank(getRightOperandName())) {
            throw new IllegalArgumentException("The right operand in the following dates to compare is blank: " + datesToCompare);
        }
    }

    /**
     * Create a <code>DateComparator</code> with the given input
     *
     * @param leftOperandName the left operand name
     * @param rightOperandName the right operand name
     * @param operator the operator
     */
    public DateComparator(final String leftOperandName, final String rightOperandName, final Operator operator) {

        if(StringUtils.isBlank(leftOperandName)) {
            throw new IllegalArgumentException("Parameter 'leftOperandName' can not be blank");

        } else if(StringUtils.isBlank(rightOperandName)) {
            throw new IllegalArgumentException("Parameter 'rightOperandName' can not be blank");

        } else if(operator == null) {
            throw new IllegalArgumentException("Parameter 'operator' can not be null");
        }

        setOperator(operator);
        setLeftOperandName(leftOperandName);
        setRightOperandName(rightOperandName);
    }

    /**
     * Return <code>true</code> if the comparison date1 OPERAND date2 is true, or if any of the operand is null, <code>false</code> otherwise
     *
     * @param operandNameToValueMap the map that holds the values for each operand
     * @param operandPrecision precision for values in the operand map, key is operandName and value is day, month, or year. If null assume day.
     * @return <code>true</code> if the comparison date1 OPERAND date2 is true, or if any of the operand is null, <code>false</code> otherwise
     */
    public boolean compare(final Map<String, Date> operandNameToValueMap, final Map<String, String> operandPrecision) {

        final Calendar leftOperandValue = getLeftOperandValue(operandNameToValueMap);
        final Calendar rightOperandValue = getRightOperandValue(operandNameToValueMap);
        final String leastPrecision = getLeastPrecision(operandPrecision);

        switch(getOperator()) {
            case EQ:
                return isEqual(leftOperandValue, rightOperandValue, leastPrecision);
            case LT:
                return isLowerThan(leftOperandValue, rightOperandValue, leastPrecision);
            case GT:
                return isGreaterThan(leftOperandValue, rightOperandValue, leastPrecision);
            case LE:
                return isLowerThanOrEqual(leftOperandValue, rightOperandValue, leastPrecision);
            case GE:
                return isGreaterThanOrEqual(leftOperandValue, rightOperandValue, leastPrecision);
            case NE:
                return isNotEqual(leftOperandValue, rightOperandValue, leastPrecision);
            default: // Should not happen
                return false;
        }
    }

    private String getLeastPrecision(final Map<String, String> operandPrecision) {
        String leftOperandPrecision = operandPrecision.get(getLeftOperandName());
        if (leftOperandPrecision == null) {
            leftOperandPrecision = PRECISION_DAY;
        }
        String rightOperandPrecision = operandPrecision.get(getRightOperandName());
        if (rightOperandPrecision == null) {
            rightOperandPrecision = PRECISION_DAY;
        }

        if (PRECISION_YEAR.equals(leftOperandPrecision) || PRECISION_YEAR.equals(rightOperandPrecision)) {
            return PRECISION_YEAR;
        } else if (PRECISION_MONTH.equals(leftOperandPrecision) || PRECISION_MONTH.equals(rightOperandPrecision)) {
            return PRECISION_MONTH;
        } else {
            return PRECISION_DAY;
        }

    }

    /**
     * Return <code>true</code> if left operand is equal to right operand, or if any of the operand is null, <code>false</code> otherwise
     *
     *
     * @param leftOperandValue the <code>Calendar</code> value for the left operand
     * @param rightOperandValue the <code>Calendar</code> value for the right operand
     * @param leastPrecision the least precision of the two operands
     * @return <code>true</code> if left operand is equal to right operand, or if any of the operand is null, <code>false</code> otherwise
     */
    private boolean isEqual(final Calendar leftOperandValue, final Calendar rightOperandValue,
                            final String leastPrecision) {
        if (leftOperandValue == null || rightOperandValue == null) {
            return true;
        } else {

            if (PRECISION_YEAR.equals(leastPrecision)) {
                // compare only year, true if the same
                return leftOperandValue.get(Calendar.YEAR) == rightOperandValue.get(Calendar.YEAR);

            } else if (PRECISION_MONTH.equals(leastPrecision)) {
                // compare only month and year, true if the same
                return leftOperandValue.get(Calendar.YEAR) == rightOperandValue.get(Calendar.YEAR) &&
                        leftOperandValue.get(Calendar.MONTH) == rightOperandValue.get(Calendar.MONTH);
            } else {
                return leftOperandValue.equals(rightOperandValue);
            }
        }
    }


    /**
     * Return <code>true</code> if left operand is strictly before right operand, or if any of the operand is null, <code>false</code> otherwise
     *
     *
     * @param leftOperandValue the <code>Calendar</code> value for the left operand
     * @param rightOperandValue the <code>Calendar</code> value for the right operand
     * @param leastPrecision the least-precise precision of the two dates: day, month, or year
     * @return <code>true</code> if left operand is strictly before right operand, or if any of the operand is null, <code>false</code> otherwise
     */
    private boolean isLowerThan(final Calendar leftOperandValue, final Calendar rightOperandValue, final String leastPrecision) {
        if (leftOperandValue == null || rightOperandValue == null) {
            return true;
        } else {

            if (PRECISION_YEAR.equals(leastPrecision)) {
                // if year is the same, can't say for sure so return true, so don't just do < comparison
                return leftOperandValue.get(Calendar.YEAR) <= rightOperandValue.get(Calendar.YEAR);

            } else if (PRECISION_MONTH.equals(leastPrecision)) {
                return (leftOperandValue.get(Calendar.YEAR) < rightOperandValue.get(Calendar.YEAR)) ||
                        (leftOperandValue.get(Calendar.YEAR) == rightOperandValue.get(Calendar.YEAR) &&
                                leftOperandValue.get(Calendar.MONTH) <= rightOperandValue.get(Calendar.MONTH));
            } else {
                return leftOperandValue.before(rightOperandValue);
            }
        }
    }

    /**
     * Return <code>true</code> if left operand is strictly after right operand, or if any of the operand is null, <code>false</code> otherwise
     *
     *
     * @param leftOperandValue the <code>Calendar</code> value for the left operand
     * @param rightOperandValue the <code>Calendar</code> value for the right operand
     * @param leastPrecision the least precise precision of the 2 dates
     * @return <code>true</code> if left operand is strictly after right operand, or if any of the operand is null, <code>false</code> otherwise
     */
    private boolean isGreaterThan(final Calendar leftOperandValue, final Calendar rightOperandValue, final String leastPrecision) {
        if (leftOperandValue == null || rightOperandValue == null) {
            return true;
        } else {

            if (PRECISION_YEAR.equals(leastPrecision)) {
                // if the years are equal, or left year > right year then consider it true
                return leftOperandValue.get(Calendar.YEAR) >= rightOperandValue.get(Calendar.YEAR);

            } else if (PRECISION_MONTH.equals(leastPrecision)) {
                // if left year > right year then true; if left year = right year and left month >= right year then true
                return (leftOperandValue.get(Calendar.YEAR) > rightOperandValue.get(Calendar.YEAR)) ||
                        (leftOperandValue.get(Calendar.YEAR) == rightOperandValue.get(Calendar.YEAR) &&
                                leftOperandValue.get(Calendar.MONTH) >= rightOperandValue.get(Calendar.MONTH));

            } else {
                // day precision or unknown precision, use full date
                return leftOperandValue.after(rightOperandValue);
            }
        }
    }

    /**
     * Return <code>true</code> if left operand is on or before right operand, or if any of the operand is null, <code>false</code> otherwise
     *
     *
     * @param leftOperandValue the <code>Calendar</code> value for the left operand
     * @param rightOperandValue the <code>Calendar</code> value for the right operand
     * @param leastPrecision the least precise precision of the 2 dates
     * @return <code>true</code> if left operand is on or before right operand, or if any of the operand is null, <code>false</code> otherwise
     */
    private boolean isLowerThanOrEqual(final Calendar leftOperandValue, final Calendar rightOperandValue, final String leastPrecision) {
        if (leftOperandValue == null || rightOperandValue == null) {
            return true;
        } else {

            if (PRECISION_YEAR.equals(leastPrecision)) {
                // if year is the same, can't say for sure so return true
                return leftOperandValue.get(Calendar.YEAR) <= rightOperandValue.get(Calendar.YEAR);

            } else if (PRECISION_MONTH.equals(leastPrecision)) {
                return (leftOperandValue.get(Calendar.YEAR) < rightOperandValue.get(Calendar.YEAR)) ||
                        (leftOperandValue.get(Calendar.YEAR) == rightOperandValue.get(Calendar.YEAR) &&
                                leftOperandValue.get(Calendar.MONTH) <= rightOperandValue.get(Calendar.MONTH));
            } else {
                return !leftOperandValue.after(rightOperandValue);
            }
        }
    }

    /**
     * Return <code>true</code> if left operand is on or after right operand, or if any of the operand is null, <code>false</code> otherwise
     *
     *
     * @param leftOperandValue the <code>Calendar</code> value for the left operand
     * @param rightOperandValue the <code>Calendar</code> value for the right operand
     * @param leastPrecision the least precise precision of the 2 dates
     * @return <code>true</code> if left operand is on or after right operand, or if any of the operand is null, <code>false</code> otherwise
     */
    private boolean isGreaterThanOrEqual(final Calendar leftOperandValue, final Calendar rightOperandValue, final String leastPrecision) {
        if (leftOperandValue == null || rightOperandValue == null) {
            return true;
        } else {

            if (PRECISION_YEAR.equals(leastPrecision)) {
                return leftOperandValue.get(Calendar.YEAR) >= rightOperandValue.get(Calendar.YEAR);

            } else if (PRECISION_MONTH.equals(leastPrecision)) {
                return (leftOperandValue.get(Calendar.YEAR) > rightOperandValue.get(Calendar.YEAR)) ||
                        (leftOperandValue.get(Calendar.YEAR) == rightOperandValue.get(Calendar.YEAR) &&
                        leftOperandValue.get(Calendar.MONTH) >= rightOperandValue.get(Calendar.MONTH));
            } else {
                return !leftOperandValue.before(rightOperandValue);
            }
        }
    }

    /**
     * Return <code>true</code> if left operand is equal to right operand, or if any of the operand is null, <code>false</code> otherwise
     *
     *
     * @param leftOperandValue the <code>Calendar</code> value for the left operand
     * @param rightOperandValue the <code>Calendar</code> value for the right operand
     * @param leastPrecision the least precise precision of the 2 dates
     * @return <code>true</code> if left operand is equal to right operand, or if any of the operand is null, <code>false</code> otherwise
     */
    private boolean isNotEqual(final Calendar leftOperandValue, final Calendar rightOperandValue, final String leastPrecision) {
        if (leftOperandValue == null || rightOperandValue == null) {
            return true;
        } else {
            if (PRECISION_YEAR.equals(leastPrecision) || PRECISION_MONTH.equals(leastPrecision)) {
                // if not precise to the day, it is impossible to say for sure that the dates are not equal
                return true;
            } else {
                return !leftOperandValue.equals(rightOperandValue);
            }
        }
    }

    /**
     * An enumeration of possible comparisons operators
     */
    public enum Operator {

        EQ("=="),
        LE("<="),
        GE(">="),
        NE("<>"),
        // The next 2 operators need to be last in the enum
        // so that a match for them is only found if a match for one of the others is not found first
        LT("<"),
        GT(">");

        /**
         * The human readable value of this enum
         */
        private String value;

        private Operator(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    /**
     * Retrieve the value for the left operand by looking up the map for the entry with the left operand name
     *
     * @param operandNameToValueMap the map that holds the values for each operand
     * @return the <code>Calendar</code> for the left operand
     */
    private Calendar getLeftOperandValue(final Map<String, Date> operandNameToValueMap) {
        return getOperandValue(operandNameToValueMap, getLeftOperandName());
    }

    /**
     * Retrieve the value for the right operand by looking up the map for the entry with the right operand name
     *
     * @param operandNameToValueMap the map that holds the values for each operand
     * @return the <code>Calendar</code> for the right operand
     */
    private Calendar getRightOperandValue(final Map<String, Date> operandNameToValueMap) {
        return getOperandValue(operandNameToValueMap, getRightOperandName());
    }

    private Calendar getOperandValue(final Map<String, Date> operandNameToValueMap, final String operandName) {
        Calendar dateOperand = null;
        if (operandNameToValueMap != null) {
            Date operand = operandNameToValueMap.get(operandName);
            if (operand != null) {
                dateOperand = Calendar.getInstance();
                dateOperand.setTime(operand);
            }
        }
        return dateOperand;
    }

    @Override
    public String toString() {

        return new StringBuilder("'")
                .append(getLeftOperandName())
                .append("' ")
                .append(getOperator().getValue())
                .append(" '")
                .append(getRightOperandName())
                .append("'")
                .toString();
    }

    //
    // Getter / Setter
    //

    public String getLeftOperandName() {
        return leftOperandName;
    }

    public void setLeftOperandName(final String leftOperandName) {
        this.leftOperandName = leftOperandName;
    }

    public String getRightOperandName() {
        return rightOperandName;
    }

    public void setRightOperandName(final String rightOperandName) {
        this.rightOperandName = rightOperandName;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(final Operator operator) {
        this.operator = operator;
    }
}
