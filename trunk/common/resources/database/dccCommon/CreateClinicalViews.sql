CREATE OR REPLACE FORCE VIEW ALIQUOT_V AS
SELECT   DISTINCT
              'BLCA' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode,INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgablca.aliquot gal,
              tcgablca.aliquot_archive aa,
              tcgablca.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'BRCA' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgabrca.aliquot gal,
              tcgabrca.aliquot_archive aa,
              tcgabrca.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'CESC' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgacesc.aliquot gal,
              tcgacesc.aliquot_archive aa,
              tcgacesc.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'COAD' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgacoad.aliquot gal,
              tcgacoad.aliquot_archive aa,
              tcgacoad.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'DLBC' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgadlbc.aliquot gal,
              tcgadlbc.aliquot_archive aa,
              tcgadlbc.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'ESCA' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaesca.aliquot gal,
              tcgaesca.aliquot_archive aa,
              tcgaesca.archive_info ai
      WHERE   gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'GBM' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgagbm.aliquot gal,
              tcgagbm.aliquot_archive aa,
              tcgagbm.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'HNSC' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1)  AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4)  AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgahnsc.aliquot gal,
              tcgahnsc.aliquot_archive aa,
              tcgahnsc.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'KIRC' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgakirc.aliquot gal,
              tcgakirc.aliquot_archive aa,
              tcgakirc.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'KIRP' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4)  AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgakirp.aliquot gal,
              tcgakirp.aliquot_archive aa,
              tcgakirp.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LAML' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalaml.aliquot gal,
              tcgalaml.aliquot_archive aa,
              tcgalaml.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LCLL' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalcll.aliquot gal,
              tcgalcll.aliquot_archive aa,
              tcgalcll.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LGG' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalgg.aliquot gal,
              tcgalgg.aliquot_archive aa,
              tcgalgg.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LIHC' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalihc.aliquot gal,
              tcgalihc.aliquot_archive aa,
              tcgalihc.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LNNH' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalnnh.aliquot gal,
              tcgalnnh.aliquot_archive aa,
              tcgalnnh.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LUAD' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaluad.aliquot gal,
              tcgaluad.aliquot_archive aa,
              tcgaluad.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LUSC' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalusc.aliquot gal,
              tcgalusc.aliquot_archive aa,
              tcgalusc.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'OV' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaov.aliquot gal,
              tcgaov.aliquot_archive aa,
              tcgaov.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'PAAD' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgapaad.aliquot gal,
              tcgapaad.aliquot_archive aa,
              tcgapaad.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'PRAD' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1)  AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaprad.aliquot gal,
              tcgaprad.aliquot_archive aa,
              tcgaprad.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'READ' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaread.aliquot gal,
              tcgaread.aliquot_archive aa,
              tcgaread.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'SALD' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4)  AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgasald.aliquot gal,
              tcgasald.aliquot_archive aa,
              tcgasald.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'SKCM' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaskcm.aliquot gal,
              tcgaskcm.aliquot_archive aa,
              tcgaskcm.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'STAD' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgastad.aliquot gal,
              tcgastad.aliquot_archive aa,
              tcgastad.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'THCA' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4) AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgathca.aliquot gal,
              tcgathca.aliquot_archive aa,
              tcgathca.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'UCEC' AS disease_abbreviation,
              gal.aliquot_id,
              gal.analyte_id,
              gal.uuid,
              gal.aliquot_barcode AS barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1) AS bcr_center_id,
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4)  AS plate,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaucec.aliquot gal,
              tcgaucec.aliquot_archive aa,
              tcgaucec.archive_info ai
      WHERE       gal.aliquot_id = aa.aliquot_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gal.uuid,
              gal.aliquot_id,
              gal.analyte_id,
              gal.aliquot_barcode,
              SUBSTR (gal.aliquot_barcode, LENGTH (gal.aliquot_barcode) - 1),
              SUBSTR (gal.aliquot_barcode, INSTR (gal.aliquot_barcode,'-',1,5) + 1, 4),
              ai.serial_index,
              ai.center_id;

