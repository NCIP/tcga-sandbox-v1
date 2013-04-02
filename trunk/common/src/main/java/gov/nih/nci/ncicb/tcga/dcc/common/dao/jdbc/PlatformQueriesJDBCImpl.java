/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.object.MappingSqlQuery;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Robert S. Sfeir
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class PlatformQueriesJDBCImpl extends BaseQueriesProcessor implements PlatformQueries {

    private static final String PLATFORM = "platform";

    public Integer getPlatformIdByName(final String platformName) {
        return getObjectIdByNameAsInteger(platformName, PLATFORM, "platform_name", "platform_id");
    }

    public Platform getPlatformForName(final String platformName) {
        return searchPlatform("platform_name", platformName);
    }

    /**
     * Return the Platform with the given alias
     *
     * @param platformAlias
     * @return the Platform with the given alias
     */
    @Override
    public Platform getPlatformWithAlias(final String platformAlias) {
        return searchPlatform("platform_alias", platformAlias);
    }

    /**
     * Returns the first Platform that has a match for <code>columnValue</code>
     * in the given <code>columnName</code>, <code>null</code> otherwise.
     *
     * @param columnName  the column to search
     * @param columnValue the value to match
     * @return the first Platform that has a match for <code>columnValue</code> in the given <code>columnName</code>, <code>null</code> otherwise.
     */
    private Platform searchPlatform(final String columnName, final String columnValue) {

        final String sql = "select * from " + PLATFORM + " where " + columnName + " = '" + columnValue + "'";
        final PlatformQuery query = new PlatformQuery(getDataSource(), sql);
        final List results = query.execute();

        return (results == null || results.size() == 0 ? null : (Platform) results.get(0));
    }

    public Collection<Map<String, Object>> getAllPlatforms() {
        String select = "select * from " + PLATFORM + " where available=1 order by platform_display_name";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        return sjdbc.queryForList(select);
    }

    @Override
    public List<Platform> getPlatformList() {
        final String select = "select platform_id, platform_name, center_type_code, platform_display_name, platform_alias " +
                "from platform  where available=1 order by platform_name, center_type_code";
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        return sjdbc.getJdbcOperations().query(select, getPlatformRowMapper());
    }

    public static ParameterizedRowMapper<Platform> getPlatformRowMapper() {
        return new ParameterizedRowMapper<Platform>() {
            public Platform mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                Platform platform = new Platform();
                platform.setPlatformDisplayName(resultSet.getString("platform_display_name"));
                platform.setPlatformId(resultSet.getInt("platform_id"));
                platform.setPlatformName(resultSet.getString("platform_name"));
                platform.setCenterType(resultSet.getString("center_type_code"));
                platform.setPlatformAlias(resultSet.getString("platform_alias"));
                return platform;
            }
        };
    }

    public String getPlatformNameById(final Integer platformId) {
        String select = "select platform_name from " + PLATFORM + " where platform_id = ?";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        return sjdbc.queryForObject(select, String.class, platformId);
    }

    /**
     * Return the <code>Platform</code> with the given Id
     *
     * @param platformId the <code>Platform</code> Id
     * @return the <code>Platform</code> with the given Id
     */
    @Override
    public Platform getPlatformById(Integer platformId) {
        return searchPlatform("platform_id", platformId.toString());
    }

    static class PlatformQuery extends MappingSqlQuery {

        PlatformQuery(DataSource ds, String selectStmt) {
            super(ds, selectStmt);
        }

        protected Platform mapRow(final ResultSet rs, final int rownum) throws SQLException {

            final Platform platform = new Platform();

            platform.setPlatformDisplayName(rs.getString("platform_display_name"));
            platform.setPlatformId(rs.getInt("platform_id"));
            platform.setPlatformName(rs.getString("platform_name"));
            platform.setCenterType(rs.getString("center_type_code"));
            platform.setPlatformAlias(rs.getString("platform_alias"));

            return platform;
        }
    }
}
