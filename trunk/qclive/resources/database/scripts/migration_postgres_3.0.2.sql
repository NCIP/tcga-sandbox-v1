create table tissue_to_tumor (
tumor_id integer NOT NULL REFERENCES tumor_info(id),
tissue_id integer NOT NULL REFERENCES tissue_info(id),
primary key (tumor_id, tissue_id)
);

insert into tissue_to_tumor(tumor_id, tissue_id) values(1, 1);
insert into tissue_to_tumor(tumor_id, tissue_id) values(1, 4);
insert into tissue_to_tumor(tumor_id, tissue_id) values(1, 0);
insert into tissue_to_tumor(tumor_id, tissue_id) values(2, 2);
insert into tissue_to_tumor(tumor_id, tissue_id) values(2, 4);
insert into tissue_to_tumor(tumor_id, tissue_id) values(2, 0);
insert into tissue_to_tumor(tumor_id, tissue_id) values(3, 3);
insert into tissue_to_tumor(tumor_id, tissue_id) values(3, 4);
insert into tissue_to_tumor(tumor_id, tissue_id) values(3, 0);

