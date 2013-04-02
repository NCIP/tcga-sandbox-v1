WITH ship AS
(SELECT DISTINCT
            sb.shipped_biospecimen_id,
            sb.uuid as UUID,
            sb.built_barcode as Barcode,
            decode(sb.shipped_item_type_id,1,'Aliquot',2,'Shipped Portion') as Element_Type,
            d.disease_Abbreviation as Disease_Study,
            sb.participant_code as Participant_Code,
            substr(sb.built_barcode,14,2) AS Sample_Code,
            substr(sb.built_barcode,16,1) AS Vial,
            sb.bcr_center_id as Center_Code,
            r_ctr.center_type_code as Center_Type,
            decode(sb.shipped_item_type_id,1,substr(sb.built_barcode,22,4),2,substr(sb.built_barcode,21,4),3,null) AS Plate_ID,
            b_ctr.short_name as BCR,
            r_ctr.short_name as Receiving_Center,
            a.serial_index as Batch_Number,
            sb.shipped_date as Ship_Date,
            sb.tss_code as TSS_Code            
     FROM   dcccommon.shipped_biospecimen sb,
            dcccommon.shipped_biospec_bcr_archive sba,
            dcccommon.archive_info a,
            dcccommon.center_to_bcr_center cbc,
            dcccommon.center b_ctr,
            dcccommon.center r_ctr,
            dcccommon.disease d
     WHERE  sb.is_viewable = 1
     AND    sb.shipped_biospecimen_id = sba.shipped_biospecimen_id
     AND    sba.archive_id = a.archive_id
     AND    a.is_latest = 1
     AND    a.deploy_status = 'Available'
     AND    a.disease_id = d.disease_id
     AND    a.center_id = b_ctr.center_id 
     AND    sb.bcr_center_id = cbc.bcr_center_id
     AND    cbc.center_id = r_ctr.center_id
       )
SELECT  DISTINCT    
            ship.UUID,
            ship.Barcode,
            ship.Element_Type,
            ship.Disease_Study,
            ship.Participant_Code,
            ship.Sample_Code,
            ship.Vial,
            ship.Center_Code,
            ship.Center_Type,
            ship.Plate_ID,
            ship.BCR,
            ship.Receiving_Center,
            ship.Batch_Number,
            ship.Ship_Date,
            ship.TSS_Code,
            ltrim(rtrim(xmlagg (xmlelement (e, v.platform || ',')).extract ('//text()'), ','),',') Platforms,
            ltrim(rtrim(xmlagg (xmlelement (e, v.Data_Type || ',')).extract ('//text()'), ','),',') as Data_Types
FROM        dcccommon.ship,
(SELECT  DISTINCT    
            sb.shipped_biospecimen_id,
            DECODE(p.platform_alias,'bio','',p.platform_alias) as Platform,
            DECODE(p.platform_alias,'bio','',dt.name) as Data_Type
FROM        dcccommon.shipped_biospecimen sb,
            dcccommon.shipped_biospecimen_file sbf,
            dcccommon.file_to_archive fa,
            dcccommon.archive_info a,
            dcccommon.platform p,
            dcccommon.data_type dt
WHERE       sb.is_viewable = 1
AND         sb.shipped_biospecimen_id = sbf.shipped_biospecimen_id
AND         sbf.file_id = fa.file_id
AND         fa.archive_id = a.archive_id
AND         a.is_latest = 1
AND         a.platform_id = p.platform_id
AND         p.base_data_type_id = dt.data_type_id) v
WHERE ship.shipped_biospecimen_id = v.shipped_biospecimen_id
GROUP BY
            ship.UUID,
            ship.Barcode,
            ship.Element_Type,
            ship.Disease_Study,
            ship.Participant_Code,
            ship.Sample_Code,
            ship.Vial,
            ship.Center_Code,
            ship.Center_Type,
            ship.Plate_ID,
            ship.BCR,
            ship.Receiving_Center,
            ship.Batch_Number,
            ship.Ship_Date,
            ship.TSS_Code
ORDER BY Barcode
