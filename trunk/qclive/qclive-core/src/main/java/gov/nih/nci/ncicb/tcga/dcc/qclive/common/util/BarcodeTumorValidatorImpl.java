/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.TissueSourceSiteQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;

import java.text.ParseException;
import java.util.List;

/**
 * Validates that a barcode belongs to a certain disease.  Checks the barcode's collection site to make sure it collects
 * a type of tissue that is valid for this disease.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class BarcodeTumorValidatorImpl implements BarcodeTumorValidator {
    private BCRIDProcessor bcrIdProcessor;
    private TissueSourceSiteQueries tissueSourceSiteQueries;

    /**
     * Checks if the given barcode is valid for the given tumor type.
     * @param barcode the full barcode
     * @param tumorAbbreviation the tumor abbreviation (e.g. GBM)
     * @return true if the barcode is valid for the tumor, false if not
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException if the barcode can't be parsed
     */
    public boolean barcodeIsValidForTumor(final String barcode, final String tumorAbbreviation) throws Processor.ProcessorException {        
        try {
            BCRID barcodeObject = bcrIdProcessor.parseAliquotBarcode(barcode);
            String tisueSourceSiteCode = barcodeObject.getSiteID();
            List<String> diseaseList = tissueSourceSiteQueries.getDiseasesForTissueSourceSiteCode(tisueSourceSiteCode);
            return diseaseList.contains(tumorAbbreviation);
        } catch (ParseException e) {
            throw new Processor.ProcessorException(e.getMessage(), e);
        }
    }

    public void setBcrIdProcessor(final BCRIDProcessor bcrIdProcessor) {
        this.bcrIdProcessor = bcrIdProcessor;
    }

    public void setTissueSourceSiteQueries(final TissueSourceSiteQueries tissueSourceSiteQueries) {
        this.tissueSourceSiteQueries = tissueSourceSiteQueries;
    }
}
