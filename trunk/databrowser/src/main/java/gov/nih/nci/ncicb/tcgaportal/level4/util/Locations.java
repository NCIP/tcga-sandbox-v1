/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.util;

import java.io.File;
import java.io.IOException;

/**
 * Convenience class to store locations of things (URLs, etc) used by the application.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Locations {
    private String userGuideLocation;
    private String onlineHelpLocation;
    private String tooltipFileLocation;

    private String tempFileLocation;

    public String getUserGuideLocation() {
        return userGuideLocation;
    }

    public void setUserGuideLocation(String userGuideLocation) {
        this.userGuideLocation = userGuideLocation;
    }

    public String getTooltipFileLocation() {
        return tooltipFileLocation;
    }

    public void setTooltipFileLocation(String tooltipFileLocation) {
        this.tooltipFileLocation = tooltipFileLocation;
    }

    public String getOnlineHelpLocation() {
        return onlineHelpLocation;
    }

    public void setOnlineHelpLocation(String onlineHelpLocation) {
        this.onlineHelpLocation = onlineHelpLocation;
    }

    public String getTempFileLocation() {
        return tempFileLocation;
    }

    public void setTempFileLocation(String tempFileLocation) {
        this.tempFileLocation = tempFileLocation;
    }

    public void makeSureLocationExists(String location) throws IOException {
        if (location != null) {
            File file = new File(location);
            if (!file.exists()) {
                File dir = new File(location);
                if (!dir.mkdirs()) {
                    throw new IOException("Could not create directory " + location);
                }
            }
        }
    }

}
