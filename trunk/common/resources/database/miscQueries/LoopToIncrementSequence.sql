declare i number(38);
begin
for j in 1..200 loop

 select annotation_item_category_seq.nextval into i from dual;
end loop;
end;