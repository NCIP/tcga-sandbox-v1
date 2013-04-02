INSERT INTO group_authorities (group_id,authority)
(select id,'ROLE_ANNOTATION_EDITOR' FROM groups WHERE group_name='AnnotationsUsers');
commit;
DELETE FROM group_authorities where authority='ANNOTATION_ITEM_CREATOR';
DELETE FROM group_authorities where authority='ANNOTATION_NOTE_CREATOR';
DELETE FROM group_authorities where authority='ANNOTATION_NOTE_EDITOR';
commit;

ALTER TABLE archive_info ADD (secondary_deploy_location VARCHAR2(2000));
DROP TABLE uuid_hierarchy CASCADE CONSTRAINTS;
CREATE TABLE uuid_hierarchy (
    disease_abbreviation        VARCHAR2(10)	NOT NULL,
    uuid                    	VARCHAR2(36)    NOT NULL,
    parent_uuid                	VARCHAR2(36),
    item_type_id               	INTEGER 	NOT NULL,
    tss_code                	VARCHAR2(10)    NOT NULL,
    center_id_bcr		NUMBER(38,0)    NOT NULL,
    batch_number		INTEGER		NOT NULL,
    barcode            		VARCHAR2(50),
    participant_number        	VARCHAR2(20)    NOT NULL,
    sample_type_code        	VARCHAR2(20),
    sample_sequence        	VARCHAR2(10),
    portion_sequence        	VARCHAR2(10),
    portion_analyte_code    	VARCHAR2(10),
    plate_id            	VARCHAR2(10),
    receiving_center_id        	NUMBER(38,0),
    slide            		VARCHAR2(10),
    slide_layer			VARCHAR2(7),
    create_date			DATE		NOT NULL,
    update_date            	DATE , 
    platforms			VARCHAR2(200),
    CONSTRAINT pk_uuid_hierarchy_idx PRIMARY KEY (uuid)
);
ALTER TABLE uuid_hierarchy ADD (
CONSTRAINT fk_uuid_hierarchy_parent
FOREIGN KEY (parent_uuid)
REFERENCES uuid_hierarchy(uuid));

DROP INDEX uuidhier_prntuuid_idx;
CREATE INDEX uuidhier_prntuuid_idx on uuid_hierarchy(parent_uuid);

DROP INDEX uuidhier_barcode_idx;
CREATE UNIQUE INDEX uuidhier_barcode_idx ON uuid_hierarchy(barcode);


DROP TABLE uuid_platform CASCADE CONSTRAINTS;
CREATE TABLE uuid_platform (
	uuid		VARCHAR2(36)	NOT NULL,
	platform_id	NUMBER(38,0)	NOT NULL,
	CONSTRAINT pk_uuid_platform_idx PRIMARY KEY (uuid,platform_id)
);

DROP TABLE annotation_classification;
CREATE TABLE annotation_classification (
    annotation_classification_id	NUMBER(38,0)	NOT NULL,
    classification_display_name		VARCHAR2(200)	NOT NULL,
    classification_description		VARCHAR2(2000),
    CONSTRAINT pk_annotation_class_idx PRIMARY KEY (annotation_classification_id)
);

ALTER TABLE annotation_category ADD (annotation_classification_id  NUMBER(38,0));
ALTER TABLE annotation_category ADD (
   CONSTRAINT fk_annotation_category_class
   FOREIGN KEY (annotation_classification_id)
   REFERENCES annotation_classification(annotation_classification_id)
);

insert into annotation_classification values(1,'Observation','Observation');
insert into annotation_classification values(2,'CenterNotification','CenterNotification');
insert into annotation_classification values(3,'Notification','Notification');
insert into annotation_classification values(4,'Rescission','Rescission');
insert into annotation_classification values(5,'Redaction','Redaction');
commit;

MERGE INTO annotation_category cat 
USING (select distinct cl.annotation_classification_id,cl.classification_display_name,ca.annotation_category_id
      from annotation_classification cl,
      (select annotation_category_id, substr(category_display_name,1,instr(category_display_name,':')-1) as class
       from annotation_category) ca
       where cl.classification_display_name = ca.class ) v
ON (cat.annotation_category_id = v.annotation_category_id)
WHEN MATCHED THEN UPDATE set annotation_classification_id=v.annotation_classification_id; 
commit;

ALTER TABLE annotation_category MODIFY annotation_classification_id NOT NULL;

update annotation_category set category_display_name = substr(category_display_name,instr(category_display_name,':')+1,100);
commit;

CREATE OR REPLACE FORCE VIEW patient_v AS
SELECT distinct 'BLCA' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id,  max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgablca.patient gp, tcgablca.patient_archive pa, tcgablca.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'BRCA' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgabrca.patient gp, tcgabrca.patient_archive pa, tcgabrca.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'CESC' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgacesc.patient gp, tcgacesc.patient_archive pa, tcgacesc.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'COAD' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgacoad.patient gp, tcgacoad.patient_archive pa, tcgacoad.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'DLBC' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgadlbc.patient gp, tcgadlbc.patient_archive pa, tcgadlbc.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'GBM' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgagbm.patient gp, tcgagbm.patient_archive pa, tcgagbm.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'HNSC' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgahnsc.patient gp, tcgahnsc.patient_archive pa, tcgahnsc.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'KIRC' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgakirc.patient gp, tcgakirc.patient_archive pa, tcgakirc.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'KIRP' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgakirp.patient gp, tcgakirp.patient_archive pa, tcgakirp.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LAML' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgalaml.patient gp, tcgalaml.patient_archive pa, tcgalaml.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LCLL' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgalcll.patient gp, tcgalcll.patient_archive pa, tcgalcll.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LGG' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgalgg.patient gp, tcgalgg.patient_archive pa, tcgalgg.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LIHC' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgalihc.patient gp, tcgalihc.patient_archive pa, tcgalihc.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LNNH' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgalnnh.patient gp, tcgalnnh.patient_archive pa, tcgalnnh.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LUAD' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgaluad.patient gp, tcgaluad.patient_archive pa, tcgaluad.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LUSC' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgalusc.patient gp, tcgalusc.patient_archive pa, tcgalusc.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'OV' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgaov.patient gp, tcgaov.patient_archive pa, tcgaov.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'PAAD' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgapaad.patient gp, tcgapaad.patient_archive pa, tcgapaad.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'PRAD' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgaprad.patient gp, tcgaprad.patient_archive pa, tcgaprad.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'READ' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgaread.patient gp, tcgaread.patient_archive pa, tcgaread.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'SALD' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgasald.patient gp, tcgasald.patient_archive pa, tcgasald.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'SKCM' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgaskcm.patient gp, tcgaskcm.patient_archive pa, tcgaskcm.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'STAD' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgastad.patient gp, tcgastad.patient_archive pa, tcgastad.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'THCA' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgathca.patient gp, tcgathca.patient_archive pa, tcgathca.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'UCEC' as disease_abbreviation,gp.uuid,gp.patient_barcode as barcode, substr(gp.patient_barcode,-4,4) as participant_number,substr(gp.patient_barcode,6,2) as tss_code,gp.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgaucec.patient gp, tcgaucec.patient_archive pa, tcgaucec.archive_info ai
WHERE gp.patient_id=pa.patient_id
  AND   pa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gp.uuid,gp.patient_barcode, substr(gp.patient_barcode,-4,4),substr(gp.patient_barcode,6,2),gp.patient_id, ai.serial_index, ai.center_id;

