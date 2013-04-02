/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DownloadNCBITraceXML;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.PopulateTraceInfo;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.TraceInfoQueries;
import org.apache.log4j.Level;

/**
 * User: fengla
 * Date: Sep 22, 2008
 */
public class UpdateTraceInfoRunner {

    private ProcessLogger logger;
    private TraceInfoQueries traceInfoQueries;
    private String downloadDirectory;

    public void runJob() {
        try {
            getLogger().logToLogger( Level.INFO, "UpdateTraceInfoRunner start runJob " );
            DownloadNCBITraceXML.getInstance().setLogger( getLogger() );
            DownloadNCBITraceXML.getInstance().setTraceInfoQueries( getTraceInfoQueries() );
            DownloadNCBITraceXML.getInstance().setDownloaddir( getDownloadDirectory() );
            DownloadNCBITraceXML.getInstance().init();
            String downloaddir = DownloadNCBITraceXML.getInstance().download();
            getLogger().logToLogger( Level.DEBUG, "UpdateTraceInfoRunner DownloadNCBITraceXML files saved in: " + downloaddir );
            PopulateTraceInfo.getInstance().setLogger( getLogger() );
            PopulateTraceInfo.getInstance().setTraceInfoQueries( getTraceInfoQueries() );
            PopulateTraceInfo.getInstance().setDownloaddir( DownloadNCBITraceXML.getInstance().getDownloaddir() );
            int pageprocessed = PopulateTraceInfo.getInstance().processXMLFile();
            getLogger().logToLogger( Level.DEBUG, "UpdateTraceInfoRunner PopulateTraceInfo page porceessed: " + pageprocessed );
            getLogger().logToLogger( Level.INFO, "UpdateTraceInfoRunner end runJob " );
        }
        catch(Exception e) {
            getLogger().logToLogger( Level.ERROR, "UpdateTraceInfoRunner runJob e " + e.getMessage() );
        }
    }

    public void setLogger( ProcessLogger logger ) {
        this.logger = logger;
    }

    public ProcessLogger getLogger() {
        return logger;
    }

    public TraceInfoQueries getTraceInfoQueries() {
        return traceInfoQueries;
    }

    public void setTraceInfoQueries( final TraceInfoQueries traceInfoQueries ) {
        this.traceInfoQueries = traceInfoQueries;
    }

    public String getDownloadDirectory() {
        return downloadDirectory;
    }

    public void setDownloadDirectory( final String downloadDirectory ) {
        this.downloadDirectory = downloadDirectory;
    }
}