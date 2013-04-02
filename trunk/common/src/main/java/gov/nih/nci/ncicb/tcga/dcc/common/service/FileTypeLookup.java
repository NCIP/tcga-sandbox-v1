/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.service;

import java.io.Serializable;
/**
 * Interface for classes that provide facilities for looking up file types.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

/**
 * Callback interface to supply Loader with file type, level, and platform type information about
 * each data file.  If the file name does not indicate a data file, return null.
 */
public interface FileTypeLookup extends Serializable {

/*    public class DataFileType {

        public String fileType;
        public int dataLevel;
        public String platformType;
    }*/

    /**
     * The Loader will call this method for each file it thinks might be a data file.  If it is a
     * data file, return a new DataFileType object; if not, return null.  Assumption is that
     * null is returned for any non-level2 file.
     * 
     * @param filename the filename
     * @param center the center name
     * @param platform the platform name
     * @return the DataFileType
     */
    String lookupFileType( String filename, String center, String platform );
}
