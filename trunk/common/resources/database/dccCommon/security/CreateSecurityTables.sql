  DROP TABLE users CASCADE CONSTRAINTS;
 CREATE TABLE users(
      username VARCHAR2(50) NOT NULL,
      password VARCHAR2(50) NOT NULL,
      enabled  NUMBER(1)    NOT NULL,
      CONSTRAINT pk_users_idx PRIMARY KEY(username));

DROP TABLE authorities  CASCADE CONSTRAINTS;
CREATE TABLE authorities (
      username 	VARCHAR2(50) NOT NULL,
      authority VARCHAR2(50) NOT NULL,
      CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username));

CREATE UNIQUE INDEX ix_auth_username ON authorities (username,authority);

DROP SEQUENCE group_seq;
CREATE SEQUENCE group_seq START WITH 1 INCREMENT BY 1;

DROP TABLE groups CASCADE CONSTRAINTS;
CREATE TABLE groups (
  id 		NUMBER(38) 	NOT NULL, 
  group_name 	VARCHAR2(50) 	NOT NULL,
  CONSTRAINT pk_groups_idx PRIMARY KEY (id));

DROP TABLE group_authorities CASCADE CONSTRAINTS;
CREATE TABLE group_authorities (
  group_id  NUMBER(38)   NOT NULL, 
  authority VARCHAR2(50) NOT NULL, 
  CONSTRAINT fk_group_authorities_group FOREIGN KEY(group_id) REFERENCES groups(id));

DROP SEQUENCE group_members_seq;
CREATE SEQUENCE group_members_seq START WITH 1 INCREMENT BY 1;

DROP TABLE group_members CASCADE CONSTRAINTS;
CREATE TABLE group_members (
  id 		NUMBER(38)      NOT NULL, 
  username 	VARCHAR2(50) 	NOT NULL, 
  group_id 	NUMBER(38) 	NOT NULL, 
  CONSTRAINT pk_group_member_idx PRIMARY KEY(id),
  CONSTRAINT fk_group_members_group FOREIGN KEY(group_id) REFERENCES groups(id));
        

DROP TABLE persistent_logins CASCADE CONSTRAINTS;
CREATE TABLE persistent_logins (
  username 	VARCHAR2(64) 	NOT NULL, 
  series 	VARCHAR2(64) 	NOT NULL,
  token 	VARCHAR2(64) 	NOT NULL, 
  last_used 	TIMESTAMP 	NOT NULL,
  CONSTRAINT pk_persistent_login_idx PRIMARY KEY(series));

DROP SEQUENCE acl_sid_seq;
CREATE SEQUENCE acl_sid_seq START WITH 100 INCREMENT BY 1; 

DROP TABLE acl_sid CASCADE CONSTRAINTS;
CREATE TABLE acl_sid (
  id 		NUMBER(38) 	NOT NULL,
  principal 	NUMBER(1) 	NOT NULL,
  sid 		VARCHAR2(100) 	NOT NULL,
  CONSTRAINT pk_acl_idx PRIMARY KEY(id),
  CONSTRAINT uk_acl_sid_idx UNIQUE(sid,principal) );

DROP SEQUENCE acl_class_seq;
CREATE SEQUENCE acl_class_seq START WITH 100 INCREMENT by 1;

DROP TABLE acl_class CASCADE CONSTRAINTS;
CREATE TABLE acl_class (
  id 	NUMBER(38) 	NOT NULL, 
  class VARCHAR2(100) 	NOT NULL, 
  CONSTRAINT pk_acl_class_idx PRIMARY KEY(id),
  CONSTRAINT uk_acl_class_idx UNIQUE (class));

DROP SEQUENCE acl_object_id_seq;
CREATE SEQUENCE acl_object_id_seq START WITH 100 INCREMENT BY 1; 

