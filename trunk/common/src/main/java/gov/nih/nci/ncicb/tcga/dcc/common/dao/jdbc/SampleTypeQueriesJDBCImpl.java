/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.SampleTypeQueries;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC implementation of SampleTypeQueries.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class SampleTypeQueriesJDBCImpl extends SimpleJdbcDaoSupport implements SampleTypeQueries {
    private static final String ALL_SAMPLE_TYPES_QUERY = "select sample_type_code, definition, is_tumor, short_letter_code from sample_type order by sample_type_code";
    private static final Integer SAMPLE_TYPE_CODE_COLUMN = 1;
    private static final Integer DEFINITION_COLUMN = 2;
    private static final Integer IS_TUMOR_COLUMN = 3;
    private static final Integer SHORT_LETTER_CODE = 4;

    @Override
    public List<SampleType> getAllSampleTypes() {
        return getSimpleJdbcTemplate().getJdbcOperations().query(ALL_SAMPLE_TYPES_QUERY,
                new ParameterizedRowMapper<SampleType>() {
                    public SampleType mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        SampleType sampleType = new SampleType();
                        sampleType.setSampleTypeCode(resultSet.getString(SAMPLE_TYPE_CODE_COLUMN));
                        sampleType.setDefinition(resultSet.getString(DEFINITION_COLUMN));
                        sampleType.setIsTumor(resultSet.getInt(IS_TUMOR_COLUMN) == 1);
                        sampleType.setShortLetterCode(resultSet.getString(SHORT_LETTER_CODE));
                        return sampleType;
                    }
                });
    }
}
