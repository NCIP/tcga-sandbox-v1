package gov.nih.nci.ncicb.tcgaportal.level4.dao.dbunit;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesCallback;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.SortSpecifier;

/**
 * Description :
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */
class MockLevel4QueriesCallback implements Level4QueriesCallback {

    Results results;
    boolean done = false;
    Throwable caughtException = null;

    MockLevel4QueriesCallback() {
    }

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