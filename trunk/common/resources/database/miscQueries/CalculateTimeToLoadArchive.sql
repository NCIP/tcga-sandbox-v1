COLUMN center HEADING 'CENTER'
COLUMN platform HEADING 'PLATFORM'
COLUMN experiment HEADING 'EXPER.'
COLUMN source_file_type HEADING 'FILE TYPE'
COLUMN elapsed HEADING 'LOAD TIME'
COLUMN start_date HEADING 'START'
COLUMN end_date HEADING 'END'
BREAK ON experiment 

select c.domain_name as center, p.platform_name as platform, d.experiment_id as experiment, cast(max(f.load_end_date) as timestamp) - cast(min(f.load_start_date) as timestamp) as elapsed,
count(distinct h.hybridization_ref_id) as barcodes, cast(min(f.load_start_date) as timestamp) as start_date, cast(max(f.load_end_date) as timestamp) as end_date
from data_set d, data_set_file f, center c, platform p, hybrid_ref_data_set h
where d.data_level = 2
and d.load_complete=1
--and d.platform_id=1
and d.data_set_id = f.data_set_id
and d.center_id=c.center_id
and d.platform_id = p.platform_id
and d.data_set_id=h.data_set_id
group by d.experiment_id, c.domain_name,p.platform_name
order by d.experiment_id;