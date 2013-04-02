/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA. User: nassaud Date: Apr 30, 2009 Time: 3:31:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class CutColumnsFastTest extends TestCase {

	private static final String SAMPLE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	static final String autoLoaderFolder = SAMPLE_DIR + "autoloader/";

	public void testCut() throws IOException {
		File f = new File(autoLoaderFolder + "test.sdrf.txt");
		int[] selectedColumns = { 12, 17 };
		List<String[]> ret = CutColumns.cut(f, selectedColumns);
		String firstHeader = ret.get(0)[0];
		String secondHeader = ret.get(0)[1];
		assertEquals(firstHeader, "Extract Name");
		assertEquals(secondHeader, "Hybridization Name");
		String first = ret.get(1)[0];
		String second = ret.get(1)[1];
		assertTrue(first.startsWith("TCGA-"));
		assertEquals(first.length(), 28);
		assertTrue(second.startsWith("0"));
		assertEquals(second.length(), 4);
		for (String[] line : ret) {
			for (String s : line) {
				System.out.print(s + "\t");
			}
			System.out.println();
		}
	}

	public void testCut2() throws IOException {
		File f = new File(autoLoaderFolder + "test.sdrf.txt");
		// int[] selectedColumns = {12,17};
		String[] selectedHeaders = { "Extract Name", "Hybridization Name" };
		List<String[]> ret = CutColumns.cut(f, selectedHeaders);
		String firstHeader = ret.get(0)[0];
		String secondHeader = ret.get(0)[1];
		assertEquals(firstHeader, "Extract Name");
		assertEquals(secondHeader, "Hybridization Name");
		String first = ret.get(1)[0];
		String second = ret.get(1)[1];
		assertTrue(first.startsWith("TCGA-"));
		assertEquals(first.length(), 28);
		assertTrue(second.startsWith("0"));
		assertEquals(second.length(), 4);
		for (String[] line : ret) {
			for (String s : line) {
				System.out.print(s + "\t");
			}
			System.out.println();
		}
	}
}
