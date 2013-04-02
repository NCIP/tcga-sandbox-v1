/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileClinical;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetClinical;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.TumorNormalClassifierI;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAMQueries class that handles BCR clinical (bio) XML files.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesClinicalXML extends DAMBaseQueriesProcessor implements DataAccessMatrixQueries {

    // note: remove last character of sample barcode because DAM does not display vial numbers
    private static final String GET_DATA_QUERY =
            "select f.file_name, f2a.file_location_url, f.file_id, substr(s.sample_barcode, 1, length(s.sample_barcode)-1) as sample_barcode, p.patient_barcode, a.date_added, " +
                    "a.serial_index, a.archive_id, f.file_size " +
                    "from patient p, sample s, portion, analyte, aliquot, shipped_biospecimen sb, " +
                    "patient_archive pa, archive_info a, file_to_archive f2a, file_info f, participant_uuid_file p2f " +
                    "where p.patient_id = pa.patient_id " +
                    "and s.sample_id=portion.sample_id " +
                    "and portion.portion_id=analyte.portion_id " +
                    "and analyte.analyte_id=aliquot.analyte_id " +
                    "and aliquot.uuid = sb.uuid " +
                    "and sb.is_viewable=1 " +
                    "and pa.archive_id = a.archive_id " +
                    "and a.is_latest=1 " +
                    "and a.deploy_status='Available' " +
                    "and a.archive_id= f2a.archive_id " +
                    "and f2a.file_id= f.file_id " +
                    "and f.file_id = p2f.file_id " +
                    "and p2f.uuid = p.uuid " +
                    "and p.patient_id=s.patient_id";

    @Override
    public List<DataSet> getDataSetsForDiseaseType(final String diseaseType) throws DAMQueriesException {
        DiseaseContextHolder.setDisease(diseaseType);
        final Map<String, DataSet> dataSetsBySample = new HashMap<String, DataSet>();
        getJdbcTemplate().query(GET_DATA_QUERY, new RowCallbackHandler() {
            @Override
            public void processRow(final ResultSet rs) throws SQLException {
                final String sample = rs.getString("sample_barcode");
                DataSet dataSetClinical = dataSetsBySample.get(sample);
                if (dataSetClinical == null) {
                    dataSetClinical = new DataSetClinical();
                    dataSetsBySample.put(sample, dataSetClinical);

                    dataSetClinical.setSample(rs.getString("sample_barcode"));
                    dataSetClinical.setAvailability(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE);
                    dataSetClinical.setBatch("Batch " + rs.getInt("serial_index"));
                    dataSetClinical.setCenterId(DataAccessMatrixQueries.CLINICAL_XML_CENTER);
                    dataSetClinical.setLevel(DataAccessMatrixQueries.LEVEL_CLINICAL);
                    dataSetClinical.setDateAdded(rs.getDate("date_added"));
                    dataSetClinical.setPlatformTypeId(DataAccessMatrixQueries.CLINICAL_PLATFORMTYPE);
                    dataSetClinical.setDiseaseType(diseaseType);
                    dataSetClinical.setArchiveId(rs.getInt("archive_id"));
                    dataSetClinical.setDataFiles(new ArrayList<DataFile>());
                }
                // add the data files now, since we have to get them already for the query.
                final DataFileClinical dataFileClinical = new DataFileClinical();
                dataFileClinical.setPermanentFile(true);
                dataFileClinical.setPlatformTypeId(DataAccessMatrixQueries.CLINICAL_PLATFORMTYPE);
                dataFileClinical.setCenterId(DataAccessMatrixQueries.CLINICAL_XML_CENTER);
                dataFileClinical.setFileId(String.valueOf(rs.getInt("file_id")));
                dataFileClinical.setFileName(rs.getString("file_name"));
                dataFileClinical.setPath(rs.getString("file_location_url"));
                dataFileClinical.setSize(rs.getLong("file_size"));
                dataFileClinical.setDisplaySample(LEVEL23_SAMPLE_PHRASE);

                dataSetClinical.getDataFiles().add(dataFileClinical);
            }
        });
        final List<DataSet> clinicalXmlDataSets = new ArrayList<DataSet>();
        clinicalXmlDataSets.addAll(dataSetsBySample.values());
        return clinicalXmlDataSets;
    }

    @Override
    public List<DataSet> getDataSetsForControls(final List<String> diseaseTypes) throws DAMQueriesException {
        return getDataSetsForDiseaseType("CNTL");
    }

    @Override
    public List<DataFile> getFileInfoForSelectedDataSets(final List<DataSet> selectedDataSets, final boolean consolidateFiles) throws DAMQueriesException {
        final List<DataFile> dataFiles = new ArrayList<DataFile>();
        final Set<String> fileIds = new HashSet<String>();
        for (final DataSet dataSet : selectedDataSets) {
            if (dataSet instanceof DataSetClinical && dataSet.getCenterId().equals(DataAccessMatrixQueries.CLINICAL_XML_CENTER)) {
                for (final DataFile dataFile : dataSet.getDataFiles()) {
                    // don't add same file multiple times
                    if (!fileIds.contains(dataFile.getFileId())) {
                        dataFiles.add(dataFile);
                        fileIds.add(dataFile.getFileId());
                    }
                }
            }
        }
        return dataFiles;
    }

    @Override
    public void addPathsToSelectedFiles(final List<DataFile> selectedFiles) throws DAMQueriesException {
        // nothing do to, the DataFiles for this already have the paths set in them when they are created in the getDataSets method
    }

}
