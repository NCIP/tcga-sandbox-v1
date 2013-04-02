/*
** This script can be run in dcccommondev to create insert statements for the dcccommon reference tables.
** The output of these selects should be saved to a file and run in your commontest schema.
**
**  PLEASE DO NOT RUN THE OUTPUT, THE INSERTS, IN ANY SCHEMA BUT YOUR commontest SCHEMA!!
**
** the set statements should prevent oracle from listing the column headers, rowcounts, etc in the output.
** and the linesize should make the insert statments appear on one line.
** This works in sqlplus but I have not tested this on all tools.
**
** Created by Shelley Alonso 3/18/2011
**
** Modification History
*
*  Matthew Nicholls - 3/23/2011 - Replaces "REPLACE_DISEASE" strings with "replace_disease" since underscore is reserved character for
*                                 regexs in Java.
**
*/
set head off
set term off
set wrap off
set echo off
set feedback off
set linesize 3000
select 'INSERT INTO visibility(visibility_id,visibility_name,identifiable) VALUES ('||visibility_id||','''||visibility_name||''','||identifiable||');' from visibility ;

select 'INSERT INTO data_level(level_number,level_definition) VALUES ('||level_number||','''||level_definition||''');' from data_level order by level_number;

select 'INSERT INTO archive_type(archive_type_id,archive_type,data_level) VALUES ('||archive_type_id||','''||archive_type||''','||nvl(data_level,0)||');' from archive_type order by archive_type_id;

select 'INSERT INTO disease (disease_id ,disease_name ,disease_abbreviation,active) VALUES ('||disease_id||','''||disease_name||''','''||disease_abbreviation||''','||active||');' FROM disease WHERE disease_abbreviation = replace_disease;

select 'INSERT INTO center_type (center_type_code,center_type_definition) VALUES ('''||center_type_code||''','''||center_type_definition||''');' from center_type;

select 'INSERT INTO data_type (data_type_id,name,center_type_code,ftp_display,available,sort_order) VALUES ('||data_type_id||','''||name||''','''||center_type_code||''','''||ftp_display||''','||available||','||data_type_id||');' from data_type order by data_type_id;

select 'INSERT INTO center (center_id,domain_name,center_type_code,display_name,short_name,sort_order) VALUES ('||center_id||','''||domain_name||''','''||center_type_code||''','''||replace(display_name,Q'[']',Q'['']')||''','''||short_name||''','||center_id||');' from center order by center_id;

select 'INSERT INTO center_to_bcr_center (bcr_center_id,center_type_code,center_id) VALUES ('''||bcr_center_id||''','''||center_type_code||''','||center_id||');' from center_to_bcr_center order by bcr_center_id;

select 'INSERT INTO data_visibility(data_visibility_id,data_type_id,visibility_id,level_number) VALUES ('||data_visibility_id||','||data_type_id||','||visibility_id||','||level_number||');' from data_visibility order by level_number;

select 'INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias, center_type_code,sort_order, available, base_data_type_id) VALUES ('||platform_id||','''||platform_name||''','''||platform_display_name||''','''||platform_alias||''','''||center_type_code||''','||platform_id||','||available||','||base_data_type_id||');' from platform order by platform_id;

select 'INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES ('||data_type_platform_id||','||data_type_id||','||platform_id||');' from data_type_to_platform order by data_type_platform_id;

select 'commit;' from dual;


