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
import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClientContext;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtilsImpl;
import gov.nih.nci.ncicb.tcga.dcc.clide.server.ClideServer;
import gov.nih.nci.ncicb.tcga.dcc.common.util.md5.MD5ChecksumCreator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Run the server and client and test that the two can transfer files over HTTP
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */
public class ClientServerOutcomesSlowTest extends ClideAbstractBaseTest {

	protected static final String CLIDE_SAMPLES_PATH = 
		Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
	
    DeleteServerFile deleteServerFile = null;

    @Before
    public void setUp() throws IOException {
        clearFilesUsed();

    }

    @After
    public void tearDown() throws IOException {
        if (deleteServerFile != null) {
            deleteServerFile.interrupt();
            deleteServerFile = null;
        }

        clearFilesUsed();
        if (server != null) {
            server.stop();
        }

        client = null;
        server = null;

    }

    /**
     * This test runs the client and server even though there are no files to transfer
     *
     * @throws InterruptedException     threading issue waiting for client/server to complete
     * @throws NoSuchAlgorithmException MD5 not on the class path
     * @throws IOException              unexpected problem with files or directories
     * @throws URISyntaxException       client or server given a bad URL or address
     */
    @Test
    public void
    testEmptyServerDirectoryTransferNoTampering()
            throws InterruptedException, NoSuchAlgorithmException, IOException, URISyntaxException {

        transferFiles();
        ensureAllFilesWereTransferred();  // in this case, it will compare zero to zero

    }


    /**
     * This test will expect that the exact number of files in one is matched in the other.  No foul play on the part of
     * the test!
     *
     * @throws InterruptedException     threading issue waiting for client/server to complete
     * @throws NoSuchAlgorithmException MD5 not on the class path
     * @throws IOException              unexpected problem with files or directories
     * @throws URISyntaxException       client or server given a bad URL or address
     */
    @Test
    public void testWholeDirectoryTransferNoTampering()
            throws InterruptedException, NoSuchAlgorithmException, IOException, URISyntaxException {

        putAllFilesIn(FROM_DIR);
        transferFiles();
        ensureAllFilesWereTransferred();

    }

    /**
     * This test will wait until the client knows about all the files on the server and then delete the last one of
     * those files to see that it shuts down clean.
     *
     * @throws InterruptedException     threading issue waiting for client/server to complete
     * @throws NoSuchAlgorithmException MD5 not on the class path
     * @throws IOException              unexpected problem with files or directories
     * @throws URISyntaxException       client or server given a bad URL or address
     */
    @Test
    public void testWholeDirTransferDeleteSecondToLastServerFileAfterClientKnowsAboutIt()
            throws InterruptedException, NoSuchAlgorithmException, IOException, URISyntaxException {

        putAllFilesIn(FROM_DIR);
        // delete the file in 3 seconds, which should be in the middle of getting an archive
        // this time probably won't work if you turn on internal debugging
        deleteServerFile = new DeleteServerFile(new File(FROM_DIR + File.separator + "z_lorem.txt"), 2200);
        deleteServerFile.start();
        transferFiles();

        // at this point the server should have noticed the error and shutdown quietly.
        Thread.sleep(5000);
        ClideServer serverTwo = (ClideServer) ctx.getBean("clideServer");
        try {
            serverTwo.start();

        } catch (Exception x) {
            fail("The first server didn't shutdown correctly.");
        }
        serverTwo.stop();


    }

    /**
     * This test will wait until the client knows about all the files on the server and then delete one of those files
     * to see how it handles the error.
     *
     * @throws InterruptedException     threading issue waiting for client/server to complete
     * @throws NoSuchAlgorithmException MD5 not on the class path
     * @throws IOException              unexpected problem with files or directories
     * @throws URISyntaxException       client or server given a bad URL or address
     */
    @Test
    public void testWholeDirTransferDeleteLastServerFileAfterClientKnowsAboutIt()
            throws InterruptedException, NoSuchAlgorithmException, IOException, URISyntaxException {

        putAllFilesIn(FROM_DIR);
        // delete z_my_pokemans in 3 seconds, which should be in the middle of getting an archive
        // this time probably won't work if you turn on internal debugging
        new DeleteServerFile(new File(FROM_DIR + File.separator + "z_my_pokemans.jpg"), 3000).start();
        transferFiles();
        ensureAllFilesWereTransferred();

    }

    @Test
    public void testReactionWhenServerIsStoppedMidRun() throws URISyntaxException, IOException {
        putAllFilesIn(FROM_DIR);
        server = (ClideServer) ctx.getBean("clideServer");
        server.setPublicKey(CLIDE_SAMPLES_PATH + "outcomes.public.key");
        server.setArchiveDir(CLIDE_SAMPLES_PATH + "to_send");
        server.setWorkingDir(CLIDE_SAMPLES_PATH + "server_working" );
        server.setSentDir(CLIDE_SAMPLES_PATH + "sent");
        server.ClideServerSetUp();
        server.start();
        try {
            Thread.sleep(3000);  // let the server completely start.  spring integratino makes startup slower
        } catch (InterruptedException e) {
            fail(e.getMessage());

        }
        new StopServerGently(server, 3000).start();  // tell the server to stop in 3 secs
        client = getTestClient();
        client.start();
    }

