alter table L4_genetic_element_type
add info_url varchar(2000);

alter table disease add workbench_track number(10);
update disease set workbench_track=41 where disease_id=1;
update disease set workbench_track=111 where disease_id=2;
update L4_genetic_element_type set INFO_URL='https://cgwb.nci.nih.gov/cgi-bin/hgTracks?position=chrCHROMOSOME:START_POS-STOP_POS' || '&' || 'fpj=WORKBENCH_TRACK' WHERE genetic_element_type_id=1;


