WHENEVER SQLERROR EXIT ;
/* turn on timing to see how long each step takes */
SET TIMING ON;
/* 
** create the new sequence cache a big number (a billion at a time since we know
** there are 11 billion or so records) to improve performance on the 
** table create as select 
*/
create SEQUENCE hyb_value_id_seq start with 1 CACHE 1000000000;
/* 
** create a new, un-partitioned table, with logging turned off;
** get all rows from hybridization_value into the new table, populating hybridization_value_id from
** the sequence, and putting zero's in platform_id and getting the new hybridization_ref_id from the 
** hyb_ref_map table
*/
ALTER SESSION FORCE PARALLEL DML;

CREATE TABLE hybridization_value_new 
TABLESPACE TCGAPORTAL_TMP
NOLOGGING
AS SELECT 
       hyb_value_id_seq.NEXTVAL AS hybridization_value_id,
       0 as platform_id,
       new_hyb_ref_id as hybridization_ref_id,
       hybridization_data_group_id,
       composite_element_id,
       value
From Hybridization_Value h, hyb_ref_map r
Where h.hybridization_ref_id = r.old_hyb_ref_id;

/*
** create indexes to make next join faster
*/
ALTER SESSION FORCE PARALLEL DDL;
CREATE INDEX hyb_value_new_comp_elem_idx on hybridization_value_new(composite_element_id)
TABLESPACE TCGAPORTAL_TMP
NOLOGGING;
-- about 4.43 hours dev
 
/* 
** create a second new table with ctas bringing together probes
*/
CREATE TABLE hyb_val_new_new
TABLESPACE tcgaportal_tmp
nologging
AS 
SELECT 
      h.hybridization_value_id,
      m.platform_id,
      h.hybridization_ref_id,
      h.hybridization_data_group_id,
      m.probe_id,
      value
From Hybridization_Value_new h, composite_element_probe_map m
Where h.composite_element_id = m.composite_element_id

/* 
** create a third new, partitioned table parallel
*/


CREATE TABLE hybridization_value_new_part (
   hybridization_value_id      NUMBER(38) NOT NULL,
   platform_id                 NUMBER(38) NOT NULL,
   hybridization_ref_id        NUMBER(38) NOT NULL,
   hybridization_data_group_id NUMBER(38) NOT NULL,
   probe_id                    NUMBER(38) NOT NULL,
   value                       VARCHAR2(255) NOT NULL)
PARTITION BY LIST(platform_id)
(
  PARTITION Genome_Wide_SNP_6 VALUES(1),
  PARTITION IlluminaDNAMethylation_OMA2CPI VALUES(2),
  PARTITION IlluminaDNAMethylation_OMA3CPI VALUES(3),
  PARTITION HT_HG_U133A VALUES(4),
  PARTITION HGCGH_244A VALUES(5),
  PARTITION HuEx_1_0_st_v2 VALUES(6),
  PARTITION HumanHap550 VALUES(7),
  PARTITION AgilentG4502A_07_OLD VALUES(8, 10, 14),
  PARTITION HmiRNA_8x15K_OLD VALUES(12),
  PARTITION HumanMethylation27 VALUES(13),
  PARTITION CGH1x1M_G4447A VALUES(15),
  PARTITION Human1MDuo VALUES(16),
  PARTITION ABI VALUES(17),
  PARTITION AgilentG4502A_07 VALUES(18),
  PARTITION HmiRNA_8x15Kv2 VALUES(20),
  PARTITION HGCGH_415K_G4124A VALUES(21)
 )
ENABLE ROW MOVEMENT
 PARALLEL, 
 NOLOGGING
AS
SELECT 
      hybridization_value_id,
      platform_id,
      hybridization_ref_id,
      hybridization_data_group_id,
      probe_id,
      value
From Hybridization_Value_new_new ;


ALTER TABLE hybridization_value RENAME TO hybridization_value_old;
ALTER TABLE hybridization_value_new_part RENAME TO hybridization_value;

/* turn parallel ddl on for the session */
ALTER SESSION FORCE PARALLEL DDL ;
 
/* 
** create a unique local index that used to be the PK and include platform_id because it is
** the partition key
*/
CREATE UNIQUE INDEX hyb_value_ref_idx ON 
hybridization_value (hybridization_data_group_id,hybridization_ref_id,probe_id,platform_id) 
LOCAL 
NOLOGGING;
-- 8.10 hours dev

/*
** create a new hyb ref to data_set relationship table; do this here so that we will only get
** relationships that are valid
*/
CREATE TABLE hybrid_ref_data_set (
   hybref_dataset_id number(38) NOT NULL,
   hybridization_ref_id number(38) NOT NULL,
   data_set_id number(38) NOT NULL, 
   hybridization_ref_name VARCHAR2(70))
;
CREATE SEQUENCE hybref_dataset_seq START WITH 1 INCREMENT BY 1
;
/*
** insert all the distinct hyb ref id/data_set id pairs based on the level3 tables
** so we get only those that have data
*/
INSERT INTO hybrid_ref_data_set (hybref_dataset_id, hybridization_ref_id, data_set_id)
SELECT hybref_dataset_seq.NEXTVAL, hybridization_ref_id,data_set_id
FROM (SELECT distinct hybridization_ref_id,data_set_id
      FROM   expgene_value);
