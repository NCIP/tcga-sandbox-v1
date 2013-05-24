/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Interface for Redaction DAO
 *
 * @author Shelley Alonso
 *         Last updated by: $Shelley Alonso$
 * @version $Rev$
 */

public interface RedactionQueries {
    /**
     *  Do redaction actions for a redacted item
     *
     * @param childUuids an array of child uuid's to redact
     * @param setToNotViewable whether to also set any shipped biospecimens in the list to not viewable
     */
     public void redact(SqlParameterSource[] childUuids, boolean setToNotViewable);


   /**
      * Do rescission actions for a redacted item
      *
      * @param childUuids an array of child uuid's to redact
      */
      public void rescind (final SqlParameterSource[] childUuids);

}
