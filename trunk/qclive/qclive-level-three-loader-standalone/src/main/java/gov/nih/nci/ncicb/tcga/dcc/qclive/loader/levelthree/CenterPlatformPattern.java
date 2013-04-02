/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree;

import java.util.List;
import java.util.ArrayList;

/**
 * Bean to hold patterns for center / platform combination
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class CenterPlatformPattern {

    private String center;
    private String platform;
    private List<String> pattern = new ArrayList<String>();

    public CenterPlatformPattern(String newCenter, String newPlatform, List<String> newPattern) {
        center = newCenter;
        platform = newPlatform;
        pattern = newPattern;
    }

    public CenterPlatformPattern() {

    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<String> getPattern() {
        return pattern;
    }

    public void setPattern(List<String> pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CenterPlatformPattern that = (CenterPlatformPattern) o;

        if (center != null ? !center.equals(that.center) : that.center != null) return false;
        if (platform != null ? !platform.equals(that.platform) : that.platform != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = center != null ? center.hashCode() : 0;
        result = 31 * result + (platform != null ? platform.hashCode() : 0);
        return result;
    }


}
