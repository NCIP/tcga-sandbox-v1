/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.client;

import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideContextHolder;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This class is the of our custom Channel Upstream Handlers: If it notices a
 * chunked download begin, it will note the length of the file and the MD5 hash
 * and eat the message assuming the file will start with the next chunk.
 * Otherwise messages are just passed upstream to the protocol handler or the
 * archive handler (if it is a small file, no chunks)
 * 
 * @author Jon Whitmore Last updated by: $
 * @version $
 */

@Component
@Scope("prototype")
public class ClientResponseHandler extends SimpleChannelUpstreamHandler {

	private final Logger logger = Logger.getLogger(ClientResponseHandler.class.getName());

	@Override
	public void handleUpstream(final ChannelHandlerContext ctx,
			final ChannelEvent e) throws Exception {
		if (e instanceof ChannelStateEvent) {
			logger.info(e.toString());
		}
		super.handleUpstream(ctx, e);
	}

	@Override
	public void messageReceived(final ChannelHandlerContext ctx,
			final MessageEvent e) throws Exception {
		final ClientContext clientContext = ClideContextHolder.getClientContext();
		if (!clientContext.isReadingChunks()) {
			HttpResponse response = (HttpResponse) e.getMessage();
			parseAndLogHeaders(response,clientContext);
			if (response.getStatus().getCode() == 200 && response.isChunked()) {
				// this is the start of a big file, next chunk will be the first
				// part of it
				clientContext.setReadingChunks(true);
				ClideContextHolder.setClientContext(clientContext);

			} else {
				// this a small file, response to a command, or garbage content
				ctx.sendUpstream(e);
			}
		} else {
			// send this message back to the pipeline for the
			// ClientArchiveHandler or
			// ClientChunkedProtocolHandler to eat the chunk
			ctx.sendUpstream(e);
		}

	}

	/**
	 * Set content length and MD5 in the ClientContext if available, and log the
	 * important header information
	 * 
	 * @param response
	 *            the HTTP response from the server
	 */
    private void parseAndLogHeaders(final HttpResponse response, final ClientContext clientContext) {
        StringBuilder sb = new StringBuilder(100);
        sb.append(response.getProtocolVersion()).append(" ").append(response.getStatus());

        if (!response.getHeaderNames().isEmpty()) {
            for (final String name : response.getHeaderNames()) {
                for (final String value : response.getHeaders(name)) {
                    sb.append(", ").append(name).append(" = ").append(value);
                    if (name.equals(HttpHeaders.Names.CONTENT_LENGTH)) {
                        clientContext.getFactory().getThroughputMonitor().setExpectedBytes(Long.parseLong(value));
                    }
                    if (name.equals(HttpHeaders.Names.CONTENT_MD5)) {
                        clientContext.setExpectedMD5(value);
                        ClideContextHolder.setClientContext(clientContext);
                    }
                }
            }
            logger.info(sb.toString());
		}
	}
}
