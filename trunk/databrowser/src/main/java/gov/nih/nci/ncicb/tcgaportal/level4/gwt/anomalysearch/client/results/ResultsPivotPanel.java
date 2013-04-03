/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.SortSpecifier;

/**
 * Results panel used for displaying a pivoted result set. Since it is a SortController,
 * it is also responsible for sorting the results.  Since it's a PagingAndExportController, it is involved
 * in pagination of the results and export.  
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class ResultsPivotPanel extends VerticalPanel implements ResultsPanelCanCopyToFilter, PagingAndExportController, SortController {

    private ModeController modeController;
    private Results fullResults;
    private FilterSpecifier.ListBy listby;
    private String tabtext;
    private String tabId;    
    private ResultsPagingPanel innerPanel;
    private SortOrderKeepTracker sortOrder = new SortOrderKeepTracker();

    public ResultsPivotPanel(Results results, String tabtext, String tabId, ModeController modeController, FilterSpecifier.ListBy listby) {
        this.modeController = modeController;
        this.fullResults = results;
        this.tabtext = tabtext;
        this.tabId = tabId;
        this.listby = listby;
        Results pageResults = extractPageResults(1);
        innerPanel = new ResultsPagingPanel(results, makeTable(pageResults), this, false);
        hookupPagingListeners();
        innerPanel.enableExportLink();
        add(innerPanel);
    }

    private ResultsTable makeTable(Results pageResults) {
        ResultsTable table;
        if (listby == FilterSpecifier.ListBy.Genes) {
            table = new ResultsTableGene(pageResults, null, this, false, true);
        } else {
            table = new ResultsTablePatient(pageResults, null, this, true);
        }
        return table;
    }

    private Results extractPageResults(int pageno) {
        Results pageResults = new Results(fullResults);
        //todo  move into Results class to share with server side paging?
        int fullRowCount = fullResults.getActualRowCount();
        int rowsPerPage = fullResults.getRowsPerPage();
        if (pageno > 0) { //in case of pageno==0, make an empty results object
            int start = rowsPerPage * (pageno - 1);
            if (start <= fullRowCount - 1) {
                int max = start + rowsPerPage;
                if (max > fullRowCount) {
                    max = fullRowCount;
                }
                for (int i = start; i < max; i++) {
                    pageResults.addRow(fullResults.getRow(i));
                }
            }
        }

        pageResults.setTotalRowCount(fullResults.getTotalRowCount());
        pageResults.setTotalPages(fullResults.getTotalPages());
        pageResults.setGatheredPages(fullResults.getGatheredPages());
        pageResults.setGatheredRows(fullResults.getActualRowCount());
        pageResults.setFinalRowCount(fullResults.isFinalRowCount());
        pageResults.setRowsSearched(fullResults.getRowsSearched());
        pageResults.setRowsToSearch(fullResults.getRowsToSearch());
        fullResults.setCurrentPage(pageno);
        pageResults.setCurrentPage(pageno);
        pageResults.setRowsPerPage(rowsPerPage);
        return pageResults;
    }

    private void hookupPagingListeners() {
        ClickListener firstPageListener = new ClickListener() {
            public void onClick(Widget sender) {
                updatePage(1);
            }
        };
        ClickListener previousPageListener = new ClickListener() {
            public void onClick(Widget sender) {
                updatePage(innerPanel.getCurrentPage() - 1);
            }
        };
        ClickListener nextPageListener = new ClickListener() {
            public void onClick(Widget sender) {
                updatePage(innerPanel.getCurrentPage() + 1);
            }
        };
        ClickListener lastPageListener = new ClickListener() {
            public void onClick(Widget sender) {
                updatePage(innerPanel.getLastPage());
            }
        };
        ChangeListener changeListener = new ChangeListener() {
            public void onChange(Widget sender) {
                updatePage(innerPanel.getCurrentPage());
            }
        };
        innerPanel.addPagingListeners(previousPageListener, nextPageListener, firstPageListener, lastPageListener, changeListener);
    }

    private void updatePage(int pageno) {
        Results pageResults = extractPageResults(pageno);
        innerPanel.replaceTable(pageResults, makeTable(pageResults));
    }

    public void addCopyToFilterClickListener(ClickListener listener) {
        innerPanel.addCopyToFilterClickListener(listener);
    }

    public ResultsTable getResultsTable() {
        return innerPanel.getResultsTable();
    }

    public FilterSpecifier.ListBy getListBy() {
        return listby;
    }

    public void writeExportData() {
        modeController.writePivotExportData(fullResults);
    }

    public void setRowsPerPage(int rowsPerPage) {
        fullResults.setRowsPerPage(rowsPerPage);
        int rows = fullResults.getActualRowCount();
        int pages = rows / rowsPerPage;
        if (rows % rowsPerPage != 0) pages++; //account for partial pages
        fullResults.setTotalPages(pages);
        fullResults.setGatheredPages(pages);
        Results pageResults = extractPageResults(1);
        innerPanel.replaceTable(pageResults, makeTable(pageResults));
    }

    //from SortController interface
    public void sort(long columnId, String annotation, boolean initialAscending) {
        boolean ascending = sortOrder.doColumnAscending(columnId, annotation, initialAscending);
        SortSpecifier sortspec = new SortSpecifier(columnId, annotation, ascending);
        fullResults.sort(sortspec);
        Results pageResults = extractPageResults(1);
        innerPanel.replaceTable(pageResults, makeTable(pageResults));
    }

    //from SortController interface
    public int getCurrentSortOrderForColumn(long columnId, String annotation) {
        return sortOrder.getCurrentSortOrderForColumn(columnId, annotation);
    }

    public String getTabText() {
        return tabtext;
    }

    public String getTabId() {
        return tabId;
    }
}


