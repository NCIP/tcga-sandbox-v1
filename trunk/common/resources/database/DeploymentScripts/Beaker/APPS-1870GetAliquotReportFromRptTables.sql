alter table samples_sent_by_bcr add batch_number number(10,0);
alter table latest_samples_received_by_dcc add (submit_level1 char(1),submit_level2 char(1), submit_level3 char(1));
CREATE OR REPLACE PROCEDURE build_sample_summary_report
IS
BEGIN

    EXECUTE IMMEDIATE 'TRUNCATE TABLE sample_level_count';
    INSERT INTO sample_level_count
    (
      sample_level_count_id,disease_abbreviation,  center_name,  center_type_code ,  portion_analyte_code, platform , data_level, sampleCount
    )
    SELECT getReportSequence(), d.disease_abbreviation,c.domain_name, c.center_type_code, 
           bb.portion_analyte_code, p.platform_alias, f.level_number,
           NVL(count(distinct bb.sample),0) as sampleCount 
    FROM center c, archive_info a, platform p, file_info f, 
         biospecimen_to_file bf, biospecimen_breakdown_all bb, disease d , file_to_archive fa 
    WHERE c.center_id = a.center_id
    AND   a.platform_id = p.platform_id  
    AND   a.archive_id = fa.archive_id  
    AND   fa.file_id = f.file_id
    AND   f.file_id = bf.file_id
    AND   a.disease_id = d.disease_id 
    AND   bf.biospecimen_id = bb.biospecimen_id  
    AND   bb.is_viewable=1
    AND   a.is_latest = 1  
    AND   a.deploy_status = 'Available' 
    GROUP BY c.domain_name, c.center_type_code, bb.portion_analyte_code, platform_alias, d.disease_abbreviation,f.level_number;

    /*
    ** Get all the samples the centers sent; path to GSC centers is thru biospecimen_gsc_file_mv,
    ** and the path to CGCC is thru biospecimen_to_file
    */
    EXECUTE IMMEDIATE 'TRUNCATE TABLE latest_samples_received_by_dcc';
    INSERT INTO latest_samples_received_by_dcc
    (
        samples_received_id,
        disease_abbreviation, 
        center_id, 
        center_name, 
        center_type_code, 
        platform, 
        biospecimen_id, 
        barcode, 
        sample, 
        portion_analyte_code, 
        is_valid, 
        is_viewable,
        date_received,
        submit_level1,
        submit_level2,
        submit_level3
    )
    SELECT
        getReportSequence,
        v.disease_abbreviation, 
        v.center_id, 
        v.domain_name, 
        v.center_type_code, 
        v.platform, 
        v.biospecimen_id, 
        v.barcode, 
        v.sample, 
        v.portion_analyte_code,
        v.is_valid, 
        v.is_viewable,
        v.date_received,
        v.submit_level1,
        v.submit_level2,
        v.submit_level3
    FROM
    (SELECT d.disease_abbreviation, c.center_id, c.domain_name, c.center_type_code, p.platform_alias as platform, bb.biospecimen_id, 
           bb.barcode, bb.sample, bb.portion_analyte_code,bb.is_valid, bb.is_viewable, min(a.date_added)  as date_received,
           max(DECODE (f.level_number,1,'Y','N')) as submit_level1,max(DECODE (f.level_number,2,'Y','N')) submit_level2,
           max(DECODE (f.level_number,3,'Y','N')) submit_level3
    FROM archive_info a, center c, platform p, disease d, file_to_archive fa, file_info f,
          biospecimen_to_file bf, biospecimen_breakdown_all bb 
    WHERE a.is_latest = 1  
    AND   a.deploy_status = 'Available' 
    AND   a.center_id = c.center_id
    AND   c.center_type_code != 'BCR'
    AND   a.platform_id = p.platform_id
    AND   a.disease_id = d.disease_id
    AND   a.archive_id = fa.archive_id
    AND   fa.file_id = f.file_id
    AND   f.file_id = bf.file_id
    AND   bf.biospecimen_id = bb.biospecimen_id
    AND   bb.is_viewable=1
    group by d.disease_abbreviation, c.center_id, c.domain_name, c.center_type_code, p.platform_alias, bb.biospecimen_id, 
           bb.barcode, bb.sample, bb.portion_analyte_code,bb.is_valid, bb.is_viewable) v;
    /*
    ** Get latest samples sent out by the BCR and store them in an intermediate table so that this join
    ** does not have to be done over and over
    */
    EXECUTE IMMEDIATE 'TRUNCATE TABLE samples_sent_by_bcr';
    INSERT INTO samples_sent_by_bcr
    (
      sample_sent_id,
      disease_abbreviation,
      center_id,
      center_name,
      center_type_code,
      biospecimen_id,
      barcode,
      sample,
      portion_analyte_code,
      ship_date,
      batch_number
    ) 
    SELECT getReportSequence,
           v.disease_abbreviation,
           v.center_id,
           v.domain_name,
           v.center_type_code,
           v.biospecimen_id,
           v.barcode,
           v.sample,
           v.portion_analyte_code,
           v.ship_date,
           v.batch_number
    FROM
    (SELECT d.disease_abbreviation,c.center_id,c.domain_name,cbc.center_type_code,b.biospecimen_id,
           b.barcode,b.sample,b.portion_analyte_code, b.ship_date, a.serial_index as batch_number
    FROM   biospecimen_breakdown_all b, bcr_biospecimen_to_archive ba, Archive_info a, disease d, 
           center c, center_to_bcr_center cbc  
    WHERE c.center_id=cbc.center_id  
    AND   cbc.bcr_center_id = b.bcr_center_id
    AND   b.biospecimen_id = ba.biospecimen_id 
    AND   ba.archive_id = a.archive_id 
    AND   a.disease_id = d.disease_id   
    AND   a.is_latest = 1
    AND   b.is_viewable=1
    AND   a.deploy_status = 'Available') v;
    
    DELETE FROM sample_summary_report_detail;
    /*
    ** Do the initial insert into the sample_summary_report_detail table, getting totals for 
    ** samples dcc received from the centers and samples bcr sent to the centers. We need to get all the 
    ** center samples whether there is a match in BCR samples, and all the BCR samples whether or not there 
    ** is a match in center samples. That is why there are 2 queries that look almost the same, UNION'ed to
    ** exclude duplicates.
    */
    INSERT INTO sample_summary_report_detail (
        sample_summary_id,
        disease_abbreviation,
        center_name,
        center_type_code,
        portion_analyte_code,
        platform,
        total_bcr_sent,
        total_centers_sent
    )
    SELECT   
       getReportSequence(),
       v.disease_abbreviation,
       v.center_name, 
       v.center_type_code, 
       v.portion_analyte_code, 
       v.platform, 
       v.samples_sent, 
       v.samples_dcc_received
    FROM
    (SELECT bcr.disease_abbreviation, bcr.center_name, bcr.center_type_code, bcr.portion_analyte_code, centers.platform, bcr.samples_sent, centers.samples_dcc_received
    FROM
    (SELECT disease_abbreviation, NVL(count(distinct sample), 0) as samples_dcc_received, center_name, center_type_code, portion_analyte_code, platform
    from latest_samples_received_by_dcc
    GROUP BY disease_abbreviation, center_name,center_type_code,portion_analyte_code, platform) centers RIGHT OUTER JOIN
    (SELECT disease_abbreviation,count(distinct sample) as samples_sent, center_name, center_type_code, portion_analyte_code
    from samples_sent_by_bcr  
    GROUP BY disease_abbreviation,center_name, center_type_code, portion_analyte_code) bcr
    ON    centers.center_name = bcr.center_name
    AND   centers.center_type_code = bcr.center_type_code
    AND   centers.portion_analyte_code = bcr.portion_analyte_code
    AND   centers.disease_abbreviation = bcr.disease_abbreviation
    UNION
    SELECT centers.disease_abbreviation, centers.center_name, centers.center_type_code, centers.portion_analyte_code, centers.platform, bcr.samples_sent, centers.samples_dcc_received 
    FROM
    (SELECT disease_abbreviation,NVL(count(distinct sample),0) as samples_dcc_received, center_name, center_type_code, portion_analyte_code, platform
    from latest_samples_received_by_dcc
    GROUP BY disease_abbreviation, center_name,center_type_code,portion_analyte_code, platform) centers LEFT OUTER JOIN
    (SELECT disease_abbreviation,COUNT(distinct sample) as samples_sent, center_name, center_type_code, portion_analyte_code
    from samples_sent_by_bcr  
    GROUP BY disease_abbreviation,center_name, center_type_code, portion_analyte_code) bcr
    ON    centers.center_name = bcr.center_name
    AND   centers.center_type_code = bcr.center_type_code
    AND   centers.portion_analyte_code = bcr.portion_analyte_code
    AND   centers.disease_abbreviation = bcr.disease_abbreviation
    ORDER by disease_abbreviation,center_name,center_type_code,portion_analyte_code) v;
    
    /*
    ** calculate unaccounted for, both bcr and centers
    */

    /*
    ** centers unaccounted for would be samples the bcr sent that the center did not send
    */
    UPDATE sample_summary_report_detail s
    SET    total_center_unaccounted =
    (SELECT  NVL(GREATEST(s.total_bcr_sent - v.samples, 0),0)
    FROM
    (SELECT  COUNT(DISTINCT l.sample) as samples,l.center_name,l.disease_abbreviation,l.center_type_code,l.portion_analyte_code,l.platform
    FROM    latest_samples_received_by_dcc l, samples_sent_by_bcr bc
    WHERE   l.sample = bc.sample
    AND     l.disease_abbreviation = bc.disease_abbreviation
    AND     l.center_name = bc.center_name
    AND     l.center_type_code = bc.center_type_code
    AND     l.portion_analyte_code = bc.portion_analyte_code
    GROUP BY l.disease_abbreviation,l.center_name,l.center_type_code,l.portion_analyte_code,l.platform) v
    WHERE v.disease_abbreviation = s.disease_abbreviation
    AND   v.center_name = s.center_name 
    AND   v.center_type_code = s.center_type_code 
    AND   v.portion_analyte_code = s.portion_analyte_code
    AND   v.platform = s.platform) ;

    /*
    ** bcr unaccounted for would be samples the centers sent that the bcr did not send
    */

    UPDATE sample_summary_report_detail s
    SET    total_bcr_unaccounted =
    (SELECT COUNT(*) FROM 
    (SELECT s.sample,l.center_name,l.disease_abbreviation,l.center_type_code,l.portion_analyte_code,l.platform
    FROM    latest_samples_received_by_dcc l LEFT OUTER JOIN  
        samples_sent_by_bcr s ON l.sample = s.sample 
        AND l.disease_abbreviation = s.disease_abbreviation
        AND l.center_name = s.center_name 
        AND l.center_type_code = s.center_type_code 
        AND l.portion_analyte_code = s.portion_analyte_code) v
    WHERE v.sample IS NULL
    AND   v.disease_abbreviation = s.disease_abbreviation
    AND   v.center_name = s.center_name
    AND   v.center_type_code = s.center_type_code
    AND   v.portion_analyte_code = s.portion_analyte_code
    AND   v.platform = s.platform);    
    
    /* 
    **  now account for those that the above would not cover
    **     if total_bcr_sent > 0 and total_center_sent = 0 , then total_center_unaccounted = total_bcr_sent
    **     if total_center_sent > 0 and total_bcr_sent = 0 , then total_bcr_unaccounted = total_center_sent
    */
    UPDATE sample_summary_report_detail
    SET    total_center_unaccounted = total_bcr_sent
    WHERE  (total_centers_sent = 0
    OR     total_centers_sent IS NULL)
    AND    total_bcr_sent > 0;
    
    UPDATE sample_summary_report_detail
    SET    total_bcr_unaccounted = total_centers_sent
    WHERE  (total_bcr_sent = 0
    OR     total_bcr_sent IS NULL)
    AND    total_centers_sent > 0;
    
    /*
    ** set the level totals: how many samples had level 1, how many level 2 and how many level 3 data
    */
    UPDATE sample_summary_report_detail s
    SET   total_with_level1 =
    (SELECT sampleCount
     FROM sample_level_count l
     WHERE l.disease_abbreviation = s.disease_abbreviation
     AND   l.center_name = s.center_name
     AND   l.center_type_code = s.center_type_code
     AND   l.portion_analyte_code = s.portion_analyte_code
     AND   l.platform = s.platform
     AND   l.data_level = 1);
     
    UPDATE sample_summary_report_detail s
    SET   total_with_level2 =
    (SELECT sampleCount
     FROM sample_level_count l
     WHERE l.disease_abbreviation = s.disease_abbreviation
     AND   l.center_name = s.center_name
     AND   l.center_type_code = s.center_type_code
     AND   l.portion_analyte_code = s.portion_analyte_code
     AND   l.platform = s.platform
     AND   l.data_level = 2);

    UPDATE sample_summary_report_detail s
    SET   total_with_level3 =
    (SELECT sampleCount
     FROM sample_level_count l
     WHERE l.disease_abbreviation = s.disease_abbreviation
     AND   l.center_name = s.center_name
     AND   l.center_type_code = s.center_type_code
     AND   l.portion_analyte_code = s.portion_analyte_code
     AND   l.platform = s.platform
     AND   l.data_level = 3);

    /*
    ** now figure out which CGCC centers and platform sent level 4 data (whether in latest archives or not) and
    ** update the field in the sample_summary_report_detail. Since there could be level4 data in latest and not latest
    ** archives; postgres will not allow an order by in the subquery, so do two updates. (erghh) DREP-16 bug filed.
    */
    
    UPDATE sample_summary_report_detail s
    SET   level_4_submitted = 
    (SELECT DISTINCT 'Y*' 
     FROM file_info f, disease d, file_to_archive fa, archive_info a , center c, platform p
     WHERE a.is_latest = 0
     AND   a.disease_id = d.disease_id
     AND   a.platform_id = p.platform_id
     AND   a.center_id = c.center_id
     AND   a.archive_id = fa.archive_id  
     AND   fa.file_id = f.file_id
     AND   f.level_number = 4
     AND   c.domain_name = s.center_name 
     AND   p.platform_alias = s.platform
     AND   s.center_type_code = 'CGCC');

     
    UPDATE sample_summary_report_detail s
    SET   level_4_submitted = 
    (SELECT DISTINCT 'Y' 
     FROM  file_info f, disease d, archive_info a , center c, platform p, file_to_archive fa
     WHERE a.is_latest = 1
     AND   a.disease_id = d.disease_id
     AND   a.platform_id = p.platform_id
     AND   a.center_id = c.center_id
     AND   a.archive_id = fa.archive_id  
     AND   fa.file_id = f.file_id  
     AND   f.level_number = 4
     AND   c.domain_name = s.center_name 
     AND   p.platform_alias = s.platform
     AND   s.center_type_code = 'CGCC');

    /*
    ** set all numeric fields to zero if they are null for ease of display and set the last_refresh timestamp
    */

    UPDATE sample_summary_report_detail
    SET total_with_level3           = NVL(total_with_level3,0),
        total_with_level2           = NVL(total_with_level2,0),
        total_with_level1           = NVL(total_with_level1,0),
        total_bcr_sent              = NVL(total_bcr_sent,0),
        total_centers_sent          = NVL(total_centers_sent,0),
        total_bcr_unaccounted       = NVL(total_bcr_unaccounted,0),
        total_center_unaccounted    = NVL(total_center_unaccounted,0),
        last_refresh                = CURRENT_TIMESTAMP;

    COMMIT;
END;
/