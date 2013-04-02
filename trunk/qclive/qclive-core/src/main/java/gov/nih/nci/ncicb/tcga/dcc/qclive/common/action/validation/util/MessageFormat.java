/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util;

import java.util.ResourceBundle;

/**
 * <code>MessageFormat</code> is a utility class that provides a means to construct 
 * and format messages displayed for end users using {@link java.util.ResourceBundle}s.
 *
 * <p>
 * This implementation version uses its class name as the base name for locating resource 
 * bundles. Future implementations could possibly extend this behavior to include other
 * namespaces to for loading resources.
 * 
 * <p>
 * The primary resource bundle for this class is backed by the <code>MessageFormat.properties</code> 
 * file located in the same package as this class. The property keys for this properties file
 * are enumerated in the {@link MessagePropertyType} enum. Each method defined in <code>MessageFormat</code>
 * class will only respect the keys in {@link MessagePropertyType}.
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 * @version 
 */
public class MessageFormat {
	
	/** Default base name for retrieving a {@link java.util.ResourceBundle}. **/
	public static final String DEFAULT_BASE_NAME = MessageFormat.class.getName();
    
    /**
     * Loads a text resource from a {@link java.util.ResourceBundle} using one of the property keys defined in the enum 
     * {@link MessagePropertyType} and formats it with the specified arguments. The resource bundle created by this method
     * uses the {@link #DEFAULT_BASE_NAME} for loading properties.
     * 
     * @param messagePropertyType - a property name whose value is specified those defined in the enum {@link MessagePropertyType}
     * @param args - a variable number of arguments that should be replaced within the formatted string 
     * @return a formatted string derived from the specified message property type and argument(s)
     */
    public static String format(MessagePropertyType messagePropertyType, Object ... args) {
    	// Get the resource bundle using the default base name
    	ResourceBundle messageProperties = ResourceBundle.getBundle(DEFAULT_BASE_NAME);
    	
    	// Retrieve the string specified by messagePropertyType that will be used for formatting
        String formatString = messageProperties.getString(messagePropertyType.getPropertyValue());
        
        // Return the formatted string
        return java.text.MessageFormat.format(formatString, args);
    }
}
