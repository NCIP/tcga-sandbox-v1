/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.validators.AnalyteTypeValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.validators.BCRValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.validators.BarcodeValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.validators.CenterValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.validators.DateValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.validators.IntegerValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.validators.SampleTypeValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.validators.SlideLayerValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.validators.UUIDValidator;

/**
 * Enum that defines the validator types that can be used when validating
 * elements using the JSR-303 custom constraint annotation {@link AssertElement}
 * .
 * 
 * @author nichollsmc 
 * Last updated by: nichollsmc
 */
public enum AssertionType {

    /**
     * Analyte type validator
     */
    ANALYTE_TYPE(new AnalyteTypeValidator()),

    /**
     * Barcode validator
     */
    BARCODE(new BarcodeValidator()),

    /**
     * BCR validator
     */
    BCR(new BCRValidator()),

    /**
     * Center validator
     */
    CENTER(new CenterValidator()),

    /**
     * Date format validator
     */
    DATE_FORMAT(new DateValidator()),

    /**
     * Integer validator
     */
    INTEGER(new IntegerValidator()),

    /**
     * Sample type validator
     */
    SAMPLE_TYPE(new SampleTypeValidator()),

    /**
     * Slide layer validator
     */
    SLIDE_LAYER(new SlideLayerValidator()),

    /**
     * UUID validator
     */
    UUID(new UUIDValidator());

    private BeanPropertyValidator validator;

    private AssertionType(BeanPropertyValidator validator) {
        this.validator = validator;
    }

    public BeanPropertyValidator getValidator() {
        return this.validator;
    }
}
