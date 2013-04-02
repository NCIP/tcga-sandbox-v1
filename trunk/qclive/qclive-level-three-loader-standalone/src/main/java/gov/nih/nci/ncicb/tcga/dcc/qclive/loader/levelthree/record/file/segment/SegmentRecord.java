/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.record.file.segment;

import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.record.CnaValue;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.record.file.FixedLengthRecord;

import java.util.List;

/**
 * Abstract representation of a record (including headers) contained in center
 * segment files.
 * 
 * @author nichollsmc
 */
public abstract class SegmentRecord extends FixedLengthRecord {

	private static final String ASSERTION_FAILURE_PREFIX              = "Record assertion failure: ";
	private static final String GENERAL_ASSERTION_FAILURE_PLACEHOLDER = ASSERTION_FAILURE_PREFIX + " %s";
	private static final String RECORD_LENGTH_MISMATCH_PLACE_HOLDER   = ASSERTION_FAILURE_PREFIX
	                                                                          + " Expected [%d] segment record values, but encountered [%d]";
	private static final String HEADER_MISTMATCH_PLACEHOLDER          = ASSERTION_FAILURE_PREFIX
	                                                                          + " Expected header value [%s] at index [%d], but encountered [%s]";

	/**
	 * Asserts preconditions for {@link SegmentRecord} elements.
	 * <p>
	 * As this method is not required for performing parsing operations, it is
	 * the callers responsibility to invoke this method.
	 * 
	 * @param isHeaderRecord
	 *            indicates whether or not a {@code SegmentRecord} represents a
	 *            column header, default is false
	 * @throws LoaderException
	 *             if any of the assertions performed by this method fails
	 */
	public void assertRecord(boolean isHeaderRecord) throws LoaderException {
		List<String> headers = getHeaders();
		if (headers == null) {
			throw new LoaderException(String.format(GENERAL_ASSERTION_FAILURE_PLACEHOLDER,
			        "the provided header list is null"));
		}

		List<String> recordValues = getRecordValues();
		if (recordValues == null) {
			throw new LoaderException(String.format(GENERAL_ASSERTION_FAILURE_PLACEHOLDER,
			        "the provided list of record values is null"));
		}

		Integer numHeaders = headers.size();
		if (numHeaders != recordValues.size()) {
			throw new LoaderException(String.format(RECORD_LENGTH_MISMATCH_PLACE_HOLDER, numHeaders,
			        recordValues.size()));
		}

		if (isHeaderRecord) {
			for (int index = 0; index < numHeaders; index++) {
				String expectedHeader = headers.get(index);
				String actualHeader = recordValues.get(index);

				if (!expectedHeader.equals(actualHeader)) {
					throw new LoaderException(String.format(HEADER_MISTMATCH_PLACEHOLDER, expectedHeader, index,
					        actualHeader));
				}
			}
		}
	}

	/**
	 * Returns the header values for a {@link SegmentRecord} type.
	 * 
	 * @return the header values
	 */
	public abstract List<String> getHeaders();

	/**
	 * Returns a {@link CnaValue} object created from the list of values
	 * returned by {@link #getRecordValues()}.
	 * 
	 * @return a {@code CnaValue} object
	 */
	public abstract CnaValue getCnaValue();

}
