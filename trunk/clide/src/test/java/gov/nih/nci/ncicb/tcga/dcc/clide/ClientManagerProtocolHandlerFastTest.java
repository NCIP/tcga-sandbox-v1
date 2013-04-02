/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.clide;

import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClideClient;
import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClientContext;
import gov.nih.nci.ncicb.tcga.dcc.clide.clientmanager.ClideClientManager;
import gov.nih.nci.ncicb.tcga.dcc.clide.clientmanager.ClientManagerProtocolHandler;
import gov.nih.nci.ncicb.tcga.dcc.clide.clientmanager.DccCenter;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class used to test ClientManagerProtocolHandler
 * @author girshiks
 *
 */
@RunWith(JMock.class)
public class ClientManagerProtocolHandlerFastTest {
	
	ClientManagerProtocolHandler handler = null;
	ChannelHandlerContext mockHandlerContext = null;
	MessageEvent mockEvent = null;
	HttpRequest mockRequest = null;
	Channel mockChannel = null;
	ClideClient mockClient = null;

	private final Mockery context = new JUnit4Mockery();

	@Before
	public void setUp() {
		handler = new ClientManagerProtocolHandler();
		mockHandlerContext = context.mock(ChannelHandlerContext.class);
		mockEvent = context.mock(MessageEvent.class);
		mockRequest = context.mock(HttpRequest.class);
		mockChannel = context.mock(Channel.class);
		mockClient = context.mock(ClideClient.class);
		System.setProperty("clide.configuration", "clide.properties");

	}

	@Test
	public void testStartClient() throws IOException, URISyntaxException {
		handler.startClient(DccCenter.UNC);

	}

	@Test
	public void testMessageReceived() throws IOException, URISyntaxException{
		final BigEndianHeapChannelBuffer channelBuffer = new BigEndianHeapChannelBuffer(
				1);
		channelBuffer.markReaderIndex();
		channelBuffer.markWriterIndex();

		ClideClientManager.addClideClient(DccCenter.UNC, mockClient);
		ClideClientManager.addClideClient(DccCenter.BROAD, mockClient);
		context.checking(new Expectations() {
			{
				allowing(mockEvent).getChannel();
				will(returnValue(mockChannel));
				allowing(mockChannel).write(
						with(any(DefaultHttpResponse.class)));
				allowing(mockEvent).getMessage();
				will(returnValue(mockRequest));
				allowing(mockRequest).getUri();
				will(returnValue("http://localhost?start=unc&stop=broad"));
				allowing(mockRequest).getContent();
				will(returnValue(channelBuffer));
				allowing(mockHandlerContext).sendUpstream(
						with(any(ChannelEvent.class)));
				allowing(mockClient).getClientContext();
				will(returnValue(new ClientContext()));
				allowing(mockClient).shutdown(null, null);

			}
		});
		handler.messageReceived(mockHandlerContext, mockEvent);

	}

	@Test
	public void testMessageReceivedBadCenterNameBadCommand()
			throws IOException, URISyntaxException {
		final BigEndianHeapChannelBuffer channelBuffer = new BigEndianHeapChannelBuffer(
				1);
		channelBuffer.markReaderIndex();
		channelBuffer.markWriterIndex();

		ClideClientManager.addClideClient(DccCenter.UNC, mockClient);
		context.checking(new Expectations() {
			{
				allowing(mockEvent).getChannel();
				will(returnValue(mockChannel));
				allowing(mockChannel).write(
						with(any(DefaultHttpResponse.class)));
				allowing(mockEvent).getMessage();
				will(returnValue(mockRequest));
				allowing(mockRequest).getUri();
				will(returnValue("http://localhost?start=OOO&badCommand=JHU"));
				allowing(mockRequest).getContent();
				will(returnValue(channelBuffer));
				allowing(mockHandlerContext).sendUpstream(
						with(any(ChannelEvent.class)));
				allowing(mockClient).getClientContext();
				will(returnValue(new ClientContext()));
				allowing(mockClient).shutdown(null, null);

			}
		});
		handler.messageReceived(mockHandlerContext, mockEvent);

	}

	@Test
	public void testMessageReceivedBadUrl() throws IOException,
			URISyntaxException {
		final BigEndianHeapChannelBuffer channelBuffer = new BigEndianHeapChannelBuffer(
				1);
		channelBuffer.markReaderIndex();
		channelBuffer.markWriterIndex();

		context.checking(new Expectations() {
			{
				allowing(mockEvent).getChannel();
				will(returnValue(mockChannel));
				allowing(mockChannel).write(
						with(any(DefaultHttpResponse.class)));
				allowing(mockEvent).getMessage();
				will(returnValue(mockRequest));
				allowing(mockRequest).getUri();
				will(returnValue("noparams"));
				allowing(mockRequest).getContent();
				will(returnValue(channelBuffer));
				allowing(mockHandlerContext).sendUpstream(
						with(any(ChannelEvent.class)));

			}
		});
		handler.messageReceived(mockHandlerContext, mockEvent);

	}
	
	@Test
	public void testGetAvailableCenters(){
		List<String>centers = handler.getAvailableCenters();		
		Assert.assertTrue (centers != null && centers.size() > 0);						
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetAvailableCentersBadCase(){
		try{
			System.setProperty("clide.configuration", "clide_bad_centers.properties");
			List<String>centers = handler.getAvailableCenters();		
			
		}finally{
			System.setProperty("clide.configuration","clide.properties");
		}
	}
	
	@After
	public void tearDown() {
		handler = null;
	}

}
