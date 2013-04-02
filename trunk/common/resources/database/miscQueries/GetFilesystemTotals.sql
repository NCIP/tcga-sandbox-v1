WITH asum as
(select sum(a.final_size_kb)/1024 as archive_mb
from archive_info a
where  (a.is_latest=1
or     (a.is_latest=0 and a.deploy_status != 'Obsolete'))
),
fsum as
(select sum(f.file_size)/1024 as file_kb
from file_to_archive fa, file_info f, archive_info a
where (a.is_latest=1
or    (a.is_latest=0 and a.deploy_status != 'Obsolete'))
and a.archive_id=fa.archive_id
and fa.file_id=f.file_id)
SELECT asum.archive_mb as total_archive_mb, fsum.file_kb as total_file_kb
FROM asum, fsum;