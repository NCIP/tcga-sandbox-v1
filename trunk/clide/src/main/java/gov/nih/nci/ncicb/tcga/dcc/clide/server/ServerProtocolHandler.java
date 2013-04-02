/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.server;

import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelFutureProgressListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.DefaultFileRegion;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.FileRegion;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants.UTF8;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.CLIENT.terminateConnection;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.FILE_SIZE_KEY;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.INTEGER_REGEX;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.PATHS_REGEX;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.PATH_KEY;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.SERVER.hereAreTheFilePathsWithSize;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.SERVER.iHaveFilesToSend;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.SERVER.sendServerLogFile;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.SERVER.terminatingConnection;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.SERVER.unableToRespond;

/**
 * Respond to client messages with instructions and queries about the data
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */

@Component
public class ServerProtocolHandler extends IdleStateAwareChannelHandler {

    private ClideUtils clideUtils;

    private final Logger logger = Logger.getLogger(ServerProtocolHandler.class.getName());

    protected void copyFilesToWorkingDir() throws IOException {
        File originalDirectory = ServerContext.getDownloadDir();
        File workingDirectory = ServerContext.getWorkingDir();
        clideUtils.copyFilesFromTo(originalDirectory, workingDirectory);
    }

    public String respondTo(final String message) throws IOException {
        String response;
        String raw;
        final ClideProtocol.CLIENT c = ClideProtocol.findCommand(message);

        switch (c) {
            case hello:
                int startNumber = message.indexOf("forceValidate=") + "forceValidate=".length();
                int endNumber = message.indexOf(";");
                boolean validate = Boolean.parseBoolean(message.substring(startNumber, endNumber));
                raw = ClideProtocol.get(iHaveFilesToSend);
                if (validate) {
                    copyFilesToWorkingDir();
                    SoundcheckRun run = new SoundcheckRun();
                    logger.info("Starting Validation");
                    new Thread(run).start();
                    while (!run.isDone()) {
                        // wait for the thread to finish before asserting anything
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            logger.error("InterruptedException waiting for SoundcheckRun to finish.");
                        }
                    }
                    logger.info("Validation complete");
                    run.cleanUp(ServerContext.getWorkingDir(), true);
                    int n = run.wasSuccessful() ? ServerContext.getFilesToSend().size() : 0;
                    response = raw.replace(INTEGER_REGEX, String.valueOf(n));
                } else {
                    response = raw.replace(INTEGER_REGEX, String.valueOf(ServerContext.getFilesToSend().size()));
                }
                break;

            case whatAreTheFiles:
                raw = ClideProtocol.get(hereAreTheFilePathsWithSize);
                final StringBuilder builder = new StringBuilder(raw.replace(PATHS_REGEX, ""));
                for (final String path : ServerContext.getFilesToSend()) {
                    builder.append(" ").append(PATH_KEY).append("=").append(path).append(" ");
                    builder.append(FILE_SIZE_KEY).append("=").append(new File(path).length());
                }
                builder.append(";");
                response = builder.toString();
                break;

            case giveMeThisFile:
                response = null;
                break;

            case receivedThisFileWithoutError:
                response = null;
                break;

            case downloadOfAllFilesCompleted:
                response = null;
                break;

            case getServerLogFile:
                response = ClideProtocol.get(sendServerLogFile);
                break;

            case unableToFindNextCommand:
                response = ClideProtocol.get(terminatingConnection);
                break;

            case terminateConnection:
                response = ClideProtocol.get(terminatingConnection);
                break;

            default:
                response = ClideProtocol.get(unableToRespond);
                break;
        }
        return response;

    }

    @Override
    public void channelOpen(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        ServerContext.addChannel(e.getChannel());
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws IOException {
        final Object msg = e.getMessage();
        if (msg instanceof DefaultHttpRequest) {
            // communication started, client opened socket
            final HttpRequest request = (HttpRequest) e.getMessage();
            String command = request.getContent().toString(Charset.forName(UTF8));
            if (command!=null&&command.length()==0){
                //We're coming from the browser
                final QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
                final Map<String, List<String>> params = queryStringDecoder.getParameters();
                final List<String> key = params.get("cmd");
                if (key!=null) {
                    command = "cmd=" + params.get("cmd").get(0) + ";";
                }
                if (command != null && command.length() == 0) {
                    command = ClideProtocol.get(terminateConnection);
                }
            }
            logger.info("Command: "+command);
            final String resp = respondTo(command);
            if (resp == null) {
                // no response for a file request, just send the file
                ctx.sendUpstream(e);
                return;
            }
            final HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(resp.getBytes().length));
            if (ClideProtocol.get(sendServerLogFile).equals(resp)){
                sendServerLogFile(e,response);
                return;
            }
            logger.info(resp);
            final Channel chan = ctx.getChannel();
            response.setContent(ChannelBuffers.copiedBuffer(resp, Charset.forName(UTF8)));
            final ChannelFuture writeFuture = chan.write(response);
            if (resp.equals(ClideProtocol.get(terminatingConnection))) {
                // shut down connection to client
                writeFuture.addListener(ChannelFutureListener.CLOSE);

            }
        }
    }

    /**
     * This method will close the current channel (ending the client session) when called by the IdleStateHandler in our
     * pipeline.  The IdleStateHandler calls this method when there has been zero I/O in X number of seconds (defined in
     * the server pipeline factory).
     *
     * @param ctx the ChannelHandlerContext
     * @param e   the IdealStateEvent
     */
    public void channelIdle(final ChannelHandlerContext ctx, final IdleStateEvent e) {
        if (e.getState() == IdleState.ALL_IDLE) {
            long millis = System.currentTimeMillis() - e.getLastActivityTimeMillis();
            logger.error("No I/O for " + millis / 1000 + " seconds.  Assuming timeout and closing channel.");
            final Channel chan = ctx.getChannel();
            if (chan != null) {
                chan.close();
            }
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
        logger.error("Unexpected error", e.getCause());
        final Channel ch = e.getChannel();
        ch.close();
    }

    @Autowired
    public void setClideUtils(ClideUtils clideUtils) {
        this.clideUtils = clideUtils;
    }

    /**
     * send Server Log, uses zero-copy and will still work on windows since the log is a smal file with a max of 10MB.
     * @param e
     * @param response
     * @throws IOException
     */
    private void sendServerLogFile(final MessageEvent e,final HttpResponse response) throws IOException {
        final FileAppender appender = (FileAppender) Logger.getRootLogger().getAppender("R");
        if (appender!=null){
        final String path = appender.getFile();
        logger.info("send serverLog: " + path);
        final File file = new File(path);
        final RandomAccessFile raf = new RandomAccessFile(file, "r");
        final long fileLength = raf.length();
        final Channel ch = e.getChannel();
        response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, fileLength);
        if (ch.isWritable()) {
            ch.write(response);
        }
        final ChannelFuture writeFuture;
        final FileRegion region = new DefaultFileRegion(raf.getChannel(), 0, fileLength);
        if (ch.isWritable()) {
            writeFuture = ch.write(region);
            writeFuture.addListener(new ChannelFutureProgressListener() {
                public void operationComplete(ChannelFuture future) {
                    region.releaseExternalResources();
                }
                public void operationProgressed(ChannelFuture future, long amount, long current, long total) {
                    final Formatter formatter = new Formatter();
                    logger.info(formatter.format("%s: %d / %d (+%d)%n", path, current, total, amount));
                }
            });
            writeFuture.addListener(ChannelFutureListener.CLOSE);
        }
    } else {
            logger.error("Root Logger Appender R cannot be found");
            final Channel ch = e.getChannel();
            ch.close();
        }
    }

}//End of Class
