package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchGWTTestCase;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CorrelationType;

/**
 * Test class for CorrelationWidget
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class CorrelationWidgetFastTest extends AnomalySearchGWTTestCase {


    public void testPValueLimit() {
        CorrelationType corrType = new CorrelationType();
        corrType.setDisplayName("test");
        CorrelationWidget widget = new CorrelationWidget(corrType);
        assertEquals(String.valueOf(CorrelationWidget.DEFAULT_PVALUE_LIMIT), widget.frequencyTextBox.getText());

        widget.frequencyTextBox.setText("0.01");
        assertEquals(0.01, widget.getPValueLimit());

        widget.frequencyTextBox.setText("nan");
        assertEquals(-1.0, widget.getPValueLimit());

        widget.frequencyTextBox.setText("");
        assertEquals(-1.0, widget.getPValueLimit());
    }
}
