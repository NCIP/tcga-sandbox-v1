/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * This class defines the command line option types that are supported by the {@link gov.nih.nci.ncicb.tcga.dcc.QCLiveTestDataGenerator}. 
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 * @version 
 */
public enum CommandLineOptionType {

	/** Help option */
	HELP(new Option("?", "help", false, "Displays usage information.")),
	
	/** Archive name option */
	ARCHIVE_NAME(new Option("a", "archive_name", true, "The name of the archive (without file extension) to generate test data for.")),
	
	/** SQL script file option */
	SQL_SCRIPT_FILE(new Option("f", "sql_script_file", true, "The absolute path to a SQL script file to run. If used in conjunction " +
			"with the -a option, the provided script will run after test data for an archive has been loaded. The -s option denoting the " +
			"databse schema to run script file against is also required when using this option")),
	
	/** Schema option */
	SCHEMA(new Option("s", "schema", true, "The database schema to execute the SQL script file against. Supported values are " +
			"local_common and local_disease"));
	
	/** Option value assigned to an instance of CommandLineOptionType */
	private Option optionValue;
	
	/**
	 * Private constructor used by the enum types defined in this class to instantiate a <code>CommandLineOptionType</code>
	 * with a specific option value.
	 * 
	 * @param optionValue - a string representing the option value
	 */
	private CommandLineOptionType(Option optionValue) {
		this.optionValue = optionValue;
	}
	
	/**
	 * Returns a {@link Option} representing the option value that was assigned to an instance of the <code>CommandLineOptionType</code> 
	 * enum type.
	 * 
	 * @return a {@link Option} representing the option value
	 */
	public Option getOptionValue() {
		return optionValue;
	}
	
	/**
	 * Static utility method that returns an {@link Options} object that contains all the available {@link Option}s defined by the
	 * {@link CommandLineOptionType} enum. This is useful for generating usage and help information when parsing command line
	 * arguments.
	 * 
	 * @return an {@link Options} object that contains all the available {@link Options} defined by the {@link CommandLineOptionType} enum
	 */
	public static Options getOptions() {
		
		final Options commandLineOptions = new Options();
		for(CommandLineOptionType cmdOptType : CommandLineOptionType.values())
			commandLineOptions.addOption(cmdOptType.getOptionValue());
		return commandLineOptions;
	}
}
