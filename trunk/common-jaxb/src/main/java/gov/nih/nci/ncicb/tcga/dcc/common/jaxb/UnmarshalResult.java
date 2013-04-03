/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.jaxb;

import javax.xml.bind.ValidationEvent;
import java.util.List;

/**
 * Bean class used to hold the results of {@link JAXBUtil} unmarshal invocations.
 * 
 * <p>
 * The resulting unmarshalled JAXB object can be retrieved by calling {@link UnmarshalResult#getJaxbObject()}.
 * 
 * <p>
 * Clients that use the various unmarshal methods of {@link JAXBUtil} with validation turned on,
 * can check the result of the validation by calling {@link #isValid()}. If the return value
 * is false, then the specific {@link ValidationEvent}s can be retrieved by calling 
 * {@link #getValidationEvents()}.
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 * @version 
 */
public class UnmarshalResult {
	
	private Object jaxbObject;
	private List<ValidationEvent> validationEvents;
	
	public UnmarshalResult(final Object jaxbObject, final List<ValidationEvent> validationEvents) {
		this.jaxbObject = jaxbObject;
		this.validationEvents = validationEvents;
	}
	
	/**
	 * Retrieves the unmarshalled JAXB object. 
	 * 
	 * @return the unmarshalled JAXB object
	 */
	public Object getJaxbObject() {
		return jaxbObject;
	}

	/**
	 * Retrieves the list of {@link ValidationEvent}s.
	 * 
	 * @return the list of {@link ValidationEvent}s
	 */
	public List<ValidationEvent> getValidationEvents() {
		return validationEvents;
	}

	/**
	 * Consults the list of {@link ValidationEvent}s returned by {@link #getValidationEvents} and
	 * determines whether or not validation errors exist.
	 * 
	 * <p>
	 * Only <code>ValidationEvents</code> with a severity of {@link ValidationEvent#ERROR} or 
	 * {@link ValidationEvent#FATAL_ERROR} are considered validation errors.
	 * 
	 * @return true if the any validation errors exist, false otherwise
	 */
	public boolean isValid() {
		boolean valid = true;
		if(validationEvents != null && !validationEvents.isEmpty()) {
			// Loop through severities, if we have any ValidationEvent.ERROR or
			// ValidationEvent.FATAL_ERROR events return false
			for(ValidationEvent validationEvent : validationEvents) {
				int severity = validationEvent.getSeverity();
				if(severity != ValidationEvent.WARNING) {
					valid = false;
				}
			}
		}
		
		return valid;
	}
}
