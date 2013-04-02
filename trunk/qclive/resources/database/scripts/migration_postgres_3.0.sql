alter table archive_info add column archive_type_id integer;

create table archive_type ( id integer not null, type varchar(50) not null, data_level int);

alter table archive_type add primary key (id);

alter table archive_info add constraint archiveinfotype_fk foreign key (archive_type_id) references archive_type(id);

alter table archive_type add constraint archivetypelevel_fk foreign key (data_level) references data_level(level_number);

insert into archive_type(id, type, data_level) values(1, 'Level_1', 1);

insert into archive_type(id, type, data_level) values(2, 'Level_2', 2);

insert into archive_type(id, type, data_level) values(3, 'Level_3', 3);

insert into archive_type(id, type, data_level) values(4, 'Level_4', 4);

insert into archive_type(id, type) values(5, 'aux');

insert into archive_type(id, type) values(6, 'mage-tab');

insert into archive_type(id, type) values(7, 'classic');

alter table archive_info add column initial_size_kb bigint;

alter table archive_info add column final_size_kb bigint;

update archive_info set archive_type_id=7 where archive_type_id is null;

alter table archive_info add column is_latest_loaded integer;

update archive_info set is_latest_loaded = 1 where is_latest=1;

update archive_info set is_latest_loaded = 0 where is_latest=0;