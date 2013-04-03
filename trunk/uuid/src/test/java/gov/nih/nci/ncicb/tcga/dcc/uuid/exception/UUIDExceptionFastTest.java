/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.exception;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.ErrorInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;

import org.junit.Before;
import org.junit.Test;

/**
 * Class to test UUIDException
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

public class UUIDExceptionFastTest {

    private UUIDException uuidException;
    
    @Before
    public void setUp() {
        uuidException = new UUIDException("Catch me if you can !");
        uuidException.setErrorInfo(new ErrorInfo(new NullPointerException("nasty null pointer")));
    }

    @Test
    public void testStackTrace() {
        assertNotNull(uuidException);
        ErrorInfo errorInfo = uuidException.getErrorInfo();
        assertNotNull(errorInfo);
        assertNotNull(errorInfo.getMessage());
        assertTrue(errorInfo.getMessage().contains("nasty null pointer"));        
        assertNotNull(errorInfo.getStackTrace());
        assertNotNull(errorInfo.getTruncatedStackTrace());
    }

}
