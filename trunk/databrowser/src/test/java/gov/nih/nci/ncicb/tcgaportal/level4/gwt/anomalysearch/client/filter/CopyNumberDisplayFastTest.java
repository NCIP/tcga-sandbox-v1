package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchGWTTestCase;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.AnomalyType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MutationType;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for CopyNumberDisplay
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class CopyNumberDisplayFastTest extends AnomalySearchGWTTestCase {

    public void testPopulateCenterPlatformListBox() {
        FilterPanel filterPanel = new FilterPanel();
//        filterPanel.initialize();

        CopyNumberDisplay cnDisplay = new CopyNumberDisplay(filterPanel, AnomalyType.GeneticElementType.Gene);
        List<ColumnType> columns = new ArrayList<ColumnType>();
        MutationType mutType = new MutationType();
        mutType.setCategory(MutationType.Category.Missense);
        CopyNumberType cnType = new CopyNumberType();
        cnType.setDisplayCenter("CENTER");
        cnType.setDisplayPlatform("PLATFORM");
        cnType.setCalculationType(CopyNumberType.CalculationType.Regular);

        columns.add(cnType);
        columns.add(mutType);
        cnDisplay.setColType(columns);
        cnDisplay.createAnomalyPanel();

        // check that there is only one item in the list box, and that is the copy number column
        assertEquals(2, cnDisplay.anomalyListBox.getItemCount());
        assertEquals(cnType.getDisplayName(), cnDisplay.anomalyListBox.getItemText(1));
        assertEquals(String.valueOf(cnType.getId()), cnDisplay.anomalyListBox.getValue(1));

    }

}
