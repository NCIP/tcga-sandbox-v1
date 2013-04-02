MERGE INTO biospecimen_breakdown_all b
USING (select distinct ai.annotation_item--,at.type_display_name
                 from annotation a, annotation_category ac, annotation_item ai, annotation_item_type at
                 where a.curated=1
                 and a.annotation_category_id = ac.annotation_category_id
                 and ac.category_display_name like 'Redaction%'
                 and a.annotation_id=ai.annotation_id
                 and ai.item_type_id=at.item_type_id) v
ON (b.specific_patient = v.annotation_item or b.sample = v.annotation_item)
WHEN MATCHED THEN UPDATE set is_viewable=0; 
commit;