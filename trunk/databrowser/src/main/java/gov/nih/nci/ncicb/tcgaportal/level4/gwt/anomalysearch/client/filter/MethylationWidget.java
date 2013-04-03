package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.StyleConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.WidgetHelper;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Jul 28, 2009
 * Time: 4:55:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class MethylationWidget extends NonMutationAnomalyWidget {

    public MethylationWidget(ColumnType cType) {
        super(cType);
    }

    protected AnomalyWidget instanceForClone() {
        return new MethylationWidget(cType);
    }

    //methylation widget has only one range operator, so only create the "greaterThen" one (misnomer)
    protected void createRangePanel() {
        greaterThanOperatorListBox = WidgetHelper.getGreaterThanOperatorListBox();
        greaterThanOperatorTextBox = WidgetHelper.getTextBoxWithValidator();
        greaterThanOperatorTextBox.setText(DEFAULT_UPPER_LIMIT);
        operatorPanel.addStyleName(StyleConstants.MARGIN_LEFT_10PX);

        operatorPanel.add(greaterThanOperatorListBox);
        operatorPanel.add(greaterThanOperatorTextBox);
        operatorPanel.setSpacing(5);
        widgetPanel.add(operatorPanel);

        addRangeClickListeners();
        addChangeListeners();
    }

    protected void addRangeClickListeners() {
        greaterThanOperatorListBox.addChangeListener(new ChangeListener() {
            public void onChange(Widget sender) {
                if (sender instanceof ListBox) {
                    if (((ListBox) sender).getValue(((ListBox) sender).getSelectedIndex()).equals("")) {
                        // clear the text box and disable it
                        greaterThanOperatorTextBox.setText("");
                        greaterThanOperatorTextBox.setEnabled(false);
                    } else {
                        greaterThanOperatorTextBox.setEnabled(true);
                        greaterThanOperatorTextBox.setText(DEFAULT_UPPER_LIMIT);
                    }
                }
            }
        });
    }

    protected void addChangeListeners() {
        greaterThanOperatorTextBox.addChangeListener(new ChangeListener() {
            public void onChange(Widget sender) {
                if (greaterThanOperatorTextBox.getText().trim().equals("")) {
                    greaterThanOperatorListBox.setSelectedIndex(0);
                } else if (greaterThanOperatorListBox.getSelectedIndex() == 0) {
                    greaterThanOperatorListBox.setSelectedIndex(1);
                }
            }
        });
    }

}
