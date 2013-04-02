/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.util.DiseaseNameLister;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.Disease;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Implementation of DAM disease queries.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DAMDiseaseQueriesJDBCImpl extends SimpleJdbcDaoSupport implements DAMDiseaseQueries {
     private static final String ALL_DISEASES_QUERY = "select disease_name, disease_abbreviation, active from disease order by disease_abbreviation";
    // get active diseases that actually have data
    private static final String ACTIVE_DISEASES_QUERY = "select distinct d.disease_name, d.disease_abbreviation, d.active from disease d, archive_info a, archive_type at where d.active=1 and d.disease_id = a.disease_id and a.is_latest=1 and a.deploy_status = 'Available' and a.archive_type_id = at.archive_type_id and at.archive_type != 'mage-tab' order by disease_abbreviation";
    private static final String DISEASE_QUERY = "select disease_name, disease_abbreviation, active from disease where disease_abbreviation=?";

    private DiseaseNameLister diseaseNameLister;
    private String controlDiseaseAbbreviation;
    private ParameterizedRowMapper<Disease> diseaseRowMapper;

    public DAMDiseaseQueriesJDBCImpl() {
        diseaseRowMapper = new ParameterizedRowMapper<Disease>() {
            public Disease mapRow(final ResultSet resultSet, final int i) throws SQLException {
                return new Disease(resultSet.getString("disease_name"),
                        resultSet.getString("disease_abbreviation"),
                        (resultSet.getInt("active") == 1));
            }
        };
    }

    /**
     * Gets the disease for this abbreviation.
     *
     * @param diseaseAbbreviation the disease abbreviation (such as 'GBM')
     * @return the disease, or null if not found
     */
    public Disease getDisease(final String diseaseAbbreviation) {
        try {
            DiseaseContextHolder.setDisease(diseaseAbbreviation);
            return getSimpleJdbcTemplate().queryForObject(DISEASE_QUERY, diseaseRowMapper, diseaseAbbreviation);
        } catch (EmptyResultDataAccessException emptyEx) {
            // disease not found
            return null;
        }
    }

    /**
     * Gets the active diseases.  Uses the configured schemaNameLister to get the list of all disease schemas, and then
     * queries for active diseases from each of them.  Active diseases are defined as diseases that have at least one non-mage-tab
     * archive associated with deploy status 'Available'.
     *
     * The CNTL (control) disease will be added if it is set to active regardless of whether there are associated archives.
     *
     * @return list of active diseases from the defined schemas
     */
    public List<Disease> getActiveDiseases() {
        return getDiseases(true);
    }

    /**
     * Gets all diseases.   Uses the configured schemaNameLister to get the list of all disease schemas, and then
     * queries for diseases from each of them.
     *
     * @return all diseases from defined schemas
     */
    public List<Disease> getDiseases() {
        return getDiseases(false);
    }

    private List<Disease> getDiseases(final boolean activeOnly) {

        Set<Disease> uniqueDiseases = new TreeSet<Disease>();
        Collection<Object> schemas = diseaseNameLister.getDiseaseNames();
        for (final Object schema : schemas) {
            DiseaseContextHolder.setDisease(schema.toString());
            uniqueDiseases.addAll(getSimpleJdbcTemplate().query(activeOnly ? ACTIVE_DISEASES_QUERY : ALL_DISEASES_QUERY,
                    diseaseRowMapper));
        }
        List<Disease> diseases = new ArrayList<Disease>(uniqueDiseases);
        if (controlDiseaseAbbreviation != null && schemas.contains(controlDiseaseAbbreviation)) {
            final Disease controlDisease = getDisease(controlDiseaseAbbreviation);
            if (controlDisease != null) {
                if (diseases.contains(controlDisease)) {
                    diseases.remove(controlDisease);
                }
                // put the control disease at the end
                if (!activeOnly || controlDisease.isActive()) {
                    diseases.add(controlDisease);
                }
            }
        }
        return diseases;
    }

    public void setDiseaseNameLister(final DiseaseNameLister diseaseNameLister) {
        this.diseaseNameLister = diseaseNameLister;
    }

    public void setControlDiseaseAbbreviation(final String controlDiseaseAbbreviation) {
        this.controlDiseaseAbbreviation = controlDiseaseAbbreviation;
    }
}
