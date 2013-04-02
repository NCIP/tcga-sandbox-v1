/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ClinicalLoaderQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalObject;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalTable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of clinical loader queries.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ClinicalLoaderQueriesJDBCImpl extends BaseQueriesProcessor implements ClinicalLoaderQueries {


    private static final String CLINICAL_TABLE_QUERY = "select e1.element_name as barcode_element_name, " +
            "e2.element_name as uuid_element_name, table_name, element_node_name, " +
            "table_id_column_name, barcode_column_name, element_table_name, archive_link_table_name, " +
            "is_dynamic, dynamic_identifier_column_name, clinical_table_id, parent_table_id " +
            "from clinical_table, clinical_xsd_element e1, clinical_xsd_element e2 " +
            "where clinical_table.barcode_element_id=e1.clinical_xsd_element_id(+) and clinical_table.uuid_element_id=e2.clinical_xsd_element_id(+)";

    private static final String PARENT_TABLE_ID_CLAUSE = "PARENT_TABLE_ID_CLAUSE";
    private static final String CLINICAL_TABLE_QUERY_BY_NODE = CLINICAL_TABLE_QUERY + " and clinical_table.element_node_name=? and " + PARENT_TABLE_ID_CLAUSE;
    private static final String CLINICAL_TABLE_QUERY_BY_NODE_WITH_PARENT = CLINICAL_TABLE_QUERY_BY_NODE.replace(PARENT_TABLE_ID_CLAUSE, "clinical_table.parent_table_id = ?");
    private static final String CLINICAL_TABLE_QUERY_BY_NODE_WITHOUT_PARENT = CLINICAL_TABLE_QUERY_BY_NODE.replace(PARENT_TABLE_ID_CLAUSE, "clinical_table.parent_table_id is null");;
    private static final String CLINICAL_TABLE_QUERY_BY_ID = CLINICAL_TABLE_QUERY + " and clinical_table.clinical_table_id=?";
    private static final String CLINICAL_TABLE_QUERY_DYNAMIC = CLINICAL_TABLE_QUERY + " and clinical_table.is_dynamic=1";
    private static final String PARENT_ID_QUERY = "select parent_table_id from clinical_table where table_name=?";
    private static final String XSD_ELEMENT_ID_QUERY = "select clinical_xsd_element_id,expected_element from clinical_xsd_element where element_name=?";

    private static final String CLINICAL_TABLE_EXISTS_QUERY = "select count(*) from clinical_table where element_node_name=?";
    private static final String XSD_ELEMENT_EXISTS_QUERY = "select count(*) from clinical_xsd_element where element_name=?";
    private static final String XSD_ELEMENTS_QUERY = "select * from clinical_xsd_element";

     private static final String ARCHIVES_TO_LOAD_QUERY = "select archive_id from archive_info, platform " +
            "where archive_id not in (select distinct archive_id from patient_archive) and is_latest=1 and " +
            "archive_info.platform_id=platform.platform_id and platform.platform_name='bio'";
    
    private static final String ARCHIVES_TO_RELOAD_QUERY = "select archive_id from archive_info, platform " +
            "where is_latest=1 and archive_info.platform_id=platform.platform_id and platform.platform_name='bio'";
    private static final String INSERT_CLINCAL_XSD_ELEMENT_QUERY = "insert into clinical_xsd_element (clinical_xsd_element_id,element_name,is_protected,description,value_type,expected_element) values(?,?,?,?,?,?)";
    private static final int XSD_ELEMENT_PUBLIC = 0;
    private static final String XSD_ELEMENT_EXPECTED = "Y";
    private static final String DEFAULT_XSD_ELEMENT_VALUE_TYPE = "String";


    @Override
    public ClinicalObject getClinicalObjectForBarcode(final String objectElementName,
                                                      final String barcode,
                                                      final Long parentId) {

        ClinicalObject result = null;
        final ClinicalTable clinicalTable = getClinicalTableForElementName(objectElementName, parentId);

        if(clinicalTable != null) {

            result = new ClinicalObject();
            result.setClinicalTable(clinicalTable);
            result.setBarcode(barcode);

            final long objectId = getId(result);
            if (objectId > 0) {
                final Map<String, String> attributes = getCurrentAttributes(result, objectId);
                result.setAttributes(attributes);
            } else {
                result = null;
            }
        }

        return  result;
    }
    
    class ClinicalTableRowMapper implements RowMapper {
        public Object mapRow(final ResultSet resultSet, final int i) throws SQLException {
            final ClinicalTable clinicalTable = new ClinicalTable();
            clinicalTable.setBarcodeElementName(resultSet.getString("barcode_element_name"));
            clinicalTable.setUuidElementName(resultSet.getString("uuid_element_name"));
            clinicalTable.setElementNodeName(resultSet.getString("element_node_name"));
            clinicalTable.setTableName(resultSet.getString("table_name"));
            clinicalTable.setIdColumnName(resultSet.getString("table_id_column_name"));
            clinicalTable.setBarcodeColumName(resultSet.getString("barcode_column_name"));
            clinicalTable.setElementTableName(resultSet.getString("element_table_name"));
            clinicalTable.setArchiveLinkTableName(resultSet.getString("archive_link_table_name"));
            clinicalTable.setDynamic(resultSet.getBoolean("is_dynamic"));
            clinicalTable.setDynamicIdentifierColumnName(resultSet.getString("dynamic_identifier_column_name"));
            clinicalTable.setClinicalTableId(resultSet.getLong("clinical_table_id"));

            final String parentTableIdColumnName = "parent_table_id";
            final Object parentIdAsObject = resultSet.getObject(parentTableIdColumnName);
            final long parentId = resultSet.getLong(parentTableIdColumnName);
            clinicalTable.setParentTableId(parentIdAsObject == null? null : parentId); //Prevent conversion of null into long

            return clinicalTable;
        }
    }

    public boolean elementRepresentsClinicalTable(final String elementName) {
        final int rowCount = getJdbcTemplate().queryForInt(CLINICAL_TABLE_EXISTS_QUERY, new Object[]{elementName});
        return rowCount > 0;
    }

    public boolean clinicalXsdElementExists(final String elementName) {
        final int rowCount = getJdbcTemplate().queryForInt(XSD_ELEMENT_EXISTS_QUERY, new Object[]{elementName});
        return rowCount > 0;
    }

    @Override
    public List<ClinicalTable> getAllClinicalTables() {
        return (List<ClinicalTable>) getJdbcTemplate().query(CLINICAL_TABLE_QUERY + " order by table_name", new ClinicalTableRowMapper());
    }

    @Override
    public ClinicalTable getClinicalTableForElementName(final String elementName,
                                                        final Long parentId) {

        String query;
        Object[] args;

        if(parentId != null) {
            query = CLINICAL_TABLE_QUERY_BY_NODE_WITH_PARENT;
            args = new Object[]{elementName, parentId};
        } else {
            query = CLINICAL_TABLE_QUERY_BY_NODE_WITHOUT_PARENT;
            args = new Object[]{elementName};
        }

        return getClinicalTable(query, args);
    }

    public ClinicalTable getClinicalTableById(final long tableId) {
        return getClinicalTable(CLINICAL_TABLE_QUERY_BY_ID, new Object[]{tableId});
    }

    private ClinicalTable getClinicalTable(final String query, final Object[] params) {
        try {
            return (ClinicalTable) getJdbcTemplate().queryForObject(query,
                    params,
                    new ClinicalTableRowMapper()
            );
        } catch (IncorrectResultSizeDataAccessException e) {
            // means no such element in the db
            return null;
        }
    }

    public List<ClinicalTable> getDynamicClinicalTables() {
        return (List<ClinicalTable>) getJdbcTemplate().query(CLINICAL_TABLE_QUERY_DYNAMIC + " order by table_name", new ClinicalTableRowMapper());
    }

    /**
     * Gets the ID (primary key) of the given clinical object. The clinical object must have its clinicalTable variable
     * set as well as its barcode.
     *
     * @param clinicalObject the object whose id you want to look up
     * @return the ID of the object or -1 if the object is not in the database
     */
    public long getId(final ClinicalObject clinicalObject) {
        if (clinicalObject.getClinicalTable().getTableName() == null) {
            throw new IllegalArgumentException("Clinical table name not specified");
        }

        List<Object> params = new ArrayList<Object>();
        StringBuilder query = new StringBuilder();
        query.append("select ").
                append(clinicalObject.getClinicalTable().getIdColumnName()).
                append(" from ").
                append(clinicalObject.getClinicalTable().getTableName()).append(" where ");

        if (clinicalObject.getClinicalTable().isDynamic()) {
            if (clinicalObject.getDynamicIdentifier() == null || clinicalObject.getParentId() == null) {
                throw new IllegalArgumentException("Clinical object does not have dynamic identifier or parent ID set, but represents a dynamic object");
            }
            query.append(clinicalObject.getClinicalTable().getDynamicIdentifierColumnName()).append("=? and ");
            query.append(clinicalObject.getParentTable().getIdColumnName()).append("=?");

            params.add(clinicalObject.getDynamicIdentifier());
            params.add(clinicalObject.getParentId());

        } else {
            query.append(clinicalObject.getClinicalTable().getBarcodeColumName()).append("=?");

            params.add(clinicalObject.getBarcode());
        }

        try {
            return getJdbcTemplate().queryForLong(query.toString(), params.toArray());
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    /**
     * Inserts the clinical object into the database.  Will insert a new record into the corresponding table plus
     * as many as needed to hold the object's attributes.  NOTE: DOES NOT RECURSE THROUGH CHILDREN.
     *
     * @param clinicalObject the object to insert
     * @param parentId the ID for the parent object, if any (use -1 if none)
     * @param archiveId the ID of the archive where this object was found
     * @param newElements {@link List} to store new elements
     * @return the ID of the newly inserted object
     */
    public long insert(final ClinicalObject clinicalObject,
                       final long parentId,
                       final long archiveId,
                       List newElements) {

        final long objectId = getNextSequenceNumber("CLINICAL_SEQ");
        final ClinicalTable clinicalTable = clinicalObject.getClinicalTable();

        // 1. build params list
        final String tableName = clinicalTable.getTableName();
        final List<String> columnNames = new ArrayList<String>();
        final List<Object> params = new ArrayList<Object>();

        columnNames.add(clinicalTable.getIdColumnName());
        params.add(objectId);

        if (clinicalObject.getBarcode() != null) {
            // if there is no barcode, there will not be a UUID
            columnNames.add("UUID");
            params.add(clinicalObject.getUuid());

            columnNames.add(clinicalTable.getBarcodeColumName());
            params.add(clinicalObject.getBarcode());
        }

        if (parentId > 0) {

            final ClinicalTable parentTable = getParentTable(clinicalTable);

            columnNames.add(parentTable.getIdColumnName());
            params.add(parentId);
        }

        if(clinicalTable.isDynamic()) {
            columnNames.add(clinicalTable.getDynamicIdentifierColumnName());
            params.add(clinicalObject.getDynamicIdentifier());
        }

        // 2. construct insert statement
        final String insertStatement = StringUtil.createInsertStatement(tableName, columnNames);

        // 3. run the insert
        getJdbcTemplate().update(insertStatement, params.toArray());

        // 4. insert attributes into element table
        final String insertForElements = makeInsertForElementTable(clinicalObject);
        for (final String attributeName : clinicalObject.getAttributeNames()) {
            final String value = clinicalObject.getValue(attributeName);
            long xsdId = getXsdElementId(attributeName);
            // element is not found
            if (xsdId == -1){
            	// element is restricted
            	// don't do anything
            } else {
                // if the element does not exist, add it
                if (xsdId == -2) {
                    xsdId = insertClinicalXsdElement(attributeName,XSD_ELEMENT_PUBLIC,attributeName,DEFAULT_XSD_ELEMENT_VALUE_TYPE,XSD_ELEMENT_EXPECTED);
                    newElements.add(attributeName);
                }
                getJdbcTemplate().update(insertForElements, new Object[]{objectId, xsdId, value});
            }
            
        }

        insertArchiveLink(clinicalObject, archiveId, objectId);

        return objectId;
    }

    /**
     * Inserts a record into the clinical_xsd_element table for new elements in the xsd for this disease
     *
     * @param elementName the name of the new element
     * @param isProtected 1 for protected, 0 for not protected
     * @param description the description of the new element
     * @param valueType the value type of the new element (ie, String, Float, etc)
     * @param expected "Y" for expected, "N" for not expected
    */
    public long insertClinicalXsdElement(String elementName, int isProtected, String description, String valueType, String expected) {
       final long xsdElementId = getNextSequenceNumber("CLINICAL_XSD_SEQ");
       getJdbcTemplate().update(INSERT_CLINCAL_XSD_ELEMENT_QUERY , new Object[]{xsdElementId,elementName,isProtected,description,valueType,expected});
        return xsdElementId;
    }

    private void insertArchiveLink(final ClinicalObject clinicalObject, final long archiveId, final long objectId) {

        if (clinicalObject.getClinicalTable().getArchiveLinkTableName() != null) {
            String archiveLinkTable = clinicalObject.getClinicalTable().getArchiveLinkTableName();
            String query = "insert into " + archiveLinkTable + "(" + archiveLinkTable + "_ID, " +
                    clinicalObject.getClinicalTable().getIdColumnName() + ", ARCHIVE_ID) values(CLINICAL_SEQ.NEXTVAL, ?, ?)";
            getJdbcTemplate().update(query, new Object[]{objectId, archiveId});

        } // else, this type of object has no direct link to archives
    }

    private String makeInsertForElementTable(final ClinicalObject clinicalObject) {
        return new StringBuilder().append("insert into ").
                append(clinicalObject.getClinicalTable().getElementTableName()).
                append("(").append(clinicalObject.getClinicalTable().getElementTableName()).append("_ID, ").
                append(clinicalObject.getClinicalTable().getIdColumnName()).append(", ").
                append("CLINICAL_XSD_ELEMENT_ID, ELEMENT_VALUE) VALUES(CLINICAL_SEQ.NEXTVAL, ?, ?, ?)").toString();
    }

    protected long getXsdElementId(final String attributeName) {
       
        //return getJdbcTemplate().queryForLong(XSD_ELEMENT_ID_QUERY, new Object[]{attributeName});
    	final Map<Long,String> elementIdMap = new HashMap<Long,String>();        	        	        	
    	getJdbcTemplate().query(XSD_ELEMENT_ID_QUERY, new Object[]{attributeName}, new RowCallbackHandler() {
            public void processRow(final ResultSet resultSet) throws SQLException {
                final long  elementId = resultSet.getLong("clinical_xsd_element_id");
                final String isExpected = resultSet.getString("expected_element");
                elementIdMap.put(elementId, isExpected);                    
            }
        });
    	
    	// if the element is not found
    	if (elementIdMap.size() == 0){
    		return -2;
    	}
    	
    	Long elementId = elementIdMap.keySet().iterator().next();
    	String isExpected = elementIdMap.get(elementId);
    	
    	if ("N".equalsIgnoreCase(isExpected)){
    		// if the element is protected
    		return -1;
    	}else{
    		return elementId;
    	}        	       
    }

    /**
     * Gets the parent table of the given clinical table.
     *
     * @param clinicalTable the clinical table
     * @return the clinical table's parent table, or null if no parent
     */
    public ClinicalTable getParentTable(final ClinicalTable clinicalTable) {
        final long parentTableId = getParentIdFromChildWithName(clinicalTable.getTableName());
        return getClinicalTableById(parentTableId);
    }

    /**
     * Return the Id of the parent table for the child table with the given name.
     *
     * @param childTableName the name of the table from which to retrieve the parent table Id
     * @return the Id of the parent table for the child table with the given name
     */
    private long getParentIdFromChildWithName(final String childTableName) {
        return getJdbcTemplate().queryForLong(PARENT_ID_QUERY, new Object[]{childTableName});
    }

    /**
     * Updates the clinical object in the database.  Changed attribute values will be updated, new attributes will be
     * inserted, and any attributes not in the object that are in the database will be deleted from the database.
     *
     * @param clinicalObject the object to update
     * @param archiveId the ID of the archive in which this object was found
     */
    public void update(final ClinicalObject clinicalObject, final long archiveId, List newElements) {
        final long objectId = getId(clinicalObject);
        // first, get all current element records for this
        final Map<String, String> storedAttributes = getCurrentAttributes(clinicalObject, objectId);

        final String updateQuery = "update " + clinicalObject.getClinicalTable().getElementTableName() +
                " set element_value=? where clinical_xsd_element_id=? and " +
                clinicalObject.getClinicalTable().getIdColumnName() + "=?";
        final String insertQuery = makeInsertForElementTable(clinicalObject);

        for (final String attributeName : clinicalObject.getAttributeNames()) {
            long xsdId = getXsdElementId(attributeName);
            if (xsdId == -1){
            	// element is not expected ; has EXPECTED_ELEMENT set to 'N'
            	// then ignore and don't save to db
            } else {
                // if the element does not exist, add it
                if (xsdId == -2) {
                    xsdId = insertClinicalXsdElement(attributeName,XSD_ELEMENT_PUBLIC,attributeName,DEFAULT_XSD_ELEMENT_VALUE_TYPE,XSD_ELEMENT_EXPECTED);
                    newElements.add(attributeName);
                }
            	final String value = clinicalObject.getValue(attributeName);
                if (storedAttributes.containsKey(attributeName)) {
                    // update value
                    getJdbcTemplate().update(updateQuery, new Object[]{value, xsdId, objectId});
                } else {
                    // insert it
                    getJdbcTemplate().update(insertQuery, new Object[]{objectId, xsdId, value});
                }
            }            
        }

        final Set<String> storedAttributeNames = storedAttributes.keySet();
        storedAttributeNames.removeAll(clinicalObject.getAttributeNames());
        // what is left should be deleted
        final String deleteQuery = "delete from " + clinicalObject.getClinicalTable().getElementTableName() +
                " where clinical_xsd_element_id=? and " + clinicalObject.getClinicalTable().getIdColumnName() + "=?";
        for (final String attributeToDelete : storedAttributeNames) {
            final long xsdId = getXsdElementId(attributeToDelete);
            if (xsdId > 0) {
                getJdbcTemplate().update(deleteQuery, new Object[]{xsdId, objectId});
            }
        }

        // now add a link to the archive, if one is not already there
       if (! archiveLinkExists(clinicalObject, objectId, archiveId)) {
           insertArchiveLink(clinicalObject, archiveId, objectId);
       }

        // note: this method assumes parent will not change... hope that is okay. will a sample ever move to be from a different patient???
    }

    @Override
    public void addArchiveLink(ClinicalObject clinicalObject, long archiveId) {
        final long objectId = getId(clinicalObject);
        if (objectId == -1)  {
            throw new IllegalArgumentException(clinicalObject.getClinicalTable().getTableName() + " " +
                    clinicalObject.getBarcode() + " cannot be linked to an archive because it's not in the database");
        }

        if (! archiveLinkExists(clinicalObject, objectId, archiveId)) {
           insertArchiveLink(clinicalObject, archiveId, objectId);
       }
    }

    private boolean archiveLinkExists(final ClinicalObject clinicalObject, final long objectId, final long archiveId) {
        String query = "select count(*) from " + clinicalObject.getClinicalTable().getArchiveLinkTableName() +
                " where ARCHIVE_ID=? and " + clinicalObject.getClinicalTable().getIdColumnName() + "=?";
        int count = getJdbcTemplate().queryForInt(query, new Object[]{archiveId, objectId});
        return count > 0;
    }

    private Map<String, String> getCurrentAttributes(final ClinicalObject clinicalObject, final long objectId) {
        final String query = "select element_name, element_value from clinical_xsd_element, " + clinicalObject.getClinicalTable().getElementTableName() +
                " where clinical_xsd_element.clinical_xsd_element_id=" + clinicalObject.getClinicalTable().getElementTableName() + ".clinical_xsd_element_id and " +
                clinicalObject.getClinicalTable().getIdColumnName() + "=?";
        final Map<String, String> currentAttributes = new HashMap<String, String>();
        getJdbcTemplate().query(query, new Object[]{objectId}, new RowCallbackHandler() {
            public void processRow(final ResultSet resultSet) throws SQLException {
                final String elementName = resultSet.getString("element_name");
                final String elementValue = resultSet.getString("element_value");
                currentAttributes.put(elementName, elementValue);
            }
        });
        return currentAttributes;
    }

     /**
     * Returns a list of archives to load for clinical loader
     * @return  a list of archive ids to load
     */
     public List<Long> getArchivesToLoadId (){
         List<Long> archivesToReturn = super.getJdbcTemplate().query(ARCHIVES_TO_LOAD_QUERY, new ParameterizedRowMapper<Long>() {
                public Long mapRow(final ResultSet resultSet, final int i) throws SQLException {
                    return resultSet.getLong("archive_id");
                }
            });
         return  archivesToReturn;
    }
    /**
    * Returns a list of archives to re-load for clinical loader
    * @return  a list of archive ids to re-load
    */
    public List<Long> getArchivesToReloadId (){
         List<Long> archivesToReturn = super.getJdbcTemplate().query(ARCHIVES_TO_RELOAD_QUERY, new ParameterizedRowMapper<Long>() {
                public Long mapRow(final ResultSet resultSet, final int i) throws SQLException {
                    return resultSet.getLong("archive_id");
                }
            });
         return  archivesToReturn;
    }
    
     /**
     * Returns a list of clinicalXSDElements
     */
    public List<String> getClinicalXsdElements(){
         List<String> elementsToReturn = super.getJdbcTemplate().query(XSD_ELEMENTS_QUERY, new ParameterizedRowMapper<String>() {
                public String mapRow(final ResultSet resultSet, final int i) throws SQLException {
                    return resultSet.getString("element_name");
                }
            });
         return  elementsToReturn;
    }

}
