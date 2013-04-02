/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.DataAccessMatrixQueries;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.Header;
import org.apache.log4j.Level;

import javax.servlet.ServletContext;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: David Nassau
 */
public class DataAccessMatrixJSPUtil {

    public static final int DAFP_POLLING_INTERVAL = 30000; //milliseconds
    private static Map<String, String> centerNames;
    private static Map<String, String> platformNames;
    private static Map<String, String> platformTypeNames;
    private static Map<String, String> centerDisplayNames;
    private static Map<String, Integer> platformTypeSortOrder;
    private static final ProcessLogger plog = new ProcessLogger();
    private static boolean storedLookups;

    public static final String ATTRIBUTE_KEY_DATATYPES = "datatypes";
    public static final String ATTRIBUTE_KEY_CENTERS = "centers";
    public static final String ATTRIBUTE_KEY_PLATFORMS = "platforms";
    public static final String FIELD_PLATFORM_ID = "platform_id";
    public static final String FIELD_PLATFORM_NAME = "platform_name";
    public static final String FIELD_CENTER_ID = "center_id";
    public static final String FIELD_CENTER_SHORT_NAME = "short_name";
    public static final String FIELD_CENTER_DISPLAY_NAME = "display_name";
    public static final String PSEUDO_CENTER_BIOTAB = "Biotab";
    public static final String PSEUDO_CENTER_XML = "XML";
    public static final String FIELD_DATA_TYPE_ID = "data_type_id";
    public static final String FIELD_DATA_TYPE_NAME = "name";
    public static final String PSEUDO_DATATYPE_CLINICAL = "Clinical";
    public static final String FIELD_DATA_TYPE_SORT_ORDER = "sort_order";

    //when called from JSPs, it passes the servlet context which contains the lookup values.
    //Thereafter it will store those lookup values in static structures and not depend on the servlet
    public static String lookupHeaderText( final Header.HeaderCategory category, final String id,
                                           final ServletContext servlet ) {
        String headerText;
        try {
            storeLookups( servlet );
            headerText = lookupHeaderText( category, id );
        }
        catch(Exception e) {
            plog.logToLogger( Level.ERROR, ProcessLogger.stackTracePrinter( e ) );
            //anything goes wrong, just return the original undecorated value
            headerText = id;
        }
        return headerText;
    }

    //this overloaded added for unit-test purposes, where we don't have a servlet context.
    public static String lookupHeaderText( final Header.HeaderCategory category, final String id,
                                           final Map<String, List> lookups ) {
        String headerText;
        try {
            storeLookups( lookups );
            headerText = lookupHeaderText( category, id );
        }
        catch(IllegalStateException e) {
            plog.logToLogger( Level.ERROR, ProcessLogger.stackTracePrinter( e ) );
            //anything goes wrong, just return the original undecorated value
            headerText = id;
        }
        return headerText;
    }

    //When called from an external class, assumes we already called it once with the servlet context
    public static String lookupHeaderText( final Header.HeaderCategory category, final String id ) {
        String ret = id;
        if(!storedLookups) {
            plog.logToLogger( Level.ERROR, "Called lookupHeaderText without first setting lookup values, returning raw id values back" );
        } else {
            if(category == Header.HeaderCategory.Center) {
                //center id includes platform: parse it out
                final int pos = id.indexOf( "." );
                if(pos >= 0) { //contains both center:platform
                    final String center = id.substring( 0, pos );
                    final String platform = id.substring( pos + 1 );
                    ret = lookupCenterName( center );
                    if(platform.length() > 0) {
                        ret += ( " (" + lookupPlatformName( platform ) + ")" );
                    }
                } else {  //just center
                    ret = lookupCenterName( id );
                }
            } else if(category == Header.HeaderCategory.PlatformType) {
                ret = lookupPlatformTypeName( id );
            } else if(category == Header.HeaderCategory.Platform) {
                ret = lookupPlatformName( id );
            }
        }
        return ret;
    }

