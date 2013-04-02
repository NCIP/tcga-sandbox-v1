/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import org.junit.Test;

import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

/**
 * Test class for the getteMethod util
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@SuppressWarnings("unchecked")
public class GetterMethodFastTest {

    /**
     * Let's take the tumor bean in common to test
     * tumor has 3 properties: tumorId, tumorName, tumorDescription;
     */
    @Test
    public void testGetterMethodSucess() throws Exception {
        Method m = GetterMethod.getGetter(Tumor.class, "tumorId");
        assertNotNull(m);
        assertEquals("getTumorId", m.getName());
    }

    @Test
    public void testGetterMethodFail() {
        try {
            Method m = GetterMethod.getGetter(Tumor.class, "blah");
            fail("This method is not supposed to exist");
        } catch (NoSuchMethodException e) {
            assertEquals("gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor.getBlah()", e.getMessage());
        }
    }

}//End of Class
