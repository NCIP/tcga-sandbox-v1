/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.web.FileInfoQueryRequest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.IN_CLAUSE_SIZE;

/**
 * @author Robert S. Sfeir
 * @author David Kane
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FileInfoQueriesJDBCImpl extends BaseQueriesProcessor implements FileInfoQueries {
    private static final String IN_CLAUSE_PLACEHOLDER = "IN_CLAUSE";
    private static final String FILE_INFO_INSERT_QUERY = " INSERT INTO file_info (file_id, file_name, file_size, level_number, data_type_id, md5) " +
            "VALUES (?,?,?,?,?,?)";

    private static final String DELETE_ARCHIVE_FILES_QUERY = "delete from file_info fi" +
            " where fi.file_id in " +
            " (select fi.file_id from" +
            " file_info fi," +
            " file_to_archive fta," +
            " archive_info a where" +
            " fi.file_id = fta.file_id and" +
            " fta.archive_id = a.archive_id and" +
            " a.archive_name like ?)";

    private static final String FILE_INFO_DELETE_FILES_QUERY = "DELETE FROM file_info where file_id IN(" +
            IN_CLAUSE_PLACEHOLDER +
            ")";
    private ArchiveQueries archiveQueries;

    private static final String UPDATE_FILE_DATA_TYPE = "update file_info fi set fi.data_type_id = ? " +
            " where fi.file_id = ( " +
            " select fi.file_id " +
            " from file_info fi, file_to_archive fta, archive_info a " +
            " where " +
            " fi.file_name = ? and " +
            " fi.file_id = fta.file_id and " +
            " fta.archive_id = a.archive_id and" +
            " a.archive_name = ?)";
    private static final String REPLACE = "REPLACE";
    private static final String GET_BCRXML_FILES_LOCATION= "select file_location_url from " +
            " uuid u," +
            " barcode_history bh," +
            " participant_uuid_file pf," +
            " file_to_archive fa," +
            " archive_info a" +
            " where u.uuid = pf.uuid" +
            " and   u.latest_barcode_id = bh.barcode_id" +
            " and   pf.file_id = fa.file_id " +
            " and fa.archive_id = a.archive_id " +
            " and a.is_latest = 1" +
            " and a.deploy_status = 'Available'" +
            " and   bh.barcode in ("+REPLACE+")";

    private static final String UPDATE_MAF_FILES_LOCATION_TO_PUBLIC =
                    "update file_to_Archive set file_location_url = replace(file_location_url,'tcga4yeo','anonymous') where file_id in" +
                    " (select f.file_id " +
                    " from archive_info a, file_to_archive fa, file_info f" +
                    " where f.file_name like '%.maf'" +
                    " and f.file_id=fa.file_id" +
                    " and fa.archive_id = a.archive_id" +
                    " and a.deploy_status = 'Available'  " +
                    " and a.deploy_location like '%tcga4yeo%')";

    private static final String ARCHIVE_IDS_PLACEHOLDER = "ARCHIVE_IDS_PLACEHOLDER";
    private static final String UPDATE_FILES_LOCATION_TO_PUBLIC = "update file_to_archive set file_location_url = replace(file_location_url,'tcga4yeo','anonymous') where archive_id in (" + ARCHIVE_IDS_PLACEHOLDER + ")";


    public FileInfoQueriesJDBCImpl() {
    }

    @Override
    public List<FileInfo> getFilesForArchive(final FileInfoQueryRequest fileInfoQueryRequest) {
        String query = "Select fi.file_id, fi.file_name, fi.file_size, fi.level_number, fi.data_type_id, fi.md5, fi.revision_of_file_id ,fa.file_location_url " +
                "from file_info fi, file_to_archive fa " +
                "where fi.file_id = fa.file_id " +
                "and fa.archive_id= ?";

        List<FileInfo> fileInfoList = getJdbcTemplate().query(query, new Object[]{fileInfoQueryRequest.getArchiveId()}, getFileInfoRowMapperWithFileLocationUrl());
        return fileInfoList;
    }



    /**
     * Adds the file information to the database
     *
     * @param fileInfo fileInfo object
     * @return fileId
     */
    @Override
    public Long addFile(final FileInfo fileInfo) {
        return addFile(fileInfo, false);
    }

    /**
     * Adds the file information to the database
     *
     * @param fileInfo        fileInfo object
     * @param useIdFromCommon Set to true if the fileId set in the fileInfo should be used while adding the fileInfo to
     *                        the database, false otherwise
     * @return fileId
     */
    @Override
    public Long addFile(final FileInfo fileInfo, final Boolean useIdFromCommon) {
        Long fileId;
        if (useIdFromCommon) {
            fileId = fileInfo.getId();
        } else {
            fileId = getNextSequenceNumber("file_seq");
        }
        final String insert = "insert into file_info(file_id, file_name, file_size, level_number, data_type_id, md5, revision_of_file_id) " +
                "values (?,?,?,?,?,?,?)";
        getJdbcTemplate().update(insert, new Object[]{fileId, fileInfo.getFileName(), fileInfo.getFileSize(),
                fileInfo.getDataLevel(), fileInfo.getDataTypeId(), fileInfo.getFileMD5(), fileInfo.getRevision()});
        fileInfo.setId(fileId);
        return fileId;
    }

    @Override
    public void addFiles(final List<FileInfo> fileInfoList) {
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getJdbcTemplate().getDataSource());
        final List<Object[]> valueList = new ArrayList<Object[]>();
        for (final FileInfo fileInfo : fileInfoList) {
            Object[] data = new Object[6];

            if (fileInfo.getId() == null ||
                    fileInfo.getId() == ConstantValues.NOT_ASSIGNED) {
                fileInfo.setId(getNextSequenceNumber("file_seq"));
            }
            int index = 0;
            data[index++] = fileInfo.getId();
            data[index++] = fileInfo.getFileName();
            data[index++] = fileInfo.getFileSize();
            data[index++] = fileInfo.getDataLevel();
            data[index++] = fileInfo.getDataTypeId();
            data[index++] = fileInfo.getFileMD5();
            valueList.add(data);
        }
        sjdbc.batchUpdate(FILE_INFO_INSERT_QUERY, valueList);
        valueList.clear();
    }

    @Override
    public void deleteFiles(final List<Long> fileIds) {
        String replaceData = StringUtil.createPlaceHolderString(fileIds.size());
        String query = FILE_INFO_DELETE_FILES_QUERY.replace(IN_CLAUSE_PLACEHOLDER, replaceData);
        getJdbcTemplate().update(query, fileIds.toArray(new Long[1]));

    }

    @Override
    public void updateFileDataType(Long fileId, Integer dataTypeId) {
        String updateQuery = "update file_info set data_type_id = ? where file_id = ?";
        getJdbcTemplate().update(updateQuery, new Object[]{dataTypeId, fileId});
    }


    @Override
    public void updateFile(final FileInfo fileInfo) {
        final String updateSQL = "update file_info set file_name = ?, file_size=?, level_number=?, data_type_id=?, md5=?, revision_of_file_id=? " +
                "where file_id = ?";
        getJdbcTemplate().update(updateSQL, new Object[]{fileInfo.getFileName(), fileInfo.getFileSize(), fileInfo.getDataLevel(),
                fileInfo.getDataTypeId(), fileInfo.getFileMD5(), fileInfo.getRevision(), fileInfo.getId()});
    }

    @Override
    public void updateFileDatTypes(final List<FileInfo> fileInfoList) {

        List<Object[]> batchParams = new ArrayList<Object[]>();

        for (final FileInfo fileInfo : fileInfoList) {
            batchParams.add(new Object[]{fileInfo.getDataTypeId(), fileInfo.getFileName(), fileInfo.getArchiveName()});
        }
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getJdbcTemplate().getDataSource());
        sjdbc.batchUpdate(UPDATE_FILE_DATA_TYPE, batchParams);

    }

    @Override
    public Long getFileId(final String fileName, final Long archiveId) {
        Long fileId = null;
        try {
            final String select = "select fi.file_id from file_info fi, file_to_archive fa " +
                    "where fi.file_id = fa.file_id and fi.file_name = ? " +
                    "and fa.archive_id = ?";

            fileId = getJdbcTemplate().queryForLong(select, new Object[]{fileName, archiveId});
        } catch (EmptyResultDataAccessException e) {
            //nothing found keep going and return default null value.
        }
        return fileId;
    }

    @Override
    public String getFileNameById(final Long fileId) {
        final String select = "select file_name from file_info where file_id = ?";
        String fileName = null;
        try {
            fileName = (String) getJdbcTemplate().queryForObject(select, new Object[]{fileId}, String.class);
        } catch (EmptyResultDataAccessException e) {
            //nothing found keep going and return default null value.
        }
        return fileName;
    }

    @Override
    public void updateFileDataLevel(final Long fileId, final Integer dataLevel) {
        String updateQuery = "update file_info set level_number = ? where file_id = ?";
        getJdbcTemplate().update(updateQuery, new Object[]{dataLevel, fileId});
    }

    @Override
    public Integer getFileDataLevel(final Long fileId) {
        try {
            String query = "select level_number from file_info where file_id = ?";
            return getJdbcTemplate().queryForInt(query, new Object[]{fileId});
        } catch (IncorrectResultSizeDataAccessException ex) {
            return null;
        }
    }

    @Override
    public FileInfo getFileForFileId(final Long fileId) {
        String query = "Select fi.file_id, fi.file_name, fi.file_size, fi.level_number, fi.data_type_id, fi.md5, fi.revision_of_file_id " +
                "from file_info fi " +
                "where fi.file_id = ? ";

        Object fileInfo = getJdbcTemplate().queryForObject(query, new Object[]{fileId}, getFileInfoRowMapper());
        return (fileInfo == null ? null : (FileInfo) fileInfo);
    }

    @Override
    public Archive getLatestArchiveContainingFile(final FileInfo fileInfo) {
        // should only be one (or zero) archives, but in case things are weird, just get the highest id
        // that matches
        final String query = "select max(a.archive_id) from file_to_archive f2a, archive_info a " +
                "where f2a.file_id=? and a.archive_id=f2a.archive_id and a.is_latest=1 and a.deploy_status='Available'";
        try {
            final long archiveId = getJdbcTemplate().queryForLong(query, new Object[]{fileInfo.getId()});
            return archiveQueries.getArchive(archiveId);

        } catch (IncorrectResultSizeDataAccessException e) {
            // not in any archive
            return null;
        }
    }

    @Override
    public String getSdrfFilePathForExperiment(final String domainName, final String platformName, final String diseaseAbbreviation) {
        final String query = "select file_location_url from file_info f, file_to_archive f2a, archive_info a, " +
                "center c, platform p, disease d, archive_type t " +
                "where a.is_latest=1 and a.deploy_status='Available' and a.archive_type_id=t.archive_type_id and t.archive_type='mage-tab' and " +
                "d.disease_id=a.disease_id and p.platform_id=a.platform_id and c.center_id=a.center_id and " +
                "a.archive_id=f2a.archive_id and f.file_id=f2a.file_id and f.file_name like '%.sdrf.%' and " +
                "c.domain_name=? and p.platform_name=? and d.disease_abbreviation=?";

        try {
            return (String) getJdbcTemplate().queryForObject(query, new Object[]{domainName, platformName, diseaseAbbreviation}, String.class);
        } catch (IncorrectResultSizeDataAccessException e) {
            // no sdrf for that experiment
            return null;
        }
    }

    @Override
    public List<String> getBCRXMLFileLocations(final List<String> patientBarcodes){
        final List<String> bcrXMLFilesLocation = new ArrayList<String>();
        int startIndex = 0;
        int endIndex = 0;
        while(endIndex < patientBarcodes.size()){
            startIndex = endIndex;
            endIndex = ((patientBarcodes.size() - endIndex) > ConstantValues.IN_CLAUSE_SIZE)?endIndex+ConstantValues.IN_CLAUSE_SIZE:patientBarcodes.size();
            final List<String> subList = patientBarcodes.subList(startIndex,endIndex);
            String query = GET_BCRXML_FILES_LOCATION;
            query = query.replace(REPLACE,StringUtil.createPlaceHolderString(subList.size()));

            bcrXMLFilesLocation.addAll(getJdbcTemplate().queryForList(query, String.class, subList.toArray()));

        }


        return bcrXMLFilesLocation;
    }


    @Override
    public void updateArchiveFilesLocationToPublic(final Set<Long> archiveIds) {

        final List<Long> archiveIdsList = new ArrayList<Long>();
        archiveIdsList.addAll(archiveIds);

        int startIndex;
        int endIndex = 0;

        while (endIndex < archiveIds.size()) {

            startIndex = endIndex;
            endIndex = (archiveIds.size() - endIndex) > IN_CLAUSE_SIZE ? endIndex + IN_CLAUSE_SIZE : archiveIds.size();

            final List<Long> archiveIdsSubList = archiveIdsList.subList(startIndex, endIndex);
            final String archiveIdsPlaceHolder = StringUtil.createPlaceHolderString(archiveIdsSubList.size());

            getJdbcTemplate().update(UPDATE_FILES_LOCATION_TO_PUBLIC.replace(ARCHIVE_IDS_PLACEHOLDER, archiveIdsPlaceHolder), archiveIdsSubList.toArray());
        }
    }

    public void deleteFilesFromArchive(final String archiveName) {
        getJdbcTemplate().update(DELETE_ARCHIVE_FILES_QUERY, archiveName);
    }

    private ParameterizedRowMapper<FileInfo> getFileInfoRowMapper() {
        return new ParameterizedRowMapper<FileInfo>() {
            public FileInfo mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setId(resultSet.getLong("file_id"));
                fileInfo.setFileName(resultSet.getString("file_name"));
                fileInfo.setFileSize(resultSet.getLong("file_size"));
                fileInfo.setDataLevel(resultSet.getInt("level_number"));
                fileInfo.setDataTypeId(resultSet.getInt("data_type_id"));
                fileInfo.setFileMD5(resultSet.getString("md5"));
                fileInfo.setRevision(resultSet.getLong("revision_of_file_id"));
                return fileInfo;

            }
        };
    }

    /**
     * Return a {@link ParameterizedRowMapper} for queries that populate file_location_url
     *
     * @return a {@link ParameterizedRowMapper} for queries that populate file_location_url
     */
    private ParameterizedRowMapper<FileInfo> getFileInfoRowMapperWithFileLocationUrl() {

        return new ParameterizedRowMapper<FileInfo>() {

            public FileInfo mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

                final FileInfo fileInfo = new FileInfo();
                fileInfo.setId(resultSet.getLong("file_id"));
                fileInfo.setFileName(resultSet.getString("file_name"));
                fileInfo.setFileSize(resultSet.getLong("file_size"));
                fileInfo.setDataLevel(resultSet.getInt("level_number"));
                fileInfo.setDataTypeId(resultSet.getInt("data_type_id"));
                fileInfo.setFileMD5(resultSet.getString("md5"));
                fileInfo.setRevision(resultSet.getLong("revision_of_file_id"));
                fileInfo.setFileLocation(resultSet.getString("file_location_url"));
                return fileInfo;

            }
        };
    }

    public void setArchiveQueries(ArchiveQueries archiveQueries) {
        this.archiveQueries = archiveQueries;
    }
}
