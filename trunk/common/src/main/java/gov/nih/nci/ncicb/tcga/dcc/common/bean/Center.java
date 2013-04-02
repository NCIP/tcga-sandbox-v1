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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Robert S. Sfeir
 */
public class Center implements Serializable {
    private static final long serialVersionUID = -8691010348367145890L;
    private Integer centerId = null;
    private String centerName = null;
    private String centerType = null;
    private String centerDisplayName = null;
    private String shortName = null;
    private List<String> emailList;
    private String centerDisplayText;
    private String bcrCenterId;

    public Center() {
        emailList = new ArrayList<String>();
    }

    public Integer getCenterId() {
        return centerId;
    }

    public String getCenterDisplayText() {
        return centerName + " (" + centerType + ")";
    }

    public void setCenterDisplayText(String centerDisplayText) {
        this.centerDisplayText = centerDisplayText;
    }

    public void setCenterId(final Integer centerId) {
        this.centerId = centerId;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(final String centerName) {
        this.centerName = centerName;
    }

    public String getCenterType() {
        return centerType;
    }

    public void setCenterType(final String centerType) {
        this.centerType = centerType;
    }

    public String getCenterEmail() {
        return getCommaSeparatedEmailList();
    }

    public String getCenterDisplayName() {
        return centerDisplayName;
    }

    public void setCenterDisplayName(final String centerDisplayName) {
        this.centerDisplayName = centerDisplayName;
    }

    public void setEmailList(final List<String> emailList) {
        this.emailList = emailList;
    }

    public List<String> getEmailList() {
        return this.emailList;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(final String shortName) {
        this.shortName = shortName;
    }

    public String getBcrCenterId() {
        return bcrCenterId;
    }

    public void setBcrCenterId(String bcrCenterId) {
        this.bcrCenterId = bcrCenterId;
    }

    public String getCommaSeparatedEmailList() {
        StringBuilder commaSeparatedEmailList = null;
        if (this.emailList != null && this.emailList.size() > 0) {
            for (final String emailStr : this.emailList) {
                if (commaSeparatedEmailList == null) {
                    commaSeparatedEmailList = new StringBuilder(emailStr);
                } else {
                    commaSeparatedEmailList.append(",").append(emailStr);
                }
            }
        }
        return (commaSeparatedEmailList != null) ? commaSeparatedEmailList.toString() : null;
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Center center = (Center) o;
        if (centerId != null ? !centerId.equals(center.centerId) : center.centerId != null) {
            return false;
        }
        if (!centerName.equals(center.centerName)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result;
        result = (centerId != null ? centerId.hashCode() : 0);
        result = 31 * result + centerName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getCenterDisplayText();
    }
}
