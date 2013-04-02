/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Defines a row in the main table on the Data Portal homepage
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@XmlRootElement(name = "tumorMainCount")
@XmlAccessorType(XmlAccessType.FIELD)
public class TumorMainCount implements Comparable<TumorMainCount> {

    @XmlElement(name = "tumorName")
    private String tumorName;

    @XmlElement(name = "tumorAbbreviation")
    private String tumorAbbreviation;

    @XmlElement(name = "casesShipped")
    private int casesShipped;

    @XmlElement(name = "casesWithData")
    private int casesWithData;

    @XmlElement(name = "lastUpdate")
    private String lastUpdate;

    public TumorMainCount() {}

    public TumorMainCount(final String tumorName,
                          final String tumorAbbreviation,
                          final int casesShipped,
                          final int casesWithData,
                          final String lastUpdate) {
        
        this.tumorName = tumorName;
        this.tumorAbbreviation = tumorAbbreviation;
        this.casesShipped = casesShipped;
        this.casesWithData = casesWithData;
        this.lastUpdate = lastUpdate;
    }

    public String getTumorName() {
        return tumorName;
    }

    public void setTumorName(final String tumorName) {
        this.tumorName = tumorName;
    }

    public String getTumorAbbreviation() {
        return tumorAbbreviation;
    }

    public void setTumorAbbreviation(final String tumorAbbreviation) {
        this.tumorAbbreviation = tumorAbbreviation;
    }

    public int getCasesShipped() {
        return casesShipped;
    }

    public void setCasesShipped(final int casesShipped) {
        this.casesShipped = casesShipped;
    }

    public int getCasesWithData() {
        return casesWithData;
    }

    public void setCasesWithData(final int casesWithData) {
        this.casesWithData = casesWithData;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(final String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Compare this tumor name to the other tumor name
     *
     * @param other the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     * @throws ClassCastException if the specified object's type prevents it
     *                            from being compared to this object.
     */
    @Override
    public int compareTo(final TumorMainCount other) {
        return this.getTumorName().compareTo(other.getTumorName());
    }
}
