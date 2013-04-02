/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Abstract class for Usage Logging.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public abstract class AbstractUsageLogger implements UsageLoggerI {

    // enum of all valid action types
    public enum ActionType {

        DAM_REQUESTED,
        DAM_RESET,
        HEADER_SELECTED,
        HEADER_INTERSECTED,
        HEADER_DESELECTED,
        HEADER_CATEGORY_SELECTED,
        HEADER_CATEGORY_INTERSECTED,
        HEADER_CATEGORY_DESELECTED,
        HEADER_TYPE_SELECTED,
        HEADER_TYPE_INTERSECTED,
        HEADER_TYPE_DESELECTED,
        FILTER_APPLIED,
        AVAILABILITY_FILTER_APPLIED,
        BATCH_FILTER_APPLIED,
        CENTER_FILTER_APPLIED,
        PLATFORM_FILTER_APPLIED,
        LEVEL_FILTER_APPLIED,
        PLATFORM_TYPE_FILTER_APPLIED,
        PROTECTED_STATUS_FILTER_APPLIED,
        SAMPLE_FILTER_APPLIED,
        TUMOR_NORMAL_FILTER_APPLIED,
        FILTER_CLEARED,
        COLOR_SCHEME_CHANGED,
        DAD_REQUESTED,
        FILES_SELECTED,
        LEVEL_1_FILES_SELECTED,
        LEVEL_2_FILES_SELECTED,
        LEVEL_3_FILES_SELECTED,
        CLINICAL_FILES_SELECTED,
        METADATA_FILES_SELECTED,
        PROTECTED_FILES_SELECTED,
        PUBLIC_FILES_SELECTED,
        CALCULATED_SIZE,
        ARCHIVE_QUEUED,
        ARCHIVE_STARTED,
        ARCHIVE_FINISHED,
        ARCHIVE_FAILED,
        UNCOMPRESSED_SIZE,
        COMPRESSED_SIZE,
        TOTAL_ARCHIVE_CREATION_TIME,
        WAITING_IN_QUEUE_TIME,
        FILE_PROCESSING_TIME,
        ARCHIVE_GENERATION_TIME,
        END_DATE_FILTER_APPLIED,
        START_DATE_FILTER_APPLIED
    }

    /**
     * Maps ActionType to string (name). Unmodifiable.
     */
    public static final Map<ActionType, String> ACTION_NAMES = Collections.unmodifiableMap(new HashMap<ActionType, String>() {
        {
            put(ActionType.DAM_REQUESTED, "data access matrix requested");
            put(ActionType.DAM_RESET, "data access matrix reset");
            put(ActionType.HEADER_SELECTED, "header selected");
            put(ActionType.HEADER_INTERSECTED, "header intersected");
            put(ActionType.HEADER_DESELECTED, "header deselected");
            put(ActionType.HEADER_CATEGORY_SELECTED, "header category selected");
            put(ActionType.HEADER_CATEGORY_INTERSECTED, "header category intersected");
            put(ActionType.HEADER_CATEGORY_DESELECTED, "header category deselected");
            put(ActionType.HEADER_TYPE_SELECTED, "header type selected");
            put(ActionType.HEADER_TYPE_INTERSECTED, "header type intersected");
            put(ActionType.HEADER_TYPE_DESELECTED, "header type deselected");
            put(ActionType.FILTER_APPLIED, "filter applied");
            put(ActionType.AVAILABILITY_FILTER_APPLIED, "availability filter applied");
            put(ActionType.BATCH_FILTER_APPLIED, "batch filter applied");
            put(ActionType.CENTER_FILTER_APPLIED, "center filter applied");
            put(ActionType.LEVEL_FILTER_APPLIED, "level filter applied");
            put(ActionType.PLATFORM_TYPE_FILTER_APPLIED, "platform type filter applied");
            put(ActionType.PROTECTED_STATUS_FILTER_APPLIED, "protected status filter applied");
            put(ActionType.SAMPLE_FILTER_APPLIED, "sample filter applied");
            put(ActionType.TUMOR_NORMAL_FILTER_APPLIED, "tumor/normal filter applied");
            put(ActionType.FILTER_CLEARED, "filter cleared");
            put(ActionType.COLOR_SCHEME_CHANGED, "color scheme changed");
            put(ActionType.DAD_REQUESTED, "data access download page requested");
            put(ActionType.FILES_SELECTED, "files selected");
            put(ActionType.LEVEL_1_FILES_SELECTED, "level 1 files selected");
            put(ActionType.LEVEL_2_FILES_SELECTED, "level 2 files selected");
            put(ActionType.LEVEL_3_FILES_SELECTED, "level 3 files selected");
            put(ActionType.CLINICAL_FILES_SELECTED, "clinical files selected");
            put(ActionType.METADATA_FILES_SELECTED, "metadata files selected");
            put(ActionType.PROTECTED_FILES_SELECTED, "protected files selected");
            put(ActionType.PUBLIC_FILES_SELECTED, "public files selected");
            put(ActionType.CALCULATED_SIZE, "calculated archive size");
            put(ActionType.ARCHIVE_QUEUED, "archive creation queued");
            put(ActionType.ARCHIVE_STARTED, "archive creation started");
            put(ActionType.ARCHIVE_FINISHED, "archive creation finished");
            put(ActionType.ARCHIVE_FAILED, "archive creation failed");
            put(ActionType.UNCOMPRESSED_SIZE, "uncompressed archive size");
            put(ActionType.COMPRESSED_SIZE, "compressed archive size");
            put(ActionType.TOTAL_ARCHIVE_CREATION_TIME, "total archive creation time");
            put(ActionType.WAITING_IN_QUEUE_TIME, "waiting in queue time");
            put(ActionType.FILE_PROCESSING_TIME, "file processing time");
            put(ActionType.ARCHIVE_GENERATION_TIME, "archive generation time");
            put(ActionType.END_DATE_FILTER_APPLIED, "end date filter applied");
            put(ActionType.START_DATE_FILTER_APPLIED, "start date filter applied");
            put(ActionType.PLATFORM_FILTER_APPLIED, "platform filter applied");
        }});
    protected static final String DATE_FORMAT_STRING = "dd-MMM-yyyy HH:mm:ss";

    /**
     * Convenience method for getting name of a given ActionType.
     *
     * @param type the ActionType to get
     * @return the name of the action
     */
    public static String getActionName(final ActionType type) {
        return ACTION_NAMES.get(type);
    }

    /**
     * Logs the given action.
     *
     * @param sessionKey used to identify the session
     * @param actionName the name of the action
     * @param value      the value of the action
     * @throws UsageLoggerException if there is an error while logging the usage
     */
    public void logAction(final String sessionKey, final String actionName, final Object value) throws UsageLoggerException {
        writeAction(sessionKey, Calendar.getInstance().getTime(), actionName, convert(value));
    }

    /**
     * Logs the given action group as having happened at the same time.  Note the Map can only take one action of
     * each type.  For multiple values for the same action type, set the value of the type to an Array.
     *
     * @param sessionKey used to identify the session
     * @param actions    a Map of actions, with keys as actionNames and values as the values for each
     * @throws UsageLoggerException if there is an error while logging the usage
     */
    public void logActionGroup(final String sessionKey, final Map<String, Object> actions) throws UsageLoggerException {
        final Date now = Calendar.getInstance().getTime();
        for (final String actionName : actions.keySet()) {
            writeAction(sessionKey, now, actionName, convert(actions.get(actionName)));
        }
    }

    /**
     * This method should actually write the action to whatever kind of Usage Log the subclass implements.
     *
     * @param sessionKey  used to identify the session
     * @param date        the date/time the action happened
     * @param actionName  the name of the action
     * @param actionValue the value of the action
     * @throws UsageLoggerException if there is an error writing the action
     */
    protected abstract void writeAction(String sessionKey, Date date, String actionName, Object actionValue)
            throws UsageLoggerException;

    /**
     * Converts values for insertion.  Dates get turned to strings using the DateFormat object.  Arrays get turned into
     * strings like "[val1, val2]" using Arrays.deepToString.  Nulls are kept as nulls.
     *
     * @param value the value to convert
     * @return the converted value
     */
    protected String convert(final Object value) {
        if (value instanceof Date) {
            // convert date to a string
            return getDateFormat().format(value);
        } else if (value instanceof Object[]) {
            // deep to-string results in "[a, b, c]" string
            return Arrays.deepToString((Object[]) value);
        } else if (value == null) {
            return convertNull();
        } else {
            return value.toString();
        }
    }

    /**
     * Convert null values.  This implementation returns null.  Subclasses override to change behavior.
     *
     * @return the value to use for null values when logging
     */
    protected String convertNull() {
        return null;
    }

    /**
     * Note: returns a new DateFormat object each time, because DateFormat objects are not synchronized.
     *
     * @return the DateFormat object to use to formatting dates into Strings for this Logger
     */
    public DateFormat getDateFormat() {
        return new SimpleDateFormat(getDateFormatString());
    }

    /**
     * Subclasses override to change format.
     *
     * @return the date format string  (dd-MMM-yyyy HH:mm:ss)
     */
    public String getDateFormatString() {
        return DATE_FORMAT_STRING;
    }

    /**
     * Inner class used to represent a logged Usage Session.  Used only by the getAllSessions method.
     */
    public class UsageSession {

        /**
         * The session key this usage is a part of
         */
        public String sessionKey;
        /**
         * The date this session was created
         */
        public Date createdOn;
        /**
         * The UsageSessionActions associated with this session
         */
        public List<UsageSessionAction> actions;
    }

    /**
     * Inner class used to represent an action within a UsageSession
     */
    public class UsageSessionAction {

        public String name;
        public String value; // may be null
        public Date actionTime;
        public List<UsageSessionAction> childActions;   // may be null
    }
}
