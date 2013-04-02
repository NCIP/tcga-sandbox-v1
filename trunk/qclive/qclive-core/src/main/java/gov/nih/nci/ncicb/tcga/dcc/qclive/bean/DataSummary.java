/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG(TM)
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: fengla
 * Date: Aug 13, 2008
 * Time: 1:49:22 PM
 * Report model class for collecting data for web and email reports
 */
public class DataSummary {

    private String tumorType = null;
    private String centerName = null;
    private List<String> platforms = new ArrayList<String>();
    private String portionAnalyte = null;

    //CGCC Properties
    private Integer samplesFromBcrToCenter = null;
    private Integer samplesReportedOnByCenter = null;
    private Integer level1ResultsReportedByCenter = null;
    private Integer level2ResultsReportedByCenter = null;
    private Integer level3ResultsReportedByCenter = null;
    private String areLevel4ReportsReportedByCenter = null;


    //BCR Properties
    private Integer totalPatients;
    private Integer totalSampleIdsReceivedByDcc;
    private Integer totalAnalyteIdsReceivedByDcc;
    private Integer totalAliqoutIdsReceivedByDcc;
    private String fieldCompletionSummary;

    //GSC Properties
    private Integer numberOfGenes;
    private Integer totalTracesSubmittedToNcbi;
    private Integer totalTraceIdsSubmittedToDcc;
    private Integer totalValidatedSomaticGenes;
    private Integer totalUnknownMutatedSomaticGenes;
    private String lastRefresh;

    public DataSummary() {
        platforms = new ArrayList<String>();
    }

    public String getTumorType() {
        return tumorType;
    }

