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
public class Surgery extends ClinicalBean {

    private static final String[] orderedAttributes = {
            "BCRSURGERYBARCODE",
            "DAYSTOPROCEDURE",            
            "PROCEDURETYPE"
    };

    private static final String insertSQL = "insert into SURGERY (BCRSURGERYBARCODE, DAYSTOPROCEDURE, PROCEDURETYPE, patient_ID, surgery_ID) values (?, ?, ?, ?," +
            " SURGERY_surgery_id_SEQ.nextval)";

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
        return "SURGERY";
    }

    public String getXmlGroupName() {
        return "SURGERIES";
    }

    public String getIdElementName() {
        return null;
    }


    public int insertSelf( final int patientID ) {
        try {
            for (int i=0; i<orderedAttributes.length; i++) {
                if (orderedAttributes[i].contains( "DAYSTO" )) {
                    if (attributes.get( orderedAttributes[i] ) == null || attributes.get( orderedAttributes[i] ).equals( "" )) {
                        insertSTH.setString( i+1, null );
                    } else {
                        insertSTH.setInt( i+1, BCRUtil.checkEmptyInt( attributes.get(orderedAttributes[i]) ));
                    }
                } else {
                    insertSTH.setString( i+1, BCRUtil.checkEmpty( attributes.get(orderedAttributes[i] )));
                }
            }
            insertSTH.setInt( orderedAttributes.length + 1, patientID );
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
