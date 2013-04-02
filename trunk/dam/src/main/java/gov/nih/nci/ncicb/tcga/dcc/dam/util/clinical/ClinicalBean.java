/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util.clinical;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * TODO: Class description
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class ClinicalBean {
    protected static Connection dbConnection;

    public static Pattern VALUE_UNITS_PATTERN = Pattern.compile( "(-?[\\d\\.]+)\\s*(\\S*)" );

    protected final Map<String, String> attributes = new HashMap<String, String>();

    public abstract String[] getOrderedAttributes();

    public abstract String getXmlElementName();

    // may return null if not in a group
    public abstract String getXmlGroupName();

    public abstract String getIdElementName();

    public static void setDBConnection( final Connection dbConnection ) {
        ClinicalBean.dbConnection = dbConnection;
    }

    public static void cleanup() throws SQLException {
        Aliquot.cleanup();
        Analyte.cleanup();
        DNA.cleanup();
        Drug.cleanup();
        Examination.cleanup();
        GBMPathology.cleanup();
        GBMSlide.cleanup();
        LungPathology.cleanup();
        LungSlide.cleanup();
        OvarianPathology.cleanup();
        OvarianSlide.cleanup();
        Patient.cleanup();
        Portion.cleanup();
        PortionSlide.cleanup();
        Protocol.cleanup();
        Radiation.cleanup();
        RNA.cleanup();
        Sample.cleanup();
        Slide.cleanup();
        Surgery.cleanup();
        TumorPathology.cleanup();
    }

    public ClinicalBean() {
        for (String attribute : getOrderedAttributes()) {
            attributes.put( attribute, "" );
        }
        try {
            prepareInsertStatement();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public abstract int insertSelf( final int parentId );

    protected abstract void prepareInsertStatement() throws SQLException;

    public ClinicalBean makeClone( ) throws IllegalAccessException, InstantiationException {
        ClinicalBean clone = this.getClass().newInstance();
        for (String attribute : attributes.keySet()) {
            clone.setAttribute( attribute, attributes.get( attribute ));
        }
        return clone;
    }


    public void setAttribute( final String patientAttribute, final String textContent ) {
        attributes.put( patientAttribute, textContent );
    }

}
