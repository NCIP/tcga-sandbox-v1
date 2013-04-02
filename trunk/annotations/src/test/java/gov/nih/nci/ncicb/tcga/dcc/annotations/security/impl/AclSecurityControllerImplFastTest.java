/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.annotations.security.impl;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.annotations.security.AclSecurityController;
import gov.nih.nci.ncicb.tcga.dcc.common.security.AclSecurityUtil;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ui.ModelMap;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AclSecurityControllerImplFastTest {

    private final JUnit4Mockery context = new JUnit4Mockery();
    private AclSecurityUtil aclSecurityUtil;
    private AclSecurityController aclSecurityController;
    private ModelMap modelMap;

    @Before
    public void setup() {
        aclSecurityUtil = context.mock(AclSecurityUtil.class);
        aclSecurityController = new AclSecurityControllerImpl();
        aclSecurityController.setAclSecurityUtil(aclSecurityUtil);
        modelMap = new ModelMap();
    }

    @Test
    public void testHandleRequestWithCorrectClassAndVoter() throws AnnotationQueries.AnnotationQueriesException {

        final boolean hasPermission = true;
        final String aclVoterName = "ACL_ANNOTATION_NOTE_EDITOR";
        final String objectClass = "DccAnnotationNote";
        final Long objectId = 1L;
        final String recipient = "usernameTest";

        context.checking(new Expectations() {{
            one(aclSecurityUtil).hasWritePermission(objectId, recipient);
            will(returnValue(hasPermission));
        }});

        aclSecurityController.handleRequest(modelMap,
                                  aclVoterName,
                                  objectClass,
                                  objectId,
                                  recipient);

        final String expectedKey = "hasPermission";
        final String unexpectedKey = "exception";
        assertTrue(modelMap.containsKey(expectedKey));
        assertFalse(modelMap.containsKey(unexpectedKey));
        assertEquals(hasPermission, modelMap.get(expectedKey));
    }

    @Test
    public void testHandleRequestWithIncorrectClass() throws AnnotationQueries.AnnotationQueriesException {

        final boolean hasPermission = false;
        final String aclVoterName = "ACL_ANNOTATION_NOTE_EDITOR";
        final String objectClass = "Incorrect";
        final Long objectId = 1L;
        final String recipient = "usernameTest";

        aclSecurityController.handleRequest(modelMap,
                                  aclVoterName,
                                  objectClass,
                                  objectId,
                                  recipient);

        final String expectedKey = "hasPermission";
        final String unexpectedKey = "exception";
        assertTrue(modelMap.containsKey(expectedKey));
        assertFalse(modelMap.containsKey(unexpectedKey));
        assertEquals(hasPermission, modelMap.get(expectedKey));
    }

    @Test
    public void testHandleRequestWithIncorrectVoter() throws AnnotationQueries.AnnotationQueriesException {

        final boolean hasPermission = false;
        final String aclVoterName = "Incorrect";
        final String objectClass = "DccAnnotationNote";
        final Long objectId = 1L;
        final String recipient = "usernameTest";

        aclSecurityController.handleRequest(modelMap,
                                  aclVoterName,
                                  objectClass,
                                  objectId,
                                  recipient);

        final String expectedKey = "hasPermission";
        final String unexpectedKey = "exception";
        assertTrue(modelMap.containsKey(expectedKey));
        assertFalse(modelMap.containsKey(unexpectedKey));
        assertEquals(hasPermission, modelMap.get(expectedKey));
    }

    @Test
    public void testHandleRequestWithCorrectClassAndVoterThrowingException() 
            throws AnnotationQueries.AnnotationQueriesException {

        final String aclVoterName = "ACL_ANNOTATION_NOTE_EDITOR";
        final String objectClass = "DccAnnotationNote";
        final Long objectId = 1L;
        final String recipient = "usernameTest";

        context.checking(new Expectations() {{
            one(aclSecurityUtil).hasWritePermission(objectId, recipient);
            will(throwException(new AnnotationQueries.AnnotationQueriesException("test exception message")));

        }});

        aclSecurityController.handleRequest(modelMap,
                                  aclVoterName,
                                  objectClass,
                                  objectId,
                                  recipient);

        final String expectedKey1 = "hasPermission";
        final String expectedKey2 = "exception";

        assertTrue(modelMap.containsKey(expectedKey1));
        assertEquals(false, modelMap.get(expectedKey1));

        assertTrue(modelMap.containsKey(expectedKey2));
        assertEquals(true, modelMap.get(expectedKey2));
    }
}
