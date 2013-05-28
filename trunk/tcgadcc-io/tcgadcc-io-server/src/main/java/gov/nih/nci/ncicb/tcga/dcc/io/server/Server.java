/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server;

import gov.nih.nci.ncicb.tcga.dcc.io.api.session.Session;

import java.net.InetSocketAddress;

import org.springframework.context.SmartLifecycle;

/**
 * Interface that defines the behavior for IO server implementations.
 * <p>
 * Extends the Spring {@link SmartLifecycle} interface.
 * 
 * @author nichollsmc
 */
public interface Server extends SmartLifecycle {
	
	/**
	 * Starts the server using the provided {@link InetSocketAddress}.
	 * 
	 * @param inetSocketAddress the {@link InetSocketAddress} used to start the server
	 */
	void start(InetSocketAddress inetSocketAddress);
	
    /**
     * Sets the {@link InetSocketAddress} used to bind the server to a specific IP/hostname and
     * port.
     * 
     * @param inetSocketAddress the {@link InetSocketAddress} to set
     */
	void setInetSocketAddress(InetSocketAddress inetSocketAddress);
	
	/**
	 * Retrieves the {@link InetSocketAddress} used to start the server.
	 * 
	 * @return the {@link InetSocketAddress} used to start the server
	 */
	InetSocketAddress getInetSocketAddress();
	
	/**
     * Sets the {@link Session} used by the server.
     * 
     * @param session the {@link Session} used by the server.
     */
	void setSession(Session session);
	
	/**
     * Retrieves the {@link Session} that will be used to configure the server session.
     * 
     * @return the {@link Session} that will be used to configure the server session
     */
	Session getSession();
	
    /**
     * List of transport protocols.
     */
    public static enum TransportProtocol {
        TCP, UDP;
    }
	
}
