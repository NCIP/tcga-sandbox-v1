package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchGWTTestCase;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MutationType;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for MutationWidget
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class MutationDisplayFastTest extends AnomalySearchGWTTestCase {

    public void testAddMutationWidget() {
        MutationDisplay mutationDisplay = new MutationDisplay(new FilterPanel());
        List<ColumnType> colTypes = new ArrayList<ColumnType>();
        MutationType mut1 = new MutationType();
        MutationType mut2 = new MutationType();
        colTypes.add(mut1);
        colTypes.add(mut2);
        mutationDisplay.setColType(colTypes);
        mutationDisplay.addAnomalyWidget(String.valueOf(mut1.getId()), true);
        List<AnomalyWidget> widgets = mutationDisplay.getSelectedWidgets();
        assertEquals(1, widgets.size());
        assertEquals(mut1.getId(), widgets.get(0).cType.getId());
    }


    public void testClearMutationPanel() {
        MutationDisplay mutationDisplay = new MutationDisplay(new FilterPanel());
        List<ColumnType> colTypes = new ArrayList<ColumnType>();
        MutationType mut1 = new MutationType();
        MutationType mut2 = new MutationType();
        colTypes.add(mut1);
        colTypes.add(mut2);
        mutationDisplay.setColType(colTypes);
        mutationDisplay.addAnomalyWidget(String.valueOf(mut1.getId()), true);
        mutationDisplay.clearPanel();
        assertEquals(0, mutationDisplay.getSelectedWidgets().size());
    }

    public void testCreateMutationPanel() {
        MutationDisplay mutationDisplay = new MutationDisplay(new FilterPanel());
        List<ColumnType> colTypes = new ArrayList<ColumnType>();
        MutationType mut1 = new MutationType();
        mut1.setCategory(MutationType.Category.Frameshift);
        MutationType mut2 = new MutationType();
        mut2.setCategory(MutationType.Category.Missense);
        colTypes.add(mut1);
        colTypes.add(mut2);
        mutationDisplay.setColType(colTypes);
        mutationDisplay.createAnomalyPanel();
        assertEquals(3, mutationDisplay.anomalyListBox.getItemCount());
        // note:item text 0 is "Select, then click Add" item
        assertEquals(mut1.getDisplayName(), mutationDisplay.anomalyListBox.getItemText(1));
        assertEquals(mut2.getDisplayName(), mutationDisplay.anomalyListBox.getItemText(2));
    }


}
