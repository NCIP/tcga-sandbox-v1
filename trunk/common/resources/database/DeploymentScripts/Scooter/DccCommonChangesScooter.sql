DROP SEQUENCE shipped_biospec_element_seq;
CREATE SEQUENCE shipped_biospec_element_seq START WITH 1 INCREMENT BY 1;

DROP TABLE shipped_item_type CASCADE CONSTRAINTS;
CREATE TABLE shipped_item_type (
    shipped_item_type_id    	INTEGER    	NOT NULL,
    shipped_item_type    	VARCHAR2(20)    NOT NULL,
    CONSTRAINT shipped_item_type_pk_idx PRIMARY KEY (shipped_item_type_id)
);
INSERT INTO shipped_item_type values(1,'Aliquot');
INSERT INTO shipped_item_type values(2,'Shipping Portion');
commit;

DROP TABLE shipped_element_type CASCADE CONSTRAINTS;
CREATE TABLE shipped_element_type (
    element_type_id	INTEGER		NOT NULL,
    element_type_name	VARCHAR2(100)	NOT NULL,
    CONSTRAINT element_type_pk_idx PRIMARY KEY (element_type_id)
);
INSERT INTO shipped_element_type VALUES (1,'sample_type_code');
INSERT INTO shipped_element_type VALUES (2,'sample_sequence');
INSERT INTO shipped_element_type VALUES (3,'portion_sequence');
INSERT INTO shipped_element_type VALUES (4,'analyte_code');
INSERT INTO shipped_element_type VALUES (5,'plate_id');
commit;

DROP TABLE shipped_biospecimen CASCADE CONSTRAINTS;
CREATE TABLE shipped_biospecimen (
    shipped_biospecimen_id    	NUMBER(38)	NOT NULL,
    uuid            		VARCHAR2(36)    NOT NULL,
    shipped_item_type_id    	INTEGER        	NOT NULL,
    built_barcode		VARCHAR2(50)    NOT NULL,
    project_code        	VARCHAR2(10)    NOT NULL,
    tss_code        		VARCHAR2(10)    NOT NULL,
    bcr_center_id        	VARCHAR2(10)    NOT NULL,
    participant_code    	VARCHAR2(25)    NOT NULL,
    is_viewable        		NUMBER(1)    	DEFAULT 1,
    is_redacted        		NUMBER(1)    	DEFAULT 0,
    shipped_date        	DATE,
    CONSTRAINT shipped_biospecimen_pk_idx PRIMARY KEY (shipped_biospecimen_id)
);

CREATE UNIQUE INDEX uk_shipped_biospec_uuid_idx ON shipped_biospecimen(uuid);
ALTER TABLE shipped_biospecimen ADD (
    CONSTRAINT fk_shipped_biospec_project 
    FOREIGN KEY (project_code)
    REFERENCES project (project_code),
    CONSTRAINT fk_shipped_biospec_bcrcenter 
    FOREIGN KEY (bcr_center_id)
    REFERENCES center_to_bcr_center (bcr_center_id),
    CONSTRAINT fk_shipped_biospec_tss 
    FOREIGN KEY (tss_code)
    REFERENCES tissue_source_site (tss_code),
    CONSTRAINT fk_shipped_biospec_itemtype 
    FOREIGN KEY (shipped_item_type_id)
    REFERENCES shipped_item_type (shipped_item_type_id)
);

DROP TABLE shipped_biospecimen_element;
CREATE TABLE shipped_biospecimen_element (
    shipped_biospecimen_element_id    	NUMBER(38)    	NOT NULL,
    shipped_biospecimen_id        	NUMBER(38)    	NOT NULL,
    element_type_id            		INTEGER        	NOT NULL,
    element_value            		VARCHAR2(100)	NOT NULL,
    CONSTRAINT shipped_biospec_element_pk_idx PRIMARY KEY (shipped_biospecimen_element_id)
);
CREATE UNIQUE INDEX shipped_biospec_element_uk_idx ON shipped_biospecimen_element (shipped_biospecimen_id,element_type_id);

ALTER TABLE shipped_biospecimen_element ADD (
    CONSTRAINT fk_shipped_biospec_elem_type
    FOREIGN KEY (element_type_id)
    REFERENCES shipped_element_type (element_type_id),
    CONSTRAINT fk_shippedbio_elem_shipbio
    FOREIGN KEY (shipped_biospecimen_id)
    REFERENCES shipped_biospecimen(shipped_biospecimen_id)
);

DROP TABLE shipped_biospec_bcr_archive;
CREATE TABLE shipped_biospec_bcr_archive (
    shipped_biospecimen_id    NUMBER(38) NOT NULL,
    archive_id        NUMBER(38) NOT NULL,
    CONSTRAINT ship_biospec_archive_pk_idx PRIMARY KEY (shipped_biospecimen_id,archive_id) 
);


DROP TABLE shipped_biospecimen_file CASCADE CONSTRAINTS;
CREATE TABLE shipped_biospecimen_file (
    shipped_biospecimen_id    NUMBER(38) NOT NULL,
    file_id            NUMBER(38) NOT NULL,
    CONSTRAINT shipped_biospec_file_pk_idx PRIMARY KEY (shipped_biospecimen_id,file_id) 
);


GRANT select ON shipped_biospecimen TO commonread;
GRANT select ON shipped_biospecimen TO readonly;
GRANT all ON shipped_biospecimen TO commonmaint;
GRANT select ON shipped_biospecimen_element TO commonread;
GRANT select ON shipped_biospecimen_element TO readonly;
GRANT all ON shipped_biospecimen_element TO commonmaint;
GRANT select ON shipped_biospecimen_file TO commonread;
GRANT select ON shipped_biospecimen_file TO readonly;
GRANT all ON shipped_biospecimen_file TO commonmaint;
GRANT select ON shipped_biospec_bcr_archive TO commonread;
GRANT select ON shipped_biospec_bcr_archive TO readonly;
GRANT all ON shipped_biospec_bcr_archive TO commonmaint;
GRANT select ON shipped_item_type TO commonread;
GRANT select ON shipped_item_type TO readonly;
GRANT all ON shipped_item_type TO commonmaint;
GRANT select ON shipped_element_type TO commonread;
GRANT select ON shipped_element_type TO readonly;
GRANT all ON shipped_element_type TO commonmaint;
GRANT select ON shipped_biospec_element_seq to commonmaint;
GRANT select ON shipped_biospec_element_seq to commonread;