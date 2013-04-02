-- these are insert statements to populate things needed for annotations; they aren't complete!

insert into annotation_category(annotation_category_id, category_display_name, category_description) values(100, 'withdrew consent', 'withdrew consent for study');

insert into annotation_category(annotation_category_id, category_display_name, category_description) values(101, 'part of project', 'part of project');

insert into annotation_category(annotation_category_id, category_display_name, category_description) values(102, 'incorrect type', 'incorrect type');

insert into ANNOTATION_ITEM_TYPE(item_type_id, type_display_name, type_description) values(100, 'patient', 'patient');

insert into ANNOTATION_ITEM_TYPE(item_type_id, type_display_name, type_description) values(101, 'sample', 'sample');

insert into ANNOTATION_ITEM_TYPE(item_type_id, type_display_name, type_description) values(102, 'aliquot', 'aliquot');

insert into ANNOTATION_CATEGORY_ITEM_TYPE(annotation_category_type_id, annotation_category_id, item_type_id) values(100, 100, 100);

insert into ANNOTATION_CATEGORY_ITEM_TYPE(annotation_category_type_id, annotation_category_id, item_type_id) values(101, 101, 102);

insert into ANNOTATION_CATEGORY_ITEM_TYPE(annotation_category_type_id, annotation_category_id, item_type_id) values(102, 102, 101);

insert into DISEASE(disease_id, disease_name, disease_abbreviation, disease_schema, active) values(1, 'Glioblastoma multiforme', 'GBM', 'TCGAGBM', 1);

insert into DISEASE(disease_id, disease_name, disease_abbreviation, disease_schema, active) values(2, 'Ovarian serous cystadenocarcinoma', 'OV', 'TCGAOV', 1);

insert into DISEASE(disease_id, disease_name, disease_abbreviation, disease_schema, active) values(3, 'Lung squamous cell carcinoma', 'LUSC', 'TCGALUSC', 1);