CREATE OR REPLACE FORCE VIEW sample_v AS
SELECT distinct 'BLCA' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgablca.sample gs, tcgablca.sample_archive sa, tcgablca.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'BRCA' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgabrca.sample gs, tcgabrca.sample_archive sa, tcgabrca.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'CESC' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgacesc.sample gs, tcgacesc.sample_archive sa, tcgacesc.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'COAD' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgacoad.sample gs, tcgacoad.sample_archive sa, tcgacoad.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'DLBC' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgadlbc.sample gs, tcgadlbc.sample_archive sa, tcgadlbc.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'GBM' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgagbm.sample gs, tcgagbm.sample_archive sa, tcgagbm.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'HNSC' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgahnsc.sample gs, tcgahnsc.sample_archive sa, tcgahnsc.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'KIRC' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgakirc.sample gs, tcgakirc.sample_archive sa, tcgakirc.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'KIRP' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgakirp.sample gs, tcgakirp.sample_archive sa, tcgakirp.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'LAML' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgalaml.sample gs, tcgalaml.sample_archive sa, tcgalaml.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'LCLL' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgalcll.sample gs, tcgalcll.sample_archive sa, tcgalcll.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'LGG' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgalgg.sample gs, tcgalgg.sample_archive sa, tcgalgg.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'LIHC' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgalihc.sample gs, tcgalihc.sample_archive sa, tcgalihc.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'LNNH' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgalnnh.sample gs, tcgalnnh.sample_archive sa, tcgalnnh.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'LUAD' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgaluad.sample gs, tcgaluad.sample_archive sa, tcgaluad.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'LUSC' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgalusc.sample gs, tcgalusc.sample_archive sa, tcgalusc.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'OV' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgaov.sample gs, tcgaov.sample_archive sa, tcgaov.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'PAAD' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgapaad.sample gs, tcgapaad.sample_archive sa, tcgapaad.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'PRAD' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgaprad.sample gs, tcgaprad.sample_archive sa, tcgaprad.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'READ' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgaread.sample gs, tcgaread.sample_archive sa, tcgaread.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'SALD' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgasald.sample gs, tcgasald.sample_archive sa, tcgasald.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'SKCM' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgaskcm.sample gs, tcgaskcm.sample_archive sa, tcgaskcm.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'STAD' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgastad.sample gs, tcgastad.sample_archive sa, tcgastad.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'THCA' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgathca.sample gs, tcgathca.sample_archive sa, tcgathca.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id
UNION
SELECT distinct 'UCEC' as disease_abbreviation,gs.uuid,gs.sample_barcode as barcode, gs.sample_id, gs.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id,substr(gs.sample_barcode,-3,2) as sample_type, substr(gs.sample_barcode,-1) as sample_sequence
FROM tcgaucec.sample gs, tcgaucec.sample_archive sa, tcgaucec.archive_info ai
WHERE gs.sample_id=sa.sample_id
  AND   sa.archive_id = ai.archive_id
  AND   ai.is_latest = 1
GROUP BY gs.uuid,gs.sample_barcode, substr(gs.sample_barcode,-4,4),substr(gs.sample_barcode,6,2),gs.sample_id,gs.patient_id,substr(gs.sample_barcode,-3,2), substr(gs.sample_barcode,-1) ,substr(gs.sample_barcode,-3,2) ,substr(gs.sample_barcode,-1), ai.serial_index, ai.center_id;



CREATE OR REPLACE FORCE VIEW portion_v AS
SELECT DISTINCT 'BLCA' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgablca.portion gp, tcgablca.portion_archive pa, tcgablca.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'BRCA' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgabrca.portion gp, tcgabrca.portion_archive pa, tcgabrca.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'CESC' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgacesc.portion gp, tcgacesc.portion_archive pa, tcgacesc.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'COAD' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgacoad.portion gp, tcgacoad.portion_archive pa, tcgacoad.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'DLBC' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgadlbc.portion gp, tcgadlbc.portion_archive pa, tcgadlbc.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'GBM' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgagbm.portion gp, tcgagbm.portion_archive pa, tcgagbm.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'HNSC' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgahnsc.portion gp, tcgahnsc.portion_archive pa, tcgahnsc.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'KIRC' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgakirc.portion gp, tcgakirc.portion_archive pa, tcgakirc.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'KIRP' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgakirp.portion gp, tcgakirp.portion_archive pa, tcgakirp.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LAML' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgalaml.portion gp, tcgalaml.portion_archive pa, tcgalaml.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LCLL' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgalcll.portion gp, tcgalcll.portion_archive pa, tcgalcll.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LGG' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgalgg.portion gp, tcgalgg.portion_archive pa, tcgalgg.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LIHC' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgalihc.portion gp, tcgalihc.portion_archive pa, tcgalihc.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LNNH' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgalnnh.portion gp, tcgalnnh.portion_archive pa, tcgalnnh.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LUAD' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgaluad.portion gp, tcgaluad.portion_archive pa, tcgaluad.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LUSC' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgalusc.portion gp, tcgalusc.portion_archive pa, tcgalusc.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'OV' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgaov.portion gp, tcgaov.portion_archive pa, tcgaov.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'PAAD' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgapaad.portion gp, tcgapaad.portion_archive pa, tcgapaad.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'PRAD' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgaprad.portion gp, tcgaprad.portion_archive pa, tcgaprad.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'READ' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgaread.portion gp, tcgaread.portion_archive pa, tcgaread.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'SALD' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgasald.portion gp, tcgasald.portion_archive pa, tcgasald.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'SKCM' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgaskcm.portion gp, tcgaskcm.portion_archive pa, tcgaskcm.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'SALD' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgasald.portion gp, tcgasald.portion_archive pa, tcgasald.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'THCA' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgathca.portion gp, tcgathca.portion_archive pa, tcgathca.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'UCEC' as disease_abbreviation,gp.sample_id,gp.uuid,gp.portion_barcode as barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1) as portion,gp.portion_id,max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM  tcgaucec.portion gp, tcgaucec.portion_archive pa, tcgaucec.archive_info ai
WHERE gp.portion_id = pa.portion_id
  AND pa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gp.sample_id,gp.uuid,gp.portion_barcode, substr(gp.portion_barcode,length(gp.portion_barcode)-1),gp.portion_id, ai.serial_index, ai.center_id;


