/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.RedactionQueries;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * JDBC implementation of RedactionQueries
 *
 * @author Shelley Alonso Last updated by: $Shelley Alonso$
 * @version $Rev$
 */

public class RedactionQueriesJDBCImpl extends SimpleJdbcDaoSupport implements RedactionQueries {

    protected final Log logger = LogFactory.getLog(getClass());
    private boolean isCommonSchema = true;

    private final static String REDACT_SHIPPED_BIOSPECIMEN = "update shipped_biospecimen set is_redacted=1, is_viewable=0 where uuid = :uuid";
    private final static String REDACT_SHIPPED_BIOSPECIMEN_KEEP_VIEWABLE = "update shipped_biospecimen set is_redacted=1 where uuid = :uuid";
    private final static String REDACT_BIOSPECIMEN_BARCODE = "update biospecimen_barcode set is_viewable = 0 where uuid = :uuid";
    private final static String REDACT_UUID_HIERARCHY = "update uuid_hierarchy set is_redacted=1 where uuid = :uuid";
          
    private final static String RESCIND_SHIPPED_BIOSPECIMEN = "update shipped_biospecimen set is_redacted = 0, is_viewable = 1 where uuid = :uuid";
    private final static String RESCIND_BIOSPECIMEN_BARCODE = "update biospecimen_barcode set is_viewable = 1 where uuid = :uuid";
    private final static String RESCIND_UUID_HIERARCHY = "update uuid_hierarchy set is_redacted=0 where uuid = :uuid";
    
    private PlatformTransactionManager transactionManager;

    /**
     * Do redaction actions for a redacted item
     *
     * @param childUuids an array of child uuid's to redact
     * @param setToNotViewable whether to also set any shipped biospecimens in the list to not viewable
     */
    public void redact(final SqlParameterSource[] childUuids, final boolean setToNotViewable) {

        // start TX
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(transactionDefinition);
        try {
            getSimpleJdbcTemplate().batchUpdate(setToNotViewable ? REDACT_SHIPPED_BIOSPECIMEN : REDACT_SHIPPED_BIOSPECIMEN_KEEP_VIEWABLE, childUuids);
            if (setToNotViewable) {
                getSimpleJdbcTemplate().batchUpdate(REDACT_BIOSPECIMEN_BARCODE, childUuids);
            }
            if (isCommonSchema) {
                getSimpleJdbcTemplate().batchUpdate(REDACT_UUID_HIERARCHY, childUuids);
            }
            transactionManager.commit(status);
        } catch (Exception ex) {
            transactionManager.rollback(status);
            logger.info(ex.getMessage());
        }
    }

    /**
     * Do rescission actions for a redacted item
     *
     * @param childUuids an array of child uuid's to redact
     */
    public void rescind(final SqlParameterSource[] childUuids) {
        // start TX
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(transactionDefinition);
        try {
            getSimpleJdbcTemplate().batchUpdate(RESCIND_SHIPPED_BIOSPECIMEN, childUuids);
            getSimpleJdbcTemplate().batchUpdate(RESCIND_BIOSPECIMEN_BARCODE, childUuids);
            if (isCommonSchema) {
            	getSimpleJdbcTemplate().batchUpdate(RESCIND_UUID_HIERARCHY, childUuids);
            }
            transactionManager.commit(status);
        } catch (Exception ex) {
            transactionManager.rollback(status);
            logger.info(ex.getMessage());
        }
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Sets whether this DAO is acting on the common schema or a disease schema.
     *
     * @param commonSchema true if common schema, false if a disease schema
     */
    public void setCommonSchema(final boolean commonSchema) {
        isCommonSchema = commonSchema;
    }
}
