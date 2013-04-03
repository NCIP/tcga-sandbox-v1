/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.dao;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Disease;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwayResults;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwaySpecifier;

import java.util.List;

/**
 * Interface for queries.
 *
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public interface Level4Queries {

    /**
     * Returns a list of AnomalyTypes and CorrelationTypes for the given disease.
     *
     * @param disease the disease name
     * @return the list of column types for the disease
     * @throws QueriesException if there is an error
     */
    List<ColumnType> getColumnTypes(String disease) throws QueriesException;

    /**
     * @return a list of tumor types handled by the application.
     * @throws QueriesException if there is an error getting the data
     */
    List<Disease> getDiseases() throws QueriesException;

    /**
     * Starts a search. Results will be returned through the callback interface.
     *
     * @param filter   the filter specifier
     * @param callback the callback to pass the results to
     * @throws QueriesException if there is an error running the query
     */
    void getAnomalyResults(FilterSpecifier filter, Level4QueriesCallback callback) throws QueriesException;

    /**
     * Starts a search.
     *
     * @param filter   the filter specifier
     * @return result object
     * @throws QueriesException if there is an error running the query
     */
    Results getAnomalyResults(FilterSpecifier filter) throws QueriesException;

    /**
     * Get pathway search results.
     *
     * @param filter   the filter specifier
     * @param callback the callback to pass the results to
     * @throws QueriesException if there is an error running the query
     */
    void getPathwayResults(FilterSpecifier filter, Level4QueriesCallback callback) throws QueriesException;

    /**
     * Retrieve information for a specific pathway
     *
     * @param sps the pathway specifier
     * @return a single pathway results object
     * @throws QueriesException if there is an error getting the pathway information
     */
    SinglePathwayResults getSinglePathway(SinglePathwaySpecifier sps) throws QueriesException;

    /**
     * Returns a result set for a pivot operation
     *
     * @param sourceListby the ListBy of the source result set. For example, if pivoting from genes to patients, is set to genes.
     * @param rowName      gene symbol or patient Id
     * @param filter       FilterSpecifier for the source result set, or it might be the source result set itself since Results extends FilterSpecifier
     * @return new Results
     * @throws QueriesException
     */
    Results getPivotResults(FilterSpecifier.ListBy sourceListby, String rowName, FilterSpecifier filter) throws QueriesException;

}
