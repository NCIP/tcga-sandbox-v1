--------------------------------------------------------
--  DDL to create brand new disease-specific schema on dev 
--------------------------------------------------------
  DROP TABLE ALIQUOT cascade constraints;
  DROP TABLE ALIQUOT_ELEMENT CASCADE constraints;
  DROP TABLE ALIQUOT_ARCHIVE CASCADE constraints;
  DROP TABLE ANALYTE cascade constraints;
  DROP TABLE ANALYTE_ARCHIVE CASCADE constraints;
  DROP TABLE ANALYTE_ELEMENT CASCADE constraints;
  DROP TABLE ANOMALY_TYPE cascade constraints;
  DROP TABLE ARCHIVE_INFO cascade constraints;
  DROP TABLE ARCHIVE_TYPE cascade constraints;
  DROP TABLE BCR_BIOSPECIMEN_TO_ARCHIVE cascade constraints;
  DROP TABLE BIOCARTA_GENE cascade constraints;
  DROP TABLE BIOCARTA_GENE_PATHWAY cascade constraints;
  DROP TABLE BIOSPECIMEN_BARCODE cascade constraints;
  DROP TABLE BIOSPECIMEN_TO_FILE cascade constraints;
  DROP TABLE CENTER cascade constraints;
  DROP TABLE CENTER_TO_BCR_CENTER cascade constraints;
  DROP TABLE CENTER_TYPE cascade constraints;
  DROP TABLE CLINICAL_FILE_TO_TABLE  cascade constraints;
  DROP TABLE CLINICAL_FILE cascade constraints;
  DROP TABLE CLINICAL_FILE_ELEMENT cascade constraints;
  DROP TABLE CLINICAL_TABLE cascade constraints;
  DROP TABLE CLINICAL_XSD_ELEMENT cascade constraints;
  DROP TABLE CLINICAL_XSD_ENUM_VALUE cascade constraints;
  DROP TABLE CNA_VALUE cascade constraints;
  DROP TABLE DATA_LEVEL cascade constraints;
  DROP TABLE DATA_SET cascade constraints;
  DROP TABLE DATA_SET_FILE cascade constraints;
  DROP TABLE DATA_TYPE cascade constraints;
  DROP TABLE DATA_TYPE_TO_PLATFORM cascade constraints;
  DROP TABLE DATA_VISIBILITY cascade constraints;
  DROP TABLE DELETE_OLD_DATA cascade constraints;
  DROP TABLE DISEASE cascade constraints;
  DROP TABLE DNA cascade constraints;
  DROP TABLE DNA_ELEMENT CASCADE constraints;
  DROP TABLE DNA_ARCHIVE CASCADE constraints;
  DROP TABLE DRUG cascade constraints;
  DROP TABLE DRUG_CONCEPT_CODE cascade constraints;
  DROP TABLE DRUG_INTGEN cascade constraints;
  DROP TABLE DRUG_INTGEN_ELEMENT CASCADE constraints;
  DROP TABLE DRUG_INTGEN_ARCHIVE CASCADE constraints;
  DROP TABLE EXAMINATION cascade constraints;
  DROP TABLE EXAMINATION_ARCHIVE CASCADE constraints;
  DROP TABLE EXAMINATION_ELEMENT CASCADE constraints;
  DROP TABLE EXPERIMENT cascade constraints;
  DROP TABLE EXPGENE_VALUE cascade constraints;
  DROP TABLE FILE_INFO cascade constraints;
  DROP TABLE FILE_TO_ARCHIVE cascade constraints;
  DROP TABLE GENE cascade constraints;
  DROP TABLE GENE_DRUG cascade constraints;
  DROP TABLE HYBRIDIZATION_REF cascade constraints;
  DROP TABLE HYBRID_REF_DATA_SET cascade constraints;
  DROP TABLE L4_ANOMALY_DATA_SET cascade constraints;
  DROP TABLE L4_ANOMALY_DATA_SET_VERSION cascade constraints;
  DROP TABLE L4_ANOMALY_DATA_VERSION cascade constraints;
  DROP TABLE L4_ANOMALY_TYPE cascade constraints;
  DROP TABLE L4_ANOMALY_VALUE cascade constraints;
  DROP TABLE L4_CORRELATION_TYPE cascade constraints;
  DROP TABLE L4_DATA_SET_GENETIC_ELEMENT cascade constraints;
  DROP TABLE L4_DATA_SET_SAMPLE cascade constraints;
  DROP TABLE L4_GENETIC_ELEMENT cascade constraints;
  DROP TABLE L4_GENETIC_ELEMENT_TYPE cascade constraints;
  DROP TABLE L4_PATIENT cascade constraints;
  DROP TABLE L4_SAMPLE cascade constraints;
  DROP TABLE L4_TARGET cascade constraints;
  DROP TABLE MAF_INFO cascade constraints;
  DROP TABLE METHYLATION_VALUE cascade constraints;
  DROP TABLE PATHWAY cascade constraints;
  DROP TABLE PATIENT cascade constraints;
  DROP TABLE PATIENT_ARCHIVE CASCADE constraints;
  DROP TABLE PATIENT_ELEMENT CASCADE constraints;
  DROP TABLE PLATFORM cascade constraints;
  DROP TABLE PORTAL_ACTION_TYPE cascade constraints;
  DROP TABLE PORTAL_SESSION cascade constraints;
  DROP TABLE PORTAL_SESSION_ACTION cascade constraints;
  DROP TABLE PORTION cascade constraints;
  DROP TABLE PORTION_ELEMENT CASCADE constraints;
  DROP TABLE PORTION_ARCHIVE CASCADE constraints;
  DROP TABLE PROTOCOL cascade constraints;
  DROP TABLE PROTOCOL_ELEMENT CASCADE constraints;
  DROP TABLE PROTOCOL_ARCHIVE CASCADE constraints;
  DROP TABLE RADIATION cascade constraints;
  DROP TABLE RADIATION_ELEMENT CASCADE constraints;
  DROP TABLE RADIATION_ARCHIVE CASCADE constraints;
  DROP TABLE RNA cascade constraints;
  DROP TABLE RNA_ELEMENT CASCADE constraints;
  DROP TABLE RNA_ARCHIVE CASCADE constraints;
  DROP TABLE SAMPLE cascade constraints;
  DROP TABLE SAMPLE_ELEMENT CASCADE constraints;
  DROP TABLE SAMPLE_ARCHIVE CASCADE constraints;
  DROP TABLE SLIDE cascade constraints;
  DROP TABLE SLIDE_ELEMENT CASCADE constraints;
  DROP TABLE SLIDE_ARCHIVE CASCADE constraints;
  DROP TABLE SUMMARY_BY_GENE cascade constraints;
  DROP TABLE SURGERY cascade constraints;
  DROP TABLE SURGERY_ELEMENT CASCADE constraints;
  DROP TABLE SURGERY_ARCHIVE CASCADE constraints;
  DROP TABLE TUMORPATHOLOGY cascade constraints;
  DROP TABLE TUMORPATHOLOGY_ELEMENT CASCADE constraints;
  DROP TABLE TUMORPATHOLOGY_ARCHIVE CASCADE constraints;
  DROP TABLE VISIBILITY cascade constraints;
  DROP SEQUENCE CENTER_CENTER_ID_SEQ;
  DROP SEQUENCE clinical_seq;
  DROP SEQUENCE CLINICAL_FILE_ELEMENT_SEQ;
  DROP SEQUENCE clinical_xsd_seq;
  DROP SEQUENCE CLINICAL_XSD_ENUM_VALUE_SEQ;
  DROP SEQUENCE CNA_VALUE_SEQ;
  DROP SEQUENCE DATA_SET_DATA_SET_ID_SEQ;
  DROP SEQUENCE DATA_SET_FILE_SEQ;
  DROP SEQUENCE DATA_TYPE_DATA_TYPE_ID_SEQ;
  DROP SEQUENCE EXPERIMENT_EXPERIMENT_ID_SEQ;
  DROP SEQUENCE EXPGENE_VALUE_SEQ;
  DROP SEQUENCE HYBREF_DATASET_SEQ;
  DROP SEQUENCE HYBREF_HYBRIDIZATION_SEQ;
  DROP SEQUENCE L4_ANOMALY_DATA_SET_ID_SEQ;
  DROP SEQUENCE L4_ANOMALY_DATA_VERSION_ID_SEQ;
  DROP SEQUENCE L4_ANOMALY_DSV_ID_SEQ;
  DROP SEQUENCE L4_ANOMALY_TYPE_SEQ;
  DROP SEQUENCE L4_ANOMALY_VALUE_ID_SEQ;
  DROP SEQUENCE L4_DATA_SET_GE_ID_SEQ;
  DROP SEQUENCE L4_DATA_SET_SAMPLE_ID_SEQ;
  DROP SEQUENCE L4_GENETIC_ELEMENT_ID_SEQ;
  DROP SEQUENCE L4_PATIENT_ID_SEQ;
  DROP SEQUENCE L4_SAMPLE_ID_SEQ;
  DROP SEQUENCE L4_TARGET_SEQ;
  DROP SEQUENCE maf_info_seq;
  DROP SEQUENCE METHYLATION_VALUE_SEQ;
  DROP SEQUENCE PLATFORM_PLATFORM_ID_SEQ;
  DROP SEQUENCE PORTAL_SESSION_ACTION_ID_SEQ;
  DROP SEQUENCE PORTAL_SESSION_ID_SEQ;
  DROP SEQUENCE rnaseq_value_seq;
  DROP SEQUENCE mirnaseq_value_seq;
  DROP SEQUENCE proteinexp_value_seq.
  
CREATE SEQUENCE rnaseq_value_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE mirnaseq_value_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE proteinexp_value_seq START WITH 1 INCREMENT BY 1;
--------------------------------------------------------
--  DDL for Sequence CENTER_CENTER_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  CENTER_CENTER_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 25 NOCACHE  NOORDER  NOCYCLE ;

--------------------------------------------------------
--  DDL for Sequence CLINICAL_SEQ used for all clinical tables
--------------------------------------------------------
  CREATE SEQUENCE clinical_seq start with 1 increment by 1;
--------------------------------------------------------
--  DDL for Sequence CLINICAL_FILE_ELEMENT_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  CLINICAL_FILE_ELEMENT_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence CLINICAL_XSD_ENUM_VALUE_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  CLINICAL_XSD_ENUM_VALUE_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 341 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence CLINICAL_XSD_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  CLINICAL_XSD_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 341 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence CNA_VALUE_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  CNA_VALUE_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence DATA_SET_DATA_SET_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  DATA_SET_DATA_SET_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence DATA_SET_FILE_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  DATA_SET_FILE_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence DATA_TYPE_DATA_TYPE_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  DATA_TYPE_DATA_TYPE_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 20 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence EXPERIMENT_EXPERIMENT_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  EXPERIMENT_EXPERIMENT_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence EXPGENE_VALUE_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  EXPGENE_VALUE_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence HYBREF_DATASET_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  HYBREF_DATASET_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence HYBREF_HYBRIDIZATION_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  HYBREF_HYBRIDIZATION_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence L4_ANOMALY_DATA_SET_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  L4_ANOMALY_DATA_SET_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence L4_ANOMALY_DATA_VERSION_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  L4_ANOMALY_DATA_VERSION_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1  NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence L4_ANOMALY_DSV_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  L4_ANOMALY_DSV_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence L4_ANOMALY_TYPE_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  L4_ANOMALY_TYPE_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence L4_ANOMALY_VALUE_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  L4_ANOMALY_VALUE_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence L4_DATA_SET_GE_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  L4_DATA_SET_GE_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence L4_DATA_SET_SAMPLE_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  L4_DATA_SET_SAMPLE_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence L4_GENETIC_ELEMENT_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  L4_GENETIC_ELEMENT_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence L4_PATIENT_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  L4_PATIENT_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence L4_SAMPLE_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  L4_SAMPLE_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence L4_TARGET_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  L4_TARGET_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence MAF_INFO_SEQ
--------------------------------------------------------

   CREATE SEQUENCE maf_info_seq	START WITH 1 INCREMENT BY 1;
--------------------------------------------------------
--  DDL for Sequence METHYLATION_VALUE_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  METHYLATION_VALUE_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence PLATFORM_PLATFORM_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  PLATFORM_PLATFORM_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 30 NOCACHE  NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence PORTAL_SESSION_ACTION_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  PORTAL_SESSION_ACTION_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Sequence PORTAL_SESSION_ID_SEQ
--------------------------------------------------------

   CREATE SEQUENCE  PORTAL_SESSION_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;
--------------------------------------------------------
--  DDL for Table ANOMALY_TYPE
--------------------------------------------------------

  CREATE TABLE ANOMALY_TYPE 
   (	ANOMALY_TYPE_ID 	NUMBER(38,0)	NOT NULL, 
	ANOMALY 		VARCHAR2(255)	NOT NULL, 
	ANOMALY_DESCRIPTION 	VARCHAR2(255)	NOT NULL, 
	VALUE_THRESHOLD 	FLOAT(126)	NOT NULL, 
	VALUE_THRESHOLD_TYPE 	VARCHAR2(3)	NOT NULL, 
	DISPLAY_NAME 		VARCHAR2(10)	NOT NULL, 
	PATIENT_THRESHOLD 	FLOAT(126), 
	COMMENTS 		VARCHAR2(1000),
	CONSTRAINT ANOMALY_TYPE_PK PRIMARY KEY (ANOMALY_TYPE_ID),
	CONSTRAINT ANOMALY_TYPE_DISPLAY_UK UNIQUE (DISPLAY_NAME),
	CONSTRAINT ANOMALY_TYPE_ANOMALY_UK UNIQUE (ANOMALY),
	CONSTRAINT ANOMALY_TYPE_THREHOLD_TYPE_CC CHECK (value_threshold_type IN ('GT', 'GTE', 'LT', 'LTE', 'E', 'NE'))
   ) ;

--------------------------------------------------------
--  Constraints for Table ANOMALY_TYPE
--------------------------------------------------------

   COMMENT ON COLUMN ANOMALY_TYPE.ANOMALY_TYPE_ID IS 'An internal, information-free integer used as a Primary Key';
 
   COMMENT ON COLUMN ANOMALY_TYPE.ANOMALY IS 'The name of the anomaly';
 
   COMMENT ON COLUMN ANOMALY_TYPE.ANOMALY_DESCRIPTION IS 'A breif description of the anomaly';
 
   COMMENT ON COLUMN ANOMALY_TYPE.VALUE_THRESHOLD IS 'The cutoff provided by CBIIT that was applied to the data to determine if the anomolgy exists in a certain gene.';
 
   COMMENT ON COLUMN ANOMALY_TYPE.VALUE_THRESHOLD_TYPE IS 'The comparision operator for the value_cutoff.  GT: greater-than  GTE: greater-than-or-equal-to  LT: less-than  LTE: less-than-or-equal-to  E: equals  NE: not-equal-to';
 
   COMMENT ON COLUMN ANOMALY_TYPE.DISPLAY_NAME IS 'A short name for GUI displays if necessary';
 
   COMMENT ON COLUMN ANOMALY_TYPE.PATIENT_THRESHOLD IS 'The suggested threshold to apply to summary_by_gene.cases_detected/summary_by_gene.cases_probed.  It is assumed to be greater-than-or-equal-to this threshold.';
 
   COMMENT ON COLUMN ANOMALY_TYPE.COMMENTS IS 'An area to store official comments about any caveats with the data.';
 
   COMMENT ON TABLE ANOMALY_TYPE  IS 'Stores meta-data on each type of anomaly';
