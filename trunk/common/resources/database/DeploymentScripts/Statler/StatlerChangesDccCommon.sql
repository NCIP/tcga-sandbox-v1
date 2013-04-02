
-- http://cbiodev80.nci.nih.gov:8087/browse/APPS-2253
DROP TABLE biospecimen_to_bam_file CASCADE CONSTRAINTS;
DROP TABLE bam_file_datatype CASCADE CONSTRAINTS;
DROP TABLE bam_file  CASCADE CONSTRAINTS;
DROP SEQUENCE bam_file_seq;
DROP SEQUENCE biospecimen_bam_seq;
DROP SEQUENCE project_overview_seq;

CREATE SEQUENCE bam_file_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE biospecimen_bam_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE project_overview_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE bam_file_datatype (
    bam_datatype_id  	NUMBER(38,0)	NOT NULL,
    bam_datatype        VARCHAR2(50)    NOT NULL,
    molecule        	VARCHAR2(10)    NOT NULL,
    general_datatype    VARCHAR2(10)    NOT NULL,
    CONSTRAINT pk_bam_file_datatype_idx PRIMARY KEY (bam_datatype_id)
);

CREATE TABLE bam_file (
    bam_file_id        	NUMBER(38,0)    NOT NULL,
    bam_file_name	VARCHAR2(100)	NOT NULL,
    disease_id        	NUMBER(38,0)    NOT NULL,
    center_id        	NUMBER(38,0)    NOT NULL,
    bam_file_size	NUMBER(38,0)    NOT NULL,
    date_received  	DATE        	NOT NULL,
    bam_datatype_id 	NUMBER(38,0)    NOT NULL,
    CONSTRAINT pk_bam_file_idx PRIMARY KEY (bam_file_id)
);

ALTER TABLE bam_file ADD (
    CONSTRAINT fk_bam_file_center
    FOREIGN KEY (center_id)
    REFERENCES center(center_id),
    CONSTRAINT fk_bam_file_disease
    FOREIGN KEY (disease_id)
    REFERENCES disease(disease_id),
    CONSTRAINT fk_bam_file_datatype
    FOREIGN KEY (bam_datatype_id)
    REFERENCES bam_file_datatype(bam_datatype_id)
);


CREATE TABLE biospecimen_to_bam_file (
    biospecimen_bam_id	NUMBER(38,0)    NOT NULL,
    bam_file_id        	NUMBER(38,0)    NOT NULL,
    biospecimen_id	NUMBER(38,0)    NOT NULL,
    CONSTRAINT pk_biospecimen_bam_idx PRIMARY KEY (biospecimen_bam_id)
);

ALTER TABLE biospecimen_to_bam_file ADD(
    CONSTRAINT fk_biospecimen_bam_biospec
    FOREIGN KEY (biospecimen_id)
    REFERENCES biospecimen_barcode(biospecimen_id),
    CONSTRAINT fk_biospecimen_bam_bam
    FOREIGN KEY (bam_file_id)
    REFERENCES bam_file(bam_file_id)
);

insert into bam_file_datatype values (1,'454','DNA','Exome');
insert into bam_file_datatype values (2,'mirna','RNA','miRNA');
insert into bam_file_datatype values (3,'rnaseq','RNA','RNAseq');
insert into bam_file_datatype values (4,'whole','DNA','Genome');
insert into bam_file_datatype values (5,'capture','DNA','Exome');
insert into bam_file_datatype values (6,'SOLiD','DNA','Exome');
insert into bam_file_datatype values (7,'SOLiD_whole_exome_extensions','DNA','Exome');
insert into bam_file_datatype values (8,'IlluminaGA-DNASeq_capture','DNA','Exome');
insert into bam_file_datatype values (9,'IlluminaGA-DNASeq_exome','DNA','Exome');
insert into bam_file_datatype values (10,'IlluminaGA-DNASeq_whole','DNA','Genome');
insert into bam_file_datatype values (11,'IlluminaHiSeq-DNASeq_capture','DNA','Exome');
insert into bam_file_datatype values (12,'Unknown','Unknown','Unknown');
commit;


ALTER TABLE archive_info ADD (data_loaded_date DATE);

