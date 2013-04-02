CREATE TABLE tissue_to_tumor (
	tumor_id INTEGER NOT NULL,
	tissue_id INTEGER NOT NULL,
    CONSTRAINT tissue_tumor_pk_idx PRIMARY KEY (tumor_id, tissue_id),
    CONSTRAINT fk_tissue_tumor_tumor FOREIGN KEY (tumor_id)
    REFERENCES tumor_info(id),
    CONSTRAINT fk_tissue_tumor_tissue FOREIGN KEY (tissue_id)
    REFERENCES tissue_info(id));
    