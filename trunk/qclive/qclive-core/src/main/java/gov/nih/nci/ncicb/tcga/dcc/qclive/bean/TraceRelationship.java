/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import java.sql.Date;

/**
 * @author fengla
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TraceRelationship {

    private long traceID;
    private int biospecimenID;
    private Date dccReceived;
    private long fileID;

    public TraceRelationship() {
    }

    public long getTraceID() {
        return traceID;
    }

    public void setTraceID( final long traceID ) {
        this.traceID = traceID;
    }

    public int getBiospecimenID() {
        return biospecimenID;
    }

    public void setBiospecimenID( final int biospecimenID ) {
        this.biospecimenID = biospecimenID;
    }

    public Date getDccReceived() {
        return dccReceived;
    }

    public void setDccReceived( final Date dccReceived ) {
        this.dccReceived = dccReceived;
    }

    public long getFileID() {
        return fileID;
    }

    public void setFileID( final long fileID ) {
        this.fileID = fileID;
    }

    public boolean equals( Object o ) {
        if(o instanceof TraceRelationship) {
            TraceRelationship tr = (TraceRelationship) o;
            return tr.getBiospecimenID() == this.getBiospecimenID() && tr.getDccReceived().equals( this.getDccReceived() ) &&
                    tr.getFileID() == this.getFileID() && tr.getTraceID() == this.getTraceID();
        } else {
            return false;
        }
    }
}
