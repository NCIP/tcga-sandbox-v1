select p.display_name as platform, level_number, sum(file_size)/1048576
from archive_info a, platform_info p, tumor_info t, file_info f, file_info_url u, file_data_level l
where a.is_latest = 1
and a.deploy_location is not null
and a.platform_id = p.id
and a.tumor_id = 3
and a.id = f.file_archive_id
and f.id = u.file_id 
and f.id =l.file_info_id
group by p.display_name,level_number
order by 1,2