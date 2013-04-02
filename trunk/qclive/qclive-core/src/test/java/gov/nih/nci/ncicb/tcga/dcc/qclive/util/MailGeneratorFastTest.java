/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.util;

import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for MailGenerator
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

/**
 * Tests the success and failure messages. For this, it uses the geronimo libraries.
 * In the application, it uses mail facilities provided by the container.
 */
@RunWith(JMock.class)
public class MailGeneratorFastTest {

    static final String myAddress = "testing";
    MailGenerator mailGen;
    Mockery context = new JUnit4Mockery();
    MailSender mockMailSender = context.mock(MailSender.class);

    @Before
    public void setUp() {
        RowCounter counter = new RowCounter();
        counter.addToRowCounts( "hybridization_value", 12000 );
        mailGen = new MailGenerator( mockMailSender, myAddress, "foo", counter );
    }

    @Test
    public void testSuccessful() {
        context.checking( new Expectations() {{
            one(mockMailSender).send(myAddress, null, "Loader Succeeded", "This is a test message", false );
        }});
        mailGen.sendSuccessEmail( "This is a test message" );
    }

    @Test
    public void testFailure() {

        PrintWriter pw = null;

        try {
            //noinspection ThrowableInstanceNeverThrown
            Exception testException = new Exception("test exception");
            StringWriter sw = new StringWriter();
            //noinspection IOResourceOpenedButNotSafelyClosed
            pw = new PrintWriter( sw );
            testException.printStackTrace( pw );
            final String stackTrace = sw.toString();
            pw.close();

            context.checking( new Expectations() {{
                one(mockMailSender).send(myAddress, null, "Loader Failed", "Loader Failed. Archive directory: foo\n\n" + stackTrace, false);
            }});

            mailGen.sendFailureEmail( testException );
        } finally {
            IOUtils.closeQuietly(pw);
        }
    }

    @Test
    public void testStart() {
        context.checking(new Expectations() {{
            one(mockMailSender).send(myAddress, null, "Loader Started", "Loader started. Archive directory: foo", false);
        }});
        mailGen.sendStartEmail();
    }
}
