set echo off
set pagesize 60
set linesize 110
set feedback off

select sysdate "DATE" from dual;

set feedback on

col sname format a30 heading 'Tablespace' justify c
col msize format 9,999,999.99 heading 'Mb|Max Size' justify c
col ssize format 9,999,999.99 heading 'Mb|Allocated' justify c
col ubytes format 9,999,999.99 heading 'Mb|Used' justify c
col fbytes format 99,999,999.99 heading 'Mb|Free' justify c
col pct format 990 heading 'Percent|Used' justify c

select
 total.tablespace_name sname,
 (total.maxbytes/1048576) msize,
 (total.bytes/1048576) ssize,
 ((total.bytes - sum(free.bytes))/1048576) ubytes,
 ((sum(free.bytes))/1048576) fbytes,
 round(((total.bytes - sum(free.bytes))/total.maxbytes) * 100) pct
from user_free_space free, (select tablespace_name, sum(bytes) bytes, sum(maxbytes) maxbytes
                                             from sys.dba_data_files
                                             group by tablespace_name) total
where total.tablespace_name = free.tablespace_name
group by total.tablespace_name, total.bytes, total.maxbytes
/

