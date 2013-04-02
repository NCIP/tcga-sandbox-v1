/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.ChartDataRow;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.StatsDashboardDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.FILTER_PIE_CHART;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_ABS_TOTAL_NUMBER_ARCHIVE_DOWNLOAD;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_ABS_TOTAL_NUMBER_ARCHIVE_RECEIVED;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_ABS_TOTAL_SIZE_ARCHIVE_DOWNLOAD;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_ABS_TOTAL_SIZE_ARCHIVE_RECEIVED;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_CUMULATIVE_NUMBER_ARCHIVE_DOWNLOAD_PER_MONTHYEAR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_CUMULATIVE_NUMBER_ARCHIVE_RECEIVED_PER_MONTHYEAR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_CUMULATIVE_SIZE_ARCHIVE_DOWNLOAD_PER_MONTHYEAR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_CUMULATIVE_SIZE_ARCHIVE_RECEIVED_PER_MONTHYEAR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_FILTER_BATCH;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_FILTER_PIE_CHART;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_FILTER_PIE_CHART_PER_DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_NUMBER_ARCHIVE_DOWNLOAD_PER_MONTHYEAR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_NUMBER_ARCHIVE_DOWNLOAD_TOTAL_PER_DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_NUMBER_ARCHIVE_RECEIVED_PER_MONTHYEAR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_NUMBER_ARCHIVE_RECEIVED_TOTAL_PER_DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_REFRESH_STATS_DASHBOARD_PROC;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_SIZE_ARCHIVE_DOWNLOAD_PER_MONTHYEAR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_SIZE_ARCHIVE_DOWNLOAD_TOTAL_PER_DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_SIZE_ARCHIVE_RECEIVED_PER_MONTHYEAR;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants.QUERY_SIZE_ARCHIVE_RECEIVED_TOTAL_PER_DISEASE;


