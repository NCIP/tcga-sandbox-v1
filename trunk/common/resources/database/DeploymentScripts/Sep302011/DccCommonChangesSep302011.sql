ALTER TABLE UUID_ITEM_TYPE ADD (xml_name VARCHAR2(20));
update uuid_item_type set xml_name = 'patient' where item_type = 'Participant';
update uuid_item_type set xml_name = 'sample' where item_type = 'Sample';
update uuid_item_type set xml_name = 'portion' where item_type = 'Portion';
update uuid_item_type set xml_name = 'analyte' where item_type = 'Analyte';
update uuid_item_type set xml_name = 'slide' where item_type = 'Slide';
update uuid_item_type set xml_name = 'aliquot' where item_type = 'Aliquot';
update uuid_item_type set xml_name = 'radiation' where item_type = 'Radiation';
update uuid_item_type set xml_name = 'drug' where item_type = 'Drug';
update uuid_item_type set xml_name = 'examination' where item_type = 'Examination';
update uuid_item_type set xml_name = 'surgery' where item_type = 'Surgery';
update uuid_item_type set xml_name = 'shipment_portion' where item_type = 'Shipped Portion';

DROP SEQUENCE file_collection_seq;
CREATE SEQUENCE file_collection_seq START WITH 1 INCREMENT BY 1;

DROP TABLE file_collection cascade constraints;
CREATE TABLE file_collection (
	file_collection_id 	NUMBER(38)	NOT NULL,
	collection_name		VARCHAR2(50)	NOT NULL,
	visibility_id		NUMBER(38)	NOT NULL,
	disease_id		NUMBER(38),
	center_type_code	VARCHAR2(10),
	center_id		NUMBER(38),
	platform_id		NUMBER(38),
	CONSTRAINT pk_file_collection_idx PRIMARY KEY(file_collection_id)
);
ALTER TABLE file_collection ADD (
	CONSTRAINT fk_file_collection_visibility
	FOREIGN KEY(visibility_id)
	REFERENCES visibility(visibility_id),
	CONSTRAINT fk_file_collection_disease
	FOREIGN KEY(disease_id)
	REFERENCES disease(disease_id),
	CONSTRAINT fk_file_collection_center
	FOREIGN KEY(center_id)
	REFERENCES center(center_id),
	CONSTRAINT fk_file_collection_platform
	FOREIGN KEY(platform_id)
	REFERENCES platform(platform_id)
);


DROP TABLE file_to_collection;
CREATE TABLE file_to_collection (
	file_collection_id	NUMBER(38)	NOT NULL,
	file_id			NUMBER(38)	NOT NULL,
	file_location_url	VARCHAR2(2000)	NOT NULL,
	file_date		DATE		NOT NULL,
	CONSTRAINT pk_file_to_collection_idx PRIMARY KEY (file_collection_id,file_id)
);
ALTER TABLE file_to_collection ADD (
	CONSTRAINT fk_file_collection_collection
	FOREIGN KEY(file_collection_id)
	REFERENCES file_collection(file_collection_id),
	CONSTRAINT fk_file_collection_file
	FOREIGN KEY(file_id)
	REFERENCES file_info(file_id)
);


