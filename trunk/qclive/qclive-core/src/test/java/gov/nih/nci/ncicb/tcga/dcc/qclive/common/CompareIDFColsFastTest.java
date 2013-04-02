/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.IDF;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.GccIdfValidator;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class CompareIDFColsFastTest extends TestCase {

	private static final String SAMPLE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	public void testIDFToListNames() throws IOException {
		IDF idf = new IDF();
		idf.initIDF(SAMPLE_DIR + "qclive/", "idfTestFile.idf.txt");
		List<String[]> idfColList = idf.getIDF();
		final Collection<String> fileList = new GccIdfValidator().getAllowedIdfHeaders();

		for (final Object aColItem : idfColList) {
			String[] value = (String[]) aColItem;
			String colName = value[0].trim();
			if (colName.length() > 0) {
				assertTrue("Should contain: " + colName + " but didn't",
						fileList.contains(colName));
			}
		}
	}

	public void testListToIDFNames() throws IOException {
		IDF idf = new IDF();
		idf.initIDF(SAMPLE_DIR + "qclive/", "idfTestFile.idf.txt");		
		List<String[]> idfColList = idf.getIDF();
		List<String> idfColNames = new ArrayList<String>();
		for (final Object aIDFColArray : idfColList) {
			String[] theItem = (String[]) aIDFColArray;
			if (theItem[0].trim().length() > 0) {
				idfColNames.add(theItem[0]);
			}
		}
		final Collection<String> fileNewList = new GccIdfValidator().getAllowedIdfHeaders();
		assertTrue("File list size should be 44 it is: " + fileNewList.size(),
				fileNewList.size() == 44);
		fileNewList.removeAll(idfColNames);
		if (fileNewList.size() > 0) {
			for (final Object aListItem : fileNewList) {
				System.out.println("Missing: " + aListItem);
			}
		}
	}
}
