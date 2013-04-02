/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide;

import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.CMD_KEY;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.ENC_KEY;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.ENC_REGEX;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.FILE_SIZE_KEY;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.INTEGER_REGEX;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.PATH_KEY;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.PATH_REGEX;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.RESP_KEY;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.CLIENT.getServerLogFile;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.CLIENT.giveMeThisFile;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.CLIENT.hello;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.CLIENT.terminateConnection;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.CLIENT.unableToFindNextCommand;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.CLIENT.whatAreTheFiles;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.SERVER.failure;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.SERVER.hereAreTheFilePathsWithSize;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.SERVER.iHaveFilesToSend;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.SERVER.sendServerLogFile;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.SERVER.terminatingConnection;
import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol.SERVER.unableToRespond;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClientContext;
import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClientProtocolHandler;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideProtocol;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtils;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtilsImpl;
import gov.nih.nci.ncicb.tcga.dcc.clide.server.ServerContext;
import gov.nih.nci.ncicb.tcga.dcc.clide.server.ServerProtocolHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test clide protocol
 * 
 * @author Jon Whitmore Last updated by: $
 * @version $
 */

@RunWith(JMock.class)
public class ClideProtocolFastTest extends ClideAbstractBaseTest {

	private final Mockery context = new JUnit4Mockery();
	private ClientProtocolHandler client = null;
	private ServerProtocolHandler server = null;
	private ClideUtils clideUtils = null;
	private ArrayList<String> filePaths = null;
	private ClientContext clientContext = null;

	@Before
	public void setUp() throws IOException {
		ServerContext.setWorkingDir(new File(WORKING_DIR));
		ServerContext.setDownloadDir(new File(ORIGINALS_DIR));

		clientContext = new ClientContext();
		clientContext.setForceValidate(false);
		clientContext.setEncryptionEnabled(false);

		ClideContextHolder.setClientContext(clientContext);

		client = new ClientProtocolHandler();
		server = new ServerProtocolHandler();
		clideUtils = context.mock(ClideUtils.class);
		client.setClideUtils(clideUtils);
		server.setClideUtils(clideUtils);
		if (filePaths == null) {
			filePaths = new ArrayList<String>();
			for (final File f : ServerContext.getDownloadDir().listFiles(
					new ClideConstants.ArchiveFilter())) {
				filePaths.add(f.getCanonicalPath());

			}
		}

	}

	@Test
	public void testClientHelloMessage() {
		final String command = client.initiateCommunication(clientContext);
		assertTrue("Client can't say hello?",
				command.matches(ClideProtocol.get(hello)));
	}

	@Test
	public void testServerHelloResponse() throws IOException {
		// This test requires a real clide utils to move files around

		server.setClideUtils(new ClideUtilsImpl());
		final String message = client.initiateCommunication(clientContext);
		final String response = server.respondTo(message);
		assertTrue("Server doesn't understand hello?",
				response.matches(ClideProtocol.get(iHaveFilesToSend)));

		assertTrue("Server can't format I have files message",
				verifyFilesToSendMessage(filePaths, response));
	}

	@Test
	public void testCheckFileSizeWayTooBig() throws Exception {
		// The checkClientFreeSpace is tested in the ClideUtils test class
		// so I can mock its result here with confidence

		context.checking(new Expectations() {
			{
				allowing(clideUtils).checkClientFreeSpace(999999999999L,
						clientContext);
				will(returnValue(false));
			}
		});
		String message = "resp=hereAreTheFilePathsWithSize "
				+ "path=/test/crazyBigFile.huge fileSize=999999999999;";
		String command = client.respondTo(message);
		assertTrue(message.matches(ClideProtocol
				.get(ClideProtocol.SERVER.hereAreTheFilePathsWithSize)));
		assertEquals(
				ClideProtocol.get(ClideProtocol.CLIENT.terminateConnection),
				command);
	}

	@Test
	public void testClientMessageAfterServerHasFilesResponse() {
		String raw = ClideProtocol.get(iHaveFilesToSend);
		String response = raw.replace(INTEGER_REGEX,
				String.valueOf(filePaths.size()));
		String command = client.respondTo(response);
		assertEquals("client can't response to 'I have files' command.",
				ClideProtocol.get(whatAreTheFiles), command);

	}

	@Test
	public void testServerPathsMessage() throws IOException {
		String message = ClideProtocol.get(whatAreTheFiles);
		String response = server.respondTo(message);
		assertTrue(
				"Server not responding to 'what are the files' message.",
				response.matches(ClideProtocol.get(hereAreTheFilePathsWithSize)));

		assertTrue("server can't format here are the paths response",
				verifyHereAreTheFilesPathsMessage(filePaths, response));

	}

