/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: David Nassau
 * <p/>
 * Abstract class from which different color schemes are derived.  The concrete classes
 * specify colors for each cell.
 */
public abstract class DAMColorScheme {

    public static final String DEFAULT_COLOR_SCHEME = DAMDefaultColorScheme.COLORSCHEMENAME; //"Availability";
    private static Map<String, DAMColorScheme> colorCodeModels = new HashMap<String, DAMColorScheme>();
    private static List<String> colorCodeModelNames = new ArrayList<String>();

    public static void registerColorScheme( String name, DAMColorScheme model ) {
        colorCodeModels.put( name, model );
        colorCodeModelNames.add( name ); //keep separate list so we show them in order
    }

    public static DAMColorScheme getColorScheme( String name ) {
        return colorCodeModels.get( name );
    }

    public static List<String> getColorSchemeNames() {
        return colorCodeModelNames;
    }

    public static String convertToHTMLColor( Color c ) {
        String ret;
        if(c == null) {
            ret = "transparent";
        } else {
            ret = "#" + Integer.toHexString( c.getRGB() & 0x00ffffff );
        }
        return ret;
    }

    public DAMColorScheme() {
        DAMColorScheme.registerColorScheme( getName(), this );
    }

    // made this public for usage logger
    public abstract String getName();

    public abstract Color getColorForCell( Cell cell );

    protected abstract String getLetterCodeForCell( Cell cell );

    protected abstract String[][] getLegend();

    public String getHTMLColorForCell( Cell cell ) {
        String ret;
        Color c = getColorForCell( cell );
        if(c == null) {
            ret = "transparent";
        } else {
            ret = convertToHTMLColor( c );
        }
        return ret;
    }
}
