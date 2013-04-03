/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearch;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.TooltipTextMap;

/**
 * A ResultsPanel containing a result table that paginates. Contains the widgets required for paging
 * through results, and for changing the number of rows displayed. Calls out to a PagingAndExportController
 * to obtain each page's worth of rows and to export data.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ResultsPagingPanel extends VerticalPanel implements ResultsPanelCanCopyToFilter {

    private PagingAndExportController pagingController;
    private Results results;
    private ResultsTable table;
    private ListBox rowsPerPageLB;

    private HTML copyToFilterLink;

    private HorizontalPanel viewResultsAsPanel = new HorizontalPanel();
    private RadioButton viewPercentageRadioButton;
    private RadioButton viewRatioRadioButton;
    private PagingHelper resultsPagingWidget;
    private ProgressBar resultsProgressBar = new ProgressBar();

    private VerticalPanel tablePagingPanel = new VerticalPanel();

    private HorizontalPanel aboveTablePanel = new HorizontalPanel();
    private HorizontalPanel aboveTablePanel2 = new HorizontalPanel();
    private HorizontalPanel belowTablePanel = new HorizontalPanel();
    private HorizontalPanel pagingPanel = new HorizontalPanel();
    private HorizontalPanel rowInfoPanel = new HorizontalPanel();

    private HTML exportLink;

    public ResultsPagingPanel() {
    }

    public ResultsPagingPanel(Results results, ResultsTable table, PagingAndExportController modeController, boolean showPercentWidget) {
        this.results = results;
        this.table = table;
        this.pagingController = modeController;
        initialize(showPercentWidget);
    }

    private void initialize(boolean showPercentWidget) {
        resultsPagingWidget = new PagingHelper(pagingPanel, rowInfoPanel);

        if (shouldShowPercentWidget(showPercentWidget)) {
            viewPercentageRadioButton = new RadioButton("resultsView", "Results as Percentages");
            viewRatioRadioButton = new RadioButton("resultsView", "Results as Ratios");
            viewResultsAsPanel.add(viewPercentageRadioButton);
            viewResultsAsPanel.add(viewRatioRadioButton);
            viewResultsAsPanel.setCellVerticalAlignment(viewPercentageRadioButton, HasVerticalAlignment.ALIGN_TOP);
            viewResultsAsPanel.setCellVerticalAlignment(viewRatioRadioButton, HasVerticalAlignment.ALIGN_TOP);
            viewPercentageRadioButton.addStyleName("smallRadioButton");
            viewRatioRadioButton.addStyleName("smallRadioButton");
            viewPercentageRadioButton.setChecked(results.hasDisplayFlag(AnomalySearchConstants.RESULTSDISPLAYFLAG_PERCENT));
            viewRatioRadioButton.setChecked(results.hasDisplayFlag(AnomalySearchConstants.RESULTSDISPLAYFLAG_RATIO));
            WidgetHelper.setDomId(viewPercentageRadioButton, SeleniumTags.VIEWASPERCENT_CHECKBOX);
            WidgetHelper.setDomId(viewRatioRadioButton, SeleniumTags.VIEWASRATIO_CHECKBOX);
            aboveTablePanel.add(viewResultsAsPanel);
        }

        if (table != null) {
            switch (pagingController.getListBy()) {
                case Genes:
                case Patients:
                    addCopyToFilterLink(listByStr());
                    break;
                default:
                    //pathway, we do nothing
            }

            addRowsPerPageListBox();
            addLinkToExport();

            aboveTablePanel.setWidth("100%");
            aboveTablePanel2.setWidth("100%");
            tablePagingPanel.add(aboveTablePanel2);
            tablePagingPanel.add(aboveTablePanel);
            //tablePagingPanel.add(new HTML("<br>"));
            table.setWidth("100%");
            tablePagingPanel.add(table);
            belowTablePanel.setWidth("100%");

            tablePagingPanel.add(belowTablePanel);
        }

        if (results != null) {
            resultsPagingWidget.updateCounts(results);
            resultsProgressBar.updateProgress(results);
            aboveTablePanel.add(rowInfoPanel);
            belowTablePanel.add(pagingPanel);
            belowTablePanel.setCellHorizontalAlignment(pagingPanel, HasHorizontalAlignment.ALIGN_RIGHT);
            aboveTablePanel.setCellVerticalAlignment(rowInfoPanel, HasVerticalAlignment.ALIGN_BOTTOM);
            aboveTablePanel.setCellHorizontalAlignment(rowInfoPanel, HasHorizontalAlignment.ALIGN_RIGHT);
            tablePagingPanel.add(resultsProgressBar);
            add(tablePagingPanel);
        }

        addStyles();
    }

    private void addRowsPerPageListBox() {
        HTML rowsPerPage = new HTML("Rows per Page:");
        aboveTablePanel2.add(rowsPerPage);
        aboveTablePanel2.setCellHorizontalAlignment(rowsPerPage, HasHorizontalAlignment.ALIGN_RIGHT);
        rowsPerPageLB = new ListBox();
        rowsPerPageLB.addItem("25");
        rowsPerPageLB.addItem("50");
        rowsPerPageLB.addItem("100");
        switch (results.getRowsPerPage()) {
            case 25:
                rowsPerPageLB.setSelectedIndex(0);
                break;
            case 50:
                rowsPerPageLB.setSelectedIndex(1);
                break;
            case 100:
                rowsPerPageLB.setSelectedIndex(2);
                break;
            default:
                throw new IllegalStateException("Weird man, it's not supposed to do that");
        }
        rowsPerPageLB.addChangeListener(new ChangeListener() {
            public void onChange(Widget sender) {
                int rows = Integer.parseInt(rowsPerPageLB.getValue(rowsPerPageLB.getSelectedIndex()));
                pagingController.setRowsPerPage(rows);
            }
        });
        aboveTablePanel2.add(rowsPerPageLB);
        aboveTablePanel2.setCellHorizontalAlignment(rowsPerPageLB, HasHorizontalAlignment.ALIGN_RIGHT);
    }

    private void addCopyToFilterLink(String copyWhat) {
        copyToFilterLink = new HTML("Copy " + copyWhat + " to Criteria");
        copyToFilterLink.addStyleName("marginLeft10px");
        copyToFilterLink.addStyleName("marginRight10px");
        copyToFilterLink.addStyleName("action");
        copyToFilterLink.addStyleName("darkBlueText");
        String ttKey = null;
        if (copyWhat.equalsIgnoreCase("genes")) { //todo  Ugh. smelly
            ttKey = AnomalySearchConstants.TOOLTIPKEY_RESULTS_GENECOPYCHECKEDTOSEARCH;
        } else if (copyWhat.equalsIgnoreCase("patients")) {
            ttKey = AnomalySearchConstants.TOOLTIPKEY_RESULTS_PATIENTCOPYCHECKEDTOSEARCH;
        }
        String ttText = TooltipTextMap.getInstance().get(ttKey);
        TooltipListener ttListener = new TooltipListener(new HTML(ttText));
        copyToFilterLink.addMouseListener(ttListener);
        belowTablePanel.add(copyToFilterLink);
    }
    
    private void addLinkToExport() {

        exportLink = new HTML("Export Table");
        exportLink.addStyleName("marginLeft10px");
        exportLink.addStyleName("action");
        exportLink.addStyleName("darkBlueText");

        // the link is not visible until the full result set is available
        exportLink.setVisible(false);

        exportLink.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                pagingController.writeExportData();
            }
        });

        String ttText = TooltipTextMap.getInstance().get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_EXPORTDATA);
        exportLink.addMouseListener(new TooltipListener(new HTML(ttText)));
        aboveTablePanel2.add(exportLink);

    }


    public HTML getExportLinkWidget() {
        return exportLink;
    }

    private boolean shouldShowPercentWidget(boolean showPercentWidget) {
        if (results != null) {
            if (results.getColumnTypes().size() < 1) {
                return false;
            }
        }
        return showPercentWidget;
    }

    public void replaceTable(Results results, ResultsTable table) {
        transferCheckBoxes(this.table, table);
        int idx = tablePagingPanel.getWidgetIndex(this.table);
        tablePagingPanel.remove(idx);
        this.table = table;
        tablePagingPanel.insert(table, idx);
        resultsPagingWidget.updateCounts(results, true);
        resultsProgressBar.updateProgress(results);
    }

    private void transferCheckBoxes(ResultsTable oldTable, ResultsTable newTable) {
        if (oldTable.results != newTable.results) {
            //assumption is broken, do nothing
            return;
        }
        //header
        Widget w1 = oldTable.getWidget(0, 0);
        Widget w2 = newTable.getWidget(0, 0);
        if (w1 instanceof SortingLinkWithCheckbox && w2 instanceof SortingLinkWithCheckbox) {
            boolean isChecked = ((SortingLinkWithCheckbox) w1).isChecked();
            ((SortingLinkWithCheckbox) w2).setChecked(isChecked);
        }
        //rows
        for (int row = 1, max = oldTable.getRowCount(); row < max; row++) {
            w1 = oldTable.getWidget(row, 0);
            w2 = newTable.getWidget(row, 0);
            if (w1 instanceof CheckBox && w2 instanceof CheckBox) {
                boolean isChecked = ((CheckBox) w1).isChecked();
                ((CheckBox) w2).setChecked(isChecked);
            }
        }
    }

    public void addPagingListeners(ClickListener prevPageListener, ClickListener nextPageListener, ClickListener firstPageListener, ClickListener lastPageListener, ChangeListener pageChangeListener) {
        resultsPagingWidget.addPreviousPageListener(prevPageListener);
        resultsPagingWidget.addNextPageListener(nextPageListener);
        resultsPagingWidget.addFirstPageListener(firstPageListener);
        resultsPagingWidget.addPageChangeListener(pageChangeListener);
        resultsPagingWidget.addLastPageListener(lastPageListener);
    }

    public int getCurrentPage() {
        return resultsPagingWidget.getCurrentPage();
    }

    public int getLastPage() {
        return resultsPagingWidget.getLastPage();
    }

    private void addStyles() {
        if (viewResultsAsPanel != null) {
            viewResultsAsPanel.addStyleName(StyleConstants.MARGIN_BOTTOM_5PX);
        }
        setCellHorizontalAlignment(tablePagingPanel, HasHorizontalAlignment.ALIGN_LEFT);
        addStyleName("paddingLeft10px");
    }

    public String getTabText() {
        return listByStr();
    }

    /**
     * Returns an identifier used as a tab ID 
     * @return Tab Identifier
     */
    public String getTabId() {
        return listByStr();
    }

    private String listByStr() {
        String name = null;
        switch (pagingController.getListBy()) {
            case Genes:
                name = AnomalySearch.dataBrowserConstants.genes();
                break;
            case Patients:
                name = AnomalySearch.dataBrowserConstants.patients();
                break;
            case Pathways:
                name = AnomalySearch.dataBrowserConstants.pathways();
                break;
        }
        return name;
    }

    //todo  don't really like the pattern of injecting the listener anymore -
    //todo  instead should use pointer to ModeController
    void addViewPercentageClickListener(ClickListener listener) {
        if (viewPercentageRadioButton != null) {
            viewPercentageRadioButton.addClickListener(listener);
        }
    }

    public void addViewRatioClickListener(ClickListener listener) {
        if (viewRatioRadioButton != null) {
            viewRatioRadioButton.addClickListener(listener);
        }
    }

    public void addCopyToFilterClickListener(ClickListener listener) {
        if (copyToFilterLink != null) {
            copyToFilterLink.addClickListener(listener);
        }
    }

    public void addRowsPerPageChangeListener(ChangeListener listener) {
        if (rowsPerPageLB != null) {
            rowsPerPageLB.addChangeListener(listener);
        }
    }

    //todo  not sure I like this lack of encapsulation
    public ResultsTable getResultsTable() {
        return table;
    }

    public void updatePageCounts(Results resultsMeta) {
        resultsPagingWidget.updateCounts(resultsMeta, false);
    }

    public void updateProgressBar(Results resultsMeta) {
        resultsProgressBar.updateProgress(resultsMeta);
    }

    //todo  don't really need this anymore since we're not chunking
    void enableExportLink() {
        if (results.isFinalRowCount()) {
            Widget exportLinkWidget = getExportLinkWidget();
            exportLinkWidget.setVisible(true);
        }
    }

    public FilterSpecifier.ListBy getListBy() {
        return pagingController.getListBy();
    }
}
