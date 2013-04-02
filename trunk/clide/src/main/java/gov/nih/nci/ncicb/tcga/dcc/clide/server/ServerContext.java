/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.server;

import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is currently a band aid to share values between client objects that currently do not have direct
 * communication with each other
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public class ServerContext {

    public static boolean fileInProgress = false;

    private static ChannelGroup allChannels = new DefaultChannelGroup();

    private static boolean internalLogging;
    private static boolean serverOnWindows;

    private static File downloadDir = null;
    private static int port = 0;
    private static String address = null;

    private static File publicKey = null;
    private static int timeout;
    private static File workingDirectory;
    private static File sentDirectory;
    private static ClideServerPipelineFactory factory = null;

    public static ClideServerPipelineFactory getFactory() {
        return factory;
    }

    public static void setFactory(ClideServerPipelineFactory factory) {
        ServerContext.factory = factory;
    }

    public static boolean isServerOnWindows() {
        return serverOnWindows;
    }

    public static void setServerOnWindows(boolean serverOnWindows) {
        ServerContext.serverOnWindows = serverOnWindows;
    }

    public static void setPublicKey(final File file) {
        publicKey = file;
    }

    public static File getPublicKey() {
        return publicKey;
    }

    public static void setInternalLogging(final boolean bool) {
        internalLogging = bool;
    }

    public static boolean getInternalLogging() {
        return internalLogging;

    }

    public static void addChannel(final Channel chan) {
        allChannels.add(chan);

    }

    public static void removeChannel(final Channel chan) {
        allChannels.remove(chan);

    }

    public static ChannelGroupFuture closeChannels() {
        return allChannels.close();

    }

    public static File getDownloadDir() {
        return downloadDir;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(final int port) {
        ServerContext.port = port;
    }

    public static String getAddress() {
        return address;
    }

    public static void setAddress(final String address) {
        ServerContext.address = address;
    }

    public static void setDownloadDir(final File downloadDir) {
        ServerContext.downloadDir = downloadDir;
    }

    public static ArrayList<String> getFilesToSend() throws IOException {
        ArrayList<String> list = new ArrayList<String>();
        File dir = getDownloadDir();
        if (!dir.isDirectory()) {
            return list;

        }
        File[] archives = dir.listFiles(new ClideConstants.ArchiveFilter());
        for (final File archive : archives) {
            list.add(archive.getCanonicalPath());
        }
        return list;

    }

    public static void setTimeout(final int timeoutInSeconds) {
        timeout = timeoutInSeconds;
    }

    public static int getTimeout() {
        return timeout;
    }

    public static void setWorkingDir(final File workingDir) {
        workingDirectory = workingDir;
    }

    public static File getWorkingDir() {
        return workingDirectory;
    }

    public static void setSentDir(final File sentDir) {
        sentDirectory = sentDir;
    }

    public static File getSentDir() {
        return sentDirectory;
    }
}
