ALTER TABLE shipped_biospecimen ADD (is_control NUMBER(1) DEFAULT 0);
 
UPDATE shipped_biospecimen SET is_control=1 WHERE tss_code IN ('AV','07');
commit;

DROP TABLE shipped_item_type CASCADE CONSTRAINTS;
CREATE TABLE shipped_item_type (
    shipped_item_type_id    	INTEGER    	NOT NULL,
    shipped_item_type    	VARCHAR2(20)    NOT NULL,
    CONSTRAINT shipped_item_type_pk_idx PRIMARY KEY (shipped_item_type_id)
);

INSERT INTO shipped_item_type values (1,'Aliquot');
INSERT INTO shipped_item_type values (2,'Shipping Portion');
commit;


CREATE OR REPLACE view shipped_biospecimen_breakdown AS
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
     
 ALTER TABLE clinical_Table ADD (is_dynamic number(1) default 0 not null, dynamic_identifier_column_name varchar2(100));
 
 DROP TABLE biospecimen_cqcf CASCADE CONSTRAINTS;
 CREATE TABLE biospecimen_cqcf (
     biospecimen_cqcf_id    	NUMBER(38)    NOT NULL, 
     patient_id        		NUMBER(38)    NOT NULL,
     CONSTRAINT pk_biospecimen_cqsf_idx PRIMARY KEY (biospecimen_cqcf_id)
 );
 ALTER TABLE biospecimen_cqcf ADD (
     CONSTRAINT fk_biospecimen_cqcf_patient 
     FOREIGN KEY (patient_id)
     REFERENCES patient(patient_id)
 );
 
 DROP TABLE biospecimen_cqcf_element CASCADE CONSTRAINTS;
 CREATE TABLE biospecimen_cqcf_element (
     biospecimen_cqcf_element_id        NUMBER(38)    NOT NULL, 
     biospecimen_cqcf_id        	NUMBER(38)    NOT NULL, 
     clinical_xsd_element_id        	NUMBER(38)    NOT NULL,
     element_value			VARCHAR2(4000),
     CONSTRAINT pk_biospecimen_cqcf_elem_idx PRIMARY KEY (biospecimen_cqcf_element_id)
 );
 ALTER TABLE biospecimen_cqcf_element ADD (
     CONSTRAINT fk_biospec_cqcf_elem_parent 
     FOREIGN KEY (biospecimen_cqcf_id)
     REFERENCES biospecimen_cqcf(biospecimen_cqcf_id),
     CONSTRAINT fk_biospec_cqcf_elem_elem 
     FOREIGN KEY (clinical_xsd_element_id)
     REFERENCES clinical_xsd_element(clinical_xsd_element_id)
 );
 
 DROP TABLE biospecimen_cqcf_Archive CASCADE CONSTRAINTS;
 CREATE TABLE biospecimen_cqcf_archive (
     biospecimen_cqcf_archive_id        NUMBER(38)    NOT NULL,
     biospecimen_cqcf_id        	NUMBER(38)    NOT NULL,
     archive_id            		NUMBER(38)    NOT NULL,
     CONSTRAINT pk_biospec_cqcf_archive_idx PRIMARY KEY (biospecimen_cqcf_archive_id)
 );
 
 ALTER TABLE biospecimen_cqcf_archive ADD (
     CONSTRAINT fk_biospec_cqcf_arch_biospec 
     FOREIGN KEY (biospecimen_cqcf_id)
     REFERENCES biospecimen_cqcf(biospecimen_cqcf_id),
     CONSTRAINT fk_biospec_cqcf_arch_arch
     FOREIGN KEY(archive_id)
     REFERENCES archive_info (archive_id)
 );
 
 
 DROP TABLE tumor_sample CASCADE CONSTRAINTS;
 CREATE TABLE tumor_sample (
     tumor_sample_id        NUMBER(38)    NOT NULL, 
     biospecimen_cqcf_id    NUMBER(38)    NOT NULL,
     CONSTRAINT pk_tumor_sample_idx PRIMARY KEY(tumor_sample_id)
 );
 
 ALTER TABLE tumor_sample ADD (
     CONSTRAINT fk_tumor_sample_biospec_qcf 
     FOREIGN KEY (biospecimen_cqcf_id)
     REFERENCES biospecimen_cqcf(biospecimen_cqcf_id)
 );
 
 DROP TABLE tumor_sample_element CASCADE CONSTRAINTS;
 CREATE TABLE tumor_sample_element (
     tumor_sample_element_id	NUMBER(38)	NOT NULL,
     tumor_sample_id        	NUMBER(38)     	NOT NULL,
     clinical_xsd_element_id    NUMBER(38)    	NOT NULL,
     element_value		VARCHAR2(4000),
     CONSTRAINT pk_tumor_sample_element_idx PRIMARY KEY (tumor_sample_element_id)
 );
 
 ALTER TABLE tumor_sample_element ADD (
     CONSTRAINT fk_tumr_sampl_elem_tumr_samp 
     FOREIGN KEY (tumor_sample_id)
     REFERENCES tumor_sample (tumor_sample_id),
     CONSTRAINT fk_tumor_sample_elem_elem 
     FOREIGN KEY (clinical_xsd_element_id)
     REFERENCES clinical_xsd_element(clinical_xsd_element_id)
 );
 
 
 DROP TABLE tumor_sample_archive CASCADE CONSTRAINTS;
 CREATE TABLE tumor_sample_archive (
     tumor_sample_archive_id	NUMBER    NOT NULL, 
     tumor_sample_id		NUMBER    NOT NULL, 
     archive_id        		NUMBER    NOT NULL,
     CONSTRAINT pk_tumor_sample_archive_idx PRIMARY KEY (tumor_sample_archive_id)
 );
 
 ALTER TABLE tumor_sample_archive ADD (
     CONSTRAINT fk_tumor_sample_arch_ts 
     FOREIGN KEY (tumor_sample_id)
     REFERENCES tumor_sample(tumor_sample_id),
     CONSTRAINT fk_tumor_sample_arch_arch
     FOREIGN KEY(archive_id)
     REFERENCES archive_info(archive_id)
 );
 
 
 DROP TABLE normal_control CASCADE CONSTRAINTS;
 CREATE TABLE normal_control (
     normal_control_id    	NUMBER(38)    NOT NULL, 
     biospecimen_cqcf_id    	NUMBER(38)    NOT NULL,
     CONSTRAINT pk_normal_control_idx PRIMARY KEY (normal_control_id)
 );
 ALTER TABLE normal_control ADD (
     CONSTRAINT fk_norm_ctl_biospec_cqsf_id 
     FOREIGN KEY (biospecimen_cqcf_id)
     REFERENCES biospecimen_cqcf (biospecimen_cqcf_id)
 );
 
 DROP TABLE normal_control_element CASCADE CONSTRAINTS;
 CREATE TABLE normal_control_element (
     normal_control_element_id    	NUMBER(38)    NOT NULL, 
     normal_control_id    		NUMBER(38)    NOT NULL, 
     clinical_xsd_element_id    	NUMBER(38)    NOT NULL, 
     element_value			VARCHAR2(4000),
     CONSTRAINT pk_normal_control_element_idx PRIMARY KEY (normal_control_element_id)
 );
 
 ALTER TABLE normal_control_element ADD (
     CONSTRAINT fk_norm_cntl_elem_norm_cntl 
     FOREIGN KEY (normal_control_id)
     REFERENCES normal_control (normal_control_id),
     CONSTRAINT fk_norm_ctl_elem_elem 
     FOREIGN KEY (clinical_xsd_element_id)
     REFERENCES clinical_xsd_element(clinical_xsd_element_id)
 );
 
 DROP TABLE normal_control_archive CASCADE CONSTRAINTS;
 CREATE TABLE normal_control_archive (
     normal_control_archive_id	NUMBER(38)    NOT NULL,
     normal_control_id    	NUMBER(38)    NOT NULL,
     archive_id        		NUMBER(38)    NOT NULL,
     CONSTRAINT pk_normal_control_arch_idx PRIMARY KEY (normal_control_id,archive_id)
 );
 
 ALTER TABLE normal_control_archive ADD (
     CONSTRAINT fk_norm_cntl_arch_norm_cntl 
     FOREIGN KEY (normal_control_id)
     REFERENCES normal_control (normal_control_id),
     CONSTRAINT fk_norm_ctl_archiv_archiv 
     FOREIGN KEY (archive_id)
     REFERENCES archive_info(archive_id)
 );
 
 
 DROP TABLE clinical_cqcf CASCADE CONSTRAINTS;
 CREATE TABLE clinical_cqcf (
     clinical_cqcf_id	NUMBER(38)    NOT NULL, 
     patient_id        	NUMBER(38)    NOT NULL,
     CONSTRAINT pk_clinical_cqsf_idx PRIMARY KEY (clinical_cqcf_id)
 );
 
 ALTER TABLE clinical_cqcf ADD (
     CONSTRAINT fk_clinical_cqsf_patient
     FOREIGN KEY (patient_id)
     REFERENCES patient (patient_id)
 );
 
 
 DROP TABLE clinical_cqcf_element CASCADE CONSTRAINTS;
 CREATE TABLE clinical_cqcf_element (
     clinical_cqcf_element_id    	NUMBER(38)    NOT NULL, 
     clinical_cqcf_id    		NUMBER(38)    NOT NULL, 
     clinical_xsd_element_id    	NUMBER(38)    NOT NULL, 
     element_value			VARCHAR2(4000),
     CONSTRAINT pk_clincal_cqsf_element_idx PRIMARY KEY (clinical_cqcf_element_id)
 );
 
 ALTER TABLE clinical_cqcf_element ADD (
     CONSTRAINT fk_clin_cqcf_elem_clin_cqsf_id
     FOREIGN KEY (clinical_cqcf_id)
     REFERENCES clinical_cqcf (clinical_cqcf_id),
     CONSTRAINT fk_clin_cqsf_elem_elem
     FOREIGN KEY (clinical_xsd_element_id)
     REFERENCES clinical_xsd_element(clinical_xsd_element_id)
 );
 
 DROP TABLE clinical_cqcf_archive CASCADE CONSTRAINTS;
 CREATE TABLE clinical_cqcf_archive (
     clinical_cqcf_archive_id	NUMBER(38)    NOT NULL, 
     clinical_cqcf_id    	NUMBER(38)    NOT NULL, 
     archive_id        		NUMBER(38)    NOT NULL,
     CONSTRAINT pk_clin_cqcf_archive_idx PRIMARY KEY (clinical_cqcf_archive_id)
 );
 
 ALTER TABLE clinical_cqcf_archive ADD (
     CONSTRAINT clin_cqcf_arch_clin_cqcf
     FOREIGN KEY (clinical_cqcf_id)
     REFERENCES clinical_cqcf(clinical_cqcf_id),
     CONSTRAINT clin_cqcf_arch_archive
     FOREIGN KEY (archive_id)
     REFERENCES archive_info(archive_id)
 );
 
 DROP TABLE follow_up CASCADE CONSTRAINTS;
 CREATE TABLE follow_up (
     follow_up_id        NUMBER(38)    NOT NULL, 
     patient_id        NUMBER(38)    NOT NULL,
     follow_up_version     VARCHAR2(1000)    NOT NULL,
     CONSTRAINT pk_follow_up_idx PRIMARY KEY (follow_up_id)
 );
 
 ALTER TABLE follow_up ADD (
     CONSTRAINT fk_follow_up_patient
     FOREIGN KEY (patient_id)
     REFERENCES patient (patient_id)
 );
 
 DROP TABLE follow_up_element CASCADE CONSTRAINTS;
 CREATE TABLE follow_up_element (
     follow_up_element_id	NUMBER(38)    NOT NULL, 
     follow_up_id        	NUMBER(38)    NOT NULL, 
     clinical_xsd_element_id    NUMBER(38)    NOT NULL,
     element_value		VARCHAR2(4000),
     CONSTRAINT pk_follow_up_element_idx PRIMARY KEY (follow_up_element_id)
 );
 
 
 ALTER TABLE follow_up_element ADD (
     CONSTRAINT fk_followup_elem_followup
     FOREIGN KEY (follow_up_id)
     REFERENCES follow_up (follow_up_id),
     CONSTRAINT fk_followup_elem_element
     FOREIGN KEY (clinical_xsd_element_id)
     REFERENCES clinical_xsd_element (clinical_xsd_element_id)
 );
 
 
 DROP TABLE follow_up_archive CASCADE CONSTRAINTS;
 CREATE TABLE follow_up_archive (
     follow_up_archive_id	NUMBER(38)    NOT NULL, 
     follow_up_id        	NUMBER(38)    NOT NULL, 
     archive_id        		NUMBER(38)    NOT NULL,
     CONSTRAINT pk_followup_archive_idx PRIMARY KEY (follow_up_archive_id)
 );
 
 ALTER TABLE follow_up_archive ADD (
     CONSTRAINT fk_followup_arch_followup
     FOREIGN KEY (follow_up_id)
     REFERENCES follow_up (follow_up_id),
     CONSTRAINT fk_followup_arch_archive
     FOREIGN KEY (archive_id)
     REFERENCES archive_info (archive_id)
 );
 
