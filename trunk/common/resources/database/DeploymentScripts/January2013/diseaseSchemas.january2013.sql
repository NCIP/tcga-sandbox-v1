INSERT INTO data_type(data_type_id, name, center_type_code, ftp_display, available, sort_order, require_compression)
values(41, 'Protected Mutations', 'GSC', 'mutations_protected', 1, 41, 1);

INSERT INTO platform(platform_id, platform_name,platform_display_name, platform_alias, center_type_code, sort_order, available, base_data_type_id)
values(59, 'IlluminaHiSeq_DNASeq_Cont', 'Illumina HiSeq 2000 DNA Sequencing - Controlled', 'IlluminaHiSeq_DNASeq_Cont', 'GSC', 59, 1, 41);

INSERT INTO platform(platform_id, platform_name,platform_display_name, platform_alias, center_type_code, sort_order, available, base_data_type_id)
values(60, 'IlluminaGA_DNASeq_Cont', 'Illumina Genome Analyzer DNA Sequencing - Controlled', 'IlluminaGA_DNASeq_Cont', 'GSC', 60, 1, 41);

INSERT INTO platform(platform_id, platform_name,platform_display_name, platform_alias, center_type_code, sort_order, available, base_data_type_id)
values(61, 'SOLiD_DNASeq_Cont', 'ABI SOLiD DNA System Sequencing - Controlled', 'SOLiD_DNASeq_Cont', 'GSC', 61, 1, 41);

INSERT INTO data_visibility (data_visibility_id, data_type_id, visibility_id, level_number)
values (228,41,1,0);
INSERT INTO data_visibility (data_visibility_id,data_type_id,visibility_id,level_number)
values (229,41,2,1);
INSERT INTO data_visibility (data_visibility_id,data_type_id,visibility_id,level_number)
values (230,41,2,2);
INSERT INTO data_visibility (data_visibility_id,data_type_id,visibility_id,level_number)
values (231,41,2,3);

commit;