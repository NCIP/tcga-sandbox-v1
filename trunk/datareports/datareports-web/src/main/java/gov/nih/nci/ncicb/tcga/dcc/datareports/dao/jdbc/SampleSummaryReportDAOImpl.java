/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Sample;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.SampleSummary;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.SampleSummaryReportDAO;
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

/**
 * Implementation of the DAO contract
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */

@Repository
public class SampleSummaryReportDAOImpl implements SampleSummaryReportDAO, Serializable {

    protected final Log logger = LogFactory.getLog(SampleSummaryReportDAOImpl.class);

    private JdbcTemplate jdbcTemplate;

    /**
     * I define here a row Mapper that maps each row of a resultset with a sample Object.
     * This is for use of all the drill-downs queries which returns samples
     */
    private final ParameterizedRowMapper<Sample> sampleRowMapper = new ParameterizedRowMapper<Sample>() {
        public Sample mapRow(ResultSet resultSet, int i) throws SQLException {
            final Sample sample = new Sample();
            // I put 1 there b/c I know there is only one column returned which is the sample column
            sample.setName(resultSet.getString(1));
            //Col 2 is the sample date string
            sample.setSampleDate(resultSet.getString(2));
            return sample;
        }
    };

    /**
     * Sample Summary rowMapper
     */
    private final ParameterizedRowMapper<SampleSummary> sampleSummaryRowMapper =
            new ParameterizedRowMapper<SampleSummary>() {
                public SampleSummary mapRow(ResultSet resultSet, int i) throws SQLException {
                    final SampleSummary summary = new SampleSummary();
                    summary.setDisease(resultSet.getString(2));
                    summary.setCenterName(resultSet.getString(3));
                    summary.setCenterType(resultSet.getString(4));
                    summary.setCenter(summary.getCenterName() + " (" + summary.getCenterType() + ")");
                    summary.setPortionAnalyte(resultSet.getString(5));
                    String platform = resultSet.getString(6);
                    summary.setPlatform((platform == null) ? "Undetermined" : platform);
                    summary.setTotalBCRSent(resultSet.getLong(7));
                    summary.setTotalCenterSent(resultSet.getLong(8));
                    summary.setTotalBCRUnaccountedFor(resultSet.getLong(9));
                    summary.setTotalCenterUnaccountedFor(resultSet.getLong(10));
                    summary.setTotalLevelOne(resultSet.getLong(11));
                    summary.setTotalLevelTwo(resultSet.getLong(12));
                    summary.setTotalLevelThree(resultSet.getLong(13));
                    String level4 = resultSet.getString(14);
                    summary.setLevelFourSubmitted((level4 == null) ? "N" : level4.trim());
                    summary.setLastRefresh(resultSet.getTimestamp(15));
                    return summary;
                }
            };

    @Resource(name = "dataReportsDataSource")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void refreshTable() {
        long start = System.currentTimeMillis();
        jdbcTemplate.execute(SampleSummaryReportConstants.QUERY_REFRESH_SAMPLE_SUMMARY_TABLE);
        long duration = System.currentTimeMillis() - start;
        logger.info("Query to refresh Sample Summary report took: " + duration / 1000 + " seconds.");
    }

    /**
     * A query to return all of the rows in the Sample Summary table for the specific tumor type
     *
     * @param tumorAbbr The abbreviation of a tumor name
     * @return a series of rows to populate the table
     */
    public List<SampleSummary> getSampleSummaryRows(final String tumorAbbr) {
        return jdbcTemplate.query(SampleSummaryReportConstants.QUERY_SAMPLE_SUMMARY_FOR_DISEASE_ABBR, sampleSummaryRowMapper,
                new Object[]{tumorAbbr});
    }

    /**
     * A query to return all of the rows in the Sample Summary table for the specific tumor type
     *
     * @return a series of rows to populate the table
     */
    public List<SampleSummary> getSampleSummaryRows() {
        return jdbcTemplate.query(SampleSummaryReportConstants.QUERY_SAMPLE_SUMMARY, sampleSummaryRowMapper);

    }

