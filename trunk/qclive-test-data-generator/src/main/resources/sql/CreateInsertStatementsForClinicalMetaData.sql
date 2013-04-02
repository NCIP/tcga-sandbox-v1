/*
** This script can be run in any disease schema, or in production as the READONLY user, to create insert statements
** from the latest clinical meta data for a specifide disease. If you runnnnnnnnnn as READONLY, then youuuuuuuuu need 
** to preface  tablename with the disease schema name in the from clause ( from TCGAREAD.clinical_xsd_element )
** 
** The output from each select statement will be a bunch of insert statements that you can save to a file and run in
** your unit test schema. PLEASE DO NOT RUN THE INSERTS IN DEV.
**
** the first statement 'set head off' should prevent oracle from listing the column headers in the output. But I have not
** tested this on all tools.
**
** Created By Shelley Alonso 2/15/2011
**
** Modification History
**
**  Shelley Alonso 2/15/2011  added replace for single quotes in the clinical_xsd_element.description fields
**
*/
set head off
set term off
set wrap off
set echo off
set feedback off
set linesize 3000
select 'INSERT INTO clinical_xsd_element (clinical_xsd_element_id,element_name,is_protected,description,value_type) values ('||clinical_xsd_element_id||','''||element_name||''','||is_protected||','''||replace(description,Q'[']',Q'['']')||''','''||value_type||''');' from clinical_xsd_element;

select 'INSERT INTO clinical_xsd_enum_value(clinical_xsd_enum_value_id,xsd_element_id,enum_value) VALUES ('||clinical_xsd_enum_value_id||','||xsd_element_id||','''||replace(enum_value, '''', '''''')||''');' from clinical_xsd_enum_value;

select CASE when parent_table_id is null then 'INSERT INTO clinical_table(clinical_table_id,table_name,join_for_sample,join_for_patient,barcode_element_id,barcode_column_name,element_node_name,element_table_name,table_id_column_name,archive_link_table_name,is_dynamic, dynamic_identifier_column_name) VALUES ('||clinical_table_id||','''||table_name||''','''||join_for_sample||''','''||join_for_patient||''','||barcode_element_id||','''||barcode_column_name||''','''||element_node_name||''','''||element_table_name||''','''||table_id_column_name||''','''||archive_link_table_name||''','||is_dynamic||','''||dynamic_identifier_column_name||''');' when barcode_element_id is null then 'INSERT INTO clinical_table(clinical_table_id,table_name,join_for_sample,join_for_patient,barcode_column_name,element_node_name,element_table_name,table_id_column_name,archive_link_table_name,parent_table_id,is_dynamic, dynamic_identifier_column_name) VALUES ('||clinical_table_id||','''||table_name||''','''||join_for_sample||''','''||join_for_patient||''','''||barcode_column_name||''','''||element_node_name||''','''||element_table_name||''','''||table_id_column_name||''','''||archive_link_table_name||''','||parent_table_id||','||is_dynamic||','''||dynamic_identifier_column_name||''');' else  'INSERT INTO clinical_table(clinical_table_id,table_name,join_for_sample,join_for_patient,barcode_element_id,barcode_column_name,element_node_name,element_table_name,table_id_column_name,archive_link_table_name,parent_table_id,is_dynamic, dynamic_identifier_column_name) VALUES ('||clinical_table_id||','''||table_name||''','''||join_for_sample||''','''||join_for_patient||''','||barcode_element_id||','''||barcode_column_name||''','''||element_node_name||''','''||element_table_name||''','''||table_id_column_name||''','''||archive_link_table_name||''','||parent_table_id||','||is_dynamic||','''||dynamic_identifier_column_name||''');' end from clinical_table order by clinical_table_id;

select 'INSERT INTO clinical_file(clinical_file_id,filename,by_patient,context,is_dynamic,clinical_table_id) VALUES ('||clinical_file_id||','''||filename||''','||by_patient||','''||context||''','||is_dynamic||','||DECODE(clinical_table_id,null,'null',clinical_table_id)||');' from clinical_file;

select 'INSERT INTO clinical_file_element(clinical_file_element_id,xsd_element_id,table_id,table_column_name,file_column_name,file_column_order,clinical_file_id,unit_column_name) VALUES ('||clinical_file_element_id||','||xsd_element_id||','||table_id||','''||table_column_name||''','''||file_column_name||''','||file_column_order||','||clinical_file_id||','''||unit_column_name||''');' from clinical_file_element;

select 'INSERT INTO clinical_file_to_table (clinical_file_table_id,clinical_file_id,clinical_table_id) VALUES ('||clinical_file_table_id||','||clinical_file_id||','||clinical_table_id||');' from clinical_file_to_table;

select 'commit;' from dual;