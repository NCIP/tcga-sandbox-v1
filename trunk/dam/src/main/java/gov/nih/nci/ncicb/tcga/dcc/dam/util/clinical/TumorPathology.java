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
public class TumorPathology extends ClinicalBean {

    private static final String[] orderedAttributes = new String[7];
    static {
        orderedAttributes[0] = "PRIMARYORMETASTATICSTATUS";
        orderedAttributes[1] = "MARGINSINVOLVED";
        orderedAttributes[2] = "VENOUSINVASION";
        orderedAttributes[3] = "LYMPHATICINVASION";
        orderedAttributes[4] = "NUMBERREGIONALLYMPHNODESEXAM";
        orderedAttributes[5] = "NUMBERREGIONALLYMPHNODESPOS";
        orderedAttributes[6] = "VERIFICATIONBYBCR";
    }

    private int tumorPathologyID;
    private Pathology path;
    private static final String insertSQL = "insert into TUMORPATHOLOGY (tumorpathology_id, PRIMARYORMETASTATICSTATUS, " +
            "MARGINSINVOLVED, VENOUSINVASION, LYMPHATICINVASION, NUMBERREGIONALLYMPHNODESEXAM, NUMBERREGIONALLYMPHNODESPOS," +
            " VERIFICATIONBYBCR, TYPE, sample_id) values (TUMORPATHOLOG_tumorpatholo_SEQ.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String selectSQL = "select tumorpathology_id from TUMORPATHOLOGY where sample_id = ?";
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

    protected void prepareInsertStatement() throws SQLException {
        if (insertSTH == null) {
            insertSTH = ClinicalBean.dbConnection.prepareStatement( insertSQL );
        }
        if (selectSTH == null) {
            selectSTH = ClinicalBean.dbConnection.prepareStatement( selectSQL );
        }
    }

    public String[] getOrderedAttributes() {
        return orderedAttributes;
    }

    public String getXmlElementName() {
        return "TUMORPATHOLOGY";
    }

    public String getXmlGroupName() {
        return null;
    }

    public String getIdElementName() {
        return null;
    }


    public int insertSelf( final int sampleID ) {

        try {
            selectSTH.setInt( 1, sampleID );
            ResultSet rs = selectSTH.executeQuery();
            if (rs.next()) {
                tumorPathologyID = rs.getInt(1);
            } else {
                insertSTH.setString( 1, BCRUtil.checkEmpty( attributes.get( "PRIMARYORMETASTATICSTATUS" ) ) );
                insertSTH.setString( 2, BCRUtil.checkEmpty( attributes.get( "MARGINSINVOLVED" ) ) );
                insertSTH.setString( 3, BCRUtil.checkEmpty( attributes.get( "VENOUSINVASION" ) ) );
                insertSTH.setString( 4, BCRUtil.checkEmpty( attributes.get( "LYMPHATICINVASION" ) ) );
                insertSTH.setString( 5, BCRUtil.checkEmpty( attributes.get( "NUMBERREGIONALLYMPHNODESEXAM" ) ) );
                insertSTH.setString( 6, BCRUtil.checkEmpty( attributes.get( "NUMBERREGIONALLYMPHNODESPOS" ) ) );
                insertSTH.setString( 7, BCRUtil.checkEmpty( attributes.get( "VERIFICATIONBYBCR" ) ) );
                insertSTH.setString( 8, BCRUtil.checkEmpty( attributes.get( "TYPE" ) ) );
                insertSTH.setInt( 9, sampleID );
                insertSTH.executeUpdate();
                selectSTH.setInt( 1, sampleID );
                rs = selectSTH.executeQuery();
                rs.next();
                tumorPathologyID = rs.getInt( 1 );
                rs.close();
            }
            return tumorPathologyID;
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.exit( -1 );
        }
        return 0;
    }

    public Pathology getPath() {
        return path;
    }

    public void setPath( final Pathology path ) {
        this.path = path;
    }

    public int getTumorPathologyID() {
        return tumorPathologyID;
    }
}
