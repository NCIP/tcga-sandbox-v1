CREATE VIEW DATA_SET_V as
select data_set_id, experiment_id, source_file_name, source_file_type, access_level, data_level 
from data_set where use_in_dam=1 and load_complete=1;
GRANT SELECT ON data_set_v to TCGAMAINT;

create view DNA_V as
select dna_id, analyte_id, case when NORMALTUMORGENOTYPEMATCH = 'YES' then 1 else 0 end as NORMALTUMORGENOTYPEMATCH, 
case when PCRAMPLIFICATIONSUCCESSFUL = 'YES' then 1 else 0 end as PCRAMPLIFICATIONSUCCESSFUL 
from DNA;
GRANT SELECT ON dna_v to TCGAMAINT;

alter table platform_type add platform_type_id number(38);

update platform_type set platform_type_id=rownum;

alter table PLATFORM_TYPE DROP PRIMARY KEY;

alter table PLATFORM_TYPE ADD CONSTRAINT PK_PLATFORM_TYPE_ID PRIMARY KEY (platform_type_id);

alter table platform_type add constraint PLATFORM_TYPE_AK1 UNIQUE(platform_id, data_type_id);

alter table center_disease add center_disease_id number(38);

update center_disease set center_disease_id=rownum;

alter table center_disease drop primary key;

alter table center_disease add constraint PK_CENTER_DISEASE_ID PRIMARY KEY(center_disease_id);

alter table center_disease add constraint CENTER_DISEASE_AK1 UNIQUE(center_id, disease_id);

alter table L4_DATA_SET_GENETIC_ELEMENT add L4_DATA_SET_GENETIC_ELEMENT_ID number(38);

update L4_DATA_SET_GENETIC_ELEMENT set L4_DATA_SET_GENETIC_ELEMENT_ID=rownum;

alter table L4_DATA_SET_GENETIC_ELEMENT drop primary key;

alter table L4_DATA_SET_GENETIC_ELEMENT add constraint PK_L4_DATA_SET_GE_ID PRIMARY KEY(L4_DATA_SET_GENETIC_ELEMENT_ID);

alter table L4_DATA_SET_GENETIC_ELEMENT add constraint L4_DATA_SET_GE_AK1 UNIQUE(anomaly_data_set_id, genetic_element_id);

CREATE SEQUENCE L4_DATA_SET_GE_id_SEQ
INCREMENT BY 1
START WITH 1000000
NOMAXVALUE
MINVALUE 1
NOCYCLE
NOCACHE
NOORDER;

GRANT SELECT ON L4_DATA_SET_GE_id_SEQ to TCGAMAINT;

alter table L4_ANOMALY_DATA_SET_VERSION add L4_ANOMALY_DATA_SET_VERSION_ID number(38);

update L4_ANOMALY_DATA_SET_VERSION set L4_ANOMALY_DATA_SET_VERSION_ID=rownum;

alter table L4_ANOMALY_DATA_SET_VERSION drop primary key;

alter table L4_ANOMALY_DATA_SET_VERSION add constraint PK_L4_ANOMALY_DSV_ID PRIMARY KEY(L4_ANOMALY_DATA_SET_VERSION_ID);

alter table L4_ANOMALY_DATA_SET_VERSION add constraint L4_ANOMALY_DSV_AK1 UNIQUE(anomaly_data_set_id, anomaly_data_version_id);

alter table L4_DATA_SET_SAMPLE add L4_DATA_SET_SAMPLE_ID number(38);

update L4_DATA_SET_SAMPLE set L4_DATA_SET_SAMPLE_ID=rownum;

alter table L4_DATA_SET_SAMPLE drop primary key;

alter table L4_DATA_SET_SAMPLE add constraint PK_L4_DATA_SET_SAMPLE_ID PRIMARY KEY(L4_DATA_SET_SAMPLE_ID);

alter table L4_DATA_SET_SAMPLE add constraint L4_DATA_SET_SAMPLE_AK1 UNIQUE(anomaly_data_set_id, sample_id);

CREATE SEQUENCE L4_DATA_SET_SAMPLE_id_SEQ
INCREMENT BY 1
START WITH 10000
NOMAXVALUE
MINVALUE 1
NOCYCLE
NOCACHE
NOORDER;

GRANT SELECT ON L4_DATA_SET_SAMPLE_id_SEQ to TCGAMAINT;

create view disease_v as select disease_id, abbreviation, name from disease where active=1;

GRANT SELECT ON disease_v to tcgamaint;

alter table L4_PATIENT add clinical_patient_id number(38);

update L4_PATIENT set clinical_patient_id=(select patient_id from patient where patient.bcrpatientbarcode=L4_PATIENT.patient);

alter table l4_sample add aliquot_id number(38);

update l4_sample set aliquot_id=(select aliquot_id from aliquot where aliquot.bcraliquotbarcode=l4_sample.barcode);

alter table hybridization_ref add aliquot_id number(38);

update hybridization_ref set aliquot_id=(select aliquot_id from aliquot where aliquot.bcraliquotbarcode=hybridization_ref.bestbarcode);
