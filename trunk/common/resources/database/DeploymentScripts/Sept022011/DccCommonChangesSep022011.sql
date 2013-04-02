CREATE OR REPLACE FORCE VIEW shipped_biospecimen_breakdown AS
   SELECT   b.shipped_biospecimen_id,
            b.built_barcode,
            b.project_code|| '-'|| b.tss_code|| '-'|| b.participant_code|| '-'|| be1.element_value AS sample,
            b.project_code || '-' || b.tss_code || '-' || b.participant_code AS participant,
            b.project_code,
            b.tss_code,
            b.participant_code,
            be1.element_value as sample_type_code,
            be2.element_value as sample_sequence,
            b.bcr_center_id,
            b.is_redacted,
            b.is_viewable,
            b.shipped_date
     FROM   shipped_biospecimen b, shipped_biospecimen_element be1, shipped_biospecimen_element be2
     WHERE  (b.shipped_biospecimen_id = be1.shipped_biospecimen_id and be1.element_type_id = 1)
     AND    (b.shipped_biospecimen_id = be2.shipped_biospecimen_id and be2.element_type_id = 2);

grant select on shipped_biospecimen_breakdown to commonread;
grant all on shipped_biospecimen_breakdown to commonmaint;

insert into uuid_item_type values(11,'Shipped Portion',11);
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
/*
** This procedure will get a count of patients/cases for which we have received data for each disease for the following
** data types:
**     Copy Number
**     Expression Array 
**     MicroRNA
**     Methylation
**     Expression RNA Sequence
**     GSC Mutation
**     GSC Genomic 
**     GSC Exome
**     GSC MicroRNA
**     GSC RNA Sequence
** Written by Shelley Alonso
**
** Revision History
**
** Shelley Alonso   01/25/2011  Add qualifier for disease select to only get active diseases 
**                  02/14/2011  Remove qualifier for disease..all should be on pcod report 
**                  06/27/2011  Add qualifier for active diseases because PO decided they don't want LCLL on the report; it
**                              has been set to inactive
**                  06/28/2011  Change the queries to calculate counts from dccCommon tables. No longer go to the disease
**                              schemas because the l2 data is no longer loaded APPS-3893
**		    08//09/2011 Changed to use shipped_biospecimen and related tables instead of biospecimen_barcode and related tables
*/
CREATE OR REPLACE PROCEDURE build_projectOverview_counts
IS  
  replaceString            VARCHAR2(4000);
  sqlString                VARCHAR2(4000);
  insertStatement          VARCHAR2(300);
  insertStatement2         VARCHAR2(300);
  mergeStatement           VARCHAR2(50); 
  mergeDataSetResults      VARCHAR2(2000);
  mergeMafResults          VARCHAR2(2000);
