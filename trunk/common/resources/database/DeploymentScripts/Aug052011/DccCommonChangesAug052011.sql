-- APPS-3924 Copy biospecimen_barcode
MERGE INTO shipped_biospecimen b
USING 
(SELECT 
    biospecimen_id,
    uuid,
    barcode,
    project_code,
    tss_code,
    bcr_center_id,
    patient,
    is_viewable,
    ship_date
FROM biospecimen_barcode) v
ON (v.biospecimen_id = b.shipped_biospecimen_id)
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
-- now populate shipped_biospecimen_element for all of the element types for an aliquot (5 of them)
MERGE INTO shipped_biospecimen_element b
USING
(SELECT 
    biospecimen_id,element_type_id,element_value
FROM
(select
    biospecimen_id,
    1 as element_type_id,
    sample_type_code as element_value
FROM biospecimen_barcode
UNION
SELECT 
    biospecimen_id,
    2 as element_type_id,
    sample_sequence as element_value
FROM biospecimen_barcode
UNION
SELECT 
    biospecimen_id,
    3 as element_type_id,
    portion_sequence as element_value
FROM biospecimen_barcode
UNION
SELECT 
    biospecimen_id,
    4 as element_type_code,
    portion_analyte_code as element_value
FROM biospecimen_barcode
UNION
SELECT 
    biospecimen_id,
    5 as element_type_id,
    plate_id as element_value
FROM biospecimen_barcode) ) v
ON (v.biospecimen_id = b.shipped_biospecimen_id and v.element_type_id=b.element_type_id)
WHEN NOT MATCHED THEN
INSERT (
    shipped_biospecimen_element_id,
    shipped_biospecimen_id,
    element_type_id,
    element_value)
VALUES (
    shipped_biospec_element_seq.NEXTVAL,
    v.biospecimen_id,
    v.element_type_id,
    v.element_value
);
commit;

-- APPS-3925 Copy biospecimen_to_file
MERGE INTO shipped_biospecimen_file b
USING
(SELECT distinct biospecimen_id,file_id FROM biospecimen_to_file) v
ON (v.biospecimen_id=b.shipped_biospecimen_id and v.file_id=b.file_id)
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
(SELECT biospecimen_id,archive_id FROM bcr_biospecimen_to_archive) v
ON (v.biospecimen_id = b.shipped_biospecimen_id)
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

