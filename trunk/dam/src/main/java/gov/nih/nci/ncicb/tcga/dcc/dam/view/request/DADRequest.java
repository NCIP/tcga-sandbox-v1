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
 * Command class for DataAccessDownloadController
 */
public class DADRequest {

    private String selectedCells;
    private boolean flatten = false;
    private String email, email2;

    /**
     * Used by the web page to communicate the list of cells that have been selected
     * by the user using JavaScript.
     *
     * @return Comma-delimited list of the cell IDs for all cells selected by the user.
     */
    public String getSelectedCells() {
        return selectedCells;
    }

    public void setSelectedCells( final String selectedCells ) {
        this.selectedCells = selectedCells;
    }

    //an "easy" way to validate the arguments. Might replace with formal Validator class at some point..
    public void validate() throws IllegalArgumentException {
        if(selectedCells == null || selectedCells.length() == 0) {
            throw new IllegalArgumentException( "selectedCells cannot be null" );
        }
    }

    public boolean isConsolidateFiles() {
        // for now we don't allow consolidated files, because it is a performance problem
        return false;
    }

    public void setConsolidateFiles(final boolean consolidateFiles) {
        // for now we don't allow consolidated files, because it is a performance problem
        // so the property was removed.
    }

    public boolean isFlatten() {
        return flatten;
    }

    public void setFlatten(final boolean flatten) {
        this.flatten = flatten;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(final String email2) {
        this.email2 = email2;
    }
}
