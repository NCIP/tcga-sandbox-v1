SELECT distinct b.tumor_abbreviation as disease, b.barcode as AliquotId,a.serial_index as bcr_batch, 
       b.center_name||' ('|| b.center_type||')' as receiving_center, 
       v.platform,
       COALESCE(v.level_one_data,0) as level_one_data,
       COALESCE(v.level_two_data,0) as level_two_data,
       COALESCE(v.level_three_data, 0) as level_three_data    
FROM   samples_sent_by_bcr b INNER JOIN biospecimen_to_archive ba
       ON b.biospecimen_id = ba.biospecimen_id
       INNER JOIN archive_info a ON  ba.archive_id = a.id
                  AND    a.is_latest = 1
                  AND    a.deploy_status = 'Available'
       LEFT OUTER JOIN 
       (select d.tumor_abbreviation, d.platform, d.barcode,d.center_name,d.center_type,
       CASE WHEN s.total_with_level1 > 1 THEN 1 ELSE 0 END as level_one_data,
       CASE WHEN s.total_with_level2 > 1 THEN 1 ELSE 0 END as level_two_data,
       CASE WHEN s.total_with_level3 > 1 THEN 1 ELSE 0 END as level_three_data   
       FROM latest_samples_received_by_dcc d,sample_summary_report_detail s
       WHERE d.center_name = s.center_name 
       AND   d.center_type = s.center_type
       AND   d.platform = s.platform
       AND   d.tumor_abbreviation = s.tumor_abbreviation) as v
       ON    b.tumor_abbreviation = v.tumor_abbreviation
             AND    b.barcode = v.barcode
             AND    b.center_name = v.center_name
             AND    b.center_type = v.center_type
             AND    b.tumor_abbreviation = v.tumor_abbreviation;


