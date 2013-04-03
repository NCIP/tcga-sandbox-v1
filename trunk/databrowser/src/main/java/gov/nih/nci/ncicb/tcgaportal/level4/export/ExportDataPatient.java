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
 * Exporter specific to patient-mode data.
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ExportDataPatient extends ExportData {

    public ExportDataPatient(FilterSpecifier.ListBy listBy) {
        this.listBy = listBy;
    }

    protected void addConstantHeaders() {
        int headerIndex = 0;
        headerList = new String[getTotalNumberOfColumns()];
        headerList[headerIndex] = HEADER_PATIENT;
    }

    protected void addDynamicHeaders() throws IOException {

        int headerIndex = 1;
        ColumnType columnType;
        for (int columnNumber = 0; columnNumber < results.getColumnTypes().size(); columnNumber++) {
            columnType = results.getColumnTypes().get(columnNumber);
            int totalExportColumns;
            totalExportColumns = numberOfColumnsInExport[columnNumber];

            if ((totalExportColumns == NUMBER_EXPORT_COLUMNS_RESULT_DOUBLE) ||
                (totalExportColumns == NUMBER_EXPORT_COLUMNS_RESULT_BLANK) ||
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

        int constantCellsAtStart = 1;
        
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
                String pValue =de.format(pValueAnnotation);
                rowData[displayCol++] = cellValue + " (" + HEADER_P_VALUE + ":" + pValue + ")";

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

                        DecimalFormat de = new java.text.DecimalFormat("0.000");
                        rowData[displayCol++] = de.format(((AnomalyResultRatio) columnResultValues[resultCol]).getRatio());
                    }
                }
            }
        }
    }

    protected void addConstantCells(int currentRow, int irow) throws IOException {

        ResultRow row = results.getRow(currentRow);
        String patientId = row.getName();

        int columnIndex = 0;
        rowData[columnIndex] = patientId;
        writeTabbedData(rowData);
    }

    protected int getTotalNumberOfColumns() {
        int totalCols = 0;
        totalCols++; // for Patient
        return totalCols+ numColsInExport;

    }

}
