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
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BamTelemetry;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.BamTelemetryReportDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.BamTelemetryReportConstants.QUERY_BAM_TELEMETRY_LIST;


/**
 * jdbc implementation of the Bam telemetry report
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Repository
public class BamTelemetryReportDAOImpl implements BamTelemetryReportDAO {

    private JdbcTemplate jdbcTemplate;

    @Resource(name = "dataReportsDataSource")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Cached
    public List<BamTelemetry> getBamTelemetryRows() {
        return jdbcTemplate.query(QUERY_BAM_TELEMETRY_LIST, bamTelemetryRowMapper);
    }

    private static final ParameterizedRowMapper<BamTelemetry> bamTelemetryRowMapper = new ParameterizedRowMapper<BamTelemetry>() {
        public BamTelemetry mapRow(ResultSet resultSet, int i) throws SQLException {
            final BamTelemetry bam = new BamTelemetry();
            bam.setBamFile(resultSet.getString(1));
            bam.setDisease(resultSet.getString(2));
            bam.setCenter(resultSet.getString(3) + " (" + resultSet.getString(9) + ")");
            bam.setFileSize(resultSet.getLong(4));
            bam.setDateReceived(resultSet.getDate(5));
            bam.setDataType(resultSet.getString(6));
            bam.setMolecule(resultSet.getString(7));
            bam.setAliquotId(resultSet.getString(8));
            bam.setParticipantId(getParticipant(resultSet.getString(8)));
            bam.setSampleId(getSample(resultSet.getString(8)));
            bam.setAliquotUUID(resultSet.getString(10));
            return bam;
        }
    };

    private static String getSample(String barcode) {
        String res = "";
        if (barcode != null && barcode.length() > 16) {
            return barcode.substring(0, 16);
        }
        return res;
    }

    private static String getParticipant(String barcode) {
        String res = "";
        if (barcode != null && barcode.length() > 12) {
            return barcode.substring(0, 12);
        }
        return res;
    }

}//End of class
