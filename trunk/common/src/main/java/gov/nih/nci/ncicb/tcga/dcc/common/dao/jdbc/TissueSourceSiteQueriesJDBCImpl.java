/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.TissueSourceSite;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TissueSourceSiteQueries;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * jdbc implementation of the tissue source site dao bean
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TissueSourceSiteQueriesJDBCImpl extends SimpleJdbcDaoSupport implements TissueSourceSiteQueries {

    public static final String QUERY_TISSUE_SOURCE_SITE = "select tss_code, tss_definition " +
            "from tissue_source_site order by tss_definition, tss_code";
    public static final String QUERY_AGGREGATE_TISSUE_SOURCE_SITE = "select " +
            "RTRIM(XMLAGG(XMLELEMENT(e,tss_code || ',')  order by tss_code).EXTRACT('//text()'),',') tss_code, " +
            "tss_definition from tissue_source_site group by tss_definition order by tss_definition, tss_code";
    public static final String QUERY_DISEASE_FOR_TISSUE_SOURCE_SITE = "select distinct disease.disease_abbreviation " +
            "from tissue_source_site tss, tss_to_disease, disease " +
            "where tss.tss_code = tss_to_disease.tss_code " +
            "and tss_to_disease.disease_id = disease.disease_id " +
            "and tss.tss_code = ? order by disease.disease_abbreviation";

    private static final String QUERY_CONTROL_TSS_CODES = "select tss.tss_code " +
            "from tissue_source_site tss, tss_to_disease tss2d, disease d " +
            "where d.disease_id=tss2d.disease_id " +
            "and tss2d.tss_code=tss.tss_code " +
            "and d.disease_abbreviation='CNTL' order by tss.tss_code";

    private static final Integer TSS_CODE_COLUMN = 1;
    private static final Integer DEFINITION_COLUMN = 2;

    @Override
    public List<TissueSourceSite> getAllTissueSourceSites() {
        return getJdbcTemplate().query(QUERY_TISSUE_SOURCE_SITE,
                new ParameterizedRowMapper<TissueSourceSite>() {
                    public TissueSourceSite mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        TissueSourceSite tss = new TissueSourceSite();
                        tss.setTissueSourceSiteId(resultSet.getString(TSS_CODE_COLUMN));
                        tss.setName(resultSet.getString(DEFINITION_COLUMN));
                        return tss;
                    }
                });
    }

    @Override
    public List<TissueSourceSite> getAggregateTissueSourceSites() {
        return getJdbcTemplate().query(QUERY_AGGREGATE_TISSUE_SOURCE_SITE,
                new ParameterizedRowMapper<TissueSourceSite>() {
                    public TissueSourceSite mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        TissueSourceSite tss = new TissueSourceSite();
                        tss.setTissueSourceSiteId(resultSet.getString(TSS_CODE_COLUMN));
                        tss.setName(resultSet.getString(DEFINITION_COLUMN));
                        return tss;
                    }
                });
    }

    @Override
    public List<String> getDiseasesForTissueSourceSiteCode(final String tissueSourceSiteCode) {
        return getJdbcTemplate().query(QUERY_DISEASE_FOR_TISSUE_SOURCE_SITE,
                new Object[]{tissueSourceSiteCode}, new ParameterizedRowMapper<String>() {
            public String mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                return resultSet.getString(TSS_CODE_COLUMN);
            }
        });
    }

    /**
     * Gets the list of TSS codes that correspond to cell line controls.
     *
     * @return list of TSS codes for controls
     */
    @Override
    public List<String> getControlTssCodes() {
        return getJdbcTemplate().query(QUERY_CONTROL_TSS_CODES,
                new ParameterizedRowMapper<String>() {
                    @Override
                    public String mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                        return rs.getString(1);
                    }
                });
    }

}//End of Class