CREATE OR REPLACE FORCE VIEW ANALYTE_V
(
   DISEASE_ABBREVIATION,
   UUID,
   BARCODE,
   ANALYTE_CODE,
   ANALYTE_ID,
   PORTION_ID,
   DATE_UPDATED,
   DATE_ADDED,
   BATCH,
   CENTER_ID
)
AS
     SELECT   DISTINCT
              'BLCA' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgablca.analyte gan,
              tcgablca.analyte_archive aa,
              tcgablca.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'BRCA' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgabrca.analyte gan,
              tcgabrca.analyte_archive aa,
              tcgabrca.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'CESC' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgacesc.analyte gan,
              tcgacesc.analyte_archive aa,
              tcgacesc.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'COAD' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgacoad.analyte gan,
              tcgacoad.analyte_archive aa,
              tcgacoad.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'DLBC' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgadlbc.analyte gan,
              tcgadlbc.analyte_archive aa,
              tcgadlbc.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'ESCA' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaesca.analyte gan,
              tcgaesca.analyte_archive aa,
              tcgaesca.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'GBM' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgagbm.analyte gan,
              tcgagbm.analyte_archive aa,
              tcgagbm.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'HNSC' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgahnsc.analyte gan,
              tcgahnsc.analyte_archive aa,
              tcgahnsc.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'KIRC' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgakirc.analyte gan,
              tcgakirc.analyte_archive aa,
              tcgakirc.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'KIRP' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgakirp.analyte gan,
              tcgakirp.analyte_archive aa,
              tcgakirp.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LAML' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalaml.analyte gan,
              tcgalaml.analyte_archive aa,
              tcgalaml.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LCLL' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalcll.analyte gan,
              tcgalcll.analyte_archive aa,
              tcgalcll.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LGG' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalgg.analyte gan,
              tcgalgg.analyte_archive aa,
              tcgalgg.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LIHC' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalihc.analyte gan,
              tcgalihc.analyte_archive aa,
              tcgalihc.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LNNH' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalnnh.analyte gan,
              tcgalnnh.analyte_archive aa,
              tcgalnnh.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LUAD' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaluad.analyte gan,
              tcgaluad.analyte_archive aa,
              tcgaluad.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LUSC' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalusc.analyte gan,
              tcgalusc.analyte_archive aa,
              tcgalusc.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'OV' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaov.analyte gan,
              tcgaov.analyte_archive aa,
              tcgaov.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'PAAD' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgapaad.analyte gan,
              tcgapaad.analyte_archive aa,
              tcgapaad.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'PRAD' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaprad.analyte gan,
              tcgaprad.analyte_archive aa,
              tcgaprad.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'READ' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaread.analyte gan,
              tcgaread.analyte_archive aa,
              tcgaread.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'SALD' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgasald.analyte gan,
              tcgasald.analyte_archive aa,
              tcgasald.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'SKCM' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaskcm.analyte gan,
              tcgaskcm.analyte_archive aa,
              tcgaskcm.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'STAD' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgastad.analyte gan,
              tcgastad.analyte_archive aa,
              tcgastad.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'THCA' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgathca.analyte gan,
              tcgathca.analyte_archive aa,
              tcgathca.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'UCEC' AS disease_abbreviation,
              gan.uuid,
              gan.analyte_barcode AS barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode))
                 AS analyte_code,
              gan.analyte_id,
              gan.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaucec.analyte gan,
              tcgaucec.analyte_archive aa,
              tcgaucec.archive_info ai
      WHERE       gan.analyte_id = aa.analyte_id
              AND aa.archive_id = ai.archive_id
   GROUP BY   gan.uuid,
              gan.analyte_barcode,
              SUBSTR (gan.analyte_barcode, LENGTH (gan.analyte_barcode)),
              gan.analyte_id,
              gan.portion_id,
              ai.serial_index,
              ai.center_id;

CREATE OR REPLACE FORCE VIEW DRUG_V
(
   DISEASE_ABBREVIATION,
   UUID,
   BARCODE,
   PATIENT_ID,
   DATE_UPDATED,
   DATE_ADDED,
   BATCH,
   CENTER_ID
)
AS
     SELECT   DISTINCT 'BLCA' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgablca.drug_intgen r,
              tcgablca.drug_intgen_archive ra,
              tcgablca.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'BRCA' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgabrca.drug_intgen r,
              tcgabrca.drug_intgen_archive ra,
              tcgabrca.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'CESC' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgacesc.drug_intgen r,
              tcgacesc.drug_intgen_archive ra,
              tcgacesc.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'COAD' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgacoad.drug_intgen r,
              tcgacoad.drug_intgen_archive ra,
              tcgacoad.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'DLBC' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgadlbc.drug_intgen r,
              tcgadlbc.drug_intgen_archive ra,
              tcgadlbc.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'ESCA' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaesca.drug_intgen r,
              tcgaesca.drug_intgen_archive ra,
              tcgaesca.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'GBM' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgagbm.drug_intgen r,
              tcgagbm.drug_intgen_archive ra,
              tcgagbm.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'HNSC' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgahnsc.drug_intgen r,
              tcgahnsc.drug_intgen_archive ra,
              tcgahnsc.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'KIRC' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgakirc.drug_intgen r,
              tcgakirc.drug_intgen_archive ra,
              tcgakirc.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'KIRP' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgakirp.drug_intgen r,
              tcgakirp.drug_intgen_archive ra,
              tcgakirp.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LAML' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalaml.drug_intgen r,
              tcgalaml.drug_intgen_archive ra,
              tcgalaml.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LCLL' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalcll.drug_intgen r,
              tcgalcll.drug_intgen_archive ra,
              tcgalcll.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LGG' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalgg.drug_intgen r,
              tcgalgg.drug_intgen_archive ra,
              tcgalgg.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LIHC' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalihc.drug_intgen r,
              tcgalihc.drug_intgen_archive ra,
              tcgalihc.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LNNH' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalnnh.drug_intgen r,
              tcgalnnh.drug_intgen_archive ra,
              tcgalnnh.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LUAD' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaluad.drug_intgen r,
              tcgaluad.drug_intgen_archive ra,
              tcgaluad.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LUSC' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalusc.drug_intgen r,
              tcgalusc.drug_intgen_archive ra,
              tcgalusc.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'OV' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaov.drug_intgen r,
              tcgaov.drug_intgen_archive ra,
              tcgaov.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'PAAD' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgapaad.drug_intgen r,
              tcgapaad.drug_intgen_archive ra,
              tcgapaad.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'PRAD' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaprad.drug_intgen r,
              tcgaprad.drug_intgen_archive ra,
              tcgaprad.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'READ' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaread.drug_intgen r,
              tcgaread.drug_intgen_archive ra,
              tcgaread.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'SALD' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgasald.drug_intgen r,
              tcgasald.drug_intgen_archive ra,
              tcgasald.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'SKCM' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaskcm.drug_intgen r,
              tcgaskcm.drug_intgen_archive ra,
              tcgaskcm.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'STAD' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgastad.drug_intgen r,
              tcgastad.drug_intgen_archive ra,
              tcgastad.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'THCA' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgathca.drug_intgen r,
              tcgathca.drug_intgen_archive ra,
              tcgathca.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'UCEC' AS disease_abbreviation,
                       r.uuid,
                       r.drug_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaucec.drug_intgen r,
              tcgaucec.drug_intgen_archive ra,
              tcgaucec.archive_info ai
      WHERE       r.drug_id = ra.drug_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.drug_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id;

