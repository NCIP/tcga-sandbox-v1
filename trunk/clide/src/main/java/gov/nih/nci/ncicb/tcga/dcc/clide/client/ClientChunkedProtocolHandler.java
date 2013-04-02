/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.client;

import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants.UTF8;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideChunkedProtocolMessage;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideContextHolder;

import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This class is in the pipeline to catch very long server responses that had to
 * be broken into chunks. The chunks are read until the final one is received
 * and then sent to the protocol handler
 * 
 * @author Jon Whitmore Last updated by: $
 * @version $
 */

@Scope("prototype")
@Component
public class ClientChunkedProtocolHandler extends SimpleChannelUpstreamHandler {

	private final Logger logger = Logger.getLogger(getClass());

	// Yes I mean StringBuffer as CLIDE is multithreaded
	private StringBuffer responseBuffer = null;

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx,
			final ExceptionEvent e) {
		logger.error("Unexpected error", e.getCause());
		Channel ch = e.getChannel();
		ch.close();
	}

	@Override
	public void messageReceived(final ChannelHandlerContext ctx,
			final MessageEvent e) throws InterruptedException {
		ClientContext clientContext = ClideContextHolder.getClientContext();
		if (clientContext.isReadingChunks()
				&& clientContext.getRequestedPath() == null) {
			// this is a command so long the response from the server was
			// chunked
			HttpChunk chunk = (HttpChunk) e.getMessage();
			if (chunk.isLast()) {
				// last chunk doesn't have content.
				clientContext.setReadingChunks(false);
				ClideContextHolder.setClientContext(clientContext);
				String response = responseBuffer.toString();
				responseBuffer = null;
				// Create a ClideChunkedProtocolMessage and send upStream
				ClideChunkedProtocolMessage message = new ClideChunkedProtocolMessage(
						e.getChannel(), response);
				ctx.sendUpstream(message);
			} else {
				// continue to append chunks
				ChannelBuffer buf = chunk.getContent();
				if (responseBuffer == null) {
					// this is the first chunk, all others will be appended
					responseBuffer = new StringBuffer();
				}
				responseBuffer.append(buf.toString(Charset.forName(UTF8)));
			}
		} else {
			// this is a normal chunked file upload
			ctx.sendUpstream(e);
		}
	}

}