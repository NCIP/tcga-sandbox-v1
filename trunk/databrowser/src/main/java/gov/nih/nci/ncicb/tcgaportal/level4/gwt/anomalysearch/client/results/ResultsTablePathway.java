/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.SortingLink;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.StyleConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.*;
import static gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants.HEADER_PATHWAY;
import static gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants.HEADER_P_VALUE;

/**
 * ResultsTable for pathway-based results.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ResultsTablePathway extends ResultsTable {

    ClickListener hyperlinkListener;

    public ResultsTablePathway() {
    }

    public ResultsTablePathway(Results results, ClickListener hyperlinkListener, ModeController mc) {
        super();
        init(results, mc);
        this.hyperlinkListener = hyperlinkListener;
        populateTable();
        addResultTableStyles();
    }

    protected void populateRowHeaderCell(int resultsRow) {
        int displayRow = resultsRow + 1;
        //add hyperlink for pathway
        String pathwayName = results.getRow(resultsRow).getName();
        int pathwayId = (Integer) results.getRow(resultsRow).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAYID);
        Hyperlink link = new Hyperlink(pathwayName, Integer.toString(pathwayId));
        link.addClickListener(hyperlinkListener);
        setWidget(displayRow, 0, link);
        getCellFormatter().addStyleName(displayRow, 0, StyleConstants.RESULTS_TABLE_CELL);
        getCellFormatter().addStyleName(displayRow, 0, StyleConstants.PURPLE_TEXT);
    }

    protected void addConstantHeaders() {
        String ttText = TooltipTextMap.getInstance().get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_PATHWAYNAME);
        String headerText = makeHeaderText(HEADER_PATHWAY, -1, SortSpecifier.FAKE_ANNOTATION_ROW_ID);
        SortingLink pathwayLink = new SortingLink(headerText, -1, SortSpecifier.FAKE_ANNOTATION_ROW_ID, true, ttText);
        setWidget(0, 0, pathwayLink);
        getCellFormatter().addStyleName(0, 0, StyleConstants.RESULTS_TABLE_HEADER);
        if (results.getActualRowCount() != 0) {
            if (results.getRow(0).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAY_FISHER) != null) {
                ttText = TooltipTextMap.getInstance().get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_PATHWAYSIGNIF);
                headerText = makeHeaderText(HEADER_P_VALUE, -1, AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAY_FISHER);
                SortingLink fischerLink = new SortingLink(headerText, -1, AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAY_FISHER, true, ttText);
                setWidget(0, 1, fischerLink);
                getCellFormatter().addStyleName(0, 1, StyleConstants.RESULTS_TABLE_HEADER);
            }
        }
    }

    protected void addDynamicHeaders() {
        //empty implementation because
        //pathway results have no dynamic columns, only constants
    }

    protected void populateDynamicCells(int resultsRow) {
        //empty implementation because
        //pathway results have no dynamic columns, only constants
    }

    protected void populateConstantCells(int resultsRow) {
        int displayRow = resultsRow + 1;
        ResultRow row = results.getRow(resultsRow);
        Double fischer = (Double) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAY_FISHER);
        if (fischer != null) {
            String sFischer = NumberFormat.getScientificFormat().format(fischer);
            setText(displayRow, 1, sFischer);
            getCellFormatter().addStyleName(displayRow, 1, StyleConstants.RESULTS_TABLE_CELL);
        }
    }

    private void addResultTableStyles() {
        this.getRowFormatter().addStyleName(0, StyleConstants.RESULTS_TABLE_HEADER);
        this.getRowFormatter().addStyleName(0, StyleConstants.PURPLE_RESULTS_TABLE_HEADER);
    }

}
