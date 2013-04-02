/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import java.sql.Timestamp;

/**
 * auther: fengla
 * Date: Apr 14, 2008
 */
public class NcbiTrace {

    private long ti;
    private String trace_name;
    private String center_name;
    private String submission_type;
    private String gene_name;
    private String reference_accession;
    private int reference_acc_max;
    private int reference_acc_min;
    private int replaced_by;
    private int basecall_length;
    private Timestamp load_date;
    private String state;

    public long getTi() {
        return ti;
    }

    public void setTi( final long ti ) {
        this.ti = ti;
    }

    public String getCenter_name() {
        return center_name;
    }

    public void setCenter_name( final String center_name ) {
        this.center_name = center_name;
    }

    public String getSubmission_type() {
        return submission_type;
    }

    public void setSubmission_type( final String submission_type ) {
        this.submission_type = submission_type;
    }

    public String getGene_name() {
        return gene_name;
    }

    public void setGene_name( final String gene_name ) {
        this.gene_name = gene_name;
    }

    public String getReference_accession() {
        return reference_accession;
    }

    public void setReference_accession( final String reference_accession ) {
        this.reference_accession = reference_accession;
    }

    public int getReference_acc_max() {
        return reference_acc_max;
    }

    public void setReference_acc_max( final int reference_acc_max ) {
        this.reference_acc_max = reference_acc_max;
    }

    public int getReference_acc_min() {
        return reference_acc_min;
    }

    public void setReference_acc_min( final int reference_acc_min ) {
        this.reference_acc_min = reference_acc_min;
    }

    public int getBasecall_length() {
        return basecall_length;
    }

    public void setBasecall_length( final int basecall_length ) {
        this.basecall_length = basecall_length;
    }

    public int getReplaced_by() {
        return replaced_by;
    }

    public void setReplaced_by( final int replaced_by ) {
        this.replaced_by = replaced_by;
    }

    public Timestamp getLoad_date() {
        return load_date;
    }

    public void setLoad_date( final Timestamp load_date ) {
        this.load_date = load_date;
    }

    public String getState() {
        return state;
    }

    public void setState( final String state ) {
        this.state = state;
    }

    public String getTrace_name() {
        return trace_name;
    }

    public void setTrace_name( final String trace_name ) {
        this.trace_name = trace_name;
    }
}
