/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.validators;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.BeanPropertyValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.SampleTypeQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.CELL_LINE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.NORMAL;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.TUMOR;

/**
 * Sample type bean property validator implementation.
 *
 * @author nichollsmc
 *         Last updated by: nichollsmc
 */

@Component
public class SampleTypeValidator implements BeanPropertyValidator {

    @Autowired
    private SampleTypeQueries sampleTypeQueries;

    @Override
    public boolean validate(String sampleType) {

        if (TUMOR.equalsIgnoreCase(sampleType)) {
            return true;
        } else if (NORMAL.equalsIgnoreCase(sampleType)) {
            return true;
        } else if (CELL_LINE.equalsIgnoreCase(sampleType)) {
            return true;
        } else
            for (SampleType sample : sampleTypeQueries.getAllSampleTypes()) {
                if (sampleType.equalsIgnoreCase(sample.getSampleTypeCode())
                        || sampleType.equalsIgnoreCase(sample.getShortLetterCode())) {
                    return true;
                }
            }
        return false;
    }

    public void setSampleTypeQueries(SampleTypeQueries sampleTypeQueries) {
        this.sampleTypeQueries = sampleTypeQueries;
    }

    @Override
    public String getMessagePropertyKey() {
        return "{gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.AssertionType.SampleTypeValidator.message}";
    }

}