INSERT INTO clinical_table(clinical_table_id, table_name, join_for_sample, join_for_patient, barcode_element_id, barcode_column_name,
    element_node_name, element_table_name, table_id_column_name, parent_table_id, archive_link_table_name, uuid_element_id, is_dynamic, dynamic_identifier_column_name)
VALUES(18, 'BIOSPECIMEN_CQCF', 'BIOSPECIMEN_CQCF.PATIENT_ID(+)=PATIENT.PATIENT_ID AND PATIENT.PATIENT_ID=SAMPLE.PATIENT_ID', 'BIOSPECIMEN_CQCF.PATIENT_ID(+)=PATIENT.PATIENT_ID',
null, null, 'biospecimen_cqcf', 'BIOSPECIMEN_CQCF_ELEMENT', 'BIOSPECIMEN_CQCF_ID', 1, 'BIOSPECIMEN_CQCF_ARCHIVE', null, 0, null);

INSERT INTO clinical_table(clinical_table_id, table_name, join_for_sample, join_for_patient, barcode_element_id, barcode_column_name,
element_node_name, element_table_name, table_id_column_name, parent_table_id, archive_link_table_name, uuid_element_id, is_dynamic, dynamic_identifier_column_name)
VALUES(19, 'TUMOR_SAMPLE', 'TUMOR_SAMPLE.BIOSPECIMEN_CQCF_ID(+)=BIOSPECIMEN_CQCF.BIOSPECIMEN_CQCF_ID AND BIOSPECIMEN_CQCF.PATIENT_ID=PATIENT.PATIENT_ID AND PATIENT.PATIENT_ID=SAMPLE.PATIENT_ID', 'TUMOR_SAMPLE.BIOSPECIMEN_CQCF_ID(+)=BIOSPECIMEN_CQCF.BIOSPECIMEN_CQCF_ID AND BIOSPECIMEN_CQCF.PATIENT_ID=PATIENT.PATIENT_ID',
null, null, 'tumor_sample', 'TUMOR_SAMPLE_ELEMENT', 'TUMOR_SAMPLE_ID', 18, 'TUMOR_SAMPLE_ARCHIVE', null, 0, null);

