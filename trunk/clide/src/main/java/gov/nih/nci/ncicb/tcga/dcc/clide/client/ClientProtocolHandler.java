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
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtils;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.event.DownloadCompleteListener;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants.UTF8;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.CLIENT.downloadOfAllFilesCompleted;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.CLIENT.giveMeThisFile;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.CLIENT.receivedThisFileWithoutError;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.CLIENT.terminateConnection;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.CLIENT.unableToFindNextCommand;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.CLIENT.whatAreTheFiles;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.COUNT_KEY;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.ENC_KEY;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.ENC_REGEX;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.FILE_SIZE_KEY;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.INTEGER_REGEX;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.PATH_KEY;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.PATH_REGEX;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.RESP_KEY;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.TRUE_FALSE_REGEX;

/**
 * Handles the client-side of CLIDE communication
 * 
 * @author Jon Whitmore Last updated by: Stanley Girshik
 * @version $
 */
@Scope("prototype")
@Component
public class ClientProtocolHandler extends IdleStateAwareChannelHandler implements DownloadCompleteListener {

	ClideUtils clideUtils;

	final private List<String> paths = Collections.synchronizedList(new ArrayList<String>());

	private final Logger logger = Logger.getLogger(ClientProtocolHandler.class.getName());

	public static String initiateCommunication(ClientContext clientContext) {
		ClideContextHolder.setClientContext(clientContext);
		String raw = ClideProtocol.get(ClideProtocol.CLIENT.hello);
		return raw.replace(TRUE_FALSE_REGEX, String.valueOf(clientContext.getForceValidate()));
	}

	private void savePaths(final String clientPathMessage) {
		final Pattern p = Pattern.compile(PATH_REGEX);
		final Matcher m = p.matcher(clientPathMessage);
		while (m.find()) {
			String arg = m.group().substring(PATH_KEY.length() + 1);
			paths.add(arg);

		}
	}

	private boolean checkClientFreeSpace(final String clientPathMessage) {
		final Pattern p = Pattern.compile(PATH_REGEX);
		final Matcher m = p.matcher(clientPathMessage);
		long total = 0;
		int count = 0;
		while (m.find()) {
			String tmp = m.group();
			total += Long.valueOf(tmp.substring(tmp.indexOf(FILE_SIZE_KEY) + FILE_SIZE_KEY.length() + 1));
			++count;
		}
		String size = FileUtil.getFormattedFileSize(total);
		logger.info("Server has " + count + " file(s) to send totaling " + size);
		return clideUtils.checkClientFreeSpace(total, ClideContextHolder.getClientContext());
	}

	/**
	 * This method will close the current channel (ending the client session)
	 * when called by the IdleStateHandler in our pipeline. The IdleStateHandler
	 * calls this method when there has been zero I/O in X number of seconds
	 * (defined in the client pipeline factory).
	 * 
	 * @param ctx
	 *            the Channel Handler Context
	 * @param e
	 *            the Ideal State Event
	 */
	public void channelIdle(final ChannelHandlerContext ctx, final IdleStateEvent e) {
		if (e.getState() == IdleState.ALL_IDLE) {
			long millis = System.currentTimeMillis() - e.getLastActivityTimeMillis();
			logger.error("No I/O for " + millis / 1000 + " seconds.  Assuming timeout and shutting down.");
			Channel chan = ctx.getChannel();
			if (chan != null) {
				chan.close();
			}
		}

	}

	public String respondTo(final String response) {
		String command;
		final ClideProtocol.SERVER s = ClideProtocol.findResponse(response);

		switch (s) {
		case iHaveFilesToSend:
			String countEquals = COUNT_KEY + "=";
			int startNumber = response.indexOf(countEquals) + countEquals.length();
			int endNumber = response.indexOf(";");
			int numberOfFiles = Integer.parseInt(response.substring(startNumber, endNumber));
			if (numberOfFiles == 0) {
				command = ClideProtocol.get(terminateConnection);
			} else {
				command = ClideProtocol.get(whatAreTheFiles);
			}
			break;

		case hereAreTheFilePathsWithSize:
			if (checkClientFreeSpace(response)) {
				savePaths(response);
				if (paths.size() > 0) {
					command = requestNextFile();
				} else {
					command = null;
				}
				break;
			} else {
				command = ClideProtocol.get(terminateConnection);
				break;
			}

		case failure:
			command = ClideProtocol.get(terminateConnection);
			break;

		case terminatingConnection:
			command = null;
			break;

		case unableToRespond:
			command = ClideProtocol.get(terminateConnection);
			break;

		default:
			command = ClideProtocol.get(unableToFindNextCommand);

		}
		return command;

	}

