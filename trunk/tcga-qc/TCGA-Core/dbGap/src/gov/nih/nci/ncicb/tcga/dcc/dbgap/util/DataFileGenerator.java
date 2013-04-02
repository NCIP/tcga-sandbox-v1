/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dbgap.util;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ClinicalMetaQueries;
import gov.nih.nci.ncicb.tcga.dcc.dbgap.dao.DbGapQueries;
import gov.nih.nci.ncicb.tcga.dcc.dbgap.DbGapSubmissionGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generates content for dbGap data files.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DataFileGenerator extends DbGapFileGeneratorParent {

    private final Map<String, DbGapQueries> dbGapQueries;

    /**
     * Constructs a data file generator using the given query interfaces.
     *
     * @param clinicalMetaQueries clinical meta queries
     * @param dbGapQueries dbGap queries
     */
    public DataFileGenerator(final Map<String, ClinicalMetaQueries> clinicalMetaQueries,
                             final Map<String, DbGapQueries> dbGapQueries) {
        super(clinicalMetaQueries);
        this.dbGapQueries = dbGapQueries;
    }

    public Map<String, DbGapQueries> getDbGapQueries() {
        return dbGapQueries;
    }

    /**
     * Generates content for the data file for the given dbGap file and disease type.
     *
     * @param dbGapFile the file specifier
     * @param diseaseType the disease, if any (null means data is not disease-specific)
     * @return the data content in a string
     */
    public String generateDataFile(final DbGapSubmissionGenerator.DbGapFile dbGapFile, final String diseaseType) {
        final StringBuilder dataContent = new StringBuilder();

        final ClinicalMetaQueries.ClinicalFile fileInfo = getClinicalFileInfo(dbGapFile, diseaseType);

        // first add the headers for the file
        for (final ClinicalMetaQueries.ClinicalFileColumn column : fileInfo.columns) {
            if (column.hasNonNullData) {
                if (column != fileInfo.columns.get(0)) {
                    dataContent.append(DbGapSubmissionGenerator.FIELD_SEPARATOR);
                }
                dataContent.append(column.columnName);
            }
        }
        if (dbGapFile == DbGapSubmissionGenerator.DbGapFile.Subjects) {
            dataContent.append(DbGapSubmissionGenerator.FIELD_SEPARATOR).append(DataDictionaryGenerator.DISEASETYPE).
                    append(DbGapSubmissionGenerator.FIELD_SEPARATOR).append(DataDictionaryGenerator.SUBJECT_CONSENT_COLUMN_NAME);
        }
        dataContent.append(DbGapSubmissionGenerator.NEWLINE);

        // now get the data -- for each DAO or just the disease
        final List<List<String>> dataValues;
        if (diseaseType != null) {
            dataValues = dbGapQueries.get(diseaseType).getClinicalData(fileInfo);
        } else {
            if (dbGapFile == DbGapSubmissionGenerator.DbGapFile.Subjects || dbGapFile == DbGapSubmissionGenerator.DbGapFile.SubjectsToSamples) {
                dataValues = dbGapQueries.values().iterator().next().getClinicalData(fileInfo);
            } else {
                dataValues = new ArrayList<List<String>>();
                for (final String disease : dbGapQueries.keySet()) {
                    final DbGapQueries queries = dbGapQueries.get(disease);
                    final List<List<String>> diseaseDataValues = queries.getClinicalData(fileInfo);
                    dataValues.addAll(diseaseDataValues);
                }
            }
        }

        for (final List<String> dataRow : dataValues) {
            if (isNotBlank(dataRow)) {
                for (int i=0; i<dataRow.size(); i++) {
                    if (fileInfo.columns.size() <= i || fileInfo.columns.get(i).hasNonNullData) {
                        if (i > 0) {
                            dataContent.append(DbGapSubmissionGenerator.FIELD_SEPARATOR);
                        }
                        dataContent.append(dataRow.get(i));
                    }
                }
                dataContent.append(DbGapSubmissionGenerator.NEWLINE);
            }
        }
        return dataContent.toString();
    }

    private boolean isNotBlank(final List<String> dataRow) {
        // assume first value is ID (sample or subject barcode) so is blank if all rest are null
        for (int i=1; i<dataRow.size(); i++) {
            if (dataRow.get(i) != null) {
                return true;
            }
        }
        return false;
    }
}
