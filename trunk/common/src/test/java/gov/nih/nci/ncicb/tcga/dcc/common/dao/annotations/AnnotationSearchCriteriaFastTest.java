/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations;

import gov.nih.nci.ncicb.tcga.dcc.common.security.impl.SecurityUtilImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.userdetails.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Class test for the annotation search criteria bean special methods
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class AnnotationSearchCriteriaFastTest {

    private List<Object> params;
    private AnnotationSearchCriteria criteria;
    private static final Long CATEGORY_ID = 100L;
    private static final Long ITEM_TYPE_ID = 200L;
    private static final String ITEM = "ITEM";
    private static final String KEYWORD = "key";

    private AnnotationSearchCriteria annotationSearchCriteria;
    private String username = "usernameTest";
    private String password = "passwordTest";
    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonLocked = true;

    @Before
    public void setup() {
        params = new ArrayList<Object>();
        criteria = new AnnotationSearchCriteria();

        annotationSearchCriteria = new AnnotationSearchCriteria();
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void testIsAdministratorFalse() throws Exception {
        final GrantedAuthority[] authorities = {new GrantedAuthorityImpl("ROLE_X"), new GrantedAuthorityImpl("ROLE_Y")};
        final User principal = new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        final Authentication authentication = new UsernamePasswordAuthenticationToken(principal, password, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertFalse(SecurityUtilImpl.isAdministrator());
    }

    @Test
    public void testIsAdministratorTrue() throws Exception {
        final GrantedAuthority[] authorities = {new GrantedAuthorityImpl("ROLE_X"),
                new GrantedAuthorityImpl("ROLE_ANNOTATIONS_ADMINISTRATOR")};
        final User principal = new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        final Authentication authentication = new UsernamePasswordAuthenticationToken(principal, password, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertTrue(SecurityUtilImpl.isAdministrator());
    }

    @Test
    public void testBuildQueryAdminByAnnotator() throws Exception {
        List<Object> list = new ArrayList<Object>();
        annotationSearchCriteria.setCurated(false);
        annotationSearchCriteria.setAnnotatorUsername(username);
        final GrantedAuthority[] authorities = {new GrantedAuthorityImpl("ROLE_X"),
                new GrantedAuthorityImpl("ROLE_ANNOTATIONS_ADMINISTRATOR")};
        final User principal = new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        final Authentication authentication = new UsernamePasswordAuthenticationToken(principal, password, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertEquals("select distinct annotation.annotation_id from annotation where annotation.entered_by=? and curated=? and rescinded=0",
                annotationSearchCriteria.buildQuery(list));
        assertEquals(2,list.size());
        assertEquals("usernameTest",list.get(0));
        assertEquals(0,list.get(1));
    }

    @Test
    public void testBuildQueryAdmin() throws Exception {
        List<Object> list = new ArrayList<Object>();
        annotationSearchCriteria.setCurated(false);
        final GrantedAuthority[] authorities = {new GrantedAuthorityImpl("ROLE_X"),
                new GrantedAuthorityImpl("ROLE_ANNOTATIONS_ADMINISTRATOR")};
        final User principal = new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        final Authentication authentication = new UsernamePasswordAuthenticationToken(principal, password, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertEquals("select distinct annotation.annotation_id from annotation where curated=? and rescinded=0",
                annotationSearchCriteria.buildQuery(list));
        assertEquals(1,list.size());
        assertEquals(0,list.get(0));
    }

    @Test
    public void testBuildQueryNonAdmin() throws Exception {
        List<Object> list = new ArrayList<Object>();
        annotationSearchCriteria.setAnnotatorUsername(username);
        final GrantedAuthority[] authorities = {new GrantedAuthorityImpl("ROLE_X")};
        final User principal = new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        final Authentication authentication = new UsernamePasswordAuthenticationToken(principal, password, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertEquals("select distinct annotation.annotation_id from annotation where (curated=1 and annotation.entered_by=?) and rescinded=0",
                annotationSearchCriteria.buildQuery(list));
        assertEquals(1,list.size());
        assertEquals("usernameTest",list.get(0));
    }

    @Test
    public void testBuildQueryItemOnly() {
        criteria.setItem(ITEM);
        Assert.assertEquals("select distinct annotation.annotation_id from annotation, annotation_item " +
                "where curated=1 and annotation.annotation_id=annotation_item.annotation_id and annotation_item like ? and rescinded=0",
                criteria.buildQuery(params));
        Assert.assertEquals(1, params.size());
        Assert.assertEquals(ITEM + "%", params.get(0));
    }

    @Test
    public void testBuildQueryMultipleItems() {
        criteria.setItem("A,B");
        Assert.assertEquals("select distinct annotation.annotation_id from annotation, annotation_item " +
                "where curated=1 and annotation.annotation_id=annotation_item.annotation_id and " +
                "(annotation_item like ? or annotation_item like ?) and rescinded=0",
                criteria.buildQuery(params));
        Assert.assertEquals(2, params.size());
        Assert.assertEquals("A%", params.get(0));
        Assert.assertEquals("B%", params.get(1));
    }

    @Test
    public void testBuildQueryMultipleItemsExact() {
        criteria.setItem("A,B");
        criteria.setExact(true);
        Assert.assertEquals("select distinct annotation.annotation_id from annotation, annotation_item " +
                "where curated=1 and annotation.annotation_id=annotation_item.annotation_id and " +
                "(annotation_item = ? or annotation_item = ?) and rescinded=0",
                criteria.buildQuery(params));
        Assert.assertEquals(2, params.size());
        Assert.assertEquals("A", params.get(0));
        Assert.assertEquals("B", params.get(1));
    }

    @Test
    public void testBuildQueryMultipleItemsSpaces() {
        criteria.setItem("C, D");
        Assert.assertEquals("select distinct annotation.annotation_id from annotation, annotation_item " +
                "where curated=1 and annotation.annotation_id=annotation_item.annotation_id and " +
                "(annotation_item like ? or annotation_item like ?) and rescinded=0",
                criteria.buildQuery(params));
        Assert.assertEquals(2, params.size());
        Assert.assertEquals("C%", params.get(0));
        Assert.assertEquals("D%", params.get(1));
    }

    @Test
    public void testBuildQueryExactItem() {
        criteria.setItem(ITEM);
        criteria.setExact(true);
        Assert.assertEquals("select distinct annotation.annotation_id from annotation, annotation_item " +
                "where curated=1 and annotation.annotation_id=annotation_item.annotation_id and annotation_item = ? and rescinded=0",
                criteria.buildQuery(params));
        Assert.assertEquals(1, params.size());
        Assert.assertEquals(ITEM, params.get(0));
    }

    @Test
    public void testBuildQueryEmptyCriteria() {
        Assert.assertEquals("select distinct annotation.annotation_id from annotation where curated=1 and rescinded=0", criteria.buildQuery(params));
        Assert.assertEquals(0, params.size());
    }

    @Test
    public void testBuildQueryCategoryOnly() {
        criteria.setCategoryId(CATEGORY_ID);
        Assert.assertEquals("select distinct annotation.annotation_id from annotation where curated=1 and annotation.annotation_category_id=? and rescinded=0", criteria.buildQuery(params));
        Assert.assertEquals(100L, params.get(0));
    }

    @Test
    public void testBuildQueryItemTypeOnly() {
        criteria.setItemTypeId(ITEM_TYPE_ID);
        Assert.assertEquals("select distinct annotation.annotation_id from annotation, annotation_item " +
                "where curated=1 and item_type_id=? and annotation.annotation_id=annotation_item.annotation_id and rescinded=0",
                criteria.buildQuery(params));
        Assert.assertEquals(200L, params.get(0));
    }

    @Test
    public void testBuildQueryKeywordOnly() {
        criteria.setKeyword(KEYWORD);
        Assert.assertEquals("select distinct annotation.annotation_id from annotation, annotation_note " +
                "where curated=1 and upper(note) like ? and annotation.annotation_id=annotation_note.annotation_id and rescinded=0",
                criteria.buildQuery(params));
        Assert.assertEquals("%" + KEYWORD.toUpperCase() + "%", params.get(0));
    }

    @Test
    public void testBuildQueryDiseaseOnly() {
        criteria.setDiseaseId(3);
        Assert.assertEquals("select distinct annotation.annotation_id from annotation, annotation_item " +
                "where curated=1 and disease_id=? and annotation.annotation_id=annotation_item.annotation_id and rescinded=0", criteria.buildQuery(params));
        Assert.assertEquals(1, params.size());
        Assert.assertEquals(3, params.get(0));
    }

    @Test
    public void testBuildQueryAuthenticatedUserSearchingItsOwnAnnotations() {

        final String username = "someoneAwesome";

        criteria.setAuthenticatedUsername(username);
        criteria.setAnnotatorUsername(username);

        Assert.assertEquals("select distinct annotation.annotation_id from annotation where annotation.entered_by=? and rescinded=0", criteria.buildQuery(params));
        Assert.assertEquals(1, params.size());
    }

    @Test
    public void testBuildQueryAuthenticatedUserSearchingOthersAnnotations() {

        final String authenticatedUsername = "someoneAwesome";
        final String annotatorUsername = "someoneAwesomer";
        final String expectedQuery = "select distinct annotation.annotation_id from annotation " +
                "where (curated=1 and annotation.entered_by=?) and rescinded=0";

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedQuery, annotatorUsername);
    }

    @Test
    public void testBuildQueryAuthenticatedUserSearchingAllAnnotators() {

        final String authenticatedUsername = "someoneAwesome";
        final String annotatorUsername = null;
        final String expectedQuery = "select distinct annotation.annotation_id from annotation " +
                "where (curated=1 or annotation.entered_by=?) and rescinded=0";

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedQuery, authenticatedUsername);
    }

    @Test
    public void testBuildQueryAuthenticatedUserSearchingAllAnnotatorsWithEmptyString() {

        final String authenticatedUsername = "someoneAwesome";
        final String annotatorUsername = "";
        final String expectedQuery = "select distinct annotation.annotation_id from annotation " +
                "where (curated=1 or annotation.entered_by=?) and rescinded=0";

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedQuery, authenticatedUsername);
    }

    @Test
    public void testBuildQueryNonAuthenticatedUserSearchingAnnotations() {

        final String authenticatedUsername = SecurityUtilImpl.NOT_AUTHENTICATED;
        final String annotatorUsername = "someoneAwesome";
        final String expectedQuery = "select distinct annotation.annotation_id from annotation where (curated=1 and annotation.entered_by=?) and rescinded=0";

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedQuery, annotatorUsername);
    }

    @Test
    public void testBuildQueryNonAuthenticatedUserNullSearchingAnnotations() {

        final String authenticatedUsername = null;
        final String annotatorUsername = "someoneAwesome";
        final String expectedQuery = "select distinct annotation.annotation_id from annotation where (curated=1 and annotation.entered_by=?) and rescinded=0";

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedQuery, annotatorUsername);
    }

    @Test
    public void testBuildQueryNonAuthenticatedUserEmptyStringSearchingAnnotations() {

        final String authenticatedUsername = "";
        final String annotatorUsername = "someoneAwesome";
        final String expectedQuery = "select distinct annotation.annotation_id from annotation where (curated=1 and annotation.entered_by=?) and rescinded=0";

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedQuery, annotatorUsername);
    }

    @Test
    public void testBuildQueryNonAuthenticatedUserSearchingAllAnnotators() {

        final String authenticatedUsername = SecurityUtilImpl.NOT_AUTHENTICATED;
        final String annotatorUsername = null;
        final String expectedQuery = "select distinct annotation.annotation_id from annotation where curated=1 and rescinded=0";

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedQuery);
    }

    @Test
    public void testBuildQueryNonAuthenticatedUserNullSearchingAllAnnotators() {

        final String authenticatedUsername = null;
        final String annotatorUsername = null;
        final String expectedQuery = "select distinct annotation.annotation_id from annotation where curated=1 and rescinded=0";

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedQuery);
    }

    @Test
    public void testBuildQueryNonAuthenticatedUserEmptyStringSearchingAllAnnotators() {

        final String authenticatedUsername = "";
        final String annotatorUsername = null;
        final String expectedQuery = "select distinct annotation.annotation_id from annotation where curated=1 and rescinded=0";

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedQuery);
    }

    @Test
    public void testBuildQueryNonAuthenticatedUserSearchingAllAnnotatorsWithEmptyString() {

        final String authenticatedUsername = SecurityUtilImpl.NOT_AUTHENTICATED;
        final String annotatorUsername = "";
        final String expectedQuery = "select distinct annotation.annotation_id from annotation where curated=1 and rescinded=0";

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedQuery);
    }

    @Test
    public void testBuildQueryNonAuthenticatedUserNullSearchingAllAnnotatorsWithEmptyString() {

        final String authenticatedUsername = null;
        final String annotatorUsername = "";
        final String expectedQuery = "select distinct annotation.annotation_id from annotation where curated=1 and rescinded=0";

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedQuery);
    }

    @Test
    public void testBuildQueryNonAuthenticatedUserEmptyStringSearchingAllAnnotatorsWithEmptyString() {

        final String authenticatedUsername = "";
        final String annotatorUsername = "";
        final String expectedQuery = "select distinct annotation.annotation_id from annotation where curated=1 and rescinded=0";

        checkSearchByAnnotator(authenticatedUsername, annotatorUsername, expectedQuery);
    }

    @Test
    public void testWithRowLimit() {
        criteria.setRowLimit(2);
        Assert.assertEquals("select distinct annotation.annotation_id from annotation where curated=1 and rownum<=? and rescinded=0", criteria.buildQuery(params));
        Assert.assertEquals(1, params.size());
        Assert.assertEquals(2, params.get(0));
    }

    // test with all criteria set
    @Test
    public void testBuildQuery() {
        // all criteria set
        criteria.setItem(ITEM);
        criteria.setCategoryId(CATEGORY_ID);
        criteria.setItemTypeId(ITEM_TYPE_ID);
        criteria.setKeyword(KEYWORD);
        criteria.setDiseaseId(3);

        Assert.assertEquals("select distinct annotation.annotation_id from annotation, annotation_item, annotation_note " +
                "where curated=1 and annotation.annotation_id=annotation_item.annotation_id and annotation_item like ? " +
                "and annotation.annotation_category_id=? and item_type_id=? and upper(note) like ? and " +
                "annotation.annotation_id=annotation_note.annotation_id and disease_id=? and rescinded=0",
                criteria.buildQuery(params));

        Assert.assertEquals(5, params.size());
        Assert.assertEquals(ITEM + "%", params.get(0));
        Assert.assertEquals(CATEGORY_ID, params.get(1));
        Assert.assertEquals(ITEM_TYPE_ID, params.get(2));
        Assert.assertEquals("%" + KEYWORD.toUpperCase() + "%", params.get(3));
        Assert.assertEquals(3, params.get(4));
    }

    @Test
    public void testBuildQueryByClassification() {
        criteria.setClassificationId(3L);
        Assert.assertEquals("select distinct annotation.annotation_id from annotation, annotation_category " +
                "where curated=1 and annotation_classification_id=? " +
                "and annotation.annotation_category_id=annotation_category.annotation_category_id and rescinded=0",
                criteria.buildQuery(params));
        Assert.assertEquals(3L, params.get(0));
    }

    @Test
    public void testBuildQueryCreatedSinceDate() {
        final Date now = new Date();
        criteria.setEnteredAfter(now);
        Assert.assertEquals("select distinct annotation.annotation_id from annotation " +
                "where curated=1 and entered_date>? and rescinded=0",
                criteria.buildQuery(params));
        Assert.assertEquals(now, params.get(0));
    }

    @Test
    public void testBuildQueryRescindedAndNotRescinded() {
        criteria.setIncludeRescinded(true);
        assertEquals("select distinct annotation.annotation_id from annotation where curated=1", criteria.buildQuery(params));
        assertEquals(0, params.size());
    }

    @Test
    public void testBuildQueryNotRescinded() {
        criteria.setIncludeRescinded(false);
        assertEquals("select distinct annotation.annotation_id from annotation where curated=1 and rescinded=0", criteria.buildQuery(params));
        assertEquals(0, params.size());
    }

    /**
     * Run a search with the given authenticated username and annotator username
     * and check assertions.
     *
     * @param authenticatedUsername the authenticated username
     * @param annotatorUsername the annotator username
     * @param expectedQuery the expected query
     * @param expectedParams parameters expected for the query
     */
    private void checkSearchByAnnotator(final String authenticatedUsername,
                                        final String annotatorUsername,
                                        final String expectedQuery,
                                        final String... expectedParams) {

        criteria.setAuthenticatedUsername(authenticatedUsername);
        criteria.setAnnotatorUsername(annotatorUsername);

        Assert.assertEquals(expectedQuery, criteria.buildQuery(params));
        Assert.assertEquals(expectedParams.length, params.size());

        for (int i=0; i<expectedParams.length; i++) {
            Assert.assertEquals(expectedParams[i], params.get(i));
        }
    }

}//End of Test
