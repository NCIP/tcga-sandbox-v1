/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api.session;

public interface Session {
    
	String getId();

	void setAttribute(String key, Object value);

	Object getAttribute(String key);

	void removeAttribute(String key);
	
	long getCreationTime();
	
//	void setStatus(Status status);
//	
//	Status getStatus();
	
//	public static enum Status {
//        NOT_CONNECTED,
//        CONNECTED,
//        CLOSED
//    }

}
