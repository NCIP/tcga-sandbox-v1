/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * A JDBC-based implementation of UsageLogger.  Writes usage information to a database
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class UsageLoggerJDBCImpl extends AbstractUsageLogger {

    // static cache of action ids, mapped to names
    // uses a Hashtable because we need the synchronization, since this class is accessed by multiple threads
    private static Hashtable<String, Integer> ACTION_TYPE_IDS = new Hashtable<String, Integer>();
    // sql queries
    private static final String INSERT_SESSION = "insert into portal_session(portal_session_id, created_on, session_key) " +
            "values(?, ?, ?)";
    private static final String INSERT_SESSION_ACTION = "insert into portal_session_action(portal_session_action_id, " +
            "portal_session_id, portal_action_type_id, action_time, value) values(?, ?, ?, ?, ?)";
    private static final String GET_SESSION_ID_QUERY = "select portal_session_id from portal_session where session_key=?";
    private static final String GET_ACTION_TYPE_ID_QUERY = "select portal_action_type_id from portal_action_type " +
            "where name=?";
    private static final String ALL_SESSIONS_QUERY = "select portal_session_id, session_key, created_on " +
            "from portal_session";
    private static final String TOP_LEVEL_ACTIONS_QUERY = "select name, value, action_time, " +
            "portal_action_type.portal_action_type_id, portal_session_id " +
            "from portal_session_action, portal_action_type " +
            "where portal_session_action.portal_action_type_id=portal_action_type.portal_action_type_id and " +
            "portal_action_type_parent is null and " +
            "portal_session_id=? order by action_time, portal_session_action.portal_action_type_id";
    private static final String CHILD_ACTIONS_QUERY = "select name, value from portal_session_action, portal_action_type " +
            "where portal_action_type_parent=? and " +
            "portal_session_action.portal_action_type_id=portal_action_type.portal_action_type_id and " +
            "action_time=? and portal_session_id=?";
    // Spring DAO object to access db
    private JdbcDaoSupport jdbcSupport;
    // incrementers for ids for session and session_action tables
    // will be configured and set in spring
    private DataFieldMaxValueIncrementer sessionIdIncrementer;
    private DataFieldMaxValueIncrementer sessionActionIdIncrementer;

    /**
     * Default constructor.
     */
    public UsageLoggerJDBCImpl() {
        // create the jdbdc dao support object
        jdbcSupport = new SimpleJdbcDaoSupport();
    }

    public void setSessionIdIncrementer(DataFieldMaxValueIncrementer incrementer) {
        sessionIdIncrementer = incrementer;
    }

    public void setSessionActionIdIncrementer(DataFieldMaxValueIncrementer incrementer) {
        sessionActionIdIncrementer = incrementer;
    }

    /**
     * Sets the data source.
     *
     * @param dataSource the data source to use -- usage logging will be written to the db specified
     */
    public void setDataSource(DataSource dataSource) {
        // pass through to jdbc dao support object
        jdbcSupport.setDataSource(dataSource);
    }

    /**
     * If start date and end date are null, returns all sessions.  If start date is null, returns all sessions from
     * first recorded until end date.  If end date is null, returns all sessions since start date.
     *
     * @param startDate the start date, or null
     * @param endDate the end date, or null
     * @return a List of all UsageSessions that have been recorded between start and end date     
     */
    public List<UsageSession> getAllSessionsForDates(Date startDate, Date endDate) {
        // get all sessions from the db
        // list the callback handler will add to
        final List<UsageSession> sessions = new ArrayList<UsageSession>();
        // use template to run the query, adding each session to the List
        String query = ALL_SESSIONS_QUERY;        
        List<Object> paramList = new ArrayList<Object>();
        if (startDate != null) {
            paramList.add(startDate);
            query += " where created_on > ?";
        }
        if (endDate != null) {
            paramList.add(endDate);
            if (startDate != null) {
                query += " and ";
            } else {
                query += " where ";
            }
            query += "created_on < ?";
        }
        query += "order by created_on";
        jdbcSupport.getJdbcTemplate().query(query, paramList.toArray(),
                new RowCallbackHandler() {
                    // handles each row
                    public void processRow(ResultSet resultSet) throws SQLException {
                        // create a new UsageSession object
                        UsageSession session = new UsageSession();
                        // add it to the list
                        sessions.add(session);
                        // set the values of the session from the result set
                        session.sessionKey = resultSet.getString(2);
                        session.createdOn = new Date(resultSet.getTimestamp(3).getTime());
                        long id = resultSet.getLong(1); // the PK for this session
                        // find all actions for this session...
                        session.actions = getSessionActions(id);
                    }
                });
        // at the end of this, all sessions have actions and actions have child actions.  return the list.
        return sessions;
    }

    /**
     * Get all actions for a session, given the session ID (not session key).
     *
     * @param sessionId the session ID you are interested in (primary key from table, not session key!)
     * @return a list of UsageSessionAction objects
     */
    public List<UsageSessionAction> getSessionActions(long sessionId) {
        final List<UsageSessionAction> actions = new ArrayList<UsageSessionAction>();
        // first get top-level actions (where type has no parent) for this session, ordered by time
        // use template to run the query
        jdbcSupport.getJdbcTemplate().query(TOP_LEVEL_ACTIONS_QUERY, new Object[]{sessionId},
                new RowCallbackHandler() {
                    // handles each row
                    public void processRow(ResultSet resultSet) throws SQLException {
                        // create an Action from the row data
                        UsageSessionAction action = new UsageSessionAction();
                        action.name = resultSet.getString(1);
                        action.value = resultSet.getString(2);
                        action.actionTime = new Date(resultSet.getTimestamp(3).getTime());
                        // add it to the list which above was set to the session.action variable
                        actions.add(action);
                        // now get any child actions for this top-level action
                        action.childActions = getChildActions(resultSet.getLong(4), resultSet.getTimestamp(3), resultSet.getLong(5));
                    }
                }
        );
        return actions;
    }

    /*
     * Get a list of "child" actions for the parent.  These are actions whose type is a child of the parent type and
     * that happened at the exact same time (Timestamp) in the same session.
     */
    private List<UsageSessionAction> getChildActions(long parentTypeId, Timestamp actionTime, long sessionId) {
        final List<UsageSessionAction> childActions = new ArrayList<UsageSessionAction>();
        // get child actions that happened at the same time (exactly)
        // use template to run the query
        jdbcSupport.getJdbcTemplate().query(CHILD_ACTIONS_QUERY, new Object[]{parentTypeId, actionTime, sessionId},
                new RowCallbackHandler() {
                    // for each row
                    public void processRow(ResultSet resultSet) throws SQLException {
                        // create the child action and add it to the list
                        UsageSessionAction action = new UsageSessionAction();
                        action.name = resultSet.getString(1);
                        action.value = resultSet.getString(2);
                        childActions.add(action);
                    }
                }
        );
        return childActions;
    }

    /**
     * Writes the given information to the database.
     *
     * @param sessionKey the session the action occurred in
     * @param date       when the action happened
     * @param actionName the name of the action -- must be a valid action type
     * @param value      the value for the action.  May be null.
     * @throws UsageLoggerException if the action name does not exist in the DB, or if there is an error writing to the db
     */
    protected void writeAction(String sessionKey, Date date, String actionName,
                               Object value) throws UsageLoggerException {
        // 1. look up sessionId (sessionKey), insert if not there
        int sessionId = getSessionId(sessionKey, date);
        // 2. insert portal_session_action for main actionName
        insertPortalSessionAction(sessionId, actionName, value, date);
    }

    /**
     * Gets the action type id (primary key in the db) for the given action name.
     *
     * @param actionName the name of the action to look up
     * @return the action_type_id of the action
     * @throws IllegalArgumentException if there is no such action with the given name
     */
    public int getActionTypeId(String actionName) throws IllegalArgumentException {
        Integer actionTypeId;
        // first, see if it is in the static cache
        actionTypeId = ACTION_TYPE_IDS.get(actionName);
        if (actionTypeId == null) {
            // if not, do a query
            actionTypeId = lookupActionTypeId(actionName);
            // then cache it for next time
            ACTION_TYPE_IDS.put(actionName, actionTypeId);
        }
        return actionTypeId;
    }

    /**
     * Does a query to find the actionTypeId for the given name
     *
     * @param actionName the action name
     * @return the action type id
     * @throws IllegalArgumentException if no id is found for the name
     */
    private Integer lookupActionTypeId(String actionName) throws IllegalArgumentException {
        int actionTypeId;
        try {
            actionTypeId = jdbcSupport.getJdbcTemplate().queryForInt(GET_ACTION_TYPE_ID_QUERY, new Object[]{actionName});
        }
        catch (EmptyResultDataAccessException e) {
            // is this the right exception?  maybe illegal argument exception instead?
            throw new IllegalArgumentException("There is no such action type as " + actionName);
        }
        return actionTypeId;
    }

    /**
     * Gets the session_id from the database for the given session key, or creates a new session in the database
     * if the given key is not found.  The new session will have its "created_on" date set to the current time.
     *
     * @param sessionKey the key to use to lookup/create the session
     * @return the session_id to the found/new session
     * @throws UsageLoggerException if there is a database error creating the new session
     */
    public int getSessionId(String sessionKey) throws UsageLoggerException {
        // call the next method with now as the time
        return getSessionId(sessionKey, Calendar.getInstance().getTime());
    }

    /**
     * Gets the session_id from the database for the given session key, or creates a new session in the database
     * if the given key is not found.
     *
     * @param sessionKey the key to use to lookup/create the session
     * @param date       the date to use if a new session has to be created
     * @return the session_id for the found/new session
     * @throws UsageLoggerException if there is a database error creating the new session
     */
    public int getSessionId(String sessionKey, Date date) throws UsageLoggerException {
        // look up; insert if necessary
        int sessionId;
        try {
            sessionId = jdbcSupport.getJdbcTemplate().queryForInt(GET_SESSION_ID_QUERY, new Object[]{sessionKey});
        }
        catch (EmptyResultDataAccessException e) {
            // if there are no results, then we need to insert it
            sessionId = sessionIdIncrementer.nextIntValue();
            insertPortalSession(sessionId, sessionKey, date);
        }
        return sessionId;
    }

    /**
     * Adds a row to the portal_session table
     *
     * @param sessionId  the id to use for the new row
     * @param sessionKey the key to use for the new row
     * @param date       the date to use for the new row
     * @throws UsageLoggerException if there is an error inserting the new row
     */
    private void insertPortalSession(int sessionId, String sessionKey, Date date) throws UsageLoggerException {
        Object[] params = new Object[]{sessionId, new Timestamp(date.getTime()), sessionKey};
        int rows = jdbcSupport.getJdbcTemplate().update(INSERT_SESSION, params);
        if (rows != 1) {
            throw new UsageLoggerException("Failed to insert portal_session row");
        }
    }

    /**
     * Adds a row to the portal_session_action table
     *
     * @param sessionId  the session_id to use for the new row [portal_session_id]
     * @param actionName the action name to use -- will look up the corresponding ID
     * @param value      the value to use for the new row
     * @param date       the time the action occurred
     * @throws UsageLoggerException if there is an error inserting the new row
     */
    private void insertPortalSessionAction(int sessionId, String actionName, Object value,
                                           Date date) throws UsageLoggerException {
        // 1. get action_type_id; will throw a runtime exception if not found.
        int actionTypeId = getActionTypeId(actionName);
        // 2. get the next id for portal_session_action
        int sessionActionId = sessionActionIdIncrementer.nextIntValue();
        // 3. do the insert
        Object[] params = new Object[]{sessionActionId, sessionId, actionTypeId, new Timestamp(date.getTime()), value};
        int rows = jdbcSupport.getJdbcTemplate().update(INSERT_SESSION_ACTION, params);
        if (rows != 1) {
            throw new UsageLoggerException("Failed to insert portal_session_action row");
        }
    }
}
