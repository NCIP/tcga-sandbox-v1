/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Aliquot;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotArchive;

import javax.sql.DataSource;
import java.util.List;

/**
 * Interface thats defines the DAO of the biospecimen report
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public interface AliquotReportDAO {

    public void setDataSource(DataSource dataSource);

    /**
     * A query to get all of the rows in the aliquot table
     *
     * @return a series of rows to populate the table
     */
    public List<Aliquot> getAliquotRows();

    /**
     * A query to returns all archives, file and url from an aliquotId in the biospecimen report
     *
     * @param aliquotId
     * @param level number
     *
     * @return a series of rows to populate the table
     */
    public List<AliquotArchive> getAliquotArchive(final String aliquotId, final int level);

}
