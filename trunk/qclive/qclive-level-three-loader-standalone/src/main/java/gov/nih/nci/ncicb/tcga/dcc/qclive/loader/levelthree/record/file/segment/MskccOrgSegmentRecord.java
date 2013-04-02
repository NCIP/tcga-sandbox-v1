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
 * {@code mskcc.org}.
 * 
 * @author nichollsmc
 */
public class MskccOrgSegmentRecord extends SegmentRecord {

	public static final String CENTER_NAME = "mskcc.org";
	
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
		cnaValue.setHybridizationRefName(values.get(Header.SAMPLE.index()));
		cnaValue.setChromosome(values.get(Header.CHROM.index()));
		cnaValue.setChrStart(values.get(Header.LOC_START.index()));
		cnaValue.setChrStop(values.get(Header.LOC_END.index()));
		cnaValue.setNumMark(values.get(Header.NUM_MARK.index()));
		cnaValue.setSegMean(values.get(Header.SEG_MEAN.index()));

		return cnaValue;
	}

	public static enum Header {

		SAMPLE("sample", 0),
		CHROM("chrom", 1),
		LOC_START("loc.start", 2),
		LOC_END("loc.end", 3),
		NUM_MARK("num.mark", 4),
		NUM_INFORMATIVE("num.informative", 5),
		SEG_MEAN("seg.mean", 6), PVAL("pval", 7),
		L_LCL("l.lcl", 8),
		L_UCL("l.ucl", 9),
		R_PVAL("r.pval", 10),
		R_LCL("r.lcl", 11),
		R_UCL("r.ucl", 12);

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
