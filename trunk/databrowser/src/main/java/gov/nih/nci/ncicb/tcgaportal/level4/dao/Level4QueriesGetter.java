/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.dao;

import java.util.Collection;
import java.util.List;

/**
 * Interface for getting level 4 queries objects.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public interface Level4QueriesGetter {
    public Level4Queries getLevel4Queries(String disease);

    public Collection<String> getDiseaseNames();
}
