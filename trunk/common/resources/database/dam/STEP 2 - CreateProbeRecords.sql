WHENEVER SQLERROR EXIT ;
/* turn on timing to see how long each step takes */
SET TIMING ON;

/* 
** create the new sequence cache a big number (a million at a time since we know
** there are over 8 million records) to improve performance on the table create as select 
*/
CREATE SEQUENCE probe_id_seq START WITH 1 CACHE 10000000;


/* 
** create the new, partitioned table in parallel, with logging turned off
** do an outer join to get comp element constants since not all comp elements have
** constants and we want all of the comp elements
*/
CREATE TABLE Probe (
    probe_id       NUMBER(36)      NOT NULL,
    platform_id    NUMBER(38)      NOT NULL,
    probe_name     VARCHAR2(50)    NOT NULL,
    chromosome     VARCHAR2(50),
    start_position VARCHAR2(50),
    end_position   VARCHAR2(50),
    ncbi_gene_id   VARCHAR2(50))
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
 PARALLEL, NOLOGGING;

ALTER SESSION FORCE PARALLEL DML; 

ALTER SESSION FORCE PARALLEL QUERY ;

 
INSERT INTO Probe (probe_id,platform_id,probe_name)
 SELECT probe_id_seq.NEXTVAL as probe_id,
	sub.platform_id,
	sub.name as probe_name
FROM (SELECT DISTINCT d.platform_id, c.name
      FROM   composite_element c , data_set d
      WHERE  c.data_set_id = d.data_set_id 
      ) sub
;
COMMIT
;
-- 7,403,257 distinct probe names in composite_element (tcgadev)
-- 8,368,787 for 15 platforms

/* turn parallel ddl on for the session */
ALTER SESSION FORCE PARALLEL DDL ;
/*
** Create unique, global index for primary key on Probe table
*/
CREATE UNIQUE INDEX pk_probe_idx ON 
probe (probe_id) GLOBAL 
PARTITION BY HASH (probe_id)
NOLOGGING ;

/*
** Create the pk constraint instructing  Oracle to use index just created 
*/
ALTER TABLE probe
    ADD CONSTRAINT pk_prob_id PRIMARY KEY (probe_id)
    USING INDEX pk_probe_idx;


/*
** create a mapping table to map old composite_element_ids to new prob ids. this will be used
** later when we create the new hybridization_value table with probe_id instead of composite_element_id
** to set the probe_id
*/
CREATE TABLE composite_element_probe_map 
TABLESPACE TCGAPORTAL_TMP 
PARALLEL, nologging
AS 
   SELECT distinct p.platform_id, p.probe_id, c.composite_element_id
   FROM   probe p, composite_element c, data_set d
   WHERE  p.probe_name = c.name 
   and    c.data_set_id = d.data_set_id
   and    d.platform_id = p.platform_id
;

CREATE unique INDEX compelem_probe_map_idx on 
composite_element_probe_map (platform_id,composite_element_id,probe_id)
TABLESPACE TCGAPORTAL_TMP;

