DROP SEQUENCE gen_method_seq;
CREATE SEQUENCE gen_method_seq START WITH 10 INCREMENT BY 1;
DROP SEQUENCE uuid_seq;
CREATE SEQUENCE uuid_seq START WITH 10 INCREMENT BY 1;
DROP SEQUENCE barcode_seq;
CREATE SEQUENCE barcode_seq START WITH 10 INCREMENT BY 1;


DROP TABLE uuid CASCADE CONSTRAINTS;
CREATE TABLE uuid (
    uuid                 VARCHAR2(36) NOT NULL,
    center_id            NUMBER(38)   NOT NULL,
    generation_method_id NUMBER(38)   NOT NULL,
    create_date          DATE         NOT NULL,
    created_by           VARCHAR2(30) NOT NULL,
    latest_barcode_id    NUMBER(38) ,
    CONSTRAINT uuid_pk_idx PRIMARY KEY (uuid)
);

DROP TABLE generation_method  CASCADE CONSTRAINTS;
CREATE TABLE generation_method (
    generation_method_id     NUMBER(38)   NOT NULL,
    generation_method        VARCHAR2(20) NOT NULL,
    CONSTRAINT generation_method_pk_idx PRIMARY KEY (generation_method_id)
);

DROP TABLE barcode_history  CASCADE CONSTRAINTS;
CREATE TABLE barcode_history(
    barcode_id        NUMBER(38)   NOT NULL,
    barcode           VARCHAR2(50) NOT NULL,
    uuid              VARCHAR2(36) NOT NULL,
    disease_id        NUMBER(38)   NOT NULL,
    effective_date    DATE         NOT NULL,
    CONSTRAINT barcode_pk_idx PRIMARY KEY (barcode_id)
);

ALTER TABLE uuid ADD (
    CONSTRAINT fk_uuid_center_id 
    FOREIGN KEY (center_id)
    REFERENCES center(center_id),
    CONSTRAINT fk_uuid_gen_method_id 
    FOREIGN KEY (generation_method_id)
    REFERENCES generation_method(generation_method_id)
);

ALTER TABLE barcode_history ADD (
    CONSTRAINT fk_barcode_uuid
    FOREIGN KEY (uuid)
    REFERENCES uuid(uuid),
    CONSTRAINT fk_barcode_disease_id 
    FOREIGN KEY (disease_id)
    REFERENCES disease(disease_id)
);

grant select on uuid to public;
grant select on barcode_history to public;
grant select on generation_method to public;