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
public class Drug extends ClinicalBean {

    // todo: add BCRDRUGBARCODE
    private static final String[] orderedAttributes = {
            "BCRDRUGBARCODE",
            "DRUGCATEGORY",
            "DRUGDOSAGE",
            "DOSAGEUNITS",
            "NUMBERCYCLES",
            "DAYSTODRUGTREATMENTSTART",            
            "DAYSTODRUGTREATMENTEND",
            "INITIALCOURSE",
            "DRUGNAME",
            "REGIMENINDICATION",
            "REGIMENINDICATIONNOTES",
            "ROUTEOFADMINISTRATION",
            "ROUTEOFADMINISTRATIONNOTES"
    };

    private static final String insertSQL;
    static {
        StringBuilder sql = new StringBuilder("insert into DRUG_intgen(");
        for (String attribute : orderedAttributes) {            
            sql.append( attribute ).append(", ");
        }
        sql.append("patient_id, drug_id) values(");
        //noinspection UnusedDeclaration
        for (String attribute : orderedAttributes) {
            sql.append("?, ");
        }
        sql.append("?, DRUG_intgen_drug_id_SEQ.nextval)");
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
        return "DRUG";
    }

    public String getXmlGroupName() {
        return "DRUGS";
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
                        insertSTH.setInt( i+1, BCRUtil.checkEmptyInt( attributes.get( orderedAttributes[i])) );
                    }
                } else if (orderedAttributes[i].equalsIgnoreCase( "NUMBERCYCLES" )) {
                    if (attributes.get(orderedAttributes[i]) == null || attributes.get(orderedAttributes[i]).equals( "" )) {
                        insertSTH.setString( i+1, null );
                    } else {
                        Float numCycles = BCRUtil.checkEmptyFloat( attributes.get( orderedAttributes[i] ));
                        if (numCycles != null) {
                            insertSTH.setFloat( i+1, numCycles );
                        }
                    }
                } else {
                    insertSTH.setString( i+1, BCRUtil.checkEmpty( attributes.get( orderedAttributes[i]) ));
                }
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
