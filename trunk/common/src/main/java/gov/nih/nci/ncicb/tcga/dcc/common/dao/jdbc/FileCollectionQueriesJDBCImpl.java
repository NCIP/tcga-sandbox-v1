package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileCollection;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileCollectionQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.md5.MD5ChecksumCreator;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JDBC implementation of FileCollectionQueries.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class FileCollectionQueriesJDBCImpl extends BaseQueriesProcessor implements FileCollectionQueries {
    private TumorQueries tumorQueries;
    private CenterQueries centerQueries;
    private PlatformQueries platformQueries;
    private FileInfoQueries fileQueries;
    private MD5ChecksumCreator md5Creator = new MD5ChecksumCreator();

    /**
     * Saves a file collection.  If it already exists with the given parameters, will not add again.
     *
     * @param collectionName      the name of the file collection
     * @param isIdentifiable      if the collection contains files with identifiable data
     * @param diseaseAbbreviation disease abbreviation or null
     * @param centerType          center type or null
     * @param centerDomain        center name or null
     * @param platformName        platform name or null
     * @return FileCollection bean that was saved
     */
    @Override
    public FileCollection saveCollection(final String collectionName, final boolean isIdentifiable, final String diseaseAbbreviation, final String centerType, final String centerDomain, final String platformName) {
        final FileCollection collection = new FileCollection();

        Long collectionId = lookupCollectionId(collectionName, isIdentifiable, diseaseAbbreviation, centerType, centerDomain, platformName);
        if (collectionId == null) {
            final Long visibilityId = getVisibilityId(isIdentifiable);

            Integer diseaseId = null;
            if (diseaseAbbreviation != null) {
                diseaseId = tumorQueries.getTumorIdByName(diseaseAbbreviation);
                if (diseaseId == -1) {
                    throw new IllegalArgumentException("Error saving collection '" + collectionName + "': Disease '" + diseaseAbbreviation + "' not found");
                }
            }
            Integer centerId = null;
            if (centerDomain != null) {
                final Center center = centerQueries.getCenterByName(centerDomain, centerType);
                if (center == null) {
                    throw new IllegalArgumentException("Error saving collection '" + collectionName + "': Center " + centerDomain + " (" + centerType + ") not found");
                }
                centerId = center.getCenterId();
            }

            Integer platformId = null;
            if (platformName != null) {
                platformId = platformQueries.getPlatformIdByName(platformName);
                if (platformId == -1) {
                    throw new IllegalArgumentException("Error saving collection '" + collectionName + "': Platform " + platformName + " not found");
                }
            }

            collectionId = getNextSequenceNumber("file_collection_seq");
            String insert = "insert into file_collection(file_collection_id, collection_name, visibility_id, disease_id, center_type_code, center_id, platform_id)" +
                    " values(?, ?,  ?, ?, ?, ?, ?)";
            getJdbcTemplate().update(insert, new Object[]{collectionId, collectionName, visibilityId, diseaseId, centerType, centerId, platformId});
        }
        collection.setId(collectionId);
        collection.setName(collectionName);
        return collection;
    }

    private Long getVisibilityId(final boolean identifiable) {
        try {
            return getJdbcTemplate().queryForLong("select visibility_id from visibility where identifiable = " + (identifiable ? "1" : "0"));
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new IllegalArgumentException("Visibility level for identifiable = " + identifiable + " not found");
        }
    }

    protected Long lookupCollectionId(final String collectionName, final boolean identifiable, final String diseaseAbbreviation, final String centerType, final String centerDomain, final String platformName) {
        Long collectionId;

        final List<Object> bindParams = new ArrayList<Object>();
        bindParams.add(collectionName);

        String completeQuery = buildFileCollectionQuery(identifiable ? 1 : 0, diseaseAbbreviation, centerType,
                centerDomain, platformName, bindParams, "file_collection_id", null, "collection_name=?");

        try {
            collectionId = getJdbcTemplate().queryForLong(completeQuery, bindParams.toArray());
        } catch (IncorrectResultSizeDataAccessException e) {
            collectionId = null;
        }
        return collectionId;
    }

    /**
     * Helper method to build queries against the file_collection table.  Note if a parameter is null that does not
     * mean "any" but rather that the value for that should be null for the collection in the database.
     *
     * The alias "fc" will be used for file_collection in the query.
     *
     * @param visibilityId the visibility ID to search for
     * @param diseaseAbbrev the disease abbreviation for the query (may be null)
     * @param centerType the center type code for the query (may be null)
     * @param center the center domain name for the query (may be null)
     * @param platform the platform name for the query (may be null)
     * @param bindParams list of bind parameters for the query
     * @param selectClause what to select in the query (e.g. "collection_name, file_collection_id")
     * @param additionalFromClause additions to the from clause
     * @param additionalWhereClause additions to the where clause other than what will be generated by the parameters passed in
     * @return the query against file_collection
     */
    public static String buildFileCollectionQuery(final Integer visibilityId, final String diseaseAbbrev, final String centerType,
                                                  final String center, final String platform, final List<Object> bindParams,
                                                  final String selectClause, final String additionalFromClause,
                                                  final String additionalWhereClause) {
        final StringBuilder query = new StringBuilder().append("select ").append(selectClause).append(" from file_collection fc");
        if (additionalFromClause != null && additionalFromClause.length() > 0) {
            query.append(", ").append(additionalFromClause);
        }
        final StringBuilder whereClause = new StringBuilder(" where ").append(additionalWhereClause);

        bindParams.add(visibilityId);
        query.append(", visibility v");
        whereClause.append(" and v.visibility_id=fc.visibility_id and v.identifiable=?");

        if (diseaseAbbrev != null) {
            query.append(", disease d");
            whereClause.append(" and d.disease_id=fc.disease_id and d.disease_abbreviation=?");
            bindParams.add(diseaseAbbrev);
        } else {
            whereClause.append(" and fc.disease_id is null");
        }

        if (centerType != null) {
            whereClause.append(" and fc.center_type_code=?");
            bindParams.add(centerType);
        } else {
            whereClause.append(" and fc.center_type_code is null");
        }

        if (center != null) {
            query.append(", center c");
            whereClause.append(" and c.center_id=fc.center_id and c.domain_name=?");
            bindParams.add(center);
        } else {
            whereClause.append(" and fc.center_id is null");
        }

        if (platform != null) {
            query.append(", platform p");
            whereClause.append(" and p.platform_id=fc.platform_id and p.platform_name=?");
            bindParams.add(platform);
        } else {
            whereClause.append(" and fc.platform_id is null");
        }

        return query.append(whereClause).toString();
    }

    /**
     * Saves files to collection.  Will create file info record first if needed.
     *
     * @param collection the collection to save file to - must already exist!
     * @param fileLocation the full path location for the file
     * @param fileDate the date the file was created/updated
     */
    @Override
    public void saveFileToCollection(final FileCollection collection, final String fileLocation, final Date fileDate) {
        // 1. check if there is already a file_info record that is linked to this collection at the same fileLocation
        // and if so, just update the date and size
        final File fileToSave = new File(fileLocation);
        final Long fileSize = getFileSize(fileToSave);
        final String md5 = getFileMd5(fileToSave);

        final Long[] fileId = new Long[1];
        fileId[0] = null;


        getJdbcTemplate().query("select file_id from file_to_collection where file_collection_id=? and file_location_url=?",
                new Object[]{collection.getId(), fileLocation},
                new RowCallbackHandler() {
                    @Override
                    public void processRow(final ResultSet resultSet) throws SQLException {
                        fileId[0] = resultSet.getLong("file_id");
                    }
                }
        );

        if (fileId[0] != null) {

            // update the size and date and md5 of the file_info
            getJdbcTemplate().update("update file_info set file_size=?, md5=? where file_id=?", new Object[]{fileSize, md5, fileId[0]});
            getJdbcTemplate().update("update file_to_collection set file_date=? where file_id=? and file_collection_id=? and file_location_url=?",
                    new Object[]{fileDate, fileId[0], collection.getId(), fileLocation});

        } else {

            // does not exist, need to save it
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(fileToSave.getName());
            fileInfo.setFileMD5(md5);
            fileInfo.setFileSize(fileSize);

            fileId[0] = fileQueries.addFile(fileInfo);
            getJdbcTemplate().update("insert into file_to_collection(file_collection_id, file_id, file_location_url, file_date) values(?, ?, ?, ?)",
                    new Object[]{collection.getId(), fileId[0], fileLocation, fileDate});
        }
    }

    protected String getFileMd5(final File file) {
        try {
            return MD5ChecksumCreator.convertStringToHex(md5Creator.generate(file));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    protected long getFileSize(final File file) {
        return file.length();
    }

    public void setTumorQueries(final TumorQueries tumorQueries) {
        this.tumorQueries = tumorQueries;
    }

    public void setCenterQueries(final CenterQueries centerQueries) {
        this.centerQueries = centerQueries;
    }

    public void setPlatformQueries(final PlatformQueries platformQueries) {
        this.platformQueries = platformQueries;
    }

    public void setFileQueries(final FileInfoQueries fileQueries) {
        this.fileQueries = fileQueries;
    }
}
