/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;


/**
 * Test class for UUID email manager
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

@RunWith(JMock.class)
public class EmailManagerFastTest {

    static final String myAddress = "testing";
    private EmailManager emailManager;
    private Mockery context = new JUnit4Mockery();
    private MailSender mockMailSender = context.mock(MailSender.class);
    private String message;
    private List<UUIDDetail> list;

    @Before
    public void setUp() {
        emailManager = new EmailManager(mockMailSender);
        message = "The following UUIDs are created for the center : \n\n";
        message += "<uuid123> \n";
        message += "<uuid456> \n";

        UUIDDetail detail1 = new UUIDDetail();
        detail1.setUuid("<uuid123>");
        list = new ArrayList<UUIDDetail>();
        list.add(detail1);
        UUIDDetail detail2 = new UUIDDetail();
        detail2.setUuid("<uuid456>");
        list.add(detail2);
    }

    @Test
    public void testSuccessful() {
        context.checking(new Expectations() {{
            one(mockMailSender).send(myAddress, null, "New UUIDs Generated", message, false);
        }});
        emailManager.sendNewUUIDListToCenter(myAddress, list);
    }

}
