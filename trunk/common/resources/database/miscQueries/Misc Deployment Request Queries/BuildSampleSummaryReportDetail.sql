CREATE OR REPLACE FUNCTION build_sample_summary_report_detail()
  RETURNS void AS
$BODY$
 	TRUNCATE TABLE sample_level_count;
	INSERT INTO sample_level_count
	(
	  tumor_abbreviation,  center_name,  center_type ,  portion_analyte, platform , level, sampleCount
	)
	SELECT t.tumor_abbreviation,c.center_name, m.center_type, 
	       bb.portion_analyte, p.platform_alias, l.level_number,
	       COALESCE(count(distinct bb.sample),0) as sampleCount 
	FROM center_info c, archive_info a, platform_info p, file_info f, center_bcr_center_map m,
	     biospecimen_to_file bf, biospecimen_breakdown_all bb, file_data_level l, tumor_info t  
	WHERE c.id = a.center_id  
	AND   c.id = m.center_id
	AND   a.platform_id = p.id  
	AND   a.id = f.file_archive_id  
	AND   f.id = bf.file_info_id  
	AND   f.id = l.file_info_id  
	AND   a.tumor_id = t.id 
	AND   bf.biospecimen_id = bb.biospecimen_id  
	AND   bb.is_viewable = 1
	AND   a.is_latest = 1  
	AND   a.deploy_status = 'Available' 
	GROUP BY c.center_name, m.center_type, bb.portion_analyte, platform_alias, t.tumor_abbreviation,l.level_number;

	TRUNCATE TABLE latest_samples_received_by_dcc;
	-- get all samples centers sent
	INSERT INTO latest_samples_received_by_dcc
	SELECT distinct t.tumor_abbreviation, c.id as center_id, c.center_name, m.center_type, p.platform_alias as platform, bb.sample, bb.portion_analyte, 
	       bb.is_valid, bb.is_viewable
	FROM archive_info a, center_info c, platform_info p, tumor_info t, file_info f, center_bcr_center_map m,
	      biospecimen_gsc_file_mv bt, biospecimen_breakdown_all bb 
	WHERE a.is_latest = 1  
	AND   a.deploy_status = 'Available' 
	AND   a.center_id = c.id
	AND   a.platform_id = p.id
	AND   a.tumor_id = t.id
	AND   a.id = f.file_archive_id
	AND   f.id = bt.file_info_id
	AND   bt.biospecimen_id = bb.biospecimen_id
	AND   bb.bcr_center_id = m.bcr_center_id
	UNION
	SELECT distinct t.tumor_abbreviation, c.id as center_id, c.center_name, m.center_type,p.platform_alias as platform, bb.sample, bb.portion_analyte, 
	       bb.is_valid, bb.is_viewable
	FROM archive_info a, center_info c, platform_info p, tumor_info t, file_info f, center_bcr_center_map m,
	      biospecimen_to_file bf, biospecimen_breakdown_all bb 
	WHERE a.is_latest = 1  
	AND   a.deploy_status = 'Available' 
	AND   a.center_id = c.id
	AND   a.platform_id = p.id
	AND   a.tumor_id = t.id
	AND   a.id = f.file_archive_id
	AND   f.id = bf.file_info_id
	AND   bf.biospecimen_id = bb.biospecimen_id
	AND   bb.bcr_center_id = m.bcr_center_id;

	DELETE FROM sample_summary_report_detail;

	INSERT INTO sample_summary_report_detail (tumor_abbreviation,center_name,center_type,portion_analyte,platform,
	                                        total_bcr_sent,total_centers_sent)
	SELECT bcr.tumor_abbreviation, bcr.center_name, bcr.center_type, bcr.portion_analyte, centers.platform, bcr.samples_sent, centers.samples_dcc_received
	FROM
	(SELECT tumor_abbreviation, COALESCE(count(distinct sample), 0) as samples_dcc_received, center_name, center_type, portion_analyte, platform
	 from latest_samples_received_by_dcc
	 WHERE   is_viewable=1
	 GROUP BY tumor_abbreviation, center_name,center_type,portion_analyte, platform) centers RIGHT OUTER JOIN
	(SELECT t.tumor_abbreviation,count(distinct b.sample) as samples_sent, center_name, m.center_type, b.portion_analyte, p.platform_alias as platform 
	 from biospecimen_breakdown_all b, biospecimen_to_archive ba, Archive_info a, tumor_info t, center_info c, 
	      platform_info p, center_bcr_center_map m  
	 WHERE c.id=m.center_id  
	 AND m.bcr_center_id = b.bcr_center_id  
	 AND b.biospecimen_id = ba.biospecimen_id  
	 AND ba.archive_id = a.id  
	 AND a.tumor_id = t.id 
	 AND a.platform_id = p.id
	 AND a.is_latest = 1 
	 AND b.is_valid=1
	 AND b.is_viewable=1 
	 AND a.deploy_status = 'Available'  
	 GROUP BY t.tumor_abbreviation,center_name, m.center_type, b.portion_analyte, p.platform_alias) bcr
	ON    centers.center_name = bcr.center_name
	AND   centers.center_type = bcr.center_type
	AND   centers.portion_analyte = bcr.portion_analyte
	AND   centers.tumor_abbreviation = bcr.tumor_abbreviation
	UNION
	SELECT centers.tumor_abbreviation, centers.center_name, centers.center_type, centers.portion_analyte, centers.platform, bcr.samples_sent, centers.samples_dcc_received 
	FROM
	(SELECT tumor_abbreviation,COALESCE(count(distinct sample),0) as samples_dcc_received, center_name, center_type, portion_analyte, platform
	 from latest_samples_received_by_dcc
	 WHERE   is_viewable=1
	 GROUP BY tumor_abbreviation, center_name,center_type,portion_analyte, platform) centers LEFT OUTER JOIN
	(SELECT t.tumor_abbreviation,COALESCE(count(distinct b.sample),0) as samples_sent, center_name, m.center_type, b.portion_analyte, p.platform_alias as platform 
	 from biospecimen_breakdown_all b, biospecimen_to_archive ba, Archive_info a, tumor_info t, center_info c, 
	      platform_info p, center_bcr_center_map m  
	 WHERE c.id=m.center_id  
	 AND m.bcr_center_id = b.bcr_center_id  
	 AND b.biospecimen_id = ba.biospecimen_id  
	 AND ba.archive_id = a.id  
	 AND a.tumor_id = t.id 
	 AND a.platform_id = p.id
	 AND a.is_latest = 1 
	 AND b.is_valid=1
	 AND b.is_viewable=1 
	 AND a.deploy_status = 'Available'  
	 GROUP BY t.tumor_abbreviation,center_name, m.center_type, b.portion_analyte, p.platform_alias) bcr
	ON    centers.center_name = bcr.center_name
	AND   centers.center_type = bcr.center_type
	AND   centers.portion_analyte = bcr.portion_analyte
	AND   centers.tumor_abbreviation = bcr.tumor_abbreviation
	ORDER by tumor_abbreviation,center_name,center_Type,portion_analyte;


	-- calculate unaccounted for, both bcr and centers
	UPDATE sample_summary_report_detail
	SET total_bcr_unaccounted = total_centers_sent - total_bcr_sent,
	    total_center_unaccounted = 0
	WHERE total_bcr_sent < total_centers_sent  ;

	UPDATE sample_summary_report_detail
	SET total_center_unaccounted = total_bcr_sent - total_centers_sent ,
	    total_bcr_unaccounted = 0
	WHERE total_bcr_sent > total_centers_sent  ;  
	
       UPDATE sample_summary_report_detail
	SET total_bcr_unaccounted = total_centers_sent, 
	    total_center_unaccounted = 0
	WHERE total_bcr_sent is NULL 
	AND   total_centers_sent > 0 ;

	UPDATE sample_summary_report_detail
	SET total_center_unaccounted = total_bcr_sent, 
	    total_bcr_unaccounted = 0
	WHERE total_centers_sent IS NULL 
	AND   total_bcr_sent > 0;  
	
	-- update total samples with level 1, 2 and 3 data

	UPDATE sample_summary_report_detail s
	SET   total_with_level1 =
	(SELECT sampleCount
	 FROM sample_level_count l
	 WHERE l.tumor_abbreviation = s.tumor_abbreviation
	 AND   l.center_name = s.center_name
	 AND   l.center_type = s.center_type
	 AND   l.portion_analyte = s.portion_analyte
	 AND   l.platform = s.platform
	 AND   l.level = 1);
	 
	UPDATE sample_summary_report_detail s
	SET   total_with_level2 =
	(SELECT sampleCount
	 FROM sample_level_count l
	 WHERE l.tumor_abbreviation = s.tumor_abbreviation
	 AND   l.center_name = s.center_name
	 AND   l.center_type = s.center_type
	 AND   l.portion_analyte = s.portion_analyte
	 AND   l.platform = s.platform
	 AND   l.level = 2);

	UPDATE sample_summary_report_detail s
	SET   total_with_level3 =
	(SELECT sampleCount
	 FROM sample_level_count l
	 WHERE l.tumor_abbreviation = s.tumor_abbreviation
	 AND   l.center_name = s.center_name
	 AND   l.center_type = s.center_type
	 AND   l.portion_analyte = s.portion_analyte
	 AND   l.platform = s.platform
	 AND   l.level = 3);

	-- now figure out which CGCC centers and platform sent level 4 data (whether in latest archives or not) and
	-- update the field in the sample_summary_report_detail. Since there could be level4 data in latest and not latest
	-- archives; postgres will not allow an order by in the subquery, so do two updates. (erghh) DREP-16 bug filed.
	 
	UPDATE ONLY sample_summary_report_detail s
	SET   level_4_submitted = 'Y*' 
	 FROM file_data_level l, file_info f, tumor_info t, archive_info a , center_info c, platform_info p
	 WHERE a.is_latest = 0
	 AND   a.tumor_id = t.id
	 AND   a.platform_id = p.id
	 AND   a.center_id = c.id
	 AND   a.id = f.file_archive_id  
	 AND   f.id = l.file_info_id  
	 AND   l.level_number = 4
	 AND c.center_name = s.center_name 
	 AND  p.platform_alias = s.platform
	 AND  s.center_type = 'CGCC';
	 
	UPDATE ONLY sample_summary_report_detail s
	SET   level_4_submitted = 'Y' 
	 FROM file_data_level l, file_info f, tumor_info t, archive_info a , center_info c, platform_info p
	 WHERE a.is_latest = 1
	 AND   a.tumor_id = t.id
	 AND   a.platform_id = p.id
	 AND   a.center_id = c.id
	 AND   a.id = f.file_archive_id  
	 AND   f.id = l.file_info_id  
	 AND   l.level_number = 4
	 AND c.center_name = s.center_name 
	 AND  p.platform_alias = s.platform
	 AND  s.center_type = 'CGCC';

	UPDATE sample_summary_report_detail
	SET total_with_level3 = 0
	WHERE total_with_level3 IS NULL;
	
	UPDATE sample_summary_report_detail
	SET total_with_level2 = 0
	WHERE total_with_level2 IS NULL;

	UPDATE sample_summary_report_detail
	SET total_with_level1 = 0
	WHERE total_with_level1 IS NULL;
	
	UPDATE sample_summary_report_detail
	SET total_bcr_sent = 0
	WHERE total_bcr_sent IS NULL;

	UPDATE sample_summary_report_detail
	SET total_centers_sent = 0
	WHERE total_centers_sent IS NULL;

	
	UPDATE sample_summary_report_detail SET last_refresh = CURRENT_TIMESTAMP;

$BODY$
  LANGUAGE 'sql' VOLATILE;