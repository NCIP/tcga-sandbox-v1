DROP TABLE dcc_property;
CREATE TABLE dcc_property (
    property_id            	NUMBER(38,0)    NOT NULL,
    property_name           	VARCHAR2(500)    NOT NULL,
    property_value        	VARCHAR2(4000),
    property_description	VARCHAR2(4000),
    application_name         	VARCHAR2(100),
    server_name         	VARCHAR2(100),
    CONSTRAINT pk_dcc_property_idx PRIMARY KEY (property_id)
);

CREATE UNIQUE INDEX uk_dcc_property_idx on dcc_property(application_name,property_name,server_name);
CREATE INDEX dcc_prop_propname_idx ON dcc_property (property_name);
CREATE INDEX dcc_prop_servname_idx ON dcc_property (server_name);

DROP SEQUENCE dcc_property_seq;
CREATE SEQUENCE dcc_property_seq START WITH 1 INCREMENT BY 1;

DROP SEQUENCE vcf_header_seq;
CREATE SEQUENCE vcf_header_seq START WITH 1 INCREMENT BY 1;

DROP TABLE vcf_header_definition;
CREATE TABLE vcf_header_definition (
    vcf_header_Def_id	NUMBER(38)	NOT NULL,
    header_type_name    VARCHAR2(10)	NOT NULL CHECK (header_type_name in ('INFO','FORMAT')),
    id_name            	VARCHAR2(500)	NOT NULL,
    number_value 	VARCHAR2(10)	NOT NULL,
    type            	VARCHAR2(25)    NOT NULL,
    description        	VARCHAR2(4000)	NOT NULL,
    CONSTRAINT pk_vcf_header_def_idx PRIMARY KEY (vcf_header_def_id)
);

CREATE UNIQUE INDEX uk_vcf_header_def_idx ON vcf_header_definition (header_Type_name,id_name);

DROP TABLE pcod_normal_tumor_stats;
CREATE TABLE pcod_normal_tumor_stats(
	disease_abbreviation		VARCHAR2(10)	NOT NULL,
	participant_barcode		VARCHAR2(50)	NOT NULL,
	cn_TumorCount			INTEGER,
	cn_NormalCount			INTEGER,
	expArray_TumorCount		INTEGER,
	expArray_NormalCount		INTEGER,
	expRnaSeq_TumorCount		INTEGER,
	expRnaSeq_NormalCount		INTEGER,
	mirna_TumorCount		INTEGER,
	mirna_NormalCount		INTEGER,
	methylation_TumorCount		INTEGER,
	methylation_NormalCount		INTEGER,
	mutation_TumorCount		INTEGER,
	mutation_NormalCount		INTEGER,
	gsc_genome_TumorCount		INTEGER,
	gsc_genome_NormalCount		INTEGER,
	gsc_exome_TumorCount		INTEGER,
	gsc_exome_NormalCount		INTEGER,
	gsc_rnaseq_TumorCount		INTEGER,
	gsc_rnaseq_NormalCount		INTEGER,
	gsc_mirna_TumorCount		INTEGER,
	gsc_mirna_NormalCount		INTEGER);
	
