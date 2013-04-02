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
 * Author: David Nassau
 */
public class ColorSchemeRequest {

    public enum Mode {

        ApplyColor, NoOp
    }

    public Mode mode = Mode.ApplyColor;
    private String colorSchemeName, selectedCells;
    private long millis;

    public Mode getMode() {
        return mode;
    }

    public void setMode( Mode mode ) {
        this.mode = mode;
    }

    public String getColorSchemeName() {
        return colorSchemeName;
    }

    public void setColorSchemeName( String colorSchemeName ) {
        this.colorSchemeName = colorSchemeName;
    }

    public String getSelectedCells() {
        return selectedCells;
    }

    public void setSelectedCells( String selectedCells ) {
        this.selectedCells = selectedCells;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis( long millis ) {
        this.millis = millis;
    }
}
