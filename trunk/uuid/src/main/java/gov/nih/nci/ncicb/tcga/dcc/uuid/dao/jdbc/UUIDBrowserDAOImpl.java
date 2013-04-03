/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache.Cached;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.PortionAnalyte;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.TissueSourceSite;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDType;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PortionAnalyteQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.SampleTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TissueSourceSiteQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.uuid.dao.UUIDBrowserDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.SEPARATOR;
import static gov.nih.nci.ncicb.tcga.dcc.uuid.constants.UUIDBrowserConstants.QUERY_EXISTING_BARCODES;
import static gov.nih.nci.ncicb.tcga.dcc.uuid.constants.UUIDBrowserConstants.QUERY_UUID_BROWSER;
import static gov.nih.nci.ncicb.tcga.dcc.uuid.constants.UUIDBrowserConstants.QUERY_UUID_BROWSER_BY_BARCODE;
import static gov.nih.nci.ncicb.tcga.dcc.uuid.constants.UUIDBrowserConstants.QUERY_UUID_BROWSER_BY_BARCODES;
import static gov.nih.nci.ncicb.tcga.dcc.uuid.constants.UUIDBrowserConstants.QUERY_UUID_BROWSER_BY_UUID;
import static gov.nih.nci.ncicb.tcga.dcc.uuid.constants.UUIDBrowserConstants.QUERY_UUID_BROWSER_BY_UUIDS;
import static gov.nih.nci.ncicb.tcga.dcc.uuid.constants.UUIDBrowserConstants.REPLACE_BARCODE;
import static gov.nih.nci.ncicb.tcga.dcc.uuid.constants.UUIDBrowserConstants.REPLACE_UUID;

