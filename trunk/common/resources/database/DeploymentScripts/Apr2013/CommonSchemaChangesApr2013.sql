
ALTER TABLE biospecimen_to_file DROP CONSTRAINT fk_biospecimen_file_file; 
ALTER TABLE biospecimen_to_file ADD CONSTRAINT fk_biospecimen_file_file 
  FOREIGN KEY (file_id)
  REFERENCES file_info (file_id)
  ON DELETE CASCADE;

DROP INDEX biospec_ncbitrace_file_idx;
CREATE INDEX biospec_ncbitrace_file_idx ON biospecimen_ncbi_trace (file_id);

ALTER TABLE biospecimen_ncbi_trace DROP CONSTRAINT fk_biospec_ncbi_trace_file ;
ALTER TABLE biospecimen_ncbi_trace ADD CONSTRAINT fk_biospec_ncbi_trace_file 
    FOREIGN KEY (file_id)
    REFERENCES file_info (file_id)
    ON DELETE CASCADE;

ALTER TABLE file_to_archive DROP CONSTRAINT fk_filetoarchive_fileid;
ALTER TABLE file_to_archive ADD CONSTRAINT fk_filetoarchive_fileid 
    FOREIGN KEY (file_id)
    REFERENCES file_info (file_id)
    ON DELETE CASCADE;

ALTER TABLE file_to_collection DROP CONSTRAINT fk_file_collection_file; 
ALTER TABLE file_to_collection ADD CONSTRAINT fk_file_collection_file 
    FOREIGN KEY (file_id)
    REFERENCES file_info (file_id)
    ON DELETE CASCADE;


ALTER TABLE participant_uuid_file DROP CONSTRAINT fk_uuid_file;
ALTER TABLE participant_uuid_file ADD (
    CONSTRAINT fk_partic_uuid_fileid 
    FOREIGN KEY (file_id)
    REFERENCES file_info (file_id)
    ON DELETE CASCADE
);

DROP INDEX ship_biospec_ncbifile_file_idx;
CREATE INDEX ship_biospec_ncbifile_file_idx ON shipped_biospec_ncbi_trace (file_id);

ALTER TABLE shipped_biospec_ncbi_trace DROP CONSTRAINT fk_shipp_biospec_ncbi_file; 
ALTER TABLE shipped_biospec_ncbi_trace ADD CONSTRAINT fk_shipp_biospec_ncbi_file 
    FOREIGN KEY (file_id)
    REFERENCES file_info (file_id)
    ON DELETE CASCADE;


ALTER TABLE shipped_biospecimen_file DROP CONSTRAINT fk_shipped_biospec_file;
   ALTER TABLE shipped_biospecimen_file ADD (
   CONSTRAINT fk_shipped_biospec_file
   FOREIGN KEY (file_id)
   REFERENCES file_info (file_id)
   ON DELETE CASCADE
);

alter table center add requires_magetab number(1) default 0;

update center set requires_magetab=1 where center_type_code='CGCC';
commit;