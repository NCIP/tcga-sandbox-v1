/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.email;

import gov.nih.nci.ncicb.tcga.dcc.common.util.GetterMethod;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.SampleSummary;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.SampleSummaryReportService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties.pocEmail;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties.pocName;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties.pocPhone;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties.serverAddress;


/**
 * Generate an email with a filtered Sample Summary report table
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */

public class CenterSampleReportGeneratorImpl implements CenterSampleReportGenerator {

    @Autowired
    private SampleSummaryReportService ssService;

    @Autowired
    private DatareportsService commonService;

    @Autowired
    private ProcessLogger logger;

    public String generateHTMLFor(String centerName) {
        StringBuilder buf = new StringBuilder();
        List<SampleSummary> ssList = ssService.getFilteredSampleSummaryReport(centerName);
        genEmailHeader(buf, ssList);
        buf.append("</tr>");
        for (final SampleSummary ss : ssList) {
            buf.append("<tr>");
            for (final Map.Entry<String, String> e : SampleSummaryReportConstants.SAMPLE_SUMMARY_COLS.entrySet()) {
                try {
                    final Method getter = GetterMethod.getGetter(ss.getClass(),e.getKey());
                    final String val = getter.invoke(ss).toString();
                    boolean bcr = false;
                    if (getter.getName().equals("getTotalBCRSent") ||
                            getter.getName().equals("getTotalCenterUnaccountedFor")){
                        bcr = true;
                    }
                    buf.append("<td style='border:1px solid #66a3d3;text-align:center;padding: 5px;'>");
                    if (getter.getName().startsWith("getTotal") && !"0".equals(val)) {
                        buf.append("<a href='").append(genSampleUrl(ss,e.getKey(),centerName,bcr)).append("'>");
                        buf.append(val).append("</a>");
                    } else {
                        if ("Y*".equals(val)) {
                            buf.append("<span style='color:red'>").append(val).append("</span>");
                        } else {
                            buf.append(val);
                        }
                    }
                    buf.append("</td>");
                } catch (Exception e1) {
                    logger.logError(e1);
                }
            }
            buf.append("</tr>");
        }
        genEmailFooter(centerName, buf);
        return buf.toString();
    }

    private String getServerUrlFull() {
        return serverAddress + SampleSummaryReportConstants.SS_URL;
    }

    private String genSampleUrl(SampleSummary ss,String colId,String centerName,boolean bcr){
        StringBuilder builder = new StringBuilder();
        String disease = ss.getDisease();
        String center = ss.getCenter();
        String portionAnalyte = ss.getPortionAnalyte();
        String platform = ss.getPlatform();
        builder.append(getServerUrlFull()).append("?centerEmail=").append(centerName);
        builder.append("&disease=").append(disease).append("&center=").append(center);
        builder.append("&portionAnalyte=").append(portionAnalyte).append("&platform=").append(platform);
        builder.append("&bcr=").append(bcr);
        builder.append("&cols=").append(colId);
        return builder.toString();
    }

    private void genEmailHeader(final StringBuilder buf,List<SampleSummary> ssList) {

        buf.append("<html><body><div><p>Sample Summary Report</p>");
        if (ssList!=null && ssList.size()>0){
            Date date = ssList.get(0).getLastRefresh();
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            String refresh = df.format(date);
            buf.append("<p>Data as of ").append(refresh).append("</p>");
        }
        buf.append("<table style='border:1px solid #66a3d3;width:950px;");
        buf.append("margin:1em auto;border-collapse:collapse;font-size:0.9em;'><tr>");
        for (Map.Entry<String, String> e : SampleSummaryReportConstants.SAMPLE_SUMMARY_COLS.entrySet()) {
            buf.append("<th style='background:#B8DBFF;text-align:center;border:1px solid #66a3d3;");
            buf.append("font-weight:bold;color:#000099;padding:5px;'>").append(e.getValue()).append("</th>");
        }
    }

    private void genEmailFooter(final String centerName, final StringBuilder buf) {
        buf.append("</table></div>").append(DatareportsCommonConstants.BR);
        buf.append(SampleSummaryReportConstants.EMAIL_NOTE1);
        buf.append(SampleSummaryReportConstants.EMAIL_NOTE2).append("&nbsp;&nbsp;<a href='");
        buf.append(genServerUrl(centerName)).append("'>Use this link to access live data.</a>");
        buf.append("&nbsp;&nbsp;").append(SampleSummaryReportConstants.QUESTIONS_COMMENTS).append(DatareportsCommonConstants.BR);
        buf.append("<div align='left'><span>").append(genSignature()).append("</span></div>").append(DatareportsCommonConstants.BR);
        buf.append("<div align='left' style='font-size:0.8em'>").append(SampleSummaryReportConstants.REPORT_DISCLAIMER).append("</div>");
        buf.append(DatareportsCommonConstants.BR).append("</body></html>");
    }

    private String genServerUrl(final String center) {
        try {
            return getServerUrlFull()+"?centerEmail="+URLEncoder.encode(center,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.logError(e);
            return getServerUrlFull();
        }
    }

    /**
     * Produce the point of contact "signature" for the email
     * @return
     */
    private String genSignature() {
        StringBuilder builder = new StringBuilder();
        builder.append(pocName).append(DatareportsCommonConstants.BR);
        builder.append(pocEmail).append(DatareportsCommonConstants.BR);
        builder.append(pocPhone);
        return builder.toString();
    }
    
}// End of Class
