/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.NcbiTrace;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.TraceInfoQueries;
import org.apache.log4j.Level;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: fengla
 * Date: Apr 7, 2008
 * Time: 2:54:32 PM
 * To change this template use File | Settings | File Templates.
 * This Util Class should extract the fields from xmls on local or remote server, then insert into the new trace_info table
 */
public class PopulateTraceInfo {

    private ProcessLogger logger;
    private NcbiTrace trace;
    private TraceInfoQueries traceInfoQueries;
    private String downloaddir;
    private int number_of_record_exsits = 0;
    private int number_of_record_insert = 0;
    private int number_of_record_update = 0;
    private String[] filelist;
    private static PopulateTraceInfo instance;

    public static PopulateTraceInfo getInstance() {
        if(instance == null) {
            instance = new PopulateTraceInfo();
        }
        return instance;
    }

    public NcbiTrace getNcbiTrace() {
        return trace;
    }

    public void setNcbiTrace( final NcbiTrace trace ) {
        this.trace = trace;
    }

    public TraceInfoQueries getTraceInfoQueries() {
        return traceInfoQueries;
    }

    public void setTraceInfoQueries( final TraceInfoQueries traceInfoQueries ) {
        this.traceInfoQueries = traceInfoQueries;
    }

    public int processXMLFile() {
        int pageprocessed = 0;
        try {
            getFileList();
            for(int i = 0; i < filelist.length; i++) {
                final String filename = filelist[i];
                if(filename.endsWith( ".xml" )) {
                    pageprocessed++;
                    getFieldValues( downloaddir + File.separator + filename );
                    getLogger().logToLogger( Level.INFO, "PopulateTraceInfo processXMLFile number_of_record_exsits: " + number_of_record_exsits );
                    getLogger().logToLogger( Level.INFO, "PopulateTraceInfo processXMLFile number_of_record_insert: " + number_of_record_insert );
                    getLogger().logToLogger( Level.INFO, "PopulateTraceInfo processXMLFile pageprocessed: " + downloaddir + File.separator + filename );
                }
            }
        }
        catch(Exception e) {
            getLogger().logToLogger( Level.ERROR, "PopulateTraceInfo processXMLFile Exception: " + e.toString() );
            e.printStackTrace();
        }
        return pageprocessed;
    }

    public void getFieldValues( final String filename ) {
        logger.logToLogger( Level.DEBUG, "getFieldValues,  filename " + filename );
        number_of_record_exsits = 0;
        number_of_record_insert = 0;
        number_of_record_update = 0;
        try {
            final Document xmldoc = getInputXML( filename );
            final NodeList tracelist = getNodes( xmldoc, "/traces/trace" );
            for(int i = 0; i < tracelist.getLength(); i++) {
                setNcbiTrace( new NcbiTrace() );
                final Node atrace = tracelist.item( i );
                final NodeList fields = atrace.getChildNodes();
                for(int j = 0; j < fields.getLength(); j++) {
                    final Node anode = fields.item( j );
                    if(anode.getNodeName().equalsIgnoreCase( "ti" )) {
                        getNcbiTrace().setTi( Long.parseLong( anode.getTextContent() ) );
                    }
                    if(anode.getNodeName().equalsIgnoreCase( "trace_name" )) {
                        getNcbiTrace().setTrace_name( anode.getTextContent() );
                    }
                    if(anode.getNodeName().equalsIgnoreCase( "center_name" )) {
                        getNcbiTrace().setCenter_name( anode.getTextContent() );
                    }
                    if(anode.getNodeName().equalsIgnoreCase( "submission_type" )) {
                        getNcbiTrace().setSubmission_type( anode.getTextContent() );
                    }
                    if(anode.getNodeName().equalsIgnoreCase( "gene_name" )) {
                        getNcbiTrace().setGene_name( anode.getTextContent() );
                    }
                    if(anode.getNodeName().equalsIgnoreCase( "reference_accession" )) {
                        getNcbiTrace().setReference_accession( anode.getTextContent() );
                    }
                    if(anode.getNodeName().equalsIgnoreCase( "reference_acc_max" )) {
                        getNcbiTrace().setReference_acc_max( Integer.parseInt( anode.getTextContent() ) );
                    }
                    if(anode.getNodeName().equalsIgnoreCase( "reference_acc_min" )) {
                        getNcbiTrace().setReference_acc_min( Integer.parseInt( anode.getTextContent() ) );
                    }
                    if(anode.getNodeName().equalsIgnoreCase( "ncbi_trace_archive" )) {
                        final NodeList archiveinfo = anode.getChildNodes();
                        for(int k = 0; k < archiveinfo.getLength(); k++) {
                            final Node archivenode = archiveinfo.item( k );
                            if(archivenode.getNodeName().equalsIgnoreCase( "replaced_by" )) {
                                getNcbiTrace().setReplaced_by( Integer.parseInt( archivenode.getTextContent() ) );
                            }
                            if(archivenode.getNodeName().equalsIgnoreCase( "basecall_length" )) {
                                getNcbiTrace().setBasecall_length( Integer.parseInt( archivenode.getTextContent() ) );
                            }
                            if(archivenode.getNodeName().equalsIgnoreCase( "load_date" )) {
                                final SimpleDateFormat dformat = new SimpleDateFormat( "MMM dd yyyy hh:mma" );
                                try {
                                    final java.util.Date udate = dformat.parse( archivenode.getTextContent().trim() );
                                    final long ldate = udate.getTime();
                                    final java.sql.Timestamp sTimestamp = new Timestamp( ldate );
                                    getNcbiTrace().setLoad_date( sTimestamp );
                                }
                                catch(ParseException e) {
                                    getLogger().logToLogger( Level.ERROR, "PopulateTraceInfo getFieldValues Exception: " + e.toString() );
                                }
                            }
                            if(archivenode.getNodeName().equalsIgnoreCase( "state" )) {
                                getNcbiTrace().setState( archivenode.getTextContent().trim() );
                            }
                        }//end of for archiveinfo
                    } //end of if archive
                }//end of for fields
                final int insertrow = insertTraceInfoTable();
            }
        }
        catch(XPathExpressionException e) {
            getLogger().logToLogger( Level.ERROR, "PopulateTraceInfo getFieldValues XPathExpressionException: " + e.toString() );
        }
        catch(TransformerException e) {
            getLogger().logToLogger( Level.ERROR, "PopulateTraceInfo getFieldValues TransformerException: " + e.toString() );
        }
    }

