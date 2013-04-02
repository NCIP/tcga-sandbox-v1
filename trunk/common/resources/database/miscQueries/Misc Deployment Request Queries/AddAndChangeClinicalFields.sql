ALTER TABLE sample ADD  (
DayOfCollection      NUMBER(2),
MonthOfCollection    NUMBER(2),
YearOfCollection     NUMBER(4));

ALTER TABLE drug_intgen ADD (
 BcrDrugBarcode  VARCHAR2(50));

ALTER TABLE drug_intgen 
RENAME COLUMN STARTDAY TO DAYOFDRUGTREATMENTSTART;
ALTER TABLE drug_intgen 
RENAME COLUMN ENDDAY TO DAYOFDRUGTREATMENTEND;
ALTER TABLE drug_intgen 
RENAME COLUMN STARTMONTH TO MONTHOFDRUGTREATMENTSTART;
ALTER TABLE drug_intgen 
RENAME COLUMN ENDMONTH TO MONTHOFDRUGTREATMENTEND;
ALTER TABLE drug_intgen  
RENAME COLUMN ENDYEAR TO YEAROFDRUGTREATMENTEND;
ALTER TABLE drug_intgen 
RENAME COLUMN STARTYEAR TO YEAROFDRUGTREATMENTSTART;

UPDATE clinical_file_element
SET table_column_name = 'DAYOFDRUGTREATMENTSTART'
WHERE table_column_name = 'STARTDAY'
AND   table_id = (SELECT table_id 
                  FROM clinical_table
                  WHERE table_name = 'DRUG_INTGEN');
UPDATE clinical_file_element
SET table_column_name = 'DAYOFDRUGTREATMENTEND'
WHERE table_column_name = 'ENDDAY'
AND   table_id = (SELECT table_id 
                  FROM clinical_table
                  WHERE table_name = 'DRUG_INTGEN');
UPDATE clinical_file_element
SET table_column_name = 'MONTHOFDRUGTREATMENTSTART'
WHERE table_column_name = 'STARTMONTH'
AND   table_id = (SELECT table_id 
                  FROM clinical_table
                  WHERE table_name = 'DRUG_INTGEN');
UPDATE clinical_file_element
SET table_column_name = 'MONTHOFDRUGTREATMENTEND'
WHERE table_column_name = 'ENDMONTH'
AND   table_id = (SELECT table_id 
                  FROM clinical_table
                  WHERE table_name = 'DRUG_INTGEN');
UPDATE clinical_file_element
SET table_column_name = 'YEAROFDRUGTREATMENTEND'
WHERE table_column_name = 'ENDYEAR'
AND   table_id = (SELECT table_id 
                  FROM clinical_table
                  WHERE table_name = 'DRUG_INTGEN');
UPDATE clinical_file_element
SET table_column_name = 'YEAROFDRUGTREATMENTSTART'
WHERE table_column_name = 'STARTYEAR'
AND   table_id = (SELECT table_id 
                  FROM clinical_table
                  WHERE table_name = 'DRUG_INTGEN');


ALTER TABLE examination ADD  (
  BcrExaminationBarcode VARCHAR2(50));

ALTER TABLE patient 
RENAME COLUMN INITPATHOLOGICDIAGNOSISMONTH TO MONTHOFINITPATHOLOGICDIAGNOSIS;
ALTER TABLE patient 
RENAME COLUMN INITIALPATHOLOGICDIAGNOSISYEAR TO YEAROFINITPATHOLOGICDIAGNOSIS;
ALTER TABLE patient 
RENAME COLUMN INITIALPATHOLOGICDIAGNOSISDAY TO DAYOFINITPATHOLOGICDIAGNOSIS;

ALTER TABLE portion ADD  (
  DAYOFCREATION  NUMBER(2),
  MONTHOFCREATION NUMBER(2), 
  YEAROFCREATION  NUMBER(4));
  
ALTER TABLE radiation 
RENAME COLUMN startDayOfRadiation TO DAYOFRADIATIONTREATMENTSTART;
ALTER TABLE radiation 
RENAME COLUMN startMonthOfRadiation TO MONTHOFRADIATIONTREATMENTSTART;
ALTER TABLE radiation 
RENAME COLUMN startYearOfRadiation TO YEAROFRADIATIONTREATMENTSTART;
ALTER TABLE radiation 
RENAME COLUMN endDayOfRadiation TO DAYOFRADIATIONTREATMENTEND;
ALTER TABLE radiation 
RENAME COLUMN endMonthOfRadiation TO MONTHOFRADIATIONTREATMENTEND;
ALTER TABLE radiation 
RENAME COLUMN endYearOfRadiation TO YEAROFRADIATIONTREATMENTEND;

ALTER TABLE radiation ADD  (
  BcrRadiationBarcode VARCHAR2(50));

ALTER TABLE surgery ADD  (
  BcrSurgeryBarcode VARCHAR2(50));
  
UPDATE clinical_file_element
SET table_column_name = 'MONTHOFINITPATHOLOGICDIAGNOSIS'
WHERE table_column_name = 'INITPATHOLOGICDIAGNOSISMONTH'
AND   table_id = (SELECT table_id 
                  FROM clinical_table
                  WHERE table_name = 'PATIENT');
UPDATE clinical_file_element
SET table_column_name = 'YEAROFINITPATHOLOGICDIAGNOSIS'
WHERE table_column_name = 'INITIALPATHOLOGICDIAGNOSISYEAR'
AND   table_id = (SELECT table_id 
                  FROM clinical_table
                  WHERE table_name = 'PATIENT');
UPDATE clinical_file_element
SET table_column_name = 'DAYOFINITPATHOLOGICDIAGNOSIS'
WHERE table_column_name = 'INITIALPATHOLOGICDIAGNOSISDAY'
AND   table_id = (SELECT table_id 
                  FROM clinical_table
                  WHERE table_name = 'PATIENT');


UPDATE clinical_file_element
SET table_column_name = 'DAYOFRADIATIONTREATMENTSTART'
WHERE table_column_name = 'STARTDAYOFRADIATION'
AND   table_id = (SELECT table_id 
                  FROM clinical_table
                  WHERE table_name = 'RADIATION');
UPDATE clinical_file_element
SET table_column_name = 'MONTHOFRADIATIONTREATMENTSTART'
WHERE table_column_name = 'STARTMONTHOFRADIATION'
AND   table_id = (SELECT table_id 
                  FROM clinical_table
                  WHERE table_name = 'RADIATION');
UPDATE clinical_file_element
SET table_column_name = 'YEAROFRADIATIONTREATMENTSTART'
WHERE table_column_name = 'STARTYEAROFRADIATION'
AND   table_id = (SELECT table_id 
                  FROM clinical_table
                  WHERE table_name = 'RADIATION');
UPDATE clinical_file_element
SET table_column_name = 'DAYOFRADIATIONTREATMENTEND'
WHERE table_column_name = 'ENDDAYOFRADIATION'
AND   table_id = (SELECT table_id 
                  FROM clinical_table
                  WHERE table_name = 'RADIATION');
UPDATE clinical_file_element
SET table_column_name = 'MONTHOFRADIATIONTREATMENTEND'
WHERE table_column_name = 'ENDMONTHOFRADIATION'
AND   table_id = (SELECT table_id 
                  FROM clinical_table
                  WHERE table_name = 'RADIATION');
UPDATE clinical_file_element
SET table_column_name = 'YEAROFRADIATIONTREATMENTEND'
WHERE table_column_name = 'ENDYEAROFRADIATION'
AND   table_id = (SELECT table_id 
                  FROM clinical_table
                  WHERE table_name = 'RADIATION');