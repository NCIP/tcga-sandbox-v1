DROP TABLE ALIQUOT CASCADE constraints;
DROP TABLE ALIQUOT_ELEMENT CASCADE constraints;
DROP TABLE ANALYTE CASCADE constraints;
DROP TABLE ANALYTE_ELEMENT CASCADE constraints;
DROP TABLE DNA CASCADE constraints;
DROP TABLE DNA_ELEMENT CASCADE constraints;
DROP TABLE DRUG_INTGEN CASCADE constraints;
DROP TABLE DRUG_INTGEN_ELEMENT CASCADE constraints;
DROP TABLE EXAMINATION CASCADE constraints;
DROP TABLE EXAMINATION_ELEMENT CASCADE constraints;
DROP TABLE GBMSLIDE CASCADE constraints;
DROP TABLE GBM_PATHOLOGY CASCADE constraints;
DROP TABLE LUNG_PATHOLOGY CASCADE constraints;
DROP TABLE OVARIAN_PATHOLOGY CASCADE constraints;
DROP TABLE PATIENT CASCADE constraints;
DROP TABLE PATIENT_ELEMENT CASCADE constraints;
DROP TABLE PORTION CASCADE constraints;
DROP TABLE PORTION_ELEMENT CASCADE constraints;
DROP TABLE PROTOCOL CASCADE constraints;
DROP TABLE PROTOCOL_ELEMENT CASCADE constraints;
DROP TABLE RADIATION CASCADE constraints;
DROP TABLE RADIATION_ELEMENT CASCADE constraints;
DROP TABLE RNA CASCADE constraints;
DROP TABLE RNA_ELEMENT CASCADE constraints;
DROP TABLE SAMPLE CASCADE constraints;
DROP TABLE SAMPLE_ELEMENT CASCADE constraints;
DROP TABLE SLIDE CASCADE constraints;
DROP TABLE SLIDE_ELEMENT CASCADE constraints;
DROP TABLE SURGERY CASCADE constraints;
DROP TABLE SURGERY_ELEMENT CASCADE constraints;
DROP TABLE TUMORPATHOLOGY CASCADE constraints;
DROP TABLE TUMORPATHOLOGY_ELEMENT CASCADE constraints;
DROP TABLE ALIQUOT_ARCHIVE CASCADE constraints;
DROP TABLE ANALYTE_ARCHIVE CASCADE constraints;
DROP TABLE DNA_ARCHIVE CASCADE constraints;
DROP TABLE DRUG_INTGEN_ARCHIVE CASCADE constraints;
DROP TABLE EXAMINATION_ARCHIVE CASCADE constraints;
DROP TABLE PATIENT_ARCHIVE CASCADE constraints;
DROP TABLE PORTION_ARCHIVE CASCADE constraints;
DROP TABLE PROTOCOL_ARCHIVE CASCADE constraints;
DROP TABLE RADIATION_ARCHIVE CASCADE constraints;
DROP TABLE RNA_ARCHIVE CASCADE constraints;
DROP TABLE SAMPLE_ARCHIVE CASCADE constraints;
DROP TABLE SURGERY_ARCHIVE CASCADE constraints;
DROP TABLE SLIDE_ARCHIVE CASCADE constraints;
DROP TABLE TUMORPATHOLOGY_ARCHIVE CASCADE constraints;
DROP TABLE CLINICAL_FILE_TO_TABLE;
DROP TABLE CLINICAL_FILE cascade constraints;
DROP TABLE CLINICAL_FILE_ELEMENT cascade constraints;
DROP TABLE CLINICAL_TABLE cascade constraints;
DROP TABLE CLINICAL_XSD_ELEMENT cascade constraints;
DROP TABLE CLINICAL_XSD_ENUM_VALUE cascade constraints;
drop view dna_v;
drop view tier1 ;
drop view public_patient;
drop view public_sample;

CREATE TABLE analyte (
    analyte_id    	NUMBER(38)    	NOT NULL,
    portion_id    	NUMBER(38)    	NOT NULL,
    analyte_barcode    	VARCHAR2(50)    NOT NULL,
    uuid        	VARCHAR2(36)    NOT NULL,
    CONSTRAINT pk_analyte_idx PRIMARY KEY (analyte_id),
    CONSTRAINT uk_analyte_uuid_idx UNIQUE (uuid)
);

CREATE TABLE analyte_element (
    analyte_element_id    	NUMBER(38)    	NOT NULL,
    analyte_id        		NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id	NUMBER(38)    	NOT NULL,
    element_value        	VARCHAR2(4000),
    CONSTRAINT pk_analyte_element_idx PRIMARY KEY (analyte_element_id)
);

