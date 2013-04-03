/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter.FilterPanel;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.*;
import static gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultDouble;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.AnomalyType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MutationType;

import java.util.HashMap;
import java.util.Map;

/**
 * Results table for gene-based results.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

//todo  there may be enough difference between the pivot and not-pivot case to split this into two classes
public class ResultsTableGene extends ResultsTable {

    private int numRepeats = 1;
    private int firstRowOfRepeat = 0;

    private ClickListener pivotLinkListener;
    private Map<Integer, Boolean> rowHasSuppressedGene = new HashMap<Integer, Boolean>();

    private boolean hasBiocartaIds, hasMirna, hasMethylation;
    private int mirnaColIdx, methylColIdx, firstDynamicIdx; //todo  change names, that "dynamic" gives wrong impression

    public ResultsTableGene(Results results, ClickListener hyperlinkListener, SortController sc, Boolean filterExists) {
        this(results, hyperlinkListener, sc, filterExists, false);
    }

    public ResultsTableGene(Results results, ClickListener hyperlinkListener, SortController sc, Boolean filterExists, boolean isPivot) {
        super();
        if (filterExists) {
            results = results.cloneResults(true);
        }
        this.pivotLinkListener = hyperlinkListener;
        this.isPivot = isPivot;
        init(results, sc);
        populateTable();
        addResultTableStyles();
    }

    protected void populateRowHeaderCell(int resultsRow) {
        int displayRow = resultsRow + 1;
        if (!suppressRepeatingConstants(resultsRow)) {
            rowHasSuppressedGene.put(displayRow, false);
            firstRowOfRepeat = displayRow;
            numRepeats = 1;
            ResultRow row = results.getRow(resultsRow);
            String geneId = row.getName();
            if (results.getRow(resultsRow).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_URL) != null) {
                geneId = new StringBuilder().append("<a href=").
                        append(results.getRow(resultsRow).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_URL)).
                        append(" target='_geneInfo'>").append(geneId).append("</a>").toString();
            }
            CheckBox geneCb = new CheckBox(geneId, true);
            WidgetHelper.setDomId(geneCb, SeleniumTags.SELECTGENE_CHECKBOX_PREFIX + geneId);
            setWidget(displayRow, 0, geneCb);
            getCellFormatter().addStyleName(displayRow, 0, StyleConstants.RESULTS_TABLE_CELL);
            getCellFormatter().setVerticalAlignment(displayRow, 0, HasVerticalAlignment.ALIGN_TOP);
            if (hasBiocartaIds) {
                String bcId = (String) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_BCGENE);
                if (bcId == null) bcId = ""; //shouldn't happen
                setText(displayRow, 1, bcId);
                getCellFormatter().addStyleName(displayRow, 1, StyleConstants.RESULTS_TABLE_CELL);
            }
        } else {
            numRepeats++;
            getFlexCellFormatter().setRowSpan(firstRowOfRepeat, 0, numRepeats);
            rowHasSuppressedGene.put(displayRow, true);
        }
    }

    private boolean copyNumberColTypeExists() {
        for (ColumnType cType : results.getColumnTypes()) {
            if (cType instanceof CopyNumberType) {
                return true;
            }
        }
        return false;
    }

    protected void addConstantHeaders() {
        //first column: gene id
        String ttText = TooltipTextMap.getInstance().get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_GENESYMBOL);
        String headerText = makeHeaderText(HEADER_GENE, -1, SortSpecifier.FAKE_ANNOTATION_ROW_ID);
        final SortingLinkWithCheckbox geneCb = new SortingLinkWithCheckbox(headerText, -1, SortSpecifier.FAKE_ANNOTATION_ROW_ID, true, ttText);
        WidgetHelper.setDomId(geneCb, SeleniumTags.SELECTALLGENES_CHECKBOX);
        geneCb.activateCheckbox(new ClickListener() {
            public void onClick(Widget sender) {
                checkAllItems(geneCb.isChecked());
            }
        });
        int nextCol = 0;
        setWidget(0, nextCol, geneCb);
        getCellFormatter().addStyleName(0, nextCol, StyleConstants.RESULTS_TABLE_HEADER);
        nextCol++;

        //next column: biocarta Id, if exists
        if (results.getActualRowCount() > 0) { //can be empty in case of pathway page
            ResultRow firstRow = results.getRow(0);
            if (firstRow != null && firstRow.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_BCGENE) != null) {
                hasBiocartaIds = true;
                ttText = TooltipTextMap.getInstance().get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_BIOCARTA);
                headerText = makeHeaderText(HEADER_BIOCARTA_ID, -1, AnomalySearchConstants.ROWANNOTATIONKEY_BCGENE);
                setWidget(0, nextCol, new SortingLink(headerText, -1, AnomalySearchConstants.ROWANNOTATIONKEY_BCGENE, true, ttText));
                getCellFormatter().addStyleName(0, nextCol, StyleConstants.RESULTS_TABLE_HEADER);
                nextCol++;
            }
        }

        for (int i = 0; i < results.getActualRowCount(); i++) {
            if (!hasMirna && results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA) != null) {
                hasMirna = true;
                ttText = TooltipTextMap.getInstance().get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_MIRNASYMBOL);
                headerText = makeHeaderText(AnomalyType.GeneticElementType.miRNA.toString(), -1, AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA);
                setWidget(0, nextCol, new SortingLink(headerText, -1, AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA, true, ttText));
                getCellFormatter().addStyleName(0, nextCol, StyleConstants.RESULTS_TABLE_HEADER);
                mirnaColIdx = nextCol;
                nextCol++;
            }

            if (!hasMethylation && results.getRow(i).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_METHYLATION_PROBE) != null) {
                hasMethylation = true;
                ttText = TooltipTextMap.getInstance().get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_METHYLATIONSYMBOL);
                headerText = makeHeaderText(AnomalyType.GeneticElementType.MethylationProbe.toString(), -1, AnomalySearchConstants.ROWANNOTATIONKEY_METHYLATION_PROBE);
                setWidget(0, nextCol, new SortingLink(headerText, -1, AnomalySearchConstants.ROWANNOTATIONKEY_METHYLATION_PROBE, true, ttText));
                getCellFormatter().addStyleName(0, nextCol, StyleConstants.RESULTS_TABLE_HEADER);
                methylColIdx = nextCol;
                nextCol++;
            }
        }

        firstDynamicIdx = nextCol;

        //some data-bound columns go in between

        //last two columns: cnv and region
        nextCol = firstDynamicIdx + numCols;
        if (copyNumberColTypeExists()) {
            ttText = TooltipTextMap.getInstance().get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_CNV);
            headerText = makeHeaderText(HEADER_CNV, -1, AnomalySearchConstants.ROWANNOTATIONKEY_CNV);
            setWidget(0, nextCol, new SortingLink(headerText, -1, AnomalySearchConstants.ROWANNOTATIONKEY_CNV, true, ttText));
            getCellFormatter().addStyleName(0, nextCol, StyleConstants.RESULTS_TABLE_HEADER);
            getCellFormatter().setAlignment(0, nextCol, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
            getCellFormatter().setWidth(0, nextCol, "35px");
            nextCol++;
        }

        ttText = TooltipTextMap.getInstance().get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_LOCATION);
        headerText = makeHeaderText("Gene Location", -1, SortSpecifier.FAKE_ANNOTATION_CHROMLOCATION);
        setWidget(0, nextCol, new SortingLink(headerText, -1, SortSpecifier.FAKE_ANNOTATION_CHROMLOCATION, true, ttText));
        getCellFormatter().addStyleName(0, nextCol, StyleConstants.RESULTS_TABLE_HEADER);
    }

    protected void addDynamicHeaders() {
        int col = firstDynamicIdx;
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
            int displayCol = firstDynamicIdx + resultsCol;
            if (rowHasSuppressedGene.get(displayRow)) {
                displayCol--;
            }
            Double pValueAnnotation = (Double) columnResultValues[resultsCol].getValueAnnotation(AnomalySearchConstants.VALUEANNOTATIONKEY_CORRELATION_PVALUE);
            String cellValue = getCellStringValue(columnResultValues[resultsCol]);

            if (pValueAnnotation != null) {
                String pValue = NumberFormat.getScientificFormat().format(pValueAnnotation);
                String celltext = cellValue + " (p-value:" + pValue + ")";
                setText(displayRow, displayCol, celltext);
                getCellFormatter().addStyleName(displayRow, displayCol, StyleConstants.RESULTS_TABLE_CORRELATION);
            } else {
                if (pivotLinkListener != null) {
                    ResultRow row = results.getRow(resultsRow);

                    String pivotId;
                    ColumnType pivotColumn = results.getColumnTypes().get(resultsCol);
                    pivotId = getPivotId(row, pivotColumn);
                    HyperlinkHTML link = new HyperlinkHTML(cellValue, pivotId, pivotColumn);
                    link.addStyleName("darkBlueText");
                    link.addClickListener(pivotLinkListener);
                    String ttText = TooltipTextMap.getInstance().get(AnomalySearchConstants.TOOLTIPKEY_RESULTS_PIVOTFROMGENE);
                    link.addMouseListener(new TooltipListener(new HTML(ttText)));
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
                getCellFormatter().addStyleName(displayRow, displayCol, StyleConstants.RESULTS_TABLE_CORRELATION);
            }
            getCellFormatter().addStyleName(displayRow, displayCol, StyleConstants.RESULTS_TABLE_CELL);
        }
    }

    protected void populateConstantCells(int resultsRow) {
        boolean moveColumnLeft = false;
        int displayRow = resultsRow + 1;
        if (rowHasSuppressedGene.get(displayRow)) {
            moveColumnLeft = true;
        }
        ResultRow row = results.getRow(resultsRow);

        //miRNA symbol
        if (hasMirna) {
            String mirnaSymbol = ((String)row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA)).toLowerCase();
            setText(displayRow, moveColumnLeft ? mirnaColIdx - 1 : mirnaColIdx, mirnaSymbol);
            getCellFormatter().addStyleName(displayRow, moveColumnLeft ? mirnaColIdx - 1 : mirnaColIdx, StyleConstants.RESULTS_TABLE_CELL);
            getCellFormatter().addStyleName(displayRow, moveColumnLeft ? mirnaColIdx - 1 : mirnaColIdx, StyleConstants.RESULTS_TABLE_DYNAMIC);
        }

        //methylation symbol
        if (hasMethylation) {
            setText(displayRow, moveColumnLeft ? methylColIdx - 1 : methylColIdx, (String) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_METHYLATION_PROBE));
            getCellFormatter().addStyleName(displayRow, moveColumnLeft ? methylColIdx - 1 : methylColIdx, StyleConstants.RESULTS_TABLE_CELL);
            getCellFormatter().addStyleName(displayRow, moveColumnLeft ? methylColIdx - 1 : methylColIdx, StyleConstants.RESULTS_TABLE_DYNAMIC);
        }

        int nextCol = firstDynamicIdx + numCols + (moveColumnLeft ? -1 : 0);

        //put CNV icon after CN cell
        if (copyNumberColTypeExists()) {
            //if (!suppressRepeatingConstants(resultsRow)) {
            Boolean cnvAnnotation = (Boolean) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_CNV);
            String cnvImageName = hasBiocartaIds ? "cnvPurple.gif" : "cnv.gif";
            Image cnvIcon = new Image(FilterPanel.IMAGES_PATH + cnvImageName, 0, 0, 16, 16);
            cnvIcon.setTitle("Copy Number Variation");
            if (cnvAnnotation != null && cnvAnnotation) {
                setWidget(displayRow, nextCol, cnvIcon);
            } else {
                setText(displayRow, nextCol, "");
            }
            //}
            getCellFormatter().addStyleName(displayRow, nextCol, StyleConstants.RESULTS_TABLE_CELL);
            getCellFormatter().setWidth(displayRow, nextCol, "35px");
            getCellFormatter().setAlignment(displayRow, nextCol, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
            nextCol++;
        }

        //chromosome region
        String locText = " ";
        if (row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM) != null) {
            String chrAnnotation = row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM).toString();
            String startAnnotation = row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_START).toString();
            String stopAnnotation = row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_STOP).toString();
            locText = "Chr " + chrAnnotation + ": " + startAnnotation + " - " + stopAnnotation;
        }
        /*  if (!suppressRepeatingConstants(resultsRow)) {
            setText(displayRow, nextCol, locText);
        }*/
        setText(displayRow, nextCol, locText);
        getCellFormatter().addStyleName(displayRow, nextCol, StyleConstants.RESULTS_TABLE_CELL);
        getCellFormatter().addStyleName(displayRow, nextCol, StyleConstants.RESULTS_TABLE_LOCATION);
    }

    protected void filterOutRows() {
        for (int irow = results.getActualRowCount(); irow > 0; irow--) {
            ResultRow row = results.getRow(irow - 1);
            Boolean matchedSearchAnnotation = (Boolean) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MATCHED_SEARCH);
            if (!matchedSearchAnnotation) {
                removeRow(irow);
            }
        }
        colorEvenRows();
    }

    protected void addResultTableStyles() {
        String tableHeaderColorStyle = isPivot ? StyleConstants.BLUE_RESULTS_TABLE_HEADER : StyleConstants.GREEN_RESULTS_TABLE_HEADER; //todo what about embedded in pathway? //: StyleConstants.PURPLE_RESULTS_TABLE_HEADER;
        this.getRowFormatter().addStyleName(0, StyleConstants.RESULTS_TABLE_HEADER);
        this.getRowFormatter().addStyleName(0, tableHeaderColorStyle);
    }

    private String getPivotId(ResultRow row, ColumnType pivotColumn) {
        String pivotId = null;
        if (pivotColumn instanceof AnomalyType) {
            AnomalyType.GeneticElementType getype = ((AnomalyType) pivotColumn).getGeneticElementType();
            if (getype == AnomalyType.GeneticElementType.miRNA) {
                pivotId = (String) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA);
            } else if(getype == AnomalyType.GeneticElementType.MethylationProbe) {
                pivotId = (String) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_METHYLATION_PROBE);
            } else {  // gene symbol
                pivotId = row.getName();
            }
        }
        return pivotId;
    }


}
