package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimenElement;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * DAO Implementation for ShippedBiospecimenQueries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ShippedBiospecimenQueriesJDBCImpl extends SimpleJdbcDaoSupport implements ShippedBiospecimenQueries {

	private static final String SHIPPED_PORTION = "Shipping Portion";
	private static final String ALIQUOT = "Aliquot";
	
    public static final int DEFAULT_FETCHSIZE = 1000;
    private static final String SHIPPED_BIOSPECIMEN_SEQ = "biospecimen_barcode_seq";
    private static final String SHIPPED_BIOSPECIMEN_ELEMENT_SEQ = "shipped_biospec_element_seq";

    private static final String GET_SHIPPED_ELEMENTS_TYPE_QUERY = "select element_type_id, element_type_name from" +
            " shipped_element_type ";

    private static final String GET_SHIPPED_ITEM_ID_QUERY = "select shipped_item_type_id from" +
            " shipped_item_type " +
            " where" +
            " shipped_item_type = ?";

    private static final String GET_SHIPPED_BIOSPECIMEN_ID_QUERY = "select shipped_biospecimen_id from" +
            " shipped_biospecimen " +
            " where" +
            " uuid=lower(?)";

    private static final String GET_SHIPPED_BIOSPECIMEN_ELEMENT_ID_QUERY = "select shipped_biospecimen_element_id from" +
            " shipped_biospecimen_element " +
            " where" +
            " shipped_biospecimen_id = ? AND" +
            " element_type_id = ?";

    private static final String SHIPPED_BIOSPECIMEN_INSERT_QUERY = "INSERT into shipped_biospecimen(shipped_biospecimen_id," +
            " uuid," +
            " shipped_item_type_id," +
            " built_barcode," +
            " project_code," +
            " tss_code," +
            " bcr_center_id," +
            " participant_code," +
            " is_viewable," +
            " is_redacted," +
            " shipped_date," +
            " batch_id)" +
            " values(?,?,?,?,?,?,?,?,?,?,?,?)";


    private static final String SHIPPED_BIOSPECIMEN_UPDATE_QUERY = " UPDATE shipped_biospecimen set shipped_item_type_id=?," +
            " built_barcode=?," +
            " project_code=?," +
            " tss_code=?," +
            " bcr_center_id=?," +
            " participant_code=?," +
            " shipped_date=?, " +
            " batch_id=? " +
            " WHERE uuid=lower(?)";

    private static final String SHIPPED_BIOSPECIMEN_ELEMENT_INSERT_QUERY = "INSERT into shipped_biospecimen_element" +
            " (shipped_biospecimen_element_id," +
            " shipped_biospecimen_id," +
            " element_type_id," +
            " element_value)" +
            " values(?,?,?,?)";


    private static final String SHIPPED_BIOSPECIMEN_ELEMENT_UPDATE_QUERY = "UPDATE shipped_biospecimen_element" +
            " set element_value=?" +
            " WHERE " +
            " shipped_biospecimen_id = ? AND" +
            " element_type_id =?";

    private static final String GET_BIOSPECIMEN_ID_FOR_UUID_QUERY = "select shipped_biospecimen_id from shipped_biospecimen where uuid=lower(?)";

    private static final String REPLACE_ME = "REPLACE_ME";
    private static final String GET_BIOSPECIMEN_IDS_FOR_UUIDS_QUERY = "select shipped_biospecimen_id from shipped_biospecimen " +
            "where uuid in(" + REPLACE_ME + ")";

    private static final String CHECK_SHIPPED_BIOSPECIMEN_FILE_EXISTS_QUERY = "select count(*) from shipped_biospecimen_file where shipped_biospecimen_id = ? and file_id=?";

    private static final String ADD_FILE_RELATIONSHIP_INSERT = "merge into shipped_biospecimen_file " +
            "using dual on (shipped_biospecimen_id=? and file_id=?) " +
            "when not matched then" +
            " insert (shipped_biospecimen_id, file_id) values(?, ?)";

    private static final String GET_BIOSPECIMEN_IDS_FOR_FILE = "select shipped_biospecimen_id from shipped_biospecimen_file where file_id=?";

    private static final String GET_BIOSPECIMEN_ID_FOR_SHIPPED_PORTION_UUID_QUERY = " select count(a.shipped_biospecimen_id) as uuidCount " +
            " from shipped_biospecimen a, " +
            " shipped_item_type b where a.shipped_item_type_id = b.shipped_item_type_id " +
            " and b.shipped_item_type = '" + ShippedBiospecimen.SHIPPED_ITEM_NAME_PORTION + "' and a.uuid=lower(?) ";

    private static final String MERGE_SHIPPED_BIOSPECIMEN_ARCHIVE = "merge into shipped_biospec_bcr_archive using dual on (shipped_biospecimen_id=? and archive_id=?) " +
            "when not matched then insert (shipped_biospecimen_id, archive_id) values(?, ?)";

    public static final String SQL_DISEASE_UUID = "select disease_abbreviation " +
            "from uuid u, barcode_history bh, disease d " +
            "where u.latest_barcode_id = bh.barcode_id " +
            "and u.UUID = ? " +
            "and bh.disease_id = d.disease_id";

    
    @Override
    public Long getShippedBiospecimenIdForUUID(String uuid) {
        try {
            return getSimpleJdbcTemplate().queryForLong(GET_BIOSPECIMEN_ID_FOR_UUID_QUERY, uuid);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    @Override
    public void addFileRelationship(Long biospecimenId, Long fileId) {
        getSimpleJdbcTemplate().update(ADD_FILE_RELATIONSHIP_INSERT, biospecimenId, fileId, biospecimenId, fileId);
    }

    @Override
    public Boolean isShippedBiospecimenShippedPortionUUIDValid(String uuid) {
        Boolean isValid = false;
        Integer uudCount = getSimpleJdbcTemplate().queryForInt(GET_BIOSPECIMEN_ID_FOR_SHIPPED_PORTION_UUID_QUERY, uuid);
        if (uudCount > 0) {
            isValid = true;
        }
        return isValid;
    }

    @Override
    public void addFileRelationships(final List<Long> biospecimenIds, final Long fileId) {
        List<Object[]> batchParams = new ArrayList<Object[]>();

        for (final Long biospecimenId : biospecimenIds) {
            batchParams.add(new Object[]{biospecimenId, fileId, biospecimenId, fileId});
        }
        getSimpleJdbcTemplate().batchUpdate(ADD_FILE_RELATIONSHIP_INSERT, batchParams);
    }

    @Override
    public List<String> getRedactedParticipants(Collection<String> participantCodeList) {
        String redactedParticipantsQuery = "select DISTINCT (PARTICIPANT_CODE) " +
                "from SHIPPED_BIOSPECIMEN " +
                "where IS_REDACTED = 1 and PARTICIPANT_CODE in (" + StringUtil.createPlaceHolderString(participantCodeList.size()) + ")";

        Object[] participantQueryParams = new Object[]{participantCodeList.toArray()};
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());

        return sjdbc.query(redactedParticipantsQuery, new ParameterizedRowMapper<String>() {
            public String mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                return resultSet.getString(1);
            }
        }, participantQueryParams);
    }

    @Override
    public void addArchiveRelationship(final Long biospecimenId, final Long archiveId) {
        getSimpleJdbcTemplate().update(MERGE_SHIPPED_BIOSPECIMEN_ARCHIVE, biospecimenId, archiveId, biospecimenId, archiveId);
    }

    @Override
    public void addArchiveRelationships(final List<Long> biospecimenIds, final Long archiveId) {
        List<Object[]> batchParams = new ArrayList<Object[]>();

        for (final Long biospecimenId : biospecimenIds) {
            batchParams.add(new Object[]{biospecimenId, archiveId, biospecimenId, archiveId});
        }
        getSimpleJdbcTemplate().batchUpdate(MERGE_SHIPPED_BIOSPECIMEN_ARCHIVE, batchParams);
    }

    private List<Long> getBiospecimenIdsForFile(final Long fileId) {
        return getSimpleJdbcTemplate().query(GET_BIOSPECIMEN_IDS_FOR_FILE, new ParameterizedRowMapper<Long>() {
            @Override
            public Long mapRow(final ResultSet resultSet, final int i) throws SQLException {
                return resultSet.getLong(1);
            }
        }, fileId);
    }


    @Override
    public void addShippedBiospecimens(final List<ShippedBiospecimen> shippedBiospecimens) {
        // 2nd arg null = tell it to look up the biospecimen type...
        addShippedBiospecimens(shippedBiospecimens, null);
    }

    @Override
    public void addShippedBiospecimenElements(final List<ShippedBiospecimenElement> shippedBiospecimenElements) {
        final List<Object[]> insertValueList = new ArrayList<Object[]>();
        final List<Object[]> updateValueList = new ArrayList<Object[]>();

        Map<String, Integer> shippedElementTypes = null;

        getJdbcTemplate().setFetchSize(DEFAULT_FETCHSIZE);


        for (final ShippedBiospecimenElement shippedBiospecimenElement : shippedBiospecimenElements) {

            // look up the element type ID if not known yet
            if (shippedBiospecimenElement.getElementTypeId() == null) {
                // if this is being called on disease schema, cannot look this up, so assume type id already set...
                // if the IDs are not set before calling the instance pointing to disease, will throw exception
                if (shippedElementTypes == null) {
                    shippedElementTypes = getShippedElementsType();
                }
                Integer elementTypeId = shippedElementTypes.get(shippedBiospecimenElement.getElementName());
                if (elementTypeId != null) {
                    shippedBiospecimenElement.setElementTypeId(elementTypeId);
                } else {
                    throw new IllegalArgumentException(shippedBiospecimenElement.getElementName() + " is not a known meta-data element name");
                }
            }

            int i = 0;
            Long shippedBiospecimenElementId;

            // if shippedBiospecimenId/elementtype id doesn't exist do insert
            if ((shippedBiospecimenElementId = getShippedBiospecimenElementId(
                    shippedBiospecimenElement.getShippedBiospecimenId(),
                    shippedBiospecimenElement.getElementTypeId())) == null) {
                Object[] data = new Object[4];
                if (shippedBiospecimenElement.getShippedBiospecimenElementId() == null ||
                        shippedBiospecimenElement.getShippedBiospecimenElementId() < 0) {
                    shippedBiospecimenElement.setShippedBiospecimenElementId(getNextSequenceNumber(SHIPPED_BIOSPECIMEN_ELEMENT_SEQ));
                }
                data[i++] = shippedBiospecimenElement.getShippedBiospecimenElementId();
                data[i++] = shippedBiospecimenElement.getShippedBiospecimenId();
                data[i++] = shippedBiospecimenElement.getElementTypeId();
                data[i++] = shippedBiospecimenElement.getElementValue();
                insertValueList.add(data);

            } else {//update
                Object[] data = new Object[3];
                shippedBiospecimenElement.setShippedBiospecimenElementId(shippedBiospecimenElementId);
                // update values
                data[i++] = shippedBiospecimenElement.getElementValue();
                data[i++] = shippedBiospecimenElement.getShippedBiospecimenId();
                data[i++] = shippedBiospecimenElement.getElementTypeId();

                updateValueList.add(data);
            }
            batchUpdate(SHIPPED_BIOSPECIMEN_ELEMENT_INSERT_QUERY, insertValueList, false);
            batchUpdate(SHIPPED_BIOSPECIMEN_ELEMENT_UPDATE_QUERY, updateValueList, false);
        }
        batchUpdate(SHIPPED_BIOSPECIMEN_ELEMENT_INSERT_QUERY, insertValueList, true);
        batchUpdate(SHIPPED_BIOSPECIMEN_ELEMENT_UPDATE_QUERY, updateValueList, true);

    }

    public Map<String, Integer> getShippedElementsType() {
        final Map<String, Integer> shippedElementTypeIdByName = new HashMap<String, Integer>();
        getSimpleJdbcTemplate().getJdbcOperations().query(GET_SHIPPED_ELEMENTS_TYPE_QUERY, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                shippedElementTypeIdByName.put(resultSet.getString("element_type_name"), resultSet.getInt("element_type_id"));
            }
        });
        return shippedElementTypeIdByName;
    }

    public void addShippedBiospecimens(final List<ShippedBiospecimen> shippedBiospecimens, Integer shippedItemId) {

        final List<Object[]> insertValueList = new ArrayList<Object[]>();
        final List<Object[]> updateValueList = new ArrayList<Object[]>();

        getJdbcTemplate().setFetchSize(DEFAULT_FETCHSIZE);

        for (final ShippedBiospecimen shippedBiospecimen : shippedBiospecimens) {
            int i = 0;
            Long shippedBiospecimenId;

            if (shippedItemId == null) {
                shippedItemId = shippedBiospecimen.getShippedBiospecimenTypeId();
                // if not set in object, look it up
                if (shippedItemId == null) {
                    shippedItemId = getShippedItemId(shippedBiospecimen.getShippedBiospecimenType());
                    if (shippedItemId == null) {
                        throw new IllegalArgumentException("Biospecimen type '" + shippedBiospecimen.getShippedBiospecimenType() + "' is not a supported shipped type");
                    }
                }
            }
            shippedBiospecimen.setShippedBiospecimenTypeId(shippedItemId);

            // if uuid doesn't exist do insert
            if ((shippedBiospecimenId = getShippedBiospecimenId(shippedBiospecimen.getUuid())) == null) {
                Object[] data = new Object[12];
                if (shippedBiospecimen.getShippedBiospecimenId() == null ||
                        shippedBiospecimen.getShippedBiospecimenId() < 0) {
                    shippedBiospecimen.setShippedBiospecimenId(getNextSequenceNumber(SHIPPED_BIOSPECIMEN_SEQ));
                }
                data[i++] = shippedBiospecimen.getShippedBiospecimenId();
                data[i++] = shippedBiospecimen.getUuid();
                data[i++] = shippedItemId;
                data[i++] = shippedBiospecimen.getBarcode();
                data[i++] = shippedBiospecimen.getProjectCode();
                data[i++] = shippedBiospecimen.getTssCode();
                data[i++] = shippedBiospecimen.getBcrCenterId();
                data[i++] = shippedBiospecimen.getParticipantCode();
                data[i++] = shippedBiospecimen.isViewable() ? 1 : 0;
                data[i++] = shippedBiospecimen.isRedacted() ? 1 : 0;
                data[i++] = shippedBiospecimen.getShippedDate();
                data[i++] = shippedBiospecimen.getBatchNumber();
                insertValueList.add(data);

            } else { // if uuid exists update
                Object[] data = new Object[9];
                shippedBiospecimen.setShippedBiospecimenId(shippedBiospecimenId);
                data[i++] = shippedItemId;
                data[i++] = shippedBiospecimen.getBarcode();
                data[i++] = shippedBiospecimen.getProjectCode();
                data[i++] = shippedBiospecimen.getTssCode();
                data[i++] = shippedBiospecimen.getBcrCenterId();
                data[i++] = shippedBiospecimen.getParticipantCode();
                data[i++] = shippedBiospecimen.getShippedDate();
                data[i++] = shippedBiospecimen.getBatchNumber();

                // this has to be last
                data[i++] = shippedBiospecimen.getUuid();

                updateValueList.add(data);

            }
            batchUpdate(SHIPPED_BIOSPECIMEN_INSERT_QUERY, insertValueList, false);
            batchUpdate(SHIPPED_BIOSPECIMEN_UPDATE_QUERY, updateValueList, false);
        }
        batchUpdate(SHIPPED_BIOSPECIMEN_INSERT_QUERY, insertValueList, true);
        batchUpdate(SHIPPED_BIOSPECIMEN_UPDATE_QUERY, updateValueList, true);


    }

    public Integer getShippedItemId(final String shippedItemType) {
        try {
            return getSimpleJdbcTemplate().queryForInt(GET_SHIPPED_ITEM_ID_QUERY, shippedItemType);
        } catch (IncorrectResultSizeDataAccessException e) {
            // means no such id
            return null;
        }
    }

    public Long getShippedBiospecimenId(final String UUID) {
        try {
            return getSimpleJdbcTemplate().queryForLong(GET_SHIPPED_BIOSPECIMEN_ID_QUERY, UUID);
        } catch (IncorrectResultSizeDataAccessException exp) {
            return null;
        }
    }

    /**
     * Get a list of Longs corresponding to UUIDs passed in.
     *
     * @param uuids list of uuids to lookup in shipped biospecimen
     * @return shipped biospecimen ids for uuids
     */
    @Override
    public List<Long> getShippedBiospecimenIds(final List<String> uuids) {
        final List<String> lowerUuids = new ArrayList<String>();
        for (final String uuid : uuids) {
            lowerUuids.add(uuid.toLowerCase());
        }
        String selectQuery = GET_BIOSPECIMEN_IDS_FOR_UUIDS_QUERY.replace(REPLACE_ME, StringUtil.createPlaceHolderString(lowerUuids.size()));

        return getJdbcTemplate().query(selectQuery, lowerUuids.toArray(),
                new ParameterizedRowMapper<Long>() {
                    @Override
                    public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                        return rs.getLong("shipped_biospecimen_id");
                    }
                }
        );
    }

    public Long getShippedBiospecimenElementId(final Long shippedBiospecimenId, final Integer elementTypeId) {
        try {
            return getSimpleJdbcTemplate().queryForLong(GET_SHIPPED_BIOSPECIMEN_ELEMENT_ID_QUERY, shippedBiospecimenId, elementTypeId);
        } catch (IncorrectResultSizeDataAccessException exp) {
            return null;
        }
    }

    private Long getNextSequenceNumber(final String sequenceName) {
        return getSimpleJdbcTemplate().queryForLong("select " + sequenceName + ".nextval from dual");
    }


    private void batchUpdate(final String query,
                             final List<Object[]> data,
                             final Boolean flush) {
        if( (data.size() >= ConstantValues.BATCH_SIZE || flush)
            && data.size() > 0) {
            getSimpleJdbcTemplate().batchUpdate(query, data);
            data.clear();
        }
    }
    
    @Override
	public MetaDataBean retrieveUUIDMetadata(String UUID){
		MetaDataBean uuidMetadata = null;

		if (StringUtils.isNotEmpty(UUID)){
			String shippedBiospecQuery = 
					" select project_code,tss_code,participant_code," +
					" shipped_biospecimen_id,shipped_item_type," +
					" bcr_center_id,UUID" +
					" from shipped_biospecimen sb, shipped_item_type si" +
					" where sb.shipped_item_type_id = si.shipped_item_type_id" +
					" and UUID = ?"; 
			
			
			String shippedBiospecElementQueryAliquot = 				
					" select sample_type_code,sample_sequence,portion_sequence,analyte_code,plate_id  from " +
					" (select element_value as sample_type_code from shipped_biospecimen_element where shipped_biospecimen_id = ? and element_type_id = 1), "+
					" (select element_value as sample_sequence from shipped_biospecimen_element where shipped_biospecimen_id = ? and element_type_id = 2)," +
					" (select element_value as portion_sequence from shipped_biospecimen_element where shipped_biospecimen_id = ? and element_type_id = 3)," + 
					" (select element_value as analyte_code from shipped_biospecimen_element where shipped_biospecimen_id = ? and element_type_id = 4), "+
					" (select element_value as plate_id from shipped_biospecimen_element where shipped_biospecimen_id = ? and element_type_id = 5)";
					
			
			List<MetaDataBean> metadataList = getSimpleJdbcTemplate().getJdbcOperations().query(shippedBiospecQuery,
					new ParameterizedRowMapper<MetaDataBean>() {
	            @Override
	            public MetaDataBean mapRow(ResultSet rs, int rowNum) throws SQLException {
	               MetaDataBean valueBean = new MetaDataBean();
	               valueBean.setProjectCode(rs.getString("project_code"));
	               valueBean.setTssCode(rs.getString("tss_code"));
	               valueBean.setParticipantCode(rs.getString("participant_code"));
	               valueBean.setReceivingCenterId(rs.getString("bcr_center_id"));
	               valueBean.setShippedBiospecId(rs.getLong("shipped_biospecimen_id"));  
	               valueBean.setUUID(rs.getString("UUID"));
	               if (ALIQUOT.equalsIgnoreCase(rs.getString ("shipped_item_type"))){
	            	   valueBean.setAliquot(true);
	               }else if (SHIPPED_PORTION.equalsIgnoreCase(rs.getString ("shipped_item_type"))){
	            	   valueBean.setShippedPortion(true);
	               }
	               return valueBean;
	            }
	        } , UUID.toLowerCase());
									

            if (metadataList.size() < 1) {
                return null;
            }
			MetaDataBean metadataElement = metadataList.get(0);
			
			if (metadataElement.isAliquot()){
				// fetch the rest of aliquot metadata
				List<MetaDataBean> metadataElementsList = getSimpleJdbcTemplate().getJdbcOperations().query(shippedBiospecElementQueryAliquot,
						new ParameterizedRowMapper<MetaDataBean>() {
		            @Override
		            public MetaDataBean mapRow(ResultSet rs, int rowNum) throws SQLException {
		               MetaDataBean valueBean = new MetaDataBean();
		               valueBean.setSampleCode(rs.getString("sample_type_code"));
		               valueBean.setVial(rs.getString("sample_sequence"));
		               valueBean.setPortionCode(rs.getString("portion_sequence"));
		               valueBean.setAnalyteCode(rs.getString("analyte_code"));
		               valueBean.setPlateId(rs.getString("plate_id"));
		               return valueBean;
		            }
		        } , metadataElement.getShippedBiospecId(),
		            metadataElement.getShippedBiospecId(),
		            metadataElement.getShippedBiospecId(),
		            metadataElement.getShippedBiospecId(),
		            metadataElement.getShippedBiospecId());
						
				// its all or nothing with metadata
				if (metadataElementsList == null || metadataElementsList.size() != 1){
					return null;
				}
				metadataElement.combineMetadata(metadataElementsList.get(0));
				uuidMetadata = metadataElement;												
			}
		}				
		return uuidMetadata;		
	}

	@Override
	public String getUUIDLevel(String uuid) {				
		String uuidType = null;
		if (StringUtils.isNotEmpty(uuid)){
			String query = " select item_type from uuid u, barcode_history bh , uuid_item_type uit "+
						   " where u.latest_barcode_id = bh.barcode_id and u.UUID = ? " +
						   " and uit.item_type_id = bh.item_type_id ";
			
			try{
				uuidType = getJdbcTemplate().queryForObject(query,String.class,uuid.toLowerCase());
			}catch (EmptyResultDataAccessException e){
				// if no results , do nothing return empty String
			}						
		}else{
			throw new IllegalArgumentException(" Unable to get level of an empty UUID ");
		}
		return uuidType;
	}	
	
    @Override
    public String getDiseaseForUUID(String uuid) {
        try {
            return getJdbcTemplate().queryForObject(SQL_DISEASE_UUID, String.class, uuid.toLowerCase());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
