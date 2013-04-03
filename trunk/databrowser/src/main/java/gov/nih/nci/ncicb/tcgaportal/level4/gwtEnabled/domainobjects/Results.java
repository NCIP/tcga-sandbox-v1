package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes.ColumnType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * TODO: Class description
 *
 * @author David Nassau
 *         Last updated by: $Author: bertondl $
 * @version $Rev: 7755 $
 */

//todo  this class duplicates much of the information in FilterSpecifier - combine them in some way?
//todo  e.g., derive Results from FilterSpecifier?
public class Results extends FilterSpecifier {

    protected int currentRow = 0;
    protected int currentPage;
    protected int totalPages;
    protected int totalRowCount;
    protected boolean isFinalRowCount;
    protected int gatheredPages;
    protected int gatheredRows;
    protected int rowsPerPage;
    protected List<ResultRow> rows = new ArrayList<ResultRow>();
    protected int displayFlags; //bitmask flags with hints on what to return from ResultValue.getText

    protected int rowsToSearch;
    protected int rowsSearched;

    public Results() {
    }

    /**
     * Instantiates the object by including the filter specifier that was used to search for the results.
     * This information will be available to the client as metadata for the result data.
     *
     * @param filter the filter specifier
     */
    public Results(FilterSpecifier filter) {
        this.ctypes = new ArrayList<ColumnType>();
        if (filter.getColumnTypes() != null) {
            for (ColumnType ctype : filter.getColumnTypes()) {
                if (ctype.isPicked()) {
                    ctypes.add(ctype);
                }
            }
        }
        this.listBy = filter.getListBy();
        this.geneList = filter.getGeneList();
        this.patientList = filter.getPatientList();
        this.disease = filter.getDisease();
        this.chromRegions = filter.getChromRegions();
        this.geneListOptions = filter.getGeneListOptions();
        this.patientListOptions = filter.getPatientListOptions();
    }

    public List<ResultRow> getRows() {
        return rows;
    }

    /**
     * Copy constructor.
     *
     * @param source the source to copy
     */
    public Results(Results source) {
        initialize(source);
    }

    /**
     * Initializes a new result set from another one
     *
     * @param source the source, takes column types and listby settings only
     */
    public void initialize(Results source) {
        this.listBy = source.getListBy();
        this.geneList = source.getGeneList();
        this.patientList = source.getPatientList();
        this.disease = source.getDisease();
        this.chromRegions = source.getChromRegions();
        this.geneListOptions = source.getGeneListOptions();
        this.patientListOptions = source.getPatientListOptions();
        this.ctypes = source.getColumnTypes();
    }

    /**
     * Returns the total number of pages returned by a search. At first, this is an estimated number of pages, as determined by the
     * DAO. Later, when the DAO has determined the actual number of rows, this will represent the precise number
     * of pages.
     *
     * @return total number of pages
     */
    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * @return the current page number. 1-based.
     */
    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * Returns the total number of rows returned by a search. At first, this is an estimated number of rows, as determined by the
     * DAO. Later, when the DAO has determined the actual number of rows, this will represent the precise number
     * of rows.  This does not indicate only the rows in a page, such as getActualRowCount; it is always
     * either an estimated or precise row count for the complete result.
     *
     * @return total row count
     */
    public int getTotalRowCount() {
        return totalRowCount;
    }

    public void setTotalRowCount(int totalRowCount) {
        this.totalRowCount = totalRowCount;
    }


    /**
     * If this method returns true, it means that getTotalRowCount is a precise row count (for the total
     * results, not the page) and not an estimate. Likewise, getTotalPages is a precise page count, not an estimate.
     *
     * @return true if total row count is final
     */
    public boolean isFinalRowCount() {
        return isFinalRowCount;
    }

    public void setFinalRowCount(boolean finalRowCount) {
        isFinalRowCount = finalRowCount;
    }

    /**
     * Returns the number of pages of results that have been "gathered" from the DAO into a complete result
     * set on the server.
     *
     * @return number of pages gathered
     */
    public int getGatheredPages() {
        return gatheredPages;
    }

    /**
     * @param gatheredPages number of gathered pages
     */
    public void setGatheredPages(int gatheredPages) {
        this.gatheredPages = gatheredPages;
    }

    public int getGatheredRows() {
        return gatheredRows;
    }

    public void setGatheredRows(int gatheredRows) {  //todo  get rid of stuff we don't need any more
        this.gatheredRows = gatheredRows;
    }

    /**
     * Returns the number of rows per page.
     *
     * @return number of rows per page
     */
    public int getRowsPerPage() {
        return rowsPerPage;
    }

