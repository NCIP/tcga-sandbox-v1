/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Robert S. Sfeir
 */
public class DataTypeQueriesJDBCImpl extends BaseQueriesProcessor implements DataTypeQueries {

    private static final String DATATYPE = "data_type";

    private static final String GET_DATA_TYPES_NAME = "select name,data_type_id from data_type";
    public DataTypeQueriesJDBCImpl() {
    }

    public String getBaseDataTypeDisplayNameForPlatform(final Integer platformId) {
        String select = "select data_type.name from data_type, platform where platform.platform_id=? and platform.base_data_type_id= data_type.data_type_id";
        final SimpleJdbcTemplate st = new SimpleJdbcTemplate(getDataSource());
        return st.queryForObject(select, String.class, platformId);
    }

    public String getCenterTypeIdForPlatformId(final Integer platformId) {
        String select = "select platform.center_type_code from platform where platform.platform_id=? ";
        final SimpleJdbcTemplate st = new SimpleJdbcTemplate(getDataSource());
        return st.queryForObject(select, String.class, platformId);
    }

    public Collection<Map<String, Object>> getAllDataTypes() {
        String select = "select * from " + DATATYPE + " where available=1 order by name";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        return sjdbc.queryForList(select);
    }

    public String getDataTypeFTPDisplayForPlatform(final String platformId) {
        String select = "select data_type.ftp_display from data_type, platform  " +
                "where platform.platform_id= ? and platform.base_data_type_id = data_type.data_type_id";
        final SimpleJdbcTemplate st = new SimpleJdbcTemplate(getDataSource());
        try {
            return st.queryForObject(select, String.class, platformId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Map<String,Long> getAllDataTypesId(){
        final Map<String,Long>  datTypesIdByName = new HashMap<String,Long>();
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        sjdbc.getJdbcOperations().query(GET_DATA_TYPES_NAME, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                datTypesIdByName.put(rs.getString("name"),rs.getLong("data_type_id"));
            }
        });

        return datTypesIdByName;
    }
}
