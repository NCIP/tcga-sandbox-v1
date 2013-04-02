/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAOImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.ArchiveQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.service.FileTypeLookup;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLoggerI;
import oracle.jdbc.pool.OracleDataSource;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcliveCloseableUtil.close;

/**
 * TODO add class javadoc description
 *
 * @author David Nassau
 * @version $Rev$
 */
public class CommandLineLoader {
    static ProcessLoggerI logger = new BareBonesLogger(); //maybe replace with a real log4j but then you need to set it up

    static DataSource dataSource;   //todo  self-configure datasource (injected for test)
    static DataSource dccCommonDataSource;

    static int archiveLimit;

    static class FileListRecord {
        String archiveDir, mageDir, filetype, filename;
    }

    public static void main(final String[] args) {
        try {
            if (args.length != 1) {
                throw new Exception("Expected filename argument");
            }
            configureDataSource();
            configureDccCommonDataSource();
            List<FileListRecord> fileList = readFileList(args[0]);
            CommandLineLoader cll = new CommandLineLoader(fileList);
            cll.go();
        } catch (Exception e) { //catch everything including runtime exceptions
            logger.logToLogger(Level.ERROR, String.format("ERROR: %s", e.getMessage()));
        }
    }

    private static void configureDataSource() throws IOException, SQLException {

        InputStream in = null;
        try {
            File f = new File("CommandLineLoader.properties");
            if (!f.exists()) {
                throw new IOException("File CommandLineLoader.properties not found");
            }
            Properties props = new Properties();
            //noinspection IOResourceOpenedButNotSafelyClosed
            in = new FileInputStream(f);
            props.load(in);
            String server = props.getProperty("oracle.server");
            if (server == null || server.length() == 0) {
                throw new IOException("Property oracle.server not found in CommandLineLoader.properties");
            }
            String port = props.getProperty("oracle.port");
            if (port == null || port.length() == 0) {
                throw new IOException("Property oracle.port not found in CommandLineLoader.properties");
            }
            String sid = props.getProperty("oracle.sid");
            if (sid == null || sid.length() == 0) {
                throw new IOException("Property oracle.sid not found in CommandLineLoader.properties");
            }
            String user = props.getProperty("oracle.user");
            if (user == null || user.length() == 0) {
                throw new IOException("Property oracle.user not found in CommandLineLoader.properties");
            }
            String password = props.getProperty("oracle.password");
            if (password == null || password.length() == 0) {
                throw new IOException("Property oracle.password not found in CommandLineLoader.properties");
            }

            String s_archiveLimit = props.getProperty("archiveLimit");
            if (s_archiveLimit == null || s_archiveLimit.length() == 0) {
                throw new IOException("Property archiveLimit not found in CommandLineLoader.properties");
            }
            archiveLimit = Integer.parseInt(s_archiveLimit);

            OracleDataSource ds = new OracleDataSource();
            ds.setDriverType("thin");
            ds.setServerName(server);
            ds.setPortNumber(Integer.parseInt(port));
            ds.setDatabaseName(sid);
            ds.setUser(user);
            ds.setPassword(password);

            //static instance shared across all loader threads
            dataSource = ds;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    
    private static void configureDccCommonDataSource() throws IOException, SQLException {

        InputStream in = null;
        try {
            File f = new File("CommandLineLoader.properties");
            if (!f.exists()) {
                throw new IOException("File CommandLineLoader.properties not found");
            }
            Properties props = new Properties();
            //noinspection IOResourceOpenedButNotSafelyClosed
            in = new FileInputStream(f);
            props.load(in);
            in.close();
            String server = props.getProperty("dccCommon.server");
            if (server == null || server.length() == 0) {
                throw new IOException("Property dccCommon.server not found in CommandLineLoader.properties");
            }
            String port = props.getProperty("dccCommon.port");
            if (port == null || port.length() == 0) {
                throw new IOException("Property dccCommon.port not found in CommandLineLoader.properties");
            }
            String sid = props.getProperty("dccCommon.sid");
            if (sid == null || sid.length() == 0) {
                throw new IOException("Property dccCommon.sid not found in CommandLineLoader.properties");
            }
            String user = props.getProperty("dccCommon.user");
            if (user == null || user.length() == 0) {
                throw new IOException("Property dccCommon.user not found in CommandLineLoader.properties");
            }
            String password = props.getProperty("dccCommon.password");
            if (password == null || password.length() == 0) {
                throw new IOException("Property dccCommon.password not found in CommandLineLoader.properties");
            }

            OracleDataSource ds = new OracleDataSource();
            ds.setDriverType("thin");
            ds.setServerName(server);
            ds.setPortNumber(Integer.parseInt(port));
            ds.setDatabaseName(sid);
            ds.setUser(user);
            ds.setPassword(password);

            //static instance shared across all loader threads
            dccCommonDataSource = ds;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private static List<FileListRecord> readFileList(final String filename) throws IOException {

        final List<FileListRecord> recordList = new ArrayList<FileListRecord>();
        BufferedReader reader = null;

        try {
            File f = checkSpecFileExists(filename);
            //noinspection IOResourceOpenedButNotSafelyClosed
            reader = new BufferedReader(new FileReader(f));
            String line = reader.readLine();
            while (line != null) {
                if (line.trim().length() != 0) { //ignore blank lines
                    String[] tokens = line.split("\t");
                    if (tokens.length != 4) {
                        throw new IOException("Expecting 4 tokens in each line of loader specification file");
                    }
                    FileListRecord record = new FileListRecord();
                    record.archiveDir = tokens[0];
                    record.mageDir = tokens[1];
                    record.filetype = tokens[2];
                    record.filename = tokens[3];
                    recordList.add(record);
                }
                line = reader.readLine();
            }
            if (recordList.size() == 0) {
                throw new IOException("No records found in loader specification file");
            }

            //make sure it's sorted - an ungrouped list would cause all kinds of havoc
            Collections.sort(recordList, new Comparator<FileListRecord>() {
                public int compare(final FileListRecord o1, final FileListRecord o2) {
                    int comp = o1.archiveDir.compareTo(o2.archiveDir);
                    if (comp == 0) {
                        comp = o1.filetype.compareTo(o2.filetype);
                    }
                    return comp;
                }
            });
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return recordList;
    }

    private static File checkSpecFileExists(final String filename) throws IOException {
        //first check in the user dir
        File f = new File(System.getProperty("user.dir") + "/" + filename);
        if (!f.exists()) {
            //try it as an absolute path
            f = new File(filename);
            if (!f.exists()) {
                throw new IOException(String.format("Could not find file %s", filename));
            }
        }
        return f;
    }

    private ArchiveQueries commonArchiveQueries;
    private ArchiveQueries diseaseArchiveQueries;
    private LoaderQueries loaderQueries;
    private UUIDDAO uuidDAO;
    private List<FileListRecord> fileList;

    public CommandLineLoader(final List<FileListRecord> fileList) {
        LoaderQueriesJdbcImpl lq = new LoaderQueriesJdbcImpl();
        ArchiveQueriesJDBCImpl diseaseArchiveImpl = new ArchiveQueriesJDBCImpl();
        ArchiveQueriesJDBCImpl commonArchiveImpl = new ArchiveQueriesJDBCImpl();
        UUIDDAOImpl uuidDaoImpl = new UUIDDAOImpl();
        lq.setDataSource(CommandLineLoader.dataSource); //inject the static datasource into loader
        lq.setLogger(CommandLineLoader.logger);         //ditto the logger
        uuidDaoImpl.setDataSource(CommandLineLoader.dccCommonDataSource);
        diseaseArchiveImpl.setDataSource(CommandLineLoader.dataSource) ;
        commonArchiveImpl.setDataSource(CommandLineLoader.dccCommonDataSource) ;

        loaderQueries = lq;
        diseaseArchiveQueries =  diseaseArchiveImpl;
        commonArchiveQueries = commonArchiveImpl;
        uuidDAO = uuidDaoImpl;
        this.fileList = fileList;
    }

    class LoaderThread extends Thread {
        private String archive, mage;
        private boolean done = false;
        private FileTypeLookup ftLookup;

        public LoaderThread(final String archive, final String mage, final FileTypeLookup ftLookup) {
            this.archive = archive;
            this.mage = mage;
            this.ftLookup = ftLookup;
        }
        
        public synchronized boolean isDone() {
            return done;
        }
        
        public synchronized void setDone(final boolean done) {
            this.done = done;
        }

        public void run() {
            Loader loader = new Loader();
            loader.setLoadDirectory(archive);
            if (mage != null) {
                loader.setMagetabDirectory(mage);
            }
            loader.setLoaderQueries(loaderQueries); //one DAO instance shared across threads
            loader.setCommonArchiveQueries(commonArchiveQueries);
            loader.setDiseaseArchiveQueries(diseaseArchiveQueries);
            loader.setUuidDAO(uuidDAO);
            loader.setLogger(CommandLineLoader.logger);
            loader.setFileTypeLookup(ftLookup);
            try {
                loader.go();
                logger.logToLogger(Level.INFO, String.format("++++ LOADER COMPLETED FOR ARCHIVE %s", archive));
            } catch (Exception e) {
                logger.logToLogger(Level.ERROR, String.format("!!! LOADER FAILED FOR ARCHIVE %s", archive));
            } finally {
                setDone(true);
                wakeParentThread(); //so the parent thread can tell when all loader threads are done
            }
        }
    }

    public void go() {
        List<LoaderThread> loaderThreads = new ArrayList<LoaderThread>();

        //create filetype lookup objects, one per archive; from them create loader threads
        List<CommandLineFileTypeLookup> ftLookups = createFileTypeCallbackInstances();
        if (ftLookups.size() > archiveLimit) {
            throw new IllegalStateException(String.format("No more than %d archives can be run simultaneously; please reduce input file", archiveLimit));
        }

        for (final CommandLineFileTypeLookup ftLookup : ftLookups) {
            LoaderThread loaderThread = new LoaderThread(ftLookup.getArchive(), ftLookup.getMage(), ftLookup);
            loaderThreads.add(loaderThread);
            loaderThread.start();
        }
        //now wait till all threads are done
        synchronized (this) {
            while (!allThreadsDone(loaderThreads)) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        logger.logToLogger(Level.INFO, "++++ ALL LOADER JOBS DONE");
    }

    private List<CommandLineFileTypeLookup> createFileTypeCallbackInstances() {
        List<CommandLineFileTypeLookup> ftLookups = new ArrayList<CommandLineFileTypeLookup>();
        String previousArchive = null;
        CommandLineFileTypeLookup ftLookup = null;
        for (final FileListRecord sfr : fileList) {
            if (previousArchive == null || !previousArchive.equals(sfr.archiveDir)) {
                ftLookup = new CommandLineFileTypeLookup(sfr.archiveDir, sfr.mageDir);
                ftLookups.add(ftLookup);
            }
            ftLookup.addFile(sfr.filename, sfr.filetype);
            previousArchive = sfr.archiveDir;
        }
        return ftLookups;
    }

    private boolean allThreadsDone(final List<LoaderThread> loaderThreads) {
        boolean ret = true;
        for (final LoaderThread loaderThread : loaderThreads) {
            if (!loaderThread.isDone()) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    synchronized void wakeParentThread() {
        notifyAll();
    }
}
