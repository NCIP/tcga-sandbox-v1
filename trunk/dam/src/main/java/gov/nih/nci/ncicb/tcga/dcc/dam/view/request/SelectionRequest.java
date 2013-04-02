/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view.request;

/**
 * Command class for DataAccessMatrixSelectorController
 */
public class SelectionRequest {

    //todo: replace with enum
    public static final String MODE_CELLS = "cells";
    public static final String MODE_HEADER = "header";
    public static final String MODE_NOOP = "noop";
    public static final String MODE_SELECTALL = "selectall";
    public static final String MODE_UNSELECTALL = "unselectall";
    private String mode, headerId, selectedCells;
    private boolean intersect;
    private long millis; //milliseconds, used for proper back button behavior

    public String getMode() {
        return mode;
    }

    public void setMode( final String mode ) {
        this.mode = mode;
    }

    public String getHeaderId() {
        return headerId;
    }

    public void setHeaderId( final String headerId ) {
        this.headerId = headerId;
    }

    public String getSelectedCells() {
        return selectedCells;
    }

    public void setSelectedCells( final String selectedCells ) {
        this.selectedCells = selectedCells;
    }

    public boolean isIntersect() {
        return intersect;
    }

    public void setIntersect( final boolean intersect ) {
        this.intersect = intersect;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis( final long millis ) {
        this.millis = millis;
    }

    public void validate() throws IllegalArgumentException {
        if(mode == null || mode.length() == 0) {
            throw new IllegalArgumentException( "mode cannot be null" );
        }
    }
}
