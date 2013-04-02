/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util.clinical;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Parent class for SLIDE
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class DiseaseSlide extends ClinicalBean {

    protected abstract PreparedStatement getInsertSth();
    protected abstract PreparedStatement getSelectSth();

    protected abstract void setInsertSth(PreparedStatement sth);
    protected abstract void setSelectSth(PreparedStatement sth);


    protected void prepareInsertStatement() throws SQLException {
        if(getInsertSth() == null && getInsertSQL() != null ) {
            setInsertSth(dbConnection.prepareStatement( getInsertSQL() ));
        }
        if(getSelectSth() == null && getSelectSQL() != null) {
            setSelectSth( dbConnection.prepareStatement( getSelectSQL() ) );
        }
    }

    public boolean check( int i ) {
        if (getSelectSth() == null) {
            return false;
        }

        try {
            getSelectSth().setInt( 1, i );
            ResultSet rs = getSelectSth().executeQuery();
            if(rs.next()) {
                rs.close();
                return true;
            }
            rs.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.exit( -1 );
        }
        return false;
    }

    protected abstract String getInsertSQL();

    protected abstract String getSelectSQL();
}
