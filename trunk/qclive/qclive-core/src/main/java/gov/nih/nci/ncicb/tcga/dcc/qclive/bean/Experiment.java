/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents an experiment, which is a unique combination of center, disease, and platform.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Experiment {

    public static final String TYPE_CGCC = "CGCC";
    public static final String TYPE_GSC = "GSC";
    public static final String TYPE_BCR = "BCR";
    public static final String TYPE_GDAC = "GDAC";

    /**
     * Regular expression and Pattern for parsing an experiment name into center, tumor, and platform
     */
    private static final String EXPERIMENT_NAME_REGEXP = "([a-zA-Z0-9\\.\\-]+)_([a-zA-Z0-9]+)\\.([\\w\\-]+)";
    public static final Pattern EXPERIMENT_NAME_PATTERN = Pattern.compile(EXPERIMENT_NAME_REGEXP);

    /**
     * Regular expression and Pattern for parsing an archive name into center, tumor, and platform
     */
    private static final String ARCHIVE_NAME_REGEXP = EXPERIMENT_NAME_REGEXP + "\\.(.*)";
    public static final Pattern ARCHIVE_NAME_PATTERN = Pattern.compile(ARCHIVE_NAME_REGEXP);

    public static final String STATUS_CHECKED = "Checked"; // is complete and ready to be validated
    public static final String STATUS_PENDING = "Pending"; // needs more archives to be ready
    public static final String STATUS_VALID = "Valid"; // validated and ready for processing and deployment
    public static final String STATUS_FAILED = "Failed"; // failed cannot be processed further
    public static final String STATUS_DEPLOYED = "Deployed"; // completely deployed and available
    public static final String STATUS_UP_TO_DATE = "Up to date"; // nothing to be done
    private String centerName;
    private String tumorName;
    private String platformName;
    private List<Archive> archives = new ArrayList<Archive>();
    private Date uploadStartDate;
    private String status;
    private String type;
    private TabDelimitedContent sdrf;
    private File sdrfFile;
    private List<Archive> previousArchives = new ArrayList<Archive>();

    public static final String BIOSPECIMEN_CORE_RESOURCE = "Biospecimen Core Resource";
    public static final String CANCER_GENOMIC_CHARACTERIZATION_CENTER = "Cancer Genomic Characterization Center";
    public static final String GENOME_SEQUENCING_CENTER = "Genome Sequencing Center";

    public Experiment() {
    }

    public Experiment( final String name ) {
        setName( name );
    }

    public final void setName( final String name ) {

        final Matcher archiveMatcher = ARCHIVE_NAME_PATTERN.matcher(name);
        final Matcher experimentMatcher = EXPERIMENT_NAME_PATTERN.matcher(name);

        if (archiveMatcher.matches()) {

            setCenterName(archiveMatcher.group(1));
            setTumorName(archiveMatcher.group(2));
            setPlatformName(archiveMatcher.group(3));

        } else if (experimentMatcher.matches()) {

            setCenterName(experimentMatcher.group(1));
            setTumorName(experimentMatcher.group(2));
            setPlatformName(experimentMatcher.group(3));

        } else {
            throw new IllegalArgumentException(name + " is not a valid Experiment name");
        }
    }

    public String getName() {
        return centerName + "_" + tumorName + "." + platformName;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName( final String centerName ) {
        this.centerName = centerName;
    }

    public String getTumorName() {
        return tumorName;
    }

    public void setTumorName( final String tumorName ) {
        this.tumorName = tumorName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName( final String platformName ) {
        this.platformName = platformName;
    }

    public String toString() {
        return getName();
    }

    public boolean equals( final Object o ) {
        return o instanceof Experiment && this.toString().equals( o.toString() );
    }

    public void addArchive( final Archive a ) {
        if(archives == null) {
            archives = new ArrayList<Archive>();
        }
        archives.add( a );
    }

    public void setArchives( final List<Archive> archives ) {
        this.archives = archives;
    }

    public List<Archive> getArchives() {
        return archives;
    }

    public Archive getArchive( final String name ) {
        for(final Archive archive : archives) {
            if(archive.getRealName().equals( name )) {
                return archive;
            }
        }
        for(final Archive archive : previousArchives) {
            if(archive.getRealName().equals( name )) {
                return archive;
            }
        }
        return null;
    }

    public List<Archive> getArchivesForStatus( final String status ) {
        final List<Archive> forStatus = new ArrayList<Archive>();
        for(final Archive a : archives) {
            if(status.equals(a.getDeployStatus())) {
                forStatus.add( a );
            }
        }
        return forStatus;
    }

    public Date getUploadStartDate() {
        return uploadStartDate;
    }

    public void setUploadStartDate( final Date uploadStartDate ) {
        this.uploadStartDate = uploadStartDate;
    }

    public void setStatus( final String s ) {
        this.status = s;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public void setType( final String type ) {
        this.type = type;
    }

    public void setSdrf( final TabDelimitedContent sdrf ) {
        this.sdrf = sdrf;
        for(final Archive a : archives) {
            a.setSdrf( sdrf );
        }
    }

    public TabDelimitedContent getSdrf() {
        return sdrf;
    }

    public void addPreviousArchive( final Archive previous ) {
        if(previousArchives == null) {
            previousArchives = new ArrayList<Archive>();
        }
        previousArchives.add( previous );
    }

    public void setPreviousArchives( final List<Archive> previousArchives ) {
        this.previousArchives = previousArchives;
    }

    public List<Archive> getPreviousArchives() {
        return previousArchives;
    }

    public File getSdrfFile() {
        return sdrfFile;
    }

    public void setSdrfFile( final File sdrfFile ) {
        this.sdrfFile = sdrfFile;
        for(final Archive a : archives) {
            a.setSdrfFile( sdrfFile );
        }
    }

    /**
     * Gets the archive that the given archive is replacing.
     *
     * @param archive the new archive
     * @return the archive that will be replaced by this archive
     */
    public Archive getPreviousArchiveFor(final Archive archive) {
        if (! archives.contains(archive)) {
            // if this archive is not part of this experiment, we don't know what the previous archive is
            return null;
        }
        // all archives in experiment are already known to be for the same center, platform, and disease
        // so just check type and serial index to find previous
        for (final Archive previousArchive : previousArchives) {
            if (archive.getArchiveType().equals(previousArchive.getArchiveType()) &&
                    archive.getSerialIndex().equals(previousArchive.getSerialIndex())) {
                return previousArchive;
            }
        }
        return null;
    }

    /**
     * Gets the revised version of the given archive in this experiment.
     *  
     * @param archive the archive that is being replaced
     * @return the archive that is replacing the given archive, or null if none
     */
    public Archive getRevisedArchiveFor(final Archive archive) {
        if (! previousArchives.contains(archive)) {
            return null;
        }
        for (final Archive currentArchive : archives) {
            if (archive.getArchiveType().equals(currentArchive.getArchiveType()) &&
                    archive.getSerialIndex().equals(currentArchive.getSerialIndex())) {
                return currentArchive;
            }
        }
        return null;
    }


    /**
     * Gets the human-readable description for the experiment type.
     * @param experimentType the experiment type
     * @return human-readable description or the experiment type if description isn't known
     */
    public static String getDescriptionForType(final String experimentType) {
        if (TYPE_BCR.equals(experimentType)) {
            return BIOSPECIMEN_CORE_RESOURCE;
        } else if (TYPE_CGCC.equals(experimentType)) {
            return CANCER_GENOMIC_CHARACTERIZATION_CENTER;
        } else if (TYPE_GSC.equals(experimentType)) {
            return GENOME_SEQUENCING_CENTER;
        } else {
            return experimentType;
        }
    }
}