/**
 * DAO implementation of the UUID Browser
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Repository
public class UUIDBrowserDAOImpl implements UUIDBrowserDAO {

    protected static final Log logger = LogFactory.getLog(UUIDBrowserDAOImpl.class);
    private SimpleJdbcTemplate jdbcTemplate;
    private static List<Tumor> diseases;
    private static List<Center> centers;
    private static List<Center> bcrs;
    private static List<Platform> platforms;
    private static List<SampleType> sampleTypes;
    private static List<PortionAnalyte> portionAnalytes;
    private static List<UUIDType> uuidTypes;
    private static List<TissueSourceSite> tissueSourceSites;

    @Autowired
    private TumorQueries diseaseQueries;

    @Autowired
    private CenterQueries centerQueries;

    @Autowired
    private PlatformQueries platformQueries;

    @Autowired
    private SampleTypeQueries sampleTypeQueries;

    @Autowired
    private PortionAnalyteQueries portionAnalyteQueries;

    @Autowired
    private UUIDTypeQueries uuidTypeQueries;

    @Autowired
    private TissueSourceSiteQueries tissueSourceSiteQueries;


    @Override
    @Resource(name = "uuidDataSource")
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }

    @PostConstruct
    private void initQueriesList() {
        setDiseases(diseaseQueries.getDiseaseList());
        setCenters(centerQueries.getRealCenterList());
        setBcrs(centerQueries.getCenterList());
        setPlatforms(platformQueries.getPlatformList());
        setSampleTypes(sampleTypeQueries.getAllSampleTypes());
        setPortionAnalytes(portionAnalyteQueries.getAllPortionAnalytes());
        setUuidTypes(uuidTypeQueries.getAllUUIDTypes());
        setTissueSourceSites(tissueSourceSiteQueries.getAggregateTissueSourceSites());
    }

    @Override
    @Cached
    public List<BiospecimenMetaData> getUUIDRows() {
        return jdbcTemplate.getJdbcOperations().query(QUERY_UUID_BROWSER, uuidBrowserRowMapper);
    }

    @Override
    public List<String> getExistingBarcodes(final List<String> barcodes) {
        final List<String> existingBarcodes = new ArrayList<String>();
        int startIndex;
        int endIndex = 0;
        final int inClauseSize = getInClauseSize();
        while (endIndex < barcodes.size()) {
            startIndex = endIndex;
            endIndex = ((barcodes.size() - endIndex) > inClauseSize) ? (endIndex + inClauseSize) : barcodes.size();
            List<String> subList = barcodes.subList(startIndex, endIndex);
            final String query = QUERY_EXISTING_BARCODES.replaceAll(REPLACE_BARCODE, StringUtil.createPlaceHolderString(subList.size()));
            existingBarcodes.addAll(jdbcTemplate.getJdbcOperations().query(query, new ParameterizedRowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("barcode");
                }
            }, subList.toArray()));

        }

        return existingBarcodes;
    }

    @Override
    public List<BiospecimenMetaData> getBiospecimenMetaDataRowsFromBarcode(String barcode) {
        return jdbcTemplate.getJdbcOperations().query(QUERY_UUID_BROWSER_BY_BARCODE,
                uuidBrowserRowMapper, barcode);
    }

    @Override
    public List<BiospecimenMetaData> getBiospecimenMetaDataRowsFromUUID(String uuid) {
        return jdbcTemplate.getJdbcOperations().query(QUERY_UUID_BROWSER_BY_UUID,
                uuidBrowserRowMapper, uuid);
    }

    @Override
    public List<BiospecimenMetaData> getBiospecimenMetaDataRowsFromMultipleUUID(List<String> uuids) {
        final List<BiospecimenMetaData> uuidRows = new ArrayList<BiospecimenMetaData>();
        int startIndex;
        int endIndex = 0;
        final int inClauseSize = getInClauseSize();
        while (endIndex < uuids.size()) {
            startIndex = endIndex;
            endIndex = ((uuids.size() - endIndex) > inClauseSize) ? (endIndex + inClauseSize) : uuids.size();
            List<String> subList = uuids.subList(startIndex, endIndex);
            final String query = QUERY_UUID_BROWSER_BY_UUIDS.replaceAll(REPLACE_UUID,
                    StringUtil.createPlaceHolderString(subList.size(), StringUtil.CaseSensitivity.LOWER_CASE));
            uuidRows.addAll(jdbcTemplate.getJdbcOperations().query(query, uuidBrowserRowMapper, subList.toArray()));

        }
        return uuidRows;
    }

    @Override
    public List<BiospecimenMetaData> getBiospecimenMetaDataRowsFromMultipleBarcode(List<String> barcodes) {
        final List<BiospecimenMetaData> uuidRows = new ArrayList<BiospecimenMetaData>();
        int startIndex;
        int endIndex = 0;
        final int inClauseSize = getInClauseSize();
        while (endIndex < barcodes.size()) {
            startIndex = endIndex;
            endIndex = ((barcodes.size() - endIndex) > inClauseSize) ? (endIndex + inClauseSize) : barcodes.size();
            List<String> subList = barcodes.subList(startIndex, endIndex);
            final String query = QUERY_UUID_BROWSER_BY_BARCODES.replaceAll(REPLACE_BARCODE,
                    StringUtil.createPlaceHolderString(subList.size(), StringUtil.CaseSensitivity.UPPER_CASE));
            uuidRows.addAll(jdbcTemplate.getJdbcOperations().query(query, uuidBrowserRowMapper, subList.toArray()));

        }
        return uuidRows;
    }

    private static final ParameterizedRowMapper<BiospecimenMetaData> uuidBrowserRowMapper =
            new ParameterizedRowMapper<BiospecimenMetaData>() {
                public BiospecimenMetaData mapRow(ResultSet resultSet, int i) throws SQLException {
                    final BiospecimenMetaData uuidBrowser = new BiospecimenMetaData();
                    uuidBrowser.setDisease(resultSet.getString("disease_abbreviation"));
                    uuidBrowser.setUuid(resultSet.getString("uuid"));
                    uuidBrowser.setParentUUID(resultSet.getString("parent_uuid"));
                    uuidBrowser.setUuidType(getUUIDType(resultSet.getInt("item_type_id")));
                    uuidBrowser.setTissueSourceSite(resultSet.getString("tss_code"));
                    uuidBrowser.setBcr(getBCR(resultSet.getInt("center_id_bcr")));
                    uuidBrowser.setBatch(resultSet.getString("batch_number"));
                    uuidBrowser.setBarcode(resultSet.getString("barcode"));
                    uuidBrowser.setParticipantId(resultSet.getString("participant_number"));
                    uuidBrowser.setSampleType(getSampleType(resultSet.getString("sample_type_code")));
                    uuidBrowser.setVialId(resultSet.getString("sample_sequence"));
                    uuidBrowser.setPortionId(resultSet.getString("portion_sequence"));
                    uuidBrowser.setAnalyteType(getPortionAnalyte(resultSet.getString("portion_analyte_code")));
                    uuidBrowser.setPlateId(resultSet.getString("plate_id"));
                    uuidBrowser.setReceivingCenter(getReceivingCenter(resultSet.getInt("receiving_center_id")));
                    uuidBrowser.setSlide(resultSet.getString("slide"));
                    uuidBrowser.setSlideLayer(resultSet.getString("slide_layer"));
                    uuidBrowser.setCreateDate(resultSet.getDate("create_date"));
                    uuidBrowser.setUpdateDate(resultSet.getDate("update_date"));
                    uuidBrowser.setPlatform(getPlatform(resultSet.getString("platforms")));
                    uuidBrowser.setRedacted(resultSet.getInt("is_redacted") == 1);
                    uuidBrowser.setShipped(resultSet.getInt("is_shipped") == 1);
                    uuidBrowser.setShippedDate(resultSet.getDate("shipped_date"));
                    uuidBrowser.setCenterCode(resultSet.getString("center_code"));
                    return uuidBrowser;
                }
            };


    private static String getUUIDType(Integer uuidTypeId) {
        for (final UUIDType uuidType : uuidTypes) {
            if (uuidTypeId != null && uuidTypeId.equals(uuidType.getUuidTypeId())) {
                return uuidType.getUuidType();
            }
        }
        return "";
    }

    private static String getReceivingCenter(Integer centerId) {
        for (final Center center : centers) {
            if (centerId != null && centerId.equals(center.getCenterId())) {
                return center.getCenterDisplayText();
            }
        }
        return "";
    }

    private static String getBCR(Integer centerId) {
        for (final Center bcr : bcrs) {
            if (centerId != null && centerId.equals(bcr.getCenterId())) {
                return bcr.getCenterDisplayText();
            }
        }
        return "";
    }

    private static String getPlatform(String platformIds) {
        String[] values = null;
        String res = "";
        if (platformIds != null) {
            values = platformIds.split(SEPARATOR);
        }
        if (values == null) {
            return "";
        }
        for (final Platform platform : platforms) {
            for (int i = 0; i < values.length; i++) {
                if (values[i] != null && values[i].equals(Integer.toString(platform.getPlatformId()))) {
                    res += platform.getPlatformAlias() + SEPARATOR;
                }
            }
        }
        return res == "" ? res : res.substring(0, res.length() - 1);
    }

    private static String getSampleType(String sampleTypeCode) {
        for (final SampleType sampleType : sampleTypes) {
            if (sampleTypeCode != null && sampleTypeCode.equals(sampleType.getSampleTypeCode())) {
                return sampleType.getDefinition();
            }
        }
        return "";
    }

    private static String getPortionAnalyte(String portionAnalyteCode) {
        for (final PortionAnalyte portionAnalyte : portionAnalytes) {
            if (portionAnalyteCode != null && portionAnalyteCode.equals(portionAnalyte.getPortionAnalyteCode())) {
                return portionAnalyte.getDefinition();
            }
        }
        return "";
    }

    @Override
    @Cached
    public List<Tumor> getDiseases() {
        return diseaseQueries.getDiseaseList();
    }

    public void setDiseases(List<Tumor> diseases) {
        this.diseases = diseases;
    }

    @Override
    @Cached
    public List<Center> getCenters() {
        return centerQueries.getCenterList();
    }

    public void setCenters(final List<Center> centers) {
        this.centers = centers;
    }

    public void setBcrs(final List<Center> bcrs) {
        this.bcrs = bcrs;
    }

    @Override
    @Cached
    public List<Center> getCentersWithBCRCode() {
        return centerQueries.getRealCenterList();
    }

    @Override
    @Cached
    public List<Platform> getPlatforms() {
        return platformQueries.getPlatformList();
    }

    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }

    @Override
    @Cached
    public List<SampleType> getSampleTypes() {
        return sampleTypeQueries.getAllSampleTypes();
    }

    public void setSampleTypes(List<SampleType> sampleTypes) {
        this.sampleTypes = sampleTypes;
    }

    @Override
    @Cached
    public List<PortionAnalyte> getPortionAnalytes() {
        return portionAnalyteQueries.getAllPortionAnalytes();
    }

    public void setPortionAnalytes(List<PortionAnalyte> portionAnalytes) {
        this.portionAnalytes = portionAnalytes;
    }

    @Override
    @Cached
    public List<UUIDType> getUuidTypes() {
        return uuidTypeQueries.getAllUUIDTypes();
    }

    public void setUuidTypes(List<UUIDType> uuidTypes) {
        this.uuidTypes = uuidTypes;
    }

    @Override
    @Cached
    public List<TissueSourceSite> getTissueSourceSites() {
        return tissueSourceSiteQueries.getAggregateTissueSourceSites();
    }

    public void setTissueSourceSites(List<TissueSourceSite> tissueSourceSites) {
        this.tissueSourceSites = tissueSourceSites;
    }

    public int getInClauseSize() {
        return ConstantValues.WS_BATCH_SIZE;
    }

}//End of Class