BEGIN
    /* 
    ** Get the rolled up counts of unique cases (or patients) by specific data types, for which we have level 2 or 3
    ** data and put it into a reporting table in dccCommon.  
    **
    ** Use MERGE statements so that an insert can be specified if there is no record for the disease,
    ** and an update can be specified if there is a record for the disease, in the projectOverview_case_count
    ** table.
    */


    MERGE INTO projectOverview_case_counts c 
    USING 
    (SELECT disease_abbreviation,
            max(decode(data_type,'cn' ,cases,0)) cnCases,
            max(decode(data_type,'exp', cases,0)) expCases,
        max(decode(data_type,'mirna', cases,0))  mirnaCases,  
        max(decode(data_type,'methylation', cases,0))  methCases,
        max(decode(data_type,'rnaseq', cases,0))  rnaseqCases, 
        max(decode(data_type,'mute', cases, 0)) mutationCases 
     FROM  
      (SELECT d.disease_abbreviation,
              COUNT(distinct b.participant_code) as cases, 
          CASE  WHEN dt.ftp_display IN ('snp','cna') THEN 'cn' 
                WHEN dt.ftp_display IN ('transcriptome','exon') THEN 'exp' 
                WHEN dt.ftp_display IN ('mirna','mirnaseq') THEN 'mirna'
                    WHEN dt.ftp_display IN ('mutations', 'tracerel') THEN 'mute'
                ELSE dt.ftp_display END  as data_type  
       FROM  shipped_biospecimen b, 
             shipped_biospecimen_file bf, 
             file_info f,
             file_to_archive fa, 
             archive_info a,
             platform p,
             data_type dt,
             disease d 
       WHERE b.is_viewable=1
       AND   b.shipped_biospecimen_id=bf.shipped_biospecimen_id
       AND   bf.file_id=f.file_id
       AND   f.level_number in (2,3)
       AND   f.file_id = fa.file_id
       AND   fa.archive_id = a.archive_id
       AND   a.is_latest=1
       AND   a.disease_id=d.disease_id
       AND   a.platform_id=p.platform_id
       AND   p.center_type_code != 'BCR'
       AND   p.base_data_type_id = dt.data_type_id 
       GROUP BY d.disease_abbreviation,
                CASE  WHEN dt.ftp_display IN ('snp','cna') THEN 'cn'  WHEN dt.ftp_display IN ('transcriptome','exon') THEN 'exp' WHEN dt.ftp_display IN ('mirna','mirnaseq') THEN 'mirna' WHEN dt.ftp_display IN ('mutations', 'tracerel') THEN 'mute' ELSE dt.ftp_display END ) 
      GROUP by disease_abbreviation) v 
    ON (c.disease_abbreviation = v.disease_abbreviation) 
    WHEN MATCHED THEN UPDATE SET  
        c.copyNumber_data_cases=v.cnCases, 
        c.expArray_data_cases= v.expCases, 
        c.microRna_data_cases=v.mirnaCases, 
        c.metholated_data_cases=v.methCases, 
        c.expRnaSeq_data_cases=v.rnaSeqCases , 
        c.gsc_mutation_data_cases=v.mutationCases
    WHEN NOT MATCHED THEN 
      INSERT (
          project_overview_id,
          disease_abbreviation,
          copyNumber_data_cases, 
          expArray_data_cases,
          microRna_data_cases,
          metholated_data_cases,
          expRnaSeq_data_cases,
          gsc_mutation_data_cases)  
      VALUES (
          report_seq.nextval,
          v.disease_abbreviation,
          v.cnCases, 
          v.expCases, 
          v.mirnaCases, 
          v.methCases, 
          v.rnaSeqCases, 
          v.mutationCases);
             
    COMMIT;
               
    /* 
    ** Finally get the bam file case totals. They are in a seperate table and not sent in archives like the other types.
    */
    MERGE INTO projectOverview_case_counts c USING 
        (SELECT max(decode(data_type,'Exome', cases,0))   exomeCases,
                max(decode(data_type,'miRNA', cases,0))   miRnaCases,
                max(decode(data_type,'Genome', cases,0))  genomeCases, 
                max(decode(data_type,'RNAseq', cases,0))  rnaSeqCases, 
                disease 
         FROM 
            (SELECT 
               count(distinct specific_patient) as cases,
               bd.general_datatype              as data_type, 
               d.disease_abbreviation           as disease
             FROM bam_file b, bam_file_datatype bd,shipped_biospecimen_bamfile bf, shipped_biospecimen_aliquot bb, disease d 
             WHERE b.bam_datatype_id = bd.bam_datatype_id 
             AND   b.bam_file_id     = bf.bam_file_id 
             AND   bf.shipped_biospecimen_id = bb.shipped_biospecimen_id
             AND   b.disease_id      = d.disease_id 
             GROUP BY bd.general_datatype,disease_abbreviation)
         GROUP BY disease ) v
         ON (c.disease_abbreviation = v.disease) 
     WHEN MATCHED THEN UPDATE SET 
         c.gsc_genome_cases   = v.genomeCases, 
         c.gsc_exome_cases    = v.exomeCases,
         c.gsc_rnaseq_cases   = v.rnaSeqCases, 
         c.gsc_microRna_cases = v.miRnaCases
     WHEN NOT MATCHED THEN          
             INSERT (
                project_overview_id,
                disease_abbreviation,
                gsc_genome_cases, 
                gsc_exome_cases,
                gsc_rnaseq_cases, 
                gsc_microRna_cases)
             VALUES (
                report_seq.nextval,
                v.disease,
                v.genomeCases, 
                v.exomeCases, 
                v.rnaseqCases, 
                v.miRnaCases);                
    COMMIT;

