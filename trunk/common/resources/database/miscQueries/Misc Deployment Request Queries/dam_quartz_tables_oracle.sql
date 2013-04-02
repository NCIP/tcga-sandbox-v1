--
-- A hint submitted by a user: Oracle DB MUST be created as "shared" and the 
-- job_queue_processes parameter  must be greater than 2, otherwise a DB lock 
-- will happen.   However, these settings are pretty much standard after any
-- Oracle install, so most users need not worry about this.
--
-- Many other users (including the primary author of Quartz) have had success
-- runing in dedicated mode, so only consider the above as a hint ;-)
--

delete from dam_qrtz_job_listeners;
delete from dam_qrtz_trigger_listeners;
delete from dam_qrtz_fired_triggers;
delete from dam_qrtz_simple_triggers;
delete from dam_qrtz_cron_triggers;
delete from dam_qrtz_blob_triggers;
delete from dam_qrtz_triggers;
delete from dam_qrtz_job_details;
delete from dam_qrtz_calendars;
delete from dam_qrtz_paused_trigger_grps;
delete from dam_qrtz_locks;
delete from dam_qrtz_scheduler_state;

drop table dam_qrtz_calendars;
drop table dam_qrtz_fired_triggers;
drop table dam_qrtz_trigger_listeners;
drop table dam_qrtz_blob_triggers;
drop table dam_qrtz_cron_triggers;
drop table dam_qrtz_simple_triggers;
drop table dam_qrtz_triggers;
drop table dam_qrtz_job_listeners;
drop table dam_qrtz_job_details;
drop table dam_qrtz_paused_trigger_grps;
drop table dam_qrtz_locks;
drop table dam_qrtz_scheduler_state;