-- APPS-3927 create shipped_biospecimen_bamfile table
DROP TABLE shipped_biospecimen_bamfile;
CREATE TABLE shipped_biospecimen_bamfile (
   shipped_biospecimen_id	NUMBER(38) NOT NULL,
   bam_file_id			NUMBER(38) NOT NULL,
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

-- APPS-3928 copy biospecimen_to_bam_file
MERGE INTO shipped_biospecimen_bamfile s
USING 
(SELECT DISTINCT biospecimen_id,bam_file_id FROM biospecimen_to_bam_file )v
ON (s.shipped_biospecimen_id=v.biospecimen_id AND s.bam_file_id = v.bam_file_id)
WHEN NOT MATCHED THEN
INSERT (
    shipped_biospecimen_id,
    bam_file_id)
VALUES (
    v.biospecimen_id,
    v.bam_file_id
);

COMMIT;

-- APPS-3944 create new relationship table for shipped_biospecimen_id to ncbi and populate it from biospecimen_ncbi_trace
DROP TABLE shipped_biospec_ncbi_trace;
CREATE TABLE shipped_biospec_ncbi_trace (
   biospecimen_trace_id		NUMBER(38) NOT NULL,
   shipped_biospecimen_id 	NUMBER(38) NOT NULL,
   ncbi_trace_id		NUMBER(38) NOT NULL,
   file_id			NUMBER(38) NOT NULL,
   dcc_date_received		DATE	   NOT NULL,
   CONSTRAINT pk_shipped_biospec_ncbitr_idx PRIMARY KEY (biospecimen_trace_id)
);
INSERT INTO shipped_biospec_ncbi_trace (biospecimen_trace_id, shipped_biospecimen_id, ncbi_trace_id,file_id,dcc_date_received)
SELECT biospecimen_trace_id, biospecimen_id, ncbi_trace_id,file_id,dcc_date_received
FROM   biospecimen_ncbi_trace;
commit;

ALTER TABLE shipped_biospec_ncbi_trace ADD (
CONSTRAINT fk_ship_biospec_ncbi_biospec
FOREIGN KEY (shipped_biospecimen_id)
REFERENCES shipped_biospecimen(shipped_biospecimen_id),
CONSTRAINT fk_shipp_biospec_ncbi_file
FOREIGN KEY (file_id)
REFERENCES file_info(file_id)
);

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

-- now set is_redacted in shipped_biospecimen based on a lookup for redacted annotations
MERGE INTO shipped_biospecimen s
USING
(SELECT b.shipped_biospecimen_id,b.specific_patient
 FROM shipped_biospecimen_aliquot b, 
 (select distinct ai.annotation_item as barcode
  from annotation_item ai, annotation a, annotation_category ac
  where ac.annotation_classification_id=5
  and  ac.annotation_category_id=a.annotation_category_id
  and a.annotation_id=ai.annotation_id) r
WHERE b.specific_patient = r.barcode or b.sample=r.barcode) v
ON (v.shipped_biospecimen_id = s.shipped_biospecimen_id)
WHEN MATCHED THEN UPDATE SET
   s.is_redacted = 1;
COMMIT;
-- we know that 2 patients were redacted and then un-redacted, so reset it's children to not redacted after the previous update
update shipped_biospecimen set is_redacted=0 where participant_code='4994';
update shipped_biospecimen set is_redacted=0 where participant_code='4334';
commit;

grant select on shipped_biospecimen to TCGABLCA;
grant select on shipped_biospecimen to TCGAPAAD;
grant select on shipped_biospecimen to TCGADLBC;
grant select on shipped_biospecimen to TCGASALD;
grant select on shipped_biospecimen to TCGALGG;
grant select on shipped_biospecimen to TCGATHCA;
grant select on shipped_biospecimen to TCGASKCM;
grant select on shipped_biospecimen to TCGAPRAD;
grant select on shipped_biospecimen to TCGALNNH;
grant select on shipped_biospecimen to TCGALIHC;
grant select on shipped_biospecimen to TCGALCLL;
grant select on shipped_biospecimen to TCGAHNSC;
grant select on shipped_biospecimen to TCGACESC;
grant select on shipped_biospecimen to TCGASTAD;
grant select on shipped_biospecimen to TCGAKIRC;
grant select on shipped_biospecimen to TCGAREAD;
grant select on shipped_biospecimen to TCGAUCEC;
grant select on shipped_biospecimen to TCGABRCA;
grant select on shipped_biospecimen to TCGALUAD;
grant select on shipped_biospecimen to TCGAKIRP;
grant select on shipped_biospecimen to TCGACOAD;
grant select on shipped_biospecimen to TCGALAML;
grant select on shipped_biospecimen to TCGAGBM;
grant select on shipped_biospecimen to TCGAOV;
grant select on shipped_biospecimen to TCGALUSC;
grant select on shipped_biospecimen_element to TCGABLCA;
grant select on shipped_biospecimen_element to TCGAPAAD;
grant select on shipped_biospecimen_element to TCGADLBC;
grant select on shipped_biospecimen_element to TCGASALD;
grant select on shipped_biospecimen_element to TCGALGG;
grant select on shipped_biospecimen_element to TCGATHCA;
grant select on shipped_biospecimen_element to TCGASKCM;
grant select on shipped_biospecimen_element to TCGAPRAD;
grant select on shipped_biospecimen_element to TCGALNNH;
grant select on shipped_biospecimen_element to TCGALIHC;
grant select on shipped_biospecimen_element to TCGALCLL;
grant select on shipped_biospecimen_element to TCGAHNSC;
grant select on shipped_biospecimen_element to TCGACESC;
grant select on shipped_biospecimen_element to TCGASTAD;
grant select on shipped_biospecimen_element to TCGAKIRC;
grant select on shipped_biospecimen_element to TCGAREAD;
grant select on shipped_biospecimen_element to TCGAUCEC;
grant select on shipped_biospecimen_element to TCGABRCA;
grant select on shipped_biospecimen_element to TCGALUAD;
grant select on shipped_biospecimen_element to TCGAKIRP;
grant select on shipped_biospecimen_element to TCGACOAD;
grant select on shipped_biospecimen_element to TCGALAML;
grant select on shipped_biospecimen_element to TCGAGBM;
grant select on shipped_biospecimen_element to TCGAOV;
grant select on shipped_biospecimen_element to TCGALUSC;
grant select on tss_to_disease to TCGABLCA;
grant select on tss_to_disease to TCGAPAAD;
grant select on tss_to_disease to TCGADLBC;
grant select on tss_to_disease to TCGASALD;
grant select on tss_to_disease to TCGALGG;
grant select on tss_to_disease to TCGATHCA;
grant select on tss_to_disease to TCGASKCM;
grant select on tss_to_disease to TCGAPRAD;
grant select on tss_to_disease to TCGALNNH;
grant select on tss_to_disease to TCGALIHC;
grant select on tss_to_disease to TCGALCLL;
grant select on tss_to_disease to TCGAHNSC;
grant select on tss_to_disease to TCGACESC;
grant select on tss_to_disease to TCGASTAD;
grant select on tss_to_disease to TCGAKIRC;
grant select on tss_to_disease to TCGAREAD;
grant select on tss_to_disease to TCGAUCEC;
grant select on tss_to_disease to TCGABRCA;
grant select on tss_to_disease to TCGALUAD;
grant select on tss_to_disease to TCGAKIRP;
grant select on tss_to_disease to TCGACOAD;
grant select on tss_to_disease to TCGALAML;
grant select on tss_to_disease to TCGAGBM;
grant select on tss_to_disease to TCGAOV;
grant select on tss_to_disease to TCGALUSC;
grant select on tss_to_disease to TCGABLCA;
grant select on tss_to_disease to TCGAPAAD;
grant select on tss_to_disease to TCGADLBC;
grant select on tss_to_disease to TCGASALD;
grant select on tss_to_disease to TCGALGG;
grant select on tss_to_disease to TCGATHCA;
grant select on tss_to_disease to TCGASKCM;
grant select on tss_to_disease to TCGAPRAD;
grant select on tss_to_disease to TCGALNNH;
grant select on tss_to_disease to TCGALIHC;
grant select on tss_to_disease to TCGALCLL;
grant select on tss_to_disease to TCGAHNSC;
grant select on tss_to_disease to TCGACESC;
grant select on tss_to_disease to TCGASTAD;
grant select on tss_to_disease to TCGAKIRC;
grant select on tss_to_disease to TCGAREAD;
grant select on tss_to_disease to TCGAUCEC;
grant select on tss_to_disease to TCGABRCA;
grant select on tss_to_disease to TCGALUAD;
grant select on tss_to_disease to TCGAKIRP;
grant select on tss_to_disease to TCGACOAD;
grant select on tss_to_disease to TCGALAML;
grant select on tss_to_disease to TCGAGBM;
grant select on tss_to_disease to TCGAOV;
grant select on tss_to_disease to TCGALUSC;
