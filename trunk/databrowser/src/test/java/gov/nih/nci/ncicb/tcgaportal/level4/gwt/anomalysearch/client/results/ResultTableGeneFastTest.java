package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchGWTTestCase;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.AnomalyResultRatio;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.AnomalyType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ExpressionType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit tests on ResultTableGene class
 *
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Aug 24, 2009
 * Time: 4:37:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResultTableGeneFastTest extends AnomalySearchGWTTestCase {

    //can't use JMock in GWT so we do it the old fashioned way..
    class SortControllerStub implements SortController {
        public int getCurrentSortOrderForColumn(long columnId, String annotation) {
            return 0;
        }

        public void sort(long columnId, String annotation, boolean initialAscending) {
            //for now, don't do anything. If add a sorting test case, may need to implement
        }
    }

    public void testSuppressRepeats() {
        Results results = prepareResults();
        ResultsTable table = new ResultsTableGene(results, null, new SortControllerStub(), false);
        assertEquals(4, table.getRowCount());
        //second gene label should be blank
        int row=1, col=0;
        assertEquals("gene1", table.getText(row,col++));
        assertEquals("mirna1", table.getText(row,col++));
        assertEquals("50%", table.getText(row,col++));
        assertEquals("50%", table.getText(row,col));

        row++;
        col=0;
        //no gene symbol for second row
        assertEquals("mirna2", table.getText(row,col++));
        assertEquals("50%", table.getText(row,col++));
        assertEquals("50%", table.getText(row,col));

        row++;
        col=0;
        assertEquals("gene2", table.getText(row,col++));
        assertEquals("mirna3", table.getText(row,col++));
        assertEquals("50%", table.getText(row,col++));
        assertEquals("50%", table.getText(row,col));
    }

    public void testSuppressRepeatsWithFiltering() {
        Results results = prepareResults();
        ResultsTable table = new ResultsTableGene(results, null, new SortControllerStub(), true);
        int row=1, col=0;
        assertEquals("gene1", table.getText(row,col++));
        assertEquals("mirna2", table.getText(row,col++));
        assertEquals("50%", table.getText(row,col++));
        assertEquals("50%", table.getText(row,col));
        row++;
        col=0;
        assertEquals("gene2", table.getText(row,col++));
        assertEquals("mirna3", table.getText(row,col++));
        assertEquals("50%", table.getText(row,col++));
        assertEquals("50%", table.getText(row,col));
    }

    private Results prepareResults() {
        //just the minimal info needed to check repeats
        ExpressionType geneExpCol = new ExpressionType(AnomalyType.GeneticElementType.Gene);
        geneExpCol.setPicked(true);
        ExpressionType mirnaExpCol = new ExpressionType(AnomalyType.GeneticElementType.miRNA);
        mirnaExpCol.setPicked(true);

        List<ColumnType> ctypes = new ArrayList<ColumnType>();
        ctypes.add(geneExpCol);
        ctypes.add(mirnaExpCol);

        FilterSpecifier filter = new FilterSpecifier();
        filter.setListBy(FilterSpecifier.ListBy.Genes);
        filter.setColumnTypes(ctypes);

        Results results = new Results(filter);
        ResultValue[] columnResults = new ResultValue[2];
        AnomalyResultRatio ratio = new AnomalyResultRatio();
        ratio.setResultParent(results);
        ratio.setAffected(10);
        ratio.setTotal(20);
        columnResults[0] = ratio;
        AnomalyResultRatio ratio2 = new AnomalyResultRatio();
        ratio2.setResultParent(results);
        ratio2.setAffected(10);
        ratio2.setTotal(20);
        columnResults[1] = ratio2;
        Map<String, Serializable> annot = new HashMap<String, Serializable>();
        annot.put(AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA, "mirna1");
        annot.put(AnomalySearchConstants.ROWANNOTATIONKEY_MATCHED_SEARCH, false);
        results.addRow("gene1", columnResults, annot);

        columnResults = new ResultValue[2];
        ratio = new AnomalyResultRatio();
        ratio.setResultParent(results);
        ratio.setAffected(10);
        ratio.setTotal(20);
        columnResults[0] = ratio;
        ratio2 = new AnomalyResultRatio();
        ratio2.setResultParent(results);
        ratio2.setAffected(10);
        ratio2.setTotal(20);
        columnResults[1] = ratio2;
        annot = new HashMap<String, Serializable>();
        annot.put(AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA, "mirna2");
        annot.put(AnomalySearchConstants.ROWANNOTATIONKEY_MATCHED_SEARCH, true);
        results.addRow("gene1", columnResults, annot);

        columnResults = new ResultValue[2];
        ratio = new AnomalyResultRatio();
        ratio.setResultParent(results);
        ratio.setAffected(10);
        ratio.setTotal(20);
        columnResults[0] = ratio;
        ratio2 = new AnomalyResultRatio();
        ratio2.setResultParent(results);
        ratio2.setAffected(10);
        ratio2.setTotal(20);
        columnResults[1] = ratio2;
        annot = new HashMap<String, Serializable>();
        annot.put(AnomalySearchConstants.ROWANNOTATIONKEY_MIRNA, "mirna3");
        annot.put(AnomalySearchConstants.ROWANNOTATIONKEY_MATCHED_SEARCH, true);
        results.addRow("gene2", columnResults, annot);

        return results;
    }

}
