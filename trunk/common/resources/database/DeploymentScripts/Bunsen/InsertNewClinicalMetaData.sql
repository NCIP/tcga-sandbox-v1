DECLARE
    uuid_element number(38);
    barcode_element number(38);
BEGIN
    	SELECT clinical_xsd_seq.NEXTVAL INTO uuid_element FROM DUAL;
    	SELECT clinical_xsd_seq.NEXTVAL INTO barcode_element FROM DUAL;
    	INSERT INTO clinical_xsd_element 
    		(clinical_xsd_element_id,element_name,is_protected,value_type,expected_element)
    	VALUES  (barcode_element,'shipment_portion_bcr_aliquot_barcode',0,'string','Y');


	INSERT INTO clinical_xsd_element (
		clinical_xsd_element_id,
		element_name,
		is_protected,
		value_type,
		expected_element)
	VALUES (uuid_element,'bcr_shipment_portion_uuid',0,'string','Y');

	INSERT INTO clinical_table (
		clinical_table_id,table_name,
		join_for_sample,
		join_for_patient,
		barcode_element_id,
		barcode_column_name,
		element_node_name,
		element_table_name,
		table_id_column_name,
		archive_link_table_name,
		parent_table_id,uuid_element_id)
	VALUES (
		17,'SHIPPED_PORTION',
		'SHIPPED_PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID',
		'SHIPPED_PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID AND SAMPLE.PATIENT_ID=PATIENT.PATIENT_ID',
		barcode_element,
		'SHIPPED_PORTION_BARCODE',
		'shipment_portion',
		'SHIPPED_PORTION_ELEMENT',
		'SHIPPED_PORTION_ID',
		'SHIPPED_PORTION_ARCHIVE',
		2,uuid_element
	);

	INSERT INTO clinical_file (clinical_file_id,filename,by_patient,context) 
	VALUES (23,'clinical_shipment_portion',0,'dam');
	
	INSERT INTO clinical_file_to_table VALUES (19,23,17);
	
	commit;
END;
/		

