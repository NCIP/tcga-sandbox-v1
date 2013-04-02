/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide;

import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClideClient;
import gov.nih.nci.ncicb.tcga.dcc.clide.server.ClideServer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for checking independance of the client and the server runtimes
 * 
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class ClideServerClientIndependanceFastTest extends ClideAbstractBaseTest {

	@Before
	public void setUp() throws IOException {
		clearFilesUsed();

	}

	@After
	public void tearDown() throws IOException {
		clearFilesUsed();
		if (server != null) {
			server.stop();
		}

		client = null;
		server = null;

	}

	@Test
	public void testServer() throws Exception {
		File downloadDir = new File(CLIDE_SAMPLES_PATH + "downloadDir");
		downloadDir.mkdir();

		try {
			server = (ClideServer) ctx3.getBean("clideServer");
			// create an empty download directory for server to clean
			server.setWorkingDir(downloadDir.getCanonicalPath());
			server.setArchiveDir(CLIDE_SAMPLES_PATH);
			server.setPublicKey(CLIDE_SAMPLES_PATH + "outcomes.public.key");
			server.ClideServerSetUp();
			server.start();
		} finally {
			downloadDir.delete();
		}

	}

	@Test(expected = IllegalArgumentException.class)
	public void testClient() throws Exception {

		server = (ClideServer) ctx3.getBean("clideServer");
		server.ClideServerSetUp();
		server.start();

		client = (ClideClient) ctx3.getBean("clideClient");
		client.start();
	}

	private Object getPrivateProperty(Object obj, String prop)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field field = obj.getClass().getDeclaredField(prop);
		field.setAccessible(true);
		return field.get(obj);
	}

}
