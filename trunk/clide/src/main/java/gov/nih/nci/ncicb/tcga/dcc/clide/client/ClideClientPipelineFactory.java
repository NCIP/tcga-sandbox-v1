/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.clide.client;

import gov.nih.nci.ncicb.tcga.dcc.clide.common.ThroughputMonitor;

import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.util.Timer;
/**
 * The interface is extends Netty's ChannelPipelineFactory to add getters for ClideClient specific functionaliy
 * @author girshiks
 *
 */
public interface ClideClientPipelineFactory extends ChannelPipelineFactory {
	
	/**
	 * Returns throughput monitor to the caller
	 * @return throughput monitor
	 */
	public ThroughputMonitor getThroughputMonitor();
	
	/**
	 * Returns timeout timer to the caller
	 * @return timeout timer.
	 */
	public Timer getTimeoutTimer() ;
	
}
