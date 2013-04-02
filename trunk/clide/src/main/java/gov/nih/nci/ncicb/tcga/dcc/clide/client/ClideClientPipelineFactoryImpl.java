/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.client;

import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ThroughputMonitor;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * Creates the message pipeline
 *
 * @author Jon Whitmore Last updated by: $Author$
 * @version $Rev$
 */

@Component
@Scope("prototype")
public class ClideClientPipelineFactoryImpl implements ClideClientPipelineFactory {

    private ThroughputMonitor monitor = null;

    private Timer timeoutTimer = null;

    @Autowired
    private ClientChunkedProtocolHandler clientChunkedProtocolHandler;

    @Autowired
    private ClientProtocolHandler clientProtocolHandler;

    @Autowired
    private ClientArchiveHandler clientArchiveHandler;
    
    @Override
    public ChannelPipeline getPipeline() throws Exception {
    	
    	final ClientContext clientContext = ClideContextHolder.getClientContext();
        final ChannelPipeline pipeline = pipeline();
        pipeline.addLast("decoder", new HttpResponseDecoder());
        pipeline.addLast("encoder", new HttpRequestEncoder());
        if (clientContext.getInternalLogging()) {
            pipeline.addLast("logs", new LoggingHandler());
        }
        pipeline.addLast("handler", new ClientResponseHandler());
        timeoutTimer = new HashedWheelTimer();
        pipeline.addLast("timeout", new IdleStateHandler(timeoutTimer, 0, 0,
                clientContext.getTimeout()));
        pipeline.addLast("protocolChunks", clientChunkedProtocolHandler);
        // Only the protocol handler pays attention to the IdleStateHandler
        pipeline.addLast("protocol", clientProtocolHandler);
        pipeline.addLast("archiver", clientArchiveHandler);

        // enable the throughput monitory
        monitor = new ThroughputMonitor(clientArchiveHandler);
        if (clientContext.getCenter()!=null) {
            final String clientName = clientContext.getCenter().name();
            monitor.setName(clientName + " Throughput - " + monitor.getId());
        }
        monitor.start();

        // wire up our listeners
        clientArchiveHandler.addDownloadCompleteListener(clientProtocolHandler);
        return pipeline;
    }

    @Override
    public ThroughputMonitor getThroughputMonitor() {
        return monitor;
    }
    
    @Override
    public Timer getTimeoutTimer() {
        return timeoutTimer;
    }
    
}//End of Class