END;
/
CREATE OR REPLACE FORCE VIEW SHIPPED_PORTION_V AS
SELECT   DISTINCT 'BLCA' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaBLCA.shipped_portion gp,tcgaBLCA.shipped_portion_archive pa,tcgaBLCA.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'BRCA' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaBRCA.shipped_portion gp,tcgaBRCA.shipped_portion_archive pa,tcgaBRCA.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'CESC' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaCESC.shipped_portion gp,tcgaCESC.shipped_portion_archive pa,tcgaCESC.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'COAD' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaCOAD.shipped_portion gp,tcgaCOAD.shipped_portion_archive pa,tcgaCOAD.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'DLBC' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaDLBC.shipped_portion gp,tcgaDLBC.shipped_portion_archive pa,tcgaDLBC.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'GBM' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaGBM.shipped_portion gp,tcgaGBM.shipped_portion_archive pa,tcgaGBM.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'HNSC' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaHNSC.shipped_portion gp,tcgaHNSC.shipped_portion_archive pa,tcgaHNSC.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'KIRC' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaKIRC.shipped_portion gp,tcgaKIRC.shipped_portion_archive pa,tcgaKIRC.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'KIRP' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaKIRP.shipped_portion gp,tcgaKIRP.shipped_portion_archive pa,tcgaKIRP.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'LAML' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaLAML.shipped_portion gp,tcgaLAML.shipped_portion_archive pa,tcgaLAML.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'LCLL' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaLCLL.shipped_portion gp,tcgaLCLL.shipped_portion_archive pa,tcgaLCLL.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'LGG' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaLGG.shipped_portion gp,tcgaLGG.shipped_portion_archive pa,tcgaLGG.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'LIHC' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaLIHC.shipped_portion gp,tcgaLIHC.shipped_portion_archive pa,tcgaLIHC.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'LNNH' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaLNNH.shipped_portion gp,tcgaLNNH.shipped_portion_archive pa,tcgaLNNH.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'LUAD' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaLUAD.shipped_portion gp,tcgaLUAD.shipped_portion_archive pa,tcgaLUAD.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'LUSC' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaLUSC.shipped_portion gp,tcgaLUSC.shipped_portion_archive pa,tcgaLUSC.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'OV' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaOV.shipped_portion gp,tcgaOV.shipped_portion_archive pa,tcgaOV.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'PAAD' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaPAAD.shipped_portion gp,tcgaPAAD.shipped_portion_archive pa,tcgaPAAD.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'PRAD' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaPRAD.shipped_portion gp,tcgaPRAD.shipped_portion_archive pa,tcgaPRAD.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id
UNION 
SELECT   DISTINCT 'READ' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaREAD.shipped_portion gp,tcgaREAD.shipped_portion_archive pa,tcgaREAD.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id
UNION 
SELECT   DISTINCT 'SALD' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaSALD.shipped_portion gp,tcgaSALD.shipped_portion_archive pa,tcgaSALD.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id
UNION 
SELECT   DISTINCT 'SKCM' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaSKCM.shipped_portion gp,tcgaSKCM.shipped_portion_archive pa,tcgaSKCM.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'STAD' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaSTAD.shipped_portion gp,tcgaSTAD.shipped_portion_archive pa,tcgaSTAD.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'THCA' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaTHCA.shipped_portion gp,tcgaTHCA.shipped_portion_archive pa,tcgaTHCA.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id 
UNION 
SELECT   DISTINCT 'UCEC' AS disease_abbreviation,gp.sample_id,gp.uuid,gp.shipped_portion_barcode AS barcode,SUBSTR (gp.shipped_portion_barcode, LENGTH (gp.shipped_portion_barcode) - 1) AS shipped_portion,gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1) AS bcr_center_id,MAX (TRUNC (ai.date_added)) AS date_updated,MIN (TRUNC (ai.date_added)) AS date_added,ai.serial_index AS batch,ai.center_id FROM  tcgaUCEC.shipped_portion gp,tcgaUCEC.shipped_portion_archive pa,tcgaUCEC.archive_info ai WHERE gp.shipped_portion_id = pa.shipped_portion_id AND   pa.archive_id = ai.archive_id GROUP BY   gp.sample_id,gp.uuid,gp.shipped_portion_barcode,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),gp.shipped_portion_id,SUBSTR (gp.shipped_portion_barcode,LENGTH (gp.shipped_portion_barcode) - 1),ai.serial_index,ai.center_id;

