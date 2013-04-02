CREATE OR REPLACE PACKAGE build_portal_action_summary
AS
    PROCEDURE get_summary_details;
    FUNCTION build_select(disease_abbreviation VARCHAR2)
    RETURN VARCHAR2;
    FUNCTION build_filter_select(disease_abbreviation VARCHAR2)
    RETURN VARCHAR2 ;
    FUNCTION build_update_statement(disease_abbreviation VARCHAR2)
    RETURN VARCHAR2;
END;
/
CREATE OR REPLACE PACKAGE BODY build_portal_action_summary
IS
    /*
    ** This package contains a stored procedure and functions to build dynamic sql to select and summarize data from the portal session log tables
    ** in each disease schema and put the summarized data into the portal_action_summary table in the dccCommon schema. This table is used
    ** for the Data Statistics Dashboard.
    **
    ** Written by Shelley Alonso
    **
    ** Revision History
    **
    ** Shelley Alonso   01/12/2011  Change the varchar field from varchar(10) to varchar(20) in the xmltable function for 
    **                              portal_action_type 14 (batch filter applied) so that the 'Unavailable' batch is not truncated.
    ** Shelley Alonso   01/13/2011  Add regexp_replace to strip out any not digit characters, except commas, in the values returned
    **                              for portal_action_type 17 (dataType filter applied) to get around the DAM bug that allows garbage in
    **                              APPS-2639; also add header to this file.
    ** Shelley Alonso   01/25/2011  Add qualifier for disease select to only get active diseases 
    */

    PROCEDURE get_summary_details
    IS
        CURSOR dCur IS 
        SELECT disease_id, disease_abbreviation
        FROM disease 
        WHERE active=1
        order by disease_id;
        sqlString       VARCHAR2(4000);
        insertStatement VARCHAR2(300);
    BEGIN
        EXECUTE IMMEDIATE ('TRUNCATE TABLE portal_action_summary');
        insertStatement := 'INSERT INTO portal_action_summary (disease_abbreviation,monthyear,action_type_id,action,selection,action_total,user_count,totalUserCount) ';
        FOR diseaseRec IN dCur LOOP
       
            sqlString := insertStatement||build_select(diseaseRec.disease_abbreviation);
            --dbms_output.put_line(sqlString);
            EXECUTE IMMEDIATE sqlString;
            sqlString := insertStatement||build_filter_select(diseaseRec.disease_abbreviation);
            --dbms_output.put_line(sqlString);
            EXECUTE IMMEDIATE sqlString;
            sqlString := build_update_statement(diseaseRec.disease_abbreviation);
            --dbms_output.put_line(sqlString);
            EXECUTE IMMEDIATE sqlString;
            commit;                  
       
        END LOOP;
    END get_summary_details;
    /*
    ** this function will build a select query to summarize by month/year, the total archive downloads, the total users downloading archives, and the 
    ** total size of archives downloaded
    */        
    FUNCTION build_select(disease_abbreviation VARCHAR2)
    RETURN VARCHAR2
    IS
        selectStatement VARCHAR2(4000);
        selectReplace   VARCHAR2(4000);
    BEGIN
        selectReplace := 'SELECT ''REPLACE_DISEASE'',TO_CHAR(v.monthyr,''MM/YYYY'') monthyr, v.type_id,v.action,v.selection,v.actionTotal ,v.usercount, 0 FROM ('||
        ' SELECT TRUNC(action_time,''MM'') as monthyr,pt.portal_action_type_id as type_id,pt.name as action,'''' as selection,count(*) as actionTotal, count(distinct portal_session_id) as usercount FROM '||
        ' TCGAREPLACE_DISEASE.portal_session_action pa, TCGAREPLACE_DISEASE.portal_action_type pt '||
        ' WHERE pa.portal_action_type_id=pt.portal_action_type_id AND   pt.portal_action_type_id=34 GROUP BY TRUNC(action_time,''MM''),pt.portal_action_type_id,pt.name, '''' UNION '||
        ' SELECT TRUNC(action_time,''MM'') as monthyr,pt.portal_action_type_id as type_id,pt.name as action,'''' as selection,sum(value) as actionTotal, count(distinct portal_session_id) as usercount FROM '||
        ' TCGAREPLACE_DISEASE.portal_session_action pa, TCGAREPLACE_DISEASE.portal_action_type pt '||
        ' WHERE pa.portal_action_type_id=pt.portal_action_type_id AND pt.portal_action_type_id=36 GROUP BY TRUNC(action_time,''MM''),pt.portal_action_type_id,pt.name,'''') v';
 
        selectStatement := replace(selectReplace,'REPLACE_DISEASE',disease_abbreviation);
        
        RETURN selectStatement;
    END build_select;
    /*
    ** this function will build a select query to summarize by month/year, the total times certain filters were used and what they were,
    ** the total users selecting each one
    */    
    FUNCTION build_filter_select(disease_abbreviation VARCHAR2)
    RETURN VARCHAR2
    IS
        selectStatement VARCHAR2(4000);
        selectReplace    VARCHAR2(4000);
    BEGIN
      /*
      ** The portal session logger puts all selection values for filters in square brackets for some reason, so the first thing done is to strip off the brackets using
      ** a substring function on the portal_session_action.value field. If the user has selected more than one value for the selected filter (for instance 3 data types) 
      ** the portal session logger will log them as one string with the values comma-seperated. So we pass the value string, now called minus_bracket, to
      ** an xmltable function which returns us a table of the values that were comma delimited.
      */
      selectReplace := 'SELECT ''REPLACE_DISEASE'',TO_CHAR(monthyr,''MM/YYYY'') monthyr,type_id,action,selection,actionTotal ,usercount, 0 FROM ('||
      'SELECT v.monthyr, v.type_id,v.action, dt.name as selection,count(*) as actiontotal,count(distinct portal_session_id) as usercount FROM '||
      ' (select TRUNC(action_time,''MM'') as monthyr,pt.portal_action_type_id as type_id,pt.name as action,regexp_replace(pa.value, ''[^0-9,]+'', '''') as minus_bracket,portal_session_id FROM '||
      ' TCGAREPLACE_DISEASE.portal_session_action pa, TCGAREPLACE_DISEASE.portal_action_type pt '||
      ' WHERE pa.portal_action_type_id=pt.portal_action_type_id AND   pt.portal_action_type_id =17) v,'||
      'xmltable(''r/c'' passing xmltype(''<r><c>'' || replace(v.minus_bracket,'','',''</c><c>'') || ''</c></r>'') columns dtype_id number(10,0) path ''.'') x, TCGAREPLACE_DISEASE.data_type dt '||
      ' WHERE x.dtype_id=dt.data_type_id group by monthyr,v.type_id,action,dt.name UNION '||
      ' SELECT v.monthyr, v.type_id,v.action, decode(trim(x.protect_stat),''P'',''Protected'',''Public'') as selection,count(*) as actiontotal,count(distinct portal_session_id) as usercount FROM '||
      '(select TRUNC(action_time,''MM'') as monthyr,pt.portal_action_type_id as type_id,pt.name as action,replace(substr(pa.value,2,length(pa.value)-2),'')'','''') as minus_bracket,portal_session_id FROM '||
      ' TCGAREPLACE_DISEASE.portal_session_action pa, TCGAREPLACE_DISEASE.portal_action_type pt '||
      ' WHERE pa.portal_action_type_id=pt.portal_action_type_id AND   pt.portal_action_type_id =18) v,xmltable(''r/c'' passing xmltype(''<r><c>'' || replace(v.minus_bracket,'','',''</c><c>'') || ''</c></r>'')'||
      ' columns protect_stat varchar2(20) path ''.'') x group by monthyr,v.type_id,action,trim(x.protect_stat) UNION '||
      ' SELECT v.monthyr, v.type_id,v.action, trim(x.batchnbr) as selection,count(*) as actiontotal,count(distinct portal_session_id) as usercount FROM '||
      ' (select TRUNC(action_time,''MM'') as monthyr,pt.portal_action_type_id as type_id,pt.name as action,replace(substr(pa.value,2,length(pa.value)-2),'')'','''') as minus_bracket,portal_session_id FROM '||
      ' TCGAREPLACE_DISEASE.portal_session_action pa, TCGAREPLACE_DISEASE.portal_action_type pt '||
      ' WHERE pa.portal_action_type_id=pt.portal_action_type_id AND   pt.portal_action_type_id =14) v, xmltable(''r/c'' passing xmltype(''<r><c>'' || replace(v.minus_bracket,'','',''</c><c>'') || ''</c></r>'')'||
      ' columns batchnbr varchar2(20) path ''.'') x group by monthyr,v.type_id,action,trim(x.batchnbr) UNION '||
      ' SELECT v.monthyr, v.type_id,v.action, ''Level ''||trim(x.levelnum) as selection,count(*) as actiontotal,count(distinct portal_session_id) as usercount FROM '||
      ' (select TRUNC(action_time,''MM'') as monthyr,pt.portal_action_type_id as type_id,pt.name as action,replace(substr(pa.value,2,length(pa.value)-2),'')'','''') as minus_bracket,portal_session_id FROM '||
      ' TCGAREPLACE_DISEASE.portal_session_action pa, TCGAREPLACE_DISEASE.portal_action_type pt '||
      ' WHERE pa.portal_action_type_id=pt.portal_action_type_id AND   pt.portal_action_type_id =16) v, xmltable(''r/c'' passing xmltype(''<r><c>'' || replace(v.minus_bracket,'','',''</c><c>'') || ''</c></r>'')'||
      ' columns levelnum char(2) path ''.'') x group by monthyr,v.type_id,action,''Level ''||trim(x.levelnum))';
      
      selectStatement := REPLACE(selectReplace,'REPLACE_DISEASE',disease_abbreviation);
      RETURN selectStatement; 
    END build_filter_select;
    /*
    ** this function will build a query to summarize by month/year the total number of sessions, and update the totalusercount in the
    ** portal_action_summary table for the disease
    */     
    FUNCTION build_update_statement(disease_abbreviation VARCHAR2)
    RETURN VARCHAR2
    IS
        dSchema         VARCHAR2(10);
        updateStatement VARCHAR2(4000);
     BEGIN
      dSchema := 'TCGA'||disease_abbreviation;
      updateStatement :=  'merge into portal_action_summary p USING (select TO_CHAR(created_on,''MM/YYYY'') monthyr, count(portal_session_id) as totalUserCount , '||
      ''''||disease_abbreviation||''''||' as disease_abbreviation from '||dschema||'.portal_session GROUP BY TO_CHAR(created_on,''MM/YYYY'')) sub'||
      ' on (p.monthyear=sub.monthyr and p.disease_abbreviation=sub.disease_abbreviation) when matched then update set totalusercount = sub.totalusercount';      
      RETURN updateStatement; 
    END build_update_statement;
END;
/
