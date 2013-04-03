DROP TABLE gene CASCADE CONSTRAINTS;
DROP TABLE drug CASCADE CONSTRAINTS;
DROP TABLE pathway CASCADE CONSTRAINTS;
DROP TABLE anomaly_type CASCADE CONSTRAINTS;
DROP TABLE gene_drug CASCADE CONSTRAINTS;
DROP TABLE biocarta_gene_pathway CASCADE CONSTRAINTS;
DROP TABLE summary_by_gene CASCADE CONSTRAINTS;
DROP TABLE biocarta_gene CASCADE CONSTRAINTS;
DROP TABLE drug_concept_code CASCADE CONSTRAINTS;

CREATE TABLE gene (
  gene_id INTEGER NOT NULL,  -- official Entrez ID integer
  entrez_symbol  VARCHAR(50)  NOT NULL,
  comments	 VARCHAR(1000)
);
ALTER TABLE gene ADD CONSTRAINT gene_pk PRIMARY KEY (gene_id);
ALTER TABLE gene ADD CONSTRAINT gene_enzsym_uk UNIQUE (entrez_symbol);
COMMENT ON TABLE gene IS 'Stores relevant entrez gene information';
COMMENT ON COLUMN gene.gene_id IS 'The ENTREZ Gene ID. Primary Key.';
COMMENT ON COLUMN gene.entrez_symbol IS 'The ENTREZ gene symbol.  Alternate Key.';
COMMENT ON COLUMN gene.comments IS 'An area to store official comments about any caveats with the data.';

CREATE TABLE biocarta_gene (
   biocarta_gene_id  INTEGER  NOT NULL,  -- increment during single load of the data
   gene_id  INTEGER   NOT NULL,
   biocarta_symbol VARCHAR(50) NOT NULL,
   comments  VARCHAR(1000)
);
ALTER TABLE biocarta_gene ADD CONSTRAINT biocarta_gene_pk PRIMARY KEY (biocarta_gene_id);
ALTER TABLE biocarta_gene ADD CONSTRAINT biocarta_gene_bcsym_uk UNIQUE (biocarta_symbol);
COMMENT ON TABLE biocarta_gene IS 'Stores relevant biocarta gene information';
COMMENT ON COLUMN biocarta_gene.biocarta_gene_id IS 'An internal, information-free integer used as a Primary Key';
COMMENT ON COLUMN biocarta_gene.gene_id IS 'Foreign Key.';
COMMENT ON COLUMN biocarta_gene.biocarta_symbol IS 'The symbol BioCarta uses to link genes to pathways. Alternate Key.';
COMMENT ON COLUMN biocarta_gene.comments IS 'An area to store official comments about any caveats with the data.';


CREATE TABLE drug (
   drug_id  INTEGER NOT NULL,  -- increment during single load of the data
   name  VARCHAR(100) NOT NULL,
   comments	 VARCHAR(1000)
);
ALTER TABLE drug ADD CONSTRAINT drug_pk PRIMARY KEY (drug_id);
ALTER TABLE drug ADD CONSTRAINT drug_name_uk UNIQUE (name);
COMMENT ON TABLE drug IS 'Stores basic information on drugs involved in cancer treatments for genes in this database.';
COMMENT ON COLUMN drug.drug_id IS 'An internal, information-free integer used as a Primary Key';
COMMENT ON COLUMN drug.name IS 'The drug name, occaisonally not a great name.  Alternate Key';
COMMENT ON COLUMN drug.comments IS 'An area to store official comments about any caveats with the data.';

CREATE TABLE drug_concept_code (
   drug_concept_code_id  INTEGER NOT NULL, -- increment during the single load of the data
   concept_code VARCHAR(50) NOT NULL,
   drug_id INTEGER NOT NULL,
   comments varchar(1000)
);
ALTER TABLE drug_concept_code ADD CONSTRAINT drug_ccode_pk PRIMARY KEY (drug_concept_code_id);
ALTER TABLE drug_concept_code ADD CONSTRAINT drug_ccode_uk UNIQUE (drug_id, concept_code);
COMMENT ON TABLE drug_concept_code IS 'Stores each concept code associated with the drug.  The concept code can be used to link out to the EVS.  Note: theoretically, there should be a 1:1 realationship.';
COMMENT ON COLUMN drug_concept_code.drug_concept_code_id IS 'An internal, information-free integer used as a Primary Key';
COMMENT ON COLUMN drug_concept_code.drug_id IS 'Foreign Key.';
COMMENT ON COLUMN drug_concept_code.concept_code is 'The NCI Thesaurus Drug Concept Code.';
COMMENT ON COLUMN drug_concept_code.comments IS 'An area to store official comments about any caveats with the data.';


