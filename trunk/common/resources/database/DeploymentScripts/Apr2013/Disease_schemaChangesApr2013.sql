ALTER TABLE biospecimen_to_file DROP CONSTRAINT fk_biospecimen_file_file;
ALTER TABLE biospecimen_to_file ADD 
  (CONSTRAINT fk_biospecimen_file_file 
   FOREIGN KEY (file_id)
   REFERENCES file_info(file_id)
   ON DELETE CASCADE
);

ALTER TABLE file_to_archive DROP CONSTRAINT fk_filetoarchive_fileid;
ALTER TABLE file_to_archive ADD (
   CONSTRAINT fk_filetoarchive_fileid 
   FOREIGN KEY (file_id)
   REFERENCES file_info (file_id)
   ON DELETE CASCADE
);

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

/*
** make a copy of the  maf_info table before starting
*/
CREATE TABLE maf_info_save AS SELECT * FROM maf_info;

/*
** get rid of duplicate rows caused by same file being loaded more than once
*/

DELETE FROM maf_info
WHERE rowid IN
   (SELECT rid
    FROM (SELECT rowid rid,row_number() OVER (PARTITION BY 
        center_id,
        file_id,
        hugo_symbol,
        entrez_gene_id,
        ncbi_build,
        chrom,
        start_position,
        end_position,
        strand,
        variant_classification,
        variant_type,
        reference_allele,
        tumor_seq_allele1,
        tumor_seq_allele2,
        dbsnp_rs,
        dbsnp_val_status,
        tumor_sample_barcode,
        match_norm_sample_barcode,
        match_norm_seq_allele1,
        match_norm_seq_allele2,
        tumor_validation_allele1,
        tumor_validation_allele2,
        match_norm_validation_allele1,
        match_norm_validation_allele2,
        verification_status,
        validation_status,
        mutation_status,
        validation_method,
        sequencing_phase,
        score,
        bam_file,
        sequencer,
        sequence_source
    order by rowid) rn
        FROM maf_info)
WHERE rn <> 1);
COMMIT;

-- delete rows for files associated with archives that are not latest.
DELETE FROM maf_info WHERE file_id IN 
(SELECT DISTINCT fa.file_id 
 FROM maf_info m,file_to_archive fa, archive_info a 
 WHERE m.file_id = fa.file_id 
 AND   fa.archive_id = a.archive_id
 AND   a.is_latest = 0);
COMMIT;
-- delete rows for files that do not have file_to_Archive associations
DELETE FROM maf_info WHERE file_id NOT IN 
(SELECT file_id FROM file_to_archive);
COMMIT;

DROP SEQUENCE maf_key_seq;
CREATE SEQUENCE maf_key_seq START WITH 1 INCREMENT BY 1;

DROP TABLE maf_key CASCADE CONSTRAINTS;
CREATE TABLE maf_key (
   maf_key_id            	NUMBER(38)	NOT NULL,
   entrez_gene_id        	NUMBER(38)      NOT NULL,
   center_id            	NUMBER(38)	NOT NULL,
   ncbi_build			VARCHAR2(15)	NOT NULL,
   chrom			VARCHAR2(25)    NOT NULL,
   start_position		NUMBER(38)	NOT NULL,
   end_position			NUMBER(38)	NOT NULL,
   strand			VARCHAR2(2)	NOT NULL,
   tumor_sample_uuid		VARCHAR2(36)    NOT NULL,
   match_norm_sample_uuid	VARCHAR2(36)    NOT NULL,
   tumor_sample_barcode		VARCHAR2(50)    NOT NULL,
   match_norm_sample_barcode	VARCHAR2(50)    NOT NULL,
   CONSTRAINT maf_key_pk_idx PRIMARY KEY (maf_key_id)
);

CREATE UNIQUE INDEX maf_key_key_uk_idx 
ON maf_key (
      entrez_gene_id,
      center_id    ,
      ncbi_build,
      chrom,
      start_position,
      end_position,
      strand,
      tumor_sample_uuid,
      match_norm_sample_uuid);
      
ALTER TABLE maf_key ADD CONSTRAINT fk_maf_key_center FOREIGN KEY (center_id) REFERENCES center (center_id) ON DELETE CASCADE;

