select bb.barcode, archive_name, count(*)
from biospecimen_barcode bb, biospecimen_to_file bf, file_info f, file_to_archive fa , archive_info a
where a.archive_name like 'broad.mit.edu_GBM.HT_HG-U133A.Level_3%1004.0'
and a.archive_id=fa.archive_id 
and fa.file_id=f.file_id
and f.file_id=bf.file_id
and bf.biospecimen_id=bb.biospecimen_id
group by bb.barcode,archive_name having count(*) > 1
order by archive_name;
