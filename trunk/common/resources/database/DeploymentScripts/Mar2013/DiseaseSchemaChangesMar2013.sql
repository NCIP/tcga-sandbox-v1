UPDATE data_type set name = 'CNV (Array)' where data_type_id = 4;
UPDATE data_type set name = 'CNV (SNP)' where data_type_id = 1;
UPDATE data_type set name = 'CNV (Low Pass DNASeq)' where data_type_id = 40;
update clinical_table set element_node_name = 'follow_up' where table_name = 'FOLLOW_UP';
COMMIT;
