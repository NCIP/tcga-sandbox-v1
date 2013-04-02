/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.constants;

import gov.nih.nci.ncicb.tcga.dcc.common.annotations.TCGAValue;
import org.springframework.stereotype.Component;

/**
 * This class wraps properties defined for the datareports
 * This class is annoted @Component so that it will be picked up by the spring engine as a bean with the default
 * name datareportsProperties.
 * This way, this bean can accept a property configurer to allow setting up constant values from a properties file.
 * This class is differentiated from DatareportsConstants as the properties even though static can not be set
 * to final since there are set at setup by the spring properties configurer. But this class will allow for
 * a common spot to get these properties which are use in many different places in the project.
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Component
public class DatareportsProperties {

    @TCGAValue (key = "tcga.datareports.biMonthly.poc.name")
    public static String pocName;

    @TCGAValue (key = "tcga.datareports.biMonthly.poc.email")
    public static String pocEmail;

    @TCGAValue (key = "tcga.datareports.biMonthly.poc.phone")
    public static String pocPhone;

    @TCGAValue (key = "tcga.datareports.server.httpColonSlashSlashHostnameAndPort")
    public static String serverAddress;

    @TCGAValue(key = "tcga.datareports.pipelineReportJsonFilesPath")
    public static String pipelineReportJsonFilesPath;

    public void setPocName(final String pocName) {
        DatareportsProperties.pocName = pocName;
    }

    public void setPocEmail(final String pocEmail) {
        DatareportsProperties.pocEmail = pocEmail;
    }

    public void setPocPhone(final String pocPhone) {
        DatareportsProperties.pocPhone = pocPhone;
    }

    public void setServerAddress(final String serverAddress) {
        DatareportsProperties.serverAddress = serverAddress;
    }

    public void setPipelineReportJsonFilesPath(String pipelineReportJsonFilesPath) {
        DatareportsProperties.pipelineReportJsonFilesPath = pipelineReportJsonFilesPath;
    }
}//End of Class
