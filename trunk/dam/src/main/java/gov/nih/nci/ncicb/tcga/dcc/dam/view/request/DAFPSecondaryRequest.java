/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.view.request;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.web.editor.UUIDPersistenceDelegate;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Author: David Nassau
 * <p/>
 * Command class for DataAccessFileProcessingSecondaryController
 */
public class DAFPSecondaryRequest {

    //argument used in http connection to pass this class in xml form
    public static final String URL_ARGUMENT = "fileInfo";
    private boolean isProtected, flatten;
    private String email;
    private UUID filePackagerKey;
    private ArrayList<DataFile> selectedFiles;
    private String archivePhysicalPathPrefix, linkSite, disease;
    private FilterRequestI filterRequest;

    public String getDisease() {
        return disease;
    }

    public void setDisease(final String disease) {
        this.disease = disease;
    }

    public ArrayList<DataFile> getSelectedFiles() {
        return selectedFiles;
    }

    public void setSelectedFiles( final ArrayList<DataFile> selectedFiles ) {
        this.selectedFiles = selectedFiles;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected( final boolean aProtected ) {
        isProtected = aProtected;
    }

    public boolean isFlatten() {
        return flatten;
    }

    public void setFlatten( final boolean flatten ) {
        this.flatten = flatten;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( final String email ) {
        this.email = email;
    }

    public UUID getFilePackagerKey() {
        return filePackagerKey;
    }

    public void setFilePackagerKey( final UUID filePackagerKey ) {
        this.filePackagerKey = filePackagerKey;
    }

    public String getArchivePhysicalPathPrefix() {
        return archivePhysicalPathPrefix;
    }

    public void setArchivePhysicalPathPrefix( String archivePhysicalPathPrefix ) {
        this.archivePhysicalPathPrefix = archivePhysicalPathPrefix;
    }

    public String getLinkSite() {
        return linkSite;
    }

    public void setLinkSite( String linkSite ) {
        this.linkSite = linkSite;
    }

    public FilterRequestI getFilterRequest() {
        return filterRequest;
    }

    public void setFilterRequest(FilterRequestI filterRequest) {
        this.filterRequest = filterRequest;
    }

    public String toXML() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final XMLEncoder encoder = new XMLEncoder( out );
        encoder.setPersistenceDelegate(java.util.UUID.class, new UUIDPersistenceDelegate());
        encoder.writeObject( this );
        encoder.flush();
        encoder.close();
        return out.toString();
    }

    public static DAFPSecondaryRequest getInstance( final String xml ) {
        final XMLDecoder decoder = new XMLDecoder( new ByteArrayInputStream( xml.getBytes() ) );
        return (DAFPSecondaryRequest) decoder.readObject();
    }
}