    public void setTumorType( final String tumorType) {
        this.tumorType = tumorType;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName( final String centerName ) {
        this.centerName = centerName;
    }

    public String getPlatform() {
        StringBuffer platformBuffer = new StringBuffer();
        for(String platform : platforms) {
            platformBuffer.append( platform );
            platformBuffer.append( ", " );
        }
        if(platformBuffer.length() > 1) {
            platformBuffer.delete( platformBuffer.length() - 2, platformBuffer.length() );
        }
        return platformBuffer.toString();
    }

    public Integer getUnaccountedForSampleCount()
    {
        Integer theUnaccountedFor = null;
        if (getSamplesFromBcrToCenter() != null)
        {
            if (getSamplesReportedOnByCenter() != null)
            {
                theUnaccountedFor = getSamplesFromBcrToCenter() - getSamplesReportedOnByCenter();
            }
            else
            {
                theUnaccountedFor = getSamplesFromBcrToCenter();
            }
        }

        return theUnaccountedFor;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void addPlatform( final String platform ) {
        platforms.add( platform );
    }

    public void setPlatform( final String platform ) {
        platforms = new ArrayList<String>();
        platforms.add( platform );
    }

    private Integer convertToInteger( String theStringRepresentation ) {
        Integer convertedValue = null;
        if(theStringRepresentation != null) {
            try {
                convertedValue = Integer.parseInt( theStringRepresentation );
            }
            catch(NumberFormatException e) {
                convertedValue = null;
            }
        }
        return convertedValue;
    }

    public Integer getSamplesReportedOnByCenter() {
        return samplesReportedOnByCenter;
    }

    public void addSamplesReportedOnByCenter( final Integer samplesReportedOnByCenter ) {
        if (this.samplesReportedOnByCenter != null)
        {
            this.samplesReportedOnByCenter += samplesReportedOnByCenter;
        }
        else
        {
            this.samplesReportedOnByCenter = samplesReportedOnByCenter;
        }
    }

    public void addSamplesReportedOnByCenter( final String samplesReportedOnByCenter ) {
        addSamplesReportedOnByCenter( convertToInteger( samplesReportedOnByCenter ) );
    }

    public Integer getLevel1ResultsReportedByCenter() {
        return level1ResultsReportedByCenter;
    }

    public void addLevel1ResultsReportedByCenter( final Integer level1ResultsReportedByCenter ) {
        if (this.level1ResultsReportedByCenter != null)
        {
            this.level1ResultsReportedByCenter += level1ResultsReportedByCenter;
        }
        else
        {
            this.level1ResultsReportedByCenter = level1ResultsReportedByCenter;
        }
    }

    public Integer getLevel2ResultsReportedByCenter() {
        return level2ResultsReportedByCenter;
    }

    public void addLevel2ResultsReportedByCenter( final Integer level2ResultsReportedByCenter ) {
        if (this.level2ResultsReportedByCenter != null)
        {
            this.level2ResultsReportedByCenter += level2ResultsReportedByCenter;
        }
        else
        {
            this.level2ResultsReportedByCenter = level2ResultsReportedByCenter;
        }
    }

    public Integer getLevel3ResultsReportedByCenter() {
        return level3ResultsReportedByCenter;
    }

    public void addLevel3ResultsReportedByCenter( final Integer level3ResultsReportedByCenter ) {
            if (this.level3ResultsReportedByCenter != null)
            {
                this.level3ResultsReportedByCenter += level3ResultsReportedByCenter;
            }
            else
            {
                this.level3ResultsReportedByCenter = level3ResultsReportedByCenter;
            }
    }

    public String getAreLevel4ReportsReportedByCenter() {
        return areLevel4ReportsReportedByCenter;
    }

    public void setAreLevel4ReportsReportedByCenter( final String areLevel4ReportsReportedByCenter ) {
        this.areLevel4ReportsReportedByCenter = areLevel4ReportsReportedByCenter;
    }

    public Integer getPercentageOflevel1ResultsReportedByCenter() {
        if(level1ResultsReportedByCenter != null && level1ResultsReportedByCenter > 0) {
            return 100 * level1ResultsReportedByCenter / samplesFromBcrToCenter;
        } else {
            return null;
        }
    }

    public Integer getPercentageOflevel2ResultsReportedByCenter() {
        if(level2ResultsReportedByCenter != null && level2ResultsReportedByCenter > 0) {
            return 100 * level2ResultsReportedByCenter / samplesFromBcrToCenter;
        } else {
            return null;
        }
    }

    public Integer getPercentageOflevel3ResultsReportedByCenter() {
        if(level3ResultsReportedByCenter != null && level3ResultsReportedByCenter > 0) {
            return 100 * level3ResultsReportedByCenter / samplesFromBcrToCenter;
        } else {
            return null;
        }
    }

    public Integer getSamplesFromBcrToCenter() {
        return samplesFromBcrToCenter;
    }

    public void setSamplesFromBcrToCenter( final Integer samplesFromBcrToCenter ) {
        this.samplesFromBcrToCenter = samplesFromBcrToCenter;
    }

    public void setCount( final String count ) {
        setSamplesFromBcrToCenter( convertToInteger( count ) );
    }

    public String getPortionAnalyte() {
        return portionAnalyte;
    }

    public void setPortionAnalyte( final String portionAnalyte) {
        this.portionAnalyte = portionAnalyte;
    }

    public Integer setCountAsNumber( final String theCountAsNumber ) {
        Integer countAsNumber;
        if(theCountAsNumber != null) {
            try {
                countAsNumber = Integer.parseInt( theCountAsNumber );
            }
            catch(NumberFormatException e) {
                countAsNumber = null;
            }
        } else {
            countAsNumber = null;
        }
        return countAsNumber;
    }

    public Integer getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients( final Integer totalPatients ) {
        this.totalPatients = totalPatients;
    }

    public Integer getTotalSampleIdsReceivedByDcc() {
        return totalSampleIdsReceivedByDcc;
    }

    public void setTotalSampleIdsReceivedByDcc( final Integer totalSampleIdsReceivedByDcc ) {
        this.totalSampleIdsReceivedByDcc = totalSampleIdsReceivedByDcc;
    }

    public Integer getTotalAnalyteIdsReceivedByDcc() {
        return totalAnalyteIdsReceivedByDcc;
    }

    public void setTotalAnalyteIdsReceivedByDcc( final Integer totalAnalyteIdsReceivedByDcc ) {
        this.totalAnalyteIdsReceivedByDcc = totalAnalyteIdsReceivedByDcc;
    }

    public Integer getTotalAliqoutIdsReceivedByDcc() {
        return totalAliqoutIdsReceivedByDcc;
    }

    public void setTotalAliqoutIdsReceivedByDcc( final Integer totalAliqoutIdsReceivedByDcc ) {
        this.totalAliqoutIdsReceivedByDcc = totalAliqoutIdsReceivedByDcc;
    }

    public String getFieldCompletionSummary() {
        return fieldCompletionSummary;
    }

    public void setFieldCompletionSummary( final String fieldCompletionSummary ) {
        this.fieldCompletionSummary = fieldCompletionSummary;
    }

    public Integer getNumberOfGenes() {
        return numberOfGenes;
    }

    public void setNumberOfGenes( final Integer numberOfGenes ) {
        this.numberOfGenes = numberOfGenes;
    }

    public Integer getTotalTracesSubmittedToNcbi() {
        return totalTracesSubmittedToNcbi;
    }

    public void setTotalTracesSubmittedToNcbi( final Integer totalTracesSubmittedToNcbi ) {
        this.totalTracesSubmittedToNcbi = totalTracesSubmittedToNcbi;
    }

    public Integer getTotalTraceIdsSubmittedToDcc() {
        return totalTraceIdsSubmittedToDcc;
    }

    public Integer getPercentageOfTraceIdsSubmittedToDcc () {
        Integer percentage = null;
        if (getTotalTracesSubmittedToNcbi() != null && getTotalTraceIdsSubmittedToDcc() != null && getTotalTracesSubmittedToNcbi()  > 0)
        {
            percentage = 100 * getTotalTraceIdsSubmittedToDcc() / getTotalTracesSubmittedToNcbi();
        }
        return percentage;
    }

    public void setTotalTraceIdsSubmittedToDcc( final Integer totalTraceIdsSubmittedToDcc ) {
        this.totalTraceIdsSubmittedToDcc = totalTraceIdsSubmittedToDcc;
    }

    public Integer getTotalValidatedSomaticGenes() {
        return totalValidatedSomaticGenes;
    }

    public void setTotalValidatedSomaticGenes( final Integer totalValidatedSomaticGenes ) {
        this.totalValidatedSomaticGenes = totalValidatedSomaticGenes;
    }

    public Integer getTotalUnknownMutatedSomaticGenes() {
        return totalUnknownMutatedSomaticGenes;
    }

    public void setTotalUnknownMutatedSomaticGenes( final Integer totalUnknownMutatedSomaticGenes ) {
        this.totalUnknownMutatedSomaticGenes = totalUnknownMutatedSomaticGenes;
    }

    public String getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh( final String lastRefresh ) {
        this.lastRefresh = lastRefresh;
    }


}



