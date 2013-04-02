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

/**
 * Integer bean property validator implementation.
 * 
 * @author nichollsmc 
 * Last updated by: nichollsmc
 */
public class IntegerValidator implements BeanPropertyValidator {

    @Override
    public boolean validate(String element) {
        try {
            Integer.parseInt(element);
        }
        catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    @Override
    public String getMessagePropertyKey() {
        return "{gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.AssertionType.IntegerValidator.message}";
    }

}
