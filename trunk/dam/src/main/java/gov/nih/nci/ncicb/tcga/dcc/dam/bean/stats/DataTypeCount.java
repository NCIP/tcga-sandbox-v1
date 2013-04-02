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
import javax.xml.bind.annotation.XmlTransient;

/**
 * Defines a row in the Case Counts and Findings table on the Cancer Details
 * page
 *
 * @author Jon Whitmore
 *         Last updated by: $Author$
 * @version $Rev$
 */
@XmlRootElement(name = "dataTypeCount")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataTypeCount implements Comparable {

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof DataTypeCount)) {
            return -1;
        }
        DataTypeCount t = (DataTypeCount) o;
        return (this.getTumorAbbrev() + this.getCountTypeValue()).compareTo(t.getTumorAbbrev() + t.getCountTypeValue());
    }

    /**
     * Represents one of the possible types with respect to counts
     */
    public static enum CountType {

        Case("Cases"),
        HealthyControl("Organ-Specific Controls");

        /**
         * The human readable value of this enum
         */
        private String value;

        private CountType(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    @XmlElement(name = "tumorAbbrev")
    private String tumorAbbrev;

    @XmlElement(name = "total")
    private int total;

    @XmlElement(name = "mRna")
    private int mRna;

    @XmlElement(name = "miRna")
    private int miRna;

    @XmlElement(name = "snp")
    private int snp;

    @XmlElement(name = "methylation")
    private int methylation;

    @XmlElement(name = "exome")
    private int exome;

    @XmlElement(name = "clinical")
    private int clinical;

    @XmlTransient
    private CountType countType;

    public DataTypeCount(final String tumor, final CountType type) {
        this.setTumorAbbrev(tumor);
        this.setCountType(type);
    }

    public DataTypeCount() {

    }

    /**
     * Return the human readable value of the enum
     *
     * @return the human readable value of the enum
     */
    @XmlElement(name = "countType")
    public String getCountTypeValue() {
        return getCountType().getValue();
    }

    public String getTumorAbbrev() {
        return tumorAbbrev;
    }

    public void setTumorAbbrev(final String tumorAbbrev) {
        this.tumorAbbrev = tumorAbbrev;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(final int total) {
        this.total = total;
    }

    public int getmRna() {
        return mRna;
    }

    public void setmRna(final int mRna) {
        this.mRna = mRna;
    }

    public int getMiRna() {
        return miRna;
    }

    public void setMiRna(final int miRna) {
        this.miRna = miRna;
    }

    public int getSnp() {
        return snp;
    }

    public void setSnp(final int snp) {
        this.snp = snp;
    }

    public int getMethylation() {
        return methylation;
    }

    public void setMethylation(final int methylation) {
        this.methylation = methylation;
    }

    public int getExome() {
        return exome;
    }

    public void setExome(final int exome) {
        this.exome = exome;
    }

    public int getClinical() {
        return clinical;
    }

    public void setClinical(final int clinical) {
        this.clinical = clinical;
    }

    public CountType getCountType() {
        return countType;
    }

    public void setCountType(final CountType countType) {
        this.countType = countType;
    }
}
