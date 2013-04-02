ALTER TABLE uuid_hierarchy ADD (is_shipped NUMBER(1), shipped_date date);

MERGE INTO uuid_hierarchy u
USING
(select uuid,shipped_date from shipped_biospecimen) v
ON (u.uuid = v.uuid)
WHEN MATCHED THEN UPDATE SET
   u.shipped_date = v.shipped_date,
   u.is_shipped = DECODE(v.shipped_date,null,0,1);
COMMIT;

DROP TABLE control_type;
CREATE TABLE control_type (
	control_type_id		INTEGER		NOT NULL,
	control_type		VARCHAR2(20)	NOT NULL,
	xml_name		VARCHAR2(50)    NOT NULL,
	CONSTRAINT pk_control_type_idx PRIMARY KEY ( control_Type_id)
);

INSERT INTO control_type values (1,'Cell Line','cell_line_control');
INSERT INTO control_type values (2,'Normal Normal','normal_normal_control');
INSERT INTO control_type values (3,'Paired Normal','paired_normal_control');
commit;

DROP TABLE control CASCADE CONSTRAINTS;
CREATE TABLE control (
	control_id	NUMBER(38)	NOT NULL,
	control_type_id	INTEGER		NOT NULL,
	CONSTRAINT pk_control_idx PRIMARY KEY (control_id)
);


ALTER TABLE control ADD (
	CONSTRAINT fk_control_control_id FOREIGN KEY (control_id) REFERENCES shipped_biospecimen(shipped_biospecimen_id),
	CONSTRAINT fk_control_controltype FOREIGN KEY (control_type_id) REFERENCES control_type(control_Type_id)
);

MERGE INTO control c
USING
(select shipped_biospecimen_id FROM shipped_biospecimen WHERE tss_code in ('07','AV')) v
ON (c.control_id = v.shipped_biospecimen_id)
WHEN NOT MATCHED THEN INSERT VALUES 
(v.shipped_biospecimen_id,1);
commit;

CREATE INDEX control_control_type_idx on control(control_type_id);
DROP TABLE control_to_disease;
CREATE TABLE control_to_disease (
	control_id	NUMBER(38) 	NOT NULL,
	disease_id	NUMBER(38)	NOT NULL
);

ALTER TABLE control_to_disease ADD (
	CONSTRAINT fk_control_control 
	FOREIGN KEY (control_id) REFERENCES control (control_id),
	CONSTRAINT fk_control_disease 
	FOREIGN KEY (disease_id) REFERENCES disease(disease_id)
);

MERGE INTO control_to_disease c
USING
(select distinct s.shipped_biospecimen_id,a.disease_id from 
 shipped_biospecimen s, shipped_biospec_bcr_archive sba, archive_info a
 where s.tss_code in ('07', 'AV')
 and s.shipped_biospecimen_id = sba.shipped_biospecimen_id
 and sba.archive_id = a.archive_id
 group by s.shipped_biospecimen_id,  a.disease_id) v
 ON
 (c.control_id=v.shipped_biospecimen_id)
 WHEN NOT MATCHED THEN INSERT VALUES
 (v.shipped_biospecimen_id,v.disease_id);
 commit;
 
ALTER TABLE shipped_biospecimen ADD (is_control NUMBER(1) DEFAULT 0);
 
UPDATE shipped_biospecimen SET is_control=1 WHERE tss_code IN ('AV','07');
commit;

CREATE OR REPLACE VIEW shipped_biospecimen_breakdown AS
   SELECT   b.shipped_biospecimen_id,
            b.built_barcode,
            b.project_code|| '-'|| b.tss_code|| '-'|| b.participant_code|| '-'|| be1.element_value AS sample,
            b.project_code || '-' || b.tss_code || '-' || b.participant_code AS participant,
            b.project_code,
            b.tss_code,
            b.participant_code,
            be1.element_value as sample_type_code,
            be2.element_value as sample_sequence,
            b.bcr_center_id,
            b.is_redacted,
            b.is_viewable,
            b.shipped_date,
            b.shipped_item_type_id,
            b.is_control
     FROM   shipped_biospecimen b, shipped_biospecimen_element be1, shipped_biospecimen_element be2
     WHERE  (b.shipped_biospecimen_id = be1.shipped_biospecimen_id and be1.element_type_id = 1)
     AND    (b.shipped_biospecimen_id = be2.shipped_biospecimen_id and be2.element_type_id = 2);
     
