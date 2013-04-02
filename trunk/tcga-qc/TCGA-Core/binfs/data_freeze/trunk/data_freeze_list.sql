-- Update LINES 14 and 42 with your disease abbreviation(s)
-- Uncomment LINES 13 and 47 to only get latest archives
-- NOTE: Archives with null batch numbers were submitted before BCR archives had to be submitted before center archives

WITH bcr AS
  (SELECT DISTINCT sb.shipped_biospecimen_id,a.serial_index as batch_number
   FROM   shipped_biospecimen sb, shipped_biospecimen_file sbf, file_to_archive fa, archive_info a, center c, disease d
   WHERE  sb.shipped_biospecimen_id = sbf.shipped_biospecimen_id
   AND    sbf.file_id = fa.file_id
   AND    fa.archive_id = a.archive_id
   AND    a.center_id = c.center_id
   AND    c.center_type_code = 'BCR'
   --AND a.is_latest = 1
   AND    a.disease_id = d.disease_id and d.disease_abbreviation = 'KIRC')
   
   SELECT DISTINCT
          SUBSTR (built_barcode, 0, 12) AS patient_barcode,
          SUBSTR (built_barcode, 0, 16) AS sample_barcode,
          built_barcode AS aliquot_barcode,
          sb.uuid AS aliquot_uuid,
          dt.name AS datatype,
          p.platform_name,
          fa.file_location_url,
          at.data_level,
          a.archive_name,
          bcr.batch_number,
          a.date_added
    FROM shipped_biospecimen sb,
         disease d,
         center c,
         platform p,
         shipped_biospecimen_file sbf,
         file_to_archive fa,
         archive_info a,
         archive_type at,
         data_type dt,
         bcr
   WHERE sb.shipped_biospecimen_id = sbf.shipped_biospecimen_id
   AND   sbf.file_id = fa.file_id
   AND   fa.archive_id = a.archive_id
   AND   a.disease_id = d.disease_id
   AND   d.disease_abbreviation = 'KIRC'
   AND   a.archive_type_id = at.archive_type_id
   AND   a.platform_id = p.platform_id
   AND   p.base_data_type_id = dt.data_type_id
   AND   sb.SHIPPED_BIOSPECIMEN_ID = bcr.shipped_biospecimen_id(+)
   --AND a.is_latest = 1        
ORDER BY built_barcode,
         dt.name,
         p.platform_name,
         data_level;