INSERT INTO clinical_table(clinical_table_id, table_name, join_for_sample, join_for_patient, barcode_element_id, barcode_column_name,
element_node_name, element_table_name, table_id_column_name, parent_table_id, archive_link_table_name, uuid_element_id, is_dynamic, dynamic_identifier_column_name)
VALUES(20, 'NORMAL_CONTROL', 'NORMAL_CONTROL.BIOSPECIMEN_CQCF_ID(+)=BIOSPECIMEN_CQCF.BIOSPECIMEN_CQCF_ID AND BIOSPECIMEN_CQCF.PATIENT_ID=PATIENT.PATIENT_ID AND PATIENT.PATIENT_ID=SAMPLE.PATIENT_ID', 'NORMAL_CONTROL.BIOSPECIMEN_CQCF_ID(+)=BIOSPECIMEN_CQCF.BIOSPECIMEN_CQCF_ID AND BIOSPECIMEN_CQCF.PATIENT_ID=PATIENT.PATIENT_ID',
null, null, 'normal_control', 'NORMAL_CONTROL_ELEMENT', 'NORMAL_CONTROL_ID', 18, 'NORMAL_CONTROL_ARCHIVE', null, 0, null);

INSERT INTO clinical_table(clinical_table_id, table_name, join_for_sample, join_for_patient, barcode_element_id, barcode_column_name,
    element_node_name, element_table_name, table_id_column_name, parent_table_id, archive_link_table_name, uuid_element_id, is_dynamic, dynamic_identifier_column_name)
