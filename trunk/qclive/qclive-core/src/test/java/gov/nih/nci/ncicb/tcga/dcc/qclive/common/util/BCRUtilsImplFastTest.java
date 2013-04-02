package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * BCRUtilsImpl unit tests.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BCRUtilsImplFastTest {

    private BCRUtilsImpl bcrUtilsImpl;

    @Before
    public void setUp() {
        bcrUtilsImpl = new BCRUtilsImpl();
    }

    @Test
    public void testIsClinicalFileTrue() {
        assertTrue(bcrUtilsImpl.isClinicalFile(new File("clinical.bla")));
    }

    @Test
    public void testIsClinicalFileFalse() {
        assertFalse(bcrUtilsImpl.isClinicalFile(new File("squirrel.nut")));
    }

    @Test
    public void testIsBiospecimenFileTrue() {
        assertTrue(bcrUtilsImpl.isBiospecimenFile(new File("biospecimen.bla")));
    }

    @Test
    public void testIsBiospecimenFileFalse() {
        assertFalse(bcrUtilsImpl.isBiospecimenFile(new File("squirrel.nut")));
    }

    @Test
    public void testIsAuxiliaryFileTrue() {
        assertTrue(bcrUtilsImpl.isAuxiliaryFile(new File("auxiliary.bla")));
    }

    @Test
    public void testIsAuxiliaryFileFalse() {
        assertFalse(bcrUtilsImpl.isAuxiliaryFile(new File("squirrel.nut")));
    }

   @Test
    public void testIsControlFileTrue() {
        assertTrue(bcrUtilsImpl.isControlFile(new File("control.bla")));
    }

    @Test
    public void testIsControlFileFalse() {
        assertFalse(bcrUtilsImpl.isControlFile(new File("squirrel.nut")));
    }
}
