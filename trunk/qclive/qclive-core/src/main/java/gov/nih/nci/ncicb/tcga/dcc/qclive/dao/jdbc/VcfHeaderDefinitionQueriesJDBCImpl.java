package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.VcfHeaderDefinitionQueries;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * JDBC Implementation of vcf header definition queries.  Reads from vcf_header_definition table.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class VcfHeaderDefinitionQueriesJDBCImpl extends SimpleJdbcDaoSupport implements VcfHeaderDefinitionQueries {

    private static final String SELECT_QUERY = "SELECT number_value, type, description " +
            "FROM vcf_header_definition " +
            "WHERE header_type_name=? and id_name=?";

    private static final String NUMBER_VALUE_COLUMN_NAME = "number_value";
    private static final String TYPE_COLUMN_NAME = "type";
    private static final String DESCRIPTION_COLUMN_NAME = "description";

    /**
     * Gets the VcfFileHeader object represented by the given header type and id.
     *
     * @param headerType the header type
     * @param headerId   the header id
     * @return the VcfFileHeader representing the definition, or null if not found
     */
    @Override
    public VcfFileHeader getHeaderDefinition(final String headerType, final String headerId) {
        try {
            return getSimpleJdbcTemplate().queryForObject(SELECT_QUERY, new RowMapper<VcfFileHeader>() {
                public VcfFileHeader mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                    final VcfFileHeader header = new VcfFileHeader(headerType);
                    final Map<String, String> values = new HashMap<String, String>();
                    values.put("ID", headerId);
                    values.put("Number", rs.getString(NUMBER_VALUE_COLUMN_NAME));
                    values.put("Type", rs.getString(TYPE_COLUMN_NAME));
                    values.put("Description", rs.getString(DESCRIPTION_COLUMN_NAME));

                    header.setValueMap(values);

                    return header;
                }
            }, headerType, headerId);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }
}
