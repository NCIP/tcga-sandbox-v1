/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.service.FileTypeLookup;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Archive bean for autoloader.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class LoaderArchive implements Serializable {

    private File directory;
    private LoaderSDRF autoLoaderSdrf;
    private String center;
    private String disease;
    private String platform;
    private int batch;
    private int revision;
    private String basename;
    private FileTypeLookup ftLookup;
    private List<DataFile> dataFiles;
    private String archiveName;
    private long experimentId;

    public LoaderArchive(String directoryName, FileTypeLookup ftLookup) throws LoaderException {
        setDirectory(directoryName);
        this.ftLookup = ftLookup;
    }

    void setDirectory(String directoryName) throws LoaderException {
        directory = new File(directoryName);
        if (!directory.exists()) {
            throw new LoaderException("Directory does not exist: " + directoryName);
        }
        if (!directory.isDirectory()) {
            throw new LoaderException("Is not a directory: " + directoryName);
        }
        try {
            parseArgumentsFromArchiveName();
        }
        catch (IOException e) {
            //todo logger
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new LoaderException(e);
        }
    }

    //some of this might be input externally

    void parseArgumentsFromArchiveName() throws IOException {
        //example: broad.mit.edu_GBM.HT_HG-U133A.1.6.0
        String dirpath = directory.getPath();
        dirpath = dirpath.replace('\\', '/');
        String filename = null;
        if (dirpath.indexOf('/') >= 0) {
            filename = dirpath.substring(dirpath.lastIndexOf('/') + 1);
        } else {
            filename = dirpath;
        }
        archiveName = filename;
        int underscore = filename.indexOf('_');
        if (underscore == -1) {
            throw new IOException("Archive name not properly formed, no '_': " + filename);
        }
        center = filename.substring(0, underscore);
        int dot1 = filename.indexOf('.', underscore + 1);
        if (dot1 == -1) {
            throw new IOException("Archive name not properly formed, no '.': " + filename);
        }
        disease = filename.substring(underscore + 1, dot1);
        int dot2 = filename.indexOf('.', dot1 + 1);
        if (dot2 == -1) {
            throw new IOException("Archive name not properly formed, no '.': " + filename);
        }
        platform = filename.substring(dot1 + 1, dot2);
        //now read from the right to get revision and batch
        dot1 = filename.lastIndexOf('.');
        dot2 = filename.lastIndexOf('.', dot1 - 1);
        revision = Integer.parseInt(filename.substring(dot2 + 1, dot1));
        dot1 = dot2;
        dot2 = filename.lastIndexOf('.', dot1 - 1);
        batch = Integer.parseInt(filename.substring(dot2 + 1, dot1));
        basename = center + "_" + disease + "." + platform;

    }

    public File getSDRFFile() throws LoaderException {
        //todo: instead of searching directory, get file name from input map?
        File[] files = directory.listFiles(new FilenameFilter() {
            public boolean accept(final File dir, final String name) {
                return name.toLowerCase().contains(".sdrf");
            }
        });
        if (files.length == 0) {
            throw new LoaderException("No sdrf file was found");
        }
        if (files.length > 1) {
            throw new LoaderException("More than one sdrf file found");
        }
        return files[0];
    }

    public List<DataFile> getDataFiles() throws LoaderException {
        if (dataFiles == null) {
            File[] files = directory.listFiles();
            dataFiles = new ArrayList<DataFile>();
            int ifile = 0;
            for (final File f : files) {
                String fname = f.getName();
                if (couldBeDataFile(fname)) {
                    String filetype = ftLookup.lookupFileType(fname, getCenter(), getPlatform());
                    if (filetype != null) {
                        DataFile dfile = new DataFile(f, filetype, 2);
                        dataFiles.add(dfile);
                    }
                }
            }
        }
        return dataFiles;
    }

    private boolean couldBeDataFile(String fname) {
        fname = fname.toLowerCase();
        boolean isDataFile = true;
        if (fname.endsWith(".sdrf.txt")) {
            isDataFile = false;
        } else if (fname.endsWith(".idf.txt")) {
            isDataFile = false;
        } else if (fname.endsWith(".cel")) {          //todo  add other non-data files
            isDataFile = false;
        } else if (fname.equals("description.txt")) {
            isDataFile = false;
        } else if (fname.equals("manifest.txt")) {
            isDataFile = false;
        } else if (fname.equals("readme.txt")) {
            isDataFile = false;
        } else if (fname.equals("sampleinfo.txt")) {
            isDataFile = false;
        } else if (fname.endsWith(".skip")) { //just a convenient way to get it to skip files, for testing
            isDataFile = false;
        }
        return isDataFile;
    }

    public String getCenter() {
        return center;
    }

    public String getDisease() {
        return disease;
    }

    public String getPlatform() {
        return platform;
    }

    public int getBatch() {
        return batch;
    }

    public int getRevision() {
        return revision;
    }

    public String getBasename() {
        return basename;
    }

    public String getArchiveName() {
        return archiveName;
    }

    public long getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(long experimentId) {
        this.experimentId = experimentId;
    }


    //todo  look this up from db?

    public String getAccessLevel() {
        String access = null;
        if (directory.getPath().toLowerCase().contains("tcga4yeo")) {
            access = "PROTECTED";
        } else {
            access = "PUBLIC";
        }
        return access;
    }

    public Set<String> getFileTypes() throws LoaderException {
        Set<String> uniqueTypes = new TreeSet<String>();
        for (final DataFile df : getDataFiles()) {
            String ftype = df.getFileType();
            if (!uniqueTypes.contains(ftype)) {
                uniqueTypes.add(ftype);
            }
        }
        return uniqueTypes;
    }

    public List<DataFile> getFilesForType(String datatype) throws LoaderException {
        List<DataFile> files4type = new ArrayList<DataFile>();
        for (DataFile df : getDataFiles()) {
            if (datatype.equals(df.getFileType())) {
                files4type.add(df);
            }
        }
        return files4type;
    }

    public String toString() {
        return new StringBuilder(disease)
                .append("-")
                .append(center)
                .append("-")
                .append(platform)
                .append("-")
                .append(experimentId)
                .toString();
    }
}
