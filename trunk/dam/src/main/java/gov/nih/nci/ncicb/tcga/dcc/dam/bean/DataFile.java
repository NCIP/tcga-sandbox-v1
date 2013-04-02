/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.bean;

import gov.nih.nci.ncicb.tcga.dcc.dam.util.DAMResourceBundle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.io.File;
import java.util.TreeSet;

/**
 * Author: David Nassau
 * Instances are created by the DAO to represent files.
 * They are used to populate the DataAccessDownloadModel.
 * Then they are used to represent files the user has selected from the tree
 * The DAO adds paths to these and they are used to process
 * the archive.
 */

public abstract class DataFile implements Serializable {
    private static final long serialVersionUID = -7264509891050942767L;
    public static final String LEVEL_2 = "2";
    public static final String LEVEL_3 = "3";

    private String platformTypeId, centerId, level, displaySample, fileName, fileId, path, platformId;
    private long size;
    private boolean isProtected;
    private Collection<String> barcodes;
    private boolean isPermanentFile; //for a level 1 file OR a cached level 2 file
    private String cacheFileToGenerate; //if level 2 cache file doesn't exist, we'll create on the fly
    private Collection<String> samples = new TreeSet<String>();
    private List<String> patients = new ArrayList<String>();
    private String diseaseType;


    public Collection<String> getBarcodes() {
        return barcodes;
    }


    public void setBarcodes(final Collection<String> barcodes) {
        this.barcodes = barcodes;
    }

    public Collection<String> getSamples() {
        return samples;
    }

    public void setSamples(final Collection<String> samples) {
        this.samples = samples;
    }

    public void setPatientsFromSamples(final Collection<String> samples) {
        // need to convert sample to patient and make a unique Set
        for (final String s : samples) {
            String patientId = s.substring(0, 12);
            if (!patients.contains(patientId)) {
                patients.add(patientId);
            }
        }
    }

    //this version used to XMLDecode the object when passing by HTTP to the file packaging server

    public void setPatients(final List<String> patients) {
        this.patients = patients;
    }

    public List<String> getPatients() {
        return patients;
    }

    public String getPlatformTypeId() {
        return platformTypeId;
    }

    public void setPlatformTypeId(final String platformTypeId) {
        this.platformTypeId = platformTypeId;
    }

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(final String centerId) {
        this.centerId = centerId;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(final String platformId) {
        this.platformId = platformId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(final String level) {
        this.level = level;
    }

    /**
     * @param value File name as it will be displayed in the DAD tree, not including the sample Id or "(protected)"
     */
    public void setFileName(final String value) {
        fileName = value;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * Internal file Id which will not be displayed. We will use this to retrieve the file (in case of level 1)
     * or to construct the file on the fly (level 2+) when needed
     *
     * NOTE: for level 3 data files, this will be set to the source file type!  Which is sort of not intuitive.
     *
     * @param value the file id
     */
    public void setFileId(final String value) {
        fileId = value;
    }

    public String getFileId() {
        return fileId;
    }

    /**
     * Size is in bytes.
     * It is both displayed in the tree and also used to calculate download size on the fly.
     *
     * @param value size in bytes
     */
    public void setSize(final long value) {
        size = value;
    }

    public long getSize() {
        return size;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(final boolean aProtected) {
        isProtected = aProtected;
    }

    public String getDisplaySample() {
        return displaySample;
    }

    public void setDisplaySample(final String displaySample) {
        this.displaySample = displaySample;
    }

    public String getDisplayBarcodes() {
        StringBuilder displayBarcodes = new StringBuilder();

        if (barcodes != null) {
            boolean firstBarcode = true;
            for (final String barcode : getBarcodes()) {
                if (!firstBarcode) {
                    displayBarcodes.append('/');
                }
                firstBarcode = false;
                displayBarcodes.append(barcode);
            }
        } else {
            displayBarcodes.append(DAMResourceBundle.getMessage("display.barcode.n_a"));
        }
        return displayBarcodes.toString();
    }

    /**
     * Initially left blank, later filled in by the DAO after the user has chosen to download
     * the file. It points to the physical location of the file on the internal file system.
     * In the case of a level 1 file, it's the location of the permanent file. For level 2+
     * it's a temp file written by the DAO from the database.
     *
     * @return path to the file
     */
    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * Set to true if the file is an actual file on disk, such as a level 1 file, a metadata file,
     * or a cached level 2/3/clinical file.
     *
     * @return boolean
     */
    public boolean isPermanentFile() {
        return isPermanentFile;
    }

    public void setPermanentFile(final boolean permanentFile) {
        isPermanentFile = permanentFile;
    }

    /**
     * Filled in by the file packager if a new cache file must be created.
     *
     * @return string
     */
    public String getCacheFileToGenerate() {
        return cacheFileToGenerate;
    }

    public void setCacheFileToGenerate(final String cacheFileToGenerate) {
        this.cacheFileToGenerate = cacheFileToGenerate;
    }

    public void setDiseaseType(final String diseaseType) {
        this.diseaseType = diseaseType;
    }

    public String getDiseaseType() {
        return diseaseType;
    }

    /**
     * Called from the file packager for each file to determine whether it needs to generate a new
     * cache file.
     *
     * @return true if cache file must be generated
     */
    public boolean decideWhetherToGenerateCacheFile() {
        boolean ret = false;
        if (mayPossiblyGenerateCacheFile()) { //only certain types of file are subject to caching
            if (isPermanentFile()) {           //will be marked as permanent file if it should be gotten from cache
                if (!(new File(getPath())).exists()) {
                    //cache file doesn't exist, so we'll need to create it
                    setCacheFileToGenerate(getPath()); //save the name so we can save cache file there
                    setPath(null);                     //so it will generate a file from db
                    setPermanentFile(false);
                    ret = true;
                }
            }
        }
        return ret;
    }

    /**
     * Certain subclasses (level 2, 3, clinical) will return true, indicating that they are subject to being
     * cached.
     *
     * @return boolean
     */
    public boolean mayPossiblyGenerateCacheFile() {
        return false;
    }
}
