/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for clinical meta queries, which get meta informtion about which files and columns to generate for
 * clinical data.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface ClinicalMetaQueries {
    public static final String DEFAULT_CONTEXT = "dam";

    /**
     * Gets a map of public clinical filenames for the default context.  Map maps file names
     * to ids.
     *
     * @return all public clinical filenames for the default context, mapped to their IDs in the database
     */
    public Map<String, Integer> getPublicClinicalFileNames();

    /**
     * Gets a map of public clinical filenames for the given context.  Only files for that
     * context will be returned.  Map keys are file names and values are ids.
     *
     * @param context the context for which to get files
     * @return all public clinical filenames for the given context, mapped to their IDs in the database
     */
    public Map<String, Integer> getPublicClinicalFileNames(String context);

    /**
     * Gets a map of all clinical filenames for the default context.  Only files that contain elements
     * will be returned (so files with no elements are excluded).  Map maps file names to ids.
     *
     * @return all clinical files for the default context, mapped to their IDs in the database
     */
    public Map<String, Integer> getAllClinicalFileNames();

    /**
     * Gets a map of all clinical filenames for the given context.  Only files that contain elements
     * will be returned (so files with no elements are excluded).  Map maps file names to ids.
     *
     * @param context the context for which to get files
     * @return all clinical files for the given context, mapped to their IDs in the database
     */
    public Map<String, Integer> getAllClinicalFileNames(String context);


    /**
     * Returns the dynamic identifier values for this clinical file, or null if not a dynamic file. If it is dynamic
     * but has no identifiers, then an empty list will be returned.
     *
     * @param clinicalFileName the name of the clinical file
     * @return the dynamic identifiers for this clinical file, if dynamic, else null
     */
    public List<String> getDynamicIdentifierValues(String clinicalFileName);


    /**
     * This gets the details of the given file.  The ClinicalFile object will have a list (ordered) of
     * the ClinicalFileColumns which it should contain.
     *
     * @param fileId      the ID of the file (in the database)
     * @param publicOnly  pass in true if this file should only contain public elements
     * @param diseaseType the disease type of the data
     * @return a ClinicalFile representing the file with that id
     */
    public ClinicalFile getClinicalFile(final int fileId, final boolean publicOnly, final String diseaseType);

    /**
     * Gets the details of a given file, with an optional dynamic identifier.  If dynamicIdentifier is not null then
     * only columns that apply to objects with that dynamicIdentifier will be part of the ClinicalFile.
     *
     * @param fileId the ID of the clinical_file record in the database
     * @param dynamicIdentifier the dynamic identifier, such as "follow_up_v2.0" which limits the ClinicalFile; may be null for non-dynamic files
     * @param publicOnly true if the file will only be for public data, false if all
     * @param diseaseType the disease type for the data, or null if data should not be disease-specific
     * @return a ClinicalFile representing the file with the given id
     */
    public ClinicalFile getClinicalFile(final int fileId, final String dynamicIdentifier, final boolean publicOnly, final String diseaseType);


    /**
     * Gets data for the given clinical file, for the list of patient or sample barcodes.
     *
     * @param clinicalFile      the file to query
     * @param barcodes          the patient or sample barcodes to get data for
     * @param byPatient         if the query is for patients, if false means for samples
     * @param barcodeExactMatch if the query should be done with "=" or "like"
     * @return mapping between patient/sample barcode (key) and a list of maps containing data values for each column for each matching row
     */
    public Map<String, List<Map<ClinicalFileColumn, String>>> getClinicalDataForBarcodes(ClinicalFile clinicalFile, Collection<String> barcodes,
                                                                                         boolean byPatient, boolean barcodeExactMatch);

    /**
     * Inner class representing a clinical file meta data.  The clinical meta DAO uses this to return information
     * about a clinical file.
     */
    public class ClinicalFile {

        public boolean equals(final Object o) {
            if (o == null || !(o instanceof ClinicalFile)) {
                return false;
            } else {
                final ClinicalFile otherFile = (ClinicalFile) o;
                if (this.id == null || otherFile.id == null) {
                    return this == otherFile;
                } else {
                    return this.id.equals(otherFile.id);
                }
            }
        }

        public int hashCode() {
            if (this.id == null) {
                return super.hashCode();
            } else {
                return id.hashCode();
            }
        }

        /**
         * The file name
         */
        public String name;
        /**
         * The file's id
         */
        public Integer id;
        /**
         * Should the file contain only public elements or all elements?
         */
        public boolean publicOnly;
        /**
         * Is the file listed by patient or by sample (false = by sample)
         */
        public boolean byPatient;
        /**
         * The list of columns for this file, in order they should appear.
         */
        public List<ClinicalFileColumn> columns;

        /**
         * The name of the column that holds the barcode that will be unique per row
         */
        public String barcodeColumnName;

        /**
         * The value for the dynamicIdentifier column for this file; may be null
         */
        public String dynamicIdentifier;

        public String dynamicIdentifierColumnName;

        public String dynamicTableName;

        public String keyTableName;
        public String keyColumnName;
        public String keyTableJoin;

    }

    /**
     * Inner class representing a column in a clinical file.
     */
    public class ClinicalFileColumn {

        /**
         * The name of the column (for display)
         */
        public String columnName;
        /**
         * The name of the table the column data is from
         */
        public String tableName;
        /**
         * The name of the column (attribute) the data is from
         */
        public String tableColumnName;

        /**
         * The join clause to use to get the data for this column, assuming a patient/sample barcode is given.
         */
        public String joinClause;
        /**
         * Is this column for a protected data element?
         */
        public boolean isProtected;
        /**
         * Description for this column (taken from the XSD element)
         */
        public String description;
        /**
         * Enumerated values for this column.  If null means value is not constrained.
         */
        public List<String> values;
        /**
         * Type of value, such as string, integer, decimal.
         */
        public String type;

        /**
         * Whether this column (as of the time this object was created) had any non-null values
         * Default is true because we assume there is data in the column
         */
        public boolean hasNonNullData = true;

        /**
         * Name of element table to pull value from.  If null, means is an actual column in the main table.
         */
        public String elementTableName;

        /**
         * Id (from the database) of the XSD element this represents
         */
        public long xsdElementId;

        /**
         * Name of the column that represents the primary key for the table
         */
        public String tableIdColumn;

        /**
         * Name of the table that links the main table to the archive
         */
        public String archiveLinkTableName;

        /**
         * For two ClinicalFileColumn objects to be equal, they should have the same tableName and columnName.
         * If either is null, object identity is used.
         *
         * @param o the object to compare to
         * @return whether the two objects are equal
         */
        public boolean equals(final Object o) {
            if (o instanceof ClinicalFileColumn) {
                final ClinicalFileColumn column = (ClinicalFileColumn) o;
                if (this.columnName == null || column.columnName == null ||
                        this.tableName == null || column.tableName == null) {
                    return this == column;
                } else {
                    return column.columnName.equals(columnName) && column.tableName.equals(tableName);
                }
            }
            return false;
        }

        public int hashCode() {
            if (this.tableName == null || this.columnName == null) {
                return super.hashCode();
            } else {
                return (this.tableName + this.columnName).hashCode();
            }
        }

        public String toString() {
            StringBuilder toString = new StringBuilder();
            toString.append("columnName ").append(columnName).
                    append("\ntableName ").append(tableName).
                    append("\ntableColumnName ").append(tableColumnName).
                    append("\njoinClause ").append(joinClause).
                    append("\nisProtected ").append(isProtected).
                    append("\ndescription ").append(description).
                    append("\ntype ").append(type).
                    append("\nelementTableName ").append(elementTableName).
                    append("\nxsdElementId ").append(xsdElementId).
                    append("\ntableIdColumn ").append(tableIdColumn).
                    append("\narchiveLinkTableName ").append(archiveLinkTableName)
                    .append("\nValue = ");
            for (String value : values) {
                toString.append(value)
                        .append(",");
            }
            toString.deleteCharAt(toString.length() - 1);
            return toString.toString();
        }
    }
}