CREATE OR REPLACE FORCE VIEW EXAMINATION_V
(
   DISEASE_ABBREVIATION,
   UUID,
   BARCODE,
   PATIENT_ID,
   DATE_UPDATED,
   DATE_ADDED,
   BATCH,
   CENTER_ID
)
AS
     SELECT   DISTINCT 'BLCA' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgablca.examination r,
              tcgablca.examination_archive ra,
              tcgablca.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'BRCA' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgabrca.examination r,
              tcgabrca.examination_archive ra,
              tcgabrca.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'CESC' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgacesc.examination r,
              tcgacesc.examination_archive ra,
              tcgacesc.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'COAD' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgacoad.examination r,
              tcgacoad.examination_archive ra,
              tcgacoad.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'DLBC' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgadlbc.examination r,
              tcgadlbc.examination_archive ra,
              tcgadlbc.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'ESCA' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaesca.examination r,
              tcgaesca.examination_archive ra,
              tcgaesca.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'GBM' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgagbm.examination r,
              tcgagbm.examination_archive ra,
              tcgagbm.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'HNSC' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgahnsc.examination r,
              tcgahnsc.examination_archive ra,
              tcgahnsc.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'KIRC' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgakirc.examination r,
              tcgakirc.examination_archive ra,
              tcgakirc.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'KIRP' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgakirp.examination r,
              tcgakirp.examination_archive ra,
              tcgakirp.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LAML' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalaml.examination r,
              tcgalaml.examination_archive ra,
              tcgalaml.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LCLL' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalcll.examination r,
              tcgalcll.examination_archive ra,
              tcgalcll.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LGG' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalgg.examination r,
              tcgalgg.examination_archive ra,
              tcgalgg.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LIHC' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalihc.examination r,
              tcgalihc.examination_archive ra,
              tcgalihc.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LNNH' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalnnh.examination r,
              tcgalnnh.examination_archive ra,
              tcgalnnh.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LUAD' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaluad.examination r,
              tcgaluad.examination_archive ra,
              tcgaluad.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LUSC' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalusc.examination r,
              tcgalusc.examination_archive ra,
              tcgalusc.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'OV' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaov.examination r,
              tcgaov.examination_archive ra,
              tcgaov.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'PAAD' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgapaad.examination r,
              tcgapaad.examination_archive ra,
              tcgapaad.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'PRAD' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaprad.examination r,
              tcgaprad.examination_archive ra,
              tcgaprad.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'READ' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaread.examination r,
              tcgaread.examination_archive ra,
              tcgaread.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'SALD' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgasald.examination r,
              tcgasald.examination_archive ra,
              tcgasald.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'SKCM' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaskcm.examination r,
              tcgaskcm.examination_archive ra,
              tcgaskcm.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'STAD' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgastad.examination r,
              tcgastad.examination_archive ra,
              tcgastad.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'THCA' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgathca.examination r,
              tcgathca.examination_archive ra,
              tcgathca.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'UCEC' AS disease_abbreviation,
                       r.uuid,
                       r.exam_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaucec.examination r,
              tcgaucec.examination_archive ra,
              tcgaucec.archive_info ai
      WHERE       r.examination_id = ra.examination_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.exam_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id;

