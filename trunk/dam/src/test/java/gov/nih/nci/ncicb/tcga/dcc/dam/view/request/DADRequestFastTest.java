package gov.nih.nci.ncicb.tcga.dcc.dam.view.request;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * Test for DADRequest
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DADRequestFastTest {
    @Test
    public void testSetConsolidated() {
        // no matter what, consolidated should be false
        DADRequest dadRequest = new DADRequest();
        assertFalse(dadRequest.isConsolidateFiles());

        dadRequest.setConsolidateFiles(true);
        assertFalse(dadRequest.isConsolidateFiles());

        dadRequest.setConsolidateFiles(false);
        assertFalse(dadRequest.isConsolidateFiles());
    }

}
