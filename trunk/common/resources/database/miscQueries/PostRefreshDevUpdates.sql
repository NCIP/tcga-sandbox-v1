UPDATE center_email set email_address = 'tcgadccteam@list.nih.gov' ;
UPDATE pi_info set email_address = 'tcgadccteam@list.nih.gov';
commit;

UPDATE archive_info set deploy_location = REPLACE(deploy_location,'tcgafiles','tcgafiles_PROD');
UPDATE file_to_archive set file_location_url = REPLACE(file_location_url,'tcgafiles','tcgafiles_PROD');

commit;
