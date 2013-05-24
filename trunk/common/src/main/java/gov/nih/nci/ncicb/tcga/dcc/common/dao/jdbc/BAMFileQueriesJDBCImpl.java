/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamAliquot;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamCGHubCenter;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamDatatype;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlFileRef;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlResult;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamXmlResultSet;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BAMFileQueries;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * JDBC Implementation for BAM File queries.
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */
@Repository
public class BAMFileQueriesJDBCImpl extends SimpleJdbcDaoSupport implements BAMFileQueries {
    protected final Log logger = LogFactory.getLog(getClass());
    private final String LIVE = "live";
    private final int BATCH_SIZE = 1000;
    private final String GET_LATEST_UPLOAD_DATE = "select max(dcc_received_date) from bam_file";
    private final String GET_BAMFILE_ID_FROM_ANALYSIS_ID = "select bam_file_id from bam_file " +
            "where analysis_id = lower(?)";
    private final String MERGE_BAMFILE = "MERGE INTO bam_file b USING " +
            "(SELECT LOWER(?) AS analysis_id FROM DUAL) chk " +
            "ON (b.analysis_id = chk.analysis_id) " +
            "WHEN NOT MATCHED THEN INSERT " +
            "(bam_file_id, bam_file_name, disease_id, center_id, bam_file_size, date_received,  " +
            "bam_datatype_id, analyte_code, analysis_id, dcc_received_date) " +
            "VALUES (?,?,?,?,?,?,?,?,LOWER(?),?) " +
            "WHEN MATCHED THEN UPDATE SET " +
            "bam_file_name = ?, " +
            "disease_id = ?, " +
            "center_id = ?, " +
            "bam_file_size = ?, " +
            "date_received = ?, " +
            "bam_datatype_id = ?, " +
            "analyte_code = ?, " +
            "dcc_received_date = ?";
    private final String MERGE_BAM_TO_ALIQUOT = "MERGE INTO shipped_biospecimen_bamfile sb USING " +
            "(SELECT ? AS bam_file_id FROM DUAL) d ON (sb.bam_file_id = d.bam_file_id) " +
            "WHEN NOT MATCHED THEN INSERT (shipped_biospecimen_id, bam_file_id) values (?, ?) " +
            "WHEN MATCHED THEN UPDATE SET shipped_biospecimen_id = ? ";
    private final String GET_BAM_DATATYPE = "select bam_datatype_id, bam_datatype, general_datatype " +
            "from bam_file_datatype order by bam_datatype";
    private final String GET_SHIPPED_BIOSPECIMEN = "select shipped_biospecimen_id, uuid from " +
            "shipped_biospecimen";
    private final String GET_CGHUB_CENTER = "select cghub_center_name, dcc_center_id from cghub_center order by " +
            "cghub_center_name";
    private final String DELETE_BAMFILE = "delete from bam_file where bam_file_id = ?";
    private final String DELETE_BAM_TO_ALIQUOT = "delete from shipped_biospecimen_bamfile where bam_file_id = ?";
    private final int FETCHSIZE = 1000;
    private final String BAMFILE_TABLE = "bam_file";
    private final String SHIPPED_BIOSPECIMEN_BAMFILE = "shipped_biospecimen_bamfile";
    private final String UNKNOWN = "Unknown";
    private List<BamDatatype> bamDatatypeList = new LinkedList<BamDatatype>();
    private List<BamAliquot> bamAliquotList = new LinkedList<BamAliquot>();
    private List<BamCGHubCenter> bamCGHubCenterList = new LinkedList<BamCGHubCenter>();

    /**
     * Get the latest dcc uploaded bam data date.
     *
     * @return date.
     */
    public Date getLatestUploadedDate() {
        return getSimpleJdbcTemplate().queryForObject(GET_LATEST_UPLOAD_DATE, Date.class);
    }

    /**
     * init Bam lookup queries.
     */
    @PostConstruct
    protected void initBamLookupQueries() {
        bamDatatypeList = getJdbcTemplate().query(GET_BAM_DATATYPE,
                new ParameterizedRowMapper<BamDatatype>() {
                    public BamDatatype mapRow(final ResultSet rs, final int i) throws SQLException {
                        final BamDatatype dt = new BamDatatype();
                        dt.setBamDatatypeId(rs.getLong(1));
                        dt.setBamDatatype(rs.getString(2));
                        dt.setGeneralDatatype(rs.getString(3));
                        return dt;
                    }
                }
        );
        bamAliquotList = getJdbcTemplate().query(GET_SHIPPED_BIOSPECIMEN,
                new ParameterizedRowMapper<BamAliquot>() {
                    public BamAliquot mapRow(final ResultSet rs, final int i) throws SQLException {
                        final BamAliquot a = new BamAliquot();
                        a.setAliquotId(rs.getLong(1));
                        a.setUuid(rs.getString(2));
                        return a;
                    }
                }
        );
        bamCGHubCenterList = getJdbcTemplate().query(GET_CGHUB_CENTER,
                new ParameterizedRowMapper<BamCGHubCenter>() {
                    public BamCGHubCenter mapRow(final ResultSet rs, final int i) throws SQLException {
                        final BamCGHubCenter c = new BamCGHubCenter();
                        c.setCGHubCenter(rs.getString(1));
                        c.setCenterId(rs.getLong(2));
                        return c;
                    }
                }
        );
    }

    /**
     * get bam file Id.
     *
     * @param analysisId
     * @return bam file Id
     */
    private Long getBamFileId(final String analysisId) {
        try {
            return getJdbcTemplate().queryForLong(GET_BAMFILE_ID_FROM_ANALYSIS_ID, analysisId);
        } catch (EmptyResultDataAccessException e) {
            return 0L;
        }
    }

