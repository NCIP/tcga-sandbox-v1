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
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Jan 24, 2009
 * Time: 12:40:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataFileLevelTwo extends DataFileLevelTwoThree implements Comparable<DataFile> {

    public DataFileLevelTwo() {
        setLevel( "2" );
    }
    public int compareTo(final DataFile dataFile) {
        return this.getFileName().compareToIgnoreCase(dataFile.getFileName());
    }
}
