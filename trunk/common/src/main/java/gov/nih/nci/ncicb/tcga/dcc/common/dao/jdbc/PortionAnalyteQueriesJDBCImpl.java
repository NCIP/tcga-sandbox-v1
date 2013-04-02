/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.PortionAnalyte;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PortionAnalyteQueries;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC implementation of PortionAnalyteQueries.
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class PortionAnalyteQueriesJDBCImpl extends SimpleJdbcDaoSupport implements PortionAnalyteQueries {
    public static final String QUERY_PORTION_ANALYTE = "select portion_analyte_code, definition " +
            "from portion_analyte order by definition";
    private static final Integer PORTION_ANALYTE_CODE_COLUMN = 1;
    private static final Integer DEFINITION_COLUMN = 2;

    @Override
    public List<PortionAnalyte> getAllPortionAnalytes() {
        return getSimpleJdbcTemplate().getJdbcOperations().query(QUERY_PORTION_ANALYTE,
                new ParameterizedRowMapper<PortionAnalyte>() {
                    public PortionAnalyte mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        PortionAnalyte portionAnalyte = new PortionAnalyte();
                        portionAnalyte.setPortionAnalyteCode(resultSet.getString(PORTION_ANALYTE_CODE_COLUMN));
                        portionAnalyte.setDefinition(resultSet.getString(DEFINITION_COLUMN));
                        return portionAnalyte;
                    }
                });
    }

}//End of Class