CREATE TABLE pathway (
   pathway_id  INTEGER  NOT NULL,   -- increment during single load of the data
   svg_file_name  VARCHAR(255) NOT NULL,
   display_name  VARCHAR(255) NOT NULL,
   svg_identifier  VARCHAR(255) NOT NULL,
   comments	 VARCHAR(1000)
);
ALTER TABLE pathway ADD CONSTRAINT pathway_pk PRIMARY KEY (pathway_id);
ALTER TABLE pathway ADD CONSTRAINT pathway_svgfile_uk UNIQUE (svg_file_name);
ALTER TABLE pathway ADD CONSTRAINT pathway_display_uk UNIQUE (display_name);
ALTER TABLE pathway ADD CONSTRAINT pathway_svgid_uk UNIQUE (svg_identifier);
COMMENT ON TABLE pathway IS 'Stores relevant pathway information';
COMMENT ON COLUMN pathway.pathway_id IS 'An internal, information-free integer used as a Primary Key';
COMMENT ON COLUMN pathway.svg_file_name IS 'Name of the svg pathway file with out pathing information.  The pathing info should be stored as a variable on the system as it could vary.';
COMMENT ON COLUMN pathway.display_name IS 'The name of the pathway to be used in GUIs and reports.  The pretty name.';
COMMENT ON COLUMN pathway.svg_identifier IS 'The textual identifier for an SVG pathway.  It may or may not be the same as the file name.';
COMMENT ON COLUMN pathway.comments IS 'An area to store official comments about any caveats with the data.';


CREATE TABLE anomaly_type (
   anomaly_type_id  INTEGER  NOT NULL, -- increment manually, low-cardinality table
   anomaly  VARCHAR(255)  NOT NULL,
   anomaly_description  VARCHAR(255)  NOT NULL,
   value_threshold  FLOAT  NOT NULL,
   value_threshold_type VARCHAR(3) NOT NULL,
   display_name VARCHAR(10) NOT NULL,
   patient_threshold  FLOAT,
   comments	 VARCHAR(1000)
);
ALTER TABLE anomaly_type ADD CONSTRAINT anomaly_type_pk PRIMARY KEY (anomaly_type_id);
ALTER TABLE anomaly_type ADD CONSTRAINT anomaly_type_anomaly_uk UNIQUE (anomaly);
ALTER TABLE anomaly_type ADD CONSTRAINT anomaly_type_display_uk UNIQUE (display_name);
ALTER TABLE anomaly_type ADD CONSTRAINT anomaly_type_threhold_type_cc CHECK (value_threshold_type IN ('GT', 'GTE', 'LT', 'LTE', 'E', 'NE'));
COMMENT ON TABLE anomaly_type IS 'Stores meta-data on each type of anomaly';
COMMENT ON COLUMN anomaly_type.anomaly_type_id IS 'An internal, information-free integer used as a Primary Key';
COMMENT ON COLUMN anomaly_type.anomaly IS 'The name of the anomaly';
COMMENT ON COLUMN anomaly_type.display_name IS 'A short name for GUI displays if necessary';
COMMENT ON COLUMN anomaly_type.anomaly_description IS 'A breif description of the anomaly';
COMMENT ON COLUMN anomaly_type.value_threshold IS 'The cutoff provided by CBIIT that was applied to the data to determine if the anomolgy exists in a certain gene.';
COMMENT ON COLUMN anomaly_type.value_threshold_type IS 'The comparision operator for the value_cutoff.  GT: greater-than  GTE: greater-than-or-equal-to  LT: less-than  LTE: less-than-or-equal-to  E: equals  NE: not-equal-to';
COMMENT ON COLUMN anomaly_type.patient_threshold IS 'The suggested threshold to apply to summary_by_gene.cases_detected/summary_by_gene.cases_probed.  It is assumed to be greater-than-or-equal-to this threshold.';
COMMENT ON COLUMN anomaly_type.comments IS 'An area to store official comments about any caveats with the data.';


