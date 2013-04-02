/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import java.util.Date;

/**
 * @author Robert S. Sfeir, David Kane
 */
public class Log {

    private int log_id = 0;
    private int fileInfoId = 0;
    private Date startTime = null;
    private Date endTime = null;
    private int resultId = 0;
    private String description = null;
    private ResultType result = null;

    public Log() {
    }

    //TODO Put corresponding row creation in DDL
    enum ResultType {

        pass, fail
    }

    public int getId() {
        return log_id;
    }

    public void setId( final int id ) {
        this.log_id = id;
    }

    public int getFileInfoId() {
        return fileInfoId;
    }

    public void setFileInfoId( final int fileInfoId ) {
        this.fileInfoId = fileInfoId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime( final Date startTime ) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime( final Date endTime ) {
        this.endTime = endTime;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId( final int resultId ) {
        this.resultId = resultId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( final String description ) {
        this.description = description;
    }

    public ResultType getResult() {
        return result;
    }

    public void setResult( final ResultType result ) {
        this.result = result;
    }

    public boolean equals( final Object o ) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        final Log log = (Log) o;
        if(log_id != log.log_id) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return log_id;
    }

    public String toString() {
        return description;
    }
}
