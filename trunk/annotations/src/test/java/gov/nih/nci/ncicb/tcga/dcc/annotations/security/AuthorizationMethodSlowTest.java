/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.annotations.security;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationCategory;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItem;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries.AnnotationQueriesException;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.security.DccAnnotationNoteRetrievalStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.acls.MutableAclService;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.jdbc.EhCacheBasedAclCache;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import java.util.Date;


/**
 * This class does several unit tests to make sure that access to restricted methods and object instances is granted
 * only when the user calling them is authenticated and as the proper authority, be it Role-based, ACL-based or both.
 *
 * Important information - PLease read before modifying/adding tests:
 * ------------------------------------------------------------------
 *
 * This class extends <code>AbstractTransactionalDataSourceSpringContextTests</code> to take full advantage
 * of database rollback after each test. Data entry is made trough call to the <code>jdbcTemplate</code> made available by
 * the class being extended.
 *
 * Updating ACL data using Spring Security API (2.0.5) relies on automatically generated primary keys for the ACL tables.
 * Under Oracle, triggers had to be set up to comply with this behavior. This complicates data entry in unit tests when
 * the primary keys need to be used in other inserts. One way around it was to dynamically create a stored procedure
 * which would be able to pass the generated primary keys through variables.
 *
 * However there are <b>RESTRICTIONS</b> as to how stored procedures can be called without breaking the rollback feature:
 *
 * The Spring 2.5.6 API explicitly states to not use <code>executeSqlScript()</code> as it will break the rollback.
 *
 * A way around it is to create the procedure using <code>jdbcTemplate</code> directly. However, it has been empirically found
 * (no documentation provides this info as far as I know) that data inserted by calling <code>update()</code>
 * or <code>execute()</code> on <code>jdbcTemplate</code> <b>BEFORE</b> calling the stored procedure will not be rolled back.
 * Data inserted <b>AFTER</b> however will be rolled back as expected. Data inserted <b>DURING</b> the procedure will be rolled back.
 * The procedure itself will not be rolled back so it needs to be removed explicitly.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class AuthorizationMethodSlowTest extends AbstractTransactionalDataSourceSpringContextTests {

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AnnotationQueries annotationQueries;

    @Autowired
    private EhCacheBasedAclCache ehCacheBasedAclCache;

    @Autowired
    private MutableAclService aclService;

    /**
     * A variable for Oracle <code>boolean</code> type
     */
    private final static int ORACLE_TRUE = 1;

    /**
     * Name of the stored procedure (see class javadoc)
     */
    private final static String SQL_PROCEDURE_NAME = "UNIT_TEST_PROCEDURE";

    @Override
    protected String getConfigPath() {
        return "authorizationTestApplicationContext.xml";
    }

    @Override
    protected void onTearDownInTransaction() throws AnnotationQueriesException {

        //Reset ACL cache (Necessary before running all Tests at once)
        final DccAnnotationNote dccAnnotationNote =  annotationQueries.getAnnotationNoteById(1L);
        ehCacheBasedAclCache.evictFromCache(new DccAnnotationNoteRetrievalStrategy().getObjectIdentity(dccAnnotationNote));
    }

    @Override
    protected void onTearDownAfterTransaction() throws Exception {

        //Drop the procedure if it exists
        //Warning: It needs to be done outside of the transaction, otherwise data entered with the procedure will not rollback
        final int procedureCount = jdbcTemplate.queryForInt("select count(OBJECT_NAME) from USER_PROCEDURES where OBJECT_NAME = '" + SQL_PROCEDURE_NAME + "'");
        if(procedureCount == 1) { //The procedure exists, it needs to be dropped
            jdbcTemplate.execute("drop procedure " + SQL_PROCEDURE_NAME);
        }

        //Reset Authentication object for the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    /**
     * Creating and calling the procedure that will do the ACL data inserts (needed because of trigger-generated primary keys: see class javadoc)
     *
     * Since there is no rollback for inserts done through regular SQL statements <b>BEFORE</b> a stored procedure is called,
     * all inserts are done in the procedure even though not all tables have trigger-generated primary keys.
     *
     * @param permissionMask the permission given to the domain object instance (DccAnnotationNote in this case)
     */
    private void populateTables(final Integer permissionMask) {

        final String procedureCreationSQL = "create or replace\n"
                + "procedure " + SQL_PROCEDURE_NAME + "(permissionMask IN INTEGER)\n"
                + "as\n"
                + "  acl_sid_id number;\n"
                + "  acl_class_id number;\n"
                + "  acl_object_identity_id number;\n"

                + "  ace_order number;\n"
                + "  disease_id number;\n"
                + "  item_type_id number;\n"
                + "  annotation_category_id number;\n"
                + "  annotation_category_type_id number;\n"
                + "  annotation_id number;\n"
                + "  annotation_item_id number;\n"
                + "  annotation_note_id number;\n"
                + "  object_id_identity number;\n"
                + "begin\n"
                + "  ace_order := 0;\n"
                + "  disease_id := 1;\n"
                + "  item_type_id := 1;\n"
                + "  annotation_category_id := 1;\n"
                + "  annotation_category_type_id := 1;\n"
                + "  annotation_id := 1;\n"
                + "  annotation_item_id := 1;\n"
                + "  annotation_note_id := 1;\n"
                + "  object_id_identity := annotation_note_id;\n"

                // Disease
                + "  insert into disease(disease_id, disease_abbreviation, disease_name, active) values(disease_id, 'DIS1', 'disease 1', " + ORACLE_TRUE + ");\n"

                //Role-based Authorization
                + "  insert into users (username, password, enabled) values ('testuser', 'zzz', " + ORACLE_TRUE + ");\n"
                + "  insert into authorities (username, authority) values ('testuser', 'ROLE_TESTUSER');\n"

                //Domain object
                + "  insert into annotation_classification (annotation_classification_id, classification_display_name) values (1, 'Notification');\n"
                + "  insert into annotation_item_type (item_type_id, type_display_name, type_description) values (item_type_id, 'Patient', 'Patient');\n"
                + "  insert into annotation_category (annotation_category_id, category_display_name, category_description, annotation_classification_id) "
                + "values (annotation_category_id, 'withdrew consent', 'withdrew consent for study', 1);\n"
                + "  insert into annotation_category_item_type (annotation_category_type_id, annotation_category_id, item_type_id) "
                + "values (annotation_category_type_id, annotation_category_id, item_type_id);\n"
                + "  insert into annotation (annotation_id, annotation_category_id, entered_by, entered_date) "
                + "values (annotation_id, annotation_category_id, 'test', to_date('2010/01/01 00:01:01', 'yyyy/mm/dd hh24:mi:ss'));\n"
                + "  insert into annotation_note (annotation_note_id, annotation_id, note, entered_by, entered_date) "
                + "values (annotation_note_id, annotation_id, 'testnote', 'testuser', to_date('2010/01/01 00:01:01', 'yyyy/mm/dd hh24:mi:ss'));\n"
                + "  insert into annotation_item (annotation_item_id, annotation_id, item_type_id, annotation_item, disease_id) "
                + "values (annotation_item_id, annotation_id, item_type_id, 'TCGA-21-2011', disease_id);\n"

                //ACL-based Authorization
                + "  insert into acl_sid (principal, sid) values (" + ORACLE_TRUE + ", 'testuser') returning id into acl_sid_id;\n"
                + "  insert into acl_class (class) values ('gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationNote') returning id into acl_class_id;\n"
                + "  insert into acl_object_identity (object_id_class, object_id_identity, owner_sid, entries_inheriting) "
                + "values (acl_class_id, object_id_identity, acl_sid_id, " + ORACLE_TRUE + ") returning id into acl_object_identity_id;\n"
                + "  insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) "
                + "values (acl_object_identity_id, ace_order, acl_sid_id, permissionMask, " + ORACLE_TRUE + ", " + ORACLE_TRUE + ", " + ORACLE_TRUE + ");\n"
                + "end;"
                ;

        jdbcTemplate.execute(procedureCreationSQL); //Create the procedure
        jdbcTemplate.update("call " + SQL_PROCEDURE_NAME + "(?)", new Object[]{permissionMask});//Call the procedure
    }

    @Test
    public void testEditAnnotationNoteWithAuthority() throws AnnotationQueriesException, BeanException {

        //Populate tables giving the user WRITE permission on the domain object
        final int permissionMask = BasePermission.WRITE.getMask();
        populateTables(permissionMask);

        //Add role-based authority for the user
        final String roleBasedSql = "insert into authorities (username, authority) values ('testuser', 'ROLE_ANNOTATION_EDITOR')";
        jdbcTemplate.update(roleBasedSql);

        //Set up Authentication object for the SecurityContext
        final UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "zzz", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            final Long dccAnnotationNoteId = 1L;
            final DccAnnotationNote dccAnnotationNote = annotationQueries.getAnnotationNoteById(dccAnnotationNoteId);
            annotationQueries.editAnnotationNote(dccAnnotationNoteId, dccAnnotationNote, "new text", "editor");

        } catch (final AccessDeniedException x) {
            Assert.fail("org.springframework.security.AccessDeniedException should not have been raised");
        }
    }

    @Test
    public void testEditAnnotationNoteWithNoRoleBasedAuthority() throws AnnotationQueriesException, BeanException {

        //Populate tables giving the user WRITE permission on the domain object
        final int permissionMask = BasePermission.WRITE.getMask();
        populateTables(permissionMask);

        //Add role-based authority for the user
        final String roleBasedSql = "insert into authorities (username, authority) values ('testuser', 'ROLE_WRONG')";
        jdbcTemplate.update(roleBasedSql);

        //Set up Authentication object for the SecurityContext
        final UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "zzz", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            final Long dccAnnotationNoteId = 1L;
            final DccAnnotationNote dccAnnotationNote = annotationQueries.getAnnotationNoteById(dccAnnotationNoteId);
            annotationQueries.editAnnotationNote(dccAnnotationNoteId, dccAnnotationNote, "new text", "editor");
            Assert.fail("org.springframework.security.AccessDeniedException should have been raised");

        } catch (final AccessDeniedException x) {
            //Expected
        }
    }

    @Test
    public void testEditAnnotationNoteWithNoACLBasedAuthority() throws AnnotationQueriesException, BeanException {

        //Populate tables giving the user READ permission on the domain object instead of expected WRITE to have the ACL-based voter deny permission
        final int permissionMask = BasePermission.READ.getMask();
        populateTables(permissionMask);

        //Add role-based authority for the user
        final String roleBasedSql = "insert into authorities (username, authority) values ('testuser', 'ROLE_ANNOTATION_NOTE_EDITOR')";
        jdbcTemplate.update(roleBasedSql);

        //Set up Authentication object for the SecurityContext
        final UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "zzz", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            final Long dccAnnotationNoteId = 1L;
            final DccAnnotationNote dccAnnotationNote = annotationQueries.getAnnotationNoteById(dccAnnotationNoteId);
            annotationQueries.editAnnotationNote(dccAnnotationNoteId, dccAnnotationNote, "new text", "editor");
            Assert.fail("org.springframework.security.AccessDeniedException should have been raised");

        } catch (final AccessDeniedException x) {
            //Expected
        }
    }

    @Test
    public void testAddNewAnnotationNoteWithNoAuthority() throws AnnotationQueriesException, BeanException {

        //Populate tables giving the user WRITE permission on the domain object
        final int permissionMask = BasePermission.WRITE.getMask();
        populateTables(permissionMask);

        //Set up Authentication object for the SecurityContext
        final UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "zzz", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            final DccAnnotationNote dccAnnotationNote = new DccAnnotationNote();
            dccAnnotationNote.setNoteText("note text");
            dccAnnotationNote.setAddedBy("testuser");

            final Long dccAnnotationId = 1L;
            annotationQueries.addNewAnnotationNote(dccAnnotationId, dccAnnotationNote);
            Assert.fail("org.springframework.security.AccessDeniedException should have been raised");

        } catch (final AccessDeniedException x) {
            //Expected
        }
    }

    @Test
    public void testAddNewAnnotationNoteWithAuthority() throws AnnotationQueriesException, BeanException {

        //Populate tables giving the user WRITE permission on the domain object
        final int permissionMask = BasePermission.WRITE.getMask();
        populateTables(permissionMask);

        //Add authority for 'testuser'
        final String sql = "insert into authorities (username, authority) values ('testuser', 'ROLE_ANNOTATION_CREATOR')";
        jdbcTemplate.update(sql);

        //Set up Authentication object for the SecurityContext
        final UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "zzz", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            final DccAnnotationNote dccAnnotationNote = new DccAnnotationNote();
            dccAnnotationNote.setNoteText("note text");
            dccAnnotationNote.setAddedBy("testuser");

            final Long dccAnnotationId = 1L;
            annotationQueries.addNewAnnotationNote(dccAnnotationId, dccAnnotationNote);

        } catch (final AccessDeniedException x) {
            Assert.fail("org.springframework.security.AccessDeniedException should not have been raised");
        }
    }

    @Test
    public void testAddNewAnnotationWithNoAuthority() throws AnnotationQueriesException, BeanException {

        //Populate tables giving the user WRITE permission on the domain object
        final int permissionMask = BasePermission.WRITE.getMask();
        populateTables(permissionMask);

        //Set up Authentication object for the SecurityContext
        final UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "zzz", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            //Set up the DccAnnotation to be added
            final DccAnnotationCategory dccAnnotationCategory = new DccAnnotationCategory();
            dccAnnotationCategory.setCategoryId(1L);

            final DccAnnotationItemType dccAnnotationItemType = new DccAnnotationItemType();
            dccAnnotationItemType.setItemTypeId(1L);

            final Date now = new Date();

            final DccAnnotation dccAnnotation = new DccAnnotation();
            dccAnnotation.setCreatedBy("testuser");
            dccAnnotation.setDateCreated(now);
            dccAnnotation.setAnnotationCategory(dccAnnotationCategory);
            
            final Tumor disease = new Tumor();
            disease.setTumorId(1);
            
            final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();
            dccAnnotationItem.setItem("test");
            dccAnnotationItem.setItemType(dccAnnotationItemType);
            dccAnnotationItem.setDisease(disease);
            
            dccAnnotation.addItem(dccAnnotationItem);
            dccAnnotation.addNote("txt", "testuser", now);

            annotationQueries.addNewAnnotation(dccAnnotation);
            Assert.fail("org.springframework.security.AccessDeniedException should have been raised");

        } catch (final AccessDeniedException x) {
            //Expected
        }
    }

    @Test
    public void testAddNewAnnotationWithAuthority() throws AnnotationQueriesException, BeanException {

        //Populate tables giving the user WRITE permission on the domain object
        final int permissionMask = BasePermission.WRITE.getMask();
        populateTables(permissionMask);

        //Add authority for 'testuser'
        final String sql1 = "insert into authorities (username, authority) values ('testuser', 'ROLE_ANNOTATION_CREATOR')";
        final String sql2 = "insert into authorities (username, authority) values ('testuser', 'ROLE_ANNOTATION_ITEM_CREATOR')";
        final String sql3 = "insert into authorities (username, authority) values ('testuser', 'ROLE_ANNOTATION_NOTE_CREATOR')";
        final String[] sqlArray = {sql1, sql2, sql3};
        jdbcTemplate.batchUpdate(sqlArray);

        //Set up Authentication object for the SecurityContext
        final UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "zzz", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            //Set up the DccAnnotation to be added
            final DccAnnotationCategory dccAnnotationCategory = new DccAnnotationCategory();
            dccAnnotationCategory.setCategoryId(1L);

            final DccAnnotationItemType dccAnnotationItemType = new DccAnnotationItemType();
            dccAnnotationItemType.setItemTypeId(1L);

            final Date now = new Date();

            final DccAnnotation dccAnnotation = new DccAnnotation();
            
            final Tumor disease = new Tumor();
            disease.setTumorId(1);
            
            final DccAnnotationItem dccAnnotationItem = new DccAnnotationItem();
            dccAnnotationItem.setItem("TCGA-01-2011");
            dccAnnotationItem.setItemType(dccAnnotationItemType);
            dccAnnotationItem.setDisease(disease);
            
            dccAnnotation.addItem(dccAnnotationItem);
            dccAnnotation.setCreatedBy("testuser");
            dccAnnotation.setDateCreated(now);
            dccAnnotation.setAnnotationCategory(dccAnnotationCategory);
            dccAnnotation.addNote("txt", "testuser", now);

            annotationQueries.addNewAnnotation(dccAnnotation);

        } catch (final AccessDeniedException x) {
            Assert.fail("org.springframework.security.AccessDeniedException should not have been raised");
        }
    }
}
