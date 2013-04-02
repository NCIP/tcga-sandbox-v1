	SELECT distinct a2.serial_index as batch, p.platform_alias as platform, c.center_name			
	FROM archive_info a, center_info c, platform_info p, file_info f, 			
	      biospecimen_to_file bf, ( SELECT b.biospecimen_id, 			
        b.barcode, 				
        b.project || '-' || b.collection_center || '-' || b.barcode || '-' || b.sample_type || b.sample_sequence || '-' || b.portion_sequence || b.portion_analyte AS aliquot, 				
        b.project || '-' || b.collection_center || '-' || b.patient || '-' || b.sample_type AS sample, 				
        b.project || '-' || b.collection_center || '-' || b.patient AS full_patient,				
        b.project, 				
        b.collection_center, 				
        b.patient, 				
        b.sample_type, 				
        b.sample_sequence, 				
        b.portion_sequence, 				
        b.portion_analyte, 				
        b.plate_id, 				
        b.bcr_center_id, 				
        b.is_valid, 				
        b.is_viewable				
        FROM biospecimen_barcode b) bb 	, biospecimen_to_archive ba, archive_info a2			
	WHERE a.deploy_status = 'Available' 			
	and   a.is_latest=1			
	and   bb.biospecimen_id = ba.biospecimen_id			
	and   ba.archive_id = a2.id			
	and   a2.is_latest=1			
	and   (a2.serial_index = 22 or a2.serial_index = 22)			
	AND   a.center_id = c.id			
	AND   a.platform_id = p.id			
	AND   a.tumor_id = 3			
	AND   a.id = f.file_archive_id			
	AND   f.id = bf.file_info_id			
	AND   bf.biospecimen_id = bb.biospecimen_id			
	AND   bb.is_viewable=1 AND bb.is_valid = 1			
	ORDER BY 1,2,3;			