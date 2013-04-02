select  v.tumor_abbreviation, v.patient as case,count(v.ncbi_trace_id) as traceIds
from
(select distinct bb.patient,t.tumor_abbreviation, bt.ncbi_trace_id
 from biospecimen_breakdown_all bb, biospecimen_ncbi_trace bt, file_info f, archive_info a,tumor_info t
where bb.biospecimen_id = bt.biospecimen_id
and bt.file_info_id = f.id
and f.file_archive_id = a.id
and a.is_latest = 1
and a.tumor_id = t.id)  as v
group by v.tumor_abbreviation,v.patient