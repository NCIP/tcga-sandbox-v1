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
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.TraceInfoQueries;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcliveCloseableUtil.close;

/**
 * Created by IntelliJ IDEA.
 * User: fengla
 * Date: Jun 4, 2008
 * Time: 10:19:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class DownloadNCBITraceXML {

    private ProcessLogger logger;
    private int querysize = 0;
    private int pagecount = 1;
    private final int pagesize = 500;//maxium 500 traces per xml
    private TraceInfoQueries traceInfoQueries;
    private String latestLoaddate = "2008-04-09";
    //private String downloaddir = System.getProperty( "user.dir" ) + File.separator + "ncbitrace";
    private String downloaddir = System.getProperty( "user.home" ) + File.separator + "download" + File.separator + "ncbitrace";
    private InputStream is;
    private FileWriter fw;
    private String xmlfiles = "TCGA_ABI_traceXML";
    private String[] fieldsWeCareForNow = {"<trace>",
            "<ti>", "<trace_name>", "<center_name>",
            "<species_code>", "<strategy>", "<trace_type_code>",
            "<submission_type>",
            "<source_type>", "<taxid>", "<amplification_forward>",
            "<amplification_reverse>", "<amplification_size>", "<center_project>", "<chemistry>",
            "<chemistry_type>", "<clip_quality_left>", "<clip_quality_right>",
            "<gene_name>",
            "<prep_group_id>", "<primer>", "<primer_code>", "<program_id>", "<project_name>",
            "<reference_accession>", "<reference_acc_max>", "<reference_acc_min>",
            "<run_machine_type>", "<seq_lib_id>", "<template_id>", "<trace_end>",
            "<trace_format>", "<reference_set_max>", "<reference_set_min>", "<anonymized_id>",
            "<extended_data>", "<field name='tcga url'>", "</extended_data>",
            "<ncbi_trace_archive>", "<replaced_by>", "<basecall_length>", "<load_date>", "<state>",
            "</ncbi_trace_archive>",
            "</trace>"};
    private String[] allFieldsWeCare = {"<trace>",
            "<ti>", "<trace_name>", "<center_name>",
            "<species_code>", "<strategy>", "<trace_type_code>",
            "<submission_type>",
            "<source_type>", "<taxid>", "<amplification_forward>",
            "<amplification_reverse>", "<amplification_size>", "<center_project>", "<chemistry>",
            "<chemistry_type>", "<clip_quality_left>", "<clip_quality_right>",
            "<gene_name>",
            "<prep_group_id>", "<primer>", "<primer_code>", "<program_id>", "<project_name>",
            "<reference_accession>", "<reference_acc_max>", "<reference_acc_min>",
            "<run_machine_type>", "<seq_lib_id>", "<template_id>", "<trace_end>",
            "<trace_format>", "<reference_set_max>", "<reference_set_min>", "<anonymized_id>",
            "<extended_data>", "<field name='tcga url'>", "</extended_data>",
            "<ncbi_trace_archive>", "<replaced_by>", "<basecall_length>", "<load_date>", "<state>",
            "</ncbi_trace_archive>",
            "</trace>"};
    private static DownloadNCBITraceXML instance;

    public static DownloadNCBITraceXML getInstance() {
        if(instance == null) {
            instance = new DownloadNCBITraceXML();
        }
        return instance;
    }

    public void init() {
        latestLoaddate = getLatestLoaddate();
        pagecount = getPagecount();
    }

    public String download() {
        try {
            for(int p = 1; p <= pagecount; p++) {
                saveXMLFiles( p );
                //saveFullXMLFiles( p );   //to download all fileds in traceXML
            }
            File savedfiles = new File( downloaddir );
            int count = savedfiles.listFiles().length;
            getLogger().logToLogger( Level.INFO, "DownloadNCBITraceXML download downloaddir: " + downloaddir );
            getLogger().logToLogger( Level.INFO, "DownloadNCBITraceXML download pages to save: " + pagecount );
            int pagemissing = checkmissingfiles();
            getLogger().logToLogger( Level.INFO, "DownloadNCBITraceXML download pagemissing saved: " + pagemissing );
        }
        catch(Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return downloaddir;
    }

    public int checkmissingfiles() {
        ArrayList missing = new ArrayList();
        try {
            for(int n = 1; n <= pagecount; n++) {
                File thefile = new File( downloaddir + File.separator + xmlfiles + "." + n + ".xml" );
                if(thefile.exists()) {
                    if(thefile.length() > 0) {
                        //System.out.println("found page " + n);
                    } else {
                        //System.out.println("file size is zero -------------- " + n);
                        missing.add( n );
                    }
                } else {
                    //System.out.println( "file missing -------------- -------------------" + n);
                    missing.add( n );
                }
            }
            for(Object p : missing.toArray()) {
                getLogger().logToLogger( Level.INFO, "DownloadNCBITraceXML checkmissingfiles saving : " + (Integer) p );
                saveXMLFiles( (Integer) p );
                //saveFullXMLFiles( (Integer) p );   //to download all fileds in traceXML
            }
            return missing.toArray().length;
        }
        catch(Exception e) {
            getLogger().logToLogger( Level.ERROR, "DownloadNCBITraceXML checkmissingfiles Exception " + e.toString() );
            return -1;
        }
    }

    public String getLatestLoaddate() {
        try {
            latestLoaddate = getTraceInfoQueries().getLastLoadDate().toString();
        }
        catch(Exception e) {
            getLogger().logToLogger( Level.ERROR, "DownloadNCBITraceXML getLatestLoaddate Exception " + e.toString() );
        }
        getLogger().logToLogger( Level.INFO, "DownloadNCBITraceXML getLatestLoaddate latestLoaddate " + latestLoaddate );
        return latestLoaddate;
    }

    public int getPagecount() {
        String countquery = "http://www.ncbi.nlm.nih.gov/Traces/trace.cgi?&cmd=retrieve&val=PROJECT_NAME=\"TCGA\"%20and%20LOAD_DATE>=\"" + latestLoaddate + "\"&dopt=xml_info&dispmax=1&page=0";
        BufferedReader br = null;
        BufferedWriter bw = null;

        try {
            downloaddir = downloaddir + File.separator + latestLoaddate;
            URL url = new URL( countquery );
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            getLogger().logToLogger( Level.INFO, "DownloadNCBITraceXML getPagecount site url: " + url );
            is = connection.getInputStream();
            //noinspection IOResourceOpenedButNotSafelyClosed
            br = new BufferedReader( new InputStreamReader( is ) );
            String line;
            if(!( new File( downloaddir ) ).exists()) {
                boolean newdir = new File( downloaddir ).mkdir();
                getLogger().logToLogger( Level.INFO, "DownloadNCBITraceXML getPagecount create newdir: " + newdir );
            }
            fw = new FileWriter( downloaddir + File.separator + "NCBIXMLDownload" + ".out" );
            //noinspection IOResourceOpenedButNotSafelyClosed
            bw = new BufferedWriter( fw );
            while(( line = br.readLine() ) != null) {
                if(line.contains( "Search result" ) || line.contains( "found" ) || line.contains( "items" )) {
                    bw.write( "old line" + line );
                    bw.newLine();
                    String valueline = line.replaceAll( "\\D", "" );
                    bw.write( "valueline = " + valueline );
                    bw.newLine();
                    if(valueline.length() > 0) {
                        querysize = new Integer( valueline ).intValue();
                        getLogger().logToLogger( Level.INFO, "DownloadNCBITraceXML getPagecount querysize: " + querysize );
                        pagecount = Math.round( querysize / pagesize ) + 1;
                    }
                }
            }
            bw.write( "latestLoaddate = " + latestLoaddate );
            bw.newLine();
            bw.write( "querysize = " + querysize );
            bw.newLine();
            is.close();
        }
        catch(IOException e) {
            getLogger().logToLogger( Level.ERROR, "DownloadNCBITraceXML getPagecount Exception " + e.toString() );
        } finally {
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(bw);
        }
        return pagecount;
    }

    public void saveFullXMLFiles( int p ) {

        BufferedReader br = null;
        BufferedWriter bw = null;

        try {
            String pagesquery = "http://www.ncbi.nlm.nih.gov/Traces/trace.cgi?&cmd=retrieve&val=PROJECT_NAME%3D%22TCGA%22%20and%20LOAD_DATE%3E%3D%22" + latestLoaddate + "%22&size=" + querysize + "&dopt=xml_info&dispmax=" + pagesize + "&page=" + p;
            //String pagesquery = "http://www.ncbi.nlm.nih.gov/Traces/trace.cgi?&cmd=retrieve&val=project_name%3D%22TCGA%22%20and%20load_date%3E%3D%2204%2F09%2F2008%22&dopt=info&size=490552&dispmax=5&page=4&next=%3E%3E
            URL url = new URL( pagesquery );
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //System.out.println( "url = " + url );
            is = connection.getInputStream();
            //noinspection IOResourceOpenedButNotSafelyClosed
            br = new BufferedReader( new InputStreamReader( is ) );
            String line;
            fw = new FileWriter( downloaddir + File.separator + xmlfiles + "." + p + ".xml" );
            //fw= new FileWriter("C:\\larry\\WorkingDoc\\"+ latestLoaddate + "\\" + xmlfiles + "." + p + ".xml");
            //noinspection IOResourceOpenedButNotSafelyClosed
            bw = new BufferedWriter( fw );
            bw.write( "<traces>" );
            while(( line = br.readLine() ) != null) {
                for(int i = 0; i < allFieldsWeCare.length; i++) {
                    if(line.contains( allFieldsWeCare[i] )) {
                        if(line.contains( "<pre><trace>" )) {
                            line = "<trace>";
                            //System.out.println(line);
                            bw.write( line );
                            bw.newLine();
                        } else {
                            //System.out.println(line);
                            bw.write( line );
                            bw.newLine();
                        }
                    }
                }
            }
            bw.write( "</traces>" );
            is.close();
            connection.disconnect();
        }
        catch(MalformedURLException e) {
            getLogger().logToLogger( Level.ERROR, "DownloadNCBITraceXML saveXMLFiles MalformedURLException " + e.toString() );
        }
        catch(IOException e) {
            getLogger().logToLogger( Level.ERROR, "DownloadNCBITraceXML saveXMLFiles IOException " + e.toString() );
        } finally {
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(bw);
        }
    }

    public void saveXMLFiles( int p ) {

        BufferedReader br = null;
        BufferedWriter bw = null;

        try {
            String pagesquery = "http://www.ncbi.nlm.nih.gov/Traces/trace.cgi?&cmd=retrieve&val=PROJECT_NAME%3D%22TCGA%22%20and%20LOAD_DATE%3E%3D%22" + latestLoaddate + "%22&size=" + querysize + "&dopt=xml_info&dispmax=" + pagesize + "&page=" + p;
            //String pagesquery = "http://www.ncbi.nlm.nih.gov/Traces/trace.cgi?&cmd=retrieve&val=project_name%3D%22TCGA%22%20and%20load_date%3E%3D%2204%2F09%2F2008%22&dopt=info&size=490552&dispmax=5&page=4&next=%3E%3E
            URL url = new URL( pagesquery );
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //System.out.println( "url = " + url );
            is = connection.getInputStream();
            //noinspection IOResourceOpenedButNotSafelyClosed
            br = new BufferedReader( new InputStreamReader( is ) );
            String line;
            fw = new FileWriter( downloaddir + File.separator + xmlfiles + "." + p + ".xml" );
            //fw= new FileWriter("C:\\larry\\WorkingDoc\\"+ latestLoaddate + "\\" + xmlfiles + "." + p + ".xml");
            //noinspection IOResourceOpenedButNotSafelyClosed
            bw = new BufferedWriter( fw );
            bw.write( "<traces>" );
            while(( line = br.readLine() ) != null) {
                for(int i = 0; i < fieldsWeCareForNow.length; i++) {
                    if(line.contains( fieldsWeCareForNow[i] )) {
                        if(line.contains( "<pre><trace>" )) {
                            line = "<trace>";
                            //System.out.println(line);
                            bw.write( line );
                            bw.newLine();
                        } else {
                            //System.out.println(line);
                            bw.write( line );
                            bw.newLine();
                        }
                    }
                }
            }
            bw.write( "</traces>" );
            //fw.close();
            is.close();
            connection.disconnect();
        }
        catch(MalformedURLException e) {
            getLogger().logToLogger( Level.ERROR, "DownloadNCBITraceXML saveXMLFiles MalformedURLException " + e.toString() );
        }
        catch(IOException e) {
            getLogger().logToLogger( Level.ERROR, "DownloadNCBITraceXML saveXMLFiles IOException " + e.toString() );
        } finally {
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(bw);
        }
    }

    public TraceInfoQueries getTraceInfoQueries() {
        return traceInfoQueries;
    }

    public void setTraceInfoQueries( TraceInfoQueries traceInfoQueries ) {
        this.traceInfoQueries = traceInfoQueries;
    }

    public void setPagecount( int count ) {
        pagecount = count;
    }

    public void setLatestLoaddate( String date ) {
        latestLoaddate = date;
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