CREATE OR REPLACE FORCE VIEW PORTION_V
AS
     SELECT   DISTINCT
              'BLCA' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgablca.portion gp,
              tcgablca.portion_archive pa,
              tcgablca.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'BRCA' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgabrca.portion gp,
              tcgabrca.portion_archive pa,
              tcgabrca.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'CESC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgacesc.portion gp,
              tcgacesc.portion_archive pa,
              tcgacesc.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'COAD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgacoad.portion gp,
              tcgacoad.portion_archive pa,
              tcgacoad.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'DLBC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgadlbc.portion gp,
              tcgadlbc.portion_archive pa,
              tcgadlbc.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'GBM' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgagbm.portion gp,
              tcgagbm.portion_archive pa,
              tcgagbm.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'HNSC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgahnsc.portion gp,
              tcgahnsc.portion_archive pa,
              tcgahnsc.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'KIRC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgakirc.portion gp,
              tcgakirc.portion_archive pa,
              tcgakirc.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'KIRP' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgakirp.portion gp,
              tcgakirp.portion_archive pa,
              tcgakirp.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LAML' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalaml.portion gp,
              tcgalaml.portion_archive pa,
              tcgalaml.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LCLL' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalcll.portion gp,
              tcgalcll.portion_archive pa,
              tcgalcll.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LGG' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalgg.portion gp,
              tcgalgg.portion_archive pa,
              tcgalgg.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LIHC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalihc.portion gp,
              tcgalihc.portion_archive pa,
              tcgalihc.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LNNH' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalnnh.portion gp,
              tcgalnnh.portion_archive pa,
              tcgalnnh.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LUAD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaluad.portion gp,
              tcgaluad.portion_archive pa,
              tcgaluad.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LUSC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalusc.portion gp,
              tcgalusc.portion_archive pa,
              tcgalusc.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'OV' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaov.portion gp,
              tcgaov.portion_archive pa,
              tcgaov.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'PAAD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgapaad.portion gp,
              tcgapaad.portion_archive pa,
              tcgapaad.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'PRAD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaprad.portion gp,
              tcgaprad.portion_archive pa,
              tcgaprad.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'READ' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaread.portion gp,
              tcgaread.portion_archive pa,
              tcgaread.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'SALD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgasald.portion gp,
              tcgasald.portion_archive pa,
              tcgasald.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'SKCM' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaskcm.portion gp,
              tcgaskcm.portion_archive pa,
              tcgaskcm.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'STAD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgastad.portion gp,
              tcgastad.portion_archive pa,
              tcgastad.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
  UNION
     SELECT   DISTINCT
              'THCA' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgathca.portion gp,
              tcgathca.portion_archive pa,
              tcgathca.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'UCEC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1) AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaucec.portion gp,
              tcgaucec.portion_archive pa,
              tcgaucec.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id;
              
