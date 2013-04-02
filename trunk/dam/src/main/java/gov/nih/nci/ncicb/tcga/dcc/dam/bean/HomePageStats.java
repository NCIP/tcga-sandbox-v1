/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

/**
 * A bean to store a row of the HOME_PAGE_STATS table
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class HomePageStats {

    private String diseaseAbbreviation;
    private Integer casesShipped;
    private Integer casesWithData;
    private String dateLastUpdated;

    public HomePageStats(final String diseaseAbbreviation,
                         final Integer casesShipped,
                         final Integer casesWithData,
                         final String dateLastUpdated) {

        this.diseaseAbbreviation = diseaseAbbreviation;
        this.casesShipped = casesShipped;
        this.casesWithData = casesWithData;
        this.dateLastUpdated = dateLastUpdated;
    }

    public String getDiseaseAbbreviation() {
        return diseaseAbbreviation;
    }

    public void setDiseaseAbbreviation(final String diseaseAbbreviation) {
        this.diseaseAbbreviation = diseaseAbbreviation;
    }

    public Integer getCasesShipped() {
        return casesShipped;
    }

    public void setCasesShipped(final Integer casesShipped) {
        this.casesShipped = casesShipped;
    }

    public Integer getCasesWithData() {
        return casesWithData;
    }

    public void setCasesWithData(final Integer casesWithData) {
        this.casesWithData = casesWithData;
    }

    public String getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(final String dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {

        return new StringBuilder("[Disease abbreviation: ")
                .append(getDiseaseAbbreviation())
                .append("], [Cases Shipped: ")
                .append(getCasesShipped())
                .append("], [Cases with Data: ")
                .append(getCasesWithData())
                .append("], [Date Last Updated: ")
                .append(getDateLastUpdated())
                .append("]")
                .toString();
    }
}
