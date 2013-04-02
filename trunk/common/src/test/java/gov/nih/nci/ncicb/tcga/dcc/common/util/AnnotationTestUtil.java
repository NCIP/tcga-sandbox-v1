/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationSearchCriteria;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Ignore;

/**
 * Utils for annotation tests.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Ignore
public class AnnotationTestUtil {

    /**
     * Static method returning a Matcher that matches expected annotation search criteria
     *
     * @param item the expected item
     * @param classificationId
     *@param categoryId the expected category id
     * @param itemTypeId the expected item type id
     * @param keyword the expected keyword
     * @param authenticatedUsername the expected authenticated username
     * @param annotatorUsername      @return a Matcher that will match AnnotationSearchCriteria to the expected values
     */
    public static Matcher<AnnotationSearchCriteria> criteriaMatching(
            final String item,
            final Long classificationId, final Long categoryId,
            final Long itemTypeId,
            final String keyword,
            final String authenticatedUsername,
            final String annotatorUsername) {

        return new TypeSafeMatcher<AnnotationSearchCriteria>() {
            @Override
            public boolean matchesSafely(final AnnotationSearchCriteria criteria) {
                // returns true if the criteria object matches the parameters
                return ((item == null && criteria.getItem() == null) ||
                        (item != null && criteria.getItem() != null && item.equals(criteria.getItem()))) &&
                        ((classificationId == null && criteria.getClassificationId() == null) ||
                        (classificationId != null && criteria.getClassificationId() != null &&
                        classificationId.equals(criteria.getClassificationId()))) &&
                        ((categoryId == null && criteria.getCategoryId() == null) ||
                                (categoryId != null && criteria.getCategoryId() != null &&
                                        categoryId.equals(criteria.getCategoryId()))) &&
                        ((itemTypeId == null && criteria.getItemTypeId() == null) ||
                                (itemTypeId != null && criteria.getItemTypeId() != null &&
                                        itemTypeId.equals(criteria.getItemTypeId()))) &&
                        ((keyword == null && criteria.getKeyword() == null) ||
                                (keyword != null && criteria.getKeyword() != null &&
                                        keyword.equals(criteria.getKeyword()))) &&
                        ((authenticatedUsername == null && criteria.getAuthenticatedUsername() == null) ||
                                authenticatedUsername != null && criteria.getAuthenticatedUsername() != null &&
                                        authenticatedUsername.equals(criteria.getAuthenticatedUsername())) &&
                        ((annotatorUsername == null && criteria.getAnnotatorUsername() == null) ||
                                annotatorUsername != null && criteria.getAnnotatorUsername() != null &&
                                        annotatorUsername.equals(criteria.getAnnotatorUsername()))
                        ;
            }

            public void describeTo(final Description description) {
                description.appendText("matches criteria");
            }
        };
    }
}