    public static void storeLookups( final ServletContext servlet ) throws IllegalStateException {
        if(!storedLookups) {
            final List platformTypes = (List) servlet.getAttribute(ATTRIBUTE_KEY_DATATYPES);
            if(platformTypes == null) {
                throw new IllegalStateException( "No 'datatypes' attribute found in servlet context" );
            }
            final List centers = (List) servlet.getAttribute(ATTRIBUTE_KEY_CENTERS);
            if(centers == null) {
                throw new IllegalStateException( "No 'centers' attribute found in servlet context" );
            }
            final List platforms = (List) servlet.getAttribute(ATTRIBUTE_KEY_PLATFORMS);
            if(platforms == null) {
                throw new IllegalStateException( "No 'platforms' attribute found in servlet context" );
            }
            storePlatformTypeNames( platformTypes );
            storePlatformTypeSortOrder( platformTypes );
            storeCenterNames( centers );
            storePlatformNames( platforms );
            storedLookups = true;
        }
    }

    public static void storeLookups( final Map<String, List> lookups ) throws IllegalStateException {
        if(!storedLookups) {
            final List platformTypes = lookups.get(ATTRIBUTE_KEY_DATATYPES);
            if(platformTypes == null) {
                throw new IllegalStateException( "No 'datatypes' attribute found in lookups" );
            }
            final List centers = lookups.get(ATTRIBUTE_KEY_CENTERS);
            if(centers == null) {
                throw new IllegalStateException( "No 'centers' attribute found in lookups" );
            }
            final List platforms = lookups.get(ATTRIBUTE_KEY_PLATFORMS);
            if(platforms == null) {
                throw new IllegalStateException( "No 'platforms' attribute found in lookups" );
            }
            storePlatformTypeNames( platformTypes );
            storePlatformTypeSortOrder( platformTypes );
            storeCenterNames( centers );
            storePlatformNames( platforms );
            storedLookups = true;
        }
    }

    private static void storePlatformNames( final List platforms ) {
        platformNames = new HashMap<String, String>();
        for(int i = 0; i < platforms.size(); i++) {
            final Map platformMap = (Map) platforms.get( i );
            final String id = ( platformMap.get(FIELD_PLATFORM_ID) ).toString();
            platformNames.put( id, (String) platformMap.get(FIELD_PLATFORM_NAME) );
        }
    }

    public static String lookupPlatformName( final String id ) throws IllegalStateException {
        if(platformNames == null) {
            return id;
        }
        String ret = platformNames.get( id );
        if(ret == null) {
            //just return the original value back
            ret = id;
        }
        return ret;
    }

    private static void storeCenterNames( final List centers ) {
        centerNames = new HashMap<String, String>();
        centerDisplayNames = new HashMap<String, String>();
        for(int i = 0; i < centers.size(); i++) {
            final Map centerMap = (Map) centers.get( i );
            final String id = ( centerMap.get(FIELD_CENTER_ID) ).toString();
            centerNames.put( id, (String) centerMap.get(FIELD_CENTER_SHORT_NAME) );
            centerDisplayNames.put( id, (String) centerMap.get(FIELD_CENTER_DISPLAY_NAME) );
        }
        //add "pseudo" centers for clinical
        centerNames.put( DataAccessMatrixQueries.CLINICAL_XML_CENTER, PSEUDO_CENTER_XML);
        centerDisplayNames.put( DataAccessMatrixQueries.CLINICAL_XML_CENTER, PSEUDO_CENTER_XML );
        centerNames.put( DataAccessMatrixQueries.CLINICAL_BIOTAB_CENTER, PSEUDO_CENTER_BIOTAB);
        centerDisplayNames.put( DataAccessMatrixQueries.CLINICAL_BIOTAB_CENTER, PSEUDO_CENTER_BIOTAB);
    }

    public static String lookupCenterName( final String id ) throws IllegalStateException {
        if(centerNames == null) {
            return id;
        }
        String ret = centerNames.get( id );
        if(ret == null) {
            //just return the original value back
            ret = id;
        }
        return ret;
    }

    public static String lookupCenterDisplayName( final String id ) throws IllegalStateException {
        if(centerDisplayNames == null) {
            return id;
        }
        String ret = centerDisplayNames.get( id );
        if(ret == null) {
            //just return the original value back
            ret = id;
        }
        return ret;
    }

