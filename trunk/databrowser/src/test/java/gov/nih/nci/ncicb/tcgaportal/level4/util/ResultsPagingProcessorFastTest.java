package gov.nih.nci.ncicb.tcgaportal.level4.util;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4Queries;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesCallback;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.mock.Level4QueriesMock;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import junit.framework.TestCase;

import java.util.List;

/**
 * TODO: class comments
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ResultsPagingProcessorFastTest extends TestCase {
    static final int defaultPageRows = 10;

    ResultsPagingProcessor rpp;
    Level4Queries l4q;
    final Object mutex = new Object();

    public void setUp() {
        rpp = new ResultsPagingProcessor();
        rpp.setRowsPerPage(defaultPageRows);
        l4q = new Level4QueriesMock();
        ((Level4QueriesMock) l4q).setCorrelationCalculator(new PearsonCorrelationCalculator());
    }

    Results fullResults;

    class TestCallback implements Level4QueriesCallback {
        public TestCallback() {
            fullResults = new Results();
        }

        public void sendFullResults(Results results) {
            rpp.prepareFullResults(results);
            fullResults = results;
            synchronized(mutex) {
                mutex.notifyAll();
            }
        }

        public Results getPageMeta() {
            return null; // not used in this test
        }

        public void sendException(QueriesException ex) {
            fail(ex.getMessage());
        }

        public void dieYoung() {
            // n/a
        }

        public Results getPage(int pageno) throws QueriesException {
            return null;  // not used in this test
        }

        public void sortResults(SortSpecifier sortspec) {
            // n/a
        }

        public void setRowsPerPage(int rowsPerPage) {
            rpp.changeRowsPerPageOnExistingResults(rowsPerPage, fullResults);
        }


        public Results getResultSet() {
            return null; //not used 
        }
    }

    public void testPages() throws QueriesException {
        List<ColumnType> cols = l4q.getColumnTypes("GBM");
        cols.get(0).setPicked(true);
        cols.get(1).setPicked(true);
        cols.get(10).setPicked(true);
        FilterSpecifier filter = new FilterSpecifier();
        filter.setColumnTypes(cols);

        filter.setDisease("GBM");
        filter.setListBy(FilterSpecifier.ListBy.Genes);

        TestCallback callback = new TestCallback();
        l4q.getAnomalyResults(filter, callback);

        //this prevents the method from completing until the callback has received all the results
        synchronized (mutex) {
            try {
                mutex.wait();
            } catch (InterruptedException e) {
            }
        }

        checkPages(fullResults, defaultPageRows);

        //change rows per page and recheck pages
        callback.setRowsPerPage(25);
        checkPages(fullResults, 25);
    }

    void checkPages(Results fullResults, int rowsPerPage) {
        checkPage(fullResults, 1, rowsPerPage);
        checkPage(fullResults, 2, rowsPerPage);
        checkPage(fullResults, fullResults.getTotalPages(), rowsPerPage);
    }

    void checkPage(Results fullResults, int pageno, int rowsPerPage) {
        Results pageResults = new Results();
        boolean gotPage = rpp.getPage(fullResults, pageResults, pageno);
        assertTrue(gotPage);

        assertEquals(pageResults.getCurrentPage(), pageno);
        assertEquals(pageResults.getTotalRowCount(), fullResults.getActualRowCount());
        assertTrue(pageResults.getActualRowCount() <= rowsPerPage);
        int totpages = pageResults.getTotalRowCount() / rowsPerPage + 1;
        assertEquals(pageResults.getTotalPages(), totpages);
        assertEquals(pageResults.getGatheredPages(), totpages);
        assertEquals(pageResults.isFinalRowCount(), true);
//        assertEquals(pageResults.isListByGene(), true);
        assertEquals(pageResults.getListBy(), FilterSpecifier.ListBy.Genes);

        System.out.println("page " + pageno);
        for (int i = 0; i < pageResults.getActualRowCount(); i++) {
            ResultRow rowA = pageResults.getRow(i); //fullResults.getRow(i);
            ResultRow rowB = fullResults.getRow(i + ((pageno - 1) * rowsPerPage));
            assertEquals(rowA, rowB);
            System.out.println(rowA.toString());
        }

        System.out.println("total pages: " + pageResults.getTotalPages());
        System.out.println("total rows: " + pageResults.getTotalRowCount());
    }

    public void testEmptyResults() throws QueriesException {
        List<ColumnType> cols = l4q.getColumnTypes("GBM");
        cols.get(0).setPicked(true);
        cols.get(1).setPicked(true);
        cols.get(10).setPicked(true);
        FilterSpecifier filter = new FilterSpecifier();
        filter.setColumnTypes(cols);
        filter.setGeneList("sdhfjkh"); //nonsense gene should return 0 rows

        filter.setDisease("GBM");
        filter.setListBy(FilterSpecifier.ListBy.Genes);

        TestCallback callback = new TestCallback();
        l4q.getAnomalyResults(filter, callback);

        //this prevents the method from completing until the callback has received all the results
        synchronized (mutex) {
            try {
                mutex.wait();
            } catch (InterruptedException e) {
            }
        }

        checkEmpty(fullResults);
    }

    private void checkEmpty(Results results) {
        assertTrue(results.isFinalRowCount());
        assertEquals(results.getTotalRowCount(), 0);

        Results pageResults = new Results();
        boolean gotPage = rpp.getPage(fullResults, pageResults, 0);
        assertTrue(gotPage);
        assertTrue(pageResults.isFinalRowCount());
        assertEquals(pageResults.getTotalRowCount(), 0);
        assertEquals(pageResults.getGatheredPages(), 0);
        assertEquals(pageResults.getActualRowCount(), 0);
        assertEquals(pageResults.getColumnTypes().size(), 3);
    }


    public void testSort() throws QueriesException {
        List<ColumnType> cols = l4q.getColumnTypes("GBM");
        cols.get(0).setPicked(true);
        cols.get(1).setPicked(true);
        cols.get(10).setPicked(true);
        FilterSpecifier filter = new FilterSpecifier();
        filter.setColumnTypes(cols);

        filter.setDisease("GBM");
//        filter.setListByGene(true);
        filter.setListBy(FilterSpecifier.ListBy.Genes);

        TestCallback callback = new TestCallback();
        l4q.getAnomalyResults(filter, callback);

        //this prevents the method from completing until the callback has received all the results
        synchronized (mutex) {
            try {
                mutex.wait();
            } catch (InterruptedException e) {
            }
        }
        //first column ascending
        fullResults.sort(new SortSpecifier(cols.get(0).getId(), null, true));
        System.out.println("printing pages for sorting by first col ascending");
        checkPages(fullResults, defaultPageRows);
        checkSortByValue(0, true);

        //second column descending
        fullResults.sort(new SortSpecifier(cols.get(1).getId(), null, false));
        System.out.println("printing pages for sorting by second col descending");
        checkPages(fullResults, defaultPageRows);
        checkSortByValue(1, false);

        //gene symbol descending
        fullResults.sort(new SortSpecifier(-1, SortSpecifier.FAKE_ANNOTATION_ROW_ID, false));
        System.out.println("printing pages for sorting by gene symbol");
        checkPages(fullResults, defaultPageRows);
        checkSortByRowId(false);

        //cnv row annotation
        fullResults.sort(new SortSpecifier(-1, AnomalySearchConstants.ROWANNOTATIONKEY_CNV, true));
        System.out.println("printing pages for sorting by CNV row annotation");
        checkPages(fullResults, defaultPageRows);
        checkSortByRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_CNV, true);

        //paired value annotation
        fullResults.sort(new SortSpecifier(cols.get(0).getId(), AnomalySearchConstants.VALUEANNOTATIONKEY_PAIRED, true));
        System.out.println("printing pages for sorting by Paired annotation");
        checkPages(fullResults, defaultPageRows);
        checkSortByValueAnnotation(0, AnomalySearchConstants.VALUEANNOTATIONKEY_PAIRED, true);

        //bogus annotation shouldn't throw exception, will just end up sorting nulls
        fullResults.sort(new SortSpecifier(-1, "bogus", true));
        checkSortByRowAnnotation("bogus", true);

        //bogus column will throw exception
        boolean threw = false;
        try {
            fullResults.sort(new SortSpecifier(999, null, true));
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        assertTrue(threw);
    }

    void checkSortByValue(int col, boolean asc) {
        double prevval = (asc ? -99999. : 99999);
        for (int irow = 0; irow < fullResults.getActualRowCount(); irow++) {
            ResultRow row = fullResults.getRow(irow);
            ResultValue rv = row.getColumnResults()[col];
            double sortval = (Double) rv.getSortableValue();
            if (asc) {
                assertTrue(sortval >= prevval);
            } else {
                assertTrue(sortval <= prevval);
            }
            prevval = sortval;
        }
    }

    void checkSortByRowAnnotation(String key, boolean asc) {
        Boolean prevval = asc;
        for (int irow = 0; irow < fullResults.getActualRowCount(); irow++) {
            ResultRow row = fullResults.getRow(irow);
            Boolean val = (Boolean) row.getRowAnnotation(key);
            if (val == null) val = false;
            if (asc) {
                assertTrue(val.compareTo(prevval) <= 0);  //boolean is compared opposite of other Comparables because we want True to sort to the top
            } else {
                assertTrue(val.compareTo(prevval) >= 0);
            }
            prevval = val;
        }
    }

    void checkSortByValueAnnotation(int col, String key, boolean asc) {
        Boolean prevval = asc;
        for (int irow = 0; irow < fullResults.getActualRowCount(); irow++) {
            ResultRow row = fullResults.getRow(irow);
            ResultValue rv = row.getColumnResults()[col];
            Boolean val = (Boolean) rv.getValueAnnotation(key);
            if (val == null) val = false;
            if (asc) {
                assertTrue(val.compareTo(prevval) <= 0);  //boolean is compared opposite of other Comparables because we want True to sort to the top
            } else {
                assertTrue(val.compareTo(prevval) >= 0);
            }
            prevval = val;
        }
    }

    void checkSortByRowId(boolean asc) {
        String prevval = (asc ? "" : "ZZZ");
        for (int irow = 0; irow < fullResults.getActualRowCount(); irow++) {
            ResultRow row = fullResults.getRow(irow);
            String id = row.getName();
            if (asc) {
                assertTrue(id.compareTo(prevval) >= 0);
            } else {
                assertTrue(id.compareTo(prevval) <= 0);
            }
            prevval = id;
        }
    }


}
