/*
 * Copyright 2009 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package gov.nih.nci.ncicb.tcga.dcc.clide.server;

import gov.nih.nci.ncicb.tcga.dcc.clide.common.ThroughputMonitor;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * Creates the CLIDE Http server message pipeline.
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */

@Component
public class ClideServerPipelineFactory implements ChannelPipelineFactory {

    private ThroughputMonitor monitor = null;

    private Timer timeoutTimer = null;

    private final Logger logger = Logger.getLogger(
            ClideServerPipelineFactory.class.getName());

    @Autowired
    private ServerResponseHandler serverResponseHandler;

    @Autowired
    private ServerProtocolHandler serverProtocolHandler;

    @Autowired
    private ServerArchiveHandler serverArchiveHandler;


    public void stopTimeoutTimer(){
        if (timeoutTimer != null) {
            timeoutTimer.stop();

        } else {
            logger.warn("timeoutTimer was null when shutdown was attempted.");
        }

    }

    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());
        // NOTE:  this is a big performance hit, even for small file transfers
        if (ServerContext.getInternalLogging()) {
            pipeline.addLast("logs", new LoggingHandler());
        }
        timeoutTimer = new HashedWheelTimer();
        pipeline.addLast("timeout", new IdleStateHandler(timeoutTimer, 0, 0, ServerContext.getTimeout()));
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
        pipeline.addLast("responseHandler", serverResponseHandler);
        pipeline.addLast("protocolHandler", serverProtocolHandler);
        pipeline.addLast("archiveHandler", serverArchiveHandler);

        // enable the throughput monitory
        monitor = new ThroughputMonitor(serverArchiveHandler);
        monitor.start();

        return pipeline;
    }

    public ThroughputMonitor getMonitor() {
        return monitor;
    }
}
