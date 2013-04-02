declare 
  hold number(38);
  stmt varchar2(4000);
  cursor seqCur is select sequence_name from user_sequences;
Begin
for sRec in seqCur loop
    stmt := 'select '||sRec.sequence_name||'.nextval from dual';
    for cntr in 1..500 loop
        execute immediate stmt;
    end loop;
end loop;
end;