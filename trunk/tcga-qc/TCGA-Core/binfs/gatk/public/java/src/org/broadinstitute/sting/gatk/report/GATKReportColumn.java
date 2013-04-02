package org.broadinstitute.sting.gatk.report;

import java.util.TreeMap;

/**
 * Holds values for a column in a GATK report table
 */
public class GATKReportColumn extends TreeMap<Object, Object> {
    private String columnName;
    private Object defaultValue;
    private boolean display;

    /**
     * Construct the column object, specifying the column name, default value, and whether or not the column should be displayed
     *
     * @param columnName  the name of the column
     * @param defaultValue  the default value of the column
     * @param display  if true, the column will be displayed in the final output
     */
    public GATKReportColumn(String columnName, Object defaultValue, boolean display) {
        this.columnName = columnName;
        this.defaultValue = defaultValue;
        this.display = display;
    }

    /**
     * Initialize an element in the column with a default value
     *
     * @param primaryKey  the primary key position in the column that should be set
     */
    public void initialize(Object primaryKey) {
        this.put(primaryKey, defaultValue);
    }

    /**
     * Return an object from the column, but if it doesn't exist, return the default value.  This is useful when writing
     * tables, as the table gets written properly without having to waste storage for the unset elements (usually the zero
     * values) in the table.
     *
     * @param primaryKey  the primary key position in the column that should be set
     * @return  the value at the specified position in the column, or the default value if the element is not set
     */
    public Object getWithoutSideEffects(Object primaryKey) {
        if (!this.containsKey(primaryKey)) {
            return defaultValue;
        }

        return this.get(primaryKey);
    }

    /**
     * Return the displayable property of the column.  If true, the column will be displayed in the final output.
     * If not, printing will be suppressed for the contents of the table.
     *
     * @return  true if the column will be displayed, false if otherwise
     */
    public boolean isDisplayable() {
        return display;
    }

    /**
     * Get the display width for this column.  This allows the entire column to be displayed with the appropriate, fixed width.
     * @return the width of this column
     */
    public int getColumnWidth() {
        int maxWidth = columnName.length();

        for (Object obj : this.values()) {
            if (obj != null) {
                int width = obj.toString().length();

                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
        }

        return maxWidth;
    }
}
