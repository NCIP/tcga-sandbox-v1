/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BcrBatchCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.CenterCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.CodeReport;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.DataType;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PlatformCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Tissue;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.TissueSourceSite;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.CodeTablesReportDAO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.CELL_LINE_CONTROL_DISPLAY;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.QUERY_BCR_BATCH;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.QUERY_CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.QUERY_DATA_LEVEL;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.QUERY_DATA_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.QUERY_DISEASE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.QUERY_DISEASE_ACTIVE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.QUERY_PLATFORM;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.QUERY_PORTION_ANALYTE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.QUERY_SAMPLE_TYPE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.QUERY_TISSUE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.CodeTablesReportConstants.QUERY_TISSUE_SOURCE_SITE;

/**
 * Jdbc Implementation of the CodeTableReportDAO interface
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@Repository
public class CodeTablesReportDAOImpl implements CodeTablesReportDAO {

    private JdbcTemplate jdbcTemplate;

    @Resource(name = "dataReportsDataSource")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<TissueSourceSite> getTissueSourceSite() {
        final List<TissueSourceSite> tssList = jdbcTemplate.query(QUERY_TISSUE_SOURCE_SITE, tssRowMapper);
        final List<TissueSourceSite> finalTssList = new ArrayList<TissueSourceSite>();
        TissueSourceSite lastTss = null;
        for (final TissueSourceSite tss : tssList) {
            if (lastTss == null || !lastTss.getCode().equals(tss.getCode())) {
                finalTssList.add(tss);
            } else if (lastTss.getCode().equals(tss.getCode())) {
                lastTss.setStudyName(CELL_LINE_CONTROL_DISPLAY);
            }
            lastTss = tss;
        }
        return finalTssList;
    }

    public List<CenterCode> getCenterCode() {
        return jdbcTemplate.query(QUERY_CENTER, centerRowMapper);
    }

    public List<CodeReport> getDataLevel() {
        return jdbcTemplate.query(QUERY_DATA_LEVEL, codeReportRowMapper);
    }

    public List<DataType> getDataType() {
        return jdbcTemplate.query(QUERY_DATA_TYPE, dataTypeRowMapper);
    }

    public List<Tumor> getTumor() {
        return jdbcTemplate.query(QUERY_DISEASE, tumorRowMapper);
    }

    public List<Tumor> getActiveTumor() {
        return jdbcTemplate.query(QUERY_DISEASE_ACTIVE, tumorRowMapper);
    }

    public List<PlatformCode> getPlatformCode() {
        return jdbcTemplate.query(QUERY_PLATFORM, platformRowMapper);
    }

    public List<CodeReport> getPortionAnalyte() {
        return jdbcTemplate.query(QUERY_PORTION_ANALYTE, codeReportRowMapper);
    }

    public List<SampleType> getSampleType() {
        return jdbcTemplate.query(QUERY_SAMPLE_TYPE, sampleTypeRowMapper);
    }

    public List<Tissue> getTissue() {
        return jdbcTemplate.query(QUERY_TISSUE, tissueRowMapper);
    }

    public List<BcrBatchCode> getBcrBatchCode() {
        return jdbcTemplate.query(QUERY_BCR_BATCH, bbRowMapper);
    }

    /**
     * row Mapper that maps each row of a resultset with a CenterCode Object.
     */
    private static final ParameterizedRowMapper<CenterCode> centerRowMapper =
            new ParameterizedRowMapper<CenterCode>() {
                public CenterCode mapRow(ResultSet resultSet, int i) throws SQLException {
                    final CenterCode cc = new CenterCode();
                    // Column Index 1: code
                    cc.setCode(resultSet.getString(1));
                    // Column Index 2: center name
                    cc.setCenterName(resultSet.getString(2));
                    // Column Index 3: center type
                    cc.setCenterType(resultSet.getString(3));
                    // Column Index 4: center display name
                    cc.setCenterDisplayName(resultSet.getString(4));
                    // Column Index 5: center short name
                    cc.setShortName(resultSet.getString(5));
                    return cc;
                }
            };

    /**
     * row Mapper that maps each row of a resultset with a DataType Object.
     */
    private static final ParameterizedRowMapper<DataType> dataTypeRowMapper =
            new ParameterizedRowMapper<DataType>() {
                public DataType mapRow(ResultSet resultSet, int i) throws SQLException {
                    final DataType dt = new DataType();
                    // Column Index 1: center type
                    dt.setCenterType(resultSet.getString(1));
                    // Column Index 2: displayName
                    dt.setDisplayName(resultSet.getString(2));
                    // Column Index 3: ftp display
                    dt.setFtpDisplay(resultSet.getString(3));
                    // Column Index 4: available
                    boolean bool = resultSet.getBoolean(4);
                    dt.setAvailable(bool ? "Yes" : "No");
                    return dt;
                }
            };

    /**
     * row Mapper that maps each row of a resultset with a DataType Object.
     */
    private static final ParameterizedRowMapper<Tumor> tumorRowMapper =
            new ParameterizedRowMapper<Tumor>() {
                public Tumor mapRow(ResultSet resultSet, int i) throws SQLException {
                    final Tumor t = new Tumor();
                    // Column Index 1: tumor name (abbreviation)
                    t.setTumorName(resultSet.getString(1));
                    // Column Index 2: tumor description (name)
                    t.setTumorDescription(resultSet.getString(2));
                    return t;
                }
            };

    /**
     * row Mapper that maps each row of a resultset with a platform Object.
     */
    private static final ParameterizedRowMapper<PlatformCode> platformRowMapper =
            new ParameterizedRowMapper<PlatformCode>() {
                public PlatformCode mapRow(ResultSet resultSet, int i) throws SQLException {
                    final PlatformCode p = new PlatformCode();
                    // Column Index 1: platformname
                    p.setPlatformName(resultSet.getString(1));
                    // Column Index 2: platform alias
                    p.setPlatformAlias(resultSet.getString(2));
                    // Column Index 3: platform display name
                    p.setPlatformDisplayName(resultSet.getString(3));
                    // Column Index 4: available
                    boolean bool = resultSet.getBoolean(4);
                    p.setAvailable(bool ? "Yes" : "No");
                    return p;
                }
            };

    /**
     * row Mapper that maps each row of a resultset with a CodeReport Object.
     */
    private static final ParameterizedRowMapper<CodeReport> codeReportRowMapper =
            new ParameterizedRowMapper<CodeReport>() {
                public CodeReport mapRow(ResultSet resultSet, int i) throws SQLException {
                    final CodeReport a = new CodeReport();
                    // Column Index 1: code
                    a.setCode(resultSet.getString(1));
                    // Column Index 2: definition
                    a.setDefinition(resultSet.getString(2));
                    return a;
                }
            };

    /**
     * row Mapper that maps each row of a resultset with a sample type Object.
     */
    private static final ParameterizedRowMapper<SampleType> sampleTypeRowMapper =
            new ParameterizedRowMapper<SampleType>() {
                public SampleType mapRow(ResultSet resultSet, int i) throws SQLException {
                    final SampleType sampleType = new SampleType();
                    // Column Index 1: code
                    sampleType.setSampleTypeCode(resultSet.getString(1));
                    // Column Index 2: definition
                    sampleType.setDefinition(resultSet.getString(2));
                    // Column Index 3: short_letter_code
                    sampleType.setShortLetterCode(resultSet.getString(3));
                    return sampleType;
                }
            };

    /**
     * row Mapper that maps each row of a resultset with a Tissue Object.
     */
    private static final ParameterizedRowMapper<Tissue> tissueRowMapper =
            new ParameterizedRowMapper<Tissue>() {
                public Tissue mapRow(ResultSet resultSet, int i) throws SQLException {
                    final Tissue t = new Tissue();
                    // Column Index 1: code
                    t.setTissue(resultSet.getString(1));
                    return t;
                }
            };

    /**
     * row Mapper that maps each row of a resultset with a Tissue Source Site Object.
     */
    private static final ParameterizedRowMapper<TissueSourceSite> tssRowMapper =
            new ParameterizedRowMapper<TissueSourceSite>() {
                public TissueSourceSite mapRow(ResultSet resultSet, int i) throws SQLException {
                    final TissueSourceSite cc = new TissueSourceSite();
                    // Column Index 1: code
                    cc.setCode(resultSet.getString(1));
                    // Column Index 2: site name
                    cc.setDefinition(resultSet.getString(2));
                    // Column Index 3: Study Name
                    cc.setStudyName(resultSet.getString(3));
                    // Column Index 4: BCR
                    cc.setBcr(resultSet.getString(4));
                    return cc;
                }
            };

    /**
     * row Mapper that maps each row of a resultset with a Bcr batches Object.
     */
    private static final ParameterizedRowMapper<BcrBatchCode> bbRowMapper =
            new ParameterizedRowMapper<BcrBatchCode>() {
                public BcrBatchCode mapRow(ResultSet resultSet, int i) throws SQLException {
                    final BcrBatchCode bb = new BcrBatchCode();
                    // Column Index 1: code
                    bb.setBcrBatch(resultSet.getString(1));
                    // Column Index 2: study code
                    bb.setStudyCode(resultSet.getString(2));
                    // Column Index 3: study name
                    bb.setStudyName(resultSet.getString(3));
                    // Column Index 4: bcr
                    bb.setBcr(resultSet.getString(4));
                    return bb;
                }
            };

}//End of Class
