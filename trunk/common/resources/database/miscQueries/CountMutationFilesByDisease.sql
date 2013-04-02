select count(v.file_id), v.tumor_abbreviation
from
(select distinct m.file_id,t.tumor_abbreviation
 from file_info f, archive_info a, maf_info m, tumor_info t
where m.file_id = f.id 
and f.file_archive_id = a.id
and a.is_latest=1
and a.tumor_id = t.id)  as v
group by v.tumor_abbreviation;