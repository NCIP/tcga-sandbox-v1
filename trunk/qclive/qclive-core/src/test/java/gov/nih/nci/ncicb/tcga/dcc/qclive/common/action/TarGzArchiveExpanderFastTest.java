/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.io.File;

import org.junit.Test;

/**
 * Test class for ArchiveExpander, using .tar.gz archives.
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class TarGzArchiveExpanderFastTest {

	private static final String SAMPLE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	private String goodArchive = SAMPLE_DIR + "qclive/tarGz/good.tar.gz";

	@Test
	public void test() throws Processor.ProcessorException {
		ArchiveExpander expander = new ArchiveExpander();
		Archive archive = new Archive();
		archive.setArchiveFile(new File(goodArchive));
		expander.execute(archive, new QcContext());
		File expandedDir = new File(SAMPLE_DIR + "qclive/tarGz/good/");
		assertTrue("Archive was not expanded", expandedDir.exists());
	}
}
