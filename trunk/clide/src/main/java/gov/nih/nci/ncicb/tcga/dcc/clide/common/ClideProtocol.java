/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.common;

import java.util.Map;
import java.util.TreeMap;

/**
 * Defines client commands and server responses as enums, returns commands for enum members and incorporates regular
 * expressions into the commands and responses for validation.  The regular expressions allow us to validate received
 * commands and responses with little code and lots of confidence.  This was a design decision aimed to make it very
 * hard to produce a malformed message.  These regular expressions can also be replaced with values and the resulting
 * string can be augmented and used as a message to be sent over HTTP.
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public class ClideProtocol {

    public static final String PATH_KEY = "path";
    public static final String FILE_SIZE_KEY = "fileSize";

    /* Allow letters, numbers, forward slashes, backslashes, dashes, underscores and periods */
    public static final String PATH_REGEX = PATH_KEY + "=[a-zA-Z0-9/\\\\_\\.:-]+ " +
            FILE_SIZE_KEY + "=[0-9]+";

    /* Allow "path=foo fileSize=12 path=bar fileSize=3456 path=/f/b/g-_-g.txt fileSize=7890" */
    public static final String PATHS_REGEX = "[" + PATH_REGEX + " ]+;";

    public static final String CMD_KEY = "cmd";

    public static final String RESP_KEY = "resp";

    public static final String ENC_KEY = "enc";

    public static final String TRUE_FALSE_REGEX = "(true|false|True|False|TRUE|FALSE)";

    public static final String ENC_REGEX = ENC_KEY + "=" + TRUE_FALSE_REGEX;

    public static final String COUNT_KEY = "count";

    public static final String INTEGER_REGEX = "[0-9]+";

    public static final String COUNT_REGEX = COUNT_KEY + "=" + INTEGER_REGEX;

    public enum CLIENT {
        hello,
        whatAreTheFiles,
        giveMeThisFile,
        receivedThisFileWithoutError,
        downloadOfAllFilesCompleted,
        getServerLogFile,
        unableToFindNextCommand,
        terminateConnection,
        commandNotFound // the server is never going to send this.  it is an indication that we got garbage back
    }

    public enum SERVER {
        iHaveFilesToSend,
        hereAreTheFilePathsWithSize,
        sendServerLogFile,
        failure,
        unableToRespond,
        terminatingConnection,
        responseNotFound // the client is never going to send this. it is an indication that we sent garbage
    }
    
    public enum MANAGER{
    	startBroad,
    	startUNC,
    	pasueCenter
    }

    private static final Map<CLIENT, String> CLIENT_COMMANDS = new TreeMap<CLIENT, String>();
    private static final Map<SERVER, String> SERVER_RESPONSES = new TreeMap<SERVER, String>();

    static {
        SERVER_RESPONSES.put(SERVER.iHaveFilesToSend, "resp=iHaveFilesToSend count=" + INTEGER_REGEX + ";");
        SERVER_RESPONSES.put(SERVER.hereAreTheFilePathsWithSize, "resp=hereAreTheFilePathsWithSize " + PATHS_REGEX);
        SERVER_RESPONSES.put(SERVER.failure, "resp=failure code=" + INTEGER_REGEX + ";");
        SERVER_RESPONSES.put(SERVER.unableToRespond, "resp=unableToRespond;");
        SERVER_RESPONSES.put(SERVER.terminatingConnection, "resp=terminatingConnection;");
        SERVER_RESPONSES.put(SERVER.sendServerLogFile, "resp=sendServerLogFile;");

        CLIENT_COMMANDS.put(CLIENT.hello, "cmd=hello forceValidate=" + TRUE_FALSE_REGEX + ";");
        CLIENT_COMMANDS.put(CLIENT.whatAreTheFiles, "cmd=whatAreTheFiles;");
        CLIENT_COMMANDS.put(CLIENT.giveMeThisFile, "cmd=giveMeThisFile " + PATH_REGEX + " " + ENC_REGEX + ";");
        CLIENT_COMMANDS.put(CLIENT.receivedThisFileWithoutError, "cmd=receivedThisFileWithoutError " + PATH_REGEX + ";");
        CLIENT_COMMANDS.put(CLIENT.downloadOfAllFilesCompleted, "cmd=downloadOfAllFilesCompleted;");
        CLIENT_COMMANDS.put(CLIENT.getServerLogFile, "cmd=getServerLogFile;");
        CLIENT_COMMANDS.put(CLIENT.unableToFindNextCommand, "cmd=unableToFindNextCommand;");
        CLIENT_COMMANDS.put(CLIENT.terminateConnection, "cmd=terminateConnection;");
    }

    public static String get(final CLIENT c) {
        return CLIENT_COMMANDS.get(c);
    }

    public static String get(final SERVER s) {
        return SERVER_RESPONSES.get(s);
    }

    public static CLIENT findCommand(final String response) {
        if (response != null) {
            for (final CLIENT c : CLIENT.values()) {
                if (matches(c, response)) {
                    return c;
                }
            }
        }
        return CLIENT.commandNotFound;  //don't know how to react to what the server sent us
    }
    
    
    
    public static SERVER findResponse(final String message) {
        if (message != null) {
            for (final SERVER s : SERVER.values()) {
                if (matches(s, message)) {
                    return s;
                }
            }
        }
        return SERVER.responseNotFound;  //don't know how to react to what the client sent us
    }

    public static boolean matches(final SERVER s, final String possibleResponse) {
        String response = get(s);
        // this if statement is necessary because responseNotFound is intentionally absent
        // from SERVER_RESPONSES
        if (response == null) {
            // doesn't exist
            return false;
        }
        return possibleResponse.matches(response);
    }

    public static boolean matches(final CLIENT c, final String possibleCommand) {
        String command = get(c);
        // this if statement is necessary because commandNotFound is intentionally absent
        // from CLIENT_COMMANDS
        if (command == null) {
            // doesn't exist
            return false;
        }
        return possibleCommand.matches(get(c));
    }

}
