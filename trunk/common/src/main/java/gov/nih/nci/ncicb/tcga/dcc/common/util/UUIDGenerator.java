/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import java.util.UUID;

/**
 * Class to generate UUIDs using JDK Utility for UUIDs
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */
public class UUIDGenerator {

    /**
     * Get a UUID number using JDKs random UUID generation algorithm
     * @return the generated UUID
     */
    public static UUID getUUID() {
        return UUID.randomUUID();
    }

}
