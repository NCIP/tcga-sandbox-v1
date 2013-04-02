/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util.clinical;

import gov.nih.nci.ncicb.tcga.dcc.dam.util.BCRUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author HickeyE
 * @version $id$
 */
public class GBMPathology extends Pathology {

    private static PreparedStatement insertSTH;

    private static final String[] orderedAttributes = {
            "HISTOLOGICTYPE",
            "HISTOLOGICNUCLEARGRADE",
            "TUMORSAMPLEANATOMICLOCATION",
            "GEMISTOCYTESPRESENT",
            "OLIGODENDROGLIALCOMPONENT",
            "LEPTOMENINGEALINVOLEMENT",
            "GFAP_POSITIVE",
            "MIB1_POSITIVE"
    };
    private static final String insertSQL = "insert into GBM_PATHOLOGY (HISTOLOGICTYPE, " +
            "HISTOLOGICNUCLEARGRADE, " +
            "TUMORSAMPLEANATOMICLOCATION, " +
            "GEMISTOCYTESPRESENT, " +
            "OLIGODENDROGLIALCOMPONENT, " +
            "LEPTOMENINGEALINVOLEMENT, " +
            "GFAP_POSITIVE, " +
            "MIB1_POSITIVE, tumorpathology_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";


    public static void cleanup() throws SQLException {
        if (insertSTH != null) {
            insertSTH.close();
            insertSTH = null;
        }
    }

    protected String getInsertSQL() {
        return insertSQL;
    }

    public String[] getOrderedAttributes() {
        return orderedAttributes;
    }

    public String getXmlElementName() {
        return "TYPE/GBMPATHOLOGY";
    }

    public String getXmlGroupName() {
        return null;
    }

    public String getIdElementName() {
        return null;
    }

    public int insertSelf( final int tumorPathologyID ) {
        try {
            insertSTH.setString( 1, BCRUtil.checkEmpty( attributes.get( "HISTOLOGICTYPE" ) ) );
            insertSTH.setString( 2, BCRUtil.checkEmpty( attributes.get( "HISTOLOGICNUCLEARGRADE" ) ) );
            insertSTH.setString( 3, BCRUtil.checkEmpty( attributes.get( "TUMORSAMPLEANATOMICLOCATION" ) ) );
            insertSTH.setString( 4, BCRUtil.checkEmpty( attributes.get( "GEMISTOCYTESPRESENT" ) ) );
            insertSTH.setString( 5, BCRUtil.checkEmpty( attributes.get( "OLIGODENDROGLIALCOMPONENT" ) ) );
            insertSTH.setString( 6, BCRUtil.checkEmpty( attributes.get( "LEPTOMENINGEALINVOLEMENT" ) ) );
            insertSTH.setString( 7, BCRUtil.checkEmpty( attributes.get( "GFAP_POSITIVE" ) ) );
            insertSTH.setString( 8, BCRUtil.checkEmpty( attributes.get( "MIB1_POSITIVE" ) ) );
            insertSTH.setInt( 9, tumorPathologyID );
            insertSTH.executeUpdate();
            return 1;
        }
        catch(SQLException e) {
            e.printStackTrace();
            //System.exit( -1 );
        }
        return 0;
    }

    protected void prepareInsertStatement() throws SQLException {
        if (insertSTH == null) {
            insertSTH = ClinicalBean.dbConnection.prepareStatement( getInsertSQL() );
        }
    }
}
