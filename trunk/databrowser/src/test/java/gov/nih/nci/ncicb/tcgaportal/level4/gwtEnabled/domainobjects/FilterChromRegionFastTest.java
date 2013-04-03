package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * TODO: Class description
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FilterChromRegionFastTest {
    @Test
    public void testOverlapsWith() {
        FilterChromRegion region = new FilterChromRegion("2", 100, 1000);

        assertFalse(region.overlapsWith("4", 40, 50));
        assertFalse(region.overlapsWith("2", 1, 10));
        assertTrue(region.overlapsWith("2", 100, 110));
        assertTrue(region.overlapsWith("2", 900, 100000));
        assertTrue(region.overlapsWith("2", 2, 500));

        region = new FilterChromRegion("2", -1, -1);
        assertFalse(region.overlapsWith("5", 50, 100));
        assertTrue(region.overlapsWith("2", 1,2));

        region = new FilterChromRegion("2", 100, -1);
        assertFalse(region.overlapsWith("2", 80, 90));
        assertTrue(region.overlapsWith("2", 80, 110));

        region = new FilterChromRegion("2", -1, 1000);
        assertFalse(region.overlapsWith("2", 1100, 1200));
        assertTrue(region.overlapsWith("2", 200, 500));
        assertTrue(region.overlapsWith("2", 200, 20000));
    }


}