VALUES(21, 'CLINICAL_CQCF', 'CLINICAL_CQCF.PATIENT_ID(+)=PATIENT.PATIENT_ID AND PATIENT.PATIENT_ID=SAMPLE.PATIENT_ID', 'CLINICAL_CQCF.PATIENT_ID(+)=PATIENT.PATIENT_ID',
null, null, 'clinical_cqcf', 'CLINICAL_CQCF_ELEMENT', 'CLINICAL_CQCF_ID', 1, 'CLINICAL_CQCF_ARCHIVE', null, 0, null);

INSERT INTO clinical_table(clinical_table_id, table_name, join_for_sample, join_for_patient, barcode_element_id, barcode_column_name,
element_node_name, element_table_name, table_id_column_name, parent_table_id, archive_link_table_name, uuid_element_id, is_dynamic, dynamic_identifier_column_name)
VALUES(22, 'FOLLOW_UP', 'FOLLOW_UP.PATIENT_ID(+)=PATIENT.PATIENT_ID AND PATIENT.PATIENT_ID=SAMPLE.PATIENT_ID', 'FOLLOW_UP.PATIENT_ID(+)=PATIENT.PATIENT_ID',
null, null, 'follow_up_.+', 'FOLLOW_UP_ELEMENT', 'FOLLOW_UP_ID', 1, 'FOLLOW_UP_ARCHIVE', null, 1, 'FOLLOW_UP_VERSION');

