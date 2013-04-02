/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ArchiveTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ExperimentQueries using JDBC
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class ExperimentQueriesJDBCImpl extends BaseQueriesProcessor implements ExperimentQueries {

    private static final String GET_EXPERIMENT_DATA_FILES_QUERY = "select a.archive_id, a.archive_name, f.file_id, f.file_name, f.level_number " +
            "from archive_info a, file_info f, file_to_archive f2a " +
            "where a.is_latest=1 and a.deploy_status='Available' " +
            "and a.archive_id= f2a.archive_id and f2a.file_id= f.file_id " +
            "and f.level_number > 0 " +
            "and a.archive_name like ? order by a.archive_id, f.file_name";

    private ArchiveQueries archiveQueries;
    private ArchiveTypeQueries archiveTypeQueries;
    private CenterQueries centerQueries;
    private PlatformQueries platformQueries;
    private TumorQueries tumorQueries;
    private DataTypeQueries dataTypeQueries;


    public Experiment getExperimentForSingleArchive(final String archiveName) {
        return getExperiment(archiveName, true);
    }
    
    @Override
    public Experiment getExperiment(final String experimentName) {
        return getExperiment(experimentName, false);
    }

    private Experiment getExperiment(final String name, final boolean isSingleArchive) {
        final String experimentName = isSingleArchive ? name : name + ".%";
        final List<Integer> archiveIds = new ArrayList<Integer>();
        // this query will get all Available archives with "latest" status, as well as all Uploaded and Validated archives
        final String query = "select archive_id from archive_info where archive_name like '" + experimentName +
                "' AND ((is_latest=1 AND deploy_status='" + Archive.STATUS_AVAILABLE +
                "') OR deploy_status = '" + Archive.STATUS_UPLOADED +
                "' OR deploy_status = '" + Archive.STATUS_DEPLOYED +
                "' OR deploy_status = '" + Archive.STATUS_VALIDATED + "')";
        getJdbcTemplate().query(query, new RowCallbackHandler() {
            public void processRow(final ResultSet resultSet) throws SQLException {
                archiveIds.add(resultSet.getInt("archive_id"));
            }
        });
        final Experiment experiment = new Experiment();
        experiment.setName(name);
        setExperimentArchives(experiment, archiveIds);
        // if the "experimentName" is really an archive name (to limit it to a single archive) then make sure
        // the previous version, if any, is found and put into the experiment
        if (experiment.getArchives().size() == 1) {
            final Archive newArchive = experiment.getArchives().get(0);
            if (newArchive.getDeployStatus().equals(Archive.STATUS_UPLOADED)) {
                final Archive previousArchive = archiveQueries.getLatestVersionArchive(newArchive);
                if (previousArchive != null) {
                    experiment.addPreviousArchive(previousArchive);
                }
            }
        }
        return experiment;
    }

    @Override
    public Map<Archive, List<FileInfo>> getExperimentDataFiles(final String experimentName) {
        final Map<Archive, List<FileInfo>> experimentDataFiles = new HashMap<Archive, List<FileInfo>>();
        getJdbcTemplate().query(GET_EXPERIMENT_DATA_FILES_QUERY, new RowCallbackHandler() {
            @Override
            public void processRow(final ResultSet rs) throws SQLException {
                final long archiveId = rs.getLong("archive_id");
                final String archiveName = rs.getString("archive_name");
                final long fileId = rs.getLong("file_id");
                final String fileName = rs.getString("file_name");
                final Integer fileLevel = rs.getInt("level_number");

                final Archive archive = new Archive();
                archive.setId(archiveId);
                archive.setRealName(archiveName);

                List<FileInfo> archiveDataFiles = experimentDataFiles.get(archive);
                if (archiveDataFiles == null) {
                    archiveDataFiles = new ArrayList<FileInfo>();
                    experimentDataFiles.put(archive, archiveDataFiles);
                }

                final FileInfo file = new FileInfo();
                file.setId(fileId);
                file.setFileName(fileName);
                file.setDataLevel(fileLevel);
                archiveDataFiles.add(file);
            }
        }, experimentName + ".%");

        return experimentDataFiles;
    }

    public ArchiveQueries getArchiveQueries() {
        return archiveQueries;
    }

    public void setArchiveQueries(final ArchiveQueries archiveQueries) {
        this.archiveQueries = archiveQueries;
    }

    /**
     * Given a list of archive ids, creates Archive beans and sets the Experiment archives and previous archives.
     * Experiment archives are 1. available, current archives that are not going to be replaced by a new upload, 2.
     * uploaded archives, 3. validated archives (which are uploaded that have already been validated).
     *
     * @param experiment the experiment
     * @param archiveIds the ids of all available&latest, uploaded, and validated archives for this experiment
     */
    protected void setExperimentArchives(Experiment experiment, List<Integer> archiveIds) {
        Date earliestUpload = null;
        // keys = archive type, values = map, with key = archive serial index and value = the archive
        Map<String, Map<String, Archive>> archivesByType = new HashMap<String, Map<String, Archive>>();
        for (int id : archiveIds) {
            Archive a = archiveQueries.getArchive(id);
            // ignore archives without a type; those are old-style and we don't want them
            if (a != null && a.getArchiveTypeId() != null && a.getArchiveTypeId() != 0) {
                // setup center, platform, etc objects in Archive
                setupArchive(a);
                if (a.getArchiveType() != null && !a.getArchiveType().equals(Archive.TYPE_CLASSIC)) {
                    // get hash of archives found with this type, which is keyed by Batch
                    Map<String, Archive> archivesByBatch = archivesByType.get(a.getArchiveType());
                    if (archivesByBatch == null) {
                        archivesByBatch = new HashMap<String, Archive>();
                        archivesByType.put(a.getArchiveType(), archivesByBatch);
                    }
                    // if no archive for this batch and type yet, or there is one with a lower revision number, put this one in
                    if (archivesByBatch.get(a.getSerialIndex()) == null ||
                            Integer.valueOf(archivesByBatch.get(a.getSerialIndex()).getRevision()) < Integer.valueOf(a.getRevision())) {
                        // we have a newer archive... if older one is Available that means it is being replaced by an uploaded one
                        Archive prevA = archivesByBatch.get(a.getSerialIndex());
                        if (prevA != null && prevA.getDeployStatus().equals(Archive.STATUS_AVAILABLE)) {
                            experiment.addPreviousArchive(prevA);
                        }
                        archivesByBatch.put(a.getSerialIndex(), a);
                    } else if (a.getDeployStatus().equals(Archive.STATUS_AVAILABLE)) {
                        // if there is a more recent uploaded archive but this one is available, add it to previous archive list
                        experiment.addPreviousArchive(a);
                    }
                    if (a.getDeployStatus().equals(Archive.STATUS_UPLOADED)) {
                        // uploaded archives deployDirectory is the deposit directory...
                        a.setDepositLocation(a.getDeployLocation());

                        // we need to find out the earliest upload time for this group
                        if (earliestUpload == null || earliestUpload.getTime() > a.getDateAdded().getTime()) {
                            earliestUpload = a.getDateAdded();
                        }
                    }
                }
            }
        }
        for (Map<String, Archive> archiveMap : archivesByType.values()) {
            for (Archive a : archiveMap.values()) {
                a.setTheCenter(centerQueries.getCenterByName(a.getDomainName(), a.getThePlatform().getCenterType()));
                experiment.addArchive(a);
            }
        }
        experiment.setUploadStartDate(earliestUpload);
        // set the experiment type based on the archive's experiment type.
        if (experiment.getArchives().size() > 0) {
            experiment.setType(experiment.getArchives().get(0).getExperimentType());
        }
    }

    private void setupArchive(final Archive archive) {
        archive.setThePlatform(platformQueries.getPlatformForName(archive.getPlatform()));
        archive.setTheCenter(centerQueries.getCenterByName(archive.getDomainName(), archive.getThePlatform().getCenterType()));
        archive.setTheTumor(tumorQueries.getTumorForName(archive.getTumorType()));
        archive.setDataType(dataTypeQueries.getBaseDataTypeDisplayNameForPlatform(archive.getThePlatform().getPlatformId()));
        archive.setArchiveType(archiveTypeQueries.getArchiveType(archive.getArchiveTypeId()));
        archive.setArchiveFile(new File(archive.getDeployLocation()));
    }

    public void setArchiveTypeQueries(final ArchiveTypeQueries archiveTypeQueries) {
        this.archiveTypeQueries = archiveTypeQueries;
    }

    public void setCenterQueries(final CenterQueries centerQueries) {
        this.centerQueries = centerQueries;
    }

    public void setPlatformQueries(final PlatformQueries platformQueries) {
        this.platformQueries = platformQueries;
    }

    public void setTumorQueries(final TumorQueries tumorQueries) {
        this.tumorQueries = tumorQueries;
    }

    public void setDataTypeQueries(final DataTypeQueries dataTypeQueries) {
        this.dataTypeQueries = dataTypeQueries;
    }
}
