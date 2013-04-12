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
DROP SEQUENCE center_email_seq;
CREATE SEQUENCE center_email_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE center_seq;
CREATE SEQUENCE center_seq START WITH 20 INCREMENT BY 1;
DROP SEQUENCE platform_seq;
CREATE SEQUENCE platform_seq START WITH 50 INCREMENT BY 1;
DROP SEQUENCE disease_seq;
CREATE SEQUENCE disease_seq START WITH 20 INCREMENT BY 1;
DROP SEQUENCE tissue_seq;
CREATE SEQUENCE tissue_seq START WITH 10 INCREMENT BY 1;
DROP SEQUENCE tissue_disease_seq;
CREATE SEQUENCE tissue_disease_seq START WITH 10 INCREMENT BY 1;
DROP SEQUENCE gen_method_seq;
CREATE SEQUENCE gen_method_seq START WITH 10 INCREMENT BY 1;
DROP SEQUENCE uuid_seq;
CREATE SEQUENCE uuid_seq START WITH 10 INCREMENT BY 1;
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
DROP SEQUENCE center_email_seq;
CREATE SEQUENCE center_email_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE center_seq;
CREATE SEQUENCE center_seq START WITH 20 INCREMENT BY 1;
DROP SEQUENCE platform_seq;
CREATE SEQUENCE platform_seq START WITH 50 INCREMENT BY 1;
DROP SEQUENCE disease_seq;
CREATE SEQUENCE disease_seq START WITH 20 INCREMENT BY 1;
DROP SEQUENCE tissue_seq;
CREATE SEQUENCE tissue_seq START WITH 10 INCREMENT BY 1;
DROP SEQUENCE tissue_disease_seq;
CREATE SEQUENCE tissue_disease_seq START WITH 10 INCREMENT BY 1;
DROP SEQUENCE gen_method_seq;
CREATE SEQUENCE gen_method_seq START WITH 10 INCREMENT BY 1;
DROP SEQUENCE uuid_seq;
CREATE SEQUENCE uuid_seq START WITH 10 INCREMENT BY 1;
DROP SEQUENCE barcode_seq;
CREATE SEQUENCE barcode_seq START WITH 10 INCREMENT BY 1;
DROP SEQUENCE tss_disease_seq;
CREATE SEQUENCE tss_disease_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE biospecimen_barcode_seq;
CREATE SEQUENCE biospecimen_barcode_seq    START WITH 250000 INCREMENT BY 1;
DROP SEQUENCE biospecimen_disease_seq;
CREATE SEQUENCE biospecimen_disease_seq    START WITH 250000 INCREMENT BY 1;
DROP SEQUENCE visibility_seq;
CREATE SEQUENCE visibility_seq    START WITH 20 INCREMENT BY 1;
DROP SEQUENCE archive_seq;
CREATE SEQUENCE archive_seq    START WITH 2000 INCREMENT BY 1;
DROP SEQUENCE biospecimen_archive_seq;
CREATE SEQUENCE biospecimen_archive_seq    START WITH 1 INCREMENT BY 1;
DROP SEQUENCE biospecimen_trace_seq;
CREATE SEQUENCE biospecimen_trace_seq    START WITH 1 INCREMENT BY 1;
DROP SEQUENCE biospecimen_file_seq;
CREATE SEQUENCE biospecimen_file_seq    START WITH 1 INCREMENT BY 1;
DROP SEQUENCE data_visibility_seq;
CREATE SEQUENCE data_visibility_seq    START WITH 1 INCREMENT BY 1;
DROP SEQUENCE file_seq;
CREATE SEQUENCE file_seq    START WITH 150000 INCREMENT BY 1;
DROP SEQUENCE file_archive_seq;
CREATE SEQUENCE file_archive_seq    START WITH 1 INCREMENT BY 1;
DROP SEQUENCE hugo_gene_seq;
CREATE SEQUENCE hugo_gene_seq    START WITH 50000 INCREMENT BY 1;
DROP SEQUENCE orphan_seq;
CREATE SEQUENCE orphan_seq    START WITH 1500000 INCREMENT BY 1;
DROP SEQUENCE pi_info_seq;
CREATE SEQUENCE pi_info_seq    START WITH 144000 INCREMENT BY 1;
DROP SEQUENCE center_type_seq ;
CREATE SEQUENCE center_type_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE process_log_seq;
CREATE SEQUENCE process_log_seq START WITH 30000 INCREMENT BY 1;
DROP SEQUENCE report_seq;
CREATE SEQUENCE report_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE  data_type_id_seq;
CREATE SEQUENCE  data_type_id_seq START WITH 10 INCREMENT BY 1;
DROP SEQUENCE trace_info_seq;
CREATE SEQUENCE trace_info_seq START WITH 2500000000 increment by 1;
DROP SEQUENCE  data_type_platform_seq;
CREATE SEQUENCE  data_type_platform_seq START WITH 10 INCREMENT BY 1;
DROP SEQUENCE bam_file_seq;
DROP SEQUENCE biospecimen_bam_seq;
CREATE SEQUENCE bam_file_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE biospecimen_bam_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE file_collection_seq;
CREATE SEQUENCE file_collection_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE pending_uuid_seq;
CREATE SEQUENCE pending_uuid_seq START WITH 1 INCREMENT BY 1;

DROP  TABLE annotation_item_type CASCADE CONSTRAINTS;
CREATE TABLE annotation_item_type
(
  item_type_id               NUMBER(38)     NOT NULL,
  type_display_name          VARCHAR2(200)  NOT NULL,
  type_description           VARCHAR2(2000) NOT NULL,
  CONSTRAINT annotation_type_pk_idx PRIMARY KEY (item_type_id)
);


DROP TABLE annotation_classification CASCADE CONSTRAINTS;
CREATE TABLE annotation_classification (
    annotation_classification_id    NUMBER(38,0)    NOT NULL,
    classification_display_name        VARCHAR2(200)    NOT NULL,
    classification_description        VARCHAR2(2000),
    CONSTRAINT pk_annotation_class_idx PRIMARY KEY (annotation_classification_id)
);

DROP  TABLE annotation_category CASCADE CONSTRAINTS;
CREATE TABLE annotation_category
(
  annotation_category_id        NUMBER(38)     NOT NULL,
  category_display_name         VARCHAR2(200)  NOT NULL,
  category_description          VARCHAR2(2000) NOT NULL,
  caDSR_description             VARCHAR2(2000) ,
  annotation_classification_id    NUMBER(38)    NOT NULL,    
  CONSTRAINT annotation_category_pk_idx PRIMARY KEY (annotation_category_id)
);

ALTER TABLE annotation_category ADD (
   CONSTRAINT fk_annotation_category_class
   FOREIGN KEY (annotation_classification_id)
   REFERENCES annotation_classification(annotation_classification_id)
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
  modified_date          DATE,
  curated          NUMBER(1,0)   DEFAULT 0 NOT NULL,
  rescinded       NUMBER(1,0)   DEFAULT 0 NOT NULL,
  CONSTRAINT annotation_pk_idx PRIMARY KEY (annotation_id)
);

ALTER TABLE annotation ADD (
  CONSTRAINT fk_annotation_category_id 
  FOREIGN KEY (annotation_category_id) 
  REFERENCES annotation_category(annotation_category_id)
);

CREATE BITMAP INDEX annotation_curated_idx ON annotation(curated);
  

DROP  TABLE annotation_item CASCADE CONSTRAINTS;
CREATE TABLE annotation_item
(
  annotation_item_id      NUMBER(38)     NOT NULL,
  annotation_id           NUMBER(38)     NOT NULL,
  item_type_id            NUMBER(38)     NOT NULL,
  annotation_item         VARCHAR2(50)   NOT NULL,
  disease_id          NUMBER(38)     NOT NULL,
  CONSTRAINT annotation_item_pk_idx PRIMARY KEY (annotation_item_id)
);


ALTER TABLE annotation_item ADD (
  CONSTRAINT fk_annotitem_annotation_id 
  FOREIGN KEY (annotation_id) 
  REFERENCES annotation(annotation_id),
  CONSTRAINT fk_annotitem_type_id 
  FOREIGN KEY (item_type_id) 
  REFERENCES annotation_item_type(item_type_id),
  CONSTRAINT fk_annotation_item_disease
  FOREIGN KEY (disease_id)
  REFERENCES disease(disease_id)
);

CREATE INDEX annotation_item_annot_idx on annotation_item(annotation_id);

  
DROP  TABLE annotation_note CASCADE CONSTRAINTS;
CREATE TABLE annotation_note
(
  annotation_note_id      NUMBER(38)     NOT NULL,
  annotation_id           NUMBER(38)     NOT NULL,
  note                    VARCHAR2(4000) NOT NULL,
  entered_by              VARCHAR2(200)  NOT NULL,
  entered_date            DATE           NOT NULL,  
  modified_by          VARCHAR2(200),
  modified_date           DATE,
  CONSTRAINT annotation_note_pk_idx PRIMARY KEY (annotation_note_id)
);

ALTER TABLE annotation_note ADD (
  CONSTRAINT fk_annotnote_annotation_id 
  FOREIGN KEY (annotation_id) 
  REFERENCES annotation(annotation_id)
);
CREATE INDEX annotation_note_annot_idx on annotation_note(annotation_id);


DROP TABLE center_type CASCADE CONSTRAINTS;
CREATE TABLE center_type 
(
   center_type_code       VARCHAR2(10)  NOT NULL,
   center_type_definition VARCHAR2(200) NOT NULL,
   CONSTRAINT center_type_pk_idx PRIMARY KEY (center_type_code)
 );
 
  
DROP TABLE center CASCADE CONSTRAINTS;
CREATE TABLE center 
(
  center_id            NUMBER(38)     NOT NULL,
  domain_name           VARCHAR2(100)  NOT NULL,
  center_type_code      VARCHAR2(10)   NOT NULL,
  display_name       VARCHAR2(200)  NOT NULL,
  short_name         VARCHAR2(10),
  sort_order            SMALLINT,
  is_uuid_converted     NUMBER(1) DEFAULT 0 NOT NULL,
  requires_magetab      NUMBER(1) DEFAULT 0 NOT NULL,
  CONSTRAINT center_pk_idx PRIMARY KEY (center_id),
  CONSTRAINT uk_center_idx UNIQUE (domain_name,center_type_code)
);
  
ALTER TABLE center ADD
CONSTRAINT fk_center_center_type
FOREIGN KEY (center_type_code)
REFERENCES center_type(center_type_code);

DROP TABLE center_email CASCADE CONSTRAINTS;
CREATE TABLE center_email
(
  center_email_id       NUMBER(38) NOT NULL,
  center_id             NUMBER(38) NOT NULL,
  email_address         VARCHAR2(2000) NOT NULL,
  CONSTRAINT center_email_pk_idx PRIMARY KEY (center_email_id)
);

ALTER TABLE center_email ADD
CONSTRAINT fk_center_email_center
FOREIGN KEY (center_id)
REFERENCES center (center_id);
 
DROP TABLE disease CASCADE CONSTRAINTS;
CREATE TABLE disease
(
  disease_id            NUMBER(38)   NOT NULL,
  disease_name          VARCHAR2(4000) NOT NULL,
  disease_abbreviation  VARCHAR2(10) NOT NULL,
  active                NUMBER(1) NOT NULL,
  CONSTRAINT disease_pk_idx PRIMARY KEY (disease_id),
  CONSTRAINT uk_disease_name_idx UNIQUE (disease_name),
  CONSTRAINT uk_disease_abbrev_idx UNIQUE (disease_abbreviation)
);

ALTER TABLE DATA_TYPE
 DROP PRIMARY KEY CASCADE;

DROP TABLE DATA_TYPE CASCADE CONSTRAINTS;

CREATE TABLE DATA_TYPE
(
  DATA_TYPE_ID      NUMBER(38)             NOT NULL,
  NAME              VARCHAR2(50)           NOT NULL,
  CENTER_TYPE_CODE  VARCHAR2(10),
  FTP_DISPLAY       VARCHAR2(100),
  AVAILABLE         NUMBER(1),
  SORT_ORDER        INTEGER,
  require_compression NUMBER(1) DEFAULT 1 NOT NULL,
  CONSTRAINT PK_DATA_TYPE_IDX PRIMARY KEY (DATA_TYPE_ID),
  CONSTRAINT UK_DATA_TYPE_NAME_IDX UNIQUE (NAME)
);

ALTER TABLE data_type ADD CONSTRAINT fk_data_type_center_type
FOREIGN KEY (center_type_code)
REFERENCES center_type(center_type_code);

DROP TABLE platform CASCADE CONSTRAINTS;
CREATE TABLE platform
(
  platform_id           NUMBER(38)    NOT NULL,
  platform_name         VARCHAR2(100) NOT NULL,
  platform_display_name VARCHAR2(100) NOT NULL,
  platform_alias        VARCHAR2(100) NOT NULL,
  center_type_code      VARCHAR2(10)  NOT NULL,
  sort_order            NUMBER(10)    NOT NULL,
  available             NUMBER(1)     NOT NULL,
  base_data_type_id     NUMBER(38)    NOT NULL,
  CONSTRAINT platform_pk_idx PRIMARY KEY (platform_id),
  CONSTRAINT uk_platform_name_idx UNIQUE (platform_name)
);

DROP TABLE data_type_to_platform CASCADE CONSTRAINTS;
CREATE TABLE data_type_to_platform
(
  data_type_platform_id    NUMBER(38)     NOT NULL,
  data_type_id        NUMBER(38)    NOT NULL,
  platform_id        NUMBER(38)    NOT NULL,
  CONSTRAINT datatype_platform_pk_idx PRIMARY KEY (data_type_platform_id)
);

DROP TABLE tissue CASCADE CONSTRAINTS;
CREATE TABLE tissue
(
  tissue_id          NUMBER(38)     NOT NULL,
  tissue             VARCHAR2(2000) NOT NULL,
  CONSTRAINT tissue_pk_idx PRIMARY KEY (tissue_id)
);

DROP TABLE tissue_to_disease CASCADE CONSTRAINTS;
CREATE TABLE tissue_to_disease
(
  tissue_disease_id    NUMBER(38)    NOT NULL,
  tissue_id        NUMBER(38)     NOT NULL,
  disease_id        NUMBER(38)    NOT NULL,
  CONSTRAINT tissue_disease_pk_idx PRIMARY KEY (tissue_disease_id),
  CONSTRAINT uk_tissue_disease_idx UNIQUE (tissue_id,disease_id)
);

