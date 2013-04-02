ALTER TABLE Sample ADD CONSTRAINT FK_Sample_PatientId
FOREIGN KEY (patient_id) REFERENCES patient(patient_id);

ALTER TABLE Radiation ADD CONSTRAINT FK_Radiation_PatientId
FOREIGN KEY (patient_id) REFERENCES patient(patient_id);

ALTER TABLE Surgery ADD CONSTRAINT FK_Surgery_PatientId
FOREIGN KEY (patient_id) REFERENCES patient(patient_id);

ALTER TABLE Drug_Intgen ADD CONSTRAINT FK_DrugIntgen_PatientId
FOREIGN KEY (patient_id) REFERENCES patient(patient_id);

/*
** fails no parents on some
select distinct i.drug_id,i.drugname , dr.drug_id, dr.name
from drug_intgen i, drug d, drug dr
where i.drug_id = d.drug_id (+)
and i.drugname = dr.name

ALTER TABLE Drug_Intgen ADD CONSTRAINT FK_DrugIntgen_DrugId
FOREIGN KEY (drug_id) REFERENCES drug(drug_id);
*/

ALTER TABLE Ovarian_Pathology ADD CONSTRAINT FK_OvarianPath_TumorPathId
FOREIGN KEY (tumorpathology_id) REFERENCES tumorpathology(tumorpathology_id);

ALTER TABLE Lung_Pathology ADD CONSTRAINT FK_LungPathology_TumorPathId
FOREIGN KEY (tumorpathology_id) REFERENCES tumorpathology(tumorpathology_id);