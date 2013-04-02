select platform_alias,to_char(date_added,'YYYY/MM') as yearMonth, sum(u.file_size)/1048576 as size
  from archive_info a,file_info f, file_info_url u, platform_info p
   where a.id=f.file_archive_id 
   and a.platform_id = p.id
   and f.id = u.file_id
   group by platform_alias , to_char(date_added,'YYYY/MM')
   order by 2