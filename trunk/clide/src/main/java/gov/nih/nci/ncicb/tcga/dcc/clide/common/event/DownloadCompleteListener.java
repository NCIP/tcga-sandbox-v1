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
 * An interface for interested parties to monitor what has been downloaded
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public interface DownloadCompleteListener {

    public void downloadCompleted(ChannelHandlerContext ctx, String serverPath, String clientPath) throws IOException;

    public void downloadFailed(ChannelHandlerContext ctx, String serverPath, String clientPath);

}
