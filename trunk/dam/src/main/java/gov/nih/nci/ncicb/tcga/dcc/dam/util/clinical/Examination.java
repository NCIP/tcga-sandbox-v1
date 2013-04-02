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
public class Examination extends ClinicalBean {

    // todo: add BCREXAMINATIONBARCODE
    private static final String[] orderedAttributes = {
            "BCREXAMINATIONBARCODE",
            "KARNOFSKYPERFORMANCESCORE",
            "EASTERNCANCERONCOLOGYGROUP",
            "PROGRESSIONSTATUS",
            "PERFORMANCESTATUSSCALETIMING",
            "PROGRESSIONDETERMINEDBY",
            "PROGRESSIONDETERMINEDBYNOTES"
    };

    private static final String insertSQL;
    static {
        StringBuilder sql = new StringBuilder( "insert into EXAMINATION (");
        for (String attribute : orderedAttributes) {
            sql.append(attribute).append( ", " );
        }
        sql.append("patient_id, examination_id) values(");
        //noinspection UnusedDeclaration
        for (String attribute : orderedAttributes) {
            sql.append("?, ");
        }
        sql.append("?, EXAMINATION_examination_id_SEQ.nextval)");
        insertSQL = sql.toString();
    }

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
        return "EXAMINATION";
    }

    public String getXmlGroupName() {
        return "EXAMINATIONS";
    }

    public String getIdElementName() {
        return null;
    }

    public int insertSelf( final int patientID ) {
        try {
            for (int i=0; i<orderedAttributes.length; i++) {
                insertSTH.setString( i+1, BCRUtil.checkEmpty( attributes.get( orderedAttributes[i] ) ));                
            }
            insertSTH.setInt( orderedAttributes.length + 1, patientID );
            insertSTH.executeUpdate();
            return 1;
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
