WHENEVER SQLERROR EXIT ;
/*
** create new hybridization_ref table 
*/
CREATE TABLE hybridization_ref_new (
hybridization_ref_id number(38) not null,
bestbarcode varchar2(50) not null, 
sample_name varchar2(50) not null,
aliquot_id number(38));

ALTER TABLE hybridization_ref_new ADD CONSTRAINT hybref_barcode_uk
UNIQUE (bestbarcode);

INSERT INTO hybridization_ref_new (hybridization_ref_id, bestbarcode, sample_name)
SELECT hybref_hybridization_seq.nextval as hybridization_ref_id, s.bestbarcode,s.sample_name
from (select distinct bestbarcode, sample_name
      from hybridization_ref) s;

ALTER TABLE hybridization_ref_new
ADD CONSTRAINT pk_hybridization_ref_idx
PRIMARY KEY (hybridization_ref_id);

/*
** create a table to map old hyb ref id's to new hyb ref id's
*/
CREATE TABLE hyb_ref_map (old_hyb_ref_id number(38), new_hyb_ref_id number(38),
   CONSTRAINT PK_HYB_ref_map PRIMARY KEY (old_hyb_ref_id , new_hyb_ref_id))
   ORGANIZATION INDEX
TABLESPACE TCGAPORTAL_TMP;

INSERT INTO hyb_ref_map 
SELECT DISTINCT h1.hybridization_ref_id,h2.hybridization_ref_id
FROM   hybridization_ref h1, hybridization_ref_new h2
WHERE h1.bestbarcode = h2.bestbarcode;

COMMIT;


/*
** now update the tables with hyb ref id's to have the new ones
*/
-- first drop foreign key constraints
ALTER TABLE expgene_value DROP CONSTRAINT FK_EXPGENE_VALUE_VALUE_HYBREF;

ALTER TABLE cna_value DROP CONSTRAINT FK_CNA_VALUE_HYBREF;

ALTER TABLE methylation_value DROP CONSTRAINT FK_METHYLATION_V_HYBRIDIZATION;

-- rename hybridization_ref_new
alter table  hybridization_ref rename to hybridization_ref_old;

alter table hybridization_ref_new rename to hybridization_ref;

ALTER SESSION FORCE PARALLEL DML;

UPDATE expgene_value e
SET  e.hybridization_ref_id =
(SELECT m.new_hyb_ref_id
 FROM   hyb_ref_map m
 WHERE  m.old_hyb_ref_id = e.hybridization_ref_id);

COMMIT;

--  11 hours dev, 52 hours qa
 
UPDATE cna_value e
SET  e.hybridization_ref_id =
(SELECT m.new_hyb_ref_id
 FROM   hyb_ref_map m
 WHERE  m.old_hyb_ref_id = e.hybridization_ref_id);

COMMIT;

-- 5 hours 11 mins dev ; 11 hours qa
UPDATE methylation_value e
SET  e.hybridization_ref_id =
(SELECT m.new_hyb_ref_id
 FROM   hyb_ref_map m
 WHERE  m.old_hyb_ref_id = e.hybridization_ref_id);

COMMIT;
 
-- 30 minutes dev; 1 hr 18 minutes qa
ALTER SESSION FORCE PARALLEL DDL;

ALTER INDEX METHYL_VALUE_HYBREFID_IDX REBUILD;

-- 39.12 seconds qa
ALTER INDEX CNA_VALUE_AK1 REBUILD;

-- 22.87 seconds qa
ALTER INDEX EXPGENE_VALUE_AK1 REBUILD;

-- 5.34 minutes qa
ALTER TABLE methylation_value ADD CONSTRAINT fk_meth_value_hybref_id
FOREIGN KEY (hybridization_ref_id)
REFERENCES hybridization_ref(hybridization_ref_id);

-- 29.33 seconds qa
ALTER TABLE cna_value ADD CONSTRAINT fk_cna_value_hybref_id
FOREIGN KEY (hybridization_ref_id)
REFERENCES hybridization_ref(hybridization_ref_id);

-- 22.81 seconds qa
ALTER TABLE expgene_value ADD CONSTRAINT fk_expgene_value_hybref_id
FOREIGN KEY (hybridization_ref_id)
REFERENCES hybridization_ref(hybridization_ref_id);

-- 2 minutes 53 seconds qa
-- *NOTE: hybridization_value will be updated when it is transformed in the next steps
