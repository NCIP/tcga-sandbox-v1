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
public class GBMSlide extends DiseaseSlide {

    protected static PreparedStatement insertSTH = null;
    private static PreparedStatement selectSTH = null;

    private static final String[] orderedAttributes = {
            "ENDOTHELIALPROLIFERATION",
            "NUCLEARPLEOMORPHISM",
            "PALISADINGNECROSIS",
            "CELLULARITY"
    };
    private static final String insertSQL = "insert into GBMSLIDE (ENDOTHELIALPROLIFERATION, NUCLEARPLEOMORPHISM, " +
            "PALISADINGNECROSIS, CELLULARITY, slide_id) values (?,?,?,?,?)";
    private static String selectSQL = "select slide_id from gbmslide where slide_id = ?";

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
        return "TYPE/GBMSLIDE";
    }

    public String getXmlGroupName() {
        return null;
    }

    public String getIdElementName() {
        return null;
    }

    protected PreparedStatement getInsertSth() {
        return insertSTH;
    }

    protected PreparedStatement getSelectSth() {
        return selectSTH;
    }

    protected void setInsertSth( final PreparedStatement sth ) {
        insertSTH = sth;
    }

    protected void setSelectSth( final PreparedStatement sth ) {
        selectSTH = sth;
    }

    protected String getInsertSQL() {
        return insertSQL;
    }

    protected String getSelectSQL() {
        return selectSQL;
    }

    public int insertSelf( final int slideID ) {
        if (! check( slideID )) {

            try {
                insertSTH.setString( 1, BCRUtil.checkEmpty( attributes.get( "ENDOTHELIALPROLIFERATION" ) ) );
                insertSTH.setString( 2, BCRUtil.checkEmpty( attributes.get( "NUCLEARPLEOMORPHISM" ) ) );
                insertSTH.setString( 3, BCRUtil.checkEmpty( attributes.get( "PALISADINGNECROSIS" ) ) );
                insertSTH.setString( 4, BCRUtil.checkEmpty( attributes.get( "CELLULARITY" ) ) );
                insertSTH.setInt( 5, slideID );
                insertSTH.executeUpdate();
                return 1;
            }
            catch(SQLException e) {
                e.printStackTrace();
                //System.exit( -1 );
            }            
        }
        return 0;
    }
}
