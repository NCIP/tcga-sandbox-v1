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
public class Radiation extends ClinicalBean {

    // todo: add BCRRADIATIONBARCODE
    private static final String[] orderedAttributes = {
            "BCRRADIATIONBARCODE",
            "DAYSTORADIATIONTREATMENTSTART",
            "DAYSTORADIATIONTREATMENTEND",
            "RADIATIONTYPE",
            "RADIATIONTYPENOTES",
            "RADIATIONDOSAGE",
            "UNITS",
            "NUMFRACTIONS",
            "ANATOMICTREATMENTSITE",
            "REGIMENINDICATION",
            "REGIMENINDICATIONNOTES",
            "RADIATIONTREATMENTONGOING"
    };

    private static final String insertSQL;
    static {
        StringBuilder sql = new StringBuilder("insert into RADIATION (");
        for (String attribute : orderedAttributes) {
            if (attribute.equalsIgnoreCase( "RADIATIONTYPE" )) {
                attribute = "TYPE"; // different column name than element name
            } 
            sql.append(attribute).append(", ");
        }
        sql.append("patient_id, radiation_id) values(");
        //noinspection UnusedDeclaration
        for (String attribute : orderedAttributes) {
            sql.append("?, ");
        }
        sql.append("?, RADIATION_radiation_id_SEQ.nextval)");
        insertSQL = sql.toString();
    }
    // private static String selectSQL = "select radiation_id"; WHATS THE KEY!!
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
        return "RADIATION";
    }

    public String getXmlGroupName() {
        return "RADIATIONS";
    }

    public String getIdElementName() {
        return null;
    }

    public int insertSelf( final int patientID ) {
        try {
            for (int i=0; i<orderedAttributes.length; i++) {
                String attribute = orderedAttributes[i];
                if (attributes.get( attribute ) == null || attributes.get( attribute ).equals( "" )) {
                    insertSTH.setString( i+1, null );
                } else if (attribute.equalsIgnoreCase( "NUMFRACTIONS" )) {
                    insertSTH.setFloat( i+1, BCRUtil.checkEmptyFloat( attributes.get(attribute) ));
                } else if (attribute.equalsIgnoreCase( "RADIATIONDOSAGE" ) || attribute.contains( "DAYSTO" )) {

                    insertSTH.setInt( i+1, BCRUtil.checkEmptyInt( attributes.get(attribute) ));
                } else {
                    insertSTH.setString( i+1, BCRUtil.checkEmpty(attributes.get(attribute) ));
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
