/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean;

/**
 * Project Case bean
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ProjectCase {

    private String disease;
    private String diseaseName;
    private String overallProgress;
    private String methylationCGCC;
    private String microRNACGCC;
    private String expressionArrayCGCC;
    private String expressionRNASeqCGCC;
    private String copyNumberSNPCGCC;
    private String genomeGSC;
    private String exomeGSC;
    private String mutationGSC;
    private String expressionRNASeqGSC;
    private String microRNAGSC;
    private String projectedCaseBCR;
    private String currentCaseGapBCR;
    private String receivedBCR;
    private String shippedBCR;
    private String completeCases;
    private String incompleteCases;
    private String lowPassGCC;
    private String lowPassGSC;

    public ProjectCase() {
    }

    public ProjectCase(String copyNumberSNPCGCC, String methylationCGCC, String expressionRNASeqCGCC,
                       String expressionArrayCGCC, String microRNACGCC, String exomeGSC, String genomeGSC) {
        this.copyNumberSNPCGCC = copyNumberSNPCGCC;
        this.methylationCGCC = methylationCGCC;
        this.expressionRNASeqCGCC = expressionRNASeqCGCC;
        this.expressionArrayCGCC = expressionArrayCGCC;
        this.microRNACGCC = microRNACGCC;
        this.exomeGSC = exomeGSC;
        this.genomeGSC = genomeGSC;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getOverallProgress() {
        return overallProgress;
    }

    public void setOverallProgress(String overallProgress) {
        this.overallProgress = overallProgress;
    }

    public String getMethylationCGCC() {
        return methylationCGCC;
    }

    public void setMethylationCGCC(String methylationCGCC) {
        this.methylationCGCC = methylationCGCC;
    }

    public String getMicroRNACGCC() {
        return microRNACGCC;
    }

    public void setMicroRNACGCC(String microRNACGCC) {
        this.microRNACGCC = microRNACGCC;
    }

    public String getExpressionArrayCGCC() {
        return expressionArrayCGCC;
    }

    public void setExpressionArrayCGCC(String expressionArrayCGCC) {
        this.expressionArrayCGCC = expressionArrayCGCC;
    }

    public String getExpressionRNASeqCGCC() {
        return expressionRNASeqCGCC;
    }

    public void setExpressionRNASeqCGCC(String expressionRNASeqCGCC) {
        this.expressionRNASeqCGCC = expressionRNASeqCGCC;
    }

    public String getCopyNumberSNPCGCC() {
        return copyNumberSNPCGCC;
    }

    public void setCopyNumberSNPCGCC(String copyNumberSNPCGCC) {
        this.copyNumberSNPCGCC = copyNumberSNPCGCC;
    }

    public String getGenomeGSC() {
        return genomeGSC;
    }

    public void setGenomeGSC(String genomeGSC) {
        this.genomeGSC = genomeGSC;
    }

    public String getExomeGSC() {
        return exomeGSC;
    }

    public void setExomeGSC(String exomeGSC) {
        this.exomeGSC = exomeGSC;
    }

    public String getMutationGSC() {
        return mutationGSC;
    }

    public void setMutationGSC(String mutationGSC) {
        this.mutationGSC = mutationGSC;
    }

    public String getExpressionRNASeqGSC() {
        return expressionRNASeqGSC;
    }

    public void setExpressionRNASeqGSC(String expressionRNASeqGSC) {
        this.expressionRNASeqGSC = expressionRNASeqGSC;
    }

    public String getMicroRNAGSC() {
        return microRNAGSC;
    }

    public void setMicroRNAGSC(String microRNAGSC) {
        this.microRNAGSC = microRNAGSC;
    }

    public String getProjectedCaseBCR() {
        return projectedCaseBCR;
    }

    public void setProjectedCaseBCR(String projectedCaseBCR) {
        this.projectedCaseBCR = projectedCaseBCR;
    }

    public String getCurrentCaseGapBCR() {
        return currentCaseGapBCR;
    }

    public void setCurrentCaseGapBCR(String currentCaseGapBCR) {
        this.currentCaseGapBCR = currentCaseGapBCR;
    }

    public String getReceivedBCR() {
        return receivedBCR;
    }

    public void setReceivedBCR(String receivedBCR) {
        this.receivedBCR = receivedBCR;
    }

    public String getShippedBCR() {
        return shippedBCR;
    }

    public void setShippedBCR(String shippedBCR) {
        this.shippedBCR = shippedBCR;
    }

    public String getCompleteCases() {
        return completeCases;
    }

    public void setCompleteCases(String completeCases) {
        this.completeCases = completeCases;
    }

    public String getIncompleteCases() {
        return incompleteCases;
    }

    public void setIncompleteCases(String incompleteCases) {
        this.incompleteCases = incompleteCases;
    }

    public String getLowPassGCC() {
        return lowPassGCC;
    }

    public void setLowPassGCC(String lowPassGCC) {
        this.lowPassGCC = lowPassGCC;
    }

    public String getLowPassGSC() {
        return lowPassGSC;
    }

    public void setLowPassGSC(String lowPassGSC) {
        this.lowPassGSC = lowPassGSC;
    }
}//End of Class
