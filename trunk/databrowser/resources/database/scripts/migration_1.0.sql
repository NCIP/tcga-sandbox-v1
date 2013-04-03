CREATE TABLE L4_patient
(
	patient_id    NUMBER(38) NOT NULL,
	patient      VARCHAR2(50) NOT NULL
);
ALTER TABLE L4_patient ADD CONSTRAINT PK_L4_patient 
	PRIMARY KEY (patient_id);

CREATE SEQUENCE L4_patient_id_SEQ
INCREMENT BY 1
START WITH 1
NOMAXVALUE
MINVALUE 1
NOCYCLE
NOCACHE
NOORDER;

CREATE OR REPLACE TRIGGER SET_L4_patient_id
BEFORE INSERT
ON L4_patient
FOR EACH ROW
BEGIN
  SELECT L4_patient_id_SEQ.NEXTVAL
  INTO :NEW.patient_id
  FROM DUAL;
END;
/

CREATE INDEX L4_patient_idx on L4_patient(patient);
insert into L4_patient(patient) select distinct patient from L4_sample where patient_id is null;

alter table L4_sample add patient_id NUMBER(38);
ALTER TABLE L4_sample ADD CONSTRAINT FK_patient_id_sample
	FOREIGN KEY (patient_id) REFERENCES L4_patient (patient_id);
update L4_sample set (patient_id)=(select patient_id from L4_patient where L4_patient.patient = L4_sample.patient);

create index L4_sample_comp_idx on L4_sample(patient_id, sample_id);