commit;

CREATE INDEX biospec_cqcf_patient_idx ON biospecimen_cqcf (patient_id);

CREATE INDEX tumor_smpl_biospec_cqcf_idx ON tumor_sample (biospecimen_cqcf_id);

CREATE INDEX normal_cntrl_biospec_cqcf_idx ON normal_control(biospecimen_cqcf_id);

CREATE INDEX clinical_cqcf_patient_idx ON clinical_cqcf(patient_id);

CREATE INDEX follow_up_patient_idx ON follow_up(patient_id);

 
 ALTER TABLE clinical_file ADD(is_dynamic NUMBER(1) DEFAULT 0 NOT NULL,clinical_table_id NUMBER(38));
 
 ALTER TABLE clinical_file ADD (
 CONSTRAINT fk_clin_file_dynamic_table
 FOREIGN KEY (clinical_table_id)
 REFERENCES clinical_table(clinical_table_id)
);

INSERT INTO clinical_file(clinical_file_id, filename, by_patient, context, is_dynamic, clinical_table_id)
VALUES(25, 'clinical_follow_up', 1, 'dam', 1, 22);

INSERT INTO clinical_file(clinical_file_id, filename, by_patient, context, is_dynamic, clinical_table_id)
VALUES(26, 'biospecimen_cqcf', 1, 'dam', 0, 18);

INSERT INTO clinical_file(clinical_file_id, filename, by_patient, context, is_dynamic, clinical_table_id)
VALUES(27, 'clinical_cqcf', 1, 'dam', 0, 21);

INSERT INTO clinical_file(clinical_file_id, filename, by_patient, context, is_dynamic, clinical_table_id)
VALUES(28, 'normal_control', 1, 'dam', 0, 20);

INSERT INTO clinical_file(clinical_file_id, filename, by_patient, context, is_dynamic, clinical_table_id)
VALUES(29, 'tumor_sample', 1, 'dam', 0, 19);

UPDATE CLINICAL_FILE SET clinical_table_id=1 WHERE filename='clinical_patient';
UPDATE CLINICAL_FILE SET clinical_table_id=2 WHERE filename='clinical_sample';
UPDATE CLINICAL_FILE SET clinical_table_id=3 WHERE filename='clinical_portion';
UPDATE CLINICAL_FILE SET clinical_table_id=4 WHERE filename='clinical_analyte';
UPDATE CLINICAL_FILE SET clinical_table_id=5 WHERE filename='clinical_aliquot';
UPDATE CLINICAL_FILE SET clinical_table_id=6 WHERE filename='clinical_protocol';
UPDATE CLINICAL_FILE SET clinical_table_id=10 WHERE filename='clinical_slide';
UPDATE CLINICAL_FILE SET clinical_table_id=15 WHERE filename='clinical_drug';
UPDATE CLINICAL_FILE SET clinical_table_id=12 WHERE filename='clinical_examination';
UPDATE CLINICAL_FILE SET clinical_table_id=13 WHERE filename='clinical_radiation';
UPDATE CLINICAL_FILE SET clinical_table_id=14 WHERE filename='clinical_surgery';
UPDATE CLINICAL_FILE SET clinical_table_id=17 WHERE filename='clinical_shipment_portion';
UPDATE CLINICAL_FILE SET clinical_table_id=22 WHERE filename='clinical_follow_up';
UPDATE CLINICAL_FILE SET clinical_table_id=18 WHERE filename='biospecimen_cqcf';
UPDATE CLINICAL_FILE SET clinical_table_id=21 WHERE filename='clinical_cqcf';
UPDATE CLINICAL_FILE SET clinical_table_id=20 WHERE filename='normal_control';
UPDATE CLINICAL_FILE SET clinical_table_id=19 WHERE filename='tumor_sample';

