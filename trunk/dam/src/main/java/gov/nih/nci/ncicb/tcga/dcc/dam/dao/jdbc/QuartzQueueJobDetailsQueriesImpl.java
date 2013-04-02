/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzQueueJobDetails;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.QuartzQueueJobDetailsQueries;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * QuartzQueueJobDetails JDBC queries for small and big queues
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QuartzQueueJobDetailsQueriesImpl extends SimpleJdbcDaoSupport implements QuartzQueueJobDetailsQueries {

    /**
     * [SMALL|BIG]_QUE_JOB_DETAILS fields
     */
    private static final String JOB_NAME = "job_name";
    private static final String JOB_GROUP = "job_group";
    private static final String DESCRIPTION = "description";
    private static final String JOB_CLASS_NAME = "job_class_name";
    private static final String IS_DURABLE = "is_durable";
    private static final String IS_VOLATILE = "is_volatile";
    private static final String IS_STATEFUL = "is_stateful";
    private static final String REQUESTS_RECOVERY = "requests_recovery";
    private static final String JOB_DATA = "job_data";

    private static String QUEUE_JOB_DETAILS_QUERY = "select "
            + JOB_NAME + ", "
            + JOB_GROUP + ", "
            + DESCRIPTION + ", "
            + JOB_CLASS_NAME + ", "
            + IS_DURABLE + ", "
            + IS_VOLATILE + ", "
            + IS_STATEFUL + ", "
            + REQUESTS_RECOVERY + ", "
            + JOB_DATA + " "
            + "from %s "
            + "where " + JOB_NAME + "=? "
            + "and " + JOB_GROUP + "=?";

    private static String SMALL_QUEUE_JOB_DETAILS_QUERY = String.format(QUEUE_JOB_DETAILS_QUERY, "small_que_job_details");

    private static String BIG_QUEUE_JOB_DETAILS_QUERY = String.format(QUEUE_JOB_DETAILS_QUERY, "big_que_job_details");

    /**
     * RowMapper for <code>QuartzQueueJobDetails</code>
     */
    private ParameterizedRowMapper<QuartzQueueJobDetails> quartzQueueJobDetailsRowMapper;

    /*
     * Constructor
     */

    public QuartzQueueJobDetailsQueriesImpl() {

        quartzQueueJobDetailsRowMapper = new ParameterizedRowMapper<QuartzQueueJobDetails>() {

            public QuartzQueueJobDetails mapRow(final ResultSet resultSet, final int i) throws SQLException {

                return new QuartzQueueJobDetails(
                        resultSet.getString(JOB_NAME),
                        resultSet.getString(JOB_GROUP),
                        resultSet.getString(DESCRIPTION),
                        resultSet.getString(JOB_CLASS_NAME),
                        resultSet.getBoolean(IS_DURABLE),
                        resultSet.getBoolean(IS_VOLATILE),
                        resultSet.getBoolean(IS_STATEFUL),
                        resultSet.getBoolean(REQUESTS_RECOVERY),
                        resultSet.getBlob(JOB_DATA)
                );
            }
        };
    }

    /**
     * Return Quartz small queue job details with the given job name and job group
     *
     * @param jobName  the job name
     * @param jobGroup the job group
     * @return Quartz small queue job details with the given job name and job group
     */
    @Override
    public QuartzQueueJobDetails getQuartzSmallQueueJobDetails(final String jobName, final String jobGroup) {
        return getQuartzQueueJobDetails(jobName, jobGroup, QuartzQueueType.SMALL);
    }

    /**
     * Return Quartz big queue job details with the given job name and job group
     *
     * @param jobName  the job name
     * @param jobGroup the job group
     * @return Quartz big queue job details with the given job name and job group
     */
    @Override
    public QuartzQueueJobDetails getQuartzBigQueueJobDetails(final String jobName, final String jobGroup) {
        return getQuartzQueueJobDetails(jobName, jobGroup, QuartzQueueType.BIG);
    }

    /**
     * Return <code>true</code> if the job with the given name and group can be found in the small queue or the big queue
     *
     * @param jobName  the job name
     * @param jobGroup the job group
     * @return <code>true</code> if the job with the given name and group can be found in the small queue or the big queue
     */
    @Override
    public boolean hasQuartzQueueJobDetails(final String jobName, final String jobGroup) {
        return getQuartzSmallQueueJobDetails(jobName, jobGroup) != null || getQuartzBigQueueJobDetails(jobName, jobGroup) != null;
    }

    /**
     * Return the <code>QuartzQueueJobDetails</code> that matches the given job name and group if found in the given Quartz queue,
     * <code>null</code> otherwise
     *
     * @param jobName  the job name
     * @param jobGroup the job group
     * @param quartzQueueType the type of Quartz queue to query
     * @return the <code>QuartzQueueJobDetails</code> that matches the given job name and group if found in the given Quartz queue, <code>null</code> otherwise
     */
    private QuartzQueueJobDetails getQuartzQueueJobDetails(final String jobName, final String jobGroup, final QuartzQueueType quartzQueueType) {

        QuartzQueueJobDetails result = null;

        String sql = null;

        switch(quartzQueueType) {
            case SMALL:
                sql = SMALL_QUEUE_JOB_DETAILS_QUERY;
                break;
            case BIG:
                sql = BIG_QUEUE_JOB_DETAILS_QUERY;
                break;
        }

        final Object[] parameters = new Object[2];
        parameters[0] = jobName;
        parameters[1] = jobGroup;

        try {
            result = getSimpleJdbcTemplate().queryForObject(sql, getQuartzQueueJobDetailsRowMapper(), parameters);

        } catch(final EmptyResultDataAccessException e) {
            // Do nothing
        }

        return result;
    }

    /**
     * Enum for the two types of Quartz queues
     */
    private enum QuartzQueueType {
        SMALL,
        BIG
    }

    /*
     * Getters / Setters
     */

    public ParameterizedRowMapper<QuartzQueueJobDetails> getQuartzQueueJobDetailsRowMapper() {
        return quartzQueueJobDetailsRowMapper;
    }

    public void setQuartzQueueJobDetailsRowMapper(final ParameterizedRowMapper<QuartzQueueJobDetails> quartzQueueJobDetailsRowMapper) {
        this.quartzQueueJobDetailsRowMapper = quartzQueueJobDetailsRowMapper;
    }
}
