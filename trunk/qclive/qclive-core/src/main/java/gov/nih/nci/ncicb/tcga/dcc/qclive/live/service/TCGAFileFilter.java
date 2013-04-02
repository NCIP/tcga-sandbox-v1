/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Robert S. Sfeir
 */
public class TCGAFileFilter implements FilenameFilter {

    private List<String> fileExtensions = new ArrayList<String>();

    public void setFileExtensions( final List<String> fileExtensions ) {
        this.fileExtensions = fileExtensions;
    }

    public boolean accept( final File dir, final String name ) {
        boolean matches = false;
        for (String fileExtension : fileExtensions) {
            if (name.toLowerCase().endsWith(fileExtension.toLowerCase())) {
                matches = true;
            }
        }
        return matches;
    }
}
