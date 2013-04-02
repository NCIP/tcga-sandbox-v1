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
 * Interface for SDRF implementations
 *
 * @author Robert S. Sfeir
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public interface TabDelimitedContent {

    void setTabDelimitedContents( Map<Integer, String[]> tabDelimitedContents );

    Map<Integer, String[]> getTabDelimitedContents();

    void setTabDelimitedHeader( String[] headerValues );

    String[] getTabDelimitedHeaderValues();
}