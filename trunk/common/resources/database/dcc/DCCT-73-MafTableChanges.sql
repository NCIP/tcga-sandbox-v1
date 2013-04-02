alter table maf_info alter column sequencing_phase TYPE character varying(50) ;
alter table maf_info add column score character varying(50) ;
alter table maf_info add column bam_file character varying(255) ;
alter table maf_info add column sequencer character varying(100) ;