-- the following pl/sql to be run in dcccommon database to populate data_loaded_date
DECLARE
   CURSOR dCur IS
      SELECT disease_abbreviation
      FROM   disease
      ORDER BY disease_abbreviation;
   updateStatement VARCHAR2(4000);
BEGIN
    updateStatement := 'UPDATE dccCommon.archive_info a '||
       'SET data_loaded_date = '||
       ' (SELECT data_loaded_date FROM tcgaREPLACEDISEASE.archive_info ta '||
       ' WHERE  ta.archive_id = a.archive_id)';
    dbms_output.put_line('sql: '||updateStatement);
    FOR dRec IN dCur LOOP
       updateStatement := REPLACE(updateStatement,'REPLACEDISEASE',dRec.disease_abbreviation);
        dbms_output.put_line('sql: '||updateStatement);
        EXECUTE IMMEDIATE(updateStatement);
        commit;
    END LOOP;
END;
/
DROP TABLE projectOverview_case_counts;

CREATE TABLE projectOverview_case_counts (
    project_overview_id            NUMBER(38,0)    NOT NULL,
    disease_abbreviation        VARCHAR2(10)    NOT NULL,
    metholated_data_cases        NUMBER(38,0),
    microRna_data_cases            NUMBER(38,0),
    expArray_data_cases            NUMBER(38,0),
    expRnaSeq_data_cases        NUMBER(38,0),
    copyNumber_data_cases         NUMBER(38,0),
    gsc_mutation_data_cases     NUMBER(38,0),
    gsc_genome_cases            NUMBER(38,0),
    gsc_exome_cases                NUMBER(38,0),
    gsc_rnaseq_cases            NUMBER(38,0),
    gsc_microRna_cases            NUMBER(38,0),
    total_metholated_cases       NUMBER(38,0),
    total_microRna_cases        NUMBER(38,0),
    total_expArray_cases        NUMBER(38,0),
    total_expRnaSeq_cases        NUMBER(38,0),
    total_copyNumber_cases       NUMBER(38,0),
    total_mutation_cases        NUMBER(38,0),
    total_gsc_genome_cases      NUMBER(38,0),
    total_gsc_exome_cases         NUMBER(38,0),
    total_gsc_rnaseq_cases        NUMBER(38,0),
    total_gsc_microRna_cases    NUMBER(38,0),
    CONSTRAINT pk_overviewCaseCounts_idx PRIMARY KEY (project_overview_id)
);

/*
** This procedure will get a count of patients/cases for which we have the following data for each disease:
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
**
*/
CREATE OR REPLACE PROCEDURE build_projectOverview_counts
IS
  CURSOR dCur IS 
  SELECT disease_id, disease_abbreviation
  FROM disease 
  WHERE active=1
  order by disease_id;
  
  replaceString        	VARCHAR2(4000);
  sqlString        	VARCHAR2(4000);
  insertStatement  	VARCHAR2(300);
  insertStatement2  	VARCHAR2(300);
  mergeStatement  	VARCHAR2(50); 
  mergeDataSetResults	VARCHAR2(2000);
  mergeMafResults	VARCHAR2(2000);
