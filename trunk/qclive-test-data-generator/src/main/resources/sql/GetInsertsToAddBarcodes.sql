/**
 * Please provide the statements in one line. Do not break the statements into multiple lines. If you break the
 * statements the statements won't get executed in the test-data generator.
 *
 * Since the select statements defined in this script are all on one line, they provided in readable form here in the prolog/comment section
 * for modification and debugging purposes.
 * 
 * select distinct 
 * CASE
 * WHEN ship_date is null THEN
 * 'INSERT INTO biospecimen_barcode (biospecimen_id,barcode,project_code,tss_code,patient,sample_type_code,sample_sequence,portion_sequence,portion_analyte_code,plate_id,bcr_center_id,is_valid,is_viewable,ship_date,uuid)values ('||
 * bb.biospecimen_id||','''||bb.barcode||''','''||bb.project_code||''','''||bb.tss_code||''','''||bb.patient||''','''||bb.sample_type_code||''','''||
 * bb.sample_sequence||''','''||bb.portion_sequence||''','''||bb.portion_analyte_code||''','''||bb.plate_id||''','''||bb.bcr_center_id||''','||bb.is_valid||','||bb.is_viewable||
 * ',null,'''||bb.uuid||''');'
 * ELSE
 * 'INSERT INTO biospecimen_barcode (biospecimen_id,barcode,project_code,tss_code,patient,sample_type_code,sample_sequence,portion_sequence,portion_analyte_code,plate_id,bcr_center_id,is_valid,is_viewable,ship_date,uuid)values ('||
 * bb.biospecimen_id||','''||bb.barcode||''','''||bb.project_code||''','''||bb.tss_code||''','''||bb.patient||''','''||bb.sample_type_code||''','''||
 * bb.sample_sequence||''','''||bb.portion_sequence||''','''||bb.portion_analyte_code||''','''||bb.plate_id||''','''||bb.bcr_center_id||''','||bb.is_valid||','||bb.is_viewable||
 * ',TO_DATE('''||to_char(bb.ship_date,'MM/DD/YYYY')||''',''MM/DD/YYYY''),'''||bb.uuid||''');'
 * end
 *  from biospecimen_barcode bb, biospecimen_to_file bf, file_to_archive fa,archive_info a, disease d, center c, platform p
 *  where a.disease_id = d.disease_id
 *  and   d.disease_abbreviation = replace_disease
 *  and   a.platform_id= p.platform_id
 *  and   p.platform_name = replace_platform
 *  and   a.center_id = c.center_id
 *  and   c.domain_name = replace_center
 *  and a.archive_id=fa.archive_id
 *  and fa.file_id = bf.file_id
 *  and bf.biospecimen_id=bb.biospecimen_id;
 *  select 'commit;' from dual;
 * 
 * select distinct 
 * 'INSERT INTO uuid (uuid,center_id,generation_method_id,latest_barcode_id,created_by,create_date)values('''||
 * uuid||''','||center_id||','||generation_method_id||','||latest_barcode_id||','''||created_by||''',TO_DATE('''||to_char(create_date,'MM/DD/YYYY')||''',''MM/DD/YYYY''));'
 *   from uuid
 * WHERE uuid in
 * (select distinct bb.uuid
 *  from biospecimen_barcode bb, biospecimen_to_file bf, file_to_archive fa,archive_info a, disease d, center c, platform p
 *  where a.disease_id = d.disease_id
 *  and   d.disease_abbreviation = replace_disease
 *  and   a.platform_id= p.platform_id
 *  and   p.platform_name = replace_platform
 *  and   a.center_id = c.center_id
 *  and   c.domain_name = replace_center
 *  and a.archive_id=fa.archive_id
 *  and fa.file_id = bf.file_id
 *  and bf.biospecimen_id=bb.biospecimen_id);
 * select 'commit;' from dual;
 *  
 *  select distinct 
 *  'INSERT INTO barcode_history (barcode_id,barcode,uuid,disease_id,effective_date)values('||
 *  barcode_id||','''||barcode||''','''||uuid||''','||disease_id||',TO_DATE('''||to_char(effective_date,'MM/DD/YYYY')||''',''MM/DD/YYYY''));'
 *    from barcode_history
 *  WHERE barcode in
 *  (select distinct bb.barcode
 *  from biospecimen_barcode bb, biospecimen_to_file bf, file_to_archive fa,archive_info a, disease d, center c, platform p
 *  where a.disease_id = d.disease_id
 *  and   d.disease_abbreviation = replace_disease
 *  and   a.platform_id= p.platform_id
 *  and   p.platform_name = replace_platform
 *  and   a.center_id = c.center_id
 *  and   c.domain_name = replace_center
 *  and a.archive_id=fa.archive_id
 *  and fa.file_id = bf.file_id
 *  and bf.biospecimen_id=bb.biospecimen_id);
 *  select 'commit;' from dual;
 * 
 *  Replacement strings for the select statements above: replace_disease, replace_platform, replace_center
 **/

set heading off
set term off
set wrap off
set echo off
set feedback off
set linesize 3000

