ALTER TABLE center_bcr_center_map ADD center_type VARCHAR(25);
UPDATE center_bcr_center_map m SET center_type = (select c.center_type from center_info c where c.id = m.center_id);
UPDATE center_bcr_center_map SET center_type = 'GSC' where bcr_center_id = '08';

CREATE OR REPLACE VIEW biospecimen_breakdown_all AS 
 SELECT b.biospecimen_id, 
        b.barcode, 
        b.project || '-' || b.collection_center || '-' || b.barcode || '-' || b.sample_type || b.sample_sequence || '-' || b.portion_sequence || b.portion_analyte AS biospecimen, 
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

CREATE SEQUENCE report_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
CREATE TABLE sample_level_count
(
  sample_level_count_id integer NOT NULL DEFAULT nextval('report_seq'::regclass),
  tumor_abbreviation character varying(2000),
  center_name character varying(2000),
  center_type character varying(25),
  portion_analyte character varying(10),
  platform character varying(2000),
  level integer,
  sampleCount bigint,
  CONSTRAINT sample_level_count_pkey PRIMARY KEY (sample_level_count_id)
);
CREATE TABLE samples_sent_by_bcr
(
     sample_sent_id integer NOT NULL DEFAULT nextval('report_seq'::regclass),
     tumor_abbreviation character varying(2000),
     center_id integer,
     center_name character varying(2000),
     center_type character varying(25),
     sample character varying(20),
     portion_analyte character varying(10),
     CONSTRAINT sample_sent_bcr_pkey PRIMARY KEY (sample_sent_id)
);

CREATE TABLE latest_samples_received_by_dcc
(
  samples_received_id integer NOT NULL DEFAULT nextval('report_seq'::regclass),
  tumor_abbreviation character varying(2000),
  center_id integer,
  center_name character varying(2000),
  center_type character varying(25),
  platform character varying(2000),
  sample character varying(20),
  portion_analyte character varying(10),
  is_valid integer,
  is_viewable integer,
  CONSTRAINT samples_received_pkey PRIMARY KEY (samples_received_id)
);

