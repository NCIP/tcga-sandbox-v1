/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.web.controllers.request;

/**
 * Request class used by the Generate UUID UI Form
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */
public class GenerateUUIDRequest {
    private int numberOfUUIDs;
    private int centerId;

    public GenerateUUIDRequest(final int numberOfUUIDs, final int centerId) {
        this.numberOfUUIDs = numberOfUUIDs;
        this.centerId = centerId;
    }

    public GenerateUUIDRequest() {
    }

    public int getNumberOfUUIDs() {
        return numberOfUUIDs;
    }

    public void setNumberOfUUIDs(final int numberOfUUIDs) {
        this.numberOfUUIDs = numberOfUUIDs;
    }

    public int getCenterId() {
        return centerId;
    }

    public void setCenterId(final int centerId) {
        this.centerId = centerId;
    }

}
