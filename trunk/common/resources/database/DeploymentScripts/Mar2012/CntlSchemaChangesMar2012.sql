DROP TABLE clinical_control;
CREATE TABLE clinical_control (
   clinical_control_id        NUMBER(38)    NOT NULL,
   patient_id            NUMBER(38)    NOT NULL,
   clinical_control_barcode    VARCHAR2(50),
   uuid                VARCHAR2(36)    NOT NULL,
   CONSTRAINT pk_clinical_control_idx PRIMARY KEY (clinical_control_id)
);
ALTER TABLE clinical_control ADD (
   CONSTRAINT fk_clinical_control_patient
   FOREIGN KEY (patient_id)
   REFERENCES patient (patient_id)
);
   
DROP TABLE clinical_control_element;
CREATE TABLE clinical_control_element(
   clinical_control_element_id    NUMBER(38)    NOT NULL,
   clinical_control_id        NUMBER(38)    NOT NULL,
   clinical_xsd_element_id    NUMBER(38)    NOT NULL,
   element_value        VARCHAR2(4000),
   CONSTRAINT pk_clinical_control_elem_idx PRIMARY KEY (clinical_control_element_id)
);
ALTER TABLE clinical_control_element ADD (
   CONSTRAINT fk_clin_cntl_elem_clin_cntl
   FOREIGN KEY (clinical_control_id)
   REFERENCES clinical_control (clinical_control_id),
   CONSTRAINT fk_clin_ctl_elem_elem_id
   FOREIGN KEY (clinical_xsd_element_id)
   REFERENCES clinical_xsd_element (clinical_xsd_element_id)
);
   
DROP TABLE clinical_control_archive;
CREATE TABLE clinical_control_archive(
   clinical_control_archive_id    NUMBER(38)    NOT NULL,
   clinical_control_id        NUMBER(38)    NOT NULL,
   archive_id            NUMBER(38)    NOT NULL,
   CONSTRAINT pk_clinical_cntl_archive_idx PRIMARY KEY (clinical_control_archive_id)
);
ALTER TABLE clinical_control_archive ADD (
   CONSTRAINT fk_clin_cntl_arch_clin_cntl
   FOREIGN KEY (clinical_control_id)
   REFERENCES clinical_control (clinical_control_id),
   CONSTRAINT fk_clin_ctl_arch_arch_id
   FOREIGN KEY (archive_id)
   REFERENCES archive_info (archive_id)
);

INSERT INTO clinical_table(clinical_table_id, table_name, join_for_sample, join_for_patient, barcode_element_id, barcode_column_name,
    element_node_name, element_table_name, table_id_column_name, parent_table_id, archive_link_table_name,
    uuid_element_id, is_dynamic)
(select 23, 'CLINICAL_CONTROL',
'CLINICAL_CONTROL.PATIENT_ID(+)=PATIENT.PATIENT_ID AND PATIENT.PATIENT_ID=SAMPLE.PATIENT_ID',
'CLINICAL_CONTROL.PATIENT_ID(+)=PATIENT.PATIENT_ID',
14, 'CLINICAL_CONTROL_BARCODE', 'control', 'CLINICAL_CONTROL_ELEMENT', 'CLINICAL_CONTROL_ID', clinical_table_id,
'CLINICAL_CONTROL_ARCHIVE', clinical_xsd_element_id, 0 from clinical_table, clinical_xsd_element
where clinical_table.table_name='PATIENT' and clinical_xsd_element.element_name='bcr_aliquot_uuid');

INSERT INTO clinical_file(clinical_file_id, filename, by_patient, context, is_dynamic, clinical_table_id)
VALUES(30, 'control', 1, 'dam', 0, 23);
commit;
