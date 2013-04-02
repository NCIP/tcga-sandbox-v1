/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dbgap.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItem;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ClinicalMetaQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TissueSourceSiteQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationSearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.dbgap.DbGapSubmissionGenerator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * JDBC implementation of DbGapQueries.  Gets data for dbGap files from the database.  Uses ClinicalMetaQueries.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DbGapQueriesJDBCImpl extends SimpleJdbcDaoSupport implements DbGapQueries {
    private static final long REDACTION_CLASSIFICATION_ID = 5L;
    private static final long RECISSION_CATEGORY_ID = 31L;

    private ClinicalMetaQueries clinicalMetaQueries;
    private TissueSourceSiteQueries tissueSourceSiteQueries;
    private AnnotationQueries annotationQueries;
    private List<String> redactedCases;

    /**
     * Makes a new queries object that uses the given clinical meta queries object.
     * 
     * @param clinicalMetaQueries the clinicalMetaQueries object to use
     */
    public DbGapQueriesJDBCImpl(final ClinicalMetaQueries clinicalMetaQueries) {
        this.clinicalMetaQueries = clinicalMetaQueries;
    }
    /**
     * Gets all clinical data for the given file.
     *
     * @param clinicalFile get the data for columns needed for this file
     * @return a list of lists of strings, where each list represents a unique record for the file, and the inner list has
     *         values for the file columns in the order as given in the file
     */
    public List<List<String>> getClinicalData( final ClinicalMetaQueries.ClinicalFile clinicalFile) {
        List<List<String>> clinicalData;

        // if Subj file or subj-sample mapping, don't use clinical meta queries, use own db connection to query
        // the biospecimen barcode table
        if (clinicalFile.id == DbGapSubmissionGenerator.DbGapFile.Subjects.getFileId()) {

            clinicalData = getSimpleJdbcTemplate().query(
                    "select distinct project_code || '-' || tss_code || '-' || patient as specific_patient, tss_code " +
                            "from biospecimen_barcode bb order by specific_patient",
                    new ParameterizedRowMapper<List<String>>() {
                        public List<String> mapRow(final ResultSet resultSet, final int i) throws SQLException {
                            final String tssCode = resultSet.getString("tss_code");
                            List<String> diseases = tissueSourceSiteQueries.getDiseasesForTissueSourceSiteCode(tssCode);
                            final List<String> rowData = new ArrayList<String>();
                            rowData.add(resultSet.getString("specific_patient"));
                            rowData.add(diseases.get(0));
                            rowData.add("1");
                            return rowData;
                        }
                    }
            );

        } else if (clinicalFile.id == DbGapSubmissionGenerator.DbGapFile.SubjectsToSamples.getFileId()) {
            clinicalData = getSimpleJdbcTemplate().query("select distinct project_code || '-' || tss_code || '-' || patient as specific_patient, barcode " +
                    "from biospecimen_barcode order by specific_patient, barcode",
                    new ParameterizedRowMapper<List<String>>() {
                        public List<String> mapRow(final ResultSet resultSet, final int i) throws SQLException {
                            final List<String> data = new ArrayList<String>();
                            data.add(resultSet.getString("barcode"));
                            data.add(resultSet.getString("specific_patient"));
                            return data;
                        }
                    }
            );

        } else {

            clinicalData = new ArrayList<List<String>>();
            // for sample (aliquot) queries, need to add join to aliquot table to the first column
            for (final ClinicalMetaQueries.ClinicalFileColumn column : clinicalFile.columns) {
                if (column.elementTableName != null) {
                    column.joinClause += " AND ALIQUOT.ANALYTE_ID=ANALYTE.ANALYTE_ID AND ANALYTE.PORTION_ID=PORTION.PORTION_ID AND PORTION.SAMPLE_ID=SAMPLE.SAMPLE_ID";
                }
            }

            if (clinicalFile.id== DbGapSubmissionGenerator.DbGapFile.Slides.getFileId()) {
                clinicalFile.barcodeColumnName="ALIQUOT_BARCODE";
            }

            final Map<String, List<Map<ClinicalMetaQueries.ClinicalFileColumn, String>>> data =
                    clinicalMetaQueries.getClinicalDataForBarcodes(clinicalFile, null, clinicalFile.byPatient, clinicalFile.byPatient );
            final List<String> barcodes = new ArrayList<String>();
            barcodes.addAll(data.keySet());
            Collections.sort(barcodes);

            for (final String patientOrSample : barcodes) {
                for (final Map<ClinicalMetaQueries.ClinicalFileColumn, String> row : data.get(patientOrSample)) {
                    final List<String> rowData = new ArrayList<String>();
                    clinicalData.add(rowData);
                    for (final ClinicalMetaQueries.ClinicalFileColumn column : clinicalFile.columns) {
                        final String value = row.get(column);
                        rowData.add(value);
                    }
                }
            }
        }

        // get a list of redacted patients, and for each row of clinical data, if it matches any of the redacted cases
        // then remove it.
        // assume barcode is present in first column of data
        List<String> redactedCases = getRedactedCases();
        List<List<String>> clinicalDataWithoutRedactions = new ArrayList<List<String>>();
        for (final List<String> dataRow : clinicalData) {
            boolean redacted = false;
            for (final String redactedCase : redactedCases) {
                if (dataRow.size() > 0 && dataRow.get(0) != null && dataRow.get(0).startsWith(redactedCase)) {
                    redacted = true;
                }
            }
            if (!redacted) {
                clinicalDataWithoutRedactions.add(dataRow);
            }
        }
        return clinicalDataWithoutRedactions;
    }

    /*
     * Note!  Once we have the shipped_biospecimen table fully populated, this code should use that to get things
     * from there that have is_redacted set to 1.  The below code is needed because right now production does not
     * have aliquots in the shipped_biospecimen table.
     */
    private List<String> getRedactedCases() {
        if (redactedCases == null) {
            AnnotationSearchCriteria searchCriteria = new AnnotationSearchCriteria();
            searchCriteria.setClassificationId(REDACTION_CLASSIFICATION_ID); // 5 is the classification for Redactions
            searchCriteria.setCurated(true);
            List<DccAnnotation> curatedRedactionAnnotations = annotationQueries.searchAnnotations(searchCriteria);

            searchCriteria = new AnnotationSearchCriteria();
            searchCriteria.setCategoryId(RECISSION_CATEGORY_ID); // Previous redaction rescinded
            List<DccAnnotation> rescindedRedactionAnnotations = annotationQueries.searchAnnotations(searchCriteria);

            List<String> rescindedRedactions = new ArrayList<String>();
            for (final DccAnnotation annotation : rescindedRedactionAnnotations) {
                for (final DccAnnotationItem item : annotation.getItems()) {
                    rescindedRedactions.add(item.getItem());
                }
            }
            redactedCases = new ArrayList<String>();
            for (final DccAnnotation annotation : curatedRedactionAnnotations) {
                for (final DccAnnotationItem item : annotation.getItems()) {
                    if (! rescindedRedactions.contains(item.getItem())) {
                        redactedCases.add(item.getItem());
                    }
                }
            }
        }
        return redactedCases;
    }

    public void setTissueSourceSiteQueries(final TissueSourceSiteQueries tissueSourceSiteQueries) {
        this.tissueSourceSiteQueries = tissueSourceSiteQueries;
    }

    public TissueSourceSiteQueries getTissueSourceSiteQueries() {
        return tissueSourceSiteQueries;
    }

    public void setAnnotationQueries(final AnnotationQueries annotationQueries) {
        this.annotationQueries = annotationQueries;
    }

    public AnnotationQueries getAnnotationQueries() {
        return annotationQueries;
    }
}
