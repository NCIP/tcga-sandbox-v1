/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.validators;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.PortionAnalyte;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.BeanPropertyValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PortionAnalyteQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Analyte type bean property validator implementation.
 *
 * @author nichollsmc
 *         Last updated by: nichollsmc
 */

@Component
public class AnalyteTypeValidator implements BeanPropertyValidator {

    @Autowired
    private PortionAnalyteQueries portionAnalyteQueries;

    @Override
    public boolean validate(String analyteType) {
        for (final PortionAnalyte analyte : portionAnalyteQueries.getAllPortionAnalytes()) {
            if (analyteType.equalsIgnoreCase(analyte.getPortionAnalyteCode())) {
                return true;
            }
        }

        return false;
    }

    public void setPortionAnalyteQueries(PortionAnalyteQueries portionAnalyteQueries) {
        this.portionAnalyteQueries = portionAnalyteQueries;
    }

    @Override
    public String getMessagePropertyKey() {
        return "{gov.nih.nci.ncicb.tcga.dcc.common.bean.validation.AnalyteTypeValidator.message}";
    }

}