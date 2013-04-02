/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * DateComparator Unit test
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DateComparatorFastTest {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    private Map<String, Date> dateMap;
    private Map<String, String> precisionMap;


    @Before
    public void setUp() {
        dateMap = new HashMap<String, Date>();
        precisionMap = new HashMap<String, String>();
    }



    @Test
    public void testConstructorEqual() throws Exception {
        checkConstructor("a", "==", "b");
    }

    @Test
    public void testConstructorLowerThan() throws Exception {
        checkConstructor("a", "<", "b");
    }

    @Test
    public void testConstructorGreaterThan() throws Exception {
        checkConstructor("a", ">", "b");
    }

    @Test
    public void testConstructorLowerThanOrEqual() throws Exception {
        checkConstructor("a", "<=", "b");
    }

    @Test
    public void testConstructorGreaterThanOrEqual() throws Exception {
        checkConstructor("a", ">=", "b");
    }

    @Test
    public void testConstructorNotEqual() throws Exception {
        checkConstructor("a", "<>", "b");
    }

    @Test
    public void testConstructorEqualLeftOperandWhitespace() throws Exception {
        checkConstructor(" a ", "==", "b");
    }

    @Test
    public void testConstructorEqualRightOperandWhitespace() throws Exception {
        checkConstructor("a", "==", " b ");
    }

    @Test
    public void testConstructorEqualBothOperandWhitespace() throws Exception {
        checkConstructor(" a ", "==", " b ");
    }

    @Test
    public void testConstructorNoValidOperator() {

        final String datesToCompare = "a*b";
        final String expectedExceptionMessage = "No valid operator could be found in the following dates to compare: " + datesToCompare;

        checkBadParameter(datesToCompare, expectedExceptionMessage);
    }

    @Test
    public void testConstructorLeftOperandBlank() {

        final String datesToCompare = ">b";
        final String expectedExceptionMessage = "The left operand in the following dates to compare is blank: " + datesToCompare;

        checkBadParameter(datesToCompare, expectedExceptionMessage);
    }

    @Test
    public void testConstructorRightOperandBlank() {

        final String datesToCompare = "a>";
        final String expectedExceptionMessage = "The right operand in the following dates to compare is blank: " + datesToCompare;

        checkBadParameter(datesToCompare, expectedExceptionMessage);
    }

    @Test
    public void testConstructorDatesToCompareNull() {

        final String datesToCompare = null;
        final String expectedExceptionMessage = "'datesToCompare' parameter can not be null";

        checkBadParameter(datesToCompare, expectedExceptionMessage);
    }

    @Test
    public void testConstructorLeftOperandNameNull() {

        final String leftOperandName = null;
        final String rightOperandName = "b";
        final DateComparator.Operator operator = DateComparator.Operator.EQ;
        final String expectedExceptionMessage = "Parameter 'leftOperandName' can not be blank";

        checkSecondConstructorBadParameter(leftOperandName, rightOperandName, operator, expectedExceptionMessage);
    }

    @Test
    public void testConstructorRightOperandNameNull() {

        final String leftOperandName = "a";
        final String rightOperandName = null;
        final DateComparator.Operator operator = DateComparator.Operator.EQ;
        final String expectedExceptionMessage = "Parameter 'rightOperandName' can not be blank";

        checkSecondConstructorBadParameter(leftOperandName, rightOperandName, operator, expectedExceptionMessage);
    }

    @Test
    public void testConstructorOperatorNull() {

        final String leftOperandName = "a";
        final String rightOperandName = "b";
        final DateComparator.Operator operator = null;
        final String expectedExceptionMessage = "Parameter 'operator' can not be null";

        checkSecondConstructorBadParameter(leftOperandName, rightOperandName, operator, expectedExceptionMessage);
    }

    /**
     * Call the 2nd constructor with an argument that will throw an Exception
     *
     * @param leftOperandName the left operand name
     * @param rightOperandName the right operand name
     * @param operator the operator
     * @param expectedExceptionMessage the expected exception message
     */
    private void checkSecondConstructorBadParameter(final String leftOperandName,
                                                    final String rightOperandName,
                                                    final DateComparator.Operator operator,
                                                    final String expectedExceptionMessage) {
        DateComparator dateComparator = null;

        try {
            dateComparator = new DateComparator(leftOperandName, rightOperandName, operator);
            fail("Exception should have been thrown");

        } catch (final Exception e) {

            assertNull(dateComparator);
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    @Test
    public void testCompareEquals() throws Exception {
        checkCompareEquals(DateComparator.Operator.EQ, 0);
    }

    @Test
    public void testCompareNotEqualsWhenLowerThan() throws Exception {
        checkCompareEquals(DateComparator.Operator.NE, 1000);
    }

    @Test
    public void testCompareNotEqualsWhenGreaterThan() throws Exception {
        checkCompareEquals(DateComparator.Operator.NE, -1000);
    }

    @Test
    public void testCompareLowerThan() throws Exception {
        checkCompareEquals(DateComparator.Operator.LT, 1000);
    }

    @Test
    public void testCompareGreaterThan() throws Exception {
        checkCompareEquals(DateComparator.Operator.GT, -1000);
    }

    @Test
    public void testCompareLowerThanOrEqualsWhenEquals() throws Exception {
        checkCompareEquals(DateComparator.Operator.LE, 0);
    }

    @Test
    public void testCompareLowerThanOrEqualsWhenLowerThan() throws Exception {
        checkCompareEquals(DateComparator.Operator.LE, 1000);
    }

    @Test
    public void testCompareGreaterThanOrEqualsWhenEquals() throws Exception {
        checkCompareEquals(DateComparator.Operator.GE, 0);
    }

    @Test
    public void testCompareGreaterThanOrEqualsWhenGreaterThan() throws Exception {
        checkCompareEquals(DateComparator.Operator.GE, -1000);
    }

    /**
     * Create a <code>DateComparator</code> with the given operator and 2 Dates with an offset
     *
     * @param operator the operator
     * @param offset the offset of the right operand value
     * @throws Exception
     */
    private void checkCompareEquals(final DateComparator.Operator operator, final long offset) throws Exception {

        final Map<String, Date> operandNameToValueMap = new HashMap<String, Date>();
        final String leftOperandName = "a";
        final String rightOperandName = "b";
        final Date leftOperandValue = new Date(0);
        final Date rightOperandValue = new Date(0 + offset);
        operandNameToValueMap.put(leftOperandName, leftOperandValue);
        operandNameToValueMap.put(rightOperandName, rightOperandValue);

        final Map<String, String> precisionMap = new HashMap<String, String>();

        final DateComparator dateComparator = new DateComparator(leftOperandName, rightOperandName, operator);
        assertTrue(dateComparator.compare(operandNameToValueMap, precisionMap));
    }

    /**
     * Call the constructor with an argument that will throw an Exception
     *
     * @param datesToCompare the datesToCompare parameter
     * @param expectedExceptionMessage the expected Exception message
     */
    private void checkBadParameter(final String datesToCompare, final String expectedExceptionMessage) {
        try {
            new DateComparator(datesToCompare);
            fail("Exception should have been thrown");

        } catch(final Exception e) {
            assertEquals(expectedExceptionMessage, e.getMessage());
        }
    }

    /**
     * Call the constructor with the argument being made of the concatenation of the given left operand name, operator and right operand name,
     * and check assertions
     *
     * @param leftOperandName the left operand name
     * @param operator the operator
     * @param rightOperandName the right operand name
     * @throws Exception
     */
    private void checkConstructor(final String leftOperandName, final String operator, final String rightOperandName) throws Exception{

        final DateComparator dateComparator = new DateComparator(leftOperandName + operator + rightOperandName);

        assertNotNull(dateComparator);
        assertEquals(leftOperandName.trim(), dateComparator.getLeftOperandName());
        assertEquals(rightOperandName.trim(), dateComparator.getRightOperandName());
        assertEquals(operator, dateComparator.getOperator().getValue());
    }

    @Test
    public void testCompareEqualsWithPrecision() throws ParseException {
        // 11/01/2000 = 11/01/2000
        runCompareWithPrecision(DateComparator.Operator.EQ, "11/01/2000", "11/01/2000", "day", "day", true);
        // 11/2000 = 11/18/2000
        runCompareWithPrecision(DateComparator.Operator.EQ, "11/01/2000", "11/18/2000", "month", "day", true);
        // 1999 = 12/12/1999
        runCompareWithPrecision(DateComparator.Operator.EQ, "05/03/1999", "12/12/1999", "year", "day", true);
        // 01/02/1985 != 01/03/1985
        runCompareWithPrecision(DateComparator.Operator.EQ, "01/02/1985", "01/03/1985", "day", "day", false);
        // 06/1997 != 07/1997
        runCompareWithPrecision(DateComparator.Operator.EQ, "06/10/1997", "07/10/1997", "month", "month", false);
        // 01/01/2000 != 1999
        runCompareWithPrecision(DateComparator.Operator.EQ, "01/01/2000", "01/01/1999", "day", "year", false);
    }



    @Test
    public void testCompareGreaterWithPrecision() throws ParseException {
        // 01/02/2012 > 01/01/2012
        runCompareWithPrecision(DateComparator.Operator.GT, "01/02/2012", "01/01/2012", "day", "day", true);
        // 01/01/2012 !> 01/15/2012
        runCompareWithPrecision(DateComparator.Operator.GT, "01/01/2012", "01/15/2012", "day", "day", false);
        // 01/10/2012 > 01/2012 (can't say false for sure, so true
        runCompareWithPrecision(DateComparator.Operator.GT, "01/10/2012", "01/15/2012", "day", "month", true);
        // 02/2012 > 01/2012
        runCompareWithPrecision(DateComparator.Operator.GT, "02/01/2012", "01/01/2012", "month", "month", true);
        // 10/10/2012 > 01/2011
        runCompareWithPrecision(DateComparator.Operator.GT, "10/10/2012", "01/15/2011", "day", "month", true);
        // 01/01/2012 !> 02/2012
        runCompareWithPrecision(DateComparator.Operator.GT, "01/01/2012", "02/15/2012", "day", "month", false);
        // 2011 > 10/15/2011 (can't say false for sure, so true)
        runCompareWithPrecision(DateComparator.Operator.GT, "01/01/2011", "10/15/2011", "year", "day", true);
        // 2010 !> 10/15/2011
        runCompareWithPrecision(DateComparator.Operator.GT, "01/01/2010", "10/15/2011", "year", "day", false);
    }

    @Test
    public void testLessThanWithPrecision() throws ParseException {
        // 01/01/1972 < 12/31/2011
        runCompareWithPrecision(DateComparator.Operator.LT, "01/01/1972", "12/31/2011", "day", "day", true);
        // 12/31/2011 !< 12/15/2011
        runCompareWithPrecision(DateComparator.Operator.LT, "12/31/2011", "12/15/2011", "day", "day", false);
        // 09/20/2010 < 09/2010 (can't say false for sure, so true)
        runCompareWithPrecision(DateComparator.Operator.LT, "09/20/2010", "09/01/2010", "day", "month", true);
        // 09/01/2010 !< 08/2010
        runCompareWithPrecision(DateComparator.Operator.LT, "09/01/2010", "08/01/2010", "day", "month", false);
        // 06/15/1995 < 1995 (can't say false for sure, so true)
        runCompareWithPrecision(DateComparator.Operator.LT, "06/15/1995", "01/01/1995", "day", "year", true);
        // 03/26/2006 !< 2005
        runCompareWithPrecision(DateComparator.Operator.LT, "03/26/2006", "01/01/2005", "day", "year", false);
    }

    @Test
    public void testLessThanOrEqualsWithPrecision() throws ParseException {
        // 01/01/1972 <= 12/31/2011
        runCompareWithPrecision(DateComparator.Operator.LE, "01/01/1972", "12/31/2011", "day", "day", true);
        // 01/01/1972 <= 01/01/1972
        runCompareWithPrecision(DateComparator.Operator.LE, "01/01/1972", "01/01/1972", "day", "day", true);

        // 12/15/1980 <= 12/1980 (can't say false for sure, so true)
        runCompareWithPrecision(DateComparator.Operator.LE, "12/15/1980", "12/01/1980", "day", "month", true);
        // 11/15/1980 <= 12/1980
        runCompareWithPrecision(DateComparator.Operator.LE, "11/15/1980", "12/01/1980", "day", "month", true);
        // 12/1980 !<= 11/01/1980
        runCompareWithPrecision(DateComparator.Operator.LE, "12/01/1980", "11/01/1980", "month", "day", false);

        // 1950 <= 06/12/1980
        runCompareWithPrecision(DateComparator.Operator.LE, "01/01/1950", "06/12/1980", "year", "day", true);
        // 1950 <= 07/07/1950
        runCompareWithPrecision(DateComparator.Operator.LE, "01/01/1950", "07/07/1950", "year", "day", true);
        // 1960 !<= 1950
        runCompareWithPrecision(DateComparator.Operator.LE, "01/01/1960", "01/01/1950", "year", "year", false);

    }

    @Test
    public void testGreaterThanOrEqualsWithPrecision() throws ParseException {
         // 01/02/2012 >= 01/01/2012
        runCompareWithPrecision(DateComparator.Operator.GE, "01/02/2012", "01/01/2012", "day", "day", true);
        // 01/01/2012 !>= 01/15/2012
        runCompareWithPrecision(DateComparator.Operator.GE, "01/01/2012", "01/15/2012", "day", "day", false);
        // 01/10/2012 >= 01/2012 (can't say false for sure, so true
        runCompareWithPrecision(DateComparator.Operator.GE, "01/10/2012", "01/15/2012", "day", "month", true);
        // 02/2012 >= 01/2012
        runCompareWithPrecision(DateComparator.Operator.GE, "02/01/2012", "01/01/2012", "month", "month", true);
        // 10/10/2012 >= 01/2011
        runCompareWithPrecision(DateComparator.Operator.GE, "10/10/2012", "01/15/2011", "day", "month", true);
        // 01/01/2012 !>= 02/2012
        runCompareWithPrecision(DateComparator.Operator.GE, "01/01/2012", "02/15/2012", "day", "month", false);
        // 2011 >= 10/15/2011 (can't say false for sure, so true)
        runCompareWithPrecision(DateComparator.Operator.GE, "01/01/2011", "10/15/2011", "year", "day", true);
        // 2010 !>= 10/15/2011
        runCompareWithPrecision(DateComparator.Operator.GE, "01/01/2010", "10/15/2011", "year", "day", false);
    }

    @Test
    public void testNotEqualsWithPrecision() throws ParseException {
        // 01/01/2011 != 01/02/2011
        runCompareWithPrecision(DateComparator.Operator.NE, "01/01/2011", "01/02/2011", "day", "day", true);
        // 01/01/2011 = 01/01/2011
        runCompareWithPrecision(DateComparator.Operator.NE, "01/01/2011", "01/01/2011", "day", "day", false);
        // 05/1981 != 05/12/1981 (can't say false for sure, so true)
        runCompareWithPrecision(DateComparator.Operator.NE, "05/01/1981", "05/12/1981", "month", "day", true);
        // 10/2003 != 10/2003 (can't say false for sure, so true)
        runCompareWithPrecision(DateComparator.Operator.NE, "10/01/2003", "10/01/2003", "month", "month", true);
        // 2009 != 2009 (can't say false for sure, so true)
        runCompareWithPrecision(DateComparator.Operator.NE, "01/01/2009", "01/01/2009", "year", "year", true);
        // 8/15/2007 != 2009
        runCompareWithPrecision(DateComparator.Operator.NE, "08/15/2007", "01/01/2009", "day", "year", true);
    }


    private void runCompareWithPrecision(final DateComparator.Operator operator,
                                         final String leftDateString, final String rightDateString,
                                         final String leftPrecision, final String rightPrecision,
                                         final boolean expectedResult) throws ParseException {
        Date leftDate = simpleDateFormat.parse(leftDateString);
        Date rightDate = simpleDateFormat.parse(rightDateString);

        dateMap.put("a", leftDate);
        dateMap.put("b", rightDate);
        precisionMap.put("a", leftPrecision);
        precisionMap.put("b", rightPrecision);

        final DateComparator dateComparator = new DateComparator("a", "b", operator);
        final boolean result = dateComparator.compare(dateMap, precisionMap);
        assertEquals(expectedResult, result);
    }
}
