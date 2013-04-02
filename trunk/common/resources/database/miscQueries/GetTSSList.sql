SELECT tss.tss_code, tss.tss_definition, d.disease_abbreviation, c.short_name
FROM tissue_source_site tss, tss_to_disease tsd, disease d, center c
WHERE tss.bcr_center_id = c.center_id 
AND   tss.tss_code = tsd.tss_code 
AND   tsd.disease_id = d.disease_id 
ORDER BY tss_code;