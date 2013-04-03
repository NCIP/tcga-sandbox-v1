DROP TABLE L4_anomaly_data_set CASCADE CONSTRAINTS;
DROP TABLE L4_anomaly_data_set_version CASCADE CONSTRAINTS;
DROP TABLE L4_anomaly_data_version CASCADE CONSTRAINTS;
DROP TABLE L4_anomaly_type CASCADE CONSTRAINTS;
DROP TABLE L4_anomaly_value CASCADE CONSTRAINTS;
DROP TABLE L4_data_set_sample CASCADE CONSTRAINTS;
DROP TABLE L4_genetic_element CASCADE CONSTRAINTS;
DROP TABLE L4_genetic_element_type CASCADE CONSTRAINTS;
DROP TABLE L4_sample CASCADE CONSTRAINTS;
DROP TABLE L4_correlation_type CASCADE CONSTRAINTS;
DROP TABLE L4_data_set_genetic_element CASCADE CONSTRAINTS;

CREATE TABLE L4_anomaly_data_set
(
	anomaly_data_set_id     NUMBER(38) NOT NULL,
	anomaly_type_id         NUMBER(38) NOT NULL,
	total_patients          NUMBER(38),
	total_genetic_elements  NUMBER(38)
);

CREATE TABLE L4_anomaly_data_set_version
(
	anomaly_data_set_id      NUMBER(38) NOT NULL,
	anomaly_data_version_id  NUMBER(38) NOT NULL
);

CREATE TABLE L4_anomaly_data_version
(
	anomaly_data_version_id  NUMBER(38) NOT NULL,
	disease_id               NUMBER(38) NOT NULL,
	version                  NUMBER(38) NOT NULL,
	is_active                NUMBER(1) DEFAULT 0 NOT NULL,
	loaded_date              DATE
);

CREATE TABLE L4_anomaly_type
(
	anomaly_type_id  NUMBER(38) NOT NULL,
	anomaly_name     VARCHAR2(50) NOT NULL,
	platform_id      NUMBER(38) NOT NULL,
	center_id        NUMBER(38) NOT NULL,
	data_type_id     NUMBER(38) NOT NULL
);


CREATE TABLE L4_anomaly_value
(
	anomaly_value_id     NUMBER(38) NOT NULL,
	anomaly_value        FLOAT NOT NULL,
	anomaly_data_set_id  NUMBER(38) NOT NULL,
	genetic_element_id   NUMBER(38) NOT NULL,
	sample_id            NUMBER(38) NOT NULL
);


CREATE TABLE L4_data_set_sample
(
	anomaly_data_set_id  NUMBER(38) NOT NULL,
	sample_id            NUMBER(38) NOT NULL,
	is_paired            NUMBER(1) NOT NULL
);


CREATE TABLE L4_genetic_element
(
	genetic_element_id       NUMBER(38) NOT NULL,
	genetic_element_name     VARCHAR2(100) NOT NULL,
	genetic_element_type_id  NUMBER(38) NOT NULL,
	start_pos                NUMBER(38) NOT NULL,
	stop_pos                 NUMBER(38) NOT NULL,
	chromosome               VARCHAR2(2) NOT NULL,
	in_cnv_region            NUMBER(1) DEFAULT 0,
	gene_id                  NUMBER(38)
);


CREATE TABLE L4_genetic_element_type
(
	genetic_element_type_id  NUMBER(38) NOT NULL,
	genetic_element_type     VARCHAR2(50) NOT NULL
);


CREATE TABLE L4_sample
(
	sample_id    NUMBER(38) NOT NULL,
	barcode      VARCHAR2(100) NOT NULL,
	patient      VARCHAR2(50) NOT NULL,
	disease_id   NUMBER(38) NOT NULL,
	sample_type  VARCHAR2(50)
);

CREATE TABLE L4_correlation_type
(
	correlation_type_id  NUMBER(38) NOT NULL,
	correlation_name     VARCHAR2(100) NOT NULL,
	anomaly_type_id_1    NUMBER(38) NOT NULL,
	anomaly_type_id_2    NUMBER(38) NOT NULL
);

CREATE TABLE L4_data_set_genetic_element
(
	anomaly_data_set_id  NUMBER(38) NOT NULL,
	genetic_element_id   NUMBER(38) NOT NULL
);

ALTER TABLE L4_anomaly_data_set ADD CONSTRAINT PK_L4_anomaly_data_set 
	PRIMARY KEY (anomaly_data_set_id);

ALTER TABLE L4_anomaly_data_set_version ADD CONSTRAINT PK_L4_anomaly_data_set_version 
	PRIMARY KEY (anomaly_data_set_id, anomaly_data_version_id);

