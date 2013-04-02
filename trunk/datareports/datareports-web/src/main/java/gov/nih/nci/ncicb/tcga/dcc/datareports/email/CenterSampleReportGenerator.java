/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.email;

/**
 * All email all centers a center-specific filtered view of the Sample Summary table
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public interface CenterSampleReportGenerator {

    /**
     * Generate the HTML email body containing the report
     *
     * @param centerName The center for which we will filter data
     * @return the HTML body of the email
     */
    public String generateHTMLFor(String centerName);

}
