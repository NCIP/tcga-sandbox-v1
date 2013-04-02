/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Robert S. Sfeir, David Kane Last updated by: Jeyanthi Thangiah
 */
public class LogQueryRequest {

    private Date startDate = null;

    public LogQueryRequest() {
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate( final Date startDate ) {
        this.startDate = startDate;
    }

    public String getStartDateAsString() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.format(startDate);
    }
}
