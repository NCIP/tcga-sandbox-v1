ALTER TABLE batch_number_assignment 
ADD CONSTRAINT pk_batch_number_assignment_idx PRIMARY KEY (batch_id);
ALTER TABLE sample_type add (is_tumor number(1,0) );
update sample_type set is_tumor=1 where sample_type_code like '0%';
update sample_type set is_tumor=0 where sample_type_Code not like '0%';
commit;

DROP TABLE projectOverview_case_counts;

CREATE TABLE projectOverview_case_counts (
	overdash_cgcc_id		NUMBER(38,0)	NOT NULL,
	disease_abbreviation		VARCHAR2(10)	NOT NULL,
	metholated_data_cases		NUMBER(38,0),
	microRna_data_cases		NUMBER(38,0),
	expArray_data_cases		NUMBER(38,0),
	expRnaSeq_data_cases		NUMBER(38,0),
	copyNumber_data_cases		NUMBER(38,0),
	gsc_mutation_data_cases 	NUMBER(38,0),
	total_metholated_cases		NUMBER(38,0),
	total_microRna_cases		NUMBER(38,0),
	total_expArray_cases		NUMBER(38,0),
	total_expRnaSeq_cases		NUMBER(38,0),
	total_copyNumber_cases		NUMBER(38,0),
	total_mutation_cases		NUMBER(38,0),
	CONSTRAINT pk_overdash_cgcc_idx PRIMARY KEY (overdash_cgcc_id)
);

CREATE OR REPLACE PROCEDURE build_projectOverview_counts
IS
  CURSOR dCur IS 
  SELECT disease_id, disease_abbreviation
  FROM disease order by disease_id;
  
  replaceString		VARCHAR2(4000);
  sqlString           	VARCHAR2(4000);
  insertStatement     	VARCHAR2(300);
  updateStatement    	VARCHAR2(4000);
  mergePart1            VARCHAR2(50); 
  mergePart2        	VARCHAR2(2000);
BEGIN
        /* 
        ** get rolled up counts of cases (or patients) by rolled up data types, for which we have level 2 or 3
        ** data and put it into a reporting table in dccCommon. Get this information from every disease schema.
        */
        mergePart1 := 'MERGE INTO projectOverview_case_counts c USING ';
        mergePart2 := 'ON (c.disease_abbreviation = v.disease) WHEN MATCHED THEN UPDATE SET '||
        ' c.copyNumber_data_cases=v.cnCases, c.expArray_data_cases= v.expCases, c.microRna_data_cases=v.mirnaCases, c.metholated_data_cases=v.methCases, c.expRnaSeq_data_cases=v.rnaSeqCases'||
        ' WHEN NOT MATCHED THEN ';

        insertStatement := 'INSERT (overdash_cgcc_id,disease_abbreviation,copyNumber_data_cases, expArray_data_cases,microRna_data_cases,metholated_data_cases,expRnaSeq_data_cases) '||
        ' VALUES (report_seq.nextval,v.disease,v.cnCases, v.expCases, v.mirnaCases, v.methCases, v.rnaSeqCases)';
        FOR diseaseRec IN dCur LOOP
       
            replaceString := '(SELECT ''REPLACE_DISEASE'' as disease,max(decode(data_type,''cn'' ,cases,0)) cnCases,max(decode(data_type,''exp'', cases,0)) expCases,'||
         'max(decode(data_type,''mirna'', cases,0))  mirnaCases,  max(decode(data_type,''methylation'', cases,0))  methCases,max(decode(data_type,''rnaseq'', cases,0))  rnaseqCases from '|| 
         ' (select count(distinct substr(h.bestbarcode,1,instr(h.bestbarcode,''-'',1,3)-1)) as cases, CASE  WHEN dt.ftp_display IN (''snp'',''cna'') THEN ''cn'' WHEN dt.ftp_display IN (''transcriptome'',''exon'') THEN ''exp'''||
             ' WHEN dt.ftp_display IN (''mirna'',''mirnaseq'') THEN ''mirna'' ELSE dt.ftp_display END  data_type '||
             ' from tcgaREPLACE_DISEASE.hybridization_ref h, tcgaREPLACE_DISEASE.hybrid_ref_data_set hd, tcgaREPLACE_DISEASE.data_set d, tcgaREPLACE_DISEASE.platform p,tcgaREPLACE_DISEASE.data_type_to_platform dp, tcgaREPLACE_DISEASE.data_type dt, tcgaREPLACE_DISEASE.data_visibility dv '||
             ' where h.hybridization_ref_id = hd.hybridization_ref_id and hd.data_set_id = d.data_set_id and d.platform_id = p.platform_id and p.platform_id = dp.platform_id '||
             ' and dp.data_type_id = dt.data_type_id and dt.data_type_id = dv.data_type_id and dv.level_number > 1 group by CASE  WHEN dt.ftp_display IN (''snp'',''cna'') THEN ''cn'' '|| 
             ' WHEN dt.ftp_display IN (''transcriptome'',''exon'') THEN ''exp'' WHEN dt.ftp_display IN (''mirna'',''mirnaseq'') THEN ''mirna'' ELSE dt.ftp_display END )) v '; 
            sqlString := replace(replaceString,'REPLACE_DISEASE',diseaseRec.disease_abbreviation);
            sqlString := mergePart1||sqlString||mergePart2||insertStatement;
            EXECUTE IMMEDIATE (sqlString);
            --dbms_output.put_line(sqlString);
                        
            updateStatement := 'UPDATE projectOverview_case_counts SET gsc_mutation_data_cases = '||
            '(SELECT count(distinct(substr(m.tumor_sample_barcode,1,instr(m.tumor_sample_barcode,''-'',1,3)-1))) FROM tcgaREPLACE_DISEASE.maf_info m)'||
            ' WHERE disease_abbreviation = ''REPLACE_DISEASE''';
            updateStatement := replace(updateStatement,'REPLACE_DISEASE',diseaseRec.disease_abbreviation);
            --dbms_output.put_line(updateStatement);
            
             EXECUTE IMMEDIATE (updateStatement);
             
             commit;
       
        END LOOP;
END;
/