CREATE OR REPLACE FORCE VIEW PATIENT_V
(
   DISEASE_ABBREVIATION,
   UUID,
   BARCODE,
   PARTICIPANT_NUMBER,
   TSS_CODE,
   PATIENT_ID,
   DATE_UPDATED,
   DATE_ADDED,
   BATCH,
   CENTER_ID
)
AS
     SELECT   DISTINCT
              'BLCA' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgablca.patient gp,
              tcgablca.patient_archive pa,
              tcgablca.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'BRCA' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgabrca.patient gp,
              tcgabrca.patient_archive pa,
              tcgabrca.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'CESC' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgacesc.patient gp,
              tcgacesc.patient_archive pa,
              tcgacesc.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'COAD' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgacoad.patient gp,
              tcgacoad.patient_archive pa,
              tcgacoad.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'DLBC' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgadlbc.patient gp,
              tcgadlbc.patient_archive pa,
              tcgadlbc.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'ESCA' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaesca.patient gp,
              tcgaesca.patient_archive pa,
              tcgaesca.archive_info ai
      WHERE   gp.patient_id = pa.patient_id
        AND   pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'GBM' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgagbm.patient gp,
              tcgagbm.patient_archive pa,
              tcgagbm.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'HNSC' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgahnsc.patient gp,
              tcgahnsc.patient_archive pa,
              tcgahnsc.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'KIRC' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgakirc.patient gp,
              tcgakirc.patient_archive pa,
              tcgakirc.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'KIRP' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgakirp.patient gp,
              tcgakirp.patient_archive pa,
              tcgakirp.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LAML' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalaml.patient gp,
              tcgalaml.patient_archive pa,
              tcgalaml.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LCLL' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalcll.patient gp,
              tcgalcll.patient_archive pa,
              tcgalcll.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LGG' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalgg.patient gp,
              tcgalgg.patient_archive pa,
              tcgalgg.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LIHC' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalihc.patient gp,
              tcgalihc.patient_archive pa,
              tcgalihc.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LNNH' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalnnh.patient gp,
              tcgalnnh.patient_archive pa,
              tcgalnnh.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LUAD' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaluad.patient gp,
              tcgaluad.patient_archive pa,
              tcgaluad.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LUSC' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalusc.patient gp,
              tcgalusc.patient_archive pa,
              tcgalusc.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'OV' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaov.patient gp,
              tcgaov.patient_archive pa,
              tcgaov.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'PAAD' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgapaad.patient gp,
              tcgapaad.patient_archive pa,
              tcgapaad.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'PRAD' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaprad.patient gp,
              tcgaprad.patient_archive pa,
              tcgaprad.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'READ' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaread.patient gp,
              tcgaread.patient_archive pa,
              tcgaread.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'SALD' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgasald.patient gp,
              tcgasald.patient_archive pa,
              tcgasald.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'SKCM' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaskcm.patient gp,
              tcgaskcm.patient_archive pa,
              tcgaskcm.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'STAD' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgastad.patient gp,
              tcgastad.patient_archive pa,
              tcgastad.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'THCA' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgathca.patient gp,
              tcgathca.patient_archive pa,
              tcgathca.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'UCEC' AS disease_abbreviation,
              gp.uuid,
              gp.patient_barcode AS barcode,
              SUBSTR (gp.patient_barcode, -4, 4) AS participant_number,
              SUBSTR (gp.patient_barcode, 6, 2) AS tss_code,
              gp.patient_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaucec.patient gp,
              tcgaucec.patient_archive pa,
              tcgaucec.archive_info ai
      WHERE       gp.patient_id = pa.patient_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.uuid,
              gp.patient_barcode,
              SUBSTR (gp.patient_barcode, -4, 4),
              SUBSTR (gp.patient_barcode, 6, 2),
              gp.patient_id,
              ai.serial_index,
              ai.center_id;

CREATE OR REPLACE FORCE VIEW PORTION_V
(
   DISEASE_ABBREVIATION,
   SAMPLE_ID,
   UUID,
   BARCODE,
   PORTION,
   PORTION_ID,
   DATE_UPDATED,
   DATE_ADDED,
   BATCH,
   CENTER_ID
)
AS
     SELECT   DISTINCT
              'BLCA' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgablca.portion gp,
              tcgablca.portion_archive pa,
              tcgablca.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'BRCA' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgabrca.portion gp,
              tcgabrca.portion_archive pa,
              tcgabrca.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'CESC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgacesc.portion gp,
              tcgacesc.portion_archive pa,
              tcgacesc.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'COAD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgacoad.portion gp,
              tcgacoad.portion_archive pa,
              tcgacoad.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'DLBC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgadlbc.portion gp,
              tcgadlbc.portion_archive pa,
              tcgadlbc.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'ESCA' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaesca.portion gp,
              tcgaesca.portion_archive pa,
              tcgaesca.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'GBM' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgagbm.portion gp,
              tcgagbm.portion_archive pa,
              tcgagbm.archive_info ai
      WHERE   gp.portion_id = pa.portion_id
        AND   pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'HNSC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgahnsc.portion gp,
              tcgahnsc.portion_archive pa,
              tcgahnsc.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'KIRC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgakirc.portion gp,
              tcgakirc.portion_archive pa,
              tcgakirc.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'KIRP' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgakirp.portion gp,
              tcgakirp.portion_archive pa,
              tcgakirp.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LAML' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalaml.portion gp,
              tcgalaml.portion_archive pa,
              tcgalaml.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LCLL' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalcll.portion gp,
              tcgalcll.portion_archive pa,
              tcgalcll.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LGG' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalgg.portion gp,
              tcgalgg.portion_archive pa,
              tcgalgg.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LIHC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalihc.portion gp,
              tcgalihc.portion_archive pa,
              tcgalihc.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LNNH' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalnnh.portion gp,
              tcgalnnh.portion_archive pa,
              tcgalnnh.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LUAD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaluad.portion gp,
              tcgaluad.portion_archive pa,
              tcgaluad.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LUSC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalusc.portion gp,
              tcgalusc.portion_archive pa,
              tcgalusc.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'OV' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaov.portion gp,
              tcgaov.portion_archive pa,
              tcgaov.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'PAAD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgapaad.portion gp,
              tcgapaad.portion_archive pa,
              tcgapaad.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'PRAD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaprad.portion gp,
              tcgaprad.portion_archive pa,
              tcgaprad.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'READ' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaread.portion gp,
              tcgaread.portion_archive pa,
              tcgaread.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'SALD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgasald.portion gp,
              tcgasald.portion_archive pa,
              tcgasald.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'SKCM' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaskcm.portion gp,
              tcgaskcm.portion_archive pa,
              tcgaskcm.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'STAD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgasald.portion gp,
              tcgasald.portion_archive pa,
              tcgasald.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
  UNION
     SELECT   DISTINCT
              'THCA' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgathca.portion gp,
              tcgathca.portion_archive pa,
              tcgathca.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'UCEC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.portion_barcode AS barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1)
                 AS portion,
              gp.portion_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaucec.portion gp,
              tcgaucec.portion_archive pa,
              tcgaucec.archive_info ai
      WHERE       gp.portion_id = pa.portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.portion_barcode,
              SUBSTR (gp.portion_barcode, LENGTH (gp.portion_barcode) - 1),
              gp.portion_id,
              ai.serial_index,
              ai.center_id;

