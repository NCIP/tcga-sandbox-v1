/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.bean;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing various aspects of an archive.
 *
 * @author sfeirr
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Archive extends ArchiveBase {

    private static final Logger logger = Logger.getLogger(Archive.class);
    private static final long serialVersionUID = 1867998523050088907L;
    public static final String STATUS_UPLOADED = "Uploaded";
    public static final String STATUS_VALIDATED = "Validated";
    public static final String STATUS_AVAILABLE = "Available";
    public static final String STATUS_IN_REVIEW = "In Review";
    public static final String STATUS_INTERRUPTED = "Interrupted";
    public static final String STATUS_DEPLOYED = "Deployed";
    public static final String STATUS_INVALID = "Invalid";
    public static final String TYPE_MAGE_TAB = "mage-tab";
    public static final String TYPE_LEVEL_1 = "Level_1";
    public static final String TYPE_LEVEL_2 = "Level_2";
    public static final String TYPE_LEVEL_3 = "Level_3";
    public static final String TYPE_LEVEL_4 = "Level_4";
    public static final String TYPE_AUX = "aux";
    public static final String TYPE_CLASSIC = "classic";
    private File theArchive = null;
    private boolean validated = false;
    private boolean expanded = false;
    private long id = 0;
    private String realName = null;
    private String tumorType = null;
    private String platform = null;
    private String serialIndex = null;
    private String revision = null;
    private String series = null;
    //TODO: dataType fields should be replaced with DatType bean
    private String dataType = null;
    private Integer dataTypeId;
    private Date dateAdded = null;
    private String deployStatus = null;
    private String deployLocation = null;
    private String secondaryDeployLocation = null;
    private static final String SEPARATOR = File.separator;
    private Platform thePlatform = null;
    private Tumor theTumor = null;
    private Center theCenter = null;
    private String displayVersion = null;
    private int isLatest = 0;
    private String archiveType;
    private TabDelimitedContent sdrf;
    private Integer dataLevel;
    private Integer archiveTypeId;
    private File sdrfFile;
    private Map<String, Long> filenameToIdMap;
    private List<String> deployedWarningMessages;
    private Boolean isDataTypeCompressed;

    public Archive(String file) {
        theArchive = new File(file);
    }

    public Archive() {
    }

    /**
     * This method returns the combination of all values of the series, revision and serial to perform a display version
     * for sorting and comparing the version in the UI.
     *
     * @return the display version
     */
    public String getDisplayVersion() {
        return displayVersion;
    }

    public void setDisplayVersion(final String displayVersion) {
        this.displayVersion = displayVersion;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(final Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDeployStatus() {
        return deployStatus;
    }

    public void setDeployStatus(final String deployStatus) {
        this.deployStatus = deployStatus;
    }

    public String getDeployLocation() {
        return deployLocation;
    }

    public void setDeployLocation(final String deployLocation) {
        this.deployLocation = deployLocation;
    }

    public String getSecondaryDeployLocation() {
        return secondaryDeployLocation;
    }

    public void setSecondaryDeployLocation(final String location) {
        secondaryDeployLocation = location;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(final String dataType) {
        this.dataType = dataType;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(final String realName) {
        this.realName = realName;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated() {
        this.validated = true;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded() {
        this.expanded = true;
    }

    public boolean exists() {
        return theArchive.exists();
    }

    public String fullArchivePathAndName() throws IOException {
        return theArchive.getCanonicalPath();
    }

    public String realName() {
        return theArchive.getName();
    }

    public String getRealPath() throws IOException {
        return theArchive.getCanonicalPath().substring(0, theArchive.getCanonicalPath().lastIndexOf(SEPARATOR));
    }

    public String getExplodedArchiveDirectoryLocation() throws IOException {
        return theArchive.getCanonicalPath().substring(0, theArchive.getCanonicalPath().lastIndexOf(getDepositedArchiveExtension()));
    }

    public String getExplodedArchiveDirName() throws IOException {
        return theArchive.getCanonicalPath().substring(theArchive.getCanonicalPath().lastIndexOf(SEPARATOR), theArchive.getCanonicalPath().length());
    }

    public String getArchiveName() {
        try {
            return theArchive.getCanonicalPath().substring(theArchive.getCanonicalPath().lastIndexOf(SEPARATOR) + 1, theArchive.getCanonicalPath().lastIndexOf(getDepositedArchiveExtension()));
        } catch (IOException e) {
            logger.info("IOException occurred when calling getArchiveName() on archive " + this.toString(), e);
            return null;
        }
    }


    public String getTumorType() {
        return tumorType;
    }

    public void setTumorType(final String tumorType) {
        this.tumorType = tumorType;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(final String platform) {
        this.platform = platform;
    }

    public String getSerialIndex() {
        return serialIndex;
    }

    public void setSerialIndex(final String serialIndex) {
        this.serialIndex = serialIndex;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(final String revision) {
        this.revision = revision;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(final String series) {
        this.series = series;
    }

    public File getArchiveFile() {
        return theArchive;
    }

    public void setArchiveFile(final File theArchive) {
        this.theArchive = theArchive;
    }

    public Platform getThePlatform() {
        return thePlatform;
    }

    public void setThePlatform(final Platform thePlatform) {
        this.thePlatform = thePlatform;
    }

    public Tumor getTheTumor() {
        return theTumor;
    }

    public void setTheTumor(final Tumor theTumor) {
        this.theTumor = theTumor;
    }

    public Center getTheCenter() {
        return theCenter;
    }

    public void setTheCenter(final Center theCenter) {
        this.theCenter = theCenter;
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Archive archive = (Archive) o;
        if (id != archive.id) {
            return false;
        }
        if (realName == null || !realName.equals(archive.realName)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result;
        result = getId().hashCode();
        result = 31 * result + realName.hashCode();
        return result;
    }

    public String toString() {
        if (realName != null) {
            return realName;
        } else if (theArchive != null) {
            return getArchiveName();
        } else {
            return "uninitialized archive";
        }
    }

    /**
     * Used to return the location of the exploded directory in the ftp site.
     *
     * @return location of dir without archive file name
     */
    public String getDeployDirectory() {
        return getDeployLocation() == null ? null :
                getDeployLocation().substring(0, getDeployLocation().lastIndexOf(getDeployedArchiveExtension()));
    }

    /**
     * Used to return the location of the exploded directory in the ftp site.
     *
     * @return location of dir without archive file name
     */
    public String getSecondaryDeployDirectory() {
        return getSecondaryDeployLocation() == null ? null :
                getSecondaryDeployLocation().substring(0, getSecondaryDeployLocation().lastIndexOf(getSecondaryDeployedArchiveExtension()));
    }

    public int getLatest() {
        return isLatest;
    }

    public void setLatest(final int latest) {
        isLatest = latest;
    }

    public String getArchiveType() {
        return archiveType;
    }

    public Integer getArchiveTypeId() {
        return archiveTypeId;
    }

    public void setArchiveTypeId(final Integer archiveTypeId) {
        this.archiveTypeId = archiveTypeId;
    }

    private Pattern levelPattern = Pattern.compile("Level_(\\d+)");

    public void setArchiveType(final String archiveType) {
        this.archiveType = archiveType;
        if (archiveType != null) {
            Matcher matcher = levelPattern.matcher(archiveType);
            if (matcher.matches()) {
                try {
                    this.setDataLevel(Integer.valueOf(matcher.group(1)));
                } catch (NumberFormatException e) {
                    // do nothing, this isn't a numeric level
                    this.setDataLevel(ConstantValues.DATALEVEL_UNKNOWN);
                }
            }
        }
    }


    public String getExperimentName() {
        return getDomainName() + "_" + getTumorType() + "." + getPlatform();
    }

    public void setSdrf(final TabDelimitedContent sdrf) {
        this.sdrf = sdrf;
    }

    public TabDelimitedContent getSdrf() {
        return sdrf;
    }

    public void setDataLevel(final Integer level) {
        this.dataLevel = level;
    }

    public Integer getDataLevel() {
        return dataLevel;
    }

    public void setSdrfFile(final File sdrfFile) {
        this.sdrfFile = sdrfFile;
    }

    public File getSdrfFile() {
        return sdrfFile;
    }


    public static String getDescriptionForType(String archiveType) {
        if (TYPE_AUX.equals(archiveType)) {
            return "auxiliary";
        } else if (TYPE_LEVEL_1.equals(archiveType)) {
            return "Level 1 (raw)";
        } else if (TYPE_LEVEL_2.equals(archiveType)) {
            return "Level 2 (processed)";
        } else if (TYPE_LEVEL_3.equals(archiveType)) {
            return "Level 3 (segmented/interpreted)";
        } else if (TYPE_LEVEL_4.equals(archiveType)) {
            return "Level 4 (summary finding)";
        } else if (TYPE_MAGE_TAB.equals(archiveType)) {
            return "MAGE-TAB";
        } else {
            return archiveType;
        }
    }

    public Map<String, Long> getFilenameToIdToMap() {
        return filenameToIdMap;
    }

    public void setFilenameToIdMap(final Map<String, Long> filenameToIdMap) {
        this.filenameToIdMap = filenameToIdMap;
    }

    public Integer getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(final Integer dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public ArchiveBase getArchiveBase() {
        return new ArchiveBase(this);
    }

    public String getArchiveNameUpToSerialIndex() {
        final String ARCHIVE_NAME_FILTER = "{0}_{1}.{2}.{3}.{4}";
        return MessageFormat.format(ARCHIVE_NAME_FILTER,
                getDomainName(),
                getTumorType(),
                getPlatform(),
                getArchiveType(),
                getSerialIndex());
    }

    public List<String> getDeployedWarningMessages() {
        return deployedWarningMessages;
    }

    public void setDeployedWarningMessages(List<String> deployWarningMessages) {
        this.deployedWarningMessages = deployWarningMessages;
    }

    /**
     * Return whether the deployed archive is compressed (tar.gz) or not.
     *
     * @return <code>true</code> if the deployed archive is compressed (tar.gz), <code>false</code> otherwise
     */
    public boolean isDeployedArchiveCompressed() {
        boolean result = false;
        if (getDeployLocation() != null
                && getDeployLocation().endsWith(ConstantValues.COMPRESSED_ARCHIVE_EXTENSION)) {
            result = true;
        }
        return result;
    }

    /**
     * Return whether the deposited archive is compressed (tar.gz) or not.
     *
     * @return <code>true</code> if the deposited archive is compressed (tar.gz), <code>false</code> otherwise
     */
    public boolean isDepositedArchiveCompressed() {

        boolean result = false;

        try {
            if (getArchiveFile() != null
                    && getArchiveFile().getCanonicalPath().endsWith(ConstantValues.COMPRESSED_ARCHIVE_EXTENSION)) {
                result = true;
            }
        } catch (final IOException e) {
            logger.info("IOException occurred when calling isDepositedArchiveCompressed() on archive " + this.toString(), e);
        }

        return result;
    }

    /**
     * Return whether the archive deployed in secondary location is compressed (tar.gz) or not.
     *
     * @return <code>true</code> if the archive deployed in secondary location is compressed (tar.gz), <code>false</code> otherwise
     */
    public boolean isSecondaryDeployedArchiveCompressed() {

        boolean result = false;

        if (getSecondaryDeployLocation() != null
                && getSecondaryDeployLocation().endsWith(ConstantValues.COMPRESSED_ARCHIVE_EXTENSION)) {
            result = true;
        }

        return result;
    }

    public Boolean isDataTypeCompressed() {
        return isDataTypeCompressed;
    }

    public void setDataTypeCompressed(Boolean dataTypeCompressed) {
        isDataTypeCompressed = dataTypeCompressed;
    }

    /**
     * Return <code>tar.gz</code> if the secondary deployed archive is compressed, <code>tar</code> otherwise.
     *
     * @return <code>tar.gz</code> if the secondary deployed archive is compressed, <code>tar</code> otherwise
     */
    public String getSecondaryDeployedArchiveExtension() {

        if (isSecondaryDeployedArchiveCompressed()) {
            return ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
        } else {
            //the fileName filter defined in service.xml will only pick .tar and .tar.gz files
            // hence the else block instead of else if.
            return ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION;
        }
    }

    /**
     * Return <code>tar.gz</code> if the deployed archive is compressed, <code>tar</code> otherwise.
     *
     * @return <code>tar.gz</code> if the deployed archive is compressed, <code>tar</code> otherwise
     */
    public String getDeployedArchiveExtension() {

        if (isDeployedArchiveCompressed()) {
            return ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
        } else {
            //the fileName filter defined in service.xml will only pick .tar and .tar.gz files
            // hence the else block instead of else if.
            return ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION;
        }
    }

    /**
     * Return <code>tar.gz</code> if the deposited archive is compressed, <code>tar</code> otherwise.
     *
     * @return <code>tar.gz</code> if the deposited archive is compressed, <code>tar</code> otherwise
     */
    public String getDepositedArchiveExtension() {

        if (isDepositedArchiveCompressed()) {
            return ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
        } else {
            //the fileName filter defined in service.xml will only pick .tar and .tar.gz files
            // hence the else block instead of else if.
            return ConstantValues.UNCOMPRESSED_ARCHIVE_EXTENSION;
        }
    }
}