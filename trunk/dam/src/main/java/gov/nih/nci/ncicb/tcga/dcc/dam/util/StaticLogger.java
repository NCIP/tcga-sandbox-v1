/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.util;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;

/**
 * This class exists solely for the purpose of providing a singleton logger instance
 * that other classes can use so they don't have to create they own copy an incur the
 * memory cost of the character buffer.
 * The alternative would be to inject a logger instance from spring, but some of the
 * classes that need it are not defined as beans, making that impractical
 */
//todo: consider having the StaticLogger class itself provide the singleton
public class StaticLogger {

    private static ProcessLogger instance;

    public static ProcessLogger getInstance() {
        if(instance == null) {
            instance = new ProcessLogger();
        }
        return instance;
    }
}