CREATE TABLE analyte_archive (
	analyte_archive_id	NUMBER(38)	NOT NULL,
	analyte_id		NUMBER(38)	NOT NULL,
	archive_id		NUMBER(38)	NOT NULL,
	CONSTRAINT pk_analyte_archive_idx PRIMARY KEY(analyte_archive_id)
);


CREATE TABLE aliquot (
    aliquot_id    	NUMBER(38)    	NOT NULL,
    analyte_id    	NUMBER(38)    	NOT NULL,
    aliquot_barcode    	VARCHAR2(50)    NOT NULL,
    uuid        	VARCHAR2(36)    NOT NULL,
    CONSTRAINT pk_aliquot_idx PRIMARY KEY (aliquot_id),
    CONSTRAINT uk_aliquot_uuid_idx UNIQUE (uuid)
);

CREATE TABLE aliquot_archive (
	aliquot_archive_id	NUMBER(38)	NOT NULL,
	aliquot_id		NUMBER(38)	NOT NULL,
	archive_id		NUMBER(38)	NOT NULL,
	CONSTRAINT pk_aliquot_archive_idx PRIMARY KEY(aliquot_archive_id)
);

CREATE TABLE aliquot_element (
    aliquot_element_id    	NUMBER(38)    	NOT NULL,
    aliquot_id        		NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id	NUMBER(38)    	NOT NULL,
    element_value        	VARCHAR2(4000)	,
    CONSTRAINT pk_aliquot_element_idx PRIMARY KEY (aliquot_element_id)
);

CREATE TABLE dna (
    dna_id        NUMBER(38)    NOT NULL,
    analyte_id    NUMBER(38)    NOT NULL,
    CONSTRAINT pk_dna_idx PRIMARY KEY (dna_id)
);

CREATE TABLE dna_archive (
	dna_archive_id		NUMBER(38)	NOT NULL,
	dna_id			NUMBER(38)	NOT NULL,
	archive_id		NUMBER(38)	NOT NULL,
	CONSTRAINT pk_dna_archive_idx PRIMARY KEY(dna_archive_id)
);

CREATE TABLE dna_element (
    dna_element_id    		NUMBER(38)    	NOT NULL,
    dna_id        		NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id	NUMBER(38)    	NOT NULL,
    element_value		VARCHAR2(4000)	,
    CONSTRAINT pk_dna_element_idx PRIMARY KEY (dna_element_id)
);

CREATE TABLE drug_intgen (
    drug_id        	NUMBER(38)    	NOT NULL,
    patient_id    	NUMBER(38)    	NOT NULL,
    drug_barcode    	VARCHAR2(50)	NOT NULL,
    uuid        	VARCHAR2(36)    NOT NULL,
    CONSTRAINT pk_drug_intgen_idx PRIMARY KEY (drug_id),
    CONSTRAINT uk_drugint_uuid_idx UNIQUE (uuid)
);

CREATE TABLE drug_intgen_archive (
	drug_intgen_archive_id		NUMBER(38)	NOT NULL,
	drug_id				NUMBER(38)	NOT NULL,
	archive_id			NUMBER(38)	NOT NULL,
	CONSTRAINT pk_drug_intgen_archive_idx PRIMARY KEY(drug_intgen_archive_id)
);

CREATE TABLE drug_intgen_element (
    drug_intgen_element_id    	NUMBER(38)    	NOT NULL,
    drug_id            		NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id	NUMBER(38)    	NOT NULL,
    element_value        	VARCHAR2(4000)	,
    CONSTRAINT pk_drugintgen_element_idx PRIMARY KEY (drug_intgen_element_id)
);

CREATE TABLE examination (
    examination_id    	NUMBER(38)    	NOT NULL,
    patient_id    	NUMBER(38)    	NOT NULL,
    exam_barcode    	VARCHAR2(50)    NOT NULL,
    uuid        	VARCHAR2(36)    NOT NULL,
    CONSTRAINT pk_examination_idx PRIMARY KEY (examination_id),
    CONSTRAINT uk_exam_uuid_idx UNIQUE (uuid)
);

CREATE TABLE examination_archive (
	examination_archive_id		NUMBER(38)	NOT NULL,
	examination_id			NUMBER(38)	NOT NULL,
	archive_id			NUMBER(38)	NOT NULL,
	CONSTRAINT pk_examination_archive_idx PRIMARY KEY(examination_archive_id)
);

