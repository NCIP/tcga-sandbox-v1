/*
** if all replacement work is completed on getting data for uuid_hierarchy, drop stored proc, uuid_platform table and all uuid_hierarchy related views
*/
drop procedure populateUuidHierarchy;
drop table uuid_platform;
drop view aliquot_v;
drop view analyte_v;
drop view drug_v;
drop view examination_v;
drop view patient_v;
drop view portion_v;
drop view radiation_v;
drop view sample_v;
drop view shipped_portion_v;
drop view slide_v;
drop view surgery_v;
alter table annotation add (rescinded number(1) DEFAULT 0 NOT NULL);

DECLARE
id NUMBER(38);
CURSOR aCur IS 
  SELECT annotation_id FROM annotation 
  WHERE annotation_category_id in 
     (SELECT annotation_category_id FROM annotation_category 
      WHERE annotation_classification_id = 4);

BEGIN
  FOR aRec IN aCur LOOP
      delete from annotation_note where annotation_id = aRec.annotation_id;
      delete from annotation_item where annotation_id = aRec.annotation_id;
      delete from annotation where annotation_id = aRec.annotation_id;
  END LOOP;
  COMMIT;
END;
/

delete from annotation_category_item_type where annotation_category_id in (select annotation_category_id from 
annotation_category where annotation_classification_id=4);
delete from annotation_category where annotation_classification_id=4;
delete from annotation_classification where annotation_classification_id=4;
commit;

insert into annotation_item_type values(7,'Shipped Portion','A shipped portion');
commit;
insert into annotation_category_item_type (annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_item_category_seq.nextval,8,7);
insert into annotation_category_item_type (annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_item_category_seq.nextval,10,7);
insert into annotation_category_item_type (annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_item_category_seq.nextval,11,7);
insert into annotation_category_item_type (annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_item_category_seq.nextval,15,7);
insert into annotation_category_item_type (annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_item_category_seq.nextval,21,7);
insert into annotation_category_item_type (annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_item_category_seq.nextval,22,7);
insert into annotation_category_item_type (annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_item_category_seq.nextval,23,7);
insert into annotation_category_item_type (annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_item_category_seq.nextval,24,7);
insert into annotation_category_item_type (annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_item_category_seq.nextval,25,7);
insert into annotation_category_item_type (annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_item_category_seq.nextval,26,7);
insert into annotation_category_item_type (annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_item_category_seq.nextval,28,7);
insert into annotation_category_item_type (annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_item_category_seq.nextval,29,7);
insert into annotation_category_item_type (annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_item_category_seq.nextval,30,7);
insert into annotation_category_item_type (annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_item_category_seq.nextval,36,7);
commit;

ALTER TABLE uuid_hierarchy ADD (is_redacted NUMBER(1) DEFAULT 0 NOT NULL);

declare 
  patientCompare  VARCHAR2(20);
  CURSOR pCur IS
  SELECT distinct substr(built_barcode, 0, 12) as patient
  from shipped_biospecimen
  where is_redacted = 1;
BEGIN
  FOR pRec in pCur LOOP
     patientCompare := pRec.patient||'%';
     update uuid_hierarchy set is_redacted = 1 where barcode like patientCompare;
  END LOOP;
  commit;
END;
/


