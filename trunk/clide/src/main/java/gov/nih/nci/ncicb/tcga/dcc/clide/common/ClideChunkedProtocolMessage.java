/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.common;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.MessageEvent;

import java.net.SocketAddress;

/**
 * Represents a very long protocol message from the either the client or server that is received in chunks because it
 * exceeds the length of a single HTTP message's content length
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public class ClideChunkedProtocolMessage implements ChannelEvent, MessageEvent {

    public ClideChunkedProtocolMessage(final Channel channel, final String response) {
        this.channel = channel;
        this.response = response;
    }

    /**
     * All protocol chunks from the server combined into a string
     */
    private String response;

    private Channel channel;

    public Object getMessage() {
        return response;
    }

    /**
     * No SocketAddress needed. This message will be intercepted by the ClideProtocolHandler
     * @return null
     */
    public SocketAddress getRemoteAddress() {
        return null;
    }

    public Channel getChannel() {
        return channel;
    }

    /**
     * There is no ChannelFuture here.  The entire message has been recieved.  Only the
     * ClideProtocolHandler should receive this message
     * @return null
     */
    public ChannelFuture getFuture() {
        return null;
    }
}
