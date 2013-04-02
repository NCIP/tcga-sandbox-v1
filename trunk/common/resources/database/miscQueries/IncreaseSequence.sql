declare hold number(38);
BEGin
for cntr in 1..500 loop
  select clinical_seq.nextval into hold from dual;
end loop;
end;