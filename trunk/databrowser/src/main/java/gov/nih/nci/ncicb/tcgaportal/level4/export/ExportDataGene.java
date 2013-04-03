package gov.nih.nci.ncicb.tcgaportal.level4.export;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import static gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.ResultRow;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.AnomalyResultRatio;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultBlank;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultDouble;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MutationType;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Exporter specific to gene-mode data.
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class ExportDataGene extends ExportData {

    boolean hasBiocartaIds, hasMirna, hasMethylation;
    int mirnaColIdx, methylColIdx;

    public ExportDataGene(FilterSpecifier.ListBy listBy) {
        this.listBy = listBy;
    }

    protected void addConstantHeaders() {

        ResultRow firstRow = results.getRow(0);
        int headerIndex = 0;
        headerList = new String[getTotalNumberOfColumns()];

        headerList[headerIndex++] = HEADER_GENE;

        if (results.getRow(0).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_BCGENE) != null) {
            hasBiocartaIds = true;
            headerList[headerIndex++] = HEADER_BIOCARTA_ID;
        }

        if (firstRow.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA) != null) {
            hasMirna = true;
            mirnaColIdx = headerIndex;
            headerList[headerIndex++] = HEADER_MI_RNA;
        }

        if (firstRow.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_METHYLATION_PROBE) != null) {
            hasMethylation = true;
            methylColIdx = headerIndex;
            headerList[headerIndex++] = HEADER_METHYLATION_REGION;
        }

        headerIndex = headerIndex + numColsInExport;
        if (copyNumberColTypeExists()) {
            headerList[headerIndex++] = HEADER_CNV;
        }

        headerList[headerIndex++] = HEADER_CHROMOSOME;
        headerList[headerIndex++] = HEADER_START;
        headerList[headerIndex] = HEADER_STOP;

    }

    protected void addDynamicHeaders() throws IOException {

        int headerIndex = 1;
        if (hasBiocartaIds) {
            headerIndex++;
        }
        if (hasMirna) {
            headerIndex++;
        }
        if (hasMethylation) {
            headerIndex++;
        }

        ColumnType columnType;
        for (int columnNumber = 0; columnNumber < results.getColumnTypes().size(); columnNumber++) {
            columnType = results.getColumnTypes().get(columnNumber);
            int totalExportColumns = numberOfColumnsInExport[columnNumber];

            if ((totalExportColumns == NUMBER_EXPORT_COLUMNS_RESULT_BLANK) ||
                    (totalExportColumns == NUMBER_EXPORT_COLUMNS_RESULT_DOUBLE) ||
                    (totalExportColumns == NUMBER_EXPORT_COLUMNS_RESULT_PATHWAYLIST)) {

                headerList[headerIndex++] = columnType.getDisplayName();

            } else if (totalExportColumns == NUMBER_EXPORT_COLUMNS_RESULT_RATIO) {
                headerList[headerIndex++] = HEADER_TOTAL + columnType.getDisplayName();
                headerList[headerIndex++] = HEADER_AFFECTED + columnType.getDisplayName();
                headerList[headerIndex++] = HEADER_RATIO + columnType.getDisplayName();
            }

        }

        writeTabbedData(headerList);
    }

    protected void addDynamicCells(int currentRow, int irow) {

        rowData = new String[getTotalNumberOfColumns()];
        ResultValue[] columnResultValues = results.getRow(currentRow).getColumnResults();

        int constantCellsAtStart = 1 + (hasBiocartaIds ? 1 : 0) + (hasMirna ? 1 : 0) + (hasMethylation ? 1 : 0);

        for (int icol = 1; icol <= numColsInResult; icol++) {
            //index of the column in the result set
            int resultCol = icol - 1;
            int displayCol = constantCellsAtStart;

            for (int previousCol=1; previousCol<icol; previousCol++) {
                displayCol = displayCol + numberOfColumnsInExport[previousCol-1];
            }

            Double pValueAnnotation = (Double) columnResultValues[resultCol].getValueAnnotation(AnomalySearchConstants.VALUEANNOTATIONKEY_CORRELATION_PVALUE);
            String cellValue;
            if (columnResultValues[resultCol] instanceof ResultDouble) {
                cellValue = ((ResultDouble)columnResultValues[resultCol]).toString(decimalFormatter);
            } else {
                cellValue = columnResultValues[resultCol].toString();
            }

            if (pValueAnnotation != null) {
                //String pValue = NumberFormat.getScientificFormat().format(pValueAnnotation);
                DecimalFormat de = new java.text.DecimalFormat("0.000E00");
                String pValue = de.format(pValueAnnotation);
                rowData[displayCol++] = cellValue + " ("+ HEADER_P_VALUE + ":" + pValue + ")";

            } else {
                int totalExportColumns = numberOfColumnsInExport[resultCol];
                if ((totalExportColumns == NUMBER_EXPORT_COLUMNS_RESULT_BLANK) ||
                        (totalExportColumns == NUMBER_EXPORT_COLUMNS_RESULT_DOUBLE) ||
                        (totalExportColumns == NUMBER_EXPORT_COLUMNS_RESULT_PATHWAYLIST)) {

                    if (isPivot()) {
                        ColumnType col = results.getColumnTypes().get(resultCol);
                        if (col instanceof MutationType) {
                            //show "yes" instead of "1"
                            double doubleValue = ((ResultDouble)columnResultValues[resultCol]).getValue();
                            cellValue = (doubleValue==1. ? "yes" : "no");
                        }
                    }
                    rowData[displayCol] = cellValue;
                } else if (totalExportColumns == NUMBER_EXPORT_COLUMNS_RESULT_RATIO) {

                    if (columnResultValues[resultCol] instanceof ResultBlank) {
                        rowData[displayCol++] = "0";
                        rowData[displayCol++] = "0";
                        rowData[displayCol++] = "";
                    } else {
                        rowData[displayCol++] = Integer.toString(((AnomalyResultRatio) columnResultValues[resultCol]).getTotal());
                        rowData[displayCol++] = Integer.toString(((AnomalyResultRatio) columnResultValues[resultCol]).getAffected());

                        final DecimalFormat decimalFormat = new java.text.DecimalFormat("0.000");
                        rowData[displayCol++] = decimalFormat.format(((AnomalyResultRatio) columnResultValues[resultCol]).getRatio());
                    }
                }
            }
        }
    }


    protected void addConstantCells(int currentRow, int irow) throws IOException {

        ResultRow row = results.getRow(currentRow);
        String geneId = row.getName();

        int columnIndex = 0;
        rowData[columnIndex++] = geneId;

        if (hasBiocartaIds) {
            String bcId = (String) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_BCGENE);
            if (bcId == null) bcId = ""; //shouldn't happen

            rowData[columnIndex] = bcId;
        }

        if (hasMirna) {
            String miRNA = (String) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA);
            if (miRNA == null) miRNA = "";
            miRNA = miRNA.toLowerCase();
            rowData[mirnaColIdx] = miRNA;
        }

        if (hasMethylation) {
            String methRegion = (String) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_METHYLATION_PROBE);
            if (methRegion == null) methRegion = "";
            rowData[methylColIdx] = methRegion;
        }

        int nextCol = numColsInExport + 1 + (hasBiocartaIds ? 1 : 0) + (hasMirna ? 1 : 0) + (hasMethylation ? 1 : 0);

        if (copyNumberColTypeExists()) {
            Boolean cnv = (Boolean)row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_CNV);
            rowData[nextCol] = (cnv != null && cnv ? "true" : "false");
            nextCol++;
        }

        String chrAnnotation = "";
        String startAnnotation = "";
        String stopAnnotation = "";

        if (row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM) != null) {
            chrAnnotation = row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM).toString();
            startAnnotation = row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_START).toString();
            stopAnnotation = row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_STOP).toString();
        }

        rowData[nextCol++] = chrAnnotation;
        rowData[nextCol++] = startAnnotation;
        rowData[nextCol] = stopAnnotation;

        writeTabbedData(rowData);

    }

    protected int getTotalNumberOfColumns() {
        int totalCols = 0;
        totalCols++; //Gene
        if (results.getRow(0).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_BCGENE) != null) {
            totalCols++;
        }

        if (results.getRow(0).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA) != null) {
            totalCols++;
        }

        if (results.getRow(0).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_METHYLATION_PROBE) != null) {
            totalCols++;
        }

        if (copyNumberColTypeExists()) {
            totalCols++;    // cnv
        }

        totalCols = totalCols + 3;    // location

        return totalCols + numColsInExport;
    }


}
