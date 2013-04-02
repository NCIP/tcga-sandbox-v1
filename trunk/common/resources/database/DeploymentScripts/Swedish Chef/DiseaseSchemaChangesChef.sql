grant select on patient to dcccommon with grant option;
grant select on sample to dcccommon with grant option;
grant select on radiation to dcccommon with grant option;
grant select on analyte to dcccommon with grant option;
grant select on aliquot to dcccommon with grant option;
grant select on drug_intgen to dcccommon with grant option;
grant select on protocol to dcccommon with grant option;
grant select on portion to dcccommon with grant option;
grant select on examination to dcccommon with grant option;
grant select on surgery to dcccommon with grant option;
grant select on slide to dcccommon with grant option;
grant select on slide_archive to dcccommon with grant option;
grant select on surgery_archive to dcccommon with grant option;
grant select on examination_archive to dcccommon with grant option;
grant select on drug_intgen_archive to dcccommon with grant option;
grant select on radiation_archive to dcccommon with grant option;
grant select on portion_archive to dcccommon with grant option;
grant select on aliquot_archive to dcccommon with grant option;
grant select on analyte_archive to dcccommon with grant option;
grant select on sample_archive to dcccommon with grant option;
grant select on patient_archive to dcccommon with grant option;
grant select on protocol_archive to dcccommon with grant option;
grant select on archive_info to dcccommon with grant option;

DROP MATERIALIZED VIEW HYBRIDIZATION_VALUE_MV;
DROP TABLE hybridization_value;
DROP TABLE hybridization_data_group;
DROP TABLE probe;
DROP TABLE tmphybref;
DROP TABLE tmpdataset;
DROP SEQUENCE HDGROUP_HYBRIDIZATION_SEQ;
DROP SEQUENCE HYB_VALUE_ID_SEQ;
DROP SEQUENCE PROBE_ID_SEQ;

alter table clinical_xsd_element add expected_element CHAR(1)   DEFAULT 'Y' NOT NULL;
alter table maf_info add sequence_source varchar2(100);

CREATE GLOBAL TEMPORARY TABLE tmpbarcode 
( barcode varchar2(100)) 
ON COMMIT DELETE ROWS ;
       
GRANT SELECT ON tmpbarcode to readonly;








