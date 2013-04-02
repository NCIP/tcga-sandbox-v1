INSERT INTO CENTER_TYPE (center_type_code,center_type_definition)
VALUES
('BCR','Biospecimen Core Resource');
INSERT INTO CENTER_TYPE (center_type_code,center_type_definition)
VALUES
('CGCC','Cancer Genome Characterization Center');
INSERT INTO CENTER_TYPE (center_type_code,center_type_definition)
VALUES
('GSC','Genomic Sequencing Center');
INSERT INTO center (
center_id,
domain_name,
center_type_code,
center_display_name, 
center_short_name,
sort_order)
VALUES(
1,
'broad.mit.edu',
'CGCC',
'Broad Institute of MIT and Harvard',
'BI',
2);
INSERT INTO center (
center_id,domain_name,center_type_code,center_display_name, center_short_name,sort_order)
VALUES(
2,'jhu-usc.edu','CGCC','Johns Hopkins / University of Southern California','JHU_USC',5);
INSERT INTO center (
center_id,domain_name,center_type_code,center_display_name, center_short_name,sort_order)
VALUES(
4,'lbl.gov','CGCC','Lawrence Berkeley National Laboratory','LBL',6);
INSERT INTO center (
center_id,domain_name,center_type_code,center_display_name, center_short_name,sort_order)
VALUES(
3,'hms.harvard.edu','CGCC','Harvard Medical School','HMS',3);
INSERT INTO center (
center_id,domain_name,center_type_code,center_display_name, center_short_name,sort_order)
VALUES(
6,'hudsonalpha.org','CGCC','HudsonAlpha Institute for Biotechnology','HAIB',10);
INSERT INTO center (
center_id,domain_name,center_type_code,center_display_name, center_short_name,sort_order)
VALUES(
5,'mskcc.org','CGCC','Memorial Sloan-Kettering Cancer Center','MSKCC',7);
INSERT INTO center (
center_id,domain_name,center_type_code,center_display_name, center_short_name,sort_order)
VALUES(
7,'unc.edu','CGCC','University of North Carolina','UNC',8);
INSERT INTO center (
center_id,domain_name,center_type_code,center_display_name, center_short_name,sort_order)
VALUES(
12,'broad.mit.edu','GSC','Broad Institute of MIT and Harvard','BI',1);
INSERT INTO center (
center_id,domain_name,center_type_code,center_display_name, center_short_name,sort_order)
VALUES(
11,'intgen.org','BCR','IGC Biospecimen Core Resource','BCR',4);
INSERT INTO center (
center_id,domain_name,center_type_code,center_display_name, center_short_name,sort_order)
VALUES(
8,'hgsc.bcm.edu','GSC','Baylor College of Medicine','BCM',1);
INSERT INTO center (
center_id,domain_name,center_type_code,center_display_name, center_short_name,sort_order)
VALUES(
9,'genome.wustl.edu','GSC','Washington University School of Medicine','WUSM',12);

INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) 
VALUES (1,'Genome_Wide_SNP_6','Affymetrix Genome-Wide Human SNP Array 6.0','Genome_Wide_SNP_6',8,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) 
VALUES (2,'IlluminaDNAMethylation_OMA002_CPI','Illumina DNA Methylation OMA002 Cancer Panel I','IlluminaDNAMethylation',17,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (6,'HuEx-1_0-st-v2','Affymetrix Human Exon 1.0 ST Array','HuEx-1_0-st-v2',4,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (5,'HG-CGH-244A','Agilent Human Genome CGH Microarray 244A','HG-CGH-244A',10,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (7,'HumanHap550','Illumina 550K Infinium HumanHap550 SNP Chip','HumanHap550',18,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (3,'IlluminaDNAMethylation_OMA003_CPI','Illumina DNA Methylation OMA003 Cancer Panel I','IlluminaDNAMethylation',17,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (4,'HT_HG-U133A','Affymetrix HT Human Genome U133 Array Plate Set','HT_HG-U133A',3,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (21,'HG-CGH-415K_G4124A','Agilent Human Genome CGH Custom Microarray 2x415K','HG-CGH-415K_G4124A',9,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (8,'AgilentG4502A_07_1','Agilent 244K Custom Gene Expression G4502A-07-1','AgilentG4502A_07',13,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (10,'AgilentG4502A_07_2','Agilent 244K Custom Gene Expression G4502A-07-2','AgilentG4502A_07',13,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (13,'HumanMethylation27','Illumina Infinium Human DNA Methylation 27','HumanMethylation27',17,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (14,'AgilentG4502A_07_3','Agilent 244K Custom Gene Expression G4502A-07-3','AgilentG4502A_07',13,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (17,'ABI','Applied Biosystems Sequence data','ABI',21,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (12,'H-miRNA_8x15K','Agilent 8 x 15K Human miRNA-specific microarray','H-miRNA_8x15K',14,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (15,'CGH-1x1M_G4447A','Agilent SurePrint G3 Human CGH Microarray Kit 1x1M','CGH-1x1M_G4447A',15,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (22,'IlluminaGA_mRNA_DGE','Illumina Genome Analyzer mRNA Digital Gene Expression','IlluminaGA_mRNA_DGE',17,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (23,'HG-U133A_2','Affymetrix Human Genome U133A 2.0 Array','HG-U133A_2',1,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (24,'HG-U133_Plus_2','Affymetrix Human Genome U133 Plus 2.0 Array','HG-U133_Plus_2',2,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (25,'Mapping250K_Nsp','Affymetrix Human Mapping 250K Nsp Array','Mapping250K_Nsp',5,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (26,'Mapping250K_Sty','Affymetrix Human Mapping 250K Sty Array','Mapping250K_Sty',6,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (27,'GenomeWideSNP_5','Affymetrix Genome-Wide Human SNP Array 5.0','GenomeWideSNP_5',7,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (28,'tissue_images','Tissue Images','tissue_images',23,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (29,'IlluminaGG','Illumina GoldenGate','IlluminaGG',16,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (30,'bio','Biospecimen Metadata - Complete Set','bio',19,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (31,'454','454 Life Sciences Genome Sequence data','454',22,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (32,'minbio','Biospecimen Metadata - Minimal Set','minbio',20,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (33,'H-miRNA_EarlyAccess','Agilent Human miRNA Early Access Array','H-miRNA_EarlyAccess',15,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (34,'WHG-CGH_4x44B','Agilent Human Genome CGH Microarray 44K','WHG-CGH_4x44B',9,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (35,'WHG-1x44K_G4112A','Agilent Whole Human Genome\','1 x 44K',11,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (36,'H-miRNA_G4470A','Agilent Human miRNA Microarray','H-miRNA_G4470A',13,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (37,'minbiotab','Biospecimen Metadata - Minimal Set - All Samples - Tab-delimited','minbiotab',20,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (38,'biotab','Biospecimen Metadata - Complete Set - All Samples - Tab-delimited','biotab',19,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (39,'WHG-4x44K_G4112F','Agilent Whole Human Genome Microarray Kit\','4 x 44K',12,0);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (16,'Human1MDuo','Illumina Human1M-Duo BeadChip','Human1MDuo',17,1);
INSERT INTO platform (platform_id,  platform_name ,platform_display_name, platform_alias,sort_order, available) VALUES (20,'H-miRNA_8x15Kv2','Agilent Human miRNA Microarray Rel12.0','H-miRNA_8x15K',13,1);

INSERT INTO disease (disease_id,  disease_name ,disease_abbreviation, active) VALUES (1,'Glioblastoma multiforme','GBM',1);
INSERT INTO disease (disease_id,  disease_name ,disease_abbreviation, active) VALUES (2,'Serous cystadenocarcinoma','OV',1);
INSERT INTO disease (disease_id,  disease_name ,disease_abbreviation, active) VALUES (8,'Lung squamous cell carcinoma','LUSC',0);
INSERT INTO disease (disease_id,  disease_name ,disease_abbreviation, active) VALUES (9,'Lung adenocarcinoma','LUAD',0);
INSERT INTO disease (disease_id,  disease_name ,disease_abbreviation, active) VALUES (10,'Breast ductal carcinoma','BRDC',0);
INSERT INTO disease (disease_id,  disease_name ,disease_abbreviation, active) VALUES (11,'Breast lobular carcinoma','BRLC',0);
INSERT INTO disease (disease_id,  disease_name ,disease_abbreviation, active) VALUES (12,'Kidney clear cell carcinoma','KIRC',0);
INSERT INTO disease (disease_id,  disease_name ,disease_abbreviation, active) VALUES (13,'Kidney papillary cell carcinoma','KIRP',0);
INSERT INTO disease (disease_id,  disease_name ,disease_abbreviation, active) VALUES (3,'Lung','LG',0);

INSERT INTO tissue (tissue_id, tissue) VALUES (1,'brain');
INSERT INTO tissue (tissue_id, tissue) VALUES (5,'breast');
INSERT INTO tissue (tissue_id, tissue) VALUES (4,'cell line');
INSERT INTO tissue (tissue_id, tissue) VALUES (7,'colon');
INSERT INTO tissue (tissue_id, tissue) VALUES (6,'kidney');
INSERT INTO tissue (tissue_id, tissue) VALUES (2,'lung');
INSERT INTO tissue (tissue_id, tissue) VALUES (0,'many');
INSERT INTO tissue (tissue_id, tissue) VALUES (3,'ovary');

INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('66','Indivumed',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('67','St Joseph''s Medical Center (MD)',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('06','Henry Ford Hospital',1);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('16','Toronto Western Hospital',1);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('19','Case Western',1);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('26','University of Florida',1);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('27','Milan - Italy, Fondazione IRCCS Instituto Neuroligico C. Besta',1);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('03','Lung Cancer Tissue Bank of CALGB',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('04','Gynecologic Oncology Group',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('13','Memorial Sloan Ketterling',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('29','Duke',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('24','Washington University',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('30','Harvard',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('02','MD Anderson Cancer Center',1);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('07','TGen',4);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('08','UCSF',1);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('09','UCSF',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('10','MD Anderson Cancer Center',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('11','MD Anderson Cancer Center',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('12','Duke',1);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('14','Emory University',1);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('15','Mayo Clinic - Rochester',1);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('17','Prince Charles Hospital (Australia)',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('18','Princess Margaret Hospital (Canada)',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('20','Fox Chase Cancer Center',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('21','Fox Chase Cancer Center',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('22','Mayo Clinic - Rochester',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('23','Cedars Sinai',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('25','Mayo Clinic - Rochester',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('28','Cedars Sinai',1);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('01','International Genomics Consortium',0);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('05','National Cancer Institute',0);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('32','St. Joseph''s Hospital',1);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('41','Christiana Healthcare',1);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('45','St. Joseph''s Medical Center (CO)',1);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('33','Johns Hopkins',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('34','University of Pittsburgh',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('35','Imperial Collect - Royal Brompton',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('39','MSKCC',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('40','MSKCC',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('43','Christiana Healthcare',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('44','Christiana Healthcare',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('46','St. Joseph''s Medical Center (CO)',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('48','Princess Margaret Hospital (Canada)',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('49','Johns Hopkins',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('50','University of Pittsburgh',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('51','Imperial College - Royal Brompton',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('55','International Genomics Consortium',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('56','International Genomics Consortium',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('60','Roswell Park',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('63','Ontario Institute for Cancer Research',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('64','Fox Chase',2);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('31','Imperial College',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('36','BC Cancer Agency',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('42','Christiana Healthcare',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('47','St. Joseph''s Medical Center (CO)',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('57','International Genomics Consortium',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('59','Roswell Park',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('61','University of Pittsburgh',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('62','Ontario Institute for Cancer Research',3);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('37','Walter Reed',5);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('38','UCSF',5);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('52','Walter Reed',5);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('58','UCSF',5);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('53','International Genomics Consortium',6);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('54','International Genomics Consortium',6);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('A0','Walter Reed',5);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('A1','UCSF',5);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('A2','Walter Reed',5);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('A3','International Genomics Consortium',6);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('A4','International Genomics Consortium',6);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('A5','UCSF',5);
INSERT INTO collection_site (collection_site_code, site_definition, tissue_id) VALUES ('A7','Christiana Healthcare',5);

INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (1,'BCR','BCR Complete Set','Private',1,1);
INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (2,'BCR','BCR Minimal Clinical Set','Public',0,1);
INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (4,'CGCC','Methylation','Public',0,1);
INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (5,'CGCC','SNP Low Level','Private',1,1);
INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (6,'CGCC','aCGH/CGH/CAN','Public',0,1);
INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (7,'CGCC','SNP Higher Level Analysis','Public',0,1);
INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (8,'GSC','Somatic Mutations','Public',0,0);
INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (9,'GSC','Trace Relationship','Private',1,0);
INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (10,'GSC','Trace','Public',0,0);
INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (11,'CGCC','Expression-Exon','Private',1,1);
INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (12,'CGCC','Expression-miRNA','Public',0,1);
INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (13,'CGCC','Expression-Genes','Public',0,1);
INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (14,'BCR','BCR Tissue Slide Images','Public',0,1);
INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (15,'CGCC','Expression-Exon','Public',1,1);
INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (16,'CGCC','SNP Low Level','Public',1,1);
INSERT INTO visibility (visibility_id, center_type_code, display_name, visibility_type,identifiable,publish) VALUES (17,'GSC','Trace Relationship','Public',1,0);

commit;