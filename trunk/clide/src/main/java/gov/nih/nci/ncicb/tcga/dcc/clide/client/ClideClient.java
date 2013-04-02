package gov.nih.nci.ncicb.tcga.dcc.clide.client;

import org.jboss.netty.bootstrap.ClientBootstrap;

public interface ClideClient extends Runnable {

	/**
	 * Connect to the server and start chatting.
	 */
	public void start();

	/**
	 * Stop the client in a thread safe way
	 * 
	 * @param bootstrap
	 *            The bootstrap this client started with
	 * @param factory
	 *            the factory that created this client's pipeline was created
	 *            with
	 */
	public void shutdown(final ClientBootstrap bootstrap, final ClideClientPipelineFactory factory);

	/**
	 * Start Clide Client with a command
	 * 
	 * @param command
	 *            command that is used with client startup
	 */
	public void startWithCommand(final String command);

	public ClientContext getClientContext();

	public void setClientContext(ClientContext context);

	public void setClientThreadName(String clientThreadName);

}