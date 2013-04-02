SELECT s.barcode as aliquot,s.ship_date,s.center_name,s.center_type,s.tumor_abbreviation
FROM   samples_sent_by_bcr s LEFT OUTER JOIN
       latest_samples_received_by_dcc l ON s.biospecimen_id = l.biospecimen_id
                             AND s.center_id = l.center_id
                             AND s.center_type = l.center_type
                             AND s.tumor_abbreviation = l.tumor_abbreviation
WHERE l.biospecimen_id is null
ORDER BY s.center_name,s.tumor_abbreviation,s.center_type,s.barcode,s.ship_date;
