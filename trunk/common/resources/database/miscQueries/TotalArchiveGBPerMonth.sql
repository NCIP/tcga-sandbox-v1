select to_char(date_added,'YYYY/MM') as month,sum(final_size_kb)/1000000 as total_gb
from archive_info 
where is_latest=1
and final_size_kb is not null
group by to_char(date_added,'YYYY/MM')
order by to_char(date_added,'YYYY/MM');