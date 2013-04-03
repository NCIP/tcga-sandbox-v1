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
public class ModeControllerGene extends ModeController {

    public static final String MODE_NAME = AnomalySearch.dataBrowserConstants.genes();
    boolean showPercent = true;

    public String getModeName() {
        return MODE_NAME;
    }

    public String getToggleButtonUpStyleName() {
        return StyleConstants.GREEN_MODE_BUTTON_UP;
    }

    public String getToggleButtonDownStyleName() {
        return StyleConstants.GREEN_MODE_BUTTON_DOWN;
    }

    public String getModeColorSchemeClass() {
        return "green";
    }

    protected void setModeStyle() {
        tabPanel.addStyleName(StyleConstants.GREEN_TAB_PANEL);
    }

    public FilterSpecifier.ListBy getListBy() {
        return FilterSpecifier.ListBy.Genes;
    }

    protected ResultsPagingPanel displayResults() {
        setDisplayFlags();

        ResultsTable table = new ResultsTableGene(results, new PivotHyperlinkListener(), this, false);
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
                pivotId = ((Hyperlink) sender).getTargetHistoryToken();
            } else if (sender instanceof HyperlinkHTML) {  //our own hack to allow tooltips
                pivotId = ((HyperlinkHTML)sender).getTargetHistoryToken();
                pivotColumn = ((HyperlinkHTML)sender).getColumn();
            }
            if (pivotId != null) {
                retrievePivotTableIntoNewTab(pivotId, pivotColumn);
            }
        }
    }

    private void retrievePivotTableIntoNewTab(final String geneId, final ColumnType pivotColumn) {
        AsyncCallback<Results> callback = new AsyncCallback<Results>() {
            public void onFailure(Throwable caught) {
                filterPanel.showError(caught.getMessage(), true);
                filterPanel.stopSearchSpinner();
            }

            public void onSuccess(Results result) {
                createTabForPatientPivot(result, geneId, pivotColumn.getId()+"");
                filterPanel.stopSearchSpinner();
            }
        };

        filterPanel.startSearchSpinner();

        FilterSpecifier filterClone = new FilterSpecifier(results);
        List<ColumnType> columnTypesList = filterClone.getColumnTypes();
        // remove all the columns other than the one related to this pivot
        removePivotColumn(columnTypesList, pivotColumn);

        createSpinnerTab(AnomalySearch.dataBrowserConstants.patients() + " for " + geneId, geneId +"[" + pivotColumn.getId()+"" + "]");
        searchService.getPivotPage(getListBy(), geneId, filterClone, callback);
        
    }

    private void createTabForPatientPivot(Results pivotResult, String geneId, String pivotColumnId) {
        ResultsPivotPanel pivotPanel = new ResultsPivotPanel(pivotResult,
                AnomalySearch.dataBrowserConstants.patients() + " for " + geneId , 
                geneId +"[" + pivotColumnId + "]",
                this, FilterSpecifier.ListBy.Patients);
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
        ResultsTable table = new ResultsTableGene(results, new PivotHyperlinkListener(), this, false);
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
        // gene allows all displays
        return true;
    }

}
