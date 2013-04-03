package gov.nih.nci.ncicb.tcgaportal.level4.export;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import static gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants.HEADER_PATHWAY;
import static gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants.HEADER_P_VALUE;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.ResultRow;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Exporter specific to pathway-mode data.
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ExportDataPathway extends ExportData {

    public ExportDataPathway(FilterSpecifier.ListBy listBy) {
        this.listBy = listBy;
    }

    protected void init(Results results) {
        this.results = results;
        numColsInResult = 0;
        numColsInExport = 0;
        if (results.getColumnTypes() != null) {  //can be null in the case of pathways
            numColsInResult = results.getColumnTypes().size();
            numColsInExport = 0;
        }
    }
    protected void addConstantHeaders() throws IOException {

        int headerIndex = 0;
        headerList = new String[getTotalNumberOfColumns()];
        headerList[headerIndex++] = HEADER_PATHWAY;

        if (results.getActualRowCount() != 0) {
            if (results.getRow(0).getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAY_FISHER) != null) {
                headerList[headerIndex] = HEADER_P_VALUE;
            }
        }
        writeTabbedData(headerList);
    }

    protected void addDynamicHeaders() {

    }

    protected void addDynamicCells(int currentRow, int irow) {

    }

    protected void addConstantCells(int currentRow, int irow) throws IOException {

        rowData = new String[getTotalNumberOfColumns()];
        ResultRow row = results.getRow(currentRow);
        String pathwayName = results.getRow(currentRow).getName();

        int columnIndex = 0;
        rowData[columnIndex++] = pathwayName;

        Double fischer = (Double) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_PATHWAY_FISHER);
        if (fischer != null) {
            //String sFischer = NumberFormat.getScientificFormat().format(fischer);
            DecimalFormat de = new java.text.DecimalFormat("0.0000E00");
            String sFischer = de.format(fischer);
            rowData[columnIndex] = sFischer;
        }
        writeTabbedData(rowData);

    }

    protected int getTotalNumberOfColumns() {
        int totalCols = 0;
        totalCols++; //pathway name
        totalCols++;   // for p-value
        // note that pathway search always shows only 2 columns !
        return totalCols;
    }

}
