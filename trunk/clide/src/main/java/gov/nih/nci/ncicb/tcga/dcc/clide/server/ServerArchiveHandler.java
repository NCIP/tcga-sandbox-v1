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
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ThroughputAware;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.secure.ClideCrypt;
import gov.nih.nci.ncicb.tcga.dcc.common.util.md5.MD5ChecksumCreator;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureProgressListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.DefaultFileRegion;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.FileRegion;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants.ENC_EXT;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants.UTF8;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.ENC_KEY;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.FILE_SIZE_KEY;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.PATH_KEY;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.SERVER.failure;

/**
 * @author The Netty Project (netty-dev@lists.jboss.org)
 * @author Trustin Lee (tlee@redhat.com)
 */

@Component
public class ServerArchiveHandler extends SimpleChannelUpstreamHandler implements ThroughputAware {

    private final Logger logger = Logger.getLogger(ServerArchiveHandler.class.getName());

    private ChunkedFile currentFile = null;
    private FileRegion region = null;
    private long currentBytesInRegion = 0;

    @Autowired
    private ClideUtils utils;

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        HttpRequest request = (HttpRequest) e.getMessage();
        if (request.getMethod() != HttpMethod.GET) {
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        if (request.isChunked()) {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        String command = request.getContent().toString(Charset.forName(UTF8));

        final ClideProtocol.CLIENT c = ClideProtocol.findCommand(command);
        switch (c) {
            case giveMeThisFile:
                respondToFileRequest(ctx, e, command);
                break;

            case receivedThisFileWithoutError:
                respondToSuccessfulFileRequest(command);
                break;

            case downloadOfAllFilesCompleted:
                respondToAllFilesDownloaded();
                break;

            default:
                logger.error("Unknown command sent upstream to ServerArchiveHandler: " + command);
        }

    }

    private void respondToSuccessfulFileRequest(final String command) {
        String path = command.substring(command.indexOf(PATH_KEY + "=") + PATH_KEY.length() + 1, command.indexOf(";"));
        path = path.replaceAll(FILE_SIZE_KEY + "=[0-9]+", "").trim();
        logger.info("Successful download: " + path);
    }

    private void respondToAllFilesDownloaded() throws IOException {
        logger.info("All Files Successfuly downloaded");
        utils.moveAllFilesIfNecessary(ServerContext.getDownloadDir(), ServerContext.getSentDir());
    }

    private void respondToFileRequest(final ChannelHandlerContext ctx, final MessageEvent e, final String command)
            throws Exception {
        String path = command.substring(command.indexOf(PATH_KEY + "=") + PATH_KEY.length() + 1, command.indexOf(" " + ENC_KEY));
        final String encrypt = command.substring(command.indexOf(ENC_KEY + "=") + ENC_KEY.length() + 1, command.indexOf(";"));
        path = path.replaceAll(FILE_SIZE_KEY + "=[0-9]+", "").trim();
        final boolean shouldEncrypt = Boolean.valueOf(encrypt);

        if (path == null) {
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }

        logger.info("Path without size " + path);
        final File file = new File(path);
        if (file.isHidden() || !file.exists()) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        if (!file.isFile()) {
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }

        final File fileToSend;
        File encryptedFile = null;
        if (shouldEncrypt) {
            logger.info("encrypting");
            final File publicKey = ServerContext.getPublicKey();
            final File workingDir = ServerContext.getWorkingDir();
            encryptedFile = new File(workingDir, file.getName() + ENC_EXT);
            encryptedFile.deleteOnExit();
            final ClideCrypt crypt = new ClideCrypt();
            final long timeSpent = crypt.encrypt(file, encryptedFile, publicKey);
            logger.info("encrypted " + utils.getFormattedThroughput(encryptedFile.length(), timeSpent));
            fileToSend = encryptedFile;
        } else {
            fileToSend = file;
        }

        logger.info("Generating MD5");
        final MD5ChecksumCreator md5Creator = new MD5ChecksumCreator();
        final String md5 = MD5ChecksumCreator.convertStringToHex(md5Creator.generate(fileToSend));
        logger.info("MD5: " + md5);

        final RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(fileToSend, "r");
        } catch (FileNotFoundException fnfe) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        final long fileLength = raf.length();
        ServerContext.getFactory().getMonitor().setExpectedBytes(fileLength);
        final HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.setHeader( HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(fileLength));
        response.setHeader(HttpHeaders.Names.CONTENT_MD5, md5);

        final Channel ch = e.getChannel();
        if (ch.isWritable()) {
            ch.write(response);
        }
        final ChannelFuture writeFuture;
        final File finalEncryptedFileForInnerClass = encryptedFile;
        if (ch.isWritable()) {
            if (ServerContext.isServerOnWindows()) {
                currentFile = new ChunkedFile(raf, 0, fileLength, 8192);
                writeFuture = ch.write(currentFile);
            } else {
                region = new DefaultFileRegion(raf.getChannel(), 0, fileLength);
                writeFuture = ch.write(region);
            }
            writeFuture.addListener(new ChannelFutureProgressListener() {
                public void operationProgressed(ChannelFuture future, long amount, long current, long total) {
                    currentBytesInRegion = current;
                }

                public void operationComplete(ChannelFuture future) throws Exception {
                    currentFile = null;
                    if (region != null) {
                        region.releaseExternalResources();
                    }
                    if (shouldEncrypt) {
                        // the deleteOnExit above will get all files (the last being more important
                        // because this operation might not run to completion) but let's delete as
                        // we go so users perceive our progress
                        boolean successfulDelete = finalEncryptedFileForInnerClass.delete();
                        if (!successfulDelete) {
                            logger.error("Unable to delete " + finalEncryptedFileForInnerClass.getCanonicalPath());
                        }
                    }
                }
            });
        }
    }

    public long getTransferredBytes() {
        if (ServerContext.isServerOnWindows()) {
            if (currentFile == null) {
                return 0L;
            }
            return currentFile.getCurrentOffset();
        } else {
            return currentBytesInRegion;
        }
    }


    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e)
            throws Exception {
        Channel ch = e.getChannel();
        Throwable cause = e.getCause();
        logger.error("Unexpected error", cause);
        if (cause instanceof TooLongFrameException) {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        if (ch.isConnected()) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void sendError(final ChannelHandlerContext ctx, final HttpResponseStatus status) {
        HttpResponse response = new DefaultHttpResponse(
                HttpVersion.HTTP_1_1, status);
        response.setHeader(
                HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        String raw = ClideProtocol.get(failure);
        String resp = raw.replace(ClideProtocol.INTEGER_REGEX, String.valueOf(status.getCode()));
        response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(resp.getBytes().length));
        response.setContent(ChannelBuffers.copiedBuffer(resp, Charset.forName(UTF8)));
        logger.error(status);
        ctx.getChannel().write(response);
    }

}