ALTER TABLE batch_number_assignment DROP PRIMARY KEY;
DROP INDEX pk_batch_number_assignment_idx;

ALTER TABLE batch_number_assignment ADD CONSTRAINT pk_batch_number_assignment_idx PRIMARY KEY  (batch_id,center_id) ; 
INSERT INTO disease (disease_id,disease_name,disease_abbreviation,active)VALUES(31, 'Controls','CNTL',1);
COMMIT;
INSERT INTO tss_to_disease (tss_disease_id,tss_code,disease_id) VALUES (tss_disease_seq.NEXTVAL,'AV',31);
INSERT INTO tss_to_disease (tss_disease_id,tss_code,disease_id) VALUES (tss_disease_seq.NEXTVAL,'07',31);
COMMIT;
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) VALUES (0,31,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) VALUES (0,31,11);
COMMIT;

ALTER TABLE data_type ADD (require_compression NUMBER(1) DEFAULT 1 NOT NULL);
UPDATE data_type SET require_compression =0 WHERE name = 'Tissue Slide Images';
COMMIT;

DROP TABLE pending_uuid;
CREATE TABLE pending_uuid (
   bcr 			VARCHAR2(10)	NOT NULL,
   center		VARCHAR2(10),
   ship_date		DATE,
   plate_id		VARCHAR2(10),   
   batch_number  	INTEGER,
   plate_coordinate	VARCHAR2(10),
   uuid			VARCHAR2(36)	NOT NULL,
   barcode		VARCHAR2(50),
   sample_type		VARCHAR2(10),
   analyte_type		VARCHAR2(10),
   portion_number	VARCHAR2(10),
   vial_number		VARCHAR2(10),
   item_type		VARCHAR2(20),
   dcc_received_date	DATE,
   created_date		DATE DEFAULT sysdate	NOT NULL,
   CONSTRAINT pk_pending_uuid_idx PRIMARY KEY (uuid)
);



DROP SEQUENCE data_freeze_seq ;
CREATE SEQUENCE data_freeze_seq START WITH 1 INCREMENT BY 1;
DROP TABLE publication CASCADE CONSTRAINTS;
CREATE TABLE publication (
    publication_id    	NUMBER(38)    	NOT NULL,
    publication_name    VARCHAR2(4000)	NOT NULL,
    publication_date    DATE        ,
    CONSTRAINT pk_publication_idx PRIMARY KEY (publication_id)
);
DROP TABLE data_freeze CASCADE CONSTRAINTS;
CREATE TABLE data_freeze (
    data_freeze_id    	NUMBER(38)	NOT NULL,
    data_freeze_date    DATE        	NOT NULL,
    data_freeze_version	VARCHAR2(1000),
    CONSTRAINT pk_data_freeze_idx PRIMARY KEY (data_freeze_id)
);

DROP TABLE publication_data_freeze;
CREATE TABLE publication_data_freeze (
    publication_id    NUMBER(38)    NOT NULL,
    data_freeze_id    NUMBER(38)    NOT NULL);
    

DROP TABLE data_freeze_disease;
CREATE TABLE data_freeze_disease (
    data_freeze_id    NUMBER(38)    NOT NULL,
    disease_id        NUMBER(38)    NOT NULL
);

CREATE UNIQUE INDEX data_freeze_disease_idx ON data_freeze_disease (data_freeze_id,disease_id);

DROP TABLE data_freeze_archive;
CREATE TABLE data_freeze_archive (
    data_freeze_id    NUMBER(38)    NOT NULL,
    archive_id        NUMBER(38)    NOT NULL
);

CREATE UNIQUE INDEX data_freeze_archive_idx ON data_freeze_archive (data_freeze_id,archive_id);
    
