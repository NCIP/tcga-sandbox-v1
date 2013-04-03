/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public class SortSpecifier implements IsSerializable {
    /**
     * special row "annotation" value which means either gene symbol or patient id
     */
    public static final String FAKE_ANNOTATION_ROW_ID = "rowid";
    /**
     * special row "annotation" which combines chromosome and start
     */
    public static final String FAKE_ANNOTATION_CHROMLOCATION = "chrloc";

    private long columnId = -1;
    private String annotation;
    private boolean ascending;

    public SortSpecifier() {
    }

    //no column id means row annotation
    //no annotation means use the sortableValue from the ResultValue
    //both means use a value annotation
    public SortSpecifier(long columnId, String annotation, boolean ascending) {
        this.columnId = columnId;
        this.annotation = annotation;
        this.ascending = ascending;
    }

    public long getColumnId() {
        return columnId;
    }

    public void setColumnId(long columnId) {
        this.columnId = columnId;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }
}
