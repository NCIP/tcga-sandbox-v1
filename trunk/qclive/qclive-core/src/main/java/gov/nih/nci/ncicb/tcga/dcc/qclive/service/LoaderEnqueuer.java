/*
 * The caBIG Software License, Version 1.0 Copyright 2009 TCGA DCC/Portal Project (�Cancer Center�)
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.service;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.common.service.FileTypeLookup;
import gov.nih.nci.ncicb.tcga.dcc.common.service.StatusCallback;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.Loader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.Logger;
import org.apache.log4j.Level;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.scheduling.quartz.JobDetailBean;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

/**
 * Implementation of LoaderStarter that adds jobs to a quartz queue.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

/**
 * Puts the Loader in the Quartz queue.
 */
public class LoaderEnqueuer implements LoaderStarter {

    protected static final String JOBRUNNER_MAPNAME = "loaderRunner";
    private static LoaderEnqueuer instance;
    private static final Object queueLock = new Object();

    public static LoaderStarter getLoaderStarter() {
        return getLoaderEnqueuer();
    }

    public static LoaderEnqueuer getLoaderEnqueuer() {
        if (instance == null) {
            instance = new LoaderEnqueuer();
        }
        return instance;
    }

    protected Scheduler quartzScheduler;
    protected LoaderQueries loaderQueries;
    protected ArchiveQueries commonArchiveQueries;
    protected ArchiveQueries diseaseArchiveQueries;
    protected UUIDDAO uuidDAO;
    protected ProcessLogger logger;
    protected MailSender mailSender;
    protected String mailTo;
    protected String switchOnTime, switchOffTime;

    // protected, and Spring uses the getLoaderEnqueuer factory method

    protected LoaderEnqueuer() {
        instance = this;
        logger = new ProcessLogger();
    }


    public SimpleTriggerBean getTrigger() {
        final SimpleTriggerBean trigger = new SimpleTriggerBean();
        trigger.setRepeatCount(0);
        return trigger;
    }

    public SimpleTrigger getTrigger(final JobDetail jobDetail) {
        final SimpleTriggerBean trigger = getTrigger();
        trigger.setJobDetail(jobDetail);
        trigger.setName(jobDetail.getName());
        trigger.setGroup(jobDetail.getGroup());
        trigger.setJobGroup(jobDetail.getGroup());
        trigger.setJobName(jobDetail.getName());
        trigger.setJobDataAsMap(jobDetail.getJobDataMap());
        trigger.setRepeatCount(0);
        return trigger;
    }

    public JobDetail getJobDetail() {
        final JobDetail jobDetail = new JobDetailBean();
        jobDetail.setJobClass(gov.nih.nci.ncicb.tcga.dcc.qclive.service.LoaderJob.class);
        return jobDetail;
    }

    public void setQuartzScheduler(final Scheduler quartzScheduler) throws SchedulerException {
        this.quartzScheduler = quartzScheduler;
        // Do not remove this comment. In case if we need 'running job at off-peak hrs' feature, uncomment the
        // following
//        startTimer();
    }

    public void setLoaderQueries(final LoaderQueries loaderQueries) {
        this.loaderQueries = loaderQueries;
    }

    public LoaderQueries getLoaderQueries() {
        return loaderQueries;
    }

    public void setDiseaseArchiveQueries(final ArchiveQueries diseaseArchiveQueries) {
        this.diseaseArchiveQueries = diseaseArchiveQueries;
    }

    public ArchiveQueries getDiseaseArchiveQueries() {
        return diseaseArchiveQueries;
    }

    public void setCommonArchiveQueries(final ArchiveQueries commonArchiveQueries) {
        this.commonArchiveQueries = commonArchiveQueries;
    }

    public ArchiveQueries getCommonArchiveQueries() {
        return commonArchiveQueries;
    }

    public UUIDDAO getUuidDAO() {
        return uuidDAO;
    }

    public void setUuidDAO(final UUIDDAO uuidDAO) {
        this.uuidDAO = uuidDAO;
    }