    private static void storePlatformTypeNames( final List platformTypes ) {
        platformTypeNames = new HashMap<String, String>();
        for(int i = 0; i < platformTypes.size(); i++) {
            final Map platformTypeMap = (Map) platformTypes.get( i );
            final String id = ( platformTypeMap.get(FIELD_DATA_TYPE_ID) ).toString();
            platformTypeNames.put( id, (String) platformTypeMap.get(FIELD_DATA_TYPE_NAME) );
        }
        //add a "pseudo" platform type for clinical
        platformTypeNames.put( DataAccessMatrixQueries.CLINICAL_PLATFORMTYPE, PSEUDO_DATATYPE_CLINICAL);
    }

    private static void storePlatformTypeSortOrder( final List platformTypes ) {
        platformTypeSortOrder = new HashMap<String, Integer>();
        for(int i = 0; i < platformTypes.size(); i++) {
            final Map platformTypeMap = (Map) platformTypes.get( i );
            final String id = ( platformTypeMap.get(FIELD_DATA_TYPE_ID) ).toString();
            platformTypeSortOrder.put( id, convertToInteger(platformTypeMap.get(FIELD_DATA_TYPE_SORT_ORDER)));
        }
    }

    private static Integer convertToInteger(final Object object) {
        if (object == null) {
            return 0;
        } else if (object instanceof Number) {
            return ((Number)object).intValue();
        } else {
            return Integer.valueOf(object.toString());
        }
    }

    public static String lookupPlatformTypeName( final String id ) throws IllegalStateException {
        if(platformTypeNames == null) {
            return id;
        }
        String ret = platformTypeNames.get( id );
        if(ret == null) {
            //just return the original value back
            ret = id;
        }
        return ret;
    }

    //to resolve an image file name, we sometimes need to take out punctuation
    public static String removePunctuation( String s ) {
        s = s.replace( "/", "" );
        s = s.replace( "(", "" );
        s = s.replace( ")", "" );
        s = s.replace( " ", "_" );
        s = s.replace( "_&_", "_and_" );
        return s;
    }

    public static String darkenColor( String htmlColor ) {
        String ret = htmlColor;
        try {
            if(htmlColor.startsWith( "#" )) {
                Color c = Color.decode( htmlColor );
                c = c.darker();
                ret = "#" + Integer.toHexString( c.getRGB() & 0x00ffffff );
            }
        }
        catch(NumberFormatException ne) {
            plog.logError( ne );
        }
        return ret;
    }

    //used to sort lookups alphabetically for getAllLookups
    static class LookupComparator implements Comparator {

        public int compare( Object o1, Object o2 ) {
            String s1 = ( (String[]) o1 )[0];
            String s2 = ( (String[]) o2 )[0];
            return s1.compareTo( s2 );
        }
    }

    //used to export all lookup values so external application knows how to formulate request
    public static Map<String, List<String[]>> getAllLookups() {
        Map<String, List<String[]>> ret = new HashMap<String, List<String[]>>();
        List<String[]> ptList = new ArrayList<String[]>();
        for(String key : platformTypeNames.keySet()) {
            String[] s = new String[2];
            s[1] = key;
            s[0] = platformTypeNames.get( key );
            ptList.add( s );
        }
        Collections.sort( ptList, new LookupComparator() );
        List<String[]> centerList = new ArrayList<String[]>();
        for(String key : centerNames.keySet()) {
            String[] s = new String[2];
            s[1] = key;
            s[0] = centerNames.get( key );
            centerList.add( s );
        }
        Collections.sort( centerList, new LookupComparator() );
        List<String[]> plList = new ArrayList<String[]>();
        for(String key : platformNames.keySet()) {
            String[] s = new String[2];
            s[1] = key;
            s[0] = platformNames.get( key );
            plList.add( s );
        }
        Collections.sort( plList, new LookupComparator() );
        ret.put( "PlatformType", ptList );
        ret.put( "Center", centerList );
        ret.put( "Platform", plList );
        return ret;
    }
}