	@Override
	public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) {
		final Object msg = e.getMessage();
		if (msg instanceof DefaultHttpResponse || msg instanceof String) {
			String message;
			if (msg instanceof DefaultHttpResponse) {
				HttpResponse response = (HttpResponse) msg;
				ChannelBuffer buf = response.getContent();
				message = buf.toString(Charset.forName(UTF8));
			} else {
				message = (String) msg;
			}
			if (!message.startsWith(RESP_KEY)) {
				// small file, not a command
				ctx.sendUpstream(e);
				return;
			}
			logger.info(message);

			String command = respondTo(message);

			// Send the HTTP request.
			if (command != null) {
				logger.info(command);
				sendRequest(ctx, command);

			} else {
				// we are done. Either we didn't understand the server or server
				// responded closing the connection.
				// clide client will notice the connection close and terminate.

			}

		} else {
			ctx.sendUpstream(e);
		}

	}

	private String requestNextFile() {
		ClientContext clientContext = ClideContextHolder.getClientContext();
		String command = ClideProtocol.get(giveMeThisFile);
		String path = paths.get(0);
		command = command.replace(PATH_REGEX, PATH_KEY + "=" + path);
		String encrypt = Boolean.toString(clientContext.shouldEncryptFile(path));
		command = command.replace(ENC_REGEX, ENC_KEY + "=" + encrypt);
		path = path.replaceAll(FILE_SIZE_KEY + "=" + INTEGER_REGEX, "").trim();
		ClideContextHolder.getClientContext().setRequestedPath(path);
		return command;
	}

	private String getConfirmSuccessfulTransfer(final String serverPath, final String clientPath) {
		String command = ClideProtocol.get(receivedThisFileWithoutError);
		command = command.replace(PATH_REGEX, PATH_KEY + "=" + serverPath + " " + FILE_SIZE_KEY + "="
				+ new File(clientPath).length());
		return command;

	}

	public ChannelFuture sendRequest(final ChannelHandlerContext ctx, final String command) {
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, ClideContextHolder
				.getClientContext().getUri().toASCIIString());
		request.setHeader(HttpHeaders.Names.HOST, ClideContextHolder.getClientContext().getHost());
		request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(command.getBytes().length));
		request.setContent(ChannelBuffers.copiedBuffer(command, Charset.forName(UTF8)));
		return ctx.getChannel().write(request);

	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
		logger.error("Unexpected error", e.getCause());
		Channel ch = e.getChannel();
		ch.close();
	}

	/**
	 * Tell the server the file was transferred correctly, request another file
	 * until no more and finally move the files if indicated by properties or
	 * terminate gracefully.
	 * 
	 * @param ctx
	 *            The context in which the current channel exists
	 * @param serverPath
	 *            The path by which the file is known on the server
	 * @param clientPath
	 *            The path by which the file is known on the client
	 */
	public void downloadCompleted(final ChannelHandlerContext ctx, final String serverPath, final String clientPath)
			throws IOException {

		ClientContext clientContext = ClideContextHolder.getClientContext();
		// Notify the server
		String successCommand = getConfirmSuccessfulTransfer(serverPath, clientPath);
		sendRequest(ctx, successCommand);

		// note file size
		final long fileSize = new File(clientPath).length();

		// Request the next file, or if we are done go to he final step
		paths.remove(serverPath + " " + FILE_SIZE_KEY + "=" + fileSize);
		String nextFileCommand;
		if (paths.size() > 0) {
			nextFileCommand = requestNextFile();
		} else {

			// Move all files if necessary
			clideUtils.moveAllFilesIfNecessary(clientContext.getDownloadDir(), clientContext.getProcessedDir());
			// Tell the server to do the same
			sendRequest(ctx, ClideProtocol.get(downloadOfAllFilesCompleted));

			logger.info("No more files to request.  Terminating connection.");
			clientContext.setRequestedPath(null);
			ClideContextHolder.setClientContext(clientContext);
			nextFileCommand = ClideProtocol.get(terminateConnection);
		}
		sendRequest(ctx, nextFileCommand);
	}

	public void downloadFailed(final ChannelHandlerContext ctx, final String serverPath, final String clientPath) {
		logger.error("Failed to download: " + serverPath);
		sendRequest(ctx, ClideProtocol.get(terminateConnection));
	}

	@Autowired
	public void setClideUtils(ClideUtils clideUtils) {
		this.clideUtils = clideUtils;
	}

}// End of Class