CREATE OR REPLACE FORCE VIEW analyte_v AS
SELECT DISTINCT 'BLCA' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgablca.analyte gan, tcgablca.analyte_archive aa, tcgablca.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'BRCA' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgabrca.analyte gan, tcgabrca.analyte_archive aa, tcgabrca.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'CESC' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgacesc.analyte gan, tcgacesc.analyte_archive aa, tcgacesc.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'COAD' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgacoad.analyte gan, tcgacoad.analyte_archive aa, tcgacoad.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'DLBC' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgadlbc.analyte gan, tcgadlbc.analyte_archive aa, tcgadlbc.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'GBM' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgagbm.analyte gan, tcgagbm.analyte_archive aa, tcgagbm.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'HNSC' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgahnsc.analyte gan, tcgahnsc.analyte_archive aa, tcgahnsc.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'KIRC' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgakirc.analyte gan, tcgakirc.analyte_archive aa, tcgakirc.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'KIRP' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgakirp.analyte gan, tcgakirp.analyte_archive aa, tcgakirp.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LAML' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgalaml.analyte gan, tcgalaml.analyte_archive aa, tcgalaml.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LCLL' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgalcll.analyte gan, tcgalcll.analyte_archive aa, tcgalcll.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LGG' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgalgg.analyte gan, tcgalgg.analyte_archive aa, tcgalgg.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LIHC' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgalihc.analyte gan, tcgalihc.analyte_archive aa, tcgalihc.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LNNH' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgalnnh.analyte gan, tcgalnnh.analyte_archive aa, tcgalnnh.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LUAD' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgaluad.analyte gan, tcgaluad.analyte_archive aa, tcgaluad.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LUSC' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgalusc.analyte gan, tcgalusc.analyte_archive aa, tcgalusc.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'OV' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgaov.analyte gan, tcgaov.analyte_archive aa, tcgaov.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'PAAD' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgapaad.analyte gan, tcgapaad.analyte_archive aa, tcgapaad.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'PRAD' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgaprad.analyte gan, tcgaprad.analyte_archive aa, tcgaprad.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'READ' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgaread.analyte gan, tcgaread.analyte_archive aa, tcgaread.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'SALD' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgasald.analyte gan, tcgasald.analyte_archive aa, tcgasald.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'SKCM' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgaskcm.analyte gan, tcgaskcm.analyte_archive aa, tcgaskcm.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'STAD' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgastad.analyte gan, tcgastad.analyte_archive aa, tcgastad.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'THCA' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgathca.analyte gan, tcgathca.analyte_archive aa, tcgathca.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'UCEC' as disease_abbreviation,gan.uuid,gan.analyte_barcode as barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)) as analyte_code,gan.analyte_id, gan.portion_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
FROM tcgaucec.analyte gan, tcgaucec.analyte_archive aa, tcgaucec.archive_info ai
WHERE gan.analyte_id=aa.analyte_id
  AND aa.archive_id=ai.archive_id
  AND ai.is_latest=1
GROUP BY gan.uuid,gan.analyte_barcode,substr(gan.analyte_barcode,length(gan.analyte_barcode)),gan.analyte_id,gan.portion_id, ai.serial_index, ai.center_id;


CREATE OR REPLACE FORCE VIEW aliquot_v AS
SELECT DISTINCT 'BLCA' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgablca.aliquot gal, tcgablca.aliquot_archive aa, tcgablca.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'BRCA' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgabrca.aliquot gal, tcgabrca.aliquot_archive aa, tcgabrca.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'CESC' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgacesc.aliquot gal, tcgacesc.aliquot_archive aa, tcgacesc.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'COAD' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgacoad.aliquot gal, tcgacoad.aliquot_archive aa, tcgacoad.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'DLBC' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgadlbc.aliquot gal, tcgadlbc.aliquot_archive aa, tcgadlbc.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'GBM' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgagbm.aliquot gal, tcgagbm.aliquot_archive aa, tcgagbm.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'HNSC' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgahnsc.aliquot gal, tcgahnsc.aliquot_archive aa, tcgahnsc.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'KIRC' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgakirc.aliquot gal, tcgakirc.aliquot_archive aa, tcgakirc.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'KIRP' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgakirp.aliquot gal, tcgakirp.aliquot_archive aa, tcgakirp.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LAML' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalaml.aliquot gal, tcgalaml.aliquot_archive aa, tcgalaml.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LCLL' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalcll.aliquot gal, tcgalcll.aliquot_archive aa, tcgalcll.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LGG' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalgg.aliquot gal, tcgalgg.aliquot_archive aa, tcgalgg.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LIHC' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalihc.aliquot gal, tcgalihc.aliquot_archive aa, tcgalihc.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LNNH' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalnnh.aliquot gal, tcgalnnh.aliquot_archive aa, tcgalnnh.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LUAD' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaluad.aliquot gal, tcgaluad.aliquot_archive aa, tcgaluad.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'LUSC' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalusc.aliquot gal, tcgalusc.aliquot_archive aa, tcgalusc.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'OV' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaov.aliquot gal, tcgaov.aliquot_archive aa, tcgaov.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'PAAD' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgapaad.aliquot gal, tcgapaad.aliquot_archive aa, tcgapaad.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'PRAD' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaprad.aliquot gal, tcgaprad.aliquot_archive aa, tcgaprad.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'READ' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaread.aliquot gal, tcgaread.aliquot_archive aa, tcgaread.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'SALD' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgasald.aliquot gal, tcgasald.aliquot_archive aa, tcgasald.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'SKCM' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaskcm.aliquot gal, tcgaskcm.aliquot_archive aa, tcgaskcm.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'STAD' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgastad.aliquot gal, tcgastad.aliquot_archive aa, tcgastad.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'THCA' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgathca.aliquot gal, tcgathca.aliquot_archive aa, tcgathca.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id
UNION
SELECT DISTINCT 'UCEC' as disease_abbreviation, gal.aliquot_id, gal.analyte_id, gal.uuid,gal.aliquot_barcode as barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1) as bcr_center_id,substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ) as plate, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaucec.aliquot gal, tcgaucec.aliquot_archive aa, tcgaucec.archive_info ai
 WHERE gal.aliquot_id=aa.aliquot_id
    AND aa.archive_id=ai.archive_id
    AND ai.is_latest=1