DROP TABLE chromosome;
CREATE TABLE chromosome (
	chromosome_name	VARCHAR2(30) 	NOT NULL,
	length		INTEGER		NOT NULL,
	build		VARCHAR2(30)    NOT NULL,
	CONSTRAINT pk_chromosome_idx PRIMARY KEY (chromosome_name,build)
);
insert into chromosome (chromosome_name, length, build)
values ('1',249250621,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('2',243199373,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('3',198022430,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('4',191154276,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('5',180915260,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('6',171115067,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('7',159138663,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('8',146364022,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('9',141213431,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('10',135534747,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('11',135006516,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('12',133851895,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('13',115169878,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('14',107349540,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('15',102531392,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('16',90354753,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('17',81195210,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('18',78077248,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('19',59128983,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('20',63025520,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('21',48129895,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('22',51304566,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('X',155270560,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('Y',59373566,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('MT',16569,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR1_RANDOM_CTG5',106433,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR1_RANDOM_CTG12',547496,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR4_RANDOM_CTG2',189789,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR4_RANDOM_CTG3',191469,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR7_RANDOM_CTG1',182896,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR8_RANDOM_CTG1',38914,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR8_RANDOM_CTG4',37175,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR9_RANDOM_CTG1',90085,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR9_RANDOM_CTG2',169874,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR9_RANDOM_CTG4',187035,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR9_RANDOM_CTG5',36148,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR11_RANDOM_CTG2',40103,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR17_RANDOM_CTG1',37498,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR17_RANDOM_CTG2',81310,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR17_RANDOM_CTG3',174588,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR17_RANDOM_CTG4',41001,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR18_RANDOM_CTG1',4262,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR19_RANDOM_CTG1',92689,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR19_RANDOM_CTG2',159169,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHR21_RANDOM_CTG9',27682,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG1',166566,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG2',186858,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG3',164239,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG4',137718,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG5',172545,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG6',172294,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG7',172149,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG9',161147,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG10',179198,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG11',161802,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG13',155397,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG14',186861,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG15',180455,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG16',179693,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG17',211173,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG19',15008,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG20',128374,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG21',129120,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG22',19913,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG23',43691,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG24',27386,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG25',40652,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG26',45941,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG27',40531,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG28',34474,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG29',41934,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG30',45867,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG31',39939,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG32',33824,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG33',41933,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG34',42152,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG35',43523,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG36',43341,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG37',39929,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG38',36651,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG39',38154,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG40',36422,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG41',39786,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('HSCHRUN_RANDOM_CTG42',38502,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000191.1',106433,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000192.1',547496,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000193.1',189789,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000194.1',191469,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000195.1',182896,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000196.1',38914,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000197.1',37175,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000198.1',90085,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000199.1',169874,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000200.1',187035,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000201.1',36148,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000202.1',40103,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000203.1',37498,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000204.1',81310,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000205.1',174588,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000206.1',41001,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000207.1',4262,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000208.1',92689,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000209.1',159169,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000210.1',27682,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000211.1',166566,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000212.1',186858,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000213.1',164239,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000214.1',137718,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000215.1',172545,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000216.1',172294,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000217.1',172149,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000218.1',161147,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000219.1',179198,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000220.1',161802,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000221.1',155397,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000222.1',186861,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000223.1',180455,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000224.1',179693,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000225.1',211173,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000226.1',15008,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000227.1',128374,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000228.1',129120,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000229.1',19913,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000230.1',43691,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000231.1',27386,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000232.1',40652,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000233.1',45941,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000234.1',40531,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000235.1',34474,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000236.1',41934,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000237.1',45867,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000238.1',39939,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000239.1',33824,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000240.1',41933,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000241.1',42152,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000242.1',43523,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000243.1',43341,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000244.1',39929,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000245.1',36651,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000246.1',38154,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000247.1',36422,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000248.1',39786,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('GL000249.1',38502,'GRCh37-lite');
insert into chromosome (chromosome_name, length, build)
values ('chr1',247249719,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr1_random',1663265,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr2',242951149,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr2_random',185571,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr3',199501827,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr3_random',749256,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr4',191273063,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr4_random',842648,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr5',180857866,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr5_h2_hap1',1794870,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr5_random',143687,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr6',170899992,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr6_cox_hap1',4731698,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr6_qbl_hap2',4565931,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr6_random',1875562,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr7',158821424,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr7_random',549659,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr8',146274826,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr8_random',943810,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr9',140273252,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr9_random',1146434,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr10',135374737,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr10_random',113275,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr11',134452384,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr11_random',215294,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr12',132349534,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr13',114142980,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr13_random',186858,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr14',106368585,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr15',100338915,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr15_random',784346,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr16',88827254,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr16_random',105485,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr17',78774742,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr17_random',2617613,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr18',76117153,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr18_random',4262,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr19',63811651,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr19_random',301858,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr20',62435964,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr21',46944323,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr21_random',1679693,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr22',49691432,'hg18');
insert into chromosome (chromosome_name, length, build)
values ('chr22_h2_hap1',63661,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chr22_random',257318,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chrX',154913754,'hg18');
insert into chromosome (chromosome_name, length, build)
values ('chrX_random',1719168,'hg18');  
insert into chromosome (chromosome_name, length, build)
values ('chrY',57772954,'hg18');   
insert into chromosome (chromosome_name, length, build)
values ('chrM',16571,'hg18');
insert into chromosome (chromosome_name, length, build)
values ('chr1',249250621,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr1_gl000191_random',106433,'GRCh37');   
insert into chromosome (chromosome_name, length, build)
values ('chr1_gl000192_random',547496,'GRCh37');   
insert into chromosome (chromosome_name, length, build)
values ('chr2',243199373,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chr3',198022430,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chr4',191154276,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chr4_ctg9_hap1',590426,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chr4_gl000193_random',189789,'GRCh37');   
insert into chromosome (chromosome_name, length, build)
values ('chr4_gl000194_random',191469,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr5',180915260,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr6',171115067,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr6_apd_hap1',4622290,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr6_cox_hap2',4795371,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr6_dbb_hap3',4610396,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr6_mann_hap4',4683263,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr6_mcf_hap5',4833398,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr6_qbl_hap6',4611984,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr6_ssto_hap7',4928567,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr7',159138663,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr7_gl000195_random',182896,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr8',146364022,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr8_gl000196_random',38914,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr8_gl000197_random',37175,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr9',141213431,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr9_gl000198_random',90085,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr9_gl000199_random',169874,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr9_gl000200_random',187035,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr9_gl000201_random',36148,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr10',135534747,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr11',135006516,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr11_gl000202_random',40103,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr12',133851895,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr13',115169878,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr14',107349540,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr15',102531392,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr16',90354753,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr17',81195210,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr17_ctg5_hap1',1680828,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr17_gl000203_random',37498,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr17_gl000204_random',81310,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr17_gl000205_random',174588,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr17_gl000206_random',41001,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr18',78077248,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr18_gl000207_random',4262,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr19',59128983,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr19_gl000208_random',92689,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr19_gl000209_random',159169,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr20',63025520,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr21',48129895,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr21_gl000210_random',27682,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chr22',51304566,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrX',155270560,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrY',59373566,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000211',166566,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000212',186858,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000213',164239,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000214',137718,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000215',172545,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000216',172294,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000217',172149,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000218',161147,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000219',179198,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000220',161802,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000221',155397,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000222',186861,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000223',180455,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000224',179693,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000225',211173,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000226',15008 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000227',128374,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000228',129120,'GRCh37');  
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000229',19913 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000230',43691 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000231',27386 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000232',40652 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000233',45941 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000234',40531 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000235',34474 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000236',41934 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000237',45867 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000238',39939 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000239',33824 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000240',41933 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000241',42152 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000242',43523 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000243',43341 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000244',39929 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000245',36651 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000246',38154 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000247',36422 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000248',39786 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrUn_gl000249',38502 ,'GRCh37'); 
insert into chromosome (chromosome_name, length, build)
values ('chrM',16571   ,'GRCh37');
commit;

/*
** if all replacement work is completed on getting data for uuid_hierarchy, drop stored proc, uuid_platform table and all uuid_hierarchy related views
drop procedure populateUuidHierarchy;
drop table uuid_platform;
drop view aliquot_v;
drop view analyte_v;
drop view drug_v;
drop view examination_v;
drop view patient_v;
drop view portion_v;
drop view radiation_v;
drop view sample_v;
drop view shipped_portion_v;
drop view slide_v;
drop view surgery_v;
*/

