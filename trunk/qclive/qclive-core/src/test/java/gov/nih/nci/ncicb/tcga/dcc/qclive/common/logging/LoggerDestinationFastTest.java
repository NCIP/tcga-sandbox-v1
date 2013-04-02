/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging;

import static junit.framework.Assert.assertEquals;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for all LoggerDestination implementations.
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class LoggerDestinationFastTest {

	private Mockery context = new JUnit4Mockery();
	
	private static final String OUTPUT_PATH = Thread.currentThread().getContextClassLoader().getResource("").getPath();

	@Test
	public void stdoutLoggerDestination() throws IOException,
			LoggerDestination.LoggerException {

        PrintStream outStream = null;

        try {
            // redirect stdout to a file
            String outfile = OUTPUT_PATH + "stdout.txt";
            //noinspection IOResourceOpenedButNotSafelyClosed
            outStream = new PrintStream(new BufferedOutputStream(
                    new FileOutputStream(outfile)));
            System.setOut(outStream);
            // send message to logger destination
            StdoutLoggerDestination dest = new StdoutLoggerDestination();
            dest.setMinLevel(Level.DEBUG);
            String message = "THIS IS AN ERROR MESSAGE";
            dest.logToDestination(Level.ERROR, message);
            outStream.close();
            // read in output file and compare to expected
            assertEquals(message, readFirstLine(outfile));
            new File(outfile).deleteOnExit();
        } finally {
            IOUtils.closeQuietly(outStream);
        }
	}

	@Test
	public void fileLoggerDestination()
			throws LoggerDestination.LoggerException, IOException {
		String outfile = OUTPUT_PATH + "file.txt";
		FileLoggerDestination dest = new FileLoggerDestination(outfile, false);
		dest.setMinLevel(Level.DEBUG);
		String message = "This message is logged to a fie";
		dest.logToDestination(Level.WARN, message);
		assertEquals(message, readFirstLine(outfile));
		new File(outfile).deleteOnExit();
	}

	@Test
	public void emailLoggerDestination()
			throws LoggerDestination.LoggerException {
		final String emailAddress = "chenjw@mail.nih.gov";
		final String message = "This is a test message that is not real";
		final String subject = message.substring(0, 20) + "...";
		// use jmock to make mock mail sender
		final MailSender mailSender = context.mock(MailSender.class);
		context.checking(new Expectations() {
			{
				one(mailSender).send(emailAddress, null, subject, message,
						false);
			}
		});
		EmailLoggerDestination dest = new EmailLoggerDestination();
		dest.setEmailAddress(emailAddress);
		dest.setMailSender(mailSender);
		dest.setMinLevel(Level.ERROR);
		dest.logToDestination(Level.ERROR, message);
	}

	private String readFirstLine(String filename) throws IOException {

        String result = null;
        BufferedReader outputReader = null;

        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            outputReader = new BufferedReader(new FileReader(
                    filename));
            String line = outputReader.readLine();
            outputReader.close();
            result = line;
        } finally {
            IOUtils.closeQuietly(outputReader);
        }

        return result;
	}
}
