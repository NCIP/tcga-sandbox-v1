package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchGWTTestCase;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.UpperAndLowerLimits;

/**
 * Test class for NonMutationAnomalyWidget
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class NonMutationAnomalyWidgetFastTest extends AnomalySearchGWTTestCase {

    public void testGetLowerOperator() {
        ColumnType colType = new CopyNumberType();
        NonMutationAnomalyWidget widget = new GeneExpressionWidget(colType);
        UpperAndLowerLimits.Operator lowerOp = widget.getLowerOperator();
        assertNotNull(lowerOp);
        assertEquals(UpperAndLowerLimits.Operator.LTE, lowerOp); // this is the default
        assertTrue(widget.lessThanOperatorTextBox.isEnabled()); // should be enabled
        widget.lessThanOperatorListBox.setSelectedIndex(0);
        // wanted to test that text box was disabled and cleared but setSelectedIndex does not cause the onChange
        // event to fire, so that doesn't work.  Boo.
        assertEquals(UpperAndLowerLimits.Operator.None, widget.getLowerOperator());
    }

    /*
      If the text box is blank, return "None" for operator even though None not selected
     */
    public void testGetLowerOperatorWithNoLimit() {
        ColumnType colType = new CopyNumberType();
        NonMutationAnomalyWidget widget = new GeneExpressionWidget(colType);
        widget.lessThanOperatorTextBox.setText("");
        assertEquals(UpperAndLowerLimits.Operator.None, widget.getLowerOperator());
    }

    public void testGetLowerLimit() throws FilterPanel.FilterPanelException {
        ColumnType colType = new CopyNumberType();
        NonMutationAnomalyWidget widget = new GeneExpressionWidget(colType);
        Double limit = widget.getLowerLimit();
        assertNotNull(limit);
        assertEquals(widget.DEFAULT_LOWER_LIMIT, String.valueOf(limit));
    }

    public void testGetBlankLowerLimit() throws FilterPanel.FilterPanelException {
        ColumnType colType = new CopyNumberType();
        NonMutationAnomalyWidget widget = new GeneExpressionWidget(colType);
        widget.lessThanOperatorTextBox.setText("");
        Double limit = widget.getLowerLimit();
        assertEquals(0., limit);
    }


    public void testGetUpperOperator() {
        ColumnType colType = new CopyNumberType();
        NonMutationAnomalyWidget widget = new GeneExpressionWidget(colType);
        UpperAndLowerLimits.Operator upperOp = widget.getUpperOperator();
        assertNotNull(upperOp);
        assertEquals(UpperAndLowerLimits.Operator.GTE, upperOp); // this is the default
        assertTrue(widget.greaterThanOperatorTextBox.isEnabled()); // should be enabled
        widget.greaterThanOperatorListBox.setSelectedIndex(0);
        // wanted to test that text box was disabled and cleared but setSelectedIndex does not cause the onChange
        // event to fire, so that doesn't work.  Boo.

        // None should be first one
        assertEquals(UpperAndLowerLimits.Operator.None, widget.getUpperOperator());
    }

    public void testGetUpperOperatorWithNoLimit() {
        ColumnType colType = new CopyNumberType();
        NonMutationAnomalyWidget widget = new GeneExpressionWidget(colType);
        widget.greaterThanOperatorTextBox.setText("");
        assertEquals(UpperAndLowerLimits.Operator.None, widget.getUpperOperator());
    }

    public void testGetUpperLimit() throws FilterPanel.FilterPanelException {
        ColumnType colType = new CopyNumberType();
        NonMutationAnomalyWidget widget = new GeneExpressionWidget(colType);
        Double limit = widget.getUpperLimit();
        assertNotNull(limit);
        assertEquals(widget.DEFAULT_UPPER_LIMIT, String.valueOf(limit));
    }

    public void testNonNumericLowerThreshold() {
        ColumnType colType = new CopyNumberType();
        NonMutationAnomalyWidget widget = new CopyNumberWidget(colType);
        widget.setLowerLimit("-0.5%");
        try {
            widget.getLowerLimit();
            fail(); //shouldn't reach here
        } catch (FilterPanel.FilterPanelException e) {
            //ok
        }
    }

    public void testEmptyLowerThreshold() throws FilterPanel.FilterPanelException {
        ColumnType colType = new CopyNumberType();
        NonMutationAnomalyWidget widget = new CopyNumberWidget(colType);
        widget.setLowerLimit("");
        assertEquals(0., widget.getLowerLimit());
    }

    public void testNonNumericFrequency() {
        ColumnType colType = new CopyNumberType();
        NonMutationAnomalyWidget widget = new CopyNumberWidget(colType);
        widget.setFrequency("30%");
        try {
            widget.getFrequency();
            fail();
        } catch (FilterPanel.FilterPanelException e) {
            //ok
        }
    }

    public void testEmptyFrequency() throws FilterPanel.FilterPanelException {
        ColumnType colType = new CopyNumberType();
        NonMutationAnomalyWidget widget = new CopyNumberWidget(colType);
        widget.setFrequency("");
        assertEquals(0.F, widget.getFrequency());
    }

}
