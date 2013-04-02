/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ArchiveType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.web.ArchiveQueryRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.object.MappingSqlQuery;

import javax.sql.DataSource;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.IN_CLAUSE_SIZE;

/**
 * @author Robert S. Sfeir
 * @author David Kane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveQueriesJDBCImpl extends BaseQueriesProcessor implements ArchiveQueries {

    private static final String ALL_AVAILABLE_PROTECTED_BIO_ARCHIVES_QUERY_FROM = "from archive_info a, disease d where a.deploy_location like '%tcga4yeo%.bio.%' and a.deploy_status='Available' and a.disease_id=d.disease_id";
    private static final String ALL_AVAILABLE_PROTECTED_BIO_ARCHIVES_QUERY = "select a.archive_id, a.deploy_location, d.disease_abbreviation " + ALL_AVAILABLE_PROTECTED_BIO_ARCHIVES_QUERY_FROM;

    public static final String DATE_FORMAT_US_STRING = "mm/dd/yy";

    public static final String RETRIEVE_MAGE_TAB_LOCATION =
            "select deploy_location " +
                    "from archive_info a, archive_type t, center c, platform p, disease d " +
                    "where a.is_latest = 1 " +
                    "and a.center_id = c.center_id and " +
                    "c.domain_name = ? " +
                    "and a.platform_id = p.platform_id " +
                    "and p.platform_name = ? " +
                    "and a.archive_type_id = t.archive_type_id " +
                    "and t.archive_type = 'mage-tab' " +
                    "and a.disease_id = d.disease_id and d.disease_abbreviation = ?";

    public static final String GET_FILE_FOR_ARCHIVE = "select f.file_name,f.file_id from file_info f, file_to_archive fa where fa.archive_id = ? and fa.file_id=f.file_id";

    public static final String GET_CENTER_QUERY = "select * from center c, platform p where c.domain_name = ? and p.platform_name = ? and c.center_type_code = 'CGCC'";

    private static final String MAX_REVISION_QUERY = "select max(revision), count(*) " +
            "from archive_info, center, disease, platform, archive_type " +
            "where archive_info.center_id=center.center_id and " +
            "archive_info.disease_id=disease.disease_id and " +
            "archive_info.platform_id=platform.platform_id and " +
            "archive_info.archive_type_id=archive_type.archive_type_id and " +
            "center.domain_name=? and " +
            "disease.disease_abbreviation=? and " +
            "platform.platform_name=? and " +
            "archive_type.archive_type=? and " +
            "archive_info.serial_index=?";


    private static final String GET_LATEST_ARCHIVE_ID = "select archive_id " +
            " from archive_info " +
            " where archive_name like ? and " +
            " is_latest = 1 and deploy_status= 'Available'";

    private static final String GET_ALL_ARCHIVE_TYPES_QUERY = "SELECT archive_type_id, archive_type, data_level " +
            "FROM archive_type ORDER BY archive_type";

    private static final String GET_MAGE_TAB_ARCHIVES = "select archive_name,deploy_location,disease_abbreviation " +
                    "from archive_info a, archive_type t, disease d " +
                    "where a.deploy_status = 'Available'" +
                    "and a.archive_type_id = t.archive_type_id " +
                    "and t.archive_type = 'mage-tab' " +
                    "and a.disease_id = d.disease_id ";

    private static final String GET_PROTECTED_MAF_ARCHIVES = "select distinct a.archive_name , a.deploy_location, a.archive_id,  d.disease_abbreviation" +
            " from archive_info a, file_to_archive fa, file_info f, disease d " +
            " where f.file_name like '%.maf'" +
            " and f.file_id=fa.file_id" +
            " and fa.archive_id = a.archive_id" +
            " and a.deploy_status = 'Available' " +
            " and a.deploy_location like '%tcga4yeo%' " +
            " and a.disease_id = d.disease_id " +
            " order by archive_name";

    private static final String ARCHIVE_IDS_PLACEHOLDER = "ARCHIVE_IDS_PLACEHOLDER";
    private static final String UPDATE_ARCHIVES_LOCATION_TO_PUBLIC = "update archive_info set deploy_location = replace(deploy_location,'tcga4yeo','anonymous') where archive_id in (" + ARCHIVE_IDS_PLACEHOLDER + ")";


    public ArchiveQueriesJDBCImpl() {
    }

    public List<Archive> getMatchingArchives(ArchiveQueryRequest queryParams) {
        List<Archive> archiveEntries = new ArrayList<Archive>();
        if (queryParams.getRowSort() == null || queryParams.getRowSort().trim().length() == 0) {
            queryParams.setRowSort("archive_info.date_added DESC, center.display_name ASC");
        }
        ArchiveQueryByParameter archiveByParameterQuery = new ArchiveQueryByParameter(getDataSource(), queryParams);
        archiveEntries.addAll(archiveByParameterQuery.execute());
        return archiveEntries;
    }

    public Archive getArchive(long archiveId) {
        ArchiveQueryById archiveByParameterQuery = new ArchiveQueryById(getDataSource(), archiveId);
        List archives = archiveByParameterQuery.execute();
        if (archives.size() > 0) {
            return (Archive) archives.get(0);
        } else {
            return null;
        }
    }

    public Archive getArchive(final String archiveName) {

        ArchiveQuery archiveByNameQuery = new ArchiveQuery(getDataSource(), getArchiveByNameQuery(archiveName));
        List archives = archiveByNameQuery.execute();
        if (archives.size() > 0) {
            final Archive archive = (Archive) archives.get(0);
            archive.setArchiveFile(new File(archive.getDeployLocation()));
            return archive;
        } else {
            return null;
        }
    }

    private String getArchiveByNameQuery(final String archiveName) {
        StringBuilder archiveQuery = new StringBuilder("SELECT ");
        archiveQuery.append(ARCHIVE_SELECT_FIELDS)
                .append(" from ").append(ARCHIVE_FROM_TABLES)
                .append(" where archive_info.archive_name = '")
                .append(archiveName)
                .append("' and ")
                .append(ARCHIVE_JOINS);
        return archiveQuery.toString();
    }

    public Archive getLatestVersionArchive(final Archive archive) {
        LatestArchiveQuery latestArchiveQuery = new LatestArchiveQuery(getDataSource(), archive);
        List archives = latestArchiveQuery.execute();
        if (archives.size() == 0) {
            return null;
        } else {
            return (Archive) archives.get(0);
        }
    }

    /**
     * Adds an archive to database
     *
     * @param theArchive archive object
     * @return archive Id
     */
    public Long addArchive(Archive theArchive) {
        return addArchive(theArchive, false);
    }

    /**
     * Adds an archive to database
     *
     * @param theArchive      the archive object
     * @param useIdFromCommon Set to true if the archive Id  set in the archive should be used while adding the archive
     *                        to database, false otherwise
     * @return archive Id
     */
    public Long addArchive(Archive theArchive, boolean useIdFromCommon) {
        Long theId;
        if (useIdFromCommon) {
            theId = theArchive.getId();
        } else {
            theId = getNextSequenceNumber("archive_seq");
        }

        String insert = "insert into archive_info(archive_id,archive_name,center_id,disease_id,platform_id,serial_index," +
                "revision,series,date_added,deploy_status,deploy_location,secondary_deploy_location,is_latest,archive_type_id) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        // only set is_latest to 1 if the archive is Available... otherwise don't!
        boolean isLatest = theArchive.getDeployStatus().equals(Archive.STATUS_AVAILABLE);
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());

        if (theId == -1) {
            return theId;
        }
        java.sql.Timestamp dateAdded = new java.sql.Timestamp(System.currentTimeMillis());
        sjdbc.update(insert, theId, theArchive.getArchiveName(), theArchive.getTheCenter().getCenterId(),
                theArchive.getTheTumor().getTumorId(), theArchive.getThePlatform().getPlatformId(),
                Integer.valueOf(theArchive.getSerialIndex()),
                Integer.valueOf(theArchive.getRevision()),
                Integer.valueOf(theArchive.getSeries()),
                dateAdded, theArchive.getDeployStatus(),
                theArchive.getDeployLocation(), theArchive.getSecondaryDeployLocation(),
                (isLatest ? 1 : 0), theArchive.getArchiveTypeId());
        return theId;
    }


    public void addLogToArchiveEntry(Long archiveId, Integer logId) {
        String insert = "insert into log_to_archives(log_id,archive_id) values (?,?)";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        sjdbc.update(insert, logId, archiveId);
    }

    public Long getArchiveIdByName(String archiveName) {
        return getObjectIdByName(archiveName, "archive_info", "archive_name", "archive_id");
    }


    public void updateDeployLocation(final Archive archive) {
        String update = "update archive_info set deploy_location = ? where archive_id=" + archive.getId();
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        sjdbc.update(update, archive.getDeployLocation());
    }

    /**
     * Updates the archive's secondary deploy location to whatever value is set in the archive bean
     *
     * @param archive the archive to update
     */
    public void updateSecondaryDeployLocation(final Archive archive) {
        getJdbcTemplate().update(
                "update archive_info set secondary_deploy_location=? where archive_id=?",
                new Object[]{archive.getSecondaryDeployLocation(), archive.getId()});
    }

    public Long exists(final String archiveName) {
        Long id;
        try {
            id = getObjectIdByName(archiveName, "archive_info", "archive_name", "archive_id");
        } catch (DataAccessException e) {
            //we didn't find anything leave it alone.  DAE is thrown when there is no data returned.
            id = -1L;
        }
        return id;
    }

    public void updateArchiveStatus(final Archive archive) {
        StringBuilder update = new StringBuilder("update archive_info set deploy_status = ?");
        update.append(" where archive_id = ?");
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        sjdbc.update(update.toString(), archive.getDeployStatus(), archive.getId());
    }

    public void updateArchiveInfo(final Long archiveInfoId) {
        Object[] params = {archiveInfoId};
        getJdbcTemplate().update("update archive_info set DATA_LOADED_DATE=SYSDATE where archive_id=? ", params);
    }

    public void setToLatest(Archive archive) {
        updateLatest(archive, false);
    }

    // 2nd param specifies if updating is_latest or is_latest_loaded field

    private void updateLatest(final Archive archive, boolean latestLoaded) {
        // set this archive to be the latest one
        String updateArchive = new StringBuilder().append("update archive_info set ").
                append(latestLoaded ? "is_latest_loaded" : "is_latest").
                append("=1 where archive_id=?").toString();
        // set is_latest/is_latest_loaded to 0 for all archives for same center/disease/platform/archiveType/serial index that is not this one
        String updateOthers = "update archive_info set " + (latestLoaded ? "is_latest_loaded" : "is_latest") + "=0 " +
                "where center_id=? and disease_id=? and " +
                "platform_id=? and archive_type_id=? and serial_index=? and archive_id!=?";
        getJdbcTemplate().update(updateArchive, new Object[]{archive.getId()});
        getJdbcTemplate().update(updateOthers, new Object[]{archive.getTheCenter().getCenterId(),
                archive.getTheTumor().getTumorId(),
                archive.getThePlatform().getPlatformId(),
                archive.getArchiveTypeId(), archive.getSerialIndex(), archive.getId()});
    }

    public void setToLatestLoaded(final Archive archive) {
        updateLatest(archive, true);
    }

    public void updateAddedDate(final Long archiveId, final Date date) {
        final String update = "update archive_info set date_added = ? where archive_id = ?";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        sjdbc.update(update, date, archiveId);
    }

    public void setArchiveInitialSize(final Long archiveId, final long sizeInKB) {
        final String updateQuery = "update archive_info set initial_size_kb=? where archive_id=?";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        sjdbc.update(updateQuery, sizeInKB, archiveId);
    }

    public void setArchiveFinalSize(final Long archiveId, final long sizeInKB) {
        final String updateQuery = "update archive_info set final_size_kb=? where archive_id=?";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        sjdbc.update(updateQuery, sizeInKB, archiveId);
    }

    public long getArchiveSize(final Long archiveId) {
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        long size = sjdbc.queryForLong("select final_size_kb from archive_info where archive_id=?", archiveId);
        if (size == 0 || size == -1) {
            size = sjdbc.queryForLong("select initial_size_kb from archive_info where archive_id=?", archiveId);
        }
        return size;
    }

    /**
     * Gets the max revision for the given archive (meaning the max revision for any archive with the same center,
     * disease, platform, archive type, and serial index as the given archive). If availableOnly is true, will only
     * consider archives with status Available, otherwise will consider all archives.
     *
     * @param archive       the archive to use for looking for revisions
     * @param availableOnly whether to consider only Available archives or not
     * @return the max revision number for archives of that experiment, type, and serial index, or -1 if none found
     */
    public Long getMaxRevisionForArchive(final Archive archive, final boolean availableOnly) {
        final long[] maxAndCount = new long[2];
        getJdbcTemplate().query(
                availableOnly ? MAX_REVISION_QUERY + " and archive_info.deploy_status='Available'" : MAX_REVISION_QUERY,
                new Object[]{
                        archive.getDomainName(),
                        archive.getTumorType(),
                        archive.getPlatform(),
                        archive.getArchiveType(),
                        archive.getSerialIndex()
                },
                new RowCallbackHandler() {
                    public void processRow(final ResultSet resultSet) throws SQLException {
                        maxAndCount[0] = resultSet.getLong(1);
                        maxAndCount[1] = resultSet.getLong(2);
                    }
                }
        );
        /*
        If there are no revisions, the max(revision) will return 0, so we need to get the row count also, and if it is
        also 0 then return -1 to indicate no revisions were found
         */
        if (maxAndCount[1] > 0) {
            return maxAndCount[0];
        } else {
            return -1L;
        }
    }


    public Long getLatestArchiveId(String archiveNameFilter) {
        Long archiveId = null;

        if (!archiveNameFilter.endsWith("%")) {
            archiveNameFilter = archiveNameFilter + "%";
        }

        try {
            archiveId = (Long) getJdbcTemplate().queryForObject(GET_LATEST_ARCHIVE_ID,
                    new Object[]{archiveNameFilter},
                    Long.class);

        } catch (DataAccessException da) {
            // archive doesn't exist
            // return null;
        }
        return archiveId;
    }

    private static final String ARCHIVE_SELECT_FIELDS = "archive_info.archive_id as id, archive_info.archive_name, " +
            "archive_info.deploy_status,archive_info.deploy_location,archive_info.secondary_deploy_location, " +
            "archive_info.archive_type_id, archive_type.archive_type, archive_type.data_level, " +
            "archive_info.date_added, archive_info.serial_index, archive_info.revision, archive_info.series, " +
            "center.center_id, center.domain_name, center.display_name as center_display_name, center.center_type_code, " +
            "platform.platform_id, platform.platform_display_name as platform_description, " +
            "platform.platform_name, platform.center_type_code as platform_center_type, " +
            "disease.disease_id, disease.disease_name, disease.disease_abbreviation, " +
            "data_type.name as data_type_name, data_type.data_type_id,data_type.require_compression  ";

    private static final String ARCHIVE_FROM_TABLES = "archive_type, archive_info, center, platform,disease, " +
            "data_type ";

    private static final String ARCHIVE_JOINS = "archive_info.center_id=center.center_id and " +
            "archive_info.disease_id=disease.disease_id and " +
            "archive_info.platform_id=platform.platform_id and " +
            "archive_info.archive_type_id=archive_type.archive_type_id and " +
            "data_type.data_type_id=platform.base_data_type_id ";


    private static String buildQueryString(ArchiveQueryRequest queryParameter) {
        StringBuffer query = new StringBuffer("SELECT distinct " + ARCHIVE_SELECT_FIELDS + "from file_to_archive, file_info, " + ARCHIVE_FROM_TABLES +
                "where " + ARCHIVE_JOINS);
        query.append(" and ");

        if (queryParameter.hasAtLeastOneParameter()) {

            //Check to see if we have a center set bool to true so we know to add the AND to the search
            boolean gotCenter = false;
            if (queryParameter.getCenter() != null && !queryParameter.getCenter().contains("-1")) {
                gotCenter = checkParameterAndAddConstraintIfNeeded(query, "center.center_id", queryParameter.getCenter());
            }
            //If a center search exists and the disease search is valid, add the AND and the disease info
            if (gotCenter && (queryParameter.getProject() != null && !queryParameter.getProject().contains("-1"))) {
                query.append(" and ");
            }
            boolean gotProject = false;
            if (queryParameter.getProject() != null && !queryParameter.getProject().contains("-1")) {
                gotProject = checkParameterAndAddConstraintIfNeeded(query, "disease.disease_id", queryParameter.getProject());
            }
            //If we have a center or disease search, add the and, then add the platform search if it's valid.
            if ((gotCenter || gotProject) && (queryParameter.getPlatform() != null && !queryParameter.getPlatform().contains("-1"))) {
                query.append(" and ");
            }
            boolean gotPlatform = false;
            if (queryParameter.getPlatform() != null && !queryParameter.getPlatform().contains("-1")) {
                gotPlatform = checkParameterAndAddConstraintIfNeeded(query, "platform.platform_id", queryParameter.getPlatform());
            }
            //If we have a ceenter, or disease, or platform and the data type search is valid add the and, and do it.
            if ((gotCenter || gotProject || gotPlatform) && (queryParameter.getDataType() != null && !queryParameter.getDataType().contains("-1"))) {
                query.append(" and ");
            }
            boolean gotDataType = false;
            if (queryParameter.getDataType() != null && !queryParameter.getDataType().contains("-1")) {
                gotDataType = checkParameterAndAddConstraintIfNeeded(query, "data_type.data_type_id", queryParameter.getDataType());
            }
            if ((gotCenter || gotProject || gotPlatform || gotDataType) && (queryParameter.getArchiveType() != null && ! queryParameter.getArchiveType().contains("-1"))) {
                query.append(" and ");
            }
            boolean gotArchiveType = false;
            if (queryParameter.getArchiveType() != null && !queryParameter.getArchiveType().equals("-1")) {
                gotArchiveType = checkParameterAndAddConstraintIfNeeded(query, "archive_type.archive_type_id", queryParameter.getArchiveType());
            }

            //We deal with the date slightly differently than other fields as we need ot check for start and end on the same field
            boolean gotDate = false;
            if ((queryParameter.getDateStart() != null && queryParameter.getDateStart().length() > 0)
                    || (queryParameter.getDateEnd() != null && queryParameter.getDateEnd().length() > 0)) {
                gotDate = true;
                if ((gotCenter || gotProject || gotPlatform || gotDataType || gotArchiveType)) {
                    query.append(" and ");
                }
                if (queryParameter.getDateStart() != null && queryParameter.getDateStart().length() > 0) {
                    query.append(" archive_info.date_added >= to_date('").append(queryParameter.getDateStart()).append("', '" + DATE_FORMAT_US_STRING + "')");
                }
                if (queryParameter.getDateEnd() != null && queryParameter.getDateStart() != null && queryParameter.getDateEnd().length() > 0 && queryParameter.getDateStart().length() > 0) {
                    query.append(" and ");
                }
                if (queryParameter.getDateEnd() != null && queryParameter.getDateEnd().length() > 0) {
                    query.append(" archive_info.date_added <= to_date('").append(queryParameter.getDateEnd()).append("', '" + DATE_FORMAT_US_STRING + "')");
                }
            }
            boolean gotFileName = false;
            if (queryParameter.getFileName() != null && queryParameter.getFileName().trim().length() > 0) {
                gotFileName = true;
                if ((gotCenter || gotProject || gotPlatform || gotDataType || gotArchiveType || gotDate)) {
                    query.append(" and ");
                }
                checkParameterAndAddConstraintIfNeeded(query, "file_info.file_name", queryParameter.getFileName());
            }
            boolean getDeployStatus = false;
            if (queryParameter.getDeployStatus() != null && queryParameter.getDeployStatus().length() != 0) {
                if ((gotCenter || gotProject || gotPlatform || gotDataType || gotArchiveType || gotDate)) {
                    query.append(" and ");
                }
                getDeployStatus = checkParameterAndAddConstraintIfNeeded(query, "archive_info.deploy_status", queryParameter.getDeployStatus());
            }
            if (gotCenter || gotProject || gotPlatform || gotDataType || gotArchiveType || gotDate || gotFileName || getDeployStatus) {
                query.append(" and ");
            }
        }
        query.append(" archive_info.is_latest = 1 and "); // always only show query for latest archives not hidden archives.
        query.append(" archive_info.archive_id = file_to_archive.archive_id and ");
        query.append(" file_to_archive.file_id = file_info.file_id");
        if (queryParameter.getRowSort() != null) {
            query.append(" order by ").append(queryParameter.getRowSort());
        }
        return query.toString();
    }

    private static String buildQueryString(final long id) {
        StringBuilder query = new StringBuilder("SELECT ").append(ARCHIVE_SELECT_FIELDS).append(" from ").append(ARCHIVE_FROM_TABLES).
                append(" where archive_info.archive_id = ").append(id).append(" and ").append(ARCHIVE_JOINS);
        return query.toString();
    }

    private static String buildLatestArchiveQueryString(Archive archive) {
        //TODO Switch to PreapredStatement in order to address SQL Injection security issues
        StringBuilder query = new StringBuilder();
        query.append("SELECT ").append(ARCHIVE_SELECT_FIELDS).append(" from ").append(ARCHIVE_FROM_TABLES).append("where ");
        query.append("archive_info.center_id=").append(archive.getTheCenter().getCenterId());
        query.append(" and archive_info.disease_id=").append(archive.getTheTumor().getTumorId());
        query.append(" and archive_info.platform_id=").append(archive.getThePlatform().getPlatformId());
        query.append(" and archive_info.serial_index=").append(archive.getSerialIndex());
        query.append(" and archive_info.archive_type_id=").append(archive.getArchiveTypeId());
        query.append(" and archive_info.is_latest=1 and archive_info.deploy_status='").append(Archive.STATUS_AVAILABLE).append("' and ");
        query.append(ARCHIVE_JOINS);

        return query.toString();
    }

    private static boolean checkParameterAndAddConstraintIfNeeded(
            StringBuffer query, String columnName,
            String parameter) {
        boolean processed = false;
        if (parameter != null && parameter.trim().length() > 0 && columnName.equals("archive_info.deploy_status")) {
            query.append(' ').append(columnName).append(" like '%").append(parameter).append("%' ");
            processed = true;
        } else if (parameter != null && parameter.trim().length() > 0 && columnName.equals("file_info.file_name")) {
            query.append(' ').append(columnName).append(" like '%").append(parameter).append("%' ");
            processed = true;
        } else if (parameter != null && !parameter.equals("0") && !columnName.equals("archive_info.date_added")) {
            query.append(' ').append(columnName).append(" in (").append(parameter).append(')');
            processed = true;
        } else if (parameter != null && parameter.length() > 0 && columnName.equals("archive_info.date_added")) {
            final java.sql.Date date = new java.sql.Date(new Date(parameter).getTime());
            query.append(' ').append(columnName).append(" >= '").append(date.toString()).append('\'');
            processed = true;
        }
        return processed;
    }

    static class ArchiveQuery extends MappingSqlQuery {

        ArchiveQuery(DataSource ds, String sql) {
            super(ds, sql);
        }

        protected Archive mapRow(ResultSet rs, int rownum) throws SQLException {
            Archive archive = new Archive("");
            archive.setRealName(rs.getString("archive_name"));
            archive.setDomainName(rs.getString("domain_name"));
            archive.setId(rs.getLong("id"));
            archive.setPlatform(rs.getString("platform_name"));
            archive.setRevision(rs.getString("revision"));
            archive.setSerialIndex(rs.getString("serial_index"));
            archive.setSeries(rs.getString("series"));
            archive.setTumorType(rs.getString("disease_abbreviation"));
            archive.setDataType(rs.getString("data_type_name"));
            archive.setDataTypeId(rs.getInt("data_type_id"));
            archive.setDataTypeCompressed((rs.getInt("require_compression") == 1)?true:false);
            archive.setDateAdded(rs.getTimestamp("date_added"));
            archive.setDeployLocation(rs.getString("deploy_location"));
            archive.setSecondaryDeployLocation(rs.getString("secondary_deploy_location"));
            archive.setDeployStatus(rs.getString("deploy_status"));
            archive.setArchiveType(rs.getString("archive_type"));
            archive.setDataLevel(rs.getInt("data_level"));
            archive.setArchiveTypeId(rs.getInt("archive_type_id"));
            archive.setExperimentType(rs.getString("center_type_code"));

            Platform archivePlatform = new Platform();
            archivePlatform.setPlatformId(rs.getInt("platform_id"));
            archivePlatform.setPlatformName(rs.getString("platform_name"));
            archivePlatform.setPlatformDisplayName(rs.getString("platform_description"));
            archivePlatform.setCenterType(rs.getString("platform_center_type"));
            archive.setThePlatform(archivePlatform);

            Tumor archiveTumor = new Tumor();
            archiveTumor.setTumorId(rs.getInt("disease_id"));
            archiveTumor.setTumorName(rs.getString("disease_abbreviation"));
            archiveTumor.setTumorDescription(rs.getString("disease_name"));
            archive.setTheTumor(archiveTumor);

            Center archiveCenter = new Center();
            archiveCenter.setCenterId(rs.getInt("center_id"));
            archiveCenter.setCenterName(rs.getString("domain_name"));
            archiveCenter.setCenterDisplayName(rs.getString("center_display_name"));
            archiveCenter.setCenterType(rs.getString("center_type_code"));
            archive.setTheCenter(archiveCenter);

            //We need this for display purposes in order to allow for a display version to be sorted properly.
            archive.setDisplayVersion(new StringBuilder().append(rs.getString("serial_index")).append(".").append(rs.getString("revision")).append(".").append(rs.getString("series")).toString());
            return archive;
        }
    }

    static class ArchiveQueryByParameter extends ArchiveQuery {
        ArchiveQueryByParameter(final DataSource ds, final ArchiveQueryRequest queryParameter) {
            super(ds, buildQueryString(queryParameter));
        }
    }


    static class ArchiveQueryById extends ArchiveQuery {
        ArchiveQueryById(final DataSource ds, final long id) {
            super(ds, buildQueryString(id));
        }
    }

    static class LatestArchiveQuery extends ArchiveQuery {
        LatestArchiveQuery(final DataSource ds, final Archive archive) {
            super(ds, buildLatestArchiveQueryString(archive));
        }
    }

    static class ArchiveSerialIndexQuery extends MappingSqlQuery {

        ArchiveSerialIndexQuery(DataSource ds, String selectStmt) {
            super(ds, selectStmt);
            super.declareParameter(new SqlParameter("platform.center_type_code", Types.CHAR));
            super.declareParameter(new SqlParameter("archive_info.serial_index", Types.INTEGER));
            super.declareParameter(new SqlParameter("disease.disease_abbreviation", Types.CHAR));
            super.declareParameter(new SqlParameter("center.domain_name", Types.CHAR));
            compile();

        }

        protected Archive mapRow(final ResultSet rs, final int rownum) throws SQLException {

            final Archive archive = new Archive();
            archive.setSerialIndex(rs.getString("serial_index"));
            archive.setDomainName(rs.getString("domain_name"));
            archive.setTumorType(rs.getString("disease_abbreviation"));
            return archive;
        }
    }

    @Override
    public String getUUIDforBarcode(final String barcode) {
        final String uuidQuery = "select uuid from barcode_history where barcode=?";
        Object[] barcodeArray = {barcode};
        List<String> uuidList = getJdbcTemplate().query(uuidQuery, barcodeArray, new ParameterizedRowMapper<String>() {
            public String mapRow(final ResultSet resultSet, final int i) throws SQLException {
                return resultSet.getString("UUID");
            }
        }
        );

        if (uuidList != null && uuidList.size() > 0) {
            return uuidList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public String getSdrfDeployLocation(final String center, final String platform, final String disease) {
        String[] center_platform_disease = {center, platform, disease};
        return (String) getJdbcTemplate().queryForObject(RETRIEVE_MAGE_TAB_LOCATION, center_platform_disease, String.class);
    }

    @Override
    public Center getCenterByDomainNameAndPlatformName(final String domainName, final String platformName) {
        String[] domain_platform = {domainName, platformName};
        return (Center) getJdbcTemplate().queryForObject(GET_CENTER_QUERY, domain_platform, CenterQueriesJDBCImpl.getCenterRowMapper());
    }

    @Override
    public List<FileInfo> getFilesForArchive(Long archiveId) {
        Object[] getfilesParams = {archiveId};
        return getJdbcTemplate().query(GET_FILE_FOR_ARCHIVE, getfilesParams,
                new ParameterizedRowMapper<FileInfo>() {
                    public FileInfo mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        FileInfo files = new FileInfo();
                        files.setId(resultSet.getLong("file_id"));
                        files.setFileName(resultSet.getString("file_name"));
                        return files;
                    }
                }
        );
    }

    /**
     * Get list of all archive types
     *
     * @return list of all archive types
     */
    @Override
    public List<ArchiveType> getAllArchiveTypes() {
        return getJdbcTemplate().query(GET_ALL_ARCHIVE_TYPES_QUERY,
                new ParameterizedRowMapper<ArchiveType>() {
                    public ArchiveType mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        final ArchiveType archiveType = new ArchiveType();
                        archiveType.setArchiveType(resultSet.getString("archive_type"));
                        archiveType.setArchiveTypeId(resultSet.getInt("archive_type_id"));
                        archiveType.setDataLevel(resultSet.getInt("data_level"));
                        if (resultSet.wasNull()) {
                            archiveType.setDataLevel(null);
                        }
                        return archiveType;
                    }
                }
        );
    }

    @Override
    public List<Archive> getMagetabArchives(){
        return getJdbcTemplate().query(GET_MAGE_TAB_ARCHIVES,
                new ParameterizedRowMapper<Archive>() {
                    @Override
                    public Archive mapRow(ResultSet resultSet, int i) throws SQLException {
                        final Archive archive = new Archive(resultSet.getString("deploy_location"));
                        archive.setRealName(resultSet.getString("archive_name"));
                        archive.setDeployLocation(resultSet.getString("deploy_location"));
                        archive.setTheTumor(new Tumor());
                        archive.getTheTumor().setTumorName(resultSet.getString("disease_abbreviation"));
                        return archive;
                    }
                });
    }

    @Override
    public List<Archive> getAllAvailableProtectedBioArchives() {

        return getJdbcTemplate().query(ALL_AVAILABLE_PROTECTED_BIO_ARCHIVES_QUERY,
                new ParameterizedRowMapper<Archive>() {

                    public Archive mapRow(final ResultSet resultSet, final int i) throws SQLException {

                        final String deployLocation = resultSet.getString("deploy_location");

                        final Archive archive = new Archive(deployLocation);
                        archive.setId(resultSet.getLong("archive_id"));
                        archive.setDeployLocation(deployLocation);
                        archive.setTumorType(resultSet.getString("disease_abbreviation"));
                        return archive;
                    }
                });
    }
    
    @Override
    public List<Archive> getProtectedMafArchives(){
        return getJdbcTemplate().query(GET_PROTECTED_MAF_ARCHIVES,
            new ParameterizedRowMapper<Archive>() {
                @Override
                public Archive mapRow(ResultSet resultSet, int i) throws SQLException {
                    final Archive archive = new Archive(resultSet.getString("deploy_location"));
                    archive.setDeployLocation(resultSet.getString("deploy_location"));
                    archive.setRealName(resultSet.getString("archive_name"));
                    archive.setId(resultSet.getLong("archive_id"));
                    archive.setTumorType(resultSet.getString("disease_abbreviation"));
                    return archive;
                }
        });
    }

    @Override
    public void updateArchivesLocationToPublic(final Set<Long> archiveIds) {

        final List<Long> archiveIdsList = new ArrayList<Long>();
        archiveIdsList.addAll(archiveIds);

        int startIndex;
        int endIndex = 0;

        while (endIndex < archiveIds.size()) {

            startIndex = endIndex;
            endIndex = (archiveIds.size() - endIndex) > IN_CLAUSE_SIZE ? endIndex + IN_CLAUSE_SIZE : archiveIds.size();

            final List<Long> archiveIdsSubList = archiveIdsList.subList(startIndex, endIndex);
            final String archiveIdsPlaceHolder = StringUtil.createPlaceHolderString(archiveIdsSubList.size());

            getJdbcTemplate().update(UPDATE_ARCHIVES_LOCATION_TO_PUBLIC.replace(ARCHIVE_IDS_PLACEHOLDER, archiveIdsPlaceHolder), archiveIdsSubList.toArray());
        }
    }
}