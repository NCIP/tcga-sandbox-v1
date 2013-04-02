/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide;

import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClideClientImpl;
import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClientContext;
import gov.nih.nci.ncicb.tcga.dcc.clide.server.ClideServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for the getServerLogFile command. As I don't know how to test with a real browser,
 * I have adapted our own in house client to take in commands as first.
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ClideGetServerLogFileSlowTest extends ClideAbstractBaseTest {

    @Before
    public void setUp() throws IOException {
        clearFilesUsed();
    }

	@After
	public void tearDown(){
		server.stop();
		server = null;
	}
	
    @Test
    public void testGetServerLogFile() throws URISyntaxException, IOException {
        server = (ClideServer) ctx.getBean("clideServer");
        server.ClideServerSetUp();
        server.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
        client = (ClideClientImpl) ctx.getBean("clideClientImpl");
        ClientContext clientContext = new ClientContext();
        clientContext.setInternalLogging(true);
        clientContext.setUri(new URI("http://localhost:8080"));
        clientContext.setHost("localhost");
        clientContext.setPort(8080);
        clientContext.setReadingChunks(false);  
        clientContext.setDownloadDir(new File(WORKING_DIR));
        client.setClientContext(clientContext);        
        //client. ClideClientSetUp();
        client.startWithCommand("cmd=getServerLogFile;");
        File log = new File(WORKING_DIR + File.separator + "clide.log");
        assertTrue(log.exists());
        assertTrue(log.length()>0);
        log.deleteOnExit();    
        
    }

}
