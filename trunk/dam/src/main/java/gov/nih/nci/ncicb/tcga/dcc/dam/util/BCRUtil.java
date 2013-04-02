/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author HickeyE
 * @version $id$
 */
public class BCRUtil {

    public static java.sql.Date convertDate( final String s ) {
        if(s == null || s.equalsIgnoreCase( "" )) {
            return null;
        }
        DateFormat df = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss.S" );
        if(s.matches( "^\\d\\d\\d\\d-\\d\\d-\\d\\d$" )) {
            df = new SimpleDateFormat( "yyyy-MM-dd" );
        }
        final java.util.Date d;
        java.sql.Date dd = null;
        try {
            d = df.parse( s );
            dd = new java.sql.Date( d.getTime() );
        }
        catch(ParseException e) {
            e.printStackTrace();
            System.exit( -1 );
        }
        return dd;
    }

    public static String checkEmpty( String s ) {
        if(s == null || s.equalsIgnoreCase( "" )) {
            s = null;
        }
        return s;
    }

    public static Float checkEmptyFloat( final String s ) {
        Float f = null;
        if(s == null || s.equalsIgnoreCase( "" )) {
            return f;
        }
        f = Float.parseFloat( s );
        return f;
    }

    public static Integer checkEmptyInt( final String s ) {
        Integer i = null;
        if(s == null || s.equalsIgnoreCase( "" )) {
            return i;
        }
        i = new Integer( s );
        return i;
    }
}