GROUP BY gal.uuid, gal.aliquot_id, gal.analyte_id, gal.aliquot_barcode,substr(gal.aliquot_barcode,length(gal.aliquot_barcode)-1), substr(gal.aliquot_barcode,instr(gal.aliquot_barcode,'-',1,5)+1,4 ), ai.serial_index, ai.center_id;

CREATE OR REPLACE FORCE VIEW slide_v AS
SELECT distinct 'BLCA' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgablca.slide gsl, tcgablca.slide_archive sa, tcgablca.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'BRCA' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgabrca.slide gsl, tcgabrca.slide_archive sa, tcgabrca.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'CESC' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgacesc.slide gsl, tcgacesc.slide_archive sa, tcgacesc.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'COAD' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgacoad.slide gsl, tcgacoad.slide_archive sa, tcgacoad.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'DLBC' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgadlbc.slide gsl, tcgadlbc.slide_archive sa, tcgadlbc.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'GBM' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgagbm.slide gsl, tcgagbm.slide_archive sa, tcgagbm.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'HNSC' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgahnsc.slide gsl, tcgahnsc.slide_archive sa, tcgahnsc.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'KIRC' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgakirc.slide gsl, tcgakirc.slide_archive sa, tcgakirc.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'KIRP' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgakirp.slide gsl, tcgakirp.slide_archive sa, tcgakirp.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'LAML' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalaml.slide gsl, tcgalaml.slide_archive sa, tcgalaml.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'LCLL' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalcll.slide gsl, tcgalcll.slide_archive sa, tcgalcll.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'LGG' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalgg.slide gsl, tcgalgg.slide_archive sa, tcgalgg.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'LIHC' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalihc.slide gsl, tcgalihc.slide_archive sa, tcgalihc.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'LNNH' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalnnh.slide gsl, tcgalnnh.slide_archive sa, tcgalnnh.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'LUAD' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaluad.slide gsl, tcgaluad.slide_archive sa, tcgaluad.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'LUSC' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalusc.slide gsl, tcgalusc.slide_archive sa, tcgalusc.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'OV' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaov.slide gsl, tcgaov.slide_archive sa, tcgaov.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'PAAD' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgapaad.slide gsl, tcgapaad.slide_archive sa, tcgapaad.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'PRAD' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaprad.slide gsl, tcgaprad.slide_archive sa, tcgaprad.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'READ' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaread.slide gsl, tcgaread.slide_archive sa, tcgaread.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'SALD' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgasald.slide gsl, tcgasald.slide_archive sa, tcgasald.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'SKCM' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaskcm.slide gsl, tcgaskcm.slide_archive sa, tcgaskcm.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'STAD' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgastad.slide gsl, tcgastad.slide_archive sa, tcgastad.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'THCA' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgathca.slide gsl, tcgathca.slide_archive sa, tcgathca.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id
UNION
SELECT distinct 'UCEC' as disease_abbreviation, gsl.uuid,gsl.slide_barcode as barcode,gsl.portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2) as slide, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaucec.slide gsl, tcgaucec.slide_archive sa, tcgaucec.archive_info ai
 WHERE gsl.slide_id=sa.slide_id
   AND sa.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY gsl.uuid,gsl.slide_barcode,portion_id,substr(gsl.slide_barcode,length(gsl.slide_barcode)-2), ai.serial_index, ai.center_id;


CREATE OR REPLACE FORCE VIEW surgery_v AS
SELECT distinct 'BLCA' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgablca.surgery r, tcgablca.surgery_archive ra, tcgablca.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'BRCA' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgabrca.surgery r, tcgabrca.surgery_archive ra, tcgabrca.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'CESC' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgacesc.surgery r, tcgacesc.surgery_archive ra, tcgacesc.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'COAD' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgacoad.surgery r, tcgacoad.surgery_archive ra, tcgacoad.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'DLBC' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgadlbc.surgery r, tcgadlbc.surgery_archive ra, tcgadlbc.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'GBM' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgagbm.surgery r, tcgagbm.surgery_archive ra, tcgagbm.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'HNSC' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgahnsc.surgery r, tcgahnsc.surgery_archive ra, tcgahnsc.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'KIRC' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgakirc.surgery r, tcgakirc.surgery_archive ra, tcgakirc.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'KIRP' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgakirp.surgery r, tcgakirp.surgery_archive ra, tcgakirp.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LAML' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalaml.surgery r, tcgalaml.surgery_archive ra, tcgalaml.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LCLL' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalcll.surgery r, tcgalcll.surgery_archive ra, tcgalcll.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LGG' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalgg.surgery r, tcgalgg.surgery_archive ra, tcgalgg.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LIHC' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalihc.surgery r, tcgalihc.surgery_archive ra, tcgalihc.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LNNH' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalnnh.surgery r, tcgalnnh.surgery_archive ra, tcgalnnh.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LUAD' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaluad.surgery r, tcgaluad.surgery_archive ra, tcgaluad.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LUSC' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalusc.surgery r, tcgalusc.surgery_archive ra, tcgalusc.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'OV' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaov.surgery r, tcgaov.surgery_archive ra, tcgaov.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'PAAD' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgapaad.surgery r, tcgapaad.surgery_archive ra, tcgapaad.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'PRAD' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaprad.surgery r, tcgaprad.surgery_archive ra, tcgaprad.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'READ' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaread.surgery r, tcgaread.surgery_archive ra, tcgaread.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'SALD' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgasald.surgery r, tcgasald.surgery_archive ra, tcgasald.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'SKCM' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaskcm.surgery r, tcgaskcm.surgery_archive ra, tcgaskcm.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'STAD' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgastad.surgery r, tcgastad.surgery_archive ra, tcgastad.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'THCA' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgathca.surgery r, tcgathca.surgery_archive ra, tcgathca.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'UCEC' as disease_abbreviation, r.uuid,r.surgery_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaucec.surgery r, tcgaucec.surgery_archive ra, tcgaucec.archive_info ai
 WHERE r.surgery_id=ra.surgery_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.surgery_barcode,r.patient_id, ai.serial_index, ai.center_id;

