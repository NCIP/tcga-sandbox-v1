/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchFactory;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchServiceAsync;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.AnomalyDisplay;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.FilterPanel;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.FilterPanelI;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.FilterSummaryPanel;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.SortSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;

import java.util.List;

/**
 * Coordinates interaction between major client components, and between client and server, for a "mode".
 * Modes are Gene, Patient, Pathway. Each mode has its own concrete class.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

public abstract class ModeController implements PagingAndExportController, SortController {

    protected FilterPanelI filterPanel;
    protected ResultsTabPanel tabPanel;
    final protected PopupPanel addToFilterPopup = new PopupPanel(true);
    protected AnomalySearchServiceAsync searchService;
    protected Results results;
    protected HorizontalPanel mainPanel;
    protected ResultsPagingPanel mainResultsPanel;
    protected AnomalySearchFactory anomalySearchFactory;
    protected final String SINGLE_PATHWAY_BUTTON_TEXT = "Update Pathway";
    protected final String SEARCH_BUTTON_TEXT = "Search";
    protected AsyncCallback<Results> searchAsyncCallback, sortAsyncCallback;
    protected AsyncCallback<String> exportAsyncCallback;

    protected String exportFileName;

    SortOrderKeepTracker sortOrder = new SortOrderKeepTracker();

    public ModeController() {
        tabPanel = new ResultsTabPanel();
        tabPanel.setWidth("100%");
        setModeStyle();
        searchAsyncCallback = makeAsyncCallback(true);
        sortAsyncCallback = makeAsyncCallback(false);
        addToFilterPopup.add(new HTML("Items were added to search criteria"));
        addToFilterPopup.addStyleName("addPopup");
    }

    //make the async callback object which will be used for both searching and sorting
    private AsyncCallback<Results> makeAsyncCallback(final boolean clearResultsOnError) {
        return new AsyncCallback<Results>() {
            public void onFailure(Throwable caught) {
                filterPanel.stopSearchSpinner();
                filterPanel.showError(caught.getMessage(), true);
                if (clearResultsOnError) {
                    displayBlankPanel();
                }
                caught.printStackTrace();
            }

            public void onSuccess(Results results) {
                filterPanel.stopSearchSpinner();
                ModeController.this.results = results;
                if (results.isEmpty()) {
                    displayEmptyResultsPanel();
                    return;
                }
                mainPanel.add(tabPanel);
                mainResultsPanel = displayResults();
                hookupPagingListeners();
                hookupCopyToFilterListener(mainResultsPanel);
                updateResultsMeta(results);
                enableExportLinkOrNot();
            }
        };
    }

    public void setAnomalySearchFactory(AnomalySearchFactory anomalySearchFactory) {
        this.anomalySearchFactory = anomalySearchFactory;
    }

    public void clearResults() {
        tabPanel.clear();
        if (mainPanel.getWidgetCount() > 1) {
            mainPanel.getWidget(1).removeFromParent(); //todo  come up with more robust way to handle tabs, not relying so much on indexes
        }
    }

    public abstract String getModeColorSchemeClass();

    protected abstract void setModeStyle();

    public void setFilterPanel(FilterPanelI filterPanel) {
        this.filterPanel = filterPanel;
    }

    public void setMainPanel(HorizontalPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    public ResultsTabPanel getResultsTabPanel() {
        return tabPanel;
    }

    public void setSearchService(AnomalySearchServiceAsync searchService) {
        this.searchService = searchService;
    }

    public abstract String getModeName();

    public abstract String getToggleButtonUpStyleName();

    public abstract String getToggleButtonDownStyleName();

    public abstract FilterSpecifier.ListBy getListBy();

    public void search() {
        filterPanel.clearError();
        filterPanel.startSearchSpinner();
        sortOrder.clear();
        try {
            FilterSpecifier filter = filterPanel.makeFilterSpecifier();
            displaySearchingPanel();
            //show the collapsed representation of the search in the filter panel
            filterPanel.searchStarted(new FilterSummaryPanel(filter));
            searchService.processFilter(filter, searchAsyncCallback);
        } catch (FilterPanel.FilterPanelException re) {  //especially to catch field input issues
            filterPanel.stopSearchSpinner();
            filterPanel.showError(re.getMessage(), true);
            displayBlankPanel();
        }
    }

    protected abstract ResultsPagingPanel displayResults();

    protected void displaySearchingPanel() {
        Panel searching = new SearchingPanel(getModeName());
        displayNonResultsPanel(searching);
    }

    protected void displayEmptyResultsPanel() {
        Panel panel = new SimplePanel();
        panel.setHeight("200px");
        HTML noResultsHtml = new HTML("No results matched your search criteria. Please click 'Modify Search Criteria' above and try a different search.  To view online help, click the question mark at the top of the page.");
        noResultsHtml.setWordWrap(true);
        noResultsHtml.setWidth("600px");
        noResultsHtml.addStyleName("paddingLeft10px");
        panel.add(noResultsHtml);
        displayNonResultsPanel(panel);
    }

    protected void displayBlankPanel() {
        SimplePanel panel = new SimplePanel();
        panel.setHeight("200px");
        displayNonResultsPanel(panel);
    }

    protected void displayNonResultsPanel(Panel content) {
        clearResults();
        mainPanel.add(tabPanel);
        tabPanel.setMainResultsPanel(new InfoPanel(getModeName() + " Summary", content), getModeColorSchemeClass());
    }

    protected abstract void updateResults();

    private void hookupPagingListeners() {
        ClickListener firstPageListener = new ClickListener() {
            public void onClick(Widget sender) {
                getNewPageData(1);
            }
        };
        ClickListener previousPageListener = new ClickListener() {
            public void onClick(Widget sender) {
                getNewPageData(mainResultsPanel.getCurrentPage() - 1);
            }
        };
        ClickListener nextPageListener = new ClickListener() {
            public void onClick(Widget sender) {
                getNewPageData(mainResultsPanel.getCurrentPage() + 1);
            }
        };
        ClickListener lastPageListener = new ClickListener() {
            public void onClick(Widget sender) {
                getNewPageData(mainResultsPanel.getLastPage());
            }
        };
        ChangeListener changeListener = new ChangeListener() {
            public void onChange(Widget sender) {
                getNewPageData(mainResultsPanel.getCurrentPage());
            }
        };
        mainResultsPanel.addPagingListeners(previousPageListener, nextPageListener, firstPageListener, lastPageListener, changeListener);
    }

    public void getNewPageData(int page) {
        if (page <= 0) return;  //todo something to reset text to current page?

        AsyncCallback<Results> callback = new AsyncCallback<Results>() {
            public void onFailure(Throwable caught) {
                filterPanel.stopSearchSpinner();
                filterPanel.showError(caught.getMessage(), true);
                caught.printStackTrace();
            }

            public void onSuccess(Results results) {
                ModeController.this.results = results;
                updateResults();
                filterPanel.stopSearchSpinner();
                enableExportLinkOrNot();
            }
        };
        filterPanel.clearError();
        filterPanel.startSearchSpinner();
        searchService.getResultsPage(getListBy(), page, callback);
    }

    protected void hookupCopyToFilterListener(final ResultsPanelCanCopyToFilter resultsPanel) {
        resultsPanel.addCopyToFilterClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                String list = resultsPanel.getResultsTable().makeListFromCheckedRows();
                if (list != null && list.length() > 0) {
                    if (resultsPanel.getListBy() == FilterSpecifier.ListBy.Genes) {
                        filterPanel.addToGeneList(list);
                    } else if (resultsPanel.getListBy() == FilterSpecifier.ListBy.Patients) {
                        filterPanel.addToPatientList(list);
                    }
                    addToFilterPopup.setPopupPosition(sender.getAbsoluteLeft(), sender.getAbsoluteTop() - 15);
                    addToFilterPopup.show();
                    Timer hideTimer = new Timer() {
                        public void run() {
                            addToFilterPopup.hide();
                        }
                    };
                    hideTimer.schedule(2000);
                }
            }
        });
    }

    public void updateResultsMeta(Results resultsMeta) {
        mainResultsPanel.updatePageCounts(resultsMeta);
        mainResultsPanel.updateProgressBar(resultsMeta);

        // copy the latest counts into the current result
        results.setGatheredPages(resultsMeta.getGatheredPages());
        results.setGatheredRows(resultsMeta.getGatheredRows());
        results.setRowsSearched(resultsMeta.getRowsSearched());
        results.setRowsToSearch(resultsMeta.getRowsToSearch());
        results.setTotalPages(resultsMeta.getTotalPages());
        results.setTotalRowCount(resultsMeta.getTotalRowCount());
        results.setFinalRowCount(resultsMeta.isFinalRowCount());
    }


    //JSNI method that allows for adding a Javascript function that can be used in GWT code
    native void setWindowScrollTop() /*-{
        $wnd.scrollTo(0, 0);
    }-*/;

    private void enableExportLinkOrNot() {
        // enable or disable the export data link
        mainResultsPanel.enableExportLink();
    }

    //from SortController interface
    public void sort(long columnId, String annotation, boolean initialAscending) {
        boolean ascending = doColumnAscending(columnId, annotation, initialAscending);
        SortSpecifier sortspec = new SortSpecifier(columnId, annotation, ascending);
        filterPanel.clearError();
        filterPanel.startSearchSpinner();
        searchService.sortResults(getListBy(), sortspec, sortAsyncCallback);
    }

    private boolean doColumnAscending(long columnId, String annotation, boolean initialAscending) {
        return sortOrder.doColumnAscending(columnId, annotation, initialAscending);
    }

    //from SortController interface
    public int getCurrentSortOrderForColumn(long columnId, String annotation) {
        return sortOrder.getCurrentSortOrderForColumn(columnId, annotation);
    }

    public abstract boolean allowsAnomalyType(AnomalyDisplay anomalyDisplay);

    public void setRowsPerPage(int rowsPerPage) {
        filterPanel.startSearchSpinner();
        searchService.setRowsPerPage(getListBy(), rowsPerPage, sortAsyncCallback);
    }

    public void writeExportData() {
        String filename = getExportFileName();

        AsyncCallback<String> exportAsyncCallback = new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                filterPanel.showError(caught.getMessage(), true);
                caught.printStackTrace();
            }

            public void onSuccess(String exportFileName) {
                if (exportFileName != null) {
                    ModeController.this.exportFileName = exportFileName;
                }
            }
        };
        searchService.exportResultsData(getListBy(), filename, exportAsyncCallback);
        String linkStr = getExportLink(filename);
        Window.open(linkStr, "The_Cancer_Genome_Atlas_Data_Portal", "menubar=yes,toolbar=yes,location=yes,status=no,resizable=yes,scrollbars=yes");
    }

    public void writePivotExportData(Results pivotResults) {
        String filename = getExportFileName();

        AsyncCallback<String> exportAsyncCallback = new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                filterPanel.showError(caught.getMessage(), true);
                caught.printStackTrace();
            }

            public void onSuccess(String exportFileName) {
                if (exportFileName != null) {
                    ModeController.this.exportFileName = exportFileName;
                }
            }
        };
        searchService.exportPivotResultsData(pivotResults.getListBy(), filename, pivotResults, exportAsyncCallback);
        String linkStr = getExportLink(filename);
        Window.open(linkStr, "The_Cancer_Genome_Atlas_Data_Portal", "menubar=yes,toolbar=yes,location=yes,status=no,resizable=yes,scrollbars=yes");
    }

    private String getExportFileName() {
        String diskFileName = String.valueOf(Random.nextInt(1000) + System.currentTimeMillis());
        diskFileName += ".txt";
        return diskFileName;
    }


    public void setExportFileName(String exportFileName) {
        this.exportFileName = exportFileName;
    }

    private String getExportLink(String serverFileName) {
        String link = "";
        if (results.isFinalRowCount()) {
            // get the file name
            link = "exportServlet?fileName=" + serverFileName;
        }
        return link;
    }

    protected void createSpinnerTab(String tabTitle) {
            // the title and ID are same for this spinner tab
            createSpinnerTab(tabTitle, tabTitle);
    }
    
    protected void createSpinnerTab(String tabTitle, String tabId) {
        SearchingPanel searching = new SearchingPanel(tabTitle, tabId);
        searching.setHeight(mainResultsPanel.getOffsetHeight() + "px");
        tabPanel.addOrReplaceTab(searching, getModeColorSchemeClass());
    }

    protected void removePivotColumn(List<ColumnType> columnTypesList, ColumnType pivotColumn) {
        for (int i=columnTypesList.size()-1; i>=0; i--) {   //iterate backwards because we're removing items
            ColumnType filterColumn = columnTypesList.get(i);
            if(!filterColumn.equals(pivotColumn)) {
                columnTypesList.remove(filterColumn);
            }
        }
    }
}