	@Test
	public void testBadResponseToClient() {
		String response = RESP_KEY + "=youAreAWasteOfBandwidth;";
		String command = client.respondTo(response);
		assertEquals("client thought this was real response: " + response,
				ClideProtocol.get(unableToFindNextCommand), command);
	}

	@Test
	public void testBadCommandToServer() throws IOException {
		String command = CMD_KEY + "=makeAPizza;";
		String response = server.respondTo(command);
		assertEquals("server thought this was a real command: " + command,
				ClideProtocol.get(unableToRespond), response);
	}

	@Test
	public void testClientCommandAfterANoSuchResponse() {
		String response = ClideProtocol.get(unableToRespond);
		String command = client.respondTo(response);
		assertEquals("the client didn't initiate termination",
				ClideProtocol.get(terminateConnection), command);
	}

	@Test
	public void testServerResponseAfterAUnableToFindNextCommand()
			throws IOException {
		String command = ClideProtocol.get(unableToFindNextCommand);
		String response = server.respondTo(command);
		assertEquals("the server didn't terminate",
				ClideProtocol.get(terminatingConnection), response);
	}

	@Test
	public void testGoodFileRequest() throws IOException {
		String raw = ClideProtocol.get(giveMeThisFile);
		String goodPath = filePaths.get(0);
		long size = new File(goodPath).length();
		raw = raw.replace(PATH_REGEX, PATH_KEY + "=" + goodPath + " "
				+ FILE_SIZE_KEY + "=" + size);
		String command = raw.replace(ENC_REGEX, ENC_KEY + "=true");
		String response = server.respondTo(command);
		assertEquals("Server should respond with a file, not a " + RESP_KEY
				+ "= message.  Should have been null", null, response);

	}

	@Test
	public void testNonexistentFileRequest() throws IOException {
		String raw = ClideProtocol.get(giveMeThisFile);
		String badPath = "/dev/null/someFileThatDoesNotExist.foo";
		raw = raw.replace(PATH_REGEX, PATH_KEY + "=" + badPath + " "
				+ FILE_SIZE_KEY + "=1234");
		String command = raw.replace(ENC_REGEX, ENC_KEY + "=false");
		String response = server.respondTo(command);
		assertNull("Server tried to send the file: " + badPath, response);

	}

	@Test
	public void testClientsReactionToFailure() {
		String raw = ClideProtocol.get(failure);
		String response = raw.replace(INTEGER_REGEX,
				String.valueOf(HttpResponseStatus.NOT_FOUND.getCode()));
		String command = client.respondTo(response);
		assertEquals("Client did not try to end session after failure.",
				ClideProtocol.get(terminateConnection), command);

	}

	@Test
	public void testServersResponseToTerminate() throws IOException {
		String response = server.respondTo(ClideProtocol
				.get(terminateConnection));
		assertEquals("Server did not know to terminate",
				ClideProtocol.get(terminatingConnection), response);
	}

	@Test
	public void testFindNullCommand() {
		assertEquals(ClideProtocol.CLIENT.commandNotFound,
				ClideProtocol.findCommand(null));
	}

	@Test
	public void testFindNullResponse() {
		assertEquals(ClideProtocol.SERVER.responseNotFound,
				ClideProtocol.findResponse(null));
	}

	@Test
	public void testGetServerLogFile() throws Exception {
		String response = server.respondTo(ClideProtocol.get(getServerLogFile));
		assertEquals(ClideProtocol.get(sendServerLogFile), response);
	}

	public static boolean verifyFilesToSendMessage(final ArrayList paths,
			final String response) {
		final String raw = ClideProtocol
				.get(ClideProtocol.SERVER.iHaveFilesToSend);
		if (!response.matches(raw)) {
			return false;
		}

		final int n = paths.size();
		final String nString = String.valueOf(n);
		final int position = response.indexOf(nString);
		if (position < 0) {
			return false;
		}

		final int length = nString.length();
		// position + length - 1 would be the index of the last char in the
		// number
		// position + length would be the index of the next char ';', the end of
		// the string
		// so position + length + 1 should equal the length of the string
		return (response.length() == position + length + 1);

	}

	public static boolean verifyHereAreTheFilesPathsMessage(
			final ArrayList paths, final String response) {
		final String raw = ClideProtocol
				.get(ClideProtocol.SERVER.hereAreTheFilePathsWithSize);
		if (!response.matches(raw)) {
			return false;
		}
		final Pattern p = Pattern.compile(PATH_REGEX);
		final Matcher m = p.matcher(response);

		int i = 0;
		while (m.find()) {
			++i;
		}
		return i == paths.size();
	}

}