ALTER TABLE L4_anomaly_data_version ADD CONSTRAINT PK_L4_anomaly_data_version 
	PRIMARY KEY (anomaly_data_version_id);

ALTER TABLE L4_anomaly_type ADD CONSTRAINT PK_L4_anomaly_type 
	PRIMARY KEY (anomaly_type_id);

ALTER TABLE L4_anomaly_value ADD CONSTRAINT PK_L4_anomaly_value 
	PRIMARY KEY (anomaly_value_id);

ALTER TABLE L4_data_set_sample ADD CONSTRAINT PK_L4_data_set_sample 
	PRIMARY KEY (anomaly_data_set_id, sample_id);

ALTER TABLE L4_genetic_element ADD CONSTRAINT PK_L4_genetic_element 
	PRIMARY KEY (genetic_element_id);

ALTER TABLE L4_genetic_element_type ADD CONSTRAINT PK_L4_genetic_element_type 
	PRIMARY KEY (genetic_element_type_id);

ALTER TABLE L4_sample ADD CONSTRAINT PK_L4_sample 
	PRIMARY KEY (sample_id);

ALTER TABLE L4_correlation_type ADD CONSTRAINT PK_L4_correlation_type
	PRIMARY KEY (correlation_type_id);

ALTER TABLE L4_data_set_genetic_element ADD CONSTRAINT PK_L4_data_set_genetic_element
	PRIMARY KEY (anomaly_data_set_id, genetic_element_id);

ALTER TABLE L4_genetic_element_type
	ADD CONSTRAINT UQ_genetic_element_type UNIQUE (genetic_element_type);

ALTER TABLE L4_sample
	ADD CONSTRAINT UQ_sample_barcode UNIQUE (barcode);


ALTER TABLE L4_anomaly_data_set ADD CONSTRAINT FK_anomaly_data_s_anomaly_type 
	FOREIGN KEY (anomaly_type_id) REFERENCES L4_anomaly_type (anomaly_type_id);

ALTER TABLE L4_anomaly_data_set_version ADD CONSTRAINT FK_anomaly_data_set 
	FOREIGN KEY (anomaly_data_set_id) REFERENCES L4_anomaly_data_set (anomaly_data_set_id);

ALTER TABLE L4_anomaly_data_set_version ADD CONSTRAINT FK_anomaly_data_version 
	FOREIGN KEY (anomaly_data_version_id) REFERENCES L4_anomaly_data_version (anomaly_data_version_id);

ALTER TABLE L4_anomaly_data_version ADD CONSTRAINT FK_anomaly_data_versio_disease 
	FOREIGN KEY (disease_id) REFERENCES disease (disease_id);

ALTER TABLE L4_anomaly_type ADD CONSTRAINT FK_anomaly_type_center 
	FOREIGN KEY (center_id) REFERENCES center (center_id);

ALTER TABLE L4_anomaly_type ADD CONSTRAINT FK_anomaly_type_data_type 
	FOREIGN KEY (data_type_id) REFERENCES data_type (data_type_id);

ALTER TABLE L4_anomaly_type ADD CONSTRAINT FK_anomaly_type_platform_type 
	FOREIGN KEY (platform_id) REFERENCES platform (platform_id);

ALTER TABLE L4_anomaly_value ADD CONSTRAINT FK_anomaly_value_data_set 
	FOREIGN KEY (anomaly_data_set_id) REFERENCES L4_anomaly_data_set (anomaly_data_set_id);

ALTER TABLE L4_anomaly_value ADD CONSTRAINT FK_L4_anomaly_val_genetic_el 
	FOREIGN KEY (genetic_element_id) REFERENCES L4_genetic_element (genetic_element_id);

ALTER TABLE L4_anomaly_value ADD CONSTRAINT FK_anomaly_value_sample 
	FOREIGN KEY (sample_id) REFERENCES L4_sample (sample_id);

ALTER TABLE L4_data_set_sample ADD CONSTRAINT FK_data_set_sample_anomaly_ds 
	FOREIGN KEY (anomaly_data_set_id) REFERENCES L4_anomaly_data_set (anomaly_data_set_id);

ALTER TABLE L4_data_set_sample ADD CONSTRAINT FK_L4_data_set_sampl_L4_sample 
	FOREIGN KEY (sample_id) REFERENCES L4_sample (sample_id);

ALTER TABLE L4_genetic_element ADD CONSTRAINT FK_genetic_element_gene 
	FOREIGN KEY (gene_id) REFERENCES gene (gene_id);