CREATE OR REPLACE FORCE VIEW RADIATION_V
(
   DISEASE_ABBREVIATION,
   UUID,
   BARCODE,
   PATIENT_ID,
   DATE_UPDATED,
   DATE_ADDED,
   BATCH,
   CENTER_ID
)
AS
     SELECT   DISTINCT 'BLCA' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgablca.radiation r,
              tcgablca.radiation_archive ra,
              tcgablca.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'BRCA' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgabrca.radiation r,
              tcgabrca.radiation_archive ra,
              tcgabrca.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'CESC' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgacesc.radiation r,
              tcgacesc.radiation_archive ra,
              tcgacesc.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'COAD' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgacoad.radiation r,
              tcgacoad.radiation_archive ra,
              tcgacoad.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'DLBC' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgadlbc.radiation r,
              tcgadlbc.radiation_archive ra,
              tcgadlbc.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'ESCA' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaesca.radiation r,
              tcgaesca.radiation_archive ra,
              tcgaesca.archive_info ai
      WHERE   r.radiation_id = ra.radiation_id
        AND   ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'GBM' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgagbm.radiation r,
              tcgagbm.radiation_archive ra,
              tcgagbm.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'HNSC' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgahnsc.radiation r,
              tcgahnsc.radiation_archive ra,
              tcgahnsc.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'KIRC' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgakirc.radiation r,
              tcgakirc.radiation_archive ra,
              tcgakirc.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'KIRP' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgakirp.radiation r,
              tcgakirp.radiation_archive ra,
              tcgakirp.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LAML' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalaml.radiation r,
              tcgalaml.radiation_archive ra,
              tcgalaml.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LCLL' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalcll.radiation r,
              tcgalcll.radiation_archive ra,
              tcgalcll.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LGG' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalgg.radiation r,
              tcgalgg.radiation_archive ra,
              tcgalgg.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LIHC' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalihc.radiation r,
              tcgalihc.radiation_archive ra,
              tcgalihc.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LNNH' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalnnh.radiation r,
              tcgalnnh.radiation_archive ra,
              tcgalnnh.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LUAD' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaluad.radiation r,
              tcgaluad.radiation_archive ra,
              tcgaluad.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LUSC' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalusc.radiation r,
              tcgalusc.radiation_archive ra,
              tcgalusc.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'OV' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaov.radiation r,
              tcgaov.radiation_archive ra,
              tcgaov.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'PAAD' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgapaad.radiation r,
              tcgapaad.radiation_archive ra,
              tcgapaad.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'PRAD' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaprad.radiation r,
              tcgaprad.radiation_archive ra,
              tcgaprad.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'READ' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaread.radiation r,
              tcgaread.radiation_archive ra,
              tcgaread.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'SALD' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgasald.radiation r,
              tcgasald.radiation_archive ra,
              tcgasald.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'SKCM' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaskcm.radiation r,
              tcgaskcm.radiation_archive ra,
              tcgaskcm.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'STAD' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgastad.radiation r,
              tcgastad.radiation_archive ra,
              tcgastad.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'THCA' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgathca.radiation r,
              tcgathca.radiation_archive ra,
              tcgathca.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'UCEC' AS disease_abbreviation,
                       r.uuid,
                       r.radiation_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaucec.radiation r,
              tcgaucec.radiation_archive ra,
              tcgaucec.archive_info ai
      WHERE       r.radiation_id = ra.radiation_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.radiation_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id;

