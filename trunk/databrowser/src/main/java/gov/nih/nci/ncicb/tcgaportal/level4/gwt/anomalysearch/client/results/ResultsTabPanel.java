package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.user.client.ui.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.FilterPanel;

/**
 * Parent panel of all result tabs.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class ResultsTabPanel extends TabPanel {
    private ResultsPanel mainResultsPanel;

    public void setMainResultsPanel(ResultsPanel mainPanel, String colorSchemeClass) {
        this.mainResultsPanel = mainPanel;
        if (this.getWidgetCount() == 0) {
            add((Widget) mainResultsPanel, makeTabCloseWidget(mainResultsPanel, colorSchemeClass + "Light", false));
        } else {
            getWidget(0).removeFromParent();
            insert((Widget) mainResultsPanel, makeTabCloseWidget(mainResultsPanel, colorSchemeClass + "Light", false), 0);
        }
        selectTab(0);
    }

    public void addOrReplaceTab(ResultsPanel newPanel, String colorSchemeClass) {
        int foundTabIdx = -1;
        for (int i = 0; i < getWidgetCount(); i++) {
            Widget w = getWidget(i);
            //tab Id is used as the unique handle for replacing a panel, tabTitle is used as display.            
            if (w instanceof ResultsPanel && ((ResultsPanel) w).getTabId().equals(newPanel.getTabId())) {
                foundTabIdx = i;
                break;
            }
        }
        int insertAt = getWidgetCount();
        if (foundTabIdx != -1) {
            getWidget(foundTabIdx).removeFromParent();
            insertAt = foundTabIdx;
        }
        Widget tabWidget = makeTabCloseWidget(newPanel, colorSchemeClass + "Light", true);
        insert((Widget) newPanel, tabWidget, insertAt);
        selectTab(insertAt);
    }

    private Widget makeTabCloseWidget(final ResultsPanel newPanel, String colorScheme, boolean allowClose) {
        HorizontalPanel tabWidget = new HorizontalPanel();
        tabWidget.add(new Label(newPanel.getTabText(), false));
        if (allowClose) {
            Image closeImage = new Image(FilterPanel.IMAGES_PATH + "red-x.gif");
            closeImage.setTitle("Close tab");
            closeImage.setStyleName("closeButton");
            closeImage.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                    remove((Widget) newPanel);
                    selectTab(0);
                }
            });
            tabWidget.add(closeImage);
        }
        tabWidget.addStyleName("tab");
        tabWidget.addStyleName(colorScheme + "Tab");

        return tabWidget;
    }
}
