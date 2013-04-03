package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.FilterPanel;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.FilterPanelI;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results.ModeController;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.CloseablePanel;

/**
 * A main tab of the application.  Top part holds filter panel, bottom holds results tabs.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class SearchTab extends VerticalPanel {
    private FilterPanel filterPanel = new FilterPanel();
    private HorizontalPanel mainResultsPanel = new HorizontalPanel();
    private ModeController modeController;

    private AnomalySearchServiceAsync anomalySearchService;
    private AnomalySearch anomalySearchFactory;


    public SearchTab(ModeController controller) {
        this.modeController = controller;
    }

    public void init(String disease) {
        filterPanel.setModeController(modeController);
        filterPanel.setAnomalySearchService(anomalySearchService, disease);
        filterPanel.setAnomalySearchFactory(anomalySearchFactory);
        filterPanel.init();

        modeController.setFilterPanel(filterPanel);

        CloseablePanel resultsPanel = new CloseablePanel("Search Results", modeController.getModeColorSchemeClass());

        // set up... add filter panel to top, and results tab panel to bottom
        resultsPanel.setContent(mainResultsPanel);
        resultsPanel.setWidth("100%");
        mainResultsPanel.setWidth("100%");
        this.add(filterPanel);
        this.add(resultsPanel);


        mainResultsPanel.addStyleName("marginTop10px");
        mainResultsPanel.addStyleName("marginBottom10px");

        //this.setWidth("600px");
    }

    public HorizontalPanel getMainResultsPanel() {
        return mainResultsPanel;
    }

    public void setAnomalySearchService(AnomalySearchServiceAsync anomalySearchService) {
        this.anomalySearchService = anomalySearchService;
    }

    public void setAnomalySearchFactory(AnomalySearch anomalySearchFactory) {
        this.anomalySearchFactory = anomalySearchFactory;
    }

    public FilterPanelI getFilterPanel() {
        return filterPanel;
    }
}
