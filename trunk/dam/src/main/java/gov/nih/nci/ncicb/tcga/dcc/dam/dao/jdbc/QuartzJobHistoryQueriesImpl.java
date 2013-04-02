/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobStatus;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.QuartzJobHistoryQueries;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * JDBC implementation for <code>QuartzJobHistory</code> queries
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QuartzJobHistoryQueriesImpl extends SimpleJdbcDaoSupport implements QuartzJobHistoryQueries {

    private final Log log = LogFactory.getLog(getClass());

    /**
     * The QRTZ_JOB_HISTORY table name
     */
    public static final String QRTZ_JOB_HISTORY = "qrtz_job_history";

    /**
     * The QRTZ_JOB_HISTORY table's column names
     */
    public static final String JOB_NAME = "job_name";
    public static final String JOB_GROUP = "job_group";
    public static final String FIRE_TIME = "fire_time";
    public static final String STATUS = "status";
    public static final String LAST_UPDATED = "last_updated";
    public static final String JOB_DATA = "job_data";
    public static final String IS_COMPLETE = "is_complete";
    public static final String TIME_ENQUEUED = "time_enqueued";
    public static final String QUEUE_NAME = "queue_name";

    /**
     * Query for inserting a <code>QuartzJobHistory</code>
     */
    private static final String INSERT_OR_UPDATE_QUERY = "merge into " + QRTZ_JOB_HISTORY  +
            " using dual " +
            "on (job_name=? and job_group=?) " +
            "when matched then " +
            "update set " + FIRE_TIME + "=?, " + STATUS + "=?, " + JOB_DATA + "=?, " + LAST_UPDATED + "=?, " + IS_COMPLETE + "=? " +
            "when not matched then " +
            "insert " +
            "(" + JOB_NAME + ", " + JOB_GROUP + ", " + FIRE_TIME + ", " + STATUS + ", " + JOB_DATA + ", " + LAST_UPDATED +
            ", " + TIME_ENQUEUED + ", " + QUEUE_NAME + ", " + IS_COMPLETE + ") " +
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Template query to extract data from the JOB_DATA column of type XML_TYPE
     */
    private static final String EXTRACT_FROM_XML_TYPE = "q.%s.extract('/%s/%s/text()').getStringVal() as %s";

    /**
     * Query for selecting all <code>QuartzJobHistory</code>
     */
    private static final String SELECT_ALL_QUERY = String.format(
            "select " +
            "%s, " + // JOB_NAME
            "%s, " + // JOB_GROUP
            "%s, " + // FIRE_TIME
            "%s, " + // STATUS
            "%s, " + // LAST_UPDATED
            EXTRACT_FROM_XML_TYPE + ", " + // LINK_TEXT
            EXTRACT_FROM_XML_TYPE + ", " + // ESTIMATED_UNCOMPRESSED_SIZE
            EXTRACT_FROM_XML_TYPE + ", " + // JOB_WS_SUBMISSION_DATE
            "%s, " + // ENQUEUE TIME
            "%s " + // QUEUE NAME
            "from %s q", // QRTZ_JOB_HISTORY

            JOB_NAME,
            JOB_GROUP,
            FIRE_TIME,
            STATUS,
            LAST_UPDATED,
            JOB_DATA, QuartzJobHistory.JOB_DATA, QuartzJobHistory.LINK_TEXT, QuartzJobHistory.LINK_TEXT,
            JOB_DATA, QuartzJobHistory.JOB_DATA, QuartzJobHistory.ESTIMATED_UNCOMPRESSED_SIZE, QuartzJobHistory.ESTIMATED_UNCOMPRESSED_SIZE,
            JOB_DATA, QuartzJobHistory.JOB_DATA, QuartzJobHistory.JOB_WS_SUBMISSION_DATE, QuartzJobHistory.JOB_WS_SUBMISSION_DATE,
            TIME_ENQUEUED,
            QUEUE_NAME,
            QRTZ_JOB_HISTORY
    );

    /**
     * Query for selecting an individual <code>QuartzJobHistory</code>
     */
    private static final String SELECT_INDIVIDUAL_QUERY = SELECT_ALL_QUERY + String.format(
            " where %s=? " + // JOB_NAME
            "and %s=?", // JOB_GROUP

            JOB_NAME,
            JOB_GROUP
    );

    /**
     * Query to delete a <code>QuartzJobHistory</code>
     */
    private static final String DELETE_QUERY = "delete from " + QRTZ_JOB_HISTORY + " where " + JOB_NAME + "=? and " + JOB_GROUP + "=?";

    private static final String QUEUE_POSITION_QUERY = "select count(*) from qrtz_job_history where time_enqueued < ? and queue_name=? and is_complete=0";

    /**
     * RowMapper for <code>QuartzJobHistory</code>
     */
    private ParameterizedRowMapper<QuartzJobHistory> quartzJobHistoryRowMapper;

    /*
     * Constructor
     */

    public QuartzJobHistoryQueriesImpl() {

        quartzJobHistoryRowMapper = new ParameterizedRowMapper<QuartzJobHistory>() {

            public QuartzJobHistory mapRow(final ResultSet resultSet, final int i) throws SQLException {

                final Long jobWSSubmissionDateAsLong = resultSet.getLong(QuartzJobHistory.JOB_WS_SUBMISSION_DATE);
                Date jobWSSubmissionDate = null;
                
                // if 0L is found, it means the job was not submitted through the web service (and converting that would give us The Epoch ...)
                if(jobWSSubmissionDateAsLong != 0L) {
                    jobWSSubmissionDate = new Date(jobWSSubmissionDateAsLong);
                }

                final QuartzJobHistory quartzJobHistory = new QuartzJobHistory(
                        resultSet.getString(JOB_NAME),
                        resultSet.getString(JOB_GROUP),
                        resultSet.getDate(FIRE_TIME),
                        QuartzJobStatus.valueOf(resultSet.getString(STATUS)),
                        resultSet.getDate(LAST_UPDATED),
                        resultSet.getString(QuartzJobHistory.LINK_TEXT),
                        Long.parseLong(resultSet.getString(QuartzJobHistory.ESTIMATED_UNCOMPRESSED_SIZE)),
                        jobWSSubmissionDate
                );
                quartzJobHistory.setQueueName(resultSet.getString(QUEUE_NAME));
                quartzJobHistory.setEnqueueDate(resultSet.getTimestamp(TIME_ENQUEUED));
                return quartzJobHistory;
            }
        };
    }

    /**
     * Persist a <code>QuartzJobHistory</code> in the DB.
     * Return the numbers of rows affected by the update
     *
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to persist
     * @return the numbers of rows affected by the update
     */
    @Override
    public int persist(final QuartzJobHistory quartzJobHistory) {

        int result = 0;

        final QuartzJobStatus status = quartzJobHistory.getStatus();

        switch(status) {
            case Succeeded:
            case Failed:
            case Queued:
            case Started:
                result = insert(quartzJobHistory);
                break;
            default:
                getLog().error(new StringBuilder("Quartz job " + STATUS + " '").append(status).append("' unexpected."));
                break;
        }

        return result;
    }

    /**
     * Retrieve all <code>QuartzJobHistory</code> from the DB
     *
     * @return a <code>List</code> of <code>QuartzJobHistory</code>
     */
    @Override
    public List<QuartzJobHistory> getAllQuartzJobHistory() {
        return getSimpleJdbcTemplate().query(SELECT_ALL_QUERY, getQuartzJobHistoryRowMapper());
    }

    /**
     * Delete the given <code>QuartzJobHistory</code> from the DB.
     * Return the numbers of rows affected by the update
     *
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to delete
     * @return the numbers of rows affected by the update
     */
    @Override
    public int delete(final QuartzJobHistory quartzJobHistory) {

        final Object[] parameters = new Object[2];

        parameters[0] = quartzJobHistory.getJobName();
        parameters[1] = quartzJobHistory.getJobGroup();

        return getSimpleJdbcTemplate().update(DELETE_QUERY, parameters);
    }

    /**
     * Return the <code>QuartzJobHistory</code> with the given job name and job group, or <code>null</code> if no match is found
     * <p/>
     * Note: the job name is the same as the <code>UUID</code> from the <code>FilePackagerBean</code> the <code>QuartzJobHistory</code> is derived from.
     *
     * @param jobName  the job name
     * @param jobGroup the job group
     * @return the <code>QuartzJobHistory</code> with the given job name and job group, or <code>null</code> if no match is found
     */
    @Override
    public QuartzJobHistory getQuartzJobHistory(final String jobName, final String jobGroup) {

        QuartzJobHistory result = null;

        final Object[] parameters = new Object[2];
        parameters[0] = jobName;
        parameters[1] = jobGroup;

        try {
            result = getSimpleJdbcTemplate().queryForObject(SELECT_INDIVIDUAL_QUERY, getQuartzJobHistoryRowMapper(), parameters);

        } catch(final EmptyResultDataAccessException e) {
            // Do nothing
        }

        return result;
    }

    /**
     * Queries to find the number of incomplete jobs for this job's queue that were enqueued before this job.  This
     * is the number of jobs ahead of this job in the queue.
     *
     * @param quartzJobHistory bean representing the job whose position you are looking for
     * @return the number of incomplete jobs in the same queue that were added earlier
     */
    @Override
    public Integer getPositionInQueue(final QuartzJobHistory quartzJobHistory) {
        return getJdbcTemplate().queryForInt(QUEUE_POSITION_QUERY, new Object[]{quartzJobHistory.getEnqueueDate(),
                quartzJobHistory.getQueueName()});
    }

    /**
     * Delete the given <code>QuartzJobHistory</code> from the DB.
     * Return the numbers of rows affected by the update
     *
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to delete
     * @return the numbers of rows affected by the update
     */
    private int insert(final QuartzJobHistory quartzJobHistory) {

        return getSimpleJdbcTemplate().update(INSERT_OR_UPDATE_QUERY,
                quartzJobHistory.getJobName(), quartzJobHistory.getJobGroup(),

                quartzJobHistory.getFireTime(),
                quartzJobHistory.getStatusAsString(),
                quartzJobHistory.getJobData(),
                quartzJobHistory.getLastUpdated(),
                quartzJobHistory.isComplete() ? 1 :0,

                quartzJobHistory.getJobName(),
                quartzJobHistory.getJobGroup(),
                quartzJobHistory.getFireTime(),
                quartzJobHistory.getStatusAsString(),
                quartzJobHistory.getJobData(),
                quartzJobHistory.getLastUpdated(),
                quartzJobHistory.getEnqueueDate(),
                quartzJobHistory.getQueueName(),
                quartzJobHistory.isComplete() ? 1 : 0
        );
    }

    /*
     * Getters / Setters
     */

    public Log getLog() {
        return log;
    }

    public ParameterizedRowMapper<QuartzJobHistory> getQuartzJobHistoryRowMapper() {
        return quartzJobHistoryRowMapper;
    }

    public void setQuartzJobHistoryRowMapper(final ParameterizedRowMapper<QuartzJobHistory> quartzJobHistoryRowMapper) {
        this.quartzJobHistoryRowMapper = quartzJobHistoryRowMapper;
    }
}
