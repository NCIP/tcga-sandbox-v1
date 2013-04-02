	SELECT v.yearMonth,count(sample) as samples
	FROM
	(SELECT to_char(date_added,'YYYY/MM') as yearMonth, bb.sample
	FROM archive_info a, file_info f,file_data_level l,
	      biospecimen_gsc_file_mv bt, biospecimen_breakdown_all bb 
	WHERE a.id = f.file_archive_id
	AND   f.id = bt.file_info_id
	and   f.id = l.file_info_id
	and   l.level_number = 1
	AND   bt.biospecimen_id = bb.biospecimen_id
	UNION
	SELECT to_char(date_added,'YYYY/MM') as yearMonth, bb.sample
	FROM archive_info a, file_info f, file_data_level l,
	      biospecimen_to_file bf, biospecimen_breakdown_all bb 
	WHERE a.id = f.file_archive_id
	AND   f.id = bf.file_info_id
	and   f.id = l.file_info_id
	and   l.level_number = 1
	AND   bf.biospecimen_id = bb.biospecimen_id) v
	GROUP BY v.yearMonth;
