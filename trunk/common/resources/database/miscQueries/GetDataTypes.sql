select distinct p.platform_alias,d.data_type_id,d.name, d.center_type_code,d.ftp_display,dv.level_number,v.visibility_name
from platform p, data_type_to_platform dp,data_type d, data_visibility dv, visibility v
where p.platform_id=dp.platform_id
and dp.data_type_id=d.data_type_id
and d.available=1 and d.data_type_id > 16
and d.data_type_id=dv.data_type_id
and dv.visibility_id=v.visibility_id
order by p.platform_alias,d.name;


select distinct p.platform_id,p.platform_alias,d.data_type_id,d.name, d.center_type_code,d.ftp_display,dv.level_number,v.visibility_name
from platform p, data_type_to_platform dp,data_type d, data_visibility dv, visibility v
where p.platform_id=dp.platform_id
and dp.data_type_id=d.data_type_id
and d.available=1 
and d.data_type_id=dv.data_type_id
and dv.visibility_id=v.visibility_id
order by p.platform_alias,d.name;