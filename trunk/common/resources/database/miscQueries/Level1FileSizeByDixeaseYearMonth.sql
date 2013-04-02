 select to_char(date_added,'YYYY/MM') as yearMonth, t.tumor_abbreviation,sum(u.file_size)/1048576 as size
  from archive_info a,file_info f, file_info_url u, file_data_level l, , tumor_info t
   where a.id=f.file_archive_id 
   and f.id = u.file_id
   and f.id = l.file_info_id
   and l.level_number = 1
   and a.tumor_id = t.id
   group by  to_char(date_added,'YYYY/MM'),t.tumor_abbreviation
   order by 1, 2;

