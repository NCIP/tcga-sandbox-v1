/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearch;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.AnomalyDisplay;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.CorrelationDisplay;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.HyperlinkHTML;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.StyleConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;

import java.util.List;

/**
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ModeControllerPatient extends ModeController {

    public static final String MODE_NAME = AnomalySearch.dataBrowserConstants.patients();
    boolean showPercent = true;
    boolean isSearchBtnEnabled;

    public String getModeName() {
        return MODE_NAME;
    }

    public String getToggleButtonUpStyleName() {
        return StyleConstants.BLUE_MODE_BUTTON_UP;
    }

    public String getToggleButtonDownStyleName() {
        return StyleConstants.BLUE_MODE_BUTTON_DOWN;
    }

    public String getModeColorSchemeClass() {
        return "blue";
    }

    protected void setModeStyle() {
        tabPanel.addStyleName(StyleConstants.BLUE_TAB_PANEL);
    }

    public FilterSpecifier.ListBy getListBy() {
        return FilterSpecifier.ListBy.Patients;
    }

    protected ResultsPagingPanel displayResults() {
        setDisplayFlags();
        ResultsTable table = new ResultsTablePatient(results, new PivotHyperlinkListener(), this);
        ResultsPagingPanel panel = new ResultsPagingPanel(results, table, this, true);
        panel.addViewPercentageClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                togglePercent(true);
            }
        });
        panel.addViewRatioClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                togglePercent(false);
            }
        });

        tabPanel.setMainResultsPanel(panel, getModeColorSchemeClass());
        return panel;
    }

    class PivotHyperlinkListener implements ClickListener {
        public void onClick(Widget sender) {
            String pivotId = null;
            ColumnType pivotColumn = null;
            
            if (sender instanceof Hyperlink) {
                pivotId = ((Hyperlink)sender).getTargetHistoryToken();
            } else if (sender instanceof HyperlinkHTML) {
                pivotId = ((HyperlinkHTML)sender).getTargetHistoryToken();
                pivotColumn = ((HyperlinkHTML)sender).getColumn();                
            }
            if (pivotId != null) {
                retrievePivotTableIntoNewTab(pivotId, pivotColumn);
            }
        }
    }

    private void retrievePivotTableIntoNewTab(final String patientId, final ColumnType pivotColumn) {
        AsyncCallback<Results> callback = new AsyncCallback<Results>() {
            public void onFailure(Throwable caught) {
                filterPanel.showError(caught.getMessage(), true);
                filterPanel.stopSearchSpinner();
            }

            public void onSuccess(Results result) {
                createTabForGenePivot(result, patientId, String.valueOf(pivotColumn.getId()));
                filterPanel.stopSearchSpinner();
            }
        };

        filterPanel.startSearchSpinner();
        FilterSpecifier filterClone = new FilterSpecifier(results);
        // remove all the columns other than the one related to this pivot
        List<ColumnType> columnTypesList = filterClone.getColumnTypes();
        removePivotColumn(columnTypesList, pivotColumn);
        createSpinnerTab("Genes for " + patientId, patientId+"[" + String.valueOf(pivotColumn.getId()) + "]");
        searchService.getPivotPage(getListBy(), patientId, filterClone, callback);
    }

    private void createTabForGenePivot(Results pivotResult, String patientId, String pivotColumnId) {
        ResultsPivotPanel pivotPanel = new ResultsPivotPanel(pivotResult, "Genes for " + patientId,
                                    patientId +"[" + pivotColumnId + "]",this, FilterSpecifier.ListBy.Genes);
        tabPanel.addOrReplaceTab(pivotPanel, getModeColorSchemeClass());
        hookupCopyToFilterListener(pivotPanel);
    }

    //just replaces the table without impacting the rest of the display
    private void togglePercent(boolean showPercent) {
        this.showPercent = showPercent;
        updateResults();
    }

    protected void updateResults() {
        setDisplayFlags();
        ResultsTable table = new ResultsTablePatient(results, new PivotHyperlinkListener(), this);
        mainResultsPanel.replaceTable(results, table);
    }

    private void setDisplayFlags() {
        results.clearDisplayFlags();
        if (showPercent) {
            results.addDisplayFlag(AnomalySearchConstants.RESULTSDISPLAYFLAG_PERCENT);
        } else {
            results.addDisplayFlag(AnomalySearchConstants.RESULTSDISPLAYFLAG_RATIO);
        }
    }

    public boolean allowsAnomalyType(AnomalyDisplay anomalyDisplay) {
        return !(anomalyDisplay instanceof CorrelationDisplay);
    }
}
