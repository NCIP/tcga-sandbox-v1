/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

/**
 * @author Robert S. Sfeir
 * @version $Id: Access.java 1253 2008-06-11 17:19:52Z sfeirr $
 */
public class Visibility {

    private Integer visibilityId = null;
    private Boolean identifiable = null;
    private String visibilityName = null;

    public Integer getVisibilityId() {
        return visibilityId;
    }

    public void setVisibilityId( final Integer visibilityId) {
        this.visibilityId = visibilityId;
    }

    public Boolean isIdentifiable() {
        return identifiable;
    }

    public void setIdentifiable( final Boolean identifiable ) {
        this.identifiable = identifiable;
    }

    public String getVisibilityName() {
        return visibilityName;
    }

    public void setVisibilityName( final String visibilityName) {
        this.visibilityName = visibilityName;
    }

    public boolean equals( final Object o ) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        final Visibility visibility = (Visibility) o;
        return getVisibilityId().equals(visibility.getVisibilityId());
    }

    public int hashCode() {
        return getVisibilityId().hashCode();
    }

    public String toString() {
        return getVisibilityName();
    }
}
