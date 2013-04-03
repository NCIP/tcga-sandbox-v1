/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.filter;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.util.StyleConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterChromRegion;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.AnomalyType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CorrelationType;

import java.util.List;

/**
 * Panel that displays a tabular summary of search criteria.  (Used for collapsed representation of filter panel.)
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FilterSummaryPanel extends VerticalPanel {

    private FilterSpecifier filter;
    private FlexTable ssTable;
    private String headerStyle;
    private final static int numColumns = 3;

    public FilterSummaryPanel(FilterSpecifier filter) {
        this.filter = filter;
        headerStyle = "greenResultsTableHeader";
        if (filter.getListBy() == FilterSpecifier.ListBy.Patients) {
            headerStyle = "blueResultsTableHeader";
        } else if (filter.getListBy() == FilterSpecifier.ListBy.Pathways) {
            headerStyle = "purpleResultsTableHeader";
        }
        makeTable();
    }

    void makeTable() {
        if (filter == null) {
            return;
        }

        ssTable = new FlexTable();
        ssTable.addStyleName("resultsTable");
        ssTable.setWidth("100%");
        int row = 0, col = 0;

        row = writeDiseaseType(row);
        row = writeHeaders(row, col);
        row = writeCriteria(row);

        switch (filter.getGeneListOptions()) {
            case List:
                row = writeGeneList(row);
                break;
            case Region:
                row = writeRegions(row);
                break;
        }

        if (filter.getPatientListOptions() == FilterSpecifier.PatientListOptions.List) {
            //noinspection UnusedAssignment
            row = writePatientList(row);
        }

        ssTable.setWidth("100%");
        setWidth("100%");
        add(ssTable);
    }

    private int writeDiseaseType(int row) {
        writeRow(row++, 0, new String[]{"Disease"}, true);

        writeRow(row++, 0, new String[]{filter.getDisease()}, false);
        return row;
    }

    private int writeCriteria(int row) {
        int col;//regular columns
        for (ColumnType ctype : filter.getPickedColumns()) {
            if (!ctype.isPicked()) continue;

            col = 0;
            if (ctype instanceof AnomalyType) {
                AnomalyType atype = (AnomalyType) ctype;
                String displayCriteria = atype.getDisplayCriteria(formatNumber(atype.getFrequency() * 100, true));
                writeRow(row, col, new String[]{atype.getDisplayPlatformType() + " (" + atype.getGeneticElementType() + ")",
                        atype.getDisplayCenter() + " / " + atype.getDisplayPlatform(),
                        displayCriteria}, false);
                row++;
            }

        }

        //correlations
        for (ColumnType ctype : filter.getPickedColumns()) {
            col = 0;
            if (ctype instanceof CorrelationType) {
                CorrelationType cortype = (CorrelationType) ctype;
                writeRow(row, col, new String[]{"Correlation", cortype.getDisplayName(),
                        cortype.getDisplayCriteria(formatNumber((float) cortype.getPvalueLimit(), false))}, false);
                row++;
            }

        }
        return row;
    }

    private void writeRow(int row, int col, String[] content, boolean isHeaderRow) {
        if (content.length == 1) {
            ssTable.getFlexCellFormatter().setColSpan(row, col, numColumns);
        }

        if (isHeaderRow) {
            ssTable.getRowFormatter().addStyleName(row, StyleConstants.RESULTS_TABLE_HEADER);
            ssTable.getRowFormatter().addStyleName(row, headerStyle);
        }

        for (String text : content) {
            ssTable.setText(row, col, text);
            ssTable.getCellFormatter().addStyleName(row, col, "smallFont");
            ssTable.getCellFormatter().addStyleName(row, col,
                    isHeaderRow ? StyleConstants.RESULTS_TABLE_HEADER : StyleConstants.RESULTS_TABLE_CELL);
            col++;
        }

    }

    private int writeHeaders(int row, int col) {
        if (filter.getPickedColumns().size() > 0) {
            writeRow(row, col, new String[]{"Platform Type", "Center / Platform", "Criteria"}, true);
            row++;
        }
        return row;
    }

    private int writePatientList(int row) {
        int col;
        String patientList = filter.getPatientList();
        if (patientList != null && patientList.length() > 0) {
            col = 0;
            writeRow(row, col, new String[]{"Patient List"}, true);
            row++;

            writeRow(row, col, new String[]{patientList}, false);
        }
        return row;
    }

    private int writeGeneList(int row) {
        int col;
        String geneList = filter.getGeneList();
        if (geneList != null && geneList.length() > 0) {
            col = 0;
            writeRow(row, col, new String[]{"Gene List"}, true);
            row++;

            writeRow(row, col, new String[]{geneList}, false);
            row++;
        }
        return row;
    }

    private int writeRegions(int row) {
        int col;
        List<FilterChromRegion> regions = filter.getChromRegions();
        if (regions != null) {
            col = 0;
            writeRow(row, col, new String[]{"Chromosome Regions"}, true);
            row++;

            for (FilterChromRegion region : regions) {
                writeRow(row, col, new String[]{region.toString()}, false);
                row++;
            }
        }
        return row;
    }


    protected String formatNumber(float number, boolean asPercent) {
        if (number <= 0) {
            return "";
        } else if (number < .01) {
            return NumberFormat.getScientificFormat().format(number) + (asPercent ? "%" : "");
        } else {
            return NumberFormat.getDecimalFormat().format(number) + (asPercent ? "%" : "");
        }
    }
}