CREATE TABLE sample_summary_report_detail (
  sample_summary_id integer NOT NULL DEFAULT nextval('report_seq'::regclass),
  tumor_abbreviation character varying(2000),
  center_name character varying(2000),
  center_type character varying(25),
  portion_analyte character varying(10),
  platform character varying(2000),
  total_bcr_sent bigint ,
  total_centers_sent bigint,
  total_bcr_unaccounted bigint,
  total_center_unaccounted bigint,
  total_with_level1 bigint,
  total_with_level2 bigint ,
  total_with_level3 bigint ,
  level_4_submitted char(2),
  last_refresh timestamp,
  CONSTRAINT samples_summary_report_pkey PRIMARY KEY (sample_summary_id));

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
	AND   a.is_latest = 1  
	AND   a.deploy_status = 'Available' 
	GROUP BY c.center_name, m.center_type, bb.portion_analyte, platform_alias, t.tumor_abbreviation,l.level_number;

	/*
	** Get all the samples the centers sent; path to GSC centers is thru biospecimen_gsc_file_mv,
	** and the path to CGCC is thru biospecimen_to_file
	*/
	TRUNCATE TABLE latest_samples_received_by_dcc;
	INSERT INTO latest_samples_received_by_dcc
	(tumor_abbreviation, center_id, center_name, center_type, platform, sample, portion_analyte, is_valid, is_viewable)
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

	/*
	** Get latest samples sent out by the BCR and store them in an intermediate table so that this join
	** does not have to be done over and over
	*/
	TRUNCATE TABLE samples_sent_by_bcr;
	INSERT INTO samples_sent_by_bcr
	(tumor_abbreviation,center_id,center_name,center_type,sample,portion_analyte) 
	SELECT distinct t.tumor_abbreviation,c.id as center_id,c.center_name,m.center_type,b.sample,b.portion_analyte
	FROM   biospecimen_breakdown_all b, biospecimen_to_archive ba, Archive_info a, tumor_info t, 
	       center_info c, center_bcr_center_map m  
	WHERE c.id=m.center_id  
	AND m.bcr_center_id = b.bcr_center_id
	AND b.biospecimen_id = ba.biospecimen_id 
	AND ba.archive_id = a.id 
	AND a.tumor_id = t.id 
	AND a.is_latest = 1
	AND b.is_valid=1
	AND b.is_viewable=1
	AND a.deploy_status = 'Available';

	DELETE FROM sample_summary_report_detail;
	/*
	** Do the initial insert into the sample_summary_report_detail table, getting totals for 
	** samples dcc received from the centers and samples bcr sent to the centers. We need to get all the 
	** center samples whether there is a match in BCR samples, and all the BCR samples whether or not there 
	** is a match in center samples. That is why there are 2 queries that look almost the same, UNION'ed to
	** exclude duplicates.
	*/
	INSERT INTO sample_summary_report_detail (tumor_abbreviation,center_name,center_type,portion_analyte,platform,
	                                        total_bcr_sent,total_centers_sent)
	SELECT bcr.tumor_abbreviation, bcr.center_name, bcr.center_type, bcr.portion_analyte, centers.platform, bcr.samples_sent, centers.samples_dcc_received
	FROM
	(SELECT tumor_abbreviation, COALESCE(count(distinct sample), 0) as samples_dcc_received, center_name, center_type, portion_analyte, platform
	 from latest_samples_received_by_dcc
	 GROUP BY tumor_abbreviation, center_name,center_type,portion_analyte, platform) centers RIGHT OUTER JOIN
	(SELECT tumor_abbreviation,count(distinct sample) as samples_sent, center_name, center_type, portion_analyte
	 from samples_sent_by_bcr  
	 GROUP BY tumor_abbreviation,center_name, center_type, portion_analyte) bcr
	ON    centers.center_name = bcr.center_name
	AND   centers.center_type = bcr.center_type
	AND   centers.portion_analyte = bcr.portion_analyte
	AND   centers.tumor_abbreviation = bcr.tumor_abbreviation
	UNION
	SELECT centers.tumor_abbreviation, centers.center_name, centers.center_type, centers.portion_analyte, centers.platform, bcr.samples_sent, centers.samples_dcc_received 
	FROM
	(SELECT tumor_abbreviation,COALESCE(count(distinct sample),0) as samples_dcc_received, center_name, center_type, portion_analyte, platform
	 from latest_samples_received_by_dcc
	 GROUP BY tumor_abbreviation, center_name,center_type,portion_analyte, platform) centers LEFT OUTER JOIN
	(SELECT tumor_abbreviation,count(distinct sample) as samples_sent, center_name, center_type, portion_analyte
	 from samples_sent_by_bcr  
	 GROUP BY tumor_abbreviation,center_name, center_type, portion_analyte) bcr
	ON    centers.center_name = bcr.center_name
	AND   centers.center_type = bcr.center_type
	AND   centers.portion_analyte = bcr.portion_analyte
	AND   centers.tumor_abbreviation = bcr.tumor_abbreviation
	ORDER by tumor_abbreviation,center_name,center_Type,portion_analyte;

	/*
	** calculate unaccounted for, both bcr and centers
	*/
	
	/*
	** centers unaccounted for would be samples the bcr sent that the center did not send
	*/
	UPDATE sample_summary_report_detail s
	SET    total_center_unaccounted =
	(SELECT  COALESCE(GREATEST(s.total_bcr_sent - v.samples, 0),0)
	 FROM
	(SELECT  COUNT(DISTINCT l.sample) as samples,l.center_name,l.tumor_abbreviation,l.center_type,l.portion_analyte,l.platform
	 FROM    latest_samples_received_by_dcc l, samples_sent_by_bcr bc
	 WHERE   l.sample = bc.sample
	 AND     l.tumor_abbreviation = bc.tumor_abbreviation
	 AND     l.center_name = bc.center_name
	 AND     l.center_type = bc.center_type
	 AND     l.portion_analyte = bc.portion_analyte
	 GROUP BY l.tumor_abbreviation,l.center_name,l.center_type,l.portion_analyte,l.platform) v
	 WHERE v.tumor_abbreviation = s.tumor_abbreviation
	 AND   v.center_name = s.center_name 
	 AND   v.center_type = s.center_type 
	 AND   v.portion_analyte = s.portion_analyte
	 AND   v.platform = s.platform) ;

	/*
	** bcr unaccounted for would be samples the centers sent that the bcr did not send
	*/

	UPDATE sample_summary_report_detail s
	SET    total_bcr_unaccounted =
	(SELECT COUNT(*) FROM 
	(SELECT  s.sample,l.center_name,l.tumor_abbreviation,l.center_type,l.portion_analyte,l.platform
	FROM    latest_samples_received_by_dcc l LEFT OUTER JOIN  
		samples_sent_by_bcr s ON l.sample = s.sample 
		AND l.tumor_abbreviation = s.tumor_abbreviation
		AND l.center_name = s.center_name 
		AND l.center_type = s.center_type 
		AND l.portion_analyte = s.portion_analyte) v
	WHERE v.sample IS NULL
	AND   v.tumor_abbreviation = s.tumor_abbreviation
	AND   v.center_name = s.center_name
	AND   v.center_type = s.center_type
	AND   v.portion_analyte = s.portion_analyte
	AND   v.platform = s.platform);	

	/* 
	**  now account for those that the above would not cover
	**     if total_bcr_sent > 0 and total_center_sent = 0 , then total_center_unaccounted = total_bcr_sent
	**     if total_center_sent > 0 and total_bcr_sent = 0 , then total_bcr_unaccounted = total_center_sent
	*/
	UPDATE sample_summary_report_detail
	SET    total_center_unaccounted = total_bcr_sent
	WHERE  (total_centers_sent = 0
	OR     total_centers_sent IS NULL)
	AND    total_bcr_sent > 0;
	
	UPDATE sample_summary_report_detail
	SET    total_bcr_unaccounted = total_centers_sent
	WHERE  (total_bcr_sent = 0
	OR     total_bcr_sent IS NULL)
	AND    total_centers_sent > 0;

	/*
	** set the level totals: how many samples had level 1, how many level 2 and how many level 3 data
	*/
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

	/*
	** now figure out which CGCC centers and platform sent level 4 data (whether in latest archives or not) and
	** update the field in the sample_summary_report_detail. Since there could be level4 data in latest and not latest
	** archives; postgres will not allow an order by in the subquery, so do two updates. (erghh) DREP-16 bug filed.
	*/
	
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
	 AND   t.tumor_abbreviation = s.tumor_abbreviation
	 AND   c.center_name = s.center_name 
	 AND   p.platform_alias = s.platform
	 AND   s.center_type = 'CGCC';
	 
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
	 AND   t.tumor_abbreviation = s.tumor_abbreviation
	 AND   c.center_name = s.center_name 
	 AND   p.platform_alias = s.platform
	 AND   s.center_type = 'CGCC';

	/*
	** set all numeric fields to zero if they are null for ease of display
	*/

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

	UPDATE sample_summary_report_detail
	SET    total_bcr_unaccounted = 0
	WHERE  total_bcr_unaccounted IS NULL;

	UPDATE sample_summary_report_detail
	SET    total_center_unaccounted = 0
	WHERE  total_center_unaccounted IS NULL;

	/*
	** set the last_refresh timestamp
	*/
	UPDATE sample_summary_report_detail SET last_refresh = CURRENT_TIMESTAMP;

$BODY$
  LANGUAGE 'sql' VOLATILE;