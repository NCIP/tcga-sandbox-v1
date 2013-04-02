/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.BaseQCLiveFastTest;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for Archive bean.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveQCLiveFastTest extends BaseQCLiveFastTest {

    public ArchiveQCLiveFastTest() throws IOException {
    }

    /**
     * Tests that setting archive type works. The type is not a level type.
     * Want to make sure no exceptions happen or anything with non-data-level types.
     */
    @Test
    public void testSetArchiveType() {
        String type = "a type";
        Archive archive = new Archive();
        archive.setArchiveType(type);
        assertEquals(type, archive.getArchiveType());
    }

    /**
     * Tests that setting archive type that is a data level works correctly.
     * Data level should be set automatically to "X" if type is "Level_X"
     */
    @Test
    public void testSetArchiveTypeLevel() {
        // type has a level
        String type = "Level_12";
        Archive archive = new Archive();
        archive.setArchiveType(type);
        assertEquals(new Integer(12), archive.getDataLevel());
    }

    /**
     * Tests that setting archive type to something with "Level_X" pattern
     * where X is not a number will work correctly. 
     */
    @Test public void testSetArchiveTypeBadLevel() {
        String type = "Level_squirrel"; // squirrel is not a number
        Archive archive = new Archive();
        archive.setArchiveType(type);
        assertEquals(type, archive.getArchiveType());
        // (test will fail if a number format exception is thrown
    }

    /**
     * Tests that if the type is null then the method will not fail.
     */
    @Test
    public void testSetArchiveTypeNull() {
        String type = null;
        Archive archive = new Archive();
        archive.setArchiveType(type);
        assertNull(archive.getArchiveType());
    }

    @Test
    public void testGetSecondaryDeployDirectoryForTarGzSecondaryDeployLocation() {

        final Archive archive = new Archive();
        archive.setSecondaryDeployLocation("archiveLocation.tar.gz");

        assertEquals("archiveLocation", archive.getSecondaryDeployDirectory());
    }

    @Test
    public void testGetSecondaryDeployDirectoryForTarSecondDeployLocation() {

        final Archive archive = new Archive();
        archive.setSecondaryDeployLocation("archiveLocation.tar");

        assertEquals("archiveLocation", archive.getSecondaryDeployDirectory());
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void testGetSecondaryDeployDirectoryForNonTarAndNonTarGzSecondaryDeployLocation() {

        final Archive archive = new Archive();
        archive.setSecondaryDeployLocation("archiveLocation.zip");

        // StringIndexOutOfBoundsException is expected since the extension is neither tar or tar.gz
        archive.getSecondaryDeployDirectory();
    }

    public void testGetSecondaryDeployDirectoryWhenNovDeployLocation() {

        final Archive archive = new Archive();
        archive.setSecondaryDeployLocation(null);

        assertNull(archive.getSecondaryDeployDirectory());
    }

    @Test
    public void testIsDeployedArchiveCompressedForTarGzDeployLocation() {

        final Archive archive = new Archive();
        archive.setDeployLocation("archiveLocation.tar.gz");

        assertTrue(archive.isDeployedArchiveCompressed());
    }

    @Test
    public void testIsDeployedArchiveCompressedForTarDeployLocation() {

        final Archive archive = new Archive();
        archive.setDeployLocation("archiveLocation.tar");

        assertFalse(archive.isDeployedArchiveCompressed());
    }

    @Test
    public void testIsDeployedArchiveCompressedForNonTarAndNonTarGzDeployLocation() {

        final Archive archive = new Archive();
        archive.setDeployLocation("archiveLocation.zip");

        assertFalse(archive.isDeployedArchiveCompressed());
    }

    @Test
    public void testIsDeployedArchiveCompressedWhenNoDeployLocation() {

        final Archive archive = new Archive();
        archive.setDeployLocation(null);

        assertFalse(archive.isDeployedArchiveCompressed());
    }

    @Test
    public void testIsSecondaryDeployedArchiveCompressedForTarGzDeployLocation() {

        final Archive archive = new Archive();
        archive.setSecondaryDeployLocation("archiveLocation.tar.gz");

        assertTrue(archive.isSecondaryDeployedArchiveCompressed());
    }

    @Test
    public void testIsSecondaryDeployedArchiveCompressedForTarDeployLocation() {

        final Archive archive = new Archive();
        archive.setSecondaryDeployLocation("archiveLocation.tar");

        assertFalse(archive.isSecondaryDeployedArchiveCompressed());
    }

    @Test
    public void testIsSecondaryDeployedArchiveCompressedForNonTarAndNonTarGzDeployLocation() {

        final Archive archive = new Archive();
        archive.setSecondaryDeployLocation("archiveLocation.zip");

        assertFalse(archive.isSecondaryDeployedArchiveCompressed());
    }

    @Test
    public void testIsSecondaryDeployedArchiveCompressedWhenNoDeployLocation() {

        final Archive archive = new Archive();
        archive.setSecondaryDeployLocation(null);

        assertFalse(archive.isSecondaryDeployedArchiveCompressed());
    }

    @Test
    public void testIsDepositedArchiveCompressedForTarGzArchiveFile() {

        final Archive archive = new Archive();
        archive.setArchiveFile(new File("archiveFile.tar.gz"));

        assertTrue(archive.isDepositedArchiveCompressed());
    }

    @Test
    public void testIsDepositedArchiveCompressedForTarArchiveFile() {

        final Archive archive = new Archive();
        archive.setArchiveFile(new File("archiveFile.tar"));

        assertFalse(archive.isDepositedArchiveCompressed());
    }

    @Test
    public void testIsDepositedArchiveCompressedForNonTarAndNonTarGzArchiveFile() {

        final Archive archive = new Archive();
        archive.setArchiveFile(new File("archiveFile.zip"));

        assertFalse(archive.isDepositedArchiveCompressed());
    }

    @Test
    public void testIsDepositedArchiveCompressedWhenNoArchiveFile() {

        final Archive archive = new Archive();
        archive.setArchiveFile(null);

        assertFalse(archive.isDepositedArchiveCompressed());
    }

    @Test
    public void testGetDepositedArchiveExtensionForTarGzArchiveFile() {

        final Archive archive = new Archive();
        archive.setArchiveFile(new File("archiveFile.tar.gz"));

        assertEquals(".tar.gz", archive.getDepositedArchiveExtension());
    }

    @Test
    public void testGetDepositedArchiveExtensionForTarArchiveFile() {

        final Archive archive = new Archive();
        archive.setArchiveFile(new File("archiveFile.tar"));

        assertEquals(".tar", archive.getDepositedArchiveExtension());
    }

    @Test
    public void testGetDepositedArchiveExtensionForNonTarAndNonTarGzArchiveFile() {

        final Archive archive = new Archive();
        archive.setArchiveFile(new File("archiveFile.zip"));

        // getDepositedArchiveExtension() returns '.tar' for all files that do not end by .tar.gz
        assertEquals(".tar", archive.getDepositedArchiveExtension());
    }

    @Test
    public void testGetDepositedArchiveExtensionWhenNoArchiveFile() {

        final Archive archive = new Archive();
        archive.setArchiveFile(null);

        // getDepositedArchiveExtension() returns '.tar' for all files that do not end by .tar.gz
        assertEquals(".tar", archive.getDepositedArchiveExtension());
    }

    @Test
    public void testGetDeployedArchiveExtensionForTarGzDeployLocation() {

        final Archive archive = new Archive();
        archive.setDeployLocation("archiveLocation.tar.gz");

        assertEquals(".tar.gz", archive.getDeployedArchiveExtension());
    }

    @Test
    public void testGetDeployedArchiveExtensionForTarDeployLocation() {

        final Archive archive = new Archive();
        archive.setDeployLocation("archiveLocation.tar");

        assertEquals(".tar", archive.getDeployedArchiveExtension());
    }

    @Test
    public void testGetDeployedArchiveExtensionForNonTarAndNonTarGzDeployLocation() {

        final Archive archive = new Archive();
        archive.setDeployLocation("archiveLocation.zip");

        // getDeployedArchiveExtension() returns '.tar' for all files that do not end by .tar.gz
        assertEquals(".tar", archive.getDeployedArchiveExtension());
    }

    @Test
    public void testGetDeployedArchiveExtensionWhenNoDeployLocation() {

        final Archive archive = new Archive();
        archive.setDeployLocation(null);

        // getDeployedArchiveExtension() returns '.tar' for all files that do not end by .tar.gz
        assertEquals(".tar", archive.getDeployedArchiveExtension());
    }

    @Test
    public void testGetSecondaryDeployedArchiveExtensionForTarGzDeployLocation() {

        final Archive archive = new Archive();
        archive.setSecondaryDeployLocation("archiveLocation.tar.gz");

        assertEquals(".tar.gz", archive.getSecondaryDeployedArchiveExtension());
    }

    @Test
    public void testGetSecondaryDeployedArchiveExtensionForTarDeployLocation() {

        final Archive archive = new Archive();
        archive.setSecondaryDeployLocation("archiveLocation.tar");

        assertEquals(".tar", archive.getSecondaryDeployedArchiveExtension());
    }

    @Test
    public void testGetSecondaryDeployedArchiveExtensionForNonTarAndNonTarGzDeployLocation() {

        final Archive archive = new Archive();
        archive.setDeployLocation("archiveLocation.zip");

        // getDeployedArchiveExtension() returns '.tar' for all files that do not end by .tar.gz
        assertEquals(".tar", archive.getSecondaryDeployedArchiveExtension());
    }

    @Test
    public void testGetSecondaryDeployedArchiveExtensionWhenNoDeployLocation() {

        final Archive archive = new Archive();
        archive.setDeployLocation(null);

        // getDeployedArchiveExtension() returns '.tar' for all files that do not end by .tar.gz
        assertEquals(".tar", archive.getSecondaryDeployedArchiveExtension());
    }
}
