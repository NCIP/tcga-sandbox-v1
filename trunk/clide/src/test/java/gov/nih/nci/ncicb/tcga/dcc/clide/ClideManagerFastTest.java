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
import gov.nih.nci.ncicb.tcga.dcc.clide.clientmanager.DccCenter;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtilsImpl;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertTrue;

/**
 * Test class used to test ClideManager
 * @author girshiks
 *
 */
@RunWith(JMock.class)
public class ClideManagerFastTest extends ClideAbstractBaseTest {

	private final Mockery context = new JUnit4Mockery();
	ChannelPipelineFactory pf = null;
	ClideClientManager cm = null;
	ClideClient mockClient = null;

	@Before
	public void setUp() throws NoSuchAlgorithmException, IOException {
		pf = context.mock(ChannelPipelineFactory.class);
		cm = new ClideClientManager();
		mockClient = context.mock(ClideClient.class);
		cm.setProps(ClideUtilsImpl.getClideProperties(
				Thread.currentThread().getContextClassLoader().getResource("clide.properties").getPath()));
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testClientManagerState() {
		assertTrue(cm.getClientManagerState() == ClideClientManager.ServerState.STOPPED);
		cm.start();
		assertTrue(cm.getClientManagerState() == ClideClientManager.ServerState.STARTED);
		cm.stop();
		assertTrue(cm.getClientManagerState() == ClideClientManager.ServerState.STOPPED);
	}

	@Test(expected = IllegalStateException.class)
	public void testClientManagerStopStoppedServer() {
		assertTrue(cm.getClientManagerState() == ClideClientManager.ServerState.STOPPED);
		cm.stop();
	}

	@Test(expected = IllegalStateException.class)
	public void testClientManagerStartStartedServer() {
		assertTrue(cm.getClientManagerState() == ClideClientManager.ServerState.STOPPED);
		cm.stop();
	}

	@Test
	public void testStartNewClientThread() {
		context.checking(new Expectations() {
			{
				allowing(mockClient).setClientThreadName("UNC");
                allowing(mockClient).run();
            }
		});
		cm.startNewClientThread(mockClient, DccCenter.UNC);
		assertTrue(cm.getClideClient(DccCenter.UNC) != null);
	}

	@Test
	public void testShutDownClientThread()  {

		final ClientContext ctx = new ClientContext();
		context.checking(new Expectations() {
			{
				allowing(mockClient).getClientContext();
				will(returnValue(ctx));
				allowing(mockClient).shutdown(null, null);
				allowing(mockClient).run();
				allowing(mockClient).setClientThreadName("UNC");
			}
		});
		cm.startNewClientThread(mockClient, DccCenter.UNC);
		ClideClient cl = cm.getClideClient(DccCenter.UNC);
		assertTrue(cl != null);
		cm.shutDownClientThread(DccCenter.UNC);

	}

	@Test (expected=UnsupportedOperationException.class)
	public void testShutDownNonExistingThread()  {

		final ClientContext ctx = new ClientContext();
		context.checking(new Expectations() {
			{
				allowing(mockClient).getClientContext();
				will(returnValue(ctx));
				allowing(mockClient).shutdown(null, null);
				allowing(mockClient).run();
				allowing(mockClient).setClientThreadName("UNC");	
			}
		});
		cm.startNewClientThread(mockClient, DccCenter.UNC);
		cm.shutDownClientThread(DccCenter.BROAD);
		// should print a record in the logs that a shutdown of an non existant
		// client is requested
	}

}