ALTER TABLE projectoverview_case_Counts DROP (
    total_metholated_cases,
    total_microRna_cases,
    total_expArray_cases,
    total_expRnaSeq_cases,
    total_copyNumber_cases ,
    total_mutation_cases ,
    total_gsc_genome_cases,
    total_gsc_exome_cases ,
    total_gsc_rnaseq_cases ,
    total_gsc_microRna_cases );

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','SS','1','Integer','"Variant status relative to non-adjacent Normal,0=wildtype,1=germline,2=somatic,3=LOH,4=post-transcriptional modification,5=unknown"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','TE','4','Integer','"Translational effect of the variant in a codon"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','AD','.','Integer','"Depth of reads supporting alleles 0/1/2/3..."');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','DP4','4','Integer','"Number of high-quality ref-forward, ref-reverse, alt-forward and alt-reverse bases"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','BQ','.','Integer','"Average base quality for reads supporting alleles"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','VAQ','1','Integer','"Variant allele quality"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','GT','1','String','"Genotype"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','DP','1','Integer','"Read depth at this position in the sample"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','FT','1','String','"Sample genotype filter"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','GL','.','FLOAT','"Genotype likelihoods"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','PL','3','Integer','"Normalized, Phred-scaled likelihoods for AA,AB,BB genotypes where A=ref and B=alt; not applicable if site is not biallelic"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','GP','.','Float','"Phred-scaled genotype posterior probabilities"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','GQ','.','Integer','"Conditional Phred-scaled genotype quality"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','HQ','2','Integer','"Haplotype qualities, two comma separated phred qualities"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','CN','1','Integer','"Copy number genotype for imprecise events"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','CNQ','1','Float','"Copy number genotype quality for imprecise events"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','CNL','.','Float','"Copy number genotype likelihood for imprecise events"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','MQ','1','Integer','"Phred style probability score that the variant is novel with respect to the genome''s ancestor"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','HAP','1','Integer','"Unique haplotype identifier"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'FORMAT','AHAP','1','Integer','"Unique identifier of ancestral haplotype"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','CICNADJ','.','Integer','"Confidence interval around copy number for the adjacency"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','CICN','2','Integer','"Confidence interval around copy number for the segment"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','CNADJ','.','Integer','"Copy number of adjacency"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','CN','1','Integer','"Copy number of segment containing breakend"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','DPADJ','.','Integer','"Read Depth of adjacency"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','CILEN','2','Integer','"Confidence interval around the length of the inserted material between breakends"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','EVENT','1','String','"ID of event associated to breakend"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','PARID','1','String','"ID of partner breakend"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','MATEID','.','String','"ID of mate breakends"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','CBRIPID','1','String','"ID of this element in DBRIP"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','DBVARID','1','String','"ID of this element in DBVAR"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','DGVID','1','String','"ID of this element in Database of Genomic Variation"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','METRANS','4','String','"Mobile element transduction info of the form CHR,START,END,POLARITY"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','MEINFO','4','String','"Mobile element info of the form NAME,START,END,POLARITY"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','BKPTID','.','String','"ID of the assembled alternate allele in the assembly file"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','HOMSEQ','.','String','"Sequence of base pair identical micro-homology at event breakpoints"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','HOMLEN','.','Integer','"Length of base pair identical micro-homology at event breakpoints"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','CIEND','2','Integer','"Confidence interval around END for imprecise variants"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','CIPOS','2','Integer','"Confidence interval around POS for imprecise variants"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','SVLEN','.','Integer','"Difference in length between REF and ALT alleles"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','SVTYPE','1','String','"Type of structural variant"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','NOVEL','0','Flag','"Indicates a novel structural variation"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','IMPRECISE','0','Flag','"Imprecise structural variation"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','1000G','0','Flag','"Indicates membership in 1000Genomes"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','VALIDATED','0','Flag','"Indicates if variant has been validated by follow-up experiment"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','SOMATIC','0','Flag','"Indicates if record is a somatic mutation"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','SB','1','Float','"Strand bias"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','VLS','1','Integer','"Final validation status relative to non-adjacent Normal,0=wildtype,1=germline,2=somatic,3=LOH,4=post transcriptional modification,5=unknown"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','SID','.','String','"Unique identifier from gene annotation source or unknown"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','GENE','.','String','"HUGO gene symbol or Unknown"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','RGN','.','String','"Region where nucleotide variant occurs in relation to a gene"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','RE','0','Flag','"Position known to have RNA-edits to occur"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','VT','1','String','"Variant type, can be SNP, INS or DEL"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','AA','1','String','"Ancestral Allele"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','AC','.','Integer','"Allele count in genotypes, for each ALT allele, in the same order as listed"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','AF','.','Float','"Allele Frequency in primary data, for each ALT allele, in the same order as listed"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','AN','1','Integer','"Total number of alleles in called genotypes"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','BQ','1','Integer','"RMS base quality"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','CIGAR','1','Integer','"Cigar string describing how to align an alternate allele to the reference allele"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','DB','0','Flag','"dbSNP membership"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','DP','1','Integer','"Total Depth across samples"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','END','1','Integer','"End position of the variant described in this record"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','H2','0','Flag','"HapMap2 membership"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','H3','0','Flag','"HapMap3 membership"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','MQ','1','Integer','"RMS Mapping Quality"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','MQ0','1','Integer','"Total Mapping Quality Zero Reads"');

