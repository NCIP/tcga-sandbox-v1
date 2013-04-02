package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.TreeSet;

import org.junit.Test;

/**
 * Test for DataFileLevelTwoConsolidated
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataFileLevelTwoConsolidatedFastTest {
    @Test
    public void testIsProtected() {
        final DataFileLevelTwoConsolidated consolidatedDataFile = new DataFileLevelTwoConsolidated();
        final DataFileLevelTwo protectedFile = new DataFileLevelTwo();
        protectedFile.setProtected(true);
        final DataFileLevelTwo publicFile = new DataFileLevelTwo();
        publicFile.setProtected(false);
        final TreeSet<DataFileLevelTwo> constituentDataFiles = new TreeSet<DataFileLevelTwo>();
        constituentDataFiles.add(protectedFile);
        consolidatedDataFile.addConstituentDataFile(publicFile);
        consolidatedDataFile.setConstituentDataFiles(constituentDataFiles);

        assertTrue(consolidatedDataFile.isProtected());
    }

    @Test
    public void testIsProtectedEmpty() {
        final DataFileLevelTwoConsolidated emptyFile = new DataFileLevelTwoConsolidated();
        assertFalse(emptyFile.isProtected());
    }

    @Test
    public void testGetDisplaySample() {
        final DataFileLevelTwoConsolidated file = new DataFileLevelTwoConsolidated();
        assertEquals("selected_samples", file.getDisplaySample());
    }
}
