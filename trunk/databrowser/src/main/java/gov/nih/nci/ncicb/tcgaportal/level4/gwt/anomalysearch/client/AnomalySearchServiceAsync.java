package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.*;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwayResults;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwaySpecifier;

import java.util.List;

/**
 * Asynchronous version of AnomalySearchService interface. Each method takes a callback object for returning results.
 * For description of each method call, see AnomalySearchService
 *
 * @author Silpa Nanan
 *         Last updated by: $Author: nassaud $
 * @version $Rev: 5360 $
 */

public interface AnomalySearchServiceAsync {

    void getDiseases(AsyncCallback<List<Disease>> async);

    void getColumnTypes(String disease, AsyncCallback<List<ColumnType>> async);

    void processFilter(FilterSpecifier filter, AsyncCallback<Results> async);

    void getResultsPage(FilterSpecifier.ListBy listBy, int page, AsyncCallback<Results> async);

    void setRowsPerPage(FilterSpecifier.ListBy listBy, int rowsPerPage, AsyncCallback<Results> async);

    void sortResults(FilterSpecifier.ListBy listBy, SortSpecifier sortspec, AsyncCallback<Results> async);

    void getSinglePathway(SinglePathwaySpecifier sps, AsyncCallback<SinglePathwayResults> async);

    void getPivotPage(FilterSpecifier.ListBy listby, String rowName, FilterSpecifier filter, AsyncCallback<Results> async);

    void getUserGuideLocation(AsyncCallback<String> async);

    void getOnlineHelpLocation(AsyncCallback<String> async);

    void getTooltipText(AsyncCallback<TooltipTextMap> async);

    void exportResultsData(FilterSpecifier.ListBy listBy, String filename, AsyncCallback<String> async);

    void exportPivotResultsData(FilterSpecifier.ListBy listBy, String filename, Results results, AsyncCallback<String> async);

    void keepAlive(AsyncCallback async);
}
