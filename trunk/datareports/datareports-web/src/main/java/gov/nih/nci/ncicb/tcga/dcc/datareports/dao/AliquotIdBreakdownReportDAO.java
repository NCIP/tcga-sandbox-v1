/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotIdBreakdown;

import javax.sql.DataSource;
import java.util.List;

/**
 * interface describing the aliquot id breakdown report
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public interface AliquotIdBreakdownReportDAO {

public void setDataSource(DataSource dataSource);

    /**
     * A query to get all of the rows of the aliquot Id breakdown report
     *
     * @return a list of AliquotIdBreakdown
     */
    public List<AliquotIdBreakdown> getAliquotIdBreakdown();
    
}
