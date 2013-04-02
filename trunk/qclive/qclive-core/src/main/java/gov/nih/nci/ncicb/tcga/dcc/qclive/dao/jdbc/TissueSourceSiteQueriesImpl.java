/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.TissueSourceSiteQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Queries for Tissue Source Site
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class TissueSourceSiteQueriesImpl extends BaseQueriesProcessor implements TissueSourceSiteQueries {

    /**
     * Gets the disease abbreviations for the given tissue source site.
     *
     * @param tissueSourceSiteCode the collection site code
     *
     * @return list of disease abbreviations
     */
    public List<String> getDiseasesForTissueSourceSiteCode(final String tissueSourceSiteCode) {
        String query = "Select distinct disease.disease_abbreviation " +
                "from tissue_source_site tss, tss_to_disease, disease " +
                "where tss.tss_code = tss_to_disease.tss_code " +
                "and tss_to_disease.disease_id = disease.disease_id " +
                "and tss.tss_code = ?";

        return getJdbcTemplate().query(query, new Object[]{tissueSourceSiteCode}, getTSSRowMapper());
    }

    private ParameterizedRowMapper<String> getTSSRowMapper() {
        return new ParameterizedRowMapper<String>() {
            public String mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                return resultSet.getString("disease_abbreviation");
            }
        };
    }

}
