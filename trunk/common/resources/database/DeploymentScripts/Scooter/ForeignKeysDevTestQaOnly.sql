ALTER TABLE shipped_biospecimen_file ADD (
    CONSTRAINT fk_ship_biospecfile_biospec
    FOREIGN KEY (shipped_biospecimen_id)
    REFERENCES shipped_biospecimen(shipped_biospecimen_id),
    CONSTRAINT fk_shipped_biospec_file
    FOREIGN KEY (file_id)
    REFERENCES file_info(file_id)
);
ALTER TABLE shipped_biospec_bcr_archive ADD (
    CONSTRAINT fk_shipped_biospec_biospec
    FOREIGN KEY (shipped_biospecimen_id)
    REFERENCES shipped_biospecimen(shipped_biospecimen_id),
    CONSTRAINT fk_shipped_biospec_archive
    FOREIGN KEY (archive_id)
    REFERENCES archive_info(archive_id)
);