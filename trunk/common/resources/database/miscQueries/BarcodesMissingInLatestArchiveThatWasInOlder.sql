SELECT DISTINCT b.barcode,c.center_name,b.is_viewable,b.is_valid
FROM   biospecimen_barcode b INNER JOIN
       biospecimen_to_file bf ON b.biospecimen_id = bf.biospecimen_id INNER JOIN
       file_info f ON bf.file_info_id = f.id INNER JOIN
       archive_info a ON f.file_archive_id = a.id AND a.is_latest=0 INNER JOIN
       center_info c ON a.center_id = c.id LEFT OUTER JOIN
	(SELECT DISTINCT b.biospecimen_id,a.center_id
	FROM   biospecimen_barcode b, biospecimen_to_file bf, file_info f, archive_info a, center_info c
	WHERE  b.biospecimen_id = bf.biospecimen_id  
	AND    bf.file_info_id = f.id
	AND    f.file_archive_id = a.id AND a.is_latest=1
	AND    a.center_id = c.id) as latest ON b.biospecimen_id = latest.biospecimen_id 
                                             AND  a.center_id = latest.center_id
WHERE latest.center_id IS NULL
ORDER BY center_name, barcode;