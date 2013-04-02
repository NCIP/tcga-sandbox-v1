/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClideClientImpl;
import gov.nih.nci.ncicb.tcga.dcc.clide.clientmanager.ClideClientManager;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtilsImpl;

import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.net.URISyntaxException;
import java.util.Properties;

/**
 * A class outside of all packages to start client manager
 * 
 * @author Stan Girshik Last updated by: $Author$
 * @version $Rev$
 */
public class ClientManager {

	private static final Logger logger = Logger.getLogger(ClientManager.class);

	public static void main(String[] args) throws URISyntaxException {
		Properties properties = ClideUtilsImpl.getClideProperties(System
				.getProperty("clide.configuration"));

		final ClideClientManager cm = new ClideClientManager();
		cm.setProps(properties);
		cm.start();
	}

}
