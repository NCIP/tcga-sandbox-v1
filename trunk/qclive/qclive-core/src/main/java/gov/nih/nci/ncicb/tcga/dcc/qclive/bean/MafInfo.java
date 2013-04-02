/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

/**
 * @author Robert Sfeir
 *         Last changed by $Author$
 * @version $Rev$
 */
public class MafInfo {

    private Integer id = null;
    private Integer centerID = null;
    private String centerName = null;
    private Long fileID = null;
    private String hugoSymbol = null;
    private Integer entrezGeneID = null;
    private String ncbibuild = null;
    private String chromosome = null;
    private Integer startPosition = null;
    private Integer endPosition = null;
    private String strand = null;
    private String variantClassification = null;
    private String variantType = null;
    private String referenceAllele = null;
    private String tumorSeqAllele1 = null;
    private String tumorSeqAllele2 = null;
    private String dbsnpRS = null;
    private String dbSNPValStatus = null;
    private String tumorSampleUUID = null;
    private String tumorSampleBarcode = null;
    private String matchNormalSampleUUID = null;
    private String matchNormalSampleBarcode = null;
    private String matchNormSeqAllele1 = null;
    private String matchNormSeqAllele2 = null;
    private String tumorValidationAllele1 = null;
    private String tumorValidationAllele2 = null;
    private String matchNormValidationAllele1 = null;
    private String matchNormValidationAllele2 = null;
    private String verificationStatus = null;
    private String validationStatus = null;
    private String mutationStatus = null;
    private String sequenceSource = null;
    private String score = null;
    private String bamFile = null;
    private String sequencer = null;
    private String sequencingPhase = null;
    private String validationMethod = null;

    public Integer getId() {
        return id;
    }

