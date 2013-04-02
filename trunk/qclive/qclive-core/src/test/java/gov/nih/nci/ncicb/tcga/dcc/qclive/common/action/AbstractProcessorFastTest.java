package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for AbstractProcessor.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AbstractProcessorFastTest {

    private final Mockery context = new JUnit4Mockery();
    private AbstractProcessor<String, String> testProcessor;
    private MailSender mockMailSender;

    @Before
    public void setUp() {
        mockMailSender = context.mock(MailSender.class);
        // set up a test processor that implements the required methods but doesn't do anything
        testProcessor = new AbstractProcessor<String, String>() {
            // does nothing, just for testing
            protected String doWork(String input, QcContext context) throws ProcessorException {
                return input;
            }

            @Override
            protected String buildEmailSubject(String input, QcContext context) {
                return "SUBJECT";
            }

            @Override
            protected String buildEmailBody(String input, QcContext context) {
                return "BODY";
            }

            public String getName() {
                return "test process";
            }

        };
        // set do email to true
        testProcessor.setDoEmail(true);
        testProcessor.setMailSender(mockMailSender);
    }

    /**
     * Test that when execute finishes successfully, if do email is enabled then the email
     * is sent.
     *
     * @throws Processor.ProcessorException if it has to
     */
    @Test
    public void testEmailSending() throws Processor.ProcessorException {
        final Archive archive = new Archive();
        final Center center = new Center();
        final List<String> emailList = new ArrayList<String>();
        emailList.add("testEmail");
        center.setEmailList(emailList);
        archive.setTheCenter(center);
        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        // we overrode the methods to get email subject and body to return SUBJECT and BODY
        // email address should be gotten from context archive
        context.checking( new Expectations() {{
            one(mockMailSender).send("testEmail", null, "SUBJECT", "BODY", false );
        }});

        testProcessor.execute("some string", qcContext);
    }

    /**
     * Test that no email is sent when inputValidationDoEmail = false
     *
     */
    @Test
    public void testSendInputValidationEmailNoSignal() {
        final Archive archive = new Archive();
        final QcContext qcContext = new QcContext();
        final Center center = new Center();
        final List<String> emailList = new ArrayList<String>();
        emailList.add("testEmail");
        center.setEmailList(emailList);
        archive.setTheCenter(center);
        qcContext.setArchive(archive);
    	testProcessor.setInputValidationDoEmail(false);
        context.checking( new Expectations() {{
            exactly(0).of(mockMailSender).send("testEmail", null, "SUBJECT", "BODY", false );
        }});
        testProcessor.sendInputValidationEmail("some string", qcContext);
    }

    /**
     * Test that no email is sent when there are no errors to report in context
     *
     */
    @Test
    public void testSendInputValidationEmailNoSend() {
        final Archive archive = new Archive();
        final QcContext qcContext = new QcContext();
        final Center center = new Center();
        final List<String> emailList = new ArrayList<String>();
        emailList.add("testEmail");
        center.setEmailList(emailList);
        archive.setTheCenter(center);
        qcContext.setArchive(archive);
    	testProcessor.setInputValidationDoEmail(true);
        context.checking( new Expectations() {{
            exactly(0).of(mockMailSender).send("testEmail", null, "SUBJECT", "BODY", false );
        }});
        testProcessor.sendInputValidationEmail("some string", qcContext);
    }

    /**
     * Test that an email is sent when there is a
     * mailSender
     * inputValidationDoEmail = true
     * errors in context
     *
     */
    @Test
    public void testSendInputValidationEmail() {
        final Archive archive = new Archive();
        final QcContext qcContext = new QcContext();
        final Center center = new Center();
        final List<String> emailList = new ArrayList<String>();
        qcContext.addError("error foo");
    	testProcessor.setInputValidationDoEmail(true);
        emailList.add("testEmail");
        center.setEmailList(emailList);
        archive.setTheCenter(center);
        qcContext.setArchive(archive);
        context.checking( new Expectations() {{
            one(mockMailSender).send("testEmail", null, "SUBJECT", "BODY", false );
        }});
        testProcessor.sendInputValidationEmail("some string", qcContext);
    }

    @Test
    public void testEmailSendingNoArchive() throws Processor.ProcessorException {
        QcContext qcContext = new QcContext();
        // no expectations, because mockMailSender should not be called,
        // since qcContext does not have Archive with Center and email set
        testProcessor.execute("hi", qcContext);

        qcContext.setArchive(new Archive());
        testProcessor.execute("archive has no center", qcContext);


    }
}
