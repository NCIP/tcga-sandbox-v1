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

CREATE TABLE exclude_from_case_count (
    participant_barcode	VARCHAR2(50) NOT NULL,
    CONSTRAINT pk_exclude_from_count_idx PRIMARY KEY (participant_barcode));
INSERT INTO exclude_from_case_count values ('TCGA-17-Z008');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z010');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z029');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z038');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z005');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z011');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z019');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z023');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z030');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z031');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z036');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z044');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z054');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z012');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z016');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z020');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z021');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z022');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z027');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z033');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z034');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z035');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z043');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z052');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z062');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z050');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z055');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z001');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z004');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z041');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z045');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z051');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z059');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z061');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z024');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z025');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z032');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z056');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z060');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z053');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z057');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z000');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z013');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z018');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z026');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z039');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z042');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z049');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z002');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z003');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z006');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z007');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z015');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z037');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z040');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z048');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z009');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z014');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z028');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z046');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z047');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z058');
INSERT INTO exclude_from_case_count values ('TCGA-17-Z017');
commit;
INSERT INTO exclude_from_case_count values ('TCGA-08-0525');
INSERT INTO exclude_from_case_count values ('TCGA-02-0337');
INSERT INTO exclude_from_case_count values ('TCGA-06-0412');
INSERT INTO exclude_from_case_count values ('TCGA-08-0521');
INSERT INTO exclude_from_case_count values ('TCGA-02-0330');
INSERT INTO exclude_from_case_count values ('TCGA-02-0250');
INSERT INTO exclude_from_case_count values ('TCGA-06-0162');
INSERT INTO exclude_from_case_count values ('TCGA-02-0336');
INSERT INTO exclude_from_case_count values ('TCGA-06-0182');
INSERT INTO exclude_from_case_count values ('TCGA-02-0456');
INSERT INTO exclude_from_case_count values ('TCGA-02-0271');
INSERT INTO exclude_from_case_count values ('TCGA-02-0260');
INSERT INTO exclude_from_case_count values ('TCGA-02-0440');
INSERT INTO exclude_from_case_count values ('TCGA-06-0149');
INSERT INTO exclude_from_case_count values ('TCGA-06-0177');
INSERT INTO exclude_from_case_count values ('TCGA-08-0511');
INSERT INTO exclude_from_case_count values ('TCGA-06-0146');
INSERT INTO exclude_from_case_count values ('TCGA-02-0446');
INSERT INTO exclude_from_case_count values ('TCGA-06-0402');
INSERT INTO exclude_from_case_count values ('TCGA-08-0509');
INSERT INTO exclude_from_case_count values ('TCGA-02-0324');
INSERT INTO exclude_from_case_count values ('TCGA-02-0290');
INSERT INTO exclude_from_case_count values ('TCGA-08-0512');
INSERT INTO exclude_from_case_count values ('TCGA-08-0517');
INSERT INTO exclude_from_case_count values ('TCGA-02-0087');
INSERT INTO exclude_from_case_count values ('TCGA-02-0289');
INSERT INTO exclude_from_case_count values ('TCGA-02-0281');
INSERT INTO exclude_from_case_count values ('TCGA-02-0439');
INSERT INTO exclude_from_case_count values ('TCGA-02-0422');
INSERT INTO exclude_from_case_count values ('TCGA-02-0269');
INSERT INTO exclude_from_case_count values ('TCGA-02-0258');
INSERT INTO exclude_from_case_count values ('TCGA-06-0179');
INSERT INTO exclude_from_case_count values ('TCGA-02-0339');
INSERT INTO exclude_from_case_count values ('TCGA-08-0531');
INSERT INTO exclude_from_case_count values ('TCGA-02-0451');
INSERT INTO exclude_from_case_count values ('TCGA-02-0285');
INSERT INTO exclude_from_case_count values ('TCGA-06-0175');
INSERT INTO exclude_from_case_count values ('TCGA-08-0520');
INSERT INTO exclude_from_case_count values ('TCGA-06-0414');
INSERT INTO exclude_from_case_count values ('TCGA-02-0321');
INSERT INTO exclude_from_case_count values ('TCGA-02-0326');
INSERT INTO exclude_from_case_count values ('TCGA-08-0524');
INSERT INTO exclude_from_case_count values ('TCGA-06-0410');
INSERT INTO exclude_from_case_count values ('TCGA-02-0106');
INSERT INTO exclude_from_case_count values ('TCGA-06-0397');
INSERT INTO exclude_from_case_count values ('TCGA-02-0266');
INSERT INTO exclude_from_case_count values ('TCGA-02-0432');
INSERT INTO exclude_from_case_count values ('TCGA-02-0333');
INSERT INTO exclude_from_case_count values ('TCGA-02-0329');
INSERT INTO exclude_from_case_count values ('TCGA-08-0516');
INSERT INTO exclude_from_case_count values ('TCGA-02-0430');
INSERT INTO exclude_from_case_count values ('TCGA-06-0164');
INSERT INTO exclude_from_case_count values ('TCGA-06-0194');
INSERT INTO exclude_from_case_count values ('TCGA-06-0409');
INSERT INTO exclude_from_case_count values ('TCGA-02-0325');
INSERT INTO exclude_from_case_count values ('TCGA-08-0514');
INSERT INTO exclude_from_case_count values ('TCGA-08-0518');
INSERT INTO exclude_from_case_count values ('TCGA-06-0394');
INSERT INTO exclude_from_case_count values ('TCGA-02-0317');
INSERT INTO exclude_from_case_count values ('TCGA-08-0510');
INSERT INTO exclude_from_case_count values ('TCGA-08-0529');
INSERT INTO exclude_from_case_count values ('TCGA-02-0111');
INSERT INTO exclude_from_case_count values ('TCGA-02-0338');
INSERT INTO exclude_from_case_count values ('TCGA-02-0332');
INSERT INTO exclude_from_case_count values ('TCGA-08-0522');
INSERT INTO exclude_from_case_count values ('TCGA-06-0413');
commit;
INSERT INTO exclude_from_case_count values ('TCGA-72-4238');
INSERT INTO exclude_from_case_count values ('TCGA-29-1704');
INSERT INTO exclude_from_case_count values ('TCGA-24-1852');
INSERT INTO exclude_from_case_count values ('TCGA-72-4235');
INSERT INTO exclude_from_case_count values ('TCGA-72-4236');
INSERT INTO exclude_from_case_count values ('TCGA-13-1817');
INSERT INTO exclude_from_case_count values ('TCGA-72-4231');
INSERT INTO exclude_from_case_count values ('TCGA-72-4233');
INSERT INTO exclude_from_case_count values ('TCGA-72-4232');
INSERT INTO exclude_from_case_count values ('TCGA-72-4240');
INSERT INTO exclude_from_case_count values ('TCGA-72-4237');
INSERT INTO exclude_from_case_count values ('TCGA-72-4234');
INSERT INTO exclude_from_case_count values ('TCGA-72-4241');
INSERT INTO exclude_from_case_count values ('TCGA-13-1819');
INSERT INTO exclude_from_case_count values ('TCGA-29-1692');
INSERT INTO exclude_from_case_count values ('TCGA-02-0002');
INSERT INTO exclude_from_case_count values ('TCGA-06-0189');
INSERT INTO exclude_from_case_count values ('TCGA-06-5417');
INSERT INTO exclude_from_case_count values ('TCGA-08-0384');
INSERT INTO exclude_from_case_count values ('TCGA-16-0846');
INSERT INTO exclude_from_case_count values ('TCGA-32-2498');
INSERT INTO exclude_from_case_count values ('TCGA-76-4932');
commit;

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
            WHERE sb.is_viewable = 1
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
            WHERE sb.is_viewable = 1
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
AND   uh.uuid = uhs.parent_uuid
AND   uhs.sample_type_code = st.sample_type_code 
UNION 
SELECT DISTINCT d.disease_abbreviation,  sb.participant case, 'Clinical' as data_type, st.sample_type_code, st.is_tumor 
FROM archive_info a,file_to_archive f2a, shipped_biospecimen_file sb2f, shipped_biospecimen_breakdown sb, 
 disease d, sample_type st, file_info f 
WHERE sb.is_viewable = 1
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
WHERE sb.is_viewable = 1
AND   sb.is_control = 0 
AND   sb.sample_type_code=st.sample_type_code 
AND   sb.shipped_biospecimen_id=sb2f.shipped_biospecimen_id 
AND   sb2f.file_id=f2a.file_id 
AND   f2a.file_id = f.file_id 
AND   f.file_name like '%biospecimen%.xml' 
AND   f2a.archive_id=a.archive_id 
AND   a.is_latest=1 AND a.deploy_status='Available' 
AND   a.disease_id=d.disease_id)
WHERE data_type IS NOT NULL
AND   case NOT IN (select participant FROM exclude_from_case_count);

Grant all on dccCommon.case_data_received to commonmaint;
GRANT ALL ON dccCommon.exclude_from_case_count to commonmaint;