CREATE OR REPLACE FORCE VIEW radiation_v AS
SELECT distinct 'BLCA' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgablca.radiation r, tcgablca.radiation_archive ra, tcgablca.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'BRCA' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgabrca.radiation r, tcgabrca.radiation_archive ra, tcgabrca.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'CESC' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgacesc.radiation r, tcgacesc.radiation_archive ra, tcgacesc.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'COAD' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgacoad.radiation r, tcgacoad.radiation_archive ra, tcgacoad.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'DLBC' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgadlbc.radiation r, tcgadlbc.radiation_archive ra, tcgadlbc.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'GBM' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgagbm.radiation r, tcgagbm.radiation_archive ra, tcgagbm.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'HNSC' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgahnsc.radiation r, tcgahnsc.radiation_archive ra, tcgahnsc.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'KIRC' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgakirc.radiation r, tcgakirc.radiation_archive ra, tcgakirc.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'KIRP' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgakirp.radiation r, tcgakirp.radiation_archive ra, tcgakirp.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LAML' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalaml.radiation r, tcgalaml.radiation_archive ra, tcgalaml.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LCLL' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalcll.radiation r, tcgalcll.radiation_archive ra, tcgalcll.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LGG' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalgg.radiation r, tcgalgg.radiation_archive ra, tcgalgg.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LIHC' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalihc.radiation r, tcgalihc.radiation_archive ra, tcgalihc.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LNNH' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalnnh.radiation r, tcgalnnh.radiation_archive ra, tcgalnnh.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LUAD' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaluad.radiation r, tcgaluad.radiation_archive ra, tcgaluad.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LUSC' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalusc.radiation r, tcgalusc.radiation_archive ra, tcgalusc.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'OV' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaov.radiation r, tcgaov.radiation_archive ra, tcgaov.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'PAAD' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgapaad.radiation r, tcgapaad.radiation_archive ra, tcgapaad.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'PRAD' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaprad.radiation r, tcgaprad.radiation_archive ra, tcgaprad.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'READ' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaread.radiation r, tcgaread.radiation_archive ra, tcgaread.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'SALD' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgasald.radiation r, tcgasald.radiation_archive ra, tcgasald.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'SKCM' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaskcm.radiation r, tcgaskcm.radiation_archive ra, tcgaskcm.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'STAD' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgastad.radiation r, tcgastad.radiation_archive ra, tcgastad.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'THCA' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgathca.radiation r, tcgathca.radiation_archive ra, tcgathca.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'UCEC' as disease_abbreviation, r.uuid,r.radiation_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaucec.radiation r, tcgaucec.radiation_archive ra, tcgaucec.archive_info ai
 WHERE r.radiation_id=ra.radiation_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.radiation_barcode,r.patient_id, ai.serial_index, ai.center_id;

CREATE OR REPLACE FORCE VIEW examination_v AS
SELECT distinct 'BLCA' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgablca.examination r, tcgablca.examination_archive ra, tcgablca.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'BRCA' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgabrca.examination r, tcgabrca.examination_archive ra, tcgabrca.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'CESC' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgacesc.examination r, tcgacesc.examination_archive ra, tcgacesc.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'COAD' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgacoad.examination r, tcgacoad.examination_archive ra, tcgacoad.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'DLBC' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgadlbc.examination r, tcgadlbc.examination_archive ra, tcgadlbc.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'GBM' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgagbm.examination r, tcgagbm.examination_archive ra, tcgagbm.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'HNSC' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgahnsc.examination r, tcgahnsc.examination_archive ra, tcgahnsc.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'KIRC' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgakirc.examination r, tcgakirc.examination_archive ra, tcgakirc.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'KIRP' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgakirp.examination r, tcgakirp.examination_archive ra, tcgakirp.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LAML' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalaml.examination r, tcgalaml.examination_archive ra, tcgalaml.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LCLL' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalcll.examination r, tcgalcll.examination_archive ra, tcgalcll.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LGG' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalgg.examination r, tcgalgg.examination_archive ra, tcgalgg.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LIHC' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalihc.examination r, tcgalihc.examination_archive ra, tcgalihc.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LNNH' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalnnh.examination r, tcgalnnh.examination_archive ra, tcgalnnh.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LUAD' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaluad.examination r, tcgaluad.examination_archive ra, tcgaluad.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LUSC' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalusc.examination r, tcgalusc.examination_archive ra, tcgalusc.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'OV' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaov.examination r, tcgaov.examination_archive ra, tcgaov.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'PAAD' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgapaad.examination r, tcgapaad.examination_archive ra, tcgapaad.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'PRAD' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaprad.examination r, tcgaprad.examination_archive ra, tcgaprad.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'READ' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaread.examination r, tcgaread.examination_archive ra, tcgaread.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'SALD' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgasald.examination r, tcgasald.examination_archive ra, tcgasald.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'SKCM' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaskcm.examination r, tcgaskcm.examination_archive ra, tcgaskcm.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'STAD' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgastad.examination r, tcgastad.examination_archive ra, tcgastad.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'THCA' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgathca.examination r, tcgathca.examination_archive ra, tcgathca.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'UCEC' as disease_abbreviation, r.uuid,r.exam_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaucec.examination r, tcgaucec.examination_archive ra, tcgaucec.archive_info ai
 WHERE r.examination_id=ra.examination_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.exam_barcode,r.patient_id, ai.serial_index, ai.center_id;

