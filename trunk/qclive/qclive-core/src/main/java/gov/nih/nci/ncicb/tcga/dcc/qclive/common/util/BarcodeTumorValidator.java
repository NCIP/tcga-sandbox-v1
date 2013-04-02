/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;

/**
 * Interface for BarcodeTumorValidator.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public interface BarcodeTumorValidator {
    /**
     * Checks if the given barcode is valid for the given tumor type.
     * 
     * @param barcode the full barcode
     * @param tumorAbbreviation the tumor abbreviation (e.g. GBM)
     * @return true if the barcode is valid for the tumor, false if not
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException if there is an error
     */
    boolean barcodeIsValidForTumor(String barcode, String tumorAbbreviation) throws Processor.ProcessorException;
}