    /**
     * A query to return all of the samples for BCR sent to center colum of the Sample Summary report
     *
     * @param tumorAbbr      cancer type abbreviation
     * @param centerName     e.g. broad.mit.edu
     * @param centerType     e.g. CGCC
     * @param portionAnalyte e.g. D
     * @return a list of maps containing one sample each
     */
    public List<Sample> getSamplesForTotalSamplesBCRSent(
            final String tumorAbbr,
            final String centerName,
            final String centerType,
            final String portionAnalyte) {

        return jdbcTemplate.query(SampleSummaryReportConstants.QUERY_SAMPLE_IDS_BCR_REPORTED_SENDING_TO_CENTER_QUERY,
                sampleRowMapper,
                new Object[]{tumorAbbr, centerName, centerType, portionAnalyte});
    }

    /**
     * A query to return all of the samples for other "total" columns in the Sample Summary report
     *
     * @param tumorAbbr      cancer type abbreviation
     * @param centerName     e.g. broad.mit.edu
     * @param centerType     e.g. CGCC
     * @param platform       e.g. Genome_Wide_SNP_6
     * @param portionAnalyte e.g. D
     * @return a list of maps containing one sample each
     */
    public List<Sample> getSamplesForTotalSamplesCenterSent(
            final String tumorAbbr,
            final String centerName,
            final String centerType,
            final String portionAnalyte,
            final String platform) {
        return jdbcTemplate.query(SampleSummaryReportConstants.QUERY_SAMPLE_IDS_DCC_RECEIVED_FROM_CENTER,
                sampleRowMapper,
                new Object[]{tumorAbbr, centerName, centerType, portionAnalyte, platform});
    }

    /**
     * A query to return all of the samples for other "total" columns in the Sample Summary report
     *
     * @param tumorAbbr      cancer type abbreviation
     * @param centerName     e.g. broad.mit.edu
     * @param centerType     e.g. CGCC
     * @param platform       e.g. Genome_Wide_SNP_6
     * @param portionAnalyte e.g. D
     * @return a list of maps containing one sample each
     */
    public List<Sample> getSamplesForTotalSamplesUnaccountedForBCR(
            final String tumorAbbr,
            final String centerName,
            final String centerType,
            final String portionAnalyte,
            final String platform) {
        return jdbcTemplate.query(SampleSummaryReportConstants.QUERY_UNACCOUNTED_FOR_BCR_SAMPLE_IDS_THAT_CENTER_REPORTED,
                sampleRowMapper,
                new Object[]{tumorAbbr, centerName, centerType, portionAnalyte, platform,
                        tumorAbbr, centerName, centerType, portionAnalyte});
    }

    /**
     * A query to return all of the samples for other "total" columns in the Sample Summary report
     *
     * @param tumorAbbr      cancer type abbreviation
     * @param centerName     e.g. broad.mit.edu
     * @param centerType     e.g. CGCC
     * @param portionAnalyte e.g. D
     * @param platform       e.g. Genome_Wide_SNP_6
     * @return a list of maps containing one sample each
     */
    public List<Sample> getSamplesForTotalSamplesUnaccountedForCenter(
            final String tumorAbbr,
            final String centerName,
            final String centerType,
            final String portionAnalyte,
            final String platform) {
        return jdbcTemplate.query(SampleSummaryReportConstants.QUERY_UNACCOUNTED_FOR_CENTER_SAMPLE_IDS_THAT_BCR_REPORTED,
                sampleRowMapper,
                new Object[]{tumorAbbr, centerName, centerType, portionAnalyte,
                        tumorAbbr, centerName, centerType, portionAnalyte, platform});


    }

    /**
     * A query to return all of the samples for a given level "total" column in the Sample Summary report
     *
     * @param tumorAbbr      cancer type abbreviation
     * @param centerName     e.g. broad.mit.edu
     * @param centerType     e.g. CGCC
     * @param portionAnalyte e.g. D
     * @param platform       e.g. Genome_Wide_SNP_6
     * @param level          1, 2 or 3
     * @return a list of maps containing one sample each
     */
    public List<Sample> getSamplesForLevelTotal(
            final String tumorAbbr,
            final String centerName,
            final String centerType,
            final String portionAnalyte,
            final String platform,
            final int level) {
        return jdbcTemplate.query(SampleSummaryReportConstants.QUERY_SAMPLE_IDS_WITH_LEVEL_X_DATA,
                sampleRowMapper,
                new Object[]{tumorAbbr, centerName, centerType, portionAnalyte, platform, level});
    }

}//End of Class
