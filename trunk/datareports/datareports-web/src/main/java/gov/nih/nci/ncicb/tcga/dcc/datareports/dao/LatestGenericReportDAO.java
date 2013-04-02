/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.LatestArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Maf;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Sdrf;

import javax.sql.DataSource;
import java.util.List;

/**
 * interface to define the java method querying the db for all the latest X as of sdrf, maf or archive reports.
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public interface LatestGenericReportDAO {

    public void setDataSource(DataSource dataSource);

    /**
     * A query to get all of the rows of the latest sdrf report
     *
     * @return a list of sdrf 
     */
    public List<Sdrf> getLatestSdrfWS();

    /**
     * A query to get all of the rows of the latest archive report
     *
     * @return a list of archive
     */
    public List<Archive> getLatestArchiveWS();

    /**
     * Return all latest archive rows by type
     *
     * @param archiveType
     *
     * @return a List of Archives
     */
    public List<Archive> getLatestArchiveWSByType(String archiveType);

    /**
     * A query to get all of the rows of the latest maf report
     *
     * @return a list of maf
     */
    public List<Maf> getLatestMafWS();

    /**
     * A query to get all of the rows of the combined latest archive,maf,sdrf report
     *
     * @return a list of combined latest archive,sdrf,maf
     */
    public List<LatestArchive> getLatestArchive();

}
