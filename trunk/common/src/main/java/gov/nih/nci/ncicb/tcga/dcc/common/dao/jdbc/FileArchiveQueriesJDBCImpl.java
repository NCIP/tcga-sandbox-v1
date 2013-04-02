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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileToArchive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC implementation of FileArchiveQueries
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FileArchiveQueriesJDBCImpl extends BaseQueriesProcessor implements FileArchiveQueries {
    private static final String IN_CLAUSE_PLACEHOLDER = "IN_CLAUSE";
    private static final String FILE_TO_ARCHIVE_INSERT_QUERY = "insert into file_to_archive(file_archive_id, archive_id, file_id, file_location_url) values(?, ?, ?, ?)";
    private static final String FILE_TO_ARCHIVE_DELETE_FILES_QUERY = "DELETE FROM file_to_archive where " +
            "archive_id = ? AND " +
            "file_id IN(" +
            IN_CLAUSE_PLACEHOLDER +
            ")";


    private static final String GET_CLINICAL_XML_FILE_LOCATION_QUERY = "select c.domain_name, fa.file_location_url from   file_info f, file_to_archive fa, archive_info a, platform p,center c \n" +
            " where  f.file_name like '%.xml'" +
            " and    f.file_id=fa.file_id" +
            " and    fa.archive_id=a.archive_id" +
            " and    a.center_id = c.center_id" +
            " and    a.deploy_status='Available' " +
            " and    a.is_latest = 1 " +
            " and    a.platform_id = p.platform_id " +
            " and    p.platform_name='bio' " +
            " order by c.domain_name asc, f.file_name asc";

    public Long addFileToArchiveAssociation(final FileInfo fileInfo, final Archive archive) {
        return addFileToArchiveAssociation(fileInfo, archive, false, new Long(-1));
    }

    /**
     * Add an association between this file and this archive.
     *
     * @param fileInfo        file info
     * @param archive         archive
     * @param useIdFromCommon Set to true if fileArchiveId should be used while adding the association to database, false otherwise
     * @param fileArchiveId   file archive id
     * @return the file to archive association id in database
     */
    public Long addFileToArchiveAssociation(final FileInfo fileInfo, final Archive archive, final Boolean useIdFromCommon, Long fileArchiveId) {
        String sql = "insert into file_to_archive(file_archive_id, archive_id, file_id, file_location_url) values(?, ?, ?, ?)";
        if (!associationExists(fileInfo, archive)) {
            if (!useIdFromCommon) {
                fileArchiveId = getNextSequenceNumber("FILE_ARCHIVE_SEQ");
            }
            getJdbcTemplate().update(sql, new Object[]{fileArchiveId, archive.getId(), fileInfo.getId(), getFileLocation(fileInfo, archive)});
        } else {
            updateAssociation(fileInfo, archive);
        }
        return fileArchiveId;
    }

    public void addFileToArchiveAssociations(final List<FileToArchive> fileToArchives) {
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        final List<Object[]> valueList = new ArrayList<Object[]>();

        for (final FileToArchive fileToArchive : fileToArchives) {
            Object[] data = new Object[4];

            if (fileToArchive.getFileArchiveId() == null ||
                    fileToArchive.getFileArchiveId() == ConstantValues.NOT_ASSIGNED) {
                fileToArchive.setFileArchiveId(getNextSequenceNumber("FILE_ARCHIVE_SEQ"));
            }
            int index = 0;
            data[index++] = fileToArchive.getFileArchiveId();
            data[index++] = fileToArchive.getArchiveId();
            data[index++] = fileToArchive.getFileId();
            data[index++] = fileToArchive.getFileLocationURL();
            valueList.add(data);
        }

        sjdbc.batchUpdate(FILE_TO_ARCHIVE_INSERT_QUERY, valueList);
        valueList.clear();
    }

    public void deleteFileToArchiveAssociations(final List<Long> fileIds, final Long archiveId) {
        String replaceData = StringUtil.createPlaceHolderString(fileIds.size());
        String query = FILE_TO_ARCHIVE_DELETE_FILES_QUERY.replace(IN_CLAUSE_PLACEHOLDER, replaceData);
        final List<Long> parameters = new ArrayList<Long>();
        parameters.add(archiveId);
        parameters.addAll(fileIds);
        getJdbcTemplate().update(query, parameters.toArray(new Long[1]));

    }

    private void updateAssociation(final FileInfo fileInfo, final Archive archive) {
        String sql = "update file_to_archive set file_location_url=? where archive_id=? and file_id=?";
        getJdbcTemplate().update(sql, new Object[]{
                getFileLocation(fileInfo, archive),
                archive.getId(),
                fileInfo.getId()
        });
    }

    public Long getFileArchiveId(final FileInfo fileInfo, final Archive archive) {
        String sql = "select file_archive_id from file_to_archive where archive_id=? and file_id=?";
        return getJdbcTemplate().queryForLong(sql, new Object[]{archive.getId(), fileInfo.getId()});
    }


    public boolean associationExists(final FileInfo fileInfo, final Archive archive) {
        String sql = "select count(*) from file_to_archive where archive_id=? and file_id=?";
        long count = getJdbcTemplate().queryForLong(sql, new Object[]{archive.getId(), fileInfo.getId()});
        return count > 0;
    }


    public Map<String, List<String>> getClinicalXMLFileLocations() {
        final Map<String, List<String>> xmlFileLocationByCenter = new HashMap<String, List<String>>();
        getJdbcTemplate().query(GET_CLINICAL_XML_FILE_LOCATION_QUERY, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                final String centerName = rs.getString("domain_name");
                List<String> xmlFileLocations = xmlFileLocationByCenter.get(centerName);
                if (xmlFileLocations == null) {
                    xmlFileLocations = new ArrayList<String>();
                    xmlFileLocationByCenter.put(centerName, xmlFileLocations);
                }
                xmlFileLocations.add(rs.getString("file_location_url"));
            }
        });

        return xmlFileLocationByCenter;
    }

    private String getFileLocation(final FileInfo fileInfo, final Archive archive) {
        // this is a URL, so don't use File.separator... URLs use forward-slashes only
        return archive.getDeployDirectory() + "/" + fileInfo.getFileName();
    }
}
