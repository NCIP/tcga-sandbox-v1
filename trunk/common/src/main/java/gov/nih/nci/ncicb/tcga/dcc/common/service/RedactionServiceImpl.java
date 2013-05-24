/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.service;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.RedactionQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDHierarchyQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Implementation of RedactionService
 *
 * @author Shelley Alonso
 *         Last updated by: $Shelley Alonso$
 * @version $Rev$
 */

@Service
public class RedactionServiceImpl implements RedactionService {

    private static final String SUBJECT_WITHDREW_CONSENT = "Subject withdrew consent";

    @Resource (name = "redactionCommonQueries")
    private RedactionQueries redactionQueriesCommon;
    @Resource (name = "redactionDiseaseQueries")
    private RedactionQueries redactionQueriesDisease;
    @Autowired
    private UUIDHierarchyQueries uuidHierarchyQueries;
    @Autowired
    private CommonBarcodeAndUUIDValidator barcodeAndUUIDValidator;
    @Resource (name = "annotationQueries")
    private AnnotationQueries annotationQueries;

    /**
     * Redacts all children of a redacted item
     *
     * @param redactedItem the barcode or uuid of the participant being redacted
     */
    public void redact(final String redactedItem, final Long categoryId) {
        final String type = getItemType(redactedItem);
        final String categoryName = annotationQueries.getAnnotationCategoryNameForId(categoryId);
        boolean setToNotViewable = true;
        if (SUBJECT_WITHDREW_CONSENT.equals(categoryName)) {
            setToNotViewable = false;
        }

        final SqlParameterSource[] childUuids = SqlParameterSourceUtils.createBatch(
                uuidHierarchyQueries.getChildUUIDs(redactedItem, type).toArray());
        redactionQueriesCommon.redact(childUuids, setToNotViewable);
        redactionQueriesDisease.redact(childUuids, setToNotViewable);
    }

    /**
     * Rescinds the redaction for all children of a rescinded item
     *
     * @param rescindedItem the barcode or uuid of the participant being rescinded
     */
    public void rescind(final String rescindedItem) {
        final String type = getItemType(rescindedItem);
        final SqlParameterSource[] childUuids = SqlParameterSourceUtils.createBatch(
                uuidHierarchyQueries.getChildUUIDs(rescindedItem, type).toArray());
        redactionQueriesCommon.rescind(childUuids);
        redactionQueriesDisease.rescind(childUuids);
    }

    protected String getItemType(final String item) throws IllegalArgumentException {
        if (barcodeAndUUIDValidator.validateAnyBarcodeFormat(item)) {
            return ConstantValues.BARCODE;
        } else if (barcodeAndUUIDValidator.validateUUIDFormat(item)) {
            return ConstantValues.UUID;
        } else {
            throw new IllegalArgumentException("Item to redact or rescind is invalid.");
        }
    }

    public void setRedactionQueriesCommon(RedactionQueries redactionQueries) {
        this.redactionQueriesCommon = redactionQueries;
    }

    public void setRedactionQueriesDisease(RedactionQueries redactionQueries) {
        this.redactionQueriesDisease = redactionQueries;
    }

    public void setBarcodeAndUUIDValidator(CommonBarcodeAndUUIDValidator barcodeAndUUIDValidator) {
        this.barcodeAndUUIDValidator = barcodeAndUUIDValidator;
    }

    public void setUuidHierarchyQueries(UUIDHierarchyQueries uuidHierarchyQueries) {
        this.uuidHierarchyQueries = uuidHierarchyQueries;
    }

    public void setAnnotationQueries(final AnnotationQueries annotationQueries) {
           this.annotationQueries = annotationQueries;
    }

}
