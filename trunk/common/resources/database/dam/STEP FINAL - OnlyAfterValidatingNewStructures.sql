WHENEVER SQLERROR EXIT ;
/*
** drop old tables and rename the new
*/
DROP TABLE hybridization_value_old
/
DROP TABLE hybridization_ref_old
/
DROP TABLE composite_element_constant
/
DROP TABLE composite_element
/
-- drop temporary intermediate and mapping tables
DROP TABLE composite_element_probe_map
/
DROP TABLE hyb_ref_map
/
DROP TABLE hybridization_value_new
/
DROP TABLE cde_lookup
/
DROP TABLE center_disease
/
DROP TABLE center_platform
/
DROP SEQUENCE COMPOSITE_ELE_COMPOSITE_EL_SEQ 
/
DROP FUNCTION DAM_COLUMN_CURSOR3
/
DROP FUNCTION DAM_COLUMN_CURSOR4
/
DROP FUNCTION GET_COMP_ELE_ID
/
DROP TRIGGER SET_L4_DATA_SET_SAMPLE_ID
/
