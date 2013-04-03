alter table l4_anomaly_type add genetic_element_type_id number(38) default 1 not null;

alter table l4_anomaly_type add constraint FK_anomaly_type_ge_type FOREIGN KEY (genetic_element_type_id) REFERENCES L4_genetic_element_type(genetic_element_type_id);

insert into L4_genetic_element_type(GENETIC_ELEMENT_TYPE_ID, GENETIC_ELEMENT_TYPE) values(2, 'methylation target');

insert into L4_genetic_element_type(GENETIC_ELEMENT_TYPE_ID, GENETIC_ELEMENT_TYPE) values(3, 'miRNA');

CREATE TABLE L4_TARGET
(
   target_id NUMBER(38) NOT NULL,
   source_genetic_element_id NUMBER(38) NOT NULL,
   target_genetic_element_id NUMBER(38) NOT NULL
);

ALTER TABLE L4_TARGET ADD CONSTRAINT PK_L4_TARGET
	PRIMARY KEY (target_id);

ALTER TABLE L4_TARGET ADD CONSTRAINT FK_TARGET_SOURCE_FK
	FOREIGN KEY (source_genetic_element_id) REFERENCES L4_GENETIC_ELEMENT (GENETIC_ELEMENT_ID);

ALTER TABLE L4_TARGET ADD CONSTRAINT FK_TARGET_TARGET_FK
	FOREIGN KEY (target_genetic_element_id) REFERENCES L4_GENETIC_ELEMENT (GENETIC_ELEMENT_ID);

ALTER TABLE L4_TARGET ADD CONSTRAINT TARGET_GE_IDS_AK1 UNIQUE (source_genetic_element_id, target_genetic_element_id);

CREATE SEQUENCE L4_TARGET_SEQ
INCREMENT BY 1
START WITH 1
NOMAXVALUE
MINVALUE 1
NOCYCLE
NOCACHE
NOORDER;

CREATE SEQUENCE L4_ANOMALY_TYPE_SEQ
INCREMENT BY 1
START WITH 29
NOMAXVALUE
MINVALUE 1
NOCYCLE
NOCACHE
NOORDER;