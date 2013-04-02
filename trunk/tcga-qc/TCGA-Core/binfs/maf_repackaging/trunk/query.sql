select distinct ai.deploy_location
from archive_info ai, file_info fi, file_to_archive fta
where ai.is_latest = 1
and ai.archive_id = fta.archive_id
and fi.file_id = fta.file_id
and fi.file_name like '%maf'
order by deploy_location