ALTER TABLE publication_data_freeze ADD (
   CONSTRAINT fk_pub_data_frz_pub
   FOREIGN KEY (publication_id)
   REFERENCES publication (publication_id),
   CONSTRAINT fk_pub_data_frz_datafrz
   FOREIGN KEY (data_freeze_id)
   REFERENCES data_freeze(data_freeze_id)
);

ALTER TABLE data_freeze_archive ADD (
   CONSTRAINT fk_data_frz_arch_datafrz
   FOREIGN KEY (data_freeze_id)
   REFERENCES data_freeze (data_freeze_id),
   CONSTRAINT fk_data_frz_arch_arch
   FOREIGN KEY (archive_id)
   REFERENCES archive_info (archive_id)
);

ALTER TABLE data_freeze_disease ADD (
   CONSTRAINT fk_data_frz_dis_datafrz
   FOREIGN KEY (data_freeze_id)
   REFERENCES data_freeze (data_freeze_id),
   CONSTRAINT fk_data_frz_dis_disease
   FOREIGN KEY (disease_id)
   REFERENCES disease (disease_id)
);

-- grants apply only on stage and prod
grant all on pending_uuid to commonmaint;
grant select on pending_uuid to commonread;
grant all on control_type to commonmaint;
grant select on control_type to commonread;
grant all on control to commonmaint;
grant select on control to commonread;
grant all on control_to_disease to commonmaint;
grant select on control_to_disease to commonread;
grant select on publication to commonread;
grant all on publication to commonmaint;
grant select on publication_data_freeze to commonread;
grant all on publication_data_freeze to commonmaint;
grant select on data_freeze to commonread;
grant all on data_freeze to commonmaint;
grant select on data_freeze_archive to commonread;
grant all on data_freeze_archive to commonmaint;
grant select on data_freeze_disease to commonread;
grant all on data_freeze_disease to commonmaint;
grant select on data_freeze_seq to commonmaint;

-- apps-5696 Firehose platforms and data types
INSERT INTO data_type (data_type_id, name, center_type_code,ftp_display,available,sort_order,require_compression)
VALUES (35,'analyses','GDAC','analyses',1,51,0);
INSERT INTO data_type (data_type_id, name, center_type_code,ftp_display,available,sort_order,require_compression)
VALUES (36,'stddata','GDAC','stddata',1,52,0);
INSERT INTO data_type (data_type_id, name, center_type_code,ftp_display,available,sort_order,require_compression)
VALUES (37,'Firehose Reports','GDAC','reports',1,53,0);

INSERT INTO data_visibility (data_visibility_id,data_type_id,visibility_id,level_number)
values (data_visibility_seq.nextval,35,1,4);
INSERT INTO data_visibility (data_visibility_id,data_type_id,visibility_id,level_number)
values (data_visibility_seq.nextval,36,1,4);
INSERT INTO data_visibility (data_visibility_id,data_type_id,visibility_id,level_number)
values (data_visibility_seq.nextval,37,1,4);


INSERT INTO platform 
(platform_id, platform_name, platform_display_name, platform_alias,center_type_code,sort_order,available,base_data_type_id)
VALUES
(54,'fh_analyses','Firehose Analyses','fh_analyses','GDAC',54,1,35);
INSERT INTO platform 
(platform_id, platform_name, platform_display_name, platform_alias,center_type_code,sort_order,available,base_data_type_id)
VALUES
(55,'fh_stddata','Firehose Standardized Data','fh_stddata','GDAC',55,1,36);
INSERT INTO platform 
(platform_id, platform_name, platform_display_name, platform_alias,center_type_code,sort_order,available,base_data_type_id)
VALUES
(56,'fh_reports','Firehose Reports','fh_reports','GDAC',56,1,37);

INSERT INTO data_type_to_platform(data_type_platform_id,data_type_id, platform_id)
VALUES (data_type_platform_seq.nextval,35,54);
INSERT INTO data_type_to_platform(data_type_platform_id,data_type_id, platform_id)
VALUES (data_type_platform_seq.nextval,36,55);
INSERT INTO data_type_to_platform(data_type_platform_id,data_type_id, platform_id)
VALUES (data_type_platform_seq.nextval,37,56);

commit;












