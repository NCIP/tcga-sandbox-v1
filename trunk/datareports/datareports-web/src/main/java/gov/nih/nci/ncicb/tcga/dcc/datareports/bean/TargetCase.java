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
 * bean representing target case for the PCOD
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TargetCase {

    private String disease;
    private String methylationGCC;
    private String microRNAGCC;
    private String expressionArrayGCC;
    private String rnaSeqGCC;
    private String copyNumberSNPGCC;
    private String genomeGSC;
    private String exomeGSC;
    private String mutationGSC;
    private String rnaSeqGSC;
    private String microRNAGSC;
    private String bcr;
    private String lowPassGCC;
    private String lowPassGSC;

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getMethylationGCC() {
        return methylationGCC;
    }

    public void setMethylationGCC(String methylationGCC) {
        this.methylationGCC = methylationGCC;
    }

    public String getMicroRNAGCC() {
        return microRNAGCC;
    }

    public void setMicroRNAGCC(String microRNAGCC) {
        this.microRNAGCC = microRNAGCC;
    }

    public String getExpressionArrayGCC() {
        return expressionArrayGCC;
    }

    public void setExpressionArrayGCC(String expressionArrayGCC) {
        this.expressionArrayGCC = expressionArrayGCC;
    }

    public String getCopyNumberSNPGCC() {
        return copyNumberSNPGCC;
    }

    public void setCopyNumberSNPGCC(String copyNumberSNPGCC) {
        this.copyNumberSNPGCC = copyNumberSNPGCC;
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

    public String getMicroRNAGSC() {
        return microRNAGSC;
    }

    public void setMicroRNAGSC(String microRNAGSC) {
        this.microRNAGSC = microRNAGSC;
    }

    public String getRnaSeqGCC() {
        return rnaSeqGCC;
    }

    public void setRnaSeqGCC(String rnaSeqGCC) {
        this.rnaSeqGCC = rnaSeqGCC;
    }

    public String getRnaSeqGSC() {
        return rnaSeqGSC;
    }

    public void setRnaSeqGSC(String rnaSeqGSC) {
        this.rnaSeqGSC = rnaSeqGSC;
    }

    public String getBcr() {
        return bcr;
    }

    public void setBcr(String bcr) {
        this.bcr = bcr;
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
}
