/*
** This script can be run in dcccommondev to create insert statements for the dcccommon reference tables.
** The output of these selects should be saved to a file and run in your commontest schema.
**
**  PLEASE DO NOT RUN THE OUTPUT, THE INSERTS, IN ANY SCHEMA BUT YOUR commontest SCHEMA!!
**
** the first statement 'set head off' should prevent oracle from listing the column headers in the output. But I have not
** tested this on all tools.
**
** Created by Shelley Alonso 2/15/2011
**
** Modification History
**
** Shelley Alonso  2/15/2011  added replace for apostrophes in tissue_source_site.definition column
** 
** Shelley Alonso  2/17/2011  added replace for apostrophes in center.display_name; 
**                            added nvl to replace null with 0 for archive_type.data_level
**                            changed the order in which the inserts are created to take into consideration foreign keys 
** Shelley Alonso  5/18/2011  added annotation_classification table selects and annotation_classification_id to 
**                            annotation_category selects (APPS-3516)
** Shelley Alonso  6/23/2011  added shipped_item_type and shipped_element_type - adding selects for inserts
** Shelley Alonso  9/07/2011  added new ref table chromosome
** Shelley Alonso  2/16/2012  added new ref table control_type
** Shelley Alonso  2/21/2012  added new ref table data_type_relationship
** Shelley Alonso  4/04/2012  added new vcf_header_definition table
**
*/
set term off
set head off
set wrap off
set echo off
set feedback off
set linesize 3000

select 'INSERT INTO project(project_code,definition) values ('''||project_code||''','''||definition||''');' from project;

select 'INSERT INTO sample_type(sample_type_code,definition,is_tumor,short_letter_code) values ('''||sample_type_code||''','''||definition||''','||is_tumor||','''||short_letter_code||''');' from sample_type;

select 'INSERT INTO portion_analyte(portion_analyte_code,definition) values ('''||portion_analyte_code||''','''||definition||''');' from portion_analyte;

select 'INSERT INTO chromosome(chromosome_name, length, build) values ('''||chromosome_name||''','||length||','''||build||''');' from chromosome;

select 'INSERT INTO GENERATION_METHOD (GENERATION_METHOD_ID, GENERATION_METHOD) VALUES ('||generation_method_id||','''||generation_method||''');' from generation_method;

select 'INSERT INTO control_type (control_type_id,control_Type,xml_name) values ('||control_type_id||','''||control_type||''','''||xml_name||''');' from control_type;

select 'INSERT INTO visibility(visibility_id,visibility_name,identifiable) VALUES ('||visibility_id||','''||visibility_name||''','||identifiable||');' from visibility ;

select 'INSERT INTO data_level(level_number,level_definition) VALUES ('||level_number||','''||level_definition||''');' from data_level order by level_number;

select 'INSERT INTO archive_type(archive_type_id,archive_type,data_level) VALUES ('||archive_type_id||','''||archive_type||''','||nvl(data_level,0)||');' from archive_type order by archive_type_id;

select 'INSERT INTO disease (disease_id ,disease_name ,disease_abbreviation,active) VALUES ('||disease_id||','''||disease_name||''','''||disease_abbreviation||''','||active||');' FROM disease order by disease_id;

select 'INSERT INTO center_type (center_type_code,center_type_definition) VALUES ('''||center_type_code||''','''||center_type_definition||''');' from center_type;

select 'INSERT INTO data_type (data_type_id,name,center_type_code,ftp_display,available,sort_order) VALUES ('||data_type_id||','''||name||''','''||center_type_code||''','''||ftp_display||''','||available||','||data_type_id||');' from data_type order by data_type_id;

select 'INSERT INTO annotation_classification(annotation_classification_id,classification_display_name,classification_description) VALUES ('||annotation_classification_id||','''||classification_display_name||''','''||classification_description||''');' from annotation_classification order by annotation_classification_id;

select 'INSERT INTO annotation_category(annotation_category_id,annotation_classification_id,category_display_name,category_description,cadsr_description) VALUES ('||annotation_category_id||','||annotation_classification_id||','''||category_display_name||''','''||category_description||''','''||cadsr_description||''');' from annotation_category order by annotation_category_id;

select 'INSERT INTO annotation_item_type(item_type_id,type_display_name,type_description) VALUES ('||item_type_id||','''||type_display_name||''','''||type_description||''');' from annotation_item_type order by item_type_id;

select 'INSERT INTO annotation_category_item_type(annotation_category_type_id,annotation_category_id,item_type_id) VALUES ('||annotation_category_type_id||','||annotation_category_id||','||item_type_id||');' from annotation_category_item_type order by annotation_category_type_id;

select 'INSERT INTO center (center_id,domain_name,center_type_code,display_name,short_name,sort_order, is_uuid_converted) VALUES ('||center_id||','''||domain_name||''','''||center_type_code||''','''||replace(display_name,Q'[']',Q'['']')||''','''||short_name||''','||center_id||','||is_uuid_converted||');' from center order by center_id;