CREATE TABLE examination_element (
    examination_element_id    	NUMBER(38)    	NOT NULL,
    examination_id        	NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id	NUMBER(38)    	NOT NULL,
    element_value        	VARCHAR2(4000) 	,
    CONSTRAINT pk_examination_element_idx PRIMARY KEY (examination_element_id)
);

CREATE TABLE patient (
    patient_id    	NUMBER(38)    	NOT NULL,
    patient_barcode    	VARCHAR2(50)    NOT NULL,
    uuid        	VARCHAR2(36)    NOT NULL,
    CONSTRAINT pk_patient_idx PRIMARY KEY (patient_id),
    CONSTRAINT uk_patient_uuid_idx UNIQUE (uuid)
);

CREATE TABLE patient_archive (
	patient_archive_id		NUMBER(38)	NOT NULL,
	patient_id			NUMBER(38)	NOT NULL,
	archive_id			NUMBER(38)	NOT NULL,
	CONSTRAINT pk_patient_archive_idx PRIMARY KEY(patient_archive_id)
);

CREATE TABLE patient_element (
    patient_element_id    	NUMBER(38)    	NOT NULL,
    patient_id        		NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id	NUMBER(38)    	NOT NULL,
    element_value        	VARCHAR2(4000)	,
    CONSTRAINT pk_patient_element_idx PRIMARY KEY (patient_element_id)
);

CREATE TABLE portion (
    portion_id    	NUMBER(38)    	NOT NULL,
    sample_id    	NUMBER(38)    	NOT NULL,
    portion_barcode    	VARCHAR2(50)    NOT NULL,
    uuid        	VARCHAR2(36)    NOT NULL,
    CONSTRAINT pk_portion_idx PRIMARY KEY (portion_id),
    CONSTRAINT uk_portion_uuid_idx UNIQUE (uuid)
);

CREATE TABLE portion_archive (
	portion_archive_id	NUMBER(38)	NOT NULL,
	portion_id		NUMBER(38)	NOT NULL,
	archive_id		NUMBER(38)	NOT NULL,
	CONSTRAINT pk_portion_archive_idx PRIMARY KEY(portion_archive_id)
);

CREATE TABLE portion_element (
    portion_element_id    	NUMBER(38)    	NOT NULL,
    portion_id        		NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id     NUMBER(38)    	NOT NULL,
    element_value        	VARCHAR2(4000)	,
    CONSTRAINT pk_portion_element_idx PRIMARY KEY (portion_element_id)
);


CREATE TABLE protocol (
    protocol_id	NUMBER(38)	NOT NULL,
    analyte_id	NUMBER(38)    	NOT NULL,
    CONSTRAINT pk_protocol_idx PRIMARY KEY (protocol_id)
);

CREATE TABLE protocol_archive (
	protocol_archive_id		NUMBER(38)	NOT NULL,
	protocol_id			NUMBER(38)	NOT NULL,
	archive_id			NUMBER(38)	NOT NULL,
	CONSTRAINT pk_protocol_archive_idx PRIMARY KEY(protocol_archive_id)
);



CREATE TABLE protocol_element (
    protocol_element_id    	NUMBER(38)    	NOT NULL,
    protocol_id        		NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id	NUMBER(38)    	NOT NULL,
    element_value        	VARCHAR2(4000)	,
    CONSTRAINT pk_protocol_element_idx PRIMARY KEY (protocol_element_id)
);

CREATE TABLE radiation (
    radiation_id        NUMBER(38)    	NOT NULL,
    patient_id        	NUMBER(38)    	NOT NULL,
    radiation_barcode	VARCHAR2(50)	NOT NULL,
    uuid            	VARCHAR2(36)    NOT NULL,
    CONSTRAINT pk_radiation_idx PRIMARY KEY (radiation_id),
    CONSTRAINT uk_radiation_uuid_idx UNIQUE (uuid)
);

CREATE TABLE radiation_archive (
	radiation_archive_id		NUMBER(38)	NOT NULL,
	radiation_id			NUMBER(38)	NOT NULL,
	archive_id			NUMBER(38)	NOT NULL,
	CONSTRAINT pk_radiation_archive_idx PRIMARY KEY(radiation_archive_id)
);

CREATE TABLE radiation_element (
    radiation_element_id        NUMBER(38)    	NOT NULL,
    radiation_id            	NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id	NUMBER(38)    	NOT NULL,
    element_value            	VARCHAR2(4000)	,
    CONSTRAINT pk_radiation_element_idx PRIMARY KEY (radiation_element_id)
);

