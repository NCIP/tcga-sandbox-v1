/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

/**
 * Description : Class used to get messages from the resource bundle files
 *
 * @author Namrata Rane Last updated by: $Author$
 * @version $Rev$
 */

public class DAMResourceBundle {

    private static ResourceBundleMessageSource resourceBundle;

    public void setResourceBundle(final ResourceBundleMessageSource resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public static String getMessage(String code) {
         return resourceBundle.getMessage(code, null, DAMResourceBundle.getLocale());
    }

    /**
     * Returns the header object for a given header Id.
     *
     * @param code key to be used to find resource bundle entry
     * @param text text to be used if code is not found in the resource bundle file
     *
     * @return resource bundle text
     */
    public static String getMessage(String code, String text) {
        return resourceBundle.getMessage(code, null, text, DAMResourceBundle.getLocale());
    }

    public static Locale getLocale() {
        return Locale.getDefault();
    }


}
