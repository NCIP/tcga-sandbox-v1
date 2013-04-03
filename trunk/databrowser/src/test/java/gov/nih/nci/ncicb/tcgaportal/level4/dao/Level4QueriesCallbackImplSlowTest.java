package gov.nih.nci.ncicb.tcgaportal.level4.dao;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.FilterSpecifier;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.AnomalyResultRatio;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.CopyNumberType;
import gov.nih.nci.ncicb.tcgaportal.level4.util.ResultsPagingProcessor;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Aug 11, 2009
 * Time: 2:01:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class Level4QueriesCallbackImplSlowTest extends TestCase {

    Level4QueriesCallback callback;
    ResultsPagingProcessor pager;

    public void setUp() {
        pager = new ResultsPagingProcessor();
        callback = new Level4QueriesCallbackImpl(pager);
    }

    public void testSendFullResults() throws QueriesException {
        List<ColumnType> ctypes = new ArrayList<ColumnType>();

        CopyNumberType cntype = new CopyNumberType();
        cntype.setDisplayCenter("hms.harvard.edu");
        cntype.setDisplayPlatform("HG-CGH-244A");
        cntype.setPicked(true);
        ctypes.add(cntype);

        FilterSpecifier filter = new FilterSpecifier();
        filter.setListBy(FilterSpecifier.ListBy.Genes);
        filter.setDisease("GBM");
        filter.setColumnTypes(ctypes);

        Results results = new Results(filter);
        AnomalyResultRatio ratio = new AnomalyResultRatio();
        ratio.setAffected(50);
        ratio.setTotal(100);
        ResultValue[] values = new ResultValue[1];
        values[0] = ratio;

        for (int i=0; i<100; i++) {
            results.addRow("gene" + (i+1), values, null);
        }

        callback.sendFullResults(results);
        Results page = callback.getPage(1);
        assertNotNull(page);
        assertEquals(4, page.getGatheredPages());
        assertEquals(4, page.getTotalPages());
        assertEquals(100, page.getTotalRowCount());
        assertEquals(25, page.getActualRowCount());
        assertEquals(1, page.getCurrentPage());
        assertEquals("gene1", page.getRow(0).getName());

        page = callback.getPage(2);
        assertNotNull(page);
        assertEquals(4, page.getGatheredPages());
        assertEquals(4, page.getTotalPages());
        assertEquals(100, page.getTotalRowCount());
        assertEquals(25, page.getActualRowCount());
        assertEquals(2, page.getCurrentPage());
        assertEquals("gene26", page.getRow(0).getName());

        //should get exception asking for page out of range
        try {
            page = callback.getPage(5);
            fail();
        } catch (QueriesException e) {
        }

    }

}
