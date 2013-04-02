select distinct v.* from
(select 
'select d.experiment_id,d.data_set_id,d.source_file_name,h.sample from data_set d, experiment e , hybrid_ref_data_set hd, hybridization_ref h where e.base_name = ''' || c.center_name||'_'|| t.tumor_abbreviation||'.'||p.platform_name || 
''' and e.data_deposit_batch = ' || serial_index ||
' and data_revision = ' || revision ||
' and e.experiment_id = d.experiment_id and d.data_level = 2 and d.use_in_dam = 1 and d.data_set_id=hd.data_set_id and hd.hybridization_ref_id=h.hybridization_ref_id;'
from archive_info a, center_info c, platform_info p, tumor_info t
where a.is_latest = 1
and a.deploy_location is not null
and a.center_id=c.id
and a.platform_id = p.id
and p.id = 10
and c.center_name='mskcc.org'
and a.tumor_id = t.id
and t.tumor_abbreviation = 'GBM'
order by c.center_name,t.tumor_abbreviation,p.platform_name,a.serial_index,a.revision) as v;
