/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc. Copyright Notice.
 * The software subject to this notice and license includes both human readable source code form and machine readable,
 * binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.columnresults;

import gov.nih.nci.ncicb.tcgaportal.level4.gwtEnabled.domainobjects.AnomalySearchConstants;

/**
 * ResultValue class used to report anomaly results in the form of a ratio, i.e. number of patients (or genes) affected
 * by an anomaly over total number of patients (or genes) tested.
 * @author David Nassau
 *         Last updated by: $Author: whitmore $
 * @version $Rev: 9303 $
 */
public class AnomalyResultRatio extends ResultValue {

    private int affected; //either patients affected or genes affected, depending on orientation of results
    private int total;    //total patients or total genes

    public AnomalyResultRatio() {
    }

    public AnomalyResultRatio(int affected, int total) {
        this.affected = affected;
        this.total = total;
    }

    /**
     * Returns the number of patients (or genes) affected.
     *
     * @return the number affected
     */
    public int getAffected() {
        return affected;
    }

    public void setAffected(int affected) {
        this.affected = affected;
    }

    /**
     * Returns the total number of patients (or genes)
     *
     * @return the total patients
     */
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Returns the result in the form of a ratio string.
     *
     * @return the ratio string
     */
    public String getRatioString() {
        return affected + "/" + total;
    }

    /**
     * Returns the result in the form of a ratio float.
     *
     * @return the ratio float
     */
    public float getRatio() {
        return (float) affected / (float) total;
    }

    /**
     * Returns the result in the form of a percentage.
     *
     * @return the percentage
     */
    public int getPercent() {
        //double ratio = (double)affected/(double)total;
        float ratio = (float) affected / (float) total;
        return (int) Math.round(ratio * 100.);
    }

    public boolean equals(Object o) {
        if (!(o instanceof AnomalyResultRatio)) {
            return false;
        }
        AnomalyResultRatio rr = (AnomalyResultRatio) o;
        return rr.affected == affected && rr.total == total; // && rr.cnvRegion == cnvRegion && rr.paired == paired;
    }

    //default is percentage
    public String toString() {
        if (resultParent != null && resultParent.hasDisplayFlag(AnomalySearchConstants.RESULTSDISPLAYFLAG_RATIO)) {
            return getRatioString();
        } else {
            if (getPercent() == 0) {
                if (affected == 0) {
                    return "0%";
                } else {
                    return "<1%";
                }
            } else {
                return getPercent() + "%";
            }
        }
    }

    public Object getSortableValue() {
        return (double) affected / (double) total;
    }
}
