ALTER TABLE archive_info ADD (secondary_deploy_location VARCHAR2(2000));
DROP SEQUENCE rnaseq_value_seq;
DROP SEQUENCE mirnaseq_value_seq;
CREATE SEQUENCE rnaseq_value_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE mirnaseq_value_seq START WITH 1 INCREMENT BY 1;
create sequence clinical_xsd_seq start with 400 increment by 1;

DROP TABLE rnaseq_value;
CREATE TABLE rnaseq_value (
	rnaseq_id			NUMBER(38,0)	NOT NULL,
	data_set_id			NUMBER(38,0)	NOT NULL,
	hybridization_ref_id		NUMBER(38,0)	NOT NULL,
	feature	        		VARCHAR2(50)	NOT NULL,
	raw_counts			BINARY_DOUBLE	NOT NULL,
	median_length_normalized	BINARY_DOUBLE,
	rpkm				BINARY_DOUBLE,
	CONSTRAINT pk_rnaseq_value_idx	PRIMARY KEY (rnaseq_id)
);

ALTER TABLE rnaseq_value ADD(
	CONSTRAINT fk_rnaseq_dataset
	FOREIGN KEY (data_set_id)
	REFERENCES data_set(data_set_id),
	CONSTRAINT fk_rnaseq_hybref
	FOREIGN KEY (hybridization_ref_id)
	REFERENCES hybridization_ref(hybridization_ref_id)
);
CREATE INDEX rnaseq_dshr_idx on rnaseq_value (data_set_id,hybridization_ref_id);

DROP TABLE mirnaseq_value;
CREATE TABLE mirnaseq_value (
	mirnaseq_id			NUMBER(38,0)	NOT NULL,
	data_set_id			NUMBER(38,0)	NOT NULL,
	hybridization_ref_id		NUMBER(38,0)	NOT NULL,
	feature				VARCHAR2(50)	NOT NULL,
	read_count			BINARY_DOUBLE	NOT NULL,
	reads_per_million		BINARY_DOUBLE	NOT NULL,
	cross_mapped			CHAR(1)		NOT NULL,
	isoform_coords			VARCHAR2(50),
	mirna_region_annotation		VARCHAR2(50),
	mirna_region_accession		VARCHAR2(50),
	CONSTRAINT pk_mirnaseq_value_idx PRIMARY KEY (mirnaseq_id)
);
CREATE INDEX mirnaseq_dshr_idx on mirnaseq_value (data_set_id,hybridization_ref_id);
ALTER TABLE mirnaseq_value ADD(
	CONSTRAINT fk_mirnaseq_dataset
	FOREIGN KEY (data_set_id)
	REFERENCES data_set(data_set_id),
	CONSTRAINT fk_mirnaseq_hybref
	FOREIGN KEY (hybridization_ref_id)
	REFERENCES hybridization_ref(hybridization_ref_id)
);

CREATE GLOBAL TEMPORARY TABLE tmphybref
( hybridization_ref_id number(38)) 
ON COMMIT DELETE ROWS ;

grant select on rnaseq_value to readonly;
grant all on rnaseq_value to commonmaint;

grant select on mirnaseq_value to readonly;
grant all on mirnaseq_value to commonmaint;

grant select on tmphybref to readonly;
grant all on tmphybref to commonmaint;