CREATE TABLE rna (
    rna_id        NUMBER(38)    NOT NULL,
    analyte_id    NUMBER(38)    NOT NULL,
    CONSTRAINT pk_rna_idx PRIMARY KEY (rna_id)
);

CREATE TABLE rna_archive(
	rna_archive_id		NUMBER(38)	NOT NULL,
	rna_id			NUMBER(38)	NOT NULL,
	archive_id		NUMBER(38)	NOT NULL,
	CONSTRAINT pk_rna_archive_idx PRIMARY KEY(rna_archive_id)
);

CREATE TABLE rna_element (
    rna_element_id            	NUMBER(38)    	NOT NULL,
    rna_id                	NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id	NUMBER(38)    	NOT NULL,
    element_value            	VARCHAR2(4000)	,
    CONSTRAINT pk_rna_element_idx PRIMARY KEY (rna_element_id)
);

CREATE TABLE sample (
    sample_id    	NUMBER(38)    	NOT NULL,
    patient_id    	NUMBER(38)    	NOT NULL,
    sample_barcode    	VARCHAR2(50)  	NOT NULL,
    uuid        	VARCHAR2(36)	NOT NULL,
    CONSTRAINT pk_sample_idx PRIMARY KEY (sample_id),
    CONSTRAINT uk_sample_uuid_idx UNIQUE (uuid)
);

CREATE TABLE sample_archive (
	sample_archive_id	NUMBER(38)	NOT NULL,
	sample_id		NUMBER(38)	NOT NULL,
	archive_id		NUMBER(38)	NOT NULL,
	CONSTRAINT pk_sample_archive_idx PRIMARY KEY(sample_archive_id)
);

CREATE TABLE sample_element (
    sample_element_id        	NUMBER(38)    	NOT NULL,
    sample_id            	NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id	NUMBER(38)    	NOT NULL,
    element_value            	VARCHAR2(4000)	,
    CONSTRAINT pk_sample_element_idx PRIMARY KEY (sample_element_id)
);

CREATE TABLE slide (
    slide_id    	NUMBER(38)    	NOT NULL,
    slide_barcode    	VARCHAR2(50)    NOT NULL,
    uuid        	VARCHAR2(36)    NOT NULL,
    portion_id		NUMBER(38)      NOT NULL,
    CONSTRAINT pk_slide_idx PRIMARY KEY (slide_id),
    CONSTRAINT uk_slide_uuid_idx UNIQUE (uuid)
);

CREATE TABLE slide_archive (
	slide_archive_id	NUMBER(38)	NOT NULL,
	slide_id		NUMBER(38)	NOT NULL,
	archive_id		NUMBER(38)	NOT NULL,
	CONSTRAINT pk_slide_archive_idx PRIMARY KEY(slide_archive_id)
);

CREATE TABLE slide_element (
    slide_element_id        	NUMBER(38)    	NOT NULL,
    slide_id            	NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id	NUMBER(38)    	NOT NULL,
    element_value            	VARCHAR2(4000)	,
    CONSTRAINT pk_slide_element_idx PRIMARY KEY (slide_element_id)
);

CREATE TABLE surgery (
    surgery_id        	NUMBER(38)    	NOT NULL,
    patient_id        	NUMBER(38)    	NOT NULL,
    surgery_barcode 	VARCHAR2(100)   NOT NULL,
    uuid            	VARCHAR2(36)    NOT NULL,
    CONSTRAINT pk_surgery_idx PRIMARY KEY (surgery_id),
    CONSTRAINT uk_surgery_uuid_idx UNIQUE (uuid)
);

CREATE TABLE surgery_archive (
	surgery_archive_id	NUMBER(38)	NOT NULL,
	surgery_id		NUMBER(38)	NOT NULL,
	archive_id		NUMBER(38)	NOT NULL,
	CONSTRAINT pk_surgery_archive_idx PRIMARY KEY(surgery_archive_id)
);

CREATE TABLE surgery_element (
    surgery_element_id        	NUMBER(38)    	NOT NULL,
    surgery_id            	NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id 	NUMBER(38)    	NOT NULL,
    element_value            	VARCHAR2(4000)	,
    CONSTRAINT pk_surgery_element_idx PRIMARY KEY (surgery_element_id)
);

CREATE TABLE tumorpathology (
    tumorpathology_id	NUMBER(38)    NOT NULL,
    sample_id        	NUMBER(38)    NOT NULL,
    CONSTRAINT pk_tumorpathology_idx PRIMARY KEY (tumorpathology_id)
);