BEGIN
	/*
	** Use MERGE statements so that an insert can be specified if there is no record for the disease,
	** and an update can be specified if there is a record for the disease, in the projectOverview_case_count
	** table.
	*/
        mergeStatement := 'MERGE INTO projectOverview_case_counts c USING ';
        mergeDataSetResults := 'ON (c.disease_abbreviation = v.disease) WHEN MATCHED THEN UPDATE SET '||
        ' c.copyNumber_data_cases=v.cnCases, c.expArray_data_cases= v.expCases, c.microRna_data_cases=v.mirnaCases, c.metholated_data_cases=v.methCases, c.expRnaSeq_data_cases=v.rnaSeqCases'||
        ' WHEN NOT MATCHED THEN ';

        mergeMafResults := 'ON (c.disease_abbreviation = v.disease) WHEN MATCHED THEN UPDATE SET '||
        ' c.gsc_mutation_data_cases = v.cases'||
        ' WHEN NOT MATCHED THEN ';
        
        insertStatement := 'INSERT (project_overview_id,disease_abbreviation,copyNumber_data_cases, expArray_data_cases,microRna_data_cases,metholated_data_cases,expRnaSeq_data_cases) '||
        ' VALUES (report_seq.nextval,v.disease,v.cnCases, v.expCases, v.mirnaCases, v.methCases, v.rnaSeqCases)';

        insertStatement2 := 'INSERT (project_overview_id,disease_abbreviation,gsc_mutation_data_cases) '||
        ' VALUES (report_seq.nextval,v.disease,v.cases)';

        /* 
        ** Get rolled up counts of cases (or patients) by rolled up data types, for which we have level 2 or 3
        ** data and put it into a reporting table in dccCommon. Get this information from every disease schema.
        ** It is necessary to build dynamic sql to replace the schema name during selects. During 
        */

        FOR diseaseRec IN dCur LOOP
	   /*
	   ** first get the case counts for level 2 and 3 cases based on ftp_display field in the data_set table, 
	   ** extrapolating the case or patient from the bestBarcode field in the hybridization_ref table, in each
	   ** disease schema.
	   */
           replaceString := '(SELECT ''REPLACE_DISEASE'' as disease,max(decode(data_type,''cn'' ,cases,0)) cnCases,max(decode(data_type,''exp'', cases,0)) expCases,'||
             'max(decode(data_type,''mirna'', cases,0))  mirnaCases,  max(decode(data_type,''methylation'', cases,0))  methCases,max(decode(data_type,''rnaseq'', cases,0))  rnaseqCases from '|| 
             ' (select count(distinct substr(h.bestbarcode,1,instr(h.bestbarcode,''-'',1,3)-1)) as cases, CASE  WHEN dt.ftp_display IN (''snp'',''cna'') THEN ''cn'' WHEN dt.ftp_display IN (''transcriptome'',''exon'') THEN ''exp'''||
             ' WHEN dt.ftp_display IN (''mirna'',''mirnaseq'') THEN ''mirna'' ELSE dt.ftp_display END  data_type '||
             ' from tcgaREPLACE_DISEASE.hybridization_ref h, tcgaREPLACE_DISEASE.hybrid_ref_data_set hd, tcgaREPLACE_DISEASE.data_set d, tcgaREPLACE_DISEASE.platform p,tcgaREPLACE_DISEASE.data_type_to_platform dp, tcgaREPLACE_DISEASE.data_type dt, tcgaREPLACE_DISEASE.data_visibility dv '||
             ' where h.hybridization_ref_id = hd.hybridization_ref_id and hd.data_set_id = d.data_set_id and d.platform_id = p.platform_id and p.platform_id = dp.platform_id '||
             ' and dp.data_type_id = dt.data_type_id and dt.data_type_id = dv.data_type_id and dv.level_number > 1 group by CASE  WHEN dt.ftp_display IN (''snp'',''cna'') THEN ''cn'' '|| 
             ' WHEN dt.ftp_display IN (''transcriptome'',''exon'') THEN ''exp'' WHEN dt.ftp_display IN (''mirna'',''mirnaseq'') THEN ''mirna'' ELSE dt.ftp_display END )) v '; 
           /*
           ** replace the string 'REPLACE_DISEASE' with the disease abbreviation from the dCur loop
           */
           sqlString := replace(replaceString,'REPLACE_DISEASE',diseaseRec.disease_abbreviation);
           sqlString := mergeStatement||sqlString||mergeDataSetResults||insertStatement;
           --dbms_output.put_line(sqlString);
           EXECUTE IMMEDIATE (sqlString);

           /*
           ** The gsc mutation case counts have to be calculated from the maf_ifno table in each disease schema, again, the
           ** case or patient extrapolated from the barcode field in this table
           */
           replaceString := '(SELECT ''REPLACE_DISEASE'' as disease,count(distinct(substr(m.tumor_sample_barcode,1,instr(m.tumor_sample_barcode,''-'',1,3)-1))) as cases FROM tcgaREPLACE_DISEASE.maf_info m) v ';
           /*
           ** replace the string 'REPLACE_DISEASE' with the disease abbreviation from the dCur loop
           */
           sqlString := replace(replaceString,'REPLACE_DISEASE',diseaseRec.disease_abbreviation);
           sqlString := mergeStatement||sqlString||mergeMafResults||insertStatement2;
              
            --dbms_output.put_line(sqlString);
            
            EXECUTE IMMEDIATE (sqlString);
             
            commit;
       
        END LOOP;
        
        /* 
        ** Finally get the bam file case totals. Since the bam files are all in the dccCommon database this does not
        ** need to be dynamic sql
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
             FROM bam_file b, bam_file_datatype bd,biospecimen_to_bam_file bf, biospecimen_breakdown_all bb, disease d 
             WHERE b.bam_datatype_id = bd.bam_datatype_id 
             AND   b.bam_file_id     = bf.bam_file_id 
             AND   bf.biospecimen_id = bb.biospecimen_id
             AND   b.disease_id      = d.disease_id 
             GROUP BY bd.general_datatype,disease_abbreviation)
         GROUP BY disease ) v
         ON (c.disease_abbreviation = v.disease) 
         WHEN MATCHED THEN UPDATE SET 
	     c.gsc_genome_cases   = v.genomeCases, 
	     c.gsc_exome_cases      = v.exomeCases,
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
        commit;

END;
/
CREATE OR REPLACE PACKAGE build_portal_action_summary
AS
    PROCEDURE get_summary_details;
    FUNCTION build_select(disease_abbreviation VARCHAR2)
    RETURN VARCHAR2;
    FUNCTION build_filter_select(disease_abbreviation VARCHAR2)
    RETURN VARCHAR2 ;
    FUNCTION build_update_statement(disease_abbreviation VARCHAR2)
    RETURN VARCHAR2;
END;
/
CREATE OR REPLACE PACKAGE BODY build_portal_action_summary
IS
    /*
    ** This package contains a stored procedure and functions to build dynamic sql to select and summarize data from the portal session log tables
    ** in each disease schema and put the summarized data into the portal_action_summary table in the dccCommon schema. This table is used
    ** for the Project Case Overview Dashboard.
    **
    ** Written by Shelley Alonso
    **
    ** Revision History
    **
    ** Shelley Alonso   01/12/2011  Change the varchar field from varchar(10) to varchar(20) in the xmltable function for 
    **                              portal_action_type 14 (batch filter applied) so that the 'Unavailable' batch is not truncated.
    ** Shelley Alonso   01/13/2011  Add regexp_replace to strip out any not digit characters, except commas, in the values returned
    **                              for portal_action_type 17 (dataType filter applied) to get around the DAM bug that allows garbage in
    **                              APPS-2639; also add header to this file.
    ** Shelley Alonso   01/25/2011  Add qualifier for disease select to only get active diseases 
    */

    PROCEDURE get_summary_details
    IS
        CURSOR dCur IS 
        SELECT disease_id, disease_abbreviation
        FROM disease 
        WHERE active=1
        order by disease_id;
        sqlString       VARCHAR2(4000);
        insertStatement VARCHAR2(300);
    BEGIN
        EXECUTE IMMEDIATE ('TRUNCATE TABLE portal_action_summary');
        insertStatement := 'INSERT INTO portal_action_summary (disease_abbreviation,monthyear,action_type_id,action,selection,action_total,user_count,totalUserCount) ';
        FOR diseaseRec IN dCur LOOP
       
            sqlString := insertStatement||build_select(diseaseRec.disease_abbreviation);
            --dbms_output.put_line(sqlString);
            EXECUTE IMMEDIATE sqlString;
            sqlString := insertStatement||build_filter_select(diseaseRec.disease_abbreviation);
            --dbms_output.put_line(sqlString);
            EXECUTE IMMEDIATE sqlString;
            sqlString := build_update_statement(diseaseRec.disease_abbreviation);
            --dbms_output.put_line(sqlString);
            EXECUTE IMMEDIATE sqlString;
            commit;                  
       
        END LOOP;
    END get_summary_details;
    /*
    ** this function will build a select query to summarize by month/year, the total archive downloads, the total users downloading archives, and the 
    ** total size of archives downloaded
    */        
    FUNCTION build_select(disease_abbreviation VARCHAR2)
    RETURN VARCHAR2
    IS
        selectStatement VARCHAR2(4000);
        selectReplace   VARCHAR2(4000);
    BEGIN
        selectReplace := 'SELECT ''REPLACE_DISEASE'',TO_CHAR(v.monthyr,''MM/YYYY'') monthyr, v.type_id,v.action,v.selection,v.actionTotal ,v.usercount, 0 FROM ('||
        ' SELECT TRUNC(action_time,''MM'') as monthyr,pt.portal_action_type_id as type_id,pt.name as action,'''' as selection,count(*) as actionTotal, count(distinct portal_session_id) as usercount FROM '||
        ' TCGAREPLACE_DISEASE.portal_session_action pa, TCGAREPLACE_DISEASE.portal_action_type pt '||
        ' WHERE pa.portal_action_type_id=pt.portal_action_type_id AND   pt.portal_action_type_id=34 GROUP BY TRUNC(action_time,''MM''),pt.portal_action_type_id,pt.name, '''' UNION '||
        ' SELECT TRUNC(action_time,''MM'') as monthyr,pt.portal_action_type_id as type_id,pt.name as action,'''' as selection,sum(value) as actionTotal, count(distinct portal_session_id) as usercount FROM '||
        ' TCGAREPLACE_DISEASE.portal_session_action pa, TCGAREPLACE_DISEASE.portal_action_type pt '||
        ' WHERE pa.portal_action_type_id=pt.portal_action_type_id AND pt.portal_action_type_id=36 GROUP BY TRUNC(action_time,''MM''),pt.portal_action_type_id,pt.name,'''') v';
 
        selectStatement := replace(selectReplace,'REPLACE_DISEASE',disease_abbreviation);
        
        RETURN selectStatement;
    END build_select;
    /*
    ** this function will build a select query to summarize by month/year, the total times certain filters were used and what they were,
    ** the total users selecting each one
    */    
    FUNCTION build_filter_select(disease_abbreviation VARCHAR2)
    RETURN VARCHAR2
    IS
        selectStatement VARCHAR2(4000);
        selectReplace    VARCHAR2(4000);
    BEGIN
      /*
      ** The portal session logger puts all selection values for filters in square brackets for some reason, so the first thing done is to strip off the brackets using
      ** a substring function on the portal_session_action.value field. If the user has selected more than one value for the selected filter (for instance 3 data types) 
      ** the portal session logger will log them as one string with the values comma-seperated. So we pass the value string, now called minus_bracket, to
      ** an xmltable function which returns us a table of the values that were comma delimited.
      */
      selectReplace := 'SELECT ''REPLACE_DISEASE'',TO_CHAR(monthyr,''MM/YYYY'') monthyr,type_id,action,selection,actionTotal ,usercount, 0 FROM ('||
      'SELECT v.monthyr, v.type_id,v.action, dt.name as selection,count(*) as actiontotal,count(distinct portal_session_id) as usercount FROM '||
      ' (select TRUNC(action_time,''MM'') as monthyr,pt.portal_action_type_id as type_id,pt.name as action,regexp_replace(pa.value, ''[^0-9,]+'', '''') as minus_bracket,portal_session_id FROM '||
      ' TCGAREPLACE_DISEASE.portal_session_action pa, TCGAREPLACE_DISEASE.portal_action_type pt '||
      ' WHERE pa.portal_action_type_id=pt.portal_action_type_id AND   pt.portal_action_type_id =17) v,'||
      'xmltable(''r/c'' passing xmltype(''<r><c>'' || replace(v.minus_bracket,'','',''</c><c>'') || ''</c></r>'') columns dtype_id number(10,0) path ''.'') x, TCGAREPLACE_DISEASE.data_type dt '||
      ' WHERE x.dtype_id=dt.data_type_id group by monthyr,v.type_id,action,dt.name UNION '||
      ' SELECT v.monthyr, v.type_id,v.action, decode(trim(x.protect_stat),''P'',''Protected'',''Public'') as selection,count(*) as actiontotal,count(distinct portal_session_id) as usercount FROM '||
      '(select TRUNC(action_time,''MM'') as monthyr,pt.portal_action_type_id as type_id,pt.name as action,replace(substr(pa.value,2,length(pa.value)-2),'')'','''') as minus_bracket,portal_session_id FROM '||
      ' TCGAREPLACE_DISEASE.portal_session_action pa, TCGAREPLACE_DISEASE.portal_action_type pt '||
      ' WHERE pa.portal_action_type_id=pt.portal_action_type_id AND   pt.portal_action_type_id =18) v,xmltable(''r/c'' passing xmltype(''<r><c>'' || replace(v.minus_bracket,'','',''</c><c>'') || ''</c></r>'')'||
      ' columns protect_stat varchar2(20) path ''.'') x group by monthyr,v.type_id,action,trim(x.protect_stat) UNION '||
      ' SELECT v.monthyr, v.type_id,v.action, trim(x.batchnbr) as selection,count(*) as actiontotal,count(distinct portal_session_id) as usercount FROM '||
      ' (select TRUNC(action_time,''MM'') as monthyr,pt.portal_action_type_id as type_id,pt.name as action,replace(substr(pa.value,2,length(pa.value)-2),'')'','''') as minus_bracket,portal_session_id FROM '||
      ' TCGAREPLACE_DISEASE.portal_session_action pa, TCGAREPLACE_DISEASE.portal_action_type pt '||
      ' WHERE pa.portal_action_type_id=pt.portal_action_type_id AND   pt.portal_action_type_id =14) v, xmltable(''r/c'' passing xmltype(''<r><c>'' || replace(v.minus_bracket,'','',''</c><c>'') || ''</c></r>'')'||
      ' columns batchnbr varchar2(20) path ''.'') x group by monthyr,v.type_id,action,trim(x.batchnbr) UNION '||
      ' SELECT v.monthyr, v.type_id,v.action, ''Level ''||trim(x.levelnum) as selection,count(*) as actiontotal,count(distinct portal_session_id) as usercount FROM '||
      ' (select TRUNC(action_time,''MM'') as monthyr,pt.portal_action_type_id as type_id,pt.name as action,replace(substr(pa.value,2,length(pa.value)-2),'')'','''') as minus_bracket,portal_session_id FROM '||
      ' TCGAREPLACE_DISEASE.portal_session_action pa, TCGAREPLACE_DISEASE.portal_action_type pt '||
      ' WHERE pa.portal_action_type_id=pt.portal_action_type_id AND   pt.portal_action_type_id =16) v, xmltable(''r/c'' passing xmltype(''<r><c>'' || replace(v.minus_bracket,'','',''</c><c>'') || ''</c></r>'')'||
      ' columns levelnum char(2) path ''.'') x group by monthyr,v.type_id,action,''Level ''||trim(x.levelnum))';
      
      selectStatement := REPLACE(selectReplace,'REPLACE_DISEASE',disease_abbreviation);
      RETURN selectStatement; 
    END build_filter_select;
    /*
    ** this function will build a query to summarize by month/year the total number of sessions, and update the totalusercount in the
    ** portal_action_summary table for the disease
    */     
    FUNCTION build_update_statement(disease_abbreviation VARCHAR2)
    RETURN VARCHAR2
    IS
        dSchema         VARCHAR2(10);
        updateStatement VARCHAR2(4000);
     BEGIN
      dSchema := 'TCGA'||disease_abbreviation;
      updateStatement :=  'merge into portal_action_summary p USING (select TO_CHAR(created_on,''MM/YYYY'') monthyr, count(portal_session_id) as totalUserCount , '||
      ''''||disease_abbreviation||''''||' as disease_abbreviation from '||dschema||'.portal_session GROUP BY TO_CHAR(created_on,''MM/YYYY'')) sub'||
      ' on (p.monthyear=sub.monthyr and p.disease_abbreviation=sub.disease_abbreviation) when matched then update set totalusercount = sub.totalusercount';      
      RETURN updateStatement; 
    END build_update_statement;
END;
/

grant all on dcccommon.bam_file_datatype to commonmaint;
grant all on dcccommon.biospecimen_to_bam_file to commonmaint;
grant all on dcccommon.bam_file to commonmaint;
grant all on projectOverview_case_counts to commonmaint;
grant select on dcccommon.bam_file_datatype to commonread,readonly;
grant select on dcccommon.biospecimen_to_bam_file to commonread,readonly;
grant select on dcccommon.bam_file to commonread,readonly;
grant select on projectOverview_case_counts to commonread,readonly;
Grant select on dcccommon.bam_file_seq to commonmaint; 
Grant select on dcccommon.biospecimen_bam_seq to commonmaint; 
Grant select on dcccommon.project_overview_seq to commonmaint; 
grant execute on build_projectOverview_counts to commonmaint;
grant execute on build_portal_action_summary to commonmaint;
