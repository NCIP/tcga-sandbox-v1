/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

public class DataFileLevelOne extends DataFile {

    public DataFileLevelOne() {
        setLevel( "1" );
        //level 1 files are always permanent files pulled from file system
        setPermanentFile( true );
    }
}
