package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * Implementation of shipped biospecimen queries.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class QcLiveShippedBiospecimenQueriesJDBCImpl extends SimpleJdbcDaoSupport implements QcLiveShippedBiospecimenQueries {
    private static final String GET_BIOSPECIMEN_ID_FOR_UUID_QUERY = "select shipped_biospecimen_id from shipped_biospecimen where uuid=lower(?)";
    private static final String ADD_FILE_RELATIONSHIP_INSERT = "insert into shipped_biospecimen_file(shipped_biospecimen_id, file_id) values(?, ?)";
    private static final String CHECK_SHIPPED_BIOSPECIMEN_FILE_EXISTS_QUERY = "select count(*) from shipped_biospecimen_file where shipped_biospecimen_id = ? and file_id=?";
    private static final String GET_BIOSPECIMEN_ID_FOR_SHIPPED_PORTION_UUID_QUERY = " select count(a.shipped_biospecimen_id) as uuidCount " +
            " from shipped_biospecimen a, " +
            " shipped_item_type b where a.shipped_item_type_id = b.shipped_item_type_id " +
            " and b.shipped_item_type = 'Shipping Portion' and a.uuid= ? ";

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
        Integer relCount = getSimpleJdbcTemplate().queryForInt(CHECK_SHIPPED_BIOSPECIMEN_FILE_EXISTS_QUERY, biospecimenId, fileId);
        if (relCount == 0) {
            getSimpleJdbcTemplate().update(ADD_FILE_RELATIONSHIP_INSERT, biospecimenId, fileId);
        }
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

}