CREATE TABLE tumorpathology_archive (
	tumorpathology_archive_id	NUMBER(38)	NOT NULL,
	tumorpathology_id		NUMBER(38)	NOT NULL,
	archive_id			NUMBER(38)	NOT NULL,
	CONSTRAINT pk_tumorpath_archive_idx PRIMARY KEY(tumorpathology_archive_id)
);

CREATE TABLE tumorpathology_element (
    tumorpathology_element_id	NUMBER(38)    	NOT NULL,
    tumorpathology_id        	NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id	NUMBER(38)    	NOT NULL,
    element_value            	VARCHAR2(4000)	,
    CONSTRAINT pk_tumorpath_element_idx PRIMARY KEY (tumorpathology_element_id)
);

--------------------------------------------------------
--  DDL for Table CLINICAL_FILE
--------------------------------------------------------

  CREATE TABLE CLINICAL_FILE 
   (	CLINICAL_FILE_ID 	NUMBER(38,0)	NOT NULL, 
	FILENAME 		VARCHAR2(50)	NOT NULL, 
	BY_PATIENT 		NUMBER(1,0) 	DEFAULT 0 NOT NULL , 
	CONTEXT 		VARCHAR2(50),
	CONSTRAINT PK_CLINICAL_FILE PRIMARY KEY (CLINICAL_FILE_ID),
	CONSTRAINT UQ_CLINICAL_FILE_FILENAME UNIQUE (FILENAME)
   ) ;
--------------------------------------------------------
--  DDL for Table CLINICAL_FILE_ELEMENT
--------------------------------------------------------

  CREATE TABLE CLINICAL_FILE_ELEMENT 
   (	CLINICAL_FILE_ELEMENT_ID 	NUMBER(38,0)	NOT NULL, 
	XSD_ELEMENT_ID 			NUMBER(38,0), 
	TABLE_ID 			NUMBER(38,0)	NOT NULL, 
	TABLE_COLUMN_NAME 		VARCHAR2(50), 
	FILE_COLUMN_NAME 		VARCHAR2(50)	NOT NULL, 
	FILE_COLUMN_ORDER 		NUMBER(38,0)	NOT NULL, 
	CLINICAL_FILE_ID 		NUMBER(38,0), 
	DISEASE_ID 			NUMBER(38,0), 
	UNIT_COLUMN_NAME 		VARCHAR2(50),
	CONSTRAINT PK_CLINICAL_FILE_ELEMENT PRIMARY KEY (CLINICAL_FILE_ELEMENT_ID)
   ) ;

  CREATE TABLE CLINICAL_TABLE 
   (	CLINICAL_TABLE_ID 	NUMBER(38,0)	NOT NULL, 
	TABLE_NAME 		VARCHAR2(50)	NOT NULL, 
	JOIN_FOR_SAMPLE 	VARCHAR2(1000)	NOT NULL, 
	JOIN_FOR_PATIENT 	VARCHAR2(1000)	NOT NULL,
	BARCODE_ELEMENT_ID 	NUMBER(38,0),
	BARCODE_COLUMN_NAME 	VARCHAR2(100),
	ELEMENT_NODE_NAME 	VARCHAR2(100) 	NOT NULL,
	ELEMENT_TABLE_NAME 	VARCHAR2(100) 	NOT NULL,
	TABLE_ID_COLUMN_NAME 	VARCHAR2(100) 	NOT NULL,
	ARCHIVE_LINK_TABLE_NAME VARCHAR2(100),
	PARENT_TABLE_ID 	NUMBER(38, 0),
	CONSTRAINT PK_CLINICAL_TABLE PRIMARY KEY (CLINICAL_TABLE_ID),
	CONSTRAINT UQ_CLINICAL_TABLE_TABLE_NAME UNIQUE (TABLE_NAME)
   ) ;


  CREATE TABLE CLINICAL_XSD_ELEMENT 
   (	CLINICAL_XSD_ELEMENT_ID 	NUMBER(38,0)	NOT NULL, 
	ELEMENT_NAME 			VARCHAR2(100)	NOT NULL, 
	IS_PROTECTED 			NUMBER(1,0) 	DEFAULT 0 NOT NULL , 
	DESCRIPTION 			VARCHAR2(500), 
	VALUE_TYPE 			VARCHAR2(20),
	CONSTRAINT PK_CLINICAL_XSD_ELEMENT PRIMARY KEY (CLINICAL_XSD_ELEMENT_ID),
	CONSTRAINT UQ_CLINICAL_XSD_ELEMENT_NAME UNIQUE (ELEMENT_NAME)
   ) ;

