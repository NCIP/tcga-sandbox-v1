DROP SEQUENCE annotation_category_type_seq;
CREATE SEQUENCE annotation_category_type_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE annotation_item_category_seq;
CREATE SEQUENCE annotation_item_category_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE item_type_seq;
CREATE SEQUENCE item_type_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE annotation_item_seq;
CREATE SEQUENCE annotation_item_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE annotation_seq;
CREATE SEQUENCE annotation_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE annotation_note_seq;
CREATE SEQUENCE annotation_note_seq START WITH 1 INCREMENT BY 1;
DROP SEQUENCE annotation_disease_seq;
CREATE SEQUENCE annotation_disease_seq START WITH 1 INCREMENT BY 1;

DROP  TABLE annotation_item_type CASCADE CONSTRAINTS;
CREATE TABLE annotation_item_type
(
  item_type_id               NUMBER(38)     NOT NULL,
  type_display_name          VARCHAR2(200)  NOT NULL,
  type_description           VARCHAR2(2000) NOT NULL,
  CONSTRAINT annotation_type_pk_idx PRIMARY KEY (item_type_id)
);

DROP  TABLE annotation_category CASCADE CONSTRAINTS;
CREATE TABLE annotation_category
(
  annotation_category_id    NUMBER(38)     NOT NULL,
  category_display_name     VARCHAR2(200)  NOT NULL,
  category_description      VARCHAR2(2000) NOT NULL,
  caDSR_description         VARCHAR2(2000) ,
  CONSTRAINT annotation_category_pk_idx PRIMARY KEY (annotation_category_id)
);

DROP  TABLE annotation_category_item_type CASCADE CONSTRAINTS;
CREATE TABLE annotation_category_item_type 
(
  annotation_category_type_id  NUMBER(38)     NOT NULL,
  annotation_category_id       NUMBER(38)     NOT NULL,
  item_type_id                 NUMBER(38)     NOT NULL,
  CONSTRAINT annot_type_cat_pk_idx PRIMARY KEY (annotation_category_type_id)
);
ALTER TABLE annotation_category_item_type ADD (
  CONSTRAINT fk_annot_itemtype_cat_id 
  FOREIGN KEY (annotation_category_id) 
  REFERENCES annotation_category(annotation_category_id),
  CONSTRAINT fk_annot_itemtype_type_id
  FOREIGN KEY (item_type_id) 
  REFERENCES annotation_item_type(item_type_id)
);



DROP  TABLE annotation CASCADE CONSTRAINTS;
CREATE TABLE annotation
(
  annotation_id           NUMBER(38)     NOT NULL,
  annotation_category_id  NUMBER(38)     NOT NULL,
  entered_by              VARCHAR2(200)  NOT NULL,
  entered_date            DATE           NOT NULL,
  modified_by             VARCHAR2(200) ,
  modified_date		  DATE,
  curated		  NUMBER(1,0)   DEFAULT 0 NOT NULL,
  CONSTRAINT annotation_pk_idx PRIMARY KEY (annotation_id)
);

ALTER TABLE annotation ADD (
  CONSTRAINT fk_annotation_category_id 
  FOREIGN KEY (annotation_category_id) 
  REFERENCES annotation_category(annotation_category_id)
);

CREATE BITMAP INDEX annotation_curated_idx ON annotation(curated);
  

DROP  TABLE annotation_item CASCADE CONSTRAINTS;
CREATE TABLE annotation_item
(
  annotation_item_id      NUMBER(38)     NOT NULL,
  annotation_id           NUMBER(38)     NOT NULL,
  item_type_id            NUMBER(38)     NOT NULL,
  annotation_item         VARCHAR2(50)   NOT NULL,
  disease_id		  NUMBER(38,0)   NOT NULL,
  CONSTRAINT annotation_item_pk_idx PRIMARY KEY (annotation_item_id)
);


ALTER TABLE annotation_item ADD (
  CONSTRAINT fk_annotitem_annotation_id 
  FOREIGN KEY (annotation_id) 
  REFERENCES annotation(annotation_id),
  CONSTRAINT fk_annotitem_type_id 
  FOREIGN KEY (item_type_id) 
  REFERENCES annotation_item_type(item_type_id),
  CONSTRAINT fk_annotation_item_disease
  FOREIGN KEY (disease_id)
  REFERENCES disease(disease_id)
);
  
DROP  TABLE annotation_note CASCADE CONSTRAINTS;
CREATE TABLE annotation_note
(
  annotation_note_id      NUMBER(38)     NOT NULL,
  annotation_id           NUMBER(38)     NOT NULL,
  note                    VARCHAR2(4000) NOT NULL,
  entered_by              VARCHAR2(200)  NOT NULL,
  entered_date            DATE           NOT NULL,  
  modified_by		  VARCHAR2(200),
  modified_date           DATE,
  CONSTRAINT annotation_note_pk_idx PRIMARY KEY (annotation_note_id)
);
ALTER TABLE annotation_note ADD (
  CONSTRAINT fk_annotnote_annotation_id 
  FOREIGN KEY (annotation_id) 
  REFERENCES annotation(annotation_id)
);

