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
import java.sql.SQLException;

/**
 * @author Silpa Nanan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class LungSlide extends DiseaseSlide {

    private static PreparedStatement insertSTH = null;
    private static PreparedStatement selectSTH = null;

    private static final String[] orderedAttributes = new String[0];
    private static final String insertSQL = null;
    private static String selectSQL = null;

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
        return "TYPE/LUNGSLIDE";
    }

    public String getXmlGroupName() {
        return null;
    }

    public String getIdElementName() {
        return null;
    }

    protected String getInsertSQL() {
        return insertSQL;
    }

    protected String getSelectSQL() {
        return selectSQL;
    }


    public int insertSelf( final int slideID ) {
        if (!check( slideID )) {
            // do insert
        }
        return 0;
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
}
