package gov.nih.nci.ncicb.tcgaportal.level4.export;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;

import java.io.IOException;
import java.io.Writer;

/**
 * Used on server to export results data to tab-delimited files. The files are stored on
 * disk on the web server and then retrieved by the browser.  This approach is necessitated
 * by security restrictions in GWT which prevent us from doing a client-side export.
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class ExportData {

    protected int numColsInResult;
    protected int numColsInExport;
    //Number of export columns corresponding to each column in the result
    protected Integer[] numberOfColumnsInExport;
    protected boolean isPivot;

    Results results;
    FilterSpecifier.ListBy listBy;

    Writer writer;

    protected String[] headerList;
    protected String[] rowData;

    public static String TAB_DELIMITER = "\t";
    public static String LINE_DELIMITER = "\r\n";
    public static int POSITION_FIRST = 1;
    public static int POSITION_NON_FIRST = 2;
    protected static final Integer NUMBER_EXPORT_COLUMNS_RESULT_BLANK = 1;
    protected static final Integer NUMBER_EXPORT_COLUMNS_RESULT_DOUBLE = 1;
    protected static final Integer NUMBER_EXPORT_COLUMNS_RESULT_RATIO = 3;
    protected static final Integer NUMBER_EXPORT_COLUMNS_RESULT_PATHWAYLIST = 1;

    protected final NumberFormatter decimalFormatter = new ExportNumberFormatter("0.000");

    public static ExportData getInstance(FilterSpecifier.ListBy listBy) {
        ExportData exportData = null;
        if (listBy == FilterSpecifier.ListBy.Genes)
            exportData = new ExportDataGene(listBy);
        else if (listBy == FilterSpecifier.ListBy.Patients)
            exportData = new ExportDataPatient(listBy);
        else if (listBy == FilterSpecifier.ListBy.Pathways)
            exportData = new ExportDataPathway(listBy);

        return exportData;
    }

    protected void init(Results results) {
        this.results = results;
        numColsInResult = 0;
        numColsInExport = 0;
        if (results.getColumnTypes() != null) {  //can be null in the case of pathways
            numColsInResult = results.getColumnTypes().size();
            getNumberOfExportColumnsForColumnsInResult();
            numColsInExport = getTotalNumberOfColumnsInExport();
        }

    }

    public void export(Results results, Writer writer, boolean writeHeaders) throws IOException {
        this.writer = writer;
        init(results);
        if (writeHeaders) {
            addHeaders();
        }
        addRowData();
    }

    public void export(Results results, Writer writer) throws IOException {
        export(results, writer, true);
    }

    private void addHeaders() throws IOException {
        addConstantHeaders();
        addDynamicHeaders();
    }

    protected abstract void addConstantHeaders() throws IOException;

    protected abstract void addDynamicHeaders() throws IOException;

    private void addRowData() throws IOException {

        for (int irow = 1; irow <= results.getActualRowCount(); irow++) {
            int currentRow = irow - 1;
            addDynamicCells(currentRow, irow);
            addConstantCells(currentRow, irow);
        }
    }

    protected abstract void addDynamicCells(int currentRow, int irow) throws IOException;

    protected abstract void addConstantCells(int currentRow, int irow) throws IOException;

    protected void writeTabbedData(String[] tabbedData) throws IOException {

        for (int i = 0; i < tabbedData.length; i++) {
            if (i != 0) {
                writer.write(TAB_DELIMITER);
            }
            writer.write("" + tabbedData[i]);
        }
        writer.write(LINE_DELIMITER);
    }

    protected abstract int getTotalNumberOfColumns();

    protected boolean copyNumberColTypeExists() {
        for (ColumnType cType : results.getColumnTypes()) {
            if (cType instanceof CopyNumberType) {
                return true;
            }
        }
        return false;
    }

    private int getTotalNumberOfColumnsInExport() {
        int totalExportColumns = 0;
        for (Integer columnsInExport : numberOfColumnsInExport) {
            totalExportColumns += columnsInExport;
        }
        return totalExportColumns;
    }

    // Method that finds out the total number of columns in the export file corresponding to each column
    // in the result
    // eg if the column in result is of type AnomalyResultRation then there will be three columns in the
    // export file: total patients, affected patients and ratio

    protected void getNumberOfExportColumnsForColumnsInResult() {

        numberOfColumnsInExport = new Integer[results.getColumnTypes().size()];

        ResultValue resultValue;
        for (int resultColumnNum = 0; resultColumnNum < results.getColumnTypes().size(); resultColumnNum++) {

            resultValue = getResultTypeForColumn(resultColumnNum);
            if (resultValue instanceof ResultBlank) {
                numberOfColumnsInExport[resultColumnNum] = NUMBER_EXPORT_COLUMNS_RESULT_BLANK;
            } else if (resultValue instanceof ResultDouble) {
                numberOfColumnsInExport[resultColumnNum] = NUMBER_EXPORT_COLUMNS_RESULT_DOUBLE;
            } else if (resultValue instanceof AnomalyResultRatio) {
                numberOfColumnsInExport[resultColumnNum] = NUMBER_EXPORT_COLUMNS_RESULT_RATIO;
            } else if (resultValue instanceof PathwayListResultValue) {
                numberOfColumnsInExport[resultColumnNum] = NUMBER_EXPORT_COLUMNS_RESULT_PATHWAYLIST;
            }
        }
    }

    private ResultValue getResultTypeForColumn(int columnNumber) {
        // scan through the values for that column to check what kind of value they have
        // some columns can have both AnomalyResultRatio as well as ResultBlank
        // the worst case scenario here would be that all the values in the result column are ResultBlanks,
        // but the possibility of that is quite less

        ResultValue returnValue = new ResultBlank();
        ResultValue resultValue;
        for (int resultRowNum = 0; resultRowNum < results.getActualRowCount(); resultRowNum++) {
            resultValue = results.getRow(resultRowNum).getColumnResults()[columnNumber];
            if (resultValue instanceof AnomalyResultRatio) {
                returnValue = new AnomalyResultRatio();
                break;
            } else if (resultValue instanceof ResultDouble) {
                returnValue = new ResultDouble();
                break;
            } else if (resultValue instanceof PathwayListResultValue) {
                returnValue = new PathwayListResultValue();
                break;
            }
        }
        return returnValue;
    }

    public boolean isPivot() {
        return isPivot;
    }

    public void setPivot(boolean pivot) {
        isPivot = pivot;
    }
}
