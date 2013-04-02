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
public class Aliquot extends ClinicalBean {

    private static final String[] orderedAttributes = { "AMOUNT", "DAYOFSHIPMENT", "MONTHOFSHIPMENT", "YEAROFSHIPMENT",
            "BCRALIQUOTBARCODE", "CONCENTRATION"};
    private int aliquotID;
    private static final String insertSQL = "insert into ALIQUOT (AMOUNT, AMOUNTUNIT, SHIPPINGDATE, BCRALIQUOTBARCODE, CONCENTRATION, " +
            "CONCENTRATIONUNIT, analyte_id, aliquot_id) values (?,?,?,?,?,?,?,ALIQUOT_aliquot_id_SEQ.nextval)";
    private static final String selectSQL = "select aliquot_ID from ALIQUOT where BCRALIQUOTBARCODE = ?";
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
        return "ALIQUOT";
    }

    public String getXmlGroupName() {
        return "ALIQUOTS";
    }

    public String getIdElementName() {
        return "BCRALIQUOTBARCODE";
    }

    public int insertSelf( final int analyteID ) {
        try {
            selectSTH.setString( 1, attributes.get( "BCRALIQUOTBARCODE" ) );
            ResultSet rs = selectSTH.executeQuery();
            if (rs.next()) {
                aliquotID = rs.getInt( 1 );
                rs.close();
                System.out.println("**** Existing aliqot found for " + attributes.get( "BCRALIQUOTBARCODE" ) );
            } else {

                if ( attributes.get( "AMOUNT" ) == null || attributes.get( "AMOUNT" ).equals( "" )) {
                    insertSTH.setString( 1, null );
                    insertSTH.setString( 2, null );
                } else {
                    Matcher amountMatcher = VALUE_UNITS_PATTERN.matcher( attributes.get( "AMOUNT" ));
                    if (amountMatcher.matches()) {
                        insertSTH.setFloat( 1, BCRUtil.checkEmptyFloat( amountMatcher.group(1) ));
                        insertSTH.setString( 2, BCRUtil.checkEmpty(amountMatcher.group( 2 )));
                    } else {
                        System.out.println("Aliquot amount " + attributes.get( "AMOUNT" ) + " does not match regular expression");
                        System.exit(-1);
                    }
                }
                // construct date based on 3 parts!
                StringBuilder shippingDate = new StringBuilder().append( attributes.get( "YEAROFSHIPMENT" ) ).append( "-" );
                if (attributes.get( "MONTHOFSHIPMENT" ).length() == 1) {
                    shippingDate.append("0");
                }
                shippingDate.append( attributes.get( "MONTHOFSHIPMENT" ) ).append( "-" );
                if (attributes.get( "DAYOFSHIPMENT" ).length() == 1) {
                    shippingDate.append("0");
                }
                shippingDate.append( attributes.get( "DAYOFSHIPMENT" ) );
                String shippingDateStr = shippingDate.toString();
                if (shippingDateStr.equals( "--" )) {
                    shippingDateStr = "";
                }
                insertSTH.setDate( 3, BCRUtil.convertDate( shippingDateStr ) );
                insertSTH.setString( 4, BCRUtil.checkEmpty( attributes.get( "BCRALIQUOTBARCODE" ) ) );
                if (attributes.get( "CONCENTRATION" ) == null || attributes.get( "CONCENTRATION" ).equals( "" )) {
                    insertSTH.setString( 5, null );
                    insertSTH.setString( 6, null );
                } else {
                    Matcher concentrationMatcher = VALUE_UNITS_PATTERN.matcher( attributes.get( "CONCENTRATION" ));
                    if (concentrationMatcher.matches()) {
                        insertSTH.setFloat( 5, BCRUtil.checkEmptyFloat( concentrationMatcher.group( 1) ));
                        insertSTH.setString( 6, BCRUtil.checkEmpty( concentrationMatcher.group( 2 )));
                    } else {
                        System.out.println("Aliquot concentration value " + attributes.get( "CONCENTRATION" ) + " does not match regular expression");
                        System.exit(-1);
                    }
                }

                insertSTH.setInt( 7, analyteID );
                //System.out.println(attributes.get( "BCRALIQUOTBARCODE" ));
                insertSTH.executeUpdate();
                
                selectSTH.setString( 1, attributes.get( "BCRALIQUOTBARCODE" ) );
                rs = selectSTH.executeQuery();
                rs.next();
                aliquotID = rs.getInt( 1 );
                rs.close();
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.exit( -1 );
        }
        return aliquotID;
    }

    protected void prepareInsertStatement() throws SQLException {
        if (insertSTH == null) {
            insertSTH = ClinicalBean.dbConnection.prepareStatement( insertSQL );
        }
        if (selectSTH == null) {
            selectSTH = ClinicalBean.dbConnection.prepareStatement( selectSQL );
        }
    }


}
