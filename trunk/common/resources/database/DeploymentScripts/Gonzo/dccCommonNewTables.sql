-- tables, sequences and triggers required by security app
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

-- Tables , sequences needed by Annotations

DROP SEQUENCE annotation_category_type_seq;
CREATE SEQUENCE annotation_category_type_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE annotation_item_category_seq;
CREATE SEQUENCE annotation_item_category_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE item_type_seq;
CREATE SEQUENCE item_type_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE annotation_item_seq;
CREATE SEQUENCE annotation_item_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE annotation_seq;
CREATE SEQUENCE annotation_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE annotation_note_seq;
CREATE SEQUENCE annotation_note_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE annotation_disease_seq;
CREATE SEQUENCE annotation_disease_seq START WITH 1 INCREMENT BY 1;

DROP  TABLE annotation_item_type CASCADE CONSTRAINTS;
CREATE TABLE annotation_item_type
(
  item_type_id               NUMBER(38)     NOT NULL,
  type_display_name          VARCHAR2(200)  NOT NULL,
  type_description           VARCHAR2(2000) NOT NULL,
  CONSTRAINT annotation_type_pk_idx PRIMARY KEY (item_type_id)
);

DROP  TABLE annotation_category CASCADE CONSTRAINTS;
CREATE TABLE annotation_category
(
  annotation_category_id    NUMBER(38)     NOT NULL,
  category_display_name     VARCHAR2(200)  NOT NULL,
  category_description      VARCHAR2(2000) NOT NULL,
  caDSR_description         VARCHAR2(2000) ,
  CONSTRAINT annotation_category_pk_idx PRIMARY KEY (annotation_category_id)
);

DROP  TABLE annotation_category_item_type CASCADE CONSTRAINTS;
CREATE TABLE annotation_category_item_type 
(
  annotation_category_type_id  NUMBER(38)     NOT NULL,
  annotation_category_id       NUMBER(38)     NOT NULL,
  item_type_id                 NUMBER(38)     NOT NULL,
  CONSTRAINT annot_type_cat_pk_idx PRIMARY KEY (annotation_category_type_id)
);
ALTER TABLE annotation_category_item_type ADD (
  CONSTRAINT fk_annot_itemtype_cat_id 
  FOREIGN KEY (annotation_category_id) 
  REFERENCES annotation_category(annotation_category_id),
  CONSTRAINT fk_annot_itemtype_type_id
  FOREIGN KEY (item_type_id) 
  REFERENCES annotation_item_type(item_type_id)
);


DROP  TABLE annotation CASCADE CONSTRAINTS;
CREATE TABLE annotation
(
  annotation_id           NUMBER(38)     NOT NULL,
  annotation_category_id  NUMBER(38)     NOT NULL,
  entered_by              VARCHAR2(200)  NOT NULL,
  entered_date            DATE           NOT NULL,
  modified_by             VARCHAR2(200) ,
  modified_date		  DATE,
  CONSTRAINT annotation_pk_idx PRIMARY KEY (annotation_id)
);
ALTER TABLE annotation ADD (
  CONSTRAINT fk_annotation_category_id 
  FOREIGN KEY (annotation_category_id) 
  REFERENCES annotation_category(annotation_category_id)
);
  

DROP  TABLE annotation_item CASCADE CONSTRAINTS;
CREATE TABLE annotation_item
(
  annotation_item_id      NUMBER(38)     NOT NULL,
  annotation_id           NUMBER(38)     NOT NULL,
  item_type_id            NUMBER(38)     NOT NULL,
  annotation_item         VARCHAR2(50)   NOT NULL,
  CONSTRAINT annotation_item_pk_idx PRIMARY KEY (annotation_item_id)
);


ALTER TABLE annotation_item ADD (
  CONSTRAINT fk_annotitem_annotation_id 
  FOREIGN KEY (annotation_id) 
  REFERENCES annotation(annotation_id),
  CONSTRAINT fk_annotitem_type_id 
  FOREIGN KEY (item_type_id) 
  REFERENCES annotation_item_type(item_type_id)
);
  
