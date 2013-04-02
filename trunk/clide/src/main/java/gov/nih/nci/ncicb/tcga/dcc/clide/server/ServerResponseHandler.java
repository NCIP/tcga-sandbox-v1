/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.server;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.springframework.stereotype.Component;

/**
 * Act as the server's SSL negotiator and otherwise pass any message received.
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */

@Component
@ChannelHandler.Sharable
public class ServerResponseHandler extends SimpleChannelUpstreamHandler {

    private final Logger logger = Logger.getLogger(
            ServerResponseHandler.class.getName());

    @Override
    public void handleUpstream(
            final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            ChannelStateEvent event = (ChannelStateEvent) e;
            if (event.getState() == ChannelState.OPEN
                    || event.getState() == ChannelState.BOUND
                    || event.getState() == ChannelState.OPEN) {
                logger.info(e.toString());
            }
        }
        super.handleUpstream(ctx, e);
    }

    @Override
    /**
     * Forward everything to the protocol handler
     */
    public void messageReceived(
            final ChannelHandlerContext ctx, final MessageEvent e) {
        ctx.sendUpstream(e);
    }

    @Override
    public void exceptionCaught(
            final ChannelHandlerContext ctx, final ExceptionEvent e) {
        logger.warn(
                "Unexpected exception from downstream.",
                e.getCause());
        e.getChannel().close();
    }

}
