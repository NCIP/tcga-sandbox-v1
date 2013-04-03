/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.util;

import gov.nih.nci.ncicb.tcgaportal.util.ProcessLogger;

/**
 * Helper for export servlet.  Right now just holds properties which are
 * set in spring from a properties file.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ExportHelper {

    // the default values are overridden by the values from properties file 
    // using spring framework
    private int timesToWaitForFile = 10;
    private int waitLengthInMilliseconds = 500;
    private int waitTimeToDeleteInMilliseconds = 500;

    private ProcessLogger logger;    

    public int getTimesToWaitForFile() {
        return timesToWaitForFile;
    }

    public void setTimesToWaitForFile(int timesToWaitForFile) {
        this.timesToWaitForFile = timesToWaitForFile;
    }

    public int getWaitLengthInMilliseconds() {
        return waitLengthInMilliseconds;
    }

    public void setWaitLengthInMilliseconds(int waitLengthInMilliseconds) {
        this.waitLengthInMilliseconds = waitLengthInMilliseconds;
    }

    public int getWaitTimeToDeleteInMilliseconds() {
        return waitTimeToDeleteInMilliseconds;
    }

    public void setWaitTimeToDeleteInMilliseconds(int waitTimeToDeleteInMilliseconds) {
        this.waitTimeToDeleteInMilliseconds = waitTimeToDeleteInMilliseconds;
    }

    public ProcessLogger getLogger() {
        return logger;
    }

    public void setLogger(ProcessLogger logger) {
        this.logger = logger;
    }
}