CREATE TABLE gene_drug (
   gene_id  INTEGER  NOT NULL,
   drug_id  INTEGER  NOT NULL
);
ALTER TABLE gene_drug ADD CONSTRAINT gene_drug_pk PRIMARY KEY (gene_id, drug_id);
COMMENT ON TABLE gene_drug IS 'Relates Entrez genes to any agents they are affected by or agents they affect.  This is a many-to-many relationship.';
COMMENT ON COLUMN gene_drug.gene_id IS 'Foreign Key';
COMMENT ON COLUMN gene_drug.drug_id IS 'Foreign Key';

CREATE TABLE biocarta_gene_pathway (
   biocarta_gene_id  INTEGER  NOT NULL,
   pathway_id  INTEGER  NOT NULL
);
ALTER TABLE biocarta_gene_pathway ADD CONSTRAINT gene_pathway_pk PRIMARY KEY (biocarta_gene_id, pathway_id);
COMMENT ON TABLE biocarta_gene_pathway IS 'Relates all BioCarta genes to their pathways.  This is a many-to-many relationship';
COMMENT ON COLUMN biocarta_gene_pathway.biocarta_gene_id IS 'Foreign Key.';
COMMENT ON COLUMN biocarta_gene_pathway.pathway_id IS 'Foreign Key.';


CREATE TABLE summary_by_gene (
   gene_id  INTEGER  NOT NULL,
   anomaly_type_id  INTEGER  NOT NULL,
   cases_probed  INTEGER  NOT NULL,
   cases_detected  INTEGER  NOT NULL,
   comments	 VARCHAR(1000)
);
ALTER TABLE summary_by_gene ADD CONSTRAINT summary_by_gene_pk PRIMARY KEY (gene_id, anomaly_type_id);
ALTER TABLE summary_by_gene ADD CONSTRAINT summary_by_gene_cases_cc CHECK (cases_probed >= cases_detected);
COMMENT ON TABLE summary_by_gene IS 'Stores the gene-level summary information, rolled-up by cases/patients, on the various anomalies';
COMMENT ON COLUMN summary_by_gene.gene_id IS 'Foreign Key';
COMMENT ON COLUMN summary_by_gene.anomaly_type_id IS 'Foreign Key';
COMMENT ON COLUMN summary_by_gene.cases_probed IS 'The total number of cases/patients tested/probed for the anomaly.';
COMMENT ON COLUMN summary_by_gene.cases_detected IS 'The total number of cases/patients in which the anomaly is found.';
COMMENT ON COLUMN summary_by_gene.comments IS 'An area to store official comments about any caveats with the data.';


ALTER TABLE biocarta_gene ADD CONSTRAINT biocarta_gene_gene_fk FOREIGN KEY (gene_id) REFERENCES gene;
ALTER TABLE gene_drug ADD CONSTRAINT gene_drug_drug_fk FOREIGN KEY (drug_id) REFERENCES drug;
ALTER TABLE gene_drug ADD CONSTRAINT gene_drug_gene_pk FOREIGN KEY (gene_id) REFERENCES gene;
ALTER TABLE biocarta_gene_pathway ADD CONSTRAINT gene_pathway_pathway_fk FOREIGN KEY (pathway_id) REFERENCES pathway;
ALTER TABLE biocarta_gene_pathway ADD CONSTRAINT gene_pathway_gene_fk FOREIGN KEY (biocarta_gene_id) REFERENCES biocarta_gene;
ALTER TABLE summary_by_gene ADD CONSTRAINT summary_by_gene_gene_fk FOREIGN KEY (gene_id) REFERENCES gene;
ALTER TABLE summary_by_gene ADD CONSTRAINT s_b_g_anomaly_type_fk FOREIGN KEY (anomaly_type_id) REFERENCES anomaly_type;
ALTER TABLE drug_concept_code ADD CONSTRAINT drug_concept_code_drug_fk FOREIGN KEY (drug_id) REFERENCES drug;

