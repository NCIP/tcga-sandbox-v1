select c.code,c.definition,c.tissue_id,t.tissue
 from collection_center c,tissue_info t
where c.code > 67
and c.tissue_id=t.id
order by code;