    @Test
    public void testClientWhenServerIsNotRunning() throws URISyntaxException {
        ClideClient c = getTestClient();
        c.start();
    }

    @Test
    public void testClientAgainstGoogleDotCom() throws URISyntaxException {
        ClideClient c = getClient("http://www.google.com");
        c.start();

    }

    @Test
    public void testClientAgainstBadAddress() throws URISyntaxException {
        ClideClient c = getClient("http://www.blahblabhlabh-lab-hla-hhdhdhhd.com");
        c.start();
        // client should shutdown after getting an UnresolvedAddressException
        // unless you registered my fake address!  you sneak.

    }

    public void ensureAllFilesWereTransferred() {
        File sent = new File(SENT_DIR);
        File to = new File(PROCESSED_DIR);
        assertEquals("Not all files transferred",
                sent.listFiles(archiveFilter).length, to.listFiles(archiveFilter).length);

    }

    public void assertFileExists(final File file) {
        assertTrue(file.getPath() + " does not exist.  File Transfer failed", file.exists());
    }

    public void assertFileSizesEqual(final File originalFile, final File transferredFile) {
        assertEquals("Transferred file is the wrong size", originalFile.length(), transferredFile.length());
    }

    public void assertMD5ChecksumsEqual(final File originalFile, final File transferredFile)
            throws NoSuchAlgorithmException, IOException {
        byte[] originalMD5 = md5Creator.generate(originalFile);
        byte[] transMD5 = md5Creator.generate(transferredFile);
        String explanation =
                "MD5 checksums not equal.  original: " + MD5ChecksumCreator.convertStringToHex(originalMD5)
                        + " transferred:" + MD5ChecksumCreator.convertStringToHex(transMD5);

        assertTrue(explanation, Arrays.equals(originalMD5, transMD5));
    }

    public void transferFiles()
            throws InterruptedException, NoSuchAlgorithmException, IOException, URISyntaxException {

        File fromDir = new File(FROM_DIR);
        assertTrue(FROM_DIR + " is not in place to be transferred.", fromDir.exists());
        server = (ClideServer) ctx.getBean("clideServer");
        server.setPublicKey(CLIDE_SAMPLES_PATH + "outcomes.public.key");
        server.setArchiveDir(CLIDE_SAMPLES_PATH + "to_send" );
        server.setWorkingDir(CLIDE_SAMPLES_PATH + "server_working" );
        server.setSentDir(CLIDE_SAMPLES_PATH + "sent" );
        server.ClideServerSetUp();
        server.start();
        Thread.sleep(3000);  // let the server completely start.  Spring integration means a slower start time.

        client = this.getTestClient();
       // client.ClideClientSetUp();
        client.start();

        File toDir = new File(PROCESSED_DIR);
        for (final File originalFile : fromDir.listFiles(archiveFilter)) {
            for (final File transFile : toDir.listFiles(archiveFilter)) {
                if (originalFile.getName().equals(transFile.getName())) {
                    assertFileExists(transFile);
                    assertFileSizesEqual(originalFile, transFile);
                    assertMD5ChecksumsEqual(originalFile, transFile);
                }
            }
        }
    }

    protected ClideClient getClient(final String serverURL) throws URISyntaxException {

        ClideClient clideClient = getTestClient();
        final URI uri = new URI(serverURL);
        final String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        final String host = uri.getHost() == null ? "localhost" : uri.getHost();
        final int port = uri.getPort() == -1 ? 80 : uri.getPort();


        clideClient.getClientContext().setUri(uri);
        clideClient.getClientContext().setHost(host);
        clideClient.getClientContext().setPort(port);

        return clideClient;
    }

   

    /**
     * This class will be used to cause problems for the client by deleting a file it is about to request
     */
    private class DeleteServerFile extends Thread {

        private File file = null;
        private long timeToWait;

        public DeleteServerFile(final File file, final long timeToWait) {
            this.file = file;
            this.timeToWait = timeToWait;
        }

        public void run() {
            try {
                Thread.sleep(timeToWait);

            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
            boolean deleted = file.delete();
            if (!deleted) {
                try {
                    fail("Could not delete " + file.getCanonicalPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * This class will be used to cause problems for the client by stopping the server mid run
     */
    private class StopServerGently extends Thread {

        private ClideServer server = null;
        private long timeToWait;

        public StopServerGently(final ClideServer server, final long timeToWait) {
            this.server = server;
            this.timeToWait = timeToWait;
        }

        public void run() {
            try {
                Thread.sleep(timeToWait);

            } catch (InterruptedException e) {
                fail(e.getMessage());
            }
            server.stop();
        }
    }
}
