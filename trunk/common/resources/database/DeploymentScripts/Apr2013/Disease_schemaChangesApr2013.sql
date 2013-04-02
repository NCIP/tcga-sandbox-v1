ALTER TABLE biospecimen_to_file DROP CONSTRAINT fk_biospecimen_file_file;
ALTER TABLE biospecimen_to_file ADD 
  (CONSTRAINT fk_biospecimen_file_file 
   FOREIGN KEY (file_id)
   REFERENCES file_info(file_id)
   ON DELETE CASCADE
);

ALTER TABLE file_to_archive DROP CONSTRAINT fk_filetoarchive_fileid;
ALTER TABLE file_to_archive ADD (
   CONSTRAINT fk_filetoarchive_fileid 
   FOREIGN KEY (file_id)
   REFERENCES file_info (file_id)
   ON DELETE CASCADE
);

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