-- these alter statements to be run for dccCommon schema and all disease schemas
ALTER TABLE data_set ADD (archive_id NUMBER(38));
ALTER TABLE archive_info ADD (data_loaded_date DATE);
ALTER TABLE data_set_file ADD (file_id NUMBER(38,0));


-- The following 3 update statements to be run in each disease schema
/*
** update new data_loaded_date
*/
DECLARE 
 CURSOR aCur IS
 SELECT substr(d.source_file_name,1,instr(d.source_file_name,'/')-1) as archive,
                     MAX(df.load_end_date) as date_loaded
 FROM data_set d, data_set_file df
   WHERE d.data_set_id = df.data_set_id
   GROUP BY substr(d.source_file_name,1,instr(d.source_file_name,'/')-1)
   ORDER BY 1;
 begin
    FOR aRec in aCur LOOP
        dbms_output.put_line('archive: '||aRec.archive||'  date: '||aRec.date_loaded);
        UPDATE archive_info a
     SET data_loaded_date = aRec.date_loaded
     WHERE a.archive_name = aRec.archive;
     END LOOP;
     commit;
 end;
/
update archive_info set data_loaded_date = date_added where data_loaded_date is  null;
commit;
/*
** set the archive_id for data_set
*/
UPDATE data_set ds
    SET archive_id =
        (SELECT a.archive_id
         FROM   archive_info a
         WHERE  a.archive_name = substr(ds.source_file_name,1,instr(ds.source_file_name,'/')-1) );
commit;


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
commit;

ALTER TABLE data_set add constraint fk_data_set_archive foreign key (archive_id) 
references archive_info(archive_id);
ALTER TABLE data_set_file add constraint fk_data_set_file_file foreign key (file_id) 
references file_info(file_id);

GRANT SELECT ON archive_info to dcccommon;
grant select on platform to dcccommon;
grant select on hybridization_ref  to dcccommon;
grant select on hybrid_ref_data_set to dcccommon;
grant select on data_set  to dcccommon;
grant select on platform  to dcccommon;
grant select on data_type_to_platform  to dcccommon;
grant select on data_type  to dcccommon;
grant select on data_visibility to dcccommon;
grant select on maf_info to dcccommon;
grant select on portal_session_action to dcccommon;
grant select on portal_action_type to dcccommon;
grant select on portal_session to dcccommon;