drop table loader_calendars;
drop table loader_fired_triggers;
drop table loader_trigger_listeners;
drop table loader_blob_triggers;
drop table loader_cron_triggers;
drop table loader_simple_triggers;
drop table loader_triggers;
drop table loader_job_listeners;
drop table loader_job_details;
drop table loader_paused_trigger_grps;
drop table loader_locks;
drop table loader_scheduler_state;


CREATE TABLE loader_job_details
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
CREATE TABLE loader_job_listeners
  (
    JOB_NAME  VARCHAR2(200) NOT NULL,
    JOB_GROUP VARCHAR2(200) NOT NULL,
    JOB_LISTENER VARCHAR2(200) NOT NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER),
    FOREIGN KEY (JOB_NAME,JOB_GROUP)
    REFERENCES loader_JOB_DETAILS(JOB_NAME,JOB_GROUP)
);
CREATE TABLE loader_triggers
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
    REFERENCES loader_JOB_DETAILS(JOB_NAME,JOB_GROUP)
);
CREATE TABLE loader_simple_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    REPEAT_COUNT NUMBER(7) NOT NULL,
    REPEAT_INTERVAL NUMBER(12) NOT NULL,
    TIMES_TRIGGERED NUMBER(10) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES loader_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE loader_cron_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    CRON_EXPRESSION VARCHAR2(120) NOT NULL,
    TIME_ZONE_ID VARCHAR2(80),
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES loader_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE loader_blob_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES loader_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE loader_trigger_listeners
  (
    TRIGGER_NAME  VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    TRIGGER_LISTENER VARCHAR2(200) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES loader_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE loader_calendars
  (
    CALENDAR_NAME  VARCHAR2(200) NOT NULL,
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (CALENDAR_NAME)
);
CREATE TABLE loader_paused_trigger_grps
  (
    TRIGGER_GROUP  VARCHAR2(200) NOT NULL,
    PRIMARY KEY (TRIGGER_GROUP)
);
CREATE TABLE loader_fired_triggers
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
CREATE TABLE loader_scheduler_state
  (
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    LAST_CHECKIN_TIME NUMBER(13) NOT NULL,
    CHECKIN_INTERVAL NUMBER(13) NOT NULL,
    PRIMARY KEY (INSTANCE_NAME)
);
CREATE TABLE loader_locks
  (
    LOCK_NAME  VARCHAR2(40) NOT NULL,
    PRIMARY KEY (LOCK_NAME)
);
INSERT INTO loader_locks values('TRIGGER_ACCESS');
INSERT INTO loader_locks values('JOB_ACCESS');
INSERT INTO loader_locks values('CALENDAR_ACCESS');
INSERT INTO loader_locks values('STATE_ACCESS');
INSERT INTO loader_locks values('MISFIRE_ACCESS');
commit;
create index idx_loader_j_req_recovery on loader_job_details(REQUESTS_RECOVERY);
create index idx_loader_t_next_fire_time on loader_triggers(NEXT_FIRE_TIME);
create index idx_loader_t_state on loader_triggers(TRIGGER_STATE);
create index idx_loader_t_nft_st on loader_triggers(NEXT_FIRE_TIME,TRIGGER_STATE);
create index idx_loader_t_volatile on loader_triggers(IS_VOLATILE);
create index idx_loader_ft_trig_name on loader_fired_triggers(TRIGGER_NAME);
create index idx_loader_ft_trig_group on loader_fired_triggers(TRIGGER_GROUP);
create index idx_loader_ft_trig_nm_gp on loader_fired_triggers(TRIGGER_NAME,TRIGGER_GROUP);
create index idx_loader_ft_trig_volatile on loader_fired_triggers(IS_VOLATILE);
create index idx_loader_ft_trig_inst_name on loader_fired_triggers(INSTANCE_NAME);
create index idx_loader_ft_job_name on loader_fired_triggers(JOB_NAME);
create index idx_loader_ft_job_group on loader_fired_triggers(JOB_GROUP);
create index idx_loader_ft_job_stateful on loader_fired_triggers(IS_STATEFUL);
create index idx_loader_ft_job_req_recov on loader_fired_triggers(REQUESTS_RECOVERY);


