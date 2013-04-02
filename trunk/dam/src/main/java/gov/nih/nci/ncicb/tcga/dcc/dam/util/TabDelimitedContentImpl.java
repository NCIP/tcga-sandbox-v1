/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import java.util.Map;

/**
 * Object class representing various Tab Delimited data values.  This is a pojo and should not be doing any processing.
 * Set and get methods are there so that they hold data which is used throughout the app.
 *
 * @author Robert S. Sfeir
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class TabDelimitedContentImpl implements TabDelimitedContent {

    private Map<Integer, String[]> tabDelimitedContents;
    private String[] headerValues;

    public void setTabDelimitedContents( final Map<Integer, String[]> tabDelimitedContents ) {
        this.tabDelimitedContents = tabDelimitedContents;
    }

    public Map<Integer, String[]> getTabDelimitedContents() {
        return tabDelimitedContents;
    }

    public void setTabDelimitedHeader( final String[] headerValues ) {
        this.headerValues = headerValues;
    }

    public String[] getTabDelimitedHeaderValues() {
        return headerValues;
    }
}