/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import java.math.BigDecimal;

/**
 * This class does conversions on BigDecimal
 *
 * @author Jeyanthi Thangiah
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BigDecimalConversions {

    /**
     * Convert a BigDecimal object to an Integer. Return a null if the input is null.
     *
     * @param bigDecimal
     */
    public static Integer bigDecimalToInteger(final BigDecimal bigDecimal){
        if(bigDecimal == null){
            return null;
        }
        else{
            return bigDecimal.intValue();
        }
    }
}