CREATE OR REPLACE FORCE VIEW shipped_biospecimen_breakdown AS
   SELECT   b.shipped_biospecimen_id,
            b.built_barcode,
            b.project_code|| '-'|| b.tss_code|| '-'|| b.participant_code|| '-'|| be1.element_value AS sample,
            b.project_code || '-' || b.tss_code || '-' || b.participant_code AS participant,
            b.project_code,
            b.tss_code,
            b.participant_code,
            be1.element_value as sample_type_code,
            be2.element_value as sample_sequence,
            b.bcr_center_id,
            b.is_redacted,
            b.is_viewable,
            b.shipped_date,
            b.shipped_item_type_id
     FROM   shipped_biospecimen b, shipped_biospecimen_element be1, shipped_biospecimen_element be2
     WHERE  (b.shipped_biospecimen_id = be1.shipped_biospecimen_id and be1.element_type_id = 1)
     AND    (b.shipped_biospecimen_id = be2.shipped_biospecimen_id and be2.element_type_id = 2);
grant select on shipped_biospecimen_breakdown to commonread;
grant all on shipped_biospecimen_breakdown to commonmaint;
grant select on shipped_portion_v to commonread;
grant all on shipped_portion_v to commonmaint;

/*
** This procedure will get a data from clinical tables in each disease schema for the uuid browser and put it into the 
** dccCommon uuid_hierarchy table, a denormalized hierarchical view of all data that has uuid associations. If the
** uuid already exists in the hierarchy, it will update the updated_date and barcode, and for some items, other
** barcode-related metadata
**
** Written by Shelley Alonso 03/10/2011
**
** Revision History
**
**   Shelley Alonso   03/22/2011  put id's in uuid_hierarchy instead of values for sample_type, item_type
**                                and center
**   Shelley Alonso   04/07/2011  add batch number, center id for bcr, and populate uuid_platform table
**   Shelley Alonso   04/27/2011  add platforms column and populate with comma-delimited list of platform_ids
**   Shelley Alonso   04/28/2011  add slide_layer column and populate based on value of first letter of slide
**   Shelley Alonso   08/09/3011  change selects from biospecimen_barcode to select from shipped_biospecimen APPS-3962
**   Shelley Alonse   08/10/2011  add selects and inserts for shipped_portion APPS-3791
*/
CREATE OR REPLACE PROCEDURE PopulateUuidHierarchy
IS
CURSOR pCur IS
   select disease_abbreviation,uuid,barcode, participant_number,tss_code,patient_id, date_added, date_updated,center_id,batch
     from patient_v 
   order by disease_abbreviation;
CURSOR sCur (patientId NUMBER, disease VARCHAR2) IS
   select disease_abbreviation,uuid,barcode, sample_type,sample_sequence,sample_id, date_added, date_updated,center_id,batch
   from sample_v 
   where patient_id = patientId
     and disease_abbreviation = disease;
CURSOR rCur (patientId NUMBER, disease VARCHAR2) IS
   select uuid,barcode, date_added, date_updated,center_id,batch
   from radiation_v 
   where patient_id = patientId
     and disease_abbreviation = disease;
CURSOR suCur (patientId NUMBER, disease VARCHAR2) IS
   select uuid,barcode,date_added,date_updated,center_id,batch
   from surgery_v 
   where patient_id = patientId
     and disease_abbreviation = disease;
CURSOR exCur (patientId NUMBER, disease VARCHAR2) IS
   select uuid,barcode,date_added, date_updated,center_id,batch
   from examination_v 
   where patient_id = patientId
     and disease_abbreviation = disease;
CURSOR dCur (patientId NUMBER, disease VARCHAR2) IS
   select uuid,barcode, date_added, date_updated,center_id,batch
   from drug_v 
   where patient_id = patientId
     and disease_abbreviation = disease;
CURSOR poCur (sampleId NUMBER, disease VARCHAR2) IS
   select disease_abbreviation,uuid,barcode, portion,portion_id,date_added, date_updated,center_id,batch
   from portion_v
   where sample_id= sampleId
     and disease_abbreviation = disease;
