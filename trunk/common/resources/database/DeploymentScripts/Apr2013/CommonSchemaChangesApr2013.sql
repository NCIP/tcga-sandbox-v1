
ALTER TABLE biospecimen_to_file DROP CONSTRAINT fk_biospecimen_file_file; 
ALTER TABLE biospecimen_to_file ADD CONSTRAINT fk_biospecimen_file_file 
  FOREIGN KEY (file_id)
  REFERENCES file_info (file_id)
  ON DELETE CASCADE;

DROP INDEX biospec_ncbitrace_file_idx;
CREATE INDEX biospec_ncbitrace_file_idx ON biospecimen_ncbi_trace (file_id);

ALTER TABLE biospecimen_ncbi_trace DROP CONSTRAINT fk_biospec_ncbi_trace_file ;
ALTER TABLE biospecimen_ncbi_trace ADD CONSTRAINT fk_biospec_ncbi_trace_file 
    FOREIGN KEY (file_id)
    REFERENCES file_info (file_id)
    ON DELETE CASCADE;

ALTER TABLE file_to_archive DROP CONSTRAINT fk_filetoarchive_fileid;
ALTER TABLE file_to_archive ADD CONSTRAINT fk_filetoarchive_fileid 
    FOREIGN KEY (file_id)
    REFERENCES file_info (file_id)
    ON DELETE CASCADE;

ALTER TABLE file_to_collection DROP CONSTRAINT fk_file_collection_file; 
ALTER TABLE file_to_collection ADD CONSTRAINT fk_file_collection_file 
    FOREIGN KEY (file_id)
    REFERENCES file_info (file_id)
    ON DELETE CASCADE;


ALTER TABLE participant_uuid_file DROP CONSTRAINT fk_uuid_file;
ALTER TABLE participant_uuid_file ADD (
    CONSTRAINT fk_partic_uuid_fileid 
    FOREIGN KEY (file_id)
    REFERENCES file_info (file_id)
    ON DELETE CASCADE
);

DROP INDEX ship_biospec_ncbifile_file_idx;
CREATE INDEX ship_biospec_ncbifile_file_idx ON shipped_biospec_ncbi_trace (file_id);

ALTER TABLE shipped_biospec_ncbi_trace DROP CONSTRAINT fk_shipp_biospec_ncbi_file; 
ALTER TABLE shipped_biospec_ncbi_trace ADD CONSTRAINT fk_shipp_biospec_ncbi_file 
    FOREIGN KEY (file_id)
    REFERENCES file_info (file_id)
    ON DELETE CASCADE;


ALTER TABLE shipped_biospecimen_file DROP CONSTRAINT fk_shipped_biospec_file;
   ALTER TABLE shipped_biospecimen_file ADD (
   CONSTRAINT fk_shipped_biospec_file
   FOREIGN KEY (file_id)
   REFERENCES file_info (file_id)
   ON DELETE CASCADE
);

alter table center add requires_magetab number(1) default 0;

update center set requires_magetab=1 where center_type_code='CGCC';
commit;

-- for APPS-6753 prepare to change bam_file and bam_file_datatype tables by deleting all the data 
-- so it can be reloaded

DELETE FROM biospecimen_to_bam_file;
DELETE FROM shipped_biospecimen_bamfile;
DROP TABLE bam_file CASCADE CONSTRAINTS PURGE;
CREATE TABLE bam_file (
    bam_file_id			NUMBER(38,0)    NOT NULL,
    bam_file_name		VARCHAR2(100)	NOT NULL,
    disease_id			NUMBER(38,0)    NOT NULL,
    center_id			NUMBER(38,0)    NOT NULL,
    bam_file_size		NUMBER(38,0)    NOT NULL,
    date_received		DATE            NOT NULL,
    bam_datatype_id		NUMBER(38,0)    NOT NULL,
    analyte_code		VARCHAR2(10),
    CONSTRAINT pk_bam_file_idx PRIMARY KEY (bam_file_id)
);

ALTER TABLE bam_file ADD (
    CONSTRAINT fk_bam_file_center
    FOREIGN KEY (center_id)
    REFERENCES center(center_id),
    CONSTRAINT fk_bam_file_disease
    FOREIGN KEY (disease_id)
    REFERENCES disease(disease_id),
    CONSTRAINT fk_bam_file_datatype
    FOREIGN KEY (bam_datatype_id)
    REFERENCES bam_file_datatype(bam_datatype_id),
    CONSTRAINT fk_bam_file_portion_analyte
    FOREIGN KEY (analyte_code)
    REFERENCES portion_analyte(portion_analyte_code)
);

ALTER TABLE bam_file_datatype DROP COLUMN molecule;

INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'WGS',	'Genome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'WCS',	'Genome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'CLONE',	'Genome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'POOLCLONE',	'Genome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'AMPLICON',	'Genome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'CLONEEND',	'Genome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'FINISHING',	'Genome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'WXS',	'Exome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'EST',	'Exome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'FL-cDNA',	'Exome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'RNA-Seq',	'RNASeq');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'miRNA-Seq',	'miRNA');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'FINISHING',	'Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'ChIP-Seq',	'Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'MNase-Seq',	'Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'DNaseHypersensitivity',	'Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'Bisulfite-Seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'CTS',	'Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'MRE-Seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'MeDIP-Seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'MDB-Seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'VALIDATION','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'ncRNA-Seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'Tn-Seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'FAIRE-seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'SELEX','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'RIP-Seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'ChIA-PET','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (bam_datatype_seq.nextval,'OTHER','Unknown');

COMMIT;

purge recyclebin;