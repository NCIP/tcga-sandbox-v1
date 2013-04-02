/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.common.event;

import org.jboss.netty.channel.ChannelHandlerContext;

import java.io.IOException;

/**
 * An interface for client componenents that acquire resources from the servers
 * and want to communicate that to parties that may be listening.
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public interface Downloader {

    public void addDownloadCompleteListener(DownloadCompleteListener dcl);

    public void addDownloadStartedListener(DownloadStartedListener dsl);

    public void fireDownloadComplete(ChannelHandlerContext ctx, String serverPath, String clientPath) throws IOException;

    public void fireDownloadFailed(ChannelHandlerContext ctx, String serverPath, String clientPath);

    public void fireDownloadStarted(ChannelHandlerContext ctx, String serverPath, String clientPath);

}