CURSOR shpCur (sampleId NUMBER, disease VARCHAR2) IS
   select disease_abbreviation,uuid,barcode, shipped_portion,
   SUBSTR (barcode, INSTR(barcode,'-',1,5) + 1, 4) AS plate, date_added, date_updated,bc.center_id as receiving_center_id,v.center_id,batch
   from shipped_portion_v v, center_to_bcr_center bc
   where sample_id= sampleId
     and disease_abbreviation = disease
     and v.bcr_center_id=bc.bcr_center_id ;
CURSOR anCur (portionId NUMBER, disease VARCHAR2) IS
   select disease_abbreviation,uuid,barcode,analyte_code,analyte_id, date_added, date_updated,center_id,batch
   from analyte_v
   where portion_id=portionId
     and disease_abbreviation = disease;
CURSOR alCur (analyteId NUMBER, disease VARCHAR2) IS
    select v.uuid,barcode,v.plate, v.date_added, v.date_updated, bc.center_id as receiving_center_id, v.center_id,v.batch
    from aliquot_v v, center_to_bcr_center bc
    WHERE v.analyte_id = analyteId 
    and   v.disease_abbreviation = disease
    and   v.bcr_center_id=bc.bcr_center_id ;
CURSOR slCur (portionId NUMBER, disease VARCHAR2) IS
  select uuid,barcode,slide, date_added, date_updated,center_id,batch
  from slide_v 
  where portion_id=portionId
    and disease_abbreviation = disease;
CURSOR platCur IS
  SELECT DISTINCT u.uuid, a.platform_id
  FROM   uuid_hierarchy u,shipped_biospecimen b, shipped_biospecimen_file bf, file_to_archive fa, archive_info a, center c
  WHERE  u.item_type_id = 6
  AND    u.uuid = b.uuid
  AND    b.is_viewable=1
  AND    b.shipped_biospecimen_id = bf.shipped_biospecimen_id
  AND    bf.file_id=fa.file_id
  AND    fa.archive_id = a.archive_id
  AND    a.is_latest = 1
  AND    a.center_id=c.center_id
  AND    c.center_type_code != 'BCR';
CURSOR treeCur (startUuid VARCHAR2) IS
    SELECT uuid,parent_uuid
    FROM uuid_hierarchy
    START WITH uuid= startUuid
    CONNECT BY uuid = prior parent_uuid;
CURSOR agCur IS
  SELECT uuid,RTRIM(XMLAGG(XMLELEMENT(e,platform_id || ',')).EXTRACT('//text()'),',') platforms 
  FROM uuid_platform 
  GROUP BY uuid;
  
patientNumber             VARCHAR2(10);
parentUUID                VARCHAR2(36);
tss                       VARCHAR2(10);
diseaseAbbreviation       VARCHAR2(10);
portionSequence           VARCHAR2(10);
sampleType                VARCHAR2(10);
sampleSequence            VARCHAR2(10);
analyteCode               VARCHAR2(10);
slideLayer                VARCHAR2(7);

