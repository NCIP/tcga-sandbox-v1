/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.bean;

import org.springframework.web.multipart.MultipartFile;

/**
 * File upload bean for importing UUIDs from a file
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

public class FileUploadBean {

    private MultipartFile file;
    private int centerId;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public int getCenterId() {
        return centerId;
    }

    public void setCenterId(final int centerId) {
        this.centerId = centerId;
    }
}