    public NodeList getNodes( final Document doc,
                              final String xPathExpression ) throws XPathExpressionException, TransformerException {
        return XPathAPI.selectNodeList( doc, xPathExpression );
    }

    public Document getInputXML( final String filename ) {
        return parseXmlFile( filename, false );
    }

    public Document parseXmlFile( final String filename, final boolean validating ) {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating( validating );
            final File thefile = new File( filename );
            // Create the builder and parse the file
            final Document doc = factory.newDocumentBuilder().parse( thefile );
            return doc;
        }
        catch(SAXException e) {
            getLogger().logToLogger( Level.ERROR, "PopulateTraceInfo parseXmlFile SAXException: " + e.toString() );
            return null;
        }
        catch(ParserConfigurationException e) {
            getLogger().logToLogger( Level.ERROR, "PopulateTraceInfo parseXmlFile ParserConfigurationException: " + e.toString() );
            return null;
        }
        catch(IOException e) {
            getLogger().logToLogger( Level.ERROR, "PopulateTraceInfo parseXmlFile IOException: " + e.toString() );
            return null;
        }
    }

    public void getFileList() {
        getLogger().logToLogger( Level.INFO, "PopulateTraceInfo getFileList downloaddir: " + downloaddir );
        final File thefile = new File( downloaddir );
        filelist = thefile.list();
        getLogger().logToLogger( Level.INFO, "PopulateTraceInfo getFileList number of files to process: " + filelist.length );
    }

    public int insertTraceInfoTable() {
        try {
            final int tiindb = getTraceInfoQueries().exists( getNcbiTrace().getTi() );
            if(tiindb > 0) {
                number_of_record_exsits++;
                //do nothing, reocrd already in table
            } else {
                //insert first
                number_of_record_insert = number_of_record_insert + getTraceInfoQueries().addTraceInfo( getNcbiTrace() );
                //then check for update
                if(!getNcbiTrace().getSubmission_type().equalsIgnoreCase( "NEW" )) {
                    final Object[] traces = getTraceInfoQueries().getMatchingTraces( getNcbiTrace().getTrace_name(), getNcbiTrace().getCenter_name() ).toArray();
                    if(traces != null && traces.length > 0) {
                        for(int i = 0; i < traces.length; i++) {
                            number_of_record_update = number_of_record_update + getTraceInfoQueries().updateTraceinfo( ( (NcbiTrace) traces[i] ).getTi(), getNcbiTrace().getTi() );
                        }
                    }//end of updates
                }//end of check
            }//end of ti exists 
        }
        catch(Exception e) {
            getLogger().logToLogger( Level.ERROR, "PopulateTraceInfo insertTraceInfoTable Exception: " + e.toString() );
        }
        return number_of_record_insert;
    }

    public ProcessLogger getLogger() {
        return logger;
    }

    public void setLogger( final ProcessLogger logger ) {
        this.logger = logger;
    }

    public String getDownloaddir() {
        return downloaddir;
    }

    public void setDownloaddir( final String downloaddir ) {
        this.downloaddir = downloaddir;
    }
}
