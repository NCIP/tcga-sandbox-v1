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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Aliquot;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.AliquotArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.AliquotReportDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotReportConstants.QUERY_ALIQUOT_ARCHIVES_FILES;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotReportConstants.QUERY_ALIQUOT_LIST;

/**
 * Implementation oof the aliquotReportDAO interface
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Repository
public class AliquotReportDAOImpl implements AliquotReportDAO {

    private JdbcTemplate jdbcTemplate;

    @Resource(name = "dataReportsDataSource")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * row Mapper that maps each row of a result set with a aliquot Object.
     */
    private static final ParameterizedRowMapper<Aliquot> aliquotRowMapper = new ParameterizedRowMapper<Aliquot>() {
        public Aliquot mapRow(ResultSet resultSet, int i) throws SQLException {
            final Aliquot aliquot = new Aliquot();
            // Column Index 1: disease
            aliquot.setDisease(resultSet.getString(1));
            // Column Index 2: aliquotId
            aliquot.setAliquotId(resultSet.getString(2));
            // Column Index 3: bcr_batch
            String tmp = resultSet.getString(3);
            aliquot.setBcrBatch(null == tmp ? "N/A" : tmp);
            // Column Index 4: receiving_center
            aliquot.setCenter(resultSet.getString(4));
            // Column Index 5: platform
            aliquot.setPlatform(resultSet.getString(5));
            // Column Index 6: level_one_data
            aliquot.setLevelOne(resultSet.getString(6));
            // Column Index 7: level_two_data
            aliquot.setLevelTwo(resultSet.getString(7));
            // Column Index 8: level_three_data
            aliquot.setLevelThree(resultSet.getString(8));
            return aliquot;
        }
    };

    private static final ParameterizedRowMapper<AliquotArchive> aliquotArchiveRowMapper = new ParameterizedRowMapper<AliquotArchive>() {
        public AliquotArchive mapRow(ResultSet resultSet, int i) throws SQLException {
            final AliquotArchive archive = new AliquotArchive();
            // Column Index 1: archive id
            archive.setArchiveId(resultSet.getInt(1));
            // Column Index 2: archive_name
            archive.setArchiveName(resultSet.getString(2));
            // Column Index 3: file id
            archive.setFileId(resultSet.getInt(3));
            // Column Index 4: file_name
            archive.setFileName(resultSet.getString(4));
            // Column Index 5: file_location_url
            archive.setFileUrl(resultSet.getString(5));

            return archive;
        }
    };

    @Cached
    public List<Aliquot> getAliquotRows() {
        return jdbcTemplate.query(QUERY_ALIQUOT_LIST, aliquotRowMapper);
    }

    public List<AliquotArchive> getAliquotArchive(final String aliquotId, final int level) {
        return jdbcTemplate.query(QUERY_ALIQUOT_ARCHIVES_FILES, aliquotArchiveRowMapper,
                new Object[]{aliquotId, level});
    }

}//End of class
