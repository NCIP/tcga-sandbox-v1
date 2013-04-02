/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.security.impl;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.security.AclSecurityUtil;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.acls.AccessControlEntry;
import org.springframework.security.acls.MutableAcl;
import org.springframework.security.acls.MutableAclService;
import org.springframework.security.acls.NotFoundException;
import org.springframework.security.acls.Permission;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.objectidentity.ObjectIdentityImpl;
import org.springframework.security.acls.sid.Sid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This class tests the AclSecurityUtilImpl class for unusual cases
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AclSecurityUtilImplFastTest {

    private final JUnit4Mockery context = new JUnit4Mockery();
    private AclSecurityUtil aclSecurityUtil;
    private MutableAclService mutableAclService;
    private AnnotationQueries annotationQueries;
    private MutableAcl acl;
    private AccessControlEntry accessControlEntry;

    @Before
    public void setup() {
        mutableAclService = context.mock(MutableAclService.class);
        annotationQueries = context.mock(AnnotationQueries.class);

        aclSecurityUtil = new AclSecurityUtilImpl();
        aclSecurityUtil.setMutableAclService(mutableAclService);
        aclSecurityUtil.setAnnotationQueries(annotationQueries);

        acl = context.mock(MutableAcl.class);
        accessControlEntry = context.mock(AccessControlEntry.class);
    }

    @Test
    public void testAddPermissionWhenNotFoundExceptionOccurs() {

        final DccAnnotationNote dccAnnotationNote = new DccAnnotationNote();
        dccAnnotationNote.setNoteId(1L);
        final Sid recipient = null;
        final Permission permission = null;

        context.checking(new Expectations() {{
            one(mutableAclService).readAclById(new ObjectIdentityImpl(DccAnnotationNote.class, dccAnnotationNote.getNoteId()));
            will(returnValue(acl));

            one(acl).getEntries();
            will(returnValue(new AccessControlEntry[0]));

            one(acl).insertAce(0, permission, recipient, true);
            will(throwException(new NotFoundException("NotFoundException should be caught, not thrown")));

            one(mutableAclService).updateAcl(acl);
        }});

        try {
            aclSecurityUtil.addPermission(dccAnnotationNote, recipient, permission);
        } catch (NotFoundException unexpected) {
            fail("NotFoundException should not have been thrown");
        }
    }

    @Test
    public void testHasPermissionWhenPermissionMaskIsDifferent() {

        final DccAnnotationNote dccAnnotationNote = new DccAnnotationNote();
        dccAnnotationNote.setNoteId(1L);
        final Sid recipient = null;
        final Sid[] recipientArray = {recipient};
        final Permission permission = BasePermission.WRITE;
        final Permission differentPermission = BasePermission.READ;
        assertTrue(permission != differentPermission);

        final AccessControlEntry[] accessControlEntries = new AccessControlEntry[1];
        accessControlEntries[0] = accessControlEntry;


        context.checking(new Expectations() {{
            one(mutableAclService).readAclById(
                    new ObjectIdentityImpl(DccAnnotationNote.class, dccAnnotationNote.getNoteId()),
                    recipientArray
            );
            will(returnValue(acl));

            one(acl).getEntries();
            will(returnValue(accessControlEntries));

            one(accessControlEntry).getPermission();
            will(returnValue(differentPermission));
        }});

        boolean result = aclSecurityUtil.hasPermission(dccAnnotationNote, recipient, permission);
        assertFalse(result);
    }

    @Test
    public void testHasPermissionWhenNotFoundExceptionOccurs() {

        final DccAnnotationNote dccAnnotationNote = new DccAnnotationNote();
        dccAnnotationNote.setNoteId(1L);
        final Sid recipient = null;
        final Sid[] recipientArray = {recipient};
        final Permission permission = BasePermission.WRITE;

        context.checking(new Expectations() {{
            one(mutableAclService).readAclById(
                    new ObjectIdentityImpl(DccAnnotationNote.class, dccAnnotationNote.getNoteId()),
                    recipientArray
            );
            will(throwException(new NotFoundException("Expected")));
        }});

        boolean result = aclSecurityUtil.hasPermission(dccAnnotationNote, recipient, permission);
        assertFalse(result);
    }

    @Test
    public void testHasPermissionWhenDccAnnotationNoteIsNull() throws AnnotationQueries.AnnotationQueriesException {

        final Long dccAnnotationNoteId = 1L;
        final String recipient = null;
        final Permission permission = BasePermission.WRITE;

        context.checking(new Expectations() {{
            one(annotationQueries).getAnnotationNoteById(dccAnnotationNoteId);
            will(returnValue(null));
        }});

        boolean result = aclSecurityUtil.hasPermission(dccAnnotationNoteId, recipient, permission);
        assertFalse(result);
    }
}