ALTER TABLE maf_info ADD (maf_key_id NUMBER(38), line_num INTEGER);

ALTER TABLE maf_info MODIFY (
     hugo_symbol         	VARCHAR2(50),
     ncbi_build          	VARCHAR2(15),
     variant_classification     VARCHAR2(35),
     variant_type        	VARCHAR2(20),
     dbSNP_RS            	VARCHAR2(15),
     dbSNP_val_status        	VARCHAR2(120),
     verification_status    	VARCHAR2(25),
     validation_status        	VARCHAR2(25),
     mutation_status        	VARCHAR2(40),
     sequencing_phase        	VARCHAR2(25),
     sequence_source        	VARCHAR2(25),
     validation_method        	VARCHAR2(250),
     score            		VARCHAR2(10),
     bam_file            	VARCHAR2(35),
     sequencer            	VARCHAR2(250),
     tumor_sample_barcode	VARCHAR2(50),
     match_norm_sample_barcode  VARCHAR2(50));
     
INSERT INTO maf_key (
    maf_key_id,
    entrez_gene_id,
    center_id    ,
    ncbi_build,
    chrom,
    start_position,
    end_position,
    strand,
    tumor_sample_barcode,
    match_norm_sample_barcode,
    tumor_sample_uuid,
    match_norm_sample_uuid)
SELECT  
    maf_key_seq.NEXTVAL,
    entrez_gene_id,
    center_id    ,
    ncbi_build,
    chrom,
    start_position,
    end_position,
    strand,
    tumor_sample_barcode,
    match_norm_sample_barcode,
    tumor_sample_uuid,
    match_norm_sample_uuid
FROM (
    SELECT DISTINCT 
       entrez_gene_id,
       center_id    ,
       ncbi_build,
       chrom,
       start_position,
       end_position,
       strand,
       tumor_sample_barcode,
       match_norm_sample_barcode,
       tumor_sample_barcode as tumor_sample_uuid,
       match_norm_sample_barcode as match_norm_sample_uuid
    FROM maf_info);
 
    
UPDATE maf_key m
   SET tumor_sample_uuid = (SELECT DISTINCT b.uuid
                            FROM   dcccommon.barcode_history b
                            WHERE  b.barcode = m.tumor_sample_barcode);
COMMIT;
                            
UPDATE maf_key m
   SET match_norm_sample_uuid = (SELECT DISTINCT b.uuid
                                 FROM   dcccommon.barcode_history b
                                 WHERE  b.barcode = m.match_norm_sample_barcode);
COMMIT;

-- populate the maf_key_id in the maf_info table
UPDATE maf_info m
   SET maf_key_id = (SELECT k.maf_key_id
                     FROM   maf_key k
                     WHERE  k.entrez_gene_id = m.entrez_gene_id
                     AND    k.center_id = m.center_id
                     AND    k.ncbi_build = m.ncbi_build
                     AND    k.chrom = m.chrom
                     AND    k.start_position = m.start_position
                     AND    k.end_position = m.end_position
                     AND    k.strand = m.strand
                     AND    k.tumor_sample_barcode = m.tumor_sample_barcode
                     AND    k.match_norm_sample_barcode = m.match_norm_sample_barcode);
COMMIT;
ALTER TABLE maf_info ADD (
   CONSTRAINT fk_maf_info_key
   FOREIGN KEY (maf_key_id)
   REFERENCES maf_key (maf_key_id)
);


-- remove the columns from maf_info that went into the maf_key table
ALTER TABLE maf_info DROP COLUMN entrez_gene_id;
ALTER TABLE maf_info DROP COLUMN center_id;
ALTER TABLE maf_info DROP COLUMN ncbi_build;
ALTER TABLE maf_info DROP COLUMN chrom;
ALTER TABLE maf_info DROP COLUMN start_position;
ALTER TABLE maf_info DROP COLUMN end_position;
ALTER TABLE maf_info DROP COLUMN strand;

ALTER TABLE maf_key DROP COLUMN tumor_sample_barcode;
ALTER TABLE maf_key DROP COLUMN match_norm_sample_barcode;

CREATE INDEX maf_key_center_idx ON maf_key(center_id);