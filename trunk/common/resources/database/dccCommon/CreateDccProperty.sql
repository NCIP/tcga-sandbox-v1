DROP TABLE dcc_property;
CREATE TABLE dcc_property (
	property_id    		NUMBER(38,0)	NOT NULL,
	property_name           VARCHAR2(4000)	NOT NULL,
	property_value		VARCHAR2(4000),
	property_description	VARCHAR2(4000),
	application_name     	VARCHAR2(4000),
	server_name 		VARCHAR2(4000),
	CONSTRAINT pk_dcc_property_idx PRIMARY KEY (property_id)
);

CREATE UNIQUE INDEX uk_dcc_property_nameappproperty_idx ON dcc_property (property_name,application_name,server_name);

DROP SEQUENCE dcc_property_seq;
CREATE SEQUENCE dcc_property_seq START WITH 1 INCREMENT BY 1;
