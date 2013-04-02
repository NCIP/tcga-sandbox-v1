/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations;

import gov.nih.nci.ncicb.tcga.dcc.common.security.impl.SecurityUtilImpl;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Class holding search criteria.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class AnnotationSearchCriteria {

    public static final String ANNOTATION_ITEM_SEPARATOR = ",";

    private String item;
    private Long categoryId;
    private Long classificationId;
    private Long itemTypeId;
    private String keyword;
    private boolean exact;
    private Integer diseaseId;
    private Boolean curated;
    private Boolean includeRescinded = false; //default is to request for all annotations - rescinded and not

    /**
     * The username of the authenticated user
     */
    private String authenticatedUsername = null;

    /**
     * The username of the user who created the annotation
     */
    private String annotatorUsername;

    private Integer rowLimit;
    private Date enteredAfter;

    public AnnotationSearchCriteria() {
        item = null;
        categoryId = null;
        itemTypeId = null;
    }

    public AnnotationSearchCriteria(
            final String item, final Long categoryId, final Long itemTypeId, final String keyword) {
        this.item = item;
        this.categoryId = categoryId;
        this.itemTypeId = itemTypeId;
        this.keyword = keyword;
    }

    public String getItem() {
        return item;
    }

    public void setItem(final String item) {
        this.item = item;
    }

    /**
     * Builds a sql query with the given parameters.
     *
     * Note on authenticatedUsername and annotatorUsername parameters:
     *
     * - annotatorUsername == blank -> annotations from all users
     * - annotatorUsername != blank -> all annotations from that user
     * - authenticatedUsername is not authenticated -> curated annotations
     * - authenticatedUsername is authenticated and annotatorUsername != authenticatedUsername -> curated annotations
     * - authenticatedUsername is authenticated and annotatorUsername == authenticatedUsername -> curated and non curated annotations
     *
     * @param params the parameters
     * @return the sql query
     */
    public String buildQuery(final List<Object> params) {

        final Set<String> tables = new LinkedHashSet<String>();
        final Set<String> joinClauses = new LinkedHashSet<String>(); // preserve the order

        final boolean isAuthenticated = !(StringUtils.isBlank(getAuthenticatedUsername()) ||
                SecurityUtilImpl.NOT_AUTHENTICATED.equals(getAuthenticatedUsername()));
        final boolean isSearchByAnnotator = !(StringUtils.isBlank(getAnnotatorUsername()));

        if (SecurityUtilImpl.isAdministrator()) {
            if (isSearchByAnnotator) {
                joinClauses.add("annotation.entered_by=?");
                params.add(getAnnotatorUsername());
            }
            if (isCurated() != null) {
                joinClauses.add("curated=?");
                params.add((isCurated() ? 1 : 0));
            }
        } else {
            if (isSearchByAnnotator) {

                if (isAuthenticated) {

                    if (getAuthenticatedUsername().equals(getAnnotatorUsername())) { //annotator == authenticated
                        // All annotations (curated and non curated) from authenticated
                        joinClauses.add("annotation.entered_by=?");
                        params.add(getAnnotatorUsername());

                    } else { //annotator != authenticated
                        // Get curated and entered by annotator
                        joinClauses.add("(curated=1 and annotation.entered_by=?)");
                        params.add(getAnnotatorUsername());
                    }
                } else {// Not authenticated
                    // Curated from the annotator
                    joinClauses.add("(curated=1 and annotation.entered_by=?)");
                    params.add(getAnnotatorUsername());

                }
            } else {

                if (isAuthenticated) {
                    // still get own pending annotations plus all curated
                    joinClauses.add("(curated=1 or annotation.entered_by=?)");
                    params.add(getAuthenticatedUsername());
                } else {
                    // not authenticated -- get all curated annotations
                    joinClauses.add("curated=1");
                }

            }
        }
        if (getItem() != null && getItem().trim().length() > 0) {
            tables.add("annotation_item");
            joinClauses.add("annotation.annotation_id=annotation_item.annotation_id");
            String[] items = getItem().split(ANNOTATION_ITEM_SEPARATOR);
            StringBuilder itemClause = new StringBuilder();
            if (items.length > 1) {
                itemClause.append("(");
            }
            boolean first = true;
            for (String anItem : items) {
                anItem = anItem.trim();
                if (! first) {
                    itemClause.append(" or ");
                }
                first = false;

                if (exact) {
                    itemClause.append("annotation_item = ?");
                    params.add(anItem);
                } else {
                    itemClause.append("annotation_item like ?");
                    params.add(anItem + "%");
                }
            }
            if (items.length > 1) {
                itemClause.append(")");
            }
            joinClauses.add(itemClause.toString());
        }
        if (getCategoryId() != null && getCategoryId() > 0) {
            joinClauses.add("annotation.annotation_category_id=?");
            params.add(getCategoryId());
        }
        if (getClassificationId() != null && getClassificationId() > 0) {
            tables.add("annotation_category");
            joinClauses.add("annotation_classification_id=?");
            joinClauses.add("annotation.annotation_category_id=annotation_category.annotation_category_id");
            params.add(getClassificationId());
        }
        if (getItemTypeId() != null && getItemTypeId() > 0) {
            tables.add("annotation_item");
            joinClauses.add("item_type_id=?");
            joinClauses.add("annotation.annotation_id=annotation_item.annotation_id");
            params.add(getItemTypeId());
        }
        if (getKeyword() != null && getKeyword().trim().length() > 0) {
            tables.add("annotation_note");
            joinClauses.add("upper(note) like ?");
            joinClauses.add("annotation.annotation_id=annotation_note.annotation_id");
            params.add("%" + getKeyword().toUpperCase() + "%");
        }
        if (getDiseaseId() != null) {
            tables.add("annotation_item");
            joinClauses.add("disease_id=?");
            joinClauses.add("annotation.annotation_id=annotation_item.annotation_id");
            params.add(diseaseId);
        }
        if (getRowLimit() != null && getRowLimit() > 0) {
            joinClauses.add("rownum<=?");
            params.add(getRowLimit());
        }
        if (getEnteredAfter() != null) {
            joinClauses.add("entered_date>?");
            params.add(getEnteredAfter());
        }
        if (!isIncludeRescinded()) { // specifically exclude rescinded items
            joinClauses.add("rescinded=0");
        }
        StringBuilder query = new StringBuilder("select distinct annotation.annotation_id from annotation");
        for (final String table : tables) {
            query.append(", ").append(table);
        }
        if (joinClauses.size() > 0) {
            query.append(" where ");
            boolean first = true;
            for (final String joinClause : joinClauses) {
                if (!first) {
                    query.append(" and ");
                }
                query.append(joinClause);
                first = false;
            }
        }
        return query.toString();
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(final Long itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setExact(final boolean exact) {
        this.exact = exact;
    }

    public void setDiseaseId(final Integer diseaseId) {
        this.diseaseId = diseaseId;
    }

    public Integer getDiseaseId() {
        return diseaseId;
    }

    public void setAuthenticatedUsername(final String username) {
        this.authenticatedUsername = username;
    }

    public String getAuthenticatedUsername() {
        return authenticatedUsername;
    }

    public String getAnnotatorUsername() {
        return annotatorUsername;
    }

    public void setAnnotatorUsername(final String annotatorUsername) {
        this.annotatorUsername = annotatorUsername;
    }

    public void setRowLimit(final Integer rowLimit) {
        this.rowLimit = rowLimit;
    }

    public Integer getRowLimit() {
        return rowLimit;
    }

    public Long getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(final Long classificationId) {
        this.classificationId = classificationId;
    }

    public void setEnteredAfter(final Date enteredAfter) {
        this.enteredAfter = enteredAfter;
    }

    public Date getEnteredAfter() {
        return enteredAfter;
    }

    public Boolean isCurated() {
        return curated;
    }

    public void setCurated(final Boolean curated) {
        this.curated = curated;
    }

    public Boolean isIncludeRescinded() {
        return includeRescinded;
    }

    public void setIncludeRescinded(final Boolean includeRescinded) {
        this.includeRescinded = includeRescinded;
    }

    public boolean equals(final Object o) {
        if (o instanceof AnnotationSearchCriteria) {
            // two search criteria are equal if they result in the same query
            final AnnotationSearchCriteria otherCriteria = (AnnotationSearchCriteria) o;
            final List<Object> params = new ArrayList<Object>(); // ignored but needed for call
            return (buildQuery(params).equals(otherCriteria.buildQuery(params)));
        } else {
            return false;
        }
    }
}