CREATE OR REPLACE FORCE VIEW drug_v AS
SELECT distinct 'BLCA' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgablca.drug_intgen r, tcgablca.drug_intgen_archive ra, tcgablca.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'BRCA' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgabrca.drug_intgen r, tcgabrca.drug_intgen_archive ra, tcgabrca.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'CESC' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgacesc.drug_intgen r, tcgacesc.drug_intgen_archive ra, tcgacesc.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'COAD' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgacoad.drug_intgen r, tcgacoad.drug_intgen_archive ra, tcgacoad.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'DLBC' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgadlbc.drug_intgen r, tcgadlbc.drug_intgen_archive ra, tcgadlbc.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'GBM' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgagbm.drug_intgen r, tcgagbm.drug_intgen_archive ra, tcgagbm.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'HNSC' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgahnsc.drug_intgen r, tcgahnsc.drug_intgen_archive ra, tcgahnsc.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'KIRC' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgakirc.drug_intgen r, tcgakirc.drug_intgen_archive ra, tcgakirc.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'KIRP' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgakirp.drug_intgen r, tcgakirp.drug_intgen_archive ra, tcgakirp.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LAML' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalaml.drug_intgen r, tcgalaml.drug_intgen_archive ra, tcgalaml.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LCLL' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalcll.drug_intgen r, tcgalcll.drug_intgen_archive ra, tcgalcll.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LGG' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalgg.drug_intgen r, tcgalgg.drug_intgen_archive ra, tcgalgg.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LIHC' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalihc.drug_intgen r, tcgalihc.drug_intgen_archive ra, tcgalihc.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LNNH' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalnnh.drug_intgen r, tcgalnnh.drug_intgen_archive ra, tcgalnnh.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LUAD' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaluad.drug_intgen r, tcgaluad.drug_intgen_archive ra, tcgaluad.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'LUSC' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgalusc.drug_intgen r, tcgalusc.drug_intgen_archive ra, tcgalusc.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'OV' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaov.drug_intgen r, tcgaov.drug_intgen_archive ra, tcgaov.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'PAAD' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgapaad.drug_intgen r, tcgapaad.drug_intgen_archive ra, tcgapaad.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'PRAD' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaprad.drug_intgen r, tcgaprad.drug_intgen_archive ra, tcgaprad.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'READ' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaread.drug_intgen r, tcgaread.drug_intgen_archive ra, tcgaread.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'SALD' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgasald.drug_intgen r, tcgasald.drug_intgen_archive ra, tcgasald.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'SKCM' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaskcm.drug_intgen r, tcgaskcm.drug_intgen_archive ra, tcgaskcm.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'STAD' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgastad.drug_intgen r, tcgastad.drug_intgen_archive ra, tcgastad.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'THCA' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgathca.drug_intgen r, tcgathca.drug_intgen_archive ra, tcgathca.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id
UNION
SELECT distinct 'UCEC' as disease_abbreviation, r.uuid,r.drug_barcode as barcode,r.patient_id, max(TRUNC(ai.date_added)) as date_updated, min(TRUNC(ai.date_added)) as date_added, ai.serial_index as batch, ai.center_id
  FROM tcgaucec.drug_intgen r, tcgaucec.drug_intgen_archive ra, tcgaucec.archive_info ai
 WHERE r.drug_id=ra.drug_id
   AND ra.archive_id=ai.archive_id
   AND ai.is_latest=1
GROUP BY r.uuid,r.drug_barcode,r.patient_id, ai.serial_index, ai.center_id;

/*
** This procedure will get a data from clinical tables in each disease schema for the uuid browser and put it into the 
** dccCommon uuid_hierarchy table, a denormalized hierarchical view of all data that has uuid associations. If the
** uuid already exists in the hierarchy, it will update the updated_date and barcode, and for some items, other
** barcode-related metadata
**
** Written by Shelley Alonso 03/10/2011
**
** Revision History
**
**   Shelley Alonso   03/22/2011  put id's in uuid_hierarchy instead of values for sample_type, item_type
**                                and center
**   Shelley Alonso   04/07/2011  add batch number, center id for bcr, and populate uuid_platform table
**   Shelley Alonso   04/27/2011  add platforms column and populate with comma-delimited list of platform_ids
**   Shelley Alonso   04/28/2011  add slide_layer column and populate based on value of first letter of slide
*/
CREATE OR REPLACE PROCEDURE PopulateUuidHierarchy
IS
CURSOR pCur IS
   select disease_abbreviation,uuid,barcode, participant_number,tss_code,patient_id, date_added, date_updated,center_id,batch
     from patient_v 
   order by disease_abbreviation;
CURSOR sCur (patientId NUMBER, disease VARCHAR2) IS
   select disease_abbreviation,uuid,barcode, sample_type,sample_sequence,sample_id, date_added, date_updated,center_id,batch
   from sample_v 
   where patient_id = patientId
     and disease_abbreviation = disease;
CURSOR rCur (patientId NUMBER, disease VARCHAR2) IS
   select uuid,barcode, date_added, date_updated,center_id,batch
   from radiation_v 
   where patient_id = patientId
     and disease_abbreviation = disease;
CURSOR suCur (patientId NUMBER, disease VARCHAR2) IS
   select uuid,barcode,date_added,date_updated,center_id,batch
   from surgery_v 
   where patient_id = patientId
     and disease_abbreviation = disease;
CURSOR exCur (patientId NUMBER, disease VARCHAR2) IS
   select uuid,barcode,date_added, date_updated,center_id,batch
   from examination_v 
   where patient_id = patientId
     and disease_abbreviation = disease;
CURSOR dCur (patientId NUMBER, disease VARCHAR2) IS
   select uuid,barcode, date_added, date_updated,center_id,batch
   from drug_v 
   where patient_id = patientId
     and disease_abbreviation = disease;
CURSOR poCur (sampleId NUMBER, disease VARCHAR2) IS
   select disease_abbreviation,uuid,barcode, portion,portion_id,date_added, date_updated,center_id,batch
   from portion_v
   where sample_id= sampleId
     and disease_abbreviation = disease;
CURSOR anCur (portionId NUMBER, disease VARCHAR2) IS
   select disease_abbreviation,uuid,barcode,analyte_code,analyte_id, date_added, date_updated,center_id,batch
   from analyte_v
   where portion_id=portionId
     and disease_abbreviation = disease;
CURSOR alCur (analyteId NUMBER, disease VARCHAR2) IS
    select v.uuid,barcode,v.plate, v.date_added, v.date_updated, bc.center_id as receiving_center_id, v.center_id,v.batch
    from aliquot_v v, center_to_bcr_center bc
    WHERE v.analyte_id = analyteId 
    and   v.disease_abbreviation = disease
    and   v.bcr_center_id=bc.bcr_center_id ;
CURSOR slCur (portionId NUMBER, disease VARCHAR2) IS
  select uuid,barcode,slide, date_added, date_updated,center_id,batch
  from slide_v 
  where portion_id=portionId
    and disease_abbreviation = disease;