CREATE TABLE dam_qrtz_job_details
  (
    JOB_NAME  VARCHAR2(200) NOT NULL,
    JOB_GROUP VARCHAR2(200) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    JOB_CLASS_NAME   VARCHAR2(250) NOT NULL, 
    IS_DURABLE VARCHAR2(1) NOT NULL,
    IS_VOLATILE VARCHAR2(1) NOT NULL,
    IS_STATEFUL VARCHAR2(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR2(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP)
);
CREATE TABLE dam_qrtz_job_listeners
  (
    JOB_NAME  VARCHAR2(200) NOT NULL, 
    JOB_GROUP VARCHAR2(200) NOT NULL,
    JOB_LISTENER VARCHAR2(200) NOT NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER),
    FOREIGN KEY (JOB_NAME,JOB_GROUP) 
	REFERENCES dam_qrtz_JOB_DETAILS(JOB_NAME,JOB_GROUP)
);
CREATE TABLE dam_qrtz_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    JOB_NAME  VARCHAR2(200) NOT NULL, 
    JOB_GROUP VARCHAR2(200) NOT NULL,
    IS_VOLATILE VARCHAR2(1) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    NEXT_FIRE_TIME NUMBER(13) NULL,
    PREV_FIRE_TIME NUMBER(13) NULL,
    PRIORITY NUMBER(13) NULL,
    TRIGGER_STATE VARCHAR2(16) NOT NULL,
    TRIGGER_TYPE VARCHAR2(8) NOT NULL,
    START_TIME NUMBER(13) NOT NULL,
    END_TIME NUMBER(13) NULL,
    CALENDAR_NAME VARCHAR2(200) NULL,
    MISFIRE_INSTR NUMBER(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (JOB_NAME,JOB_GROUP) 
	REFERENCES dam_qrtz_JOB_DETAILS(JOB_NAME,JOB_GROUP) 
);
CREATE TABLE dam_qrtz_simple_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    REPEAT_COUNT NUMBER(7) NOT NULL,
    REPEAT_INTERVAL NUMBER(12) NOT NULL,
    TIMES_TRIGGERED NUMBER(10) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES dam_qrtz_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE dam_qrtz_cron_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    CRON_EXPRESSION VARCHAR2(120) NOT NULL,
    TIME_ZONE_ID VARCHAR2(80),
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES dam_qrtz_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE dam_qrtz_blob_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
        REFERENCES dam_qrtz_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE dam_qrtz_trigger_listeners
  (
    TRIGGER_NAME  VARCHAR2(200) NOT NULL, 
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    TRIGGER_LISTENER VARCHAR2(200) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES dam_qrtz_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE dam_qrtz_calendars
  (
    CALENDAR_NAME  VARCHAR2(200) NOT NULL, 
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (CALENDAR_NAME)
);
CREATE TABLE dam_qrtz_paused_trigger_grps
  (
    TRIGGER_GROUP  VARCHAR2(200) NOT NULL, 
    PRIMARY KEY (TRIGGER_GROUP)
);
CREATE TABLE dam_qrtz_fired_triggers 
  (
    ENTRY_ID VARCHAR2(95) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    IS_VOLATILE VARCHAR2(1) NOT NULL,
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    FIRED_TIME NUMBER(13) NOT NULL,
    PRIORITY NUMBER(13) NOT NULL,
    STATE VARCHAR2(16) NOT NULL,
    JOB_NAME VARCHAR2(200) NULL,
    JOB_GROUP VARCHAR2(200) NULL,
    IS_STATEFUL VARCHAR2(1) NULL,
    REQUESTS_RECOVERY VARCHAR2(1) NULL,
    PRIMARY KEY (ENTRY_ID)
);
CREATE TABLE dam_qrtz_scheduler_state 
  (
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    LAST_CHECKIN_TIME NUMBER(13) NOT NULL,
    CHECKIN_INTERVAL NUMBER(13) NOT NULL,
    PRIMARY KEY (INSTANCE_NAME)
);
CREATE TABLE dam_qrtz_locks
  (
    LOCK_NAME  VARCHAR2(40) NOT NULL, 
    PRIMARY KEY (LOCK_NAME)
);
INSERT INTO dam_qrtz_locks values('TRIGGER_ACCESS');
INSERT INTO dam_qrtz_locks values('JOB_ACCESS');
INSERT INTO dam_qrtz_locks values('CALENDAR_ACCESS');
INSERT INTO dam_qrtz_locks values('STATE_ACCESS');
INSERT INTO dam_qrtz_locks values('MISFIRE_ACCESS');
create index idx_dam_j_req_recovery on dam_qrtz_job_details(REQUESTS_RECOVERY);
create index idx_dam_t_next_fire_time on dam_qrtz_triggers(NEXT_FIRE_TIME);
create index idx_dam_t_state on dam_qrtz_triggers(TRIGGER_STATE);
create index idx_dam_t_nft_st on dam_qrtz_triggers(NEXT_FIRE_TIME,TRIGGER_STATE);
create index idx_dam_t_volatile on dam_qrtz_triggers(IS_VOLATILE);
create index idx_dam_ft_trig_name on dam_qrtz_fired_triggers(TRIGGER_NAME);
create index idx_dam_ft_trig_group on dam_qrtz_fired_triggers(TRIGGER_GROUP);
create index idx_dam_ft_trig_nm_gp on dam_qrtz_fired_triggers(TRIGGER_NAME,TRIGGER_GROUP);
create index idx_dam_ft_trig_volatile on dam_qrtz_fired_triggers(IS_VOLATILE);
create index idx_dam_ft_trig_inst_name on dam_qrtz_fired_triggers(INSTANCE_NAME);
create index idx_dam_ft_job_name on dam_qrtz_fired_triggers(JOB_NAME);
create index idx_dam_ft_job_group on dam_qrtz_fired_triggers(JOB_GROUP);
create index idx_dam_ft_job_stateful on dam_qrtz_fired_triggers(IS_STATEFUL);
create index idx_dam_ft_job_req_recovery on dam_qrtz_fired_triggers(REQUESTS_RECOVERY);


commit;
