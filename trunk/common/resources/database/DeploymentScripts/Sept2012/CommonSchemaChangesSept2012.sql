CREATE OR REPLACE PACKAGE pcod_report 
AS
    PROCEDURE build_projectOverview_counts;
    
    PROCEDURE get_pcod_case_counts;
    
    PROCEDURE get_pcod_normal_tumor_counts;
END;
/
CREATE OR REPLACE PACKAGE BODY pcod_report 
IS
     /*
     ** This package contains procedures to populate 2 tables for the Project Overview Case Counts (PCOD) report. One
     ** will populate a table with counts of tumor and normal samples for each participant, by disease and type of data.
     ** The other will calculate total case counts by disease and types of data.
     **
     **  Written by Shelley Alonso
     **  
     **  Modification History
     **
     **  04/02/2012   Shelley Alonso    Created as a package to include existing procedure build_projectOverview_counts
     **                                 and add new procedure get_pcod_normal_tumor_counts to populate a new table which will be 
     **                                 used to determine whether or not a case is complete (APPS-5706)
     **
     **  05/21/2012  Shelley Alonso    	APPS-6212 Added two new columns to the projectoverview_case_counts table to capture counts of 
     **                                	cases for whom low pass sequencing data has been submitted. These have been counted in with 
     **                    		Copy Number data up to now, based on data type. Now they will be seperated out,for GCC, based on platform
     **                    		IlluminaHiSeq_DNASeqC, and if they are IlluminaHiSeq_DNASeqC, will be excluded from the GCC copynumber counts.
     **                                 For GSC, the will counted as gsc low pass sequencing counts if the bam file was submitted by
     **                                 Harvard Medical School (HMS) and will be excluded from the counts for GSC gsc_genome_cases.
     **
     **  07/24/2012  Shelley Alonso     APPS-6476 Exclude entries with CNTL as the disease from bam_file counts. Project Office does not want to
     **					see these on the PCOD report.
     **
     **	 08/14/2012  Shelley Alonso	APPS-6507  Change select for GSC low pass to count ANY files from hms as low pass and select should be 
     **				                   from shipped_biospecime_breakdown so it includes any shipped biospecimen not just aliquots
     */
          
     PROCEDURE build_projectOverview_counts 
     IS
     BEGIN
     
         get_pcod_normal_tumor_counts;
         get_pcod_case_counts;
     
     END build_projectOverview_counts;
     
     PROCEDURE get_pcod_case_counts
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
     **
     ** Revision History
     **
     ** Shelley Alonso   01/25/2011  Add qualifier for disease select to only get active diseases 
     **                  02/14/2011  Remove qualifier for disease..all should be on pcod report 
     **                  06/27/2011  Add qualifier for active diseases because PO decided they don't want LCLL on the report; it
     **                              has been set to inactive
     **                  06/28/2011  Change the queries to calculate counts from dccCommon tables. No longer go to the disease
     **                              schemas because the l2 data is no longer loaded APPS-3893
     **                 08/09/2011  Changed to use shipped_biospecimen and related tables instead of biospecimen_barcode and related 
     **                              tables
     */
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
         max(decode(data_type,'mute', cases, 0)) mutationCases,
         max(decode(data_type,'lowpass',cases, 0)) lowpassCases
      FROM  
       (SELECT d.disease_abbreviation,
           COUNT(distinct b.participant_code) as cases, 
           CASE  WHEN dt.ftp_display IN ('snp','cna') and p.platform_name != 'IlluminaHiSeq_DNASeqC' THEN 'cn' 
             WHEN dt.ftp_display = 'cna' and p.platform_name = 'IlluminaHiSeq_DNASeqC' then 'lowpass'
             WHEN dt.ftp_display IN ('transcriptome','exon') THEN 'exp' 
             WHEN dt.ftp_display IN ('mirna','mirnaseq') THEN 'mirna'
             WHEN dt.ftp_display IN ('mutations', 'tracerel') THEN 'mute'
             WHEN dt.ftp_display IN ('rnaseq', 'rnaseqV2') THEN 'rnaseq'
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
             CASE  WHEN dt.ftp_display IN ('snp','cna') and p.platform_name != 'IlluminaHiSeq_DNASeqC' THEN 'cn' WHEN dt.ftp_display = 'cna' and p.platform_name = 'IlluminaHiSeq_DNASeqC' then 'lowpass' WHEN dt.ftp_display IN ('transcriptome','exon') THEN 'exp' WHEN dt.ftp_display IN ('mirna','mirnaseq') THEN 'mirna' WHEN dt.ftp_display IN ('mutations', 'tracerel') THEN 'mute' WHEN dt.ftp_display IN ('rnaseq', 'rnaseqV2') THEN 'rnaseq' ELSE dt.ftp_display END)
       GROUP by disease_abbreviation) v 
     ON (c.disease_abbreviation = v.disease_abbreviation) 
     WHEN MATCHED THEN UPDATE SET  
         c.copyNumber_data_cases=v.cnCases, 
         c.expArray_data_cases= v.expCases, 
         c.microRna_data_cases=v.mirnaCases, 
         c.metholated_data_cases=v.methCases, 
         c.expRnaSeq_data_cases=v.rnaSeqCases , 
         c.gsc_mutation_data_cases=v.mutationCases,
         c.gcc_lowpass_cases = v.lowpassCases
     WHEN NOT MATCHED THEN 
       INSERT (
           project_overview_id,
           disease_abbreviation,
           copyNumber_data_cases, 
           expArray_data_cases,
           microRna_data_cases,
           metholated_data_cases,
           expRnaSeq_data_cases,
           gsc_mutation_data_cases,
           gcc_lowpass_cases)  
       VALUES (
           report_seq.nextval,
           v.disease_abbreviation,
           v.cnCases, 
           v.expCases, 
           v.mirnaCases, 
           v.methCases, 
           v.rnaSeqCases, 
           v.mutationCases,
           v.lowpassCases);

     COMMIT;

         /* 
         ** Finally get the bam file case totals. They are in a seperate table and not sent in archives like the other types.
         */
         MERGE INTO projectOverview_case_counts c USING 
             (SELECT max(decode(data_type,'Exome', cases,0))   exomeCases,
                     max(decode(data_type,'miRNA', cases,0))   miRnaCases,
                     max(decode(data_type,'Genome', cases,0))  genomeCases, 
                     max(decode(data_type,'RNAseq', cases,0))  rnaSeqCases, 
                     max(decode(data_type,'lowpass', cases,0)) lowPassCases,
                     disease 
              FROM 
                 (SELECT 
                    count(distinct participant) as cases,
                    CASE
                      WHEN c.domain_name = 'hms.harvard.edu' THEN 'lowpass'
                      ELSE bd.general_datatype           
                    END as data_type, 
                    d.disease_abbreviation           as disease
                  FROM bam_file b, bam_file_datatype bd,shipped_biospecimen_bamfile bf, shipped_biospecimen_breakdown bb, disease d, center c 
                  WHERE b.bam_datatype_id = bd.bam_datatype_id 
                  AND   b.bam_file_id     = bf.bam_file_id 
                  AND   bf.shipped_biospecimen_id = bb.shipped_biospecimen_id
                  AND   (b.disease_id      = d.disease_id and d.disease_abbreviation != 'CNTL')
                  AND   b.center_id      = c.center_id
                  GROUP BY CASE WHEN c.domain_name = 'hms.harvard.edu' THEN 'lowpass' ELSE bd.general_datatype END,disease_abbreviation)
              GROUP BY disease ) v
              ON (c.disease_abbreviation = v.disease) 
          WHEN MATCHED THEN UPDATE SET 
              c.gsc_genome_cases   = v.genomeCases, 
              c.gsc_exome_cases    = v.exomeCases,
              c.gsc_rnaseq_cases   = v.rnaSeqCases, 
              c.gsc_microRna_cases = v.miRnaCases,
              c.gsc_lowpass_cases  = v.lowPassCases
          WHEN NOT MATCHED THEN          
                  INSERT (
                     project_overview_id,
                     disease_abbreviation,
                     gsc_genome_cases, 
                     gsc_exome_cases,
                     gsc_rnaseq_cases, 
                     gsc_microRna_cases,
                     gsc_lowpass_cases)
                  VALUES (
                     report_seq.nextval,
                     v.disease,
                     v.genomeCases, 
                     v.exomeCases, 
                     v.rnaseqCases, 
                     v.miRnaCases,
                     v.lowPassCases);                
         COMMIT;
     
     
     END;
     
     PROCEDURE get_pcod_normal_tumor_counts IS
     /*
     **  This procedure will determine if we have recieved data in each data type category by disease and case/participant for normal 
     **  and tumor samples for the following data types:
     **  
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
     */
     BEGIN
    /* 
    ** Determine if we have , for which we have level 2 or 3
    ** data and put it into a reporting table in dccCommon.  
    **
    ** Use MERGE statements so that an insert can be specified if there is no record for the disease,
    ** and an update can be specified if there is a record for the disease, in the projectOverview_case_count
    ** table.
    */
        MERGE INTO pcod_normal_tumor_stats pcod USING
        (SELECT disease_abbreviation,
               participant,
               max(decode(data_type,'cnTumor' ,cnt,0)) cnTumorSamples,
               max(decode(data_type,'cnNormal' ,cnt,0)) cnNormalSamples,
               max(decode(data_type,'expTumor', cnt,0)) expTumorSamples,
               max(decode(data_type,'expNormal', cnt,0)) expNormalSamples,
               max(decode(data_type,'mirnaTumor', cnt,0))  mirnaTumorSamples,  
               max(decode(data_type,'mirnaNormal', cnt,0))  mirnaNormalSamples,  
               max(decode(data_type,'methylationTumor', cnt,0))  methTumorSamples,
               max(decode(data_type,'methylationNormal', cnt,0))  methNormalSamples,
               max(decode(data_type,'rnaseqTumor', cnt,0))  rnaseqTumorSamples, 
               max(decode(data_type,'rnaseqNormal', cnt,0))  rnaseqNormalSamples, 
               max(decode(data_type,'muteTumor', cnt, 0)) mutationTumorSamples, 
               max(decode(data_type,'muteNormal', cnt, 0)) mutationNormalSamples 
        FROM  
             (SELECT distinct d.disease_abbreviation,
               b.participant,1 as cnt,
                CASE  
                    WHEN dt.ftp_display IN ('snp','cna') AND s.is_tumor = 1 THEN 'cnTumor' 
                WHEN dt.ftp_display IN ('snp','cna') AND s.is_tumor = 0 THEN 'cnNormal'
                WHEN dt.ftp_display IN ('transcriptome','exon') AND s.is_tumor = 1 THEN 'expTumor' 
                WHEN dt.ftp_display IN ('transcriptome','exon') AND s.is_tumor = 0 THEN 'expNormal'
                WHEN dt.ftp_display IN ('mirna','mirnaseq') AND s.is_tumor = 1 THEN 'mirnaTumor'
                WHEN dt.ftp_display IN ('mirna','mirnaseq') AND s.is_tumor = 0 THEN 'mirnaNormal'
                WHEN dt.ftp_display IN ('mutations', 'tracerel') AND s.is_tumor = 1 THEN 'muteTumor'
                WHEN dt.ftp_display IN ('mutations', 'tracerel') AND s.is_tumor = 0 THEN 'muteNormal'
                WHEN dt.ftp_display IN ('rnaseq', 'rnaseqV2') AND s.is_tumor = 0 THEN 'rnaseqNormal'
                WHEN dt.ftp_display IN ('rnaseq', 'rnaseqV2') AND s.is_tumor = 1 THEN 'rnaseqTumor'
                ELSE DECODE(s.is_tumor,1,dt.ftp_display||'Tumor',dt.ftp_display||'Normal') 
               END  as data_type                  
                FROM shipped_biospecimen_breakdown b, 
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
                WHEN dt.ftp_display IN ('rnaseq', 'rnaseqV2') AND s.is_tumor = 0 THEN 'rnaseqNormal'
                WHEN dt.ftp_display IN ('rnaseq', 'rnaseqV2') AND s.is_tumor = 1 THEN 'rnaseqTumor'
               ELSE DECODE(s.is_tumor,1,dt.ftp_display||'Tumor',dt.ftp_display||'Normal') END
            )
        GROUP BY disease_abbreviation,participant) v
        ON (pcod.disease_abbreviation = v.disease_abbreviation AND pcod.participant_barcode = v.participant) 
        WHEN MATCHED THEN UPDATE SET 
           pcod.cn_TumorCount = v.cnTumorSamples,
           pcod.cn_NormalCount = v.cnNormalSamples,
           pcod.expArray_TumorCount = v.expTumorSamples,
           pcod.expArray_NormalCount = v.expNormalSamples,
           pcod.expRnaSeq_TumorCount = v.rnaSeqTumorSamples,
           pcod.expRnaSeq_NormalCount = v.rnaSeqNormalSamples,
           pcod.mirna_TumorCount = v.mirnaTumorSamples,
           pcod.mirna_NormalCount = v.mirnaNormalSamples,
           pcod.methylation_TumorCount = v.methTumorSamples,
           pcod.methylation_NormalCount = v.methNormalSamples,
           pcod.mutation_TumorCount = v.mutationTumorSamples,
           pcod.mutation_NormalCount = v.mutationNormalSamples
        WHEN NOT MATCHED THEN 
        INSERT (
          disease_abbreviation,
          participant_barcode,
          cn_TumorCount,
          cn_NormalCount,
          expArray_TumorCount,
          expArray_NormalCount,
          expRnaSeq_TumorCount,
          expRnaSeq_NormalCount,
          mirna_TumorCount,
          mirna_NormalCount,
          methylation_TumorCount,
          methylation_NormalCount,
          mutation_TumorCount,
          mutation_NormalCount)
        VALUES (
          v.disease_abbreviation,
          v.participant,
          v.cnTumorSamples,
          v.cnNormalSamples,
          v.expTumorSamples,
          v.expNormalSamples,
          v.rnaSeqTumorSamples,
          v.rnaSeqNormalSamples,
          v.mirnaTumorSamples,
          v.mirnaNormalSamples,
          v.methTumorSamples,
          v.methNormalSamples,
          v.mutationTumorSamples,
          v.mutationNormalSamples);
        COMMIT;

                
    /* 
    ** Finally get the bam file case totals. They are in a seperate table and not sent in archives like the other types.
    */
    MERGE INTO pcod_normal_tumor_stats pcod USING 
     (SELECT
          disease,
              participant,
              max(decode(data_type,'ExomeTumor', cnt,0))    exomeTumorSamples,
              max(decode(data_type,'ExomeNormal', cnt,0))   exomeNormalSamples,
              max(decode(data_type,'miRNATumor', cnt,0))    miRnaTumorSamples,
              max(decode(data_type,'miRNANormal', cnt,0))   miRnaNormalSamples,
              max(decode(data_type,'GenomeTumor', cnt,0))   genomeTumorSamples, 
              max(decode(data_type,'GenomeNormal', cnt,0))  genomenormalSamples, 
              max(decode(data_type,'RNAseqTumor', cnt,0))   rnaSeqTumorSamples, 
              max(decode(data_type,'RNAseqNormal', cnt,0))  rnaSeqNormalSamples
           FROM 
           (SELECT 
              d.disease_abbreviation           as disease,
              bb.participant,
              1 as cnt,
              DECODE(s.is_tumor,1,bd.general_datatype||'Tumor',bd.general_datatype||'Normal')  as data_type         
            FROM bam_file b, bam_file_datatype bd,shipped_biospecimen_bamfile bf, shipped_biospecimen_breakdown bb, disease d, sample_type s 
            WHERE b.bam_datatype_id = bd.bam_datatype_id 
            AND   b.bam_file_id     = bf.bam_file_id 
            AND   bf.shipped_biospecimen_id = bb.shipped_biospecimen_id
            AND   bb.sample_type_code = s.sample_type_code
            AND   (b.disease_id      = d.disease_id and d.disease_abbreviation != 'CNTL'))
          GROUP BY disease,participant ) v
    ON (pcod.disease_abbreviation = v.disease AND pcod.participant_barcode = v.participant) 
    WHEN MATCHED THEN UPDATE SET 
      pcod.gsc_genome_TumorCount   = v.genomeTumorSamples, 
      pcod.gsc_genome_NormalCount   = v.genomeNormalSamples, 
      pcod.gsc_exome_TumorCount    = v.exomeTumorSamples,
      pcod.gsc_exome_NormalCount    = v.exomeNormalSamples,
      pcod.gsc_rnaseq_TumorCount   = v.rnaSeqTumorSamples, 
      pcod.gsc_rnaseq_NormalCount  = v.rnaSeqNormalSamples, 
      pcod.gsc_miRna_TumorCount = v.miRnaTumorSamples,
      pcod.gsc_miRna_NormalCount = v.miRnaNormalSamples
    WHEN NOT MATCHED THEN          
          INSERT (
         disease_abbreviation,
         participant_barcode,
         gsc_genome_tumorCount,
         gsc_genome_NormalCount,
         gsc_exome_TumorCount,
         gsc_exome_NormalCount,
         gsc_rnaseq_TumorCount,
         gsc_rnaseq_NormalCount,
         gsc_miRna_TumorCount,
         gsc_miRna_NormalCount)
          VALUES (
         v.disease,
         v.participant,
         v.genomeTumorSamples,
         v.genomeNormalSamples,
         v.exomeTumorSamples, 
         v.exomeNormalSamples,
         v.rnaSeqTumorSamples,
         v.rnaSeqNormalSamples,
         v.miRnaTumorSamples,
         v.miRnaNormalSamples);                
    COMMIT;
    
    
    END;
END;
/
grant execute on pcod_report to commonmaint;