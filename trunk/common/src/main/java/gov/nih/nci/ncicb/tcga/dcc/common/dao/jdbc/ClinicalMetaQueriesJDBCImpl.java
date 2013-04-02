/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ClinicalMetaQueries;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JDBC implementation of clinical meta DAO interface.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ClinicalMetaQueriesJDBCImpl extends SimpleJdbcDaoSupport implements ClinicalMetaQueries {
    private static final String FILENAME_QUERY_PUBLIC = "select distinct f.filename, f.clinical_file_id from clinical_file f, clinical_file_element fe, " +
            "clinical_xsd_element e where f.clinical_file_id=fe.clinical_file_id " +
            "and fe.xsd_element_id=e.clinical_xsd_element_id and e.is_protected=0 and f.context=? order by f.filename";
    private static final String FILENAME_QUERY_ALL = "select distinct f.filename, f.clinical_file_id from clinical_file f, clinical_file_element fe, " +
            "clinical_xsd_element e where f.clinical_file_id=fe.clinical_file_id " +
            "and fe.xsd_element_id=e.clinical_xsd_element_id and f.context=? order by f.filename";

    private static final String FILENAMES_WITH_MAIN_TABLE = "select filename, f.clinical_file_id, element_table_name from " +
            "clinical_file f, clinical_table t where f.clinical_table_id=t.clinical_table_id and f.context=?";

    private static final String DYNAMIC_VALUES_QUERY = "select table_name, dynamic_identifier_column_name, archive_link_table_name, table_id_column_name " +
            "from clinical_table t, clinical_file f " +
            "where t.clinical_table_id=f.clinical_table_id and t.is_dynamic=1 and f.is_dynamic=1 and f.filename=?";


    // the query parts used by the getClinicalFile method
    private static final String CLINICAL_FILE_QUERY_BASE = "SELECT FILE_COLUMN_NAME, TABLE_COLUMN_NAME, e.ELEMENT_NAME, TABLE_NAME, " +
            "BY_PATIENT, JOIN_FOR_SAMPLE, JOIN_FOR_PATIENT, e.IS_PROTECTED, e.DESCRIPTION, e.CLINICAL_XSD_ELEMENT_ID, " +
            "e.VALUE_TYPE, ELEMENT_TABLE_NAME, TABLE_ID_COLUMN_NAME, ARCHIVE_LINK_TABLE_NAME, FILE_COLUMN_ORDER " +
            "FROM CLINICAL_FILE_ELEMENT fe, CLINICAL_XSD_ELEMENT e, CLINICAL_TABLE t, CLINICAL_FILE f  ";
    private static final String CLINICAL_FILE_QUERY_BASE_WHERE = "WHERE t.CLINICAL_TABLE_ID=fe.TABLE_ID AND fe.XSD_ELEMENT_ID=e.CLINICAL_XSD_ELEMENT_ID " +
            "AND f.CLINICAL_FILE_ID=fe.CLINICAL_FILE_ID AND f.CLINICAL_FILE_ID=? ";
    private static final String CLINICAL_FILE_QUERY_WHERE_DISEASE = CLINICAL_FILE_QUERY_BASE_WHERE +
            "AND (fe.DISEASE_ID=d.DISEASE_ID OR fe.DISEASE_ID IS NULL) AND d.DISEASE_ABBREVIATION=?";
    private static final String CLINICAL_FILE_QUERY_ORDER_BY = " ORDER BY FILE_COLUMN_ORDER";

    // query for public files when disease is given
    private static final String CLINICAL_PUBLIC_FILE_WITH_DISEASE =
            CLINICAL_FILE_QUERY_BASE + ", DISEASE d " + CLINICAL_FILE_QUERY_WHERE_DISEASE +
                    " AND e.IS_PROTECTED=0 " + CLINICAL_FILE_QUERY_ORDER_BY;
    // query for protected files when disease is given
    private static final String CLINICAL_PROTECTED_FILE_WITH_DISEASE =
            CLINICAL_FILE_QUERY_BASE + ", DISEASE d " +
                    CLINICAL_FILE_QUERY_WHERE_DISEASE + CLINICAL_FILE_QUERY_ORDER_BY;
    // query for public files when no disease is given
    private static final String CLINICAL_PUBLIC_FILE_NULL_DISEASE =
            CLINICAL_FILE_QUERY_BASE + CLINICAL_FILE_QUERY_BASE_WHERE +
                    " AND e.IS_PROTECTED=0 " + CLINICAL_FILE_QUERY_ORDER_BY;
    // query for protected files when no disease is given
    private static final String CLINICAL_PROTECTED_FILE_NULL_DISEASE =
            CLINICAL_FILE_QUERY_BASE + CLINICAL_FILE_QUERY_BASE_WHERE + CLINICAL_FILE_QUERY_ORDER_BY;

    private static final String CLINICAL_ELEMENT_ENUM_QUERY = "SELECT ENUM_VALUE from CLINICAL_XSD_ENUM_VALUE WHERE XSD_ELEMENT_ID=?";

    private static final String INCLUDE_TABLE_INFO_SELECT = "select table_name, barcode_column_name, element_table_name, dynamic_identifier_column_name, " +
            "join_for_patient, join_for_sample, by_patient, barcode_element_id, table_id_column_name, archive_link_table_name " +
            "from clinical_table, clinical_file " +
            "where clinical_file.clinical_file_id=? and clinical_file.clinical_table_id=clinical_table.clinical_table_id " +
            "order by table_name";

    private static final String INCLUDE_OTHER_TABLE_INFO_SELECT = "select distinct table_name, barcode_column_name, element_table_name, dynamic_identifier_column_name, " +
            "join_for_patient, join_for_sample, by_patient, barcode_element_id, table_id_column_name, archive_link_table_name " +
            "from clinical_table, clinical_file, clinical_file_to_table " +
            "where clinical_file.clinical_file_id=? and clinical_file_to_table.clinical_file_id=clinical_file.clinical_file_id and " +
            "clinical_file_to_table.clinical_table_id=clinical_table.clinical_table_id order by table_name";

    public static final Pattern JOIN_CLAUSE_PATTERN = Pattern.compile("(\\w+)\\.\\w+(\\(\\+\\))?=(\\w+)\\.\\w+(\\(\\+\\))?");

    // constants for columns in database
    private static final String FILE_COLUMN_NAME = "FILE_COLUMN_NAME";
    private static final String TABLE_COLUMN_NAME = "TABLE_COLUMN_NAME";
    private static final String ELEMENT_NAME = "ELEMENT_NAME";
    private static final String TABLE_NAME = "TABLE_NAME";
    private static final String JOIN_FOR_PATIENT = "JOIN_FOR_PATIENT";
    private static final String BY_PATIENT = "BY_PATIENT";
    private static final String JOIN_FOR_SAMPLE = "JOIN_FOR_SAMPLE";
    private static final String IS_PROTECTED = "IS_PROTECTED";
    private static final String FILENAME = "FILENAME";
    private static final String CLINICAL_FILE_ID = "CLINICAL_FILE_ID";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String DESCRIPTION_COLUMN_NAME = DESCRIPTION;
    private static final String CLINICAL_XSD_ELEMENT_ID = "CLINICAL_XSD_ELEMENT_ID";
    private static final String TABLE_ID_COLUMN_NAME = "TABLE_ID_COLUMN_NAME";
    private static final String ELEMENT_TABLE_NAME = "ELEMENT_TABLE_NAME";
    private static final String VALUE_TYPE = "VALUE_TYPE";
    private static final String ARCHIVE_LINK_TABLE_NAME = "ARCHIVE_LINK_TABLE_NAME";
    private static final String BARCODE_COLUMN_NAME = "BARCODE_COLUMN_NAME";
    private static final String BARCODE_ELEMENT_ID = "BARCODE_ELEMENT_ID";
    private static final String TRUE_FOR_IS_LATEST = "1";
    private static final String TRUE_FOR_BY_PATIENT = TRUE_FOR_IS_LATEST;
    private static final String FALSE_FOR_IS_PROTECTED = "0";
    private static final String ARCHIVE_INFO_TABLE = "ARCHIVE_INFO";
    private static final String PATIENT_TABLE = "PATIENT";
    private static final String PATIENT_ARCHIVE_TABLE = "PATIENT_ARCHIVE";
    private static final String JOIN_ARCHIVE_TO_PATIENT_ARCHIVE = "ARCHIVE_INFO.ARCHIVE_ID=PATIENT_ARCHIVE.ARCHIVE_ID";
    private static final String JOIN_PATIENT_TO_PATIENT_ARCHIVE = "PATIENT.PATIENT_ID=PATIENT_ARCHIVE.PATIENT_ID";
    private static final String SAMPLE_TABLE = "SAMPLE";
    private static final String SAMPLE_ARCHIVE_TABLE = "SAMPLE_ARCHIVE";
    private static final String JOIN_ARCHIVE_TO_SAMPLE_ARCHIVE = "ARCHIVE_INFO.ARCHIVE_ID=SAMPLE_ARCHIVE.ARCHIVE_ID";
    private static final String JOIN_SAMPLE_TO_SAMPLE_ARCHIVE = "SAMPLE.SAMPLE_ID=SAMPLE_ARCHIVE.SAMPLE_ID";
    private static final String ARCHIVE_ID_COLUMN = "ARCHIVE_ID";
    private static final String ELEMENT_VALUE = "ELEMENT_VALUE";
    private static final String ENUM_VALUE = "ENUM_VALUE";
    private static final String IS_LATEST_COLUMN = "IS_LATEST";
    private static final String DYNAMIC_IDENTIFIER_COLUMN_NAME = "DYNAMIC_IDENTIFIER_COLUMN_NAME";

    /**
     * @return a map of all clinical filenames that have public data, mapped to their IDs in the database
     */
    public Map<String, Integer> getPublicClinicalFileNames() {
        return getClinicalFileMap(false, DEFAULT_CONTEXT);
    }

    /**
     * Gets a map of public clinical filenames for the given context.  Only files for that context will be returned.  Map
     * keys are file names and values are ids.
     *
     * @param context the context for which to get files
     * @return all public clinical filenames for the given context, mapped to their IDs in the database
     */
    public Map<String, Integer> getPublicClinicalFileNames(final String context) {
        return getClinicalFileMap(false, context);
    }

    /**
     * @return a map of all clinical files that contain elements, mapped to their IDs in the database
     */
    public Map<String, Integer> getAllClinicalFileNames() {
        return getClinicalFileMap(true, DEFAULT_CONTEXT);
    }

    /**
     * Gets a map of all clinical filenames for the given context.  Only files that contain elements will be returned (so
     * files with no elements are excluded).  Map maps file names to ids.
     *
     * @param context the context for which to get files
     * @return all clinical files for the given context, mapped to their IDs in the database
     */
    public Map<String, Integer> getAllClinicalFileNames(final String context) {
        return getClinicalFileMap(true, context);
    }

    /**
     * Returns the dynamic identifier values for this clinical file, or null if not a dynamic file.
     *
     * @param clinicalFileName the name of the clinical file
     * @return the dynamic identifiers for this clinical file, if dynamic, else null
     */
    @Override
    public List<String> getDynamicIdentifierValues(final String clinicalFileName) {
        List<String> dynamicIdentifiers = null;

        try {
            final Map<String, Object> results = getSimpleJdbcTemplate().queryForMap(DYNAMIC_VALUES_QUERY, clinicalFileName);
            final String tableName = results.get(TABLE_NAME).toString();
            final String dynamicColumName = results.get(DYNAMIC_IDENTIFIER_COLUMN_NAME).toString();
            final String archiveLinkTableName = results.get(ARCHIVE_LINK_TABLE_NAME).toString();
            final String tableIdColumnName = results.get(TABLE_ID_COLUMN_NAME).toString();
            final String valueQuery = new StringBuilder().append("select distinct t.").append(dynamicColumName).
                    append(" from archive_info a, ").append(tableName).append(" t, ").append(archiveLinkTableName).
                    append(" a2t ").append(" where a2t.archive_id=a.archive_id and a.is_latest=1 and a.deploy_status='Available' and ").
                    append("a2t.").append(tableIdColumnName).append("=t.").append(tableIdColumnName).append(" order by t.").
                    append(dynamicColumName).toString();
            dynamicIdentifiers = getSimpleJdbcTemplate().query(valueQuery, new RowMapper<String>() {
                @Override
                public String mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                    return rs.getString(1);
                }
            });
        } catch (IncorrectResultSizeDataAccessException e) {
            // ok, that means not dynamic
        }

        return dynamicIdentifiers;
    }

    /*
     * Helper method to get clinical file information given a query.
     */
    private Map<String, Integer> getClinicalFileMap(final boolean includeProtected, String context) {
        if (context == null) {
            context = DEFAULT_CONTEXT;
        }
        final Map<String, Integer> fileMap = new HashMap<String, Integer>();
        getJdbcTemplate().query(includeProtected ? FILENAME_QUERY_ALL : FILENAME_QUERY_PUBLIC, new Object[]{context},
                new RowCallbackHandler() {
                    public void processRow(final ResultSet resultSet) throws SQLException {
                        fileMap.put(resultSet.getString(FILENAME), resultSet.getInt(CLINICAL_FILE_ID));
                    }
                }
        );

        // now look for files that have just main tables
        getJdbcTemplate().query(FILENAMES_WITH_MAIN_TABLE, new Object[]{context}, new RowCallbackHandler() {
            public void processRow(final ResultSet resultSet) throws SQLException {
                if (includeProtected) {
                    // if we want to include protected, just include this file
                    fileMap.put(resultSet.getString(FILENAME), resultSet.getInt(CLINICAL_FILE_ID));
                } else {
                    // public only, so need to check to see if the element table has any public attributes
                    final String elementTable = resultSet.getString(ELEMENT_TABLE_NAME);
                    final int publicElementCount = getJdbcTemplate().queryForInt("select count(*) from " + elementTable + " t, clinical_xsd_element e " +
                            "where t.clinical_xsd_element_id=e.clinical_xsd_element_id and e.is_protected=0");
                    if (publicElementCount > 0) {
                        fileMap.put(resultSet.getString(FILENAME), resultSet.getInt(CLINICAL_FILE_ID));
                    }
                }
            }
        });
        return fileMap;
    }


    public ClinicalFile getClinicalFile(final int fileId, final boolean publicOnly, final String diseaseType) {
        return getClinicalFile(fileId, null, publicOnly, diseaseType);
    }


    /**
     * This gets the details of the given file.  The ClinicalFile object will have a list (ordered) of
     * the ClinicalFileColumns which it should contain.
     *
     * @param fileId      the ID of the file (in the database)
     * @param dynamicIdentifier the dynamic identifier for this file, or null
     * @param publicOnly  pass in true if this file should only contain public elements
     * @param diseaseType the disease type of the data in the file
     * @return a ClinicalFile representing the file with that id
     */
    public ClinicalFile getClinicalFile(final int fileId, final String dynamicIdentifier, final boolean publicOnly, final String diseaseType) {
        final String query;
        if (publicOnly && diseaseType == null) {
            query = CLINICAL_PUBLIC_FILE_NULL_DISEASE;
        } else if (publicOnly) {
            query = CLINICAL_PUBLIC_FILE_WITH_DISEASE;
        } else if (diseaseType == null) {
            query = CLINICAL_PROTECTED_FILE_NULL_DISEASE;
        } else {
            query = CLINICAL_PROTECTED_FILE_WITH_DISEASE;
        }

        final ClinicalFile file = new ClinicalFile();
        file.id = fileId;
        file.publicOnly = publicOnly;
        final List<ClinicalFileColumn> columns = new ArrayList<ClinicalFileColumn>();
        file.columns = columns;
        file.dynamicIdentifier = dynamicIdentifier;
        addDefaultColumns(file, publicOnly, INCLUDE_TABLE_INFO_SELECT, true);
        addDefaultColumns(file, publicOnly, INCLUDE_OTHER_TABLE_INFO_SELECT, false);

        final Object[] params = diseaseType == null ? new Object[]{fileId} : new Object[]{fileId, diseaseType};
        getJdbcTemplate().query(query, params, new RowCallbackHandler() {
            public void processRow(final ResultSet resultSet) throws SQLException {
                // pull out fields and create a column
                final ClinicalFileColumn column = new ClinicalFileColumn();
                column.xsdElementId = resultSet.getLong(CLINICAL_XSD_ELEMENT_ID);
                column.columnName = resultSet.getString(FILE_COLUMN_NAME);
                column.tableName = resultSet.getString(TABLE_NAME);
                column.tableIdColumn = resultSet.getString(TABLE_ID_COLUMN_NAME);


                column.archiveLinkTableName = getStringValueHandleNull(resultSet, ARCHIVE_LINK_TABLE_NAME, null);

                column.tableColumnName = getStringValueHandleNull(resultSet, TABLE_COLUMN_NAME, null);
                if (column.tableColumnName == null) {
                    column.tableColumnName = resultSet.getString(ELEMENT_NAME);
                    column.elementTableName = resultSet.getString(ELEMENT_TABLE_NAME);
                }

                if (resultSet.getInt(BY_PATIENT) == 1) {
                    column.joinClause = resultSet.getString(JOIN_FOR_PATIENT);
                    file.byPatient = true;
                } else {
                    column.joinClause = resultSet.getString(JOIN_FOR_SAMPLE);
                    file.byPatient = false;
                }
                column.isProtected = resultSet.getInt(IS_PROTECTED) == 1;

                column.description = getStringValueHandleNull(resultSet, DESCRIPTION_COLUMN_NAME, "");

                final String query;
                final Object[] params;
                if (column.elementTableName != null) {
                    query = "select count(*) from " + column.elementTableName + " where clinical_xsd_element_id=? and element_value is not null";
                    params = new Object[]{column.xsdElementId};
                } else {
                    query = "select count(*) from " + column.tableName + " where " + column.tableColumnName + " is not null";
                    params = null;
                }
                final int numNotNull = getJdbcTemplate().queryForInt(query, params);
                column.hasNonNullData = (numNotNull > 0);

                // fetch any enum values
                column.values = fetchEnumValues(column.xsdElementId);
                column.type = resultSet.getString(VALUE_TYPE);

                final int fileColumnOrder = resultSet.getInt("FILE_COLUMN_ORDER");

                // expect file column order to be 1-based
                if (fileColumnOrder > 0 && fileColumnOrder <= columns.size()) {
                    columns.add(fileColumnOrder - 1, column);
                } else {
                    // otherwise put at end
                    columns.add(column);
                }
            }
        });

        return file;
    }

    private String getStringValueHandleNull(final ResultSet resultSet, final String columnName, final String valueToUseIfNull) throws SQLException {
        String value = resultSet.getString(columnName);
        if (resultSet.wasNull()) {
            value = valueToUseIfNull;
        }
        return value;
    }

    private void addDefaultColumns(final ClinicalFile file, final boolean publicOnly, final String query, boolean isMainTable) {
        final List resultsList = getJdbcTemplate().queryForList(query, file.id);
        for (final Object resultsObj : resultsList) {
            final Map results = (Map) resultsObj;
            final String tableName = results.get(TABLE_NAME).toString();
            final String barcodeColumnName = results.get(BARCODE_COLUMN_NAME) == null ? null : results.get(BARCODE_COLUMN_NAME).toString();
            final String elementTableName = results.get(ELEMENT_TABLE_NAME).toString();
            final String joinForPatient = results.get(JOIN_FOR_PATIENT).toString();
            final String joinForSample = results.get(JOIN_FOR_SAMPLE).toString();
            final long barcodeElementId = results.get(BARCODE_ELEMENT_ID) == null ? 0 : Long.valueOf(results.get(BARCODE_ELEMENT_ID).toString());
            final String tableIdColumnName = results.get(TABLE_ID_COLUMN_NAME).toString();
            final String archiveLinkTableName = results.get(ARCHIVE_LINK_TABLE_NAME) == null ? null : results.get(ARCHIVE_LINK_TABLE_NAME).toString();
            final boolean byPatient = results.get(BY_PATIENT).toString().equals(TRUE_FOR_BY_PATIENT);
            String dynamicIdentifierColumnName = null;
            if (results.get("DYNAMIC_IDENTIFIER_COLUMN_NAME") != null) {
                dynamicIdentifierColumnName = results.get("DYNAMIC_IDENTIFIER_COLUMN_NAME").toString();
                file.dynamicIdentifierColumnName = dynamicIdentifierColumnName;
                file.dynamicTableName = tableName;
            }
            if (isMainTable) {
                file.keyTableName = tableName;
                file.keyColumnName = tableIdColumnName;
                file.keyTableJoin = (byPatient ? joinForPatient : joinForSample);
            }
            file.byPatient = byPatient;

            if (barcodeColumnName != null) {
                final ClinicalFileColumn barcodeColumn = makeBarcodeColumn(barcodeElementId);
                barcodeColumn.tableColumnName = barcodeColumnName;
                barcodeColumn.xsdElementId = barcodeElementId;
                barcodeColumn.tableIdColumn = tableIdColumnName;
                barcodeColumn.joinClause = (byPatient ? joinForPatient : joinForSample);
                barcodeColumn.tableName = tableName;
                barcodeColumn.archiveLinkTableName = archiveLinkTableName;
                file.columns.add(0, barcodeColumn);
                file.barcodeColumnName = barcodeColumnName;
            }

            addColumnsForDefaultTableElements(file, publicOnly, tableName, elementTableName, joinForPatient,
                    joinForSample, tableIdColumnName, archiveLinkTableName, byPatient, dynamicIdentifierColumnName);
        }
    }

    private void addColumnsForDefaultTableElements(final ClinicalFile file, final boolean publicOnly, final String tableName,
                                                   final String elementTableName, final String joinForPatient,
                                                   final String joinForSample, final String tableIdColumnName,
                                                   final String archiveLinkTableName, final boolean byPatient, final String dynamicColumnName) {
        // build the query according to the elementTableName, to get all the actual xsd elements that have values
        final StringBuilder elementsQuery = new StringBuilder().
                append("select distinct e.ELEMENT_NAME, e.IS_PROTECTED, e.DESCRIPTION, e.CLINICAL_XSD_ELEMENT_ID, e.VALUE_TYPE ").
                append("from ").append(elementTableName).append(" t, clinical_xsd_element e, ").append(tableName).append(" o ").
                append("where e.clinical_xsd_element_id=t.clinical_xsd_element_id and o.").append(tableIdColumnName).append("=t.").append(tableIdColumnName);
        if (publicOnly) {
            elementsQuery.append(" and e.IS_PROTECTED=0");
        }
        Object[] params = new Object[]{};
        if (file.dynamicIdentifier != null) {
            elementsQuery.append(" and o.").append(dynamicColumnName).append("=?");
            params = new Object[]{file.dynamicIdentifier};
        }
        elementsQuery.append(" order by e.ELEMENT_NAME");

        getJdbcTemplate().query(elementsQuery.toString(), new RowCallbackHandler() {

            public void processRow(final ResultSet resultSet) throws SQLException {
                final ClinicalFileColumn column = new ClinicalFileColumn();
                column.xsdElementId = resultSet.getLong(CLINICAL_XSD_ELEMENT_ID);
                column.columnName = resultSet.getString(ELEMENT_NAME);
                column.tableColumnName = resultSet.getString(ELEMENT_NAME);
                column.elementTableName = elementTableName;
                column.tableName = tableName;
                column.tableIdColumn = tableIdColumnName;
                column.archiveLinkTableName = archiveLinkTableName;
                if (byPatient) {
                    column.joinClause = joinForPatient;
                } else {
                    column.joinClause = joinForSample;
                }

                column.description = getStringValueHandleNull(resultSet, DESCRIPTION_COLUMN_NAME, "");
                column.isProtected = resultSet.getInt(IS_PROTECTED) != 0;

                // we know it has data because otherwise the query wouldn't have found it
                column.hasNonNullData = true;

                // fetch any enum values
                column.values = fetchEnumValues(column.xsdElementId);
                column.type = resultSet.getString(VALUE_TYPE);

                // add this column to the file!
                file.columns.add(column);
            }
        }, params);
    }

    private ClinicalFileColumn makeBarcodeColumn(final long barcodeElementId) {
        final String barcodeElementQuery = "select element_name, description, value_type, is_protected from clinical_xsd_element where clinical_xsd_element_id=?";
        final Map results = getJdbcTemplate().queryForMap(barcodeElementQuery, barcodeElementId);
        final String barcodeElementName = results.get(ELEMENT_NAME).toString();
        final String description = results.get(DESCRIPTION) == null ? "" : results.get(DESCRIPTION).toString();
        final String valueType = results.get(VALUE_TYPE) == null ? "" : results.get(VALUE_TYPE).toString();
        final boolean isProtected = !results.get(IS_PROTECTED).toString().equals(FALSE_FOR_IS_PROTECTED);

        final ClinicalFileColumn barcodeColumn = new ClinicalFileColumn();
        barcodeColumn.columnName = barcodeElementName;
        barcodeColumn.description = description;
        barcodeColumn.isProtected = isProtected;
        barcodeColumn.hasNonNullData = true;
        barcodeColumn.type = valueType;
        barcodeColumn.values = new ArrayList<String>();
        return barcodeColumn;
    }

    @Override
    public Map<String, List<Map<ClinicalFileColumn, String>>> getClinicalDataForBarcodes(final ClinicalFile clinicalFile, final Collection<String> patientOrSampleBarcodes,
                                                                                         final boolean byPatient, final boolean barcodeExactMatch) {
        // 1. look through columns in the file and sort them by whether they are in X_element tables or not (where X = patient, sample, aliquot, etc)

        final Map<String, Set<ClinicalFileColumn>> elementTablesToQuery = new HashMap<String, Set<ClinicalFileColumn>>();
        final Set<ClinicalFileColumn> columnsToQuery = new HashSet<ClinicalFileColumn>();

        for (final ClinicalFileColumn column : clinicalFile.columns) {
            if (column.elementTableName != null) {
                Set<ClinicalFileColumn> elementTableColumns = elementTablesToQuery.get(column.elementTableName);
                if (elementTableColumns == null) {
                    elementTableColumns = new HashSet<ClinicalFileColumn>();
                    elementTablesToQuery.put(column.elementTableName, elementTableColumns);
                }
                elementTableColumns.add(column);
            } else {
                columnsToQuery.add(column);
            }
        }
        // this will store the data per row key for the file (e.g. aliquot barcode if the file is of aliquots)
        final Map<String, Map<ClinicalFileColumn, String>> columnDataPerKey = new HashMap<String, Map<ClinicalFileColumn, String>>();
        // this will store the mapping between row key and sample/patient barcode it belongs to
        final Map<String, String> keyToPatientOrSampleBarcode = new HashMap<String, String>();

        if (clinicalFile.keyColumnName == null) {
            deduceFileKeyColumn(clinicalFile);
        }

        // do queries separately for data in "element" tables (key-value pairs) and other data (which is usually just barcodes)
        if (columnsToQuery.size() > 0) {
            getMainTableData(patientOrSampleBarcodes, barcodeExactMatch, columnDataPerKey, columnsToQuery,
                    keyToPatientOrSampleBarcode, clinicalFile);
        }
        if (elementTablesToQuery.size() > 0) {
            getElementTableData(patientOrSampleBarcodes, barcodeExactMatch, columnDataPerKey,
                    elementTablesToQuery, keyToPatientOrSampleBarcode, clinicalFile);
        }

        // now sort the data by barcode so the file is in order
        final List<String> rowKeys = new ArrayList<String>();
        rowKeys.addAll(columnDataPerKey.keySet());
        Collections.sort(rowKeys);

        // put the data into the final overly-complex map!  this is done so we can put the actual sample or patient barcode in the output
        // file.  We can't just use the barcode from the list of barcodes passed in, because the DAM truncates the last letter
        // from the sample barcode, so we need to get it from the DB.  I am sure there is a better way to do this, but for now this
        // works.
        // Outer map: key = patient or sample barcode from the db, value = List of rows of data that go with this barcode
        // List: list (sorted by low-level barcode) of data maps
        // Inner map: key = clinical file column, value = actual data!
        final Map<String, List<Map<ClinicalFileColumn, String>>> dataByPatientOrSample = new HashMap<String, List<Map<ClinicalFileColumn, String>>>();
        // for each key
        for (final String rowKey : rowKeys) {
            // find the matching patient or sample barcode
            final String patientOrSampleBarcode = keyToPatientOrSampleBarcode.get(rowKey);
            // then get the list of data rows for the patient/sample barcode
            List<Map<ClinicalFileColumn, String>> barcodeData = dataByPatientOrSample.get(patientOrSampleBarcode);
            if (barcodeData == null) {
                // make a new list if we haven't seen this barcode before
                barcodeData = new ArrayList<Map<ClinicalFileColumn, String>>();
                dataByPatientOrSample.put(patientOrSampleBarcode, barcodeData);
            }
            // put all the data for this key in the list for the patient/sample barcode
            barcodeData.add(columnDataPerKey.get(rowKey));
        }
        // ... and we're done
        return dataByPatientOrSample;
    }

    private void deduceFileKeyColumn(final ClinicalFile clinicalFile) {
        // check all columns
        final Set<String> tableColumnNames = new HashSet<String>();
        ClinicalFileColumn keyColumn = null;
        for (final ClinicalFileColumn column : clinicalFile.columns) {
            tableColumnNames.add(column.tableColumnName.toUpperCase());
            if (column.tableIdColumn != null) {
                keyColumn = column;
            }
        }
        if (keyColumn != null) {
            clinicalFile.keyTableName = keyColumn.tableName;
            clinicalFile.keyColumnName = keyColumn.columnName;
            clinicalFile.keyTableJoin = keyColumn.joinClause;
        } else {
            throw new IllegalArgumentException("Clinical file specification does not contain a unique key");
        }
    }

    // "main" table means not an element table, so the values are in named columns
    private void getMainTableData(final Collection<String> patientOrSampleBarcodes,
                                  final boolean barcodeExactMatch,
                                  final Map<String, Map<ClinicalFileColumn, String>> columnDataPerKey,
                                  final Set<ClinicalFileColumn> columnsToQuery,
                                  final Map<String, String> keyToPatientOrSampleBarcode,
                                  final ClinicalFile clinicalFile) {
        final Set<String> tablesToQuery = new TreeSet<String>();
        final Set<String> tableJoins = new TreeSet<String>();
        final Set<String> selectItems = new HashSet<String>();
        selectItems.add(clinicalFile.keyTableName + "." + clinicalFile.keyColumnName);
        tableJoins.add(clinicalFile.keyTableJoin);
        selectItems.add(clinicalFile.byPatient ? "PATIENT_BARCODE" : "SAMPLE_BARCODE");
        if (clinicalFile.dynamicIdentifier != null) {
            tablesToQuery.add(clinicalFile.dynamicTableName.toUpperCase());
            tableJoins.add(clinicalFile.dynamicIdentifierColumnName + "='" + clinicalFile.dynamicIdentifier + "'");
        }
        for (final ClinicalFileColumn column : columnsToQuery) {
            selectItems.add(column.tableName + "." + column.tableColumnName);
            tablesToQuery.add(column.tableName.toUpperCase());
            tableJoins.add(column.joinClause);
            if (column.archiveLinkTableName != null) {
                tablesToQuery.add(column.archiveLinkTableName.toUpperCase());
                tableJoins.add(ARCHIVE_INFO_TABLE + "." + ARCHIVE_ID_COLUMN + "=" + column.archiveLinkTableName + "." + ARCHIVE_ID_COLUMN);
                tableJoins.add(column.tableName + "." + column.tableIdColumn + "=" + column.archiveLinkTableName + "." + column.tableIdColumn);
            }
        }
        if (clinicalFile.byPatient) {
            tablesToQuery.add(PATIENT_TABLE);
            tablesToQuery.add(PATIENT_ARCHIVE_TABLE);
            tableJoins.add(JOIN_ARCHIVE_TO_PATIENT_ARCHIVE);
            tableJoins.add(JOIN_PATIENT_TO_PATIENT_ARCHIVE);
        } else {
            tablesToQuery.add(SAMPLE_TABLE);
            tablesToQuery.add(SAMPLE_ARCHIVE_TABLE);
            tableJoins.add(JOIN_ARCHIVE_TO_SAMPLE_ARCHIVE);
            tableJoins.add(JOIN_SAMPLE_TO_SAMPLE_ARCHIVE);
        }

        final boolean queryForSpecificBarcodes = patientOrSampleBarcodes != null;
        final String query = assembleQuery(selectItems, tablesToQuery, tableJoins,
                queryForSpecificBarcodes ? ((clinicalFile.byPatient ? "PATIENT_BARCODE" : "SAMPLE_BARCODE") + (barcodeExactMatch ? "=" : " like ") + "?") : null);
        if (queryForSpecificBarcodes) {
            for (final String patientOrSampleBarcode : patientOrSampleBarcodes) {
                final List<Map<String, Object>> results = getSimpleJdbcTemplate().queryForList(query, barcodeExactMatch ? patientOrSampleBarcode : patientOrSampleBarcode + "%");
                processMainTableResultRow(results, clinicalFile, columnsToQuery, columnDataPerKey, keyToPatientOrSampleBarcode);
            }
        } else {
            final List<Map<String, Object>> results = getSimpleJdbcTemplate().queryForList(query);
            processMainTableResultRow(results, clinicalFile, columnsToQuery, columnDataPerKey, keyToPatientOrSampleBarcode);
        }
    }

    private void processMainTableResultRow(final List<Map<String, Object>> results,
                                           final ClinicalFile clinicalFile,
                                           final Set<ClinicalFileColumn> columnsToQuery,
                                           final Map<String, Map<ClinicalFileColumn, String>> columnDataPerKey,
                                           final Map<String, String> keyToPatientOrSampleBarcode) {
        for (final Map<String, Object> result : results) {
            final String rowKey = String.valueOf(result.get(clinicalFile.keyColumnName));
            final String patientOrSampleFromDb = String.valueOf(result.get(clinicalFile.byPatient ? "PATIENT_BARCODE" : "SAMPLE_BARCODE"));
            keyToPatientOrSampleBarcode.put(rowKey, patientOrSampleFromDb);
            Map<ClinicalFileColumn, String> rowData = columnDataPerKey.get(rowKey);
            if (rowData == null) {
                rowData = new HashMap<ClinicalFileColumn, String>();
                columnDataPerKey.put(rowKey, rowData);
            }
            for (final ClinicalFileColumn column : columnsToQuery) {
                rowData.put(column, String.valueOf(result.get(column.tableColumnName.toUpperCase())));
            }
        }
    }

    private void getElementTableData(final Collection<String> patientOrSampleBarcodes,
                                     final boolean barcodeExactMatch,
                                     final Map<String, Map<ClinicalFileColumn, String>> columnDataPerBarcode,
                                     final Map<String, Set<ClinicalFileColumn>> elementTablesToQuery,
                                     final Map<String, String> barcodeToPatientOrSampleBarcode,
                                     final ClinicalFile clinicalFile) {

        for (final String elementTable : elementTablesToQuery.keySet()) {
            final Map<String, Map<Long, String>> dataByBarcode = new HashMap<String, Map<Long, String>>();
            // use the first column object to get the info for how to query this table
            final Set<ClinicalFileColumn> columns = elementTablesToQuery.get(elementTable);
            final ClinicalFileColumn firstColumn = columns.iterator().next();
            final Set<String> tablesToQuery = new HashSet<String>();
            tablesToQuery.add(elementTable.toUpperCase());
            tablesToQuery.add(firstColumn.tableName.toUpperCase());
            final Set<String> tableJoins = new HashSet<String>();
            if (clinicalFile.dynamicIdentifier != null) {
                tablesToQuery.add(clinicalFile.dynamicTableName.toUpperCase());
                tableJoins.add(clinicalFile.dynamicIdentifierColumnName + "='" + clinicalFile.dynamicIdentifier + "'");
            }
            tableJoins.add(firstColumn.joinClause);
            tableJoins.add(elementTable + "." + firstColumn.tableIdColumn + "=" + firstColumn.tableName + "." + firstColumn.tableIdColumn);
            final Set<String> selectItems = new HashSet<String>();
            selectItems.add(clinicalFile.keyTableName + "." + clinicalFile.keyColumnName);
            tableJoins.add(clinicalFile.keyTableJoin);
            selectItems.add(CLINICAL_XSD_ELEMENT_ID);
            selectItems.add(ELEMENT_VALUE);
            selectItems.add(clinicalFile.byPatient ? "PATIENT_BARCODE" : "SAMPLE_BARCODE");
            if (clinicalFile.byPatient) {
                tablesToQuery.add(PATIENT_TABLE);
                tablesToQuery.add(PATIENT_ARCHIVE_TABLE);
                tableJoins.add(JOIN_ARCHIVE_TO_PATIENT_ARCHIVE);
                tableJoins.add(JOIN_PATIENT_TO_PATIENT_ARCHIVE);
            } else {
                tablesToQuery.add(SAMPLE_TABLE);
                tablesToQuery.add(SAMPLE_ARCHIVE_TABLE);
                tableJoins.add(JOIN_ARCHIVE_TO_SAMPLE_ARCHIVE);
                tableJoins.add(JOIN_SAMPLE_TO_SAMPLE_ARCHIVE);
            }
            if (firstColumn.archiveLinkTableName != null) {
                tablesToQuery.add(firstColumn.archiveLinkTableName.toUpperCase());
                tableJoins.add(ARCHIVE_INFO_TABLE + "." + ARCHIVE_ID_COLUMN + "=" + firstColumn.archiveLinkTableName + "." + ARCHIVE_ID_COLUMN);
                tableJoins.add(firstColumn.tableName + "." + firstColumn.tableIdColumn + "=" + firstColumn.archiveLinkTableName + "." + firstColumn.tableIdColumn);
            }

            final boolean queryForSpecificBarcodes = patientOrSampleBarcodes != null;
            String query = assembleQuery(selectItems, tablesToQuery, tableJoins,
                    queryForSpecificBarcodes ? ((clinicalFile.byPatient ? "PATIENT_BARCODE" : "SAMPLE_BARCODE") + (barcodeExactMatch ? "=" : " like ") + "?") : null);

            query += " ORDER BY " + clinicalFile.keyTableName + "." + clinicalFile.keyColumnName;
            final RowCallbackHandler rowCallbackHandler = new RowCallbackHandler() {
                @Override
                public void processRow(final ResultSet resultSet) throws SQLException {
                    final String rowBarcode = resultSet.getString(clinicalFile.keyColumnName);
                    final String patientOrSampleFromDb = resultSet.getString(clinicalFile.byPatient ? "PATIENT_BARCODE" : "SAMPLE_BARCODE");
                    barcodeToPatientOrSampleBarcode.put(rowBarcode, patientOrSampleFromDb);
                    final Long xsdElementId = resultSet.getLong(CLINICAL_XSD_ELEMENT_ID);
                    final String value = resultSet.getString(ELEMENT_VALUE);
                    Map<Long, String> barcodeData = dataByBarcode.get(rowBarcode);
                    if (barcodeData == null) {
                        barcodeData = new HashMap<Long, String>();
                        dataByBarcode.put(rowBarcode, barcodeData);
                    }
                    barcodeData.put(xsdElementId, value);
                }
            };
            if (queryForSpecificBarcodes) {
                for (final String patientOrSampleBarcode : patientOrSampleBarcodes) {
                    getJdbcTemplate().query(query,
                            new Object[]{(barcodeExactMatch ? patientOrSampleBarcode : patientOrSampleBarcode + "%")},
                            rowCallbackHandler);
                }
            } else {
                getJdbcTemplate().query(query, rowCallbackHandler);
            }
            // now go through the columns and match the xsd IDs with column objects and put in hash
            for (final String rowBarcode : dataByBarcode.keySet()) {
                final Map<Long, String> dataPerXsdId = dataByBarcode.get(rowBarcode);
                Map<ClinicalFileColumn, String> barcodeColumnData = columnDataPerBarcode.get(rowBarcode);
                if (barcodeColumnData == null) {
                    barcodeColumnData = new HashMap<ClinicalFileColumn, String>();
                    columnDataPerBarcode.put(rowBarcode, barcodeColumnData);
                }
                for (final ClinicalFileColumn column : columns) {
                    final String columnValue = dataPerXsdId.get(column.xsdElementId);
                    barcodeColumnData.put(column, String.valueOf(columnValue)); // to make null into a string if val is null
                }
            }
        }
    }

    private String assembleQuery(final Set<String> selectItems, final Set<String> tablesToQuery,
                                 final Set<String> tableJoins, final String additionToWhereClause) {
        final Set<String> joinClauses = parseJoinClauses(tablesToQuery, tableJoins, new HashSet<String>());
        final StringBuilder query = new StringBuilder().append("SELECT distinct ");
        final Iterator<String> selectIterator = selectItems.iterator();
        while (selectIterator.hasNext()) {
            query.append(selectIterator.next());
            if (selectIterator.hasNext()) {
                query.append(", ");
            }
        }
        query.append(" FROM ");
        final Iterator<String> tableIterator = tablesToQuery.iterator();
        while (tableIterator.hasNext()) {
            query.append(tableIterator.next());
            if (tableIterator.hasNext()) {
                query.append(", ");
            }
        }
        query.append(" WHERE ");
        if (additionToWhereClause != null) {
            query.append(additionToWhereClause);
            if (joinClauses.size() > 0) {
                query.append(" AND ");
            }
        }
        final Iterator<String> joinIterator = joinClauses.iterator();
        while (joinIterator.hasNext()) {
            final String join = joinIterator.next();
            if (join.trim().length() > 0) {
                query.append(join);
                if (joinIterator.hasNext()) {
                    query.append(" AND ");
                }
            }
        }
        query.append(" AND ").append(ARCHIVE_INFO_TABLE).append(".").append(IS_LATEST_COLUMN).append("=").append(TRUE_FOR_IS_LATEST);
        return query.toString();
    }

    protected Set<String> parseJoinClauses(
            final Set<String> tablesToQuery, final Set<String> tableJoins, final Set<String> aliasedTables) {
        final Set<String> joinClauses = new TreeSet<String>();
        // now for each join, get out name of table and add to set
        for (final String joinClause : tableJoins) {
            final String[] joins = joinClause.split("(?i) AND "); // the (?i) makes it case-insensitive
            // each join should be x.y=a.b
            for (String join : joins) {
                join = join.trim();
                // a.b=x.y or a.b(+)=x.y or a.b=x.y(+)
                final Matcher joinPatternMatcher = JOIN_CLAUSE_PATTERN.matcher(join);
                if (joinPatternMatcher.matches()) {
                    final String leftTable = joinPatternMatcher.group(1);
                    final String rightTable = joinPatternMatcher.group(3);
                    // only add to tablesToQuery if not aliases
                    if (!tablesToQuery.contains(leftTable) &&
                            !aliasedTables.contains(leftTable) && !aliasedTables.contains(leftTable.toUpperCase())) {
                        tablesToQuery.add(leftTable.toUpperCase());
                    }
                    if (!tableJoins.contains(rightTable) &&
                            !aliasedTables.contains(rightTable) && !aliasedTables.contains(rightTable.toUpperCase())) {
                        tablesToQuery.add(rightTable.toUpperCase());
                    }
                }
                if (join.trim().length() > 0) {
                    joinClauses.add(join);
                }
            }
        }
        return joinClauses;
    }


    private List<String> fetchEnumValues(final long xsdElementId) {
        final List<String> values = new ArrayList<String>();
        getJdbcTemplate().query(CLINICAL_ELEMENT_ENUM_QUERY, new Object[]{xsdElementId}, new RowCallbackHandler() {
            public void processRow(final ResultSet rs) throws SQLException {
                values.add(rs.getString(ENUM_VALUE));
            }
        });

        return values;
    }
}
