/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * @author Robert S. Sfeir
 * @version $Rev$
 * @since 3.0
 *
 * Base Test class to help setup the various environment variables for unit tests.  This class pulls its properties
 * by looking for the properties file which is in the classpath.  The properties file is in the common module under
 * TCGA-Core/common/src/test/java/fast/gov/nih/nci/ncicb/tcga/dcc/common/tests.properties and contains all the properties needed
 * by other unit tests in other modules.
 */
public class BaseQCLiveFastTest {

    private static String qcliveSamplesPath;
    private static String autoLoaderSamplesPath;
    private static final String SAMPLE_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    public BaseQCLiveFastTest() throws IOException {
    	setupPaths();
    }

    private void setupPaths() throws IOException {
        qcliveSamplesPath = SAMPLE_DIR + "qclive";
        autoLoaderSamplesPath = SAMPLE_DIR + "autoloader";
    }

    public static String getQcliveSamplesPath() {
        return qcliveSamplesPath;
    }

    public static String getAutoLoaderSamplesPath() {
        return autoLoaderSamplesPath;
    }

    @Test
    public void testPropertiesLoader() throws Exception {
        System.out.println(getQcliveSamplesPath());
        assertNotNull(getQcliveSamplesPath());
        assertEquals(getQcliveSamplesPath(), SAMPLE_DIR + "qclive");
        assertEquals(getAutoLoaderSamplesPath(), SAMPLE_DIR + "autoloader");
    }
}
