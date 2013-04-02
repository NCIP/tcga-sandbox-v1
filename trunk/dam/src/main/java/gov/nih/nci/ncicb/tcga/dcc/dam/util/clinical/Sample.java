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
public class Sample extends ClinicalBean {

    private static final String[] orderedAttributes = new String[14];
    static {
        orderedAttributes[0] = "SAMPLETYPE";
        orderedAttributes[1] = "LONGESTDIMENSION";
        orderedAttributes[2] = "INTERMEDIATEDIMENSION";
        orderedAttributes[3] = "SHORTESTDIMENSION";
        orderedAttributes[4] = "INITIALWEIGHT";
        orderedAttributes[5] = "CURRENTWEIGHT";
        orderedAttributes[6] = "FREEZINGMETHOD";
        orderedAttributes[7] = "OCTEMBEDDED";
        orderedAttributes[8] = "DAYSTOCOLLECTION";
        orderedAttributes[9] = "TIMEBETWEENCLAMPINGANDFREEZING";
        orderedAttributes[10] = "TIMEBETWEENEXCISIONANDFREEZING";
        orderedAttributes[11] = "BCRSAMPLEBARCODE";
    }
    private int sampleID;
    private TumorPathology tumorPathology;
    private static final String insertSQL = "insert into SAMPLE (sample_ID, SAMPLETYPE, LONGESTDIMENSION, INTERMEDIATEDIMENSION, " +
            "SHORTESTDIMENSION, INITIALWEIGHT, CURRENTWEIGHT, FREEZINGMETHOD, OCTEMBEDDED, DAYSTOCOLLECTION, " +
            "TIMEBETWEENCLAMPINGANDFREEZING, TIMEBETWEENEXCISIONANDFREEZING, BCRSAMPLEBARCODE, patient_ID) values (SAMPLE_sample_id_SEQ.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String selectSQL = "select sample_ID from SAMPLE where BCRSAMPLEBARCODE = ?";
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
        return "SAMPLE";
    }

    public String getXmlGroupName() {
        return "SAMPLES";
    }

    public String getIdElementName() {
        return "BCRSAMPLEBARCODE";
    }


    private void setInteger(String attributeName, int bindIndex) throws SQLException {
        Integer value = BCRUtil.checkEmptyInt(attributes.get(attributeName));
        if (value == null) {
            insertSTH.setString(bindIndex, null);
        } else {
            insertSTH.setInt(bindIndex, value);
        }
    }

    public int insertSelf( final int patientID ) {
        try {
            selectSTH.setString( 1, attributes.get( "BCRSAMPLEBARCODE" ) );
            ResultSet rs = selectSTH.executeQuery();
            if (rs.next()) {
                sampleID = rs.getInt( 1 );
                rs.close();
            } else {
                insertSTH.setString( 1, BCRUtil.checkEmpty( attributes.get( "SAMPLETYPE" ) ) );
                insertSTH.setString( 2, BCRUtil.checkEmpty( attributes.get( "LONGESTDIMENSION" ) ) );
                insertSTH.setString( 3, BCRUtil.checkEmpty( attributes.get( "INTERMEDIATEDIMENSION" ) ) );
                insertSTH.setString( 4, BCRUtil.checkEmpty( attributes.get( "SHORTESTDIMENSION" ) ) );
                insertSTH.setString( 5, BCRUtil.checkEmpty( attributes.get( "INITIALWEIGHT" ) ) );                
                if(BCRUtil.checkEmptyFloat( attributes.get( "CURRENTWEIGHT" ) ) == null) {
                    insertSTH.setString( 6, null );
                } else {
                    insertSTH.setFloat( 6, BCRUtil.checkEmptyFloat( attributes.get( "CURRENTWEIGHT" ) ) );
                }
                insertSTH.setString( 7, BCRUtil.checkEmpty( attributes.get( "FREEZINGMETHOD" ) ) );
                insertSTH.setString( 8, BCRUtil.checkEmpty( attributes.get( "OCTEMBEDDED" ) ) );

                setInteger("DAYSTOCOLLECTION", 9);

                insertSTH.setString( 10, BCRUtil.checkEmpty( attributes.get( "TIMEBETWEENCLAMPINGANDFREEZING" ) ) );
                insertSTH.setString( 11, BCRUtil.checkEmpty( attributes.get( "TIMEBETWEENEXCISIONANDFREEZING" ) ) );
                insertSTH.setString( 12, BCRUtil.checkEmpty( attributes.get( "BCRSAMPLEBARCODE" ) ) );
                insertSTH.setInt( 13, patientID );
                System.out.println( "BCRSAMPLEBARCODE: " + attributes.get( "BCRSAMPLEBARCODE" ) );
                insertSTH.executeUpdate();
                selectSTH.setString( 1, attributes.get( "BCRSAMPLEBARCODE" ) );
                rs = selectSTH.executeQuery();
                rs.next();
                sampleID = rs.getInt( 1 );
                rs.close();
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.exit( -1 );
        }
        return sampleID;
    }

    protected void prepareInsertStatement() throws SQLException {
        if(insertSTH == null) {
            insertSTH = ClinicalBean.dbConnection.prepareStatement( insertSQL );
        }
        if(selectSTH == null) {
            selectSTH = ClinicalBean.dbConnection.prepareStatement( selectSQL );
        }
    }

    public int getSampleID() {
        return sampleID;
    }

    public TumorPathology getTumorPathology() {
        return tumorPathology;
    }

    public void setTumorPathology( final TumorPathology tumorPathology ) {
        this.tumorPathology = tumorPathology;
    }
}
