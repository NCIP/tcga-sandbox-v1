/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columntypes;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Abstract class representing any type of column.
 *
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public abstract class ColumnType implements IsSerializable {

    static long lastColTypeId = 0;

    //each column type is assigned a unique ID
    static synchronized long assignColumnTypeId() {
        return ++lastColTypeId;
    }

    private long coltypeId;
    private boolean picked;
    private String displayName;

    public ColumnType() {
        coltypeId = assignColumnTypeId();
    }

    public long getId() {
        return coltypeId;
    }

    /**
     * Set by the client to indicate that a particular column type has been "picked"
     * (e.g. checkbox checked) to be displayed in the output
     *
     * @param picked is the column selected by user
     */
    public void setPicked(boolean picked) {
        this.picked = picked;
    }

    public boolean isPicked() {
        return picked;
    }

    /**
     * @return the name that should be displayed in the UI
     */
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    /**
     * Clones this column type. We could not use Object.clone() because of serializability issues.
     *
     * @return a clone of this column
     */
    public Object cloneColumn() {
        ColumnType column = (ColumnType) instanceForClone();
        column.setPicked(isPicked());
        column.setDisplayName(getDisplayName());
        return column;
    }

    /**
     * Creates a new instance for use by cloneColumn.
     *
     * @return instance
     */
    protected abstract ColumnType instanceForClone();

    /**
     * Gets the selection criteria as a string. Used to display breadcrumb text.
     * Note: needed to use GWT NumberFormat but this is not a GWT class, so now pass in
     * the pre-formatted string for frequency (or pvalue).  If on client side, use GWT's
     * NumberFormat, if on server, use java.text.NumberFormat.
     *
     * @param formattedFrequency the frequency (or pvalue), if applicable, pre-formatted. null or empty to indicate none.
     * @return criteria in string form
     */
    //todo  instead, create a number formatter interface and inject that from GWT application
    public abstract String getDisplayCriteria(String formattedFrequency);
}
