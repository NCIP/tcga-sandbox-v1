update platform set base_data_type_id=40 where platform_name='IlluminaHiSeq_DNASeqC';

update file_info
set data_type_id=40
where file_id in
(select distinct f.file_id
from file_info f, file_to_archive f2a, archive_info a, platform p
where a.platform_id= p.platform_id and a.archive_id= f2a.archive_id
and f2a.file_id=f.file_id and p.platform_name='IlluminaHiSeq_DNASeqC');

insert into disease(disease_id, disease_abbreviation, disease_name, active) values(34, 'UCS', 'Uterine Carcinosarcoma', 1);

commit;