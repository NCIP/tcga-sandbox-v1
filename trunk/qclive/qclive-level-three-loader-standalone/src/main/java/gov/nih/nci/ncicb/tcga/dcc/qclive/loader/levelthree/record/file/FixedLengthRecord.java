/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.record.file;

import java.util.Collections;
import java.util.List;

/**
 * Bean that represents a fixed length record from a flat file.
 * 
 * @author nichollsmc
 */
public class FixedLengthRecord {

	private Integer               recordNumber;
	private List<String>          recordValues;

	/**
	 * @return the recordNumber
	 */
	public Integer getRecordNumber() {
		return new Integer(recordNumber);
	}

	/**
	 * @param recordNumber
	 *            the recordNumber to set
	 */
	public void setRecordNumber(Integer recordNumber) {
		this.recordNumber = recordNumber;
	}

	/**
	 * @return the recordValues
	 */
	public List<String> getRecordValues() {
		return Collections.unmodifiableList(recordValues);
	}

	/**
	 * @param recordValues
	 *            the recordValues to set
	 */
	public void setRecordValues(List<String> recordValues) {
		this.recordValues = recordValues;
	}

}
