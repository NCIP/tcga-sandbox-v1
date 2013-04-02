alter table  latest_samples_received_by_dcc add column date_received timestamp without time zone;
alter table samples_sent_by_bcr add column ship_date date;