select 'INSERT INTO center_to_bcr_center (bcr_center_id,center_type_code,center_id) VALUES ('''||bcr_center_id||''','''||center_type_code||''','||center_id||');' from center_to_bcr_center order by bcr_center_id;

select 'INSERT INTO batch_number_assignment (batch_id,disease_id,center_id) values ('||batch_id||','||disease_id||','||center_id||');' from batch_number_assignment order by batch_id;

select 'INSERT INTO data_visibility(data_visibility_id,data_type_id,visibility_id,level_number) VALUES ('||data_visibility_id||','||data_type_id||','||visibility_id||','||level_number||');' from data_visibility order by level_number;

select 'INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias, center_type_code,sort_order, available, base_data_type_id) VALUES ('||platform_id||','''||platform_name||''','''||platform_display_name||''','''||platform_alias||''','''||center_type_code||''','||platform_id||','||available||','||base_data_type_id||');' from platform order by platform_id;

select 'INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES ('||data_type_platform_id||','||data_type_id||','||platform_id||');' from data_type_to_platform order by data_type_platform_id;

select 'INSERT INTO tissue_source_site (tss_code, tss_definition ,receiving_center_id) VALUES ('''||tss_code||''','''||replace(tss_definition,Q'[']',Q'['']')||''','||receiving_center_id||');' from tissue_source_site order by tss_code;

select 'INSERT INTO tss_to_disease (tss_disease_id,tss_code, disease_id) VALUES ('||tss_disease_id||','''||tss_code||''','||disease_id||');' from tss_to_disease order by tss_disease_id;

select 'INSERT INTO tissue (tissue_id,tissue) VALUES ('||tissue_id||','''||tissue||''');' from tissue order by tissue_id;

select 'INSERT INTO tissue_to_disease (tissue_disease_id,tissue_id,disease_id) VALUES ('||tissue_disease_id||','||tissue_id||','||disease_id||');' from tissue_to_disease order by tissue_disease_id;

select 'INSERT INTO shipped_element_type (element_type_id,element_type_name) VALUES ('||element_type_id||','''||element_type_name||''');' from shipped_element_type;

select 'INSERT INTO shipped_item_type (shipped_item_type_id,shipped_item_type) VALUES ('||shipped_item_type_id||','''||shipped_item_type||''');' from shipped_item_type;

select 'INSERT INTO uuid_item_type(item_type_id,item_type,sort_order,xml_name) VALUES('||item_type_id||','''||item_type||''','||sort_order||','''||xml_name||''');' from uuid_item_type;

select 'INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description) VALUES('||vcf_header_def_id||','''||header_type_name||''','''||id_name||''','''||number_value||''','''||type||''','''||REPLACE(description,Q'[']',Q'['']')||''');' from vcf_header_definition;

select 'commit;' from dual;
