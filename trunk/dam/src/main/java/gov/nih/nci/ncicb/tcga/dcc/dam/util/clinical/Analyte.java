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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;

/**
 * @author HickeyE
 * @version $id$
 */
public class Analyte extends ClinicalBean {

    private static final String[] orderedAttributes = { "ANALYTETYPE",
            "CONCENTRATION",
            "AMOUNT",
            "A260A280RATIO",
            "GELIMAGEFILE",
            "WELLNUMBER",
            "BCRANALYTEBARCODE" };
    private int analyteID;
    private static final String insertSQL = "insert into ANALYTE (TYPE, CONCENTRATION, CONCENTRATIONUNIT, AMOUNT, AMOUNTUNIT, A260A280RATIO, GELIMAGEFILE, " +
            "WELLNUMBER, BCRANALYTEBARCODE, portion_id, analyte_id) values (?,?,?,?,?,?,?,?,?,?, ANALYTE_analyte_id_SEQ.nextval)";
    private static final String selectSQL = "select analyte_id from ANALYTE where BCRANALYTEBARCODE = ?";
    private static PreparedStatement selectSTH = null;
    private static PreparedStatement insertSTH = null;

    public static void cleanup() throws SQLException {
        if (selectSTH != null) {
            selectSTH.close();
            selectSTH = null;
        }
        if (insertSTH != null) {
            insertSTH.close();
            insertSTH = null;
        }
    }

    public String[] getOrderedAttributes() {
        return orderedAttributes;
    }

    public String getXmlElementName() {
        return "ANALYTE";
    }

    public String getXmlGroupName() {
        return "ANALYTES";
    }

    public String getIdElementName() {
        return "BCRANALYTEBARCODE";
    }

    public int insertSelf( final int portionID ) {
        try {
            // look for analyte with this barcode
            selectSTH.setString( 1, attributes.get( getIdElementName()) );
            ResultSet rs = selectSTH.executeQuery();
            if (rs.next()) {
                analyteID = rs.getInt(1);
                rs.close();
                System.out.println("*** Existing analyte found for " + attributes.get( getIdElementName()) );
            } else {

                insertSTH.setString( 1, BCRUtil.checkEmpty( attributes.get( "ANALYTETYPE" ) ) );

                if( attributes.get( "CONCENTRATION" ) == null || attributes.get( "CONCENTRATION" ).equals( "" )) {
                    // no concentration or units
                    insertSTH.setString( 2, null );
                    insertSTH.setString( 3, null );
                } else {
                    Matcher concentrationMatcher = VALUE_UNITS_PATTERN.matcher( attributes.get( "CONCENTRATION" ));
                    if (concentrationMatcher.matches()) {
                        insertSTH.setFloat( 2, BCRUtil.checkEmptyFloat( concentrationMatcher.group( 1 )));
                        insertSTH.setString( 3, BCRUtil.checkEmpty( concentrationMatcher.group( 2 )));
                    } else {
                        System.out.println("Concentration value " + attributes.get("CONCENTRATION") + " did not match regular expression");
                        System.exit(-1);
                    }
                }
                if( attributes.get( "AMOUNT" ) == null || attributes.get( "AMOUNT" ).equals( "" )) {
                    insertSTH.setString( 4, null );
                    insertSTH.setString( 5, null );
                } else {
                    Matcher amountMatcher = VALUE_UNITS_PATTERN.matcher(attributes.get( "AMOUNT" ));
                    if (amountMatcher.matches()) {
                        insertSTH.setFloat( 4, BCRUtil.checkEmptyFloat( amountMatcher.group( 1 )));
                        insertSTH.setString( 5, BCRUtil.checkEmpty( amountMatcher.group( 2 )));
                    } else {
                        System.out.println("Amount value " + attributes.get( "AMOUNT" ) + " does not match regular expression");
                        System.exit(-1);
                    }
                }
                insertSTH.setString( 6, BCRUtil.checkEmpty( attributes.get( "A260A280RATIO" ) ) );
                insertSTH.setString( 7, BCRUtil.checkEmpty( attributes.get( "GELIMAGEFILE" ) ) );
                insertSTH.setString( 8, BCRUtil.checkEmpty( attributes.get( "WELLNUMBER" ) ) );
                insertSTH.setString( 9, BCRUtil.checkEmpty( attributes.get( "BCRANALYTEBARCODE" ) ) );
                insertSTH.setInt( 10, portionID );
                insertSTH.executeUpdate();
                selectSTH.setString( 1, attributes.get( "BCRANALYTEBARCODE" ) );
                rs = selectSTH.executeQuery();
                rs.next();
                analyteID = rs.getInt( 1 );
                rs.close();
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return analyteID;
    }

    protected void prepareInsertStatement() throws SQLException {
        if (insertSTH == null) {
            insertSTH = ClinicalBean.dbConnection.prepareStatement( insertSQL );
        }
        if (selectSTH == null) {
            selectSTH = ClinicalBean.dbConnection.prepareStatement( selectSQL );
        }
    }

    public int getAnalyteID() {
        return analyteID;
    }

    public void lookupAnalyteID() {
        try {
            selectSTH.setString( 1, attributes.get( "BCRANALYTEBARCODE" ) );
            ResultSet rs = selectSTH.executeQuery();
            if(rs.next()) {
                analyteID = rs.getInt( 1 );
                rs.close();
            } else {
                throw new SQLException( "no analyteID found for: " + attributes.get( "BCRANALYTEBARCODE" ) );
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.exit( -1 );
        }
    }
}
