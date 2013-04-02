/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import java.io.Serializable;

/**
 * @author Robert S. Sfeir
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Platform implements Serializable{

    private Integer platformId = null;
    private String platformName = null;
    private String platformDisplayName = null;
    private String centerType = null;
    private String platformAlias = null;

    public Platform() {
    }

    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId( final Integer platformId ) {
        this.platformId = platformId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName( final String platformName ) {
        this.platformName = platformName;
    }

    public String getPlatformDisplayName() {
        return platformDisplayName;
    }

    public void setPlatformDisplayName( final String platformDisplayName ) {
        this.platformDisplayName = platformDisplayName;
    }

    public String getCenterType() {
        return centerType;
    }

    public void setCenterType(final String centerType) {
        this.centerType = centerType;
    }

    public String getPlatformAlias() {
        return platformAlias;
    }

    public void setPlatformAlias(final String platformAlias) {
        this.platformAlias = platformAlias;
    }

    public boolean equals( final Object o ) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        final Platform platform = (Platform) o;
        if(platformId != null ? !platformId.equals( platform.platformId ) : platform.platformId != null) {
            return false;
        }
        if(!platformName.equals( platform.platformName )) {
            return false;
        }
        if(platformDisplayName != null ? !platformDisplayName.equals(platform.getPlatformDisplayName()): platform.getPlatformDisplayName() != null)
            return false;

        if(centerType != null ? !centerType.equals(platform.getCenterType()): platform.getCenterType() != null)
            return false;

        if(platformAlias != null ? !platformAlias.equals(platform.getPlatformAlias()): platform.getPlatformAlias() != null)
            return false;

        return true;
    }

    public int hashCode() {

        int result;

        result = (platformId != null ? platformId.hashCode() : 0);
        result = 31 * result + platformName.hashCode();
        result += (platformDisplayName != null) ? platformDisplayName.hashCode() : 0;
        result += (centerType != null) ? centerType.hashCode() : 0;
        result += (platformAlias != null) ? platformAlias.hashCode() : 0;

        return result;
    }

    public String toString() {
        return platformName;
    }
}