BEGIN 
FOR pRec IN pCur
 LOOP
 
    MERGE INTO uuid_hierarchy USING dual ON (uuid=pRec.uuid)
    WHEN MATCHED THEN UPDATE SET update_date = pRec.date_updated, barcode=pRec.barcode
    WHEN NOT MATCHED THEN INSERT (uuid,item_type_id , participant_number,tss_code, barcode, disease_abbreviation,create_date,center_id_bcr,batch_number) 
    VALUES (pRec.uuid,1,pRec.participant_number, pRec.tss_code,pRec.barcode,pRec.disease_abbreviation,pRec.date_added,pRec.center_id,pRec.batch);
    patientNumber          := pRec.participant_number;
    tss                    := pRec.tss_code;
    diseaseAbbreviation    := pRec.disease_abbreviation;
    FOR rRec IN rCur (pRec.patient_id,pRec.disease_abbreviation) 
    LOOP
       MERGE INTO uuid_hierarchy USING dual ON (uuid=rRec.uuid)
       WHEN MATCHED THEN UPDATE SET update_date = rRec.date_updated, barcode = rRec.barcode
       WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
       VALUES (rRec.uuid,pRec.uuid,7,patientNumber,tss,rRec.barcode,diseaseAbbreviation,rRec.date_added,rRec.center_id,rRec.batch);
    END LOOP;   
    FOR suRec IN suCur (pRec.patient_id,pRec.disease_abbreviation)
    LOOP
       MERGE INTO uuid_hierarchy USING dual ON (uuid=suRec.uuid)
       WHEN MATCHED THEN UPDATE SET update_date = suRec.date_added, barcode=suRec.barcode
       WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
       VALUES (suRec.uuid,pRec.uuid,10,patientNumber,tss,suRec.barcode,diseaseAbbreviation,suRec.date_added,suRec.center_id,suRec.batch);
    END LOOP;   
    FOR exRec IN exCur (pRec.patient_id,pRec.disease_abbreviation)
    LOOP
       MERGE INTO uuid_hierarchy USING dual ON (uuid=exRec.uuid)
       WHEN MATCHED THEN UPDATE SET update_date = exRec.date_added, barcode=exRec.barcode
       WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
       VALUES (exRec.uuid,pRec.uuid,9,patientNumber,tss,exRec.barcode,diseaseAbbreviation,exRec.date_added,exRec.center_id,exRec.batch);
    END LOOP;   
    FOR dRec IN dCur (pRec.patient_id,pRec.disease_abbreviation)
    LOOP
       MERGE INTO uuid_hierarchy USING dual ON (uuid=dRec.uuid)
       WHEN MATCHED THEN UPDATE SET update_date = dRec.date_updated, barcode=dRec.barcode
       WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
       VALUES (dRec.uuid,pRec.uuid,8,patientNumber,tss,dRec.barcode,diseaseAbbreviation,dRec.date_added,dRec.center_id,dRec.batch);
    END LOOP;   
    FOR sRec IN sCur (pRec.patient_id,pRec.disease_abbreviation) 
    LOOP
       sampleSequence := sRec.sample_sequence;
       sampleType := sRec.sample_type;
       MERGE INTO uuid_hierarchy USING dual ON (uuid=sRec.uuid)
       WHEN MATCHED THEN UPDATE SET update_date = pRec.date_updated, barcode=sRec.barcode, sample_type_code=sRec.sample_type, sample_sequence=sRec.sample_sequence
       WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,sample_type_code,sample_sequence,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
       VALUES (sRec.uuid,pRec.uuid,2,patientNumber,tss,sampleType,sRec.sample_sequence,sRec.barcode,diseaseAbbreviation,sRec.date_added,sRec.center_id,sRec.batch);
       
       FOR shpRec in shpCur (sRec.sample_id,sRec.disease_abbreviation)
       LOOP
       
           MERGE INTO uuid_hierarchy USING dual ON (uuid=shpRec.uuid)
           WHEN MATCHED THEN UPDATE SET update_date = shpRec.date_updated, barcode=shpRec.barcode
           WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,sample_type_code,sample_sequence,portion_sequence,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number,receiving_center_id,plate_id) 
           VALUES (shpRec.uuid,sRec.uuid,11,patientNumber,tss,sampleType,sampleSequence,shpRec.shipped_portion,shpRec.barcode,diseaseAbbreviation,shpRec.date_added,shpRec.center_id,shpRec.batch,shpRec.receiving_center_id,shpRec.plate);
       END LOOP;
       
       FOR poRec in poCur (sRec.sample_id,sRec.disease_abbreviation)
       LOOP
       
           MERGE INTO uuid_hierarchy USING dual ON (uuid=poRec.uuid)
           WHEN MATCHED THEN UPDATE SET update_date = poRec.date_updated, barcode=poRec.barcode
           WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,sample_type_code,sample_sequence,portion_sequence,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
           VALUES (poRec.uuid,sRec.uuid,3,patientNumber,tss,sampleType,sampleSequence,poRec.portion,poRec.barcode,diseaseAbbreviation,poRec.date_added,poRec.center_id,poRec.batch);
           portionSequence := poRec.portion;
          FOR anRec in anCur (poRec.portion_id,poRec.disease_abbreviation)
          LOOP

            analyteCode := anRec.analyte_code;
         
         
            MERGE INTO uuid_hierarchy USING dual ON (uuid=anRec.uuid)
            WHEN MATCHED THEN UPDATE SET update_date = anRec.date_updated, barcode=anRec.barcode, portion_analyte_code=anRec.analyte_code
            WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,sample_type_code,sample_sequence,portion_sequence,portion_analyte_code,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
             VALUES (anRec.uuid,poRec.uuid,4,patientNumber,tss,sampleType,sampleSequence,portionSequence,anRec.analyte_code,anRec.barcode,diseaseAbbreviation,anRec.date_added,anRec.center_id,anRec.batch);

             
             FOR alRec in alCur (anRec.analyte_id, anRec.disease_abbreviation)
              LOOP
                MERGE INTO uuid_hierarchy USING dual ON (uuid=alRec.uuid)
                WHEN MATCHED THEN UPDATE SET update_date = alRec.date_updated, barcode=alRec.barcode, portion_analyte_code=analyteCode, plate_id=alRec.plate
                WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,sample_type_code,sample_sequence,portion_sequence,portion_analyte_code,plate_id,receiving_center_id,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
                VALUES (alRec.uuid,anRec.uuid,6,patientNumber,tss,sampleType,sampleSequence,portionSequence,analyteCode,alRec.plate,alRec.receiving_center_id,alRec.barcode,diseaseAbbreviation,alRec.date_added,alRec.center_id,alRec.batch);
             END LOOP;
             
          END LOOP;
          
          FOR slRec IN slCur (poRec.portion_id,poRec.disease_abbreviation)
          LOOP
             SELECT DECODE(substr(slRec.slide,1,1),'T','top','B','bottom','M','middle',null) into slideLayer fROM DUAL;
             MERGE INTO uuid_hierarchy USING dual ON (uuid=slRec.uuid)
             WHEN MATCHED THEN UPDATE SET update_date = slRec.date_updated, slide=slRec.slide, slide_layer=slideLayer, barcode=slRec.barcode
             WHEN NOT MATCHED THEN INSERT (uuid,parent_uuid,item_type_id, participant_number,tss_code ,sample_type_code,sample_sequence,portion_sequence,slide,slide_layer,barcode,disease_abbreviation,create_date,center_id_bcr,batch_number) 
             VALUES (slRec.uuid,poRec.uuid,5,patientNumber,tss,sampleType,sampleSequence,portionSequence,slRec.slide,slideLayer,slRec.barcode,diseaseAbbreviation,slRec.date_added,slRec.center_id,slRec.batch);

          END LOOP;
      
      END LOOP;
   END LOOP;
END LOOP;
commit;

FOR platRec IN platCur LOOP
    /*
    ** get the hierarchy of items from uuid_hierarchy for the aliquot uuid that is in the platRec and insert
    ** a record in the uuid_platform relationship table, if there is 
    ** not already one there
    */
    FOR treeRec IN treeCur (platRec.uuid) LOOP
       MERGE INTO uuid_platform u
       USING DUAL
       ON (u.uuid = treeRec.uuid and u.platform_id = platRec.platform_id)
        WHEN NOT MATCHED THEN 
       INSERT(uuid,platform_id) VALUES (treeRec.uuid,platRec.platform_id);
    END LOOP;   

END LOOP;
COMMIT;
FOR agRec IN agCur LOOP
    UPDATE uuid_hierarchy 
    SET    platforms = agRec.platforms
    WHERE  uuid = agRec.uuid;
END LOOP;
COMMIT;

END;
/