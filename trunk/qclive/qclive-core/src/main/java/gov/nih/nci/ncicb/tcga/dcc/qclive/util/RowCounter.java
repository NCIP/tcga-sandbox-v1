/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility to keep track of the rows in a table with a given name
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class RowCounter implements Serializable {

    //storing as one-element array means we don't keep having to insert new elements into the map
    private Map<String, Long[]> rowCounts;

    public RowCounter() {
        rowCounts = new HashMap<String, Long[]>();
    }

    public void addToRowCounts(String table) {
        addToRowCounts(table, 1);
    }

    public void addToRowCounts(String table, long rows) {
        Long[] count = rowCounts.get(table);
        if (count == null) {
            count = new Long[1];
            count[0] = (long) rows;
            rowCounts.put(table, count);
        } else {
            count[0] += rows;
        }
    }

    public void addToRowCounts(RowCounter otherRowCounter) {
        for (String table : otherRowCounter.rowCounts.keySet()) {
            addToRowCounts(table, otherRowCounter.rowCounts.get(table)[0]);
        }
    }

    public long getRowCount(String table) {
        long count = 0;
        Long[] ctr = rowCounts.get(table);
        if (ctr != null && ctr.length > 0) {
            count = ctr[0];
        }
        return count;
    }

}