/**
 * jdbc implementation of the Stats dashboard dao layer
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Repository
public class StatsDashboardDAOImpl implements StatsDashboardDAO, Serializable {

    protected final Log logger = LogFactory.getLog(getClass());

    private JdbcTemplate jdbcTemplate;

    @Resource(name = "dataReportsDataSource")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void refreshStatsDashboardProcedure() {
        long start = System.currentTimeMillis();
        jdbcTemplate.execute(QUERY_REFRESH_STATS_DASHBOARD_PROC);
        long duration = System.currentTimeMillis() - start;
        logger.info("refresh Stats Dashboard procedure took: " + duration / 1000 + " seconds.");
    }

    @Override
    public List<ChartDataRow> getNumberArchivesDownloadedDrillDown(final String disease) {
        return jdbcTemplate.query(QUERY_NUMBER_ARCHIVE_DOWNLOAD_PER_MONTHYEAR,
                dualValueRowMapper, new Object[]{disease});
    }

    @Override
    public List<ChartDataRow> getSizeArchivesDownloadedDrillDown(final String disease) {
        return jdbcTemplate.query(QUERY_SIZE_ARCHIVE_DOWNLOAD_PER_MONTHYEAR,
                dualValueRowMapper, new Object[]{disease});
    }

    @Override
    public List<ChartDataRow> getNumberArchivesReceivedDrillDown(final String disease) {
        return jdbcTemplate.query(QUERY_NUMBER_ARCHIVE_RECEIVED_PER_MONTHYEAR,
                dualValueRowMapper, new Object[]{disease});
    }

    @Override
    public List<ChartDataRow> getSizeArchivesReceivedDrillDown(final String disease) {
        return jdbcTemplate.query(QUERY_SIZE_ARCHIVE_RECEIVED_PER_MONTHYEAR,
                dualValueRowMapper, new Object[]{disease});
    }

    @Override
    public List<ChartDataRow> getCumulativeNumberArchivesDownloadedDrillDown(String disease) {
        return jdbcTemplate.query(QUERY_CUMULATIVE_NUMBER_ARCHIVE_DOWNLOAD_PER_MONTHYEAR,
                dualValueRowMapper, new Object[]{disease});
    }

    @Override
    public List<ChartDataRow> getCumulativeSizeArchivesDownloadedDrillDown(String disease) {
        return jdbcTemplate.query(QUERY_CUMULATIVE_SIZE_ARCHIVE_DOWNLOAD_PER_MONTHYEAR,
                dualValueRowMapper, new Object[]{disease});
    }

    @Override
    public List<ChartDataRow> getCumulativeNumberArchivesReceivedDrillDown(String disease) {
        return jdbcTemplate.query(QUERY_CUMULATIVE_NUMBER_ARCHIVE_RECEIVED_PER_MONTHYEAR,
                dualValueRowMapper, new Object[]{disease});
    }

    @Override
    public List<ChartDataRow> getCumulativeSizeArchivesReceivedDrillDown(String disease) {
        return jdbcTemplate.query(QUERY_CUMULATIVE_SIZE_ARCHIVE_RECEIVED_PER_MONTHYEAR,
                dualValueRowMapper, new Object[]{disease});
    }

    @Override
    public List<ChartDataRow> getFilterPieChart(final String type) {
        return jdbcTemplate.query(QUERY_FILTER_PIE_CHART, dualValueRowMapper, new Object[]{type});
    }

    @Override
    public List<ChartDataRow> getFilterBatch() {
        return jdbcTemplate.query(QUERY_FILTER_BATCH, dualValueRowMapper,
                new Object[]{FILTER_PIE_CHART.get("batchFilter")});
    }

    @Override
    public List<ChartDataRow> getFilterPieChartDrillDown(String type, String selection) {
        return jdbcTemplate.query(QUERY_FILTER_PIE_CHART_PER_DISEASE, dualValueRowMapper,
                new Object[]{type, selection});
    }

    @Override
    public Long getAbsoluteTotalNumberArchiveDownloaded() {
        return jdbcTemplate.queryForLong(QUERY_ABS_TOTAL_NUMBER_ARCHIVE_DOWNLOAD);
    }

    @Override
    public Long getAbsoluteTotalSizeArchiveDownloaded() {
        return jdbcTemplate.queryForLong(QUERY_ABS_TOTAL_SIZE_ARCHIVE_DOWNLOAD);
    }

    @Override
    public Long getAbsoluteTotalNumberArchiveReceived() {
        return jdbcTemplate.queryForLong(QUERY_ABS_TOTAL_NUMBER_ARCHIVE_RECEIVED);
    }

    @Override
    public Long getAbsoluteTotalSizeArchiveReceived() {
        return jdbcTemplate.queryForLong(QUERY_ABS_TOTAL_SIZE_ARCHIVE_RECEIVED);
    }

    @Override
    public List<ChartDataRow> getNumberArchivesDownloadedTotal() {
        return jdbcTemplate.query(QUERY_NUMBER_ARCHIVE_DOWNLOAD_TOTAL_PER_DISEASE, dualValueRowMapper);
    }

    @Override
    public List<ChartDataRow> getSizeArchivesDownloadedTotal() {
        return jdbcTemplate.query(QUERY_SIZE_ARCHIVE_DOWNLOAD_TOTAL_PER_DISEASE, dualValueRowMapper);
    }

    @Override
    public List<ChartDataRow> getNumberArchivesReceivedTotal() {
        return jdbcTemplate.query(QUERY_NUMBER_ARCHIVE_RECEIVED_TOTAL_PER_DISEASE, dualValueRowMapper);
    }

    @Override
    public List<ChartDataRow> getSizeArchivesReceivedTotal() {
        return jdbcTemplate.query(QUERY_SIZE_ARCHIVE_RECEIVED_TOTAL_PER_DISEASE, dualValueRowMapper);
    }


    private static final ParameterizedRowMapper<ChartDataRow> dualValueRowMapper =
            new ParameterizedRowMapper<ChartDataRow>() {
                public ChartDataRow mapRow(ResultSet resultSet, int i) throws SQLException {
                    return new ChartDataRow(resultSet.getString(1), resultSet.getString(2));
                }
            };

}//End of Class