CREATE OR REPLACE FORCE VIEW SAMPLE_V
(
   DISEASE_ABBREVIATION,
   UUID,
   BARCODE,
   SAMPLE_ID,
   PATIENT_ID,
   DATE_UPDATED,
   DATE_ADDED,
   BATCH,
   CENTER_ID,
   SAMPLE_TYPE,
   SAMPLE_SEQUENCE
)
AS
     SELECT   DISTINCT 'BLCA' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgablca.sample gs,
              tcgablca.sample_archive sa,
              tcgablca.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'BRCA' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgabrca.sample gs,
              tcgabrca.sample_archive sa,
              tcgabrca.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'CESC' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgacesc.sample gs,
              tcgacesc.sample_archive sa,
              tcgacesc.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'COAD' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgacoad.sample gs,
              tcgacoad.sample_archive sa,
              tcgacoad.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'DLBC' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgadlbc.sample gs,
              tcgadlbc.sample_archive sa,
              tcgadlbc.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'ESCA' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgaesca.sample gs,
              tcgaesca.sample_archive sa,
              tcgaesca.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'GBM' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgagbm.sample gs,
              tcgagbm.sample_archive sa,
              tcgagbm.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'HNSC' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgahnsc.sample gs,
              tcgahnsc.sample_archive sa,
              tcgahnsc.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'KIRC' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgakirc.sample gs,
              tcgakirc.sample_archive sa,
              tcgakirc.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'KIRP' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgakirp.sample gs,
              tcgakirp.sample_archive sa,
              tcgakirp.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LAML' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgalaml.sample gs,
              tcgalaml.sample_archive sa,
              tcgalaml.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LCLL' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgalcll.sample gs,
              tcgalcll.sample_archive sa,
              tcgalcll.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LGG' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgalgg.sample gs,
              tcgalgg.sample_archive sa,
              tcgalgg.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LIHC' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgalihc.sample gs,
              tcgalihc.sample_archive sa,
              tcgalihc.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LNNH' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgalnnh.sample gs,
              tcgalnnh.sample_archive sa,
              tcgalnnh.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LUAD' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgaluad.sample gs,
              tcgaluad.sample_archive sa,
              tcgaluad.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LUSC' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgalusc.sample gs,
              tcgalusc.sample_archive sa,
              tcgalusc.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'OV' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgaov.sample gs,
              tcgaov.sample_archive sa,
              tcgaov.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'PAAD' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgapaad.sample gs,
              tcgapaad.sample_archive sa,
              tcgapaad.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'PRAD' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgaprad.sample gs,
              tcgaprad.sample_archive sa,
              tcgaprad.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'READ' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgaread.sample gs,
              tcgaread.sample_archive sa,
              tcgaread.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'SALD' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgasald.sample gs,
              tcgasald.sample_archive sa,
              tcgasald.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'SKCM' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgaskcm.sample gs,
              tcgaskcm.sample_archive sa,
              tcgaskcm.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'STAD' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgastad.sample gs,
              tcgastad.sample_archive sa,
              tcgastad.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'THCA' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgathca.sample gs,
              tcgathca.sample_archive sa,
              tcgathca.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'UCEC' AS disease_abbreviation,
                       gs.uuid,
                       gs.sample_barcode AS barcode,
                       gs.sample_id,
                       gs.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id,
                       SUBSTR (gs.sample_barcode, -3, 2) AS sample_type,
                       SUBSTR (gs.sample_barcode, -1) AS sample_sequence
       FROM   tcgaucec.sample gs,
              tcgaucec.sample_archive sa,
              tcgaucec.archive_info ai
      WHERE       gs.sample_id = sa.sample_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gs.uuid,
              gs.sample_barcode,
              SUBSTR (gs.sample_barcode, -4, 4),
              SUBSTR (gs.sample_barcode, 6, 2),
              gs.sample_id,
              gs.patient_id,
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              SUBSTR (gs.sample_barcode, -3, 2),
              SUBSTR (gs.sample_barcode, -1),
              ai.serial_index,
              ai.center_id;

