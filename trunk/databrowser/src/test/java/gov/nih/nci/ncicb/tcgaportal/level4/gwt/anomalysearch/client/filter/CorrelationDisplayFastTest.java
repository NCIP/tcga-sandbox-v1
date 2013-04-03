package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchGWTTestCase;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CorrelationType;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for CorrelationDisplay
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class CorrelationDisplayFastTest extends AnomalySearchGWTTestCase {

    public void testClearCorrelationPanel() {
        CorrelationDisplay correlationDisplay = new CorrelationDisplay(new FilterPanel());
        addSomething(correlationDisplay);
        correlationDisplay.clearPanel();
        assertTrue(correlationDisplay.selectedCorrelationWidgets.isEmpty());
    }

    public void testCreateCorrelationsPanel() {
        CorrelationDisplay display = new CorrelationDisplay(new FilterPanel());
        CorrelationType correlationType = new CorrelationType();
        correlationType.setDisplayName("test");
        List<ColumnType> cols = new ArrayList<ColumnType>();
        cols.add(correlationType);
        display.setColType(cols);
        display.createAnomalyPanel();
        assertEquals(2, display.anomalyListBox.getItemCount());
    }

    private void addSomething(CorrelationDisplay display) {
        CorrelationType correlationType = new CorrelationType();
        correlationType.setDisplayName("test");
        List<ColumnType> cols = new ArrayList<ColumnType>();
        cols.add(correlationType);
        display.setColType(cols);
        display.addAnomalyWidget(String.valueOf(correlationType.getId()), true);
    }
}
