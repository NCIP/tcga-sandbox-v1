/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.clide.clientmanager;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClideClientImpl;
import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClientContext;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideChunkedProtocolMessage;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtils;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtilsImpl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Handles ClientManager commands. This class is invoked by Netty framework
 * to parse and process commands submitted via http to Client Manager.
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public class ClientManagerProtocolHandler extends SimpleChannelUpstreamHandler {
    private static final Logger logger = Logger
            .getLogger(ClientManagerProtocolHandler.class.getName());

    
    public List<String> getAvailableCenters(){
    	
    	Properties properties = ClideUtilsImpl.getClideProperties(System.getProperty("clide.configuration"));
    	String centersList = properties.getProperty("availablecenters");
    	
    	if (StringUtils.isEmpty(centersList)){
    		throw new IllegalArgumentException("Unable to retrieve avalable centers");
    	}    	
    	String [] centersArray = centersList.split(",");
    	if (centersArray.length < 1){
    		throw new IllegalArgumentException("Inccorect format for availble centers");
    	}    	    	
    	return Arrays.asList(centersArray);
    }
    
    public void startClient(DccCenter center) throws IOException,
            URISyntaxException {

        Properties properties = getCenterProperties(center);

        // initialize application context
        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext(
                ClideConstants.APP_CONTEXT);
        ctx.registerShutdownHook();
        // read properties
        ClideUtils clideUtils = (ClideUtils) ctx.getBean("clideUtilsImpl");
        ClientContext clientContext = new ClientContext();

        clientContext.setCenter(center);
        String uriString = properties.getProperty("serverURI");
        String destinationPath = properties.getProperty("clientDownloadDirectory");
        String processedPath = properties.getProperty("clientProcessedDirectory");
        String privateKey = properties.getProperty("privateKey");
        String encrypt = properties.getProperty("clideEnableEncryption");
        String internalLogging = properties.getProperty("clientInternalLogging");
        String timeout = properties.getProperty("timeoutInSeconds");
        String validate = properties.getProperty("forceValidate");
        String diskSpaceThreshold = properties.getProperty("diskSpaceThreshold");
        String noSpaceEmailTo = properties.getProperty("noSpaceEmailTo");
        String noSpaceEmailBcc = properties.getProperty("noSpaceEmailBcc");
        String noSpaceEmailSubject = properties.getProperty("noSpaceEmailSubject");
        String noSpaceEmailContent = properties.getProperty("noSpaceEmailContent");

        URI serverUri = new URI(uriString);

        final String scheme = serverUri.getScheme() == null ? "http"
                : serverUri.getScheme();
        final String host = serverUri.getHost() == null ? "localhost"
                : serverUri.getHost();
        final int port = serverUri.getPort() == -1 ? 80 : serverUri.getPort();

        if (!scheme.equals("http")) {
            throw new IllegalArgumentException("Only http is supported.");
        }

        // setContext
        clientContext.setDestinationPath(destinationPath);
        clientContext.setProcessedPath(processedPath);
        clientContext.setProcessedDir(clideUtils.validateClientProcessedDirectory(processedPath, destinationPath));
        clientContext.setDownloadDir(clideUtils.validateClientDownloadedDirectory(destinationPath));
        clientContext.setEncryptionEnabled(clideUtils.validateEncryption(encrypt));
        clientContext.setForceValidate(clideUtils.validateForceValidate(validate));
        clientContext.setUri(serverUri);
        clientContext.setHost(host);
        clientContext.setPort(port);
        clientContext.setPrivateKey(clideUtils.validatePrivateKey(privateKey));
        clientContext.setInternalLogging(clideUtils.validateClientInternalLogging(internalLogging));
        clientContext.setTimeout(clideUtils.validateTimeout(timeout));
        clientContext.setDiskSpaceThreshold(diskSpaceThreshold);
        clientContext.setNoSpaceEmailBcc(noSpaceEmailBcc);
        clientContext.setNoSpaceEmailContent(noSpaceEmailContent);
        clientContext.setNoSpaceEmailSubject(noSpaceEmailSubject);
        clientContext.setNoSpaceEmailTo(noSpaceEmailTo);

        // perform directory operations
        clideUtils.cleanUpDirectory(destinationPath);
        clideUtils.setUpDirectories(clientContext);
        clideUtils.checkDiskSpace(clientContext);

        ClideClientImpl clideClientImpl = (ClideClientImpl) ctx.getBean("clideClientImpl");
        clideClientImpl.setClientContext(clientContext);

        ClideClientManager.startNewClientThread(clideClientImpl, center);
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx,
                                final MessageEvent event) throws IOException, URISyntaxException {

        HttpRequest request = (HttpRequest) event.getMessage();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(
                request.getUri());

        // process parameters
        Map<String, List<String>> urlParams = queryStringDecoder
                .getParameters();
        if (!urlParams.isEmpty()) {
            for (Entry<String, List<String>> entrySet : urlParams.entrySet()) {
                final String key = entrySet.getKey();
                final ClientManagerCommand command = ClientManagerCommand.fromString(key);
                if (command != null) {
                    List<String> parameterValues = entrySet.getValue();
                    for (String parameterValue : parameterValues) {
                        DccCenter center = DccCenter.fromString(parameterValue);
                        if (center != null) {
                            switch (command) {
                                case START:
                                    startClient(center);
                                    logger.debug("starting " + parameterValue + " client ");
                                    break;
                                case STOP:
                                    try {
                                        ClideClientManager.shutDownClientThread(center);
                                        logger.debug("stopping " + parameterValue + " client ");
                                    } catch (UnsupportedOperationException e) {
                                        /* this exception means that a client was not found in the
                                               * client manager registry. Probably means a user
                                               * submitted a shut down request for a terminated client
                                               */
                                        logger.error("Unable to shut down Clide Client: " + e);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }
        }
        final String responseString = constructHtmlMessage(request);
        ClideChunkedProtocolMessage message = new ClideChunkedProtocolMessage(
                event.getChannel(), responseString);

        // create and response
        writeResponse(event, responseString);
        ctx.sendUpstream(message);
    }

    /**
     * Composes http response and closes channel
     *
     * @param event          passed from Netty
     * @param responseBuffer that contains reponse details.
     */
    private void writeResponse(MessageEvent event, String responseBuffer) {

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.setContent(ChannelBuffers.copiedBuffer(responseBuffer,
                CharsetUtil.UTF_8));
        response.setHeader(CONTENT_TYPE, "text/html;charset=UTF-8");


        // Write the response.
        final ChannelFuture future = event.getChannel().write(response);

        // close channel
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture future)
                    throws Exception {
                future.getChannel().close();
            }
        });
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
                                final ExceptionEvent e) {
        logger.error("Unexpected error", e.getCause());
        Channel ch = e.getChannel();
        ch.close();
    }


    private Properties getCenterProperties(DccCenter center) {
    	Properties returnProperties;    
        returnProperties = ClideUtilsImpl.getClideProperties(center.toString()+".properties");        
        return returnProperties;
    }

    private String constructHtmlMessage(HttpRequest request) {

        StringBuilder returnHtmlMessage = new StringBuilder();
        // constructing html return message
        returnHtmlMessage.append(constructHtmlResponse());

        ChannelBuffer content = request.getContent();
        if (content.readable()) {
            returnHtmlMessage.append("CONTENT: "
                    + content.toString(CharsetUtil.UTF_8) + "\r\n");
        }
        return returnHtmlMessage.toString();
    }

    private String constructHtmlResponse() {
        final StringBuffer responseBuffer = new StringBuffer();

        responseBuffer.append("<html><body>");
        responseBuffer.append("<table border=\"1\">");

        List <String> availableCenters = getAvailableCenters();
        for (String center : availableCenters) {        	
        	DccCenter centerToLookup = DccCenter.fromString(center);
        	
        	if (centerToLookup == null){
        		throw new IllegalArgumentException (" Unrecognized center " + centerToLookup );
        	}
        	
            if (ClideClientManager.getClideClient(centerToLookup) != null) {
                // means it is running

                responseBuffer.append("<tr><td><a href=\"" + "?stop=" + center
                        + "\">Stop " + center + " </a></td></tr> ");
                responseBuffer.append("\r\n");
            } else {
                // means it is not running
                responseBuffer.append("<tr><td><a href=\"" + "?start=" + center
                        + "\">Start " + center + " </a></td></tr> ");
                responseBuffer.append("\r\n");
            }
        }

        responseBuffer.append("</table>");
        responseBuffer.append("</body></html>");

        return responseBuffer.toString();
    }

}
