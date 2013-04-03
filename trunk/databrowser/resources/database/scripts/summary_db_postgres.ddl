-- psql -U postgres -A -t -f SummaryDB.ddl
DROP TABLE summary.gene CASCADE;
DROP TABLE summary.drug CASCADE;
DROP TABLE summary.pathway CASCADE;
DROP TABLE summary.anomaly_type CASCADE;
DROP TABLE summary.gene_drug CASCADE;
DROP TABLE summary.biocarta_gene_pathway CASCADE;
DROP TABLE summary.summary_by_gene CASCADE;
DROP TABLE summary.biocarta_gene CASCADE;
DROP TABLE summary.drug_concept_code CASCADE;

CREATE TABLE summary.gene (
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

CREATE TABLE summary.biocarta_gene (
   biocarta_gene_id  INTEGER  NOT NULL,  -- increment during single load of the data
   gene_id  INTEGER   NOT NULL,
   biocarta_symbol VARCHAR(50) NOT NULL,
   comments  VARCHAR(1000)
);
ALTER TABLE biocarta_gene ADD CONSTRAINT biocarta_gene_pk PRIMARY KEY (biocarta_gene_id);
ALTER TABLE biocarta_gene ADD CONSTRAINT biocarta_gene_bcsym_uk UNIQUE (biocarta_symbol);
COMMENT ON TABLE biocarta_gene IS 'Stores relevant biocarta gene information';
COMMENT ON COLUMN biocarta_gene.biocarta_symbol IS 'The symbol BioCarta uses to link genes to pathways. Alternate Key.';
COMMENT ON COLUMN biocarta_gene.comments IS 'An area to store official comments about any caveats with the data.';


CREATE TABLE summary.drug (
   drug_id  INTEGER NOT NULL,  -- increment during single load of the data
   name  VARCHAR(100) NOT NULL,
   comments	 VARCHAR(1000)
);
ALTER TABLE drug ADD CONSTRAINT drug_pk PRIMARY KEY (drug_id);
ALTER TABLE drug ADD CONSTRAINT drug_name_uk UNIQUE (name);
COMMENT ON TABLE drug IS 'Stores basic information on drugs involved in cancer treatments for genes in this database.';
COMMENT ON COLUMN drug.drug_id IS 'Internal ID, not for consumption.';
COMMENT ON COLUMN drug.name IS 'The drug name, occaisonally not a great name.  Alternate Key';
COMMENT ON COLUMN drug.comments IS 'An area to store official comments about any caveats with the data.';

CREATE TABLE summary.drug_concept_code (
   drug_concept_code_id  INTEGER NOT NULL, -- increment during the single load of the data
   concept_code VARCHAR(50) NOT NULL,
   drug_id INTEGER NOT NULL,
   comments varchar(1000)
);
ALTER TABLE drug_concept_code ADD CONSTRAINT drug_ccode_pk PRIMARY KEY (drug_concept_code_id);
ALTER TABLE drug_concept_code ADD CONSTRAINT drug_ccode_uk UNIQUE (drug_id, concept_code);
COMMENT ON TABLE drug_concept_code IS 'Stores each concept code associated with the drug.  The concept code can be used to link out to the EVS.  Note: theoretically, there should be a 1:1 realationship.';
COMMENT ON COLUMN drug_concept_code.concept_code is 'The NCI Thesaurus Drug Concept Code.';
COMMENT ON COLUMN drug_concept_code.comments IS 'An area to store official comments about any caveats with the data.';


CREATE TABLE summary.pathway (
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
COMMENT ON COLUMN pathway.pathway_id IS 'Internal ID, not for consumption';
COMMENT ON COLUMN pathway.svg_file_name IS 'Name of the svg pathway file with out pathing information.  The pathing info should be stored as a variable on the system as it could vary.';
COMMENT ON COLUMN pathway.display_name IS 'The name of the pathway to be used in GUIs and reports.  The pretty name.';
COMMENT ON COLUMN pathway.svg_identifier IS 'The textual identifier for an SVG pathway.  It may or may not be the same as the file name.';
COMMENT ON COLUMN pathway.comments IS 'An area to store official comments about any caveats with the data.';


CREATE TABLE summary.anomaly_type (
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
COMMENT ON COLUMN anomaly_type.anomaly IS 'The name of the anomaly';
COMMENT ON COLUMN anomaly_type.display_name IS 'A short name for GUI displays if necessary';
COMMENT ON COLUMN anomaly_type.anomaly_description IS 'A breif description of the anomaly';
COMMENT ON COLUMN anomaly_type.value_threshold IS 'The cutoff provided by CBIIT that was applied to the data to determine if the anomolgy exists in a certain gene.';
COMMENT ON COLUMN anomaly_type.value_threshold_type IS 'The comparision operator for the value_cutoff.  GT: greater-than  GTE: greater-than-or-equal-to  LT: less-than  LTE: less-than-or-equal-to  E: equals  NE: not-equal-to';
COMMENT ON COLUMN anomaly_type.patient_threshold IS 'The suggested threshold to apply to summary_by_gene.cases_detected/summary_by_gene.cases_probed.  It is assumed to be greater-than-or-equal-to this threshold.';
COMMENT ON COLUMN anomaly_type.comments IS 'An area to store official comments about any caveats with the data.';


CREATE TABLE summary.gene_drug (
   gene_id  INTEGER  NOT NULL,
   drug_id  INTEGER  NOT NULL
);
ALTER TABLE gene_drug ADD CONSTRAINT gene_drug_pk PRIMARY KEY (gene_id, drug_id);


CREATE TABLE summary.biocarta_gene_pathway (
   biocarta_gene_id  INTEGER  NOT NULL,
   pathway_id  INTEGER  NOT NULL
);
ALTER TABLE biocarta_gene_pathway ADD CONSTRAINT gene_pathway_pk PRIMARY KEY (biocarta_gene_id, pathway_id);


CREATE TABLE summary.summary_by_gene (
   gene_id  INTEGER  NOT NULL,
   anomaly_type_id  INTEGER  NOT NULL,
   cases_probed  INTEGER  NOT NULL,
   cases_detected  INTEGER  NOT NULL,
   comments	 VARCHAR(1000)
);
ALTER TABLE summary_by_gene ADD CONSTRAINT summary_by_gene_pk PRIMARY KEY (gene_id, anomaly_type_id);
ALTER TABLE summary_by_gene ADD CONSTRAINT summary_by_gene_cases_cc CHECK (cases_probed >= cases_detected);
COMMENT ON TABLE summary_by_gene IS 'Stores the gene-level summary information, rolled-up by cases/patients, on the various anomalies';
COMMENT ON COLUMN summary_by_gene.cases_probed IS 'The total number of cases/patients tested/probed for the anomaly.';
COMMENT ON COLUMN summary_by_gene.cases_detected IS 'The total number of cases/patients in which the anomaly is found.';
COMMENT ON COLUMN summary_by_gene.comments IS 'An area to store official comments about any caveats with the data.';




ALTER TABLE biocarta_gene ADD CONSTRAINT biocarta_gene_gene_fk FOREIGN KEY (gene_id) REFERENCES gene;
ALTER TABLE gene_drug ADD CONSTRAINT gene_drug_drug_fk FOREIGN KEY (drug_id) REFERENCES drug;
ALTER TABLE gene_drug ADD CONSTRAINT gene_drug_gene_pk FOREIGN KEY (gene_id) REFERENCES gene;
ALTER TABLE biocarta_gene_pathway ADD CONSTRAINT gene_pathway_pathway_fk FOREIGN KEY (pathway_id) REFERENCES pathway;
ALTER TABLE biocarta_gene_pathway ADD CONSTRAINT gene_pathway_gene_fk FOREIGN KEY (biocarta_gene_id) REFERENCES biocarta_gene;
ALTER TABLE summary_by_gene ADD CONSTRAINT summary_by_gene_gene_fk FOREIGN KEY (gene_id) REFERENCES gene;
ALTER TABLE summary_by_gene ADD CONSTRAINT summary_by_gene_anomaly_type_fk FOREIGN KEY (anomaly_type_id) REFERENCES anomaly_type;
ALTER TABLE drug_concept_code ADD CONSTRAINT drug_concept_code_drug_fk FOREIGN KEY (drug_id) REFERENCES drug;

INSERT INTO anomaly_type values (1, 'MUTATION', 'A mutation exists in the gene of interest as seen by sequence analysis.', 1, 'GTE', 'MUTATION');
INSERT INTO anomaly_type values (2, 'OVER-EXPRESSION, AFFYMETRIX', 'Over-expression of the gene is seen in Affymetrix 133A Microarray analysis.', 2, 'GTE', 'EXP+_AF');
INSERT INTO anomaly_type values (3, 'UNDER-EXPRESSION, AFFYMETRIX', 'Under-expression of the gene is seen in Affymetrix 133A Microarray analysis.', 0.5, 'LTE', 'EXP-_AF');