DELETE FROM clinical_file_to_table WHERE clinical_file_id IN (SELECT clinical_file_id FROM clinical_file WHERE context='dam');

insert into clinical_file_element(clinical_file_element_id, xsd_element_id, table_id, table_column_name, file_column_name, file_column_order, clinical_file_id)
select 11, clinical_xsd_element_id, 1, 'PATIENT_BARCODE', 'bcr_patient_barcode', 1, 25
from clinical_xsd_element where element_name='bcr_patient_barcode';

update clinical_xsd_element set expected_element='Y' where element_name like '%form_completion%';

--
-- Updating Clinical XSD elements: protected
--
merge into clinical_xsd_element using dual on (element_name='concurrent_chemotherapy_dose')
when matched then update set is_protected=1
when not matched then insert (clinical_xsd_element_id, element_name, is_protected, description, value_type, expected_element)
values (clinical_xsd_seq.nextval, 'concurrent_chemotherapy_dose', 1, 'Here goes the description', 'string', 'Y');

merge into clinical_xsd_element using dual on (element_name='new_neoplasm_occurrence_anatomic_site_text')
when matched then update set is_protected=1
when not matched then insert (clinical_xsd_element_id, element_name, is_protected, description, value_type, expected_element)
values (clinical_xsd_seq.nextval, 'new_neoplasm_occurrence_anatomic_site_text', 1, 'Here goes the description', 'string', 'Y');

merge into clinical_xsd_element using dual on (element_name='other_anatomic_site_normal_tissue')
when matched then update set is_protected=1
when not matched then insert (clinical_xsd_element_id, element_name, is_protected, description, value_type, expected_element)
values (clinical_xsd_seq.nextval, 'other_anatomic_site_normal_tissue', 1, 'Here goes the description', 'string', 'Y');

merge into clinical_xsd_element using dual on (element_name='other_anatomic_site')
when matched then update set is_protected=1
when not matched then insert (clinical_xsd_element_id, element_name, is_protected, description, value_type, expected_element)
values (clinical_xsd_seq.nextval, 'other_anatomic_site', 1, 'Here goes the description', 'string', 'Y');

merge into clinical_xsd_element using dual on (element_name='other_metastatic_involvement_anatomic_site')
when matched then update set is_protected=1
when not matched then insert (clinical_xsd_element_id, element_name, is_protected, description, value_type, expected_element)
values (clinical_xsd_seq.nextval, 'other_metastatic_involvement_anatomic_site', 1, 'Here goes the description', 'string', 'Y');

merge into clinical_xsd_element using dual on (element_name='reason_path_confirm_diagnosis_not_matching')
when matched then update set is_protected=1
when not matched then insert (clinical_xsd_element_id, element_name, is_protected, description, value_type, expected_element)
values (clinical_xsd_seq.nextval, 'reason_path_confirm_diagnosis_not_matching', 1, 'Here goes the description', 'string', 'Y');

merge into clinical_xsd_element using dual on (element_name='other_diagnosis')
when matched then update set is_protected=1
when not matched then insert (clinical_xsd_element_id, element_name, is_protected, description, value_type, expected_element)
values (clinical_xsd_seq.nextval, 'other_diagnosis', 1, 'Here goes the description', 'string', 'Y');

merge into clinical_xsd_element using dual on (element_name='other_method_of_initial_pathological_diagnosis')
when matched then update set is_protected=1
when not matched then insert (clinical_xsd_element_id, element_name, is_protected, description, value_type, expected_element)
values (clinical_xsd_seq.nextval, 'other_method_of_initial_pathological_diagnosis', 1, 'Here goes the description', 'string', 'Y');