INSERT INTO anomaly_type values (1, 'MUTATION', 'A mutation exists in the gene of interest as seen by sequence analysis.', 1, 'GTE', 'MUTATION', .05, null);
INSERT INTO anomaly_type values (2, 'OVEREXPRESSION, AFFYMETRIX', 'Over-expression of the gene is seen in Affymetrix 133A Microarray analysis.', 2, 'GTE', 'EXP+_AF', 0.25, null);
INSERT INTO anomaly_type values (3, 'UNDEREXPRESSION, AFFYMETRIX', 'Under-expression of the gene is seen in Affymetrix 133A Microarray analysis.', 0.5, 'LTE', 'EXP-_AF', 0.15, null);
INSERT INTO anomaly_type values (4, 'AMPLIFIED', 'Amplification of all or part of the gene, SNP6.0 input', 2.5, 'GTE', 'AMP', 0.1, null);
INSERT INTO anomaly_type values (5, 'DELETED', 'Deletion of all or part of the gene, SNP6.0 input', 1.5, 'LTE', 'DEL', 0.1, null);



CREATE or REPLACE VIEW pathway_to_gene
AS
SELECT g.gene_id AS entrez_id,
   g.comments AS gene_comments,
   g.entrez_symbol,
   bg.biocarta_symbol,
   bg.comments AS biocartagene_comments,
   p.svg_file_name,
   p.svg_identifier,
   p.display_name AS svg_display_name,
   p.comments AS pathway_comments
FROM gene g,
   biocarta_gene bg,
   biocarta_gene_pathway bgp,
   pathway p
WHERE g.gene_id = bg.gene_id
AND   bg.biocarta_gene_id = bgp.biocarta_gene_id
AND   bgp.pathway_id = p.pathway_id
WITH READ ONLY;
COMMENT ON TABLE pathway_to_gene IS 'Relates pathways directly to Entrez genes.  This view is intended to be used by developers.';


CREATE or REPLACE VIEW sbg_mart
AS
SELECT g.gene_id AS entrez_id,
   bg.biocarta_symbol,
   p.svg_file_name,
   p.svg_identifier,
   p.display_name AS svg_display_name,
   d.name AS drug_name,
   dcc.concept_code AS drug_concept_code,
   COALESCE(ROUND((s_affyUP.cases_detected/s_affyUP.cases_probed), 4), null) AS overexpressed_affy,
   COALESCE(ROUND((s_affyDOWN.cases_detected/s_affyDOWN.cases_probed), 4), null) AS underexpressed_affy,
   COALESCE(ROUND((mutation.cases_detected/mutation.cases_probed), 4), null) AS mutated,
   COALESCE(ROUND((amplified.cases_detected/amplified.cases_probed), 4), null) AS amplified,
   COALESCE(ROUND((deleted.cases_detected/deleted.cases_probed), 4), null) AS deleted,
   (CASE WHEN s_affyUP.cases_detected/s_affyUP.cases_probed >= a_affyUP.patient_threshold THEN 'TRUE' ELSE 'FALSE' END) AS is_overexpressed_affy,
   (CASE WHEN s_affyDOWN.cases_detected/s_affyDOWN.cases_probed >= a_affyDOWN.patient_threshold THEN 'TRUE' ELSE 'FALSE' END) AS is_underexpressed_affy,
   (CASE WHEN mutation.cases_detected/mutation.cases_probed >= a_mutated.patient_threshold THEN 'TRUE' ELSE 'FALSE' END) AS is_mutated,
   (CASE WHEN amplified.cases_detected/amplified.cases_probed >= a_amplified.patient_threshold THEN 'TRUE' ELSE 'FALSE' END) AS is_amplified,
   (CASE WHEN deleted.cases_detected/deleted.cases_probed >= a_deleted.patient_threshold THEN 'TRUE' ELSE 'FALSE' END) AS is_deleted
FROM gene g,
   biocarta_gene bg,
   biocarta_gene_pathway bgp,
   pathway p,
   gene_drug gd,
   drug d,
   drug_concept_code dcc,
   summary_by_gene s_affyUP,
   summary_by_gene s_affyDOWN,
   summary_by_gene mutation,
   summary_by_gene amplified,
   summary_by_gene deleted,
   anomaly_type a_affyUP,
   anomaly_type a_affyDOWN,
   anomaly_type a_mutated,
   anomaly_type a_amplified,
   anomaly_type a_deleted
