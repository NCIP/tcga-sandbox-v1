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
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Visibility;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.VisibilityQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Robert S. Sfeir
 * @version $Id: AccessQueriesJDBCImpl.java 1253 2008-06-11 17:19:52Z sfeirr $
 */
public class VisibilityQueriesJDBCImpl extends BaseQueriesProcessor implements VisibilityQueries {
    // todo? make BaseQueriesProcessor extend SimpleJdbcDaoSupport so don't need to do this...
	// todo : use spring dependency injection to inject the SimpleJdbcTemplate?
    private SimpleJdbcTemplate simpleJdbcTemplate;

    private static final String LEAST_VISIBILITY_QUERY = "select visibility.visibility_id, visibility.visibility_name, visibility.identifiable " +
                    "from visibility, data_visibility, data_type " +
                    "where visibility.visibility_id = data_visibility.visibility_id " +
                    "and data_type.data_type_id = data_visibility.data_type_id " +
                    "and data_type.name = ?";

    private static final String ARCHIVE_VISIBILITY_QUERY = "select visibility.visibility_id, visibility.visibility_name, visibility.identifiable " +
                    "from visibility, data_visibility, data_type " +
                    "where visibility.visibility_id = data_visibility.visibility_id " +
                    "and data_type.data_type_id = data_visibility.data_type_id " +
                    "and data_visibility.level_number = ? and data_type.name = ?";
    
    private static final String PLATFORM_VISIBILITY_QUERY = "select v.visibility_id, v.visibility_name, v.identifiable " +
    				"from platform p, data_type_to_platform dp, data_type d, data_visibility dv, visibility v " +
    				"where p.platform_name = ? " +
    				"and p.platform_id = dp.platform_id " +
    				"and dp.data_type_id = d.data_type_id " +
    				"and d.name = ? " +
    				"and d.center_type_code = ? " +
    				"and d.available = 1 " +
    				"and d.data_type_id = dv.data_type_id " +
    				"and dv.level_number = ? " +
    				"and dv.visibility_id = v.visibility_id";

    public Visibility getVisibilityForArchive( final Archive archive ) {
        if (simpleJdbcTemplate == null) {
            simpleJdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        }
        final List<Visibility> accessList = simpleJdbcTemplate.query(ARCHIVE_VISIBILITY_QUERY, getVisibilityRowMapper(), archive.getDataLevel(), archive.getDataType());
        return (accessList == null || accessList.size() == 0 ? null : accessList.get(0));
    }

    @Override
    public Visibility getLeastVisibilityForDataType(final String dataTypeName) {
        if (simpleJdbcTemplate == null) {
            simpleJdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        }

        final List<Visibility> visibilities = simpleJdbcTemplate.query(LEAST_VISIBILITY_QUERY, getVisibilityRowMapper(), dataTypeName);
        for (final Visibility visibility : visibilities) {
            if (visibility.isIdentifiable()) {
                return  visibility;
            }
        }
        return visibilities.size() > 0 ? visibilities.get(0) : null;
    }

	@Override
	public Visibility getVisibilityForPlatform(final String platformName, 
										final String dataTypeName,
										final String centerTypeCode,
										final Integer levelNumber) {
		if (simpleJdbcTemplate == null) {
			simpleJdbcTemplate = new SimpleJdbcTemplate(getDataSource());
		}
		final List<Visibility> visibilities = simpleJdbcTemplate.query(
												PLATFORM_VISIBILITY_QUERY, 
												getVisibilityRowMapper(), 
												platformName,
												dataTypeName,
												centerTypeCode,
												levelNumber);
        return (visibilities == null || visibilities.size() == 0 ? null : visibilities.get(0));
	}

    private ParameterizedRowMapper<Visibility> getVisibilityRowMapper() {
        return new ParameterizedRowMapper<Visibility>() {
            public Visibility mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                final Visibility visibility = new Visibility();
                visibility.setVisibilityId(resultSet.getInt("visibility_id"));
                visibility.setVisibilityName(resultSet.getString("visibility_name"));
                visibility.setIdentifiable(resultSet.getInt("identifiable") == 1 );
                return visibility;
            }
        };
    }
    
}
