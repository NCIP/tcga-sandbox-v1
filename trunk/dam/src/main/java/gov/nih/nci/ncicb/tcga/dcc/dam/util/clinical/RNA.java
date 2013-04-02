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
public class RNA extends ClinicalBean {

    private static final String[] orderedAttributes = {
            "RATIO28S18S",
            "RINVALUE"
    };

    private static final String insertSQL = "insert into RNA (RATIO28S18S, RINVALUE, " +
            "analyte_id, rna_id) values (?, ?, ?, RNA_rnaid_SEQ.nextval)";
    // private static String selectSQL = "select drug_id from DRUG where "; WHATS THE KEY!!
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
        return "RNA";
    }

    public String getXmlGroupName() {
        return null;
    }

    public String getIdElementName() {
        return null;
    }

    public int insertSelf( final int analyteID ) {
        try {
            insertSTH.setString( 1, BCRUtil.checkEmpty( attributes.get( "RATIO28S18S" ) ) );
            insertSTH.setString( 2, BCRUtil.checkEmpty( attributes.get( "RINVALUE" ) ) );
            insertSTH.setInt( 3, analyteID );
            return insertSTH.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.exit( -1 );
        }
        return 0;
    }

    protected void prepareInsertStatement() throws SQLException {
        if (insertSTH == null) {
            insertSTH = ClinicalBean.dbConnection.prepareStatement( insertSQL );
        }
    }
}
