insert into annotation_classification (annotation_classification_id, classification_display_name)
values(1, 'Redaction');
insert into annotation_classification (annotation_classification_id, classification_display_name)
values(2, 'Notification');
insert into annotation_classification (annotation_classification_id, classification_display_name)
values(3, 'Observation');
insert into annotation_classification (annotation_classification_id, classification_display_name)
values(4, 'CenterNotification');
insert into annotation_classification (annotation_classification_id, classification_display_name)
values(5, 'Rescission');
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (1,'Tumor tissue origin incorrect','Tumor tissue origin incorrect','',1);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (2,'Tumor type incorrect','Tumor type incorrect','',1);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (3,'Genotype mismatch','Genotype mismatch','',1);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (4,'Subject withdrew consent','Subject withdrew consent','',1);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (5,'Subject identity unknown','Subject identity unknown','',1);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (6,'Prior malignancy','Prior malignancy','',2);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (7,'Neoadjuvant therapy','Neoadjuvant therapy','',2);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (8,'Qualification metrics changed','Qualification metrics changed','',2);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (9,'Pathology outside specification','Pathology outside specification','',2);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (10,'Molecular analysis outside specification','Molecular analysis outside specification','',2);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (11,'Duplicate item','Duplicate item','',2);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (13,'Sample compromised','Sample compromised','',2);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (14,'Clinical data insufficient','Clinical data insufficient','',2);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (15,'Item does not meet study protocol','Item does not meet study protocol','',2);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (17,'Item in special subset','Item in special subset','',2);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (18,'Qualified in error','Qualified in error','',2);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (21,'Item is noncanonical','Item is noncanonical','',2);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (22,'New notification type','New notification type','',2);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (23,'Tumor class but appears normal','Tumor class but appears normal','',3);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (24,'Normal class but appears diseased','Normal class but appears diseased','',3);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (25,'Item may not meet study protocol','Item may not meet study protocol','',3);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (26,'New observation type','New observation type','',3);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (27,'Duplicate case','Duplicate case','',1);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (28,'Center QC failed','Center QC failed','',4);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (29,'Item flagged DNU','Item flagged DNU','',4);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (30,'General','General','',3);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (31,'Previous Redaction rescinded','Previous Redaction rescinded','',5);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (32,'Previous Notification rescinded','Previous Notification rescinded','',5);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (33,'Previous Observation  rescinded','Previous Observation  rescinded','',5);
insert into annotation_category (annotation_category_id,category_display_name,category_description,cadsr_description,annotation_classification_id)
values (34,'Previous Center Notification rescinded','Previous Center Notification rescinded','',5);

insert into annotation_item_type (item_type_id,type_display_name,type_description) 
values (1,'Aliquot','An aliquot');
insert into annotation_item_type (item_type_id,type_display_name,type_description) 
values (2,'Analyte','An analyte');
insert into annotation_item_type (item_type_id,type_display_name,type_description) 
values (3,'Patient','A patient');
insert into annotation_item_type (item_type_id,type_display_name,type_description) 
values (4,'Portion','A portion');
insert into annotation_item_type (item_type_id,type_display_name,type_description) 
values (5,'Sample','A sample');
insert into annotation_item_type (item_type_id,type_display_name,type_description) 
values (6,'Slide','A slide');

insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,1,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,1,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,2,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,2,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,3,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,3,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,4,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,5,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,6,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,7,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,8,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,8,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,8,6);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,8,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,8,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,8,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,9,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,9,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,9,6);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,10,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,10,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,10,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,10,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,11,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,11,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,11,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,11,6);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,11,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,11,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,13,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,14,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,15,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,15,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,15,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,15,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,15,6);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,15,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,17,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,17,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,18,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,21,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,21,6);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,21,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,21,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,21,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,21,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,22,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,22,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,22,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,22,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,22,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,22,6);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,23,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,23,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,23,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,23,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,23,6);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,24,6);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,24,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,24,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,24,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,24,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,25,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,25,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,25,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,25,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,25,6);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,25,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,26,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,26,6);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,26,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,26,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,26,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,26,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,27,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,28,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,28,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,28,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,28,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,28,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,29,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,29,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,29,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,29,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,29,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,30,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,30,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,30,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,30,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,30,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,30,6);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,31,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,31,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,31,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,31,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,31,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,31,6);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,32,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,32,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,32,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,32,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,32,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,32,6);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,33,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,33,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,33,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,33,5);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,33,4);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) values (annotation_category_type_seq.nextval,33,6);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) 
values (annotation_category_type_seq.nextval,34,1);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) 
values (annotation_category_type_seq.nextval,34,2);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) 
values (annotation_category_type_seq.nextval,34,3);
insert into annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) 
values (annotation_category_type_seq.nextval,34,5);
commit;