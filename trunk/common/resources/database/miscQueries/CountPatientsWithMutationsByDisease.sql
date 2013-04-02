select count(v.patient) as cases, v.tumor_abbreviation
from
(select distinct bb.patient,t.tumor_abbreviation
 from biospecimen_breakdown_all bb, file_info f, archive_info a, maf_info m, tumor_info t
where m.tumor_sample_barcode = bb.barcode 
and m.file_id = f.id 
and f.file_archive_id = a.id
and a.is_latest = 1
and a.tumor_id = t.id)  as v
group by v.tumor_abbreviation