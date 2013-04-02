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
public class Portion extends ClinicalBean {

    private static final String[] orderedAttributes = {"DAYOFCREATION", "MONTHOFCREATION", "YEAROFCREATION", "WEIGHT", "BCRPORTIONBARCODE"};
    private int portionID;
    private static String insertSQL = "insert into portion (portion_ID, DAYOFCREATION, MONTHOFCREATION, YEAROFCREATION, WEIGHT, WEIGHTUNIT, BCRPORTIONBARCODE, sample_id) values (PORTION_portion_id_SEQ.nextval, ?, ?, ?, ?, ?, ?, ?) ";
    private static String selectSQL = "select portion_id from portion where BCRPORTIONBARCODE = ?";
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
        return "PORTION";
    }

    public String getXmlGroupName() {
        return "PORTIONS";
    }

    public String getIdElementName() {
        return "BCRPORTIONBARCODE";
    }

    private void setInteger(String attributeName, int bindIndex) throws SQLException {
        Integer value = BCRUtil.checkEmptyInt(attributes.get(attributeName));
        if (value == null) {
            insertSTH.setString(bindIndex, null);
        } else {
            insertSTH.setInt(bindIndex, value);
        }
    }


    public int insertSelf( final int sampleID ) {
        try {
            selectSTH.setString( 1, attributes.get( "BCRPORTIONBARCODE" ) );
            ResultSet rs = selectSTH.executeQuery();
            if (rs.next()) {
                portionID = rs.getInt( 1 );
                // jwc todo this should not happen so take this out once patrick fixes the XML
                System.out.println("---- DUPLICATE PORTION " + attributes.get( "BCRPORTIONBARCODE" ));
            } else {
                setInteger("DAYOFCREATION", 1);
                setInteger("MONTHOFCREATION", 2);
                setInteger("YEAROFCREATION", 3);


                String weight = attributes.get( "WEIGHT" );
                if (weight == null || weight.equals( "" )) {
                    insertSTH.setString( 4, null );
                    insertSTH.setString( 5, null );
                } else {
                    Matcher weightMatcher = VALUE_UNITS_PATTERN.matcher( weight );
                    if (weightMatcher.matches()) {
                        insertSTH.setFloat( 4, BCRUtil.checkEmptyFloat( weightMatcher.group( 1 )));
                        insertSTH.setString( 5, BCRUtil.checkEmpty( weightMatcher.group( 2 )));
                    } else {
                        System.out.println("Portion weight " + weight + " does not match regular expression");
                        System.exit(-1);
                    }
                }
                insertSTH.setString( 6, BCRUtil.checkEmpty( attributes.get( "BCRPORTIONBARCODE" ) ) );
                insertSTH.setInt( 7, sampleID );
                insertSTH.executeUpdate();
                selectSTH.setString( 1, attributes.get( "BCRPORTIONBARCODE" ) );
                rs = selectSTH.executeQuery();
                rs.next();
                portionID = rs.getInt( 1 );
                rs.close();
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.exit( -1 );
        }
        return portionID;
    }

    protected void prepareInsertStatement() throws SQLException {
        if(insertSTH == null) {
                insertSTH = ClinicalBean.dbConnection.prepareStatement( insertSQL );
        }
        if(selectSTH == null) {
            selectSTH = ClinicalBean.dbConnection.prepareStatement( selectSQL );
        }
    }

    public int getPortionID() {
        return portionID;
    }
}
