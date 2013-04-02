-- APPS-3924 Copy biospecimen_barcode
-- found some biospeciemn_barcode records in disease schema were missing uuid's ; fix with this statement
--before merging into shipped_biospecimen
merge into biospecimen_barcode b
using (select biospecimen_id,uuid from dccCommon.biospecimen_barcode) v
on (b.biospecimen_id=v.biospecimen_id)
WHEN MATCHED THEN
UPDATE SET b.uuid=v.uuid;
commit;
-- now merge into shipped_biospecimen
MERGE INTO shipped_biospecimen s
USING (SELECT 
    biospecimen_id,
    uuid,
    1,
    barcode,
    project_code,
    tss_code,
    bcr_center_id,
    patient,
    is_viewable,
    0,
    ship_date
FROM biospecimen_barcode) v
ON (s.shipped_biospecimen_id = v.biospecimen_id)
WHEN NOT MATCHED THEN
INSERT (
    shipped_biospecimen_id,
    uuid,
    shipped_item_type_id,
    built_barcode,
    project_code,
    tss_code,
    bcr_center_id,
    participant_code,
    is_viewable,
    is_redacted,
    shipped_date)
VALUES ( 
    v.biospecimen_id,
    v.uuid,
    1,
    v.barcode,
    v.project_code,
    v.tss_code,
    v.bcr_center_id,
    v.patient,
    v.is_viewable,
    0,
    v.ship_date
);
commit;
-- now set is_redacted in shipped_biospecimen based on a lookup from dccCommon shipped_biospecimen
MERGE INTO shipped_biospecimen s
USING
(SELECT shipped_biospecimen_id,is_redacted
 FROM dccCommon.shipped_biospecimen where is_redacted=1) v
ON (v.shipped_biospecimen_id = s.shipped_biospecimen_id)
WHEN MATCHED THEN UPDATE SET
   s.is_redacted = 1;
COMMIT;

-- now populate shipped_biospecimen_element for all of the element types for an aliquot (5 of them)
MERGE INTO shipped_biospecimen_element s
USING
(select
    se.shipped_biospecimen_element_id,se.shipped_biospecimen_id,se.element_type_id,se.element_value
FROM dccCommon.shipped_biospecimen_element se, dccCommon.shipped_biospecimen s, dccCommon.tss_to_disease ts ,disease d
WHERE se.shipped_biospecimen_id=s.shipped_biospecimen_id and s.tss_code=ts.tss_code and ts.disease_id=d.disease_id) v
ON (s.shipped_biospecimen_element_id = v.shipped_biospecimen_element_id)
WHEN NOT MATCHED THEN
INSERT (
    shipped_biospecimen_element_id,
    shipped_biospecimen_id,
    element_type_id,
    element_value)
VALUES (
    v.shipped_biospecimen_element_id,
    v.shipped_biospecimen_id,
    v.element_type_id,
    v.element_value);

commit;

-- APPS-3925 Copy biospecimen_to_file
MERGE INTO shipped_biospecimen_file s
USING
(SELECT distinct biospecimen_id,file_id FROM biospecimen_to_file ) v
ON (s.shipped_biospecimen_id = v.biospecimen_id AND s.file_id=v.file_id)
WHEN NOT MATCHED THEN
INSERT (
    shipped_biospecimen_id,
    file_id
)
VALUES (
    v.biospecimen_id,
    v.file_id
);

commit;

-- APPS-3926 Copy bcr_biospecimen_to_archive

MERGE INTO shipped_biospec_bcr_archive b
USING
(SELECT distinct biospecimen_id,archive_id FROM bcr_biospecimen_to_archive) v
ON (v.biospecimen_id = b.shipped_biospecimen_id AND v.archive_id = b.archive_id)
WHEN NOT MATCHED THEN
INSERT (
   shipped_biospecimen_id,
   archive_id
 )
 VALUES (
   v.biospecimen_id,
   v.archive_id
 );

commit;

-- APPS-3927 create new view shipped_biospecimen_aliquot
CREATE OR REPLACE FORCE VIEW shipped_biospecimen_aliquot
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


