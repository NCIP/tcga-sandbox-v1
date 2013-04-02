/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDType;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDTypeQueries;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC implementation of UUIDTypeQueries.
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Repository
public class UUIDTypeQueriesJDBCImpl extends SimpleJdbcDaoSupport implements UUIDTypeQueries {
    protected final Log logger = LogFactory.getLog(getClass());
    public static final String QUERY_UUID_ITEM_TYPE = "select item_type_id, item_type, sort_order " +
            "from uuid_item_type order by sort_order";
    public static final String QUERY_UUID_ITEM_TYPE_ID = "Select item_type_id from uuid_item_type " +
            "where upper(item_type)=? ";
    private static final Integer ITEM_TYPE_ID_COLUMN = 1;
    private static final Integer ITEM_TYPE_COLUMN = 2;
    private static final Integer SORT_ORDER_COLUMN = 3;

    @Override
    public List<UUIDType> getAllUUIDTypes() {
        return getSimpleJdbcTemplate().getJdbcOperations().query(QUERY_UUID_ITEM_TYPE,
                new ParameterizedRowMapper<UUIDType>() {
                    public UUIDType mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        UUIDType uuidType = new UUIDType();
                        uuidType.setUuidTypeId(resultSet.getInt(ITEM_TYPE_ID_COLUMN));
                        uuidType.setUuidType(resultSet.getString(ITEM_TYPE_COLUMN));
                        uuidType.setSortOrder(resultSet.getInt(SORT_ORDER_COLUMN));
                        return uuidType;
                    }
                });
    }

    @Override
    public Long getUUIDTypeID(final String itemType) {
        try {
            return getSimpleJdbcTemplate().queryForLong(QUERY_UUID_ITEM_TYPE_ID,
                    new Object[]{itemType == null ? "" : itemType.toUpperCase()});
        } catch (EmptyResultDataAccessException e) {
            logger.info(e);
            return null;
        }
    }
}//End of Class