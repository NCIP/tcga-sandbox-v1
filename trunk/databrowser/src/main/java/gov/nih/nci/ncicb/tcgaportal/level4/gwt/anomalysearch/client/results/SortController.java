/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwt.anomalysearch.client.results;

/**
 * A class that knows how to execute a sort operation.  In the case of ModeController, it will call
 * the server to sort results on the server.  In the case of ResultsPivotPanel, it will execute the sort
 * client-side.  This class will be closely associated with the helper class SortOrderKeepTracker.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface SortController {
    public static final int SORTED_ASCENDING = 1;
    public static final int SORTED_DESCENDING = 2;
    public static final int SORTED_NOT = 3;

    /**
     * Sorts by column and/or annotation
     * @param columnId   The unique ID of the column, or -1 for no column (i.e. row name or row annotation)
     * @param annotation Name of the annotation to sort, if any. May come from AnomalySearchConstants
     * @param initialAscending  True if the column is initially to be sorted ascending order
     */
    void sort(long columnId, String annotation, boolean initialAscending);

    /**
     * Gets the current sort order for a column value or annotation, or row annotation
     * @param columnId   The unique ID of the column, or -1 for no column (i.e. row name or row annotation)
     * @param annotation Name of the annotation to sort, if any. May come from AnomalySearchConstants
     * @return the current sort order: SORTED_ASCENDING, SORTED_DESCENDING, OR SORTED_NOT
     */
    int getCurrentSortOrderForColumn(long columnId, String annotation);
}
