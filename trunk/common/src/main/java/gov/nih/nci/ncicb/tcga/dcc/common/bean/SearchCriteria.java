/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import java.util.Date;

/**
 * Class used to specify the criteria for searching UUIDs
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */
public class SearchCriteria {

    private int disease;
    private int centerId;
    private String uuid;
    private String barcode;
    private String submittedBy;
    private boolean newSearch;

    private Date creationDate;

    public boolean isNewSearch() {
        return newSearch;
    }

    public void setNewSearch(boolean newSearch) {
        this.newSearch = newSearch;
    }

    public int getDisease() {
        return disease;
    }

    public void setDisease(final int disease) {
        this.disease = disease;
    }

    public int getCenterId() {
        return centerId;
    }

    public void setCenterId(final int centerId) {
        this.centerId = centerId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(final String barcode) {
        this.barcode = barcode;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(final String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }
}

