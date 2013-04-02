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

/**
 * @author HickeyE
 * @version $id$
 */
public class Slide extends ClinicalBean {

    private static final String[] orderedAttributes = new String[14];
    static {
        orderedAttributes[0] = "SECTIONLOCATION";
        orderedAttributes[1] = "NUMBERPROLIFERATINGCELLS";
        orderedAttributes[2] = "PERCENTTUMORCELLS";
        orderedAttributes[3] = "PERCENTTUMORNUCLEI";
        orderedAttributes[4] = "PERCENTNORMALCELLS";
        orderedAttributes[5] = "PERCENTNECROSIS";
        orderedAttributes[6] = "PERCENTSTROMALCELLS";
        orderedAttributes[7] = "PERCENTINFLAMINFILTRATION";
        orderedAttributes[8] = "PERCENTLYMPHOCYTEINFILTRATION";
        orderedAttributes[9] = "PERCENTMONOCYTEINFILTRATION";
        orderedAttributes[10] = "PERCENTGRANULOCYTEINFILTRATION";
        orderedAttributes[11] = "PERCENTNEUTROPHILINFILTRATION";
        orderedAttributes[12] = "PERCENTEOSINOPHILINFILTRATION";
        orderedAttributes[13] = "BCRSLIDEBARCODE";
    }
    private int slideID;
    private static String insertSQL = "insert into SLIDE (slide_id, NUMBERPROLIFERATINGCELLS, PERCENTTUMORCELLS, " +
            "PERCENTTUMORNUCLEI, PERCENTNORMALCELLS, PERCENTNECROSIS, PERCENTSTROMALCELLS, PERCENTINFLAMINFILTRATION, " +
            "PERCENTLYMPHOCYTEINFILTRATION, PERCENTMONOCYTEINFILTRATION, PERCENTGRANULOCYTEINFILTRATION, " +
            "PERCENTNEUTROPHILINFILTRATION, PERCENTEOSINOPHILINFILTRATION, BCRSLIDEBARCODE, TYPE) values " +
            "(SLIDE_slide_id_SEQ.nextval, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?, ? )";
    private static String selectSQL = "select slide_id from slide where bcrslidebarcode = ?";
    private static String selectAllSQL = "select slide_id from slide " +
            "where NUMBERPROLIFERATINGCELLS = ? " +
            "and PERCENTTUMORCELLS = ? " +
            "and PERCENTTUMORNUCLEI = ? " +
            "and PERCENTNORMALCELLS = ? " +
            "and PERCENTNECROSIS = ? " +
            "and PERCENTSTROMALCELLS = ? " +
            "and PERCENTINFLAMINFILTRATION = ? " +
            "and PERCENTLYMPHOCYTEINFILTRATION = ? " +
            "and PERCENTMONOCYTEINFILTRATION = ? " +
            "and PERCENTGRANULOCYTEINFILTRATION = ? " +
            "and PERCENTNEUTROPHILINFILTRATION = ? " +
            "and PERCENTEOSINOPHILINFILTRATION = ? " +
            "and BCRSLIDEBARCODE = ? ";
    private static PreparedStatement selectSTH = null;
    private static PreparedStatement selectAllSTH = null;
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

    protected void prepareInsertStatement() throws SQLException {
        if(insertSTH == null) {
            insertSTH = ClinicalBean.dbConnection.prepareStatement( insertSQL );
        }
        if(selectSTH == null) {
            selectSTH = ClinicalBean.dbConnection.prepareStatement( selectSQL );
        }
        if(selectAllSTH == null) {
            selectAllSTH = ClinicalBean.dbConnection.prepareStatement( selectAllSQL );
        }
    }

    public String[] getOrderedAttributes() {
        return orderedAttributes;
    }

    public String getXmlElementName() {
        return "SLIDE";
    }

    public String getXmlGroupName() {
        return "SLIDES";
    }

    public String getIdElementName() {
        return "BCRSLIDEBARCODE";
    }


    public int insertSelf( int portionID ) {
        try {
            //  should be the exact same info if it is already in the database
            StringBuilder q = new StringBuilder("select slide_id from slide where ");
            for (String attribute : orderedAttributes) {
                if (!attribute.equals( "SECTIONLOCATION" )) {

                    q.append(attribute);
                    if (attributes.get( attribute ) == null || attributes.get( attribute ).equals( "" )) {
                        q.append(" is null ");
                    } else {
                        q.append(" = ").append("'").append(attributes.get( attribute )).append("' ");
                    }
                    if (! attribute.equals( "BCRSLIDEBARCODE" )) {
                        q.append("and ");
                    }
                }
            }

            selectAllSTH = ClinicalBean.dbConnection.prepareStatement( q.toString() );            
            ResultSet rs = selectAllSTH.executeQuery();
            if(rs.next()) {   // if the slide exists with the exact info
                slideID = rs.getInt( 1 );
                rs.close();
                return slideID;
            } else {    // if the barcode exists, this will correctly throw and error
                insertSTH.setString( 1, BCRUtil.checkEmpty( attributes.get( "NUMBERPROLIFERATINGCELLS" ) ) );
                insertSTH.setString( 2, BCRUtil.checkEmpty( attributes.get( "PERCENTTUMORCELLS" ) ) );
                insertSTH.setString( 3, BCRUtil.checkEmpty( attributes.get( "PERCENTTUMORNUCLEI" ) ) );
                insertSTH.setString( 4, BCRUtil.checkEmpty( attributes.get( "PERCENTNORMALCELLS" ) ) );
                insertSTH.setString( 5, BCRUtil.checkEmpty( attributes.get( "PERCENTNECROSIS" ) ) );
                insertSTH.setString( 6, BCRUtil.checkEmpty( attributes.get( "PERCENTSTROMALCELLS" ) ) );
                insertSTH.setString( 7, BCRUtil.checkEmpty( attributes.get( "PERCENTINFLAMINFILTRATION" ) ) );
                insertSTH.setString( 8, BCRUtil.checkEmpty( attributes.get( "PERCENTLYMPHOCYTEINFILTRATION" ) ) );
                insertSTH.setString( 9, BCRUtil.checkEmpty( attributes.get( "PERCENTMONOCYTEINFILTRATION" ) ) );
                insertSTH.setString( 10, BCRUtil.checkEmpty( attributes.get( "PERCENTGRANULOCYTEINFILTRATION" ) ) );
                insertSTH.setString( 11, BCRUtil.checkEmpty( attributes.get( "PERCENTNEUTROPHILINFILTRATION" ) ) );
                insertSTH.setString( 12, BCRUtil.checkEmpty( attributes.get( "PERCENTEOSINOPHILINFILTRATION" ) ) );
                insertSTH.setString( 13, BCRUtil.checkEmpty( attributes.get( "BCRSLIDEBARCODE" ) ) );
                insertSTH.setString( 14, BCRUtil.checkEmpty( attributes.get( "TYPE" ) ) );
                insertSTH.executeUpdate();
                selectSTH.setString( 1, attributes.get( "BCRSLIDEBARCODE" ) );
                rs = selectSTH.executeQuery();
                rs.next();
                slideID = rs.getInt( 1 );
                rs.close();
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.exit( -1 );
        }
        return slideID;
    }

    public int getSlideID() {
        return slideID;
    }

    public String getSectionLocation() {
        return attributes.get( "SECTIONLOCATION" );
    }
}