CREATE TABLE CLINICAL_XSD_ENUM_VALUE 
   (	CLINICAL_XSD_ENUM_VALUE_ID 	NUMBER(38,0)	NOT NULL, 
	XSD_ELEMENT_ID 			NUMBER(38,0)	NOT NULL, 
	ENUM_VALUE 			VARCHAR2(100),
	CONSTRAINT PK_CLINICAL_XSD_ENUM_VALUE PRIMARY KEY (CLINICAL_XSD_ENUM_VALUE_ID),
	CONSTRAINT CLINICAL_XSD_ENUM_VAL_UK UNIQUE (XSD_ELEMENT_ID, ENUM_VALUE)
   ) ;

CREATE TABLE CLINICAL_FILE_TO_TABLE
(   CLINICAL_FILE_TABLE_ID	NUMBER(38,0)	NOT NULL,
    CLINICAL_FILE_ID		NUMBER(38,0)	NOT NULL,
    CLINICAL_TABLE_ID		NUMBER(38,0)	NOT NULL,
    CONSTRAINT PK_CLINICAL_FILE_TABLE_PK PRIMARY KEY (CLINICAL_FILE_TABLE_ID)
);
--------------------------------------------------------
--  DDL for Index ALIQUOT_ANALYTE_IDX
--------------------------------------------------------

  CREATE INDEX ALIQUOT_ANALYTE_IDX ON ALIQUOT (ANALYTE_ID) 
  ;
--------------------------------------------------------
--  DDL for Index ANALYTE_PORTION_IDX
--------------------------------------------------------

  CREATE INDEX ANALYTE_PORTION_IDX ON ANALYTE (PORTION_ID) 
  ;
--------------------------------------------------------
--  Ref Constraints for Table CLINICAL_FILE_ELEMENT
--------------------------------------------------------

  ALTER TABLE CLINICAL_FILE_ELEMENT ADD CONSTRAINT FK_CLINICAL_FILE_CLINICAL_FILE FOREIGN KEY (CLINICAL_FILE_ID)
	  REFERENCES CLINICAL_FILE (CLINICAL_FILE_ID) ENABLE;
 
  ALTER TABLE CLINICAL_FILE_ELEMENT ADD CONSTRAINT FK_CLINICAL_FILE_CLINICAL_TABL FOREIGN KEY (TABLE_ID)
	  REFERENCES CLINICAL_TABLE (CLINICAL_TABLE_ID) ENABLE;
 
  ALTER TABLE CLINICAL_FILE_ELEMENT ADD CONSTRAINT FK_CLINICAL_FILE_CLINICAL_XSD_ FOREIGN KEY (XSD_ELEMENT_ID)
	  REFERENCES CLINICAL_XSD_ELEMENT (CLINICAL_XSD_ELEMENT_ID) ENABLE;
 
  ALTER TABLE CLINICAL_FILE_ELEMENT ADD CONSTRAINT FK_CLINICAL_FILE_ELEM_DISEASE FOREIGN KEY (DISEASE_ID)
	  REFERENCES DISEASE (DISEASE_ID) ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table CLINICAL_XSD_ENUM_VALUE
--------------------------------------------------------

ALTER TABLE CLINICAL_FILE_TO_TABLE ADD (
CONSTRAINT fk_clinical_file_table
FOREIGN KEY (clinical_table_id) REFERENCES clinical_table(clinical_table_id),
CONSTRAINT fk_clinical_file_file
FOREIGN KEY (clinical_file_id) REFERENCES clinical_file(clinical_file_id));

ALTER TABLE CLINICAL_XSD_ENUM_VALUE ADD CONSTRAINT FK_CLIN_XSD_ENUM_XSD_ELEM FOREIGN KEY (XSD_ELEMENT_ID)
	  REFERENCES CLINICAL_XSD_ELEMENT (CLINICAL_XSD_ELEMENT_ID) ENABLE;

ALTER TABLE aliquot_archive ADD CONSTRAINT fk_aliquot_archive 
FOREIGN KEY (archive_id) REFERENCES archive_info(archive_id);

ALTER TABLE analyte_archive ADD CONSTRAINT fk_analyte_archive 
FOREIGN KEY (archive_id) REFERENCES archive_info(archive_id);

ALTER TABLE dna_archive ADD CONSTRAINT fk_dna_archive 
FOREIGN KEY (archive_id) REFERENCES archive_info(archive_id);

ALTER TABLE drug_intgen_archive ADD CONSTRAINT fk_drug_intgen_archive 
FOREIGN KEY (archive_id) REFERENCES archive_info(archive_id);

