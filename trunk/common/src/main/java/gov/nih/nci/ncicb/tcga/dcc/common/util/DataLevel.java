/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.common.util;

/**
 * Data levels enumeration
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public enum DataLevel {

    Level0(0),
    Level1(1),
    Level2(2),
    Level3(3),
    Level4(4);

    private int level;

    DataLevel(final int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }
}
