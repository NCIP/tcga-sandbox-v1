/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a data matrix file.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class DataMatrix {

    private String filename;
    private int reporterCount;
    private File file;

    public String toString() {
        return filename;
    }

    /**
     * The Names which were in the first line of the file
     */
    private String[] names;
    /**
     * The types of data in the file
     */
    private String[] quantitationTypes;
    /**
     * What kind of name is in Names?  Generally "Hybridization REF"
     */
    private String nameType;
    /**
     * The type of reporter.  Should be "Composite Element REF" or "Reporter REF"
     */
    private String reporterType;
    /**
     * The types (names) of the constants, if any
     */
    private String[] constantTypes;
    /**
     * The constant values.  Each array maps to a type from constantTypes.
     */
//    private final Map<String, String[]> constants = new HashMap<String, String[]>();

    /**
     * Set the Names of the columns in the file.  May be repeats if there are multiple quantitation types.
     *
     * @param names the array of names from the file
     */
    public void setNames( final String[] names ) {
        this.names = names.clone();
    }

    public String[] getNames() {
        return names;
    }

    public String getName( final int column ) {
        return names[column];
    }

    public void setQuantitationTypes( final String[] quantitationTypes ) {
        this.quantitationTypes = quantitationTypes.clone();
    }

    public void setNameType( final String nameType ) {
        this.nameType = nameType;
    }

    public String getNameType() {
        return nameType;
    }

    public void setReporterType( final String reporterType ) {
        this.reporterType = reporterType;
    }

    public String getReporterType() {
        return reporterType;
    }

    public String[] getQuantitationTypes() {
        return quantitationTypes;
    }

    public Set<String> getDistinctQuantitationTypes() {
        Set<String> types = new HashSet<String>();
        for(String type : quantitationTypes) {
            if(!types.contains( type )) {
                types.add( type );
            }
        }
        return types;
    }

    public void setConstantTypes( final String[] constantTypes ) {
        this.constantTypes = constantTypes.clone();
    }

    public String[] getConstantTypes() {
        return constantTypes;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename( final String filename ) {
        this.filename = filename;
    }

    public void setNumReporters( final int reporterCount ) {
        this.reporterCount = reporterCount;
    }

    public int getReporterCount() {
        return reporterCount;
    }

    public void setFile( final File file ) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
