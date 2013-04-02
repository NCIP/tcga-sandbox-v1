select fu.file_location_url
            from archive_info a, archive_type t, file_info f, file_info_url fu  
            where a.is_latest = 1 
            and a.archive_type_id = t.id 
            and a.id = f.file_archive_id 
            and f.file_name like '%sdrf%' 
            and f.id = fu.file_id 
            and t.type = 'mage-tab' 
UNION
select fu.file_location_url
            from archive_info a, file_info f, file_info_url fu  
            where a.is_latest = 1 
            and a.archive_name not like '%mage-tab%'
            and a.id = f.file_archive_id 
            and f.file_name like '%sdrf%' 
            and f.id = fu.file_id 
order by 1;
