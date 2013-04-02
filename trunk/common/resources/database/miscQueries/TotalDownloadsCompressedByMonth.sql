col "Total Archives Downloaded" format 9,999,999
col "Total Comp Monthly Gbs" format 9,999,999.99

select sum(archivesDownloaded) as "Total Archives Downloaded", sum(total_mbs)/1024 as "Total Comp Monthly Gbs", theDate, sortDate
from
(select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgabrca.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgablca.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgacesc.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgacoad.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgadlbc.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgagbm.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgahnsc.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgakirc.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgakirp.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgalaml.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgalcll.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgalgg.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgalihc.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgalnnh.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgaluad.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgalusc.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgaov.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgapaad.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgaprad.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgaread.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgasald.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgaskcm.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgaskcm.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgastad.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgathca.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
union
select count(*) as archivesDownloaded, sum(value)/1048576 as total_mbs, to_char(action_time,'MM/YYYY') as theDate, to_char(action_time,'YYYYMM') as sortDate
from tcgaucec.portal_session_action
where portal_action_type_id = 37
--and action_time >= to_timestamp(sysdate - 365)
group by to_char(action_time,'MM/YYYY') , to_char(action_time,'YYYYMM')
)
group by theDate,sortDate
order by sortDate