/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.webservice;

import com.sun.jersey.api.core.InjectParam;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.BeanToTextExporter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.LatestGenericReportService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_TIME_FORMAT_US;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.TAB;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.LatestGenericReportConstants.ARCHIVE_COLS;

/**
 * jersey Restful webservice for latest archive report
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
@Path("/latestarchive")
@Scope("request")
public class ArchiveWS {

    protected final Log logger = LogFactory.getLog(getClass());

    @InjectParam
    private LatestGenericReportService service;

    /**
     * archive ws report
     *
     * @param archiveType
     * @return text
     */
    @GET
    @Produces("text/plain")
    public String getArchiveReport(@QueryParam("archiveType") String archiveType) {
        final StringWriter out = new StringWriter();
        final Map<String, String> columns = ARCHIVE_COLS;
        final List<Archive> data;
        if (archiveType != null) {
            data = service.getLatestArchiveWSByType(archiveType);
        } else {
            data = service.getLatestArchiveWS();
        }
        return BeanToTextExporter.beanListToText(TAB, out, columns, data, DATE_TIME_FORMAT_US);
    }

}// End of class
