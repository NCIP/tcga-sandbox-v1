/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.RemoteService;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwayResults;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwaySpecifier;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Main client-server interface for GWT communication.
 *
 * @author Silpa Nanan
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */

/**
 * Both the anomalysearch and pathwaysearch sub-apps use the same interface for their
 * client-servlet communication.
 */
public interface AnomalySearchService extends RemoteService {
    /**
     * Returns a list of available disease, or tumor, types
     *
     * @return list of disease types
     * @throws SearchServiceException
     */
    List<Disease> getDiseases() throws SearchServiceException;

    /**
     * Returns a list of ColumnType objects. These is used to build the anomaly filter UI.
     * The same objects are then sent through processFilter to execute the filter.
     *
     * @param disease Disease abbreviation
     * @return the list of ColumnType objects for this disease
     * @throws SearchServiceException
     */
    List<ColumnType> getColumnTypes(String disease) throws SearchServiceException;

    /**
     * Executes an anomaly filter.  Returns the first page of anomaly results.
     *
     * @param filter Filter specifier
     * @return page of results
     * @throws SearchServiceException
     */
    Results processFilter(FilterSpecifier filter) throws SearchServiceException;

    /**
     * Fetches any page of anomaly results.
     *
     * @param page Page number to fetch. One-based
     * @return page of results
     * @throws SearchServiceException
     */
    Results getResultsPage(FilterSpecifier.ListBy listBy, int page) throws SearchServiceException;

    /**
     * Changes number of rows per page.  Returns the first page again, with the new count.
     *
     * @param listBy
     * @param rowsPerPage
     * @return
     * @throws SearchServiceException
     */
    Results setRowsPerPage(FilterSpecifier.ListBy listBy, int rowsPerPage) throws SearchServiceException;

    /**
     * Sorts results by any column; returns the first page.
     *
     * @param sortspec the specifier
     * @return first resultspage
     * @throws SearchServiceException
     */
    Results sortResults(FilterSpecifier.ListBy listBy, SortSpecifier sortspec) throws SearchServiceException;

    /**
     * Returns information about a pathway, including a link to pathway graphic.
     *
     * @param sps
     * @return
     * @throws SearchServiceException
     */
    SinglePathwayResults getSinglePathway(SinglePathwaySpecifier sps) throws SearchServiceException;

    Results getPivotPage(FilterSpecifier.ListBy sourceListby, String rowName, FilterSpecifier filter) throws SearchServiceException;

    /**
     * Returns URL to the user guide.
     *
     * @return
     * @throws SearchServiceException
     */
    String getUserGuideLocation() throws SearchServiceException;

    /**
     * Returns URL to the HTML version of the user guide.
     *
     * @return
     */
    String getOnlineHelpLocation();

    /**
     * Returns tooltip text to be displayed in popups in the data browser.
     *
     * @return
     * @throws SearchServiceException
     */
    TooltipTextMap getTooltipText() throws SearchServiceException;

    /**
     * Client regularly sends keepalive message to server, so server will keep session open.
     * Allows servlet to dump session within a few minutes after user navigates away.
     */
    void keepAlive();

    /**
     * Method used by export functionality in main results tables. Since the result set is held by the server,
     * it's not necessary to serialize and send the results through the interface.
     *
     * @return the export file name
     * @throws SearchServiceException if needed
     */
    String exportResultsData(FilterSpecifier.ListBy listBy, String filename) throws SearchServiceException;

    /**
     * Method used by export functionality in pivot tables. Since the result set is held by the client,
     * it IS necessary to serialize and send the results through the interface.
     *
     * @return the export file name
     * @throws SearchServiceException if needed
     */
    String exportPivotResultsData(FilterSpecifier.ListBy listBy, String filename, Results results) throws SearchServiceException;


    /**
     * Serializable exception class which is thrown for any error that occurs on the server
     * which needs to be reported to the client.
     */
    public class SearchServiceException extends Exception implements IsSerializable {
        public SearchServiceException() {
        }

        public SearchServiceException(String message) {
            super(message);
        }

        public SearchServiceException(Throwable t) {
            super(t);
        }
    }

}