--------------------------------------------------------
--  DDL for Table ARCHIVE_INFO
--------------------------------------------------------

  CREATE TABLE ARCHIVE_INFO 
   (	ARCHIVE_ID 			NUMBER(38,0)	NOT NULL, 
	ARCHIVE_NAME 			VARCHAR2(2000)	NOT NULL, 
	ARCHIVE_TYPE_ID 		NUMBER(38,0), 
	CENTER_ID 			NUMBER(38,0)	NOT NULL, 
	DISEASE_ID 			NUMBER(38,0)	NOT NULL, 
	PLATFORM_ID 			NUMBER(38,0)	NOT NULL, 
	SERIAL_INDEX 			NUMBER(10,0)	NOT NULL, 
	REVISION 			NUMBER(10,0)	NOT NULL, 
	SERIES 				NUMBER(10,0)	NOT NULL, 
	DATE_ADDED 			TIMESTAMP	NOT NULL, 
	DEPLOY_STATUS 			VARCHAR2(25)	NOT NULL, 
	DEPLOY_LOCATION 		VARCHAR2(2000), 
	SECONDARY_DEPLOY_LOCATION 	VARCHAR2(2000),
	IS_LATEST 			NUMBER(1,0) 	DEFAULT 1 NOT NULL , 
	INITIAL_SIZE_KB 		NUMBER(38,0), 
	FINAL_SIZE_KB 			NUMBER(38,0), 
	IS_LATEST_LOADED 		NUMBER(1,0),
	DATA_LOADED_DATE		DATE,
	CONSTRAINT ARCHIVE_INFO_PK_IDX PRIMARY KEY (ARCHIVE_ID),
	CONSTRAINT ARCHIVE_INFO_UK_NAME UNIQUE (ARCHIVE_NAME)	
   ) ;