ALTER TABLE L4_genetic_element ADD CONSTRAINT FK_genetic_element_type 
	FOREIGN KEY (genetic_element_type_id) REFERENCES L4_genetic_element_type (genetic_element_type_id);

ALTER TABLE L4_sample ADD CONSTRAINT FK_sample_disease 
	FOREIGN KEY (disease_id) REFERENCES disease (disease_id);

ALTER TABLE L4_correlation_type ADD CONSTRAINT FK_correlation_anomaly_type1
	FOREIGN KEY (anomaly_type_id_1) REFERENCES L4_anomaly_type (anomaly_type_id);

ALTER TABLE L4_correlation_type ADD CONSTRAINT FK_correlation_anomaly_type2
	FOREIGN KEY (anomaly_type_id_2) REFERENCES L4_anomaly_type (anomaly_type_id);

ALTER TABLE L4_data_set_genetic_element ADD CONSTRAINT FK_data_set_ge_ds
	FOREIGN KEY (anomaly_data_set_id) REFERENCES L4_anomaly_data_set (anomaly_data_set_id);

ALTER TABLE L4_data_set_genetic_element ADD CONSTRAINT FK_data_set_ge_ge
	FOREIGN KEY (genetic_element_id) REFERENCES L4_genetic_element (genetic_element_id);

CREATE INDEX L4_sample_patient_idx on L4_sample(patient);
CREATE INDEX L4_anomaly_val_sample_idx on L4_anomaly_value(sample_id);
CREATE INDEX L4_anomaly_val_ge_idx on L4_anomaly_value(genetic_element_id);
CREATE INDEX L4_anomaly_val_ds_idx on L4_anomaly_value(anomaly_data_set_id);
CREATE INDEX L4_ds_sample_idx on L4_data_set_sample(sample_id);
CREATE INDEx L4_ds_sample_ds_idx on L4_data_set_sample(anomaly_data_set_id);
CREATE INDEX L4_ds_genetic_element_idx on L4_data_set_genetic_element(genetic_element_id);
CREATE INDEX L4_ds_genetic_element_ds_idx on L4_data_set_genetic_element(anomaly_data_set_id);
CREATE INDEX L4_genetic_element_name on L4_genetic_element(genetic_element_name);

ALTER TABLE L4_anomaly_value
	ADD CONSTRAINT UQ_anomaly_val UNIQUE (anomaly_data_set_id, genetic_element_id, sample_id);


--sequences and triggers

DROP SEQUENCE L4_anomaly_data_version_id_SEQ;
DROP SEQUENCE L4_anomaly_data_set_id_SEQ;
DROP SEQUENCE L4_anomaly_value_id_SEQ;
DROP SEQUENCE L4_genetic_element_id_SEQ;
DROP SEQUENCE L4_sample_id_SEQ;

CREATE SEQUENCE L4_anomaly_data_version_id_SEQ
INCREMENT BY 1
START WITH 1
NOMAXVALUE
MINVALUE 1
NOCYCLE
NOCACHE
NOORDER;

CREATE OR REPLACE TRIGGER SET_L4_anomaly_data_version_id
BEFORE INSERT
ON L4_anomaly_data_version
FOR EACH ROW
BEGIN
  SELECT L4_anomaly_data_version_id_SEQ.NEXTVAL
  INTO :NEW.anomaly_data_version_id
  FROM DUAL;
END;
/

CREATE SEQUENCE L4_anomaly_data_set_id_SEQ
INCREMENT BY 1
START WITH 1
NOMAXVALUE
MINVALUE 1
NOCYCLE
NOCACHE
NOORDER;

CREATE OR REPLACE TRIGGER SET_L4_anomaly_data_set_id
BEFORE INSERT
ON L4_anomaly_data_set
FOR EACH ROW
BEGIN
  SELECT L4_anomaly_data_set_id_SEQ.NEXTVAL
  INTO :NEW.anomaly_data_set_id
  FROM DUAL;
END;
/

CREATE SEQUENCE L4_anomaly_value_id_SEQ
INCREMENT BY 1
START WITH 1
NOMAXVALUE
MINVALUE 1
NOCYCLE
NOCACHE
NOORDER;

CREATE OR REPLACE TRIGGER SET_L4_anomaly_value_id
BEFORE INSERT
ON L4_anomaly_value
FOR EACH ROW
BEGIN
  SELECT L4_anomaly_value_id_SEQ.NEXTVAL
  INTO :NEW.anomaly_value_id
  FROM DUAL;
END;
/


CREATE SEQUENCE L4_genetic_element_id_SEQ
INCREMENT BY 1
START WITH 1
NOMAXVALUE
MINVALUE 1
NOCYCLE
NOCACHE
NOORDER;