    public void setMailSender(final MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public MailSender getMailSender() {
        return mailSender;
    }

    public void setMailTo(final String mailTo) {
        this.mailTo = mailTo;
    }

    /**
     * What time the Quartz queue should start processing jobs. Entered in military time, like
     * "19:30".  Until this time, the queue will just hold jobs in reserve.
     *
     * @param switchOnTime time after which new jobs can be started
     * @throws IllegalArgumentException if the time string is of the wrong format
     * @throws SchedulerException       if there is a problem with the 1scheduler
     */
    public void setSwitchOnTime(final String switchOnTime) throws IllegalArgumentException, SchedulerException {
        // Do not remove this comment. In case if we need 'running job at off-peak hrs' feature, uncomment the
        // following
        //      validateTime(switchOnTime);
        this.switchOnTime = switchOnTime;
        //      startTimer();
    }

    /**
     * What time the Quartz queue should stop processing jobs.  Entered in military time, like
     * "07:00".  Any job that is already processing will be allowed to continue, but no new jobs
     * will be started until the switchOnTime comes around.
     *
     * @param switchOffTime time after which new jobs should not be started
     * @throws IllegalArgumentException if the time string is of the wrong format
     * @throws SchedulerException       if there is a problem with the scheduler
     */
    public void setSwitchOffTime(final String switchOffTime) throws IllegalArgumentException, SchedulerException {
        // Do not remove this comment. In case if we need 'running job at off-peak hrs' feature, uncomment the
        // following
        //     validateTime(switchOffTime);
        this.switchOffTime = switchOffTime;
        //     startTimer();
    }

    private void startTimer() throws SchedulerException {
        // only run once Spring has set all three fields
        if (switchOnTime == null || switchOffTime == null || quartzScheduler == null) {
            return;
        }
        int switchOnHours = Integer.parseInt(switchOnTime.substring(0, 2));
        int switchOnMinutes = Integer.parseInt(switchOnTime.substring(3));
        int switchOffHours = Integer.parseInt(switchOffTime.substring(0, 2));
        int switchOffMinutes = Integer.parseInt(switchOffTime.substring(3));
        Calendar now = Calendar.getInstance();

        Calendar switchOnCalendar = (Calendar) now.clone();
        switchOnCalendar.set(Calendar.HOUR_OF_DAY, switchOnHours);
        switchOnCalendar.set(Calendar.MINUTE, switchOnMinutes);
        // the timer won't start (and repeat) if the original date/time has passed
        // so we need a calendar in the future
        Calendar timerSwitchOnCalendar = (Calendar) switchOnCalendar.clone();
        if (switchOnCalendar.before(now)) {
            //roll forward one day
            timerSwitchOnCalendar.roll(Calendar.DAY_OF_MONTH, 1);
        }

        Calendar switchOffCalendar = (Calendar) now.clone();
        switchOffCalendar.set(Calendar.HOUR_OF_DAY, switchOffHours);
        switchOffCalendar.set(Calendar.MINUTE, switchOffMinutes);
        // the timer won't start (and repeat) if the original date/time has passed
        // so we need a calendar in the future
        Calendar timerSwitchOffCalendar = (Calendar) switchOffCalendar.clone();
        if (switchOffCalendar.before(now)) {
            //roll forward one day
            timerSwitchOffCalendar.roll(Calendar.DAY_OF_MONTH, 1);
        }

        // off time should be early in the day and on time should be later in the day, therefore
        // if now is in between off and on (during the workday), don't start.  otherwise start
        // use the pre-rolled values to keep your head from spinning 
        if (now.after(switchOffCalendar) && now.before(switchOnCalendar)) {
            quartzScheduler.standby();
        } else {
            quartzScheduler.start();
        }


        Timer timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        try {
                            if (!quartzScheduler.isStarted()) {
                                // The scheduler should already be running on the weekend.
                                // see the TimerTask below
                                quartzScheduler.start();
                            }
                        } catch (SchedulerException e) {
                            Logger.getLogger().logError(e);
                        }
                    }
                },
                timerSwitchOnCalendar.getTime(),
                getTimerInterval());

        timer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        try {
                            Calendar cal = Calendar.getInstance();
                            if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                                    & cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                                // No reason to stand by on the weekend as the system is not heavily
                                // used at that time
                                quartzScheduler.standby();
                            }
                        } catch (SchedulerException e) {
                            Logger.getLogger().logError(e);
                        }
                    }
                },
                timerSwitchOffCalendar.getTime(),
                getTimerInterval());
    }

    protected long getTimerInterval() {
        return 86400000; //milliseconds in a day
    }

    //military time - "##:##"   no AM or PM.

    private void validateTime(final String timeStr) throws IllegalArgumentException {
        String regex = "(00|01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23)[:](0|1|2|3|4|5)\\d{1}";
        if (!Pattern.matches(regex, timeStr)) {
            throw new IllegalArgumentException("Time must be specified as hh:mm");
        }
    }

    /**
     * External callers should call this through the LoaderStarter interface.  It creates a Loader and
     * queues it.
     *
     * @param loadDirectory    Location of the exploded archive containing data files
     * @param magetabDirectory (Optional) Location of the magetab archive containing SDRF. If archive is old style, leave null
     * @param ftLookup         callback for the Loader to look up information about file type, level, and platform
     * @param statusCallback   callback for the Loader to report the current job's status to the caller
     * @throws SchedulerException
     */
    public void queueLoaderJob(final String loadDirectory,
                               final String magetabDirectory,
                               final FileTypeLookup ftLookup,
                               final StatusCallback statusCallback,
                               final String experimentName) throws SchedulerException {
        Loader loader = new Loader();
        loader.setLoadDirectory(loadDirectory);
        loader.setMagetabDirectory(magetabDirectory);
        loader.setFileTypeLookup(ftLookup);
        loader.setStatusCallback(statusCallback);
        loader.setMailSender(mailSender);
        loader.setMailTo(mailTo);
        loader.setLoaderQueries(loaderQueries);
        loader.setCommonArchiveQueries(commonArchiveQueries);
        loader.setDiseaseArchiveQueries(diseaseArchiveQueries);
        loader.setUuidDAO(uuidDAO);
        queueLoaderJob(loader, experimentName);
    }

    private void queueLoaderJob(final Loader loader, final String experimentName) throws SchedulerException {
        String jobname = String.valueOf((new Random()).nextInt(1000) + System.currentTimeMillis());
        LoaderRunner runner = new LoaderRunner();
        runner.setLoader(loader);

        final JobDetail jobDetail = getJobDetail();
        jobDetail.setName(jobname);
        jobDetail.getJobDataMap().put(JOBRUNNER_MAPNAME, runner);
        jobDetail.setGroup(getLoaderJobGroupName(experimentName));

        final Trigger trigger = getTrigger(jobDetail);
        trigger.setStartTime(new Date());

        logger.logToLogger(Level.INFO, "Scheduling loading of " + loader.getLoadDirectory() + " for " + trigger.getStartTime().toString());
        synchronized (queueLock) {
            quartzScheduler.scheduleJob(jobDetail, trigger);
        }
    }

    // JobGroupName to identify the loader jobs belongs to the same experiment.
    // It is based on experiment name. Do not change the group name.

    protected String getLoaderJobGroupName(final String experimentName) {
        return ConstantValues.LEVEL_2_LOADER_GROUP_NAME + "_" + experimentName;
    }

}
