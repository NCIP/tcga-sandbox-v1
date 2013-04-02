/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Robert S. Sfeir
 */
public class CenterQueriesJDBCImpl implements CenterQueries {
    public static final String GET_UUID_CENTER_QUERY = "select center_id" +
	                " from center " +
	                " where is_uuid_converted > 0  and domain_name=? and center_type_code=?";
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public CenterQueriesJDBCImpl() {
    }

    /**
     * Returns the center id for a given center name
     *
     * @param centerName center name
     * @return Center Id
     */
    public Integer findCenterId(final String centerName, final String centerType) {
        String select = "Select center_id, domain_name, center_type_code, display_name, short_name" +
                " from center " +
                " where domain_name = ?" +
                " and center_type_code = ?";

        List<Center> centerList = jdbcTemplate.query(select, getCenterRowMapper(), centerName, centerType);
        return (centerList == null || centerList.size() == 0 ? null : centerList.get(0).getCenterId());
    }

    @Deprecated
    /**
     * Returns a Collection of Map of column names Vs values for the center table
     * Reason for deprecating : this method should ideally return a List of Center beans, however this method has been used
     * in DAM at places.  A separate ticket has been added for changing them to use the correct method.
     */
    public Collection<Map<String, Object>> getAllCenters() {
        return getAllObjectsAsList("center", "display_name");
    }

    /**
     * Returns the center for a given center Id
     *
     * @param centerId center Id
     * @return Center for the given center Id, null otherwise
     */
    public Center getCenterById(final Integer centerId) {
        String select = "Select center_id, domain_name, center_type_code, display_name, short_name" +
                " from center " +
                " where center_id = ?";

        Center center = null;
        List<Center> centerList = jdbcTemplate.query(select, getCenterRowMapper(), centerId);
        if ((centerList != null) && (centerList.size() > 0)) {
            center = centerList.get(0);
            center.setEmailList(getCenterEmails(center.getCenterId()));
            return center;
        }
        return center;
    }

    /**
     * Returns list of all the centers
     *
     * @return list of centers
     */
    public List<Center> getCenterList() {
        String select = "select center_id, domain_name, center_type_code, display_name, short_name " +
                "from center order by domain_name,center_type_code";
        List<Center> centerList = jdbcTemplate.query(select, getCenterRowMapper());
        updateWithEmails(centerList);
        return centerList;
    }

    /**
     * Returns list of all the real centers with existing bcr center id
     * center_id=10 is combined GSC which is a fake center
     *
     * @return list of centers
     */
    public List<Center> getRealCenterList() {
        String select = "select c.center_id, domain_name, c.center_type_code, display_name, short_name, bcr_center_id " +
                "from center c, center_to_bcr_center b  where c.center_id = b.center_id " +
                "and c.center_type_code = b.center_type_code and c.center_id <> 10 order by bcr_center_id";
        List<Center> centerList = jdbcTemplate.query(select, fullCenterRowMapper);
        updateWithEmails(centerList);
        return centerList;
    }

    /**
     * Returns a center for a given name and center type code
     *
     * @param centerName     center name
     * @param centerTypeCode center type code
     * @return Returns a center for the specified name and center type code, null otherwise
     */
    public Center getCenterByName(final String centerName, final String centerTypeCode) {
        String select = "Select center_id, domain_name, center_type_code, display_name, short_name" +
                " from center " +
                " where domain_name = ?" +
                " and center_type_code = ?";

        List<Center> centerList = jdbcTemplate.query(select, getCenterRowMapper(), centerName, centerTypeCode.toUpperCase());
        Center center = null;
        if ((centerList != null) && (centerList.size() > 0)) {
            center = centerList.get(0);
            center.setEmailList(getCenterEmails(center.getCenterId()));
            return center;
        }
        return center;
    }

