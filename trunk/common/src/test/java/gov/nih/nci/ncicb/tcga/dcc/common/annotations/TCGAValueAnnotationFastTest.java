/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.annotations;

import org.junit.Test;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * Class use for testing the TCGAValue custom annotation using non-annotations spring test
 * because we use a JUnit version greater than 4.4
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

public class TCGAValueAnnotationFastTest extends AbstractDependencyInjectionSpringContextTests {

    private DummyService service;

    public void setService(final DummyService service) {
        this.service = service;
    }

    @Test
    public void testPropertyPlaceholderConfigurer() {
        assertNotNull(service);
        assertEquals("This annotation injection has to work ! - great function indeed B",
                service.getServiceString());
    }

// specifies the Spring configuration to load for this test fixture
    protected String[] getConfigLocations() {
        return new String[] { "classpath:gov/nih/nci/ncicb/tcga/dcc/common/annotations/applicationContext-test.xml" };
    }


}//End of class
