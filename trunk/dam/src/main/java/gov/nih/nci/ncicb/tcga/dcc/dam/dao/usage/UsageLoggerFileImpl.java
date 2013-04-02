/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of UsageLogger that writes usage information to a file.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class UsageLoggerFileImpl extends AbstractUsageLogger {

    // date format used for this class
    protected static final String DATE_FORMAT_STRING = "MM/dd/yyyy HH:mm:ss zzz";
    private String filename; // filename for logging
    private Writer writer;   // handle to the file writer
    private String logdir;   // directory for logging
    private boolean append = false;

    /**
     * Sets the filename to current time in milliseconds + .log, and append to false
     *
     * @param logdir the directory in which to write the usage log
     */
    public UsageLoggerFileImpl(String logdir) {
        this(logdir, null, false);
    }

    /**
     * Will write usage log to logdir/filename
     *
     * @param logdir   the directory in which to write the usage log
     * @param filename the name of the file; will be created if it doesn't exist; will be overwritten if exists!
     */
    public UsageLoggerFileImpl(String logdir, String filename) {
        this(logdir, filename, false);
    }

    /**
     * Will write to or append to logdir/filename depending on append flag.  If filename is null,
     * will be set to [time_in_milliseconds].log.
     *
     * @param logdir   the directory in which to write the usage log
     * @param filename the name of the file to write to
     * @param append   if true, append to current file (given by filename), if false, overwrite it
     */
    public UsageLoggerFileImpl(String logdir, String filename, boolean append) {
        if (filename == null) {
            filename = System.currentTimeMillis() + ".log";
        }
        this.logdir = logdir;
        this.filename = filename;
        this.append = append;
    }

    /**
     * If start date and end date are null, returns all sessions.  If start date is null, returns all sessions from
     * first recorded until end date.  If end date is null, returns all sessions since start date.
     *
     * @return a List of all UsageSessions that have been recorded between start and end date
     * @throws UsageLoggerException if there is an error
     * @param startDate the start date, or null
     * @param endDate the end date, or null
     */
    public List<UsageSession> getAllSessionsForDates(Date startDate, Date endDate) throws UsageLoggerException {
        List<UsageSession> sessions = new ArrayList<UsageSession>();
        BufferedReader logFile = null;
        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            logFile = new BufferedReader(new FileReader(getFile()));
            String line = logFile.readLine();
            Pattern actionPattern = Pattern.compile("^\\[(.+)\\](.+)=(.*)@(.+)$");
            UsageSession currentSession = null;
            while (line != null) {
                Matcher m = actionPattern.matcher(line);
                if (m.matches()) {
                    String sessionId = m.group(1);
                    String actionName = m.group(2);
                    String value = m.group(3);
                    String dateString = m.group(4);
                    Date date = getDateFormat().parse(dateString);
                    boolean include = true;
                    if (startDate != null && date.before(startDate)) {
                        include = false;
                    }
                    if (endDate != null && date.after(startDate)) {
                        include = false;
                    }
                    if (include) {
                        if (currentSession == null || !sessionId.equals(currentSession.sessionKey)) {
                            currentSession = new UsageSession();
                            sessions.add(currentSession);
                        }
                        UsageSessionAction action = new UsageSessionAction();
                        action.name = actionName;
                        action.value = value;
                        action.actionTime = date;
                        currentSession.actions.add(action);
                    }
                }
                line = logFile.readLine();
            }
        }
        catch (FileNotFoundException e) {
            throw new UsageLoggerException("Could not open usage log", e);
        }
        catch (IOException e) {
            throw new UsageLoggerException("Error reading log", e);
        }
        catch (ParseException e) {
            throw new UsageLoggerException("Error parsing date in log", e);
        } finally {
            IOUtils.closeQuietly(logFile);
        }
        return sessions;
    }

    /**
     * Format for File-based implementation is "MM/dd/yyyy HH:mm:ss zzz"
     *
     * @return the date format string
     */
    public String getDateFormatString() {
        return DATE_FORMAT_STRING;
    }

    /**
     * If you want the full path to the file, use getFile.
     *
     * @return the filename that this uses for logging (note: directory/path not included)
     */
    public String getFilename() {
        return this.filename;
    }

    /**
     * @return the File object representing the file that this uses for logging
     * @throws java.io.IOException in case we can't create the file.
     */
    public File getFile() throws IOException {
        final File theFile = new File(getLogdir() + File.separator + getFilename());
        if (!theFile.exists()) {
            boolean fileCreated = theFile.createNewFile();
            if (!fileCreated) {
                throw new IOException("Failed to create file " + theFile.getPath());
            }
        }
        return theFile;
    }

    /**
     * @return the directory in which this writes its log file
     */
    public String getLogdir() {
        return logdir;
    }
    // -------- Private/Protected methods ---------

    /**
     * Convert null values.  Use an empty string to avoid the word "null" showing up in log files.
     *
     * @return an empty string
     */
    protected String convertNull() {
        return "";
    }

    // Writes the action to a file.
    protected void writeAction(String sessionId, Date date, String actionName,
                               Object value) throws UsageLoggerException {
        // build the string to write to the file
        StringBuffer msg = new StringBuffer();
        msg.append("[");
        msg.append(sessionId);
        msg.append("]");
        msg.append(actionName);
        msg.append("=");
        msg.append(value);
        msg.append("@");
        msg.append(convert(date));
        msg.append(System.getProperty("line.separator"));
        log(msg.toString());
    }

    /*
     * Gets the Writer used for writing.  Will initialize if it hasn't been created yet.
     */
    private Writer getWriter() throws IOException {
        if (this.writer == null) {
            this.writer = new PrintWriter(new BufferedWriter(new FileWriter(getFile(), append)));
        }
        return this.writer;
    }

    /*
     * Prints the given message using the Writer.  Re-throws IOExceptions as UsageLoggerExceptions.
     */
    private void log(String msg) throws UsageLoggerException {
        try {
            Writer out = getWriter();
            out.append(msg);
            out.flush();
        }
        catch (IOException ioe) {
            throw new UsageLoggerException(ioe);
        }
    }
}  // end of UsageLoggerFileImpl
