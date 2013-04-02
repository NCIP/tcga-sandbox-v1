/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import org.apache.log4j.Level;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class which allows us to manage connection closings quickly and cleanly across the whole DAO layer.
 *
 * @author Robert S. Sfeir
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class SQLProcessingCleaner {

    //This is not injected from Spring because we're using static methods
    private static ProcessLogger logger = new ProcessLogger();

    /**
     * Gets the default TCGA logger injected by Spring
     *
     * @return the logger
     */
    public static ProcessLogger getLogger() {
        return logger;
    }

    /**
     * Sets the default TCGA logger by injection from the Spring Framework
     *
     * @param logger the logger to be created.
     */
    public static void setLogger( final ProcessLogger logger ) {
        SQLProcessingCleaner.logger = logger;
    }

    /**
     * Cleans up a Connection object and closes it.
     *
     * @param connection the connection you want to close
     */
    public static void cleanUpConnection( final Connection... connection ) {
        for(Connection aConnection : connection) {
            if(aConnection != null) {
                try {
                    aConnection.close();
                }
                catch(SQLException e) {
                    logger.logToLogger( Level.WARN, "Tried to close connection, but was already closed." );
                }
            }
        }
    }

    /**
     * Cleans up a Statement passed in and closes it.
     *
     * @param statement the Statement object you want to close.
     */
    public static void cleanUpStatement( final Statement... statement ) {
        for(Statement aStatement : statement) {
            if(aStatement != null) {
                try {
                    aStatement.close();
                }
                catch(SQLException e) {
                    logger.logToLogger( Level.WARN, "Tried to close statement, but was already closed." );
                }
            }
        }
    }

    /**
     * Cleans up a ResultSet passed in and closes it out.
     *
     * @param resultSet the ResultSet you want to close.
     */
    public static void cleanUpResultSet( final ResultSet... resultSet ) {
        for(ResultSet aResultSet : resultSet) {
            if(aResultSet != null) {
                try {
                    aResultSet.close();
                }
                catch(SQLException e) {
                    logger.logToLogger( Level.WARN, "Tried to close resultset, but was already closed." );
                }
            }
        }
    }

    /**
     * Convenience method to pass in one each of resultSet, statement and connection.
     * Takes care of closing things in the proper order.
     * There can only be one of each in this case, you can't pass multiples of each.
     * If you have multiples to close use the other individual methods to do so.
     *
     * @param resultSet  the resultSet you want to close
     * @param statement  the statement you want to close
     * @param connection the connection you want to close
     */
    public static void cleanUp( final ResultSet resultSet, final Statement statement, final Connection connection ) {
        cleanUpResultSet( resultSet );
        cleanUpStatement( statement );
        cleanUpConnection( connection );
    }
}
