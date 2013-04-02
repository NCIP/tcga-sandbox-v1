alter table center add requires_magetab number(1) default 0;

update center set requires_magetab=1 where center_type_code='CGCC';