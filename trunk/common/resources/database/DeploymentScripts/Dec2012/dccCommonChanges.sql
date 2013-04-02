insert into disease(disease_id, disease_abbreviation, disease_name, active) 
values (35,'ACC','Adrenocortical carcinoma', 1);
insert into disease(disease_id, disease_abbreviation, disease_name, active) 
values (36,'LCML','Chronic Myelogenous Leukemia', 1);
insert into disease(disease_id, disease_abbreviation, disease_name, active) 
values (37,'PCPG','Pheochromocytoma and Paraganglioma', 1);
insert into disease(disease_id, disease_abbreviation, disease_name, active)
values (38,'MISC','Miscellaneous', 1);
commit;

insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) 
values ('OR','University of Michigan',11);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id)
values (tss_disease_seq.NEXTVAL,'OR',35);

insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) 
values ('OS','Brigham and Women''s Hospital',11);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id)
values (tss_disease_seq.NEXTVAL,'OS',35);

insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) 
values ('P6','Translational Genomics Research Institute',11);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id)
values (tss_disease_seq.NEXTVAL,'P6',35);

insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) 
values ('PA','University of Minnesota',11);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id)
values (tss_disease_seq.NEXTVAL,'PA',35);

insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) 
values ('P7','Translational Genomics Research Institute',11);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id)
values (tss_disease_seq.NEXTVAL,'P7',37);

insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) 
values ('P8','University of Pittsburgh',11);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id)
values (tss_disease_seq.NEXTVAL,'P8',37);

insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) 
values ('PF','Fox Chase',11);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id)
values (tss_disease_seq.NEXTVAL,'PF',35);

insert into tissue_source_site (tss_code,tss_definition, receiving_center_id)
values ('OW','International Genomics Consortium',11);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id)
values (tss_disease_seq.NEXTVAL,'OW',38);

insert into tissue_source_site (tss_code,tss_definition, receiving_center_id)
values ('PK','University Health Network',11);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id)
values (tss_disease_seq.NEXTVAL,'PK',35);

commit;