DROP  TABLE annotation_note CASCADE CONSTRAINTS;
CREATE TABLE annotation_note
(
  annotation_note_id      NUMBER(38)     NOT NULL,
  annotation_id           NUMBER(38)     NOT NULL,
  note                    VARCHAR2(4000) NOT NULL,
  entered_by              VARCHAR2(200)  NOT NULL,
  entered_date            DATE           NOT NULL,  
  modified_by		  VARCHAR2(200),
  modified_date           DATE,
  CONSTRAINT annotation_note_pk_idx PRIMARY KEY (annotation_note_id)
);
ALTER TABLE annotation_note ADD (
  CONSTRAINT fk_annotnote_annotation_id 
  FOREIGN KEY (annotation_id) 
  REFERENCES annotation(annotation_id)
);

-- Tables and sequences needed by UUID app

DROP SEQUENCE gen_method_seq;
CREATE SEQUENCE gen_method_seq START WITH 10 INCREMENT BY 1;
DROP SEQUENCE uuid_seq;
CREATE SEQUENCE uuid_seq START WITH 10 INCREMENT BY 1;
DROP SEQUENCE barcode_seq;
CREATE SEQUENCE barcode_seq START WITH 10 INCREMENT BY 1;


DROP TABLE uuid CASCADE CONSTRAINTS;
CREATE TABLE uuid (
    uuid                 VARCHAR2(36) NOT NULL,
    center_id            NUMBER(38)   NOT NULL,
    generation_method_id NUMBER(38)   NOT NULL,
    create_date          DATE         NOT NULL,
    created_by           VARCHAR2(30) NOT NULL,
    latest_barcode_id    NUMBER(38) ,
    CONSTRAINT uuid_pk_idx PRIMARY KEY (uuid)
);

DROP TABLE generation_method  CASCADE CONSTRAINTS;
CREATE TABLE generation_method (
    generation_method_id     NUMBER(38)   NOT NULL,
    generation_method        VARCHAR2(20) NOT NULL,
    CONSTRAINT generation_method_pk_idx PRIMARY KEY (generation_method_id)
);

DROP TABLE barcode_history  CASCADE CONSTRAINTS;
CREATE TABLE barcode_history(
    barcode_id        NUMBER(38)   NOT NULL,
    barcode           VARCHAR2(50) NOT NULL,
    uuid              VARCHAR2(36) NOT NULL,
    disease_id        NUMBER(38)   NOT NULL,
    effective_date    DATE         NOT NULL,
    CONSTRAINT barcode_pk_idx PRIMARY KEY (barcode_id)
);

ALTER TABLE uuid ADD (
    CONSTRAINT fk_uuid_center_id 
    FOREIGN KEY (center_id)
    REFERENCES center(center_id),
    CONSTRAINT fk_uuid_gen_method_id 
    FOREIGN KEY (generation_method_id)
    REFERENCES generation_method(generation_method_id)
);

ALTER TABLE barcode_history ADD (
    CONSTRAINT fk_barcode_uuid
    FOREIGN KEY (uuid)
    REFERENCES uuid(uuid),
    CONSTRAINT fk_barcode_disease_id 
    FOREIGN KEY (disease_id)
    REFERENCES disease(disease_id)
);

