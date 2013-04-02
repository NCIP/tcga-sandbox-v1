/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader;

import gov.nih.nci.ncicb.tcga.dcc.common.service.FileTypeLookup;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO add class javadoc description
 *
 * @author David Nassau
 * @version $Rev$
 */
public class CommandLineFileTypeLookup implements FileTypeLookup {
    private String archive, mage;
    private Map<String,String> fileTypesMappedToNames = new HashMap<String,String>();

    public CommandLineFileTypeLookup(String archive, String mage) {
        this.archive = archive;
        this.mage = mage;
    }

    public String getArchive() {
        return archive;
    }

    public String getMage() {
        return mage;
    }

    public void addFile(String filename, String filetype) {
        fileTypesMappedToNames.put(filename, filetype);
    }

    public String lookupFileType(final String filename, final String center, final String platform) {
        return fileTypesMappedToNames.get(filename);
    }


}
