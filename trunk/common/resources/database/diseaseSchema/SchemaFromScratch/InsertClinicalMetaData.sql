Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (179, 'bcr_drug_barcode', 0, 'unique identifier for a subject drug regimen', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (183, 'days_to_birth', 0, 'days elapsed from date of initial diagnosis to birth', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (184, 'days_to_death', 0, 'days elapsed from date of initial diagnosis to death', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (185, 'days_to_last_followup', 0, 'days elapsed from date of initial diagnosis to last follow-up', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (186, 'days_to_tumor_progression', 0, 'days elapsed from date of initial diagnosis to tumor progression', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (187, 'days_to_tumor_recurrence', 0, 'days elapsed from date of initial diagnosis to tumor recurrence', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (188, 'age_at_initial_pathologic_diagnosis', 0, 'age at initial pathologic diagnosis in years', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (189, 'days_to_collection', 0, 'days elapsed from date of initial diagnosis to collection of sample', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (190, 'days_to_drug_treatment_start', 0, 'days elapsed from date of initial diagnosis to start of drug treatment', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (191, 'days_to_drug_treatment_end', 0, 'days elapsed from date of initial diagnosis to end of drug treatment', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (192, 'days_to_radiation_treatment_start', 0, 'days elapsed from date of initial diagnosis to start of radiation treatment', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (193, 'days_to_radiation_treatment_end', 0, 'days elapsed from date of initial diagnosis to end of radiation treatment', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (194, 'days_to_procedure', 0, 'days elapsed from date of initial diagnosis to surgical procedure', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (107, 'percent_stromal_cells', 0, 'the percentage of reactive cells that are present in a malignant tumor sample or specimen but are not malignant such as fibroblasts, vascular structures, etc', 'decimal');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (109, 'percent_tumor_nuclei', 0, 'the percentage of tumor nuclei in a malignant neoplasm sample', 'decimal');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (112, 'pretreatment_history', 0, 'indicates whether any treatment was given to the patient prior to surgery', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (136, 'sample_type', 0, 'type of tissue collected', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (141, 'time_between_clamping_and_freezing', 0, 'time, in minutes, between vascular clamping and tissue freezing', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (142, 'time_between_excision_and_freezing', 0, 'time, in minutes, between surgical excision and tissue freezing ', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (157, 'tumor_tissue_site', 0, 'originating or primary anatomic site of the disease', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (159, 'venous_invasion', 0, 'indicates if large vessel or venous invasion was detected by surgery or presence in a tumor specimen', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (160, 'verification_by_bcr', 0, 'indicates if the pathology determination from the tissue center agrees with that of the BCR', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (4, 'additional_chemo_therapy', 0, 'indicator related to the additional administration of chemotherapy after the return of a disease after a period of remission', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (5, 'additional_drug_therapy', 0, 'indicator related to the additional administration of targeted molecular therapy after the return of a disease after a period of remission', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (7, 'additional_immuno_therapy', 0, 'indicator related to the additional administration of immunotherapy after the return of a disease after a period of remission', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (8, 'additional_radiation_therapy', 0, 'indicator related to the additional administration of radiation therapy after the return of a disease after a period of remission', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (9, 'amount', 0, 'total amount, in micrograms, of molecular analyte purified from the tissue sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (13, 'anatomic_treatment_site', 0, 'describes radiation therapy administration in reference to location of disease', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (15, 'bcr_analyte_barcode', 0, 'unique identifier for an individual analyte purified from tissue', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (17, 'bcr_portion_barcode', 0, 'unique identifier for an individual portion of a tissue sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (22, 'chemo_therapy', 0, 'indicates adjuvant postoperative chemotherapy was administered', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (23, 'concentration', 0, 'concentration in micrograms/microliter of molecular analyte purified from a tissue sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (41, 'dosage_units', 0, 'units used for dosage measurement', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (44, 'drug_name', 0, 'name of drug', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (45, 'eastern_cancer_oncology_group', 0, 'the ECOG functional performance status of the subject', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (51, 'gemistocytes_present', 0, 'whether gemistocytes were found in pathology studies of the sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (53, 'gfap_positive', 0, 'whether glial fibrillary acidic protein test was positive in pathology studies of the sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (58, 'histologic_type', 0, 'the histologic type of the GBM sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (59, 'hormonal_therapy', 0, 'indicates whether adjuvant postoperative hormone therapy was administered', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (61, 'informed_consent_verified', 0, 'signifies whether or not a consent by a patient to permit the acquisition of tissue or bodily fluid has been obtained', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (62, 'initial_course', 0, 'indicates whether this was the initial course of drug treatment', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (63, 'initial_pathologic_diagnosis_method', 0, 'name of the procedure to secure the tissue used for the original pathologic diagnosis', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (68, 'leptomeningeal_involement', 0, 'indicates whether leptomeningeal involvement was found during pathology studies of the sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (75, 'month_of_creation', 0, 'the month in which the portion was taken from the tumor sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (85, 'month_of_shipment', 0, 'the month in which the aliquot was shipped to the sequencing or characterization center for analysis', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (90, 'number_cycles', 0, 'total number of cycles administered to the subject of a protocol specified drug', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (94, 'numfractions', 0, 'total number of radiation therapy sessions (fractions) which the patient completed to receive the required radiation dose to primary treatment fields', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (110, 'performance_status_scale_timing', 0, 'timepoint in reference to a patient''s therapy in which a performance status assessment is conducted', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (114, 'primary_therapy_outcome_success', 0, 'measure of success of outcome after primary treatment (surgery and adjuvant therapies)', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (115, 'prior_glioma', 0, 'indicates if subject had an earlier diagnosis of glioma with a lower histological grade than the current disease grade', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (117, 'progression_determined_by', 0, 'name of the procedure or testing method used to diagnose tumor recurrence', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (118, 'progression_determined_by_notes', 1, 'description of a method of diagnosing recurrent neoplastic disease that is different than the options previously specified', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (123, 'radiation_dosage', 0, 'total dose of radiation therapy administered, expressed as a dosage amount', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (124, 'radiation_therapy', 0, 'indicates adjuvant postoperative radiation was administered', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (126, 'radiation_type', 0, 'classification for the form of radiation therapy administered to a subject', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (127, 'radiation_type_notes', 1, 'text field to describe of a method of radiation treatment administered to a patient', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (129, 'regimen_indication', 0, 'identifies the reason for the administration of a treatment regimen', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (130, 'regimen_indication_notes', 1, 'text to capture the indication for a treatment regimen that is different than the one(s) specified', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (134, 'route_of_administration', 0, 'code or name to represent an access route for the administration of drug agents', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (137, 'section_location', 0, 'indicates if the slide was taken from the top or bottom of the portion', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (139, 'site_of_tumor_first_recurrence', 0, 'description of tumor first recurrence in reference to extent of disease', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (163, 'well_number', 0, 'well number in the gel in which the analyte was run', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (133, 'rinvalue', 0, 'RNA integrity number from Agilent bioanalyzer', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (98, 'pcr_amplification_successful', 0, 'did the PCR reaction amplify the correct bands', 'decimal');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (99, 'percent_eosinophil_infiltration', 0, 'the percentage of infiltration by eosinophils in a tumor sample', 'decimal');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (101, 'percent_inflam_infiltration', 0, 'the percentage of a tumor sample showing a local response to cellular injury, marked by capillary dilatation, edema and leukocyte infiltration', 'decimal');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (89, 'nuclear_pleomorphism', 0, 'occurrence of nuclei with various distinct forms', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (91, 'number_proliferating_cells', 0, 'indicates the presence of reproducing endothelial cells in a malignant neoplasm sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (151, 'tumor_grade', 0, 'numeric value to express the degree of abnormality of cancer cells, a measure of differentiation and aggressiveness', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (152, 'tumor_residual_disease', 0, 'category to represent the size in millimeters of the largest remaining nodule of ovarian carcinoma', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (156, 'tumor_stage', 0, 'the FIGO staging criteria based on the assignment of TNM categories (AJCC 6th Ed) into groups used to select and evaluate therapy, estimate prognosis and calculate end results', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (158, 'units', 0, 'a unit of measurement corresponding to a dose of radiation therapy given to a subject', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (166, 'year_of_creation', 0, 'the year in which the portion was taken from the tumor sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (176, 'year_of_shipment', 0, 'the year in which the aliquot was shipped to the sequencing or characterization center for analysis', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (180, 'bcr_radiation_barcode', 0, 'unique identifier for a subject radiation treatment', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (181, 'bcr_surgery_barcode', 0, 'unique identifier for a subject surgical procedure', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (182, 'bcr_examination_barcode', 0, 'unique identifier for a subject examination', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (3, 'a260_a280_ratio', 0, 'ratio of absorbance at 260nm to absorbance at 280nm of analyte extracted from a sample portion', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (6, 'additional_hormone_therapy', 0, 'indicator related to the additional administration of hormone therapy after the return of a disease after a period of remission', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (11, 'analyte_type', 0, 'type of analyte extracted from sample portion', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (12, 'anatomic_organ_subdivision', 0, 'spatial description to provide the exact location of an anatomic site or location', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (14, 'bcr_aliquot_barcode', 0, 'aliquot of an analyte extracted from a portion of a sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (16, 'bcr_patient_barcode', 0, 'subject identifier', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (18, 'bcr_sample_barcode', 0, 'unique identifier for an individual tissue sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (20, 'bcr_slide_barcode', 0, 'slide taken from a portion of a sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (21, 'cellularity', 0, 'description of the number of cells relative to tumor volume', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (25, 'current_weight', 0, 'the current weight, in grams, of the tissue sample ', 'decimal');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (28, 'day_of_creation', 0, 'the day on which the portion was taken from the tumor sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (38, 'day_of_shipment', 0, 'the day on which the aliquot was shipped to the sequencing or characterization center for analysis', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (42, 'drug_category', 0, 'category of drug', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (43, 'drug_dosage', 0, 'dosage of drug', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (46, 'endothelial_proliferation', 0, 'indicates the presence or absence of reproducing endothelial cells in a malignant neoplasm sample or specimen', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (47, 'ethnicity', 1, NULL, 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (48, 'experimental_protocol_type', 0, 'type of experimental protocol', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (49, 'freezing_method', 0, 'the method by which the tumor sample was frozen', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (50, 'gel_image_file', 0, 'the name of the file containing the gel image of the analyte', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (52, 'gender', 0, 'gender of the subject', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (54, 'histological_type', 0, 'the category for a neoplasm''s histologic subtype', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (55, 'histologic_nuclear_grade', 0, 'the histologic nuclear grade of the GBM sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (60, 'immuno_therapy', 0, 'indicates whether adjuvant postoperative immunotherapy was administered', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (64, 'initial_weight', 0, 'the weight, in grams, of the tissue sample when it was received', 'decimal');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (65, 'intermediate_dimension', 0, 'the second longest dimension, in cm, of the tumor', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (66, 'jewish_origin', 1, NULL, 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (67, 'karnofsky_performance_score', 0, 'score from the Karnofsky Performance status scale', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (69, 'longest_dimension', 0, 'the longest dimension, in cm, of the tumor', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (70, 'lymphatic_invasion', 0, 'indicates if malignant cells were present in small or thin-walled vessels suggesting lymphatic involvement', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (71, 'margins_involved', 0, 'indicates if the margins of the pathology section contain tumor cells', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (72, 'mib1_positive', 0, 'whether the MIB-1 test was positive in pathology studies of the sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (88, 'normal_tumor_genotype_match', 0, 'did the tumor and normal tissue genotypes agree', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (92, 'number_regional_lymphnodes_exam', 0, 'number of regional lymph nodes examined', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (93, 'number_regional_lymphnodes_pos', 0, 'number of regional lymph nodes positive for tumor cells', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (95, 'oct_embedded', 0, 'indicates whether the tumor sample was OCT embedded', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (96, 'oligodendroglial_component', 0, 'indicates whether an oligodendroglial component was found in pathology studies of the sample', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (97, 'palisading_necrosis', 0, 'indicates the presence or absence of small, irregular regions of necrosis surrounded by dense accumulations of tumor cells', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (100, 'percent_granulocyte_infiltration', 0, 'the percentage of infiltration by granulocytes in a tumor sample', 'decimal');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (102, 'percent_lymphocyte_infiltration', 0, 'the percentage of infiltration by lymphocytes in a tumor sample', 'decimal');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (103, 'percent_monocyte_infiltration', 0, 'the percentage of infiltration by monocytes in a tumor sample', 'decimal');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (104, 'percent_necrosis', 0, 'the percentage of cell death in a malignant tumor sample', 'decimal');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (105, 'percent_neutrophil_infiltration', 0, 'the percentage of infiltration by neutrophils in a tumor sample', 'decimal');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (106, 'percent_normal_cells', 0, 'the percentage of normal cell content in a malignant tumor sample', 'decimal');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (108, 'percent_tumor_cells', 0, 'the percentage of malignant cell tumor nuclei content in a tumor sample', 'decimal');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (111, 'person_neoplasm_cancer_status', 0, 'tumor status of the subject', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (113, 'primary_ormetastatic_status', 0, 'indicates whether the tumor sample is primary or metastatic', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (116, 'procedure_type', 0, 'type of surgical procedure', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (119, 'progression_status', 0, 'indicates if the disease has progressed', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (120, 'protocol_file_name', 0, 'the filename for the protocol', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (121, 'protocol_name', 0, 'the name of the protocol', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (122, 'race', 1, NULL, 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (125, 'radiation_treatment_ongoing', 0, 'signifies if a subject is still receiving radiation therapy', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (128, 'ratio_28s_18s', 0, 'ratio of intensity of the 28S and 18S rRNA bands', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (131, 'residual_tumor', 0, 'describes the status of a tissue margin following surgical resection', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (135, 'route_of_administration_notes', 1, 'the specified route of administration of a medication', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (138, 'shortest_dimension', 0, 'the shortest dimension, in cm, of the tumor', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (140, 'targeted_molecular_therapy', 0, 'indicates adjuvant postoperative targeted molecular therapy was administered', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (143, 'tnm_pathology_lymphnode_status', 0, NULL, 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (145, 'tnm_pathology_metastatic_status', 0, NULL, 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (147, 'tnm_pathology_stage_grouping', 0, NULL, 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (149, 'tnm_pathology_tumor_status', 0, NULL, 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (153, 'tumor_sample_anatomic_location', 0, 'the anatomic region in the brain where the glioblastoma was located ', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (161, 'vital_status', 0, 'summary level description of subject survival status', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (162, 'weight', 0, 'the weight of the portion taken from the tissue sample ', 'decimal');  
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (195, 'days_to_initial_pathologic_diagnosis', 0, 'days elapsed from date of initial diagnosis to initial diagnosis (point of reference)', 'decimal');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (197, 'days_to_drug_therapy_start', 0, 'days elapsed from date of initial diagnosis to start of drug treatment', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (198, 'days_to_drug_therapy_end', 0, 'days elapsed from date of initial diagnosis to end of drug treatment', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (199, 'days_to_radiation_therapy_start', 0, 'days elapsed from date of initial diagnosis to start of radiation treatment', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (200, 'days_to_radiation_therapy_end', 0, 'days elapsed from date of initial diagnosis to end of radiation treatment', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (201, 'days_to_form_completion', 0, 'days elapsed from date of initial diagnosis to completion of form', 'integer');
 Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (202, 'course_number', 0, 'radiation course number', 'integer');
 Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (203, 'regimen_number', 0, 'drug regimen number', 'integer');
  Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (204, 'total_dose', 0, 'total drug dose', 'decimal');
    Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (205, 'total_dose_units', 0, 'total drug dose units', 'string');
    Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (206, 'prescribed_dose', 0, 'prescribed drug dose', 'decimal');
 Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (207, 'prescribed_dose_units', 0, 'prescribed drug dose units', 'string');
 Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (208, 'therapy_type', 0, 'therapy types', 'string');
 Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (209, 'therapy_ongoing', 0, 'is drug therapy ongoing', 'string');
 Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (210, 'therapy_type_notes', 0, 'therapy type notes', 'string');
 Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (211, 'prior_diagnosis', 0, 'prior diagnosis', 'string');
 Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (212, 'days_to_last_known_alive', 0, 'days elapsed from date of initial diagnosis to date last known alive', 'string');
 Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (213, 'days_to_new_tumor_event_after_initial_treatment', 0, 'days elapsed from date of initial diagnosis to date of new tumor event after initial treatment', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (214, 'additional_pharmaceutical_therapy', 0, 'indicator related to the additional administration of pharmaceutical therapy after the return of a disease after a period of remission', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (215, 'additional_surgery_locoregional_procedure', 0, 'indicator related to the additional locoregional surgical procedure after the return of a disease after a period of remission', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (216, 'days_to_additional_surgery_locoregional_procedure', 0, 'days elapsed from date of initial diagnosis to date of additional locoregional surgery', 'integer');

Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (217, 'additional_surgery_metastatic_procedure', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (218, 'days_to_additional_surgery_metastatic_procedure', 0, '', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (219, 'papillary_renal_cell_carcinoma_morphology_type', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (220, 'laterality', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (221, 'lymphnodes_examined_prior_presentation', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (222, 'number_of_lymphnodes_examined', 0, '', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (223, 'number_of_lymphnodes_positive', 0, '', 'integer');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (224, 'primary_tumor_pathologic_spread', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (225, 'lymphnode_pathologic_spread', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (226, 'distant_metastasis_distant_spread', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (227, 'platelet_qualitative_result', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (228, 'lactate_dehydrogenase_result', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (229, 'serum_calcium_result', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (230, 'hemoglobin_result', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (231, 'white_cell_count_result', 0, '', 'string');
 Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (232, 'erythrocyte_sedimentation_rate_result', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (233, 'bcr_patient_uuid', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (234, 'bcr_sample_uuid', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (235, 'bcr_portion_uuid', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (236, 'bcr_analyte_uuid', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (237, 'bcr_aliquot_uuid', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (238, 'bcr_slide_uuid', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (239, 'bcr_radiation_uuid', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (240, 'bcr_examination_uuid', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (241, 'bcr_surgery_uuid', 0, '', 'string');
Insert into CLINICAL_XSD_ELEMENT
   (CLINICAL_XSD_ELEMENT_ID, ELEMENT_NAME, IS_PROTECTED, DESCRIPTION, VALUE_TYPE)
 Values
   (242, 'bcr_drug_uuid', 0, '', 'string');
COMMIT;
Insert into CLINICAL_TABLE
   (CLINICAL_TABLE_ID, TABLE_NAME, JOIN_FOR_SAMPLE, JOIN_FOR_PATIENT, BARCODE_COLUMN_NAME, BARCODE_ELEMENT_ID, ELEMENT_NODE_NAME, ELEMENT_TABLE_NAME, PARENT_TABLE_ID, TABLE_ID_COLUMN_NAME, ARCHIVE_LINK_TABLE_NAME, UUID_ELEMENT_ID)
 Values
   (1, 'PATIENT', 'PATIENT.PATIENT_ID=SAMPLE.PATIENT_ID', ' ', 'PATIENT_BARCODE', 16, 'patient', 'PATIENT_ELEMENT', null, 'PATIENT_ID', 'PATIENT_ARCHIVE', 233);
Insert into CLINICAL_TABLE
   (CLINICAL_TABLE_ID, TABLE_NAME, JOIN_FOR_SAMPLE, JOIN_FOR_PATIENT, BARCODE_COLUMN_NAME, BARCODE_ELEMENT_ID, ELEMENT_NODE_NAME, ELEMENT_TABLE_NAME, PARENT_TABLE_ID, TABLE_ID_COLUMN_NAME, ARCHIVE_LINK_TABLE_NAME, UUID_ELEMENT_ID)
 Values
   (2, 'SAMPLE', ' ', 'SAMPLE.PATIENT_ID=PATIENT.PATIENT_ID', 'SAMPLE_BARCODE', 18, 'sample', 'SAMPLE_ELEMENT', 1, 'SAMPLE_ID', 'SAMPLE_ARCHIVE', 234);
Insert into CLINICAL_TABLE
   (CLINICAL_TABLE_ID, TABLE_NAME, JOIN_FOR_SAMPLE, JOIN_FOR_PATIENT, BARCODE_COLUMN_NAME, BARCODE_ELEMENT_ID, ELEMENT_NODE_NAME, ELEMENT_TABLE_NAME, PARENT_TABLE_ID, TABLE_ID_COLUMN_NAME, ARCHIVE_LINK_TABLE_NAME, UUID_ELEMENT_ID)
 Values
   (3, 'PORTION', 'PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID', 'PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID AND SAMPLE.PATIENT_ID=PATIENT.PATIENT_ID', 'PORTION_BARCODE', 17, 'portion', 'PORTION_ELEMENT', 2, 'PORTION_ID', 'PORTION_ARCHIVE', 235);
Insert into CLINICAL_TABLE
   (CLINICAL_TABLE_ID, TABLE_NAME, JOIN_FOR_SAMPLE, JOIN_FOR_PATIENT, BARCODE_COLUMN_NAME, BARCODE_ELEMENT_ID, ELEMENT_NODE_NAME, ELEMENT_TABLE_NAME, PARENT_TABLE_ID, TABLE_ID_COLUMN_NAME, ARCHIVE_LINK_TABLE_NAME, UUID_ELEMENT_ID)
 Values
   (4, 'ANALYTE', 'ANALYTE.PORTION_ID=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID', 'ANALYTE.PORTION_ID=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID AND SAMPLE.PATIENT_ID=PATIENT.PATIENT_ID', 'ANALYTE_BARCODE', 15, 'analyte', 'ANALYTE_ELEMENT', 3, 'ANALYTE_ID', 'ANALYTE_ARCHIVE', 236);
Insert into CLINICAL_TABLE
   (CLINICAL_TABLE_ID, TABLE_NAME, JOIN_FOR_SAMPLE, JOIN_FOR_PATIENT, BARCODE_COLUMN_NAME, BARCODE_ELEMENT_ID, ELEMENT_NODE_NAME, ELEMENT_TABLE_NAME, PARENT_TABLE_ID, TABLE_ID_COLUMN_NAME, ARCHIVE_LINK_TABLE_NAME, UUID_ELEMENT_ID)
 Values
   (5, 'ALIQUOT', 'ALIQUOT.ANALYTE_ID=ANALYTE.ANALYTE_ID AND ANALYTE.PORTION_ID=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID', 'ALIQUOT.ANALYTE_ID=ANALYTE.ANALYTE_ID AND ANALYTE.PORTION_ID=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID AND SAMPLE.PATIENT_ID=PATIENT.PATIENT_ID', 'ALIQUOT_BARCODE', 14, 'aliquot', 'ALIQUOT_ELEMENT', 4, 'ALIQUOT_ID', 'ALIQUOT_ARCHIVE', 237);
Insert into CLINICAL_TABLE
   (CLINICAL_TABLE_ID, TABLE_NAME, JOIN_FOR_SAMPLE, JOIN_FOR_PATIENT, BARCODE_COLUMN_NAME, BARCODE_ELEMENT_ID, ELEMENT_NODE_NAME, ELEMENT_TABLE_NAME, PARENT_TABLE_ID, TABLE_ID_COLUMN_NAME, ARCHIVE_LINK_TABLE_NAME)
 Values
   (6, 'PROTOCOL', 'PROTOCOL.ANALYTE_ID=ANALYTE.ANALYTE_ID AND ANALYTE.PORTION_ID=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID', 'PROTOCOL.ANALYTE_ID=ANALYTE.ANALYTE_ID AND ANALYTE.PORTION_ID=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID AND SAMPLE.PATIENT_ID=PATIENT.PATIENT_ID', null, null, 'protocol', 'PROTOCOL_ELEMENT', 4, 'PROTOCOL_ID', 'PROTOCOL_ARCHIVE');
Insert into CLINICAL_TABLE
   (CLINICAL_TABLE_ID, TABLE_NAME, JOIN_FOR_SAMPLE, JOIN_FOR_PATIENT, BARCODE_COLUMN_NAME, BARCODE_ELEMENT_ID, ELEMENT_NODE_NAME, ELEMENT_TABLE_NAME, PARENT_TABLE_ID, TABLE_ID_COLUMN_NAME, ARCHIVE_LINK_TABLE_NAME)
 Values
   (7, 'DNA', 'DNA.ANALYTE_ID(+)=ANALYTE.ANALYTE_ID AND ANALYTE.PORTION_ID=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID', 'DNA.ANALYTE_ID(+)=ANALYTE.ANALYTE_ID AND ANALYTE.PORTION_ID=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID AND SAMPLE.PATIENT_ID=PATIENT.PATIENT_ID', null, null, 'dna', 'DNA_ELEMENT', 4, 'DNA_ID', 'DNA_ARCHIVE');
Insert into CLINICAL_TABLE
   (CLINICAL_TABLE_ID, TABLE_NAME, JOIN_FOR_SAMPLE, JOIN_FOR_PATIENT, BARCODE_COLUMN_NAME, BARCODE_ELEMENT_ID, ELEMENT_NODE_NAME, ELEMENT_TABLE_NAME, PARENT_TABLE_ID, TABLE_ID_COLUMN_NAME, ARCHIVE_LINK_TABLE_NAME)
 Values
   (8, 'RNA', 'RNA.ANALYTE_ID(+)=ANALYTE.ANALYTE_ID AND ANALYTE.PORTION_ID=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID', 'RNA.ANALYTE_ID(+)=ANALYTE.ANALYTE_ID AND ANALYTE.PORTION_ID=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID AND SAMPLE.PATIENT_ID=PATIENT.PATIENT_ID', null, null, 'rna', 'RNA_ELEMENT', 4, 'RNA_ID', 'RNA_ARCHIVE');
Insert into CLINICAL_TABLE
  (CLINICAL_TABLE_ID, TABLE_NAME, JOIN_FOR_SAMPLE, JOIN_FOR_PATIENT, BARCODE_COLUMN_NAME, BARCODE_ELEMENT_ID, ELEMENT_NODE_NAME, ELEMENT_TABLE_NAME, PARENT_TABLE_ID, TABLE_ID_COLUMN_NAME, ARCHIVE_LINK_TABLE_NAME, UUID_ELEMENT_ID)
 Values
   (10, 'SLIDE', 'SLIDE.PORTION_ID(+)=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID', 'SLIDE.PORTION_ID(+)=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID AND SAMPLE.PATIENT_ID=PATIENT.PATIENT_ID', 'SLIDE_BARCODE', 20, 'slide', 'SLIDE_ELEMENT', 3, 'SLIDE_ID', 'SLIDE_ARCHIVE', 238);
Insert into CLINICAL_TABLE
   (CLINICAL_TABLE_ID, TABLE_NAME, JOIN_FOR_SAMPLE, JOIN_FOR_PATIENT, BARCODE_COLUMN_NAME, BARCODE_ELEMENT_ID, ELEMENT_NODE_NAME, ELEMENT_TABLE_NAME, PARENT_TABLE_ID, TABLE_ID_COLUMN_NAME, ARCHIVE_LINK_TABLE_NAME, UUID_ELEMENT_ID)
 Values
   (12, 'EXAMINATION', 'EXAMINATION.PATIENT_ID(+)=PATIENT.PATIENT_ID AND PATIENT.PATIENT_ID=SAMPLE.PATIENT_ID', 'EXAMINATION.PATIENT_ID(+)=PATIENT.PATIENT_ID', 'EXAM_BARCODE', 182, 'examination', 'EXAMINATION_ELEMENT', 1, 'EXAMINATION_ID', 'EXAMINATION_ARCHIVE', 240);
Insert into CLINICAL_TABLE
   (CLINICAL_TABLE_ID, TABLE_NAME, JOIN_FOR_SAMPLE, JOIN_FOR_PATIENT, BARCODE_COLUMN_NAME, BARCODE_ELEMENT_ID, ELEMENT_NODE_NAME, ELEMENT_TABLE_NAME, PARENT_TABLE_ID, TABLE_ID_COLUMN_NAME, ARCHIVE_LINK_TABLE_NAME, UUID_ELEMENT_ID)
 Values
   (13, 'RADIATION', 'RADIATION.PATIENT_ID(+)=PATIENT.PATIENT_ID AND PATIENT.PATIENT_ID=SAMPLE.PATIENT_ID', 'RADIATION.PATIENT_ID(+)=PATIENT.PATIENT_ID', 'RADIATION_BARCODE', 180, 'radiation', 'RADIATION_ELEMENT', 1, 'RADIATION_ID', 'RADIATION_ARCHIVE', 239);
Insert into CLINICAL_TABLE
   (CLINICAL_TABLE_ID, TABLE_NAME, JOIN_FOR_SAMPLE, JOIN_FOR_PATIENT, BARCODE_COLUMN_NAME, BARCODE_ELEMENT_ID, ELEMENT_NODE_NAME, ELEMENT_TABLE_NAME, PARENT_TABLE_ID, TABLE_ID_COLUMN_NAME, ARCHIVE_LINK_TABLE_NAME, UUID_ELEMENT_ID)
 Values
   (14, 'SURGERY', 'SURGERY.PATIENT_ID(+)=PATIENT.PATIENT_ID AND PATIENT.PATIENT_ID=SAMPLE.PATIENT_ID', 'SURGERY.PATIENT_ID(+)=PATIENT.PATIENT_ID', 'SURGERY_BARCODE', 181, 'surgery', 'SURGERY_ELEMENT', 1, 'SURGERY_ID', 'SURGERY_ARCHIVE', 241);
Insert into CLINICAL_TABLE
   (CLINICAL_TABLE_ID, TABLE_NAME, JOIN_FOR_SAMPLE, JOIN_FOR_PATIENT, BARCODE_COLUMN_NAME, BARCODE_ELEMENT_ID, ELEMENT_NODE_NAME, ELEMENT_TABLE_NAME, PARENT_TABLE_ID, TABLE_ID_COLUMN_NAME, ARCHIVE_LINK_TABLE_NAME, UUID_ELEMENT_ID)
 Values
   (15, 'DRUG_INTGEN', 'DRUG_INTGEN.PATIENT_ID(+)=PATIENT.PATIENT_ID AND PATIENT.PATIENT_ID=SAMPLE.PATIENT_ID', 'DRUG_INTGEN.PATIENT_ID(+)=PATIENT.PATIENT_ID', 'DRUG_BARCODE', 179, 'drug', 'DRUG_INTGEN_ELEMENT', 1, 'DRUG_ID', 'DRUG_INTGEN_ARCHIVE', 242);
Insert into CLINICAL_TABLE
   (CLINICAL_TABLE_ID, TABLE_NAME, JOIN_FOR_SAMPLE, JOIN_FOR_PATIENT, BARCODE_COLUMN_NAME, BARCODE_ELEMENT_ID, ELEMENT_NODE_NAME, ELEMENT_TABLE_NAME, PARENT_TABLE_ID, TABLE_ID_COLUMN_NAME, ARCHIVE_LINK_TABLE_NAME)
 Values
   (16, 'TUMORPATHOLOGY', 'TUMORPATHOLOGY.SAMPLE_ID(+)=SAMPLE.SAMPLE_ID', 'TUMORPATHOLOGY.SAMPLE_ID(+)=SAMPLE.SAMPLE_ID AND SAMPLE.PATIENT_ID=PATIENT.PATIENT_ID', null, null, 'tumor_pathology', 'TUMORPATHOLOGY_ELEMENT', 2, 'TUMORPATHOLOGY_ID', 'TUMORPATHOLOGY_ARCHIVE');
COMMIT;
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (1, 'clinical_patient', 1, 'dam');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (1, 1, 1);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (2, 'clinical_sample', 0, 'dam');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (2, 2, 2);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (3, 'clinical_portion', 0, 'dam');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (3, 3, 3);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (4, 'clinical_analyte', 0, 'dam');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (4, 4, 4);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (5, 'clinical_aliquot', 0, 'dam');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (5, 5, 5);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (6, 'clinical_protocol', 0, 'dam');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (6, 6, 6);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (7, 'clinical_slide', 0, 'dam');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (7, 7, 10);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (10, 'clinical_drug', 1, 'dam');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (8, 10, 15);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (11, 'clinical_examination', 1, 'dam');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (9, 11, 12);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (12, 'clinical_radiation', 1, 'dam');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (10, 12, 13);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (13, 'clinical_surgery', 1, 'dam');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (11, 13, 14);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (14, 'dbgap_subjects', 1, 'dbgap');
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (15, 'dbgap_samples', 0, 'dbgap');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (12, 15, 2);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (16, 'dbgap_slides', 0, 'dbgap');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (13, 16, 10);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (17, 'dbgap_subjects_to_samples', 1, 'dbgap');
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (18, 'dbgap_subjects_drugs', 1, 'dbgap');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (14, 18, 15);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (19, 'dbgap_subjects_examinations', 1, 'dbgap');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (15, 19, 12);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (20, 'dbgap_subjects_radiations', 1, 'dbgap');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (16, 20, 13);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (21, 'dbgap_subjects_surgeries', 1, 'dbgap');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (17, 21, 14);
Insert into CLINICAL_FILE
   (CLINICAL_FILE_ID, FILENAME, BY_PATIENT, CONTEXT)
 Values
   (22, 'dbgap_subjects_info', 1, 'dbgap');
Insert into CLINICAL_FILE_TO_TABLE
   (CLINICAL_FILE_TABLE_ID, CLINICAL_FILE_ID, CLINICAL_TABLE_ID)
 Values
   (18, 22, 1);
COMMIT;
Insert into CLINICAL_FILE_ELEMENT
   (CLINICAL_FILE_ELEMENT_ID, XSD_ELEMENT_ID, TABLE_ID, TABLE_COLUMN_NAME, FILE_COLUMN_NAME, 
    FILE_COLUMN_ORDER, CLINICAL_FILE_ID, DISEASE_ID)
 Values
   (1, 16, 1, 'PATIENT_BARCODE', 'bcr_patient_barcode',
    1, 14, NULL);
Insert into CLINICAL_FILE_ELEMENT
   (CLINICAL_FILE_ELEMENT_ID, XSD_ELEMENT_ID, TABLE_ID, TABLE_COLUMN_NAME, FILE_COLUMN_NAME, 
    FILE_COLUMN_ORDER, CLINICAL_FILE_ID, DISEASE_ID)
 Values
   (2, 14, 5, 'ALIQUOT_BARCODE', 'bcr_aliquot_barcode',
    1, 17, NULL);
Insert into CLINICAL_FILE_ELEMENT
   (CLINICAL_FILE_ELEMENT_ID, XSD_ELEMENT_ID, TABLE_ID, TABLE_COLUMN_NAME, FILE_COLUMN_NAME, 
    FILE_COLUMN_ORDER, CLINICAL_FILE_ID, DISEASE_ID)
 Values
   (3, 16, 1, 'PATIENT_BARCODE', 'bcr_patient_barcode',
    2, 17, NULL);
 -- specifically add analyte barcode to protocol file
 Insert into CLINICAL_FILE_ELEMENT
   (CLINICAL_FILE_ELEMENT_ID, XSD_ELEMENT_ID, TABLE_ID, TABLE_COLUMN_NAME, FILE_COLUMN_NAME,
    FILE_COLUMN_ORDER, CLINICAL_FILE_ID, DISEASE_ID)
 Values
   (4, 15, 4, 'ANALYTE_BARCODE', 'bcr_analyte_barcode',
    2, 6, NULL);
COMMIT;
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (1, 11, ' ');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (2, 11, 'DNA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (3, 11, 'EBV Immortalized Normal');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (4, 11, 'GenomePlex (Rubicon) Amplified DNA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (5, 11, 'Repli-G (Qiagen) DNA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (6, 11, 'RNA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (7, 11, 'Total RNA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (9, 21, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (10, 21, 'LOW');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (11, 21, 'MEDIUM');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (12, 21, 'HIGH');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (13, 46, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (14, 46, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (15, 46, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (16, 49, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (17, 49, 'LIQUID N2 VAPOR');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (18, 49, 'DRY ICE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (19, 49, '-80 C');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (20, 49, '-20 C');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (21, 52, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (22, 52, 'MALE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (23, 52, 'FEMALE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (24, 70, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (25, 70, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (26, 70, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (27, 71, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (28, 71, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (29, 71, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (30, 89, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (31, 89, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (32, 89, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (33, 97, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (34, 97, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (35, 97, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (36, 112, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (37, 112, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (38, 112, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (40, 113, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (41, 113, 'PRIMARY');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (42, 113, 'METASTATIC');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (43, 113, 'MALIGNANT');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (44, 113, 'NON-MALIGNANT');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (45, 136, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (46, 136, 'Blood Derived Normal');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (47, 136, 'Buccal Cell Normal');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (48, 136, 'Cell Line Control');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (49, 136, 'DNA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (50, 136, 'EBV Immortalized Normal');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (51, 136, 'GenomePlex (Rubicon) Amplified DNA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (52, 136, 'Primary Tumor');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (53, 136, 'Recurrent Tumor');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (54, 136, 'Repli-G (Qiagen) DNA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (55, 136, 'RNA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (56, 136, 'Slides');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (57, 136, 'Solid Tissue Normal');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (58, 136, 'Total RNA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (59, 153, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (60, 153, 'C70.0');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (61, 153, 'C71.0');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (62, 153, 'C71.1');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (63, 153, 'C71.2');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (64, 153, 'C71.3');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (65, 153, 'C71.4');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (66, 153, 'C71.5');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (67, 153, 'C71.6');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (68, 153, 'C71.7');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (69, 153, 'C71.8');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (70, 153, 'C71.9');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (71, 153, 'C72.0');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (72, 153, 'C72.9');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (73, 153, 'C34.0');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (74, 153, 'C34.1');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (75, 153, 'C34.2');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (76, 153, 'C34.3');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (77, 153, 'C34.8');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (78, 153, 'C34.9');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (79, 153, 'BRAIN, NOS');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (80, 153, 'BLOOD');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (81, 153, 'CEREBRAL MENINGES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (82, 153, 'CEREBRUM');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (83, 153, 'FRONTAL LOBE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (84, 153, 'TEMPORAL LOBE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (85, 153, 'PARIETAL LOBE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (86, 153, 'OCCIPITAL LOBE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (87, 153, 'VENTRICLE, NOS');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (88, 153, 'CEREBELLUM, NOS');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (89, 153, 'BRAIN STEM');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (90, 153, 'OVERLAPPING LESION OF BRAIN');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (91, 153, 'SPINAL CORD');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (92, 153, 'NERVOUS SYSTEM, NOS');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (93, 157, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (94, 157, 'BRAIN');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (95, 157, 'LUNG');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (96, 157, 'OVARY');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (97, 157, 'PERITONEUM (OVARY)');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (98, 157, 'COLON');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (99, 159, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (100, 159, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (101, 159, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (102, 4, 'Yes');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (103, 4, 'No');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (104, 4, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (105, 5, 'Yes');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (106, 5, 'No');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (107, 5, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (108, 6, 'Yes');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (109, 6, 'No');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (110, 6, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (111, 7, 'Yes');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (112, 7, 'No');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (113, 7, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (114, 8, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (115, 8, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (116, 8, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (117, 22, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (118, 22, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (119, 22, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (120, 42, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (121, 42, 'CHEMO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (122, 42, 'HORMONAL');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (123, 42, 'IMMUNO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (124, 42, 'TARGETED');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (125, 45, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (126, 45, '0');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (127, 45, '1');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (128, 45, '2');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (129, 45, '3');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (130, 45, '4');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (131, 45, '5');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (132, 47, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (133, 47, 'HISPANIC OR LATINO"');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (134, 47, 'NOT HISPANIC OR LATINO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (135, 51, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (136, 51, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (137, 51, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (138, 53, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (139, 53, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (140, 53, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (141, 55, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (142, 55, 'LOW');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (143, 55, 'HIGH');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (144, 55, '1');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (145, 55, '2');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (146, 55, '3');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (147, 55, '4');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (148, 58, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (149, 58, 'ANAPLASTIC ASTROCYTOMA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (150, 58, 'GIANT CELL GLIOBLASTOMA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (151, 58, 'GLIOBLASTOMA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (152, 58, 'GLIOSARCOMA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (153, 58, 'GLIOBLASTOMA MULTIFORME');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (154, 58, 'NO HISTOLOGY AVAILABLE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (155, 59, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (156, 59, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (157, 59, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (158, 60, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (159, 60, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (160, 60, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (161, 61, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (162, 61, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (163, 61, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (165, 62, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (166, 62, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (167, 62, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (168, 63, 'CYTOLOGY');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (169, 63, 'FINE NEEDLE ASPIRATION BIOPSY');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (170, 63, 'INCISION BIOPSY');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (171, 63, 'EXCISIONAL BIOPSY');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (172, 63, 'TUMOR RESECTION');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (173, 63, 'OTHER METHOD, SPECIFY IN NOTES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (174, 66, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (175, 66, 'ASHKENAZI');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (176, 66, 'SEPHARDIC');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (177, 67, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (178, 67, '0');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (179, 67, '10');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (180, 67, '20');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (181, 67, '30');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (182, 67, '40');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (183, 67, '50');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (184, 67, '60');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (185, 67, '70');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (186, 67, '80');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (187, 67, '90');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (188, 67, '100');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (189, 68, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (190, 68, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (191, 68, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (192, 72, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (193, 72, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (194, 72, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (195, 96, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (196, 96, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (197, 96, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (198, 110, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (199, 110, 'PREOPERATIVE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (200, 110, 'PRE-ADJUVANT THERAPY');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (201, 110, 'POST-ADJUVANT THERAPY');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (202, 110, 'OTHER');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (203, 111, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (204, 111, 'TUMOR FREE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (205, 111, 'WITH TUMOR');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (206, 114, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (207, 114, 'PROGRESSIVE DISEASE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (208, 114, 'STABLE DISEASE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (209, 114, 'PARTIAL RESPONSE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (210, 114, 'COMPLETE RESPONSE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (211, 115, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (212, 115, 'Yes');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (213, 115, 'No');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (214, 117, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (215, 117, 'LABORATORY');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (216, 117, 'PATHOLOGY');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (219, 117, 'RADIATION');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (220, 117, 'PHYSICAL EXAMINATION');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (221, 117, 'MOLECULAR MARKER(S)');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (222, 117, 'FIRST SEEN AT FURTHER SURGERY');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (223, 117, 'OTHER METHOD: SPECIFY IN NOTES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (224, 119, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (225, 119, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (226, 119, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (227, 122, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (228, 122, 'NOT REPORTED');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (229, 122, 'WHITE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (230, 122, 'AMERICAN INDIAN OR ALASKA NATIVE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (231, 122, 'BLACK OR AFRICAN AMERICAN');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (232, 122, 'ASIAN');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (233, 122, 'NATIVE HAWAIIAN OR OTHER PACIFIC ISLANDER');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (234, 124, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (235, 124, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (236, 124, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (238, 125, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (239, 125, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (240, 125, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (241, 126, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (242, 126, 'EXTERNAL BEAM');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (243, 126, 'IMPLANTS');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (244, 126, 'RADIOISOTOPES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (245, 126, 'COMBINATION');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (246, 126, 'OTHER: SPECIFY IN NOTES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (247, 129, 'OTHER: SPECIFY IN NOTES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (248, 129, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (249, 129, 'ADJUVANT');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (250, 129, 'PROGRESSION');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (251, 129, 'RECURRENCE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (252, 129, 'PALLIATIVE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (253, 131, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (254, 131, 'RX');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (255, 131, 'R0');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (256, 131, 'R1');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (257, 131, 'R2');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (258, 134, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (259, 134, 'ORAL');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (260, 134, 'INTRAVENOUS (IV)');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (261, 134, 'INTRATUMORAL');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (262, 134, 'INTRA-PERITONEAL (IP)');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (263, 134, 'IV AND IP');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (264, 134, 'OTHER: SPECIFY IN NOTES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (265, 137, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (266, 137, 'TOP');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (267, 137, 'BOTTOM');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (268, 139, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (269, 139, 'METASTASIS');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (270, 139, 'LOCO-REGIONAL');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (271, 140, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (272, 140, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (273, 140, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (274, 143, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (275, 143, 'N0');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (276, 143, 'N1');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (277, 143, 'N2');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (278, 143, 'N3');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (279, 143, 'NX');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (280, 145, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (281, 145, 'M0');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (282, 145, 'M1');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (283, 145, 'MX');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (284, 147, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (285, 147, '0');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (286, 147, 'IA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (287, 147, 'IB');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (288, 147, 'IC');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (289, 147, 'II');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (290, 147, 'IIA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (291, 147, 'IIB');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (292, 147, 'IIC');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (293, 147, 'III');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (294, 147, 'IIIA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (295, 147, 'IIIB');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (296, 147, 'IIIC');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (297, 147, 'IV');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (298, 149, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (299, 149, 'TX');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (300, 149, 'TIS');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (301, 149, 'T4');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (302, 149, 'T3C');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (303, 149, 'T3B');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (304, 149, 'T3A');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (305, 149, 'T3');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (306, 149, 'T2C');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (307, 149, 'T2B');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (308, 149, 'T2A');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (309, 149, 'T2');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (310, 149, 'T1C');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (311, 149, 'T1B');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (312, 149, 'T1A');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (313, 149, 'T1');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (314, 149, 'T0');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (315, 151, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (316, 151, 'GX');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (317, 151, 'GB');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (318, 151, 'G1');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (319, 151, 'G2');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (320, 151, 'G3');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (321, 151, 'G4');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (322, 152, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (323, 152, 'No Macroscopic Disease');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (324, 152, '1-10 mm');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (325, 152, '11-20 mm');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (326, 152, '>20 mm');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (327, 156, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (328, 156, 'IA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (329, 156, 'IB');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (330, 156, 'IC');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (331, 156, 'IIA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (332, 156, 'IIB');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (333, 156, 'IIC');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (334, 156, 'IIIA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (335, 156, 'IIIB');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (336, 156, 'IIIC');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (337, 156, 'IV');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (338, 161, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (339, 161, 'LIVING');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (340, 161, 'DECEASED');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (341, 136, 'Primary Blood Derived Cancer');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (342, 136, 'Recurrent Blood Derived Cancer');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (343, 136, 'Repli-G X (Qiagen) DNA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (344, 11, 'Repli-G X (Qiagen) DNA');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (345, 157, 'KIDNEY');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (346, 157, 'LYMPH NODE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (347, 157, 'OMENTUM');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (348, 157, 'RECTUM');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (349, 217, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (350, 217, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (351, 217, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (352, 211, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (353, 211, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (354, 211, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (355, 214, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (356, 214, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (357, 214, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (358, 63, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (359, 110, 'AT RECURRENCE/PROGRESSION OF DISEASE');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (360, 110, 'POST-SECONDARY THERAPY');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (361, 117, 'IMAGING STUDY');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (362, 208, 'Chemotherapy');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (363, 208, 'Hormone Therapy');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (364, 208, 'Immunotherapy');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (365, 208, 'Targeted Molecular therapy');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (366, 208, 'Other, specify');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (367, 134, 'SUBCUTANEOUS (SC)');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (368, 134, 'INTRAMUSCULAR (IM)');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (369, 209, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (370, 209, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (371, 209, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (372, 224, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (373, 224, 'TX');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (374, 224, 'T0');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (375, 224, 'T1');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (376, 224, 'Ta1');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (377, 224, 'T1b');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (378, 224, 'T2');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (379, 224, 'T2a');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (380, 224, 'T2b');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (381, 224, 'T3');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (382, 224, 'T3a');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (383, 224, 'T3b');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (384, 224, 'T3c');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (385, 224, 'T4');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (386, 225, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (387, 225, 'NX');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (388, 225, 'N0');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (389, 225, 'N1');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (390, 226, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (391, 226, 'M0');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (392, 226, 'M1');
   Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (393, 221, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (394, 221, 'YES');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (395, 221, 'NO');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (396, 232, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (397, 232, 'Elevated');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (398, 232, 'Normal');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (399, 231, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (400, 231, 'Elevated');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (401, 231, 'Normal');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (402, 231, 'Low');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (403, 227, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (404, 227, 'Elevated');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (405, 227, 'Normal');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (406, 227, 'Low');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (407, 230, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (408, 230, 'Elevated');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (409, 230, 'Normal');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (410, 230, 'Low');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (411, 229, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (412, 229, 'Elevated');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (413, 229, 'Normal');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (414, 229, 'Low');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (415, 228, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (416, 228, 'Elevated');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (417, 228, 'Normal');   
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (418, 219, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (419, 219, 'Type 1');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (420, 219, 'Type 2');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (421, 220, NULL);
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (422, 220, 'Right');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (423, 220, 'Left');
Insert into CLINICAL_XSD_ENUM_VALUE
   (CLINICAL_XSD_ENUM_VALUE_ID, XSD_ELEMENT_ID, ENUM_VALUE)
 Values
   (424, 220, 'Bilateral');   
COMMIT;
