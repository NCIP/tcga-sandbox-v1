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

/**
 * Interface for the DAO to send chunks of result rows to the servlet.
 * Created by IntelliJ IDEA Jan 29, 2009
 *
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */

public interface Level4QueriesCallback {

    /**
     * Sends all result rows at once.
     *
     * @param results
     */
    void sendFullResults(Results results);

    /**
     * Send an exception. The exception will then be reported to the client.
     *
     * @param ex the exeption that should be sent
     */
    void sendException(QueriesException ex);

    /**
     * When called, sets a flag which tells the DAO to terminate the current search as soon as possible.
     */
    void dieYoung();

    /**
     * Retrieves a single page of results.
     *
     * @param pageno
     * @return
     * @throws QueriesException
     */
    Results getPage(int pageno) throws QueriesException;

    /**
     * Sorts the current results.
     *
     * @param sortspec
     */
    void sortResults(SortSpecifier sortspec);

    /**
     * Sets the number of rows to display per page in the UI.
     *
     * @param rowsPerPage
     */
    void setRowsPerPage(int rowsPerPage);

    /**
     * Retrieves the current results. Used for export.
     *
     * @return
     */
    Results getResultSet();
}