WHERE g.gene_id = bg.gene_id
AND   bg.biocarta_gene_id = bgp.biocarta_gene_id(+)
AND   bgp.pathway_id = p.pathway_id(+)
AND   g.gene_id = gd.gene_id(+)
AND   gd.drug_id = d.drug_id(+)
AND   d.drug_id = dcc.drug_id(+)
AND   g.gene_id = s_affyUP.gene_id(+)
AND   g.gene_id = s_affyDOWN.gene_id(+)
AND   g.gene_id = mutation.gene_id(+)
AND   g.gene_id = amplified.gene_id(+)
AND   g.gene_id = deleted.gene_id(+)
AND   s_affyUP.anomaly_type_id = a_affyUP.anomaly_type_id(+)
AND   s_affyDOWN.anomaly_type_id = a_affyDOWN.anomaly_type_id(+)
AND   mutation.anomaly_type_id = a_mutated.anomaly_type_id(+)
AND   deleted.anomaly_type_id = a_deleted.anomaly_type_id(+)
AND   amplified.anomaly_type_id = a_amplified.anomaly_type_id(+)
AND   s_affyUP.anomaly_type_id(+) = 2
AND   s_affyDOWN.anomaly_type_id(+) = 3
AND   mutation.anomaly_type_id(+) = 1
AND   amplified.anomaly_type_id(+) = 4
AND   deleted.anomaly_type_id(+) = 5
WITH READ ONLY;
COMMENT ON TABLE sbg_mart IS 'Denormalizes all tables except those pertaining to drugs/agents.  This is akin to a mini-datamart.  This view is not meant to be used by developers, but for ad hoc queries.';


CREATE OR REPLACE VIEW sbg_patient_percentage
AS
select anomaly, sum(case when patientRatio  >= 0.01 then cg else 0 end) one,
	sum(case when patientRatio >= 0.02 then cg else 0 end) two,
	sum(case when patientRatio >= 0.03 then cg else 0 end) three,
	sum(case when patientRatio >= 0.04 then cg else 0 end) four,
	sum(case when patientRatio >= 0.05 then cg else 0 end) five,
	sum(case when patientRatio >= 0.06 then cg else 0 end) six,
	sum(case when patientRatio >= 0.07 then cg else 0 end) seven,
	sum(case when patientRatio >= 0.08 then cg else 0 end) eight,
	sum(case when patientRatio >= 0.09 then cg else 0 end) nine,
	sum(case when patientRatio >= 0.1 then cg else 0 end) ten,
	sum(case when patientRatio >= 0.2 then cg else 0 end) twenty,
	sum(case when patientRatio >= 0.3 then cg else 0 end) thirty,
	sum(case when patientRatio >= 0.4 then cg else 0 end) forty,
	sum(case when patientRatio >= 0.5 then cg else 0 end) fifty,
	sum(case when patientRatio >= 0.6 then cg else 0 end) sixty,
	sum(case when patientRatio >= 0.7 then cg else 0 end) seventy,
	sum(case when patientRatio >= 0.8 then cg else 0 end) eighty,
	sum(case when patientRatio >= 0.9 then cg else 0 end) ninety,
	sum(case when patientRatio >= 1 then cg else 0 end) hundred
from
	(select anomaly, count(gene_id) cg, round(cases_detected/cases_probed, 2) as patientRatio
		from summary_by_gene sbg, anomaly_type at
		where sbg.anomaly_type_id = at.anomaly_type_id
		group by anomaly, round(cases_detected/cases_probed, 2))
group by anomaly
WITH READ ONLY;
COMMENT ON TABLE sbg_patient_percentage IS 'Consecutively sums the number of genes determined to be anomalous as the ''pateint threshold'' increases in pre-defined increments.  This view performs the analysis for each anomaly.  It is not intended to be used by developers, rather it is to be used in reporting and ''patient threshold'' decision making.';

CREATE OR REPLACE VIEW number_anomalies_gene
AS
select entrez_id, (DECODE(IS_OVEREXPRESSED_AFFY, 'TRUE',1,0) +
		DECODE(IS_UNDEREXPRESSED_AFFY, 'TRUE',1,0) +
		DECODE(IS_MUTATED, 'TRUE',1,0) +
		DECODE(IS_AMPLIFIED, 'TRUE',1,0)  +
		DECODE(IS_DELETED, 'TRUE',1,0)) as anomaly_number
from
	(select distinct entrez_id,
		is_overexpressed_affy,
		is_underexpressed_affy,
		is_mutated,
		is_amplified,
		is_deleted
	from sbg_mart) x
WITH READ ONLY;
COMMENT ON TABLE number_anomalies_gene IS 'Shows the number of anomalies per gene in order to determine good examples for demonstrations, etc.  This view is not meant to be used by developers.';