CURSOR platCur IS
  SELECT DISTINCT u.uuid, a.platform_id
  FROM   uuid_hierarchy u,biospecimen_barcode b, biospecimen_to_file bf, file_to_archive fa, archive_info a, center c
  WHERE  u.item_type_id = 6
  AND    u.uuid = b.uuid
  AND    b.is_viewable=1
  AND    b.biospecimen_id = bf.biospecimen_id
  AND    bf.file_id=fa.file_id
  AND    fa.archive_id = a.archive_id
  AND    a.is_latest = 1
  AND    a.center_id=c.center_id
  AND    c.center_type_code != 'BCR';
CURSOR treeCur (startUuid VARCHAR2) IS
    SELECT uuid,parent_uuid
    FROM uuid_hierarchy
    START WITH uuid= startUuid
    CONNECT BY uuid = prior parent_uuid;
CURSOR agCur IS
  SELECT uuid,RTRIM(XMLAGG(XMLELEMENT(e,platform_id || ',')).EXTRACT('//text()'),',') platforms 
  FROM uuid_platform 
  GROUP BY uuid;
  
patientNumber             VARCHAR2(10);
parentUUID                VARCHAR2(36);
tss                       VARCHAR2(10);
diseaseAbbreviation       VARCHAR2(10);
portionSequence           VARCHAR2(10);
sampleType                VARCHAR2(10);
sampleSequence            VARCHAR2(10);
analyteCode               VARCHAR2(10);
slideLayer                VARCHAR2(7);

BEGIN 
FOR pRec IN pCur
 LOOP
 
    MERGE INTO uuid_hierarchy USING dual ON (uuid=pRec.uuid)
    WHEN MATCHED THEN UPDATE SET update_date = pRec.date_updated, barcode=pRec.barcode
    WHEN NOT MATCHED THEN INSERT (uuid,item_type_id , participant_number,tss_code, barcode, disease_abbreviation,create_date,center_id_bcr,batch_number) 
    VALUES (pRec.uuid,1,pRec.participant_number, pRec.tss_code,pRec.barcode,pRec.disease_abbreviation,pRec.date_added,pRec.center_id,pRec.batch);
    patientNumber          := pRec.participant_number;
    tss                    := pRec.tss_code;
    diseaseAbbreviation    := pRec.disease_abbreviation;
    FOR rRec IN rCur (pRec.patient_id,pRec.disease_abbreviation) 
    LOOP
       MERGE INTO uuid_hierarchy USING dual ON (uuid=rRec.uuid)
       WHEN MATCHED THEN UPDATE SET update_date = rRec.date_updated, barcode = rRec.barcode
       WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
       VALUES (rRec.uuid,pRec.uuid,7,patientNumber,tss,rRec.barcode,diseaseAbbreviation,rRec.date_added,rRec.center_id,rRec.batch);
    END LOOP;   
    FOR suRec IN suCur (pRec.patient_id,pRec.disease_abbreviation)
    LOOP
       MERGE INTO uuid_hierarchy USING dual ON (uuid=suRec.uuid)
       WHEN MATCHED THEN UPDATE SET update_date = suRec.date_added, barcode=suRec.barcode
       WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
       VALUES (suRec.uuid,pRec.uuid,10,patientNumber,tss,suRec.barcode,diseaseAbbreviation,suRec.date_added,suRec.center_id,suRec.batch);
    END LOOP;   
    FOR exRec IN exCur (pRec.patient_id,pRec.disease_abbreviation)
    LOOP
       MERGE INTO uuid_hierarchy USING dual ON (uuid=exRec.uuid)
       WHEN MATCHED THEN UPDATE SET update_date = exRec.date_added, barcode=exRec.barcode
       WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
       VALUES (exRec.uuid,pRec.uuid,9,patientNumber,tss,exRec.barcode,diseaseAbbreviation,exRec.date_added,exRec.center_id,exRec.batch);
    END LOOP;   
    FOR dRec IN dCur (pRec.patient_id,pRec.disease_abbreviation)
    LOOP
       MERGE INTO uuid_hierarchy USING dual ON (uuid=dRec.uuid)
       WHEN MATCHED THEN UPDATE SET update_date = dRec.date_updated, barcode=dRec.barcode
       WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
       VALUES (dRec.uuid,pRec.uuid,8,patientNumber,tss,dRec.barcode,diseaseAbbreviation,dRec.date_added,dRec.center_id,dRec.batch);
    END LOOP;   
    FOR sRec IN sCur (pRec.patient_id,pRec.disease_abbreviation) 
    LOOP
       sampleSequence := sRec.sample_sequence;
       sampleType := sRec.sample_type;
       MERGE INTO uuid_hierarchy USING dual ON (uuid=sRec.uuid)
       WHEN MATCHED THEN UPDATE SET update_date = pRec.date_updated, barcode=sRec.barcode, sample_type_code=sRec.sample_type, sample_sequence=sRec.sample_sequence
       WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,sample_type_code,sample_sequence,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
       VALUES (sRec.uuid,pRec.uuid,2,patientNumber,tss,sampleType,sRec.sample_sequence,sRec.barcode,diseaseAbbreviation,sRec.date_added,sRec.center_id,sRec.batch);
       
       FOR poRec in poCur (sRec.sample_id,sRec.disease_abbreviation)
       LOOP
       
           MERGE INTO uuid_hierarchy USING dual ON (uuid=poRec.uuid)
           WHEN MATCHED THEN UPDATE SET update_date = poRec.date_updated, barcode=poRec.barcode
           WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,sample_type_code,sample_sequence,portion_sequence,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
           VALUES (poRec.uuid,sRec.uuid,3,patientNumber,tss,sampleType,sampleSequence,poRec.portion,poRec.barcode,diseaseAbbreviation,poRec.date_added,poRec.center_id,poRec.batch);
           portionSequence := poRec.portion;
          FOR anRec in anCur (poRec.portion_id,poRec.disease_abbreviation)
          LOOP

            analyteCode := anRec.analyte_code;
         
         
            MERGE INTO uuid_hierarchy USING dual ON (uuid=anRec.uuid)
            WHEN MATCHED THEN UPDATE SET update_date = anRec.date_updated, barcode=anRec.barcode, portion_analyte_code=anRec.analyte_code
            WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,sample_type_code,sample_sequence,portion_sequence,portion_analyte_code,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
             VALUES (anRec.uuid,poRec.uuid,4,patientNumber,tss,sampleType,sampleSequence,portionSequence,anRec.analyte_code,anRec.barcode,diseaseAbbreviation,anRec.date_added,anRec.center_id,anRec.batch);

             
             FOR alRec in alCur (anRec.analyte_id, anRec.disease_abbreviation)
              LOOP
                MERGE INTO uuid_hierarchy USING dual ON (uuid=alRec.uuid)
                WHEN MATCHED THEN UPDATE SET update_date = alRec.date_updated, barcode=alRec.barcode, portion_analyte_code=analyteCode, plate_id=alRec.plate
                WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,sample_type_code,sample_sequence,portion_sequence,portion_analyte_code,plate_id,receiving_center_id,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
                VALUES (alRec.uuid,anRec.uuid,6,patientNumber,tss,sampleType,sampleSequence,portionSequence,analyteCode,alRec.plate,alRec.receiving_center_id,alRec.barcode,diseaseAbbreviation,alRec.date_added,alRec.center_id,alRec.batch);
             END LOOP;
             
          END LOOP;
          
          FOR slRec IN slCur (poRec.portion_id,poRec.disease_abbreviation)
          LOOP
             SELECT DECODE(substr(slRec.slide,1,1),'T','top','B','bottom','M','middle',null) into slideLayer fROM DUAL;
             MERGE INTO uuid_hierarchy USING dual ON (uuid=slRec.uuid)
             WHEN MATCHED THEN UPDATE SET update_date = slRec.date_updated, slide=slRec.slide, slide_layer=slideLayer, barcode=slRec.barcode
             WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,sample_type_code,sample_sequence,portion_sequence,slide,slide_layer,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
             VALUES (slRec.uuid,poRec.uuid,5,patientNumber,tss,sampleType,sampleSequence,portionSequence,slRec.slide,slideLayer,slRec.barcode,diseaseAbbreviation,slRec.date_added,slRec.center_id,slRec.batch);

          END LOOP;
      
      END LOOP;
   END LOOP;