merge into clinical_xsd_element using dual on (element_name='history_of_prior_malignancy')
when matched then update set is_protected=1
when not matched then insert (clinical_xsd_element_id, element_name, is_protected, description, value_type, expected_element)
values (clinical_xsd_seq.nextval, 'history_of_prior_malignancy', 1, 'Here goes the description', 'string', 'Y');

merge into clinical_xsd_element using dual on (element_name='cytogenetic_report_submitted')
when matched then update set is_protected=1
when not matched then insert (clinical_xsd_element_id, element_name, is_protected, description, value_type, expected_element)
values (clinical_xsd_seq.nextval, 'cytogenetic_report_submitted', 1, 'Here goes the description', 'string', 'Y');

merge into clinical_xsd_element using dual on (element_name='extrahepatic_recurrent_disease_location_text')
when matched then update set is_protected=1
when not matched then insert (clinical_xsd_element_id, element_name, is_protected, description, value_type, expected_element)
values (clinical_xsd_seq.nextval, 'extrahepatic_recurrent_disease_location_text', 1, 'Here goes the description', 'string', 'Y');

merge into clinical_xsd_element using dual on (element_name='history_of_neoadjuvant_treatment')
when matched then update set is_protected=1
when not matched then insert (clinical_xsd_element_id, element_name, is_protected, description, value_type, expected_element)
values (clinical_xsd_seq.nextval, 'history_of_neoadjuvant_treatment', 1, 'Here goes the description', 'string', 'Y');

--
-- Updating Clinical XSD elements: public
--
merge into clinical_xsd_element using dual on (element_name='jewish_origin')
when matched then update set is_protected=0
when not matched then insert (clinical_xsd_element_id, element_name, is_protected, description, value_type, expected_element)
values (clinical_xsd_seq.nextval, 'jewish_origin', 0, 'Here goes the description', 'string', 'Y');

merge into clinical_xsd_element using dual on (element_name='ethnicity')
when matched then update set is_protected=0
when not matched then insert (clinical_xsd_element_id, element_name, is_protected, description, value_type, expected_element)
values (clinical_xsd_seq.nextval, 'ethnicity', 0, 'Here goes the description', 'string', 'Y');

merge into clinical_xsd_element using dual on (element_name='race')
when matched then update set is_protected=0
when not matched then insert (clinical_xsd_element_id, element_name, is_protected, description, value_type, expected_element)
values (clinical_xsd_seq.nextval, 'race', 0, 'Here goes the description', 'string', 'Y');



commit;

ALTER TABLE data_type ADD (require_compression NUMBER(1) DEFAULT 1 NOT NULL);
UPDATE data_type SET require_compression =0 WHERE name = 'Tissue Slide Images';
COMMIT;


DROP TABLE proteinexp_value;
CREATE TABLE proteinexp_value (
	proteinexp_id			NUMBER(38)	NOT NULL,
	data_set_id			NUMBER(38)	NOT NULL,
	hybridization_ref_id		NUMBER(38)	NOT NULL,
	antibody_name			VARCHAR2(100)	NOT NULL,
	hugo_gene_symbol		VARCHAR2(100),
	protein_expression_value	FLOAT		NOT NULL,
	CONSTRAINT pk_proteinexp_idx PRIMARY KEY (proteinexp_id)
);

/* do not create in prod or stage
ALTER TABLE proteinexp_value ADD (
	CONSTRAINT fk_proteinexp_dataset
	FOREIGN KEY (data_set_id)
	REFERENCES data_Set(data_set_id),
	CONSTRAINT fk_proteinexp_hybref
	FOREIGN KEY (hybridization_ref_id)
	REFERENCES hybridization_ref(hybridization_ref_id)
);
*/
	
CREATE INDEX proteinexp_dataset_idx ON proteinexp_value(data_Set_id,hybridization_ref_id);	
CREATE SEQUENCE proteinexp_value_seq START WITH 1 INCREMENT BY 1;	