CREATE OR REPLACE FORCE VIEW SLIDE_V
(
   DISEASE_ABBREVIATION,
   UUID,
   BARCODE,
   PORTION_ID,
   SLIDE,
   DATE_UPDATED,
   DATE_ADDED,
   BATCH,
   CENTER_ID
)
AS
     SELECT   DISTINCT
              'BLCA' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgablca.slide gsl,
              tcgablca.slide_archive sa,
              tcgablca.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'BRCA' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgabrca.slide gsl,
              tcgabrca.slide_archive sa,
              tcgabrca.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'CESC' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgacesc.slide gsl,
              tcgacesc.slide_archive sa,
              tcgacesc.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'COAD' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgacoad.slide gsl,
              tcgacoad.slide_archive sa,
              tcgacoad.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'DLBC' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgadlbc.slide gsl,
              tcgadlbc.slide_archive sa,
              tcgadlbc.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
    UNION
     SELECT   DISTINCT
              'ESCA' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaesca.slide gsl,
              tcgaesca.slide_archive sa,
              tcgaesca.archive_info ai
      WHERE   gsl.slide_id = sa.slide_id
        AND   sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
 UNION
     SELECT   DISTINCT
              'GBM' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgagbm.slide gsl,
              tcgagbm.slide_archive sa,
              tcgagbm.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'HNSC' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgahnsc.slide gsl,
              tcgahnsc.slide_archive sa,
              tcgahnsc.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'KIRC' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgakirc.slide gsl,
              tcgakirc.slide_archive sa,
              tcgakirc.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'KIRP' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgakirp.slide gsl,
              tcgakirp.slide_archive sa,
              tcgakirp.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LAML' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalaml.slide gsl,
              tcgalaml.slide_archive sa,
              tcgalaml.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LCLL' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalcll.slide gsl,
              tcgalcll.slide_archive sa,
              tcgalcll.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LGG' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalgg.slide gsl,
              tcgalgg.slide_archive sa,
              tcgalgg.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LIHC' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalihc.slide gsl,
              tcgalihc.slide_archive sa,
              tcgalihc.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LNNH' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalnnh.slide gsl,
              tcgalnnh.slide_archive sa,
              tcgalnnh.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LUAD' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaluad.slide gsl,
              tcgaluad.slide_archive sa,
              tcgaluad.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LUSC' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalusc.slide gsl,
              tcgalusc.slide_archive sa,
              tcgalusc.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'OV' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaov.slide gsl, tcgaov.slide_archive sa, tcgaov.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'PAAD' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgapaad.slide gsl,
              tcgapaad.slide_archive sa,
              tcgapaad.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'PRAD' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaprad.slide gsl,
              tcgaprad.slide_archive sa,
              tcgaprad.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'READ' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaread.slide gsl,
              tcgaread.slide_archive sa,
              tcgaread.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'SALD' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgasald.slide gsl,
              tcgasald.slide_archive sa,
              tcgasald.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'SKCM' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaskcm.slide gsl,
              tcgaskcm.slide_archive sa,
              tcgaskcm.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'STAD' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgastad.slide gsl,
              tcgastad.slide_archive sa,
              tcgastad.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'THCA' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgathca.slide gsl,
              tcgathca.slide_archive sa,
              tcgathca.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'UCEC' AS disease_abbreviation,
              gsl.uuid,
              gsl.slide_barcode AS barcode,
              gsl.portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2)
                 AS slide,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaucec.slide gsl,
              tcgaucec.slide_archive sa,
              tcgaucec.archive_info ai
      WHERE       gsl.slide_id = sa.slide_id
              AND sa.archive_id = ai.archive_id
   GROUP BY   gsl.uuid,
              gsl.slide_barcode,
              portion_id,
              SUBSTR (gsl.slide_barcode, LENGTH (gsl.slide_barcode) - 2),
              ai.serial_index,
              ai.center_id;

CREATE OR REPLACE FORCE VIEW SURGERY_V
(
   DISEASE_ABBREVIATION,
   UUID,
   BARCODE,
   PATIENT_ID,
   DATE_UPDATED,
   DATE_ADDED,
   BATCH,
   CENTER_ID
)
AS
     SELECT   DISTINCT 'BLCA' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgablca.surgery r,
              tcgablca.surgery_archive ra,
              tcgablca.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'BRCA' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgabrca.surgery r,
              tcgabrca.surgery_archive ra,
              tcgabrca.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'CESC' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgacesc.surgery r,
              tcgacesc.surgery_archive ra,
              tcgacesc.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'COAD' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgacoad.surgery r,
              tcgacoad.surgery_archive ra,
              tcgacoad.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'DLBC' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgadlbc.surgery r,
              tcgadlbc.surgery_archive ra,
              tcgadlbc.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'ESCA' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaesca.surgery r,
              tcgaesca.surgery_archive ra,
              tcgaesca.archive_info ai
      WHERE   r.surgery_id = ra.surgery_id
        AND   ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'GBM' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgagbm.surgery r,
              tcgagbm.surgery_archive ra,
              tcgagbm.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'HNSC' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgahnsc.surgery r,
              tcgahnsc.surgery_archive ra,
              tcgahnsc.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'KIRC' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgakirc.surgery r,
              tcgakirc.surgery_archive ra,
              tcgakirc.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'KIRP' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgakirp.surgery r,
              tcgakirp.surgery_archive ra,
              tcgakirp.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LAML' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalaml.surgery r,
              tcgalaml.surgery_archive ra,
              tcgalaml.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LCLL' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalcll.surgery r,
              tcgalcll.surgery_archive ra,
              tcgalcll.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LGG' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalgg.surgery r,
              tcgalgg.surgery_archive ra,
              tcgalgg.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LIHC' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalihc.surgery r,
              tcgalihc.surgery_archive ra,
              tcgalihc.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LNNH' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalnnh.surgery r,
              tcgalnnh.surgery_archive ra,
              tcgalnnh.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LUAD' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaluad.surgery r,
              tcgaluad.surgery_archive ra,
              tcgaluad.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'LUSC' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgalusc.surgery r,
              tcgalusc.surgery_archive ra,
              tcgalusc.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'OV' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaov.surgery r,
              tcgaov.surgery_archive ra,
              tcgaov.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'PAAD' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgapaad.surgery r,
              tcgapaad.surgery_archive ra,
              tcgapaad.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'PRAD' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaprad.surgery r,
              tcgaprad.surgery_archive ra,
              tcgaprad.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'READ' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaread.surgery r,
              tcgaread.surgery_archive ra,
              tcgaread.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'SALD' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgasald.surgery r,
              tcgasald.surgery_archive ra,
              tcgasald.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'SKCM' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaskcm.surgery r,
              tcgaskcm.surgery_archive ra,
              tcgaskcm.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'STAD' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgastad.surgery r,
              tcgastad.surgery_archive ra,
              tcgastad.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'THCA' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgathca.surgery r,
              tcgathca.surgery_archive ra,
              tcgathca.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT 'UCEC' AS disease_abbreviation,
                       r.uuid,
                       r.surgery_barcode AS barcode,
                       r.patient_id,
                       MAX (TRUNC (ai.date_added)) AS date_updated,
                       MIN (TRUNC (ai.date_added)) AS date_added,
                       ai.serial_index AS batch,
                       ai.center_id
       FROM   tcgaucec.surgery r,
              tcgaucec.surgery_archive ra,
              tcgaucec.archive_info ai
      WHERE       r.surgery_id = ra.surgery_id
              AND ra.archive_id = ai.archive_id
   GROUP BY   r.uuid,
              r.surgery_barcode,
              r.patient_id,
              ai.serial_index,
              ai.center_id;

