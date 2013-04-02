/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.CodeTableQueries;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * Class to validate if the different codes (project, Tissue source site etc) exist in database
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Repository
public class CodeTableQueriesJDBCImpl implements CodeTableQueries {

    private SimpleJdbcTemplate jdbcTemplate;

    /**
     * Checks if the given project name exists in database
     * @param projectName project name
     * @return true if exists otherwise false
     */
    public boolean projectNameExists(final String projectName) {
        return codeExists(projectName, "project", "project_code", "project_code");
    }

    /**
     * Checks if the given tissue source site code exists in database
     * @param tssCode tissue source site code
     * @return true if exists otherwise false
     */
    public boolean tssCodeExists(final String tssCode) {
        return codeExists(tssCode, "tissue_source_site", "tss_code", "tss_code");
    }

    /**
     * Checks if a given sample type exists in database
     * @param sampleType sample type
     * @return true if exists otherwise false
     */
    public boolean sampleTypeExists(final String sampleType) {
        return codeExists(sampleType, "sample_type", "sample_type_code", "sample_type_code");
    }

    /**
     * Checks if the given portion analyte exists in database
     * @param portionAnalyte portion analyte
     * @return true if exists otherwise false
     */
    public boolean portionAnalyteExists(final String portionAnalyte) {
        return codeExists(portionAnalyte, "portion_analyte", "portion_analyte_code", "portion_analyte_code");
    }

    /**
     * Checks if the given bcr center id exists in database
     * @param bcrCenterId bcr center id
     * @return true if exists otherwise false
     */
    public boolean bcrCenterIdExists(final String bcrCenterId) {
        return codeExists(bcrCenterId, "center_to_bcr_center", "bcr_center_id", "bcr_center_id");
    }

    private String getCodeByName(final String searchString, final String tableName, final String colToSearchBy, final String colToReturn){
        String code;
        try {
            String select = "select " + colToReturn + " from " + tableName + " where " + colToSearchBy + " = ?";
            code = jdbcTemplate.queryForObject(select, String.class, searchString);
        }
        catch(DataAccessException e) {
            code = null;
        }
        return code;
    }

    boolean codeExists(final String searchString, final String tableName, final String colToSearchBy, final String colToReturn) {
        String code = getCodeByName(searchString, tableName, colToSearchBy, colToReturn);
        return code != null;
    }    

    public void setDataSource(final DataSource dataSource) {
        this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }
    
}