END LOOP;
commit;

FOR platRec IN platCur LOOP
    /*
    ** get the hierarchy of items from uuid_hierarchy for the aliquot uuid that is in the platRec and insert
    ** a record in the uuid_platform relationship table, if there is 
    ** not already one there
    */
    FOR treeRec IN treeCur (platRec.uuid) LOOP
       MERGE INTO uuid_platform u
       USING DUAL
       ON (u.uuid = treeRec.uuid and u.platform_id = platRec.platform_id)
        WHEN NOT MATCHED THEN 
       INSERT(uuid,platform_id) VALUES (treeRec.uuid,platRec.platform_id);
    END LOOP;   

END LOOP;
COMMIT;
FOR agRec IN agCur LOOP
    UPDATE uuid_hierarchy 
    SET    platforms = agRec.platforms
    WHERE  uuid = agRec.uuid;
END LOOP;
COMMIT;

END;
/
DROP TABLE transaction_log CASCADE CONSTRAINTS;
CREATE TABLE transaction_log
(
  TRANSACTION_LOG_ID	NUMBER(38)	NOT NULL,
  ARCHIVE_NAME		VARCHAR2(2000) 	NOT NULL,  
  ENVIRONMENT           VARCHAR2(50)    NOT NULL,
  ISSUCCESSFUL          CHAR(1)        check (isSuccessful in ('Y','N')), 
  CREATED_DATE		TIMESTAMP,
  UPDATED_DATE		TIMESTAMP,
  CONSTRAINT transaction_log_id_idx PRIMARY KEY (TRANSACTION_LOG_ID) 
);

/**
Individual transaction record
**/
DROP TABLE transaction_log_record CASCADE CONSTRAINTS;
CREATE TABLE transaction_log_record
(
  TRANSACTION_LOG_RECORD_ID	NUMBER(38)	NOT NULL,
  TRANSACTION_LOG_ID		NUMBER(38) 	NOT NULL,  
  LOGGING_STATE             	VARCHAR2(500)     NOT NULL,  
  TRANSACTION_LOG_TS        	TIMESTAMP(6)       NOT NULL,
  ISSUCCESSFUL          	CHAR(1)        check (isSuccessful in ('Y','N')), 
  CONSTRAINT transaction_log_record_id_idx PRIMARY KEY (TRANSACTION_LOG_RECORD_ID)
);

ALTER TABLE transaction_log_record ADD (
  CONSTRAINT fk_transaction_log_id
  FOREIGN KEY (transaction_log_id)
  REFERENCES transaction_log(transaction_log_id)
);

DROP TABLE transaction_error CASCADE CONSTRAINTS;
CREATE TABLE transaction_error
(
  TRANSACTION_ERROR_ID  	NUMBER(38)	NOT NULL,
  TRANSACTION_LOG_ID		NUMBER(38)  NOT NULL,
  ARCHIVE_NAME	VARCHAR2(2000) 	NOT NULL,  
  ERROR_MESSAGE                 CLOB,      
  CONSTRAINT transaction_error_id_idx PRIMARY KEY (transaction_error_id)
);

ALTER TABLE transaction_error ADD (
  CONSTRAINT fk_error_to_transaction_log_id
  FOREIGN KEY (transaction_log_id)
  REFERENCES transaction_log(transaction_log_id)
);

DROP SEQUENCE TRANSACTION_LOG_ID_SEQ;
CREATE SEQUENCE TRANSACTION_LOG_ID_SEQ
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1;
DROP SEQUENCE transaction_log_record_id_seq;
CREATE SEQUENCE transaction_log_record_id_seq
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1;
DROP SEQUENCE 	transaction_log_error_id_seq;
CREATE SEQUENCE transaction_log_error_id_seq
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1;
GRANT SELECT ON TRANSACTION_LOG_ID_SEQ to commonmaint;
grant select on transaction_log_record_id_seq to commonmaint;
grant select on transaction_log_error_id_seq to commonmaint;
grant all on transaction_error to commonmaint;
grant all on transaction_log to commonmaint;
grant all on transaction_log_record to commonmaint;
GRANT ALL ON uuid_platform to commonmaint;
GRANT SELECT ON uuid_platform to commonread;
GRANT ALL ON uuid_hierarchy to commonmaint;
GRANT SELECT ON uuid_hierarchy to commonread;
GRANT ALL ON annotation_classification to commonmaint;
GRANT SELECT ON annotation_classification to commonread;
