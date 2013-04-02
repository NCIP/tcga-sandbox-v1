/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.LatestArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Maf;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Sdrf;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsCommonConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.LatestGenericReportConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.LatestGenericReportDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties.serverAddress;

/**
 * Class to implement LatestXReportDAO
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Repository
public class LatestGenericReportDAOImpl implements LatestGenericReportDAO {

    private JdbcTemplate jdbcTemplate;

    @Resource(name = "dataReportsDataSource")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<Sdrf> getLatestSdrfWS() {
        return jdbcTemplate.query(LatestGenericReportConstants.QUERY_LATEST_SDRF, sdrfRowMapper);
    }


    public List<Archive> getLatestArchiveWS() {
        return jdbcTemplate.query(LatestGenericReportConstants.QUERY_LATEST_ARCHIVE, archiveRowMapper);
    }

    public List<Archive> getLatestArchiveWSByType(final String archiveType) {
        return jdbcTemplate.query(LatestGenericReportConstants.QUERY_LATEST_ARCHIVE_BY_TYPE, archiveRowMapper, new Object[]{archiveType});
    }

    public List<Maf> getLatestMafWS() {
        return jdbcTemplate.query(LatestGenericReportConstants.QUERY_LATEST_MAF, mafRowMapper);
    }

    public List<LatestArchive> getLatestArchive() {
        return jdbcTemplate.query(LatestGenericReportConstants.QUERY_LATEST_COMBINED, latestArchSdrfMafRowMapper);
    }

    /**
     * row Mapper that maps each row of a resultset with a Sdrf Object.
     */
    private static final ParameterizedRowMapper<Sdrf> sdrfRowMapper = new ParameterizedRowMapper<Sdrf>() {
        public Sdrf mapRow(ResultSet resultSet, int i) throws SQLException {
            final Sdrf sdrf = new Sdrf();
            // Column Index 1: archive name
            sdrf.setRealName(resultSet.getString(1));
            // Column Index 2: date added
            sdrf.setDateAdded(resultSet.getTimestamp(2));
            // Column Index 3: file url
            String url = resultSet.getString(3);
            sdrf.setSdrfUrl(url == null ? DatareportsCommonConstants.NOT_AVAILABLE : serverAddress + url);
            return sdrf;
        }
    };

    /**
     * row Mapper that maps each row of a resultset with an archive Object.
     */
    private static final ParameterizedRowMapper<Archive> archiveRowMapper = new ParameterizedRowMapper<Archive>() {
        public Archive mapRow(ResultSet resultSet, int i) throws SQLException {
            final Archive archive = new Archive();
            // Column Index 1: archive name
            archive.setRealName(resultSet.getString(1));
            // Column Index 2: date added
            archive.setDateAdded(resultSet.getTimestamp(2));
            // Column Index 3: archive url
            String url = resultSet.getString(3);
            archive.setDeployLocation(url == null ? DatareportsCommonConstants.NOT_AVAILABLE : serverAddress + url);
            return archive;
        }
    };

    /**
     * row Mapper that maps each row of a resultset with a maf Object.
     */
    private static final ParameterizedRowMapper<Maf> mafRowMapper = new ParameterizedRowMapper<Maf>() {
        public Maf mapRow(ResultSet resultSet, int i) throws SQLException {
            final Maf maf = new Maf();
            // Column Index 1: archive name
            maf.setRealName(resultSet.getString(1));
            // Column Index 2: date added
            maf.setDateAdded(resultSet.getTimestamp(2));
            // Column Index 3: file url
            String url = resultSet.getString(3);
            maf.setMafUrl(url == null ? DatareportsCommonConstants.NOT_AVAILABLE : serverAddress + url);
            return maf;
        }
    };

    /**
     * row Mapper that maps each row of a resultset with a latestArchive Object.
     */
    private static final ParameterizedRowMapper<LatestArchive> latestArchSdrfMafRowMapper =
            new ParameterizedRowMapper<LatestArchive>() {
                public LatestArchive mapRow(ResultSet resultSet, int i) throws SQLException {
                    final LatestArchive combi = new LatestArchive();
                    // Column Index 1: archive name
                    combi.setArchiveName(resultSet.getString(1));
                    // Column Index 2: archive date added
                    combi.setDateAdded(resultSet.getTimestamp(2));
                    // Column Index 3: archive url
                    String archiveUrl = resultSet.getString(3);
                    combi.setArchiveUrl(archiveUrl == null ? DatareportsCommonConstants.NOT_AVAILABLE : serverAddress + archiveUrl);
                    // Column Index 4: archive type
                    combi.setArchiveType(resultSet.getString(4));
                    // Column Index 5: sdrf file name
                    String sdrf = resultSet.getString(5);
                    combi.setSdrfName(sdrf == null ? DatareportsCommonConstants.NOT_AVAILABLE : sdrf);
                    // Column Index 6: sdrf url
                    String sdrfUrl = resultSet.getString(6);
                    combi.setSdrfUrl(sdrfUrl == null ? DatareportsCommonConstants.NOT_AVAILABLE : serverAddress + sdrfUrl);
                    // Column Index 7: maf file name
                    String maf = resultSet.getString(7);
                    combi.setMafName(maf == null ? DatareportsCommonConstants.NOT_AVAILABLE : maf);
                    // Column Index 8: maf url
                    String mafUrl = resultSet.getString(8);
                    combi.setMafUrl(mafUrl == null ? DatareportsCommonConstants.NOT_AVAILABLE : serverAddress + mafUrl);
                    return combi;
                }
            };

}//End of Class