select distinct CASE WHEN ship_date is null THEN 'INSERT INTO biospecimen_barcode (biospecimen_id,barcode,project_code,tss_code,patient,sample_type_code,sample_sequence,portion_sequence,portion_analyte_code,plate_id,bcr_center_id,is_valid,is_viewable,ship_date,uuid)values ('||bb.biospecimen_id||','''||bb.barcode||''','''||bb.project_code||''','''||bb.tss_code||''','''||bb.patient||''','''||bb.sample_type_code||''','''||bb.sample_sequence||''','''||bb.portion_sequence||''','''||bb.portion_analyte_code||''','''||bb.plate_id||''','''||bb.bcr_center_id||''','||bb.is_valid||','||bb.is_viewable||',null,'''||bb.uuid||''');' ELSE 'INSERT INTO biospecimen_barcode (biospecimen_id,barcode,project_code,tss_code,patient,sample_type_code,sample_sequence,portion_sequence,portion_analyte_code,plate_id,bcr_center_id,is_valid,is_viewable,ship_date,uuid)values ('||bb.biospecimen_id||','''||bb.barcode||''','''||bb.project_code||''','''||bb.tss_code||''','''||bb.patient||''','''||bb.sample_type_code||''','''||bb.sample_sequence||''','''||bb.portion_sequence||''','''||bb.portion_analyte_code||''','''||bb.plate_id||''','''||bb.bcr_center_id||''','||bb.is_valid||','||bb.is_viewable||',TO_DATE('''||to_char(bb.ship_date,'MM/DD/YYYY')||''',''MM/DD/YYYY''),'''||bb.uuid||''');' end from biospecimen_barcode bb, biospecimen_to_file bf, file_to_archive fa,archive_info a, disease d, platform p where a.disease_id = d.disease_id and   d.disease_abbreviation = replace_disease and   a.platform_id= p.platform_id and   p.platform_name = replace_platform and a.archive_id=fa.archive_id and fa.file_id = bf.file_id and bf.biospecimen_id=bb.biospecimen_id;

select 'commit;' from dual;

select distinct 'INSERT INTO uuid (uuid,center_id,generation_method_id,latest_barcode_id,created_by,create_date)values('''||uuid||''','||center_id||','||generation_method_id||','||latest_barcode_id||','''||created_by||''',TO_DATE('''||to_char(create_date,'MM/DD/YYYY')||''',''MM/DD/YYYY''));' from uuid WHERE uuid in (select distinct bb.uuid from biospecimen_barcode bb, biospecimen_to_file bf, file_to_archive fa,archive_info a, disease d,platform p where a.disease_id = d.disease_id and   d.disease_abbreviation = replace_disease and   a.platform_id= p.platform_id and   p.platform_name = replace_platform and a.archive_id=fa.archive_id and fa.file_id = bf.file_id and bf.biospecimen_id=bb.biospecimen_id);

select 'commit;' from dual;
 
select distinct 'INSERT INTO barcode_history (barcode_id,barcode,uuid,disease_id,effective_date, item_type_id)values('||barcode_id||','''||barcode||''','''||uuid||''','||disease_id||',TO_DATE('''||to_char(effective_date,'MM/DD/YYYY')||''',''MM/DD/YYYY''),'||item_type_id||');' from barcode_history WHERE barcode in (select distinct bb.barcode from biospecimen_barcode bb, biospecimen_to_file bf, file_to_archive fa,archive_info a, disease d, platform p where a.disease_id = d.disease_id and   d.disease_abbreviation = replace_disease and   a.platform_id= p.platform_id and   p.platform_name = replace_platform and a.archive_id=fa.archive_id and fa.file_id = bf.file_id and bf.biospecimen_id=bb.biospecimen_id);

select 'commit;' from dual;

select distinct CASE WHEN shipped_date is null THEN 'INSERT INTO shipped_biospecimen (shipped_biospecimen_id,uuid,shipped_item_type_id,built_barcode,project_code,tss_code,bcr_center_id,participant_code,is_viewable,is_redacted,shipped_date) values ('||bb.shipped_biospecimen_id||','''||bb.uuid||''','||bb.shipped_item_type_id||','''||bb.built_barcode||''','''||bb.project_code||''','''||bb.tss_code||''','''||bb.bcr_center_id||''','''||bb.participant_code||''','||bb.is_viewable||','||bb.is_redacted||',null);' ELSE 'INSERT INTO shipped_biospecimen (shipped_biospecimen_id,uuid,shipped_item_type_id,built_barcode,project_code,tss_code,bcr_center_id,participant_code,is_viewable,is_redacted,shipped_date) values ('||bb.shipped_biospecimen_id||','''||bb.uuid||''','||bb.shipped_item_type_id||','''||bb.built_barcode||''','''||bb.project_code||''','''||bb.tss_code||''','''||bb.bcr_center_id||''','''||bb.participant_code||''','||bb.is_viewable||','||bb.is_redacted||',TO_DATE('''||to_char(bb.shipped_date,'MM/DD/YYYY')||''',''MM/DD/YYYY''));' END from shipped_biospecimen bb, shipped_biospecimen_file bf, file_to_archive fa,archive_info a, disease d, platform p where a.disease_id = d.disease_id and   d.disease_abbreviation = replace_disease and   a.platform_id= p.platform_id and   p.platform_name = replace_platform and a.archive_id=fa.archive_id and fa.file_id = bf.file_id and bf.shipped_biospecimen_id=bb.shipped_biospecimen_id;

select 'commit' from dual; 

select distinct 'INSERT INTO shipped_biospecimen_element (shipped_biospecimen_element_id,shipped_biospecimen_id,element_type_id, element_value) VALUES ('||sbe.shipped_biospecimen_element_id||','||sbe.shipped_biospecimen_id||','||sbe.element_type_id||','''||sbe.element_value||''');' from shipped_biospecimen_element sbe,shipped_biospecimen bb, shipped_biospecimen_file bf, file_to_archive fa,archive_info a, disease d, platform p where a.disease_id = d.disease_id and d.disease_abbreviation = replace_disease and a.platform_id= p.platform_id and p.platform_name = replace_platform and a.archive_id=fa.archive_id and fa.file_id = bf.file_id and bf.shipped_biospecimen_id=bb.shipped_biospecimen_id and bb.shipped_biospecimen_id = sbe.shipped_biospecimen_id;

select 'commit' from dual; 

 
 