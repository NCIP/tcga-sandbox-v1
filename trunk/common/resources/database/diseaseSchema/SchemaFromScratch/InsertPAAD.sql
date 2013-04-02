set echo on;
-- for ov database only 
DROP VIEW AVG_COL_LEN;

CREATE OR REPLACE FORCE VIEW AVG_COL_LEN
(
   TABLE_NAME,
   COLUMN_NAME,
   AVG_COL_LEN,
   LAST_ANALYZED
)
AS
   SELECT   table_name,
            column_name,
            avg_col_len,
            last_analyzed
     FROM   all_tab_columns
    WHERE   owner = 'TCGAPAAD';

 
Insert into disease (disease_id,disease_abbreviation,disease_name, active, dam_default,workbench_track)
Values (27,'PAAD','Pancreatic adenocarcinoma ',0,1,207);
commit;