ALTER TABLE tissue_to_disease ADD (
CONSTRAINT fk_tissue_disease_tissue
FOREIGN KEY (tissue_id)
REFERENCES tissue(tissue_id),
CONSTRAINT fk_tissue_disease_disease
FOREIGN KEY (disease_id)
REFERENCES disease(disease_id));

DROP TABLE tissue_source_site CASCADE CONSTRAINTS;
CREATE TABLE tissue_source_site
(
   tss_code               VARCHAR2(10)   NOT NULL,
   tss_definition            VARCHAR2(200)  NOT NULL,
   receiving_center_id       NUMBER(38)     NOT NULL,
   CONSTRAINT tissue_source_site_pk_idx PRIMARY KEY (tss_code)
);

ALTER TABLE tissue_source_site
ADD CONSTRAINT fk_tissue_source_site_center
FOREIGN KEY (receiving_center_id)
REFERENCES center (center_id);

DROP TABLE tss_to_disease CASCADE CONSTRAINTS;
CREATE TABLE tss_to_disease 
(
   tss_disease_id    NUMBER(38)     NOT NULL,
   tss_code        VARCHAR2(10)    NOT NULL,
   disease_id        NUMBER(38)    NOT NULL,
   CONSTRAINT tss_to_disease_pk_idx PRIMARY KEY (tss_disease_id)
);

ALTER TABLE tss_to_disease ADD (
CONSTRAINT fk_tss_to_disease_disease
FOREIGN KEY (disease_id)
REFERENCES disease(disease_id),
CONSTRAINT fk_tss_to_disease_tss
FOREIGN KEY (tss_code)
REFERENCES tissue_source_site(tss_code)
);


