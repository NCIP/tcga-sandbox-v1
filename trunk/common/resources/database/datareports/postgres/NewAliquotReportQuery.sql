SELECT d.disease_abbreviation,c.domain_name, m.center_type_code, 
            bb.portion_analyte_code, p.platform_alias, bb.barcode, 
            max(DECODE (f.level_number,1,'Submitted','Not Submitted')) level1,
            max(DECODE (f.level_number,2,'Submitted','Not Submitted')) level2,
            max(DECODE (f.level_number,3,'Submitted','Not Submitted')) level3
    FROM center c, archive_info a, platform p, file_info f, center_to_bcr_center m,
         biospecimen_to_file bf, biospecimen_barcode bb, disease d , file_to_archive fa 
    WHERE c.center_id = a.center_id  
    AND   c.center_id = m.center_id
    AND   a.platform_id = p.platform_id  
    AND   a.archive_id = fa.archive_id  
    AND   fa.file_id = f.file_id
    AND   f.file_id = bf.file_id
    AND   a.disease_id = d.disease_id 
    AND   bf.biospecimen_id = bb.biospecimen_id  
    AND   a.is_latest = 1  
    AND   a.deploy_status = 'Available' 
  GROUP BY c.domain_name, m.center_type_code, bb.portion_analyte_code, platform_alias, d.disease_abbreviation,bb.barcode;