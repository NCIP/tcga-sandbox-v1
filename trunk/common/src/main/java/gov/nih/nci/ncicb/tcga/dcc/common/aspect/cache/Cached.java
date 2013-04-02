/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A neat annotation to mark any method that should be caught by the cache aspect.
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.METHOD)
public @interface Cached {
}
