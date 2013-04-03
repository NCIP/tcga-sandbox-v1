package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchGWTTestCase;

/**
 * Test class for RegionWidget.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class RegionWidgetFastTest extends AnomalySearchGWTTestCase {

    public void testGetChromosome() {
        RegionWidget widget = new RegionWidget();
        assertEquals(widget.getChromosome(), widget.chromosome.getValue(widget.chromosome.getSelectedIndex())); 
        widget.chromosome.setSelectedIndex(2); // the 3rd item is chromosome 3
        assertEquals("3", widget.getChromosome());
    }

    public void testGetStart() {
        RegionWidget widget = new RegionWidget();
        assertEquals(-1, widget.getStart());
        widget.start.setText("100");
        assertEquals(100, widget.getStart());
    }

    public void testGetStartWithSpaces() {
        RegionWidget widget = new RegionWidget();
        widget.start.setText(" 1000 ");
        assertEquals(1000, widget.getStart());
    }

    public void testGetStop() {
        RegionWidget widget = new RegionWidget();
        assertEquals(-1, widget.getStop());
        widget.stop.setText("1000");
        assertEquals(1000, widget.getStop());
    }

    public void testGetStopWithSpaces() {
        RegionWidget widget = new RegionWidget();
        widget.stop.setText(" 1000   ");
        assertEquals(1000, widget.getStop());
    }

    public void testGetStartInvalid() {
        boolean numberFormatThrown = false;
        try {
            RegionWidget widget = new RegionWidget();
            widget.start.setText("squirrel");
            widget.getStart();
        } catch (NumberFormatException ex) {
            numberFormatThrown = true;
        }
        assertTrue("Number format exception should have been thrown", numberFormatThrown);
    }
}
