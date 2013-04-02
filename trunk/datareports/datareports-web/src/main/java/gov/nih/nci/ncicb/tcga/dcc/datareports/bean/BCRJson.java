/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.bean;

/**
 * Bean class representing a BCR json file to use for the PCOD abd the BCR Pipeline report
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BCRJson {

    private String disease;
    private Integer shipped;
    private Integer received;
    private Integer dq_genotype;
    private Integer dq_init_screen;
    private Integer dq_mol;
    private Integer dq_other;
    private Integer dq_path;
    private Integer pending_init_screen;
    private Integer pending_mol_qc;
    private Integer pending_path_qc;
    private Integer pending_shipment;
    private Integer qual_mol;
    private Integer qual_path;
    private Integer submitted_to_bcr;
    private Integer qualified_hold;
    private Float qual_pass_rate;

    public BCRJson() {
    }

    //Used in the PCOD
    public BCRJson(String disease, Integer shipped, Integer pendingShipment, Integer received, Float jsonPassRate) {
        this.disease = disease;
        this.shipped = shipped;
        this.pending_shipment = pendingShipment;
        this.received = received;
        this.qual_pass_rate = jsonPassRate;
    }

    //Used in the BCR Pipeline report
    public BCRJson(BCRJson bcr) {
        this.disease = bcr.getDisease();
        this.shipped = bcr.getShipped();
        this.received = bcr.getReceived();
        this.dq_genotype = bcr.getDq_genotype();
        this.dq_init_screen = bcr.getDq_init_screen();
        this.dq_mol = bcr.getDq_mol();
        this.dq_other = bcr.getDq_other();
        this.dq_path = bcr.getDq_path();
        this.pending_init_screen = bcr.getPending_init_screen();
        this.pending_mol_qc = bcr.getPending_mol_qc();
        this.pending_path_qc = bcr.getPending_path_qc();
        this.pending_shipment = bcr.getPending_shipment();
        this.qual_mol = bcr.getQual_mol();
        this.qual_path = bcr.getQual_path();
        this.submitted_to_bcr = bcr.getSubmitted_to_bcr();
        this.qualified_hold = bcr.getQualified_hold();
        this.qual_pass_rate = bcr.getQual_pass_rate();
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public Integer getShipped() {
        return shipped;
    }

    public void setShipped(Integer shipped) {
        this.shipped = shipped;
    }

    public Integer getPending_shipment() {
        return pending_shipment;
    }

    public void setPending_shipment(Integer pending_shipment) {
        this.pending_shipment = pending_shipment;
    }

    public Integer getReceived() {
        return received;
    }

    public void setReceived(Integer received) {
        this.received = received;
    }

    public Integer getDq_genotype() {
        return dq_genotype;
    }

    public void setDq_genotype(Integer dq_genotype) {
        this.dq_genotype = dq_genotype;
    }

    public Integer getDq_init_screen() {
        return dq_init_screen;
    }

    public void setDq_init_screen(Integer dq_init_screen) {
        this.dq_init_screen = dq_init_screen;
    }

    public Integer getDq_mol() {
        return dq_mol;
    }

    public void setDq_mol(Integer dq_mol) {
        this.dq_mol = dq_mol;
    }

    public Integer getDq_other() {
        return dq_other;
    }

    public void setDq_other(Integer dq_other) {
        this.dq_other = dq_other;
    }

    public Integer getDq_path() {
        return dq_path;
    }

    public void setDq_path(Integer dq_path) {
        this.dq_path = dq_path;
    }

    public Integer getPending_init_screen() {
        return pending_init_screen;
    }

    public void setPending_init_screen(Integer pending_init_screen) {
        this.pending_init_screen = pending_init_screen;
    }

    public Integer getPending_mol_qc() {
        return pending_mol_qc;
    }

    public void setPending_mol_qc(Integer pending_mol_qc) {
        this.pending_mol_qc = pending_mol_qc;
    }

    public Integer getPending_path_qc() {
        return pending_path_qc;
    }

    public void setPending_path_qc(Integer pending_path_qc) {
        this.pending_path_qc = pending_path_qc;
    }

    public Integer getQual_mol() {
        return qual_mol;
    }

    public void setQual_mol(Integer qual_mol) {
        this.qual_mol = qual_mol;
    }

    public Integer getQual_path() {
        return qual_path;
    }

    public void setQual_path(Integer qual_path) {
        this.qual_path = qual_path;
    }

    public Integer getSubmitted_to_bcr() {
        return submitted_to_bcr;
    }

    public void setSubmitted_to_bcr(Integer submitted_to_bcr) {
        this.submitted_to_bcr = submitted_to_bcr;
    }

    public Integer getQualified_hold() {
        return qualified_hold;
    }

    public void setQualified_hold(Integer qualified_hold) {
        this.qualified_hold = qualified_hold;
    }

    public Float getQual_pass_rate() {
        return qual_pass_rate;
    }

    public void setQual_pass_rate(Float qual_pass_rate) {
        this.qual_pass_rate = qual_pass_rate;
    }
}