ALTER TABLE examination_archive ADD CONSTRAINT fk_examination_archive 
FOREIGN KEY (archive_id) REFERENCES archive_info(archive_id);

ALTER TABLE patient_archive ADD CONSTRAINT fk_patient_archive 
FOREIGN KEY (archive_id) REFERENCES archive_info(archive_id);

ALTER TABLE portion_archive ADD CONSTRAINT fk_portion_archive 
FOREIGN KEY (archive_id) REFERENCES archive_info(archive_id);

ALTER TABLE protocol_archive ADD CONSTRAINT fk_protocol_archive 
FOREIGN KEY (archive_id) REFERENCES archive_info(archive_id);

ALTER TABLE radiation_archive ADD CONSTRAINT fk_radiation_archive 
FOREIGN KEY (archive_id) REFERENCES archive_info(archive_id);

ALTER TABLE rna_archive ADD CONSTRAINT fk_rna_archive 
FOREIGN KEY (archive_id) REFERENCES archive_info(archive_id);

ALTER TABLE sample_archive ADD CONSTRAINT fk_sample_archive 
FOREIGN KEY (archive_id) REFERENCES archive_info(archive_id);

ALTER TABLE slide_archive ADD CONSTRAINT fk_slide_archive 
FOREIGN KEY (archive_id) REFERENCES archive_info(archive_id);

ALTER TABLE slide ADD CONSTRAINT fk_slide_portion 
FOREIGN KEY (portion_id) REFERENCES portion(portion_id);

ALTER TABLE surgery_archive ADD CONSTRAINT fk_surgery_archive 
FOREIGN KEY (archive_id) REFERENCES archive_info(archive_id);

ALTER TABLE tumorpathology_archive ADD CONSTRAINT fk_tumorpathology_archive 
FOREIGN KEY (archive_id) REFERENCES archive_info(archive_id);

ALTER TABLE aliquot_element ADD CONSTRAINT fk_aliquot_element 
FOREIGN KEY (clinical_xsd_element_id) REFERENCES clinical_xsd_element (clinical_xsd_element_id);

ALTER TABLE analyte_element ADD CONSTRAINT fk_analyte_element 
FOREIGN KEY (clinical_xsd_element_id) REFERENCES clinical_xsd_element (clinical_xsd_element_id);

ALTER TABLE dna_element ADD CONSTRAINT fk_dna_element 
FOREIGN KEY (clinical_xsd_element_id) REFERENCES clinical_xsd_element (clinical_xsd_element_id);

ALTER TABLE drug_intgen_element ADD CONSTRAINT fk_drug_intgen_elment 
FOREIGN KEY (clinical_xsd_element_id) REFERENCES clinical_xsd_element (clinical_xsd_element_id);

ALTER TABLE examination_element ADD CONSTRAINT fk_examination_element 
FOREIGN KEY (clinical_xsd_element_id) REFERENCES clinical_xsd_element (clinical_xsd_element_id);

ALTER TABLE patient_element ADD CONSTRAINT fk_patient_element 
FOREIGN KEY (clinical_xsd_element_id) REFERENCES clinical_xsd_element (clinical_xsd_element_id);

ALTER TABLE portion_element ADD CONSTRAINT fk_portion_element 
FOREIGN KEY (clinical_xsd_element_id) REFERENCES clinical_xsd_element (clinical_xsd_element_id);

ALTER TABLE protocol_element ADD CONSTRAINT fk_protocol_element 
FOREIGN KEY (clinical_xsd_element_id) REFERENCES clinical_xsd_element (clinical_xsd_element_id);

ALTER TABLE radiation_element ADD CONSTRAINT fk_radiation_element 
FOREIGN KEY (clinical_xsd_element_id) REFERENCES clinical_xsd_element (clinical_xsd_element_id);

ALTER TABLE rna_element ADD CONSTRAINT fk_rna_element 
FOREIGN KEY (clinical_xsd_element_id) REFERENCES clinical_xsd_element (clinical_xsd_element_id);

ALTER TABLE sample_element ADD CONSTRAINT fk_sample_element 
FOREIGN KEY (clinical_xsd_element_id) REFERENCES clinical_xsd_element (clinical_xsd_element_id);

ALTER TABLE slide_element ADD CONSTRAINT fk_slide_element 
FOREIGN KEY (clinical_xsd_element_id) REFERENCES clinical_xsd_element (clinical_xsd_element_id);

ALTER TABLE surgery_element ADD CONSTRAINT fk_surgery_element 
FOREIGN KEY (clinical_xsd_element_id) REFERENCES clinical_xsd_element (clinical_xsd_element_id);

