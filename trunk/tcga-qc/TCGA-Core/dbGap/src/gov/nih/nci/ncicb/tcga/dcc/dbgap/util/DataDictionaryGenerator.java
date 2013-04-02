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
import gov.nih.nci.ncicb.tcga.dcc.dbgap.DbGapSubmissionGenerator;

import java.util.Map;

/**
 * Generates data dictionary content for dbGap submissions.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DataDictionaryGenerator extends DbGapFileGeneratorParent {
    public static final String SUBJECT_CONSENT_COLUMN_NAME = "CONSENT";
    public static final String DISEASETYPE = "DISEASETYPE";
    
    private static final String DICTIONARY_HEADER = "VARNAME" + DbGapSubmissionGenerator.FIELD_SEPARATOR +
            "VARDESC" + DbGapSubmissionGenerator.FIELD_SEPARATOR + "TYPE" + DbGapSubmissionGenerator.FIELD_SEPARATOR + "VALUES";
    private static final String SUBJECT_CONSENT_LINE = SUBJECT_CONSENT_COLUMN_NAME + DbGapSubmissionGenerator.FIELD_SEPARATOR +
            "Consent group as determined by DAC" + DbGapSubmissionGenerator.FIELD_SEPARATOR + "encoded value" +
            DbGapSubmissionGenerator.FIELD_SEPARATOR + "1=General Research Use";
    private static final String DISEASE_TYPE_LINE = DISEASETYPE + DbGapSubmissionGenerator.FIELD_SEPARATOR +
            "Disease type" + DbGapSubmissionGenerator.FIELD_SEPARATOR + "string";

    /**
     * Constructs a data dictionary generator that uses the given clinical meta queries interface.
     *
     * @param clinicalMetaQueries the interface to clinical meta queries to use
     */
    public DataDictionaryGenerator(final Map<String, ClinicalMetaQueries> clinicalMetaQueries) {
        super(clinicalMetaQueries);
    }

    /**
     * Generates data dictionary content for the given file descriptor and given disease, if any. The returned string
     * may be written to a file as a data dictionary file.
     *
     * @param dbGapFile the file descriptor to generate a data dictionary for
     * @param disease the disease the file is for, or null if all diseases
     * @return a string containing contents for a data dictionary file
     */
    public String generateDataDictionary(final DbGapSubmissionGenerator.DbGapFile dbGapFile, final String disease) {
        StringBuilder dictionary = new StringBuilder();
        // append headers
        dictionary.append(DICTIONARY_HEADER).append(DbGapSubmissionGenerator.NEWLINE);
        
        ClinicalMetaQueries.ClinicalFile fileInfo = getClinicalFileInfo(dbGapFile, disease);
        for (final ClinicalMetaQueries.ClinicalFileColumn fileColumn : fileInfo.columns) {
            if (fileColumn.hasNonNullData) {
                dictionary.append(fileColumn.columnName).append(DbGapSubmissionGenerator.FIELD_SEPARATOR);
                dictionary.append(fileColumn.description).append(DbGapSubmissionGenerator.FIELD_SEPARATOR);
                dictionary.append(fileColumn.type);
                if (fileColumn.values != null && fileColumn.values.size() > 0) {
                    for (final String value : fileColumn.values) {
                        dictionary.append(DbGapSubmissionGenerator.FIELD_SEPARATOR).append(value);
                    }
                } else {
                    dictionary.append(DbGapSubmissionGenerator.FIELD_SEPARATOR); // blank value to represent no values
                }
                dictionary.append(DbGapSubmissionGenerator.NEWLINE);
            }
        }

        if (dbGapFile == DbGapSubmissionGenerator.DbGapFile.Subjects) {
            // add special DISEASETYPE and CONSENT fields for subjects file
            dictionary.append(DISEASE_TYPE_LINE);
            // add possible values for disease
            for (final String aDisease : getClinicalMetaQueries().keySet()) {
                dictionary.append(DbGapSubmissionGenerator.FIELD_SEPARATOR).append(aDisease);
            }
            dictionary.append(DbGapSubmissionGenerator.NEWLINE);
            dictionary.append(SUBJECT_CONSENT_LINE).append(DbGapSubmissionGenerator.NEWLINE);
        }
        return dictionary.toString();
    }
}
