/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.annotations.security;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.security.AclSecurityUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.security.DccAnnotationNoteRetrievalStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.acls.AccessControlEntry;
import org.springframework.security.acls.Acl;
import org.springframework.security.acls.MutableAclService;
import org.springframework.security.acls.Permission;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.jdbc.EhCacheBasedAclCache;
import org.springframework.security.acls.objectidentity.ObjectIdentity;
import org.springframework.security.acls.sid.PrincipalSid;
import org.springframework.security.acls.sid.Sid;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/**
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class AclSecurityUtilImplSlowTest extends AbstractTransactionalDataSourceSpringContextTests {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AnnotationQueries annotationQueries;

    @Autowired
    private EhCacheBasedAclCache ehCacheBasedAclCache;

    @Autowired
    private AclSecurityUtil aclSecurityUtil;

    @Autowired
    private MutableAclService mutableAclService;

    /** A variable for Oracle "boolean" type */
    private final static int ORACLE_TRUE = 1;

    @Override
    protected String getConfigPath() {
        return "./authorizationTestApplicationContext.xml";
    }

    @Override
    protected void onSetUpInTransaction() throws AnnotationQueries.AnnotationQueriesException {

        //Role-based Authorization
        final String authorizationSQL1 = "insert into users (username, password, enabled) values ('testuser', 'zzz', " + ORACLE_TRUE + ")";
        final String authorizationSQL2 = "insert into authorities (username, authority) values ('testuser', 'ROLE_TESTUSER')";
        final String[] authorizationSQLArray = {authorizationSQL1, authorizationSQL2};
        jdbcTemplate.batchUpdate(authorizationSQLArray);

        //Domain object
        final int item_type_id = 1;
        final long annotation_category_id = 1;
        final long annotation_category_type_id = 1;
        final long annotation_id = 1;
        final long annotation_note_id = 1;
        final String domainObjectSQL0 = "insert into annotation_classification (annotation_classification_id, classification_display_name) " +
                "values (1, 'Notification')";
        final String domainObjectSQL1 = "insert into annotation_item_type (item_type_id, type_display_name, type_description) "
                + "values (" + item_type_id + ", 'patient', 'patient')";
        final String domainObjectSQL2 = "insert into annotation_category (annotation_category_id, category_display_name, category_description, annotation_classification_id) "
                + "values (" + annotation_category_id + ", 'withdrew consent', 'withdrew consent for study', 1)";
        final String domainObjectSQL3 = "insert into annotation_category_item_type (annotation_category_type_id, annotation_category_id, item_type_id) "
                + "values (" + annotation_category_type_id + ", " + annotation_category_id + ", " + item_type_id + ")";
        final String domainObjectSQL4 = "insert into annotation (annotation_id, annotation_category_id, entered_by, entered_date) "
                + "values (" + annotation_id + ", " + annotation_category_id + ", 'test', to_date('2010/01/01 00:01:01', 'yyyy/mm/dd hh24:mi:ss'))";
        final String domainObjectSQL5 = "insert into annotation_note (annotation_note_id, annotation_id, note, entered_by, entered_date) "
                + "values (" + annotation_note_id + ", " + annotation_id + ", 'testnote', 'testuser', to_date('2010/01/01 00:01:01', 'yyyy/mm/dd hh24:mi:ss'))";
        final String[] domainObjectSQLArray = {
                domainObjectSQL0,
                domainObjectSQL1,
                domainObjectSQL2,
                domainObjectSQL3,
                domainObjectSQL4,
                domainObjectSQL5
        };
        jdbcTemplate.batchUpdate(domainObjectSQLArray);
    }

    @Override
    protected void onTearDownInTransaction() throws AnnotationQueries.AnnotationQueriesException {

        //Reset ACL cache (Necessary before running all Tests at once)
        DccAnnotationNote dccAnnotationNote = annotationQueries.getAnnotationNoteById(1L);
        ehCacheBasedAclCache.evictFromCache(new DccAnnotationNoteRetrievalStrategy().getObjectIdentity(dccAnnotationNote));
    }


    @Override
    protected void onTearDownAfterTransaction() throws Exception {

        //Reset Authentication object for the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public void testAddPermission() throws AnnotationQueries.AnnotationQueriesException {

        //Retrieve Domain object to protect
        final DccAnnotationNote dccAnnotationNote = annotationQueries.getAnnotationNoteById(1L);
        assertNotNull(dccAnnotationNote);

        //Set up Authentication object for the SecurityContext
        final UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "zzz", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Adding WRITE permission to second user
        final Sid recipient = new PrincipalSid("testuser2");
        Permission permission = BasePermission.WRITE;
        aclSecurityUtil.addPermission(dccAnnotationNote, recipient, permission);

        //Retrieve ACL from the domain object
        final ObjectIdentity objectIdentity = new DccAnnotationNoteRetrievalStrategy()
                .getObjectIdentity(dccAnnotationNote);
        final Sid[] sidArray = {recipient};
        Acl acl = mutableAclService.readAclById(objectIdentity, sidArray);
        assertNotNull(acl);

        //Retrieve ACE entries
        AccessControlEntry[] accessControlEntries = acl.getEntries();

        //There should be only the one added
        assertEquals(1, accessControlEntries.length);
        AccessControlEntry accessControlEntry = accessControlEntries[0];
        assertNotNull(accessControlEntry);

        //Verify that the correct permission was given to the recipient
        assertEquals(recipient, accessControlEntry.getSid());
        assertEquals(permission, accessControlEntry.getPermission());
    }

    public void testAddPermissionWithDefaultRecipient() throws AnnotationQueries.AnnotationQueriesException {

        //Retrieve Domain object to protect
        final DccAnnotationNote dccAnnotationNote = annotationQueries.getAnnotationNoteById(1L);
        assertNotNull(dccAnnotationNote);

        //Set up Authentication object for the SecurityContext
        final String testUser = "testuser";
        final UserDetails userDetails = userDetailsService.loadUserByUsername(testUser);
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "zzz", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Adding WRITE permission to same user
        Permission permission = BasePermission.WRITE;
        aclSecurityUtil.addPermission(dccAnnotationNote, permission);

        //Retrieve ACL from the domain object
        final ObjectIdentity objectIdentity = new DccAnnotationNoteRetrievalStrategy()
                .getObjectIdentity(dccAnnotationNote);

        final Sid recipient = new PrincipalSid(testUser);
        final Sid[] sidArray = {recipient};
        Acl acl = mutableAclService.readAclById(objectIdentity, sidArray);
        assertNotNull(acl);

        //Retrieve ACE entries
        AccessControlEntry[] accessControlEntries = acl.getEntries();

        //There should be only the one added
        assertEquals(1, accessControlEntries.length);
        AccessControlEntry accessControlEntry = accessControlEntries[0];
        assertNotNull(accessControlEntry);

        //Verify that the correct permission was given to the recipient
        assertEquals(recipient, accessControlEntry.getSid());
        assertEquals(permission, accessControlEntry.getPermission());
    }

    public void testHasPermissionWithObject() throws AnnotationQueries.AnnotationQueriesException {

        //Retrieve Domain object on which to check the permission
        final DccAnnotationNote dccAnnotationNote = annotationQueries.getAnnotationNoteById(1L);
        assertNotNull(dccAnnotationNote);

        //Set up Authentication object for the SecurityContext
        final String testUser = "testuser";
        final UserDetails userDetails = userDetailsService.loadUserByUsername(testUser);
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "zzz", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Adding WRITE permission to same user
        Permission permission = BasePermission.WRITE;
        aclSecurityUtil.addPermission(dccAnnotationNote, permission);

        final Sid recipient = new PrincipalSid(testUser);
        boolean hasPermission = aclSecurityUtil.hasPermission(dccAnnotationNote, recipient, BasePermission.WRITE);
        assertTrue("The recipient does not have the permission", hasPermission);
    }

    public void testHasPermissionWithObjectId() throws AnnotationQueries.AnnotationQueriesException {

        //Retrieve Domain object on which to check the permission
        final long dccAnnotationNoteId = 1L;
        final DccAnnotationNote dccAnnotationNote = annotationQueries.getAnnotationNoteById(dccAnnotationNoteId);
        assertNotNull(dccAnnotationNote);

        //Set up Authentication object for the SecurityContext
        final String testUser = "testuser";
        final UserDetails userDetails = userDetailsService.loadUserByUsername(testUser);
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "zzz", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Adding WRITE permission to same user
        Permission permission = BasePermission.WRITE;
        aclSecurityUtil.addPermission(dccAnnotationNote, permission);

        boolean hasPermission = aclSecurityUtil.hasPermission(dccAnnotationNoteId, testUser, BasePermission.WRITE);
        assertTrue("The recipient does not have the permission", hasPermission);
    }

    public void testHasWritePermission() throws AnnotationQueries.AnnotationQueriesException {

        //Retrieve Domain object on which to check the permission
        final long dccAnnotationNoteId = 1L;
        final DccAnnotationNote dccAnnotationNote = annotationQueries.getAnnotationNoteById(dccAnnotationNoteId);
        assertNotNull(dccAnnotationNote);

        //Set up Authentication object for the SecurityContext
        final String testUser = "testuser";
        final UserDetails userDetails = userDetailsService.loadUserByUsername(testUser);
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "zzz", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Adding WRITE permission to same user
        Permission permission = BasePermission.WRITE;
        aclSecurityUtil.addPermission(dccAnnotationNote, permission);

        boolean hasPermission = aclSecurityUtil.hasWritePermission(dccAnnotationNoteId, testUser);
        assertTrue("The recipient does not have the permission", hasPermission);
    }
}