    /**
     * Looks up Center to BCR Center mapping table and returns the center ID for the given BCR Center ID
     *
     * @param bcrCenterCode BCR Center code
     * @return Center ID
     */
    public Integer getCenterIdForBCRCenter(final String bcrCenterCode) {
        String query = "Select center_id from center_to_bcr_center where bcr_center_id = ?";
        Integer id = null;
        try {
            id = jdbcTemplate.queryForObject(query, Integer.class, bcrCenterCode);
        } catch (DataAccessException e) {
            return null;
        }
        return id;
    }

    private void updateWithEmails(final List<Center> centerList) {
        if ((centerList != null) && (centerList.size() > 0)) {
            for (Center center : centerList) {
                center = centerList.get(0);
                center.setEmailList(getCenterEmails(center.getCenterId()));
            }
        }
    }

    public static ParameterizedRowMapper<Center> getCenterRowMapper() {
        return new ParameterizedRowMapper<Center>() {
            public Center mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                Center center = new Center();
                center.setCenterDisplayName(resultSet.getString("display_name"));
                int centerId = resultSet.getInt("center_id");
                center.setCenterId(centerId);
                center.setCenterName(resultSet.getString("domain_name"));
                center.setCenterType(resultSet.getString("center_type_code"));
                center.setShortName(resultSet.getString("short_name"));
                return center;
            }
        };
    }

    private static final ParameterizedRowMapper<Center> fullCenterRowMapper =
            new ParameterizedRowMapper<Center>() {
                public Center mapRow(ResultSet resultSet, int i) throws SQLException {
                    Center center = new Center();
                    center.setCenterDisplayName(resultSet.getString("display_name"));
                    center.setCenterId(resultSet.getInt("center_id"));
                    center.setCenterName(resultSet.getString("domain_name"));
                    center.setCenterType(resultSet.getString("center_type_code"));
                    center.setShortName(resultSet.getString("short_name"));
                    center.setBcrCenterId(resultSet.getString("bcr_center_id"));
                    return center;
                }
            };

    public List<String> getCenterEmails(final int centerId) {
        String select = "Select email_address from center_email where center_id = ?";
        List<String> emailList = jdbcTemplate.query(select, new ParameterizedRowMapper<String>() {
            public String mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                return resultSet.getString("email_address");
            }
        }, centerId);
        return (emailList == null || emailList.size() == 0 ? null : emailList);

    }

    public Collection<Map<String, Object>> getAllObjectsAsList(final String tableName, final String orderBy) {
        String select = new StringBuilder().append("select * from ").append(tableName).append(" order by ").append(orderBy).toString();
        return this.jdbcTemplate.queryForList(select);
    }

	@Override
	public List<Center> getConvertedToUUIDCenters() {
		
		String select = "select center_id, domain_name, center_type_code, display_name, short_name" +
	                " from center " +
	                " where is_uuid_converted > 0";
		
		List<Center> centerList = jdbcTemplate.query(select, getCenterRowMapper());
		return centerList;
				 
	}

	@Override
	public boolean isCenterCenvertedToUUID(Center center) {
		List <Center> convertedCenterList = getConvertedToUUIDCenters();
		return convertedCenterList.contains(center);		
	}

    @Override
    public boolean isCenterConvertedToUUID(final String centerName, final String centerTypeCode){
        List<Long> centerIds = jdbcTemplate.queryForList(GET_UUID_CENTER_QUERY,Long.class,centerName,centerTypeCode);

        return !centerIds.isEmpty();
    }

    @Override
    public boolean doesCenterRequireMageTab(final String centerName, final String centerTypeCode) {
        boolean requiresMageTab = false;
        try {
            final int requiresValue = jdbcTemplate.queryForInt("select requires_magetab from center where domain_name=? and center_type_code=?",
                    centerName, centerTypeCode);
            requiresMageTab = requiresValue == 1;
        } catch (IncorrectResultSizeDataAccessException e) {
            // can't find the center
        }
        return requiresMageTab;
    }

}
