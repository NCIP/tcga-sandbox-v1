DROP TABLE batch_number_assignment ;
CREATE TABLE batch_number_assignment 
(
   batch_id      integer   not null,
   tumor_id      integer   not null,
   bcr_center_id integer  not null
);

ALTER TABLE batch_number_assignment ADD 
CONSTRAINT fk_batch_tumor_id
FOREIGN KEY (tumor_id)
REFERENCES tumor_info(id);
ALTER TABLE batch_number_assignment ADD 
CONSTRAINT fk_batch_center_id
FOREIGN KEY (bcr_center_id)
REFERENCES center_info(id);

insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(1,1,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(2,1,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(3,1,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(4,1,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(5,1,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(6,1,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(7,1,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(8,1,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(9,3,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(10,1,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(11,3,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(12,3,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(13,3,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(14,3,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(15,3,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(16,1,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(17,3,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(18,3,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(19,3,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(20,1,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(21,3,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(22,3,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(23,2,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(24,3,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(25,13,10);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(26,1,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(28,6,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(29,6,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(30,6,11);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(31,2,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(32,8,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(33,6,11);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(34,4,10);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(35,6,10);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(36,6,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(37,4,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(38,1,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(39,2,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(40,3,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(41,6,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(42,24,1);
insert into batch_number_assignment (batch_id,tumor_id,bcr_center_id) values(43,24,11);
