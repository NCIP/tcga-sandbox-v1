/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.dao;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.SortSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.util.ResultsPagingProcessor;
import gov.nih.nci.ncicb.tcgaportal.util.ProcessLogger;

/**
 * Implementation of Level4QueriesCallback used by the servlet AnomalySearchServiceImpl.
 *
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public class Level4QueriesCallbackImpl implements Level4QueriesCallback {

    static final boolean DEBUG = false;

    private ResultsPagingProcessor pagingProcessor;
    protected Results fullResults;
    private QueriesException exception;
    private boolean dieYoung; //when set to true, causes DAO thread to end before gathering all pages
    private ProcessLogger logger;
    
    public Level4QueriesCallbackImpl(ResultsPagingProcessor pagingProcessor) {
        this(pagingProcessor, new ProcessLogger());
    }

    public Level4QueriesCallbackImpl(ResultsPagingProcessor pagingProcessor, ProcessLogger logger) {
        this.pagingProcessor = pagingProcessor;
        this.logger = logger;
    }
    
    public void setRowsPerPage(int rowsPerPage) {
        pagingProcessor.changeRowsPerPageOnExistingResults(rowsPerPage, fullResults);
    }

    /**
     * Called to signal that the search should terminate early instead of proceeding to completion.
     */
    public synchronized void dieYoung() {
        if (DEBUG) System.out.println(">>setting dieYoung flag");
        dieYoung = true;
        fullResults = null;
    }

    public synchronized void sendFullResults(Results results) {
        if (dieYoung) {
            return;
        }
        pagingProcessor.prepareFullResults(results);
        fullResults = results;
        notifyAll(); //in case it's waiting in getPage
    }

    /**
     * Called by servlet class to retrieve any page of results. One-based index.
     *
     * @param pageno Page number, starting with 1
     * @return results object with only this page's worth of results
     * @throws gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException
     *          if there is an error
     */
    public synchronized Results getPage(int pageno) throws QueriesException {
        if (DEBUG) logger.logDebug(">>callback.getPage  page==" + pageno);
        //if DAO reported an exception, throw it here
        if (exception != null) {
            throwToClient();
        }

        if (noRecordsFound()) {
            pageno = 0; //will retrieve an empty page result set
        } else if (badPageRequest(pageno)) {
            throw new QueriesException("Page does not exist: " + pageno);
        }

        Results pageResults = new Results();
        boolean gotPage = pagingProcessor.getPage(fullResults, pageResults, pageno);
        if (DEBUG) logger.logDebug("ret==" + pageResults);
        while (!gotPage) { //page does not yet exist
            try {
                if (DEBUG) logger.logDebug("calling wait");
                wait(); //wait until the page is added to the paging processor
                if (DEBUG) logger.logDebug("woke from wait");
            } catch (InterruptedException e) {
                // ignore
            }

            if (exception != null) {
                throwToClient();
            }

            //check again - we may have just gotten the final page count
            if (noRecordsFound()) {
                pageno = 0; //will retrieve an empty page result set
            } else if (badPageRequest(pageno)) {
                throw new QueriesException("Page does not exist: " + pageno);
            }

            gotPage = pagingProcessor.getPage(fullResults, pageResults, pageno);
            if (DEBUG) logger.logDebug("ret==" + pageResults);
        }
        if (DEBUG) logger.logDebug("<<callback.getPage");
        return pageResults;
    }

    private boolean noRecordsFound() {
        return fullResults != null && (fullResults.isFinalRowCount() && fullResults.getTotalRowCount() == 0);
    }

    private boolean badPageRequest(int page) {
        return fullResults != null && (page < 1 || (fullResults.isFinalRowCount() && fullResults.getTotalPages() < page));
    }

    private Results throwToClient() throws QueriesException {
        QueriesException e = exception;
        exception = null;
        fullResults = null;
        throw e;
    }

    /**
     * callback method for DAO to report any exception that happened there. This will then
     * throw to the client the next time it asks for a page.
     *
     * @param ex the exeption to send
     */
    public void sendException(QueriesException ex) {
        if (DEBUG) logger.logDebug(">>callback.sendException");
        exception = ex;
        if (DEBUG) logger.logDebug("going into synchronized block");
        synchronized (this) {
            if (DEBUG) logger.logDebug("calling notifyAll");
            notifyAll(); //if waiting in getPage, wake up and throw the exception
        }
        if (DEBUG) logger.logDebug("<<callback.sendException");
    }

    public synchronized void sortResults(SortSpecifier sortspec) {
        if (DEBUG) logger.logDebug(">>callback.sortResults()");
        //if we haven't gathered all rows by now, wait
        while (!fullResults.isGatheredAllRows()) {
            if (DEBUG) logger.logDebug("have not gathered all rows, waiting");
            try {
                wait();
            } catch (InterruptedException e) {
                // ignore
            }
            if (DEBUG) logger.logDebug("woke from wait in sortResults");
        }

        fullResults.sort(sortspec);

        if (DEBUG) logger.logDebug("<<callback.sortResults()");
    }


    public Results getResultSet() {
        if (fullResults.isGatheredAllRows())
            return fullResults;
        else
            return null;
    }

}
