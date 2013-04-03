package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

import com.google.gwt.user.client.ui.HorizontalPanel;
import gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.AnomalySearchGWTTestCase;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;

/**
 * TODO: INFO ABOUT CLASS
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */

//todo remove test?
public class PagingWidgetFastTest extends AnomalySearchGWTTestCase {

    public void testUpdateCounts() {
        Results fakeResults = new Results();
        int rowsPerPage = 10;
        fakeResults.setRowsPerPage(rowsPerPage);
        fakeResults.setCurrentPage(25);
        fakeResults.setFinalRowCount(false);
        fakeResults.setGatheredPages(25);
        fakeResults.setTotalPages(100);
        fakeResults.setGatheredRows(25*rowsPerPage);
        fakeResults.setTotalRowCount(100*rowsPerPage);

        PagingHelper pagingWidget = new PagingHelper(new HorizontalPanel(), new HorizontalPanel());
        pagingWidget.updateCounts(fakeResults);

        assertEquals(25, pagingWidget.getCurrentPage());

        assertTrue(pagingWidget.firstPageButton.isEnabled());
        assertEquals("of 25+", pagingWidget.totalPagesHtml.getHTML());
        // we are at the last gathered page, so don't enable
        assertFalse(pagingWidget.nextPageButton.isEnabled());
        assertFalse(pagingWidget.lastPageButton.isEnabled());
        assertEquals("Results " + (25*rowsPerPage-(rowsPerPage-1)) + " - " + (25*rowsPerPage) + " of " + (25*rowsPerPage) + "+", pagingWidget.totalRowDisplay.getText());
    }

    public void testUpdateCountsLast() {
        Results fakeResults = new Results();
        int rowsPerPage = 10;
        fakeResults.setRowsPerPage(rowsPerPage);
        fakeResults.setCurrentPage(20);
        fakeResults.setFinalRowCount(true);
        fakeResults.setGatheredPages(25);
        fakeResults.setTotalPages(25);
        fakeResults.setGatheredRows(25*rowsPerPage);
        fakeResults.setTotalRowCount(100*rowsPerPage);

        PagingHelper pagingWidget = new PagingHelper(new HorizontalPanel(), new HorizontalPanel());
        pagingWidget.updateCounts(fakeResults);

        assertEquals(20, pagingWidget.getCurrentPage());

        assertTrue(pagingWidget.firstPageButton.isEnabled());
        assertEquals("of 25", pagingWidget.totalPagesHtml.getHTML());
        // are more pages, so allow next
        assertTrue(pagingWidget.nextPageButton.isEnabled());
        // is final, so allow last
        assertTrue(pagingWidget.lastPageButton.isEnabled());
        assertEquals("Results " + (20*rowsPerPage-(rowsPerPage-1)) + " - " + (20*rowsPerPage) + " of " + (100*rowsPerPage),
                pagingWidget.totalRowDisplay.getText());
    }
}
