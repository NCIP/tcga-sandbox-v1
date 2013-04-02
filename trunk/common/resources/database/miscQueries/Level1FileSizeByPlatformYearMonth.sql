select to_char(date_added,'YYYY/MM') as yearMonth,  p.platform_alias,sum(u.file_size)/1048576 as size
  from archive_info a,file_info f, file_info_url u, file_data_level l, platform_info p
   where a.id=f.file_archive_id 
   and f.id = u.file_id
   and f.id = l.file_info_id
   and l.level_number = 1
   and a.platform_id = p.id
   group by  to_char(date_added,'YYYY/MM'),p.platform_alias
   order by 1, 2,3;