    public void setId( final Integer ID ) {
        this.id = ID;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(final String centerName) {
        this.centerName = centerName;
    }

    public String getHugoSymbol() {
        return hugoSymbol;
    }

    public void setHugoSymbol( final String hugoSymbol ) {
        this.hugoSymbol = hugoSymbol;
    }

    public Integer getEntrezGeneID() {
        return entrezGeneID;
    }

    public void setEntrezGeneID( final Integer entrezGeneID ) {
        this.entrezGeneID = entrezGeneID;
    }

    public Long getFileID() {
        return fileID;
    }

    public void setFileID( final Long fileID ) {
        this.fileID = fileID;
    }

    public Integer getCenterID() {
        return centerID;
    }

    public void setCenterID( final Integer centerID ) {
        this.centerID = centerID;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome( final String chromosome ) {
        this.chromosome = chromosome;
    }

    public Integer getStartPosition() {
        return startPosition;
    }

    public void setStartPosition( final Integer startPosition ) {
        this.startPosition = startPosition;
    }

    public Integer getEndPosition() {
        return endPosition;
    }

    public void setEndPosition( final Integer endPosition ) {
        this.endPosition = endPosition;
    }

    public String getVariantClassification() {
        return variantClassification;
    }

    public void setVariantClassification( final String variantClassification ) {
        this.variantClassification = variantClassification;
    }

    public String getVariantType() {
        return variantType;
    }

    public void setVariantType( final String variantType ) {
        this.variantType = variantType;
    }

    public String getDbsnpRS() {
        return dbsnpRS;
    }

    public void setDbsnpRS( final String dbsnpRS ) {
        this.dbsnpRS = dbsnpRS;
    }

    public String getTumorSampleBarcode() {
        return tumorSampleBarcode;
    }

    public void setTumorSampleBarcode( final String tumorSampleBarcode ) {
        this.tumorSampleBarcode = tumorSampleBarcode;
    }

    public String getMatchNormalSampleBarcode() {
        return matchNormalSampleBarcode;
    }

    public void setMatchNormalSampleBarcode( final String matchNormalSampleBarcode ) {
        this.matchNormalSampleBarcode = matchNormalSampleBarcode;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus( final String verificationStatus ) {
        this.verificationStatus = verificationStatus;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus( final String validationStatus ) {
        this.validationStatus = validationStatus;
    }

    public String getMutationStatus() {
        return mutationStatus;
    }

    public void setMutationStatus( final String mutaionStatus ) {
        this.mutationStatus = mutaionStatus;
    }

    public String getNcbibuild() {
        return ncbibuild;
    }

    public void setNcbibuild( final String ncbibuild ) {
        this.ncbibuild = ncbibuild;
    }

    public String getReferenceAllele() {
        return referenceAllele;
    }

    public void setReferenceAllele( final String referenceAllele ) {
        this.referenceAllele = referenceAllele;
    }

    public String getStrand() {
        return strand;
    }

    public void setStrand( final String strand ) {
        this.strand = strand;
    }

    public String getTumorSeqAllele1() {
        return tumorSeqAllele1;
    }

    public void setTumorSeqAllele1( final String tumorSeqAllele1 ) {
        this.tumorSeqAllele1 = tumorSeqAllele1;
    }

    public String getTumorSeqAllele2() {
        return tumorSeqAllele2;
    }

    public void setTumorSeqAllele2( final String tumorSeqAllele2 ) {
        this.tumorSeqAllele2 = tumorSeqAllele2;
    }

    public String getDbSNPValStatus() {
        return dbSNPValStatus;
    }

    public void setDbSNPValStatus( final String dbSNPValStatus ) {
        this.dbSNPValStatus = dbSNPValStatus;
    }

    public String getMatchNormSeqAllele1() {
        return matchNormSeqAllele1;
    }

    public void setMatchNormSeqAllele1( final String matchNormSeqAllele1 ) {
        this.matchNormSeqAllele1 = matchNormSeqAllele1;
    }

    public String getMatchNormSeqAllele2() {
        return matchNormSeqAllele2;
    }

    public void setMatchNormSeqAllele2( final String matchNormSeqAllele2 ) {
        this.matchNormSeqAllele2 = matchNormSeqAllele2;
    }

    public String getTumorValidationAllele1() {
        return tumorValidationAllele1;
    }

    public void setTumorValidationAllele1( final String tumorValidationAllele1 ) {
        this.tumorValidationAllele1 = tumorValidationAllele1;
    }

    public String getTumorValidationAllele2() {
        return tumorValidationAllele2;
    }

    public void setTumorValidationAllele2( final String tumorValidationAllele2 ) {
        this.tumorValidationAllele2 = tumorValidationAllele2;
    }

    public String getMatchNormValidationAllele1() {
        return matchNormValidationAllele1;
    }

    public void setMatchNormValidationAllele1( final String matchNormValidationAllele1 ) {
        this.matchNormValidationAllele1 = matchNormValidationAllele1;
    }

    public String getMatchNormValidationAllele2() {
        return matchNormValidationAllele2;
    }

    public void setMatchNormValidationAllele2( final String matchNormValidationAllele2 ) {
        this.matchNormValidationAllele2 = matchNormValidationAllele2;
    }

    public String getSequenceSource() {
        return sequenceSource;
    }

    public void setSequenceSource(final String sequenceSource) {
        this.sequenceSource = sequenceSource;
    }

    public String getScore() {
        return score;
    }

    public void setScore(final String score) {
        this.score = score;
    }

    public String getBamFile() {
        return bamFile;
    }

    public void setBamFile(final String bamFile) {
        this.bamFile = bamFile;
    }

    public String getSequencer() {
        return sequencer;
    }

    public void setSequencer(final String sequencer) {
        this.sequencer = sequencer;
    }

    public void setSequencingPhase(final String phase) {
        this.sequencingPhase = phase;
    }

    public String getSequencingPhase() {
        return sequencingPhase;
    }

    public void setValidationMethod(final String validationMethod) {
        this.validationMethod = validationMethod;
    }

    public String getValidationMethod() {
        return validationMethod;
    }

    public String getTumorSampleUUID() {
        return tumorSampleUUID;
    }

    public void setTumorSampleUUID(String tumorSampleUUID) {
        this.tumorSampleUUID = tumorSampleUUID;
    }

    public String getMatchNormalSampleUUID() {
        return matchNormalSampleUUID;
    }

    public void setMatchNormalSampleUUID(String matchNormalSampleUUID) {
        this.matchNormalSampleUUID = matchNormalSampleUUID;
    }
}