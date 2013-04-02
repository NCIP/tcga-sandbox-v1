-- these alter statements to be run for dccCommon schema and all disease schemas
ALTER TABLE data_set_file ADD (file_id NUMBER(38));
ALTER TABLE data_set ADD (archive_id NUMBER(38));
ALTER TABLE archive_info ADD (data_loaded_date DATE);

-- The following 3 update statements to be run in each diseae schema
/*
** update new data_loaded_date
*/
 UPDATE archive_info a
     SET data_loaded_date = 
     (SELECT v.date_loaded
      FROM
          (SELECT d.data_set_id, substr(d.source_file_name,1,instr(d.source_file_name,'/')-1) as archive,
                     MAX(df.load_end_date) as date_loaded
            FROM data_set d, data_set_file df
            WHERE d.data_set_id = df.data_set_id
            GROUP BY d.data_set_id,substr(d.source_file_name,1,instr(d.source_file_name,'/')-1) ) v
      WHERE v.archive = a.archive_name);
 

/*
** set the archive_id for data_set
*/
UPDATE data_set ds
    SET archive_id =
        (SELECT a.archive_id
         FROM   archive_info a
         WHERE  a.archive_name = substr(ds.source_file_name,1,instr(ds.source_file_name,'/')-1) );

/*  
** set file_id for fk in data_set_file
*/
UPDATE data_set_file dsf
    SET (dsf.file_id) =
        (SELECT v.file_id
          FROM 
            (WITH             
             nomatch AS
            (SELECT data_set_id,data_set_file_id,
             REVERSE(SUBSTR(REVERSE(file_name),1, INSTR(REVERSE(file_name),'/', 1,1)-1)) as file_name
             FROM  data_set_file 
             WHERE file_name NOT IN (SELECT file_name FROM file_info))
            SELECT ds.data_set_id,df.data_set_file_id,df.file_name, f.file_id, fa.archive_id
            FROM   data_set_file df, file_info f, file_to_archive fa, data_set ds
            WHERE  df.file_name = f.file_name
            AND    f.file_id = fa.file_id
            AND    fa.archive_id = ds.archive_id
            AND    ds.data_set_id = df.data_set_id
            UNION
            SELECT nomatch.data_set_id,nomatch.data_set_file_id,nomatch.file_name,f.file_id, fa.archive_id
            FROM nomatch, data_set ds, file_info f, file_to_archive fa
            WHERE  nomatch.file_name = f.file_name
            AND    f.file_id = fa.file_id
            AND    fa.archive_id  = ds.archive_id
            AND    ds.data_set_id = nomatch.data_set_id) v
        WHERE  v.data_set_id = dsf.data_set_id
        AND    v.data_set_file_id = dsf.data_set_file_id);  

-- the following pl/sql to be run in dcccommon database
DECLARE
   CURSOR dCur IS
      SELECT disease_abbreviation
      FROM   disease
      ORDER BY disease_abbreviation;
   updateStatement VARCHAR2(4000);
BEGIN
	updateStatement := 'UPDATE dccCommon.archive_info a '||
	   'SET data_loaded_date = '||
	   ' SELECT data_loaded_date FROM tcgaREPLACEDISEASE.archive_info ta '||
	   ' WHERE  ta.archive_id = a.archive_id)';
	FOR dRec IN dCur LOOP
	   updateStatement := REPLACE(updateStatement,'REPLACEDISEASE',dRec.disease_abbreviation);
	   EXECUTE IMMEDIATE(updateStatement);
	END LOOP;
END;