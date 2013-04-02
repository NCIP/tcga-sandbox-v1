ALTER TABLE barcode_history ADD (item_type_id INTEGER );

-- set item type id for records that have a match in uuid_hierarchy
MERGE INTO barcode_history b
USING
(select distinct uh.uuid,uh.barcode,uh.item_type_id FROM uuid_hierarchy uh, barcode_history bh
where uh.uuid=bh.uuid) v
ON (b.uuid=v.uuid)
WHEN MATCHED THEN
UPDATE SET b.item_type_id=v.item_type_id;
commit;

-- set item type id for records that did not have a match in uuid_hierarchy, but have a match in shipped_biospecimen
MERGE INTO barcode_history b
USING
(select distinct bh.barcode,sb.built_barcode,sb.uuid,case when sb.shipped_item_type_id = 1 THEN 6 ELSE 11 end item_type_id
 FROM shipped_biospecimen sb, barcode_history bh
where bh.item_type_id is null and sb.uuid=bh.uuid) v
ON (b.uuid=v.uuid)
WHEN MATCHED THEN
UPDATE SET b.item_type_id=v.item_type_id;
commit;

-- set item type id for records that did not have a match in uuid_hierarchy or shipped_biospecimen
MERGE INTO barcode_history b
USING
(select barcode,
       case 
       when length(barcode) = 12 then 1
       when length(barcode) = 16 then 2
       when length(barcode) = 19 then 3
       when length(barcode) = 20 then 4
       when length(barcode) = 23 then 5
       when length(rtrim(barcode)) = 28 then 6 
       when barcode like '%-E1' then 9
       when barcode like '%-R1' then 7
       when length(barcode) = 15 and (barcode like '%-H1' or barcode like '%H2')  then 8
       when barcode like '%-S1' or barcode like '%-S2' then 10 end item_type_id
 from barcode_history where item_type_id is null )v
 ON (b.barcode=v.barcode)
 WHEN MATCHED THEN
 UPDATE SET b.item_type_id=v.item_type_id;
commit;

ALTER TABLE barcode_history ADD (
    FOREIGN KEY (item_type_id)
    REFERENCES uuid_item_type (item_type_id)
);

-- APPS-5197 add new column to sample_type table and populate it
ALTER TABLE sample_type ADD (short_letter_code VARCHAR2(25));

update sample_type set short_letter_code='TP' where sample_type_code ='01';
update sample_type set short_letter_code='TR' where sample_type_code ='02';
update sample_type set short_letter_code='TB' where sample_type_code ='03';
update sample_type set short_letter_code='TRBM' where sample_type_code ='04';
update sample_type set short_letter_code='TAP' where sample_type_code ='05';
update sample_type set short_letter_code='TM' where sample_type_code ='06';
update sample_type set short_letter_code='TAM' where sample_type_code ='07';
update sample_type set short_letter_code='THOC' where sample_type_code ='08';
update sample_type set short_letter_code='TBM' where sample_type_code ='09';
update sample_type set short_letter_code='NB' where sample_type_code ='10';
update sample_type set short_letter_code='NT' where sample_type_code ='11';
update sample_type set short_letter_code='NBC' where sample_type_code ='12';
update sample_type set short_letter_code='NEBV' where sample_type_code ='13';
update sample_type set short_letter_code='NBM' where sample_type_code ='14';
update sample_type set short_letter_code='CELLC' where sample_type_code ='20';
update sample_type set short_letter_code='TRB' where sample_type_code ='40';
update sample_type set short_letter_code='CELL' where sample_type_code ='50';
update sample_type set short_letter_code='XP' where sample_type_code ='60';
update sample_type set short_letter_code='XCL' where sample_type_code ='61';
commit;

-- APPS-5228 add center_code to uuid_hierarchy and populate it
ALTER TABLE uuid_hierarchy ADD (center_code VARCHAR2(10));

MERGE into uuid_hierarchy u
USING
(select bcr_center_id,uuid from shipped_biospecimen) s
ON (u.uuid=s.uuid)
WHEN MATCHED THEN UPDATE SET
u.center_code = s.bcr_center_id;

