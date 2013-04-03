package gov.nih.nci.ncicb.tcgaportal.level4.dao.jdbc;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesCallback;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.SortSpecifier;

/**
    Callback used by DAO for unit testing.
 * User: nassaud
 * Date: Oct 1, 2009
 * Time: 10:59:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class MockCallback implements Level4QueriesCallback {

    Results results;
    boolean done = false;
    Throwable caughtException = null;

    public void sendFullResults(Results theResults) {
        results = theResults;
        done = true;
    }

    public Results getPageMeta() {
        return null;
    }

    public void sendException(QueriesException ex) {
        caughtException = ex;
        done = true;
    }

    public void dieYoung() {
        done = true;
    }

    public Results getPage(int pageno) throws QueriesException {
        return null;
    }

    public void sortResults(SortSpecifier sortspec) {
        // n/a
    }

    public void setRowsPerPage(int rowsPerPage) {
        // n/a
    }

    public Results getResultSet() {
        return null; // not used
    }
}
