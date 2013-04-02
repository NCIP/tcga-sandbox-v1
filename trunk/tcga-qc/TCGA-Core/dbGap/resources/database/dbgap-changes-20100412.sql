update clinical_file_element set file_column_name='ANALYTETYPE'
where clinical_file_element_id = (select clinical_file_element_id
from clinical_file_element, clinical_file
where clinical_file_element.clinical_file_id=clinical_file.clinical_file_id and filename='dbgap_samples' and table_column_name='TYPE');