SELECT l.barcode as aliquot,l.date_received,l.center_name,l.center_type,l.tumor_abbreviation
FROM   latest_samples_received_by_dcc l JOIN
       biospecimen_barcode b ON l.biospecimen_id=b.biospecimen_id 
                           AND b.is_viewable=1 LEFT OUTER JOIN
       samples_sent_by_bcr s ON l.biospecimen_id = s.biospecimen_id
                             AND l.center_id = s.center_id
                             AND l.center_type = s.center_type
                             AND l.tumor_abbreviation = s.tumor_abbreviation
WHERE s.biospecimen_id is null
ORDER BY l.center_name,l.tumor_abbreviation,l.center_type,l.barcode,l.date_received;