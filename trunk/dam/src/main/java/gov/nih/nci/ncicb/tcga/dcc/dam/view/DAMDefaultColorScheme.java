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
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DAMResourceBundle;

import java.awt.*;

/**
 * Author: David Nassau
 * <p/>
 * Implements colors for Availability.
 */
public class DAMDefaultColorScheme extends DAMColorScheme {

    public static final String COLORSCHEMENAME = "Availability";
    public static Color COLOR_AVAILABLE = new Color( 0x99ff66 );
    public static Color COLOR_PENDING = new Color( 0xffff00 );
    public static Color COLOR_NOTAVAILABLE = new Color( 0x99ccff );
    public static Color COLOR_NOTAPPLICABLE = new Color( 0xffffff );

    public DAMDefaultColorScheme() {
        super();
    }

    // made this public for usage logger
    public String getName() {
        return COLORSCHEMENAME;
    }

    public Color getColorForCell( Cell cell ) {
        Color ret = null;
        String availability = cell.getAvailability();
        if(availability == null) {
            ret = COLOR_NOTAPPLICABLE;
        } else if(availability.equals( DataAccessMatrixQueries.AVAILABILITY_AVAILABLE )) {
            ret = COLOR_AVAILABLE;
        } else if(availability.equals( DataAccessMatrixQueries.AVAILABILITY_PENDING )) {
            ret = COLOR_PENDING;
        } else if(availability.equals( DataAccessMatrixQueries.AVAILABILITY_NOTAVAILABLE )) {
            ret = COLOR_NOTAVAILABLE;
        } else if(availability.equals( DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE )) {
            ret = COLOR_NOTAPPLICABLE;
        }
        return ret;
    }

    protected String getLetterCodeForCell( Cell cell ) {
        String ret = cell.getAvailability();
        if(ret.equals( DataAccessMatrixQueries.AVAILABILITY_NOTAPPLICABLE )) {
            ret = "";
        }
        return ret;
    }

    protected String[][] getLegend() {
        String[][] ret = new String[4][3];
        int i = 0;
        ret[i][0] = DataAccessMatrixQueries.AVAILABILITY_AVAILABLE;
        ret[i][1] = DAMColorScheme.convertToHTMLColor( COLOR_AVAILABLE );  // "Available";
        ret[i++][2] = DAMResourceBundle.getMessage("legend.Available");
        ret[i][0] = DataAccessMatrixQueries.AVAILABILITY_PENDING;
        ret[i][1] = DAMColorScheme.convertToHTMLColor( COLOR_PENDING ); // "Pending";
        ret[i++][2] = DAMResourceBundle.getMessage("legend.Pending");
        ret[i][0] = DataAccessMatrixQueries.AVAILABILITY_NOTAVAILABLE;
        ret[i][1] = DAMColorScheme.convertToHTMLColor( COLOR_NOTAVAILABLE ); // "Not Available";
        ret[i++][2] = DAMResourceBundle.getMessage("legend.NotAvailable");
        ret[i][0] = "&nbsp;";
        ret[i][1] = DAMColorScheme.convertToHTMLColor( COLOR_NOTAPPLICABLE ); // "Not Applicable";
        ret[i++][2] = DAMResourceBundle.getMessage("legend.NotApplicable");
        return ret;
    }

    
}