INSERT INTO vcf_header_definition(vcf_header_def_id,header_type_name,id_name ,number_value ,type,description)
VALUES (vcf_header_seq.NEXTVAL,'INFO','NS','1','Integer','"Number of Samples With Data"');

commit;

DROP sequence pending_uid_seq;
CREATE sequence pending_uid_seq start with 1 increment by 1;

DROP TABLE pending_uuid;
CREATE TABLE pending_uuid (
   pending_uuid_id    NUMBER(38)    NOT NULL,
   bcr             VARCHAR2(10)    NOT NULL,
   center        VARCHAR2(10),
   ship_date        DATE,
   plate_id        VARCHAR2(10),   
   batch_number      INTEGER,
   plate_coordinate    VARCHAR2(10),
   uuid            VARCHAR2(36),
   barcode        VARCHAR2(50),
   sample_type        VARCHAR2(10),
   analyte_type        VARCHAR2(10),
   portion_number    VARCHAR2(10),
   vial_number        VARCHAR2(10),
   item_type        VARCHAR2(20),
   dcc_received_date    DATE,
   created_date        DATE DEFAULT sysdate    NOT NULL,
   CONSTRAINT pk_pending_uuid_idx PRIMARY KEY (pending_uuid_id)
);


---- POPULATE ROLES FOR DATA REPORTS ---
declare
  datareports_group_id number;
begin

-- GROUPS
insert into groups(group_name) values ('DataReports') returning id into datareports_group_id;

-- GROUP_AUTHORITIES
insert into group_authorities(group_id, authority) values(datareports_group_id, 'ROLE_PENDING_METADATA_WS_USER');

-- GROUP_MEMBERS
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'arik', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'blackaa', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'bowenj', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'cbiitappscan', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'chenjw', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'chual', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'craind', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'demchokja', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'eleyg2', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'fergusonm', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'greenejm2', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'hayesd', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'holleys', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'mcclurbe', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'pyena', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'savagec', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'sfeirr', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'shawk', datareports_group_id);
insert into group_members(id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'snyderee', datareports_group_id);

commit;
end;
/

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
     **  04/02/2012   Created as a package to include existing procedure build_projectOverview_counts and add new procdure
     **               get_pcod_normal_tumor_counts to populate a new table which will be used to determine whether or not a
     **               case is complete (APPS-5706)
     **
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
         max(decode(data_type,'mute', cases, 0)) mutationCases 
      FROM  
       (SELECT d.disease_abbreviation,
           COUNT(distinct b.participant_code) as cases, 
           CASE  WHEN dt.ftp_display IN ('snp','cna') THEN 'cn' 
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
             CASE  WHEN dt.ftp_display IN ('snp','cna') THEN 'cn'  WHEN dt.ftp_display IN ('transcriptome','exon') THEN 'exp' WHEN dt.ftp_display IN ('mirna','mirnaseq') THEN 'mirna' WHEN dt.ftp_display IN ('mutations', 'tracerel') THEN 'mute' WHEN dt.ftp_display IN ('rnaseq', 'rnaseqV2') THEN 'rnaseq' ELSE dt.ftp_display END ) 
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
            AND   b.disease_id      = d.disease_id)
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

