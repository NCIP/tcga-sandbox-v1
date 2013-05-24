/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlResultSet;

import java.util.Date;

/**
 * Interface for BAM File queries.
 *
 * @author ramanr
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface BAMFileQueries {

    /**
     * get latest uploaded date of bam data.
     *
     * @return
     */
    public Date getLatestUploadedDate();

    /**
     * persist a bam xml resultset into the database.
     *
     * @param bamXmlResultSet
     */
    public void store(final BamXmlResultSet bamXmlResultSet);

    /**
     * get bam datatype Id from bam datatype.
     *
     * @param bamDatatype
     * @return bam datatype Id
     */
    public Long getDatatypeBAMId(final String bamDatatype);

    /**
     * get aliquot biospecimen Id from aliquot uuid.
     *
     * @param uuid
     * @return biospecimen Id
     */
    public Long getAliquotId(final String uuid);

    /**
     * get DCC center id from Id from cghub center.
     *
     * @param cghubCenter
     * @return dcc center Id
     */
    public Long getDCCCenterId(final String cghubCenter);
}
