package org.quartz.impl.jdbcjobstore;

import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import org.quartz.JobDataMap;
import org.quartz.JobPersistenceException;
import org.quartz.SchedulerConfigException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerSignaler;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;
import org.quartz.utils.Key;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Subclass of Quartz's JobStoreCMT class which provides the following functionalities:
 * -  Delegates to a Spring-managed DataSource instead of Quartz-managed connection pool
 * -  Invokes jobs in the order they were scheduled. (Quartz invokes jobs based on the
 * scheduled time, but if the scheduled time is missed it doesn't invoke the job in the
 * order it was scheduled)
 * Usage:
 * 1. Define the following in application context class
 * <bean id="applicationContextProvider" class="gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext" lazy-init="false">
 * </bean>
 * 2. Define data source bean
 * Eg.
 * <bean id="quartzDataSource" class="org.springframework.jndi.JndiObjectFactoryBean" lazy-init="true">
 * <property name="jndiName" value="java:quartzOracleDS"/>
 * </bean>
 * 3. Define the following properties in scheduler bean
 * <bean id="bigjobScheduler" class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
 * <property name="schedulerName" value="bigjobScheduler"/>
 * <property name="quartzProperties">
 * <props>
 * <!-- this should match the datasource bean name defined in spring application context file-->
 * <prop key="org.quartz.jobStore.dataSource">quartzDataSource</prop>
 * <prop key="org.quartz.jobStore.class">org.quartz.impl.jdbcjobstore.QueueJobStore</prop>
 * <prop key="org.quartz.jobStore.driverDelegateClass">org.quartz.impl.jdbcjobstore.oracle.OracleDelegate</prop>
 * <prop key="org.quartz.jobStore.tablePrefix">DAM_BIGJOB_</prop>
 * </props>
 * </property>
 * </bean>
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QueueJobStore extends JobStoreCMT {
    // To identify the jobs which are already fired but not completed
    public static final String RECOVERED_INTERRUPTED_JOB = "recovered_incomplete_job";

    /**
     * Initializes  Spring-managed connection pool instead of Quartz-managed connection pool
     * The datasource name is  injected from <prop key="org.quartz.jobStore.dataSource">,
     * the corresponding datasource bean and gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext
     * should be defined in spring application context file
     *
     * @param loadHelper
     * @param signaler
     * @throws SchedulerConfigException
     */
    public void initialize(final ClassLoadHelper loadHelper, final SchedulerSignaler signaler) throws SchedulerConfigException {

        setDontSetAutoCommitFalse(true);
        try {
            //  set non-transactional data source
            if (getNonManagedTXDataSource() == null) {
                setNonManagedTXDataSource(getDataSource());
            }
            // get transactional data source
            //ApplicationContext ctx = SpringApplicationContext.getApplicationContext();
            final DataSource dataSource = (DataSource) SpringApplicationContext.getObject(getDataSource());
            // get non-transactional data source
            final DataSource nonTxDataSource = (DataSource) SpringApplicationContext.getObject(getNonManagedTXDataSource());


            // Register spring-managed transactional connection provider for Quartz
            DBConnectionManager.getInstance().addConnectionProvider(
                    getDataSource(),
                    new ConnectionProvider() {
                        public Connection getConnection() throws SQLException {
                            // Return a transactional Connection, if any.
                            return DataSourceUtils.doGetConnection(dataSource);
                        }

                        public void shutdown() {
                            // Do nothing - a Spring-managed DataSource has its own lifecycle.
                        }
                    }
            );

            // Register Spring-managed non-transactional connection provider for Quartz
            DBConnectionManager.getInstance().addConnectionProvider(
                    getNonManagedTXDataSource(),
                    new ConnectionProvider() {
                        public Connection getConnection() throws SQLException {
                            // Always return a non-transactional Connection.
                            return nonTxDataSource.getConnection();
                        }

                        public void shutdown() {
                            // Do nothing - a Spring-managed DataSource has its own lifecycle.
                        }
                    }
            );
            getLog().info("Using spring managed connection provider");
        } catch (Exception e) {
            getLog().warn("Cannot use spring managed connection provider" + e.toString() + ". Falling back to quartz connection provider");
        }

        super.initialize(loadHelper, signaler);

    }

    /**
     * return the misfire time to be 0 so that quartz gets the jobs in the next firing time order
     *
     * @return
     */
    protected long getMisfireTime() {
        return 0;
    }

    /**
     * Recover any failed or misfired jobs and clean up the data store as
     * appropriate.
     *
     * @param conn
     * @throws JobPersistenceException
     */
    protected void recoverJobs(final Connection conn) throws JobPersistenceException {
        try {
            // update inconsistent job states
            int rows = getDelegate().updateTriggerStatesFromOtherStates(conn,
                    STATE_WAITING, STATE_ACQUIRED, STATE_BLOCKED);

            rows += getDelegate().updateTriggerStatesFromOtherStates(conn,
                    STATE_PAUSED, STATE_PAUSED_BLOCKED, STATE_PAUSED_BLOCKED);

            getLog().info("Freed " + rows + " triggers from 'acquired' / 'blocked' state.");

            // clean up misfired jobs
            recoverMisfiredJobs(conn, true);

            // recover jobs marked for recovery that were not fully executed
            Trigger[] recoveringJobTriggers = getDelegate()
                    .selectTriggersForRecoveringJobs(conn);
            getLog().info("Recovering " + recoveringJobTriggers.length + " jobs that were in-progress at the time of the last shut-down.");

            for (int i = 0; i < recoveringJobTriggers.length; ++i) {
                if (jobExists(conn, recoveringJobTriggers[i].getJobName(),
                        recoveringJobTriggers[i].getJobGroup())) {
                    recoveringJobTriggers[i].computeFirstFireTime(null);
                    storeTrigger(conn, null, recoveringJobTriggers[i], null, false,
                            STATE_WAITING, false, true);
                }
            }
            getLog().info("Recovery complete.");

            // As we have to invoke the jobs which are already in complete state, update
            // complete triggers next firing time to trigger start time
            Key[] ct = getDelegate().selectTriggersInState(conn, STATE_COMPLETE);
            for (int i = 0; ct != null && i < ct.length; i++) {
                Trigger trigger = getDelegate().selectTrigger(conn, ct[i].getName(), ct[i].getGroup());
                updateCompletedTriggerToWaiting(conn, ct[i].getName(), ct[i].getGroup());
            }

            // clean up any fired trigger entries
            int n = getDelegate().deleteFiredTriggers(conn);
            getLog().info("Removed " + n + " stale fired job entries.");
        } catch (JobPersistenceException e) {
            throw e;
        } catch (Exception e) {
            throw new JobPersistenceException("Couldn't recover jobs: " + e.getMessage(), e);
        }
    }

    /**
     * Update complete trigger next firing time to trigger start time
     *
     * @param conn
     * @param triggerName
     * @param groupName
     * @return
     * @throws JobPersistenceException
     */
    protected boolean updateCompletedTriggerToWaiting(Connection conn,
                                                      String triggerName, String groupName)
            throws JobPersistenceException {
        boolean updatedTrigger = false;
        try {
            // this must be called before we delete the trigger, obviously
            Trigger trigger = getDelegate().selectTrigger(conn, triggerName, groupName);
            if (trigger instanceof SimpleTrigger) {
                ((SimpleTrigger) trigger).setNextFireTime(((SimpleTrigger) trigger).getStartTime());
                JobDataMap jobDataMap = trigger.getJobDataMap();
                // this parameter can be used to identify whether the job is already fired but not completed
                jobDataMap.put(RECOVERED_INTERRUPTED_JOB, true);
            }
            getDelegate().updateTrigger(conn, trigger, STATE_WAITING, null);
            getLog().info(
                    "Updated " + triggerName + " next firing time " + trigger.getStartTime() + " status from complete to waiting.");

        } catch (ClassNotFoundException e) {
            throw new JobPersistenceException("Couldn't update trigger: "
                    + e.getMessage(), e);
        } catch (SQLException e) {
            throw new JobPersistenceException("Couldn't update trigger: "
                    + e.getMessage(), e);
        } catch (IOException e) {
            throw new JobPersistenceException("Couldn't update trigger: "
                    + e.getMessage(), e);
        }

        return updatedTrigger;
    }

}
