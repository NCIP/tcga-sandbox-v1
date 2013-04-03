/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults.ResultValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */


public class ResultRow implements IsSerializable {

    protected String name;
    protected int rowIndex;
    protected ResultValue[] columnResults;
    protected Map<String, Serializable> rowAnnotations;

    public ResultRow() {
    }

    public ResultRow(String name, int row, ResultValue[] columnResults, Map<String, Serializable> rowAnnotations) {
        this.name = name;
        this.rowIndex = row;
        this.columnResults = columnResults;
        this.rowAnnotations = rowAnnotations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int row) {
        this.rowIndex = row;
    }

    public Serializable getRowAnnotation(String key) {
        Serializable ret = null;
        if (rowAnnotations != null) {
            ret = rowAnnotations.get(key);
        }
        return ret;
    }

    public Map<String, Serializable> getRowAnnotations() {
        return rowAnnotations;
    }

    public void setRowAnnotations(Map<String, Serializable> rowAnnotations) {
        this.rowAnnotations = rowAnnotations;
    }

    public void addRowAnnotation(String key, Serializable value) {
        if (rowAnnotations == null) {
            rowAnnotations = new HashMap<String, Serializable>();
        }
        rowAnnotations.put(key, value);
    }

    public ResultValue[] getColumnResults() {
        return columnResults;
    }

    public void setColumnResults(ResultValue[] columnResults) {
        this.columnResults = columnResults;
    }

    //only used in testing?

    public boolean equals(Object o) {
        if (!(o instanceof ResultRow)) {
            return false;
        }
        ResultRow r = (ResultRow) o;
        if (!r.getName().equals(getName())) {
            return false;
        }
        if (r.getRowIndex() != getRowIndex()) {
            return false;
        }
        if (rowAnnotations.size() != r.rowAnnotations.size()) {
            return false;
        }
        for (String key : rowAnnotations.keySet()) {
            if (!rowAnnotations.get(key).equals(r.rowAnnotations.get(key))) {
                return false;
            }
        }

        ResultValue[] rr1 = r.getColumnResults();
        ResultValue[] rr2 = getColumnResults();
        for (int i = 0; i < rr1.length; i++) {
            if (!rr1[i].equals(rr2[i])) {
                return false;
            }
        }

        return true;
    }

    //currently just used for testing

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        for (int i = 0; i < columnResults.length; i++) {
            sb.append(columnResults[i].toString()).append("\t");
        }
        if (rowAnnotations != null) {
            for (String key : rowAnnotations.keySet()) {
                sb.append(key).append("=").append(rowAnnotations.get(key)).append("\t");
            }
        }
        return sb.toString();
    }
}
