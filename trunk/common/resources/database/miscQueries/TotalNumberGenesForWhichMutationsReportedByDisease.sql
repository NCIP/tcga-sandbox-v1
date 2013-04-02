select count(entrez_gene_id), t.tumor_abbreviation
from maf_info m, file_info f, archive_info a, tumor_info t
where m.file_id = f.id
and f.file_archive_id=a.id 
and a.tumor_id=t.id
group by tumor_abbreviation