DROP TABLE acl_object_identity CASCADE CONSTRAINTS;
CREATE TABLE acl_object_identity (
  id 			NUMBER(38) NOT NULL, 
  object_id_class 	NUMBER(38) NOT NULL, 
  object_id_identity 	NUMBER(38) NOT NULL, 
  parent_object 	NUMBER(38), 
  owner_sid 		NUMBER(38), 
  entries_inheriting 	NUMBER(1)  NOT NULL, 
  CONSTRAINT pk_acl_object_id_idx   PRIMARY KEY (id),
  CONSTRAINT uk_acl_object_id_idx   UNIQUE (object_id_class,object_id_identity), 
  CONSTRAINT fk_acl_obj_id_parent   FOREIGN KEY(parent_object) REFERENCES acl_object_identity(id), 
  CONSTRAINT fk_acl_obj_id_class    FOREIGN KEY(object_id_class) REFERENCES acl_class(id), 
  CONSTRAINT fk_acl_obj_id_ownersid FOREIGN KEY(owner_sid) REFERENCES acl_sid(id) );

DROP SEQUENCE acl_entry_seq;
CREATE SEQUENCE acl_entry_seq START WITH 100 INCREMENT BY 1;

DROP TABLE acl_entry CASCADE CONSTRAINTS;
CREATE TABLE acl_entry ( 
  id 			NUMBER(38) NOT NULL, 
  acl_object_identity 	NUMBER(38) NOT NULL,
  ace_order 		NUMBER(38) NOT NULL,
  sid 			NUMBER(38) NOT NULL, 
  mask 			NUMBER(38) NOT NULL,
  granting 		NUMBER(1)  NOT NULL,
  audit_success 	NUMBER(1)  NOT NULL, 
  audit_failure 	NUMBER(1)  NOT NULL,
  CONSTRAINT pk_acl_entry_idx   PRIMARY KEY (id),
  CONSTRAINT uk_acl_entry_idx   UNIQUE(acl_object_identity,ace_order), 
  CONSTRAINT fk_acl_entry_objid FOREIGN KEY(acl_object_identity) REFERENCES acl_object_identity(id), 
  CONSTRAINT fk_acl_entry_sid   FOREIGN KEY(sid) REFERENCES acl_sid(id) );

DROP TRIGGER set_group_id;

CREATE OR REPLACE TRIGGER set_group_id
BEFORE INSERT
ON groups
FOR EACH ROW
BEGIN
  SELECT group_seq.NEXTVAL
  INTO :NEW.id
  FROM DUAL;
END;
/

DROP TRIGGER set_group_members_id;

CREATE OR REPLACE TRIGGER set_group_members_id
BEFORE INSERT
ON group_members
FOR EACH ROW
BEGIN
  SELECT group_members_seq.NEXTVAL
  INTO :NEW.id
  FROM DUAL;
END;
/

DROP TRIGGER set_acl_sid_id;

CREATE OR REPLACE TRIGGER set_acl_sid_id
BEFORE INSERT
ON acl_sid
FOR EACH ROW
BEGIN
  SELECT acl_sid_seq.NEXTVAL
  INTO :NEW.id
  FROM DUAL;
END;
/


DROP TRIGGER set_acl_class_id;

CREATE OR REPLACE TRIGGER set_acl_class_id
BEFORE INSERT
ON acl_class
FOR EACH ROW
BEGIN
  SELECT acl_class_seq.NEXTVAL
  INTO :NEW.id
  FROM DUAL;
END;
/

DROP TRIGGER set_acl_object_id;

CREATE OR REPLACE TRIGGER set_acl_object_id
BEFORE INSERT
ON acl_object_identity
FOR EACH ROW
BEGIN
  SELECT acl_object_id_seq.NEXTVAL
  INTO :NEW.id
  FROM DUAL;
END;
/

DROP TRIGGER set_acl_entry_id;

CREATE OR REPLACE TRIGGER set_acl_entry_id
BEFORE INSERT
ON acl_entry
FOR EACH ROW
BEGIN
  SELECT acl_entry_seq.NEXTVAL
  INTO :NEW.id
  FROM DUAL;
END;
/
purge recyclebin;
