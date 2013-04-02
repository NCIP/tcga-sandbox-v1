/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.ChromInfoUtils;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.ChromInfoUtilsImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * ChromInfoUtilsImpl unit tests
 *
 * @author Tarek Hassan
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class ChromInfoUtilsImplSlowTest extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "common.unittest.properties";
    private static final String TEST_DATA_FOLDER = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private static final String TEST_DATA_FILE = "/qclive/dao/ChromInfo_TestData.xml";
    private ChromInfoUtilsImpl chromInfoUtilsImpl;

    public ChromInfoUtilsImplSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Before
    public void setUp() throws Exception {

        super.setUp();
        chromInfoUtilsImpl = new ChromInfoUtilsImpl();
        final ChromInfoQueriesImpl chromInfoQueries = new ChromInfoQueriesImpl();
        chromInfoQueries.setDataSource(getDataSource());
        chromInfoUtilsImpl.setChromInfoQueries(chromInfoQueries);
        chromInfoUtilsImpl.init();
    }

    @Test
    public void testGetChromSizeForGenomeBuildValidMatchingCase() throws ChromInfoUtilsImpl.UnknownChromException {

        assertEquals(249250621, chromInfoUtilsImpl.getChromSizeForGenomeBuild("chr1", "GRCh37-lite"));
    }

    @Test
    public void testGetChromSizeForGenomeBuildValidNonUniqueChromosomeName() throws ChromInfoUtilsImpl.UnknownChromException {

        assertEquals(100, chromInfoUtilsImpl.getChromSizeForGenomeBuild("nonUniqueChromosomeName", "smallestLength"));
        assertEquals(500, chromInfoUtilsImpl.getChromSizeForGenomeBuild("nonUniqueChromosomeName", "largestLength"));
    }

    @Test
    public void testGetChromSizeForGenomeBuildValidMixedCase() throws ChromInfoUtils.UnknownChromException {

        assertEquals(249250621, chromInfoUtilsImpl.getChromSizeForGenomeBuild("CHR1", "GRCH37-LITE"));
        assertEquals(249250621, chromInfoUtilsImpl.getChromSizeForGenomeBuild("CHR1", "grch37-lite"));
        assertEquals(249250621, chromInfoUtilsImpl.getChromSizeForGenomeBuild("chr1", "GRCH37-LITE"));
        assertEquals(249250621, chromInfoUtilsImpl.getChromSizeForGenomeBuild("chr1", "grch37-lite"));
    }

    @Test
    public void testGetChromSizeForGenomeBuildInvalidChromosome() {

        try {
            chromInfoUtilsImpl.getChromSizeForGenomeBuild("abc", "GRCh37-lite");
            fail("UnknownChromException was not thrown.");

        } catch (final ChromInfoUtils.UnknownChromException e) {
            assertEquals("Invalid chromosome: abc", e.getMessage());
        }
    }

    @Test
    public void testGetChromSizeForGenomeBuildInvalidGenomeBuild() {

        try {
            chromInfoUtilsImpl.getChromSizeForGenomeBuild("chr1", "abc");
            fail("UnknownChromException was not thrown.");

        } catch (final ChromInfoUtils.UnknownChromException e) {
            assertEquals("Invalid genome build: abc", e.getMessage());
        }
    }

    @Test
    public void testIsValidChromCoordInRange() {

        assertTrue(chromInfoUtilsImpl.isValidChromCoord("chr1", 249250621, "GRCh37-lite"));
        assertTrue(chromInfoUtilsImpl.isValidChromCoord("chrX", 1, "GRCh37-lite"));
        assertTrue(chromInfoUtilsImpl.isValidChromCoord("nonUniqueChromosomeName", 90, "smallestLength"));
        assertTrue(chromInfoUtilsImpl.isValidChromCoord("nonUniqueChromosomeName", 450, "largestLength"));
    }

    @Test
    public void testIsValidChromCoordOutsideRange() {

        assertFalse(chromInfoUtilsImpl.isValidChromCoord("chr1", 0, "GRCh37-lite"));
        assertFalse(chromInfoUtilsImpl.isValidChromCoord("chr1", 249250622, "GRCh37-lite"));
        assertFalse(chromInfoUtilsImpl.isValidChromCoord("nonUniqueChromosomeName", -50, "smallestLength"));
        assertFalse(chromInfoUtilsImpl.isValidChromCoord("nonUniqueChromosomeName", 550, "largestLength"));
    }

    @Test
    public void testIsValidChromCoordMixedCase() {

        assertTrue(chromInfoUtilsImpl.isValidChromCoord("CHR1", 249250621, "GRCH37-LITE"));
        assertTrue(chromInfoUtilsImpl.isValidChromCoord("chr1", 249250621, "grch37-lite"));
        assertTrue(chromInfoUtilsImpl.isValidChromCoord("CHR1", 249250621, "grch37-lite"));
        assertTrue(chromInfoUtilsImpl.isValidChromCoord("chr1", 249250621, "GRCH37-LITE"));
    }

    @Test
    public void testIsValidChromValueMatchingCase() {

        assertTrue(chromInfoUtilsImpl.isValidChromValue("chr1"));
        assertTrue(chromInfoUtilsImpl.isValidChromValue("chrX"));
    }

    @Test
    public void testIsValidChromValueUpperCase() {

        assertTrue(chromInfoUtilsImpl.isValidChromValue("CHR1"));
        assertTrue(chromInfoUtilsImpl.isValidChromValue("CHRX"));
    }

    @Test
    public void testIsValidChromValueLowerCase() {

        assertTrue(chromInfoUtilsImpl.isValidChromValue("chr1"));
        assertTrue(chromInfoUtilsImpl.isValidChromValue("chrx"));
    }

    @Test
    public void testIsValidChromValueNonExistingChromosome() {

        assertFalse(chromInfoUtilsImpl.isValidChromValue("chr25"));
        assertFalse(chromInfoUtilsImpl.isValidChromValue("chrB"));
        assertFalse(chromInfoUtilsImpl.isValidChromValue("chrUn_zl000232"));
    }

    @Test
    public void testIsValidChromValueWhitespaceInChromosomeName() {

        assertFalse(chromInfoUtilsImpl.isValidChromValue("  chrX"));
        assertFalse(chromInfoUtilsImpl.isValidChromValue("chrX  "));
        assertFalse(chromInfoUtilsImpl.isValidChromValue("  chrX  "));
    }
}
