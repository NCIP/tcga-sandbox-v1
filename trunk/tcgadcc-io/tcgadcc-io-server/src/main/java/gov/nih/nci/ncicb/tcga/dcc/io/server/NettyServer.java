/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Extension of the {@link Server} interface that defines the behavior specific
 * to server implementations using Netty.
 * 
 * @author nichollsmc
 */
public interface NettyServer extends Server {

    /**
     * Create and return a configured {@link ServerBootstrap} instance.
     * 
     * @return configured {@link ServerBootstrap} instance
     */
    ServerBootstrap createServerBootstrap();

    /**
     * Retrieve the pre-configured {@link ServerBootstrap} instance.
     * 
     * @return the pre-configured {@link ServerBootstrap} instance
     */
    ServerBootstrap getServerBootstrap();

    /**
     * Set a pre-configured {@link ServerBootstrap} instance.
     * 
     * @return the pre-configured {@link ServerBootstrap} to set
     */
    void setServerBootstrap(ServerBootstrap serverBootstrap);

    /**
     * Retrieve the pre-configured {@link ChannelInitializer} instance used to
     * initialize the server.
     * <p>
     * The returned instance will of the type {@link SocketChannel}.
     * 
     * @return the pre-configured {@link ChannelInitializer} instance used to
     *         initialize the server
     */
    ChannelInitializer<SocketChannel> getChannelInitializer();

    /**
     * Set a pre-configured {@link ChannelInitializer} instance that the server
     * will use for initialization at start-up.
     * 
     * @return the pre-configured {@link ChannelInitializer} to set
     */
    void setChannelInitializer(ChannelInitializer<SocketChannel> channelInitializer);

}