INSERT INTO data_type(data_type_id,name,center_type_code,ftp_display,available,sort_order)
VALUES
(38,'RNASeqV2','CGCC','rnaseqV2',1,38);

INSERT INTO platform(platform_id,platform_name,platform_display_name,platform_alias,center_type_code,sort_order,available,base_data_Type_id)
VALUES
(57,'IlluminaGA_RNASeqV2','Illumina Genome Analyzer RNA Sequencing Version 2 analysis','IlluminaGA_RNASeqV2','CGCC',57,1,38);
INSERT INTO platform(platform_id,platform_name,platform_display_name,platform_alias,center_type_code,sort_order,available,base_data_Type_id)
VALUES
(58,'IlluminaHiSeq_RNASeqV2','Illumina HiSeq 2000 RNA Sequencing Version 2 analysis','IlluminaHiSeq_RNASeqV2','CGCC',58,1,38);

INSERT INTO data_type_to_platform (data_type_platform_id,data_Type_id,platform_id) VALUES(129,38,57);
INSERT INTO data_type_to_platform (data_type_platform_id,data_Type_id,platform_id) VALUES(137,38,58);

INSERT INTO data_visibility (data_visibility_id,data_type_id,visibility_id,level_number) values (128,38,1,3);
INSERT INTO data_visibility (data_visibility_id,data_type_id,visibility_id,level_number) values (129,38,1,0);

commit;

ALTER TABLE shipped_biospecimen ADD (batch_id INTEGER);

MERGE INTO shipped_biospecimen sb
USING
(select distinct sb.shipped_biospecimen_id,a.serial_index
 from shipped_biospecimen sb, shipped_biospec_bcr_archive sba, archive_info a
 where sb.shipped_biospecimen_id = sba.shipped_biospecimen_id
 and   sba.archive_id = a.archive_id and a.is_latest = 1 order by 1) v
 ON (sb.shipped_biospecimen_id = v.shipped_biospecimen_id)
 WHEN MATCHED THEN UPDATE
 SET sb.batch_id = v.serial_index;
 commit;
-- these we had to do specifically because there were 2 batch id's connected with them; we took the latest
update shipped_biospecimen set batch_id=4 where shipped_biospecimen_id=9316;
update shipped_biospecimen set batch_id=4 where shipped_biospecimen_id=9326;
update shipped_biospecimen set batch_id=4 where shipped_biospecimen_id=9330;
commit;
MERGE INTO shipped_biospecimen sb
USING
(select distinct biospecimen_id,a.serial_index, max(a.date_added) as date_added
  from bcr_biospecimen_to_archive ba,archive_info a
  where ba.archive_id = a.archive_id
  and biospecimen_id in 
  (select shipped_biospecimen_id from shipped_biospecimen where batch_id is null)
    group by biospecimen_id,a.serial_index 
 order by 1) v
 ON (sb.shipped_biospecimen_id = v.biospecimen_id)
 WHEN MATCHED THEN UPDATE
 SET sb.batch_id = v.serial_index;
 commit;
 
delete from projectoverview_case_counts where disease_abbreviation = 'LNNH';
delete from disease where disease_abbreviation='LNNH';
commit;

update disease set active=1 where disease_abbreviation ='KICH';
commit;

execute build_sample_summary_report;
execute pcod_report.build_projectoverview_counts;

GRANT EXECUTE ON pcod_report to commonmaint;
GRANT ALL ON pcod_normal_tumor_stats to commonmaint;
GRANT ALL ON vcf_header_definition to commonmaint;
GRANT ALL ON pending_uuid to commonmaint;
GRANT SELECT ON pcod_normal_tumor_stats to readonly;
GRANT SELECT ON vcf_header_definition to readonly;
GRANT SELECT ON pending_uuid to readonly;

-- sequence grants
GRANT SELECT ON vcf_header_seq to commonmaint;
GRANT SELECT ON pending_uuid_seq to commonmaint;
