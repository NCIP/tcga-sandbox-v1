/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Fake service for testing cache
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class SlowService {

    protected final Log logger = LogFactory.getLog(getClass());

    @Cached
    public String snailMethod() {
        try {
            // pretend this is a slow method
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.error("Booo, yak, exception", e);
        }
        return "Oh man! , I am a slow process";
    }


    @Cached
    public int getMyInt(int a, int b) {
        return (a + b);
    }

    @ClearCache
    public void byebyeCache() {
    }

    ;

}
