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
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtilsImpl;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ThroughputAware;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.event.DownloadCompleteListener;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.event.DownloadStartedListener;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.event.Downloader;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.secure.ClideCrypt;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.util.md5.MD5ChecksumCreator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants.ENC_EXT;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants.UTF8;

/**
 * Assume that messages wil be file chunks we need to save. When the
 * ClientResponseHandler notes that chunked content is coming next, it sets a
 * flag in the ClientContext, saves the file name and eats the message. The file
 * then comes as chunks (that the ClientResponseHandler and the
 * ClientProtocolHandler ignore) which are assembled into a file and computed
 * into an MD5 digest to determine success for failure.
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */

@Component
public class ClientArchiveHandler extends SimpleChannelUpstreamHandler
        implements Downloader, ThroughputAware {

    private final ArrayList<DownloadCompleteListener> downloadCompleteListeners = new ArrayList<DownloadCompleteListener>();

    private final ArrayList<DownloadStartedListener> downloadStartedListeners = new ArrayList<DownloadStartedListener>();

    private FileChannel wChannel = null;

    private MessageDigest md5Digest = null;

    private ClideCrypt crypt = new ClideCrypt();

    private final AtomicLong transferredBytes = new AtomicLong();

    private final Logger logger = Logger.getLogger(ClientArchiveHandler.class
            .getName());

    public long getTransferredBytes() {
        return transferredBytes.get();
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx,
                                final MessageEvent e) throws Exception {
        final ClientContext clientContext = ClideContextHolder.getClientContext();
        if (clientContext.isReadingChunks()) {
            // part of a big file spanning multiple chunks
            HttpChunk chunk = (HttpChunk) e.getMessage();
            if (chunk.isLast()) {
                // last chunk doesn't have content. close the file
                clientContext.setReadingChunks(false);
                wChannel.close();
                wChannel = null;
                String md5 = MD5ChecksumCreator.convertStringToHex(md5Digest
                        .digest());
                logger.log(Level.INFO, "MD5: " + md5);

                String requestedPath = clientContext.getRequestedPath();
                String localPath = clientContext.getDownloadDir()
                        + File.separator + getFileName(requestedPath);

                transferredBytes.set(0L);
                String expectedMD5 = clientContext.getExpectedMD5();
                if (md5.equals(expectedMD5)) {
                    if (clientContext.shouldEncryptFile(requestedPath)) {
                        ClideUtilsImpl util = new ClideUtilsImpl();
                        logger.info("decrypting");
                        File encryptedFile = new File(localPath + ENC_EXT);
                        encryptedFile.deleteOnExit();
                        long timeSpent = crypt.decrypt(encryptedFile, new File(
                                localPath), clientContext.getPrivateKey());
                        logger.info("decrypted "
                                + util.getFormattedThroughput(
                                encryptedFile.length(), timeSpent));
                        encryptedFile.delete();
                    }
                    fireDownloadComplete(ctx, requestedPath, localPath);
                } else {
                    fireDownloadFailed(ctx, requestedPath, localPath);
                }

            } else {
                // continue to append chunks
                ByteBuffer bb = chunk.getContent().toByteBuffer();
                transferredBytes.addAndGet(chunk.getContent().readableBytes());
                if (wChannel == null) {
                    // this is the first chunk, all others will be appended
                    String requestedPath = clientContext.getRequestedPath();
                    String localPath = clientContext.getDownloadDir()
                            + File.separator + getFileName(requestedPath);
                    File file;
                    if (clientContext.shouldEncryptFile(requestedPath)) {
                        file = new File(localPath + ENC_EXT);
                    } else {
                        file = new File(localPath);
                    }
                    wChannel = new FileOutputStream(file, false).getChannel();
                    md5Digest = MessageDigest.getInstance("MD5");
                    md5Digest.reset();

                    fireDownloadStarted(ctx, requestedPath, localPath);

                }
                wChannel.write(bb);
                // we have to rewind an NIO buffer to read it again. not the
                // case with the netty equivalent
                bb.rewind();
                md5Digest.update(bb);
            }
        } else {
            // if we aren't reading chunks and the protocol handler didn't
            // consume this, it is a small file
            saveSmallFile(ctx, e);
        }
    }

    private void saveLogFile(final MessageEvent e) throws Exception {
        ClientContext clientContext = ClideContextHolder.getClientContext();
        final FileAppender appender = (FileAppender) Logger.getRootLogger()
                .getAppender("R");
        final String requestedPath = appender.getFile();
        final String localPath = clientContext.getDownloadDir()
                + File.separator + requestedPath;
        if (e.getMessage() instanceof String) {
            FileUtil.writeContentToFile((String) e.getMessage(), new File(
                    localPath));
        } else if (e.getMessage() instanceof HttpResponse) {
            final HttpResponse response = (HttpResponse) e.getMessage();
            final ChannelBuffer content = response.getContent();
            FileUtil.writeContentToFile(
                    content.toString(Charset.forName(UTF8)),
                    new File(localPath));
        }
    }

    private void saveSmallFile(final ChannelHandlerContext ctx,
                               final MessageEvent e) throws Exception {
        ClientContext clientContext = ClideContextHolder.getClientContext();
        final String requestedPath = clientContext.getRequestedPath();
        if (requestedPath == null) {
            // We're not treating with files coming from the clide process but
            // with requests to logs
            saveLogFile(e);
            return;
        }
        final String localPath = clientContext.getDownloadDir()
                + File.separator + getFileName(requestedPath);
        fireDownloadStarted(ctx, requestedPath, localPath);
        final HttpResponse response = (HttpResponse) e.getMessage();
        final ChannelBuffer content = response.getContent();
        final ByteBuffer bb = content.toByteBuffer();
        transferredBytes.addAndGet(content.readableBytes());
        if (content.readable()) {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            md5Digest.update(bb);
            String md5 = MD5ChecksumCreator.convertStringToHex(md5Digest.digest());
            logger.log(Level.INFO, md5);
            transferredBytes.set(0L);
            // we have to rewind an NIO buffer to read it again. not the case
            // with the netty equivalent
            bb.rewind();
            if (md5.equals(clientContext.getExpectedMD5())) {
                if (clientContext.shouldEncryptFile(requestedPath)) {
                    File encryptedFile = new File(localPath + ENC_EXT);
                    encryptedFile.deleteOnExit();
                    writeByteBufferToFile(bb, encryptedFile, false);
                    // decrypt
                    File decryptedFile = new File(localPath);
                    crypt.decrypt(encryptedFile, decryptedFile,clientContext.getPrivateKey());
                    encryptedFile.delete();
                } else {
                    writeByteBufferToFile(bb, new File(localPath), false);

                }
                fireDownloadComplete(ctx, requestedPath, localPath);
            } else {
                fireDownloadFailed(ctx, requestedPath, localPath);
            }

        } else {
            // we got garbage??
            logger.log(Level.ERROR,
                    "Client just downloaded unreadable bytes?!?");
            fireDownloadFailed(ctx, requestedPath, localPath);
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
                                final ExceptionEvent e) throws Exception {
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

        String requestedPath = ClideContextHolder.getClientContext()
                .getRequestedPath();
        String localPath = ClideContextHolder.getClientContext()
                .getDownloadDir() + File.separator + getFileName(requestedPath);
        fireDownloadFailed(ctx, requestedPath, localPath);
    }

    private void sendError(final ChannelHandlerContext ctx,
                           final HttpResponseStatus status) {
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                status);
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE,
                "text/plain; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(
                "Failure: " + status.toString() + "\r\n", Charset.forName(UTF8)));

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }

    public void addDownloadCompleteListener(final DownloadCompleteListener dcl) {
        if (!downloadCompleteListeners.contains(dcl)) {
            downloadCompleteListeners.add(dcl);
        }
    }

    public void addDownloadStartedListener(final DownloadStartedListener dsl) {
        if (!downloadStartedListeners.contains(dsl)) {
            downloadStartedListeners.add(dsl);

        }
    }

    public void fireDownloadComplete(final ChannelHandlerContext ctx,
                                     final String serverPath, final String clientPath)
            throws IOException {
        logger.log(Level.INFO, "Completed download: " + serverPath);
        for (final DownloadCompleteListener listener : downloadCompleteListeners) {
            listener.downloadCompleted(ctx, serverPath, clientPath);
        }
    }

    public void fireDownloadFailed(final ChannelHandlerContext ctx,
                                   final String serverPath, final String clientPath) {
        logger.log(Level.ERROR, "Failed to download: " + serverPath);
        for (final DownloadCompleteListener listener : downloadCompleteListeners) {
            listener.downloadFailed(ctx, serverPath, clientPath);
        }
    }

    public void fireDownloadStarted(final ChannelHandlerContext ctx,
                                    final String serverPath, final String clientPath) {
        logger.log(Level.INFO, "Started download: " + serverPath);
        for (final DownloadStartedListener listener : downloadStartedListeners) {
            listener.downloadStarted(ctx, serverPath, clientPath);
        }
    }

    /**
     * Returns the file name at the end of the path. If the client is running on
     * *nix and the server is running on Windows Java will incorrectly return
     * the file name as the complete Windows path
     *
     * @param path a canonical file path
     * @return only the file name regardless of what platform the file came
     *         from.
     */
    public String getFileName(final String path) {
        String name = new File(path).getName();
        int backslashIndex = name.indexOf("\\");
        if (backslashIndex > 0) {
            // we've got a Windows path as the file name
            int startName = name.lastIndexOf("\\") + 1;
            return name.substring(startName);
        } else {
            return name;
        }
    }

    private void writeByteBufferToFile(final ByteBuffer bbuf, final File file,
                                       final boolean append) {
        FileChannel wChannel = null;
        try {
            // Create a writable file channel
            wChannel = new FileOutputStream(file, append).getChannel();

            // Write the ByteBuffer contents; the bytes between the ByteBuffer's
            // position and the limit is written to the file
            wChannel.write(bbuf);

        } catch (IOException iox) {
            logger.log(Level.ERROR, "I/0 Error while writing " + file);
        } finally {
            // Close the file
            if (wChannel != null) {
                try {
                    wChannel.close();
                } catch (IOException iox) {
                    logger.log(Level.ERROR, "Unable to close " + file);
                }
            }
        }
    }

}
