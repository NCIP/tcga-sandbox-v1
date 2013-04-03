package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchGWTTestCase;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MutationType;

/**
 * Test class for MutationWidget.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class MutationWidgetFastTest extends AnomalySearchGWTTestCase {

    public void testGetCategory() {
        MutationType mutType = new MutationType();
        mutType.setCategory(MutationType.Category.Frameshift);
        MutationWidget widget = new MutationWidget(mutType);
        assertEquals(MutationType.Category.Frameshift, widget.getCategory());
    }
}
