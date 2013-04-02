package gov.nih.nci.ncicb.tcga.dcc.qclive.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test class for ChromInfoUtilsSoundCheckImpl
 *
 * @author Tarek Hassan
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ChromInfoUtilsSoundCheckImplFastTest {

    private ChromInfoUtilsSoundCheckImpl chromInfoUtils = new ChromInfoUtilsSoundCheckImpl();

    @Test
    public void testIsValidChromCoordValidNoWhitespace() {

        assertTrue(chromInfoUtils.isValidChromCoord("chr1", 249250621, "abc"));
        assertTrue(chromInfoUtils.isValidChromCoord("chr1", 249250650, "abc"));
        assertTrue(chromInfoUtils.isValidChromCoord("chrUn_gl000232", 1, "abc"));
    }

    @Test
    public void testIsValidChromCoordInvalidWhitespace() {

        assertFalse(chromInfoUtils.isValidChromCoord("", 249250622, "abc"));
        assertFalse(chromInfoUtils.isValidChromCoord("   ", 1, "abc"));
        assertFalse(chromInfoUtils.isValidChromCoord("  chr1", 1, "abc"));
        assertFalse(chromInfoUtils.isValidChromCoord("chr1  ", 1, "abc"));
        assertFalse(chromInfoUtils.isValidChromCoord("  chr1 ", 1, "abc"));
    }

    @Test
    public void testIsValidChromCoordInvalidCoordinate() {

        assertFalse(chromInfoUtils.isValidChromCoord("xyz", 0, "abc"));
        assertFalse(chromInfoUtils.isValidChromCoord("xyz", -100, "abc"));
    }

    @Test
    public void testIsValidChromValueNoWhitespace() {

        assertTrue(chromInfoUtils.isValidChromValue("chrX"));
        assertTrue(chromInfoUtils.isValidChromValue("chrx"));
        assertTrue(chromInfoUtils.isValidChromValue("chr25"));
        assertTrue(chromInfoUtils.isValidChromValue("chrB"));
        assertTrue(chromInfoUtils.isValidChromValue("chrUn_zl000232"));
    }

    @Test
    public void testIsValidChromValueWhitespace() {

        assertFalse(chromInfoUtils.isValidChromValue("  chrX"));
        assertFalse(chromInfoUtils.isValidChromValue("chrx  "));
        assertFalse(chromInfoUtils.isValidChromValue("  chr25  "));
        assertFalse(chromInfoUtils.isValidChromValue(""));
    }
}
