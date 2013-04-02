/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.usage;

import gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage.AbstractUsageLogger;
import junit.framework.TestCase;

/**
 * Test class for UsageLogger
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class AbstractUsageLoggerFastTest extends TestCase {

    public void testGetActionName() {
        // iterate through all ActionTypes in enum, and make sure there is a value in the hash
        // this is to make sure that if someone adds an ActionType, they also add a description.
        for(final AbstractUsageLogger.ActionType type : AbstractUsageLogger.ActionType.values()) {
            final String name = AbstractUsageLogger.getActionName(type);
            assertNotNull( type.toString(), name );
        }
    }
}
