Insert into disease (disease_id,disease_abbreviation,disease_name, active)
Values (39,'UVM','Uveal Melanoma',1);
commit;

UPDATE data_type set name = 'CNV (Array)' where data_type_id = 4;
UPDATE data_type set name = 'CNV (SNP)' where data_type_id = 1;
UPDATE data_type set name = 'CNV (Low Pass DNASeq)' where data_type_id = 40;
COMMIT;

alter table home_page_drilldown drop column total_tumor;
alter table home_page_drilldown drop column matched_normal;
alter table home_page_drilldown drop column unmatched_normal;

alter table home_page_drilldown add case_count number(38);
alter table home_page_drilldown add healthy_control_count number(38);

alter table home_page_stats add cases_shipped number(38);
alter table home_page_stats add cases_with_data number(38);
alter table home_page_stats drop column patients_with_samples;
alter table home_page_stats drop column downloadable_tumor_samples;

delete from home_page_drilldown;

delete from disease where disease_abbreviation = 'LCLL';
COMMIT;

CREATE MATERIALIZED VIEW case_data_received 
BUILD IMMEDIATE
REFRESH COMPLETE ON DEMAND
ENABLE QUERY REWRITE as
SELECT * FROM
(SELECT DISTINCT d.disease_abbreviation,participant as case, bd.general_datatype as data_type,st.sample_type_code,st.is_tumor 
            FROM   bam_file b, bam_file_datatype bd,shipped_biospecimen_bamfile bf, shipped_biospecimen_breakdown sb, 
                   disease d , sample_type st 
            WHERE  b.bam_datatype_id = bd.bam_datatype_id 
            AND    b.bam_file_id     = bf.bam_file_id 
            AND    bf.shipped_biospecimen_id = sb.shipped_biospecimen_id 
            AND    sb.is_viewable = 1 
            AND    sb.is_control = 0 
            AND    sb.sample_type_code=st.sample_type_code 
            AND    b.disease_id = d.disease_id 
            AND    bd.general_datatype = 'Exome' 
UNION
SELECT DISTINCT d.disease_abbreviation, sb.participant as case , 'Exome' as data_type, st.sample_Type_code,st.is_tumor 
            FROM archive_info a,file_to_archive f2a, shipped_biospecimen_file sbf, shipped_biospecimen_breakdown sb, 
                 archive_type at,platform p,disease d, sample_type st 
            WHERE sb.is_redacted=0 
            AND   sb.is_viewable = 1 
            AND   sb.is_control = 0 
            AND   sb.sample_type_code = st.sample_type_code 
            AND   sb.shipped_biospecimen_id=sbf.shipped_biospecimen_id 
            AND   sbf.file_id=f2a.file_id 
            AND   f2a.archive_id=a.archive_id 
            AND   a.is_latest=1 AND a.deploy_status='Available' 
            AND   a.disease_id=d.disease_id 
            AND   a.platform_id=p.platform_id 
            AND   p.platform_name = 'ABI'
UNION
SELECT DISTINCT d.disease_abbreviation,  sb.participant case,  
                       CASE  
                         WHEN p.platform_name = 'Genome_Wide_SNP_6' THEN 'SNP' 
                         WHEN p.platform_name IN ('IlluminaDNAMethylation_OMA002_CPI','HumanMethylation27','HumanMethylation450','IlluminaDNAMethylation_OMA003_CPI') 
                              THEN 'Methylation' 
                         WHEN d.disease_abbreviation in ('GBM','OV') AND 
                              p.platform_name IN ('IlluminaHiSeq_mRNA_DGE','IlluminaGA_mRNA_DGE','AgilentG4502A_07_1','AgilentG4502A_07_2','AgilentG4502A_07_3', 
                              'HT_HG-U133A','HuEx-1_0-st-v2','IlluminaHiSeq_RNASeqV2', 'IlluminaHiSeq_RNASeq') THEN 'mRNA' 
                         WHEN d.disease_abbreviation in ('GBM','OV') AND p.platform_name in ('H-miRNA_8x15Kv2','H-miRNA_8x15K')  THEN 'miRNA' 
                       END  as data_type , st.sample_type_code, st.is_tumor  
            FROM archive_info a,file_to_archive f2a, shipped_biospecimen_file sb2f, shipped_biospecimen_breakdown sb,archive_type at, 
                 platform p,disease d,sample_type st 
            WHERE sb.is_redacted=0 
            AND   sb.is_viewable = 1 
            AND   sb.is_control = 0 
            AND   sb.sample_type_code=st.sample_type_code 
            AND   sb.shipped_biospecimen_id=sb2f.shipped_biospecimen_id 
            AND   sb2f.file_id=f2a.file_id 
            AND   f2a.archive_id=a.archive_id 
            AND   a.is_latest=1 AND a.deploy_status='Available' 
            AND   a.archive_type_id=at.archive_type_id 
            AND   at.data_level=1 
            AND   a.disease_id=d.disease_id 
            AND   a.platform_id=p.platform_id 
            AND   p.platform_name IN ('Genome_Wide_SNP_6','IlluminaDNAMethylation_OMA002_CPI','HumanMethylation27','HumanMethylation450', 
                                    'IlluminaDNAMethylation_OMA003_CPI','IlluminaHiSeq_mRNA_DGE','IlluminaGA_mRNA_DGE','AgilentG4502A_07_1', 
                                    'AgilentG4502A_07_2','AgilentG4502A_07_3','HT_HG-U133A','HuEx-1_0-st-v2','IlluminaHiSeq_RNASeqV2', 
                                    'IlluminaHiSeq_RNASeq','H-miRNA_8x15Kv2','H-miRNA_8x15K') 
UNION 
SELECT DISTINCT d.disease_abbreviation, sb.participant as case, 
       DECODE(bd.general_datatype,'RNAseq','mRNA',bd.general_datatype) as data_type , st.sample_type_code, st.is_tumor 
FROM   bam_file b, bam_file_datatype bd,shipped_biospecimen_bamfile bf, shipped_biospecimen_breakdown sb, disease d, center c, sample_type st 
WHERE  b.bam_datatype_id = bd.bam_datatype_id 
AND    b.bam_file_id     = bf.bam_file_id 
AND    bf.shipped_biospecimen_id = sb.shipped_biospecimen_id 
AND    sb.is_viewable = 1 
AND    sb.is_control = 0 
AND    sb.sample_type_code=st.sample_type_code 
AND    b.disease_id = d.disease_id 
AND    b.center_id  = c.center_id 
AND    c.domain_name in ('unc.edu','bcgsc.ca') 
AND    bd.general_datatype in ('RNAseq','miRNA') 
UNION
SELECT DISTINCT d.disease_abbreviation, uh.barcode as case, 'Clinical' as data_type , st.sample_type_code, st.is_tumor
FROM archive_info a, file_to_archive f2a, file_info f, participant_uuid_file puf, uuid_hierarchy uh, uuid_hierarchy uhs, 
 disease d, sample_type st 
WHERE puf.file_id = f.file_id 
AND   f.file_name like '%clinical%.xml' 
AND   f.file_id = f2a.file_id 
AND   f2a.archive_id = a.archive_id 
AND   a.is_latest = 1 
AND   a.deploy_status='Available' 
AND   a.disease_id = d.disease_id 
AND   puf.uuid = uh.uuid 
AND   uh.is_redacted=0 
AND   uh.uuid = uhs.parent_uuid 
AND   uhs.sample_type_code = st.sample_type_code 
UNION 
SELECT DISTINCT d.disease_abbreviation,  sb.participant case, 'Clinical' as data_type, st.sample_type_code, st.is_tumor 
FROM archive_info a,file_to_archive f2a, shipped_biospecimen_file sb2f, shipped_biospecimen_breakdown sb, 
 disease d, sample_type st, file_info f 
WHERE sb.is_redacted=0 
AND   sb.is_viewable = 1 
AND   sb.is_control = 0 
AND   sb.sample_type_code=st.sample_type_code 
AND   sb.shipped_biospecimen_id=sb2f.shipped_biospecimen_id 
AND   sb2f.file_id=f2a.file_id 
AND   f2a.file_id = f.file_id 
AND   f.file_name like '%clinical%.xml' 
AND   f2a.archive_id=a.archive_id 
AND   a.is_latest=1 AND a.deploy_status='Available' 
AND   a.disease_id=d.disease_id
UNION
SELECT DISTINCT d.disease_abbreviation,  sb.participant case, 'Biospecimen' as data_type, st.sample_type_code, st.is_tumor 
FROM archive_info a,file_to_archive f2a, shipped_biospecimen_file sb2f, shipped_biospecimen_breakdown sb, 
 disease d, sample_type st, file_info f 
WHERE sb.is_redacted=0 
AND   sb.is_viewable = 1 
AND   sb.is_control = 0 
AND   sb.sample_type_code=st.sample_type_code 
AND   sb.shipped_biospecimen_id=sb2f.shipped_biospecimen_id 
AND   sb2f.file_id=f2a.file_id 
AND   f2a.file_id = f.file_id 
AND   f.file_name like '%biospecimen%.xml' 
AND   f2a.archive_id=a.archive_id 
AND   a.is_latest=1 AND a.deploy_status='Available' 
AND   a.disease_id=d.disease_id)
WHERE data_type IS NOT NULL;