COMMIT;

INSERT INTO hybrid_ref_data_set (hybref_dataset_id, hybridization_ref_id, data_set_id)
SELECT hybref_dataset_seq.NEXTVAL, hybridization_ref_id,data_set_id
FROM (SELECT distinct hybridization_ref_id,data_set_id
      FROM   cna_value);
COMMIT;

INSERT INTO hybrid_ref_data_set (hybref_dataset_id, hybridization_ref_id, data_set_id)
SELECT hybref_dataset_seq.NEXTVAL, hybridization_ref_id,data_set_id
FROM (SELECT distinct hybridization_ref_id,data_set_id
      FROM methylation_value); 
COMMIT;
/*
** insert all the distinct hyb ref id/data_set id pairs from the hyb value table joined 
** with the hyb data group table so we get those that have data
*/

INSERT INTO hybrid_ref_data_set (hybref_dataset_id, hybridization_ref_id, data_set_id)
SELECT hybref_dataset_seq.NEXTVAL, hybridization_ref_id,data_set_id
FROM (SELECT distinct h.hybridization_ref_id,dg.data_set_id
      FROM hybridization_value h, hybridization_data_group dg
      WHERE h.hybridization_data_group_id = dg.hybridization_data_group_id);
COMMIT;

ALTER TABLE hybrid_ref_data_set ADD (
  CONSTRAINT pk_hybref_data_set PRIMARY KEY (hybref_dataset_id),
  CONSTRAINT uk_hybref_dataset_id UNIQUE (hybridization_ref_id,data_set_id));

/*
** now get the old hyb ref names from the old hyb ref table base on the table that maps the old 
** hyb ref id's to the new hyb ref id's
*/
UPDATE hybrid_ref_data_set hd
SET hybridization_ref_name =
(SELECT DISTINCT hybridization_ref_name 
 FROM   hybridization_ref_old o, hybridization_ref_map m
 WHERE  o.hybridization_ref_id = m.old_hyb_ref_id
 AND    m.new_hyb_ref_id = hd.hybridization_ref_id)
;
COMMIT
;
ALTER TABLE hybrid_ref_data_set ADD (
  CONSTRAINT fk_hybref_dataset_hybref
  FOREIGN KEY (hybridization_ref_id)
  REFERENCES hybridization_ref(hybridization_ref_id),
  CONSTRAINT fk_hybref_dataset_dataset
  FOREIGN KEY (data_set_id)
  REFERENCES data_set(data_set_id));

CREATE INDEX hybref_dataset_dataset_idx ON hybrid_ref_data_set(data_set_id);


/* 
** Set LOGGING ON for both new indices and the table, since it was created with 
** NOLOGGING. It has to explicitly be set back to LOGGING or it will
** continue to not be logged
*/
--ALTER INDEX pk_hyb_value_idx LOGGING;


ALTER INDEX hyb_value_ref_idx LOGGING;


ALTER TABLE hybridization_value LOGGING
;

ALTER TABLE probe LOGGING
;

ALTER INDEX pk_probe_idx LOGGING
;
/*
** alter the sequence to cache a more reasonable number now
*/
ALTER SEQUENCE hyb_value_id_seq CACHE 20
;
ALTER SEQUENCE probe_id_seq CACHE 20
;
/*
** create a partitioned materialized view, for just publicly-viewable hybridization_value records
*/

CREATE MATERIALIZED VIEW hybridization_value_mv 
PARTITION BY LIST(platform_id)
(
  PARTITION Genome_Wide_SNP_6 VALUES(1),
  PARTITION IlluminaDNAMethylation_OMA2CPI VALUES(2),
  PARTITION IlluminaDNAMethylation_OMA3CPI VALUES(3),
  PARTITION HT_HG_U133A VALUES(4),
  PARTITION HGCGH_244A VALUES(5),
  PARTITION HuEx_1_0_st_v2 VALUES(6),
  PARTITION HumanHap550 VALUES(7),
  PARTITION AgilentG4502A_07_OLD VALUES(8, 10, 14),
  PARTITION HmiRNA_8x15K_OLD VALUES(12),
  PARTITION HumanMethylation27 VALUES(13),
  PARTITION CGH1x1M_G4447A VALUES(15),
  PARTITION Human1MDuo VALUES(16),
  PARTITION ABI VALUES(17),
  PARTITION AgilentG4502A_07 VALUES(18),
  PARTITION HmiRNA_8x15Kv2 VALUES(20),
  PARTITION HGCGH_415K_G4124A VALUES(21)
 )
ENABLE ROW MOVEMENT
PARALLEL 2
REFRESH FORCE
AS 
SELECT h.* 
FROM hybridization_value h , hybridization_data_group dg, data_set d
WHERE h.hybridization_data_group_id = dg.hybridization_data_group_id
AND   dg.data_set_id = d.data_set_id
AND   d.access_level = 'PUBLIC';

-- 2 hours dev