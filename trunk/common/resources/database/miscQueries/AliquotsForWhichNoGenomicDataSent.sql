select distinct s.disease_abbreviation,s.center_name as sent_to_center,
s.barcode as aliquot
,max(decode(l.barcode,NULL,'No Genomic Data','Data Received'))  status  
from samples_sent_by_bcr s left outer join
     latest_samples_received_by_dcc l on s.biospecimen_id=l.biospecimen_id
Where l.barcode is null
group by s.disease_abbreviation,s.center_name,s.barcode
order by s.barcode 