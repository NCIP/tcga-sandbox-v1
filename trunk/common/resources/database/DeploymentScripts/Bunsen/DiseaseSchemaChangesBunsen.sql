
DROP TABLE shipped_portion CASCADE CONSTRAINTS;
CREATE TABLE shipped_portion (
    shipped_portion_id    	NUMBER(38)    	NOT NULL,
    sample_id    		NUMBER(38)     	NOT NULL,
    shipped_portion_barcode	VARCHAR2(50)    NOT NULL,
    uuid            		VARCHAR2(36)	NOT NULL,
    CONSTRAINT shipped_portion_pk_idx PRIMARY KEY (shipped_portion_id)
);


DROP TABLE shipped_portion_element;
CREATE TABLE shipped_portion_element (
    shipped_portion_element_id		NUMBER(38)	NOT NULL,
    shipped_portion_id        		NUMBER(38)    	NOT NULL,
    clinical_xsd_element_id            	NUMBER(38) 	NOT NULL,
    element_value            		VARCHAR2(4000),
    CONSTRAINT shipped_portion_element_pk_idx PRIMARY KEY (shipped_portion_element_id)
);


DROP TABLE shipped_portion_archive;
CREATE TABLE shipped_portion_archive (
    shipped_portion_archive_id	NUMBER(38) NOT NULL,
    shipped_portion_id    	NUMBER(38) NOT NULL,
    archive_id        		NUMBER(38) NOT NULL,
    CONSTRAINT ship_portion_archive_pk_idx PRIMARY KEY (shipped_portion_archive_id) 
);


ALTER TABLE shipped_portion ADD (
	CONSTRAINT fk_shipped_portion_sample
	FOREIGN KEY (sample_id)
	REFERENCES sample (sample_id)
);

ALTER TABLE shipped_portion_element ADD (
	CONSTRAINT fk_shipped_port_elem_port
	FOREIGN KEY (shipped_portion_id)
	REFERENCES shipped_portion (shipped_portion_id),
	CONSTRAINT fk_shipped_port_elem_elem
	FOREIGN KEY (clinical_xsd_element_id)
	REFERENCES clinical_xsd_element (clinical_xsd_element_id)
);

ALTER TABLE shipped_portion_archive ADD (
	CONSTRAINT fk_shipped_port_arch_portion
	FOREIGN KEY (shipped_portion_id)
	REFERENCES shipped_portion (shipped_portion_id),
	CONSTRAINT fk_shipped_portion_arc_archive
	FOREIGN KEY (archive_id)
	REFERENCES archive_info (archive_id)
);