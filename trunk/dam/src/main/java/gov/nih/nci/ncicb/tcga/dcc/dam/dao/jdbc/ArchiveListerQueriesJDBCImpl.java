package gov.nih.nci.ncicb.tcga.dcc.dam.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.FileCollectionQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.ArchiveListLink;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.ArchiveListerQueries;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation of ArchiveListerQueries.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveListerQueriesJDBCImpl extends SimpleJdbcDaoSupport implements ArchiveListerQueries {

    // queries for all disease abbreviations that have available latest archives for a given visibility identifiable value (0 for open or 1 for controlled)
    private static final String GET_DISEASES_QUERY = "select distinct disease_abbreviation, max(a.date_added) as latest_date " +
            "from disease d, archive_info a, platform p, data_type dt, data_visibility dv, visibility v, archive_type at " +
            "where d.disease_id=a.disease_id " +
            "and p.platform_id=a.platform_id and p.base_data_type_id=dt.data_type_id " +
            "and dt.data_type_id=dv.data_type_id and dv.visibility_id=v.visibility_id " +
            "and a.archive_type_id=at.archive_type_id and at.data_level=dv.level_number " +
            "and a.is_latest=1 and a.deploy_status='Available' " +
            "and v.identifiable=? " +
            "group by disease_abbreviation order by disease_abbreviation";

    // queries for all center type codes that have available latest archives for a given visibility and disease
    private static final String GET_CENTER_TYPES_QUERY = "select distinct ct.center_type_code, max(a.date_added) as latest_date " +
            "from disease d, archive_info a, platform p, data_type dt, data_visibility dv, visibility v, archive_type at, center_type ct " +
            "where d.disease_id=a.disease_id " +
            "and p.platform_id=a.platform_id and p.base_data_type_id=dt.data_type_id " +
            "and dt.data_type_id=dv.data_type_id and dv.visibility_id=v.visibility_id " +
            "and a.archive_type_id=at.archive_type_id and at.data_level=dv.level_number " +
            "and a.is_latest=1 and a.deploy_status='Available' " +
            "and ct.center_type_code=p.center_type_code " +
            "and v.identifiable=? and d.disease_abbreviation=? " +
            "group by ct.center_type_code order by center_type_code";

    // queries for all center domain names that have available latest archives for a given visibility, disease, and center type
    private static final String GET_CENTERS_QUERY = "select distinct domain_name, max(a.date_added) as latest_date " +
            "from disease d, archive_info a, platform p, data_type dt, data_visibility dv, visibility v, archive_type at, center c " +
            "where d.disease_id=a.disease_id " +
            "and p.platform_id=a.platform_id and p.base_data_type_id=dt.data_type_id " +
            "and dt.data_type_id=dv.data_type_id and dv.visibility_id=v.visibility_id " +
            "and a.archive_type_id=at.archive_type_id and at.data_level=dv.level_number " +
            "and a.is_latest=1 and a.deploy_status='Available' " +
            "and c.center_id=a.center_id " +
            "and v.identifiable=? and d.disease_abbreviation=? and c.center_type_code=?" +
            "group by domain_name order by domain_name";

    // queries for all platform names that have available latest archives for a given visibility, disease, center type, and center domain name
    private static final String GET_PLATFORMS_QUERY = "select distinct platform_name, max(a.date_added) as latest_date " +
            "from disease d, archive_info a, platform p, data_type dt, data_visibility dv, visibility v, archive_type at, center c " +
            "where d.disease_id=a.disease_id " +
            "and p.platform_id=a.platform_id and p.base_data_type_id=dt.data_type_id " +
            "and dt.data_type_id=dv.data_type_id and dv.visibility_id=v.visibility_id " +
            "and a.archive_type_id=at.archive_type_id and at.data_level=dv.level_number " +
            "and a.is_latest=1 and a.deploy_status='Available' " +
            "and c.center_id=a.center_id " +
            "and v.identifiable=? and d.disease_abbreviation=? and c.center_type_code=? and c.domain_name=?" +
            "group by platform_name order by platform_name";

    // queries for all archive names and deploy locations that are latest, available, and for a given visibility, disease, center type, center, and platform
    private static final String GET_ARCHIVES_QUERY = "select distinct archive_name, deploy_location, final_size_kb, date_added " +
            "from disease d, archive_info a, platform p, data_type dt, data_visibility dv, visibility v, archive_type at, center c " +
            "where d.disease_id=a.disease_id " +
            "and p.platform_id=a.platform_id and p.base_data_type_id=dt.data_type_id " +
            "and dt.data_type_id=dv.data_type_id and dv.visibility_id=v.visibility_id " +
            "and a.archive_type_id=at.archive_type_id and at.data_level=dv.level_number " +
            "and a.is_latest=1 and a.deploy_status='Available' " +
            "and c.center_id=a.center_id " +
            "and v.identifiable=? and d.disease_abbreviation=? and c.center_type_code=? and c.domain_name=? and p.platform_name=?" +
            "order by archive_name";

    // queries for all files (names and locations) for a given archive name
    private static final String GET_ARCHIVE_FILES_QUERY = "select file_name, file_location_url, file_size, date_added " +
            "from file_info f, archive_info a, file_to_archive f2a " +
            "where f.file_id=f2a.file_id and a.archive_id=f2a.archive_id " +
            "and a.archive_name=? " +
            "order by file_name";


    // instance variables

    private TumorQueries tumorQueries;
    private CenterQueries centerQueries;
    private PlatformQueries platformQueries;
    private ArchiveQueries archiveQueries;


    /**
     * Gets the identifiable parameter to use for a query based on the access level
     * @param accessLevel either open or controlled
     * @return 0 if open, 1 if controlled
     * @throws IllegalArgumentException if an accessLevel other than open or controlled is given
     * @throws gov.nih.nci.ncicb.tcga.dcc.dam.dao.ArchiveListerQueries.ArchiveListerException if the access level is not open or controlled
     */
    private int getIdentifiableForAccessLevel(final String accessLevel) throws ArchiveListerException {
        final int identifiable;
        if (accessLevel.equalsIgnoreCase("open")) {
            identifiable = 0;
        } else if (accessLevel.equalsIgnoreCase("controlled")) {
            identifiable = 1;
        } else {
            throw new ArchiveListerException(accessLevel + " is not a valid access level (must be 'open' or 'controlled'");
        }
        return identifiable;
    }


    /**
     * Gets the RowMapper to use for making ArchiveListLinks based on a query returning just the name for the link.
     * @param linkMaker used for generating the URL for the ArchiveListLink
     * @param forCollections flag for whether this will make file collection links or archive links
     * @return a RowMapper parameterized to ArchiveListLink
     */
    private ParameterizedRowMapper<ArchiveListLink> getRowMapper(final LinkMaker linkMaker, final boolean forCollections) {
        return new ParameterizedRowMapper<ArchiveListLink>() {
            @Override
            public ArchiveListLink mapRow(final ResultSet resultSet, final int i) throws SQLException {
                final String value = resultSet.getString(1);
                final Date modifiedDate = resultSet.getTimestamp("latest_date");
                final ArchiveListLink archiveListLink = new ArchiveListLink();
                archiveListLink.setDisplayName(value);
                archiveListLink.setUrl(linkMaker.makeLinkUrl(value, forCollections));
                archiveListLink.setDeployDate(modifiedDate);
                return archiveListLink;
           }
        };
    }

    private void validateParameters(final String disease, final String centerType,
                                    final String center, final String platform, final String archiveName,
                                    final String collectionName) throws ArchiveListerException {


        if (disease != null && !diseaseExists(disease)) {
            throw new ArchiveListerException("'" + disease + "' is not a valid disease abbreviation");
        }
        if (centerType != null && !centerTypeExists(centerType)) {
            throw new ArchiveListerException("'" + centerType + "' is not a valid center type code");
        }
        if (center != null && !centerExists(centerType, center)) {
            throw new ArchiveListerException("'" + center + "' is not a valid " + centerType + " center domain name");
        }
        if (platform != null && !platformExists(platform)) {
            throw new ArchiveListerException("'" + platform + "' is not a valid platform name");
        }
        if (archiveName != null && !archiveExists(archiveName)) {
            throw new ArchiveListerException("'" + archiveName + "' is not a valid archive name");
        }
        if (collectionName != null && !collectionExists(collectionName)) {
            throw new ArchiveListerException("'" + collectionName + "' does not exist on the filesystem");
        }

    }

    private boolean collectionExists(final String collectionName) {
        final int count = getJdbcTemplate().queryForInt("select count(*) from file_collection where collection_name=?", new Object[]{collectionName});
        return count > 0;
    }

    private boolean archiveExists(final String archiveName) {
        final Long archiveId = archiveQueries.getArchiveIdByName(archiveName);
        return archiveId != null && archiveId > 0;
    }

    private boolean platformExists(final String platform) {
        final Integer platformId = platformQueries.getPlatformIdByName(platform);
        return platformId != null && platformId > 0;
    }

    private boolean centerExists(final String centerType, final String center) {
        final Integer centerId = centerQueries.findCenterId(center, centerType);
        return centerId != null && centerId > 0;
    }

    private boolean centerTypeExists(final String centerType) {
        final int count = getSimpleJdbcTemplate().queryForInt("select count(*) from center_type where center_type_code=?", centerType);
        return count > 0;
    }

    private boolean diseaseExists(final String diseaseAbbreviation) {
        final Integer diseaseId = tumorQueries.getTumorIdByName(diseaseAbbreviation);
        return diseaseId != null && diseaseId != -1;
    }

    /**
     * Gets objects representing all diseases for which, for the
     * given access level, there are available archives.
     *
     * @param linkMaker   the link maker for making URLs for each ArchiveListLink
     * @param accessLevel the access level
     * @return a list of ArchiveListLink objects
     */
    @Override
    public List<ArchiveListLink> getDiseaseLinks(final LinkMaker linkMaker, final String accessLevel) throws ArchiveListerException {
        return getSimpleJdbcTemplate().query(GET_DISEASES_QUERY, getRowMapper(linkMaker, false),
                getIdentifiableForAccessLevel(accessLevel));
    }

    /**
     * Gets objects, representing all center types for which, for the
     * given access level and disease, there are available archives.
     *
     * @param linkMaker   the link maker for making URLs for each ArchiveListLink
     * @param accessLevel the access level
     * @param disease     the disease abbreviation
     * @return a list of ArchiveListLink objects
     */
    @Override
    public List<ArchiveListLink> getCenterTypeLinks(final LinkMaker linkMaker, final String accessLevel, final String disease) throws ArchiveListerException {
        validateParameters(disease, null, null, null, null, null);
        return getSimpleJdbcTemplate().query(GET_CENTER_TYPES_QUERY, getRowMapper(linkMaker, false),
                getIdentifiableForAccessLevel(accessLevel), disease);
    }

    /**
     * Gets objects, representing all centers for which, for the given
     * access level, disease, and center type, there are available archives.
     *
     * @param linkMaker   the link maker for making URLs for each ArchiveListLink
     * @param accessLevel the access level
     * @param disease     the disease abbreviation
     * @param centerType  the center type code
     * @return a list of ArchiveListLink objects
     */
    @Override
    public List<ArchiveListLink> getCenterLinks(final LinkMaker linkMaker, final String accessLevel, final String disease, final String centerType) throws ArchiveListerException {
        validateParameters(disease, centerType, null, null, null, null);
        return getSimpleJdbcTemplate().query(GET_CENTERS_QUERY, getRowMapper(linkMaker, false),
                getIdentifiableForAccessLevel(accessLevel), disease, centerType);
    }

    /**
     * Gets objects representing all platforms for which, for the given
     * access level, disease, center type, and center, there are available archives.
     *
     * @param linkMaker   the link maker for making URLs for each ArchiveListLink
     * @param accessLevel the access level
     * @param disease     the disease abbreviation
     * @param centerType  the center type code
     * @param center      the center domain name
     * @return a list of ArchiveListLink objects
     */
    @Override
    public List<ArchiveListLink> getPlatformLinks(final LinkMaker linkMaker, final String accessLevel, final String disease, final String centerType, final String center) throws ArchiveListerException {
        validateParameters(disease, centerType, center, null, null, null);
        return getSimpleJdbcTemplate().query(GET_PLATFORMS_QUERY, getRowMapper(linkMaker, false),
                getIdentifiableForAccessLevel(accessLevel), disease, centerType, center);
    }

    /**
     * Gets objects representing all available archives for the given parameters -- the archive directory, the archive
     * (.tar.gz) itself, and the archive .md5 file.
     *
     * @param linkMaker   the link maker for making URLs for each ArchiveListLink
     * @param accessLevel the access level -- open or controlled
     * @param disease     the disease abbreviation
     * @param centerType  the center type code
     * @param center      the center domain name
     * @param platform    the platform name
     * @return a list of ArchiveListLink objects, 3 per actual archive
     */
    @Override
    public List<ArchiveListLink> getArchiveLinks(final LinkMaker linkMaker, final String accessLevel, final String disease,
                                                 final String centerType, final String center, final String platform) throws ArchiveListerException {

        validateParameters(disease, centerType, center, platform, null, null);
        final List<ArchiveListLink> archiveLinks = new ArrayList<ArchiveListLink>();
        getJdbcTemplate().query(GET_ARCHIVES_QUERY,
                new Object[]{getIdentifiableForAccessLevel(accessLevel), disease, centerType, center, platform},
                new RowCallbackHandler() {
                    @Override
                    public void processRow(final ResultSet resultSet) throws SQLException {
                        final String archiveName = resultSet.getString("archive_name");
                        final String deployLocation = resultSet.getString("deploy_location");
                        final Date addedDate = resultSet.getTimestamp("date_added");
                        final Long compressedArchiveSize = resultSet.getLong("final_size_kb") * 1024;

                        // add a link for the directory, for the tar.gz and for the md5
                        final ArchiveListLink archiveDirectoryLink = new ArchiveListLink();
                        archiveDirectoryLink.setDisplayName(archiveName);
                        archiveDirectoryLink.setUrl(linkMaker.makeLinkUrl(archiveName, false));
                        archiveDirectoryLink.setDeployDate(addedDate);

                        final ArchiveListLink archiveLink = new ArchiveListLink();
                        archiveLink.setDisplayName(archiveName + ".tar.gz");
                        archiveLink.setUrl(deployLocation);
                        archiveLink.setDeployDate(addedDate);
                        archiveLink.setFileSizeInBytes(compressedArchiveSize);

                        final ArchiveListLink archiveMd5Link = new ArchiveListLink();
                        archiveMd5Link.setDisplayName(archiveName + ".tar.gz.md5");
                        archiveMd5Link.setUrl(deployLocation + ".md5");
                        archiveMd5Link.setDeployDate(addedDate);

                        archiveLinks.add(archiveDirectoryLink);
                        archiveLinks.add(archiveLink);
                        archiveLinks.add(archiveMd5Link);
                    }
                });

        return archiveLinks;
    }

    /**
     * Gets objects, representing all files for the given archive.
     *
     * @param archiveName the archive name
     * @return a list of ArchiveListLink objects
     */
    @Override
    public List<ArchiveListLink> getArchiveFileLinks(final String archiveName) throws ArchiveListerException {
        validateParameters(null, null, null, null, archiveName, null);
        final List<ArchiveListLink> archiveLinks = new ArrayList<ArchiveListLink>();
        getJdbcTemplate().query(GET_ARCHIVE_FILES_QUERY,
                new Object[]{archiveName},
                new RowCallbackHandler() {
                    @Override
                    public void processRow(final ResultSet resultSet) throws SQLException {
                        final String fileName = resultSet.getString("file_name");
                        final String fileLocation = resultSet.getString("file_location_url");
                        final Long fileSizeBytes = resultSet.getLong("file_size");

                        final ArchiveListLink fileLink = new ArchiveListLink();
                        fileLink.setDisplayName(fileName);
                        fileLink.setUrl(fileLocation);
                        fileLink.setDeployDate(resultSet.getTimestamp("date_added"));
                        fileLink.setFileSizeInBytes(fileSizeBytes);

                        archiveLinks.add(fileLink);
                    }
                });

        return archiveLinks;
    }

    /**
     * Queries the file_collection table and makes ArchiveListLink objects that can request those specific collections.
     *
     * @param linkMaker used to make URLs
     * @param accessLevel open or controlled
     * @param diseaseAbbrev the disease abbreviation, or null
     * @param centerType the center type code, or null
     * @param center the center domain name, or null
     * @param platform the platform name, or null
     * @return list of ArchiveListLinks representing any collections found
     * @throws ArchiveListerException if parameters are invalid
     */
    @Override
    public List<ArchiveListLink> getCollectionLinks(final LinkMaker linkMaker, final String accessLevel,
                                                    final String diseaseAbbrev, final String centerType,
                                                    final String center, final String platform) throws ArchiveListerException {
        validateParameters(diseaseAbbrev, centerType, center, platform, null, null);
        final List<Object> bindParams = new ArrayList<Object>();
        final String query = buildCollectionLinksQuery(getIdentifiableForAccessLevel(accessLevel), diseaseAbbrev, centerType, center, platform, bindParams);
        return getSimpleJdbcTemplate().query(query, getRowMapper(linkMaker, true), bindParams.toArray());
    }


    /**
     * Builds the query to find all collections with given parameters.  Note if a parameter is null then "is null" is
     * added to the query -- it doesn't mean any value is acceptable.
     * @param identifiable the integer value for identifiable, for visibility table
     * @param diseaseAbbrev the disease abbreviation or null
     * @param centerType the center type code or null
     * @param center the center domain name or null
     * @param platform the platform name or null
     * @param bindParams list to put bind parameters in for the query
     * @return SQL query to find collection_names matching
     */
    protected String buildCollectionLinksQuery(final Integer identifiable, final String diseaseAbbrev,
                                               final String centerType, final String center, final String platform, final List<Object> bindParams) {


        final String fileCollectionQuery = FileCollectionQueriesJDBCImpl.buildFileCollectionQuery(identifiable, diseaseAbbrev,
                centerType, center, platform, bindParams, "collection_name, max(f2c.file_date) as latest_date", "file_to_collection f2c", "fc.file_collection_id=f2c.file_collection_id");

        return fileCollectionQuery + " group by collection_name order by collection_name";
    }

    /**
     * Gets ArchiveListLink objects representing all files in the given file_collection, found by querying the
     * file_collection and file_to_collection tables
     *
     * @param collectionName the name of the collection to get files for
     * @param accessLevel open or controlled
     * @param disease disease abbreviation or null
     * @param centerType center type code or null
     * @param center center domain name or null
     * @param platform platform name or null
     * @return list of ArchiveListLinks representing files in the collection
     * @throws ArchiveListerException if parameters are invalid
     */
    @Override
    public List<ArchiveListLink> getCollectionFileLinks(final String collectionName, final String accessLevel,
                                                        final String disease, final String centerType, final String center,
                                                        final String platform) throws ArchiveListerException {

        validateParameters(disease, centerType, center, platform, null, collectionName);
        final List<Object> bindParams = new ArrayList<Object>();
        final String query = buildCollectionFileLinksQuery(collectionName, getIdentifiableForAccessLevel(accessLevel), disease, centerType, center, platform, bindParams);
        final List<ArchiveListLink> collectionFileLinks = new ArrayList<ArchiveListLink>();
        getJdbcTemplate().query(query,
                bindParams.toArray(),
                new RowCallbackHandler() {
                    @Override
                    public void processRow(final ResultSet resultSet) throws SQLException {
                        final String fileName = resultSet.getString("file_name");
                        final String fileLocation = resultSet.getString("file_location_url");
                        final Long fileSizeInBytes = resultSet.getLong("file_size");
                        final Date fileDate = resultSet.getTimestamp("file_date");

                        final ArchiveListLink fileLink = new ArchiveListLink();
                        fileLink.setDisplayName(fileName);
                        fileLink.setUrl(fileLocation);
                        fileLink.setFileSizeInBytes(fileSizeInBytes);
                        fileLink.setDeployDate(fileDate);

                        collectionFileLinks.add(fileLink);
                    }
                });
        return collectionFileLinks;
    }

    /**
     * Builds the query for finding files in a collection.
     *
     * @param collectionName name of the file_collection
     * @param identifiable 0 or 1
     * @param disease the disease abbreviation
     * @param centerType center type code
     * @param center the center domain name
     * @param platform the platform name
     * @param bindParams list to store bind parameters for the query
     * @return SQL query for finding files in a collection
     * @throws ArchiveListerException if parameters are invalid
     */
    protected String buildCollectionFileLinksQuery(final String collectionName, final Integer identifiable, final String disease,
                                                   final String centerType, final String center, final String platform,
                                                   final List<Object> bindParams) throws ArchiveListerException {

        bindParams.add(collectionName);
        final String fileCollectionLinkQuery = FileCollectionQueriesJDBCImpl.buildFileCollectionQuery(identifiable, disease,
                centerType, center, platform, bindParams,
                "file_name, file_location_url, file_size, file_date",
                "file_to_collection f2c, file_info f",
                "fc.collection_name=? and fc.file_collection_id=f2c.file_collection_id and f.file_id=f2c.file_id");
        return fileCollectionLinkQuery + " order by file_name";
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

    public void setArchiveQueries(final ArchiveQueries archiveQueries) {
        this.archiveQueries = archiveQueries;
    }
}