CREATE OR REPLACE TRIGGER SET_L4_genetic_element_id
BEFORE INSERT
ON L4_genetic_element
FOR EACH ROW
BEGIN
  SELECT L4_genetic_element_id_SEQ.NEXTVAL
  INTO :NEW.genetic_element_id
  FROM DUAL;
END;
/

CREATE SEQUENCE L4_sample_id_SEQ
INCREMENT BY 1
START WITH 1
NOMAXVALUE
MINVALUE 1
NOCYCLE
NOCACHE
NOORDER;

CREATE OR REPLACE TRIGGER SET_L4_sample_id
BEFORE INSERT
ON L4_sample
FOR EACH ROW
BEGIN
  SELECT L4_sample_id_SEQ.NEXTVAL
  INTO :NEW.sample_id
  FROM DUAL;
END;
/

-- initial data
INSERT INTO L4_genetic_element_type(genetic_element_type_id, genetic_element_type) VALUES(1, 'gene');
-- Copy Number: HMS
INSERT INTO L4_anomaly_type(anomaly_type_id, anomaly_name, platform_id, center_id, data_type_id) VALUES(1, 'HMS HG-CGH-244A', 5, 3, 4);
-- Copy Number: MSKCC
INSERT INTO L4_anomaly_type(anomaly_type_id, anomaly_name, platform_id, center_id, data_type_id) VALUES(2, 'MSKCC HG-CGH-244A', 5, 5, 4);
insert into L4_anomaly_type(anomaly_type_id, anomaly_name, platform_id, center_id, data_type_id) values(11, 'BI HT_HG-U133A', 4, 1, 3);
insert into L4_anomaly_type(anomaly_type_id, anomaly_name, platform_id, center_id, data_type_id) values(12, 'UNC AgilentG4502A_07', 13, 7, 3);
insert into L4_anomaly_type(ANOMALY_TYPE_ID, ANOMALY_NAME, PLATFORM_ID, CENTER_ID, DATA_TYPE_ID) values(13, 'BI Genome_Wide_SNP_6', 1, 1, 4);
insert into L4_anomaly_type(ANOMALY_TYPE_ID, ANOMALY_NAME, PLATFORM_ID, CENTER_ID, DATA_TYPE_ID) values(14, 'HAIB HumanHap550', 7, 6, 4);
-- combined mutations
insert into L4_anomaly_type(ANOMALY_TYPE_ID, ANOMALY_NAME, PLATFORM_ID, CENTER_ID, DATA_TYPE_ID) values(15, 'Missense', 12, 10, 7);
insert into L4_anomaly_type(ANOMALY_TYPE_ID, ANOMALY_NAME, PLATFORM_ID, CENTER_ID, DATA_TYPE_ID) values(16, 'Nonsense', 12, 10, 7);
insert into L4_anomaly_type(ANOMALY_TYPE_ID, ANOMALY_NAME, PLATFORM_ID, CENTER_ID, DATA_TYPE_ID) values(17, 'Silent', 12, 10, 7);
insert into L4_anomaly_type(ANOMALY_TYPE_ID, ANOMALY_NAME, PLATFORM_ID, CENTER_ID, DATA_TYPE_ID) values(18, 'SpliceSite', 12, 10, 7);
insert into L4_anomaly_type(ANOMALY_TYPE_ID, ANOMALY_NAME, PLATFORM_ID, CENTER_ID, DATA_TYPE_ID) values(19, 'Frameshift Indel', 12, 10, 7);
insert into L4_anomaly_type(ANOMALY_TYPE_ID, ANOMALY_NAME, PLATFORM_ID, CENTER_ID, DATA_TYPE_ID) values(20, 'In-Frame Indel', 12, 10, 7);
-- correlations
insert into L4_correlation_type(correlation_type_id, correlation_name, anomaly_type_id_1, anomaly_type_id_2) values(1, 'HMS Copy Number vs BI Gene Expression', 1, 11);
insert into L4_correlation_type(correlation_type_id, correlation_name, anomaly_type_id_1, anomaly_type_id_2) values(2, 'HMS Copy Number vs UNC Gene Expression', 1, 12);
insert into L4_correlation_type(correlation_type_id, correlation_name, anomaly_type_id_1, anomaly_type_id_2) values(3, 'MSKCC Copy Number vs BI Gene Expression', 2, 11);
insert into L4_correlation_type(correlation_type_id, correlation_name, anomaly_type_id_1, anomaly_type_id_2) values(4, 'MSKCC Copy Number vs UNC Gene Expression', 2, 12);


