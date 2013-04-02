DROP TABLE subscriber_group CASCADE CONSTRAINTS;
CREATE TABLE subscriber_group
(
   group_id		NUMBER(38)  	NOT NULL,
   group_name		VARCHAR2(30) 	NOT NULL,
   group_description	VARCHAR2(200)	NOT NULL,
   CONSTRAINT subscriber_group_pk_idx PRIMARY KEY (group_id)
);

DROP TABLE contact CASCADE CONSTRAINTS;
CREATE TABLE contact 
(
   contact_id		NUMBER(38)  	NOT NULL,
   first_name		VARCHAR2(30) 	NOT NULL,
   middle_initial 	CHAR(1)	,
   last_name		VARCHAR2(30)	NOT NULL,
   suffix		VARCHAR2(15),
   email_address	VARCHAR2(254)	NOT NULL,
   CONSTRAINT contact_pk_idx PRIMARY KEY (contact_id)
);

DROP TABLE group_contact CASCADE CONSTRAINTS;
CREATE TABLE group_contact 
(
   group_contact_id	NUMBER(38)  	NOT NULL,
   group_id		NUMBER(38)  	NOT NULL,
   contact_id		NUMBER(38)  	NOT NULL,
   CONSTRAINT group_contact_pk_idx PRIMARY KEY (group_contact_id)
);

DROP TABLE event CASCADE CONSTRAINTS;
CREATE TABLE event 
(
   event_id		NUMBER(38)  	NOT NULL,
   event_name		VARCHAR2(30)	NOT NULL,
   event_description	VARCHAR2(200)	NOT NULL,
   CONSTRAINT event_pk_idx PRIMARY KEY (event_id)
);

DROP TABLE notification_method CASCADE CONSTRAINTS;
CREATE TABLE notification_method 
(
   notification_method_id	NUMBER(38)  	NOT NULL,
   method_name			VARCHAR2(20)	NOT NULL,
   CONSTRAINT notify_method_pk_idx PRIMARY KEY (notification_method_id)
);

DROP TABLE notification_frequency CASCADE CONSTRAINTS;
CREATE TABLE notification_frequency
(
   notification_frequency_id	NUMBER(38)	NOT NULL,
   frequency_name		VARCHAR2(20)	NOT NULL,
   frequency_pattern		VARCHAR2(50),
   CONSTRAINT notification_frequency_pk_idx PRIMARY KEY (notification_frequency_id)
);

DROP TABLE subscription CASCADE CONSTRAINTS;
CREATE TABLE subscription 
(
   subscription_id		NUMBER(38)  	NOT NULL,
   subscriber_id		NUMBER(38)  	NOT NULL,
   last_notification_date	TIMESTAMP,
   event_id			NUMBER(38)  	NOT NULL,
   notification_method_id	NUMBER(38)  	NOT NULL,
   notification_frequency_id	NUMBER(38)	NOT NULL,
   CONSTRAINT contact_subscript_pk_idx PRIMARY KEY (subscription_id)
);

DROP TABLE subscription_disease CASCADE CONSTRAINTS;
CREATE TABLE subscription_disease
(
   subscription_disease_id	NUMBER(38)	NOT NULL,
   subscription_id		NUMBER(38)	NOT NULL,
   disease_id			NUMBER(38)	NOT NULL,
   CONSTRAINT subscript_disease_pk_idx PRIMARY KEY (subscription_disease_id)
);

DROP SEQUENCE contact_seq;
CREATE SEQUENCE contact_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE subscription_seq;
CREATE SEQUENCE subscription_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE event_seq;
CREATE SEQUENCE event_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE method_seq;
CREATE SEQUENCE method_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE group_seq;
CREATE SEQUENCE group_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE notification_freq_seq;
CREATE SEQUENCE notification_freq_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE group_contact_seq;
CREATE SEQUENCE group_contact_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE subscript_disease_seq;
CREATE SEQUENCE subscript_disease_seq START WITH 1 INCREMENT BY 1;

ALTER TABLE subscription_disease ADD (
   CONSTRAINT fk_subscr_disease_subscript
   FOREIGN KEY (subscription_id)
   REFERENCES subscription(subscription_id),
   CONSTRAINT fk_subscr_disease_disease
   FOREIGN KEY (disease_id)
   REFERENCES disease(disease_id)
);

ALTER TABLE subscription ADD (
   CONSTRAINT fk_subscr_event
   FOREIGN KEY (event_id)
   REFERENCES event(event_id),
   CONSTRAINT fk_subscr_notify_method
   FOREIGN KEY (notification_method_id)
   REFERENCES notification_method(notification_method_id),
   CONSTRAINT fk_subscr_notify_freq
   FOREIGN KEY (notification_frequency_id)
   REFERENCES notification_method(notification_frequency_id)
);

ALTER TABLE group_contact ADD (
   CONSTRAINT fk_group_contact_group
   FOREIGN KEY (group_id)
   REFERENCES group(group_id),
   CONSTRAINT fk_group_contact_contact
   FOREIGN KEY (contact_id)
   REFERENCES contact(contact_id)
);
   
