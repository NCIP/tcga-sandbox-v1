/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import java.util.Set;

/**
 * Interface for objects that list schema names.  
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public interface DiseaseNameLister {
    /**
     * Gets the list of defined disease names that we have data source information for.
     * 
     * @return disease names
     */
    public Set<Object> getDiseaseNames();
}
