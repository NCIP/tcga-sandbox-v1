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
 * @author Silpa Nanan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class OvarianPathology extends Pathology {

    private static PreparedStatement insertSTH;

    private static final String[] orderedAttributes = {
            "HISTOLOGICNUCLEARGRADE",
            "TUMORSAMPLEANATOMICLOCATION",
            "TNMPATHOLOGYSTAGEGROUPING",
            "TNMPATHOLOGYTUMORSTATUS",
            "TNMPATHOLOGYLYMPHNODESTATUS",
            "TNMPATHOLOGYMETASTATICSTATUS"
    };

    private static final String insertSQL = "insert into OVARIAN_PATHOLOGY (HISTOLOGICNUCLEARGRADE, " +
            "TUMORSAMPLEANATOMICLOCATION, " +
            "TNMPATHOLOGYSTAGEGROUPING, " +
            "TNMPATHOLOGYTUMORSTATUS, " +
            "TNMPATHOLOGYLYMPHNODESTATUS, " +
            "TNMPATHOLOGYMETASTATICSTATUS, tumorpathology_id) values (?, ?, ?, ?, ?, ?, ?)";

    public static void cleanup() throws SQLException {

        if (insertSTH != null) {
            insertSTH.close();
            insertSTH = null;
        }
    }

    public String[] getOrderedAttributes() {
        return orderedAttributes;
    }

    public String getXmlElementName() {
        return "TYPE/OVARIANPATHOLOGY";
    }

    public String getXmlGroupName() {
        return null;
    }

    public String getIdElementName() {
        return null;
    }

    protected String getInsertSQL() {
        return insertSQL;
    }

    public int insertSelf( final int tumorPathologyID ) {
        try {
            insertSTH.setString( 1, BCRUtil.checkEmpty( attributes.get( "HISTOLOGICNUCLEARGRADE" ) ) );
            insertSTH.setString( 2, BCRUtil.checkEmpty( attributes.get( "TUMORSAMPLEANATOMICLOCATION" ) ) );
            insertSTH.setString( 3, BCRUtil.checkEmpty( attributes.get( "TNMPATHOLOGYSTAGEGROUPING" ) ) );
            insertSTH.setString( 4, BCRUtil.checkEmpty( attributes.get( "TNMPATHOLOGYTUMORSTATUS" ) ) );
            insertSTH.setString( 5, BCRUtil.checkEmpty( attributes.get( "TNMPATHOLOGYLYMPHNODESTATUS" ) ) );
            insertSTH.setString( 6, BCRUtil.checkEmpty( attributes.get( "TNMPATHOLOGYMETASTATICSTATUS" ) ) );
            insertSTH.setInt( 7, tumorPathologyID );
            return insertSTH.executeUpdate();
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
