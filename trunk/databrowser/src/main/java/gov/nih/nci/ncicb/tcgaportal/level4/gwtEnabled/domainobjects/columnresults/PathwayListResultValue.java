/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;
import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.Results;

/**
 *
 *  @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
//todo  it's not clear this is used in any production code. I only see it used in tests. Confirm and delete
public class PathwayListResultValue extends ResultValue {
    private ResultValue avg;
    private ResultValue max;
    private boolean sortByMax = false;  //todo  maybe replace with reference to a singleton like the FilterSpecifier

    public PathwayListResultValue() {
    }

    @Override
    public void setResultParent(Results resultParent) {
        super.setResultParent(resultParent);
        if (max != null) max.setResultParent(resultParent);
        if (avg != null) avg.setResultParent(resultParent);
    }

    public ResultValue getAverage() {
        return avg;
    }

    public void setAverage(ResultValue avg) {
        this.avg = avg;
        if (resultParent != null) avg.setResultParent(resultParent);
    }

    public ResultValue getMaximum() {
        return max;
    }

    public void setMaximum(ResultValue max) {
        this.max = max;
        if (resultParent != null) avg.setResultParent(resultParent);
    }

    //average value is the default
    public String toString() {
        if (resultParent != null && resultParent.hasDisplayFlag(AnomalySearchConstants.RESULTSDISPLAYFLAG_MAX)) {
            return max.toString();
        } else {
            return avg.toString();
        }
    }

    public Object getSortableValue() {
        Object ret = null;
        if (sortByMax) {
            ret = max.getSortableValue();
        } else {
            ret = avg.getSortableValue();
        }
        return ret;
    }
}