DROP TABLE uuid CASCADE CONSTRAINTS;
CREATE TABLE uuid (
    uuid                 VARCHAR2(36) NOT NULL,
    center_id            NUMBER(38)   NOT NULL,
    generation_method_id NUMBER(38)   NOT NULL,
    created_by           VARCHAR2(30) NOT NULL,
    create_date          DATE         NOT NULL,
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
CREATE TABLE barcode_history (
    barcode_id        NUMBER(38)   NOT NULL,
    barcode           VARCHAR2(50) NOT NULL,
    uuid              VARCHAR2(36) NOT NULL,
    disease_id        NUMBER(38)   NOT NULL,
    effective_date    DATE         NOT NULL,
    item_type_id      INTEGER,
    CONSTRAINT barcode_pk_idx PRIMARY KEY (barcode_id)
);

DROP TABLE visibility CASCADE CONSTRAINTS PURGE;
CREATE TABLE visibility
(
  visibility_id        NUMBER(38)    NOT NULL,
  visibility_name      VARCHAR2(20)    NOT NULL,
  identifiable         NUMBER(1)    NOT NULL,
  CONSTRAINT visibility_pk_idx PRIMARY KEY (visibility_id)
);

DROP TABLE archive_info CASCADE CONSTRAINTS PURGE;
CREATE TABLE archive_info
(
  archive_id                 NUMBER(38)             NOT NULL ,
  archive_name                 VARCHAR2(2000)        NOT NULL,
  archive_type_id             NUMBER(38),
  center_id                 NUMBER(38)             NOT NULL,
  disease_id                 NUMBER(38)             NOT NULL,
  platform_id                 NUMBER(38)             NOT NULL,
  serial_index                 NUMBER(10)             NOT NULL,
  revision                     NUMBER(10)             NOT NULL,
  series                     NUMBER(10)             NOT NULL,
  date_added                   TIMESTAMP              NOT NULL,
  deploy_status               VARCHAR2(25)         NOT NULL,
  deploy_location             VARCHAR2(2000),
  secondary_deploy_location     VARCHAR2(2000),
  is_latest                    NUMBER(1)  DEFAULT 1    NOT NULL ,
  initial_size_kb             NUMBER(38),
  final_size_kb             NUMBER(38),
  is_latest_loaded             NUMBER(1),
  data_loaded_date        DATE,
  CONSTRAINT archive_info_pk_idx PRIMARY KEY (archive_id),
  CONSTRAINT archive_info_uk_name UNIQUE (archive_name)
);

DROP TABLE archive_type CASCADE CONSTRAINTS PURGE;
CREATE TABLE archive_type
(
  archive_type_id    NUMBER(38)   NOT NULL,
  archive_type       VARCHAR2(50) NOT NULL,
  data_level         INTEGER,
  CONSTRAINT archive_type_pk_idx PRIMARY KEY (archive_type_id)
);
DROP TABLE batch_number_assignment CASCADE CONSTRAINTS;
CREATE TABLE batch_number_assignment
(
  batch_id      INTEGER NOT NULL,
  disease_id    NUMBER(38) NOT NULL,
  center_id     NUMBER(38) NOT NULL,
  CONSTRAINT pk_batch_number_assignment_idx PRIMARY KEY (batch_id,center_id)  
);

DROP TABLE biospecimen_barcode CASCADE CONSTRAINTS PURGE;
CREATE TABLE biospecimen_barcode
(
  biospecimen_id        NUMBER(38)          NOT NULL,
  barcode                 VARCHAR2(100)    NOT NULL,
  project_code       VARCHAR2(10)     NOT NULL,
  tss_code              VARCHAR2(10)     NOT NULL,
  patient                 VARCHAR2(10)     NOT NULL,
  sample_type_code      VARCHAR2(10)     NOT NULL,
  sample_sequence         VARCHAR2(10)     NOT NULL,
  portion_sequence        VARCHAR2(10)     NOT NULL,
  portion_analyte_code  VARCHAR2(10)     NOT NULL,
  plate_id                 VARCHAR2(10)     NOT NULL,
  bcr_center_id         VARCHAR2(10)     NOT NULL,
  is_valid                 NUMBER(1) DEFAULT 1,
  is_viewable        NUMBER(1) DEFAULT 1,
  ship_date            DATE,
  uuid                VARCHAR2(36),
  CONSTRAINT biospecimen_barcode_pk_idx PRIMARY KEY (biospecimen_id),
  CONSTRAINT biospecimen_barcode_uk_bcidx UNIQUE (barcode),
  CONSTRAINT biospecimen_barcode_uk_uuid UNIQUE (uuid)
);

DROP TABLE bcr_biospecimen_to_archive CASCADE CONSTRAINTS PURGE;
CREATE TABLE bcr_biospecimen_to_archive
(
  biospecimen_archive_id     NUMBER(38) NOT NULL,
  biospecimen_id             NUMBER(38) NOT NULL,
  archive_id                 NUMBER(38) NOT NULL,
  CONSTRAINT biospecimen_to_archive_pk_idx PRIMARY KEY (biospecimen_archive_id),
  CONSTRAINT biospecimen_to_archive_uk_idx UNIQUE (biospecimen_id, archive_id));

DROP TABLE biospecimen_to_file CASCADE CONSTRAINTS PURGE;
CREATE TABLE biospecimen_to_file
(
  biospecimen_file_id       NUMBER(38)     NOT NULL,
  biospecimen_id            NUMBER(38)     NOT NULL,
  file_id                   NUMBER(38)     NOT NULL,
  file_col_name             VARCHAR2(100),
  CONSTRAINT biospecimen_to_file_pk_idx PRIMARY KEY (biospecimen_file_id),
  CONSTRAINT biospecimen_to_file_uk_idx UNIQUE (biospecimen_id, file_id, file_col_name)
);

DROP TABLE center_to_bcr_center CASCADE CONSTRAINTS PURGE;
CREATE TABLE center_to_bcr_center
(
  bcr_center_id      VARCHAR2(10) NOT NULL,
  center_id          NUMBER(38)   NOT NULL,
  center_type_code   VARCHAR2(25) NOT NULL,
  CONSTRAINT center_bcr_center_pk PRIMARY KEY (bcr_center_id)
);


DROP TABLE data_level CASCADE CONSTRAINTS PURGE;
CREATE TABLE data_level
(
  level_number             NUMBER(10)        NOT NULL,
  level_definition         VARCHAR2(100)     NOT NULL,
  CONSTRAINT data_level_pk PRIMARY KEY (level_number)
);

DROP TABLE data_visibility CASCADE CONSTRAINTS PURGE;
CREATE TABLE data_visibility
(
  data_visibility_id   NUMBER(38) NOT NULL,
  data_type_id         NUMBER(38) NOT NULL,
  visibility_id        NUMBER(38) NOT NULL,
  level_number         NUMBER(10) NOT NULL,
  CONSTRAINT data_visibility_pk_idx PRIMARY KEY (data_visibility_id),
  CONSTRAINT data_visibility_uk UNIQUE (data_type_id, visibility_id, level_number)
);

DROP TABLE file_info CASCADE CONSTRAINTS PURGE;
CREATE TABLE file_info
(
  file_id             NUMBER(38)         NOT NULL,
  file_name           VARCHAR2(2000)     NOT NULL,
  file_size           NUMBER(38), 
  level_number        SMALLINT, 
  data_type_id        NUMBER(38),
  md5                 CHAR(32), 
  revision_of_file_id NUMBER(38),
  CONSTRAINT file_info_pk_idx PRIMARY KEY (file_id)
);

DROP TABLE file_to_archive CASCADE CONSTRAINTS PURGE;
CREATE TABLE file_to_archive (
 file_archive_id    NUMBER(38) NOT NULL,
 file_id            NUMBER(38) NOT NULL, 
 archive_id         NUMBER(38) NOT NULL,
 file_location_url  VARCHAR2(2000),
 CONSTRAINT file_to_archive_pk_idx PRIMARY KEY (file_archive_id),
 CONSTRAINT file_to_archive_uk_idx UNIQUE (file_id,archive_id)
);
  
DROP TABLE hugo_gene CASCADE CONSTRAINTS PURGE;
CREATE TABLE hugo_gene (
  hugo_gene_id    NUMBER(38)     NOT NULL ,
  approved_symbol VARCHAR2(25)   NOT NULL,
  approved_name   VARCHAR2(250)  NOT NULL,
  hgnc_id         NUMBER(38) ,
  entrez_gene_id  NUMBER(38),
  CONSTRAINT hugo_gene_pk_idx PRIMARY KEY (hugo_gene_id),
  CONSTRAINT hugo_gene_uk UNIQUE (approved_symbol));

DROP TABLE latest_samples_received_by_dcc CASCADE CONSTRAINTS PURGE;
CREATE TABLE latest_samples_received_by_dcc
( 
  samples_received_id      NUMBER(38)     NOT NULL,
  disease_abbreviation     VARCHAR2(10),
  center_id                NUMBER(38),
  center_name              VARCHAR2(100),
  center_type_code         VARCHAR2(25),
  platform                 VARCHAR2(100),
  biospecimen_id           NUMBER(38),
  barcode                VARCHAR2(100),
  sample                   VARCHAR2(20),
  portion_analyte_code     VARCHAR2(10),
  is_valid                 NUMBER(1),
  is_viewable              NUMBER(1),
  date_received            TIMESTAMP,
  submit_level1        CHAR(1),
  submit_level2        CHAR(1), 
  submit_level3        CHAR(1),
  CONSTRAINT samples_received_pkey PRIMARY KEY (samples_received_id)
) ;

DROP TABLE process_log CASCADE CONSTRAINTS PURGE;
CREATE TABLE process_log
(
  log_id              number(38) NOT NULL ,
  start_time          timestamp  NOT NULL,
  end_time            timestamp ,
  result_id           number(38),
  description         CLOB,
  CONSTRAINT log_pkey PRIMARY KEY (log_id)
);

DROP TABLE log_to_archives CASCADE CONSTRAINTS PURGE;
CREATE TABLE log_to_archives
(
  log_id     number(38) NOT NULL,
  archive_id number(38) NOT NULL,
  CONSTRAINT log2archives_pk PRIMARY KEY (log_id, archive_id)
);

DROP TABLE log_to_files CASCADE CONSTRAINTS PURGE;
CREATE TABLE log_to_files
(
  log_id  number(38) NOT NULL,
  file_id number(38) NOT NULL,
  CONSTRAINT log2files_pk PRIMARY KEY (log_id, file_id)
);

DROP TABLE pi_info CASCADE CONSTRAINTS PURGE;
CREATE TABLE pi_info
(
  pi_info_id    NUMBER(38)         NOT NULL,
  prefix        VARCHAR2(10),
  first_name    VARCHAR2(100),
  last_name     VARCHAR2(100),
  suffix        VARCHAR2(10),
  bcr_center_id VARCHAR2(10)       NOT NULL,
  center_type   VARCHAR2(10)       NOT NULL,
  email_address VARCHAR2(1000)     NOT NULL,
  CONSTRAINT pi_info_pk_idx PRIMARY KEY (pi_info_id)
);

DROP TABLE portion_analyte CASCADE CONSTRAINTS PURGE;
CREATE TABLE portion_analyte
(
  portion_analyte_code  VARCHAR2(10)     NOT NULL,
  definition            VARCHAR2(100)     NOT NULL,
  CONSTRAINT portion_anlayte_pk_idx PRIMARY KEY (portion_analyte_code)
);

DROP TABLE project CASCADE CONSTRAINTS PURGE;
CREATE TABLE project
(
  project_code           VARCHAR2(10)   NOT NULL,
  definition             VARCHAR2(100)  NOT NULL,
  CONSTRAINT project_pk_idx   PRIMARY KEY (project_code)
);

DROP TABLE sample_level_count CASCADE CONSTRAINTS PURGE;
CREATE TABLE sample_level_count
(
  sample_level_count_id   NUMBER(38) NOT NULL,
  disease_abbreviation    VARCHAR2(20),
  center_name             VARCHAR2(100),
  center_type_code        VARCHAR2(25),
  portion_analyte_code    VARCHAR2(10),
  platform                VARCHAR2(100),
  data_level              NUMBER(10),
  samplecount             NUMBER(38),
  CONSTRAINT sample_level_count_pkey PRIMARY KEY (sample_level_count_id)
);

DROP TABLE samples_sent_by_bcr CASCADE CONSTRAINTS PURGE;
CREATE TABLE samples_sent_by_bcr
(
     sample_sent_id         NUMBER     NOT NULL,
     disease_abbreviation   VARCHAR2(20),
     center_id              NUMBER(38),
     center_name            VARCHAR2(100),
     center_type_code       VARCHAR2(25),
     biospecimen_id         NUMBER(38),
     barcode                VARCHAR2(100),
     sample                 VARCHAR2(20),
     portion_analyte_code   VARCHAR2(10),
     ship_date              DATE,
     batch_number        NUMBER(10,0),
     CONSTRAINT sample_sent_bcr_pkey PRIMARY KEY (sample_sent_id)
);


DROP TABLE sample_summary_report_detail CASCADE CONSTRAINTS PURGE;
CREATE TABLE sample_summary_report_detail
(
  sample_summary_id        NUMBER(38) NOT NULL,
  disease_abbreviation        VARCHAR2(20),
  center_name                    VARCHAR2(2000),
  center_type_code                 VARCHAR2(25),
  portion_analyte_code            VARCHAR2(10),
  platform                         VARCHAR2(2000),
  total_bcr_sent                 INTEGER DEFAULT 0,
  total_centers_sent           INTEGER DEFAULT 0,
  total_bcr_unaccounted         INTEGER DEFAULT 0,
  total_center_unaccounted       INTEGER DEFAULT 0,
  total_with_level1             INTEGER DEFAULT 0,
  total_with_level2             INTEGER DEFAULT 0,
  total_with_level3             INTEGER DEFAULT 0,
  level_4_submitted             CHAR(2),
  last_refresh                     DATE,
  CONSTRAINT samples_summary_report_pkey PRIMARY KEY (sample_summary_id)
);

DROP TABLE sample_type CASCADE CONSTRAINTS PURGE;
CREATE TABLE sample_type
(
  sample_type_code  VARCHAR2(10)   NOT NULL,
  definition        VARCHAR2(100)  NOT NULL,
  is_tumor            NUMBER(1,0),
  short_letter_code VARCHAR2(25),
  CONSTRAINT sample_type_pk_idx PRIMARY KEY (sample_type_code)
);

DROP TABLE trace_info CASCADE CONSTRAINTS PURGE;
CREATE TABLE trace_info
(
  ncbi_trace_id         NUMBER(38)     NOT NULL,
  trace_name             VARCHAR2(250)     NOT NULL,
  center_name             VARCHAR2(25)     NOT NULL,
  submission_type         VARCHAR2(25)     NOT NULL,
  gene_name             VARCHAR2(25),
  reference_accession   VARCHAR2(25),
  reference_acc_max     NUMBER(38),
  reference_acc_min     NUMBER(38),
  replaced_by             NUMBER(38),
  basecall_length         NUMBER(38),
  load_date             TIMESTAMP     NOT NULL,
  state             VARCHAR2(25),
  CONSTRAINT trace_info_pk_idx PRIMARY KEY (ncbi_trace_id),
  CONSTRAINT trace_info_centertracedate_uk UNIQUE (trace_name, center_name, load_date)
);

DROP TABLE biospecimen_ncbi_trace CASCADE CONSTRAINTS PURGE;
CREATE TABLE biospecimen_ncbi_trace
(
  biospecimen_trace_id      NUMBER(38)         NOT NULL,
  biospecimen_id            NUMBER(38)         NOT NULL,
  ncbi_trace_id             NUMBER(38)         NOT NULL,
  file_id                   NUMBER(38)         NOT NULL,
  dcc_date_received         DATE               NOT NULL,
  CONSTRAINT biospecimen_ncbi_trace_pk_idx PRIMARY KEY (biospecimen_trace_id),
  CONSTRAINT biospecimen_ncbi_trace_uk_idx UNIQUE (ncbi_trace_id, biospecimen_id)
);

DROP INDEX biospec_ncbitrace_file_idx;
CREATE INDEX biospec_ncbitrace_file_idx ON biospecimen_ncbi_trace (file_id);

ALTER TABLE BIOSPECIMEN_NCBI_TRACE ADD (
  CONSTRAINT FK_BIOSPEC_NCBITRACE_BIOSPEC 
 FOREIGN KEY (BIOSPECIMEN_ID) 
 REFERENCES BIOSPECIMEN_BARCODE (BIOSPECIMEN_ID),
  CONSTRAINT FK_BIOSPEC_NCBI_TRACE_FILE 
 FOREIGN KEY (FILE_ID) 
 REFERENCES FILE_INFO (FILE_ID) ON DELETE CASCADE);

DROP TABLE projectOverview_case_counts;
CREATE TABLE projectOverview_case_counts (
    project_overview_id           NUMBER(38,0)    NOT NULL,
    disease_abbreviation        VARCHAR2(10)    NOT NULL,
    metholated_data_cases      NUMBER(38,0),
    microRna_data_cases           NUMBER(38,0),
    expArray_data_cases          NUMBER(38,0),
    expRnaSeq_data_cases        NUMBER(38,0),
    copyNumber_data_cases        NUMBER(38,0),
    gsc_mutation_data_cases     NUMBER(38,0),
    gsc_genome_cases            NUMBER(38,0),
    gsc_exome_cases            NUMBER(38,0),
    gsc_rnaseq_cases            NUMBER(38,0),
    gsc_microRna_cases            NUMBER(38,0),
    gsc_lowpass_cases           NUMBER(38,0),
    gcc_lowpass_cases           NUMBER(38,0),
    CONSTRAINT pk_overviewCaseCounts_idx PRIMARY KEY (project_overview_id)
);

DROP TABLE bam_file_datatype CASCADE CONSTRAINTS PURGE;
CREATE TABLE bam_file_datatype (
    bam_datatype_id      NUMBER(38,0)    NOT NULL,
    bam_datatype        VARCHAR2(50)    NOT NULL,
    molecule            VARCHAR2(10)    NOT NULL,
    general_datatype    VARCHAR2(10)    NOT NULL,
    CONSTRAINT pk_bam_file_datatype_idx PRIMARY KEY (bam_datatype_id)
);

DROP TABLE bam_file CASCADE CONSTRAINTS;
CREATE TABLE bam_file (
    bam_file_id            NUMBER(38,0)    NOT NULL,
    bam_file_name    VARCHAR2(100)    NOT NULL,
    disease_id            NUMBER(38,0)    NOT NULL,
    center_id            NUMBER(38,0)    NOT NULL,
    bam_file_size    NUMBER(38,0)    NOT NULL,
    date_received      DATE            NOT NULL,
    bam_datatype_id     NUMBER(38,0)    NOT NULL,
    CONSTRAINT pk_bam_file_idx PRIMARY KEY (bam_file_id)
);

ALTER TABLE bam_file ADD (
    CONSTRAINT fk_bam_file_center
    FOREIGN KEY (center_id)
    REFERENCES center(center_id),
    CONSTRAINT fk_bam_file_disease
    FOREIGN KEY (disease_id)
    REFERENCES disease(disease_id),
    CONSTRAINT fk_bam_file_datatype
    FOREIGN KEY (bam_datatype_id)
    REFERENCES bam_file_datatype(bam_datatype_id)
);


DROP TABLE biospecimen_to_bam_file CASCADE CONSTRAINTS PURGE;
CREATE TABLE biospecimen_to_bam_file (
    biospecimen_bam_id    NUMBER(38,0)    NOT NULL,
    bam_file_id            NUMBER(38,0)    NOT NULL,
    biospecimen_id    NUMBER(38,0)    NOT NULL,
    CONSTRAINT pk_biospecimen_bam_idx PRIMARY KEY (biospecimen_bam_id)
);

ALTER TABLE biospecimen_to_bam_file ADD(
    CONSTRAINT fk_biospecimen_bam_biospec
    FOREIGN KEY (biospecimen_id)
    REFERENCES biospecimen_barcode(biospecimen_id),
    CONSTRAINT fk_biospecimen_bam_bam
    FOREIGN KEY (bam_file_id)
    REFERENCES bam_file(bam_file_id)
);

DROP TABLE chromosome;
CREATE TABLE chromosome (
    chromosome_name    VARCHAR2(30)     NOT NULL,
    length        INTEGER        NOT NULL,
    build        VARCHAR2(30)    NOT NULL,
    CONSTRAINT pk_chromosome_idx PRIMARY KEY (chromosome_name,build)
);

DROP TABLE uuid_item_type;
CREATE TABLE uuid_item_type (
    item_type_id    INTEGER         NOT NULL,
    item_type        VARCHAR2(15)    NOT NULL,
    sort_order        NUMBER(2,0)        NOT NULL,
    xml_name        VARCHAR2(20)    NOT NULL,    
    CONSTRAINT pk_item_type_idx PRIMARY KEY (item_type_id)
);

DROP TABLE uuid_hierarchy CASCADE CONSTRAINTS;
CREATE TABLE uuid_hierarchy (
    disease_abbreviation        VARCHAR2(10)    NOT NULL,
    uuid                        VARCHAR2(36)    NOT NULL,
    parent_uuid                    VARCHAR2(36),
    item_type_id                   INTEGER     NOT NULL,
    tss_code                    VARCHAR2(10)    NOT NULL,
    center_id_bcr        NUMBER(38,0)    NOT NULL,
    batch_number        INTEGER        NOT NULL,
    barcode                    VARCHAR2(50),
    participant_number            VARCHAR2(20)    NOT NULL,
    sample_type_code            VARCHAR2(20),
    sample_sequence            VARCHAR2(10),
    portion_sequence            VARCHAR2(10),
    portion_analyte_code        VARCHAR2(10),
    plate_id                VARCHAR2(10),
    receiving_center_id            NUMBER(38,0),
    slide                    VARCHAR2(10),
    slide_layer            VARCHAR2(7),
    create_date            DATE        NOT NULL,
    update_date                DATE , 
    platforms            VARCHAR2(4000),
    is_redacted             NUMBER(1) DEFAULT 0 NOT NULL,
    center_code            VARCHAR2(10),
    is_shipped            NUMBER(1),
    shipped_date        DATE,
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

DROP TABLE portal_action_summary;
CREATE TABLE PORTAL_ACTION_SUMMARY
(
  DISEASE_ABBREVIATION  VARCHAR2(10 BYTE)       NOT NULL,
  MONTHYEAR             VARCHAR2(7 BYTE)        NOT NULL,
  ACTION_TYPE_ID        NUMBER(38)              NOT NULL,
  ACTION                VARCHAR2(50 BYTE)       NOT NULL,
  SELECTION             VARCHAR2(50 BYTE),
  ACTION_TOTAL          NUMBER(38)              NOT NULL,
  USER_COUNT            NUMBER(10)              NOT NULL,
  TOTALUSERCOUNT        NUMBER(10)
);

DROP TABLE HOME_PAGE_STATS;
CREATE TABLE HOME_PAGE_STATS
(
  DISEASE_ABBREVIATION  VARCHAR2(10 BYTE)       NOT NULL,
  DATE_LAST_UPDATED     DATE,
  CASES_SHIPPED         NUMBER(38),
  CASES_WITH_DATA       NUMBER(38)
);

DROP TABLE HOME_PAGE_DRILLDOWN;
CREATE TABLE HOME_PAGE_DRILLDOWN
(
  DISEASE_ABBREVIATION   VARCHAR2(10 BYTE)      NOT NULL,
  HEADER_NAME            VARCHAR2(20 BYTE)      NOT NULL,
  CASE_COUNT             NUMBER(38),
  HEALTHY_CONTROL_COUNT  NUMBER(38)
);

-- objects for capturing data freezes
DROP SEQUENCE data_freeze_seq ;
CREATE SEQUENCE data_freeze_seq START WITH 1 INCREMENT BY 1;
DROP TABLE publication CASCADE CONSTRAINTS;
CREATE TABLE publication (
    publication_id        NUMBER(38)        NOT NULL,
    publication_name    VARCHAR2(4000)    NOT NULL,
    publication_date    DATE        ,
    CONSTRAINT pk_publication_idx PRIMARY KEY (publication_id)
);
DROP TABLE data_freeze CASCADE CONSTRAINTS;
CREATE TABLE data_freeze (
    data_freeze_id        NUMBER(38)    NOT NULL,
    data_freeze_date    DATE            NOT NULL,
    data_freeze_version    VARCHAR2(1000),
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


CREATE OR REPLACE FUNCTION getReportSequence
RETURN NUMBER
IS
    seq NUMBER(38);
BEGIN 

    SELECT report_seq.NEXTVAL INTO seq FROM DUAL;
    RETURN(seq);
END;
/
/*
** This procdure will get data from the latest BCR archives and the latest data center archives, place it into
** tables from which summaries by sample, center, platform, disease  will be compiled and put into the 
** sample_summary_report_detail for the Sample Counts for TCGA Data report. The intermediate tables' data
** is used also by the Aliquot Reports 
**
** Written by Shelley Alonso Initial Development 7/28/2010
**
** Revision History
**
** Shelley Alonso   08/27/2010 No longer include BCR archives in the samples centers sent counts
** Shelley Alonso   09/21/2010 Add batch_number to samples_sent_by_bcr table and submit_level1,2,and 3 to the 
**                   latest_samplese_received_by_dcc table to facilitate more easily getting data for
**                             the aliquot reports
** Shelley Alonso   09/21/2010 Change updates for sample_summary_report_detail table to one statement with
**                   nvl to make more efficient. 
** Shelley Alonso   11/12/2010 refactor some queries for efficiency
** Shelley Alonso   03/17/2011 Add documentation header
** Shelley Alonso   08/09/2011 Use shipped_biospecimen and related tables and shipped_biospecimen_aliquot view now
** Shelley Alonso   12/16/2011 APPS-4257 Fix query that counts number of missing samples so it gets distinct count
** Shelley Alonso   01/13/2012 APPS-5266 Query that counts number of missing samples is still wrong, fix it. 
**  
*/
CREATE OR REPLACE PROCEDURE build_sample_summary_report
IS
BEGIN

    EXECUTE IMMEDIATE 'TRUNCATE TABLE sample_level_count';
    INSERT INTO sample_level_count
    (
      sample_level_count_id,disease_abbreviation,  center_name,  center_type_code ,  portion_analyte_code, platform , data_level, sampleCount
    )
    SELECT getReportSequence(), d.disease_abbreviation,c.domain_name, c.center_type_code,
           bb.portion_analyte_code, p.platform_alias, f.level_number,
           NVL(count(distinct bb.sample),0) as sampleCount
    FROM center c, archive_info a, platform p, file_info f,
         shipped_biospecimen_file bf, shipped_biospecimen_aliquot bb, disease d , file_to_archive fa
    WHERE c.center_id = a.center_id
    AND   a.platform_id = p.platform_id
    AND   a.archive_id = fa.archive_id
    AND   fa.file_id = f.file_id
    AND   f.file_id = bf.file_id
    AND   a.disease_id = d.disease_id
    AND   bf.shipped_biospecimen_id = bb.shipped_biospecimen_id
    AND   bb.is_viewable=1
    AND   a.is_latest = 1
    AND   a.deploy_status = 'Available'
    GROUP BY c.domain_name, c.center_type_code, bb.portion_analyte_code, platform_alias, d.disease_abbreviation,f.level_number;

    /*
    ** Get all the samples the centers sent; path to GSC centers is thru biospecimen_gsc_file_mv,
    ** and the path to CGCC is thru biospecimen_to_file
    */
    EXECUTE IMMEDIATE 'TRUNCATE TABLE latest_samples_received_by_dcc';
    INSERT INTO latest_samples_received_by_dcc
    (
        samples_received_id,
        disease_abbreviation,
        center_id,
        center_name,
        center_type_code,
        platform,
        biospecimen_id,
        barcode,
        sample,
        portion_analyte_code,
        is_viewable,
        date_received,
        submit_level1,
        submit_level2,
        submit_level3
    )
    SELECT
        getReportSequence,
        v.disease_abbreviation,
        v.center_id,
        v.domain_name,
        v.center_type_code,
        v.platform,
        v.biospecimen_id,
        v.barcode,
        v.sample,
        v.portion_analyte_code,
        v.is_viewable,
        v.date_received,
        v.submit_level1,
        v.submit_level2,
        v.submit_level3
    FROM
    (SELECT d.disease_abbreviation, c.center_id, c.domain_name, c.center_type_code, p.platform_alias as platform, bb.shipped_biospecimen_id as biospecimen_id,
           bb.barcode, bb.sample, bb.portion_analyte_code,bb.is_viewable, min(a.date_added)  as date_received,
           max(DECODE (f.level_number,1,'Y','N')) as submit_level1,max(DECODE (f.level_number,2,'Y','N')) submit_level2,
           max(DECODE (f.level_number,3,'Y','N')) submit_level3
    FROM archive_info a, center c, platform p, disease d, file_to_archive fa, file_info f,
          shipped_biospecimen_file bf, shipped_biospecimen_aliquot bb
    WHERE a.is_latest = 1
    AND   a.deploy_status = 'Available'
    AND   a.center_id = c.center_id
    AND   c.center_type_code != 'BCR'
    AND   a.platform_id = p.platform_id
    AND   a.disease_id = d.disease_id
    AND   a.archive_id = fa.archive_id
    AND   fa.file_id = f.file_id
    AND   f.file_id = bf.file_id
    AND   bf.shipped_biospecimen_id = bb.shipped_biospecimen_id
    AND   bb.is_viewable=1
    group by d.disease_abbreviation, c.center_id, c.domain_name, c.center_type_code, p.platform_alias, bb.shipped_biospecimen_id,
           bb.barcode, bb.sample, bb.portion_analyte_code, bb.is_viewable) v;
    /*
    ** Get latest samples sent out by the BCR and store them in an intermediate table so that this join
    ** does not have to be done over and over
    */
    EXECUTE IMMEDIATE 'TRUNCATE TABLE samples_sent_by_bcr';
    INSERT INTO samples_sent_by_bcr
    (
      sample_sent_id,
      disease_abbreviation,
      center_id,
      center_name,
      center_type_code,
      biospecimen_id,
      barcode,
      sample,
      portion_analyte_code,
      ship_date,
      batch_number
    )
    SELECT getReportSequence,
           v.disease_abbreviation,
           v.center_id,
           v.domain_name,
           v.center_type_code,
           v.biospecimen_id,
           v.barcode,
           v.sample,
           v.portion_analyte_code,
           v.ship_date,
           v.batch_number
    FROM
    (SELECT d.disease_abbreviation,c.center_id,c.domain_name,cbc.center_type_code,b.shipped_biospecimen_id as biospecimen_id,
           b.barcode,b.sample,b.portion_analyte_code, b.ship_date, a.serial_index as batch_number
    FROM   shipped_biospecimen_aliquot b, shipped_biospec_bcr_archive ba, Archive_info a, disease d,
           center c, center_to_bcr_center cbc
    WHERE c.center_id=cbc.center_id
    AND   cbc.bcr_center_id = b.bcr_center_id
    AND   b.shipped_biospecimen_id = ba.shipped_biospecimen_id
    AND   ba.archive_id = a.archive_id
    AND   a.disease_id = d.disease_id
    AND   a.is_latest = 1
    AND   b.is_viewable=1
    AND   a.deploy_status = 'Available') v;

    DELETE FROM sample_summary_report_detail;
    /*
    ** Do the initial insert into the sample_summary_report_detail table, getting totals for
    ** samples dcc received from the centers and samples bcr sent to the centers. We need to get all the
    ** center samples whether there is a match in BCR samples, and all the BCR samples whether or not there
    ** is a match in center samples. That is why there are 2 queries that look almost the same, UNION'ed to
    ** exclude duplicates.
    */
    INSERT INTO sample_summary_report_detail (
        sample_summary_id,
        disease_abbreviation,
        center_name,
        center_type_code,
        portion_analyte_code,
        platform,
        total_bcr_sent,
        total_centers_sent
    )
    SELECT
       getReportSequence(),
       v.disease_abbreviation,
       v.center_name,
       v.center_type_code,
       v.portion_analyte_code,
       v.platform,
       v.samples_sent,
       v.samples_dcc_received
    FROM
    (SELECT bcr.disease_abbreviation, bcr.center_name, bcr.center_type_code, bcr.portion_analyte_code, centers.platform, bcr.samples_sent, centers.samples_dcc_received
    FROM
    (SELECT disease_abbreviation, NVL(count(distinct sample), 0) as samples_dcc_received, center_name, center_type_code, portion_analyte_code, platform
    from latest_samples_received_by_dcc
    GROUP BY disease_abbreviation, center_name,center_type_code,portion_analyte_code, platform) centers RIGHT OUTER JOIN
    (SELECT disease_abbreviation,count(distinct sample) as samples_sent, center_name, center_type_code, portion_analyte_code
    from samples_sent_by_bcr
    GROUP BY disease_abbreviation,center_name, center_type_code, portion_analyte_code) bcr
    ON    centers.center_name = bcr.center_name
    AND   centers.center_type_code = bcr.center_type_code
    AND   centers.portion_analyte_code = bcr.portion_analyte_code
    AND   centers.disease_abbreviation = bcr.disease_abbreviation
    UNION
    SELECT centers.disease_abbreviation, centers.center_name, centers.center_type_code, centers.portion_analyte_code, centers.platform, bcr.samples_sent, centers.samples_dcc_received
    FROM
    (SELECT disease_abbreviation,NVL(count(distinct sample),0) as samples_dcc_received, center_name, center_type_code, portion_analyte_code, platform
    from latest_samples_received_by_dcc
    GROUP BY disease_abbreviation, center_name,center_type_code,portion_analyte_code, platform) centers LEFT OUTER JOIN
    (SELECT disease_abbreviation,COUNT(distinct sample) as samples_sent, center_name, center_type_code, portion_analyte_code
    from samples_sent_by_bcr
    GROUP BY disease_abbreviation,center_name, center_type_code, portion_analyte_code) bcr
    ON    centers.center_name = bcr.center_name
    AND   centers.center_type_code = bcr.center_type_code
    AND   centers.portion_analyte_code = bcr.portion_analyte_code
    AND   centers.disease_abbreviation = bcr.disease_abbreviation
    ORDER by disease_abbreviation,center_name,center_type_code,portion_analyte_code) v;

    /*
    ** calculate unaccounted for, both bcr and centers
    ** Centers unaccounted for would be samples the bcr sent that the center did not send
    */
    UPDATE sample_summary_report_detail s
    SET    total_center_unaccounted =
    (SELECT  NVL(GREATEST(s.total_bcr_sent - v.samples, 0),0)
    FROM
    (SELECT  COUNT(DISTINCT l.sample) as samples,l.center_name,l.disease_abbreviation,l.center_type_code,l.portion_analyte_code,l.platform
    FROM    latest_samples_received_by_dcc l, samples_sent_by_bcr bc
    WHERE   l.sample = bc.sample
    AND     l.disease_abbreviation = bc.disease_abbreviation
    AND     l.center_name = bc.center_name
    AND     l.center_type_code = bc.center_type_code
    AND     l.portion_analyte_code = bc.portion_analyte_code
    GROUP BY l.disease_abbreviation,l.center_name,l.center_type_code,l.portion_analyte_code,l.platform) v
    WHERE v.disease_abbreviation = s.disease_abbreviation
    AND   v.center_name = s.center_name
    AND   v.center_type_code = s.center_type_code
    AND   v.portion_analyte_code = s.portion_analyte_code
    AND   v.platform = s.platform) ;
    COMMIT;
    /*
    ** Bcr unaccounted for would be samples the centers sent that the bcr did not send
    */
    MERGE INTO sample_summary_report_detail sd
    USING
    (SELECT COUNT(DISTINCT l.sample) as samples,l.disease_abbreviation, l.center_name, l.center_type_code, l.portion_analyte_code, l.platform
         FROM latest_samples_received_by_dcc l 
         WHERE l.sample NOT IN 
           (SELECT DISTINCT sample FROM samples_Sent_by_bcr sb
            WHERE sb.disease_abbreviation = l.disease_abbreviation 
            AND   sb.center_name          = l.center_name 
            AND   sb.center_type_code     = l.center_type_code 
            AND   sb.portion_analyte_code = l.portion_analyte_code 
            AND   sb.sample               = l.sample)
         GROUP BY l.disease_abbreviation, l.center_name, l.center_type_code, l.portion_analyte_code, l.platform) v
     ON (sd.disease_abbreviation = v.disease_abbreviation AND
         sd.center_name = v.center_name AND
         sd.center_Type_code = v.center_type_code AND
         sd.portion_analyte_code = v.portion_analyte_code AND
         sd.platform = v.platform)
     WHEN MATCHED THEN UPDATE SET
         sd.total_bcr_unaccounted =  v.samples;     
    COMMIT;
        
    /*
    ** set the level totals: how many samples had level 1, how many level 2 and how many level 3 data
    */
    UPDATE sample_summary_report_detail s
    SET   total_with_level1 =
    (SELECT sampleCount
     FROM sample_level_count l
     WHERE l.disease_abbreviation = s.disease_abbreviation
     AND   l.center_name = s.center_name
     AND   l.center_type_code = s.center_type_code
     AND   l.portion_analyte_code = s.portion_analyte_code
     AND   l.platform = s.platform
     AND   l.data_level = 1);

    UPDATE sample_summary_report_detail s
    SET   total_with_level2 =
    (SELECT sampleCount
     FROM sample_level_count l
     WHERE l.disease_abbreviation = s.disease_abbreviation
     AND   l.center_name = s.center_name
     AND   l.center_type_code = s.center_type_code
     AND   l.portion_analyte_code = s.portion_analyte_code
     AND   l.platform = s.platform
     AND   l.data_level = 2);

    UPDATE sample_summary_report_detail s
    SET   total_with_level3 =
    (SELECT sampleCount
     FROM sample_level_count l
     WHERE l.disease_abbreviation = s.disease_abbreviation
     AND   l.center_name = s.center_name
     AND   l.center_type_code = s.center_type_code
     AND   l.portion_analyte_code = s.portion_analyte_code
     AND   l.platform = s.platform
     AND   l.data_level = 3);

    /*
    ** now figure out which CGCC centers and platform sent level 4 data (whether in latest archives or not) and
    ** update the field in the sample_summary_report_detail. Since there could be level4 data in latest and not latest
    ** archives; postgres will not allow an order by in the subquery, so do two updates. (erghh) DREP-16 bug filed.
    */

    UPDATE sample_summary_report_detail s
    SET   level_4_submitted =
    (SELECT DISTINCT 'Y*'
     FROM file_info f, disease d, file_to_archive fa, archive_info a , center c, platform p
     WHERE a.is_latest = 0
     AND   a.disease_id = d.disease_id
     AND   a.platform_id = p.platform_id
     AND   a.center_id = c.center_id
     AND   a.archive_id = fa.archive_id
     AND   fa.file_id = f.file_id
     AND   f.level_number = 4
     AND   c.domain_name = s.center_name
     AND   p.platform_alias = s.platform
     AND   s.center_type_code = 'CGCC');


    UPDATE sample_summary_report_detail s
    SET   level_4_submitted =
    (SELECT DISTINCT 'Y'
     FROM  file_info f, disease d, archive_info a , center c, platform p, file_to_archive fa
     WHERE a.is_latest = 1
     AND   a.disease_id = d.disease_id
     AND   a.platform_id = p.platform_id
     AND   a.center_id = c.center_id
     AND   a.archive_id = fa.archive_id
     AND   fa.file_id = f.file_id
     AND   f.level_number = 4
     AND   c.domain_name = s.center_name
     AND   p.platform_alias = s.platform
     AND   s.center_type_code = 'CGCC');

    /*
    ** set all numeric fields to zero if they are null for ease of display and set the last_refresh timestamp
    */

    UPDATE sample_summary_report_detail
    SET total_with_level3           = NVL(total_with_level3,0),
        total_with_level2           = NVL(total_with_level2,0),
        total_with_level1           = NVL(total_with_level1,0),
        total_bcr_sent              = NVL(total_bcr_sent,0),
        total_centers_sent          = NVL(total_centers_sent,0),
        total_bcr_unaccounted       = NVL(total_bcr_unaccounted,0),
        total_center_unaccounted    = NVL(total_center_unaccounted,0),
        last_refresh                = CURRENT_TIMESTAMP;

    COMMIT;
END;
/

-- foreign key constraints
ALTER TABLE uuid ADD (
    CONSTRAINT fk_uuid_center_id 
    FOREIGN KEY (center_id)
    REFERENCES center(center_id),
    CONSTRAINT fk_uuid_gen_method_id 
    FOREIGN KEY (generation_method_id)
    REFERENCES generation_method(generation_method_id)
);

ALTER TABLE barcode_history ADD (
    CONSTRAINT fk_barcode_hist_uuid
    FOREIGN KEY (uuid)
    REFERENCES uuid(uuid),
    CONSTRAINT fk_barcode__hist_disease
    FOREIGN KEY (disease_id)
    REFERENCES disease(disease_id),
    FOREIGN KEY (item_type_id)
    REFERENCES uuid_item_type (item_type_id)
);
ALTER TABLE archive_info ADD (
  CONSTRAINT fk_archive_center FOREIGN KEY (center_id)
      REFERENCES center (center_id),
  CONSTRAINT fk_archive_platform_ FOREIGN KEY (platform_id)
      REFERENCES platform (platform_id),
  CONSTRAINT fk_archive_disease FOREIGN KEY (disease_id)
      REFERENCES disease (disease_id) ,
  CONSTRAINT fk_archiveinfotype FOREIGN KEY (archive_type_id)
      REFERENCES archive_type (archive_type_id));

ALTER TABLE archive_type ADD
  CONSTRAINT fk_archivetype_level FOREIGN KEY (data_level)
      REFERENCES data_level (level_number);

ALTER TABLE batch_number_assignment ADD 
(CONSTRAINT fk_batch_number_center
FOREIGN KEY (center_id)
REFERENCES center(center_id),
CONSTRAINT fk_batch_number_disease
FOREIGN KEY (disease_id)
REFERENCES disease(disease_id));

ALTER TABLE platform ADD 
(CONSTRAINT fk_platform_center_type 
FOREIGN KEY (center_type_code)
REFERENCES center_type (center_type_code),
CONSTRAINT fk_platform_data_type 
FOREIGN KEY (base_data_type_id)
REFERENCES data_type (data_type_id));

ALTER TABLE biospecimen_barcode ADD (
  CONSTRAINT fk_biospecimen_tss_site FOREIGN KEY (tss_code)
      REFERENCES tissue_source_site (tss_code),
  CONSTRAINT fk_biospecimen_barcode_portion FOREIGN KEY (portion_analyte_code)
      REFERENCES portion_analyte (portion_analyte_code),
  CONSTRAINT fk_biospecimen_barcode_project FOREIGN KEY (project_code)
      REFERENCES project (project_code) ,
  CONSTRAINT fk_biospecimen_barcode_sample FOREIGN KEY (sample_type_code)
      REFERENCES sample_type (sample_type_code));
      
      
ALTER TABLE bcr_biospecimen_to_archive ADD (
  CONSTRAINT fk_biospecimen_archive_arch FOREIGN KEY (archive_id)
      REFERENCES archive_info (archive_id),
  CONSTRAINT fk_biospecimen_archive_biospec FOREIGN KEY (biospecimen_id)
      REFERENCES biospecimen_barcode (biospecimen_id));

ALTER TABLE biospecimen_to_file ADD (
  CONSTRAINT fk_biospecimen_file_biospec 
  FOREIGN KEY (biospecimen_id)
  REFERENCES biospecimen_barcode (biospecimen_id),
  CONSTRAINT fk_biospecimen_file_file 
  FOREIGN KEY (file_id)
  REFERENCES file_info (file_id) 
  ON CASCADE DELETE);
            
ALTER TABLE file_to_archive ADD
CONSTRAINT FK_filetoarchive_archiveid 
FOREIGN KEY (archive_id)
 REFERENCES archive_info(archive_id);
ALTER TABLE file_to_archive ADD
CONSTRAINT FK_filetoarchive_fileid 
 FOREIGN KEY (file_id)
 REFERENCES file_info(file_id) ON DELETE CASCADE;

ALTER TABLE file_info ADD (
CONSTRAINT fk_file_info_datatype FOREIGN KEY (data_type_id) 
REFERENCES data_type(data_type_id),
CONSTRAINT fk_file_info_fileRev FOREIGN KEY (revision_of_file_id)
REFERENCES file_info (file_id));

ALTER TABLE data_type_to_platform ADD(
    CONSTRAINT fk_datatype_platform_platform
    FOREIGN KEY (platform_id)
    REFERENCES platform(platform_id),
    CONSTRAINT fk_datatype_platform_datatype
    FOREIGN KEY (data_type_id)
    REFERENCES data_type(data_type_id)
);
alter table data_visibility add (
  constraint fk_data_visibility_visid FOREIGN KEY (visibility_id)
    REFERENCES visibility(visibility_id),
  constraint fk_data_visibility_dtid FOREIGN KEY (data_type_id)
    REFERENCES data_type(data_type_id)
);



-- Security tables
  DROP TABLE users CASCADE CONSTRAINTS;
 CREATE TABLE users(
      username VARCHAR2(50) NOT NULL,
      password VARCHAR2(50) NOT NULL,
      enabled  NUMBER(1)    NOT NULL,
      CONSTRAINT pk_users_idx PRIMARY KEY(username));

DROP TABLE authorities  CASCADE CONSTRAINTS;
CREATE TABLE authorities (
      username     VARCHAR2(50) NOT NULL,
      authority VARCHAR2(50) NOT NULL,
      CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username));

CREATE UNIQUE INDEX ix_auth_username ON authorities (username,authority);

DROP SEQUENCE group_seq;
CREATE SEQUENCE group_seq START WITH 1 INCREMENT BY 1;

DROP TABLE groups CASCADE CONSTRAINTS;
CREATE TABLE groups (
  id         NUMBER(38)     NOT NULL, 
  group_name     VARCHAR2(50)     NOT NULL,
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
  id         NUMBER(38)      NOT NULL, 
  username     VARCHAR2(50)     NOT NULL, 
  group_id     NUMBER(38)     NOT NULL, 
  CONSTRAINT pk_group_member_idx PRIMARY KEY(id),
  CONSTRAINT fk_group_members_group FOREIGN KEY(group_id) REFERENCES groups(id));
        

DROP TABLE persistent_logins CASCADE CONSTRAINTS;
CREATE TABLE persistent_logins (
  username     VARCHAR2(64)     NOT NULL, 
  series     VARCHAR2(64)     NOT NULL,
  token     VARCHAR2(64)     NOT NULL, 
  last_used     TIMESTAMP     NOT NULL,
  CONSTRAINT pk_persistent_login_idx PRIMARY KEY(series));

DROP SEQUENCE acl_sid_seq;
CREATE SEQUENCE acl_sid_seq START WITH 100 INCREMENT BY 1; 

DROP TABLE acl_sid CASCADE CONSTRAINTS;
CREATE TABLE acl_sid (
  id         NUMBER(38)     NOT NULL,
  principal     NUMBER(1)     NOT NULL,
  sid         VARCHAR2(100)     NOT NULL,
  CONSTRAINT pk_acl_idx PRIMARY KEY(id),
  CONSTRAINT uk_acl_sid_idx UNIQUE(sid,principal) );

DROP SEQUENCE acl_class_seq;
CREATE SEQUENCE acl_class_seq START WITH 100 INCREMENT by 1;

DROP TABLE acl_class CASCADE CONSTRAINTS;
CREATE TABLE acl_class (
  id     NUMBER(38)     NOT NULL, 
  class VARCHAR2(100)     NOT NULL, 
  CONSTRAINT pk_acl_class_idx PRIMARY KEY(id),
  CONSTRAINT uk_acl_class_idx UNIQUE (class));

DROP SEQUENCE acl_object_id_seq;
CREATE SEQUENCE acl_object_id_seq START WITH 100 INCREMENT BY 1; 

DROP TABLE acl_object_identity CASCADE CONSTRAINTS;
CREATE TABLE acl_object_identity (
  id             NUMBER(38) NOT NULL, 
  object_id_class     NUMBER(38) NOT NULL, 
  object_id_identity     NUMBER(38) NOT NULL, 
  parent_object     NUMBER(38), 
  owner_sid         NUMBER(38), 
  entries_inheriting     NUMBER(1)  NOT NULL, 
  CONSTRAINT pk_acl_object_id_idx   PRIMARY KEY (id),
  CONSTRAINT uk_acl_object_id_idx   UNIQUE (object_id_class,object_id_identity), 
  CONSTRAINT fk_acl_obj_id_parent   FOREIGN KEY(parent_object) REFERENCES acl_object_identity(id), 
  CONSTRAINT fk_acl_obj_id_class    FOREIGN KEY(object_id_class) REFERENCES acl_class(id), 
  CONSTRAINT fk_acl_obj_id_ownersid FOREIGN KEY(owner_sid) REFERENCES acl_sid(id) );

DROP SEQUENCE acl_entry_seq;
CREATE SEQUENCE acl_entry_seq START WITH 100 INCREMENT BY 1;

DROP TABLE acl_entry CASCADE CONSTRAINTS;
CREATE TABLE acl_entry ( 
  id             NUMBER(38) NOT NULL, 
  acl_object_identity     NUMBER(38) NOT NULL,
  ace_order         NUMBER(38) NOT NULL,
  sid             NUMBER(38) NOT NULL, 
  mask             NUMBER(38) NOT NULL,
  granting         NUMBER(1)  NOT NULL,
  audit_success     NUMBER(1)  NOT NULL, 
  audit_failure     NUMBER(1)  NOT NULL,
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

DROP TABLE transaction_log CASCADE CONSTRAINTS;
CREATE TABLE transaction_log
(
  TRANSACTION_LOG_ID    NUMBER(38)    NOT NULL,
  ARCHIVE_NAME        VARCHAR2(2000)     NOT NULL,  
  ENVIRONMENT           VARCHAR2(50)     NOT NULL,
  ISSUCCESSFUL          CHAR(1)        check (isSuccessful in ('Y','N')), 
  CREATED_DATE        TIMESTAMP,
  UPDATED_DATE        TIMESTAMP,
  CONSTRAINT transaction_log_id_idx PRIMARY KEY (TRANSACTION_LOG_ID) 
);

DROP TABLE transaction_log_record CASCADE CONSTRAINTS;
CREATE TABLE transaction_log_record
(
  TRANSACTION_LOG_RECORD_ID    NUMBER(38)    NOT NULL,
  TRANSACTION_LOG_ID        NUMBER(38)     NOT NULL,  
  LOGGING_STATE                 VARCHAR2(500)    NOT NULL,  
  TRANSACTION_LOG_TS            TIMESTAMP(6)       NOT NULL,
  ISSUCCESSFUL              CHAR(1)            check (isSuccessful in ('Y','N')), 
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
  TRANSACTION_ERROR_ID      NUMBER(38)    NOT NULL,
  TRANSACTION_LOG_ID        NUMBER(38)  NOT NULL,
  ARCHIVE_NAME    VARCHAR2(2000)     NOT NULL,  
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
DROP SEQUENCE     transaction_log_error_id_seq;
CREATE SEQUENCE transaction_log_error_id_seq
    MINVALUE 1
    START WITH 1
    INCREMENT BY 1;
DROP SEQUENCE shipped_biospec_element_seq;
CREATE SEQUENCE shipped_biospec_element_seq START WITH 1 INCREMENT BY 1;

DROP TABLE shipped_item_type CASCADE CONSTRAINTS;
CREATE TABLE shipped_item_type (
    shipped_item_type_id        INTEGER        NOT NULL,
    shipped_item_type        VARCHAR2(20)    NOT NULL,
    CONSTRAINT shipped_item_type_pk_idx PRIMARY KEY (shipped_item_type_id)
);

DROP TABLE shipped_element_type CASCADE CONSTRAINTS;
CREATE TABLE shipped_element_type (
    element_type_id    INTEGER        NOT NULL,
    element_type_name    VARCHAR2(100)    NOT NULL,
    CONSTRAINT element_type_pk_idx PRIMARY KEY (element_type_id)
);

DROP TABLE shipped_biospecimen CASCADE CONSTRAINTS;
CREATE TABLE shipped_biospecimen (
    shipped_biospecimen_id        NUMBER(38)    NOT NULL,
    uuid                    VARCHAR2(36)    NOT NULL,
    shipped_item_type_id        INTEGER            NOT NULL,
    built_barcode        VARCHAR2(50)    NOT NULL,
    project_code            VARCHAR2(10)    NOT NULL,
    tss_code                VARCHAR2(10)    NOT NULL,
    bcr_center_id            VARCHAR2(10)    NOT NULL,
    participant_code        VARCHAR2(25)    NOT NULL,
    is_viewable                NUMBER(1)        DEFAULT 1,
    is_redacted                NUMBER(1)        DEFAULT 0,
    shipped_date            DATE,
    is_control            NUMBER(1)     DEFAULT 0,
    batch_id            INTEGER,
    CONSTRAINT shipped_biospecimen_pk_idx PRIMARY KEY (shipped_biospecimen_id)
);

CREATE UNIQUE INDEX uk_shipped_biospec_uuid_idx ON shipped_biospecimen(uuid);

ALTER TABLE shipped_biospecimen ADD (
    CONSTRAINT fk_shipped_biospec_project 
    FOREIGN KEY (project_code)
    REFERENCES project (project_code),
    CONSTRAINT fk_shipped_biospec_bcrcenter 
    FOREIGN KEY (bcr_center_id)
    REFERENCES center_to_bcr_center (bcr_center_id),
    CONSTRAINT fk_shipped_biospec_tss 
    FOREIGN KEY (tss_code)
    REFERENCES tissue_source_site (tss_code),
    CONSTRAINT fk_shipped_biospec_itemtype 
    FOREIGN KEY (shipped_item_type_id)
    REFERENCES shipped_item_type (shipped_item_type_id)
);

DROP TABLE shipped_biospecimen_element;
CREATE TABLE shipped_biospecimen_element (
    shipped_biospecimen_element_id        NUMBER(38)        NOT NULL,
    shipped_biospecimen_id            NUMBER(38)        NOT NULL,
    element_type_id                    INTEGER            NOT NULL,
    element_value                    VARCHAR2(100)    NOT NULL,
    CONSTRAINT shipped_biospec_element_pk_idx PRIMARY KEY (shipped_biospecimen_element_id)
);
CREATE UNIQUE INDEX shipped_biospec_element_uk_idx ON shipped_biospecimen_element (shipped_biospecimen_id,element_type_id);

ALTER TABLE shipped_biospecimen_element ADD (
    CONSTRAINT fk_shipped_biospec_elem_type
    FOREIGN KEY (element_type_id)
    REFERENCES shipped_element_type (element_type_id),
    CONSTRAINT fk_shippedbio_elem_shipbio
    FOREIGN KEY (shipped_biospecimen_id)
    REFERENCES shipped_biospecimen(shipped_biospecimen_id)
);

DROP TABLE shipped_biospec_bcr_archive;
CREATE TABLE shipped_biospec_bcr_archive (
    shipped_biospecimen_id    NUMBER(38) NOT NULL,
    archive_id        NUMBER(38) NOT NULL,
    CONSTRAINT ship_biospec_archive_pk_idx PRIMARY KEY (shipped_biospecimen_id,archive_id) 
);


DROP TABLE shipped_biospecimen_file CASCADE CONSTRAINTS;
CREATE TABLE shipped_biospecimen_file (
    shipped_biospecimen_id    NUMBER(38) NOT NULL,
    file_id            NUMBER(38) NOT NULL,
    CONSTRAINT shipped_biospec_file_pk_idx PRIMARY KEY (shipped_biospecimen_id,file_id) 
);

ALTER TABLE shipped_biospecimen_file ADD (
    CONSTRAINT fk_ship_biospecfile_biospec
    FOREIGN KEY (shipped_biospecimen_id)
    REFERENCES shipped_biospecimen(shipped_biospecimen_id),
    CONSTRAINT fk_shipped_biospec_file
    FOREIGN KEY (file_id)
    REFERENCES file_info(file_id)
);
ALTER TABLE shipped_biospec_bcr_archive ADD (
    CONSTRAINT fk_shipped_biospec_biospec
    FOREIGN KEY (shipped_biospecimen_id)
    REFERENCES shipped_biospecimen(shipped_biospecimen_id),
    CONSTRAINT fk_shipped_biospec_archive
    FOREIGN KEY (archive_id)
    REFERENCES archive_info(archive_id)
);

DROP TABLE shipped_biospecimen_bamfile;
CREATE TABLE shipped_biospecimen_bamfile (
   shipped_biospecimen_id    NUMBER(38) NOT NULL,
   bam_file_id            NUMBER(38) NOT NULL,
   CONSTRAINT pk_shipped_biospec_bamfile_idx PRIMARY KEY (shipped_biospecimen_id,bam_file_id)
);

ALTER TABLE shipped_biospecimen_bamfile ADD (
   CONSTRAINT fk_shipped_biospec_bam_biospec
   FOREIGN KEY (shipped_biospecimen_id)
   REFERENCES shipped_biospecimen (shipped_biospecimen_id),
   CONSTRAINT fk_shipped_biospec_bam_bamfile
   FOREIGN KEY (bam_file_id)
   REFERENCES bam_file (bam_file_id)
);

DROP TABLE shipped_biospec_ncbi_trace;
CREATE TABLE shipped_biospec_ncbi_trace (
   biospecimen_trace_id        NUMBER(38) NOT NULL,
   shipped_biospecimen_id     NUMBER(38) NOT NULL,
   ncbi_trace_id        NUMBER(38) NOT NULL,
   file_id            NUMBER(38) NOT NULL,
   dcc_date_received        DATE       NOT NULL,
   CONSTRAINT pk_shipped_biospec_ncbitr_idx PRIMARY KEY (biospecimen_trace_id)
);

DROP INDEX ship_biospec_ncbifile_file_idx;
CREATE INDEX ship_biospec_ncbifile_file_idx ON shipped_biospec_ncbi_trace (file_id);

ALTER TABLE shipped_biospec_ncbi_trace ADD (
CONSTRAINT fk_ship_biospec_ncbi_biospec
FOREIGN KEY (shipped_biospecimen_id)
REFERENCES shipped_biospecimen(shipped_biospecimen_id),
CONSTRAINT fk_shipp_biospec_ncbi_file
FOREIGN KEY (file_id)
REFERENCES file_info(file_id) ON DELETE CASCADE
);

DROP TABLE file_collection cascade constraints;
CREATE TABLE file_collection (
    file_collection_id     NUMBER(38)    NOT NULL,
    collection_name        VARCHAR2(50)    NOT NULL,
    visibility_id        NUMBER(38)    NOT NULL,
    disease_id        NUMBER(38),
    center_type_code    VARCHAR2(10),
    center_id        NUMBER(38),
    platform_id        NUMBER(38),
    CONSTRAINT pk_file_collection_idx PRIMARY KEY(file_collection_id)
);
ALTER TABLE file_collection ADD (
    CONSTRAINT fk_file_collection_visibility
    FOREIGN KEY(visibility_id)
    REFERENCES visibility(visibility_id),
    CONSTRAINT fk_file_collection_disease
    FOREIGN KEY(disease_id)
    REFERENCES disease(disease_id),
    CONSTRAINT fk_file_collection_center
    FOREIGN KEY(center_id)
    REFERENCES center(center_id),
    CONSTRAINT fk_file_collection_platform
    FOREIGN KEY(platform_id)
    REFERENCES platform(platform_id)
);


DROP TABLE file_to_collection;
CREATE TABLE file_to_collection (
    file_collection_id    NUMBER(38)    NOT NULL,
    file_id            NUMBER(38)    NOT NULL,
    file_location_url    VARCHAR2(2000)    NOT NULL,
    file_date        DATE        NOT NULL,
    CONSTRAINT pk_file_to_collection_idx PRIMARY KEY (file_collection_id,file_id)
);
ALTER TABLE file_to_collection ADD (
    CONSTRAINT fk_file_collection_collection
    FOREIGN KEY(file_collection_id)
    REFERENCES file_collection(file_collection_id),
    CONSTRAINT fk_file_collection_file
    FOREIGN KEY(file_id)
    REFERENCES file_info(file_id) ON DELETE CASCADE
);

DROP TABLE control_type CASCADE CONSTRAINTS;
CREATE TABLE control_type (
    control_type_id        INTEGER        NOT NULL,
    control_type        VARCHAR2(20)    NOT NULL,
    xml_name        VARCHAR2(50)    NOT NULL,
    CONSTRAINT pk_control_type_idx PRIMARY KEY ( control_Type_id)
);

DROP TABLE control CASCADE CONSTRAINTS;
CREATE TABLE control (
    control_id    NUMBER(38)    NOT NULL,
    control_type_id    INTEGER        NOT NULL,
    CONSTRAINT pk_control_idx PRIMARY KEY (control_id)
);


ALTER TABLE control ADD (
    CONSTRAINT fk_control_control_id FOREIGN KEY (control_id) REFERENCES shipped_biospecimen(shipped_biospecimen_id),
    CONSTRAINT fk_control_controltype FOREIGN KEY (control_type_id) REFERENCES control_type(control_Type_id)
);

DROP TABLE control_to_disease;
CREATE TABLE control_to_disease (
    control_id    NUMBER(38)     NOT NULL,
    disease_id    NUMBER(38)    NOT NULL
);

ALTER TABLE control_to_disease ADD (
    CONSTRAINT fk_control_control 
    FOREIGN KEY (control_id) REFERENCES control (control_id),
    CONSTRAINT fk_control_disease 
    FOREIGN KEY (disease_id) REFERENCES disease(disease_id)
);

DROP TABLE pending_uuid;
CREATE TABLE pending_uuid (
   pending_uuid_id    NUMBER(38)    NOT NULL,
   bcr             VARCHAR2(10)    NOT NULL,
   center        VARCHAR2(10),
   ship_date        DATE,
   plate_id        VARCHAR2(10),   
   batch_number      INTEGER,
   plate_coordinate    VARCHAR2(10),
   uuid            VARCHAR2(36),
   barcode        VARCHAR2(50),
   sample_type        VARCHAR2(10),
   analyte_type        VARCHAR2(10),
   portion_number    VARCHAR2(10),
   vial_number        VARCHAR2(10),
   item_type        VARCHAR2(20),
   dcc_received_date    DATE,
   created_date        DATE DEFAULT sysdate    NOT NULL,
   CONSTRAINT pk_pending_uuid_idx PRIMARY KEY (pending_uuid_id)
);

DROP SEQUENCE vcf_header_seq;
CREATE SEQUENCE vcf_header_seq START WITH 1 INCREMENT BY 1;

DROP TABLE vcf_header_definition;
CREATE TABLE vcf_header_definition (
    vcf_header_Def_id    NUMBER(38)    NOT NULL,
    header_type_name    VARCHAR2(10)    NOT NULL CHECK (header_type_name in ('INFO','FORMAT')),
    id_name                VARCHAR2(500)    NOT NULL,
    number_value     VARCHAR2(10)    NOT NULL,
    type                VARCHAR2(25)    NOT NULL,
    description            VARCHAR2(4000)    NOT NULL,
    CONSTRAINT pk_vcf_header_def_idx PRIMARY KEY (vcf_header_def_id)
);

CREATE UNIQUE INDEX uk_vcf_header_def_idx ON vcf_header_definition (header_Type_name,id_name);

DROP TABLE dcc_property;
CREATE TABLE dcc_property (
    property_id                NUMBER(38,0)    NOT NULL,
    property_name               VARCHAR2(4000)    NOT NULL,
    property_value            VARCHAR2(4000),
    property_description    VARCHAR2(4000),
    application_name             VARCHAR2(100),
    server_name             VARCHAR2(100),
    CONSTRAINT pk_dcc_property_idx PRIMARY KEY (property_id)
);

CREATE INDEX dcc_prop_propname_idx ON dcc_property (property_name);
CREATE INDEX dcc_prop_servname_idx ON dcc_property (server_name);
CREATE INDEX dcc_prop_appname_idx ON dcc_property (application_name);
CREATE UNIQUE INDEX uk_dcc_property_idx ON dcc_property (application_name, property_name, server_name);

DROP SEQUENCE dcc_property_seq;
CREATE SEQUENCE dcc_property_seq START WITH 1 INCREMENT BY 1;

DROP TABLE pcod_normal_tumor_stats;
CREATE TABLE pcod_normal_tumor_stats(
    disease_abbreviation        VARCHAR2(10)    NOT NULL,
    participant_barcode        VARCHAR2(20)    NOT NULL,
    cn_TumorCount            INTEGER,
    cn_NormalCount            INTEGER,
    expArray_TumorCount        INTEGER,
    expArray_NormalCount        INTEGER,
    expRnaSeq_TumorCount        INTEGER,
    expRnaSeq_NormalCount        INTEGER,
    mirna_TumorCount        INTEGER,
    mirna_NormalCount        INTEGER,
    methylation_TumorCount        INTEGER,
    methylation_NormalCount        INTEGER,
    mutation_TumorCount        INTEGER,
    mutation_NormalCount        INTEGER,
    gsc_genome_TumorCount        INTEGER,
    gsc_genome_NormalCount        INTEGER,
    gsc_exome_TumorCount        INTEGER,
    gsc_exome_NormalCount        INTEGER,
    gsc_rnaseq_TumorCount        INTEGER,
    gsc_rnaseq_NormalCount        INTEGER,
    gsc_mirna_TumorCount        INTEGER,
    gsc_mirna_NormalCount        INTEGER);



CREATE OR REPLACE FORCE VIEW SHIPPED_BIOSPECIMEN_ALIQUOT
(
   SHIPPED_BIOSPECIMEN_ID,
   BARCODE,
   BIOSPECIMEN,
   ANALYTE,
   SAMPLE,
   SPECIFIC_PATIENT,
   PROJECT_CODE,
   TSS_CODE,
   PATIENT,
   SAMPLE_TYPE_CODE,
   SAMPLE_SEQUENCE,
   PORTION_SEQUENCE,
   PORTION_ANALYTE_CODE,
   PLATE_ID,
   BCR_CENTER_ID,
   IS_REDACTED,
   IS_VIEWABLE,
   SHIP_DATE
)
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
            be1.element_value AS sample_type_code,
            be2.element_value AS sample_sequence,
            be3.element_value AS portion_sequence,
            be4.element_value AS portion_analyte_code,
            be5.element_value AS plate_id,
            b.bcr_center_id,
            b.is_redacted,
            b.is_viewable,
            b.shipped_date
     FROM   shipped_biospecimen b,
            shipped_biospecimen_element be1,
            shipped_biospecimen_element be2,
            shipped_biospecimen_element be3,
            shipped_biospecimen_element be4,
            shipped_biospecimen_element be5
    WHERE   b.shipped_item_type_id = 1
            AND (b.shipped_biospecimen_id = be1.shipped_biospecimen_id
                 AND be1.element_type_id = 1)
            AND (b.shipped_biospecimen_id = be2.shipped_biospecimen_id
                 AND be2.element_type_id = 2)
            AND (b.shipped_biospecimen_id = be3.shipped_biospecimen_id
                 AND be3.element_type_id = 3)
            AND (b.shipped_biospecimen_id = be4.shipped_biospecimen_id
                 AND be4.element_type_id = 4)
            AND (b.shipped_biospecimen_id = be5.shipped_biospecimen_id
                 AND be5.element_type_id = 5);



CREATE OR REPLACE FORCE VIEW shipped_biospecimen_breakdown AS
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

CREATE OR REPLACE PACKAGE pcod_report 
AS
    PROCEDURE build_projectOverview_counts;
    
    PROCEDURE get_pcod_case_counts;
    
    PROCEDURE get_pcod_normal_tumor_counts;
END;
/
CREATE OR REPLACE PACKAGE BODY pcod_report 
IS
     /*
     ** This package contains procedures to populate 2 tables for the Project Overview Case Counts (PCOD) report. One
     ** will populate a table with counts of tumor and normal samples for each participant, by disease and type of data.
     ** The other will calculate total case counts by disease and types of data.
     **
     **  Written by Shelley Alonso
     **  
     **  Modification History
     **
     **  04/02/2012   Shelley Alonso    Created as a package to include existing procedure build_projectOverview_counts
     **                                 and add new procedure get_pcod_normal_tumor_counts to populate a new table which will be 
     **                                 used to determine whether or not a case is complete (APPS-5706)
     **
     **  05/21/2012  Shelley Alonso    APPS-6212 Added two new columns to the projectoverview_case_counts table to capture counts of 
     **                                 cases for whom low pass sequencing data has been submitted. These have been counted in with 
     **                    Copy Number data up to now, based on data type. Now they will be seperated out,for GCC, based on platform
     **                    IlluminaHiSeq_DNASeqC, and if they are IlluminaHiSeq_DNASeqC, will be excluded from the GCC copynumber counts.
     **                                 For GSC, the will counted as gsc low pass sequencing counts if the bam file was submitted by
     **                                 Harvard Medical School (HMS) and will be excluded from the counts for GSC gsc_genome_cases.
     */
          
     PROCEDURE build_projectOverview_counts 
     IS
     BEGIN
     
         get_pcod_normal_tumor_counts;
         get_pcod_case_counts;
     
     END build_projectOverview_counts;
     
     PROCEDURE get_pcod_case_counts
     /*
     ** This procedure will get a count of patients/cases for which we have received data for each disease for the following
     ** data types:
     **     Copy Number
     **     Expression Array 
     **     MicroRNA
     **     Methylation
     **     Expression RNA Sequence
     **     GSC Mutation
     **     GSC Genomic 
     **     GSC Exome
     **     GSC MicroRNA
     **     GSC RNA Sequence
     **
     ** Revision History
     **
     ** Shelley Alonso   01/25/2011  Add qualifier for disease select to only get active diseases 
     **                  02/14/2011  Remove qualifier for disease..all should be on pcod report 
     **                  06/27/2011  Add qualifier for active diseases because PO decided they don't want LCLL on the report; it
     **                              has been set to inactive
     **                  06/28/2011  Change the queries to calculate counts from dccCommon tables. No longer go to the disease
     **                              schemas because the l2 data is no longer loaded APPS-3893
     **                 08/09/2011  Changed to use shipped_biospecimen and related tables instead of biospecimen_barcode and related 
     **                              tables
     */
     IS  
       replaceString            VARCHAR2(4000);
       sqlString                VARCHAR2(4000);
       insertStatement          VARCHAR2(300);
       insertStatement2         VARCHAR2(300);
       mergeStatement           VARCHAR2(50); 
       mergeDataSetResults      VARCHAR2(2000);
       mergeMafResults          VARCHAR2(2000);
     BEGIN
         /* 
         ** Get the rolled up counts of unique cases (or patients) by specific data types, for which we have level 2 or 3
         ** data and put it into a reporting table in dccCommon.  
         **
         ** Use MERGE statements so that an insert can be specified if there is no record for the disease,
         ** and an update can be specified if there is a record for the disease, in the projectOverview_case_count
         ** table.
         */
     
     MERGE INTO projectOverview_case_counts c 
     USING 
     (SELECT disease_abbreviation,
         max(decode(data_type,'cn' ,cases,0)) cnCases,
         max(decode(data_type,'exp', cases,0)) expCases,
         max(decode(data_type,'mirna', cases,0))  mirnaCases,  
         max(decode(data_type,'methylation', cases,0))  methCases,
         max(decode(data_type,'rnaseq', cases,0))  rnaseqCases, 
         max(decode(data_type,'mute', cases, 0)) mutationCases,
         max(decode(data_type,'lowpass',cases, 0)) lowpassCases
      FROM  
       (SELECT d.disease_abbreviation,
           COUNT(distinct b.participant_code) as cases, 
           CASE  WHEN dt.ftp_display IN ('snp','cna') and p.platform_name != 'IlluminaHiSeq_DNASeqC' THEN 'cn' 
             WHEN dt.ftp_display = 'cna' and p.platform_name = 'IlluminaHiSeq_DNASeqC' then 'lowpass'
             WHEN dt.ftp_display IN ('transcriptome','exon') THEN 'exp' 
             WHEN dt.ftp_display IN ('mirna','mirnaseq') THEN 'mirna'
             WHEN dt.ftp_display IN ('mutations', 'tracerel') THEN 'mute'
             WHEN dt.ftp_display IN ('rnaseq', 'rnaseqV2') THEN 'rnaseq'
             ELSE dt.ftp_display END  as data_type  
        FROM  shipped_biospecimen b, 
          shipped_biospecimen_file bf, 
          file_info f,
          file_to_archive fa, 
          archive_info a,
          platform p,
          data_type dt,
          disease d 
        WHERE b.is_viewable=1
        AND   b.shipped_biospecimen_id=bf.shipped_biospecimen_id
        AND   bf.file_id=f.file_id
        AND   f.level_number in (2,3)
        AND   f.file_id = fa.file_id
        AND   fa.archive_id = a.archive_id
        AND   a.is_latest=1
        AND   a.disease_id=d.disease_id
        AND   a.platform_id=p.platform_id
        AND   p.center_type_code != 'BCR'
        AND   p.base_data_type_id = dt.data_type_id 
        GROUP BY d.disease_abbreviation,
             CASE  WHEN dt.ftp_display IN ('snp','cna') and p.platform_name != 'IlluminaHiSeq_DNASeqC' THEN 'cn' WHEN dt.ftp_display = 'cna' and p.platform_name = 'IlluminaHiSeq_DNASeqC' then 'lowpass' WHEN dt.ftp_display IN ('transcriptome','exon') THEN 'exp' WHEN dt.ftp_display IN ('mirna','mirnaseq') THEN 'mirna' WHEN dt.ftp_display IN ('mutations', 'tracerel') THEN 'mute' WHEN dt.ftp_display IN ('rnaseq', 'rnaseqV2') THEN 'rnaseq' ELSE dt.ftp_display END)
       GROUP by disease_abbreviation) v 
     ON (c.disease_abbreviation = v.disease_abbreviation) 
     WHEN MATCHED THEN UPDATE SET  
         c.copyNumber_data_cases=v.cnCases, 
         c.expArray_data_cases= v.expCases, 
         c.microRna_data_cases=v.mirnaCases, 
         c.metholated_data_cases=v.methCases, 
         c.expRnaSeq_data_cases=v.rnaSeqCases , 
         c.gsc_mutation_data_cases=v.mutationCases,
         c.gcc_lowpass_cases = v.lowpassCases
     WHEN NOT MATCHED THEN 
       INSERT (
           project_overview_id,
           disease_abbreviation,
           copyNumber_data_cases, 
           expArray_data_cases,
           microRna_data_cases,
           metholated_data_cases,
           expRnaSeq_data_cases,
           gsc_mutation_data_cases,
           gcc_lowpass_cases)  
       VALUES (
           report_seq.nextval,
           v.disease_abbreviation,
           v.cnCases, 
           v.expCases, 
           v.mirnaCases, 
           v.methCases, 
           v.rnaSeqCases, 
           v.mutationCases,
           v.lowpassCases);

     COMMIT;

         /* 
         ** Finally get the bam file case totals. They are in a seperate table and not sent in archives like the other types.
         */
         MERGE INTO projectOverview_case_counts c USING 
             (SELECT max(decode(data_type,'Exome', cases,0))   exomeCases,
                     max(decode(data_type,'miRNA', cases,0))   miRnaCases,
                     max(decode(data_type,'Genome', cases,0))  genomeCases, 
                     max(decode(data_type,'RNAseq', cases,0))  rnaSeqCases, 
                     max(decode(data_type,'lowpass', cases,0)) lowPassCases,
                     disease 
              FROM 
                 (SELECT 
                    count(distinct specific_patient) as cases,
                    CASE
                      WHEN bd.general_datatype = 'Genome' and c.domain_name = 'hms.harvard.edu' THEN 'lowpass'
                      ELSE bd.general_datatype           
                    END as data_type, 
                    d.disease_abbreviation           as disease
                  FROM bam_file b, bam_file_datatype bd,shipped_biospecimen_bamfile bf, shipped_biospecimen_aliquot bb, disease d, center c 
                  WHERE b.bam_datatype_id = bd.bam_datatype_id 
                  AND   b.bam_file_id     = bf.bam_file_id 
                  AND   bf.shipped_biospecimen_id = bb.shipped_biospecimen_id
                  AND   b.disease_id      = d.disease_id 
                  AND   b.center_id      = c.center_id
                  GROUP BY CASE WHEN bd.general_datatype = 'Genome' and c.domain_name = 'hms.harvard.edu' THEN 'lowpass' ELSE bd.general_datatype END,disease_abbreviation)
              GROUP BY disease ) v
              ON (c.disease_abbreviation = v.disease) 
          WHEN MATCHED THEN UPDATE SET 
              c.gsc_genome_cases   = v.genomeCases, 
              c.gsc_exome_cases    = v.exomeCases,
              c.gsc_rnaseq_cases   = v.rnaSeqCases, 
              c.gsc_microRna_cases = v.miRnaCases,
              c.gsc_lowpass_cases  = v.lowPassCases
          WHEN NOT MATCHED THEN          
                  INSERT (
                     project_overview_id,
                     disease_abbreviation,
                     gsc_genome_cases, 
                     gsc_exome_cases,
                     gsc_rnaseq_cases, 
                     gsc_microRna_cases,
                     gsc_lowpass_cases)
                  VALUES (
                     report_seq.nextval,
                     v.disease,
                     v.genomeCases, 
                     v.exomeCases, 
                     v.rnaseqCases, 
                     v.miRnaCases,
                     v.lowPassCases);                
         COMMIT;
     
     
     END;
     
     PROCEDURE get_pcod_normal_tumor_counts IS
     /*
     **  This procedure will determine if we have recieved data in each data type category by disease and case/participant for normal 
     **  and tumor samples for the following data types:
     **  
     **     Copy Number
     **     Expression Array 
     **     MicroRNA
     **     Methylation
     **     Expression RNA Sequence
     **     GSC Mutation
     **     GSC Genomic 
     **     GSC Exome
     **     GSC MicroRNA
     **     GSC RNA Sequence
     */
     BEGIN
    /* 
    ** Determine if we have , for which we have level 2 or 3
    ** data and put it into a reporting table in dccCommon.  
    **
    ** Use MERGE statements so that an insert can be specified if there is no record for the disease,
    ** and an update can be specified if there is a record for the disease, in the projectOverview_case_count
    ** table.
    */
        MERGE INTO pcod_normal_tumor_stats pcod USING
        (SELECT disease_abbreviation,
               participant,
               max(decode(data_type,'cnTumor' ,cnt,0)) cnTumorSamples,
               max(decode(data_type,'cnNormal' ,cnt,0)) cnNormalSamples,
               max(decode(data_type,'expTumor', cnt,0)) expTumorSamples,
               max(decode(data_type,'expNormal', cnt,0)) expNormalSamples,
               max(decode(data_type,'mirnaTumor', cnt,0))  mirnaTumorSamples,  
               max(decode(data_type,'mirnaNormal', cnt,0))  mirnaNormalSamples,  
               max(decode(data_type,'methylationTumor', cnt,0))  methTumorSamples,
               max(decode(data_type,'methylationNormal', cnt,0))  methNormalSamples,
               max(decode(data_type,'rnaseqTumor', cnt,0))  rnaseqTumorSamples, 
               max(decode(data_type,'rnaseqNormal', cnt,0))  rnaseqNormalSamples, 
               max(decode(data_type,'muteTumor', cnt, 0)) mutationTumorSamples, 
               max(decode(data_type,'muteNormal', cnt, 0)) mutationNormalSamples 
        FROM  
             (SELECT distinct d.disease_abbreviation,
               b.participant,1 as cnt,
                CASE  
                    WHEN dt.ftp_display IN ('snp','cna') AND s.is_tumor = 1 THEN 'cnTumor' 
                WHEN dt.ftp_display IN ('snp','cna') AND s.is_tumor = 0 THEN 'cnNormal'
                WHEN dt.ftp_display IN ('transcriptome','exon') AND s.is_tumor = 1 THEN 'expTumor' 
                WHEN dt.ftp_display IN ('transcriptome','exon') AND s.is_tumor = 0 THEN 'expNormal'
                WHEN dt.ftp_display IN ('mirna','mirnaseq') AND s.is_tumor = 1 THEN 'mirnaTumor'
                WHEN dt.ftp_display IN ('mirna','mirnaseq') AND s.is_tumor = 0 THEN 'mirnaNormal'
                WHEN dt.ftp_display IN ('mutations', 'tracerel') AND s.is_tumor = 1 THEN 'muteTumor'
                WHEN dt.ftp_display IN ('mutations', 'tracerel') AND s.is_tumor = 0 THEN 'muteNormal'
                WHEN dt.ftp_display IN ('rnaseq', 'rnaseqV2') AND s.is_tumor = 0 THEN 'rnaseqNormal'
                WHEN dt.ftp_display IN ('rnaseq', 'rnaseqV2') AND s.is_tumor = 1 THEN 'rnaseqTumor'
                ELSE DECODE(s.is_tumor,1,dt.ftp_display||'Tumor',dt.ftp_display||'Normal') 
               END  as data_type                  
                FROM shipped_biospecimen_breakdown b, 
                 shipped_biospecimen_file bf, 
                 file_info f,
                 file_to_archive fa, 
                 archive_info a,
                 platform p,
                 data_type dt,
                 disease d ,
                 sample_type s
             WHERE b.is_viewable=1 
             AND   b.sample_Type_code = s.sample_type_code
             AND   b.shipped_biospecimen_id=bf.shipped_biospecimen_id
             AND   bf.file_id=f.file_id
             AND   f.level_number in (2,3)
             AND   f.file_id = fa.file_id
             AND   fa.archive_id = a.archive_id
             AND   a.is_latest=1
             AND   a.disease_id=d.disease_id
             AND   a.platform_id=p.platform_id
             AND   p.center_type_code != 'BCR'
             AND   p.base_data_type_id = dt.data_type_id 
            GROUP BY d.disease_abbreviation,b.participant,
            CASE  WHEN dt.ftp_display IN ('snp','cna') AND s.is_tumor = 1 THEN 'cnTumor' 
                WHEN dt.ftp_display IN ('snp','cna') AND s.is_tumor = 0 THEN 'cnNormal'
                WHEN dt.ftp_display IN ('transcriptome','exon') AND s.is_tumor = 1 THEN 'expTumor' 
                WHEN dt.ftp_display IN ('transcriptome','exon') AND s.is_tumor = 0 THEN 'expNormal'
                WHEN dt.ftp_display IN ('mirna','mirnaseq') AND s.is_tumor = 1 THEN 'mirnaTumor'
                WHEN dt.ftp_display IN ('mirna','mirnaseq') AND s.is_tumor = 0 THEN 'mirnaNormal'
                WHEN dt.ftp_display IN ('mutations', 'tracerel') AND s.is_tumor = 1 THEN 'muteTumor'
                WHEN dt.ftp_display IN ('mutations', 'tracerel') AND s.is_tumor = 0 THEN 'muteNormal'
                WHEN dt.ftp_display IN ('rnaseq', 'rnaseqV2') AND s.is_tumor = 0 THEN 'rnaseqNormal'
                WHEN dt.ftp_display IN ('rnaseq', 'rnaseqV2') AND s.is_tumor = 1 THEN 'rnaseqTumor'
               ELSE DECODE(s.is_tumor,1,dt.ftp_display||'Tumor',dt.ftp_display||'Normal') END
            )
        GROUP BY disease_abbreviation,participant) v
        ON (pcod.disease_abbreviation = v.disease_abbreviation AND pcod.participant_barcode = v.participant) 
        WHEN MATCHED THEN UPDATE SET 
           pcod.cn_TumorCount = v.cnTumorSamples,
           pcod.cn_NormalCount = v.cnNormalSamples,
           pcod.expArray_TumorCount = v.expTumorSamples,
           pcod.expArray_NormalCount = v.expNormalSamples,
           pcod.expRnaSeq_TumorCount = v.rnaSeqTumorSamples,
           pcod.expRnaSeq_NormalCount = v.rnaSeqNormalSamples,
           pcod.mirna_TumorCount = v.mirnaTumorSamples,
           pcod.mirna_NormalCount = v.mirnaNormalSamples,
           pcod.methylation_TumorCount = v.methTumorSamples,
           pcod.methylation_NormalCount = v.methNormalSamples,
           pcod.mutation_TumorCount = v.mutationTumorSamples,
           pcod.mutation_NormalCount = v.mutationNormalSamples
        WHEN NOT MATCHED THEN 
        INSERT (
          disease_abbreviation,
          participant_barcode,
          cn_TumorCount,
          cn_NormalCount,
          expArray_TumorCount,
          expArray_NormalCount,
          expRnaSeq_TumorCount,
          expRnaSeq_NormalCount,
          mirna_TumorCount,
          mirna_NormalCount,
          methylation_TumorCount,
          methylation_NormalCount,
          mutation_TumorCount,
          mutation_NormalCount)
        VALUES (
          v.disease_abbreviation,
          v.participant,
          v.cnTumorSamples,
          v.cnNormalSamples,
          v.expTumorSamples,
          v.expNormalSamples,
          v.rnaSeqTumorSamples,
          v.rnaSeqNormalSamples,
          v.mirnaTumorSamples,
          v.mirnaNormalSamples,
          v.methTumorSamples,
          v.methNormalSamples,
          v.mutationTumorSamples,
          v.mutationNormalSamples);
        COMMIT;

                
    /* 
    ** Finally get the bam file case totals. They are in a seperate table and not sent in archives like the other types.
    */
    MERGE INTO pcod_normal_tumor_stats pcod USING 
     (SELECT
          disease,
              participant,
              max(decode(data_type,'ExomeTumor', cnt,0))    exomeTumorSamples,
              max(decode(data_type,'ExomeNormal', cnt,0))   exomeNormalSamples,
              max(decode(data_type,'miRNATumor', cnt,0))    miRnaTumorSamples,
              max(decode(data_type,'miRNANormal', cnt,0))   miRnaNormalSamples,
              max(decode(data_type,'GenomeTumor', cnt,0))   genomeTumorSamples, 
              max(decode(data_type,'GenomeNormal', cnt,0))  genomenormalSamples, 
              max(decode(data_type,'RNAseqTumor', cnt,0))   rnaSeqTumorSamples, 
              max(decode(data_type,'RNAseqNormal', cnt,0))  rnaSeqNormalSamples
           FROM 
           (SELECT 
              d.disease_abbreviation           as disease,
              bb.participant,
              1 as cnt,
              DECODE(s.is_tumor,1,bd.general_datatype||'Tumor',bd.general_datatype||'Normal')  as data_type         
            FROM bam_file b, bam_file_datatype bd,shipped_biospecimen_bamfile bf, shipped_biospecimen_breakdown bb, disease d, sample_type s 
            WHERE b.bam_datatype_id = bd.bam_datatype_id 
            AND   b.bam_file_id     = bf.bam_file_id 
            AND   bf.shipped_biospecimen_id = bb.shipped_biospecimen_id
            AND   bb.sample_type_code = s.sample_type_code
            AND   b.disease_id      = d.disease_id)
          GROUP BY disease,participant ) v
    ON (pcod.disease_abbreviation = v.disease AND pcod.participant_barcode = v.participant) 
    WHEN MATCHED THEN UPDATE SET 
      pcod.gsc_genome_TumorCount   = v.genomeTumorSamples, 
      pcod.gsc_genome_NormalCount   = v.genomeNormalSamples, 
      pcod.gsc_exome_TumorCount    = v.exomeTumorSamples,
      pcod.gsc_exome_NormalCount    = v.exomeNormalSamples,
      pcod.gsc_rnaseq_TumorCount   = v.rnaSeqTumorSamples, 
      pcod.gsc_rnaseq_NormalCount  = v.rnaSeqNormalSamples, 
      pcod.gsc_miRna_TumorCount = v.miRnaTumorSamples,
      pcod.gsc_miRna_NormalCount = v.miRnaNormalSamples
    WHEN NOT MATCHED THEN          
          INSERT (
         disease_abbreviation,
         participant_barcode,
         gsc_genome_tumorCount,
         gsc_genome_NormalCount,
         gsc_exome_TumorCount,
         gsc_exome_NormalCount,
         gsc_rnaseq_TumorCount,
         gsc_rnaseq_NormalCount,
         gsc_miRna_TumorCount,
         gsc_miRna_NormalCount)
          VALUES (
         v.disease,
         v.participant,
         v.genomeTumorSamples,
         v.genomeNormalSamples,
         v.exomeTumorSamples, 
         v.exomeNormalSamples,
         v.rnaSeqTumorSamples,
         v.rnaSeqNormalSamples,
         v.miRnaTumorSamples,
         v.miRnaNormalSamples);                
    COMMIT;
    
    
    END;
END;
/

DROP TABLE participant_uuid_file;
CREATE TABLE participant_uuid_file (
    uuid    VARCHAR2(36)     NOT NULL,
    file_id    NUMBER(38,0)    NOT NULL,
    CONSTRAINT pk_part_uuid_file_idx PRIMARY KEY (uuid,file_id)
);

ALTER TABLE participant_uuid_file ADD (
    CONSTRAINT fk_uuid_file
    FOREIGN KEY (file_id)
    REFERENCES file_info (file_id) ON DELETE CASCADE
);

CREATE INDEX part_uuid_file_file_idx ON participant_uuid_file (file_id);

DROP MATERIALIZED VIEW case_data_received;
CREATE MATERIALIZED VIEW case_data_received 
BUILD IMMEDIATE
REFRESH COMPLETE ON DEMAND
ENABLE QUERY REWRITE as
SELECT * FROM
(SELECT DISTINCT d.disease_abbreviation,participant as case, bd.general_datatype as data_type,st.sample_type_code,st.is_tumor 
            FROM   bam_file b, bam_file_datatype bd,shipped_biospecimen_bamfile bf, shipped_biospecimen_breakdown sb, 
                   disease d , sample_type st 
            WHERE  b.bam_datatype_id = bd.bam_datatype_id 
            AND    b.bam_file_id     = bf.bam_file_id 
            AND    bf.shipped_biospecimen_id = sb.shipped_biospecimen_id 
            AND    sb.is_viewable = 1 
            AND    sb.is_control = 0 
            AND    sb.sample_type_code=st.sample_type_code 
            AND    b.disease_id = d.disease_id 
            AND    bd.general_datatype = 'Exome' 
UNION
SELECT DISTINCT d.disease_abbreviation, sb.participant as case , 'Exome' as data_type, st.sample_Type_code,st.is_tumor 
            FROM archive_info a,file_to_archive f2a, shipped_biospecimen_file sbf, shipped_biospecimen_breakdown sb, 
                 archive_type at,platform p,disease d, sample_type st 
            WHERE sb.is_viewable = 1
            AND   sb.is_control = 0 
            AND   sb.sample_type_code = st.sample_type_code 
            AND   sb.shipped_biospecimen_id=sbf.shipped_biospecimen_id 
            AND   sbf.file_id=f2a.file_id 
            AND   f2a.archive_id=a.archive_id 
            AND   a.is_latest=1 AND a.deploy_status='Available' 
            AND   a.disease_id=d.disease_id 
            AND   a.platform_id=p.platform_id 
            AND   p.platform_name = 'ABI'
UNION
SELECT DISTINCT d.disease_abbreviation,  sb.participant case,  
                       CASE  
                         WHEN p.platform_name = 'Genome_Wide_SNP_6' THEN 'SNP' 
                         WHEN p.platform_name IN ('IlluminaDNAMethylation_OMA002_CPI','HumanMethylation27','HumanMethylation450','IlluminaDNAMethylation_OMA003_CPI') 
                              THEN 'Methylation' 
                         WHEN d.disease_abbreviation in ('GBM','OV') AND 
                              p.platform_name IN ('IlluminaHiSeq_mRNA_DGE','IlluminaGA_mRNA_DGE','AgilentG4502A_07_1','AgilentG4502A_07_2','AgilentG4502A_07_3', 
                              'HT_HG-U133A','HuEx-1_0-st-v2','IlluminaHiSeq_RNASeqV2', 'IlluminaHiSeq_RNASeq') THEN 'mRNA' 
                         WHEN d.disease_abbreviation in ('GBM','OV') AND p.platform_name in ('H-miRNA_8x15Kv2','H-miRNA_8x15K')  THEN 'miRNA' 
                       END  as data_type , st.sample_type_code, st.is_tumor  
            FROM archive_info a,file_to_archive f2a, shipped_biospecimen_file sb2f, shipped_biospecimen_breakdown sb,archive_type at, 
                 platform p,disease d,sample_type st 
            WHERE sb.is_viewable = 1
            AND   sb.is_control = 0 
            AND   sb.sample_type_code=st.sample_type_code 
            AND   sb.shipped_biospecimen_id=sb2f.shipped_biospecimen_id 
            AND   sb2f.file_id=f2a.file_id 
            AND   f2a.archive_id=a.archive_id 
            AND   a.is_latest=1 AND a.deploy_status='Available' 
            AND   a.archive_type_id=at.archive_type_id 
            AND   at.data_level=1 
            AND   a.disease_id=d.disease_id 
            AND   a.platform_id=p.platform_id 
            AND   p.platform_name IN ('Genome_Wide_SNP_6','IlluminaDNAMethylation_OMA002_CPI','HumanMethylation27','HumanMethylation450', 
                                    'IlluminaDNAMethylation_OMA003_CPI','IlluminaHiSeq_mRNA_DGE','IlluminaGA_mRNA_DGE','AgilentG4502A_07_1', 
                                    'AgilentG4502A_07_2','AgilentG4502A_07_3','HT_HG-U133A','HuEx-1_0-st-v2','IlluminaHiSeq_RNASeqV2', 
                                    'IlluminaHiSeq_RNASeq','H-miRNA_8x15Kv2','H-miRNA_8x15K') 
UNION 
SELECT DISTINCT d.disease_abbreviation, sb.participant as case, 
       DECODE(bd.general_datatype,'RNAseq','mRNA',bd.general_datatype) as data_type , st.sample_type_code, st.is_tumor 
FROM   bam_file b, bam_file_datatype bd,shipped_biospecimen_bamfile bf, shipped_biospecimen_breakdown sb, disease d, center c, sample_type st 
WHERE  b.bam_datatype_id = bd.bam_datatype_id 
AND    b.bam_file_id     = bf.bam_file_id 
AND    bf.shipped_biospecimen_id = sb.shipped_biospecimen_id 
AND    sb.is_viewable = 1 
AND    sb.is_control = 0 
AND    sb.sample_type_code=st.sample_type_code 
AND    b.disease_id = d.disease_id 
AND    b.center_id  = c.center_id 
AND    c.domain_name in ('unc.edu','bcgsc.ca') 
AND    bd.general_datatype in ('RNAseq','miRNA') 
UNION
SELECT DISTINCT d.disease_abbreviation, uh.barcode as case, 'Clinical' as data_type , st.sample_type_code, st.is_tumor
FROM archive_info a, file_to_archive f2a, file_info f, participant_uuid_file puf, uuid_hierarchy uh, uuid_hierarchy uhs, 
 disease d, sample_type st 
WHERE puf.file_id = f.file_id 
AND   f.file_name like '%clinical%.xml' 
AND   f.file_id = f2a.file_id 
AND   f2a.archive_id = a.archive_id 
AND   a.is_latest = 1 
AND   a.deploy_status='Available' 
AND   a.disease_id = d.disease_id 
AND   puf.uuid = uh.uuid 
AND   uh.uuid = uhs.parent_uuid
AND   uhs.sample_type_code = st.sample_type_code 
UNION 
SELECT DISTINCT d.disease_abbreviation,  sb.participant case, 'Clinical' as data_type, st.sample_type_code, st.is_tumor 
FROM archive_info a,file_to_archive f2a, shipped_biospecimen_file sb2f, shipped_biospecimen_breakdown sb, 
 disease d, sample_type st, file_info f 
WHERE sb.is_viewable = 1
AND   sb.is_control = 0 
AND   sb.sample_type_code=st.sample_type_code 
AND   sb.shipped_biospecimen_id=sb2f.shipped_biospecimen_id 
AND   sb2f.file_id=f2a.file_id 
AND   f2a.file_id = f.file_id 
AND   f.file_name like '%clinical%.xml' 
AND   f2a.archive_id=a.archive_id 
AND   a.is_latest=1 AND a.deploy_status='Available' 
AND   a.disease_id=d.disease_id
UNION
SELECT DISTINCT d.disease_abbreviation,  sb.participant case, 'Biospecimen' as data_type, st.sample_type_code, st.is_tumor 
FROM archive_info a,file_to_archive f2a, shipped_biospecimen_file sb2f, shipped_biospecimen_breakdown sb, 
 disease d, sample_type st, file_info f 
WHERE sb.is_viewable = 1
AND   sb.is_control = 0 
AND   sb.sample_type_code=st.sample_type_code 
AND   sb.shipped_biospecimen_id=sb2f.shipped_biospecimen_id 
AND   sb2f.file_id=f2a.file_id 
AND   f2a.file_id = f.file_id 
AND   f.file_name like '%biospecimen%.xml' 
AND   f2a.archive_id=a.archive_id 
AND   a.is_latest=1 AND a.deploy_status='Available' 
AND   a.disease_id=d.disease_id)
WHERE data_type IS NOT NULL;

purge recyclebin;
   