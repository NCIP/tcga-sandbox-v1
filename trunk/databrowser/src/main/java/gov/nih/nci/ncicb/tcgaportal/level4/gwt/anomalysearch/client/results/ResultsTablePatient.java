/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.user.client.ui.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.FilterPanel;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.*;
import static gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants.HEADER_PATIENT;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultDouble;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MutationType;

/**
 * Results table for patient-based results.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

//todo  there may be enough difference between the pivot and not-pivot case to split this into two classes
public class ResultsTablePatient extends ResultsTable {

    //todo  general utility for showing boolean icons?

    private ClickListener pivotLinkListener;

    class PairedWidget extends Composite {
        private HTML colValueHtml;
        private Image pairedImage = new Image(FilterPanel.IMAGES_PATH + "paired.gif", 0, 0, 16, 16);

        public PairedWidget(String colValue, ClickListener pivotLinkListner, HyperlinkHTML link) {

            if(pivotLinkListner != null) {
                colValueHtml = link;
            }else {
                colValueHtml = new HTML();
                colValueHtml.setHTML(colValue);
            }
            HorizontalPanel panel = new HorizontalPanel();
            panel.add(colValueHtml);
            panel.add(pairedImage);
            pairedImage.setTitle("Data is based on paired tumor/normal results");
            pairedImage.addStyleName(StyleConstants.MARGIN_RIGHT_10PX);
            panel.setCellHorizontalAlignment(pairedImage, HasHorizontalAlignment.ALIGN_RIGHT);
            panel.setWidth("100%");

            initWidget(panel);
        }
    }

    public ResultsTablePatient(Results results, ClickListener hyperlinkListener, SortController sc) {
        this(results, hyperlinkListener, sc, false);
    }

    public ResultsTablePatient(Results results, ClickListener hyperlinkListener, SortController sc, boolean isPivot) {
        super();
        this.pivotLinkListener = hyperlinkListener;
        this.isPivot = isPivot;
        init(results, sc);
        populateTable();
        addResultTableStyles();
    }

    protected void populateRowHeaderCell(int resultsRow) {
        int displayRow = resultsRow + 1;
        CheckBox patientCb = new CheckBox(results.getRow(resultsRow).getName());
        setWidget(displayRow, 0, patientCb);
        getCellFormatter().addStyleName(displayRow, 0, StyleConstants.RESULTS_TABLE_CELL);
    }

    protected void addConstantHeaders() {
        String ttText = TooltipTextMap.getInstance().get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_PATIENTID);
        String headerText = makeHeaderText(HEADER_PATIENT, -1, SortSpecifier.FAKE_ANNOTATION_ROW_ID);
        final SortingLinkWithCheckbox patientCb = new SortingLinkWithCheckbox(headerText, -1, SortSpecifier.FAKE_ANNOTATION_ROW_ID, true, ttText);
        WidgetHelper.setDomId(patientCb, SeleniumTags.SELECTALLGENES_CHECKBOX);
        patientCb.activateCheckbox(new ClickListener() {
            public void onClick(Widget sender) {
                checkAllItems(patientCb.isChecked());
            }
        });
        setWidget(0, 0, patientCb);
        getCellFormatter().addStyleName(0, 0, StyleConstants.RESULTS_TABLE_HEADER);
    }

    protected void addDynamicHeaders() {
        int col = 1;
        for (ColumnType ct : results.getColumnTypes()) {
            String ttText = getTooltipTextForColumn(ct);
            String headerText = makeHeaderText(ct);
            SortingLink link = new SortingLink(headerText, ct.getId(), null, false, ttText);
            setWidget(0, col, link);
            getCellFormatter().addStyleName(0, col, StyleConstants.RESULTS_TABLE_HEADER);
            col++;
        }
    }

    protected void populateDynamicCells(int resultsRow) {
        int displayRow = resultsRow + 1;
        ResultValue[] columnResultValues = results.getRow(resultsRow).getColumnResults();

        for (int resultsCol = 0; resultsCol < numCols; resultsCol++) {
            int displayCol = resultsCol + 1;
            Boolean pairedAnnotation = (Boolean) columnResultValues[resultsCol].getValueAnnotation(AnomalySearchConstants.VALUEANNOTATIONKEY_PAIRED);
            String cellValue = getCellStringValue(columnResultValues[resultsCol]);

            String pivotId;
            ColumnType pivotColumn;
            HyperlinkHTML link = null;
            if (pivotLinkListener != null) {
                ResultRow row = results.getRow(resultsRow);
                pivotColumn = results.getColumnTypes().get(resultsCol);
                pivotId = getPivotId(row, pivotColumn);
                link = new HyperlinkHTML(cellValue, pivotId, pivotColumn);
                link.addStyleName("darkBlueText");
                link.addClickListener(pivotLinkListener);
                String ttText = TooltipTextMap.getInstance().get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_PIVOTFROMPATIENT);
                link.addMouseListener(new TooltipListener(new HTML(ttText)));
            }

            if (pairedAnnotation != null && pairedAnnotation) {
                PairedWidget pairedValue = new PairedWidget(cellValue, pivotLinkListener, link);
                setWidget(displayRow, displayCol, pairedValue);
            } else {
                if (pivotLinkListener != null) {
                    setWidget(displayRow, displayCol, link);
                } else {
                    ColumnType col = results.getColumnTypes().get(resultsCol);
                    if (isPivot && col instanceof MutationType) {
                        //show "yes" instead of "1"
                        double d = ((ResultDouble)columnResultValues[resultsCol]).getValue();
                        cellValue = (d==1. ? "yes" : "no");
                    }
                    setText(displayRow, displayCol, cellValue);
                }
                getCellFormatter().addStyleName(displayRow, displayCol, StyleConstants.RESULTS_TABLE_CELL);
            }
            getCellFormatter().addStyleName(displayRow, displayCol, StyleConstants.RESULTS_TABLE_CELL);
            getCellFormatter().addStyleName(displayRow, displayCol, StyleConstants.RESULTS_TABLE_DYNAMIC);
        }
    }

    protected void populateConstantCells(int resultsRow) {
    }

    private void addResultTableStyles() {
        String tableHeaderColorStyle = isPivot ? StyleConstants.GREEN_RESULTS_TABLE_HEADER : StyleConstants.BLUE_RESULTS_TABLE_HEADER;
        this.getRowFormatter().addStyleName(0, StyleConstants.RESULTS_TABLE_HEADER);
        this.getRowFormatter().addStyleName(0, tableHeaderColorStyle);
    }

    private String getPivotId(ResultRow row, ColumnType pivotColumn) {
        // added a separate method for future, just in case if we need to add some
        // complicated logic here like gene search
        return row.getName(); // patient ID
    }

}
