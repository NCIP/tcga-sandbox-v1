ALTER TABLE qrtz_job_history ADD(
	time_enqueued 	TIMESTAMP ,
	queue_name  	VARCHAR2(100),
	is_complete 	NUMBER(1) DEFAULT 1 NOT NULL);