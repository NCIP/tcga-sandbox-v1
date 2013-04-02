/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.validators;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.BeanPropertyValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;

/**
 * Barcode bean property validator implementation.
 * 
 * @author nichollsmc 
 * Last updated by: nichollsmc
 */
public class BarcodeValidator implements BeanPropertyValidator {

    @Override
    public boolean validate(String barcode) {
        if (barcode != null && barcode.contains("*")) {
            return true;
        }
        else {
            return new CommonBarcodeAndUUIDValidatorImpl().validateAnyBarcodeFormat(barcode
                    .toUpperCase());
        }
    }

    @Override
    public String getMessagePropertyKey() {
        return "{gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.AssertionType.BarcodeValidator.message}";
    }

}
