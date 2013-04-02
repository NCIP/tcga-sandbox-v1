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
 * command class for DataAccessFileProcessingController
 */
public class DAFPRequest {

    private String treeNodeIds, email;
    private boolean flatten;

    /**
     * Comma-delimited list of tree node IDs representing files selected by the user.
     *
     * @return
     */
    public String getTreeNodeIds() {
        return treeNodeIds;
    }

    public void setTreeNodeIds( final String treeNodeIds ) {
        this.treeNodeIds = treeNodeIds;
    }

    /**
     * Email address entered by the user.
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    public void setEmail( final String email ) {
        this.email = email;
    }

    /**
     * True if the user has chosen to flatten the directory structure within the archive.
     *
     * @return
     */
    public boolean isFlatten() {
        return flatten;
    }

    public void setFlatten( final boolean flatten ) {
        this.flatten = flatten;
    }

    public void validate() throws IllegalArgumentException {
        if(treeNodeIds == null || treeNodeIds.length() == 0) {
            throw new IllegalArgumentException( "treeNodeIds cannot be null" );
        }
        if(email == null || email.length() == 0) {
            throw new IllegalArgumentException( "email cannot be null" );
        }
    }
}
