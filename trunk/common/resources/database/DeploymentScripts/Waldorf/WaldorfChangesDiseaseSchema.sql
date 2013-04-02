ALTER TABLE clinical_table add (uuid_element_id number(38,0));

ALTER TABLE clinical_table add (
CONSTRAINT fk_clin_table_uuid_xsd_elem
FOREIGN KEY (uuid_element_id)
REFERENCES clinical_xsd_element(clinical_xsd_element_id));

INSERT INTO CLINICAL_XSD_ELEMENT(CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
(SELECT MAX(CLINICAL_XSD_ELEMENT_ID)+1, 'bcr_patient_uuid', 0, '', 'string' from clinical_xsd_element);
INSERT INTO CLINICAL_XSD_ELEMENT(CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
(SELECT MAX(CLINICAL_XSD_ELEMENT_ID)+1, 'bcr_sample_uuid', 0, '', 'string' from clinical_xsd_element);
INSERT INTO CLINICAL_XSD_ELEMENT(CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
(SELECT MAX(CLINICAL_XSD_ELEMENT_ID)+1, 'bcr_portion_uuid', 0, '', 'string' from clinical_xsd_element);
INSERT INTO CLINICAL_XSD_ELEMENT(CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
(SELECT MAX(CLINICAL_XSD_ELEMENT_ID)+1, 'bcr_analyte_uuid', 0, '', 'string' from clinical_xsd_element);
INSERT INTO CLINICAL_XSD_ELEMENT(CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
(SELECT MAX(CLINICAL_XSD_ELEMENT_ID)+1, 'bcr_aliquot_uuid', 0, '', 'string' from clinical_xsd_element);
INSERT INTO CLINICAL_XSD_ELEMENT(CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
(SELECT MAX(CLINICAL_XSD_ELEMENT_ID)+1, 'bcr_slide_uuid', 0, '', 'string' from clinical_xsd_element);
INSERT INTO CLINICAL_XSD_ELEMENT(CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
(SELECT MAX(CLINICAL_XSD_ELEMENT_ID)+1, 'bcr_drug_uuid', 0, '', 'string' from clinical_xsd_element);
INSERT INTO CLINICAL_XSD_ELEMENT(CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
(SELECT MAX(CLINICAL_XSD_ELEMENT_ID)+1, 'bcr_radiation_uuid', 0, '', 'string' from clinical_xsd_element);
INSERT INTO CLINICAL_XSD_ELEMENT(CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
(SELECT MAX(CLINICAL_XSD_ELEMENT_ID)+1, 'bcr_surgery_uuid', 0, '', 'string' from clinical_xsd_element);
INSERT INTO CLINICAL_XSD_ELEMENT(CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
(SELECT MAX(CLINICAL_XSD_ELEMENT_ID)+1, 'bcr_examination_uuid', 0, '', 'string' from clinical_xsd_element);

UPDATE CLINICAL_TABLE SET uuid_element_id=(select clinical_xsd_element_id from clinical_xsd_element where element_name='bcr_patient_uuid')
WHERE ELEMENT_NODE_NAME='patient';
UPDATE CLINICAL_TABLE SET uuid_element_id=(select clinical_xsd_element_id from clinical_xsd_element where element_name='bcr_sample_uuid')
WHERE ELEMENT_NODE_NAME='sample';
UPDATE CLINICAL_TABLE SET uuid_element_id=(select clinical_xsd_element_id from clinical_xsd_element where element_name='bcr_portion_uuid')
WHERE ELEMENT_NODE_NAME='portion';
UPDATE CLINICAL_TABLE SET uuid_element_id=(select clinical_xsd_element_id from clinical_xsd_element where element_name='bcr_analyte_uuid')
WHERE ELEMENT_NODE_NAME='analyte';
UPDATE CLINICAL_TABLE SET uuid_element_id=(select clinical_xsd_element_id from clinical_xsd_element where element_name='bcr_aliquot_uuid')
WHERE ELEMENT_NODE_NAME='aliquot';
UPDATE CLINICAL_TABLE SET uuid_element_id=(select clinical_xsd_element_id from clinical_xsd_element where element_name='bcr_slide_uuid')
WHERE ELEMENT_NODE_NAME='slide';
UPDATE CLINICAL_TABLE SET uuid_element_id=(select clinical_xsd_element_id from clinical_xsd_element where element_name='bcr_drug_uuid')
WHERE ELEMENT_NODE_NAME='drug';
UPDATE CLINICAL_TABLE SET uuid_element_id=(select clinical_xsd_element_id from clinical_xsd_element where element_name='bcr_radiation_uuid')
WHERE ELEMENT_NODE_NAME='radiation';
UPDATE CLINICAL_TABLE SET uuid_element_id=(select clinical_xsd_element_id from clinical_xsd_element where element_name='bcr_surgery_uuid')
WHERE ELEMENT_NODE_NAME='surgery';
UPDATE CLINICAL_TABLE SET uuid_element_id=(select clinical_xsd_element_id from clinical_xsd_element where element_name='bcr_examination_uuid')
WHERE ELEMENT_NODE_NAME='examination';
commit;
