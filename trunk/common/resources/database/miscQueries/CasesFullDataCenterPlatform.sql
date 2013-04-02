select distinct center_name,platform,patient
from
(SELECT distinct c.center_name, m.center_type, p.platform_alias as platform, count(distinct l.level_number) as levels ,bb.patient
	FROM center_info c, archive_info a, platform_info p, file_info f, center_bcr_center_map m,
	     biospecimen_to_file bf, biospecimen_breakdown_all bb, file_data_level l, tumor_info t  
	WHERE c.id = a.center_id  
	AND   c.id = m.center_id
	AND   a.platform_id = p.id  
	AND   a.id = f.file_archive_id  
	AND   f.id = bf.file_info_id  
	AND   f.id = l.file_info_id 
	AND   l.level_number in (1,2,3)
	AND   a.tumor_id = t.id 
	and   t.tumor_abbreviation='OV'
	AND   bf.biospecimen_id = bb.biospecimen_id 
	AND   bb.is_viewable=1
	AND   a.is_latest = 1  
	AND   a.deploy_status = 'Available' 
	AND   m.center_type='CGCC'
	group by c.center_name, m.center_type, p.platform_alias, bb.patient) as v
WHERE v.levels = 3;

select count(distinct patient) as cases,center_name,platform
from
(SELECT distinct c.center_name, m.center_type, p.platform_alias as platform, count(distinct l.level_number) as levels ,bb.patient
	FROM center_info c, archive_info a, platform_info p, file_info f, center_bcr_center_map m,
	     biospecimen_to_file bf, biospecimen_breakdown_all bb, file_data_level l, tumor_info t  
	WHERE c.id = a.center_id  
	AND   c.id = m.center_id
	AND   a.platform_id = p.id  
	AND   a.id = f.file_archive_id  
	AND   f.id = bf.file_info_id  
	AND   f.id = l.file_info_id 
	AND   l.level_number in (1,2,3)
	AND   a.tumor_id = t.id 
	and   t.tumor_abbreviation='OV'
	AND   bf.biospecimen_id = bb.biospecimen_id  
	AND   bb.is_viewable=1
	AND   a.is_latest = 1  
	AND   a.deploy_status = 'Available' 
	AND   m.center_type='CGCC'
	group by c.center_name, m.center_type, p.platform_alias, bb.patient) as v
WHERE v.levels = 3;

select count(distinct patient) as cases
from
(SELECT distinct c.center_name, m.center_type, p.platform_alias as platform, count(distinct l.level_number) as levels ,bb.patient
	FROM center_info c, archive_info a, platform_info p, file_info f, center_bcr_center_map m,
	     biospecimen_to_file bf, biospecimen_breakdown_all bb, file_data_level l, tumor_info t  
	WHERE c.id = a.center_id  
	AND   c.id = m.center_id
	AND   a.platform_id = p.id  
	AND   a.id = f.file_archive_id  
	AND   f.id = bf.file_info_id  
	AND   f.id = l.file_info_id 
	AND   l.level_number in (1,2,3)
	AND   a.tumor_id = t.id 
	and   t.tumor_abbreviation='OV'
	AND   bf.biospecimen_id = bb.biospecimen_id  
	AND   bb.is_viewable=1
	AND   a.is_latest = 1  
	AND   a.deploy_status = 'Available' 
	AND   m.center_type='CGCC'
	group by c.center_name, m.center_type, p.platform_alias, bb.patient) as v
WHERE v.levels = 3;