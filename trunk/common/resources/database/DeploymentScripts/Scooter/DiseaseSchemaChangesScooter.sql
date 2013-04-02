
DROP TABLE shipped_biospecimen CASCADE CONSTRAINTS;
CREATE TABLE shipped_biospecimen (
    shipped_biospecimen_id    	NUMBER(38)    	NOT NULL,
    uuid            		VARCHAR2(36)	NOT NULL,
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

DROP TABLE shipped_biospecimen_element;
CREATE TABLE shipped_biospecimen_element (
    shipped_biospecimen_element_id    	NUMBER(38)    	NOT NULL,
    shipped_biospecimen_id        	NUMBER(38)    	NOT NULL,
    element_type_id            		INTEGER 	NOT NULL,
    element_value            		VARCHAR2(100)	NOT NULL,
    CONSTRAINT shipped_biospec_element_pk_idx PRIMARY KEY (shipped_biospecimen_element_id)
);
CREATE UNIQUE INDEX shipped_biospec_element_uk_idx ON shipped_biospecimen_element (shipped_biospecimen_id,element_type_id);


DROP TABLE shipped_biospec_bcr_archive;
CREATE TABLE shipped_biospec_bcr_archive (
    shipped_biospecimen_id    	NUMBER(38) NOT NULL,
    archive_id        		NUMBER(38) NOT NULL,
    CONSTRAINT ship_biospec_archive_pk_idx PRIMARY KEY (shipped_biospecimen_id,archive_id) 
);


DROP TABLE shipped_biospecimen_file CASCADE CONSTRAINTS;
CREATE TABLE shipped_biospecimen_file (
    shipped_biospecimen_id    	NUMBER(38) NOT NULL,
    file_id            		NUMBER(38) NOT NULL,
    CONSTRAINT shipped_biospec_file_pk_idx PRIMARY KEY (shipped_biospecimen_id,file_id) 
);
