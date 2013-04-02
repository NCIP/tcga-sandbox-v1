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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author HickeyE
 * @version $id$
 */
public class Patient extends ClinicalBean {

    private static final String[] orderedAttributes = {
            "TUMORTISSUESITE",
            "HISTOLOGICALTYPE",
            "PRIORGLIOMA",
            "GENDER",
            "VITALSTATUS",
            "DAYSTOBIRTH",
            "DAYSTODEATH",
            "DAYSTOLASTFOLLOWUP",            
            "RACE",
            "BCRPATIENTBARCODE",
            "PRETREATMENTHISTORY",
            "RADIATIONTHERAPY",
            "INFORMEDCONSENTVERIFIED",
            "AGEATINITIALPATHOLOGICDIAGNOSIS",
            "CHEMOTHERAPY",
            "IMMUNOTHERAPY",
            "HORMONALTHERAPY",
            "TARGETEDMOLECULARTHERAPY",
            "DAYSTOTUMORPROGRESSION",
            "DAYSTOTUMORRECURRENCE",            
            "SITEOFTUMORFIRSTRECURRENCE",
            "TUMORSTAGE",
            "TUMORGRADE",
            "RESIDUALTUMOR",
            "TUMORRESIDUALDISEASE",
            "PRIMARYTHERAPYOUTCOMESUCCESS",
            "ETHNICITY",
            "JEWISHORIGIN",
            "ADDITIONALRADIATIONTHERAPY",
            "ADDITIONALCHEMOTHERAPY",
            "ADDITIONALIMMUNOTHERAPY",
            "ADDITIONALHORMONETHERAPY",
            "ADDITIONALDRUGTHERAPY",
            "ANATOMICORGANSUBDIVISION",
            "INITIALPATHOLOGICDIAGNOSISMETHOD",
            "PERSONNEOPLASMCANCERSTATUS"
    };
    private int patientID;
    private Set<Sample> samples = new HashSet<Sample>();

    private static final String selectSQL = "select patient_id from PATIENT where BCRPATIENTBARCODE = ?";
    private static PreparedStatement selectSTH = null;
    private static PreparedStatement insertSTH = null;
    private Date dateAdded;

    public static int getPatientIdForBarcode(final String patientBarcode) throws SQLException {
        selectSTH.setString(1, patientBarcode);
        final ResultSet rs = selectSTH.executeQuery();
        int patientID = -1;
        if (rs.next()) {
            patientID = rs.getInt( 1 );
            rs.close();
        }
        return patientID;
    }

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
        return "PATIENT";
    }

    public String getXmlGroupName() {
        return null;
    }

    public String getIdElementName() {
        return "BCRPATIENTBARCODE";
    }

    public ClinicalBean makeClone( ) throws IllegalAccessException, InstantiationException {
        Patient clone = (Patient) super.makeClone();
        clone.setDateAdded( dateAdded );
        return clone;
    }

    public int insertSelf(int id) {
        try {
            for (int i=0; i<orderedAttributes.length; i++) {
                String value = attributes.get(orderedAttributes[i]);
                if (orderedAttributes[i].contains( "DAYSTO" )) {
                    // attributes with DAYSTO in them are integers, not strings
                    Integer val = BCRUtil.checkEmptyInt( value );
                    if (val == null) {
                        insertSTH.setString( i+1, null );
                    } else {
                        insertSTH.setInt( i+1, val);
                    }
                } else {
                    insertSTH.setString( i+1, BCRUtil.checkEmpty( value ));
                }
            }
            insertSTH.setString(orderedAttributes.length + 1, BCRUtil.checkEmpty( attributes.get("ARCHIVE_NAME") ));
            insertSTH.setString(orderedAttributes.length + 2, BCRUtil.checkEmpty( attributes.get("DISEASE") ));
            insertSTH.setDate( orderedAttributes.length + 3, new java.sql.Date( dateAdded.getTime() ) );
            insertSTH.executeUpdate();

            patientID = getPatientIdForBarcode(attributes.get("BCRPATIENTBARCODE"));
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.exit( -1 );
        }
        return patientID;
    }

    protected void prepareInsertStatement() throws SQLException {
        if (insertSTH == null) {
            StringBuilder str = new StringBuilder("insert into PATIENT (patient_id, ");
            for (String attribute : orderedAttributes) {
                if(attribute.equals( "PERSONNEOPLASMCANCERSTATUS" )) {
                    attribute = "PERSONNEOPLASMSTATUS"; // different name in XML and DB
                } else if(attribute.equals("INITIALPATHOLOGICDIAGNOSISMETHOD")) {
                    attribute = "INITIALPATHDIAGNOSISMETHOD";
                } else if (attribute.equals("AGEATINITIALPATHOLOGICDIAGNOSIS")) {
                    attribute = "AGEATINITPATHOLOGICDIAGNOSIS";
                }

                str.append(attribute).append(", ");
            }
            str.append("archive_name, disease_name, dcc_date_added) values(PATIENT_patient_id_SEQ.NEXTVAL, ");
            //noinspection UnusedDeclaration
            for(String orderedAttribute : orderedAttributes) {
                str.append("?, ");
            }
            str.append("?, ?, ?)");

            insertSTH = ClinicalBean.dbConnection.prepareStatement( str.toString() );
        }
        if(selectSTH == null) {
            selectSTH = ClinicalBean.dbConnection.prepareStatement( selectSQL );
        }
    }

    public void addSample( final Sample s ) {
        samples.add( s );
    }

    public Set<Sample> getSamples() {
        return samples;
    }

    public void setSamples( final HashSet<Sample> samples ) {
        this.samples = samples;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(final int id) {
        patientID = id;
    }

    public void setDateAdded( final Date dateAdded ) {
        this.dateAdded = dateAdded;
    }
}
