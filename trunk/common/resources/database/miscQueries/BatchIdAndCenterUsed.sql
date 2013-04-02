SELECT distinct a.serial_index as batch_number_id,t.tumor_abbreviation as disease_abbreviation,
t.tumor_name as study_name, c.short_name as bcr
FROM   biospecimen_to_archive ba , Archive_info a, tumor_info t, center_info c
WHERE  ba.archive_id = a.id 
AND    a.tumor_id = t.id 
and    a.deploy_status='Available'
and    a.is_latest = 1
AND    a.center_id = c.id ;