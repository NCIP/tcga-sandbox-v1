DROP VIEW biospecimen_breakdown_all; 
CREATE OR REPLACE VIEW biospecimen_breakdown_all AS 
 SELECT b.biospecimen_id, 
        b.barcode, 
 	b.project || '-' || b.collection_center || '-' || b.patient || '-' || b.sample_type || b.sample_sequence || '-' || b.portion_sequence || b.portion_analyte || '-' || b.plate_id || '-' || b.bcr_center_id AS built_barcode, 
 	b.project || '-' || b.collection_center || '-' || b.patient || '-' || b.sample_type || b.sample_sequence || '-' || b.portion_sequence || b.portion_analyte AS biospecimen,        
        b.project || '-' || b.collection_center || '-' || b.patient || '-' || b.sample_type || '-' || b.portion_analyte AS analyte, 
        b.project || '-' || b.collection_center || '-' || b.patient || '-' || b.sample_type AS sample, 
        b.project || '-' || b.collection_center || '-' || b.patient AS specific_patient, 
        b.project, 
        b.collection_center, 
        b.patient, 
        b.sample_type, 
        b.sample_sequence, 
        b.portion_sequence, 
        b.portion_analyte, 
        b.plate_id, 
        b.bcr_center_id, 
        b.is_valid, 
        b.is_viewable
   FROM biospecimen_barcode b;
GRANT select on biospecimen_breakdown_all to tcgaread;
GRANT select on biospecimen_breakdown_all to tcgamaint;
