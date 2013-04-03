package gov.nih.nci.ncicb.tcgaportal.level4.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Test class for FishersExact
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class FishersExactFastTest {

    @Test
    public void testFisher() {
        assessFisher(3, 1, 10, 1, 0.3000,0.3000,0.9999);
        assessFisher(13, 3, 26, 13, 0.0169,0.9994,0.0085);
        assessFisher(100, 10, 1000, 50, 0.0259,0.0214,0.9923);
        assessFisher(100, 12, 1000, 30, 0.0000,0.0000,1.0000);
        assessFisher(100, 1, 1000, 40, 0.1715,0.9865,0.0763);
        assessFisher(100, 0, 1000, 50, 0.0069,1.0000,0.0045);
        assessFisher(500, 1, 3285, 1, 0.1522,0.1522,1.0000);
        assessFisher(500, 54, 3285, 489, 0.0051,0.9984,0.0025);
        assessFisher(500, 54, 3285, 400, 0.3345,0.8642,0.1719);
        assessFisher(500, 54, 3785, 300, 0.0128,0.0085,0.9946);
        assessFisher(500, 54, 3785, 200, 0.0000,0.0000,1.0000);
        assessFisher(500, 54, 3785, 100, 0.0000,0.0000,1.0000);
        assessFisher(500, 54, 3785, 454, 0.4165, 0.8303, 0.2107);
        assessFisher(500, 44, 3285, 454, .0003,0.9999,0.0001);
        assessFisher(500, 34, 3285, 454, 0.0000,1.0000,0.0000);
        assessFisher(490, 24, 3775, 454, 0.0000,1.0000,0.0000);
        assessFisher(500, 14, 3785, 454, 0.0000,1.0000,0.0000);
        assessFisher(500, 4, 3785, 454, 0.0000,1.0000,0.0000);
    }

    private void assessFisher(final int totalChanged,
                              final int changedInNode,
                              final int totalOverall,
                              final int totalInNode,
                              final double expectedTwoTail,
                              final double expectedRightTail,
                              final double expectedLeftTail)
    {
        final FishersExact f = new FishersExactImpl();
        final double twoTail;
        twoTail = f.calculateFisherTwoTail(totalChanged,changedInNode,totalOverall,totalInNode);
        assertEquals(expectedTwoTail,twoTail,.0001);
        final double leftTail;
        leftTail = f.calculateFisherLeftTail(totalChanged,changedInNode,totalOverall,totalInNode);
        assertEquals(expectedLeftTail,leftTail,.0001);
        final double rightTail;
        rightTail = f.calculateFisherRightTail(totalChanged,changedInNode,totalOverall,totalInNode);
        assertEquals(expectedRightTail,rightTail,.0001);
    }
}
