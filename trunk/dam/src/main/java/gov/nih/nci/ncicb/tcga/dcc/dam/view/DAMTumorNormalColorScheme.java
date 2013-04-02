/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.StaticLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DAMResourceBundle;
import org.apache.log4j.Level;

import java.awt.*;

/**
 * Author: David Nassau
 * <p/>
 * Implements colors for tissue type.
 */
public class DAMTumorNormalColorScheme extends DAMColorScheme {

    public static Color COLOR_TUMOR_WITH_MATCHED_NORMAL = new Color( 0x33CCFF ); //Color(0x0000ff);
    public static Color COLOR_TUMOR_WITHOUT_MATCHED_NORMAL = new Color( 0x33FFFF ); //Color(0x3366CC);
    public static Color COLOR_NORMAL_WITH_MATCHED_TUMOR = new Color( 0xFFFF33 ); //Color(0xffcc99);
    public static Color COLOR_NORMAL_WITHOUT_MATCHED_TUMOR = new Color( 0xFFCC33 );
    public static Color COLOR_CELL_LINE_CONTROL = new Color(0xDDDDDD);
    public static Color COLOR_NONE = new Color( 0xffffff );

    // made public for usage logger
    public String getName() {
        return "Tumor/Normal";
    }

    public Color getColorForCell( Cell cell ) {
        Color ret = COLOR_NONE;
        if(cell.getAvailability().equals( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE )) {
            String tumorNormal = cell.getTumorNormal();
            if(tumorNormal == null) {
                ret = COLOR_NONE;
            } else if(tumorNormal.equals( DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL )) {
                ret = COLOR_TUMOR_WITH_MATCHED_NORMAL;
            } else if(tumorNormal.equals( DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITHOUT_MATCHED_NORMAL )) {
                ret = COLOR_TUMOR_WITHOUT_MATCHED_NORMAL;
            } else if(tumorNormal.equals( DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR )) {
                ret = COLOR_NORMAL_WITH_MATCHED_TUMOR;
            } else if(tumorNormal.equals( DataAccessMatrixQueries.TUMORNORMAL_HEALTHY_TISSUE_CONTROL)) {
                ret = COLOR_NORMAL_WITHOUT_MATCHED_TUMOR;
            } else if (tumorNormal.equals(DataAccessMatrixQueries.TUMORNORMAL_CELL_LINE_CONTROL)) {
                ret = COLOR_CELL_LINE_CONTROL;
            } else {
                StaticLogger.getInstance().logToLogger( Level.ERROR, "Unexpected tumor/normal type: " + tumorNormal );
            }
        }
        return ret;
    }


    protected String getLetterCodeForCell( Cell cell ) {
        String ret = "";
        if(cell.getAvailability().equals( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE )) {
            ret = cell.getTumorNormal();
        }
        return ret;
    }

    protected String[][] getLegend() {
        String[][] ret = new String[6][3];
        int i = 0;
        ret[i][0] = DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITH_MATCHED_NORMAL;
        ret[i][1] = DAMColorScheme.convertToHTMLColor( COLOR_TUMOR_WITH_MATCHED_NORMAL );
        ret[i++][2] = DAMResourceBundle.getMessage("legend.TumorMatchedNormal");//"Tumor, Matched Normal";
        ret[i][0] = DataAccessMatrixQueries.TUMORNORMAL_TUMOR_WITHOUT_MATCHED_NORMAL;
        ret[i][1] = DAMColorScheme.convertToHTMLColor( COLOR_TUMOR_WITHOUT_MATCHED_NORMAL );
        ret[i++][2] = DAMResourceBundle.getMessage("legend.TumorNoMatchedNormal"); // "Tumor, no Matched Normal";
        ret[i][0] = DataAccessMatrixQueries.TUMORNORMAL_NORMAL_WITH_MATCHED_TUMOR;
        ret[i][1] = DAMColorScheme.convertToHTMLColor( COLOR_NORMAL_WITH_MATCHED_TUMOR );
        ret[i++][2] = DAMResourceBundle.getMessage("legend.NormalMatchedTumor"); //"Normal, Matched Tumor";
        ret[i][0] = DataAccessMatrixQueries.TUMORNORMAL_HEALTHY_TISSUE_CONTROL;
        ret[i][1] = DAMColorScheme.convertToHTMLColor( COLOR_NORMAL_WITHOUT_MATCHED_TUMOR );
        ret[i++][2] = DAMResourceBundle.getMessage("legend.NormalNoMatchedTumor"); //"Normal, no Matched Tumor";
        ret[i][0] = DataAccessMatrixQueries.TUMORNORMAL_CELL_LINE_CONTROL;
        ret[i][1] = DAMColorScheme.convertToHTMLColor( COLOR_CELL_LINE_CONTROL);
        ret[i++][2] = DAMResourceBundle.getMessage("legend.CellLineControl");
        ret[i][0] = "&nbsp;";
        ret[i][1] = DAMColorScheme.convertToHTMLColor( COLOR_NONE );
        ret[i++][2] = DAMResourceBundle.getMessage("legend.NoAvailableDataset"); //"No Available Dataset";
        return ret;
    }
}
