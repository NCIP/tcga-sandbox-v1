/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.util;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: May 18, 2009
 * Time: 3:16:48 PM
 * To change this template use File | Settings | File Templates.
 */
//Logger singleton which we need because ProcessLogger is not serializable, so we can't
// include as a variable in the Loader
public class Logger {

    private static ProcessLogger plog = new ProcessLogger();

    public static ProcessLogger getLogger() {
        return plog;
    }
}
