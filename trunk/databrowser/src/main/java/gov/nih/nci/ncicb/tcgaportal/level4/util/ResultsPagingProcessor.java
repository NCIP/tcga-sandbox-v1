package gov.nih.nci.ncicb.tcgaportal.level4.util;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.ResultRow;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;

/**
 * TODO: Class description
 *
 * @author David Nassau
 *         Last updated by: $Author: nassaud $
 * @version $Rev: 5360 $
 */
public class ResultsPagingProcessor {

    public static class ResultsPagingProcessorException extends Exception {
        public ResultsPagingProcessorException(Throwable cause) {
            super(cause);
        }

        public ResultsPagingProcessorException(String msg) {
            super(msg);
        }
    }

    private int rowsPerPage = 25;

    public ResultsPagingProcessor() {
    }

    //copy ctor
    public ResultsPagingProcessor(ResultsPagingProcessor orig) {
        rowsPerPage = orig.rowsPerPage;
    }

    public synchronized void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public synchronized void changeRowsPerPageOnExistingResults(int rowsPerPage, Results fullResults) {
        this.rowsPerPage = rowsPerPage;
        if (fullResults != null && fullResults.getActualRowCount() > 0) {
            fullResults.setRowsPerPage(rowsPerPage);
            fullResults.setTotalPages(calcPages(fullResults.getTotalRowCount(), true));
            fullResults.setGatheredPages(calcPages(fullResults.getActualRowCount(), fullResults.isFinalRowCount())); //round up if all rows are in, otherwise down
        }
    }

    public synchronized void prepareFullResults(Results fullResults) {
        fullResults.setRowsPerPage(rowsPerPage);

        fullResults.setFinalRowCount(true);

        //since we're getting all results in one shot, we can safely set total count = actual count
        int rowcount = fullResults.getActualRowCount();
        int pagecount = calcPages(rowcount, true); //round up
        fullResults.setTotalRowCount(rowcount);
        fullResults.setTotalPages(pagecount);
        fullResults.setGatheredPages(pagecount);
    }

    //calculates the total pages based on the "totalPages" property, which could be an estimate.
    // we round up for totalPages, round down for gatheredPages
    // (this avoids the client fetching a partial page)
    private int calcPages(int totalRows, boolean roundup) {
        int totalPages = totalRows / rowsPerPage;
        if (roundup && totalRows % rowsPerPage != 0) {
            totalPages++;
        }
        return totalPages;
    }

    public synchronized boolean getPage(Results fullResults, Results pageResults, int pageno) {
        if (fullResults == null || pageno > fullResults.getGatheredPages()) {
            //page data hasn't yet been received
            return false;
        }

        //make an AnomalyResult that will represent only the current page
        //AnomalyResults pageResults = new AnomalyResults(fullResults);
        pageResults.initialize(fullResults);
        int fullRowCount = fullResults.getActualRowCount();

        if (pageno > 0) { //in case of pageno==0, make an empty results object
            int start = rowsPerPage * (pageno - 1);
            if (start <= fullRowCount - 1) {
                int max = start + rowsPerPage;
                if (max > fullRowCount) {
                    max = fullRowCount;
                }
                for (int i = start; i < max; i++) {
                    pageResults.addRow(fullResults.getRow(i));
                }
            }
        }

        //"total" rows could be an estimate
        pageResults.setTotalRowCount(fullResults.getTotalRowCount());
        //"total" pages is based on total rows and could be an estimate
        pageResults.setTotalPages(fullResults.getTotalPages());
        //"gathered" pages is based on actual rows and represents what's available right now
        pageResults.setGatheredPages(fullResults.getGatheredPages());
        //"gathered" rows is based on actual rows in the full result set
        pageResults.setGatheredRows(fullResults.getActualRowCount());
        //flag indicates whether to consider total rows/pages as a final count, not estimate
        pageResults.setFinalRowCount(fullResults.isFinalRowCount());
        pageResults.setRowsSearched(fullResults.getRowsSearched());
        pageResults.setRowsToSearch(fullResults.getRowsToSearch());

        fullResults.setCurrentPage(pageno);
        pageResults.setCurrentPage(pageno);
        pageResults.setRowsPerPage(rowsPerPage);

        setParentPointerOnResultsPage(pageResults);

        return true;
    }

    //all ResultValue objects need to know who their parent Results object is,
    //so they can check display flags and return the appropriate text
    private void setParentPointerOnResultsPage(Results pageResults) {
        for (int i = 0; i < pageResults.getActualRowCount(); i++) {
            ResultRow row = pageResults.getRow(i);
            for (ResultValue rv : row.getColumnResults()) {
                rv.setResultParent(pageResults);
            }
        }
    }

}
