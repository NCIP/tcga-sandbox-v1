ALTER SESSION FORCE PARALLEL DDL;
/
CREATE UNIQUE INDEX pk_l4_anomaly_value_idx ON 
l4_anomaly_value (anomaly_value_id) GLOBAL
PARTITION BY HASH (anomaly_value_id); 
/
ALTER TABLE l4_anomaly_value ADD CONSTRAINT pk_l4anomaly_value_id
PRIMARY KEY (anomaly_value_id)
USING INDEX pk_l4_anomaly_value_idx;
/