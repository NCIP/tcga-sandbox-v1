package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.server;

import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4Queries;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesCallback;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.Level4QueriesGetter;
import gov.nih.nci.ncicb.tcgaportal.level4.dao.QueriesException;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchService;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Disease;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.SortSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.MutationType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwayResults;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.pathway.SinglePathwaySpecifier;
import gov.nih.nci.ncicb.tcgaportal.pathway.dao.IPathwayQueries;
import gov.nih.nci.ncicb.tcgaportal.pathway.util.PathwayDiagramHandler;
import org.apache.batik.transcoder.TranscoderException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for AnomalySearchServiceImpl.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class AnomalySearchServiceImplFastTest {
    private Mockery context = new JUnit4Mockery();
    private Level4Queries mockLevel4Queries = context.mock(Level4Queries.class);
    private Level4QueriesCallback mockLevel4Callback_genes = context.mock(Level4QueriesCallback.class, "genesCallback");
    private Level4QueriesCallback mockLevel4Callback_patients = context.mock(Level4QueriesCallback.class, "patientsCallback");
    private Level4QueriesCallback mockLevel4Callback_pathways = context.mock(Level4QueriesCallback.class, "pathwaysCallback");
    private PathwayDiagramHandler mockPathwayDiagramHandler = context.mock(PathwayDiagramHandler.class);
    private AnomalySearchServiceImpl searchService;
    private Level4QueriesGetter mockQueriesGetter;
    private FilterSpecifier filter;

    @Before
    public void setup() {
        searchService = new TestableAnomalySearchServiceImpl();
        mockQueriesGetter = context.mock(Level4QueriesGetter.class);
        context.checking(new Expectations() {{
            allowing(mockQueriesGetter).getLevel4Queries("DIS");
            will(returnValue(mockLevel4Queries));
        }});
        filter = new FilterSpecifier();
        filter.setDisease("DIS");
    }

    @Test
    public void testProcessFilter_genes() throws AnomalySearchService.SearchServiceException, QueriesException {
        filter.setListBy(FilterSpecifier.ListBy.Genes);
        context.checking( new Expectations() {{
            one(mockLevel4Callback_genes).dieYoung();
            one(mockLevel4Queries).getAnomalyResults(filter, mockLevel4Callback_genes);
            one(mockLevel4Callback_genes).getPage(1);
        }});

        searchService.processFilter(filter);
    }

    @Test
    public void testProcessFilter_patients() throws AnomalySearchService.SearchServiceException, QueriesException {
        AnomalySearchServiceImpl searchService = new TestableAnomalySearchServiceImpl();
        filter.setListBy(FilterSpecifier.ListBy.Patients);
        context.checking( new Expectations() {{
            one(mockLevel4Callback_patients).dieYoung();
            one(mockLevel4Queries).getAnomalyResults(filter, mockLevel4Callback_patients);
            one(mockLevel4Callback_patients).getPage(1);
        }});

        searchService.processFilter(filter);
    }

    @Test
    public void testProcessFilter_pathways() throws AnomalySearchService.SearchServiceException, QueriesException {
        AnomalySearchServiceImpl searchService = new TestableAnomalySearchServiceImpl();
        filter.setListBy(FilterSpecifier.ListBy.Pathways);
        context.checking( new Expectations() {{
            one(mockLevel4Callback_pathways).dieYoung();
            one(mockLevel4Queries).getPathwayResults(filter, mockLevel4Callback_pathways);
            one(mockLevel4Callback_pathways).getPage(1);
        }});

        searchService.processFilter(filter);
    }

    @Test
    public void testGetDiseases() throws QueriesException, AnomalySearchService.SearchServiceException {
        final List<Disease> diseases = new ArrayList<Disease>();

        context.checking(new Expectations() {{
            one(mockQueriesGetter).getDiseaseNames();
            will(returnValue(Arrays.asList("DIS")));
            one(mockLevel4Queries).getDiseases();
            will(returnValue(diseases));
        }});
        List<Disease> diseasesRet = searchService.getDiseases();
        assertEquals(diseases, diseasesRet);
    }

    @Test
    public void testGetColumnTypes() throws QueriesException, AnomalySearchService.SearchServiceException {
        final List<ColumnType> colTypes = new ArrayList<ColumnType>();
        CopyNumberType type1 = new CopyNumberType();
        type1.setDisplayCenter("center1");
        type1.setDisplayPlatform("platform1");
        MutationType type2 = new MutationType();
        type2.setCategory(MutationType.Category.Frameshift);
        colTypes.add(type1);
        colTypes.add(type2);

        context.checking(new Expectations(){{
            one(mockLevel4Queries).getColumnTypes("DIS");
            will(returnValue(colTypes));
        }});
        List<ColumnType> ret = searchService.getColumnTypes("DIS");
        assertEquals(colTypes.size(), ret.size());
        assertEquals(type1.getDisplayName(), ret.get(0).getDisplayName());
        assertEquals(type2.getDisplayName(), ret.get(1).getDisplayName());

    }

    @Test
    public void testGetResultsPage() throws AnomalySearchService.SearchServiceException, QueriesException {
        final Results fakeResults = new Results();
        context.checking(new Expectations(){{
            one(mockLevel4Callback_genes).getPage(1);
            will(returnValue(fakeResults));
        }});
        Results pageResults = searchService.getResultsPage(FilterSpecifier.ListBy.Genes, 1);
        assertEquals(fakeResults, pageResults);
    }

    @Test
    public void testGetSinglePathway() throws AnomalySearchService.SearchServiceException, IOException,
            TranscoderException, IPathwayQueries.PathwayQueriesException, QueriesException {
        final SinglePathwaySpecifier sps = new SinglePathwaySpecifier();
        sps.setFilterSpecifier(filter);
        final SinglePathwayResults pathway = new SinglePathwayResults();
        pathway.setName("testPathway");
        context.checking(new Expectations(){{
            one(mockLevel4Queries).getSinglePathway(sps);
            will(returnValue(pathway));
            one(mockPathwayDiagramHandler).fetchPathwayImage("testPathway", new ArrayList<String>());
            one(mockPathwayDiagramHandler).planDeletionOfImage("");
        }});
        searchService.getSinglePathway(sps);
    }

    @Test
    public void testSortResults() throws AnomalySearchService.SearchServiceException, QueriesException {
        final SortSpecifier sortSpec = new SortSpecifier();
        context.checking(new Expectations(){{
            one(mockLevel4Callback_genes).sortResults(sortSpec);
            one(mockLevel4Callback_genes).getPage(1);
        }});
        searchService.sortResults(FilterSpecifier.ListBy.Genes, sortSpec);
    }


    class TestableAnomalySearchServiceImpl extends AnomalySearchServiceImpl {

        protected Level4QueriesGetter getLevel4QueriesGetter() {
            return mockQueriesGetter;
        }
        
        boolean isRunningLocal() {
            return false;
        }

        protected Level4QueriesCallback makeNewCallback(final FilterSpecifier.ListBy listBy) {
            return getCallback(listBy);
        }

        protected Level4QueriesCallback getCallback(final FilterSpecifier.ListBy listBy) {
            if (listBy == FilterSpecifier.ListBy.Genes) {
                return mockLevel4Callback_genes;
            } else if (listBy == FilterSpecifier.ListBy.Patients) {
                return mockLevel4Callback_patients;
            } else if (listBy == FilterSpecifier.ListBy.Pathways) {
                return mockLevel4Callback_pathways;
            } else {
                throw new IllegalArgumentException("Unknown listby type: " + listBy);
            }
        }

        protected PathwayDiagramHandler getPathwayDiagramHandler() {
            return mockPathwayDiagramHandler;
        }

        protected void removeCallback(final FilterSpecifier.ListBy listBy) {
            // don't do anything            
        }
    }
}
