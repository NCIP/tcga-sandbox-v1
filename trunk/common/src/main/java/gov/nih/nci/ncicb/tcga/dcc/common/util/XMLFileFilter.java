/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Julien Baboud
 */
public class XMLFileFilter implements FilenameFilter {

    String ext;

    public XMLFileFilter(String ext) {
        this.ext = "." + ext.toLowerCase();
    }

    public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(ext);
    }
}
