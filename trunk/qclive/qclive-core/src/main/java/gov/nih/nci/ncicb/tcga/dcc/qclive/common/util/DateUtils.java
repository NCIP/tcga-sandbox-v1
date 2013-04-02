/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Utility class used for Date validation
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class DateUtils {

    private final static String YEAR_REGEXP = "^[0-9]{4}$";
    private final static String MONTH_OR_DAY_REGEXP = "^[0-9]{1,2}$";

    private final static Pattern YEAR_PATTERN = Pattern.compile(YEAR_REGEXP);
    private final static Pattern MONTH_OR_DAY_PATTERN = Pattern.compile(MONTH_OR_DAY_REGEXP);

    private final static String SIMPLE_DATE_FORMAT_STR = "yyyy-MM-dd";

    /**
     * Return <code>true</code> if day, month and year have the expected format.
     *
     * @param day
     * @param month
     * @param year
     * @return <code>true</code> if day, month and year have the expected format
     * @throws DateUtilsException if day, month or year do not have the expected format
     */
    public static boolean validateDateFormat(final String day, final String month, final String year) throws DateUtilsException {

        boolean result = true;


        StringBuilder errorMessage = new StringBuilder();

        if(!YEAR_PATTERN.matcher(year).matches()) {
            errorMessage.append("year '").append(year).append("' expected to be 4 digits");
        }

        if(!MONTH_OR_DAY_PATTERN.matcher(month).matches()) {

            if(!StringUtils.isBlank(errorMessage.toString())) {
                errorMessage.append(", ");
            }

            errorMessage.append("month '").append(month).append("' expected to be 1 or 2 digits");
        }

        if(!MONTH_OR_DAY_PATTERN.matcher(day).matches()) {

            if(!StringUtils.isBlank(errorMessage.toString())) {
                errorMessage.append(", ");
            }

            errorMessage.append("day '").append(day).append("' expected to be 1 or 2 digits");
        }

        if(!StringUtils.isBlank(errorMessage.toString())) {
            throw new DateUtilsException(errorMessage.toString());
        }

        return result;
    }

    /**
     * Checks if the date represented by the passed parameters is valid.
     * All parameters are 1-based meaning 1 for Jan, 2 for Feb etc unlike Java Calendar API
     *
     * @param day date value for the date (1-based)
     * @param month month value for the date (1-based)
     * @param year year value for the date (1-based)
     * @return  the <code>Date</code> if valid, <code>null</code> otherwise
     * @throws DateUtilsException if the date does not have the expected format
     */
     public static Date validate(final String day, final String month, final String year) throws DateUtilsException {

         Date result = null;

         if(validateDateFormat(day, month, year)) {

             final Calendar cal = Calendar.getInstance();
             cal.setLenient(false);
             cal.set(Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day), 0, 0, 0);
             cal.set(Calendar.MILLISECOND, 0);

             try {
                 //Important : getTime has to be called to find out that the date is correct.
                 result = cal.getTime();

             } catch (IllegalArgumentException e) {
                 // this exception is thrown by the Java Calender getTime Method, if the date is not valid
             }
         }

         return result;
    }

    /**
     * Return <code>true</code> if the date represented by the passed parameters is strictly before the given upper limit,
     * <code>false</code> otherwise.
     *
     * Note: All parameters are 1-based meaning 1 for Jan, 2 for Feb etc unlike Java Calendar API.
     *
     * @param day date value for the date
     * @param month month value for the date
     * @param year year value for the date
     * @param strictUpperLimit the strict upper limit for the given date
     * @return <code>true</code> if the date represented by the passed parameters is strictly before the given upper limit,
     * <code>false</code> otherwise
     */
    public static boolean isDateStrictlyBeforeGivenTime(final String day,
                                                        final String month,
                                                        final String year,
                                                        final Calendar strictUpperLimit) {

        boolean result = true;

        final Calendar calendarFromDate = Calendar.getInstance();
        calendarFromDate.setLenient(false);
        calendarFromDate.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day), 0, 0, 0);
        calendarFromDate.set(Calendar.MILLISECOND, 0);


        if(!calendarFromDate.before(strictUpperLimit)) {
            result = false;
        }

        return result;
    }

    /**
     * Return the next day from the given day (the next day at 00:00)
     *
     * @param day the day to calculate the next day for
     * @return the next day from the given day
     */
    public static Calendar getNextDayFrom(final Calendar day) {

        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);

        // Next day
        day.add(Calendar.DAY_OF_YEAR, 1);

        return day;
    }

    /**
     * Return the date made up of the given year month and day.
     *
     * Note that year must be 4 digits while month and day must be 2 digits
     *
     * @param year the year as a 4-digit string
     * @param month the month as a 2 digit string
     * @param day the day as a 2-digit string
     * @return the date made up of the given year month and day
     * @throws ParseException
     */
    public static Date makeDate(final String year, final String month, final String day) throws ParseException {

        final String dateString = new StringBuilder().append(year).append("-").append(month).append("-").append(day).toString();
        return makeDate(dateString);
    }

    /**
     * Return the date represented by the given string, expected to be in "yyyy-MM-dd" format.
     *
     * Note: It assumes the represented date is a valid day in the calendar.
     *
     * @param dateString the date representation as a string
     * @return date represented by the given string if it is in the expected format <code>null</code> otherwise
     * @throws ParseException
     */
    public static Date makeDate(final String dateString) throws ParseException {

        Date result = null;

        if(dateString != null) {
            result = new SimpleDateFormat(SIMPLE_DATE_FORMAT_STR).parse(dateString);
        }

        return result;
    }
}