    public void setRowsPerPage(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    /**
     * Returns true when all rows have been gathered from the DAO into a complete result set on the server.
     * Note that this will often be the same as isFinalRowCount() but does not have to be.
     * The DAO may produce a final row count before gathering all rows.
     *
     * @return true if all rows have been gathered
     */
    public boolean isGatheredAllRows() {
        return isFinalRowCount() && getTotalRowCount() == getActualRowCount();
    }

    /**
     * The actual number of rows contained in this instance. If this instance represents a page,
     * it will be one page's worth of rows. If it represents all results, it will be the total number of
     * rows gathered so far from the database.
     *
     * @return the actual number of rows
     */
    public int getActualRowCount() {
        int ret = 0;
        if (rows != null) ret = rows.size();
        return ret;
    }

    /**
     * Adds a row. The assumption is that the indexing of columnResults matches that of the ColumnTypes
     * which were added in the ctor or initialize method.
     *
     * @param rowId          Gene symbol or patient ID
     * @param columnResults  Array of resultValue instances
     * @param rowAnnotations Map of annotations to be attached to this row.
     */
    public void addRow(String rowId, ResultValue[] columnResults, Map<String, Serializable> rowAnnotations) {
        if (columnResults == null) {
            columnResults = new ResultValue[0]; //todo  fill with a ResultBlank
        }
        ResultRow row = new ResultRow(rowId, currentRow, columnResults, rowAnnotations);
        rows.add(row);

        currentRow++;
    }

    /**
     * Used to copy a row from one AnomalyResults instance to another
     *
     * @param row the row to add
     */
    public void addRow(ResultRow row) {
        rows.add(row);
    }

    /**
     * Returns a specific row.
     *
     * @param rowIdx the index to get
     * @return the row at the index
     */
    public ResultRow getRow(int rowIdx) {
        return rows.get(rowIdx);
    }

    /**
     * Sorts the rows.
     *
     * @param sortspec the specifier for sorting
     * @return true if sorting happened, false if not
     */
    public boolean sort(SortSpecifier sortspec) {
        if (!isGatheredAllRows()) {
            return false;
        }
        if (sortspec.getColumnId() == -1 && sortspec.getAnnotation() == null) {
            //nothing actionable, throw exception?
            return false;
        }

        //find the column index
        int foundCol = -1;
        if (sortspec.getColumnId() >= 0) {
            int icol = 0;
            for (ColumnType ctype : getColumnTypes()) {
                if (ctype.getId() == sortspec.getColumnId()) {
                    foundCol = icol;
                    break;
                }
                icol++;
            }
            if (foundCol == -1) {
                throw new IllegalArgumentException("Column not found: " + sortspec.getColumnId());
            }
        }

        Collections.sort(rows, new RowComparator(foundCol, sortspec.getAnnotation(), sortspec.isAscending()));  //todo  add annotation sort
        return true;
    }


    class RowComparator implements Comparator<ResultRow> {
        private int columnIndex;
        private String annotation;
        private boolean ascending;

        public RowComparator(int columnIndex, String annotation, boolean ascending) {
            this.columnIndex = columnIndex;
            this.annotation = annotation;
            this.ascending = ascending;
        }

        public int compare(ResultRow o1, ResultRow o2) {
            int ret;
            if (columnIndex >= 0) {
                if (annotation == null) {
                    ret = compareColumnValues(o1, o2);
                } else {
                    ret = compareValueAnnotations(o1, o2);
                }
            } else {
                ret = compareRowAnnotations(o1, o2, null);
            }

            //sorting on two levels one is the sort spec value
            //and the second on the gene name
            if (ret == 0)
                ret = compareRowAnnotations(o1, o2, SortSpecifier.FAKE_ANNOTATION_ROW_ID);


            return ret;
        }

        private int compareRowAnnotations(ResultRow o1, ResultRow o2, String annotationParam) {
            Object val1, val2;
            String useAnnotation;
            if (annotationParam == null)
                useAnnotation = this.annotation;
            else
                useAnnotation = annotationParam;

            if (useAnnotation.equals(SortSpecifier.FAKE_ANNOTATION_ROW_ID)) {
                //special case: the row ID (patient id or gene symbol)
                val1 = o1.getName();
                val2 = o2.getName();
            } else if (useAnnotation.equals(SortSpecifier.FAKE_ANNOTATION_CHROMLOCATION)) {
                val1 = calcChromosomeLocation(o1);
                val2 = calcChromosomeLocation(o2);
            } else {
                val1 = o1.getRowAnnotation(useAnnotation);
                val2 = o2.getRowAnnotation(useAnnotation);
            }
            return compareValues(val1, val2);
        }

        private Long calcChromosomeLocation(ResultRow rr) {
            String chromStr = (String) rr.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_CHROM);
            int iChrom;
            if (chromStr == null) {
                iChrom = 24;
            } else if (chromStr.equals("X")) {
                iChrom = 22;
            } else if (chromStr.equals("Y")) {
                iChrom = 23;
            } else if (chromStr.equals("?")) {
                iChrom = 24;
            } else {
                iChrom = Integer.parseInt(chromStr);
            }
            long iStart = 0;
            if (rr.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_START) != null) {
                iStart = (Long) rr.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_REGION_START);
            }
            return (long) iChrom * 1000000000L + iStart;
        }

        private int compareValueAnnotations(ResultRow o1, ResultRow o2) {
            ResultValue rv1 = o1.getColumnResults()[columnIndex];
            ResultValue rv2 = o2.getColumnResults()[columnIndex];
            Object anot1 = rv1.getValueAnnotation(annotation);
            Object anot2 = rv2.getValueAnnotation(annotation);
            return compareValues(anot1, anot2);
        }

        private int compareColumnValues(ResultRow o1, ResultRow o2) {
            ResultValue r1 = o1.getColumnResults()[columnIndex];
            ResultValue r2 = o2.getColumnResults()[columnIndex];
            return compareValues(r1.getSortableValue(), r2.getSortableValue());
        }

        private int compareValues(Object val1, Object val2) {
            int ret;
            if (val1 == null && val2 == null) {
                ret = 0;
            } else if (val1 == null || val2 == null) {
                //one null, other not. Sort non-null value to top on first click
                if (val1 == null) {
                    ret = (ascending ? -1 : 1);
                } else {
                    ret = (ascending ? 1 : -1);
                }
            } else if (val1 instanceof Boolean && val2 instanceof Boolean) {
                //special case for boolean: we want True to sort to the top on first click
                if (ascending) {
                    ret = ((Boolean) val2).compareTo((Boolean) val1);
                } else {
                    ret = ((Boolean) val1).compareTo((Boolean) val2);
                }
            } else if (val1 instanceof Comparable && val2 instanceof Comparable) {  //is it slow to keep checking the type?
                if (ascending) {
                    ret = ((Comparable) val1).compareTo(val2);
                } else {
                    ret = ((Comparable) val2).compareTo(val1);
                }
            } else if (val1 instanceof Number && val2 instanceof Number) {
                double dval1 = ((Number) val1).doubleValue();
                double dval2 = ((Number) val2).doubleValue();
                if (dval1 > dval2) {
                    ret = (ascending ? 1 : -1);
                } else if (dval1 < dval2) {
                    ret = (ascending ? -1 : 1);
                } else {
                    ret = 0;
                }
            } else {
                throw new IllegalArgumentException("ResultValue.getSortableValue() must implement Comparable or extend Number");
            }
            return ret;
        }
    }

    public void addDisplayFlag(int flag) {
        displayFlags = displayFlags | flag;
    }

    public boolean hasDisplayFlag(int flag) {
        return (displayFlags & flag) == flag;
    }

    public void clearDisplayFlags() {
        displayFlags = 0;
    }

    public boolean isEmpty() {
        return isFinalRowCount() && getActualRowCount() == 0;
    }

    public void setRowsToSearch(int toSearch) {
        this.rowsToSearch = toSearch;
    }

    public int getRowsToSearch() {
        return rowsToSearch;
    }

    public void setRowsSearched(int searched) {
        this.rowsSearched = searched;
    }

    public int getRowsSearched() {
        return rowsSearched;
    }

    /**
     * Returns a clone of the Results.
     *
     * @param matchesFilterOnly if true, includes only rows with the annotation ROWANNOTATIONKEY_MATCHED_SEARCH set to true
     * @return
     */
    public Results cloneResults(boolean matchesFilterOnly) {
        Results clone = new Results(this);
        for (int irow = 0; irow < this.getActualRowCount(); irow++) {
            ResultRow row = this.getRow(irow);
            if (matchesFilterOnly) {
                Boolean match = (Boolean) row.getRowAnnotation(AnomalySearchConstants.ROWANNOTATIONKEY_MATCHED_SEARCH);
                if (match != null && match) {
                    clone.addRow(row);
                }
            } else {
                clone.addRow(row);
            }
        }
        return clone;
    }
}