-- ONLY FOR SCHEMAS int tiers above dev
-- drop foreign key constraints for level 3 tables that hinder performance on production. The code is testing in dev and test so
-- that will not insert a data set id that is not in the data set table
ALTER TABLE rnaseq_value DISABLE CONSTRAINT fk_rnaseq_dataset;
ALTER TABLE rnaseq_value DISABLE CONSTRAINT fk_rnaseq_hybref;	
ALTER TABLE mirnaseq_value disable CONSTRAINT fk_mirnaseq_dataset;
ALTER TABLE mirnaseq_value disable CONSTRAINT  fk_mirnaseq_hybref;
ALTER TABLE EXPGENE_VALUE disable CONSTRAINT FK_EXPGENE_VALUE_DATASET;
ALTER TABLE EXPGENE_VALUE disable CONSTRAINT FK_EXPGENE_VALUE_HYBREF_ID;
ALTER TABLE CNA_VALUE disable CONSTRAINT FK_CNA_VALUE_DATASET;
ALTER TABLE CNA_VALUE disable CONSTRAINT FK_CNA_VALUE_HYBREF_ID;
ALTER TABLE METHYLATION_VALUE disable CONSTRAINT FK_METHYLATION_VALUE_DATA_SET ;
ALTER TABLE METHYLATION_VALUE disable CONSTRAINT FK_METH_VALUE_HYBREF_ID;

-- grants apply only on stage and prod
grant all on proteinexp_value to commonmaint;
grant select on proteinexp_value to readonly;
grant all on shipped_item_type to commonmaint;
grant select on shipped_item_type to readonly;
grant all on shipped_biospecimen_breakdown to commonmaint;
grant select on shipped_biospecimen_breakdown to readonly;
grant all on biospecimen_cqcf to commonmaint;
grant select on biospecimen_cqcf to readonly;
grant all on biospecimen_cqcf_element to commonmaint;
grant select on biospecimen_cqcf_element to readonly;
grant all on biospecimen_cqcf_archive to commonmaint;
grant select on biospecimen_cqcf_archive to readonly;
grant all on tumor_sample to commonmaint;
grant select on tumor_sample to readonly;
grant all on tumor_sample_element to commonmaint;
grant select on tumor_sample_element to readonly;
grant all on tumor_sample_archive to commonmaint;
grant select on tumor_sample_archive to readonly;
grant all on normal_control to commonmaint;
grant select on normal_control to readonly;
grant all on normal_control_element to commonmaint;
grant select on normal_control_element to readonly;
grant all on normal_control_archive to commonmaint;
grant select on normal_control_archive to readonly;
grant all on clinical_cqcf to commonmaint;
grant select on clinical_cqcf to readonly;
grant all on clinical_cqcf_element to commonmaint;
grant select on clinical_cqcf_element to readonly;
grant all on clinical_cqcf_archive to commonmaint;
grant select on clinical_cqcf_archive to readonly;
grant all on follow_up to commonmaint;
grant select on follow_up to readonly;
grant all on follow_up_element to commonmaint;
grant select on follow_up_element to readonly;
grant all on follow_up_archive to commonmaint;
grant select on follow_up_archive to readonly;

-- apps-5696 Firehose platforms and data types
INSERT INTO data_type (data_type_id, name, center_type_code,ftp_display,available,sort_order,require_compression)
VALUES (35,'analyses','GDAC','analyses',1,51,0);
INSERT INTO data_type (data_type_id, name, center_type_code,ftp_display,available,sort_order,require_compression)
VALUES (36,'stddata','GDAC','stddata',1,52,0);
INSERT INTO data_type (data_type_id, name, center_type_code,ftp_display,available,sort_order,require_compression)
VALUES (37,'Firehose Reports','GDAC','reports',1,53,0);


MERGE INTO data_visibility dv
USING (SELECT data_visibility_id,data_type_id,visibility_id,level_number FROM dcccommon.data_visibility) commdv
ON (dv.data_visibility_id = commdv.data_visibility_id)
WHEN NOT MATCHED THEN INSERT (data_visibility_id,data_type_id,visibility_id,level_number)
VALUES (commdv.data_visibility_id,commdv.data_type_id,commdv.visibility_id,commdv.level_number);


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

MERGE INTO data_type_to_platform dp
USING (SELECT data_type_platform_id,data_type_id, platform_id from dcccommon.data_type_to_platform where data_Type_id in (35,36,37)) commdp
ON (dp.data_type_id = commdp.data_type_id and dp.platform_id = commdp.platform_id)
WHEN NOT MATCHED THEN INSERT (data_type_platform_id,data_type_id, platform_id)
VALUES (commdp.data_type_platform_id,commdp.data_type_id,commdp.platform_id);
commit;
