/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;

/**
 * A controller called by a ResultsPanelCanCopyToFilter to obtain single-page result sets; also to
 * export results to text file.
 * As implemented by ModeController, paging involves frequent interactions with the server. Full result
 * sets are kept on the server, and only single-page result sets are sent down to the client.
 * As implemented by ResultsPivotPanel, paging is handled on the client.  Pivoted result sets are
 * handed down in full from the server.
 *
 * @author David Nassau
 * @version $Rev$
 */
public interface PagingAndExportController {

    /*
     *
      * @return Gene, Patient or Pathway
     */
    FilterSpecifier.ListBy getListBy();

    /**
     * Exports the current result set to text file. This will always involve interaction with the server, since
     * GWT security does not permit client-side file writing.
     */
    void writeExportData();

    /**
     * Called by the ResultsPagingPanel to change the number of rows displayed.
     *
     * @param rowsPerPage
     */
    void setRowsPerPage(int rowsPerPage);
}
