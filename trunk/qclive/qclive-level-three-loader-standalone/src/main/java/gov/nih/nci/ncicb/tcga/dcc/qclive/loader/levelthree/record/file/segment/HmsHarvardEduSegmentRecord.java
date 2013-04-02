/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.record.file.segment;

import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree.record.CnaValue;

import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of the {@link SegmentRecord} for segment files from center
 * {@code hms.harvard.edu}.
 * 
 * @author nichollsmc
 */
public class HmsHarvardEduSegmentRecord extends SegmentRecord {

	public static final String CENTER_NAME = "hms.harvard.edu";
	
	@Override
	public List<String> getHeaders() {
		List<String> headers = new LinkedList<String>();
		Header[] headerValues = Header.values();
		for (Header header : headerValues) {
			headers.add(header.headerValue());
		}

		return headers;
	}

	@Override
	public CnaValue getCnaValue() {
		List<String> values = getRecordValues();

		CnaValue cnaValue = new CnaValue();
		cnaValue.setChromosome(values.get(Header.CHROMOSOME.index()));
		cnaValue.setChrStart(values.get(Header.START.index()));
		cnaValue.setChrStop(values.get(Header.END.index()));
		cnaValue.setNumMark(values.get(Header.PROBE_NUMBER.index()));
		cnaValue.setSegMean(values.get(Header.SEGMENT_MEAN.index()));

		return cnaValue;
	}

	public static enum Header {

		CHROMOSOME("Chromosome", 0),
		START("Start", 1),
		END("End", 2),
		PROBE_NUMBER("Probe_Number", 3),
		SEGMENT_MEAN("Segment_Mean", 4);

		private String  headerValue;
		private Integer index;

		private Header(String headerValue, Integer index) {
			this.headerValue = headerValue;
			this.index = index;
		}

		/**
		 * @return the headerValue
		 */
		public String headerValue() {
			return headerValue;
		}

		/**
		 * @return the index
		 */
		public Integer index() {
			return index;
		}

	}

}
