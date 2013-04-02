/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.clide.clientmanager;

import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClideClient;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.CompositeThroughputMonitor;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.HttpEntityLifecycle;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ThroughputAware;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClideClientManager sets up and starts Netty framework for ClientManager.
 * Client Manager is responsible for administering Clide clients. 
 * 
 * ClideClientManager also maintains a registry of clients so other components 
 * may get client active client information.  
 * 
 * @author Stanley Girshik
 * @version $Rev$
 */
public class ClideClientManager implements HttpEntityLifecycle, ThroughputAware {

	private static final Logger logger = Logger.getLogger(ClideClientManager.class);

    public enum ServerState {
		STARTED, STOPPED
	};

	private ServerBootstrap bootstrap;
	private ServerState state = ServerState.STOPPED;
	private Properties props = null;
    CompositeThroughputMonitor compositeMonitor = null;

	private static ExecutorService executorService = null;

    private static Map<DccCenter, ClideClient> clideClientStore = Collections
			.synchronizedMap(new HashMap<DccCenter, ClideClient>());

	@Override
	public void start() {

		// can only start a non-started server
		if (this.state == ServerState.STARTED) {
			throw new IllegalStateException("Unable to start a started server");
		}

		String address = props.getProperty("clientmanageraddress");
		int port = Integer.parseInt(props.getProperty("clientmanagerport"));
		int threadPoolSize = Integer.parseInt(props.getProperty("threadpoolsize"));
		executorService = Executors.newFixedThreadPool(threadPoolSize);

		// Configure the server.
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new ClideManagerPipelineFactory());

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(address, port));
        compositeMonitor = new CompositeThroughputMonitor(this);
        compositeMonitor.setName("Composite Throughput"
                + " - " + compositeMonitor.getId());
        compositeMonitor.start();
		state = ServerState.STARTED;
		logger.info("Server started at " + address + " on port " + port);
	}

	@Override
	public void stop() {
		if (state == ServerState.STARTED) {
			logger.info("Begin client manager shutdown");
            compositeMonitor.stopMonitoring();
			bootstrap.releaseExternalResources();
			logger.info("Client manager shutdown complete");
			state = ServerState.STOPPED;
		} else if (state == ServerState.STOPPED) {
			// can only stop a started server
			throw new IllegalStateException("Unable to stop a stopped server");
		}
	}

	/**
	 * Returns state of the server
	 * 
	 * @return server state
	 */
	public ServerState getClientManagerState() {
		return state;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	/**
	 * Starts a new clide client thread
	 * 
	 * @param client
	 *            a reference to ClideClientImpl to start
	 * @param center
	 *            from where the client will download files
	 */
	public static void startNewClientThread(ClideClient client, DccCenter center) {
        client.setClientThreadName(center.name());
		if (executorService != null) {
			executorService.execute(client);
			clideClientStore.put(center, client);
		}
	}

	/**
	 * Shuts down clide client thread.
	 * 
	 * @param center
	 *            which is used to look up the client.
	 * @throws UnsupportedOperationException
	 */
	public static void shutDownClientThread(DccCenter center) {
		ClideClient client = clideClientStore.get(center);
		if (client != null) {
			client.shutdown(client.getClientContext().getBootsrap(), client.getClientContext().getFactory());
		} else {
			logger.error(" Client for "+ center
					+ " center does not appear to be registerd with Client Manager");
			throw new UnsupportedOperationException(center.toString());
		}
	}

	/**
	 * Retrieves ClideClientImpl from ClientManager registry
	 * 
	 * @param center
	 * @return
	 */
	public static ClideClient getClideClient(DccCenter center) {
		return clideClientStore.get(center);
	}

	/**
	 * Removes Clide Client from client manager registry
	 * 
	 * @param center
	 */
	public static void removeClideClient(DccCenter center) {
		clideClientStore.remove(center);
	}

	/**
	 * Adds a new Clide Client to the registry
	 * 
	 * @param center
	 */
	public static void addClideClient(DccCenter center, ClideClient client) {
		clideClientStore.put(center, client);
	}

    @Override
    public long getTransferredBytes() {
        long bytes = 0L;
        for (ClideClient client : clideClientStore.values()) {
            if (client != null && client.getClientContext()!=null
                    && client.getClientContext().getFactory()!=null
                    && client.getClientContext().getFactory().getThroughputMonitor()!=null) {
                bytes+=client.getClientContext().getFactory().getThroughputMonitor().getTransferredBytes();
            }
        }
        return bytes;
    }

}
