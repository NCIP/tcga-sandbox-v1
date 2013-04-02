select count(entrez_gene_id), f.file_name
from maf_info m, file_info f
where m.file_id = f.id
group by file_name;