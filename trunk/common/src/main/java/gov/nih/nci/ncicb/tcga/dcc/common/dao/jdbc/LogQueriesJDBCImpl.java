/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Log;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.LogQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.web.LogQueryRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.OracleLobHandler;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Robert S. Sfeir
 * @author David Kane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class LogQueriesJDBCImpl extends BaseQueriesProcessor implements LogQueries {

    private static final String LOG = "process_log";
    private OracleLobHandler lobHandler;

    public void setLobHandler(final OracleLobHandler oracleLobHandler) {
        this.lobHandler = oracleLobHandler;
    }

    public List<Log> getLogInDateRange(LogQueryRequest queryParams) {
        List<Log> logEntries = new ArrayList<Log>();
        LogQueryByParameter logByParameterQuery = new LogQueryByParameter(getDataSource(), queryParams);
        logEntries.addAll(logByParameterQuery.execute());
        return logEntries;
    }

    public void updateLogEntry(final Log log) {
        String update = "update process_log set start_time=?, end_time=?, result_id=?, description=? where log_id=?";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        sjdbc.update(update, log.getStartTime(), log.getEndTime(), log.getResultId(), log.getDescription(), log.getId());
    }

    public Integer addLogEntry(final Log log) {
        if (log.getId() < 1) {
            log.setId(getNextSequenceNumberAsInteger("process_log_seq"));
        }
        JdbcTemplate jdbctemplate = getJdbcTemplate();
        jdbctemplate.execute(
                "INSERT INTO process_log (log_id,start_time,end_time,result_id,description) VALUES (?,?,?,?,?)",
                new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
                    protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                            throws SQLException {
                        ps.setLong(1, log.getId());
                        ps.setTime(2, new java.sql.Time(log.getStartTime().getTime()));
                        if (log.getEndTime() != null) {
                            ps.setTime(3, new java.sql.Time(log.getEndTime().getTime()));
                        } else {
                            ps.setDate(3, null);
                        }
                        ps.setLong(4, log.getResultId());
                        lobCreator.setClobAsString(ps, 5, log.getDescription());
                    }
                }
        );

        return log.getId();
    }


    @Override
    public Long addTransactionLog(String archiveName, String env) {

        SimpleJdbcTemplate template = new SimpleJdbcTemplate(getDataSource());
        Long txLogid = getJdbcTemplate().queryForLong("select TRANSACTION_LOG_ID_SEQ.nextval from dual");
        Object[] params = {txLogid, archiveName, env};
        template.update(" insert into TRANSACTION_LOG(TRANSACTION_LOG_ID,ARCHIVE_NAME,ISSUCCESSFUL,ENVIRONMENT,CREATED_DATE) " +
                " values (?,?,null,?,SYSDATE) ", params);
        return txLogid;
    }

    @Override
    public void updateTransactionLogStatus(String archiveName, Boolean isSuccessful) {
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(getDataSource());
        String successFlag;
        if (isSuccessful) {
            successFlag = "Y";
        } else {
            successFlag = "N";
        }
        Object[] params = {successFlag, archiveName};
        template.update(" update TRANSACTION_LOG set ISSUCCESSFUL = ? , UPDATED_DATE = SYSDATE where TRANSACTION_LOG_ID = " +
                " (select max(transaction_log_id) from transaction_log where archive_name = ?) ", params);


    }

    @Override
    public void updateTransactionLogStatus(Long transactionLogId, Boolean isSuccessful) {
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(getDataSource());
        String successFlag;
        if (isSuccessful) {
            successFlag = "Y";
        } else {
            successFlag = "N";
        }
        Object[] params = {successFlag, transactionLogId};
        template.update(" update TRANSACTION_LOG set ISSUCCESSFUL = ?,UPDATED_DATE = SYSDATE where TRANSACTION_LOG_ID = ?", params);
    }

    @Override
    public void addTransactionLogRecord(Long txLogId, String loggingClass) {
        Object[] params = {txLogId, loggingClass, new Timestamp((new Date()).getTime()), "N"};
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(getDataSource());
        template.update(" insert into transaction_log_record(TRANSACTION_LOG_RECORD_ID," +
                " TRANSACTION_LOG_ID," +
                " LOGGING_STATE," +
                " TRANSACTION_LOG_TS," +
                " ISSUCCESSFUL )" +
                " values (TRANSACTION_LOG_ID_SEQ.nextval,?,?,?,?) ", params);

    }

    @Override
    public void updateTransactionLogRecordResult(Long txLogId, String loggingClass, Boolean isSuccessful) {
        String successFlag;
        if (isSuccessful) {
            successFlag = "Y";
        } else {
            successFlag = "N";
        }

        Object[] params = {successFlag, txLogId, loggingClass};
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(getDataSource());
        template.update("  update transaction_log_record set ISSUCCESSFUL = ? where TRANSACTION_LOG_ID = ? and  LOGGING_STATE = ? ", params);

    }

    @Override
    public void addErrorMessage(Long txLogId, String archiveName, String errorMessage) {
        Object[] params = {archiveName, errorMessage, txLogId};
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(getDataSource());
        template.update(" insert into TRANSACTION_ERROR (TRANSACTION_ERROR_ID,ARCHIVE_NAME,ERROR_MESSAGE,TRANSACTION_LOG_ID) values " +
                " (TRANSACTION_LOG_ERROR_ID_SEQ.nextval,?,?,?) ", params);

    }

    public Collection getAllLogEntries() {
        return getAllObjectsAsList(LOG, "start_time");
    }

    static class LogQueryByParameter extends MappingSqlQuery {

        LogQueryByParameter(DataSource ds, LogQueryRequest queryParameter) {
            //TODO Does not work if the Date is null
            /*
            Bug in Oracle 9.2 through 10.2 jdbc drivers causes java.util.Date to be
             mapped to java.sql.Date which does not include time information. Once we
            move to Oracle JDBC 11.1, the CAST and string conversion below can be replaced
            with just a direct date comparison
             */
            super(ds, "SELECT log_id,description,start_time, end_time FROM process_log where start_time >= " +
                    "CAST(to_date(\'" + queryParameter.getStartDateAsString() + "\' ,\'MM/DD/YYYY\') as TIMESTAMP) " +
                    " ORDER BY start_time");
        }

        protected Log mapRow(ResultSet rs, int rownum) throws SQLException {
            Log log = new Log();
            log.setId(rs.getInt("log_id"));
            log.setDescription(rs.getString("description"));
            log.setStartTime(rs.getDate("start_time"));
            log.setEndTime(rs.getDate("end_time"));
            return log;
        }
    }


}
