/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

/**
 * Subclass of DataFileLevelTwoThree that is just for Level 3 files.  Makes sure they are never protected.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataFileLevelThree extends DataFileLevelTwoThree {

    public DataFileLevelThree() {
        setLevel( "3" );
    }

    /**
     * Sets protected status to false no matter what is passed in
     *
     * @param isProtected is ignored
     */
    public void setProtected( boolean isProtected ) {
        super.setProtected( false );
    }
}
