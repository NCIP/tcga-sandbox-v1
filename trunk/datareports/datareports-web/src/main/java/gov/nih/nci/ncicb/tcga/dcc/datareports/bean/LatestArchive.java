/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean;

import java.util.Date;

/**
 * Class that defines the latest combined archive, sdrf,maf report.
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

public class LatestArchive {

    private String archiveName;
    private Date dateAdded;
    private String archiveUrl;
    private String archiveType;
    private String sdrfName;
    private String sdrfUrl;
    private String mafName;
    private String mafUrl;

    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(final String archiveName) {
        this.archiveName = archiveName;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(final Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getArchiveType() {
        return archiveType;
    }

    public void setArchiveType(final String archiveType) {
        this.archiveType = archiveType;
    }

    public String getArchiveUrl() {
        return archiveUrl;
    }

    public void setArchiveUrl(final String archiveUrl) {
        this.archiveUrl = archiveUrl;
    }

    public String getSdrfName() {
        return sdrfName;
    }

    public void setSdrfName(final String sdrfName) {
        this.sdrfName = sdrfName;
    }

    public String getSdrfUrl() {
        return sdrfUrl;
    }

    public void setSdrfUrl(final String sdrfUrl) {
        this.sdrfUrl = sdrfUrl;
    }

    public String getMafName() {
        return mafName;
    }

    public void setMafName(final String mafName) {
        this.mafName = mafName;
    }

    public String getMafUrl() {
        return mafUrl;
    }

    public void setMafUrl(final String mafUrl) {
        this.mafUrl = mafUrl;
    }
}//End of Class
