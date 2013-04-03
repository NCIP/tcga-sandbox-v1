/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.GWTNumberFormatter;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.SortingLink;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.SortingLinkWithCheckbox;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.StyleConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.TooltipTextMap;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.NumberFormatter;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultDouble;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.*;

/**
 * A GWT FlexTable for visualization of results.  It uses JavaScript to create an HTML rendering
 * of a result set.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class ResultsTable extends FlexTable {
    protected static final boolean SUPPRESS_REPEATS = true; //maybe eventually will want to give user control over this

    protected Results results;
    protected int numCols;
    protected SortController sortController;
    protected boolean isPivot;
    private NumberFormatter decimalFormatter;

    public ResultsTable() {
        this.decimalFormatter = new GWTNumberFormatter("#####0.000");
    }

    protected void init(Results results, SortController sc) {
        this.results = results;
        this.sortController = sc;

        numCols = 0;
        if (results.getColumnTypes() != null) {  //can be null in the case of pathways
            numCols = results.getColumnTypes().size();
        }
        addStyleName("resultsTable");
    }

    protected void populateTable() {
        populateResultTableHeaders();
        populateResultTableRows();
        enableSortingOrNot();
        colorEvenRows();
    }

    private void populateResultTableHeaders() {
        addConstantHeaders();
        addDynamicHeaders();
    }

    protected abstract void addConstantHeaders();

    protected abstract void addDynamicHeaders();

    private void populateResultTableRows() {
        for (int resultsRow = 0; resultsRow < results.getActualRowCount(); resultsRow++) {
            populateRowHeaderCell(resultsRow);
            populateDynamicCells(resultsRow);
            populateConstantCells(resultsRow);
        }
    }

    protected abstract void populateRowHeaderCell(int resultsRow);

    protected abstract void populateDynamicCells(int resultsRow);

    protected abstract void populateConstantCells(int resultsRow);

    /**
     * Gets the string value from a ResultValue instance, using a number formatter for real numbers
     * @param rv
     * @return
     */
    protected String getCellStringValue(ResultValue rv) {
        String cellValue;
        if (rv instanceof ResultDouble) {
            cellValue = ((ResultDouble)rv).toString(decimalFormatter);
        } else {
            cellValue = rv.toString();
        }
        return cellValue;
    }

    //todo  move into subclasses?
    protected void checkAllItems(boolean check) {
        for (int irow = 0; irow < results.getActualRowCount(); irow++) {
            Widget firstColWidget = getWidget(irow + 1, 0);
            if (firstColWidget instanceof CheckBox) {
                ((CheckBox) firstColWidget).setChecked(check);
            }
        }
    }

    private void enableSortingOrNot() {
        for (int icol = 0; icol < getCellCount(0); icol++) {
            Widget w = getWidget(0, icol);
            if (w instanceof SortingLink) {
                final SortingLink link = (SortingLink) w;
                link.activateLink(new ClickListener() {
                    public void onClick(Widget sender) {
                        sortController.sort(link.getColumnId(), link.getAnnotationName(), link.isInitialAscending());
                    }
                });
            }
        }
    }

    //used for "dynamic" results columns, not constant columns
    //todo  get strings from consts class
    protected String getTooltipTextForColumn(ColumnType ctype) {
        String key = null, ttText = null;
        if (ctype instanceof CopyNumberType) {
            if (((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                key = AnomalySearchConstants.TOOLTIPKEY_RESULTS_CNGENE;
            } else {
                key = AnomalySearchConstants.TOOLTIPKEY_RESULTS_CNMIRNA;
            }
        } else if (ctype instanceof ExpressionType) {
            if (((AnomalyType) ctype).getGeneticElementType() == AnomalyType.GeneticElementType.Gene) {
                key = AnomalySearchConstants.TOOLTIPKEY_RESULTS_EXPGENE;
            } else {
                key = AnomalySearchConstants.TOOLTIPKEY_RESULTS_EXPMIRNA;
            }
        } else if (ctype instanceof MutationType) {
            key = AnomalySearchConstants.TOOLTIPKEY_RESULTS_MUTATION;
        } else if (ctype instanceof CorrelationType) {
            key = AnomalySearchConstants.TOOLTIPKEY_RESULTS_CORRELATION;
        } else if (ctype instanceof MethylationType) {
            key = AnomalySearchConstants.TOOLTIPKEY_RESULTS_METHYLATION;
        }
        if (key != null) {
            ttText = TooltipTextMap.getInstance().get(key);
        }
        return ttText;
    }

    protected String makeListFromCheckedRows() {
        StringBuilder ret = new StringBuilder();
        for (int irow = 0, max = results.getActualRowCount(); irow < max; irow++) {
            Widget w = getWidget(irow + 1, 0);
            if (w instanceof CheckBox) {
                CheckBox tableItem = (CheckBox) w;
                if (tableItem.isChecked()) {
                    if (ret.length() != 0) {
                        ret.append(", ");
                    }
                    ret.append(tableItem.getText());
                    tableItem.setChecked(false);
                }
            }
        }
        //and the top check box
        Widget w = getWidget(0, 0);
        if (w instanceof SortingLinkWithCheckbox) {
            ((SortingLinkWithCheckbox) w).setChecked(false);
        }
        return ret.toString();
    }

    //makes the header text, including up or down arrow for sort
    protected String makeHeaderText(ColumnType ct) {
        String text = ct.getDisplayName();
        if (isPivot && ct instanceof MutationType) {
            //add "found" because we're going to display yes/no instead of 1/0
            text += " found";
        }
        return makeHeaderText(text, ct.getId(), null);
    }

    //makes the header text, including up or down arrow for sort
    protected String makeHeaderText(String text, long columnId, String annotation) {
        int sortOrder = sortController.getCurrentSortOrderForColumn(columnId, annotation);
        if (sortOrder == SortController.SORTED_ASCENDING) {
            text += " <span style=\"color:orangered;\">&uarr;</span>";
        } else if (sortOrder == SortController.SORTED_DESCENDING) {
            text += " <span style=\"color:orangered;\">&darr;</span>";
        }
        return text;
    }

    protected void colorEvenRows() {
        int numRows = getRowCount();

        //if the first column value is empty i.i it is a repeating gene row,
        // then check what the previous row was set to and use the same style 

        boolean previousStyleEven = true;
        boolean continueSameStyle;
        int colsInFirstRow = getCellCount(0);

        for (int i = 1; i < numRows; i++) { // skip header
            getRowFormatter().removeStyleName(i, StyleConstants.EVEN_ROW);
            continueSameStyle = getCellCount(i) < colsInFirstRow;

            if (!continueSameStyle) {
                if (!previousStyleEven) {
                    getRowFormatter().addStyleName(i, StyleConstants.EVEN_ROW);
                    previousStyleEven = true;
                } else {
                    previousStyleEven = false;
                }
            } else {
                if (previousStyleEven) {
                    getRowFormatter().addStyleName(i, StyleConstants.EVEN_ROW);
                }
            }
        }
    }

    //suppress within column if text is the same as previous and gene/patient name is the same
    protected boolean suppressRepeatingText(int resultsCol, int resultsRow) {
        boolean suppress = false;
        if (SUPPRESS_REPEATS && resultsRow > 0 && isGeneLevelColumn(resultsCol)) {
            String thisRowName = results.getRow(resultsRow).getName();  //gene or patient
            String prevRowName = results.getRow(resultsRow - 1).getName();
            if (thisRowName.equals(prevRowName)) {
                suppress = true;
            }
        }
        return suppress;
    }

    //for CNV icon and gene location
    protected boolean suppressRepeatingConstants(int resultsRow) {
        boolean suppress = false;
        if (SUPPRESS_REPEATS && resultsRow > 0) {
            String thisRowName = results.getRow(resultsRow).getName();  //gene or patient
            String prevRowName = results.getRow(resultsRow - 1).getName();
            if (thisRowName.equals(prevRowName)) {
                suppress = true;
            }
        }
        return suppress;
    }

    private boolean isGeneLevelColumn(int resultsCol) {
        boolean geneLevel = true;
        ColumnType ctype = results.getColumnTypes().get(resultsCol);
        if (ctype instanceof AnomalyType) {
            AnomalyType.GeneticElementType getype = ((AnomalyType) ctype).getGeneticElementType();
            if (getype == AnomalyType.GeneticElementType.miRNA || getype == AnomalyType.GeneticElementType.MethylationProbe) {
                geneLevel = false;
            }
        }
        return geneLevel;
    }

}
