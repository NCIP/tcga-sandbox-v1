WHENEVER SQLERROR EXIT ;
/*
** remove unused column in hybridization_data_group
*/
ALTER TABLE hybridization_data_group DROP COLUMN data_level
;
/*
** alter data set table to add center_id and platform_id
*/
ALTER TABLE data_set ADD (center_id NUMBER(38), platform_id NUMBER(38));

UPDATE data_set d
SET (d.center_id, d.platform_id) =
(SELECT cp.center_id, cp.platform_id
 FROM experiment e, center_platform cp
 WHERE e.center_platform_id = cp.center_platform_id
 AND   e.experiment_id = d.experiment_id);

 COMMIT;
/*
** create indexes on new columns to support the FK constraints to be defined
*/
CREATE INDEX dataset_center_idx ON data_set(center_id);
CREATE INDEX dataset_platform_idx ON data_set (platform_id);
/*
** add foreign key constraints
*/
ALTER TABLE data_set ADD (
   CONSTRAINT FK_dataset_center
   FOREIGN KEY (center_id)
   REFERENCES center(center_id),
   CONSTRAINT fk_dataset_platform (platform_id)
   REFERENCES platform(platform_id)
);
ALTER TABLE data_set MODIFY (center_id NOT NULL, platform_id NOT NULL);

/*
** add center_id and platform_id to experiment table and get rid of unnecessary relationship
** to intermiate table, center_platform
*/
ALTER TABLE experiment ADD (center_id NUMBER(38), platform_id NUMBER(38));

/*
** set values for center_id and platform_id
*/
UPDATE experiment e
SET center_id =
(SELECT cp.center_id
 FROM center_platform cp
 WHERE cp.center_platform_id = e.center_platform_id);

COMMIT;

UPDATE experiment e
SET platform_id =
(SELECT cp.platform_id
 FROM center_platform cp
 WHERE cp.center_platform_id = e.center_platform_id);

COMMIT;
ALTER TABLE experiment MODIFY (center_id NOT NULL, platform_id NOT NULL);

ALTER TABLE experiment DROP COLUMN center_platform_id;

/*
** add fk constraints on center_id and platform_id
*/
ALTER TABLE experiment ADD (
   CONSTRAINT fk_experiment_center
   FOREIGN KEY (center_id)
   REFERENCES center(center_id),
   CONSTRAINT fk_experiment_platform
   FOREIGN KEY (platform_id)
   REFERENCES platform(platform_id)
);
/*
** create indexes to support foreign keys
*/
CREATE INDEX experiment_platform_idx ON experiment(platform_id);

CREATE INDEX experiment_center_idx ON experiment(center_id);

/* 
** add new fields to data_set_v view
*/
CREATE OR REPLACE FORCE VIEW DATA_SET_V
(
   DATA_SET_ID,
   EXPERIMENT_ID,
   SOURCE_FILE_NAME,
   SOURCE_FILE_TYPE,
   ACCESS_LEVEL,
   DATA_LEVEL,
   platform_id,
   center_id
)
AS
   SELECT   data_set_id,
            experiment_id,
            source_file_name,
            source_file_type,
            access_level,
            data_level,
            platform_id,
            center_id
     FROM   data_set
    WHERE   use_in_dam = 1 AND load_complete = 1;