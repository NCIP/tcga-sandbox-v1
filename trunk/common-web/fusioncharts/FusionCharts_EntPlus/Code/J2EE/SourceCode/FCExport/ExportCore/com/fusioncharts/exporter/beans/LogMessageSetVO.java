/**
 * 
 */
package com.fusioncharts.exporter.beans;

import java.util.HashSet;
import java.util.Set;

import com.fusioncharts.exporter.error.LOGMESSAGE;

/**
 * Holds sets of errors and warnings. Usually, only one error will be set but
 * there can be multiple number of warnings.
 * 
 * @author InfoSoft Global (P) Ltd.
 * 
 */
public class LogMessageSetVO {
	Set<LOGMESSAGE> errorsSet;
	Set<LOGMESSAGE> warningSet;
	String otherMessages = null;

	public LogMessageSetVO() {
		errorsSet = new HashSet<LOGMESSAGE>();
		warningSet = new HashSet<LOGMESSAGE>();
	}

	public void addError(LOGMESSAGE error) {
		if (errorsSet == null) {
			errorsSet = new HashSet<LOGMESSAGE>();
		}
		errorsSet.add(error);
	}

	/**
	 * Appends other messages to existing messages.
	 * 
	 * @param otherMessages
	 */
	public void addOtherMessages(String otherMessages) {
		if (this.otherMessages == null) {
			this.otherMessages = "";
		}
		this.otherMessages += otherMessages;
	}

	public void addWarning(LOGMESSAGE warning) {
		if (warningSet == null) {
			warningSet = new HashSet<LOGMESSAGE>();
		}
		warningSet.add(warning);
	}

	/**
	 * Returns the value in the field errorsSet
	 * 
	 * @return the errorsSet
	 */
	public Set<LOGMESSAGE> getErrorsSet() {
		return errorsSet;
	}

	/**
	 * Returns the value in the field otherMessages
	 * 
	 * @return the otherMessages
	 */
	public String getOtherMessages() {
		return otherMessages;
	}

	/**
	 * Returns the value in the field warningSet
	 * 
	 * @return the warningSet
	 */
	public Set<LOGMESSAGE> getWarningSet() {
		return warningSet;
	}

	/**
	 * Sets the value for errorsSet
	 * 
	 * @param errorsSet
	 *            the errorsSet to set
	 */
	public void setErrorsSet(Set<LOGMESSAGE> errorsSet) {
		this.errorsSet = errorsSet;
	}

	/**
	 * Sets the value for otherMessages
	 * 
	 * @param otherMessages
	 *            the otherMessages to set
	 */
	public void setOtherMessages(String otherMessages) {
		this.otherMessages = otherMessages;
	}

	/**
	 * Sets the value for warningSet
	 * 
	 * @param warningSet
	 *            the warningSet to set
	 */
	public void setWarningSet(Set<LOGMESSAGE> warningSet) {
		this.warningSet = warningSet;
	}
}
