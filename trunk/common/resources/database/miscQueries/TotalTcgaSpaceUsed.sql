set echo off
set pagesize 60
set linesize 110
set feedback off

select sysdate "DATE" from dual;

set feedback on

col sname format a30 heading 'Tablespace' justify c
col ubytes format 999,999,999.99 heading 'Mb|Used' justify c
col dummy noprint
compute sum of ubytes 

WITH
total as
(select tablespace_name sname, sum(bytes) bytes, sum(maxbytes) maxbytes
  from sys.dba_data_files
  WHERE tablespace_name like 'TCGA%' or tablespace_name = 'DCCCOMMON'
  group by tablespace_name) ,
free as
(select tablespace_name sname, sum(bytes) bytes
 from user_free_space
 group by tablespace_name)
select
 total.sname,
 (total.bytes - free.bytes)/1048576 ubytes, tot.total_mb
from total,free,(select sum(total.bytes - free.bytes)/1048576 total_mb from total, free where total.sname=free.sname)tot 
where total.sname = free.sname
/