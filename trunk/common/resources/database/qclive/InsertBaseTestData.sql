/* 
** To be used in unit test database only
*/
INSERT INTO generation_method (generation_method_id,generation_method) VALUES (1,'Web');
INSERT INTO generation_method (generation_method_id,generation_method) VALUES (2,'REST');
INSERT INTO generation_method (generation_method_id,generation_method) VALUES (3,'Upload');
INSERT INTO generation_method (generation_method_id,generation_method) VALUES (4, 'API');
commit;

INSERT INTO project (project_code, definition) values ('TCGA','The Cancer Genome Atlas');

INSERT INTO sample_type (sample_type_code, definition) values ('06','Metastatic');
INSERT INTO sample_type (sample_type_code, definition) values ('07','Additional Metastatic');
INSERT INTO sample_type (sample_type_code, definition) values ('05','Additional - New Primary');
INSERT INTO sample_type (sample_type_code, definition) values ('14','Bone Marrow Normal');
INSERT INTO sample_type (sample_type_code, definition) values ('02','Recurrent Solid Tumor');
INSERT INTO sample_type (sample_type_code, definition) values ('03','Primary Blood Derived Cancer');
INSERT INTO sample_type (sample_type_code, definition) values ('04','Recurrent Blood Derived Cancer');
INSERT INTO sample_type (sample_type_code, definition) values ('13','EBV Immortalized Normal');
INSERT INTO sample_type (sample_type_code, definition) values ('20','Cell Line Control');
INSERT INTO sample_type (sample_type_code, definition) values ('12','Buccal Cell Normal');
INSERT INTO sample_type (sample_type_code, definition) values ('11','Solid Tissue Normal');
INSERT INTO sample_type (sample_type_code, definition) values ('10','Blood Derived Normal');
INSERT INTO sample_type (sample_type_code, definition) values ('01','Primary solid Tumor');

INSERT INTO portion_analyte (portion_analyte_code, definition) values ('D','DNA');
INSERT INTO portion_analyte (portion_analyte_code, definition) values ('R','RNA');
INSERT INTO portion_analyte (portion_analyte_code, definition) values ('T','Total RNA');
INSERT INTO portion_analyte (portion_analyte_code, definition) values ('G','Whole Genome Amplification (WGA) produced using GenomePlex (Rubicon) DNA');
INSERT INTO portion_analyte (portion_analyte_code, definition) values ('X','Whole Genome Amplification (WGA) produced using Repli-G X (Qiagen) DNA (2nd Reaction)');
INSERT INTO portion_analyte (portion_analyte_code, definition) values ('W','Whole Genome Amplification (WGA) produced using Repli-G (Qiagen) DNA');

INSERT INTO visibility values (1,'Public',0);
INSERT INTO visibility values (2,'Private',1);
COMMIT;

INSERT INTO CENTER_TYPE (center_type_code,center_type_definition)
VALUES
('BCR','Biospecimen Core Resource');
INSERT INTO CENTER_TYPE (center_type_code,center_type_definition)
VALUES
('CGCC','Cancer Genome Characterization Center');
INSERT INTO CENTER_TYPE (center_type_code,center_type_definition)
VALUES
('GSC','Genomic Sequencing Center');
INSERT INTO CENTER_TYPE (center_type_code, center_type_definition)
VALUES
('GDAC','Genome Data Analysis Center');
INSERT INTO CENTER_TYPE (center_type_code, center_type_definition)
VALUES
('COM','Other Company');

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(1,'broad.mit.edu','CGCC','Broad Institute of MIT and Harvard','BI',1);

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(2,'jhu-usc.edu','CGCC','Johns Hopkins / University of Southern California','JHU_USC',6);

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(3,'hms.harvard.edu','CGCC','Harvard Medical School','HMS',3);

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(4,'lbl.gov','CGCC','Lawrence Berkeley National Laboratory','LBL',6);

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(5,'mskcc.org','CGCC','Memorial Sloan-Kettering Cancer Center','MSKCC',7);

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(6,'hudsonalpha.org','CGCC','HudsonAlpha Institute for Biotechnology','HAIB',8);

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(7,'unc.edu','CGCC','University of North Carolina','UNC',5);

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(8,'hgsc.bcm.edu','GSC','Baylor College of Medicine','BCM',9);

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(9,'genome.wustl.edu','GSC','Washington University School of Medicine','WUSM',10);

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(10,'combined GSCs','GSC','Combined GSCs',' ',10);

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(11,'nationwidechildrens.org','BCR','Nationwide Children''s Hospital','NWCH',11);

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(12,'broad.mit.edu','GSC','Broad Institute of MIT and Harvard','BI',12);

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(13,'genome.wustl.edu','BCR','Washington University School of Medicine','WUSM',13);

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(14,'intgen.org','BCR','IGC Biospecimen Core Resource','IGC',14);

