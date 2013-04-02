package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.DataMatrixFileBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.DataMatrixQueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC Impl for DataMatrix Queries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DataMatrixQueriesJDBCImpl implements DataMatrixQueries {
    private SimpleJdbcTemplate diseaseJdbcTemplate;

    private static final String GET_MULTIPLE_ALIQUOT_DATA_MATRIX_FILES = "SELECT  " +
            "biospecimen_to_file.file_id," +
            "file_info.file_name," +
            "file_info.data_type_id," +
            "archive_info.archive_name," +
            "archive_info.archive_id," +
            "archive_info.deploy_location " +
            "FROM " +
            "biospecimen_to_file, " +
            "file_info, " +
            "file_to_archive," +
            "archive_info," +
            "center " +
            "WHERE " +
            "biospecimen_to_file.file_id = file_info.file_id AND " +
            "file_info.file_id = file_to_archive.file_id AND " +
            "file_to_archive.archive_id = archive_info.archive_id AND " +
            "archive_info.center_id = center.center_id AND " +
            "center.center_type_code = 'CGCC' AND " +
            "archive_info.is_latest = 1  AND " +
            "file_info.level_number = 2 " +
            "GROUP BY " +
            "biospecimen_to_file.file_id, " +
            "file_info.file_name," +
            "file_info.data_type_id," +
            "archive_info.archive_id," +
            "archive_info.archive_name," +
            "archive_info.deploy_location " +
            "HAVING " +
            "count(biospecimen_to_file.file_id) > 1";



    public Map<String,List<DataMatrixFileBean>> getMultipleAliquotDataMatrixFiles(){
        final Map<String,List<DataMatrixFileBean>> dataMatrixFilesByArchiveName = new HashMap<String,List<DataMatrixFileBean>>();

        diseaseJdbcTemplate.getJdbcOperations().query(GET_MULTIPLE_ALIQUOT_DATA_MATRIX_FILES, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                final DataMatrixFileBean dataMatrixFileBean = new DataMatrixFileBean();
                dataMatrixFileBean.setArchiveName(resultSet.getString("archive_name"));
                dataMatrixFileBean.setArchiveId(resultSet.getLong("archive_id"));
                dataMatrixFileBean.setArchiveDeployLocation(resultSet.getString("deploy_location"));
                dataMatrixFileBean.setFileId(resultSet.getLong("file_id"));
                dataMatrixFileBean.setFileName(resultSet.getString("file_name"));
                dataMatrixFileBean.setDataTypeId(resultSet.getLong("data_type_id"));

                List<DataMatrixFileBean> dataMatrixFiles =  dataMatrixFilesByArchiveName.get(dataMatrixFileBean.getArchiveName());
                if(dataMatrixFiles == null){
                    dataMatrixFiles = new ArrayList<DataMatrixFileBean>();
                    dataMatrixFilesByArchiveName.put(dataMatrixFileBean.getArchiveName(),dataMatrixFiles);
                }
                dataMatrixFiles.add(dataMatrixFileBean);
            }
        });

        return dataMatrixFilesByArchiveName;
    }

    @Autowired
    public void setDiseaseJdbcTemplate(SimpleJdbcTemplate diseaseJdbcTemplate) {
        this.diseaseJdbcTemplate = diseaseJdbcTemplate;
    }

}
