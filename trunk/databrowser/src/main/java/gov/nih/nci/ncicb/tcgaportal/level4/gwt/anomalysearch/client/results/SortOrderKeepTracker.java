package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

/**
 * Keeps track of sort orders. You need one of these per ModeController, for the main result sets.
 * Plus one per PathwayDiagramPanel, and one per ResultsPivotPanel.
 * User: nassaud
 * Date: Oct 6, 2009
 * Time: 4:23:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class SortOrderKeepTracker {

    class SortOrder {
        long columnId;
        String annotation;
        boolean ascending;
    }

    private SortOrder currentSortOrder;

    public int getCurrentSortOrderForColumn(long columnId, String annotation) {
        int ret = 0;
        if (currentSortOrder != null) {
            if (columnId == currentSortOrder.columnId) {
                if (annotation == null && currentSortOrder.annotation == null) {
                    //column is sorted, return whether it's asc or desc
                    ret = (currentSortOrder.ascending ? 1 : 2);
                } else if (annotation != null && currentSortOrder.annotation != null
                        && annotation.equals(currentSortOrder.annotation)) {
                    //is sorted by annotation, return whether asc or desc
                    ret = (currentSortOrder.ascending ? 1 : 2);
                }
            }
        }
        return ret;
    }

    public boolean doColumnAscending(long columnId, String annotation, boolean initialAscending) {
        boolean doAscending = initialAscending;
        if (currentSortOrder != null) {
            if (columnId == currentSortOrder.columnId) {
                if (annotation == null && currentSortOrder.annotation == null) {
                    //sorting by column value alone, flip the order
                    doAscending = !currentSortOrder.ascending;
                } else if (annotation != null && currentSortOrder.annotation != null
                        && annotation.equals(currentSortOrder.annotation)) {
                    //sorting by annotation, flip the order
                    doAscending = !currentSortOrder.ascending;
                }
            }
        }
        //record the new sort order
        currentSortOrder = new SortOrder();
        currentSortOrder.columnId = columnId;
        currentSortOrder.annotation = annotation;
        currentSortOrder.ascending = doAscending;
        return doAscending;
    }

    public void clear() {
        currentSortOrder = null;
    }
}
