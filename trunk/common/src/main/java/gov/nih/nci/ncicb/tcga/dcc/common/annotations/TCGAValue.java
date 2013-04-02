/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a custom annotation defined to inject values from a property file into a annotations
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Retention (RetentionPolicy.RUNTIME)
@Target ({ElementType.METHOD, ElementType.FIELD})
public @interface TCGAValue {
    String key();
    String defaultValue() default "";

}//end of annotation definition