commit;
/*
** This procdure will get data from the latest BCR archives and the latest data center archives, place it into
** tables from which summaries by sample, center, platform, disease  will be compiled and put into the 
** sample_summary_report_detail for the Sample Counts for TCGA Data report. The intermediate tables' data
** is used also by the Aliquot Reports 
**
** Written by Shelley Alonso Initial Development 7/28/2010
**
** Revision History
**
** Shelley Alonso   08/27/2010 No longer include BCR archives in the samples centers sent counts
** Shelley Alonso   09/21/2010 Add batch_number to samples_sent_by_bcr table and submit_level1,2,and 3 to the 
**			       latest_samplese_received_by_dcc table to facilitate more easily getting data for
**                             the aliquot reports
** Shelley Alonso   09/21/2010 Change updates for sample_summary_report_detail table to one statement with
**			       nvl to make more efficient. 
** Shelley Alonso   11/12/2010 refactor some queries for efficiency
** Shelley Alonso   03/17/2011 Add documentation header
** Shelley Alonso   08/09/2011 Use shipped_biospecimen and related tables and shipped_biospecimen_aliquot view now
** Shelley Alonso   12/16/2011 APPS-4257 Fix query that counts number of missing samples so it gets distinct count
** Shelley Alonso   01/13/2012 APPS-5266 Query that counts number of missing samples is still wrong, fix it. 
**  
*/
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
         shipped_biospecimen_file bf, shipped_biospecimen_aliquot bb, disease d , file_to_archive fa
    WHERE c.center_id = a.center_id
    AND   a.platform_id = p.platform_id
    AND   a.archive_id = fa.archive_id
    AND   fa.file_id = f.file_id
    AND   f.file_id = bf.file_id
    AND   a.disease_id = d.disease_id
    AND   bf.shipped_biospecimen_id = bb.shipped_biospecimen_id
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
        v.is_viewable,
        v.date_received,
        v.submit_level1,
        v.submit_level2,
        v.submit_level3
    FROM
    (SELECT d.disease_abbreviation, c.center_id, c.domain_name, c.center_type_code, p.platform_alias as platform, bb.shipped_biospecimen_id as biospecimen_id,
           bb.barcode, bb.sample, bb.portion_analyte_code,bb.is_viewable, min(a.date_added)  as date_received,
           max(DECODE (f.level_number,1,'Y','N')) as submit_level1,max(DECODE (f.level_number,2,'Y','N')) submit_level2,
           max(DECODE (f.level_number,3,'Y','N')) submit_level3
    FROM archive_info a, center c, platform p, disease d, file_to_archive fa, file_info f,
          shipped_biospecimen_file bf, shipped_biospecimen_aliquot bb
    WHERE a.is_latest = 1
    AND   a.deploy_status = 'Available'
    AND   a.center_id = c.center_id
    AND   c.center_type_code != 'BCR'
    AND   a.platform_id = p.platform_id
    AND   a.disease_id = d.disease_id
    AND   a.archive_id = fa.archive_id
    AND   fa.file_id = f.file_id
    AND   f.file_id = bf.file_id
    AND   bf.shipped_biospecimen_id = bb.shipped_biospecimen_id
    AND   bb.is_viewable=1
    group by d.disease_abbreviation, c.center_id, c.domain_name, c.center_type_code, p.platform_alias, bb.shipped_biospecimen_id,
           bb.barcode, bb.sample, bb.portion_analyte_code, bb.is_viewable) v;
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
    (SELECT d.disease_abbreviation,c.center_id,c.domain_name,cbc.center_type_code,b.shipped_biospecimen_id as biospecimen_id,
           b.barcode,b.sample,b.portion_analyte_code, b.ship_date, a.serial_index as batch_number
    FROM   shipped_biospecimen_aliquot b, shipped_biospec_bcr_archive ba, Archive_info a, disease d,
           center c, center_to_bcr_center cbc
    WHERE c.center_id=cbc.center_id
    AND   cbc.bcr_center_id = b.bcr_center_id
    AND   b.shipped_biospecimen_id = ba.shipped_biospecimen_id
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
    ** Centers unaccounted for would be samples the bcr sent that the center did not send
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
    COMMIT;
    /*
    ** Bcr unaccounted for would be samples the centers sent that the bcr did not send
    */
    MERGE INTO sample_summary_report_detail sd
    USING
    (SELECT COUNT(DISTINCT l.sample) as samples,l.disease_abbreviation, l.center_name, l.center_type_code, l.portion_analyte_code, l.platform
         FROM latest_samples_received_by_dcc l 
         WHERE l.sample NOT IN 
           (SELECT DISTINCT sample FROM samples_Sent_by_bcr sb
            WHERE sb.disease_abbreviation = l.disease_abbreviation 
            AND   sb.center_name          = l.center_name 
            AND   sb.center_type_code     = l.center_type_code 
            AND   sb.portion_analyte_code = l.portion_analyte_code 
            AND   sb.sample               = l.sample)
         GROUP BY l.disease_abbreviation, l.center_name, l.center_type_code, l.portion_analyte_code, l.platform) v
     ON (sd.disease_abbreviation = v.disease_abbreviation AND
         sd.center_name = v.center_name AND
         sd.center_Type_code = v.center_type_code AND
         sd.portion_analyte_code = v.portion_analyte_code AND
         sd.platform = v.platform)
     WHEN MATCHED THEN UPDATE SET
         sd.total_bcr_unaccounted =  v.samples;     
    COMMIT;
        
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

-- for SARC
update disease set active=1 where disease_abbreviation = 'SARC';
delete from tss_to_disease where disease_id in (select disease_id from disease where disease_abbreviation = 'SALD');
delete from disease where disease_abbreviation = 'SALD';
commit;


update projectoverview_case_counts set disease_abbreviation = 'SARC' where disease_abbreviation = 'SALD';
commit;