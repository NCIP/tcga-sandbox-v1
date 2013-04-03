package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchGWTTestCase;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;

/**
 * Test class for CopyNumberWidget
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class CopyNumberWidgetFastTest extends AnomalySearchGWTTestCase {

    public void testGetCalculationType() {
        CopyNumberWidget widget = makeWidget();
        assertEquals(CopyNumberType.CalculationType.Regular, widget.getCalculationType());
    }

    public void testSetGisticCheckboxEnabled() {
        CopyNumberWidget widget = makeWidget();
        widget.gisticCheckBox.setChecked(true);
        widget.setGisticCheckboxEnabled(false);
        assertFalse(widget.gisticCheckBox.isEnabled());
        assertFalse(widget.gisticCheckBox.isChecked());
    }

    public void testIsDoGistic() {
        CopyNumberWidget widget = makeWidget();
        widget.gisticCheckBox.setChecked(true);
        assertTrue(widget.isDoGistic());
        widget.gisticCheckBox.setChecked(false);
        assertFalse(widget.isDoGistic());
    }

    private CopyNumberWidget makeWidget() {
        CopyNumberType type = new CopyNumberType();
        return new CopyNumberWidget(type);
    }
}