--------------------------------------------------------
--  DDL for Table ARCHIVE_TYPE
--------------------------------------------------------

  CREATE TABLE ARCHIVE_TYPE 
   (	ARCHIVE_TYPE_ID 	NUMBER(38,0)	NOT NULL, 
	ARCHIVE_TYPE 		VARCHAR2(50)	NOT NULL, 
	DATA_LEVEL 		INTEGER,
	CONSTRAINT ARCHIVE_TYPE_PK_IDX PRIMARY KEY (ARCHIVE_TYPE_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table BCR_BIOSPECIMEN_TO_ARCHIVE
--------------------------------------------------------

  CREATE TABLE BCR_BIOSPECIMEN_TO_ARCHIVE 
   (	BIOSPECIMEN_ARCHIVE_ID 	NUMBER(38,0)	NOT NULL, 
	BIOSPECIMEN_ID 		NUMBER(38,0)	NOT NULL, 
	ARCHIVE_ID 		NUMBER(38,0)	NOT NULL,
	CONSTRAINT BIOSPECIMEN_TO_ARCHIVE_PK_IDX PRIMARY KEY (BIOSPECIMEN_ARCHIVE_ID),
	CONSTRAINT BIOSPECIMEN_TO_ARCHIVE_UK_IDX UNIQUE (BIOSPECIMEN_ID, ARCHIVE_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table BIOCARTA_GENE
--------------------------------------------------------

  CREATE TABLE BIOCARTA_GENE 
   (	BIOCARTA_GENE_ID 	NUMBER(38,0)	NOT NULL, 
	GENE_ID 		NUMBER(38,0)	NOT NULL, 
	BIOCARTA_SYMBOL 	VARCHAR2(50)	NOT NULL, 
	COMMENTS 		VARCHAR2(1000),
	CONSTRAINT BIOCARTA_GENE_PK PRIMARY KEY (BIOCARTA_GENE_ID),
	CONSTRAINT BIOCARTA_GENE_BCSYM_UK UNIQUE (BIOCARTA_SYMBOL)
   ) ;
 

   COMMENT ON COLUMN BIOCARTA_GENE.BIOCARTA_GENE_ID IS 'An internal, information-free integer used as a Primary Key';
 
   COMMENT ON COLUMN BIOCARTA_GENE.GENE_ID IS 'Foreign Key.';
 
   COMMENT ON COLUMN BIOCARTA_GENE.BIOCARTA_SYMBOL IS 'The symbol BioCarta uses to link genes to pathways. Alternate Key.';
 
   COMMENT ON COLUMN BIOCARTA_GENE.COMMENTS IS 'An area to store official comments about any caveats with the data.';
 
   COMMENT ON TABLE BIOCARTA_GENE  IS 'Stores relevant biocarta gene information';
--------------------------------------------------------
--  DDL for Table BIOCARTA_GENE_PATHWAY
--------------------------------------------------------

  CREATE TABLE BIOCARTA_GENE_PATHWAY 
   (	BIOCARTA_GENE_ID 	NUMBER(38,0)	NOT NULL, 
	PATHWAY_ID 		NUMBER(38,0)	NOT NULL,
	CONSTRAINT GENE_PATHWAY_PK PRIMARY KEY (BIOCARTA_GENE_ID, PATHWAY_ID)
   ) ;
 

   COMMENT ON COLUMN BIOCARTA_GENE_PATHWAY.BIOCARTA_GENE_ID IS 'Foreign Key.';
 
   COMMENT ON COLUMN BIOCARTA_GENE_PATHWAY.PATHWAY_ID IS 'Foreign Key.';
 
   COMMENT ON TABLE BIOCARTA_GENE_PATHWAY  IS 'Relates all BioCarta genes to their pathways.  This is a many-to-many relationship';
--------------------------------------------------------
--  DDL for Table BIOSPECIMEN_BARCODE
--------------------------------------------------------

  CREATE TABLE BIOSPECIMEN_BARCODE 
   (	BIOSPECIMEN_ID 		NUMBER(38,0)	NOT NULL, 
	BARCODE 		VARCHAR2(100)	NOT NULL, 
	PROJECT_CODE 		VARCHAR2(10)	NOT NULL, 
	TSS_CODE 		VARCHAR2(10)	NOT NULL, 
	PATIENT 		VARCHAR2(10)	NOT NULL, 
	SAMPLE_TYPE_CODE 	VARCHAR2(10)	NOT NULL, 
	SAMPLE_SEQUENCE 	VARCHAR2(10)	NOT NULL, 
	PORTION_SEQUENCE 	VARCHAR2(10)	NOT NULL, 
	PORTION_ANALYTE_CODE 	VARCHAR2(10)	NOT NULL, 
	PLATE_ID 		VARCHAR2(10)	NOT NULL, 
	BCR_CENTER_ID 		VARCHAR2(10)	NOT NULL, 
	IS_VALID 		NUMBER(1,0) DEFAULT 1, 
	IS_VIEWABLE 		NUMBER(1,0) DEFAULT 1, 
	SHIP_DATE 		DATE, 
	UUID 			VARCHAR2(36),
	CONSTRAINT BIOSPECIMEN_BARCODE_PK_IDX PRIMARY KEY (BIOSPECIMEN_ID),
	CONSTRAINT BIOSPECIMEN_BARCODE_UK_BCIDX UNIQUE (BARCODE),
	CONSTRAINT BIOSPECIMEN_BARCODE_UK_UUID UNIQUE (UUID)
   ) ;
--------------------------------------------------------
--  DDL for Table BIOSPECIMEN_TO_FILE
--------------------------------------------------------

  CREATE TABLE BIOSPECIMEN_TO_FILE 
   (	BIOSPECIMEN_FILE_ID 	NUMBER(38,0)	NOT NULL, 
	BIOSPECIMEN_ID 		NUMBER(38,0)	NOT NULL, 
	FILE_ID 		NUMBER(38,0)	NOT NULL, 
	FILE_COL_NAME 		VARCHAR2(100),
	CONSTRAINT BIOSPECIMEN_TO_FILE_PK_IDX PRIMARY KEY (BIOSPECIMEN_FILE_ID),
	CONSTRAINT BIOSPECIMEN_TO_FILE_UK_IDX UNIQUE (BIOSPECIMEN_ID, FILE_ID, FILE_COL_NAME)
   ) ;
--------------------------------------------------------
--  DDL for Table CENTER
--------------------------------------------------------

  CREATE TABLE CENTER 
   (	CENTER_ID 		NUMBER(38,0)	NOT NULL, 
	DOMAIN_NAME 		VARCHAR2(50)	NOT NULL, 
	CENTER_TYPE_CODE 	VARCHAR2(10), 
	DISPLAY_NAME 		VARCHAR2(200), 
	SHORT_NAME 		VARCHAR2(50), 
	SORT_ORDER 		NUMBER(10,0),
    requires_magetab      NUMBER(1) DEFAULT 0 NOT NULL,
	CONSTRAINT PK_CENTER PRIMARY KEY (CENTER_ID),
	CONSTRAINT CENTER_UK_IDX UNIQUE (DOMAIN_NAME, CENTER_TYPE_CODE)
   ) ;
 

   COMMENT ON COLUMN CENTER.CENTER_ID IS 'Internal ID.';
 
   COMMENT ON COLUMN CENTER.DOMAIN_NAME IS 'Official domain name from the DCC.';
--------------------------------------------------------
--  DDL for Table CENTER_TO_BCR_CENTER
--------------------------------------------------------

  CREATE TABLE CENTER_TO_BCR_CENTER 
   (	BCR_CENTER_ID 		VARCHAR2(10)	NOT NULL, 
	CENTER_ID 		NUMBER(38,0)	NOT NULL, 
	CENTER_TYPE_CODE 	VARCHAR2(25)	NOT NULL,
	CONSTRAINT CENTER_BCR_CENTER_MAP_PK PRIMARY KEY (BCR_CENTER_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table CENTER_TYPE
--------------------------------------------------------

  CREATE TABLE CENTER_TYPE 
   (	CENTER_TYPE_CODE 	VARCHAR2(10)	NOT NULL, 
	CENTER_TYPE_DEFINITION 	VARCHAR2(200)	NOT NULL,
	CONSTRAINT CENTER_TYPE_PK_IDX PRIMARY KEY (CENTER_TYPE_CODE)
   ) ;
--------------------------------------------------------
--  DDL for Table CLINICAL_FILE
--------------------------------------------------------

  CREATE TABLE CLINICAL_FILE 
   (	CLINICAL_FILE_ID 	NUMBER(38,0)	NOT NULL, 
	FILENAME 		VARCHAR2(50)	NOT NULL, 
	BY_PATIENT 		NUMBER(1,0) 	DEFAULT 0 NOT NULL , 
	CONTEXT 		VARCHAR2(50),
	is_dynamic 		NUMBER(1) 	DEFAULT 0  NOT NULL,
	clinical_table_id 	NUMBER(38),
	CONSTRAINT PK_CLINICAL_FILE PRIMARY KEY (CLINICAL_FILE_ID),
	CONSTRAINT UQ_CLINICAL_FILE_FILENAME UNIQUE (FILENAME)
   ) ;

CREATE TABLE CLINICAL_FILE_TO_TABLE
(   CLINICAL_FILE_TABLE_ID	NUMBER(38,0)	NOT NULL,
    CLINICAL_FILE_ID		NUMBER(38,0)	NOT NULL,
    CLINICAL_TABLE_ID		NUMBER(38,0)	NOT NULL,
    CONSTRAINT PK_CLINICAL_FILE_TABLE_PK PRIMARY KEY (CLINICAL_FILE_TABLE_ID)
);

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
--------------------------------------------------------
--  DDL for Table CLINICAL_TABLE
--------------------------------------------------------

  CREATE TABLE CLINICAL_TABLE 
   (	CLINICAL_TABLE_ID 		NUMBER(38,0)	NOT NULL, 
	TABLE_NAME 			VARCHAR2(50)	NOT NULL, 
	JOIN_FOR_SAMPLE 		VARCHAR2(1000)	NOT NULL, 
	JOIN_FOR_PATIENT 		VARCHAR2(1000)	NOT NULL,
	BARCODE_ELEMENT_ID 		NUMBER(38,0),
	BARCODE_COLUMN_NAME 		VARCHAR2(100),
	ELEMENT_NODE_NAME 		VARCHAR2(100) 	NOT NULL,
	ELEMENT_TABLE_NAME 		VARCHAR2(100) 	NOT NULL,
	TABLE_ID_COLUMN_NAME 		VARCHAR2(100) 	NOT NULL,
	PARENT_TABLE_ID 		NUMBER(38, 0),
	ARCHIVE_LINK_TABLE_NAME 	VARCHAR2(100),
	UUID_ELEMENT_ID 		NUMBER(38,0),
	IS_DYNAMIC 			NUMBER(1) DEFAULT 0 NOT NULL, 
	DYNAMIC_IDENTIFIER_COLUMN_NAME	VARCHAR2(100),
	CONSTRAINT PK_CLINICAL_TABLE PRIMARY KEY (CLINICAL_TABLE_ID),
	CONSTRAINT UQ_CLINICAL_TABLE_TABLE_NAME UNIQUE (TABLE_NAME)
   ) ;
--------------------------------------------------------
--  DDL for Table CLINICAL_XSD_ELEMENT
--------------------------------------------------------

  CREATE TABLE CLINICAL_XSD_ELEMENT 
   (	CLINICAL_XSD_ELEMENT_ID 	NUMBER(38,0)	NOT NULL, 
	ELEMENT_NAME 			VARCHAR2(200)	NOT NULL, 
	IS_PROTECTED 			NUMBER(1,0) 	DEFAULT 0 NOT NULL , 
	DESCRIPTION 			VARCHAR2(500), 
	VALUE_TYPE 			VARCHAR2(20),
	EXPECTED_ELEMENT 		CHAR(1)   	DEFAULT 'Y' NOT NULL,
	CONSTRAINT PK_CLINICAL_XSD_ELEMENT PRIMARY KEY (CLINICAL_XSD_ELEMENT_ID),
	CONSTRAINT UQ_CLINICAL_XSD_ELEMENT_NAME UNIQUE (ELEMENT_NAME)
   ) ;
--------------------------------------------------------
--  DDL for Table CLINICAL_XSD_ENUM_VALUE
--------------------------------------------------------

  CREATE TABLE CLINICAL_XSD_ENUM_VALUE 
   (	CLINICAL_XSD_ENUM_VALUE_ID 	NUMBER(38,0)	NOT NULL, 
	XSD_ELEMENT_ID 			NUMBER(38,0)	NOT NULL, 
	ENUM_VALUE 			VARCHAR2(100),
	CONSTRAINT PK_CLINICAL_XSD_ENUM_VALUE PRIMARY KEY (CLINICAL_XSD_ENUM_VALUE_ID),
	CONSTRAINT CLINICAL_XSD_ENUM_VAL_UK UNIQUE (XSD_ELEMENT_ID, ENUM_VALUE)
   ) ;
--------------------------------------------------------
--  DDL for All Clinical Tables
--------------------------------------------------------
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
    element_value        	VARCHAR2(4000)	,
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
    portion_id		NUMBER(38) 	NOT NULL,
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
    surgery_barcode 	VARCHAR2(50)   	NOT NULL,
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
 
--------------------------------------------------------
--  DDL for Table CNA_VALUE
--------------------------------------------------------

  CREATE TABLE CNA_VALUE 
   (	CNA_VALUE_ID 		NUMBER(38,0)	NOT NULL, 
	DATA_SET_ID 		NUMBER(38,0)	NOT NULL, 
	HYBRIDIZATION_REF_ID 	NUMBER(38,0)	NOT NULL, 
	CHROMOSOME 		VARCHAR2(50)	NOT NULL, 
	CHR_START 		NUMBER(38,0)	NOT NULL, 
	CHR_STOP 		NUMBER(38,0)	NOT NULL, 
	NUM_MARK 		NUMBER(7,0), 
	SEG_MEAN 		VARCHAR2(50)	NOT NULL,
	CONSTRAINT PK_CNA_VALUE PRIMARY KEY (CNA_VALUE_ID),
	CONSTRAINT CNA_VALUE_AK1 UNIQUE (HYBRIDIZATION_REF_ID, DATA_SET_ID, CHROMOSOME, CHR_START, CHR_STOP)
   ) ;
--------------------------------------------------------
--  DDL for Table DATA_LEVEL
--------------------------------------------------------

  CREATE TABLE DATA_LEVEL 
   (	LEVEL_NUMBER 		NUMBER(10,0)	NOT NULL, 
	LEVEL_DEFINITION 	VARCHAR2(100)	NOT NULL,
	CONSTRAINT DATA_LEVEL_PK PRIMARY KEY (LEVEL_NUMBER)
   ) ;
--------------------------------------------------------
--  DDL for Table DATA_SET
--------------------------------------------------------

  CREATE TABLE DATA_SET 
   (	DATA_SET_ID 		NUMBER(38,0)	NOT NULL, 
	EXPERIMENT_ID 		NUMBER(38,0)	NOT NULL, 
	SOURCE_FILE_NAME 	VARCHAR2(255)	NOT NULL, 
	SOURCE_FILE_TYPE 	VARCHAR2(50)	NOT NULL, 
	ACCESS_LEVEL 		VARCHAR2(10)	NOT NULL, 
	LOAD_COMPLETE 		NUMBER(1,0)	DEFAULT 0 NOT NULL , 
	USE_IN_DAM 		NUMBER(1,0) DEFAULT 0, 
	DAM_COMMENTS 		VARCHAR2(255), 
	DATA_LEVEL 		NUMBER(1,0), 
	CENTER_ID 		NUMBER(38,0)	NOT NULL, 
	PLATFORM_ID 		NUMBER(38,0)	NOT NULL,
	ARCHIVE_ID		NUMBER(38,0),
	CONSTRAINT PK_DATA_SET_IDX PRIMARY KEY (DATA_SET_ID),
	CONSTRAINT DATA_LEVEL_CC CHECK (data_level in (2,3)),
	CONSTRAINT DATA_SET_ACCESS_LEVEL_CC CHECK (access_level in ('PUBLIC', 'PROTECTED')),
	CONSTRAINT DATA_SET_UK_IDX UNIQUE (SOURCE_FILE_NAME)) ;
 

   COMMENT ON COLUMN DATA_SET.DATA_SET_ID IS 'Internal ID.';
 
   COMMENT ON COLUMN DATA_SET.EXPERIMENT_ID IS 'The original experiment in which the file appeared.  Internal ID.';
 
   COMMENT ON COLUMN DATA_SET.SOURCE_FILE_NAME IS 'The full path and name of the data''s source file.';
 
   COMMENT ON COLUMN DATA_SET.SOURCE_FILE_TYPE IS 'The type of data contained in the source file.  E.g., snp data may have copynumber data as well as birdseed data.';
 
   COMMENT ON COLUMN DATA_SET.ACCESS_LEVEL IS 'Indicates if the data set contains public or protected data.';
 
   COMMENT ON COLUMN DATA_SET.LOAD_COMPLETE IS '0/1 to indicate when the data set (file) is completely loaded into the DB.  Checking this column avoids trying to pull out a partial data set and crashing an app.  Doing the complete load in a single transaction is not possible due to the use of sqlldr and the size of the data.';
 
   COMMENT ON COLUMN DATA_SET.USE_IN_DAM IS '0/1 to indicate if this data set should be a part of the DAM.  In certain cases, it should not.  If set to zero, the DAM_Comments column must have a value.';
 
   COMMENT ON COLUMN DATA_SET.DAM_COMMENTS IS 'If column use_in_DAM is set to zero, this column indicates why.';
--------------------------------------------------------
--  DDL for Table DATA_SET_FILE
--------------------------------------------------------

  CREATE TABLE DATA_SET_FILE 
   (	DATA_SET_FILE_ID 	NUMBER(38,0)	NOT NULL, 
	DATA_SET_ID 		NUMBER(38,0)	NOT NULL, 
	FILE_NAME 		VARCHAR2(300)	NOT NULL, 
	LOAD_START_DATE 	DATE		NOT NULL, 
	LOAD_END_DATE 		DATE, 
	IS_LOADED 		NUMBER(1,0) DEFAULT 0,
	file_id			NUMBER(38,0),
	CONSTRAINT PK_DATA_SET_FILE_IDX PRIMARY KEY (DATA_SET_FILE_ID),
	CONSTRAINT UK_DATASET_FILE_IDX UNIQUE (DATA_SET_ID, FILE_NAME) 
   ) ;

--------------------------------------------------------
--  DDL for Table DATA_TYPE
--------------------------------------------------------

  CREATE TABLE DATA_TYPE 
   (	DATA_TYPE_ID 		NUMBER(38,0)	NOT NULL, 
	NAME 			VARCHAR2(50)	NOT NULL, 
	CENTER_TYPE_CODE 	VARCHAR2(10), 
	FTP_DISPLAY 		VARCHAR2(100), 
	AVAILABLE 		NUMBER(1,0), 
	SORT_ORDER 		NUMBER(38,0),
	require_compression NUMBER(1) DEFAULT 1 NOT NULL,
	CONSTRAINT PK_DATA_TYPE PRIMARY KEY (DATA_TYPE_ID),
	CONSTRAINT DATA_TYPE_UK_IDX UNIQUE (NAME)
   ) ;
 

   COMMENT ON COLUMN DATA_TYPE.DATA_TYPE_ID IS 'Internal ID.';
 
   COMMENT ON COLUMN DATA_TYPE.NAME IS 'The type of data generated by the platform, such as cna or snp.';
--------------------------------------------------------
--  DDL for Table DATA_TYPE_TO_PLATFORM
--------------------------------------------------------

  CREATE TABLE DATA_TYPE_TO_PLATFORM 
   (	DATA_TYPE_PLATFORM_ID 	NUMBER(38,0)	NOT NULL, 
	DATA_TYPE_ID 		NUMBER(38,0)	NOT NULL, 
	PLATFORM_ID 		NUMBER(38,0)	NOT NULL,
	CONSTRAINT DATATYPE_PLATFORM_PK_IDX PRIMARY KEY (DATA_TYPE_PLATFORM_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table DATA_VISIBILITY
--------------------------------------------------------

  CREATE TABLE DATA_VISIBILITY 
   (	DATA_VISIBILITY_ID 	NUMBER(38,0)	NOT NULL, 
	DATA_TYPE_ID 		NUMBER(38,0)	NOT NULL, 
	VISIBILITY_ID 		NUMBER(38,0)	NOT NULL, 
	LEVEL_NUMBER 		NUMBER(10,0)	NOT NULL,
	CONSTRAINT DATA_VISIBILITY_PK_IDX PRIMARY KEY (DATA_VISIBILITY_ID),
	CONSTRAINT DATA_VISIBILITY_UK_IDX UNIQUE (DATA_TYPE_ID, VISIBILITY_ID, LEVEL_NUMBER)
   ) ;
--------------------------------------------------------
--  DDL for Table DELETE_OLD_DATA
--------------------------------------------------------

  CREATE TABLE DELETE_OLD_DATA 
   (	EXPERIMENT_ID 	NUMBER(38,0), 
	DATA_SET_ID 	NUMBER(38,0)
   ) ;
--------------------------------------------------------
--  DDL for Table DISEASE
--------------------------------------------------------

  CREATE TABLE DISEASE 
   (	DISEASE_ID 		NUMBER(38,0)	NOT NULL, 
	DISEASE_ABBREVIATION 	VARCHAR2(10)	NOT NULL, 
	DISEASE_NAME 		VARCHAR2(50), 
	ACTIVE 			NUMBER(1,0) DEFAULT 0, 
	DAM_DEFAULT 		NUMBER(1,0) DEFAULT 0, 
	WORKBENCH_TRACK 	NUMBER(10,0),
	CONSTRAINT PK_DISEASE PRIMARY KEY (DISEASE_ID),
	CONSTRAINT DISEASE_UK_IDX UNIQUE (DISEASE_ABBREVIATION)
   ) ;
 

   COMMENT ON COLUMN DISEASE.DISEASE_ID IS 'Internal ID.';
 
   COMMENT ON COLUMN DISEASE.DISEASE_ABBREVIATION IS 'Short abbreviated name for the disease.  E.g., GBM for glioblastoma malforme.';
--------------------------------------------------------
--  DDL for Table DRUG
--------------------------------------------------------

  CREATE TABLE DRUG 
   (	DRUG_ID 	NUMBER(38,0)	NOT NULL, 
	NAME 		VARCHAR2(100)	NOT NULL, 
	COMMENTS 	VARCHAR2(1000),
	CONSTRAINT DRUG_PK PRIMARY KEY (DRUG_ID),
	CONSTRAINT DRUG_NAME_UK_IDX UNIQUE (NAME)
   ) ;
 

   COMMENT ON COLUMN DRUG.DRUG_ID IS 'An internal, information-free integer used as a Primary Key';
 
   COMMENT ON COLUMN DRUG.NAME IS 'The drug name, occaisonally not a great name.  Alternate Key';
 
   COMMENT ON COLUMN DRUG.COMMENTS IS 'An area to store official comments about any caveats with the data.';
 
   COMMENT ON TABLE DRUG  IS 'Stores basic information on drugs involved in cancer treatments for genes in this database.';
--------------------------------------------------------
--  DDL for Table DRUG_CONCEPT_CODE
--------------------------------------------------------

  CREATE TABLE DRUG_CONCEPT_CODE 
   (	DRUG_CONCEPT_CODE_ID 	NUMBER(38,0)	NOT NULL, 
	CONCEPT_CODE 		VARCHAR2(50)	NOT NULL, 
	DRUG_ID 		NUMBER(38,0)	NOT NULL, 
	COMMENTS VARCHAR2(1000),
	CONSTRAINT DRUG_CCODE_PK PRIMARY KEY (DRUG_CONCEPT_CODE_ID),
	CONSTRAINT DRUG_CCODE_UK UNIQUE (DRUG_ID, CONCEPT_CODE)
   ) ;
 

   COMMENT ON COLUMN DRUG_CONCEPT_CODE.DRUG_CONCEPT_CODE_ID IS 'An internal, information-free integer used as a Primary Key';
 
   COMMENT ON COLUMN DRUG_CONCEPT_CODE.CONCEPT_CODE IS 'The NCI Thesaurus Drug Concept Code.';
 
   COMMENT ON COLUMN DRUG_CONCEPT_CODE.DRUG_ID IS 'Foreign Key.';
 
   COMMENT ON COLUMN DRUG_CONCEPT_CODE.COMMENTS IS 'An area to store official comments about any caveats with the data.';
 
   COMMENT ON TABLE DRUG_CONCEPT_CODE  IS 'Stores each concept code associated with the drug.  The concept code can be used to link out to the EVS.  Note: theoretically, there should be a 1:1 realationship.';
--------------------------------------------------------
--  DDL for Table EXPERIMENT
--------------------------------------------------------

  CREATE TABLE EXPERIMENT 
   (	EXPERIMENT_ID 		NUMBER(38,0)	NOT NULL, 
	BASE_NAME 		VARCHAR2(255)	NOT NULL, 
	DATA_DEPOSIT_BATCH 	NUMBER(38,0)	NOT NULL, 
	DATA_REVISION 		NUMBER(38,0)	NOT NULL, 
	CENTER_ID 		NUMBER(38,0)	NOT NULL, 
	PLATFORM_ID 		NUMBER(38,0)	NOT NULL,
	CONSTRAINT PK_EXPERIMENT_IDX PRIMARY KEY (EXPERIMENT_ID),
	CONSTRAINT EXPERIMENT_UK_IDX UNIQUE (BASE_NAME, DATA_DEPOSIT_BATCH, DATA_REVISION)
   ) ;
 

   COMMENT ON COLUMN EXPERIMENT.EXPERIMENT_ID IS 'Internal ID.';
 
   COMMENT ON COLUMN EXPERIMENT.BASE_NAME IS 'The base name of the experiment could be derived but is here for convenience.  It is comprised of center, disease, platform, center''s deposit batch number, and data revision number.';
 
   COMMENT ON COLUMN EXPERIMENT.DATA_DEPOSIT_BATCH IS 'Integer representing which submission batch the data comes from.  It is the same as the DCC serial number of an archive.';
 
   COMMENT ON COLUMN EXPERIMENT.DATA_REVISION IS 'An integer that represents the which revision of the data files these data come from.';
--------------------------------------------------------
--  DDL for Table EXPGENE_VALUE
--------------------------------------------------------

  CREATE TABLE EXPGENE_VALUE 
   (	EXPGENE_VALUE_ID 	NUMBER(38,0)	NOT NULL , 
	DATA_SET_ID 		NUMBER(38,0)	NOT NULL , 
	HYBRIDIZATION_REF_ID 	NUMBER(38,0)	NOT NULL , 
	ENTREZ_GENE_SYMBOL 	VARCHAR2(50)	NOT NULL , 
	EXPRESSION_VALUE 	VARCHAR2(70)	NOT NULL ,
	CONSTRAINT PK_EXPGENE_VALUE PRIMARY KEY (EXPGENE_VALUE_ID),
	CONSTRAINT EXPGENE_VALUE_UK_IDX UNIQUE (HYBRIDIZATION_REF_ID, DATA_SET_ID, ENTREZ_GENE_SYMBOL)
   ) ;

--------------------------------------------------------
--  DDL for Table FILE_INFO
--------------------------------------------------------

  CREATE TABLE FILE_INFO 
   (	FILE_ID 	NUMBER(38,0)	NOT NULL, 
	FILE_NAME 	VARCHAR2(2000)	NOT NULL, 
	FILE_SIZE 	NUMBER(38,0), 
	LEVEL_NUMBER 	INTEGER, 
	DATA_TYPE_ID 	NUMBER(38,0), 
	MD5 		CHAR(32), 
	REVISION_OF_FILE_ID NUMBER(38,0),
	CONSTRAINT FILE_INFO_PK_IDX PRIMARY KEY (FILE_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table FILE_TO_ARCHIVE
--------------------------------------------------------

  CREATE TABLE FILE_TO_ARCHIVE 
   (	FILE_ARCHIVE_ID		NUMBER(38,0)	NOT NULL,
   	FILE_ID 		NUMBER(38,0)	NOT NULL, 
	ARCHIVE_ID 		NUMBER(38,0)	NOT NULL, 
	FILE_LOCATION_URL 	VARCHAR2(2000),
	CONSTRAINT FILE_TO_ARCHIVE_PK_IDX PRIMARY KEY (FILE_ARCHIVE_ID),
	CONSTRAINT FILE_TO_ARCHIVE_UK_IDX UNIQUE (FILE_ID,ARCHIVE_ID)	
   ) ;
--------------------------------------------------------
--  DDL for Table GENE
--------------------------------------------------------

  CREATE TABLE GENE 
   (	GENE_ID 	NUMBER(38,0)	NOT NULL, 
	ENTREZ_SYMBOL 	VARCHAR2(50)	NOT NULL, 
	COMMENTS 	VARCHAR2(1000),
	CONSTRAINT GENE_PK_IDX PRIMARY KEY (GENE_ID),
	CONSTRAINT GENE_ENZSYM_UK_IDX UNIQUE (ENTREZ_SYMBOL)
   ) ;
 

   COMMENT ON COLUMN GENE.GENE_ID IS 'The ENTREZ Gene ID. Primary Key.';
 
   COMMENT ON COLUMN GENE.ENTREZ_SYMBOL IS 'The ENTREZ gene symbol.  Alternate Key.';
 
   COMMENT ON COLUMN GENE.COMMENTS IS 'An area to store official comments about any caveats with the data.';
 
   COMMENT ON TABLE GENE  IS 'Stores relevant entrez gene information';
--------------------------------------------------------
--  DDL for Table GENE_DRUG
--------------------------------------------------------

  CREATE TABLE GENE_DRUG 
   (	GENE_ID 	NUMBER(38,0)	NOT NULL, 
	DRUG_ID 	NUMBER(38,0)	NOT NULL,
	CONSTRAINT GENE_DRUG_PK PRIMARY KEY (GENE_ID, DRUG_ID)
   ) ;
 

   COMMENT ON COLUMN GENE_DRUG.GENE_ID IS 'Foreign Key';
 
   COMMENT ON COLUMN GENE_DRUG.DRUG_ID IS 'Foreign Key';
 
   COMMENT ON TABLE GENE_DRUG  IS 'Relates Entrez genes to any agents they are affected by or agents they affect.  This is a many-to-many relationship.';
--------------------------------------------------------
--  DDL for Table HYBRIDIZATION_REF
--------------------------------------------------------

  CREATE TABLE HYBRIDIZATION_REF 
   (	HYBRIDIZATION_REF_ID 	NUMBER		NOT NULL, 
	BESTBARCODE 		VARCHAR2(50)	NOT NULL, 
	SAMPLE_NAME 		VARCHAR2(50)	NOT NULL, 
	ALIQUOT_ID 		NUMBER(38,0),
	UUID			VARCHAR2(36),
	CONSTRAINT PK_HYBRIDIZATION_REF_IDX PRIMARY KEY (HYBRIDIZATION_REF_ID),
	CONSTRAINT UK_HYBRIDIZATION_REF_BARCODE UNIQUE (BESTBARCODE) 
   ) ;
--------------------------------------------------------
--  DDL for Table HYBRID_REF_DATA_SET
--------------------------------------------------------

  CREATE TABLE HYBRID_REF_DATA_SET 
   (	HYBREF_DATASET_ID 	NUMBER		NOT NULL, 
	HYBRIDIZATION_REF_ID 	NUMBER(38,0)	NOT NULL, 
	DATA_SET_ID 		NUMBER(38,0)	NOT NULL, 
	HYBRIDIZATION_REF_NAME 	VARCHAR2(70),
	CONSTRAINT PK_HYBREF_DATA_SET PRIMARY KEY (HYBREF_DATASET_ID),
	CONSTRAINT UK_HYBREF_DATASET_IDX UNIQUE (HYBRIDIZATION_REF_ID, DATA_SET_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table L4_ANOMALY_DATA_SET
--------------------------------------------------------

  CREATE TABLE L4_ANOMALY_DATA_SET 
   (	ANOMALY_DATA_SET_ID 	NUMBER(38,0)	NOT NULL, 
	ANOMALY_TYPE_ID 	NUMBER(38,0)	NOT NULL, 
	TOTAL_PATIENTS 		NUMBER(38,0), 
	TOTAL_GENETIC_ELEMENTS 	NUMBER(38,0),
	CONSTRAINT PK_L4_ANOMALY_DATA_SET_IDX PRIMARY KEY (ANOMALY_DATA_SET_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table L4_ANOMALY_DATA_SET_VERSION
--------------------------------------------------------

  CREATE TABLE L4_ANOMALY_DATA_SET_VERSION 
   (	ANOMALY_DATA_SET_ID 		NUMBER(38,0)	NOT NULL, 
	ANOMALY_DATA_VERSION_ID 	NUMBER(38,0)	NOT NULL, 
	L4_ANOMALY_DATA_SET_VERSION_ID 	NUMBER(38,0),
	CONSTRAINT PK_L4_ANOMALY_DSV_IDX PRIMARY KEY (L4_ANOMALY_DATA_SET_VERSION_ID),
	CONSTRAINT L4_ANOMALY_DSV_UK_IDX UNIQUE (ANOMALY_DATA_SET_ID, ANOMALY_DATA_VERSION_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table L4_ANOMALY_DATA_VERSION
--------------------------------------------------------

  CREATE TABLE L4_ANOMALY_DATA_VERSION 
   (	ANOMALY_DATA_VERSION_ID 	NUMBER(38,0)	NOT NULL, 
	DISEASE_ID 			NUMBER(38,0)	NOT NULL, 
	VERSION 			NUMBER(38,0)	NOT NULL, 
	IS_ACTIVE 			NUMBER(1,0) 	DEFAULT 0 NOT NULL , 
	LOADED_DATE 			DATE,
	CONSTRAINT PK_L4_ANOMALY_DATA_VERSION PRIMARY KEY (ANOMALY_DATA_VERSION_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table L4_ANOMALY_TYPE
--------------------------------------------------------

  CREATE TABLE L4_ANOMALY_TYPE 
   (	ANOMALY_TYPE_ID 	NUMBER(38,0)	NOT NULL, 
	ANOMALY_NAME 		VARCHAR2(50)	NOT NULL, 
	PLATFORM_ID 		NUMBER(38,0)	NOT NULL, 
	CENTER_ID 		NUMBER(38,0)	NOT NULL, 
	DATA_TYPE_ID 		NUMBER(38,0)	NOT NULL, 
	GENETIC_ELEMENT_TYPE_ID NUMBER(38,0) 	DEFAULT 1 NOT NULL ,
	CONSTRAINT PK_L4_ANOMALY_TYPE_IDX PRIMARY KEY (ANOMALY_TYPE_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table L4_ANOMALY_VALUE
--------------------------------------------------------

  CREATE TABLE L4_ANOMALY_VALUE 
   (	ANOMALY_VALUE_ID 	NUMBER(38,0)	NOT NULL, 
	ANOMALY_VALUE 		FLOAT(126)	NOT NULL, 
	ANOMALY_DATA_SET_ID 	NUMBER(38,0)	NOT NULL, 
	GENETIC_ELEMENT_ID 	NUMBER(38,0)	NOT NULL, 
	SAMPLE_ID 		NUMBER(38,0)	NOT NULL
   );
--------------------------------------------------------
--  DDL for Table L4_CORRELATION_TYPE
--------------------------------------------------------

  CREATE TABLE L4_CORRELATION_TYPE 
   (	CORRELATION_TYPE_ID 	NUMBER(38,0)	NOT NULL, 
	CORRELATION_NAME 	VARCHAR2(100)	NOT NULL, 
	ANOMALY_TYPE_ID_1 	NUMBER(38,0)	NOT NULL, 
	ANOMALY_TYPE_ID_2 	NUMBER(38,0)	NOT NULL,
	CONSTRAINT PK_L4_CORRELATION_TYPE PRIMARY KEY (CORRELATION_TYPE_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table L4_DATA_SET_GENETIC_ELEMENT
--------------------------------------------------------

  CREATE TABLE L4_DATA_SET_GENETIC_ELEMENT 
   (	ANOMALY_DATA_SET_ID 		NUMBER(38,0)	NOT NULL, 
	GENETIC_ELEMENT_ID 		NUMBER(38,0)	NOT NULL, 
	L4_DATA_SET_GENETIC_ELEMENT_ID 	NUMBER(38,0),
	CONSTRAINT PK_L4_DATA_SET_GE_ID PRIMARY KEY (L4_DATA_SET_GENETIC_ELEMENT_ID),
	CONSTRAINT L4_DATA_SET_GE_UK_IDX UNIQUE (ANOMALY_DATA_SET_ID, GENETIC_ELEMENT_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table L4_DATA_SET_SAMPLE
--------------------------------------------------------

  CREATE TABLE L4_DATA_SET_SAMPLE 
   (	ANOMALY_DATA_SET_ID 	NUMBER(38,0)	NOT NULL, 
	SAMPLE_ID 		NUMBER(38,0)	NOT NULL, 
	IS_PAIRED 		NUMBER(1,0)	NOT NULL, 
	L4_DATA_SET_SAMPLE_ID 	NUMBER(38,0),
	CONSTRAINT PK_L4_DATA_SET_SAMPLE_IDX PRIMARY KEY (L4_DATA_SET_SAMPLE_ID),
	CONSTRAINT L4_DATA_SET_SAMPLE_UK_IDX UNIQUE (ANOMALY_DATA_SET_ID, SAMPLE_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table L4_GENETIC_ELEMENT
--------------------------------------------------------

  CREATE TABLE L4_GENETIC_ELEMENT 
   (	GENETIC_ELEMENT_ID 	NUMBER(38,0)	NOT NULL, 
	GENETIC_ELEMENT_NAME 	VARCHAR2(100)	NOT NULL, 
	GENETIC_ELEMENT_TYPE_ID NUMBER(38,0)	NOT NULL, 
	START_POS 		NUMBER(38,0)	NOT NULL, 
	STOP_POS 		NUMBER(38,0)	NOT NULL, 
	CHROMOSOME 		VARCHAR2(2)	NOT NULL, 
	IN_CNV_REGION 		NUMBER(1,0) DEFAULT 0, 
	GENE_ID 		NUMBER(38,0),
	CONSTRAINT PK_GENETIC_ELEMENT_IDX PRIMARY KEY (GENETIC_ELEMENT_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table L4_GENETIC_ELEMENT_TYPE
--------------------------------------------------------

  CREATE TABLE L4_GENETIC_ELEMENT_TYPE 
   (	GENETIC_ELEMENT_TYPE_ID 	NUMBER(38,0)	NOT NULL, 
	GENETIC_ELEMENT_TYPE 		VARCHAR2(50)	NOT NULL, 
	INFO_URL 			VARCHAR2(2000),
	CONSTRAINT PK_L4_GENETIC_ELEMENT_TYPE PRIMARY KEY (GENETIC_ELEMENT_TYPE_ID),
	CONSTRAINT UQ_GENETIC_ELEMENT_TYPE UNIQUE (GENETIC_ELEMENT_TYPE)
   ) ;
--------------------------------------------------------
--  DDL for Table L4_PATIENT
--------------------------------------------------------

  CREATE TABLE L4_PATIENT 
   (	PATIENT_ID 		NUMBER(38,0)	NOT NULL, 
	PATIENT 		VARCHAR2(50)	NOT NULL, 
	CLINICAL_PATIENT_ID 	NUMBER(38,0),
	CONSTRAINT PK_L4_PATIENT_IDX PRIMARY KEY (PATIENT_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table L4_SAMPLE
--------------------------------------------------------

  CREATE TABLE L4_SAMPLE 
   (	SAMPLE_ID 		NUMBER(38,0)	NOT NULL, 
	BARCODE 		VARCHAR2(100)	NOT NULL, 
	PATIENT 		VARCHAR2(50)	NOT NULL, 
	DISEASE_ID 		NUMBER(38,0)	NOT NULL, 
	SAMPLE_TYPE 		VARCHAR2(50), 
	PATIENT_ID 		NUMBER(38,0)	NOT NULL, 
	ALIQUOT_ID 		NUMBER(38,0),
	CONSTRAINT PK_L4_SAMPLE PRIMARY KEY (SAMPLE_ID),
	CONSTRAINT UK_SAMPLE_BARCODE_IDX UNIQUE (BARCODE),
	CONSTRAINT UK_L4_SAMPLE_COMP_IDX UNIQUE (PATIENT_ID, SAMPLE_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table L4_TARGET
--------------------------------------------------------

  CREATE TABLE L4_TARGET 
   (	TARGET_ID 			NUMBER(38,0)	NOT NULL, 
	SOURCE_GENETIC_ELEMENT_ID 	NUMBER(38,0)	NOT NULL, 
	TARGET_GENETIC_ELEMENT_ID 	NUMBER(38,0)	NOT NULL,
	CONSTRAINT PK_L4_TARGET_IDX PRIMARY KEY (TARGET_ID),
	CONSTRAINT TARGET_GE_IDS_UK_IDX UNIQUE (SOURCE_GENETIC_ELEMENT_ID, TARGET_GENETIC_ELEMENT_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table MAF_INFO
--------------------------------------------------------

  CREATE TABLE MAF_INFO 
   (	MAF_INFO_ID 			NUMBER(38,0)	NOT NULL, 
	CENTER_ID 			NUMBER(38,0)	NOT NULL, 
	FILE_ID 			NUMBER(38,0)	NOT NULL, 
	HUGO_SYMBOL 			VARCHAR2(4000)	NOT NULL,
	ENTREZ_GENE_ID 			NUMBER(38,0)	NOT NULL, 
	NCBI_BUILD 			VARCHAR2(4000)	NOT NULL,
	CHROM 				VARCHAR2(4000)	NOT NULL,
	START_POSITION 			NUMBER(38,0)	NOT NULL, 
	END_POSITION 			NUMBER(38,0)	NOT NULL, 
	STRAND 				VARCHAR2(4000)		NOT NULL,
	VARIANT_CLASSIFICATION 		VARCHAR2(4000),
	VARIANT_TYPE 			VARCHAR2(4000)	NOT NULL,
	REFERENCE_ALLELE 		VARCHAR2(4000)	NOT NULL,
	TUMOR_SEQ_ALLELE1 		VARCHAR2(4000)	NOT NULL,
	TUMOR_SEQ_ALLELE2 		VARCHAR2(4000)	NOT NULL,
	DBSNP_RS 			VARCHAR2(4000),
	DBSNP_VAL_STATUS 		VARCHAR2(4000),
	TUMOR_SAMPLE_BARCODE 		VARCHAR2(4000)	NOT NULL,
	MATCH_NORM_SAMPLE_BARCODE 	VARCHAR2(4000)	NOT NULL,
	MATCH_NORM_SEQ_ALLELE1 		VARCHAR2(4000),
	MATCH_NORM_SEQ_ALLELE2 		VARCHAR2(4000),
	TUMOR_VALIDATION_ALLELE1 	VARCHAR2(4000),
	TUMOR_VALIDATION_ALLELE2 	VARCHAR2(4000),
	MATCH_NORM_VALIDATION_ALLELE1 	VARCHAR2(4000),
	MATCH_NORM_VALIDATION_ALLELE2 	VARCHAR2(4000),
	VERIFICATION_STATUS 		VARCHAR2(4000),
	VALIDATION_STATUS 		VARCHAR2(4000),
	MUTATION_STATUS 		VARCHAR2(4000),
	VALIDATION_METHOD 		VARCHAR2(4000),
	SEQUENCING_PHASE 		VARCHAR2(4000),
	SCORE 				VARCHAR2(4000),
	BAM_FILE 			VARCHAR2(4000),
	SEQUENCER 			VARCHAR2(4000),
	SEQUENCE_SOURCE 		VARCHAR2(4000),
	CONSTRAINT MAF_INFO_PK_IDX PRIMARY KEY (MAF_INFO_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table METHYLATION_VALUE
--------------------------------------------------------

  CREATE TABLE METHYLATION_VALUE 
   (	METHYLATION_VALUE_ID 	NUMBER(38,0)	NOT NULL, 
	DATA_SET_ID 		NUMBER(38,0)	NOT NULL, 
	HYBRIDIZATION_REF_ID 	NUMBER(38,0)	NOT NULL, 
	BETA_VALUE 		VARCHAR2(70)	NOT NULL, 
	ENTREZ_GENE_SYMBOL 	VARCHAR2(255), 
	CHROMOSOME 		VARCHAR2(2), 
	CHR_POSITION 		NUMBER(38,0),
	PROBE_NAME		VARCHAR2(50),
	CONSTRAINT PK_METHYLATION_VALUE PRIMARY KEY (METHYLATION_VALUE_ID)
   ) ;
--------------------------------------------------------
--  DDL for Table 
--------------------------------------------------------

  CREATE TABLE PATHWAY 
   (	PATHWAY_ID 	NUMBER(38,0)	NOT NULL, 
	SVG_FILE_NAME 	VARCHAR2(255)	NOT NULL, 
	DISPLAY_NAME 	VARCHAR2(255)	NOT NULL, 
	SVG_IDENTIFIER 	VARCHAR2(255)	NOT NULL, 
	COMMENTS 	VARCHAR2(1000),
	CONSTRAINT PATHWAY_PK PRIMARY KEY (PATHWAY_ID),
	CONSTRAINT PATHWAY_DISPLAY_UK_IDX UNIQUE (DISPLAY_NAME),
	CONSTRAINT PATHWAY_SVGFILE_UK_IDX UNIQUE (SVG_FILE_NAME),
	CONSTRAINT PATHWAY_SVGID_UK_IDX UNIQUE (SVG_IDENTIFIER) 
   ) ;
 

   COMMENT ON COLUMN PATHWAY.PATHWAY_ID IS 'An internal, information-free integer used as a Primary Key';
 
   COMMENT ON COLUMN PATHWAY.SVG_FILE_NAME IS 'Name of the svg pathway file with out pathing information.  The pathing info should be stored as a variable on the system as it could vary.';
 
   COMMENT ON COLUMN PATHWAY.DISPLAY_NAME IS 'The name of the pathway to be used in GUIs and reports.  The pretty name.';
 
   COMMENT ON COLUMN PATHWAY.SVG_IDENTIFIER IS 'The textual identifier for an SVG pathway.  It may or may not be the same as the file name.';
 
   COMMENT ON COLUMN PATHWAY.COMMENTS IS 'An area to store official comments about any caveats with the data.';
 
   COMMENT ON TABLE PATHWAY  IS 'Stores relevant pathway information';
--------------------------------------------------------
--  DDL for Table PLATFORM
--------------------------------------------------------

  CREATE TABLE PLATFORM 
   (	PLATFORM_ID 		NUMBER(38,0)	NOT NULL, 
	PLATFORM_NAME 		VARCHAR2(100)	NOT NULL, 
	CENTER_TYPE_CODE 	VARCHAR2(10), 
	PLATFORM_DISPLAY_NAME 	VARCHAR2(200), 
	PLATFORM_ALIAS 		VARCHAR2(200), 
	AVAILABLE 		NUMBER(1,0), 
	SORT_ORDER 		INTEGER, 
	BASE_DATA_TYPE_ID 	NUMBER(38,0)	NOT NULL,
	CONSTRAINT PK_PLATFORM_IDX PRIMARY KEY (PLATFORM_ID)
   ) ;
 

   COMMENT ON COLUMN PLATFORM.PLATFORM_ID IS 'Internal ID.';
 
   COMMENT ON COLUMN PLATFORM.PLATFORM_NAME IS 'Name of the platform.';
--------------------------------------------------------
--  DDL for Table PORTAL_ACTION_TYPE
--------------------------------------------------------

  CREATE TABLE PORTAL_ACTION_TYPE 
   (	PORTAL_ACTION_TYPE_ID 		NUMBER(38,0)	NOT NULL, 
	NAME 				VARCHAR2(50)	NOT NULL, 
	DESCR 				VARCHAR2(255), 
	PORTAL_ACTION_TYPE_PARENT 	NUMBER(38,0),
	CONSTRAINT PK_PORTAL_ACTION_TYPE_IDX PRIMARY KEY (PORTAL_ACTION_TYPE_ID),
	CONSTRAINT UK_PORTAL_ACTION_TYPE_NAME_IDX UNIQUE (NAME) 
   ) ;
--------------------------------------------------------
--  DDL for Table PORTAL_SESSION
--------------------------------------------------------

  CREATE TABLE PORTAL_SESSION 
   (	PORTAL_SESSION_ID 	NUMBER(38,0)	NOT NULL, 
	CREATED_ON 		TIMESTAMP (6)	NOT NULL, 
	SESSION_KEY 		VARCHAR2(50)	NOT NULL,
	CONSTRAINT PK_PORTAL_SESSION_IDX PRIMARY KEY (PORTAL_SESSION_ID),
	CONSTRAINT UK_PORTAL_SESSION_SESSION_KEY UNIQUE (SESSION_KEY)
   ) ;
--------------------------------------------------------
--  DDL for Table PORTAL_SESSION_ACTION
--------------------------------------------------------

  CREATE TABLE PORTAL_SESSION_ACTION 
   (	PORTAL_SESSION_ACTION_ID 	NUMBER(38,0)	NOT NULL, 
	PORTAL_SESSION_ID 		NUMBER(38,0)	NOT NULL, 
	PORTAL_ACTION_TYPE_ID 		NUMBER(38,0)	NOT NULL, 
	ACTION_TIME 			TIMESTAMP (6)	NOT NULL, 
	VALUE 				VARCHAR2(50),
	CONSTRAINT PK_PORTAL_SESSION_ACTION PRIMARY KEY (PORTAL_SESSION_ACTION_ID),
	CONSTRAINT UK_ALL_PORTAL_SESSION_ACTION UNIQUE (PORTAL_SESSION_ID, PORTAL_ACTION_TYPE_ID, ACTION_TIME, VALUE) 
   ) ;
--------------------------------------------------------
--  DDL for Table SUMMARY_BY_GENE
--------------------------------------------------------

  CREATE TABLE SUMMARY_BY_GENE 
   (	GENE_ID 	NUMBER(38,0)	NOT NULL, 
	ANOMALY_TYPE_ID NUMBER(38,0)	NOT NULL, 
	CASES_PROBED 	INTEGER		NOT NULL, 
	CASES_DETECTED 	INTEGER		NOT NULL, 
	COMMENTS 	VARCHAR2(1000),
	CONSTRAINT SUMMARY_BY_GENE_PK PRIMARY KEY (GENE_ID, ANOMALY_TYPE_ID),
	CONSTRAINT SUMMARY_BY_GENE_CASES_CC CHECK (cases_probed >= cases_detected)
   ) ;
 

   COMMENT ON COLUMN SUMMARY_BY_GENE.GENE_ID IS 'Foreign Key';
 
   COMMENT ON COLUMN SUMMARY_BY_GENE.ANOMALY_TYPE_ID IS 'Foreign Key';
 
   COMMENT ON COLUMN SUMMARY_BY_GENE.CASES_PROBED IS 'The total number of cases/patients tested/probed for the anomaly.';
 
   COMMENT ON COLUMN SUMMARY_BY_GENE.CASES_DETECTED IS 'The total number of cases/patients in which the anomaly is found.';
 
   COMMENT ON COLUMN SUMMARY_BY_GENE.COMMENTS IS 'An area to store official comments about any caveats with the data.';
 
   COMMENT ON TABLE SUMMARY_BY_GENE  IS 'Stores the gene-level summary information, rolled-up by cases/patients, on the various anomalies';
--------------------------------------------------------
--  DDL for Table VISIBILITY
--------------------------------------------------------

  CREATE TABLE VISIBILITY 
   (	VISIBILITY_ID 		NUMBER(38,0)	NOT NULL, 
	VISIBILITY_NAME 	VARCHAR2(20)	NOT NULL, 
	IDENTIFIABLE 		NUMBER(1,0)	NOT NULL,
	CONSTRAINT VISIBILITY_PK_IDX PRIMARY KEY (VISIBILITY_ID) 
   ) ;
 
--------------------------------------------------------
--  DDL for Table TMPBARCODE
--------------------------------------------------------
DROP TABLE TMPBARCODE;
CREATE GLOBAL TEMPORARY TABLE TMPBARCODE
(
  BARCODE  VARCHAR2(100 BYTE)
)
ON COMMIT DELETE ROWS
NOCACHE;

DROP TABLE tmphybref;
CREATE GLOBAL TEMPORARY TABLE tmphybref
( hybridization_ref_id number(38)) 
ON COMMIT DELETE ROWS ;

--------------------------------------------------------
--  DDL for Index CNA_VALUE_DATASET_IDX
--------------------------------------------------------

  CREATE INDEX CNA_VALUE_DATASET_IDX ON CNA_VALUE (DATA_SET_ID) 
  ;
--------------------------------------------------------
--  DDL for Index DATASET_CENTER_IDX
--------------------------------------------------------

  CREATE INDEX DATASET_CENTER_IDX ON DATA_SET (CENTER_ID) 
  ;
--------------------------------------------------------
--  DDL for Index DATASET_PLATFORM_IDX
--------------------------------------------------------

  CREATE INDEX DATASET_PLATFORM_IDX ON DATA_SET (PLATFORM_ID) 
  ;
--------------------------------------------------------
--  DDL for Index DATA_SET_EXPERIMENTID_IDX
--------------------------------------------------------

  CREATE INDEX DATA_SET_EXPERIMENTID_IDX ON DATA_SET (EXPERIMENT_ID) 
  ;
--------------------------------------------------------
--  DDL for Index DNA_ANALYTE_IDX
--------------------------------------------------------

  CREATE INDEX DNA_ANALYTE_IDX ON DNA (ANALYTE_ID) 
  ;
--------------------------------------------------------
--  DDL for Index DRUG_INTGEN_PATIENT_IDX
--------------------------------------------------------

  CREATE INDEX DRUG_INTGEN_PATIENT_IDX ON DRUG_INTGEN (PATIENT_ID) 
  ;
--------------------------------------------------------
--  DDL for Index EXAMINATION_PATIENT_IDX
--------------------------------------------------------

  CREATE INDEX EXAMINATION_PATIENT_IDX ON EXAMINATION (PATIENT_ID) 
  ;
--------------------------------------------------------
--  DDL for Index EXPERIMENT_CENTER_IDX
--------------------------------------------------------

  CREATE INDEX EXPERIMENT_CENTER_IDX ON EXPERIMENT (CENTER_ID) 
  ;
--------------------------------------------------------
--  DDL for Index EXPERIMENT_PLATFORM_IDX
--------------------------------------------------------

  CREATE INDEX EXPERIMENT_PLATFORM_IDX ON EXPERIMENT (PLATFORM_ID) 
  ;
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Index EXPGENE_VALUE_DATASETID_IDX
--------------------------------------------------------

  CREATE INDEX EXPGENE_VALUE_DATASETID_IDX ON EXPGENE_VALUE (DATA_SET_ID) 
  ;
--------------------------------------------------------
--  DDL for Index HYBREF_DATASET_DATASET_IDX
--------------------------------------------------------

  CREATE INDEX HYBREF_DATASET_DATASET_IDX ON HYBRID_REF_DATA_SET (DATA_SET_ID) 
  ;
--------------------------------------------------------
--  DDL for Index L4_ANOMALYVAL_GE_IDX
--------------------------------------------------------

  CREATE  INDEX L4_ANOMALYVAL_GE_IDX ON L4_ANOMALY_VALUE (GENETIC_ELEMENT_ID) ;
--------------------------------------------------------
--  DDL for Index L4_ANOMALYVAL_SAMPLE_IDX
--------------------------------------------------------

  CREATE INDEX L4_ANOMALYVAL_SAMPLE_IDX ON L4_ANOMALY_VALUE (SAMPLE_ID);
--------------------------------------------------------
--  DDL for Index L4_ANOMALYVAL_VALUE_IDX
--------------------------------------------------------

  CREATE UNIQUE INDEX L4_ANOMALYVAL_VALUE_IDX ON L4_ANOMALY_VALUE (ANOMALY_VALUE, SAMPLE_ID, GENETIC_ELEMENT_ID, ANOMALY_DATA_SET_ID) ;
--------------------------------------------------------
--  DDL for Index L4_DS_GENETIC_ELEMENT_DS_IDX
--------------------------------------------------------

  CREATE INDEX L4_DS_GENETIC_ELEMENT_DS_IDX ON L4_DATA_SET_GENETIC_ELEMENT (ANOMALY_DATA_SET_ID) 
  ;
--------------------------------------------------------
--  DDL for Index L4_DS_GENETIC_ELEMENT_IDX
--------------------------------------------------------

  CREATE INDEX L4_DS_GENETIC_ELEMENT_IDX ON L4_DATA_SET_GENETIC_ELEMENT (GENETIC_ELEMENT_ID) 
  ;
--------------------------------------------------------
--  DDL for Index L4_DS_SAMPLE_DS_IDX
--------------------------------------------------------

  CREATE INDEX L4_DS_SAMPLE_DS_IDX ON L4_DATA_SET_SAMPLE (ANOMALY_DATA_SET_ID) 
  ;
--------------------------------------------------------
--  DDL for Index L4_DS_SAMPLE_IDX
--------------------------------------------------------

  CREATE INDEX L4_DS_SAMPLE_IDX ON L4_DATA_SET_SAMPLE (SAMPLE_ID) 
  ;
--------------------------------------------------------
--  DDL for Index L4_GENETIC_ELEMENT_NAME
--------------------------------------------------------

  CREATE INDEX L4_GENETIC_ELEMENT_NAME ON L4_GENETIC_ELEMENT (GENETIC_ELEMENT_NAME) 
  ;
--------------------------------------------------------
--  DDL for Index L4_PATIENT_IDX
--------------------------------------------------------

  CREATE INDEX L4_PATIENT_IDX ON L4_PATIENT (PATIENT) ;
--------------------------------------------------------
--  DDL for Index L4_SAMPLE_PATIENT_IDX
--------------------------------------------------------

  CREATE INDEX L4_SAMPLE_PATIENT_IDX ON L4_SAMPLE (PATIENT) 
  ;
--------------------------------------------------------
--  DDL for Index METHYL_VALUE_DATASETID_IDX
--------------------------------------------------------

  CREATE INDEX METHYL_VALUE_DATASETID_IDX ON METHYLATION_VALUE (DATA_SET_ID) 
  ;
--------------------------------------------------------
--  DDL for Index METHYL_VALUE_HYBREFID_IDX
--------------------------------------------------------

  CREATE INDEX METHYL_VALUE_HYBREFID_IDX ON METHYLATION_VALUE (HYBRIDIZATION_REF_ID) 
  ;
--------------------------------------------------------
--  DDL for Index PORTION_SAMPLE_IDX
--------------------------------------------------------

  CREATE INDEX PORTION_SAMPLE_IDX ON PORTION (SAMPLE_ID) 
  ;
--------------------------------------------------------
--  DDL for Index PROTOCOL_ANALYTE_IDX
--------------------------------------------------------

  CREATE INDEX PROTOCOL_ANALYTE_IDX ON PROTOCOL (ANALYTE_ID) 
  ;
--------------------------------------------------------
--  DDL for Index RADIATION_PATIENT_ID
--------------------------------------------------------

  CREATE INDEX RADIATION_PATIENT_ID ON RADIATION (PATIENT_ID) 
  ;
--------------------------------------------------------
--  DDL for Index RNA_ANALYTE_IDX
--------------------------------------------------------

  CREATE INDEX RNA_ANALYTE_IDX ON RNA (ANALYTE_ID) 
  ;
--------------------------------------------------------
--  DDL for Index SAMPLE_PATIENT_IDX
--------------------------------------------------------

  CREATE INDEX SAMPLE_PATIENT_IDX ON SAMPLE (PATIENT_ID) 
  ;
--------------------------------------------------------
--  DDL for Index SURGERY_PATIENT_IDX
--------------------------------------------------------

  CREATE INDEX SURGERY_PATIENT_IDX ON SURGERY (PATIENT_ID) 
  ;
--------------------------------------------------------
--  Ref Constraints for Table ALIQUOT
--------------------------------------------------------

  ALTER TABLE ALIQUOT ADD CONSTRAINT FK_ALIQUOT_ANALYTE FOREIGN KEY (ANALYTE_ID)
	  REFERENCES ANALYTE (ANALYTE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ANALYTE
--------------------------------------------------------

  ALTER TABLE ANALYTE ADD CONSTRAINT FK_ANALYTE_PORTION FOREIGN KEY (PORTION_ID)
	  REFERENCES PORTION (PORTION_ID) ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table BIOCARTA_GENE
--------------------------------------------------------

  ALTER TABLE BIOCARTA_GENE ADD CONSTRAINT BIOCARTA_GENE_GENE_FK FOREIGN KEY (GENE_ID)
	  REFERENCES GENE (GENE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table BIOCARTA_GENE_PATHWAY
--------------------------------------------------------

  ALTER TABLE BIOCARTA_GENE_PATHWAY ADD CONSTRAINT GENE_PATHWAY_GENE_FK FOREIGN KEY (BIOCARTA_GENE_ID)
	  REFERENCES BIOCARTA_GENE (BIOCARTA_GENE_ID) ENABLE;
 
  ALTER TABLE BIOCARTA_GENE_PATHWAY ADD CONSTRAINT GENE_PATHWAY_PATHWAY_FK FOREIGN KEY (PATHWAY_ID)
	  REFERENCES PATHWAY (PATHWAY_ID) ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table CLINICAL_FILE
--------------------------------------------------------
 ALTER TABLE clinical_file ADD (
 CONSTRAINT fk_clin_file_dynamic_table
 FOREIGN KEY (clinical_table_id)
 REFERENCES clinical_table(clinical_table_id)
);

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
--  Ref Constraints for Table CLINICAL_FILE_TO_TABLE
--------------------------------------------------------
ALTER TABLE CLINICAL_FILE_TO_TABLE ADD (
CONSTRAINT fk_clinical_file_table
FOREIGN KEY (clinical_table_id) REFERENCES clinical_table(clinical_table_id),
CONSTRAINT fk_clinical_file_file
FOREIGN KEY (clinical_file_id) REFERENCES clinical_file(clinical_file_id));

--------------------------------------------------------
--  Ref Constraints for Table CLINICAL_XSD_ENUM_VALUE
--------------------------------------------------------

  ALTER TABLE CLINICAL_XSD_ENUM_VALUE ADD CONSTRAINT FK_CLIN_XSD_ENUM_XSD_ELEM FOREIGN KEY (XSD_ELEMENT_ID)
	  REFERENCES CLINICAL_XSD_ELEMENT (CLINICAL_XSD_ELEMENT_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table CNA_VALUE
--------------------------------------------------------

  ALTER TABLE CNA_VALUE ADD CONSTRAINT FK_CNA_VALUE_DATASET FOREIGN KEY (DATA_SET_ID)
	  REFERENCES DATA_SET (DATA_SET_ID) ENABLE;
 
  ALTER TABLE CNA_VALUE ADD CONSTRAINT FK_CNA_VALUE_HYBREF_ID FOREIGN KEY (HYBRIDIZATION_REF_ID)
	  REFERENCES HYBRIDIZATION_REF (HYBRIDIZATION_REF_ID) ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table DATA_SET
--------------------------------------------------------

  ALTER TABLE DATA_SET ADD CONSTRAINT FK_DATASET_CENTER FOREIGN KEY (CENTER_ID)
	  REFERENCES CENTER (CENTER_ID) ENABLE;
 
  ALTER TABLE DATA_SET ADD CONSTRAINT FK_DATASET_PLATFORM FOREIGN KEY (PLATFORM_ID)
	  REFERENCES PLATFORM (PLATFORM_ID) ENABLE;
 
  ALTER TABLE DATA_SET ADD CONSTRAINT FK_DATA_SET_EXPERIMENT FOREIGN KEY (EXPERIMENT_ID)
	  REFERENCES EXPERIMENT (EXPERIMENT_ID) ENABLE;
  ALTER TABLE DATA_SET ADD CONSTRAINT FK_DATA_SET_ARCHIVE FOREIGN KEY (ARCHIVE_ID)
	  REFERENCES ARCHIVE_INFO (ARCHIVE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table DATA_SET_FILE
--------------------------------------------------------

  ALTER TABLE DATA_SET_FILE ADD CONSTRAINT FK_DATASET_FILE_DATASET FOREIGN KEY (DATA_SET_ID)
	  REFERENCES DATA_SET (DATA_SET_ID) ENABLE;

  ALTER TABLE DATA_SET_FILE ADD CONSTRAINT FK_DATASET_FILE_FILE FOREIGN KEY (FILE_ID)
	  REFERENCES FILE_INFO (FILE_ID) ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table DNA
--------------------------------------------------------

  ALTER TABLE DNA ADD CONSTRAINT FK_DNA_ANALYTE FOREIGN KEY (ANALYTE_ID)
	  REFERENCES ANALYTE (ANALYTE_ID) ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table DRUG_CONCEPT_CODE
--------------------------------------------------------

  ALTER TABLE DRUG_CONCEPT_CODE ADD CONSTRAINT DRUG_CONCEPT_CODE_DRUG_FK FOREIGN KEY (DRUG_ID)
	  REFERENCES DRUG (DRUG_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table DRUG_INTGEN
--------------------------------------------------------

  ALTER TABLE DRUG_INTGEN ADD CONSTRAINT FK_DRUGINTGEN_PATIENTID FOREIGN KEY (PATIENT_ID)
	  REFERENCES PATIENT (PATIENT_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table EXAMINATION
--------------------------------------------------------

  ALTER TABLE EXAMINATION ADD CONSTRAINT FK_EXAMINATION_PATIENT FOREIGN KEY (PATIENT_ID)
	  REFERENCES PATIENT (PATIENT_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table EXPERIMENT
--------------------------------------------------------

  ALTER TABLE EXPERIMENT ADD CONSTRAINT FK_EXPERIMENT_CENTER FOREIGN KEY (CENTER_ID)
	  REFERENCES CENTER (CENTER_ID) ENABLE;
 
  ALTER TABLE EXPERIMENT ADD CONSTRAINT FK_EXPERIMENT_PLATFORM FOREIGN KEY (PLATFORM_ID)
	  REFERENCES PLATFORM (PLATFORM_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table EXPGENE_VALUE
--------------------------------------------------------

  ALTER TABLE EXPGENE_VALUE ADD CONSTRAINT FK_EXPGENE_VALUE_DATASET FOREIGN KEY (DATA_SET_ID)
	  REFERENCES DATA_SET (DATA_SET_ID) ENABLE;
 
  ALTER TABLE EXPGENE_VALUE ADD CONSTRAINT FK_EXPGENE_VALUE_HYBREF_ID FOREIGN KEY (HYBRIDIZATION_REF_ID)
	  REFERENCES HYBRIDIZATION_REF (HYBRIDIZATION_REF_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table GENE_DRUG
--------------------------------------------------------

  ALTER TABLE GENE_DRUG ADD CONSTRAINT GENE_DRUG_DRUG_FK FOREIGN KEY (DRUG_ID)
	  REFERENCES DRUG (DRUG_ID) ENABLE;
 
  ALTER TABLE GENE_DRUG ADD CONSTRAINT GENE_DRUG_GENE_PK FOREIGN KEY (GENE_ID)
	  REFERENCES GENE (GENE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table HYBRID_REF_DATA_SET
--------------------------------------------------------

  ALTER TABLE HYBRID_REF_DATA_SET ADD CONSTRAINT FK_HYBREF_DATASET_DATASETID FOREIGN KEY (DATA_SET_ID)
	  REFERENCES DATA_SET (DATA_SET_ID) ENABLE;
 
  ALTER TABLE HYBRID_REF_DATA_SET ADD CONSTRAINT FK_HYBREF_DATASET_HYBREFID FOREIGN KEY (HYBRIDIZATION_REF_ID)
	  REFERENCES HYBRIDIZATION_REF (HYBRIDIZATION_REF_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table L4_ANOMALY_DATA_SET
--------------------------------------------------------

  ALTER TABLE L4_ANOMALY_DATA_SET ADD CONSTRAINT FK_ANOMALY_DATA_S_ANOMALY_TYPE FOREIGN KEY (ANOMALY_TYPE_ID)
	  REFERENCES L4_ANOMALY_TYPE (ANOMALY_TYPE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table L4_ANOMALY_DATA_SET_VERSION
--------------------------------------------------------

  ALTER TABLE L4_ANOMALY_DATA_SET_VERSION ADD CONSTRAINT FK_ANOMALY_DATA_SET FOREIGN KEY (ANOMALY_DATA_SET_ID)
	  REFERENCES L4_ANOMALY_DATA_SET (ANOMALY_DATA_SET_ID) ENABLE;
 
  ALTER TABLE L4_ANOMALY_DATA_SET_VERSION ADD CONSTRAINT FK_ANOMALY_DATA_VERSION FOREIGN KEY (ANOMALY_DATA_VERSION_ID)
	  REFERENCES L4_ANOMALY_DATA_VERSION (ANOMALY_DATA_VERSION_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table L4_ANOMALY_DATA_VERSION
--------------------------------------------------------

  ALTER TABLE L4_ANOMALY_DATA_VERSION ADD CONSTRAINT FK_ANOMALY_DATA_VERSIO_DISEASE FOREIGN KEY (DISEASE_ID)
	  REFERENCES DISEASE (DISEASE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table L4_ANOMALY_TYPE
--------------------------------------------------------

  ALTER TABLE L4_ANOMALY_TYPE ADD CONSTRAINT FK_ANOMALY_TYPE_CENTER FOREIGN KEY (CENTER_ID)
	  REFERENCES CENTER (CENTER_ID) ENABLE;
 
  ALTER TABLE L4_ANOMALY_TYPE ADD CONSTRAINT FK_ANOMALY_TYPE_DATA_TYPE FOREIGN KEY (DATA_TYPE_ID)
	  REFERENCES DATA_TYPE (DATA_TYPE_ID) ENABLE;
 
  ALTER TABLE L4_ANOMALY_TYPE ADD CONSTRAINT FK_ANOMALY_TYPE_GE_TYPE FOREIGN KEY (GENETIC_ELEMENT_TYPE_ID)
	  REFERENCES L4_GENETIC_ELEMENT_TYPE (GENETIC_ELEMENT_TYPE_ID) ENABLE;
 
  ALTER TABLE L4_ANOMALY_TYPE ADD CONSTRAINT FK_ANOMALY_TYPE_PLATFORM_TYPE FOREIGN KEY (PLATFORM_ID)
	  REFERENCES PLATFORM (PLATFORM_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table L4_ANOMALY_VALUE
--------------------------------------------------------

  ALTER TABLE L4_ANOMALY_VALUE ADD CONSTRAINT FK_ANOMALYVALUE_ANOMDATASETID FOREIGN KEY (ANOMALY_DATA_SET_ID)
	  REFERENCES L4_ANOMALY_DATA_SET (ANOMALY_DATA_SET_ID) ENABLE;
 
  ALTER TABLE L4_ANOMALY_VALUE ADD CONSTRAINT FK_ANOMALYVALUE_GENETICELEMID FOREIGN KEY (GENETIC_ELEMENT_ID)
	  REFERENCES L4_GENETIC_ELEMENT (GENETIC_ELEMENT_ID) ENABLE;
 
  ALTER TABLE L4_ANOMALY_VALUE ADD CONSTRAINT FK_ANOMALYVALUE_SAMPLEID FOREIGN KEY (SAMPLE_ID)
	  REFERENCES L4_SAMPLE (SAMPLE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table L4_CORRELATION_TYPE
--------------------------------------------------------

  ALTER TABLE L4_CORRELATION_TYPE ADD CONSTRAINT FK_CORRELATION_ANOMALY_TYPE1 FOREIGN KEY (ANOMALY_TYPE_ID_1)
	  REFERENCES L4_ANOMALY_TYPE (ANOMALY_TYPE_ID) ENABLE;
 
  ALTER TABLE L4_CORRELATION_TYPE ADD CONSTRAINT FK_CORRELATION_ANOMALY_TYPE2 FOREIGN KEY (ANOMALY_TYPE_ID_2)
	  REFERENCES L4_ANOMALY_TYPE (ANOMALY_TYPE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table L4_DATA_SET_GENETIC_ELEMENT
--------------------------------------------------------

  ALTER TABLE L4_DATA_SET_GENETIC_ELEMENT ADD CONSTRAINT FK_DATA_SET_GE_DS FOREIGN KEY (ANOMALY_DATA_SET_ID)
	  REFERENCES L4_ANOMALY_DATA_SET (ANOMALY_DATA_SET_ID) ENABLE;
 
  ALTER TABLE L4_DATA_SET_GENETIC_ELEMENT ADD CONSTRAINT FK_DATA_SET_GE_GE FOREIGN KEY (GENETIC_ELEMENT_ID)
	  REFERENCES L4_GENETIC_ELEMENT (GENETIC_ELEMENT_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table L4_DATA_SET_SAMPLE
--------------------------------------------------------

  ALTER TABLE L4_DATA_SET_SAMPLE ADD CONSTRAINT FK_DATA_SET_SAMPLE_ANOMALY_DS FOREIGN KEY (ANOMALY_DATA_SET_ID)
	  REFERENCES L4_ANOMALY_DATA_SET (ANOMALY_DATA_SET_ID) ENABLE;
 
  ALTER TABLE L4_DATA_SET_SAMPLE ADD CONSTRAINT FK_L4_DATA_SET_SAMPL_L4_SAMPLE FOREIGN KEY (SAMPLE_ID)
	  REFERENCES L4_SAMPLE (SAMPLE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table L4_GENETIC_ELEMENT
--------------------------------------------------------

  ALTER TABLE L4_GENETIC_ELEMENT ADD CONSTRAINT FK_GENETIC_ELEMENT_GENE FOREIGN KEY (GENE_ID)
	  REFERENCES GENE (GENE_ID) ENABLE;
 
  ALTER TABLE L4_GENETIC_ELEMENT ADD CONSTRAINT FK_GENETIC_ELEMENT_TYPE FOREIGN KEY (GENETIC_ELEMENT_TYPE_ID)
	  REFERENCES L4_GENETIC_ELEMENT_TYPE (GENETIC_ELEMENT_TYPE_ID) ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table L4_SAMPLE
--------------------------------------------------------

  ALTER TABLE L4_SAMPLE ADD CONSTRAINT FK_PATIENT_ID_SAMPLE FOREIGN KEY (PATIENT_ID)
	  REFERENCES L4_PATIENT (PATIENT_ID) ENABLE;
 
  ALTER TABLE L4_SAMPLE ADD CONSTRAINT FK_SAMPLE_DISEASE FOREIGN KEY (DISEASE_ID)
	  REFERENCES DISEASE (DISEASE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table L4_TARGET
--------------------------------------------------------

  ALTER TABLE L4_TARGET ADD CONSTRAINT FK_TARGET_SOURCE_FK FOREIGN KEY (SOURCE_GENETIC_ELEMENT_ID)
	  REFERENCES L4_GENETIC_ELEMENT (GENETIC_ELEMENT_ID) ENABLE;
 
  ALTER TABLE L4_TARGET ADD CONSTRAINT FK_TARGET_TARGET_FK FOREIGN KEY (TARGET_GENETIC_ELEMENT_ID)
	  REFERENCES L4_GENETIC_ELEMENT (GENETIC_ELEMENT_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table MAF_INFO
--------------------------------------------------------

  ALTER TABLE MAF_INFO ADD CONSTRAINT FK_MAF_INFO_CENTER FOREIGN KEY (CENTER_ID)
	  REFERENCES CENTER (CENTER_ID) ENABLE;
 
  ALTER TABLE MAF_INFO ADD CONSTRAINT FK_MAF_INFO_FILE FOREIGN KEY (FILE_ID)
	  REFERENCES FILE_INFO (FILE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table METHYLATION_VALUE
--------------------------------------------------------

  ALTER TABLE METHYLATION_VALUE ADD CONSTRAINT FK_METHYLATION_VALUE_DATA_SET FOREIGN KEY (DATA_SET_ID)
	  REFERENCES DATA_SET (DATA_SET_ID) ENABLE;
 
  ALTER TABLE METHYLATION_VALUE ADD CONSTRAINT FK_METH_VALUE_HYBREF_ID FOREIGN KEY (HYBRIDIZATION_REF_ID)
	  REFERENCES HYBRIDIZATION_REF (HYBRIDIZATION_REF_ID) ENABLE;


--------------------------------------------------------
--  Ref Constraints for Table PLATFORM
--------------------------------------------------------

  ALTER TABLE PLATFORM ADD CONSTRAINT FK_PLATFORM_CENTER_TYPE FOREIGN KEY (CENTER_TYPE_CODE)
	  REFERENCES CENTER_TYPE (CENTER_TYPE_CODE) ENABLE;
 
  ALTER TABLE PLATFORM ADD CONSTRAINT FK_PLATFORM_DATA_TYPE FOREIGN KEY (BASE_DATA_TYPE_ID)
	  REFERENCES DATA_TYPE (DATA_TYPE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PORTAL_ACTION_TYPE
--------------------------------------------------------

  ALTER TABLE PORTAL_ACTION_TYPE ADD CONSTRAINT FK_PORTAL_ACTION_PORTAL_ACTION FOREIGN KEY (PORTAL_ACTION_TYPE_PARENT)
	  REFERENCES PORTAL_ACTION_TYPE (PORTAL_ACTION_TYPE_ID) ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table PORTAL_SESSION_ACTION
--------------------------------------------------------

  ALTER TABLE PORTAL_SESSION_ACTION ADD CONSTRAINT FK_PORTAL_SESSIO_PORTAL_ACTION FOREIGN KEY (PORTAL_ACTION_TYPE_ID)
	  REFERENCES PORTAL_ACTION_TYPE (PORTAL_ACTION_TYPE_ID) ENABLE;
 
  ALTER TABLE PORTAL_SESSION_ACTION ADD CONSTRAINT FK_PORTAL_SESSIO_PORTAL_SESSIO FOREIGN KEY (PORTAL_SESSION_ID)
	  REFERENCES PORTAL_SESSION (PORTAL_SESSION_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PORTION
--------------------------------------------------------

  ALTER TABLE PORTION ADD CONSTRAINT FK_PORTION_SAMPLE FOREIGN KEY (SAMPLE_ID)
	  REFERENCES SAMPLE (SAMPLE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PROTOCOL
--------------------------------------------------------

  ALTER TABLE PROTOCOL ADD CONSTRAINT FK_PROTOCOL_ANALYTE FOREIGN KEY (ANALYTE_ID)
	  REFERENCES ANALYTE (ANALYTE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table RADIATION
--------------------------------------------------------

  ALTER TABLE RADIATION ADD CONSTRAINT FK_RADIATION_PATIENTID FOREIGN KEY (PATIENT_ID)
	  REFERENCES PATIENT (PATIENT_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table RNA
--------------------------------------------------------

  ALTER TABLE RNA ADD CONSTRAINT FK_RNA_ANALYTE FOREIGN KEY (ANALYTE_ID)
	  REFERENCES ANALYTE (ANALYTE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table SAMPLE
--------------------------------------------------------

  ALTER TABLE SAMPLE ADD CONSTRAINT FK_SAMPLE_PATIENTID FOREIGN KEY (PATIENT_ID)
	  REFERENCES PATIENT (PATIENT_ID) ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table SUMMARY_BY_GENE
--------------------------------------------------------

  ALTER TABLE SUMMARY_BY_GENE ADD CONSTRAINT SUMMARY_BY_GENE_GENE_FK FOREIGN KEY (GENE_ID)
	  REFERENCES GENE (GENE_ID) ENABLE;
 
  ALTER TABLE SUMMARY_BY_GENE ADD CONSTRAINT S_B_G_ANOMALY_TYPE_FK FOREIGN KEY (ANOMALY_TYPE_ID)
	  REFERENCES ANOMALY_TYPE (ANOMALY_TYPE_ID) ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table SURGERY
--------------------------------------------------------

  ALTER TABLE SURGERY ADD CONSTRAINT FK_SURGERY_PATIENTID FOREIGN KEY (PATIENT_ID)
	  REFERENCES PATIENT (PATIENT_ID) ENABLE;

--------------------------------------------------------
--  Ref Constraints for Table TUMORPATHOLOGY
--------------------------------------------------------

  ALTER TABLE TUMORPATHOLOGY ADD CONSTRAINT FK_TUMORPATH_SAMPLE FOREIGN KEY (SAMPLE_ID)
	  REFERENCES SAMPLE (SAMPLE_ID) ENABLE;

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


ALTER TABLE clinical_table ADD (
CONSTRAINT fk_clinical_table_table 
FOREIGN KEY (parent_table_id) REFERENCES clinical_table(clinical_table_id),
CONSTRAINT fk_clin_table_uuid_xsd_elem
FOREIGN KEY (uuid_element_id)
REFERENCES clinical_xsd_element(clinical_xsd_element_id),
CONSTRAINT fk_clinical_table_barcode 
FOREIGN KEY (barcode_element_id) 
REFERENCES clinical_xsd_element(clinical_xsd_element_id));


--------------------------------------------------------
--  DDL for View DATA_SET_V
--------------------------------------------------------

  CREATE OR REPLACE VIEW DATA_SET_V (DATA_SET_ID, EXPERIMENT_ID, SOURCE_FILE_NAME, SOURCE_FILE_TYPE, ACCESS_LEVEL, DATA_LEVEL, PLATFORM_ID, CENTER_ID) AS 
  SELECT   data_set_id,
            experiment_id,
            source_file_name,
            source_file_type,
            access_level,
            data_level,
            platform_id,
            center_id
     FROM   data_set
    WHERE   use_in_dam = 1 AND load_complete = 1
 ;
--------------------------------------------------------
--  DDL for View DISEASE_V
--------------------------------------------------------

  CREATE OR REPLACE VIEW DISEASE_V (DISEASE_ID, ABBREVIATION, NAME) AS 
  SELECT   disease_id, disease_abbreviation, disease_name
     FROM   disease
    WHERE   active = 1
 ;
--------------------------------------------------------
--  DDL for View PATHWAY_TO_GENE
--------------------------------------------------------

  CREATE OR REPLACE VIEW PATHWAY_TO_GENE (ENTREZ_ID, GENE_COMMENTS, ENTREZ_SYMBOL, BIOCARTA_SYMBOL, BIOCARTAGENE_COMMENTS, SVG_FILE_NAME, SVG_IDENTIFIER, SVG_DISPLAY_NAME, PATHWAY_COMMENTS) AS 
  SELECT   g.gene_id AS entrez_id,
            g.comments AS gene_comments,
            g.entrez_symbol,
            bg.biocarta_symbol,
            bg.comments AS biocartagene_comments,
            p.svg_file_name,
            p.svg_identifier,
            p.display_name AS svg_display_name,
            p.comments AS pathway_comments
     FROM   gene g,
            biocarta_gene bg,
            biocarta_gene_pathway bgp,
            pathway p
    WHERE       g.gene_id = bg.gene_id
            AND bg.biocarta_gene_id = bgp.biocarta_gene_id
            AND bgp.pathway_id = p.pathway_id
   WITH READ ONLY
 ;
--------------------------------------------------------
--  DDL for View SBG_MART
--------------------------------------------------------

  CREATE OR REPLACE VIEW SBG_MART (ENTREZ_ID, BIOCARTA_SYMBOL, SVG_FILE_NAME, SVG_IDENTIFIER, SVG_DISPLAY_NAME, DRUG_NAME, DRUG_CONCEPT_CODE, OVEREXPRESSED_AFFY, UNDEREXPRESSED_AFFY, MUTATED, AMPLIFIED, DELETED, IS_OVEREXPRESSED_AFFY, IS_UNDEREXPRESSED_AFFY, IS_MUTATED, IS_AMPLIFIED, IS_DELETED) AS 
  SELECT   g.gene_id AS entrez_id,
            bg.biocarta_symbol,
            p.svg_file_name,
            p.svg_identifier,
            p.display_name AS svg_display_name,
            d.name AS drug_name,
            dcc.concept_code AS drug_concept_code,
            COALESCE (
               ROUND ( (s_affyUP.cases_detected / s_affyUP.cases_probed), 4),
               NULL
            )
               AS overexpressed_affy,
            COALESCE (
               ROUND ( (s_affyDOWN.cases_detected / s_affyDOWN.cases_probed),
                      4),
               NULL
            )
               AS underexpressed_affy,
            COALESCE (
               ROUND ( (mutation.cases_detected / mutation.cases_probed), 4),
               NULL
            )
               AS mutated,
            COALESCE (
               ROUND ( (amplified.cases_detected / amplified.cases_probed),
                      4),
               NULL
            )
               AS amplified,
            COALESCE (
               ROUND ( (deleted.cases_detected / deleted.cases_probed), 4),
               NULL
            )
               AS deleted,
            (CASE
                WHEN s_affyUP.cases_detected / s_affyUP.cases_probed >=
                        a_affyUP.patient_threshold
                THEN
                   'TRUE'
                ELSE
                   'FALSE'
             END)
               AS is_overexpressed_affy,
            (CASE
                WHEN s_affyDOWN.cases_detected / s_affyDOWN.cases_probed >=
                        a_affyDOWN.patient_threshold
                THEN
                   'TRUE'
                ELSE
                   'FALSE'
             END)
               AS is_underexpressed_affy,
            (CASE
                WHEN mutation.cases_detected / mutation.cases_probed >=
                        a_mutated.patient_threshold
                THEN
                   'TRUE'
                ELSE
                   'FALSE'
             END)
               AS is_mutated,
            (CASE
                WHEN amplified.cases_detected / amplified.cases_probed >=
                        a_amplified.patient_threshold
                THEN
                   'TRUE'
                ELSE
                   'FALSE'
             END)
               AS is_amplified,
            (CASE
                WHEN deleted.cases_detected / deleted.cases_probed >=
                        a_deleted.patient_threshold
                THEN
                   'TRUE'
                ELSE
                   'FALSE'
             END)
               AS is_deleted
     FROM   gene g,
            biocarta_gene bg,
            biocarta_gene_pathway bgp,
            pathway p,
            gene_drug gd,
            drug d,
            drug_concept_code dcc,
            summary_by_gene s_affyUP,
            summary_by_gene s_affyDOWN,
            summary_by_gene mutation,
            summary_by_gene amplified,
            summary_by_gene deleted,
            anomaly_type a_affyUP,
            anomaly_type a_affyDOWN,
            anomaly_type a_mutated,
            anomaly_type a_amplified,
            anomaly_type a_deleted
    WHERE       g.gene_id = bg.gene_id
            AND bg.biocarta_gene_id = bgp.biocarta_gene_id(+)
            AND bgp.pathway_id = p.pathway_id(+)
            AND g.gene_id = gd.gene_id(+)
            AND gd.drug_id = d.drug_id(+)
            AND d.drug_id = dcc.drug_id(+)
            AND g.gene_id = s_affyUP.gene_id(+)
            AND g.gene_id = s_affyDOWN.gene_id(+)
            AND g.gene_id = mutation.gene_id(+)
            AND g.gene_id = amplified.gene_id(+)
            AND g.gene_id = deleted.gene_id(+)
            AND s_affyUP.anomaly_type_id = a_affyUP.anomaly_type_id(+)
            AND s_affyDOWN.anomaly_type_id = a_affyDOWN.anomaly_type_id(+)
            AND mutation.anomaly_type_id = a_mutated.anomaly_type_id(+)
            AND deleted.anomaly_type_id = a_deleted.anomaly_type_id(+)
            AND amplified.anomaly_type_id = a_amplified.anomaly_type_id(+)
            AND s_affyUP.anomaly_type_id(+) = 2
            AND s_affyDOWN.anomaly_type_id(+) = 3
            AND mutation.anomaly_type_id(+) = 1
            AND amplified.anomaly_type_id(+) = 4
            AND deleted.anomaly_type_id(+) = 5
   WITH READ ONLY
 ;
--------------------------------------------------------
--  DDL for View NUMBER_ANOMALIES_GENE
--------------------------------------------------------

  CREATE OR REPLACE VIEW NUMBER_ANOMALIES_GENE (ENTREZ_ID, ANOMALY_NUMBER) AS 
  SELECT   entrez_id,
            (  DECODE (IS_OVEREXPRESSED_AFFY, 'TRUE', 1, 0)
             + DECODE (IS_UNDEREXPRESSED_AFFY, 'TRUE', 1, 0)
             + DECODE (IS_MUTATED, 'TRUE', 1, 0)
             + DECODE (IS_AMPLIFIED, 'TRUE', 1, 0)
             + DECODE (IS_DELETED, 'TRUE', 1, 0))
               AS anomaly_number
     FROM   (SELECT   DISTINCT entrez_id,
                               overexpressed_affy,
                               underexpressed_affy,
                               mutated,
                               amplified,
                               deleted,
                               is_overexpressed_affy,
                               is_underexpressed_affy,
                               is_mutated,
                               is_amplified,
                               is_deleted
               FROM   sbg_mart) x
   WITH READ ONLY
 ;
--------------------------------------------------------
--  DDL for View SBG_PATIENT_PERCENTAGE
--------------------------------------------------------

  CREATE OR REPLACE VIEW SBG_PATIENT_PERCENTAGE (ANOMALY, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, TWENTY, THIRTY, FORTY, FIFTY, SIXTY, SEVENTY, EIGHTY, NINETY, HUNDRED) AS 
  SELECT   anomaly,
              SUM (CASE WHEN patientRatio >= 0.01 THEN cg ELSE 0 END) one,
              SUM (CASE WHEN patientRatio >= 0.02 THEN cg ELSE 0 END) two,
              SUM (CASE WHEN patientRatio >= 0.03 THEN cg ELSE 0 END) three,
              SUM (CASE WHEN patientRatio >= 0.04 THEN cg ELSE 0 END) four,
              SUM (CASE WHEN patientRatio >= 0.05 THEN cg ELSE 0 END) five,
              SUM (CASE WHEN patientRatio >= 0.06 THEN cg ELSE 0 END) six,
              SUM (CASE WHEN patientRatio >= 0.07 THEN cg ELSE 0 END) seven,
              SUM (CASE WHEN patientRatio >= 0.08 THEN cg ELSE 0 END) eight,
              SUM (CASE WHEN patientRatio >= 0.09 THEN cg ELSE 0 END) nine,
              SUM (CASE WHEN patientRatio >= 0.1 THEN cg ELSE 0 END) ten,
              SUM (CASE WHEN patientRatio >= 0.2 THEN cg ELSE 0 END) twenty,
              SUM (CASE WHEN patientRatio >= 0.3 THEN cg ELSE 0 END) thirty,
              SUM (CASE WHEN patientRatio >= 0.4 THEN cg ELSE 0 END) forty,
              SUM (CASE WHEN patientRatio >= 0.5 THEN cg ELSE 0 END) fifty,
              SUM (CASE WHEN patientRatio >= 0.6 THEN cg ELSE 0 END) sixty,
              SUM (CASE WHEN patientRatio >= 0.7 THEN cg ELSE 0 END) seventy,
              SUM (CASE WHEN patientRatio >= 0.8 THEN cg ELSE 0 END) eighty,
              SUM (CASE WHEN patientRatio >= 0.9 THEN cg ELSE 0 END) ninety,
              SUM (CASE WHEN patientRatio >= 1 THEN cg ELSE 0 END) hundred
       FROM   (  SELECT   anomaly,
                          COUNT (gene_id) cg,
                          ROUND (cases_detected / cases_probed, 2)
                             AS patientRatio
                   FROM   summary_by_gene sbg, anomaly_type at
                  WHERE   sbg.anomaly_type_id = at.anomaly_type_id
               GROUP BY   anomaly, ROUND (cases_detected / cases_probed, 2))
   GROUP BY   anomaly
   WITH READ ONLY
 ;

CREATE OR REPLACE VIEW biospecimen_breakdown_all AS 
 SELECT biospecimen_id, 
        barcode, 
        project_code || '-' || tss_code || '-' || patient || '-' || sample_type_code || sample_sequence || '-' || portion_sequence || portion_analyte_code AS biospecimen, 
        project_code || '-' || tss_code || '-' || patient || '-' || sample_type_code || '-' || portion_analyte_code AS analyte, 
        project_code || '-' || tss_code || '-' || patient || '-' || sample_type_code AS sample, 
        project_code || '-' || tss_code || '-' || patient AS specific_patient,
        project_code,
        tss_code,
        patient,
        sample_type_code,
        sample_sequence,
        portion_sequence,
        portion_analyte_code,
        plate_id,
        bcr_center_id,
        is_valid,
        is_viewable,
        ship_date
   FROM biospecimen_barcode;
   
CREATE OR REPLACE VIEW archive_v AS 
 SELECT a.archive_id, a.archive_name, a.center_id, a.disease_id, a.platform_id, a.serial_index, a.revision, 
        a.series, a.date_added, a.deploy_status, a.deploy_location, a.is_latest, 
        c.domain_name || '_' || d.disease_abbreviation || '_'  || p.platform_name AS archive_base_name,
        a.archive_type_id
   FROM archive_info a, center c, disease d, platform p
  WHERE a.center_id = c.center_id 
  AND   a.disease_id = d.disease_id 
  AND   a.platform_id = p.platform_id;
  
DROP SEQUENCE rnaseq_value_seq;
DROP SEQUENCE mirnaseq_value_seq;
CREATE SEQUENCE rnaseq_value_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE mirnaseq_value_seq START WITH 1 INCREMENT BY 1;


DROP TABLE rnaseq_value;
CREATE TABLE rnaseq_value (
	rnaseq_id			NUMBER(38,0)	NOT NULL,
	data_set_id			NUMBER(38,0)	NOT NULL,
	hybridization_ref_id		NUMBER(38,0)	NOT NULL,
	feature	        		VARCHAR2(50)	NOT NULL,
	raw_counts			BINARY_DOUBLE,
	median_length_normalized	BINARY_DOUBLE,
	rpkm				BINARY_DOUBLE,
	normalized_counts		FLOAT,
	scaled_estimate 		VARCHAR2(50),
	transcript_id 			VARCHAR2(4000),	
	CONSTRAINT pk_rnaseq_value_idx	PRIMARY KEY (rnaseq_id)
);

ALTER TABLE rnaseq_value ADD(
	CONSTRAINT fk_rnaseq_dataset
	FOREIGN KEY (data_set_id)
	REFERENCES data_set(data_set_id),
	CONSTRAINT fk_rnaseq_hybref
	FOREIGN KEY (hybridization_ref_id)
	REFERENCES hybridization_ref(hybridization_ref_id)
);

DROP TABLE mirnaseq_value;
CREATE TABLE mirnaseq_value (
	mirnaseq_id			NUMBER(38,0)	NOT NULL,
	data_set_id			NUMBER(38,0)	NOT NULL,
	hybridization_ref_id		NUMBER(38,0)	NOT NULL,
	feature				VARCHAR2(50)	NOT NULL,
	read_count			BINARY_DOUBLE	NOT NULL,
	reads_per_million		BINARY_DOUBLE	NOT NULL,
	cross_mapped			CHAR(1)		NOT NULL,
	isoform_coords			VARCHAR2(50),
	mirna_region_annotation		VARCHAR2(50),
	mirna_region_accession		VARCHAR2(50),
	CONSTRAINT pk_mirnaseq_value_idx PRIMARY KEY (mirnaseq_id)
);

ALTER TABLE mirnaseq_value ADD(
	CONSTRAINT fk_mirnaseq_dataset
	FOREIGN KEY (data_set_id)
	REFERENCES data_set(data_set_id),
	CONSTRAINT fk_mirnaseq_hybref
	FOREIGN KEY (hybridization_ref_id)
	REFERENCES hybridization_ref(hybridization_ref_id)
);

DROP TABLE shipped_item_type CASCADE CONSTRAINTS;
CREATE TABLE shipped_item_type (
    shipped_item_type_id    	INTEGER    	NOT NULL,
    shipped_item_type    	VARCHAR2(20)    NOT NULL,
    CONSTRAINT shipped_item_type_pk_idx PRIMARY KEY (shipped_item_type_id)
);

DROP TABLE shipped_biospecimen CASCADE CONSTRAINTS;
CREATE TABLE shipped_biospecimen (
    shipped_biospecimen_id    	NUMBER(38)	NOT NULL,
    uuid            		VARCHAR2(36)    NOT NULL,
    shipped_item_type_id    	INTEGER        	NOT NULL,
    built_barcode		VARCHAR2(50)    NOT NULL,
    project_code        	VARCHAR2(10)    NOT NULL,
    tss_code        		VARCHAR2(10)    NOT NULL,
    bcr_center_id        	VARCHAR2(10)    NOT NULL,
    participant_code    	VARCHAR2(25)    NOT NULL,
    is_viewable        		NUMBER(1)    	DEFAULT 1,
    is_redacted        		NUMBER(1)    	DEFAULT 0,
    shipped_date        	DATE,
    is_control			NUMBER(1)	DEFAULT 0,
    batch_id            INTEGER,
    CONSTRAINT shipped_biospecimen_pk_idx PRIMARY KEY (shipped_biospecimen_id)
);

CREATE UNIQUE INDEX uk_shipped_biospec_uuid_idx ON shipped_biospecimen(uuid);

DROP TABLE shipped_biospecimen_element;
CREATE TABLE shipped_biospecimen_element (
    shipped_biospecimen_element_id    	NUMBER(38)    	NOT NULL,
    shipped_biospecimen_id        	NUMBER(38)    	NOT NULL,
    element_type_id            		INTEGER        	NOT NULL,
    element_value            		VARCHAR2(100)	NOT NULL,
    CONSTRAINT shipped_biospec_element_pk_idx PRIMARY KEY (shipped_biospecimen_element_id)
);
CREATE UNIQUE INDEX shipped_biospec_element_uk_idx ON shipped_biospecimen_element (shipped_biospecimen_id,element_type_id);


DROP TABLE shipped_biospec_bcr_archive;
CREATE TABLE shipped_biospec_bcr_archive (
    shipped_biospecimen_id    	NUMBER(38) NOT NULL,
    archive_id        		NUMBER(38) NOT NULL,
    CONSTRAINT ship_biospec_archive_pk_idx PRIMARY KEY (shipped_biospecimen_id,archive_id) 
);

DROP TABLE shipped_biospecimen_file CASCADE CONSTRAINTS;
CREATE TABLE shipped_biospecimen_file (
    shipped_biospecimen_id    	NUMBER(38) NOT NULL,
    file_id            		NUMBER(38) NOT NULL,
    CONSTRAINT shipped_biospec_file_pk_idx PRIMARY KEY (shipped_biospecimen_id,file_id) 
);

DROP TABLE shipped_portion CASCADE CONSTRAINTS;
CREATE TABLE shipped_portion (
    shipped_portion_id    	NUMBER(38)    	NOT NULL,
    sample_id    		NUMBER(38)     	NOT NULL,
    shipped_portion_barcode	VARCHAR2(50)    NOT NULL,
    uuid            		VARCHAR2(36)	NOT NULL,
    CONSTRAINT shipped_portion_pk_idx PRIMARY KEY (shipped_portion_id)
);


DROP TABLE shipped_portion_element;
CREATE TABLE shipped_portion_element (
    shipped_portion_element_id		NUMBER(38)	NOT NULL,
    shipped_portion_id        		NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id            	NUMBER(38) 	NOT NULL,
    element_value            		VARCHAR2(4000),
    CONSTRAINT shipped_portion_element_pk_idx PRIMARY KEY (shipped_portion_element_id)
);


DROP TABLE shipped_portion_archive;
CREATE TABLE shipped_portion_archive (
    shipped_portion_archive_id	NUMBER(38) NOT NULL,
    shipped_portion_id    	NUMBER(38) NOT NULL,
    archive_id        		NUMBER(38) NOT NULL,
    CONSTRAINT ship_portion_archive_pk_idx PRIMARY KEY (shipped_portion_archive_id) 
);


ALTER TABLE shipped_portion ADD (
	CONSTRAINT fk_shipped_portion_sample
	FOREIGN KEY (sample_id)
	REFERENCES sample (sample_id)
);

ALTER TABLE shipped_portion_element ADD (
	CONSTRAINT fk_shipped_port_elem_port
	FOREIGN KEY (shipped_portion_id)
	REFERENCES shipped_portion (shipped_portion_id),
	CONSTRAINT fk_shipped_port_elem_elem
	FOREIGN KEY (clinical_xsd_element_id)
	REFERENCES clinical_xsd_element (clinical_xsd_element_id)
);

ALTER TABLE shipped_portion_archive ADD (
	CONSTRAINT fk_shipped_port_arch_portion
	FOREIGN KEY (shipped_portion_id)
	REFERENCES shipped_portion (shipped_portion_id),
	CONSTRAINT fk_shipped_portion_arc_archive
	FOREIGN KEY (archive_id)
	REFERENCES archive_info (archive_id)
);

DROP TABLE proteinexp_value;
CREATE TABLE proteinexp_value (
	proteinexp_id			NUMBER(38)	NOT NULL,
	data_set_id			NUMBER(38)	NOT NULL,
	hybridization_ref_id		NUMBER(38)	NOT NULL,
	antibody_name			VARCHAR2(500)	NOT NULL,
	hugo_gene_symbol		VARCHAR2(50),
	protein_expression_value	FLOAT	NOT NULL,
	CONSTRAINT pk_proteinexp_idx PRIMARY KEY (proteinexp_id)
);

ALTER TABLE proteinexp_value ADD (
	CONSTRAINT fk_proteinexp_dataset
	FOREIGN KEY (data_set_id)
	REFERENCES data_Set(data_set_id),
	CONSTRAINT fk_proteinexp_hybref
	FOREIGN KEY (hybridization_ref_id)
	REFERENCES hybridization_ref(hybridization_ref_id)
);
CREATE INDEX proteinexp_dataset_idx ON proteinexp_value(data_Set_id,hybridization_ref_id);

CREATE OR REPLACE FORCE VIEW shipped_biospecimen_aliquot
AS
   SELECT   b.shipped_biospecimen_id,
            b.built_barcode,
            b.project_code
            || '-'
            || b.tss_code
            || '-'
            || b.participant_code
            || '-'
            || be1.element_value
            || be2.element_value
            || '-'
            || be3.element_value
            || be4.element_value
               AS biospecimen,
               b.project_code
            || '-'
            || b.tss_code
            || '-'
            || b.participant_code
            || '-'
            || be1.element_value
            || '-'
            || be3.element_value
               AS analyte,
               b.project_code
            || '-'
            || b.tss_code
            || '-'
            || b.participant_code
            || '-'
            || be1.element_value
               AS sample,
            b.project_code || '-' || b.tss_code || '-' || b.participant_code
               AS specific_patient,
            b.project_code,
            b.tss_code,
            b.participant_code,
            be1.element_value as sample_type_code,
            be2.element_value as sample_sequence,
            be3.element_value as portion_sequence,
            be4.element_value as portion_analyte_code,
            be5.element_value as plate_id,
            b.bcr_center_id,
            b.is_redacted,
            b.is_viewable,
            b.shipped_date
     FROM   shipped_biospecimen b, shipped_biospecimen_element be1, shipped_biospecimen_element be2,
            shipped_biospecimen_element be3, shipped_biospecimen_element be4, shipped_biospecimen_element be5
     WHERE  b.shipped_item_type_id = 1
     AND    (b.shipped_biospecimen_id = be1.shipped_biospecimen_id and be1.element_type_id = 1)
     AND    (b.shipped_biospecimen_id = be2.shipped_biospecimen_id and be2.element_type_id = 2)
     AND    (b.shipped_biospecimen_id = be3.shipped_biospecimen_id and be3.element_type_id = 3)
     AND    (b.shipped_biospecimen_id = be4.shipped_biospecimen_id and be4.element_type_id = 4)
     AND    (b.shipped_biospecimen_id = be5.shipped_biospecimen_id and be5.element_type_id = 5);

CREATE OR REPLACE FORCE shipped_biospecimen_breakdown AS
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
            b.is_control,
            b.batch_id
     FROM   shipped_biospecimen b, shipped_biospecimen_element be1, shipped_biospecimen_element be2
     WHERE  (b.shipped_biospecimen_id = be1.shipped_biospecimen_id and be1.element_type_id = 1)
     AND    (b.shipped_biospecimen_id = be2.shipped_biospecimen_id and be2.element_type_id = 2);

DROP TABLE participant_uuid_file;
CREATE TABLE participant_uuid_file (
	uuid	VARCHAR2(36) 	NOT NULL,
	file_id	NUMBER(38,0)	NOT NULL,
	CONSTRAINT pk_part_uuid_file_idx PRIMARY KEY (uuid,file_id)
);


CREATE INDEX part_uuid_file_file_idx ON participant_uuid_file (file_id);

create unique index patient_archive_patient_idx on patient_archive(patient_id,archive_id);

purge recyclebin;