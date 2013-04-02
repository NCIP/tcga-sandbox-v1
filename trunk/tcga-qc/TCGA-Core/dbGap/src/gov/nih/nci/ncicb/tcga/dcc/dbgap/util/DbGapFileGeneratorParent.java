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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract parent for util classes that generate files for dbGaP submission.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DbGapFileGeneratorParent {
    private Map<String, ClinicalMetaQueries> clinicalMetaQueries;

    public DbGapFileGeneratorParent(final Map<String, ClinicalMetaQueries> clinicalMetaQueries) {
        if (clinicalMetaQueries == null || clinicalMetaQueries.size() < 1) {
            throw new IllegalArgumentException("ClinicalMetaQueries can't be empty or null");
        }
        this.clinicalMetaQueries = clinicalMetaQueries;
    }

    public Map<String, ClinicalMetaQueries> getClinicalMetaQueries() {
        return clinicalMetaQueries;
    }

    protected ClinicalMetaQueries.ClinicalFile getClinicalFileInfo(final DbGapSubmissionGenerator.DbGapFile dbGapFile, final String diseaseType) {
        ClinicalMetaQueries.ClinicalFile clinicalFile;
        if (diseaseType != null) {
            clinicalFile = clinicalMetaQueries.get(diseaseType).getClinicalFile(dbGapFile.getFileId(), true, diseaseType);
        } else {
            // need to get the file for all diseases, and then set column hasNonNullData = false only if all columns are false
            List<ClinicalMetaQueries.ClinicalFile> files = new ArrayList<ClinicalMetaQueries.ClinicalFile>();
            for (final ClinicalMetaQueries queries : clinicalMetaQueries.values()) {
                ClinicalMetaQueries.ClinicalFile file = queries.getClinicalFile(dbGapFile.getFileId(), true, null);
                files.add(file);
            }

            clinicalFile = files.get(0);
            for (final ClinicalMetaQueries.ClinicalFile file : files) {
                if (file.columns.size() != clinicalFile.columns.size()) {
                    throw new IllegalArgumentException("Clinical files of type " + dbGapFile.getFilename() +
                            " do not have the same columns across schemas");
                }
            }
            List<ClinicalMetaQueries.ClinicalFileColumn> columns = clinicalFile.columns;
            for (int i=0; i<columns.size(); i++) {

                for (final ClinicalMetaQueries.ClinicalFile file : files) {
                    // check that the names match
                    if (! columns.get(i).equals(file.columns.get(i))) {
                        throw new IllegalArgumentException("Column " + i + " different in some schemas (" +
                                columns.get(i).columnName + ")");
                    }
                    // if the first file doesn't have data for a column, see if other diseases do, then set to "has data"
                    if (! columns.get(i).hasNonNullData) {
                        if (file.columns.get(i).hasNonNullData) {
                            columns.get(i).hasNonNullData = true;
                        }
                    }
                }
            }
        }
        return clinicalFile;
    }
}
