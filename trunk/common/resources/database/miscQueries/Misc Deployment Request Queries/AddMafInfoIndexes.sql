CREATE INDEX maf_info_file_idx ON maf_info(file_id, tumor_sample_barcode);

CREATE INDEX maf_info_center_barcode_idx ON maf_info(center_id,tumor_sample_barcode);



