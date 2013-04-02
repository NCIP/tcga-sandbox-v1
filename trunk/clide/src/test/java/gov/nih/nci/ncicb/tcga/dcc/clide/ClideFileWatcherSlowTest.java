/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide;

import gov.nih.nci.ncicb.tcga.dcc.clide.server.ClideServer;
import gov.nih.nci.ncicb.tcga.dcc.clide.server.ServerContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static junit.framework.Assert.assertFalse;

/**
 * Test class for the clide File watcher on the archive server directory feature
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ClideFileWatcherSlowTest extends ClideAbstractBaseTest {

    CreateRandomFile createRandomFile = null;

    @Before
    public void setUp() throws IOException {
        clearFilesUsed();

    }

    @After
    public void tearDown() throws IOException, InterruptedException {
        if (createRandomFile != null) {
            //createRandomFile.interrupt();
            createRandomFile.gentleStop();
            Thread.sleep(3000);
            createRandomFile = null;
        }
        clearFilesUsed();
        if (server != null) {
            server.stop();
        }
        server = null;
    }


    @Test
    public void testServerFileWatcher() throws Exception {
        server = (ClideServer) ctx2.getBean("clideServer");
        server.ClideServerSetUp();
        server.start();
        Thread.sleep(1000);
        assertFalse(ServerContext.fileInProgress);
        createRandomFile = new CreateRandomFile(
                File.createTempFile("test", ".tar.gz", new File(FROM_DIR)));
        //new File(new File(FROM_DIR),"rashmi.tar.gz"));
        createRandomFile.start();
        //let's wait some time to make sure
        //the file gets picked up and enough time
        // has passed for a modification detection
        Thread.sleep(8000);

        // uncomment this once the directory poller is back.
        //assertTrue(ServerContext.fileInProgress);
    }

    private class CreateRandomFile extends Thread {
        private File file = null;
        private boolean shouldStop = false;

        public CreateRandomFile(final File file) {
            this.file = file;
        }

        public void gentleStop() {
            shouldStop = true;
        }

        public void run() {
            FileOutputStream fos = null;
            DataOutputStream dos = null;
            try {
                fos = new FileOutputStream(file);
                dos = new DataOutputStream(fos);
                //This take a long long time to finish if ever ...
                //Good thing after the test is done this thread is interrupted.
                for (int i = 0; i < 999999999; i++) {
                    dos.writeChars("blahblahblah");
                    dos.flush();
                    if (shouldStop) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                try {
                    dos.close();
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }


}//End of Test
