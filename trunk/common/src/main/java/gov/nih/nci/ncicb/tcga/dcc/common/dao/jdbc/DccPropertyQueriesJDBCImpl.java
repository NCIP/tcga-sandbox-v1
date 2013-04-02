package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccProperty;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DccPropertyQueries;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for DCC property queries.
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DccPropertyQueriesJDBCImpl extends SimpleJdbcDaoSupport implements DccPropertyQueries {

    private static final String GET_PROPERTY_VALUE =  " select property_value from dcc_Property where property_name =? and  application_name =?";

    private static final String GET_PROPERTY_OBJECT = "select " +
            "property_id," +
            "property_name," +
            "property_value," +
            "property_description," +
            "application_name,"+
            "server_name from dcc_Property where property_name=? and application_name =? ";

    private static final String GET_APP_PROPERTY_OBJECT = "select " +
            "property_id," +
            "property_name," +
            "property_value," +
            "property_description," +
            "application_name,"+
            "server_name from dcc_Property where application_name =?";

    private static final String DELETE_PROPERTY = "delete from dcc_Property where property_id =?";

    private static final String ADD_OR_UPDATE_PROPERTY = "merge into dcc_Property using dual on (property_id =?) "+
            " when matched then " +
            " update set property_name=? ," +
            " property_value = ?," +
            " property_description = ?,"+
            " application_name = ?,"+
            " server_name = ? " +
            " when not matched then " +
            " insert (property_id, property_name, property_value, property_description, application_name,server_name) " +
            " values (dcc_property_seq.nextval,?,?,?,?,?)";

    @Override
    public String getPropertyValue (final String propertyName, final String applicationName){
        return  getSimpleJdbcTemplate().queryForObject(GET_PROPERTY_VALUE,String.class,propertyName, applicationName);

    }

    @Override
    public DccProperty getDccProperty (final String propertyName, final String applicationName){
        final List<DccProperty> property =  getDccProperties(GET_PROPERTY_OBJECT,new Object[]{propertyName,applicationName});
        if(property.size() == 1){
            return property.get(0);
        }
        return null;
    }

    @Override
    public List<DccProperty> getDccPropertiesForAnApplication(final String applicationName) {
        return getDccProperties(GET_APP_PROPERTY_OBJECT,new Object[]{applicationName});
    }

    private List<DccProperty> getDccProperties(final String query, final Object[] parameters){

        return getSimpleJdbcTemplate().query(query, new RowMapper<DccProperty>() {
            public DccProperty mapRow(final ResultSet resultSet, final int i) throws SQLException {
                DccProperty dccProperty= new DccProperty();
                dccProperty.setPropertyId(resultSet.getLong("property_id"));
                dccProperty.setPropertyName(resultSet.getString("property_name"));
                dccProperty.setPropertyValue(resultSet.getString("property_value"));
                dccProperty.setPropertyDescription(resultSet.getString("property_description"));
                dccProperty.setApplicationName(resultSet.getString("application_name"));
                dccProperty.setServerName(resultSet.getString("server_name"));

                return dccProperty;
            }
        }, parameters);
    }


    @Override
    public void addOrUpdateProperty(final DccProperty dccProperty){
        getSimpleJdbcTemplate().update(ADD_OR_UPDATE_PROPERTY,
                dccProperty.getPropertyId(),
                dccProperty.getPropertyName(),
                dccProperty.getPropertyValue(),
                dccProperty.getPropertyDescription(),
                dccProperty.getApplicationName(),
                dccProperty.getServerName(),
                dccProperty.getPropertyName(),
                dccProperty.getPropertyValue(),
                dccProperty.getPropertyDescription(),
                dccProperty.getApplicationName(),
                dccProperty.getServerName()
                );
    }

    @Override
    public void deleteProperty(final DccProperty dccProperty){
        getSimpleJdbcTemplate().update(DELETE_PROPERTY,dccProperty.getPropertyId());
    }

}
