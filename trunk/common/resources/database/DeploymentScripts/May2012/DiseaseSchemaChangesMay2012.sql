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
            b.is_control,
            b.shipped_date,
            b.shipped_item_type_id,
            b.batch_id
     FROM   shipped_biospecimen b, shipped_biospecimen_element be1, shipped_biospecimen_element be2
     WHERE  (b.shipped_biospecimen_id = be1.shipped_biospecimen_id and be1.element_type_id = 1)
     AND    (b.shipped_biospecimen_id = be2.shipped_biospecimen_id and be2.element_type_id = 2);
 
update clinical_xsd_element set is_protected=0 where expected_element='Y';
commit;

update data_visibility set visibility_id=1 where data_type_id = 10;
commit;

update data_visibility set visibility_id=1 where level_number = 2 and data_type_id in (7,12);
commit;


DROP TABLE participant_uuid_file;
CREATE TABLE participant_uuid_file (
	uuid	VARCHAR2(36) 	NOT NULL,
	file_id	NUMBER(38,0)	NOT NULL,
	CONSTRAINT pk_part_uuid_file_idx PRIMARY KEY (uuid,file_id)
);


CREATE INDEX part_uuid_file_file_idx ON participant_uuid_file (file_id);

create index sample_patientid_idx on sample(patient_id);

create unique index patient_archive_patient_idx on patient_archive(patient_id,archive_id);

grant select on participant_uuid_file to readonly;