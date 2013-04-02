/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Robert S. Sfeir
 *         Last updated by: $Author$
 * @version $Rev$
 */
// TODO: This class handles disease now. There is already a Disease bean class used in DAM. These two classes
// should be combined and all the related classess/methods/objects should be renamed to disease instead of tumor 
// for better clarity.

public class TumorQueriesJDBCImpl extends BaseQueriesProcessor implements TumorQueries {
    // use getter, since it initializes it if it's not yet set.
    // note: would be better to upgrade BaseQueriesProcessor to extend SimpleJdbcDaoSupport!
    private SimpleJdbcTemplate simpleJdbcTemplate;

    public Integer getTumorIdByName(final String diseaseName) {
        return getObjectIdByNameAsInteger(diseaseName, "disease", "disease_abbreviation", "disease_id");
    }

    public Tumor getTumorForName(final String diseaseName) {
        final String select = "select disease_id, disease_abbreviation, disease_name from disease where disease_abbreviation=?";
        final List<Tumor> results = getSimpleJdbcTemplate().getJdbcOperations().query(select,
                new TumorMapper(), diseaseName);
        return (results == null || results.size() == 0 ? null : results.get(0));
    }

    public Tumor getTumorForId(final Integer diseaseId) {
        final String select = "select disease_id, disease_abbreviation, disease_name from disease where disease_id=?";
        final List<Tumor> results = getSimpleJdbcTemplate().getJdbcOperations().query(select,
                new TumorMapper(), diseaseId);
        return (results == null || results.size() == 0 ? null : results.get(0));
    }

    public Collection<Map<String, Object>> getAllTumors() {
        return getAllObjectsAsList("disease", "disease_name");
    }

    public String getTumorNameById(final Integer diseaseId) {
        final String select = "select disease_abbreviation from disease where disease_id = ?";
        String diseaseName = null;
        try {
            diseaseName = getSimpleJdbcTemplate().queryForObject(select, String.class, diseaseId);
        } catch (DataAccessException e) {
            // this means not found, so return null
        }
        return diseaseName;
    }

    public List<Integer> getTissueIdsForTumor(final String diseaseAbbreviation) {
        final List<Integer> tissueIds = new ArrayList<Integer>();
        final String query = "select tissue_id from tissue_to_disease tt, disease ti where tt.disease_id=ti.disease_id and ti.disease_abbreviation=?";
        getJdbcTemplate().query(query, new Object[]{diseaseAbbreviation}, new RowCallbackHandler() {
            public void processRow(final ResultSet resultSet) throws SQLException {
                tissueIds.add(resultSet.getInt("tissue_id"));
            }
        });
        return tissueIds;
    }

    @Override
    public List<Tumor> getDiseaseList() {
        final String select = "select disease_id, disease_abbreviation, disease_name from disease order by disease_abbreviation";
        return getSimpleJdbcTemplate().getJdbcOperations().query(select, new TumorMapper());
    }

    public SimpleJdbcTemplate getSimpleJdbcTemplate() {
        if (simpleJdbcTemplate == null) {
            simpleJdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        }
        return simpleJdbcTemplate;
    }

    static class TumorMapper implements ParameterizedRowMapper<Tumor> {
        public Tumor mapRow(final ResultSet resultSet, final int i) throws SQLException {
            final Tumor tumor = new Tumor();
            tumor.setTumorId(resultSet.getInt("disease_id"));
            tumor.setTumorName(resultSet.getString("disease_abbreviation"));
            tumor.setTumorDescription(resultSet.getString("disease_name"));
            return tumor;
        }
    }
}
