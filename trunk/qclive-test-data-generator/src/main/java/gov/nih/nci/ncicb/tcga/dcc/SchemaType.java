/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc;

/**
 * This class defines the supported database schema types that are supported by the 
 * {@link QCLiveTestDataGenerator#executeSQLScriptFile(String, org.springframework.core.io.Resource)} method.
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 * @version 
 */
public enum SchemaType {
	
	/** Local common schema type */
	LOCAL_COMMON("local_common"),
	
	/** Local disease schema type */
	LOCAL_DISEASE("local_disease");
	
	/** Schema value assigned to an instance of SchemaType */
	private String schemaValue;
	
	/**
	 * Private constructor used by the enum types defined in this class to instantiate a <code>SchemaType</code>
	 * with a specific schema value.
	 * 
	 * @param schemaValue - a string representing the schema value
	 */
	private SchemaType(String schemaValue) {
		this.schemaValue = schemaValue;
	}
	
	/**
	 * Returns a string representing the option value that was assigned to an instance of the <code>SchemaType</code> 
	 * enum type.
	 * 
	 * @return a string representing the schema value
	 */
	public String getSchemaValue() {
		return this.schemaValue;
	}
	
	/**
	 * Returns a comma separated list of supported schema types.
	 */
	public static String getSupportedSchemaTypes() {
		
		StringBuilder schemaTypes = new StringBuilder();
		schemaTypes.append('[');
		for(SchemaType schemaType : SchemaType.values())
			schemaTypes.append(' ' + schemaType.getSchemaValue() + ',');
		schemaTypes.setCharAt(schemaTypes.lastIndexOf(","), ' ');
		schemaTypes.append(']');
		
		return schemaTypes.toString();
	}
}
