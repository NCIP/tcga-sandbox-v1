/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache.Cached;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotIdBreakdown;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotIdBreakdownReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.AliquotIdBreakdownReportDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * implementation of the biospecimen breakdown interface
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
@Repository
public class AliquotIdBreakdownReportDAOImpl implements AliquotIdBreakdownReportDAO {

    private JdbcTemplate jdbcTemplate;

    @Resource(name = "dataReportsDataSource")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Cached
    public List<AliquotIdBreakdown> getAliquotIdBreakdown() {
        return jdbcTemplate.query(AliquotIdBreakdownReportConstants.QUERY_ALIQUOT_ID_BREAKDOWN, bbRowMapper);
    }

    private static final ParameterizedRowMapper<AliquotIdBreakdown> bbRowMapper =
            new ParameterizedRowMapper<AliquotIdBreakdown>() {
                public AliquotIdBreakdown mapRow(ResultSet resultSet, int i) throws SQLException {
                    final AliquotIdBreakdown bb = new AliquotIdBreakdown();
                    // Column Index 1: aliquot id
                    bb.setAliquotId(resultSet.getString(1));
                    // Column Index 2: analyte id
                    bb.setAnalyteId(resultSet.getString(2));
                    // Column Index 3: sample id
                    bb.setSampleId(resultSet.getString(3));
                    // Column Index 4: participant id
                    bb.setParticipantId(resultSet.getString(4));
                    // Column Index 5: project
                    bb.setProject(resultSet.getString(5));
                    // Column Index 6: tissue source site
                    bb.setTissueSourceSite(resultSet.getString(6));
                    // Column Index 7: participant
                    bb.setParticipant(resultSet.getString(7));
                    // Column Index 8: sampleType
                    bb.setSampleType(resultSet.getString(8));
                    // Column Index 9: vial id
                    bb.setVialId(resultSet.getString(9));
                    // Column Index 10: portion id
                    bb.setPortionId(resultSet.getString(10));
                    // Column Index 11: portion analyte
                    bb.setPortionAnalyte(resultSet.getString(11));
                    // Column Index 12: plate id
                    bb.setPlateId(resultSet.getString(12));
                    // Column Index 13: center id
                    bb.setCenterId(resultSet.getString(13));

                    return bb;
                }
            };

}//End of Class
