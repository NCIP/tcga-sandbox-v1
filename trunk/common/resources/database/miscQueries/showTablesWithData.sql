Set serveroutput on
declare
type t is ref cursor;
c t;
dummy integer;
begin
  for f in (select table_name from user_tables) loop
     open c for 'select 1 from '||f.table_name;
     fetch c into dummy;
     if c%found then
         dbms_output.put_line(f.table_name);
     end if;
     close c;
   end loop;
end;