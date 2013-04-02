/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.validators;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.BeanPropertyValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Center bean property validator implementation
 *
 * @author nichollsmc
 *         Last updated by: nichollsmc
 */

@Component
public class CenterValidator implements BeanPropertyValidator {

    @Autowired
    private CenterQueries centerQueries;

    @Override
    public boolean validate(String center) {
        for (final Center c : centerQueries.getRealCenterList()) {
            if (center.equalsIgnoreCase(c.getShortName())
                    || center.equalsIgnoreCase(c.getCenterName())
                    || center.equalsIgnoreCase(c.getBcrCenterId())) {
                return true;
            }
        }

        return false;
    }

    public void setCenterQueries(CenterQueries centerQueries) {
        this.centerQueries = centerQueries;
    }

    @Override
    public String getMessagePropertyKey() {
        return "{gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.AssertionType.CenterValidator.message}";
    }

}
