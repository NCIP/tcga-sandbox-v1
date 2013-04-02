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
import org.springframework.stereotype.Component;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.BOTTOM;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.MIDDLE;
import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.TOP;

/**
 * Slide layer bean property validator implementation.
 *
 * @author nichollsmc
 *         Last updated by: nichollsmc
 */

@Component
public class SlideLayerValidator implements BeanPropertyValidator {

    @Override
    public boolean validate(String slideLayer) {

        if ("T".equalsIgnoreCase(slideLayer) || TOP.equalsIgnoreCase(slideLayer)) {
            return true;
        } else if ("M".equalsIgnoreCase(slideLayer) || MIDDLE.equalsIgnoreCase(slideLayer)) {
            return true;
        } else if ("B".equalsIgnoreCase(slideLayer) || BOTTOM.equalsIgnoreCase(slideLayer)) {
            return true;
        }

        return false;
    }

    @Override
    public String getMessagePropertyKey() {
        return "{gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.AssertionType.SlideLayerValidator.message}";
    }

}