INSERT INTO center  (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(15, 'bcgsc.ca', 'CGCC', 'Canada''s Michael Smith Genome Sciences Centre', 'BCGSC', 15);

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(16,'broad.mit.edu','GDAC','Broad Institute of MIT and Harvard','BI',16);

INSERT INTO center  (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(17, 'systemsbiology.org', 'GDAC', 'Institute for Systems Biology', 'ISB', 17);

INSERT INTO center  (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(18, 'lbl.gov', 'GDAC', 'Lawrence Berkely National Laboratory', 'LBL', 18);

INSERT INTO center  (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(19, 'mskcc.gov', 'GDAC', 'Memorial Sloan-Kettering Cancer Center', 'MSKCC', 19);

INSERT INTO center  (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(20, 'ucsc.edu', 'GDAC', 'University of California, Santa Cruz', 'UCSC', 20);

INSERT INTO center  (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(21, 'mdanderson.org', 'GDAC', 'MD Anderson', 'MD', 21);

INSERT INTO center  (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(22, 'rubicongenomics.com', 'COM', 'Rubicon Genomics', 'RG', 22);

INSERT INTO center (center_id,domain_name,center_type_code,display_name, short_name,sort_order)
VALUES(23,'hgsc.bcm.edu','CGCC','Baylor College of Medicine','BCM',23);

commit;

INSERT INTO center_to_bcr_center (bcr_center_id,center_id,center_type_code) VALUES ('01',1,'CGCC');
INSERT INTO center_to_bcr_center (bcr_center_id,center_id,center_type_code) VALUES ('02',3,'CGCC');
INSERT INTO center_to_bcr_center (bcr_center_id,center_id,center_type_code) VALUES ('03',4,'CGCC');
INSERT INTO center_to_bcr_center (bcr_center_id,center_id,center_type_code) VALUES ('04',5,'CGCC');
INSERT INTO center_to_bcr_center (bcr_center_id,center_id,center_type_code) VALUES ('05',2,'CGCC');
INSERT INTO center_to_bcr_center (bcr_center_id,center_id,center_type_code) VALUES ('06',6,'CGCC');
INSERT INTO center_to_bcr_center (bcr_center_id,center_id,center_type_code) VALUES ('07',7,'CGCC');
INSERT INTO center_to_bcr_center (bcr_center_id,center_id,center_type_code) VALUES ('08',1,'GSC');
INSERT INTO center_to_bcr_center (bcr_center_id,center_id,center_type_code) VALUES ('09',9,'GSC');
INSERT INTO center_to_bcr_center (bcr_center_id,center_id,center_type_code) VALUES ('10',8,'GSC');
INSERT INTO center_to_bcr_center (bcr_center_id,center_id,center_type_code) VALUES ('13',15,'CGCC');
INSERT INTO center_to_bcr_center (bcr_center_id,center_id,center_type_code) VALUES ('14',1,'GDAC');
INSERT INTO center_to_bcr_center (bcr_center_id,center_id,center_type_code) VALUES ('16',4,'GDAC');
INSERT INTO center_to_bcr_center (bcr_center_id,center_id,center_type_code) VALUES ('17',5,'GDAC');
commit;

INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (1,'SNP','CGCC','snp',1,6);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (2,'DNA Methylation','CGCC','methylation',1,5);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (3,'Expression-Genes','CGCC','transcriptome',1,1);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (4,'Copy Number Results','CGCC','cna',1,4);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (5,'Expression-Exon','CGCC','exon',1,2);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (6,'Expression-miRNA','CGCC','mirna',1,3);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (7,'Somatic Mutations','GSC','mutations',1,10);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (10,'Complete Clinical Set','BCR','clin',1,11);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (11,'Minimal Clinical Set','BCR','clin',1,12);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (12,'Trace-Sample Relationship','GSC','tracerel',1,9);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (13,'SNP Copy Number Results','CGCC','cna',0,8);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (14,'Sequencing Trace','GSC','seq',0,13);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (15,'SNP Frequencies','CGCC','snp',0,7);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (16,'Tissue Slide Images','BCR','slide_images',1,14);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (17,'Short-Read Relationship','GSC','sr',1,15);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (18,'BAM-file Relationship','GSC','bam',1,16);
INSERT INTO data_type (data_type_id,  name ,center_type_code, ftp_display,available,sort_order)VALUES (25,'Quantification-Exon','CGCC','rnaseq',1,25);
commit;

INSERT INTO data_level (level_number,level_definition)VALUES(1,'Raw data');
INSERT INTO data_level (level_number,level_definition)VALUES(2,'Normalized data');
INSERT INTO data_level (level_number,level_definition)VALUES(3,'Aggregated data');
INSERT INTO data_level (level_number,level_definition)VALUES(4,'Regions of Interest data');
INSERT INTO data_level (level_number,level_definition)VALUES(0,'No Level');


INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(1,1,2,1);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(2,2,1,1);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(3,3,1,1);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(4,4,1,1);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(5,5,2,1);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(6,6,1,1);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(7,7,1,1);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(8,10,2,1);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(9,11,1,1);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(10,12,2,1);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(11,13,2,1);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(12,14,1,1);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(13,15,1,1);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(14,16,1,1);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(15,1,2,2);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(16,2,1,2);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(17,3,1,2);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(18,4,1,2);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(19,6,1,2);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(20,7,1,2);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(21,10,2,2);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(22,11,1,2);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(23,12,2,2);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(24,13,2,2);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(25,15,1,2);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(26,16,1,2);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(27,1,1,3);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(28,2,1,3);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(29,3,1,3);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(30,4,1,3);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(31,5,1,3);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(32,6,1,3);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(33,7,1,3);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(34,12,1,3);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(35,13,1,3);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(36,14,1,3);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(37,15,1,3);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(38,17,1,3);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(39,18,1,3);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(41,5,2,2);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(42,1,2,0);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(43,2,1,0);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(44,3,1,0);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(45,4,1,0);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(46,5,2,0);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(47,6,1,0);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(48,7,1,0);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(49,10,2,0);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(50,11,1,0);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(51,12,2,0);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(52,13,2,0);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(53,15,1,0);
INSERT INTO data_Visibility (data_visibility_id,data_type_id, visibility_id,level_number)
VALUES(54,16,1,0);


INSERT INTO archive_type(archive_type_id,archive_type,data_level) values (1,'Level_1',1);
INSERT INTO archive_type(archive_type_id,archive_type,data_level) values (2,'Level_2',2);
INSERT INTO archive_type(archive_type_id,archive_type,data_level) values (3,'Level_3',3);
INSERT INTO archive_type(archive_type_id,archive_type,data_level) values (4,'Level_4',4);
INSERT INTO archive_type(archive_type_id,archive_type) values (5,'aux');
INSERT INTO archive_type(archive_type_id,archive_type) values (6,'mage-tab');
INSERT INTO archive_type(archive_type_id,archive_type) values (7,'classic');
commit;

INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (1,'Genome_Wide_SNP_6','Affymetrix Genome-Wide Human SNP Array 6.0','Genome_Wide_SNP_6',8,1,'CGCC',1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (2,'IlluminaDNAMethylation_OMA002_CPI','Illumina DNA Methylation OMA002 Cancer Panel I','IlluminaDNAMethylation',17,1,'CGCC',2);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (3,'IlluminaDNAMethylation_OMA003_CPI','Illumina DNA Methylation OMA003 Cancer Panel I','IlluminaDNAMethylation',17,1,'CGCC',2);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (4,'HT_HG-U133A','Affymetrix HT Human Genome U133 Array Plate Set','HT_HG-U133A',3,1,'CGCC',3);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (5,'HG-CGH-244A','Agilent Human Genome CGH Microarray 244A','HG-CGH-244A',10,1,'CGCC',4);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (6,'HuEx-1_0-st-v2','Affymetrix Human Exon 1.0 ST Array','HuEx-1_0-st-v2',4,1,'CGCC',5);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (7,'HumanHap550','Illumina 550K Infinium HumanHap550 SNP Chip','HumanHap550',18,1,'CGCC',1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (8,'AgilentG4502A_07_1','Agilent 244K Custom Gene Expression G4502A-07-1','AgilentG4502A_07',13,1,'CGCC',3);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (10,'AgilentG4502A_07_2','Agilent 244K Custom Gene Expression G4502A-07-2','AgilentG4502A_07',13,1,'CGCC',3);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (12,'H-miRNA_8x15K','Agilent 8 x 15K Human miRNA-specific microarray','H-miRNA_8x15K',14,1,'CGCC',6);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (13,'HumanMethylation27','Illumina Infinium Human DNA Methylation 27','HumanMethylation27',17,1,'CGCC',2);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (14,'AgilentG4502A_07_3','Agilent 244K Custom Gene Expression G4502A-07-3','AgilentG4502A_07',13,1,'CGCC',3);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (15,'CGH-1x1M_G4447A','Agilent SurePrint G3 Human CGH Microarray Kit 1x1M','CGH-1x1M_G4447A',15,1,'CGCC',4);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (16,'Human1MDuo','Illumina Human1M-Duo BeadChip','Human1MDuo',17,1,'CGCC',1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (17,'ABI','Applied Biosystems Sequence data','ABI',21,1,'GSC',12);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (18,'AgilentG4502A_07','Agilent 244K Custom Gene Expression G4502A-07','AgilentG4502A_07',18,1,'CGCC',3);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (20,'H-miRNA_8x15Kv2','Agilent Human miRNA Microarray Rel12.0','H-miRNA_8x15K',13,1,'CGCC',6);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (21,'HG-CGH-415K_G4124A','Agilent Human Genome CGH Custom Microarray 2x415K','HG-CGH-415K_G4124A',9,1,'CGCC',3);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (22,'IlluminaGA_mRNA_DGE','Illumina Genome Analyzer mRNA Digital Gene Expression','IlluminaGA_mRNA_DGE',22,1,'CGCC',3);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (23,'HG-U133A_2','Affymetrix Human Genome U133A 2.0 Array','HG-U133A_2',23,0,'CGCC',3);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (24,'HG-U133_Plus_2','Affymetrix Human Genome U133 Plus 2.0 Array','HG-U133_Plus_2',24,0,'CGCC',3);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (25,'Mapping250K_Nsp','Affymetrix Human Mapping 250K Nsp Array','Mapping250K_Nsp',25,0,'CGCC',1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (26,'Mapping250K_Sty','Affymetrix Human Mapping 250K Sty Array','Mapping250K_Sty',26,0,'CGCC',1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (27,'GenomeWideSNP_5','Affymetrix Genome-Wide Human SNP Array 5.0','GenomeWideSNP_5',27,0,'CGCC',1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (28,'tissue_images','Tissue Images','tissue_images',28,0,'BCR',16);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (29,'IlluminaGG','Illumina GoldenGate','IlluminaGG',16,0,'CGCC',1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (30,'bio','Biospecimen Metadata - Complete Set','bio',19,0,'BCR',10);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (31,'454','454 Life Sciences Genome Sequence data','454',22,0,'GSC',14);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (32,'minbio','Biospecimen Metadata - Minimal Set','minbio',20,0,'BCR',11);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (33,'H-miRNA_EarlyAccess','Agilent Human miRNA Early Access Array','H-miRNA_EarlyAccess',15,0,'CGCC',6);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (34,'WHG-CGH_4x44B','Agilent Human Genome CGH Microarray 44K','WHG-CGH_4x44B',9,0,'CGCC',4);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (35,'WHG-1x44K_G4112A','Agilent Whole Human Genome','1 x 44K',11,0,'CGCC',3);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (36,'H-miRNA_G4470A','Agilent Human miRNA Microarray','H-miRNA_G4470A',13,0,'CGCC',6);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (37,'minbiotab','Biospecimen Metadata - Minimal Set - All Samples - Tab-delimited','minbiotab',20,0,'BCR',11);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (38,'biotab','Biospecimen Metadata - Complete Set - All Samples - Tab-delimited','biotab',19,0,'BCR',10);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (39,'WHG-4x44K_G4112F','Agilent Whole Human Genome Microarray Kit','4 x 44K',12,0,'CGCC',3);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (40,'IlluminaGA_DNASeq','Illumina Genome Analyzer DNA Sequencing','IlluminaGA_DNASeq',40,1,'GSC',7);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available, center_type_code, base_data_type_id)VALUES (41, 'IlluminaGA_RNASeq', 'Illumina Genome Analyzer RNA Sequencing', 'IlluminaGA_RNASeq', 41, 1, 'CGCC', 25);
commit;

INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (1,3,23);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (2,3,24);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (3,3,4);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (4,5,6);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (5,1,25);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (6,4,25);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (7,1,26);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (8,4,26);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (9,1,27);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (10,4,27);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (11,6,36);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (12,4,34);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (13,4,5);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (14,3,35);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (15,3,39);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (16,1,29);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (17,4,29);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (18,2,2);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (19,1,7);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (21,10,30);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (24,12,17);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (26,7,31);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (27,12,31);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (28,14,31);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (29,1,1);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (30,11,32);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (31,6,12);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (32,6,33);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (33,11,37);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (34,10,38);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (35,3,8);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (36,3,10);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (37,2,3);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (38,2,13);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (39,16,28);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (40,3,14);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (41,1,16);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (42,4,15);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (43,6,20);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (44,3,22);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (45,4,21);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (50,17,40);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (51,18,40);
INSERT INTO data_type_to_platform (data_type_platform_id,data_type_id,platform_id) VALUES (52,7,40);
commit;

INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (1,'GBM','Glioblastoma multiforme',1);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (3,'LUSC','Lung squamous cell carcinoma',1);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (2,'OV','Ovarian serous cystadenocarcinoma',1);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (4,'LUAD','Lung adenocarcinoma',1);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (5,'BRCA','Breast invasive carcinoma',1);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (6,'COAD','Colon adenocarcinoma',1);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (7,'KIRC','Kidney renal clear cell carcinoma',0);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (8,'KIRP','Kidney renal papillary cell carcinoma',1);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (9,'STAD','Stomach adenocarcinoma',0);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (10,'HNSC','Head and Neck squamous cell carcinoma',0);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (11,'LIHC','Liver hepatocellular carcinoma',0);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (12,'CESC','Cervical Squamous Cell Carcinoma',0);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (13,'LAML','Acute Myeloid Leukemia',1);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (14,'LCLL','Chronic Lymphocytic Leukemia',0);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (15,'SKCM','Cutaneous Melanoma',0);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (16,'BLNP','Bladder Urothelial Carcinoma - Non-Papillary (NP)',0);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (17,'BLP','Bladder Urothelial Carcinoma - Papillary (P)',0);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (18,'LNNH','Lymphoid Neoplasm Non-Hodgkins Lymphoma',0);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (20,'THCA','Thyroid carcinoma',0);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (21,'LGG','Brain Lower Grade Glioma',0);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (22,'PRAD','Prostate adenocarcinoma',0);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (23,'UCEC','Uterine Corpus Endometrioid Carcinoma',1);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (24,'READ','Rectum adenocarcinoma',1);
commit;
insert into tissue (tissue_id,tissue) values (0,'many');
insert into tissue (tissue_id,tissue) values (1,'brain');
insert into tissue (tissue_id,tissue) values (2,'lung');
insert into tissue (tissue_id,tissue) values (3,'ovary');
insert into tissue (tissue_id,tissue) values (4,'cell line control');
insert into tissue (tissue_id,tissue) values (5,'breast');
insert into tissue (tissue_id,tissue) values (6,'kidney');
insert into tissue (tissue_id,tissue) values (7,'colon');
insert into tissue (tissue_id,tissue) values (8,'liver');
insert into tissue (tissue_id,tissue) values (9,'lymph');
insert into tissue (tissue_id,tissue) values (10,'blood');
insert into tissue (tissue_id,tissue) values (11,'stomach');
insert into tissue (tissue_id,tissue) values (13,'thyroid');
insert into tissue (tissue_id,tissue) values (14,'bladder');
insert into tissue (tissue_id,tissue) values (15,'endometrial');
insert into tissue (tissue_id,tissue) values (16,'skin');
insert into tissue (tissue_id,tissue) values (17,'rectal');
insert into tissue (tissue_id,tissue) values (18,'cervix');
insert into tissue (tissue_id,tissue) values (19,'prostate');
insert into tissue (tissue_id,tissue) values (20,'head and neck');
insert into tissue (tissue_id,tissue) values (21,'fallopian tube');


insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,1,1);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,4,1);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,0,1);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,2,3);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,4,2);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,0,2);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,4,3);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,3,2);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,0,3);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,2,4);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,5,5);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,7,6);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,6,7);

insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,6,8);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,11,9);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,20,10);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,8,11);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,18,12);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,10,13);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,10,14);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,16,15);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,14,16);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,14,17);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,9,18);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,13,20);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,1,21);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,19,22);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,15,23);
insert into tissue_to_disease (tissue_disease_id,tissue_id,disease_id) values (tissue_disease_seq.NEXTVAL,17,24);

insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('01','International Genomics Consortium',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('02','MD Anderson Cancer Center',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('03','Lung Cancer Tissue Bank of CALGB',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('04','Gynecologic Oncology Group',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('05','Indivumed',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('06','Henry Ford Hospital',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('07','TGen',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('08','UCSF',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('09','UCSF',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('10','MD Anderson Cancer Center',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('11','MD Anderson Cancer Center',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('12','Duke',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('13','Memorial Sloan Ketterling',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('14','Emory University',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('15','Mayo Clinic - Rochester',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('16','Toronto Western Hospital',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('17','Prince Charles Hospital (Australia)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('18','Princess Margaret Hospital (Canada)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('19','Case Western',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('20','Fox Chase Cancer Center',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('21','Fox Chase Cancer Center',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('22','Mayo Clinic - Rochester',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('23','Cedars Sinai',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('24','Washington University',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('25','Mayo Clinic - Rochester',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('26','University of Florida',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('27','Milan - Italy, Fondazione IRCCS Instituto Neuroligico C. Besta',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('28','Cedars Sinai',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('29','Duke',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('30','Harvard',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('31','Imperial College',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('32','St. Joseph''s Hospital',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('33','Johns Hopkins',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('34','University of Pittsburgh',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('35','Imperial Collect - Royal Brompton',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('36','BC Cancer Agency',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('37','Walter Reed',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('38','UCSF',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('39','MSKCC',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('40','MSKCC',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('41','Christiana Healthcare',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('42','Christiana Healthcare',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('43','Christiana Healthcare',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('44','Christiana Healthcare',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('45','St. Joseph''s Medical Center (CO)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('46','St. Joseph''s Medical Center (CO)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('47','St. Joseph''s Medical Center (CO)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('48','Princess Margaret Hospital (Canada)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('49','Johns Hopkins',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('50','University of Pittsburgh',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('51','Imperial College - Royal Brompton',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('52','Walter Reed',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('53','International Genomics Consortium',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('54','International Genomics Consortium',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('55','International Genomics Consortium',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('56','International Genomics Consortium',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('57','International Genomics Consortium',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('58','UCSF',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('59','Roswell Park',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('60','Roswell Park',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('61','University of Pittsburgh',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('62','Ontario Institute for Cancer Research',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('63','Ontario Institute for Cancer Research',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('64','Fox Chase',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('65','Roswell Park',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('66','Indivumed',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('67','St Joseph''s Medical Center (MD)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('70','ILSBio',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('71','ILSBio',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('72','NCH',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('73','Roswell Park',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('74','Swedish Neurosciences',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('A1','UCSF',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('A2','Walter Reed',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('A3','International Genomics Consortium',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('A4','International Genomics Consortium',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('A5','UCSF',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('A6','Christiana Healthcare',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('A7','Christiana Healthcare',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('A8','Indivumed',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AA','Indivumed',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AB','Washington University',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AC','International Genomics Consortium',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AD','International Genomics Consortium',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AE','Washington University',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AF','Christiana Healthcare',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AG','Indivumed',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AH','International Genomics Consortium',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AI','Washington University',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AJ','International Genomics Conosrtium',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AK','Fox Chase',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AL','Fox Chase',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AM','Cureline',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AN','Cureline',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AO','MSKCC',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AP','MSKCC',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AQ','UNC ',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AR','Mayo',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AS','St. Joseph''s Medical Center-(MD)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AT','St. Joseph''s Medical Center-(MD)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AU','St. Joseph''s Medical Center-(MD)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AV','NCH',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AW','Cureline',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AX','Gynecologic Ongology Group',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AY','UNC',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('AZ','University of Pittsburgh',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('B0','University of Pittsburgh',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('B1','University of Pittsburgh',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('B2','Christiana Healthcare',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('B3','Christiana Healthcare',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('B4','Cureline',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('B5','Duke',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('B6','Duke',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('B7','Cureline',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('B8','UNC',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('B9','UNC',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BA','UNC',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BB','Johns Hopkins',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BC','UNC',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BD','University of Pittsburgh',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BE','Curleline',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BF','Cureline',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BG','University of Pittsburgh',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BH','University of Pittsburgh',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BI','University of Pittsburgh',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BJ','University of Pittsburgh',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BK','Christiana Healthcare',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BL','Christiana Healthcare',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BM','UNC',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BN','Christiana Healthcare',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BP','MSKCC',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BQ','MSKCC',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BR','Asterand',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BS','University of Hawaii',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BT','University of Pittsburgh',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BU','University of Pittsburgh',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BV','St. Joseph''s Medical Center-(MD)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BW','St. Joseph''s Medical Center-(MD)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BX','St. Joseph''s Medical Center-(MD)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BY','St. Joseph''s Medical Center-(MD)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('BZ','St. Joseph''s Medical Center-(MD)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('C2','St. Joseph''s Medical Center-(MD)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('C3','St. Joseph''s Medical Center-(MD)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('C4','Indivumed',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('C5','Medical College of Wisconsin',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('C6','University of Texas MD Anderson Cancer Center',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('C7','University of Utah',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('C8','ILSBio',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('C9','ILSBio',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CA','ILSBio',14); 	 
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CB','ILSBio',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CC','ILSBio',14); 	 
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CD','ILSBio',14);	 
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CE','ILSBio' ,14);	 
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CF','ILSBio',11); 	 
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CG','Indivumed',14);	 
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CH','Indivumed',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CI','University of Pittsburgh',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CJ','MD Anderson Cancer Center',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CK','Harvard',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CL','Harvard',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CM','MSKCC',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CN','University of Pittsburgh',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CP','Swedish Neurosciences',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CQ','University Health Network, Toronto',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CR','Vanderbilt University',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CS','Thomas Jefferson University',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CT','UNC',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CU','UNC',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CV','MD Anderson Cancer Center',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CW','Mayo Clinic - Rochester',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CX','Medical College of Georgia',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CY','Ontario Institute for Cancer Research (OICR)',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('CZ','Harvard',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('D1','Mayo Clinic',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('D2','MD Anderson',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('D3','MD Anderson',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('D4','MD Anderson',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('D5','Greater Poland Cancer Center',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('D6','Greater Poland Cancer Center',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('D7','Greater Poland Cancer Center',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('D8','Greater Poland Cancer Center',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('D9','Greater Poland Cancer Center',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('DA','Yale',11);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('DB','Mayo Clinic - Rochester',14);
insert into tissue_source_site (tss_code,tss_definition, receiving_center_id) values ('DC','MSKCC',14);

insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (1,'01',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (2,'02',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (3,'03',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (4,'04',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (5,'05',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (6,'06',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (7,'07',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (8,'07',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (9,'07',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (10,'07',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (11,'07',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (12,'07',6);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (13,'07',7);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (14,'07',8);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (15,'07',9);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (16,'07',10);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (17,'07',11);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (18,'07',12);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (19,'07',13);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (20,'07',14);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (21,'07',15);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (22,'07',16);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (23,'07',17);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (24,'07',18);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (25,'07',20);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (26,'07',21);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (27,'07',22);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (28,'07',23);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (29,'07',24);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (30,'08',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (31,'09',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (32,'10',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (33,'11',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (34,'12',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (35,'13',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (36,'14',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (37,'15',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (38,'16',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (39,'17',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (40,'18',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (41,'19',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (42,'20',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (43,'21',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (44,'22',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (45,'23',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (46,'24',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (47,'25',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (48,'26',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (49,'27',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (50,'28',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (51,'29',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (52,'30',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (53,'31',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (54,'32',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (55,'33',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (56,'34',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (57,'35',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (58,'36',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (59,'37',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (60,'38',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (61,'39',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (62,'40',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (63,'41',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (64,'42',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (65,'43',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (66,'44',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (67,'45',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (68,'46',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (69,'47',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (70,'48',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (71,'49',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (72,'50',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (73,'51',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (74,'55',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (75,'56',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (76,'57',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (77,'59',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (78,'60',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (79,'61',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (80,'64',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (81,'65',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (82,'66',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (83,'67',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (84,'70',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (85,'71',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (86,'72',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (87,'73',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (88,'74',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (89,'A1',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (90,'A2',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (91,'A3',8);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (92,'A4',7);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (93,'A5',23);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (94,'A6',6);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (95,'A7',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (96,'A8',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (97,'AA',6);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (98,'AB',13);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (99,'AC',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (100,'AD',6);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (101,'AE',6);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (102,'AF',24);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (103,'AG',24);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (104,'AH',24);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (105,'AI',24);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (106,'AJ',23);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (107,'AK',8);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (108,'AL',7);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (109,'AM',6);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (110,'AN',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (111,'AO',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (112,'AP',23);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (113,'AQ',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (114,'AR',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (115,'AS',8);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (116,'AT',7);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (117,'AU',6);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (118,'AV',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (119,'AV',2);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (120,'AV',3);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (121,'AV',10);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (122,'AV',4);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (123,'AV',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (124,'AV',7);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (125,'AV',8);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (126,'AV',11);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (127,'AV',16);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (128,'AV',15);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (129,'AV',24);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (130,'AV',6);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (131,'AV',22);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (132,'AV',23);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (133,'AV',13);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (134,'AV',9);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (135,'AV',14);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (136,'AV',21);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (137,'AV',17);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (138,'AV',20);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (139,'AV',18);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (140,'AV',12);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (141,'AW',23);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (142,'AX',23);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (143,'AY',6);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (144,'AZ',6);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (145,'B0',7);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (146,'B1',8);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (147,'B2',7);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (148,'B3',8);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (149,'B4',7);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (150,'B5',23);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (151,'B6',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (152,'B7',9);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (153,'B8',7);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (154,'B9',8);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (155,'BA',10);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (156,'BB',10);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (157,'BC',11);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (158,'BD',11);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (159,'BE',10);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (160,'BF',15);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (161,'BG',23);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (162,'BH',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (163,'BI',12);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (164,'BJ',20);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (165,'BK',23);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (166,'BL',17);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (167,'BM',24);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (168,'BN',16);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (169,'BN',17);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (170,'BP',7);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (171,'BQ',8);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (172,'BR',9);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (173,'BS',23);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (174,'BT',16);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (175,'BU',17);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (176,'BV',21);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (177,'BW',11);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (178,'BX',9);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (179,'BY',10);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (180,'BZ',20);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (181,'C2',18);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (182,'C3',22);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (183,'C4',16);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (184,'C5',12);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (185,'C6',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (186,'C7',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (187,'C8',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (188,'C9',17);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (189,'CA',6);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (190,'CB',7);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (191,'CC',11);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (192,'CD',9);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (193,'CE',20);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (194,'CF',16);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (195,'CG',9);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (196,'CH',22);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (197,'CI',24);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (198,'CJ',7);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (199,'CK',6);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (200,'CL',24);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (201,'CM',6);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (202,'CN',10);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (203,'CP',21);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (204,'CQ',10);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (205,'CR',10);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (206,'CS',21);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (207,'CT',17);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (208,'CU',16);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (209,'CV',10);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (210,'CW',7);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (211,'CX',10);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (212,'CY',6);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (213,'CZ',7);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (214,'D1',23);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (215,'D2',23);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (216,'D3',15);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (217,'D4',13);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (218,'D5',6);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (219,'D6',10);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (220,'D7',9);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (221,'D8',5);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (222,'D9',15);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (223,'DA',15);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (224,'DB',1);
insert into tss_to_disease (tss_disease_id,tss_code,disease_id) values (225,'DC',24);
commit;

INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (1,1,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (2,1,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (3,1,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (4,1,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (5,1,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (6,1,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (7,1,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (8,1,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (9,2,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (10,1,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (11,2,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (12,2,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (13,2,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (14,2,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (15,2,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (16,1,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (17,2,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (18,2,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (19,2,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (20,1,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (21,2,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (22,2,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (23,3,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (24,2,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (25,13,13);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (26,1,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (28,6,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (29,6,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (30,6,11);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (31,3,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (32,7,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (33,6,11);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (34,4,13);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (35,6,13);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (36,6,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (37,4,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (38,1,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (39,3,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (40,2,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (41,6,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (42,24,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (43,24,11);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (44,2,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (45,6,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (46,24,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (47,5,11);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (48,9,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (49,23,11);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (50,7,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (51,8,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (52,4,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (53,3,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (54,10,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (55,11,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (56,5,11);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (57,9,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (58,4,14);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (59,23,11);
INSERT INTO batch_number_assignment(batch_id,disease_id,center_id) values (60,3,14);
commit;

