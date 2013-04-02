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
public class Protocol extends ClinicalBean {

    private static final String[] orderedAttributes = {
            "EXPERIMENTALPROTOCOLTYPE",
            "PROTOCOLNAME",
            "PROTOCOLFILENAME"
    };

    private static final String insertSQL = "insert into protocol (EXPERIMENTALPROTOCOLTYPE, PROTOCOLNAME, PROTOCOLFILENAME, " +
            "analyte_id, protocol_id) values (?,?,?,?,PROTOCOL_protocol_id_SEQ.nextval)";
    private static PreparedStatement insertSTH = null;

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
        return "PROTOCOL";
    }

    public String getXmlGroupName() {
        return "PROTOCOLS";
    }

    public String getIdElementName() {
        return null;
    }


    public int insertSelf( final int analyteID ) {
        try {
            insertSTH.setString( 1, BCRUtil.checkEmpty( attributes.get( "EXPERIMENTALPROTOCOLTYPE" ) ) );
            insertSTH.setString( 2, BCRUtil.checkEmpty( attributes.get( "PROTOCOLNAME" ) ) );
            insertSTH.setString( 3, BCRUtil.checkEmpty( attributes.get( "PROTOCOLFILENAME" ) ) );
            insertSTH.setInt( 4, analyteID );
            return insertSTH.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.exit(-1);

        }
        return 0;
    }

    protected void prepareInsertStatement() throws SQLException {
        if(insertSTH == null) {
                insertSTH = ClinicalBean.dbConnection.prepareStatement( insertSQL );
        }
    }
}
