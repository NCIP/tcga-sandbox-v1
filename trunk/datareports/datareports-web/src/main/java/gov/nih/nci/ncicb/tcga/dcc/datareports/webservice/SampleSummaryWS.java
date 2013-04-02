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
import gov.nih.nci.ncicb.tcga.dcc.common.util.BeanToTextExporter;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.SampleSummary;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.SampleSummaryReportService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.DATE_FORMAT_US;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants.TAB;

/**
 * jersey Restful webservice for sample summary
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Path("/samplesummary")
@Scope("request")
public class SampleSummaryWS {

    protected final Log logger = LogFactory.getLog(getClass());

    @InjectParam
    private SampleSummaryReportService service;

    /**
     * sample summary ws report
     *
     * @return text
     */
    @GET
    @Produces("text/plain")
    public String getSampleSummaryReport() {
        final StringWriter out = new StringWriter();
        final Map<String, String> columns = SampleSummaryReportConstants.SAMPLE_SUMMARY_COLS;
        final List<SampleSummary> data = service.getSampleSummaryReport();
        return BeanToTextExporter.beanListToText(TAB, out, columns, data, DATE_FORMAT_US);
    }

} //End of Class
