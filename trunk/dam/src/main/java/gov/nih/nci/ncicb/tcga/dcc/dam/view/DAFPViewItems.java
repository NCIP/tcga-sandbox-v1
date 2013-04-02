/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DAMJobStatus;

import java.util.UUID;

/**
 * Bean holding information needed by the dataAccessFileProcessing view.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAFPViewItems {

    private String email;
    private String diseaseType;
    private DAMJobStatus jobStatus;

    private UUID filePackagerKey;

    public String getEmail() {
        return email;
    }

    public void setEmail( final String email ) {
        this.email = email;
    }

    public String getDiseaseType() {
        return diseaseType;
    }

    public void setDiseaseType( final String diseaseType ) {
        this.diseaseType = diseaseType;
    }

    public UUID getFilePackagerKey() {
        return filePackagerKey;
    }

    public void setFilePackagerKey( final UUID filePackagerKey ) {
        this.filePackagerKey = filePackagerKey;
    }

    public void setJobStatus(final DAMJobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public DAMJobStatus getJobStatus() {
        return jobStatus;
    }
}
