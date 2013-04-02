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
**		    08/09/2011  Changed to use shipped_biospecimen and related tables instead of biospecimen_barcode and related tables
**		    04/02/2012  Added code to 
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
/* if APPS-5706 happens to get the normal and tumor sample counts by participant into a table 
SELECT distinct d.disease_abbreviation,
       b.participant,1 as cnt,
 CASE  WHEN dt.ftp_display IN ('snp','cna') AND s.is_tumor = 1 THEN 'cnTumor' 
                WHEN dt.ftp_display IN ('snp','cna') AND s.is_tumor = 0 THEN 'cnNormal'
                WHEN dt.ftp_display IN ('transcriptome','exon') AND s.is_tumor = 1 THEN 'expTumor' 
                WHEN dt.ftp_display IN ('transcriptome','exon') AND s.is_tumor = 0 THEN 'expNormal'
                WHEN dt.ftp_display IN ('mirna','mirnaseq') AND s.is_tumor = 1 THEN 'mirnaTumor'
                WHEN dt.ftp_display IN ('mirna','mirnaseq') AND s.is_tumor = 0 THEN 'mirnaNormal'
                WHEN dt.ftp_display IN ('mutations', 'tracerel') AND s.is_tumor = 1 THEN 'muteTumor'
                WHEN dt.ftp_display IN ('mutations', 'tracerel') AND s.is_tumor = 0 THEN 'muteNormal'
                ELSE DECODE(s.is_tumor,1,dt.ftp_display||'Tumor',dt.ftp_display||'Normal') END  as data_type                  
       FROM  shipped_biospecimen_breakdown b, 
             shipped_biospecimen_file bf, 
             file_info f,
             file_to_archive fa, 
             archive_info a,
             platform p,
             data_type dt,
             disease d ,
             sample_type s
       WHERE b.is_viewable=1 
       AND   b.sample_Type_code = s.sample_type_code
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
       GROUP BY d.disease_abbreviation,b.participant,
CASE  WHEN dt.ftp_display IN ('snp','cna') AND s.is_tumor = 1 THEN 'cnTumor' 
                WHEN dt.ftp_display IN ('snp','cna') AND s.is_tumor = 0 THEN 'cnNormal'
                WHEN dt.ftp_display IN ('transcriptome','exon') AND s.is_tumor = 1 THEN 'expTumor' 
                WHEN dt.ftp_display IN ('transcriptome','exon') AND s.is_tumor = 0 THEN 'expNormal'
                WHEN dt.ftp_display IN ('mirna','mirnaseq') AND s.is_tumor = 1 THEN 'mirnaTumor'
                WHEN dt.ftp_display IN ('mirna','mirnaseq') AND s.is_tumor = 0 THEN 'mirnaNormal'
                WHEN dt.ftp_display IN ('mutations', 'tracerel') AND s.is_tumor = 1 THEN 'muteTumor'
                WHEN dt.ftp_display IN ('mutations', 'tracerel') AND s.is_tumor = 0 THEN 'muteNormal'
                ELSE DECODE(s.is_tumor,1,dt.ftp_display||'Tumor',dt.ftp_display||'Normal') END
ORDER BY participant
*/