ALTER TABLE tumorpathology_element ADD CONSTRAINT fk_tumorpathology_element
FOREIGN KEY (clinical_xsd_element_id) REFERENCES clinical_xsd_element (clinical_xsd_element_id);


ALTER TABLE clinical_table ADD CONSTRAINT fk_clinical_table_table 
FOREIGN KEY (parent_table_id) REFERENCES clinical_table(clinical_table_id);

ALTER TABLE clinical_table ADD CONSTRAINT fk_clinical_table_barcode 
FOREIGN KEY (barcode_element_id) REFERENCES clinical_xsd_element(clinical_xsd_element_id);

ALTER TABLE SURGERY ADD CONSTRAINT FK_SURGERY_PATIENTID FOREIGN KEY (PATIENT_ID)
	  REFERENCES PATIENT (PATIENT_ID) ENABLE;

ALTER TABLE TUMORPATHOLOGY ADD CONSTRAINT FK_TUMORPATH_SAMPLE FOREIGN KEY (SAMPLE_ID)
	  REFERENCES SAMPLE (SAMPLE_ID) ENABLE;
	  
ALTER TABLE SAMPLE ADD CONSTRAINT FK_SAMPLE_PATIENTID FOREIGN KEY (PATIENT_ID)
	REFERENCES PATIENT (PATIENT_ID) ENABLE;
ALTER TABLE PROTOCOL ADD CONSTRAINT FK_PROTOCOL_ANALYTE FOREIGN KEY (ANALYTE_ID)
	  REFERENCES ANALYTE (ANALYTE_ID) ENABLE;

ALTER TABLE RADIATION ADD CONSTRAINT FK_RADIATION_PATIENTID FOREIGN KEY (PATIENT_ID)
	  REFERENCES PATIENT (PATIENT_ID) ENABLE;

ALTER TABLE RNA ADD CONSTRAINT FK_RNA_ANALYTE FOREIGN KEY (ANALYTE_ID)
	  REFERENCES ANALYTE (ANALYTE_ID) ENABLE;

ALTER TABLE DNA ADD CONSTRAINT FK_DNA_ANALYTE FOREIGN KEY (ANALYTE_ID)
	  REFERENCES ANALYTE (ANALYTE_ID) ENABLE;


ALTER TABLE DRUG_INTGEN ADD CONSTRAINT FK_DRUGINTGEN_PATIENTID FOREIGN KEY (PATIENT_ID)
	  REFERENCES PATIENT (PATIENT_ID) ENABLE;

ALTER TABLE EXAMINATION ADD CONSTRAINT FK_EXAMINATION_PATIENT FOREIGN KEY (PATIENT_ID)
	  REFERENCES PATIENT (PATIENT_ID) ENABLE;

ALTER TABLE ALIQUOT ADD CONSTRAINT FK_ALIQUOT_ANALYTE FOREIGN KEY (ANALYTE_ID)
	  REFERENCES ANALYTE (ANALYTE_ID) ENABLE;

ALTER TABLE ANALYTE ADD CONSTRAINT FK_ANALYTE_PORTION FOREIGN KEY (PORTION_ID)
	  REFERENCES PORTION (PORTION_ID) ENABLE;
	  


CREATE SEQUENCE clinical_seq start with 1 increment by 1;

-- drop these sequences, replace by clinical_seq
  DROP SEQUENCE PROTOCOL_PROTOCOL_ID_SEQ;
  DROP SEQUENCE RADIATION_RADIATION_ID_SEQ;
  DROP SEQUENCE RNA_RNAID_SEQ;
  DROP SEQUENCE SAMPLE_SAMPLE_ID_SEQ;
  DROP SEQUENCE SLIDE_SLIDE_ID_SEQ;
  DROP SEQUENCE SURGERY_SURGERY_ID_SEQ;
  DROP SEQUENCE TUMORPATHOLOG_TUMORPATHOLO_SEQ;
  DROP SEQUENCE PATIENT_PATIENT_ID_SEQ;
  DROP SEQUENCE DNA_DNAID_SEQ;
  DROP SEQUENCE DRUG_INTGEN_DRUG_ID_SEQ;
  DROP SEQUENCE EXAMINATION_EXAMINATION_ID_SEQ;
  DROP SEQUENCE ALIQUOT_ALIQUOT_ID_SEQ;
  DROP SEQUENCE ANALYTE_ANALYTE_ID_SEQ;
  DROP SEQUENCE PORTION_PORTION_ID_SEQ;
  DROP SEQUENCE PORTION_SLIDE_PS_ID_SEQ;


purge recyclebin;