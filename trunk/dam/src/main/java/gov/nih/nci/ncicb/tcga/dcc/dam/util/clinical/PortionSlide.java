/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util.clinical;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author HickeyE
 * @version $id$
 */
public class PortionSlide  {

    private int portionSlideID;
    private static String insertSQL = "insert into portion_slide (portion_slide_id, portion_id, slide_id, sectionlocation) " +
            "values (PORTION_SLIDE_ps_id_SEQ.nextval, ?,?,?)";
    private static String selectSQL = "select portion_slide_id from portion_slide where slide_ID = ? and portion_ID = ? and sectionlocation = ?";
    private static PreparedStatement insertSTH = null;
    private static PreparedStatement selectSTH = null;

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

    public PortionSlide() {
        try {
            if(insertSTH == null) {
                insertSTH = ClinicalBean.dbConnection.prepareStatement( insertSQL );
            }
            if(selectSTH == null) {
                selectSTH = ClinicalBean.dbConnection.prepareStatement( selectSQL );
            }
        }
        catch(
                SQLException e
                ) {
            e.printStackTrace();
            System.exit( -1 );
        }
    }

    public int insertSelf( int portionID, int slideID, String section ) {
        try {
            insertSTH.setInt( 1, portionID );
            insertSTH.setInt( 2, slideID );
            insertSTH.setString( 3, section );
            insertSTH.executeUpdate();
            selectSTH.setInt( 1, slideID );
            selectSTH.setInt( 2, portionID );
            selectSTH.setString( 3, section );
            ResultSet rs = selectSTH.executeQuery();
            rs.next();
            portionSlideID = rs.getInt( 1 );
            rs.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.exit( -1 );
        }
        return portionSlideID;
    }
}