grant select on uuid to public;
grant select on barcode_history to public;
grant select on generation_method to public;
GRANT ALL ON ANNOTATION_ITEM_TYPE TO commonmaint;
GRANT ALL ON ACL_ENTRY TO commonmaint;
GRANT ALL ON GENERATION_METHOD TO commonmaint;
GRANT ALL ON BARCODE_HISTORY TO commonmaint;
GRANT ALL ON ANNOTATION_OLD TO commonmaint;
GRANT ALL ON ANNOTATION_CATEGORY TO commonmaint;
GRANT ALL ON ANNOTATION_CATEGORY_ITEM_TYPE TO commonmaint;
GRANT ALL ON ANNOTATION TO commonmaint;
GRANT ALL ON ANNOTATION_ITEM TO commonmaint;
GRANT ALL ON ANNOTATION_NOTE TO commonmaint;
GRANT ALL ON UUID TO commonmaint;
GRANT ALL ON USERS TO commonmaint;
GRANT ALL ON AUTHORITIES TO commonmaint;
GRANT ALL ON GROUPS TO commonmaint;
GRANT ALL ON GROUP_AUTHORITIES TO commonmaint;
GRANT ALL ON GROUP_MEMBERS TO commonmaint;
GRANT ALL ON PERSISTENT_LOGINS TO commonmaint;
GRANT ALL ON ACL_SID TO commonmaint;
GRANT ALL ON ACL_CLASS TO commonmaint;
GRANT ALL ON ACL_OBJECT_IDENTITY TO commonmaint;
grant select on ACL_CLASS_SEQ to commonmaint;
grant select on ACL_ENTRY_SEQ to commonmaint;
grant select on ACL_OBJECT_ID_SEQ to commonmaint;
grant select on ACL_SID_SEQ to commonmaint;
grant select on ANNOTATION_CATEGORY_TYPE_SEQ to commonmaint;
grant select on ANNOTATION_DISEASE_SEQ to commonmaint;
grant select on ANNOTATION_ITEM_CATEGORY_SEQ to commonmaint;
grant select on ANNOTATION_ITEM_SEQ to commonmaint;
grant select on ANNOTATION_NOTE_SEQ to commonmaint;
grant select on ANNOTATION_SEQ to commonmaint;
grant select on ARCHIVE_SEQ to commonmaint;
grant select on BARCODE_SEQ to commonmaint;
grant select on BIOSPECIMEN_ARCHIVE_SEQ to commonmaint;
grant select on BIOSPECIMEN_BARCODE_SEQ to commonmaint;
grant select on BIOSPECIMEN_DISEASE_SEQ to commonmaint;
grant select on BIOSPECIMEN_FILE_SEQ to commonmaint;
grant select on BIOSPECIMEN_TRACE_SEQ to commonmaint;
grant select on CENTER_EMAIL_SEQ to commonmaint;
grant select on CENTER_SEQ to commonmaint;
grant select on DATA_TYPE_ID_SEQ to commonmaint;
grant select on DATA_TYPE_PLATFORM_SEQ to commonmaint;
grant select on DATA_VISIBILITY_SEQ to commonmaint;
grant select on DISEASE_SEQ to commonmaint;
grant select on FILE_ARCHIVE_SEQ to commonmaint;
grant select on FILE_SEQ to commonmaint;
grant select on GEN_METHOD_SEQ to commonmaint;
grant select on GROUP_MEMBERS_SEQ to commonmaint;
grant select on GROUP_SEQ to commonmaint;
grant select on HUGO_GENE_SEQ to commonmaint;
grant select on ITEM_TYPE_SEQ to commonmaint;
grant select on NCBI_TRACE_SEQ to commonmaint;
grant select on ORPHAN_SEQ to commonmaint;
grant select on PI_INFO_SEQ to commonmaint;
grant select on PLATFORM_SEQ to commonmaint;
grant select on PROCESS_LOG_SEQ to commonmaint;
grant select on REPORT_SEQ to commonmaint;
grant select on TISSUE_DISEASE_SEQ to commonmaint;
grant select on TISSUE_SEQ to commonmaint;
grant select on TRACE_INFO_SEQ to commonmaint;
grant select on TSS_DISEASE_SEQ to commonmaint;
grant select on UUID_SEQ to commonmaint;
grant select on VISIBILITY_SEQ to commonmaint;

INSERT INTO generation_method (generation_method_id,generation_method)VALUES(1,'Web');                                                                          
                                                                                
INSERT INTO generation_method (generation_method_id,generation_method)VALUES(2,'REST');                                                                         
                                                                                
INSERT INTO generation_method (generation_method_id,generation_method)VALUES(3,'Upload');                                                                       
                                                                                
INSERT INTO generation_method (generation_method_id,generation_method)VALUES(4,'API'); 
commit;