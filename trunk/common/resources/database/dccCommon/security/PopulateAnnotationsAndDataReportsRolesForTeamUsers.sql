declare
  group_id_1 number;
  group_id_2 number;
  group_id_3 number;
begin

delete from group_members;
delete from group_authorities;
delete from groups;
delete from users;

-- USERS

---- DEVS
insert into users (username, password, enabled) values ('alonsos', 'alonsos', 1);
insert into users (username, password, enabled) values ('baboudj', 'baboudj', 1);
insert into users (username, password, enabled) values ('bertondl', 'bertondl', 1);
insert into users (username, password, enabled) values ('girshiks', 'girshiks', 1);
insert into users (username, password, enabled) values ('kigonyapa', 'kigonyapa', 1);
insert into users (username, password, enabled) values ('nichollsmc', 'nichollsmc', 1);
insert into users (username, password, enabled) values ('ramanr', 'ramanr', 1);
insert into users (username, password, enabled) values ('srinivasand', 'srinivasand',1);
insert into users (username, password, enabled) values ('waltonj', 'waltonj', 1);

---- QA
insert into users (username, password, enabled) values ('barlettasp', 'barlettasp', 1);
insert into users (username, password, enabled) values ('sanbhadtirn', 'sanbhadtirn', 1);

---- BINFS
insert into users (username, password, enabled) values ('arik', 'arik', 1);
insert into users (username, password, enabled) values ('ayalabe', 'ayalabe', 1);
insert into users (username, password, enabled) values ('chual', 'chual', 1);
insert into users (username, password, enabled) values ('jensenma', 'jensenma', 1);
insert into users (username, password, enabled) values ('kothiyalp', 'kothiyalp', 1);
insert into users (username, password, enabled) values ('pihltd', 'pihltd', 1);
insert into users (username, password, enabled) values ('pontiusj', 'pontiusj', 1);
insert into users (username, password, enabled) values ('snyderee', 'snyderee', 1);
insert into users (username, password, enabled) values ('wangz', 'wangz', 1);
insert into users (username, password, enabled) values ('robesontn', 'robesontn', 1);

---- OTHERS
insert into users (username, password, enabled) values ('hadfielj', 'hadfielj', 1);

---- CBIIT
insert into users (username, password, enabled) values ('cbiitappscan', 'cbiitappscan', 1);

commit;

-- GROUPS
insert into groups (group_name) values ('AnnotationsUsers') returning id into group_id_1;
insert into groups (group_name) values ('AnnotationsAdmin') returning id into group_id_2;
insert into groups (group_name) values ('DataReports') returning id into group_id_3;

-- GROUP_AUTHORITIES
insert into group_authorities(group_id, authority) values(group_id_1, 'ROLE_ANNOTATION_CREATOR');
insert into group_authorities(group_id, authority) values(group_id_1, 'ROLE_ANNOTATION_EDITOR');
insert into group_authorities(group_id, authority) values(group_id_1, 'ROLE_ANNOTATION_ITEM_CREATOR');
insert into group_authorities(group_id, authority) values(group_id_1, 'ROLE_ANNOTATION_NOTE_EDITOR');

insert into group_authorities(group_id, authority) values(group_id_2, 'ROLE_ANNOTATIONS_ADMINISTRATOR');
insert into group_authorities(group_id, authority) values(group_id_2, 'ROLE_ANNOTATION_CREATOR');
insert into group_authorities(group_id, authority) values(group_id_2, 'ROLE_ANNOTATION_EDITOR');
insert into group_authorities(group_id, authority) values(group_id_2, 'ROLE_ANNOTATION_ITEM_CREATOR');
insert into group_authorities(group_id, authority) values(group_id_2, 'ROLE_ANNOTATION_NOTE_EDITOR');

insert into group_authorities(group_id, authority) values(group_id_3, 'ROLE_PENDING_METADATA_WS_USER');

-- GROUP_MEMBERS

---- AnnotationsUsers
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'alonsos', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'arik', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'ayalabe', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'baboudj', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'bertondl', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'chual', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'girshiks', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'hadfielj', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'kothiyalp', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'nichollsmc', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'pihltd', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'pontiusj', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'ramanr', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'sanbhadtirn', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'srinivasand', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'wangz', group_id_1);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'robesontn', group_id_1);

---- AnnotationsAdmin
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'barlettasp', group_id_2);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'cbiitappscan', group_id_2);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'jensenma', group_id_2);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'kigonyapa', group_id_2);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'snyderee', group_id_2);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'waltonj', group_id_2);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'alonsos', group_id_2);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'robesontn', group_id_2);

---- DataReports
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'alonsos', group_id_3);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'baboudj', group_id_3);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'barlettasp', group_id_3);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'bertondl', group_id_3);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'girshiks', group_id_3);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'kigonyapa', group_id_3);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'nichollsmc', group_id_3);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'ramanr', group_id_3);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'sanbhadtirn', group_id_3);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'srinivasand', group_id_3);
insert into group_members (id, username, group_id) values (GROUP_MEMBERS_SEQ.NEXTVAL, 'waltonj', group_id_3);

commit;
end;