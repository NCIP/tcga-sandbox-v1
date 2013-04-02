/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.clide.clientmanager;

import org.apache.commons.lang.StringUtils;
/**
 * Enumeration is used to list client manager operations.
 * @author girshiks
 *
 */
public enum ClientManagerCommand{
		
	START("START"),
	STOP("STOP"),
	PAUSE("PAUSE"),
	RESUME("RESUME");
	
	private String command;

	ClientManagerCommand(String newCommand) {
		this.command = newCommand;
	}
	
	public static ClientManagerCommand fromString(String commandToConvert) {
		if (StringUtils.isNotEmpty(commandToConvert)) {
			for (ClientManagerCommand cmCommand : ClientManagerCommand.values()) {
				
				if (cmCommand.command.equalsIgnoreCase(commandToConvert)) {
					return cmCommand;
				} 
			}
		}
		return null;

	}



}