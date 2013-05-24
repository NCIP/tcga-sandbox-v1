
-- for APPS-6753 prepare to change bam_file and bam_file_datatype tables by deleting all the data 
-- so it can be reloaded

DELETE FROM biospecimen_to_bam_file;
DELETE FROM shipped_biospecimen_bamfile;
DROP TABLE bam_file CASCADE CONSTRAINTS PURGE;
CREATE TABLE bam_file (
    bam_file_id			NUMBER(38,0)    NOT NULL,
    bam_file_name		VARCHAR2(256)	NOT NULL,
    disease_id			NUMBER(38,0) NOT NULL,
    center_id			NUMBER(38,0)    NOT NULL,
    bam_file_size		NUMBER(38,0)    NOT NULL,
    date_received		DATE            NOT NULL,
    bam_datatype_id		NUMBER(38,0)    NOT NULL,
    analyte_code		VARCHAR2(10),
    analysis_id			VARCHAR2(36)	NOT NULL,
    dcc_received_date		DATE		NOT NULL,
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

CREATE UNIQUE INDEX bam_file_uk_analysis_idx ON bam_file (analysis_id);

CREATE TABLE cghub_center (
    cghub_center_name   VARCHAR2(25) NOT NULL,
    dcc_center_id       NUMBER(38) NOT NULL,
    CONSTRAINT cghub_center_pk_idx PRIMARY KEY (cghub_center_name)
);
ALTER TABLE cghub_center ADD
CONSTRAINT fk_cghub_center_centerid
FOREIGN KEY (dcc_center_id)
REFERENCES center(center_id);

INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('BCGSC' , 15);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('BCCAGSC' , 15);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('BCM' , 8);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('BI' , 12);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('HAIB' , 6);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('HMS' , 3);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('HMS-RK' , 3);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('IGC' , 26);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('ISB' , 17);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('JHU_USC' , 2);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('USC-JHU' , 2);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('LBL' , 4);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('MDA' , 25);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('MSKCC' , 5);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('NCH' , 27);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('RG' , 22);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('UCSC' , 29);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('UNC' , 7);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('UNC-LCCC' , 7);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('USC' , 28);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('VUMC' , 30);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('WUSM' , 9);
INSERT INTO cghub_center(cghub_center_name, dcc_center_id)
VALUES ('WUGSC' , 9);
COMMIT;

ALTER TABLE bam_file_datatype DROP COLUMN molecule;
DELETE FROM bam_file_datatype;
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (1,'WGS','Genome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (2,'WCS','Genome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (3,'CLONE','Genome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (4,'POOLCLONE','Genome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (5,'AMPLICON','Genome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (6,'CLONEEND','Genome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (7,'FINISHING','Genome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (8,'WXS','Exome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (9,'EST','Exome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (10,'FL-cDNA','Exome');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (11,'RNA-Seq','RNASeq');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (12,'miRNA-Seq',	'miRNA');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (13,'FINISHING','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (14,'ChIP-Seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (15,'MNase-Seq',	'Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (16,'DNaseHypersensitivity',	'Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (17,'Bisulfite-Seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (18,'CTS','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (19,'MRE-Seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (20,'MeDIP-Seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (21,'MDB-Seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (22,'VALIDATION','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (23,'ncRNA-Seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (24,'Tn-Seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (25,'FAIRE-seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (26,'SELEX','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (27,'RIP-Seq','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (28,'ChIA-PET','Unknown');
INSERT INTO bam_File_datatype (bam_datatype_id, bam_datatype, general_datatype)
VALUES (29,'OTHER','Unknown');

COMMIT;

update data_type set name='CNV (CN Array)' where name='CNV (Array)';
update data_type set name='CNV (SNP Array)' where name='CNV (SNP)';

COMMIT;

Insert into disease (disease_id,disease_abbreviation,disease_name, active)
Values (40,'TGCT','Testicular Germ Cell Tumors',1);
commit;

purge recyclebin;