CREATE OR REPLACE FORCE VIEW SHIPPED_PORTION_V
(
   DISEASE_ABBREVIATION,
   SAMPLE_ID,
   UUID,
   BARCODE,
   SHIPPED_PORTION,
   SHIPPED_PORTION_ID,
   BCR_CENTER_ID,
   DATE_UPDATED,
   DATE_ADDED,
   BATCH,
   CENTER_ID
)
AS
     SELECT   DISTINCT
              'BLCA' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgablca.shipped_portion gp,
              tcgablca.shipped_portion_archive pa,
              tcgablca.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'BRCA' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgabrca.shipped_portion gp,
              tcgabrca.shipped_portion_archive pa,
              tcgabrca.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'CESC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgacesc.shipped_portion gp,
              tcgacesc.shipped_portion_archive pa,
              tcgacesc.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'COAD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgacoad.shipped_portion gp,
              tcgacoad.shipped_portion_archive pa,
              tcgacoad.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'DLBC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgadlbc.shipped_portion gp,
              tcgadlbc.shipped_portion_archive pa,
              tcgadlbc.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'ESCA' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaesca.shipped_portion gp,
              tcgaesca.shipped_portion_archive pa,
              tcgaesca.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'GBM' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgagbm.shipped_portion gp,
              tcgagbm.shipped_portion_archive pa,
              tcgagbm.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'HNSC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgahnsc.shipped_portion gp,
              tcgahnsc.shipped_portion_archive pa,
              tcgahnsc.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'KIRC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgakirc.shipped_portion gp,
              tcgakirc.shipped_portion_archive pa,
              tcgakirc.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'KIRP' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgakirp.shipped_portion gp,
              tcgakirp.shipped_portion_archive pa,
              tcgakirp.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LAML' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalaml.shipped_portion gp,
              tcgalaml.shipped_portion_archive pa,
              tcgalaml.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LCLL' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalcll.shipped_portion gp,
              tcgalcll.shipped_portion_archive pa,
              tcgalcll.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LGG' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalgg.shipped_portion gp,
              tcgalgg.shipped_portion_archive pa,
              tcgalgg.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LIHC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalihc.shipped_portion gp,
              tcgalihc.shipped_portion_archive pa,
              tcgalihc.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LNNH' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalnnh.shipped_portion gp,
              tcgalnnh.shipped_portion_archive pa,
              tcgalnnh.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LUAD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaluad.shipped_portion gp,
              tcgaluad.shipped_portion_archive pa,
              tcgaluad.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'LUSC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgalusc.shipped_portion gp,
              tcgalusc.shipped_portion_archive pa,
              tcgalusc.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'OV' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaov.shipped_portion gp,
              tcgaov.shipped_portion_archive pa,
              tcgaov.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'PAAD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgapaad.shipped_portion gp,
              tcgapaad.shipped_portion_archive pa,
              tcgapaad.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'PRAD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaprad.shipped_portion gp,
              tcgaprad.shipped_portion_archive pa,
              tcgaprad.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'READ' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaread.shipped_portion gp,
              tcgaread.shipped_portion_archive pa,
              tcgaread.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'SALD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgasald.shipped_portion gp,
              tcgasald.shipped_portion_archive pa,
              tcgasald.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'SKCM' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaskcm.shipped_portion gp,
              tcgaskcm.shipped_portion_archive pa,
              tcgaskcm.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'STAD' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgastad.shipped_portion gp,
              tcgastad.shipped_portion_archive pa,
              tcgastad.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'THCA' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgathca.shipped_portion gp,
              tcgathca.shipped_portion_archive pa,
              tcgathca.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id
   UNION
     SELECT   DISTINCT
              'UCEC' AS disease_abbreviation,
              gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode AS barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS shipped_portion,
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1)
                 AS bcr_center_id,
              MAX (TRUNC (ai.date_added)) AS date_updated,
              MIN (TRUNC (ai.date_added)) AS date_added,
              ai.serial_index AS batch,
              ai.center_id
       FROM   tcgaucec.shipped_portion gp,
              tcgaucec.shipped_portion_archive pa,
              tcgaucec.archive_info ai
      WHERE       gp.shipped_portion_id = pa.shipped_portion_id
              AND pa.archive_id = ai.archive_id
              AND ai.is_latest = 1
   GROUP BY   gp.sample_id,
              gp.uuid,
              gp.shipped_portion_barcode,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              gp.shipped_portion_id,
              SUBSTR (gp.shipped_portion_barcode,
                      LENGTH (gp.shipped_portion_barcode) - 1),
              ai.serial_index,
              ai.center_id;


