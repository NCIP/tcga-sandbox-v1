/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.util.BigDecimalConversions;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelOne;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelOne;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataSetReducer;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.TumorNormalClassifierI;
import org.apache.log4j.Level;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: David Nassau Last Updated By: Jeyanthi Thangiah
 * <p/>
 * Gathers information about CGCC level 1 files from the Oracle disease specific database.
 */
public class DAMQueriesCGCCLevel1 extends DAMQueriesFilesystem implements DAMSubmittedSampleI {

    private static final String CGCC_CENTER_TYPE = "CGCC";

    private final ProcessLogger logger = new ProcessLogger();

    private static final String DATASET_SQL = "select distinct bb.built_barcode as barcode, " +
            "bb.sample, " +
            "ai.archive_id as archive_id, ai.center_id as center_id, " +
            "dt.data_type_id as platform_type_id, dt.sort_order, pi.platform_id as platform_id, ai.deploy_status as availability, " +
            "v.identifiable, ai.serial_index, ai.revision, ai.date_added, pi.platform_alias, 1 as data_level " +
            "from shipped_biospecimen_breakdown bb, archive_info ai, shipped_biospecimen_file bf, file_info f, file_to_archive f2a, data_type dt, " +
            "visibility v, disease d, platform pi, data_visibility dv " +
            "where f.level_number = 1 " +
            "and ai.is_latest = 1  " +
            "and d.disease_abbreviation = ? " +
            "and d.disease_id = ai.disease_id " +
            "and f.file_id = f2a.file_id " +
            "and f2a.archive_id = ai.archive_id " +
            "and pi.base_data_type_id = dt.data_type_id " +
            "and v.visibility_id=dv.visibility_id " +
            "and dv.data_type_id=dt.data_type_id " +
            "and dv.level_number = 1" +
            "and bf.file_id = f.file_id " +
            "and pi.platform_id = ai.platform_id " +
            "and bb.shipped_biospecimen_id = bf.shipped_biospecimen_id " +
            "and pi.center_type_code='" + CGCC_CENTER_TYPE + "' " +
            "and bb.is_viewable = 1 " +
            "and bb.is_control=?";

      private static final String FILE_INFO_QUERY = "select distinct fi.file_id, fi.file_name, fi.file_size " +
            "from shipped_biospecimen bb, platform p," +
            " archive_info ai, shipped_biospecimen_file bf, file_info fi, file_to_archive f2a " +
            " where fi.level_number = 1 " +
            " and bb.built_barcode =  ?" +
            " and p.platform_id=ai.platform_id and p.center_type_code='" + CGCC_CENTER_TYPE + "' " +
            " and ai.is_latest = 1 " +
            " and ai.platform_id = ? " +
            " and ai.center_id = ? " +
            " and bf.file_id = fi.file_id " +
            " and fi.file_id = f2a.file_id " +
            " and f2a.archive_id = ai.archive_id " +
            " and bb.shipped_biospecimen_id = bf.shipped_biospecimen_id";

    public static final String EXCLUDE_CENTER_TYPE_CODE =   "BCR";

    @Override
    protected String getFileInfoQuery() {
        return FILE_INFO_QUERY;
    }

    @Override
    protected String getDatasetSql() {
        return DATASET_SQL;
    }

    @Override
    protected DataSet getNewDataSetObject() {
        return new DataSetLevelOne();
    }

    @Override
    protected DataFile getNewFileInfoInstance() {
        return new DataFileLevelOne();
    }

    @Override
    protected boolean dataSetShouldBeIncluded(final DataSet dataset) {
        return dataset instanceof DataSetLevelOne;
    }

    @Override
    protected boolean dataFileShouldBeIncluded(final DataFile dataFile) {
        return dataFile instanceof DataFileLevelOne;
    }


    /* below is for DAMSubmittedSampleI interface */

    private static final String SAMPLE_SQL = "select distinct sample, ai.serial_index as batch "
              + "from shipped_biospecimen_breakdown bb, shipped_biospec_bcr_archive b2a, archive_info ai, center_to_bcr_center cbc "
              + "where  cbc.center_id = ? "
              + "and bb.bcr_center_id = cbc.bcr_center_id and bb.shipped_biospecimen_id = b2a.shipped_biospecimen_id "
              + "and b2a.archive_id = ai.archive_id "
              + "and ai.is_latest=1 and bb.is_viewable=1 and ai.deploy_status = 'Available' and bb.is_control=? "
              + "order by sample, batch";


    public Set<String> getSubmittedSampleIds( final String diseaseType ) throws DataAccessMatrixQueries.DAMQueriesException {
            return getSubmittedIds(0);
    }

    private Set<String> getSubmittedIds(final int isControlValue) throws DAMQueriesException {
        try {
            final Set<String> submittedIds = new HashSet<String>();
            final List<String> centerIds = getAllCenterIds();

            //query finds samples that have been submitted to a particular center
            final SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate( getDataSource() );
            for(final String centerId : centerIds) {
                final List<Map<String, Object>> resultSet = jdbc.queryForList(SAMPLE_SQL, Integer.valueOf(centerId), isControlValue );
                for(final Map<String, Object> record : resultSet) {
                    String sample = (String) record.get( SAMPLE );
                    int batch = BigDecimalConversions.bigDecimalToInteger((BigDecimal) record.get( "batch" ));
                    submittedIds.add(centerId + "|" + batch + "|" + sample);
                }
            }
            return submittedIds;
        }
        catch(DataAccessException e) {
            logger.logToLogger( Level.ERROR, ProcessLogger.stackTracePrinter( e ) );
            throw new DataAccessMatrixQueries.DAMQueriesException( e );
        }
    }

    /**
     * Returns all submitted control samples.
     *
     * @param diseaseTypes list of all diseases
     * @return submitted controls
     */
    @Override
    public Set<String> getSubmittedControls(final List<String> diseaseTypes) throws DAMQueriesException {
        final Set<String> submittedControls = new HashSet<String>();
        for (final String disease : diseaseTypes) {
            DiseaseContextHolder.setDisease(disease);
            submittedControls.addAll(getSubmittedIds(1));
        }

        return submittedControls;
    }

    private static final String ALL_CENTERS_QUERY = "select center_id from center where center_type_code != '"+EXCLUDE_CENTER_TYPE_CODE +"'order by center_id";

    private List<String> getAllCenterIds() {
        final SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate( getDataSource() );
        final List<Map<String, Object>> resultSet = jdbc.queryForList(ALL_CENTERS_QUERY);
        final List<String> ret = new ArrayList<String>( resultSet.size() );
        for(final Map<String, Object> record : resultSet) {
            ret.add( record.get( "center_id" ).toString() );
        }
        return ret;
    }

}