    /**
     * persist bam xml results into the database.
     *
     * @param bamXmlResultSet
     */
    @Override
    @Transactional
    public void store(final BamXmlResultSet bamXmlResultSet) {
        checkLookupTables();
        logger.info("Loading data in DB ...");
        final List<Object[]> mergeParametersBAM = new ArrayList<Object[]>();
        final List<Object[]> mergeParametersBAMToAliquots = new ArrayList<Object[]>();
        getJdbcTemplate().setFetchSize(FETCHSIZE);
        final Date latestDate = bamXmlResultSet.getFetchDate();
        final List<BamXmlResult> bamXmlResultList = bamXmlResultSet.getBamXmlResultList();
        for (final BamXmlResult bamXmlResult : bamXmlResultList) {
            final String analysisId = bamXmlResult.getAnalysisId();
            Long bamFileId = getBamFileId(analysisId);
            if (bamFileId != 0L && !LIVE.equalsIgnoreCase(bamXmlResult.getState())) {
                getJdbcTemplate().update(DELETE_BAM_TO_ALIQUOT, new Object[]{bamFileId});
                getJdbcTemplate().update(DELETE_BAMFILE, new Object[]{bamFileId});
                logger.info("Deleted non live record for analysis_id '" + analysisId + "'.");
            } else {
                if (bamFileId == 0L) {
                    bamFileId = getBamSeqNextval();
                }
                final BamXmlFileRef bamFile = bamXmlResult.getBamXmlFileRefList().get(0);
                final Long bamDatatypeId = getDatatypeBAMId(bamXmlResult.getLibraryStrategy());
                final Long biospecimenId = getAliquotId(bamXmlResult.getAliquotUUID());
                mergeParametersBAM.add(new Object[]{analysisId, bamFileId,
                        bamFile.getFileName(), bamXmlResult.getDisease(), bamXmlResult.getCenter(),
                        bamFile.getFileSize(), bamXmlResult.getDateReceived(), bamDatatypeId,
                        bamXmlResult.getAnalyteCode(), analysisId, latestDate,
                        bamFile.getFileName(), bamXmlResult.getDisease(), bamXmlResult.getCenter(),
                        bamFile.getFileSize(), bamXmlResult.getDateReceived(), bamDatatypeId,
                        bamXmlResult.getAnalyteCode(), latestDate});
                if (biospecimenId != 0L) {
                    mergeParametersBAMToAliquots.add(new Object[]{bamFileId, biospecimenId, bamFileId, biospecimenId});
                }
                batchUpdate(MERGE_BAMFILE, BAMFILE_TABLE, mergeParametersBAM, false);
                batchUpdate(MERGE_BAM_TO_ALIQUOT, SHIPPED_BIOSPECIMEN_BAMFILE, mergeParametersBAMToAliquots, false);
            }
        }
        batchUpdate(MERGE_BAMFILE, BAMFILE_TABLE, mergeParametersBAM, true);
        batchUpdate(MERGE_BAM_TO_ALIQUOT, SHIPPED_BIOSPECIMEN_BAMFILE, mergeParametersBAMToAliquots, true);
    }

    /**
     * checks lookup tables are filed up, otherwise init them.
     */
    private void checkLookupTables() {
        if (bamDatatypeList.size() == 0 || bamAliquotList.size() == 0) {
            initBamLookupQueries();
        }
    }

    /**
     * get Bam sequence next value.
     *
     * @return next value
     */
    private Long getBamSeqNextval() {
        return getJdbcTemplate().queryForLong("select BAM_FILE_SEQ.nextval from dual");
    }

    /**
     * batchUpdate util method.
     *
     * @param query
     * @param tableName
     * @param data
     * @param flush
     */
    private void batchUpdate(final String query, final String tableName, final List<Object[]> data,
                             final Boolean flush) {
        if ((data.size() >= BATCH_SIZE || flush) && data.size() > 0) {
            getSimpleJdbcTemplate().batchUpdate(query, data);
            logger.info("Merged " + data.size() + " records in table '" + tableName + "' ...");
            data.clear();
        }
    }

    /**
     * get bam datatype Id from bam datatype.
     *
     * @param bamDatatype
     * @return bam datatype Id
     */
    public Long getDatatypeBAMId(final String bamDatatype) {
        checkLookupTables();
        BamDatatype unknown = new BamDatatype();
        for (BamDatatype dt : bamDatatypeList) {
            if (dt.getBamDatatype().equals(bamDatatype)) {
                return dt.getBamDatatypeId();
            }
            if (UNKNOWN.equals(dt.getBamDatatype())) {
                unknown = dt;
            }
        }
        return unknown.getBamDatatypeId();
    }

    /**
     * get aliquot biospecimen Id from aliquot uuid.
     *
     * @param uuid
     * @return biospecimen Id
     */
    public Long getAliquotId(final String uuid) {
        checkLookupTables();
        for (BamAliquot a : bamAliquotList) {
            if (a.getUuid().equals(uuid)) {
                return a.getAliquotId();
            }
        }
        return 0L;
    }

    /**
     * get DCC center id from Id from cghub center.
     *
     * @param cghubCenter
     * @return dcc center Id
     */
    public Long getDCCCenterId(final String cghubCenter) {
        checkLookupTables();
        for (BamCGHubCenter c : bamCGHubCenterList) {
            if (c.getCGHubCenter().equals(cghubCenter)) {
                return c.getCenterId();
            }
        }
        return 0L;
    }

}//End of Class
