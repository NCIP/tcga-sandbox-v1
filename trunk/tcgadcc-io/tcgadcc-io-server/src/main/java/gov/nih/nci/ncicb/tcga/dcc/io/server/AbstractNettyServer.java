/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server;

import gov.nih.nci.ncicb.tcga.dcc.io.api.session.Session;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;

/**
 * Abstract implementation of the {@link NettyServer} interface.
 * 
 * @author nichollsmc
 */
public abstract class AbstractNettyServer implements NettyServer {

    protected ServerBootstrap                   serverBootstrap;
    protected ChannelInitializer<SocketChannel> channelInitializer;
    protected InetSocketAddress                 inetSocketAddress;
    protected Session                           session;

    @Override
    public ServerBootstrap getServerBootstrap() {
        return serverBootstrap;
    }

    @Override
    public void setServerBootstrap(ServerBootstrap serverBootstrap) {
        this.serverBootstrap = serverBootstrap;
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return channelInitializer;
    }

    @Override
    public void setChannelInitializer(ChannelInitializer<SocketChannel> channelInitializer) {
        this.channelInitializer = channelInitializer;
    }

    @Override
    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    @Override
    public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }
    
    @Override
    public String toString() {
        return new StringBuilder().append("[host:")
                                  .append(inetSocketAddress.getHostName())
                                  .append("],")
                                  .append("[port:")
                                  .append(inetSocketAddress.getPort())
                                  .append("]")
                                  .toString();
    }
    
}
