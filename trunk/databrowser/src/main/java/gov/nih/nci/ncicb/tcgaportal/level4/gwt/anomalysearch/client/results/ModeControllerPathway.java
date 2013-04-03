/*
* Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
* The software subject to this notice and license includes both human readable source code form and machine readable,
* binary, object code form (the "caBIG Software").
*
* Please refer to the complete License text for full details at the root of the project.
*/

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearch;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.AnomalyDisplay;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.FilterPanel;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.FilterSummaryPanel;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.StyleConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwayResults;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwaySpecifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for pathway mode
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class ModeControllerPathway extends ModeController {

    public static final String MODE_NAME = AnomalySearch.dataBrowserConstants.pathways();

    public ModeControllerPathway() {
        super();
        tabPanel.addTabListener(new TabListener() {
            public boolean onBeforeTabSelected(SourcesTabEvents sender, int tabIndex) {
                return true;
            }

            public void onTabSelected(SourcesTabEvents sender, int tabIndex) {
                if (tabIndex == 0) {
                    filterPanel.setSearchButtonText(SEARCH_BUTTON_TEXT + " " + MODE_NAME);
                } else {
                    filterPanel.setSearchButtonText(SINGLE_PATHWAY_BUTTON_TEXT);
                }
            }
        });
    }

    public String getModeColorSchemeClass() {
        return "purple";
    }

    protected void setModeStyle() {
        tabPanel.addStyleName(StyleConstants.PURPLE_TAB_PANEL);
    }

    public String getModeName() {
        return MODE_NAME;
    }

    public String getToggleButtonUpStyleName() {
        return StyleConstants.PURPLE_MODE_BUTTON_UP;
    }

    public String getToggleButtonDownStyleName() {
        return StyleConstants.PURPLE_MODE_BUTTON_DOWN;
    }

    public FilterSpecifier.ListBy getListBy() {
        return FilterSpecifier.ListBy.Pathways;
    }

    class PathwayHyperlinkListener implements ClickListener {
        public void onClick(Widget sender) {
            String pathwayId = ((Hyperlink) sender).getTargetHistoryToken();
            retrievePathwayIntoNewTab(pathwayId, ((Hyperlink) sender).getText());
        }
    }

    protected ResultsPagingPanel displayResults() {
        ResultsTable table = new ResultsTablePathway(results, new PathwayHyperlinkListener(), this);
        ResultsPagingPanel panel = new ResultsPagingPanel(results, table, this, false);
        tabPanel.setMainResultsPanel(panel, getModeColorSchemeClass());
        return panel;
    }

    protected void updateResults() {
        ResultsTable table = new ResultsTablePathway(results, new PathwayHyperlinkListener(), this);
        mainResultsPanel.replaceTable(results, table);
    }

    protected void addToElementList(String list) {
        filterPanel.addToGeneList(list);
    }

    public boolean allowsAnomalyType(AnomalyDisplay anomalyDisplay) {
        return true;
    }

    private void retrievePathwayIntoNewTab(String pathwayId, String linkText) {
        AsyncCallback<SinglePathwayResults> callback = new AsyncCallback<SinglePathwayResults>() {
            public void onFailure(Throwable caught) {
                filterPanel.showError(caught.getMessage(), true);
                filterPanel.stopSearchSpinner();
            }

            public void onSuccess(SinglePathwayResults result) {
                createTabForPathway(result);
                filterPanel.stopSearchSpinner();
            }
        };

        filterPanel.startSearchSpinner();
        SinglePathwaySpecifier sps = new SinglePathwaySpecifier();
        sps.setId(pathwayId);
        try {
            FilterSpecifier filter = filterPanel.makeFilterSpecifier();
            // create filter summary before changing listby type!
            filterPanel.searchStarted(new FilterSummaryPanel(filter));
            sps.setFilterSpecifier(filter);
            sps.getFilterSpecifier().setListBy(FilterSpecifier.ListBy.Genes);
            searchService.getSinglePathway(sps, callback);
            createSpinnerTab(PathwayDiagramPanel.makeTitle(linkText));
        } catch (FilterPanel.FilterPanelException e) {
            filterPanel.stopSearchSpinner();
            //if we had a convenient place, we could display the error message
        }
    }

    void retrievePathwayIntoExistingTab(String pathwayId, final PathwayDiagramPanel pathwayDiagramPanel, boolean highlight) {
        AsyncCallback<SinglePathwayResults> callback = new AsyncCallback<SinglePathwayResults>() {
            public void onFailure(Throwable caught) {
                filterPanel.showError(caught.getMessage(), true);
                filterPanel.stopSearchSpinner();
            }

            public void onSuccess(SinglePathwayResults result) {
                pathwayDiagramPanel.replacePathwayDiagram(result);
                filterPanel.stopSearchSpinner();
            }
        };

        filterPanel.startSearchSpinner();
        SinglePathwaySpecifier sps = new SinglePathwaySpecifier();
        sps.setId(pathwayId);
        try {
            FilterSpecifier filter;
            if (highlight) {
                filter = filterPanel.makeFilterSpecifier();
            } else {
                filter = makeEmptyFilterSpecifier();
            }
            // create filter summary before changing listby type!
            filterPanel.searchStarted(new FilterSummaryPanel(filter));
            sps.setFilterSpecifier(filter);
            sps.getFilterSpecifier().setListBy(FilterSpecifier.ListBy.Genes);
            searchService.getSinglePathway(sps, callback);
        } catch (FilterPanel.FilterPanelException e) {
            filterPanel.stopSearchSpinner();
            //if we had a convenient place, we could display the error message
        }
    }

    //makes a filter specifier with no columns - this will ensure that the
    //pathway diagram will come back unhighlighted
    private FilterSpecifier makeEmptyFilterSpecifier() throws FilterPanel.FilterPanelException {
        FilterSpecifier filter = filterPanel.makeFilterSpecifier();
        List<ColumnType> emptyColumnList = new ArrayList<ColumnType>();
        filter.setColumnTypes(emptyColumnList);
        return filter;
    }

    private void createTabForPathway(SinglePathwayResults pathwayResult) {
        final PathwayDiagramPanel panel = new PathwayDiagramPanel(pathwayResult, this);
        panel.addCopyToFilterClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                String list = panel.makeListFromCheckedRows();
                addToElementList(list);
                addToFilterPopup.setPopupPosition(sender.getAbsoluteLeft(), sender.getAbsoluteTop() - 15);
                addToFilterPopup.show();
                Timer hideTimer = new Timer() {
                    public void run() {
                        addToFilterPopup.hide();
                    }
                };
                hideTimer.schedule(2000);
            }
        });
        tabPanel.addOrReplaceTab(panel, getModeColorSchemeClass());
    }

    protected void hookupCopyToFilterListener() {

    }

    public void search() {

        int selectedTabIdx = tabPanel.getTabBar().getSelectedTab();
        if (selectedTabIdx <= 0) {
            //main tab, do regular search
            super.search();
        } else {
            //secondary tab, update that particular pathway
            PathwayDiagramPanel panel = (PathwayDiagramPanel) tabPanel.getWidget(selectedTabIdx);
            String pathwayId = panel.getPathwayId();
            retrievePathwayIntoNewTab(pathwayId, panel.getTabText());
        }
    }
}
