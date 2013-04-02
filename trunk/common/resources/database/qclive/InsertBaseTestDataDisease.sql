/* 
** To be used in unit test database only
*/

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

--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (1,'GBM','Glioblastoma multiforme',1);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (3,'LUSC','Lung squamous cell carcinoma',1);
INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (2,'OV','Ovarian serous cystadenocarcinoma',1);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (4,'LUAD','Lung adenocarcinoma',1);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (5,'BRCA','Breast invasive carcinoma',1);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (6,'COAD','Colon adenocarcinoma',1);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (7,'KIRC','Kidney renal clear cell carcinoma',0);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (8,'KIRP','Kidney renal papillary cell carcinoma',1);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (9,'STAD','Stomach adenocarcinoma',0);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (10,'HNSC','Head and Neck squamous cell carcinoma',0);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (11,'LIHC','Liver hepatocellular carcinoma',0);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (12,'CESC','Cervical Squamous Cell Carcinoma',0);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (13,'LAML','Acute Myeloid Leukemia',1);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (14,'LCLL','Chronic Lymphocytic Leukemia',0);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (15,'SKCM','Cutaneous Melanoma',0);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (16,'BLNP','Bladder Urothelial Carcinoma - Non-Papillary (NP)',0);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (17,'BLP','Bladder Urothelial Carcinoma - Papillary (P)',0);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (18,'LNNH','Lymphoid Neoplasm Non-Hodgkins Lymphoma',0);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (20,'THCA','Thyroid carcinoma',0);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (21,'LGG','Brain Lower Grade Glioma',0);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (22,'PRAD','Prostate adenocarcinoma',0);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (23,'UCEC','Uterine Corpus Endometrioid Carcinoma',1);
--INSERT INTO disease (disease_id,disease_abbreviation,disease_name , active) values (24,'READ','Rectum adenocarcinoma',1);
commit;




