ALTER TABLE rnaseq_value add (
normalized_counts	FLOAT,
scaled_estimate 	VARCHAR2(50),
transcript_id 		VARCHAR2(4000));

ALTER TABLE rnaseq_value MODIFY (raw_counts null);

INSERT INTO data_type(data_type_id,name,center_type_code,ftp_display,available,sort_order)
VALUES
(38,'RNASeqV2','CGCC','rnaseqV2',1,38);

INSERT INTO platform(platform_id,platform_name,platform_display_name,platform_alias,center_type_code,sort_order,available,base_data_Type_id)
VALUES
(57,'IlluminaGA_RNASeqV2','Illumina Genome Analyzer RNA Sequencing Version 2 analysis','IlluminaGA_RNASeqV2','CGCC',57,1,38);
INSERT INTO platform(platform_id,platform_name,platform_display_name,platform_alias,center_type_code,sort_order,available,base_data_Type_id)
VALUES
(58,'IlluminaHiSeq_RNASeqV2','Illumina HiSeq 2000 RNA Sequencing Version 2 analysis','IlluminaHiSeq_RNASeqV2','CGCC',58,1,38);


INSERT INTO data_type_to_platform (data_type_platform_id,data_Type_id,platform_id) VALUES(129,38,57);
INSERT INTO data_type_to_platform (data_type_platform_id,data_Type_id,platform_id) VALUES(137,38,58);

INSERT INTO data_visibility (data_visibility_id,data_type_id,visibility_id,level_number) values (128,38,1,3);
INSERT INTO data_visibility (data_visibility_id,data_type_id,visibility_id,level_number) values (129,38,1,0);
commit;

ALTER TABLE shipped_biospecimen ADD (batch_id INTEGER);

MERGE INTO shipped_biospecimen sb
USING
(select distinct sb.shipped_biospecimen_id,a.serial_index
 from shipped_biospecimen sb, shipped_biospec_bcr_archive sba, archive_info a
 where sb.shipped_biospecimen_id = sba.shipped_biospecimen_id
 and   sba.archive_id = a.archive_id and a.is_latest = 1 order by 1) v
 ON (sb.shipped_biospecimen_id = v.shipped_biospecimen_id)
 WHEN MATCHED THEN UPDATE
 SET sb.batch_id = v.serial_index;
 commit;
-- these we had to do specifically because there were 2 batch id's connected with them; we took the latest
update shipped_biospecimen set batch_id=4 where shipped_biospecimen_id=9316;
update shipped_biospecimen set batch_id=4 where shipped_biospecimen_id=9326;
update shipped_biospecimen set batch_id=4 where shipped_biospecimen_id=9330;
commit;
MERGE INTO shipped_biospecimen sb
USING
(select distinct biospecimen_id,a.serial_index, max(a.date_added) as date_added
  from bcr_biospecimen_to_archive ba,archive_info a
  where ba.archive_id = a.archive_id
  and biospecimen_id in 
  (select shipped_biospecimen_id from shipped_biospecimen where batch_id is null)
    group by biospecimen_id,a.serial_index 
 order by 1) v
 ON (sb.shipped_biospecimen_id = v.biospecimen_id)
 WHEN MATCHED THEN UPDATE
 SET sb.batch_id = v.serial_index;
 commit;
 
-- this will only update anything in the TCGAKICH schema
update disease set active=1 where disease_abbreviation ='KICH';
commit;

