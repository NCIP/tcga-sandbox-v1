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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Maf;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.LatestGenericReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.LatestGenericReportService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * Web service that create a text file of the latest maf report
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
@Path("/latestmaf")
@Scope("request")
public class MafWS {

    protected final Log logger = LogFactory.getLog(getClass());

    @InjectParam
    private LatestGenericReportService service;

    /**
     * maf ws report
     *
     * @return text
     */
    @GET
    @Produces("text/plain")
    public String getMafReport() {
        final StringWriter out = new StringWriter();
        final Map<String, String> columns = LatestGenericReportConstants.MAF_COLS;
        final List<Maf> data = service.getLatestMafWS();
        return BeanToTextExporter.beanListToText(DatareportsCommonConstants.TAB, out, columns, data, DatareportsCommonConstants.DATE_TIME_FORMAT_US);
    }

}// End of class
