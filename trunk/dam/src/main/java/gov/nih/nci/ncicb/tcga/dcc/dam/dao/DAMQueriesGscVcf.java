package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileGscVcf;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetGscVcf;

/**
 * Finds and serves all VCF files from GSC data archives that have associations to shipped biospecimens in the db.
 *
 * @author waltonj
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesGscVcf extends DAMQueriesFilesystem {


    private static final String DATASET_SQL = "select distinct bb.built_barcode as barcode, " +
            "bb.sample, " +
            "ai.archive_id as archive_id, ai.center_id as center_id, " +
            "dt.data_type_id as platform_type_id, dt.sort_order, pi.platform_id as platform_id, ai.deploy_status as availability, " +
            "v.identifiable, ai.serial_index, ai.revision, ai.date_added, pi.platform_alias, at.data_level " +
            "from shipped_biospecimen_breakdown bb, archive_info ai, shipped_biospecimen_file bf, " +
            "file_info f, file_to_archive f2a, data_type dt, archive_type at, " +
            "visibility v, disease d, platform pi, data_visibility dv " +
            "where ai.is_latest = 1  " +
            "and d.disease_abbreviation = ? " +
            "and d.disease_id = ai.disease_id " +
            "and f.file_id = f2a.file_id " +
            "and f2a.archive_id = ai.archive_id " +
            "and pi.base_data_type_id = dt.data_type_id " +
            "and v.visibility_id=dv.visibility_id " +
            "and dv.data_type_id=dt.data_type_id " +
            "and dv.level_number = at.data_level " +
            "and at.archive_type_id = ai.archive_type_id " +
            "and bf.file_id = f.file_id " +
            "and pi.platform_id = ai.platform_id " +
            "and bb.shipped_biospecimen_id = bf.shipped_biospecimen_id " +
            "and f.file_name like '%.vcf' " +
            "and pi.center_type_code='GSC' " +
            "and bb.is_viewable = 1 " +
            "and bb.is_control=?";

    private static final String FILE_INFO_QUERY = "select distinct fi.file_id, fi.file_name, fi.file_size " +
            "from shipped_biospecimen bb, platform p," +
            " archive_info ai, shipped_biospecimen_file bf, file_info fi, file_to_archive f2a " +
            " where bb.built_barcode =  ?" +
            " and p.platform_id=ai.platform_id and p.center_type_code='GSC' " +
            " and ai.is_latest = 1 " +
            " and ai.platform_id = ? " +
            " and ai.center_id = ? " +
            " and bf.file_id = fi.file_id " +
            " and fi.file_id = f2a.file_id " +
            " and f2a.archive_id = ai.archive_id " +
            " and fi.file_name like '%.vcf' " +
            " and bb.shipped_biospecimen_id = bf.shipped_biospecimen_id";

    @Override
    protected String getFileInfoQuery() {
        return FILE_INFO_QUERY;
    }

    @Override
    protected String getDatasetSql() {
        return DATASET_SQL;
    }

    @Override
    protected DataSet getNewDataSetObject() {
        return new DataSetGscVcf();
    }

    @Override
    protected DataFile getNewFileInfoInstance() {
        return new DataFileGscVcf();
    }

    @Override
    protected boolean dataSetShouldBeIncluded(final DataSet dataset) {
        return dataset instanceof DataSetGscVcf;
    }

    @Override
    protected boolean dataFileShouldBeIncluded(final DataFile dataFile) {
        return dataFile instanceof DataFileGscVcf;
    }
}

