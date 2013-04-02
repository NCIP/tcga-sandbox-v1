/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import junit.framework.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * DateUtils unit tests
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DateUtilsFastTest {

    @Test
    public void testValidateValidDate() throws DateUtilsException {

        final Date date = DateUtils.validate("1", "1", "2011");

        final Calendar calendar = Calendar.getInstance();
        calendar.set(2011, 0, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        assertNotNull(date);
        assertEquals(calendar.getTime(), date);
    }

    @Test
    public void testValidateInvalidDay() throws DateUtilsException {

        final Date date = DateUtils.validate("32", "1", "2011");
        assertNull(date);
    }

    @Test
    public void testValidateInvalidMonth() throws DateUtilsException {

        final Date date = DateUtils.validate("1", "13", "2011");
        assertNull(date);
    }

    @Test
    public void testValidateInvalidFormat() {

        try {
            DateUtils.validate("1", "13", "11");
            fail("DateUtilsException was not thrown");
        } catch (final DateUtilsException e) {
            assertEquals("year '11' expected to be 4 digits",
                    e.getMessage());
        }
    }

    @Test
    public void testValidateDateFormatWithValidMonthDayAndYear() throws DateUtilsException {
        DateUtils.validateDateFormat("11", "5", "2009");
   }

    @Test
    public void testValidateDateFormatWithUnexpectedDigitNumberForMonthDayAndYear() {
        try {
            DateUtils.validateDateFormat("123", "456", "78");
            fail("DateUtilsException was not thrown");

        } catch (final DateUtilsException e) {
            assertEquals("year '78' expected to be 4 digits, month '456' expected to be 1 or 2 digits, day '123' expected to be 1 or 2 digits",
                    e.getMessage());
        }
    }

    @Test
    public void testGetNextDayFromAfterFeb282011() {
        checkNextDayFromExpectations(2011, 1, 28, 0, 0, 0, 0, 2011, 2, 1);
    }

    @Test
    public void testGetNextDayFromAfterDec31() {
        checkNextDayFromExpectations(2011, 11, 31, 0, 0, 0, 0, 2012, 0, 1);
    }

    @Test
    public void testGetNextDayFromWhenStartOfDay() {
        checkNextDayFromExpectations(2011, 0, 1, 0, 0, 0, 0, 2011, 0, 2);
    }

    @Test
    public void testGetNextDayWhenEndOfDay() {
        checkNextDayFromExpectations(2011, 0, 1, 23, 59, 59, 999, 2011, 0, 2);
    }

    @Test
    public void testIsDateStrictlyBeforeGivenTimeInThePast() {

        final Calendar januaryFirst2011 = makeCalendar(2011, 0, 1, 0, 0, 0, 0);
        final boolean result = DateUtils.isDateStrictlyBeforeGivenTime("24", "10", "2020", januaryFirst2011);

        assertFalse(result);
    }

    @Test
    public void testIsDateStrictlyBeforeGivenTimeInTheFuture() {

        final Calendar januaryFirst2011 = makeCalendar(2011, 0, 1, 0, 0, 0, 0);
        final boolean result = DateUtils.isDateStrictlyBeforeGivenTime("24", "10", "1900", januaryFirst2011);

        assertTrue(result);
    }

    @Test
    public void testIsDateStrictlyBeforeGivenTimeWhenTimeIsExactlyTheStartOfDate() {

        final Calendar januaryFirst2011 = makeCalendar(2011, 0, 1, 0, 0, 0, 0);
        final boolean result = DateUtils.isDateStrictlyBeforeGivenTime("1", "1", "2011", januaryFirst2011);

        assertFalse(result);
    }

    @Test
    public void testIsDateStrictlyBeforeGivenTimeWhenTimeIsShortlyAfterStartOfDate() {

        final Calendar januaryFirst2011 = makeCalendar(2011, 0, 1, 0, 0, 0, 1);
        final boolean result = DateUtils.isDateStrictlyBeforeGivenTime("1", "1", "2011", januaryFirst2011);

        assertTrue(result);
    }

    @Test
    public void testIsDateStrictlyBeforeGivenTimeWhenTimeIsShortlyBeforeStartOfDate() {

        final Calendar januaryFirst2011 = makeCalendar(2011, 0, 1, 23, 59, 59, 999);
        final boolean result = DateUtils.isDateStrictlyBeforeGivenTime("2", "1", "2011", januaryFirst2011);

        assertFalse(result);
    }

    @Test
    public void testIsDateStrictlyBeforeGivenTimeWhenTimeIsInMiddleOfStartOfDate() {

        final Calendar januaryFirst2011 = makeCalendar(2011, 0, 1, 12, 0, 0, 0);
        final boolean result = DateUtils.isDateStrictlyBeforeGivenTime("1", "1", "2011", januaryFirst2011);

        assertTrue(result);
    }

    @Test
    public void testMakeDateFromValidString() throws ParseException {

        final Date date = DateUtils.makeDate("2012-01-02");

        Assert.assertEquals(2012 - 1900, date.getYear());
        Assert.assertEquals(1 - 1, date.getMonth()); // Java API is 0-based
        Assert.assertEquals(2, date.getDate());
    }

    @Test
    public void testMakeDateFromInvalidString() {

        final Date date;
        try {
            date = DateUtils.makeDate("a2012-b1-c2");
            fail("ParseException was not thrown.");

        } catch (final ParseException e) {
            Assert.assertEquals("Unparseable date: \"a2012-b1-c2\"", e.getMessage());
        }
    }

    @Test
    public void testMakeDateFromNullString() throws ParseException {

        final Date date = DateUtils.makeDate(null);

        assertNull(date);
    }

    @Test
    public void testMakeDateFromValidYearMonthDay() throws ParseException {

        final Date date = DateUtils.makeDate("2012", "01", "02");

        Assert.assertEquals(2012 - 1900, date.getYear());
        Assert.assertEquals(1 - 1, date.getMonth()); // Java API is 0-based
        Assert.assertEquals(2, date.getDate());
    }

    @Test
    public void testMakeDateFromInvalidYearMonthDay() {

        final Date date;
        try {
            date = DateUtils.makeDate("a1", "b2", "c3");
            fail("ParseException was not thrown.");

        } catch (final ParseException e) {
            Assert.assertEquals("Unparseable date: \"a1-b2-c3\"", e.getMessage());
        }
    }

    /**
     * Check expectations when calling getNextDayFrom() with the given values
     *
     * @param year current year
     * @param month current month (0-based)
     * @param day current day
     * @param hourOfDay hour of the current day
     * @param minute minute of the current day
     * @param second second of the current day
     * @param millisecond millisecond of the current day
     * @param expectedYear expected year
     * @param expectedMonth expected month (0-based)
     * @param expectedDay expected day
     */
    private void checkNextDayFromExpectations(final int year,
                                          final int month,
                                          final int day,
                                          final int hourOfDay,
                                          final int minute,
                                          final int second,
                                          final int millisecond,
                                          final int expectedYear,
                                          final int expectedMonth,
                                          final int expectedDay) {

        final Calendar currentDay = makeCalendar(year, month, day, hourOfDay, minute, second, millisecond);
        final Calendar nextDay = DateUtils.getNextDayFrom(currentDay);

        assertEquals(expectedYear, nextDay.get(Calendar.YEAR));
        assertEquals(expectedMonth, nextDay.get(Calendar.MONTH));
        assertEquals(expectedDay, nextDay.get(Calendar.DATE));
        assertEquals(0, nextDay.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, nextDay.get(Calendar.MINUTE));
        assertEquals(0, nextDay.get(Calendar.SECOND));
        assertEquals(0, nextDay.get(Calendar.MILLISECOND));
    }

    /**
     * Return a {@link Calendar} with the given parameters
     *
     * @param year the year
     * @param month the month (0-based)
     * @param day the day
     * @param hourOfDay the hour
     * @param minute the minute
     * @param second the second
     * @param millisecond the millisecond
     *
     * @return a {@link Calendar} with the given parameters
     */
    private Calendar makeCalendar(final int year,
                                  final int month,
                                  final int day,
                                  final int hourOfDay,
                                  final int minute,
                                  final int second,
                                  final int millisecond) {

        final Calendar result = Calendar.getInstance();
        result.setLenient(false);
        result.set(year, month, day, hourOfDay, minute, second);
        result.set(Calendar.MILLISECOND, millisecond);

        return result;
    }
}
